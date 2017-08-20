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
package fr.evercraft.everpermissions.service.permission.storage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.data.ESubjectData;
import fr.evercraft.everpermissions.service.permission.data.EUserData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubjectReference;

public class EConfigSubjectStorage extends EConfig<EverPermissions> {	
	private final String collection;
	private final String typeWorld;
	
	private final String parentIdentifier;

	public EConfigSubjectStorage(final EverPermissions plugin, final String collection, final String typeWorld) {
		super(plugin, collection + "/" + typeWorld);
		this.collection = collection;
		this.typeWorld = typeWorld;
		
		this.parentIdentifier = (this.collection.equals(PermissionService.SUBJECTS_GROUP)) ? "inheritances" : "subgroups";
	}
	
	@Override
	protected void loadDefault() {
		if (!this.isNewDirs()) return;
		
		Optional<Asset> asset = this.plugin.getPluginContainer().getAsset("collections/" + this.collection + ".conf");
		if (!asset.isPresent()) return;
		
		URL jarConfigFile = asset.get().getUrl();
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setURL(jarConfigFile).build();
		try {
			this.getNode().setValue(loader.load());
		} catch (IOException e) {}
	}
	
	public boolean load(final ESubject subject) {
		if (subject.getSubjectData() instanceof ESubjectData) return true;
		
		ConfigurationNode configSubject = this.get(subject.getIdentifier());
		ESubjectData dataSubject = (ESubjectData) subject.getSubjectData();
		
		// Si le fichier de configuration existe
		if (!configSubject.isVirtual()) {
			// Chargement du name
			String name = configSubject.getNode("name").getString(null);
			if (name != null) {
				Optional<String> oldName = subject.getFriendlyIdentifier();
				if (!oldName.isPresent()) {
					subject.setFriendlyIdentifierExecute(name);
					this.plugin.getELogger().debug("Loading : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "name=" + name + ";"
							+ "type=" + this.typeWorld + ")");
				} else if (!oldName.get().equals(name)) {
					this.plugin.getELogger().warn("Loading error : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "name1=" + name + ";"
							+ "name2=" + oldName.get() + ";"
							+ "type=" + this.typeWorld + ")");
				}
			}
			
			// Chargement des permissions
			for (Entry<Object, ? extends ConfigurationNode> permission : configSubject.getNode("permissions").getChildrenMap().entrySet()) {
				if (permission.getKey() instanceof String && permission.getValue().getValue() instanceof Boolean) {
					dataSubject.setPermissionExecute(this.typeWorld, (String) permission.getKey(), Tristate.fromBoolean(permission.getValue().getBoolean(false)));
					this.plugin.getELogger().debug("Loading : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "permission=" + permission.getKey().toString() + ";"
							+ "value=" + permission.getValue().getBoolean(false) + ";"
							+ "type=" + this.typeWorld + ")");
				} else {
					this.plugin.getELogger().warn("Loading error : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "permission=" + permission.getKey().toString() + ";"
							+ "type=" + this.typeWorld + ")");
				}
			}
			
			// Chargement des options
			for (Entry<Object, ? extends ConfigurationNode> option : configSubject.getNode("options").getChildrenMap().entrySet()) {
				String value = option.getValue().getString(null);
				if (option.getKey() instanceof String && value != null) {
					dataSubject.setOptionExecute(this.typeWorld, (String) option.getKey(), value);
					this.plugin.getELogger().debug("Loading : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "option=" + option.getKey() + ";"
							+ "name=" + value + ";"
							+ "type=" + this.typeWorld + ")");
				} else {
					this.plugin.getELogger().warn("Loading error : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "option=" + option.getValue().toString() + ";"
							+ "type=" + this.typeWorld + ")");
				}
			}
			
			// Chargement les sous-groupes
			try {
				for (String subgroup : configSubject.getNode(this.parentIdentifier).getList(TypeToken.of(String.class))) {
					Subject group = this.plugin.getService().getGroupSubjects().loadSubject(subgroup).join();
					if (group != null) {
						dataSubject.addParentExecute(this.typeWorld, group.asSubjectReference());
						this.plugin.getELogger().debug("Loading : ("
								+ "identifier=" + subject.getIdentifier() + ";"
								+ "subgroup=" + group.getIdentifier() + ";"
								+ "type=" + this.typeWorld + ")");
					} else {
						this.plugin.getELogger().warn("Loading error : ("
								+ "identifier=" + subject.getIdentifier() + ";"
								+ "subgroup=" + subgroup + ";"
								+ "type=" + this.typeWorld + ")");
					}
				}
			} catch (ObjectMappingException e) {}
			
			// Chargement du groupe
			String groups = configSubject.getNode("group").getString(null);
			if (dataSubject instanceof EUserData && groups != null) {
				Subject group = this.plugin.getService().getGroupSubjects().loadSubject(groups).join();
				if (group != null) {
					((EUserData) dataSubject).setGroupExecute(this.typeWorld, group.asSubjectReference());
					this.plugin.getELogger().debug("Loading : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "group=" + group.getIdentifier() + ";"
							+ "type=" + this.typeWorld + ")");
				} else {
					this.plugin.getELogger().warn("Loading error : ("
							+ "identifier=" + subject.getIdentifier() + ";"
							+ "group=" + group + ";"
							+ "type=" + this.typeWorld + ")");
				}
			}
			
			// Chargement du default
			boolean isDefault = configSubject.getNode("default").getBoolean(false);
			if (subject instanceof EGroupSubject && isDefault) {
				((EGroupSubject) subject).setDefault(this.typeWorld, true);
				this.plugin.getELogger().debug("Loading : ("
						+ "identifier=" + subject.getIdentifier() + ";"
						+ "default='true';"
						+ "type=" + this.typeWorld + ")");
			}
			
			if (subject instanceof EGroupSubject) {
				((EGroupSubject) subject).registerTypeWorld(this.typeWorld);
			}
			return true;
		}
		return false;
	}
	
	public boolean load(Collection<ESubject> subjects) {
		for (ESubject subject : subjects) {
			if (!this.load(subject)) return false;
		}
		return true;
	}
	
	/*
	 * Permissions
	 */

	public boolean setPermission(final ESubjectData subject, final String permission, final Tristate value, final boolean insert) {
		ConfigurationNode permissions = this.getNode().getNode(subject.getIdentifier(), "permissions");
		// Supprime une permission
		if (value.equals(Tristate.UNDEFINED)) {
			permissions.removeChild(permission);
			this.plugin.getELogger().debug("Removed from configs file : (identifier='" + subject + "';permission='" + permission + "';type='" + this.typeWorld + "')");
		// Ajoute une permission
		} else {
			permissions.getNode(permission).setValue(value.asBoolean());
			this.plugin.getELogger().debug("Added to the configs file : (identifier='" + subject + "';permission='" + permission + "';value='" + value.asBoolean() + "';type='" + this.typeWorld + "')");
		}
		return this.save(true);
	}

	public boolean clearPermissions(final ESubjectData subject) {
		this.get(subject.getIdentifier()).removeChild("permissions");
		
		this.plugin.getELogger().debug("Removed the permissions configuration file : (identifier='" + subject + "';type='" + this.typeWorld + "')");
		return this.save(true);
	}
	
	/*
	 * Options
	 */
	
	public boolean setOption(final ESubjectData subject, final String option, final String value, final boolean insert) {
		ConfigurationNode options = this.get(subject + ".options");
		if (value == null) {
			options.removeChild(option);
			this.plugin.getELogger().debug("Removed from configs file : (identifier='" + subject + "';option='" + option + "';type='" + this.typeWorld + "')");
		} else {
			options.getNode(option).setValue(value);
			this.plugin.getELogger().debug("Added to the configs file : (identifier='" + subject + "';option='" + option + "';value='" + value + "';type='" + this.typeWorld + "')");
		}
		return this.save(true);
	}

	public boolean clearOptions(final ESubjectData subject) {
		this.get(subject.getIdentifier()).removeChild("options");
		this.plugin.getELogger().debug("Removed the options configuration file : (identifier='" + subject + "';type='" + this.typeWorld + "')");
		return this.save(true);
	}

	/*
	 * Groups
	 */
	
	public boolean addParent(final ESubjectData subject, final SubjectReference parent) {
		try {
			List<String> subgroups = new ArrayList<String>(this.get(subject.getIdentifier() + "." + this.parentIdentifier).getList(TypeToken.of(String.class)));
			subgroups.add(parent.getSubjectIdentifier());
			this.get(subject.getIdentifier() + "." + this.parentIdentifier).setValue(subgroups);
			this.plugin.getELogger().debug("Added to the configs file : (identifier='" + subject + "';" + this.parentIdentifier + "='" + parent.getSubjectIdentifier() + "';type='" + this.typeWorld + "')");
			return this.save(true);
		} catch (ObjectMappingException e) {}
		return false;
	}
	
	public boolean setGroup(final ESubjectData subject, final SubjectReference parent, boolean insert) {
		this.get(subject.getIdentifier() + ".group").setValue(parent.getSubjectIdentifier());
		this.plugin.getELogger().debug("Added to the configs file : (identifier='" + subject + "';group='" + parent.getSubjectIdentifier() + "';type='" + this.typeWorld + "')");
		return this.save(true);
    }

	public boolean removeParent(final ESubjectData subject, final SubjectReference parent) {
		ConfigurationNode group = this.get(subject.getIdentifier() + ".group");
		if (!group.isVirtual() && group.getString("").equals(parent.getSubjectIdentifier())) {
			this.get(subject.getIdentifier()).removeChild("group");
			return this.save(true);
		}
		
		try {
			List<String> subgroups = new ArrayList<String>(this.get(subject.getIdentifier() + "." + this.parentIdentifier).getList(TypeToken.of(String.class)));
			subgroups.remove(parent.getSubjectIdentifier());
			if (subgroups.isEmpty()) {
				this.get(subject.getIdentifier()).removeChild(this.parentIdentifier);
			} else {
				this.get(subject.getIdentifier() + "." + this.parentIdentifier).setValue(subgroups);
			}
			this.plugin.getELogger().debug("Removed from configs file : (identifier='" + subject + "';subgroup='" + parent.getSubjectIdentifier() + "';type='" + this.typeWorld + "')");
			return this.save(true);
		} catch (ObjectMappingException e) {}
		return false;
	}
	
	public boolean clearParents(final ESubjectData subject) {
		ConfigurationNode config = this.get(subject.getIdentifier());
		config.removeChild(this.parentIdentifier);
		config.removeChild("group");
		this.plugin.getELogger().debug("Removed the group configuration file : (identifier='" + subject + "';type='" + this.typeWorld + "')");
		return this.save(true);
	}
	
	public boolean setFriendlyIdentifier(ESubject subject, @Nullable String name) {
		if (name == null) {
			this.get(subject.getIdentifier()).removeChild("name");
		} else {
			this.get(subject.getIdentifier() + ".name").setValue(name);
		}
		return this.save(true);
	}
	
	public boolean setDefault(EGroupSubject subject, boolean value) {
		if (!value) {
			this.get(subject.getIdentifier()).removeChild("default");
		} else {
			this.get(subject.getIdentifier() + ".default").setValue(true);
		}
		return this.save(true);
	}

	public boolean hasSubject(String identifier) {
		return !this.get(identifier).isVirtual();
	}

	public Set<String> getAllIdentifiers() {
		return this.getNode().getChildrenMap().keySet().stream().map(collection -> collection.toString()).collect(Collectors.toSet());
	}

	public Map<SubjectReference, Boolean> getAllWithPermission(final String permission) {
		List<String> permissions = new ArrayList<String>();
		String[] elements = permission.split(".");
		for (int cpt1=elements.length; cpt1>0; cpt1--) {
			List<String> value = new ArrayList<String>();
			for (int cpt2=0; cpt2<cpt1; cpt2++) {
				value.add(elements[cpt2]);
			}
			permissions.add(String.join(".", value));
		}
		
		ImmutableMap.Builder<SubjectReference, Boolean> subjects = ImmutableMap.builder();
		for (Entry<Object, ? extends CommentedConfigurationNode> configSubject : this.getNode().getChildrenMap().entrySet()) {
			ConfigurationNode configPermission = configSubject.getValue().getNode("permissions");
			if (configPermission.isVirtual()) continue;
			
			for (String value : permissions) {
				ConfigurationNode result = configPermission.getNode(value);
				if (!result.isVirtual()) {
					subjects.put(new ESubjectReference(this.plugin.getService(), this.collection, configSubject.getKey().toString()), result.getBoolean(false));
					continue;
				}
			}
		}
		return subjects.build();
	}
}