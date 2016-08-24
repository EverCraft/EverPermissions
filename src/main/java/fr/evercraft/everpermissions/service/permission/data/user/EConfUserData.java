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
package fr.evercraft.everpermissions.service.permission.data.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import com.google.common.reflect.TypeToken;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.data.EPConfUsers;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EConfUserData implements IUserData {
	private final EverPermissions plugin;

    public EConfUserData(final EverPermissions plugin) {
        this.plugin = plugin;
    }
    
    public void load(final EUserData subject) {
    	// Pour tous les types de joueur
    	for (Entry<String, EPConfUsers> world : this.plugin.getManagerData().getConfUsers().entrySet()) {
    		ConfigurationNode user = world.getValue().get(subject.getIdentifier());
    		// Si le fichier de configuration existe
    		if (user.getValue() != null) {
    			Set<Context> contexts = EContextCalculator.getContextWorld(world.getKey());
    			// Chargement des permissions
    			for (Entry<Object, ? extends ConfigurationNode> permission : user.getNode("permissions").getChildrenMap().entrySet()) {
	    			if (permission.getKey() instanceof String && permission.getValue().getValue() instanceof Boolean) {
	    				subject.setPermissionExecute(contexts, (String) permission.getKey(), Tristate.fromBoolean(permission.getValue().getBoolean(false)));
	    				this.plugin.getLogger().debug("Loading : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "permission=" + permission.getKey().toString() + ";"
	    						+ "value=" + permission.getValue().getBoolean(false) + ";"
	    						+ "type=" + world.getKey() + ")");
	    			} else {
	    				this.plugin.getLogger().warn("Loading error : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "permission=" + permission.getKey().toString() + ";"
	    						+ "type=" + world.getKey() + ")");
	    			}
	    		}
    			
    			// Chargement des options
    			for (Entry<Object, ? extends ConfigurationNode> option : user.getNode("options").getChildrenMap().entrySet()) {
    				String value = option.getValue().getString(null);
	    			if (option.getKey() instanceof String && value != null) {
	    				subject.setOptionExecute(contexts, (String) option.getKey(), value);
	    				this.plugin.getLogger().debug("Loading : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "option=" + option.getKey() + ";"
	    						+ "name=" + value + ";"
	    						+ "type=" + world.getKey() + ")");
	    			} else {
	    				this.plugin.getLogger().warn("Loading error : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "option=" + option.getValue().toString() + ";"
	    						+ "type=" + world.getKey() + ")");
	    			}
	    		}
    			
    			// Chargement les sous-groupes
    			try {
					for (String subgroup : user.getNode("subgroups").getList(TypeToken.of(String.class))) {
						EGroupSubject group = this.plugin.getService().getGroupSubjects().get(subgroup);
						if (group != null) {
							subject.addSubParentExecute(contexts, group);
							this.plugin.getLogger().debug("Loading : ("
		    						+ "identifier=" + subject.getIdentifier() + ";"
		    						+ "subgroup=" + group.getIdentifier() + ";"
		    						+ "type=" + world.getKey() + ")");
						} else {
							this.plugin.getLogger().warn("Loading error : ("
									+ "identifier=" + subject.getIdentifier() + ";"
									+ "subgroup=" + subgroup + ";"
									+ "type=" + world.getKey() + ")");
						}
					}
				} catch (ObjectMappingException e) {}
    			
    			// Chargement du groupe
    			String groups = user.getNode("group").getString(null);
    			if (groups != null) {
    				EGroupSubject group = this.plugin.getService().getGroupSubjects().get(groups);
    				if (group != null) {
    					subject.addParentExecute(contexts, group);
    					this.plugin.getLogger().debug("Loading : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "group=" + group.getIdentifier() + ";"
	    						+ "type=" + world.getKey() + ")");
	    			} else {
	    				this.plugin.getLogger().warn("Loading error : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "group=" + group + ";"
	    						+ "type=" + world.getKey() + ")");
	    			}
    			}
    		}
    	}
    }
    
    /*
     * Permissions
     */

    public boolean setPermission(final String subject, final String world, final String permission, final Tristate value, final boolean insert) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			ConfigurationNode permissions = conf.get().getNode().getNode(subject, "permissions");
			// Supprime une permission
			if (value.equals(Tristate.UNDEFINED)) {
				permissions.removeChild(permission);
				this.plugin.getLogger().debug("Removed from configs file : (identifier='" + subject + "';permission='" + permission + "';type='" + world + "')");
			// Ajoute une permission
			} else {
				permissions.getNode(permission).setValue(value.asBoolean());
				this.plugin.getLogger().debug("Added to the configs file : (identifier='" + subject + "';permission='" + permission + "';value='" + value.asBoolean() + "';type='" + world + "')");
			}
			this.plugin.getManagerData().saveUser(world);
			return true;
		}
    	return false;
    }

    public boolean clearPermissions(final String subject, final String world) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			conf.get().get(subject).removeChild("permissions");
			
			this.plugin.getLogger().debug("Removed the permissions configuration file : (identifier='" + subject + "';type='" + world + "')");
			this.plugin.getManagerData().saveUser(world);
			return true;
		}
        return false;
    }
    
    public boolean clearPermissions(final String subject) {
    	// Pour tous les types de joueur
		for (Entry<String, EPConfUsers> conf : this.plugin.getManagerData().getConfUsers().entrySet()) {
			conf.getValue().get(subject).removeChild("permissions");
		}
		this.plugin.getLogger().debug("Removed the permissions configuration file : (identifier='" + subject + "')");
		this.plugin.getManagerData().saveUsers();
		return true;
    }
    
    /*
     * Options
     */
    
    public boolean setOption(final String subject, final String world, final String option, final String value, final boolean insert) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			ConfigurationNode options = conf.get().get(subject + ".options");
			if (value == null) {
				options.removeChild(option);
				this.plugin.getLogger().debug("Removed from configs file : (identifier='" + subject + "';option='" + option + "';type='" + world + "')");
			} else {
				options.getNode(option).setValue(value);
				this.plugin.getLogger().debug("Added to the configs file : (identifier='" + subject + "';option='" + option + "';value='" + value + "';type='" + world + "')");
			}
			this.plugin.getManagerData().saveUser(world);
			return true;
    	}
        return false;
    }

    public boolean clearOptions(final String subject, final String world) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			conf.get().get(subject).removeChild("options");
			this.plugin.getLogger().debug("Removed the options configuration file : (identifier='" + subject + "';type='" + world + "')");
			this.plugin.getManagerData().saveUser(world);
			return true;
		}
        return false;
    }

    public boolean clearOptions(final String subject) {
    	// Pour tous les types de joueur
		for (Entry<String, EPConfUsers> world : this.plugin.getManagerData().getConfUsers().entrySet()) {
			world.getValue().get(subject).removeChild("options");
		}
		this.plugin.getLogger().debug("Removed the options configuration file : (identifier='" + subject + "')");
		this.plugin.getManagerData().saveUsers();
		return true;
    }

    /*
     * Groups
     */
    
    public boolean addParent(final String subject, final String world, final String parent) {
		Optional<EPConfUsers> users = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (users.isPresent()) {
			users.get().get(subject + ".group").setValue(parent);
			this.plugin.getLogger().debug("Added to the configs file : (identifier='" + subject + "';group='" + parent + "';type='" + world + "')");
			this.plugin.getManagerData().saveUser(world);
			return true;
		}
    	return false;
    }

    public boolean removeParent(final String subject, final String world, final String parent) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			conf.get().get(subject).removeChild("group");
			this.plugin.getLogger().debug("Removed from configs file : (identifier='" + subject + "';group='" + parent + "';type='" + world + "')");
			this.plugin.getManagerData().saveUser(world);
			return true;
		}
        return false;
    }
    
    public boolean clearParents(final String subject, final String world) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			conf.get().get(subject).removeChild("group");
			this.plugin.getLogger().debug("Removed the group configuration file : (identifier='" + subject + "';type='" + world + "')");
			this.plugin.getManagerData().saveUser(world);
			return true;
    	}
        return false;
    }

    public boolean clearParents(final String subject) {
    	// Pour tous les types de joueur
		for (Entry<String, EPConfUsers> conf : this.plugin.getManagerData().getConfUsers().entrySet()) {
			conf.getValue().get(subject).removeChild("group");
		}
		this.plugin.getLogger().debug("Removed the group configuration file : (identifier='" + subject + "')");
		this.plugin.getManagerData().saveUsers();
        return true;
    }
    
    /*
     * SubGroups
     */
    
    public boolean addSubParent(final String subject, final String world, final String parent) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			List<String> subgroups;
			try {
				subgroups = new ArrayList<String>(conf.get().get(subject + ".subgroups").getList(TypeToken.of(String.class)));
				subgroups.add(parent);
				conf.get().get(subject + ".subgroups").setValue(subgroups);
				this.plugin.getLogger().debug("Added to the configs file : (identifier='" + subject + "';subgroup='" + parent + "';type='" + world + "')");
				this.plugin.getManagerData().saveUser(world);
				return true;
			} catch (ObjectMappingException e) {}
    	}
    	return false;
    }

    public boolean removeSubParent(final String subject, final String world, final String parent) {
		Optional<EPConfUsers> conf = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (conf.isPresent()) {
			List<String> subgroups;
			try {
				subgroups = new ArrayList<String>(conf.get().get(subject + ".subgroups").getList(TypeToken.of(String.class)));
				subgroups.remove(parent);
				if (subgroups.isEmpty()) {
					conf.get().get(subject).removeChild("subgroups");
				} else {
					conf.get().get(subject + ".subgroups").setValue(subgroups);
				}
				this.plugin.getLogger().debug("Removed from configs file : (identifier='" + subject + "';subgroup='" + parent + "';type='" + world + "')");
				this.plugin.getManagerData().saveUser(world);
				return true;
			} catch (ObjectMappingException e) {}
		}
        return false;
    }
    
    public boolean clearSubParents(final String subject, final String world) {
		Optional<EPConfUsers> users = this.plugin.getManagerData().getConfUser(world);
		// Si le fichier de configuration existe
		if (users.isPresent()) {
			users.get().get(subject).removeChild("subgroups");
			this.plugin.getLogger().debug("Removed the subgroups configuration file : (identifier='" + subject + "';type='" + world + "')");
			this.plugin.getManagerData().saveUser(world);
			return true;
		}
        return false;
    }

    public boolean clearSubParents(final String subject) {
    	// Pour tous les types de joueur
    	for (Entry<String, EPConfUsers> conf : this.plugin.getManagerData().getConfUsers().entrySet()) {
    		conf.getValue().get(subject).removeChild("group");
		}
    	this.plugin.getLogger().debug("Removed the subgroups configuration file : (identifier='" + subject + "')");
    	this.plugin.getManagerData().saveUsers();
		return true;
    }
}
