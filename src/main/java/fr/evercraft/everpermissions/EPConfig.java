/*
 * This file is part of EverPermissions.
 *
 * EverPermissions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverPermissions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverPermissions.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.everpermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import fr.evercraft.everapi.plugin.file.EConfig;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class EPConfig extends EConfig<EverPermissions> {
	
	public static final String DEFAULT = "default";
	
	public EPConfig(final EverPermissions plugin) {
		super(plugin);
	}
	
	public void reload() {
		super.reload();
		this.plugin.getELogger().setDebug(this.isDebug());
	}
	
	@Override
	public List<String> getHeader() {
		return 	Arrays.asList(	"####################################################### #",
								"               EverPermissions (By rexbut)               #",
								"    For more information : https://docs.evercraft.fr     #",
								"####################################################### #");
	}
	
	@Override
	public void loadDefault() {
		this.configDefault();
		this.sqlDefault();
		
		addDefault("collections", ImmutableMap.of(),
							"Configure the collection for each world",
							"Example : ",
							"	default=[",
							"	  world,",
							"     DIM1",
							"	],",
							"	type-nether=[",
							"     DIM-1",
							"	]",
							"The worlds 'world' and 'DIM1' will have the same collection but not 'DIM-1'");
	}

	public Map<String, String> getTypeWorld(String collection) {
		ImmutableMap.Builder<String, String> worlds = ImmutableMap.builder();
		for (Entry<Object, ? extends CommentedConfigurationNode> value : this.get("collections." + collection).getChildrenMap().entrySet()) {
			for (CommentedConfigurationNode list : value.getValue().getChildrenList()) {
				String world = list.getString(null);
				if (world != null) {
					worlds.put(world, value.getKey().toString());
				}
			}
		}
		return worlds.build();
	}
	
	public String getTypeWorld(String collection, String world) {
		return this.getWorld(collection, world).orElse(EPConfig.DEFAULT);
	}
	
	private Optional<String> getWorld(String collection, String world) {
		for (Entry<Object, ? extends CommentedConfigurationNode> value : this.get("collections." + collection).getChildrenMap().entrySet()) {
			for (CommentedConfigurationNode list : value.getValue().getChildrenList()) {
				if (list.getString("").equals(world)) {
					return Optional.of(value.getKey().toString());
				}
			}
		}
		return Optional.empty();
	}
	
	public Set<String> getCollections() {
		return this.get("collections").getChildrenMap().keySet().stream().map(collection -> collection.toString()).collect(Collectors.toSet());
	}
	

	
	public void registerCollection(String collection) {
		if (collection.equals(PermissionService.SUBJECTS_DEFAULT)) return;
		
		ConfigurationNode config = this.get("collections." + collection).getNode(EPConfig.DEFAULT);
		boolean save = false;
		
		List<String> worlds = new ArrayList<String>();
		try {
			worlds.addAll(config.getList(TypeToken.of(String.class)));
		} catch (ObjectMappingException e) {}
		
		for (World world : this.plugin.getGame().getServer().getWorlds()) {
			if (this.getTypeWorld(collection, world.getName()).equals(EPConfig.DEFAULT) && !worlds.contains(world.getName())) {
				worlds.add(world.getName());
				save = true;
			}
		}
		
		config.setValue(worlds);
		if (config.isVirtual()) {
			config.setValue(ImmutableList.of());
			save = true;
		}
		
		if (save) this.save(true);
	}

	public void registerWorld(String world) {
		boolean save = false;
		
		for (String collection : this.getCollections()) {
			if (!this.getWorld(collection, world).isPresent()) {
				CommentedConfigurationNode config = this.get("collections." + collection).getNode(EPConfig.DEFAULT);
				
				try {
					List<String> worlds = new ArrayList<String>();
					worlds.addAll(config.getList(TypeToken.of(String.class)));
					worlds.add(world);
					config.setValue(worlds);
					
					save = true;
				} catch (ObjectMappingException e) {}
			}
		}
		
		if (save) this.save(true);
	}
}
