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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.reflect.TypeToken;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.data.ESubjectData;
import fr.evercraft.everpermissions.service.permission.data.EUserData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;
import fr.evercraft.everpermissions.storage.EPConfUsers;

public class EConfigSubjectStorage extends EConfig<EverPermissions> {	
	private final String collection;
	private final String typeWorld;

    public EConfigSubjectStorage(final EverPermissions plugin, final String collection, final String typeWorld) {
    	super(plugin, collection + "/" + typeWorld);
        this.collection = collection;
        this.typeWorld = typeWorld;
    }
    
    @Override
    protected void loadDefault() {
    }
    
    public boolean load(final ESubject subject) {
    	if (subject.getSubjectData() instanceof ESubjectData) return true;
    	
		ConfigurationNode configSubject = this.get(subject.getIdentifier());
		ESubjectData dataSubject = (ESubjectData) subject.getSubjectData();
		
		// Si le fichier de configuration existe
		if (!configSubject.isVirtual()) {
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
				String subgroups = (subject instanceof EGroupSubject) ? "inheritances" : "subgroups";
				for (String subgroup : configSubject.getNode(subgroups).getList(TypeToken.of(String.class))) {
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
			if (subject instanceof EUserSubject && groups != null) {
				Subject group = this.plugin.getService().getGroupSubjects().loadSubject(groups).join();
				if (group != null) {
					dataSubject.addParentExecute(this.typeWorld, group.asSubjectReference());
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
			return true;
		}
		return false;
    }
    
    public boolean load(Set<ESubject> subjects) {
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
    
    public boolean addParent(final ESubjectData subject, final SubjectReference parent, boolean insert) {
		this.get(subject.getIdentifier() + ".group").setValue(parent.getSubjectIdentifier());
		this.plugin.getELogger().debug("Added to the configs file : (identifier='" + subject + "';group='" + parent.getSubjectIdentifier() + "';type='" + this.typeWorld + "')");
		return this.save(true);
    }

    public boolean removeParent(final ESubjectData subject, final SubjectReference parent) {
		this.get(subject.getIdentifier()).removeChild("group");
		this.plugin.getELogger().debug("Removed from configs file : (identifier='" + subject + "';group='" + parent.getSubjectIdentifier() + "';type='" + this.typeWorld + "')");
		return this.save(true);
    }
    
    public boolean clearParents(final ESubjectData subject) {
		this.get(subject.getIdentifier()).removeChild("group");
		this.plugin.getELogger().debug("Removed the group configuration file : (identifier='" + subject + "';type='" + this.typeWorld + "')");
		return this.save(true);
    }
    
    /*
     * SubGroups
     */
    
    public boolean addSubParent(final ESubjectData subject, final String world, final SubjectReference parent) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			List<String> subgroups;
			try {
				subgroups = new ArrayList<String>(conf.get().get(subject + ".subgroups").getList(TypeToken.of(String.class)));
				subgroups.add(parent.getSubjectIdentifier());
				conf.get().get(subject + ".subgroups").setValue(subgroups);
				this.plugin.getELogger().debug("Added to the configs file : (identifier='" + subject + "';subgroup='" + parent.getSubjectIdentifier() + "';type='" + world + "')");
				this.plugin.getManagerData().saveUser(world);
				return true;
			} catch (ObjectMappingException e) {}
    	}
    	return false;
    }

    public boolean removeSubParent(final ESubjectData subject, final String world, final SubjectReference parent) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			List<String> subgroups;
			try {
				subgroups = new ArrayList<String>(conf.get().get(subject + ".subgroups").getList(TypeToken.of(String.class)));
				subgroups.remove(parent.getSubjectIdentifier());
				if (subgroups.isEmpty()) {
					conf.get().get(subject.getIdentifier()).removeChild("subgroups");
				} else {
					conf.get().get(subject + ".subgroups").setValue(subgroups);
				}
				this.plugin.getELogger().debug("Removed from configs file : (identifier='" + subject + "';subgroup='" + parent.getSubjectIdentifier() + "';type='" + world + "')");
				this.plugin.getManagerData().saveUser(world);
				return true;
			} catch (ObjectMappingException e) {}
		}
        return false;
    }
    
    public boolean clearSubParents(final ESubjectData subject, final String world) {
		Optional<EPConfUsers> users = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (users.isPresent()) {
			users.get().get(subject.getIdentifier()).removeChild("subgroups");
			this.plugin.getELogger().debug("Removed the subgroups configuration file : (identifier='" + subject + "';type='" + world + "')");
			this.plugin.getManagerData().saveUser(world);
			return true;
		}
        return false;
    }

    public boolean clearSubParents(final ESubjectData subject) {
    	// Pour tous les types de joueur
    	for (Entry<String, EPConfUsers> conf : this.plugin.getManagerData().getConfUsers().entrySet()) {
    		conf.getValue().get(subject.getIdentifier()).removeChild("group");
		}
    	this.plugin.getELogger().debug("Removed the subgroups configuration file : (identifier='" + subject + "')");
    	this.plugin.getManagerData().saveUsers();
		return true;
    }

	public boolean setFriendlyIdentifier(ESubject subject, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean setDefault(EGroupSubject subject) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addParent(ESubjectData subject, SubjectReference parent) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setGroup(ESubjectData subject, SubjectReference parent, boolean insert) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasSubject(String identifier) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<String> getAllIdentifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<SubjectReference, Boolean> getAllWithPermission(String permission) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setDefault(EGroupSubject subject, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}
}