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
package fr.evercraft.everpermissions.event.group;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermGroupEvent;

public class EPermGroupEvent implements PermGroupEvent {

    private final Subject subject;
    private final Cause cause;
    private final Action action;

    public EPermGroupEvent(final Subject subject, final Cause cause, final Action action) {    	
        this.subject = subject;
        this.cause = cause;
        this.action = action;
    }

    @Override
    public Subject getSubject() {
        return this.subject;
    }
    
    @Override
    public Action getAction() {
        return this.action;
    }

	@Override
	public Cause getCause() {
		return this.cause;
	}
}
