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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableMap;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.plugin.file.EMessage;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

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
	public void loadDefault() {
		addDefault("DEBUG", false, "Displays plugin performance in the logs");
		addDefault("LANGUAGE", EMessage.FRENCH, "Select language messages", "Examples : ", "  French : FR_fr", "  English : EN_en");
		
		addComment("SQL", 	"Save the user in a database : ",
							" H2 : \"jdbc:h2:" + this.plugin.getPath().toAbsolutePath() + "/permissions\"",
							" SQL : \"jdbc:mysql://[login[:password]@]<host>:<port>/<database>\"",
							"By default users are saving in the 'users/'");
		
		addDefault("SQL.enable", false);
		addDefault("SQL.url", "jdbc:mysql://root:password@localhost:3306/minecraft");
		addDefault("SQL.prefix", "everpermissions_");
		
		addDefault("collections", ImmutableMap.of(),
							"Configure the collection for each world",
							"Example : ",
							"	world=default",
							"	DIM1=default",
							"	DIM-1=nether",
							"The worlds 'world' and 'DIM1' will have the same collection but not 'DIM-1'");
	}

	public Map<String, String> getTypeWorld(String collection) {
		ImmutableMap.Builder<String, String> worlds = ImmutableMap.builder();
		for (Entry<Object, ? extends CommentedConfigurationNode> value : this.get("collections." + collection).getChildrenMap().entrySet()) {
			worlds.put(value.getKey().toString(), value.getValue().getString(EPConfig.DEFAULT));
		}
		return worlds.build();
	}
	
	public String getTypeWorld(String collection, String world) {
		return this.get("collections." + collection + "." + world).getString(EPConfig.DEFAULT);
	}
	
	public Set<String> getCollections() {
		return this.get("collections").getChildrenMap().keySet().stream().map(collection -> collection.toString()).collect(Collectors.toSet());
	}
	

	
	public void registerCollection(String collection) {
		ConfigurationNode config = this.get("collections." + collection);
		boolean save = false;
		
		for (World world : this.plugin.getGame().getServer().getWorlds()) {
			ConfigurationNode typeWorld = config.getNode(world.getName());
			if (typeWorld.isVirtual()) {
				typeWorld.setValue(EPConfig.DEFAULT);
				save = true;
			}
		}
		
		if (config.isVirtual()) {
			config.setValue(ImmutableMap.of());
			save = true;
		}
		
		if (save) this.save(true);
	}

	public void registerWorld(String world) {
		boolean save = false;
		
		for (Entry<Object, ? extends CommentedConfigurationNode> value : this.get("collections").getChildrenMap().entrySet()) {
			CommentedConfigurationNode typeWorld = value.getValue().getNode(world);
			if (typeWorld.isVirtual()) {
				typeWorld.setValue(EPConfig.DEFAULT);
				save = true;
			}
		}
		
		if (save) this.save(true);
	}
}
