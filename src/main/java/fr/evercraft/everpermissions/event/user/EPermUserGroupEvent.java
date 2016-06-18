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

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.plugin.EPlugin;

public class EPermUserGroupEvent extends EPermUserEvent implements PermUserEvent.Group {

    public EPermUserGroupEvent(final Subject subject, final Cause cause, final EPlugin plugin) {
        super(subject, cause, plugin, Action.USER_GROUP_CHANGED);
    }
}
