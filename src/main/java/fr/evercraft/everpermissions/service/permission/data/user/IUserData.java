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
package fr.evercraft.everpermissions.service.permission.data.user;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

public interface IUserData {
    
    public CompletableFuture<Boolean> load(EUserData subject);
    
    /*
     * Permissions
     */

    public CompletableFuture<Boolean> setPermission(final String subject, final String world, final String  permission, final Tristate value, final boolean insert);

    public CompletableFuture<Boolean> clearPermissions(final String subject, final String world);
    
    public CompletableFuture<Boolean> clearPermissions(final String subject);
    
    /*
     * Options
     */
    
    public CompletableFuture<Boolean> setOption(final String subject, final String world, final String type, final String name, final boolean insert);

    public CompletableFuture<Boolean> clearOptions(final String subject, final String world);

    public CompletableFuture<Boolean> clearOptions(final String subject);

    /*
     * Groups
     */
    
    public CompletableFuture<Boolean> addParent(final String subject, final String world, final SubjectReference parent, final boolean insert);

    public CompletableFuture<Boolean> removeParent(final String subject, final String world, final SubjectReference parent);

    public CompletableFuture<Boolean> clearParents(final String subject, final String world);

    public CompletableFuture<Boolean> clearParents(final String subject);
    
    /*
     * SubGroups
     */
    
    public CompletableFuture<Boolean> addSubParent(final String subject, final String world, final SubjectReference parent);

    public CompletableFuture<Boolean> removeSubParent(final String subject, final String world, final SubjectReference parent);
    
    public CompletableFuture<Boolean> clearSubParents(final String subject, final String world);

    public CompletableFuture<Boolean> clearSubParents(final String subject);
}
