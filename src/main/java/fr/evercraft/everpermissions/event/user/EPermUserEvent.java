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
package fr.evercraft.everpermissions.event.user;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;

public class EPermUserEvent implements PermUserEvent {

    private final Subject subject;
    private final Cause cause;
    private final Action action;
    
    private Optional<EPlayer> player;

    public EPermUserEvent(final Subject subject, final Cause cause, final EPlugin plugin, final Action action) {
        this.subject = subject;
        this.action = action;
        this.cause = cause;
        
        if(this.subject.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_USER)) {
    		try {
    			this.player = plugin.getEServer().getEPlayer(UUID.fromString(this.subject.getIdentifier()));
    		} catch(IllegalArgumentException e) {
    			this.player = Optional.empty();
    		}
    	} else {
    		this.player = Optional.empty();
    	}
    }

    public Subject getSubject() {
        return this.subject;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public Optional<EPlayer> getPlayer() {
    	return this.player;
    }

	@Override
	public Cause getCause() {
		return this.cause;
	}
}
