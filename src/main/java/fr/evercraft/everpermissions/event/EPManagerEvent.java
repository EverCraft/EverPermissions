/**
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

import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.services.permission.event.PermGroupEvent;
import fr.evercraft.everapi.services.permission.event.PermOtherEvent;
import fr.evercraft.everapi.services.permission.event.PermSystemEvent;
import fr.evercraft.everapi.services.permission.event.PermUserEvent;
import fr.evercraft.everpermissions.EverPermissions;

public class EPManagerEvent {
	private EverPermissions plugin;
	
	public EPManagerEvent(final EverPermissions plugin) {
		this.plugin = plugin;
	}
	
	public boolean post(final PermSystemEvent.Action action) {
		this.plugin.getLogger().debug("Event PermSystemEvent : (Action='" + action.name() +"')");
		return this.plugin.getGame().getEventManager().post(new PermSystemEvent(this.plugin, action));
	}
	
	public boolean post(final Subject subject, final PermUserEvent.Action action) {
		this.plugin.getLogger().debug("Event PermUserEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		return this.plugin.getGame().getEventManager().post(new PermUserEvent(this.plugin, subject, action));
	}
	
	public boolean post(final Subject subject, final PermGroupEvent.Action action) {
		this.plugin.getLogger().debug("Event PermGroupEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		return this.plugin.getGame().getEventManager().post(new PermGroupEvent(this.plugin, subject, action));
	}
	
	public boolean post(final Subject subject, final PermOtherEvent.Action action) {
		this.plugin.getLogger().debug("Event PermOtherEvent : (Subject='" + subject.getIdentifier() + "';Action='" + action.name() +"')");
		return this.plugin.getGame().getEventManager().post(new PermOtherEvent(this.plugin, subject, action));
	}
}
