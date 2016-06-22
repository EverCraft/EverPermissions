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

import java.util.Set;

import javax.annotation.Nullable;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.event.PermGroupEvent.Action;
import fr.evercraft.everpermissions.EverPermissions;

public class ETransientGroupData extends MemorySubjectData {
	private final EverPermissions plugin;
	
	private final Subject subject;
	
	public ETransientGroupData(final EverPermissions plugin, final Subject subject) {
		super(plugin.getService());
		
		this.plugin = plugin;
		this.subject = subject;
	}

	/*
	 * Permissions
	 */
	
    @Override
    public boolean setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
        if(super.setPermission(contexts, permission, value)) {
        	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_PERMISSION_CHANGED);
        	return true;
        }
        return false;
    }
    
    @Override
    public boolean clearPermissions(final Set<Context> context) {
    	 if(super.clearPermissions(context)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_PERMISSION_CHANGED);
         	return true;
         }
         return false;
    }

    @Override
    public boolean clearPermissions() {
    	 if(super.clearPermissions()) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_PERMISSION_CHANGED);
         	return true;
         }
         return false;
    }
    
    /*
     * Inheritances
     */

    @Override
    public boolean addParent(final Set<Context> contexts, final Subject parent) {
    	if(super.addParent(contexts, parent)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean removeParent(final Set<Context> contexts, final Subject parent) {
    	if(super.addParent(contexts, parent)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
         	return true;
        }
    	return false;
    }
    
    @Override
    public boolean clearParents(final Set<Context> contexts) {
    	if(super.clearParents(contexts)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean clearParents() {
    	if(super.clearParents()) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_INHERITANCE_CHANGED);
         	return true;
        }
    	return false;
    }

    /*
     * Options
     */
    
    @Override
    public boolean setOption(final Set<Context> contexts, final String key, final @Nullable String value) {
    	if(super.setOption(contexts, key, value)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_OPTION_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean clearOptions(final Set<Context> contexts) {
    	if(super.clearOptions(contexts)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_OPTION_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean clearOptions() {
    	if(super.clearOptions()) {
         	this.plugin.getManagerEvent().post(this.subject, Action.GROUP_OPTION_CHANGED);
         	return true;
        }
    	return false;
    }
}
