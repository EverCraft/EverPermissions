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
package fr.evercraft.everpermissions.service.permission.data.other;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.event.PermOtherEvent.Action;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.data.EOptionSubjectData;

public class EOtherData extends EOptionSubjectData {
    public EOtherData(final EverPermissions plugin, final Subject subject) {
    	super(plugin, subject);
    	
    	load();
    }
    
    public void reload() {  
    	super.reload();
    	load();
    }
    
    public void load() {    	
		ConfigurationNode user = this.plugin.getManagerData().getConfOther().get(subject.getIdentifier());
		if (user.getValue() != null) {
			Set<Context> contexts = new HashSet<Context>();
			
			// Chargement des permissions
			for (Entry<Object, ? extends ConfigurationNode> permission : user.getNode("permissions").getChildrenMap().entrySet()) {
    			if (permission.getKey() instanceof String && permission.getValue().getValue() instanceof Boolean) {
    				this.setPermissionExecute(contexts, (String) permission.getKey(), Tristate.fromBoolean(permission.getValue().getBoolean(false)));
    				this.plugin.getELogger().debug("Loading : ("
    						+ "identifier=" + subject.getIdentifier() + ";"
    						+ "permission=" + permission.getKey().toString() + ";"
    						+ "value=" + permission.getValue().getBoolean(false) + ")");
    			} else {
    				this.plugin.getELogger().warn("Loading error : (identifier=" + subject.getIdentifier() + ";permission=" + permission.getKey().toString() + ")");
    			}
    		}
			
			// Chargement des options
			for (Entry<Object, ? extends ConfigurationNode> option : user.getNode("options").getChildrenMap().entrySet()) {
				String value = option.getValue().getString(null);
    			if (option.getKey() instanceof String && value != null) {
    				this.setOptionExecute(contexts, (String) option.getKey(), value);
    				this.plugin.getELogger().debug("Loading : ("
    						+ "identifier=" + subject.getIdentifier() + ";"
    						+ "option=" + option.getKey() + ";"
    						+ "name=" + value + ";)");
    			} else {
    				this.plugin.getELogger().warn("Loading error : (identifier=" + subject.getIdentifier() + ";option=" + option.getValue().toString() + ";)");
    			}
    		}
		}
    }
    
    /*
     * Permissions
     */
    
    public boolean setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
    	checkNotNull(permission, "permission");
    	checkNotNull(value, "value");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.setPermissionExecute(contexts, permission, value)) {
    		ConfigurationNode permissions = this.plugin.getManagerData().getConfOther().getNode().getNode(subject.getIdentifier(), "permissions");
    		// Si le fichier de configuration existe
    		if (value.equals(Tristate.UNDEFINED)) {
    			permissions.removeChild(permission);
    			this.plugin.getELogger().debug("Inclusion in the database (identifier='" + subject.getIdentifier() + "';permission='" + permission + "')");
    		} else {
    			permissions.getNode(permission).setValue(value.asBoolean());
    			this.plugin.getELogger().warn("Added to the configs file : ("
	    					+ "identifier=" + subject.getIdentifier() + ";"
	    					+ "permission=" + permission + ";"
	    					+ "value=" + value.asBoolean() + ")");
    		}
    		
    		this.plugin.getManagerEvent().post(this.subject, Action.OTHER_PERMISSION_CHANGED);
    		this.plugin.getManagerData().saveOther();
    	}
        return false;
    }
    
    @Override
    public boolean clearPermissions(final Set<Context> contexts) {
        return clearPermissions();
    }
    
    @Override
    public boolean clearPermissions() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearPermissionsExecute()) {
    		this.plugin.getManagerData().getConfOther().get(subject.getIdentifier()).removeChild("permissions");
			this.plugin.getELogger().debug("Removed the permissions configuration file : (" + "identifier=" + subject.getIdentifier() + ")");
			
			this.plugin.getManagerEvent().post(this.subject, Action.OTHER_PERMISSION_CHANGED);
			this.plugin.getManagerData().saveOther();
			return true;
    	}
        return false;
    }

    /*
     * Groups
     */

    public boolean addParent(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
    	if (this.addParentExecute(contexts, parent)) {
    		this.plugin.getManagerEvent().post(this.subject, Action.OTHER_INHERITANCE_CHANGED);
			return true;
        }
        return false;
    }
    
    public boolean removeParent(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
    	if (this.removeParentExecute(contexts, parent)) {
    		this.plugin.getManagerEvent().post(this.subject, Action.OTHER_INHERITANCE_CHANGED);
			return true;
        }
        return false;
    }

    public boolean clearParents(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
    	if (this.clearParentsExecute(contexts)) {
    		this.plugin.getManagerEvent().post(this.subject, Action.OTHER_INHERITANCE_CHANGED);
			return true;
        }
        return false;
    }
    
    public boolean clearParents() {
    	if (this.clearParentsExecute()) {
    		this.plugin.getManagerEvent().post(this.subject, Action.OTHER_INHERITANCE_CHANGED);
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
    		ConfigurationNode options = this.plugin.getManagerData().getConfOther().getNode().getNode(subject.getIdentifier(), "options");
			if (value == null) {
				options.removeChild(option);
				this.plugin.getELogger().debug("Removed from configs file : (identifier='" + subject.getIdentifier() + "';option='" + option + "')");
			} else {
				options.getNode(option).setValue(value);
				this.plugin.getELogger().debug("Added to the configs file : (identifier='" + subject.getIdentifier() + "';option='" + option + "';value='" + value + "')");
			}
			
    		this.plugin.getManagerEvent().post(this.subject, Action.OTHER_OPTION_CHANGED);
    		this.plugin.getManagerData().saveOther();
			return true;
    	}
        return false;
    }
    
    @Override
    public boolean clearOptions(final Set<Context> contexts) {
        return clearOptions();
    }

    @Override
    public boolean clearOptions() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if (this.clearOptionsExecute()) {
    		this.plugin.getManagerData().getConfOther().getNode().getNode(subject.getIdentifier()).removeChild("options");
			this.plugin.getELogger().debug("Removed the options configuration file : (identifier=" + subject.getIdentifier() + ")");
			
    		this.plugin.getManagerEvent().post(this.subject, Action.OTHER_OPTION_CHANGED);
    		this.plugin.getManagerData().saveOther();
			return true;
        }
        return false;
    }
}
