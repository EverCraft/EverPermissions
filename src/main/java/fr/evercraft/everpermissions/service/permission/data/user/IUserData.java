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
package fr.evercraft.everpermissions.service.permission.data.user;

import org.spongepowered.api.util.Tristate;

public interface IUserData {
    
    public void load(EUserData subject);
    
    /*
     * Permissions
     */

    public boolean setPermission(final String subject, final String world, final String  permission, final Tristate value, final boolean insert);

    public boolean clearPermissions(final String subject, final String world);
    
    public boolean clearPermissions(final String subject);
    
    /*
     * Options
     */
    
    public boolean setOption(final String subject, final String world, final String type, final String name, final boolean insert);

    public boolean clearOptions(final String subject, final String world);

    public boolean clearOptions(final String subject);

    /*
     * Groups
     */
    
    public boolean addParent(final String subject, final String world, final String parent);

    public boolean removeParent(final String subject, final String world, final String parent);

    public boolean clearParents(final String subject, final String world);

    public boolean clearParents(final String subject);
    
    /*
     * SubGroups
     */
    
    public boolean addSubParent(final String subject, final String world, final String parent);

    public boolean removeSubParent(final String subject, final String world, final String parent);
    
    public boolean clearSubParents(final String subject, final String world);

    public boolean clearSubParents(final String subject);
}
