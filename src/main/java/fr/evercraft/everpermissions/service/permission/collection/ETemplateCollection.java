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
package fr.evercraft.everpermissions.service.permission.collection;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.ETemplateSubject;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectReference;

import com.google.common.base.Preconditions;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ETemplateCollection extends ESubjectCollection<ETemplateSubject> {

    public ETemplateCollection(final EverPermissions plugin) {
    	super(plugin, PermissionService.SUBJECTS_ROLE_TEMPLATE);
    }
    
	@Override
	protected ETemplateSubject add(String identifier) {
    	return new ETemplateSubject(this.plugin, identifier, this);
	}
	
	@Override
	public void suggestUnload(String identifier) {}
	
	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
		Preconditions.checkNotNull(contexts, "contexts");
		Preconditions.checkNotNull(permission, "permission");
		
		return this.getAllWithPermission(this.plugin.getService().getContextCalculator().getUser(contexts), permission);
	}
}
