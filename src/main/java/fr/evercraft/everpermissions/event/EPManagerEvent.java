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

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.ESpongeEventFactory;
import fr.evercraft.everapi.event.PermGroupEvent;
import fr.evercraft.everapi.event.PermOtherEvent;
import fr.evercraft.everapi.event.PermSystemEvent;
import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everpermissions.EverPermissions;

public class EPManagerEvent {
	private EverPermissions plugin;
	
	public EPManagerEvent(final EverPermissions plugin) {
		this.plugin = plugin;
	}
	
	public Cause getCause() {
		return Cause.source(this.plugin).build();
	}
	
	public boolean post(final PermSystemEvent.Action action) {
		this.plugin.getELogger().debug("Event PermSystemEvent : (Action='" + action.name() +"')");
		
		if (action.equals(PermSystemEvent.Action.RELOADED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermSystemEventReloaded(this.getCause()));
		} else if (action.equals(PermSystemEvent.Action.DEFAULT_GROUP_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermSystemEventDefault(this.getCause()));
		}
		return false;
	}
	
	public boolean post(final Subject subject, final PermUserEvent.Action action) {
		Optional<EPlayer> player = Optional.empty();
		if (subject.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_USER)) {
    		try {
    			player = plugin.getEServer().getEPlayer(UUID.fromString(subject.getIdentifier()));
    		} catch(IllegalArgumentException e) {}
    	}
		return this.post(subject, player, action);
	}
	
	public boolean post(final Subject subject, Optional<EPlayer> player, final PermUserEvent.Action action) {
		this.plugin.getELogger().debug("Event PermUserEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		
		if (action.equals(PermUserEvent.Action.USER_ADDED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermUserEventAdd(subject, player, this.getCause()));
		} else if (action.equals(PermUserEvent.Action.USER_REMOVED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermUserEventRemove(subject, player, this.getCause()));
		} else if (action.equals(PermUserEvent.Action.USER_PERMISSION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermUserEventPermission(subject, player, this.getCause()));
		} else if (action.equals(PermUserEvent.Action.USER_OPTION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermUserEventOption(subject, player, this.getCause()));
		} else if (action.equals(PermUserEvent.Action.USER_GROUP_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermUserEventGroup(subject, player, this.getCause()));
		} else if (action.equals(PermUserEvent.Action.USER_SUBGROUP_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermUserEventSubGroup(subject, player, this.getCause()));
		}
		return false;
	}
	
	public boolean post(final Subject subject, final PermGroupEvent.Action action) {
		this.plugin.getELogger().debug("Event PermGroupEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		
		if (action.equals(PermGroupEvent.Action.GROUP_ADDED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermGroupEventAdd(subject, this.getCause()));
		} else if (action.equals(PermGroupEvent.Action.GROUP_REMOVED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermGroupEventRemove(subject, this.getCause()));
		} else if (action.equals(PermGroupEvent.Action.GROUP_PERMISSION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermGroupEventPermission(subject, this.getCause()));
		} else if (action.equals(PermGroupEvent.Action.GROUP_INHERITANCE_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermGroupEventInheritance(subject, this.getCause()));
		} else if (action.equals(PermGroupEvent.Action.GROUP_OPTION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermGroupEventOption(subject, this.getCause()));
		}
		return false;
	}
	
	public boolean post(final Subject subject, final PermOtherEvent.Action action) {
		this.plugin.getELogger().debug("Event PermOtherEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		
		if (action.equals(PermOtherEvent.Action.OTHER_ADDED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermOtherEventAdd(subject, this.getCause()));
		} else if (action.equals(PermOtherEvent.Action.OTHER_REMOVED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermOtherEventRemove(subject, this.getCause()));
		} else if (action.equals(PermOtherEvent.Action.OTHER_PERMISSION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermOtherEventPermission(subject, this.getCause()));
		} else if (action.equals(PermOtherEvent.Action.OTHER_INHERITANCE_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermOtherEventInheritance(subject, this.getCause()));
		} else if (action.equals(PermOtherEvent.Action.OTHER_OPTION_CHANGED)) {
			return this.plugin.getGame().getEventManager().post(ESpongeEventFactory.createPermOtherEventOption(subject, this.getCause()));
		}
		return false;
	}
}
