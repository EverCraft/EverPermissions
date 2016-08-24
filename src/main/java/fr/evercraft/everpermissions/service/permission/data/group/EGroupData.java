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
package fr.evercraft.everpermissions.service.permission.data.group;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import com.google.common.reflect.TypeToken;

import fr.evercraft.everapi.event.PermGroupEvent.Action;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.data.EPConfGroups;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.data.EOptionSubjectData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EGroupData extends EOptionSubjectData {

    public EGroupData(final EverPermissions plugin, final EGroupSubject subject) {
        super(plugin, subject);
    }

	public void reload() {
		this.clearPermissionsExecute();
		this.clearOptionsExecute();
		this.clearParentsExecute();
    }
    
    /*
     * Permissions
     */
    
    @Override
    public boolean setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(permission, "permission");
    	checkNotNull(value, "value");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.setPermissionExecute(contexts, permission, value)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if (world.isPresent()) {
    			Optional<EPConfGroups> users = this.plugin.getManagerData().getConfGroup(world.get());
    			// Si le fichier de configuration existe
    			if (users.isPresent()) {
    				ConfigurationNode permissions = users.get().getNode().getNode(subject.getIdentifier(), "permissions");
    				// Supprime une permission
    				if (value.equals(Tristate.UNDEFINED)) {
    					permissions.removeChild(permission);
    					this.plugin.getLogger().debug("Removed from configs file : (identifier='" + subject.getIdentifier() + "';permission='" + permission + "';type='" + world.get() + "')");
    				// Ajoute une permission
    				} else {
    					permissions.getNode(permission).setValue(value.asBoolean());
    					this.plugin.getLogger().debug("Added to the configs file : (identifier='" + subject.getIdentifier() + "';permission='" + permission + "';type='" + world.get() + "')");
    				}
    				this.plugin.getManagerEvent().post(this.subject, Action.GROUP_PERMISSION_CHANGED);
    				this.plugin.getManagerData().saveGroup(world.get());
    				return true;
    			}
    		}
    	}
        return false;
    }
    
    @Override
    public boolean clearPermissions(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearPermissionsExecute(contexts)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if (world.isPresent()) {
    			Optional<EPConfGroups> conf = this.plugin.getManagerData().getConfGroup(world.get());
    			// Si le fichier de configuration existe
    			if (conf.isPresent()) {
    				conf.get().getNode().getNode(subject.getIdentifier()).removeChild("permissions");
    				this.plugin.getLogger().debug("Removed the permissions configuration file : (identifier='" + subject.getIdentifier() + "';type='" + world.get() + "')");
    				
    				this.plugin.getManagerEvent().post(this.subject, Action.GROUP_PERMISSION_CHANGED);
    				this.plugin.getManagerData().saveGroup(world.get());
    				return true;
    			}
        	}
    	}
        return false;
    }
    
    @Override
    public boolean clearPermissions() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearPermissionsExecute()) {
    		// Pour tous les types de groupe
    		for (Entry<String, EPConfGroups> world : this.plugin.getManagerData().getConfGroups().entrySet()) {
    			world.getValue().getNode().getNode(subject.getIdentifier()).removeChild("permissions");
    		}
    		this.plugin.getLogger().debug("Removed the permissions configuration file : (identifier='" + subject.getIdentifier() + "')");
    		
			this.plugin.getManagerEvent().post(this.subject, Action.GROUP_PERMISSION_CHANGED);
			this.plugin.getManagerData().saveGroups();
			return true;
    	}
        return false;
    }

    /*
     * Groups
     */
    
    @Override
    public boolean addParent(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");    	
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.addParentExecute(contexts, parent)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if (world.isPresent()) {
    			Optional<EPConfGroups> conf = this.plugin.getManagerData().getConfGroup(world.get());
    			// Si le fichier de configuration existe
    			if (conf.isPresent()) {
					try {
						ConfigurationNode node = conf.get().getNode().getNode(subject.getIdentifier());
						List<String> subgroups = new ArrayList<String>(node.getNode("inheritances").getList(TypeToken.of(String.class)));
						subgroups.add(parent.getIdentifier());
						
						node.getNode("inheritances").setValue(subgroups);
        				this.plugin.getLogger().debug("Added to the configs file : (identifier='" + subject.getIdentifier() + "';inheritance='" + parent.getIdentifier() + "';type='" + world.get() + "')");
        				
        				this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
        				this.plugin.getManagerData().saveGroup(world.get());
        				return true;
					} catch (ObjectMappingException e) {}
    			}
        	}
        }
        return false;
    }
    
    @Override
    public boolean removeParent(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.removeParentExecute(contexts, parent)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if (world.isPresent()) {
    			Optional<EPConfGroups> conf = this.plugin.getManagerData().getConfGroup(world.get());
    			// Si le fichier de configuration existe
    			if (conf.isPresent()) {
					try {
						ConfigurationNode node = conf.get().getNode().getNode(subject.getIdentifier());
						List<String> subgroups =  new ArrayList<String>(node.getNode("inheritances").getList(TypeToken.of(String.class)));
						subgroups.remove(parent.getIdentifier());
						if (subgroups.isEmpty()) {
							node.removeChild("inheritances");
						} else {
							node.getNode("inheritances").setValue(subgroups);
						}
						this.plugin.getLogger().debug("Removed from configs file : (identifier='" + subject.getIdentifier() + "';inheritance='" + parent.getIdentifier() + "';type='" + world.get() + "')");
						
						this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
						this.plugin.getManagerData().saveGroup(world.get());
						return true;
					} catch (ObjectMappingException e) {}
    			}
        	}
        }
        return false;
    }
    
    @Override
    public boolean clearParents(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearParentsExecute(contexts)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if (world.isPresent()) {
    			Optional<EPConfGroups> users = this.plugin.getManagerData().getConfGroup(world.get());
    			// Si le fichier de configuration existe
    			if (users.isPresent()) {
    				users.get().getNode().getNode(subject.getIdentifier()).removeChild("inheritances");
    				this.plugin.getLogger().debug("Removed the inheritances configuration file : (identifier='" + subject.getIdentifier() + "';type='" + world.get() + "')");
    				
    				this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
    				this.plugin.getManagerData().saveGroup(world.get());
    				return true;
    			}
        	}
        }
        return false;
    }
    
    @Override
    public boolean clearParents() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearParentsExecute()) {
    		// Pour tous les types de groupe
    		for (Entry<String, EPConfGroups> world : this.plugin.getManagerData().getConfGroups().entrySet()) {
    			world.getValue().getNode().getNode(subject.getIdentifier()).removeChild("inheritances");
    		}
        	this.plugin.getLogger().debug("Removed the inheritances configuration file : (identifier='" + subject.getIdentifier() + "')");
			this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
			this.plugin.getManagerData().saveGroups();
			return true;
        }
        return false;
    }

    /*
     * Options
     */
    
    @Override
    public boolean setOption(final Set<Context> contexts, final String option, final String value) {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.setOptionExecute(contexts, option, value)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if (world.isPresent()) {
    			Optional<EPConfGroups> conf = this.plugin.getManagerData().getConfGroup(world.get());
    			// Si le fichier de configuration existe
    			if (conf.isPresent()) {
    				ConfigurationNode options = conf.get().getNode().getNode(subject.getIdentifier(), "options");
    				if (value == null) {
    					options.removeChild(option);
    					this.plugin.getLogger().warn("Removed from configs file : (identifier='" + subject.getIdentifier() + "';option='" + option + "';type='" + world.get() + "')");
    				} else {
    					options.getNode(option).setValue(value);
    					this.plugin.getLogger().warn("Added to the configs file : (identifier='" + subject.getIdentifier() + "';option='" + option + "';name='" + value + "';type='" + world.get() + "')");
    				}
    				this.plugin.getManagerEvent().post(this.subject, Action.GROUP_OPTION_CHANGED);
    				this.plugin.getManagerData().saveGroup(world.get());
    				return true;
    			}
        	}
    	}
        return false;
    }

    
    @Override
    public boolean clearOptions(final Set<Context> contexts) {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearOptionsExecute(contexts)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if (world.isPresent()) {
    			Optional<EPConfGroups> conf = this.plugin.getManagerData().getConfGroup(world.get());
    			// Si le fichier de configuration existe
    			if (conf.isPresent()) {
    				conf.get().getNode().getNode(subject.getIdentifier()).removeChild("options");
    				this.plugin.getLogger().warn("Removed the options configuration file : (identifier='" + subject.getIdentifier() + "';type='" + world.get() + "')");
    			}
    			this.plugin.getManagerEvent().post(this.subject, Action.GROUP_OPTION_CHANGED);
    			this.plugin.getManagerData().saveGroup(world.get());
    			return true;
        	}
    	}
        return false;
    }

    @Override
    public boolean clearOptions() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearOptionsExecute()) {
    		// Pour tous les types de groupe
    		for (Entry<String, EPConfGroups> group : this.plugin.getManagerData().getConfGroups().entrySet()) {
    			group.getValue().get(subject.getIdentifier()).removeChild("options");
    		}
    		this.plugin.getLogger().warn("Removed the options configuration file : (identifier='" + subject.getIdentifier() + "')");
    		this.plugin.getManagerEvent().post(this.subject, Action.GROUP_OPTION_CHANGED);
    		this.plugin.getManagerData().saveGroups();
    		return true;
        }
        return false;
    }
}
