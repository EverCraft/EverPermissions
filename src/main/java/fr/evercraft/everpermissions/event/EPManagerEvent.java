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
package fr.evercraft.everpermissions.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermGroupEvent;
import fr.evercraft.everapi.event.PermOtherEvent;
import fr.evercraft.everapi.event.PermSystemEvent;
import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.event.group.EPermGroupAddEvent;
import fr.evercraft.everpermissions.event.group.EPermGroupInheritanceEvent;
import fr.evercraft.everpermissions.event.group.EPermGroupOptionEvent;
import fr.evercraft.everpermissions.event.group.EPermGroupPermissionEvent;
import fr.evercraft.everpermissions.event.group.EPermGroupRemoveEvent;
import fr.evercraft.everpermissions.event.other.EPermOtherAddEvent;
import fr.evercraft.everpermissions.event.other.EPermOtherInheritanceEvent;
import fr.evercraft.everpermissions.event.other.EPermOtherOptionEvent;
import fr.evercraft.everpermissions.event.other.EPermOtherPermissionEvent;
import fr.evercraft.everpermissions.event.other.EPermOtherRemoveEvent;
import fr.evercraft.everpermissions.event.system.EPermSystemDefaultEvent;
import fr.evercraft.everpermissions.event.system.EPermSystemReloadEvent;
import fr.evercraft.everpermissions.event.user.EPermUserAddEvent;
import fr.evercraft.everpermissions.event.user.EPermUserGroupEvent;
import fr.evercraft.everpermissions.event.user.EPermUserOptionEvent;
import fr.evercraft.everpermissions.event.user.EPermUserPermissionEvent;
import fr.evercraft.everpermissions.event.user.EPermUserRemoveEvent;
import fr.evercraft.everpermissions.event.user.EPermUserSubGroupEvent;

public class EPManagerEvent {
	private EverPermissions plugin;
	
	public EPManagerEvent(final EverPermissions plugin) {
		this.plugin = plugin;
	}
	
	public Cause getCause() {
		return Cause.source(this.plugin).build();
	}
	
	public boolean post(final PermSystemEvent.Action action) {
		this.plugin.getLogger().debug("Event PermSystemEvent : (Action='" + action.name() +"')");
		
		if(action.equals(PermSystemEvent.Action.RELOADED)) {
			return this.plugin.getGame().getEventManager().post(new EPermSystemReloadEvent(this.getCause()));
		} else if(action.equals(PermSystemEvent.Action.DEFAULT_GROUP_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermSystemDefaultEvent(this.getCause()));
		}
		return false;
	}
	
	public boolean post(final Subject subject, final PermUserEvent.Action action) {
		this.plugin.getLogger().debug("Event PermUserEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		
		if(action.equals(PermUserEvent.Action.USER_ADDED)) {
			return this.plugin.getGame().getEventManager().post(new EPermUserAddEvent(subject, this.getCause(), this.plugin));
		} else if(action.equals(PermUserEvent.Action.USER_REMOVED)) {
			return this.plugin.getGame().getEventManager().post(new EPermUserRemoveEvent(subject, this.getCause(), this.plugin));
		} else if(action.equals(PermUserEvent.Action.USER_PERMISSION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermUserPermissionEvent(subject, this.getCause(), this.plugin));
		} else if(action.equals(PermUserEvent.Action.USER_OPTION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermUserOptionEvent(subject, this.getCause(), this.plugin));
		} else if(action.equals(PermUserEvent.Action.USER_GROUP_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermUserGroupEvent(subject, this.getCause(), this.plugin));
		} else if(action.equals(PermUserEvent.Action.USER_SUBGROUP_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermUserSubGroupEvent(subject, this.getCause(), this.plugin));
		}
		return false;
	}
	
	public boolean post(final Subject subject, final PermGroupEvent.Action action) {
		this.plugin.getLogger().debug("Event PermGroupEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		
		if(action.equals(PermGroupEvent.Action.GROUP_ADDED)) {
			return this.plugin.getGame().getEventManager().post(new EPermGroupAddEvent(subject, this.getCause()));
		} else if(action.equals(PermGroupEvent.Action.GROUP_REMOVED)) {
			return this.plugin.getGame().getEventManager().post(new EPermGroupRemoveEvent(subject, this.getCause()));
		} else if(action.equals(PermGroupEvent.Action.GROUP_PERMISSION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermGroupPermissionEvent(subject, this.getCause()));
		} else if(action.equals(PermGroupEvent.Action.GROUP_INHERITANCE_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermGroupInheritanceEvent(subject, this.getCause()));
		} else if(action.equals(PermGroupEvent.Action.GROUP_OPTION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermGroupOptionEvent(subject, this.getCause()));
		}
		return false;
	}
	
	public boolean post(final Subject subject, final PermOtherEvent.Action action) {
		this.plugin.getLogger().debug("Event PermOtherEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		
		if(action.equals(PermOtherEvent.Action.OTHER_ADDED)) {
			return this.plugin.getGame().getEventManager().post(new EPermOtherAddEvent(subject, this.getCause()));
		} else if(action.equals(PermOtherEvent.Action.OTHER_REMOVED)) {
			return this.plugin.getGame().getEventManager().post(new EPermOtherRemoveEvent(subject, this.getCause()));
		} else if(action.equals(PermOtherEvent.Action.OTHER_PERMISSION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermOtherPermissionEvent(subject, this.getCause()));
		} else if(action.equals(PermOtherEvent.Action.OTHER_INHERITANCE_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermOtherInheritanceEvent(subject, this.getCause()));
		} else if(action.equals(PermOtherEvent.Action.OTHER_OPTION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(new EPermOtherOptionEvent(subject, this.getCause()));
		}
		return false;
	}
}
