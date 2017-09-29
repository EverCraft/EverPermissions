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

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectReference;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.services.permission.EUserCollection;
import fr.evercraft.everapi.services.permission.EUserSubject;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EPUserSubject;

public class EPUserCollection extends EPSubjectCollection<EUserSubject> implements EUserCollection {

	public EPUserCollection(final EverPermissions plugin, String collectionIdentifier) {
		super(plugin, collectionIdentifier);
	}
	
	@Override
	protected EPUserSubject add(String identifier) {
		return new EPUserSubject(this.plugin, identifier, this);
	}
	
	@Override
	public void suggestUnload(String identifier) {
		if (!this.getIdentifier().equals(PermissionService.SUBJECTS_USER)) return;
		
		try {
			if (this.plugin.getGame().getServer().getPlayer(UUID.fromString(identifier)).isPresent()) return;
		} catch (Exception e) {}
		
		EUserSubject player = this.identifierSubjects.remove(identifier);
		if (player != null) {
			this.plugin.getELogger().debug("Unloading the player : " + identifier);
		}
	}
	
	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
		Preconditions.checkNotNull(contexts, "contexts");
		Preconditions.checkNotNull(permission, "permission");
		
		return this.getAllWithPermission(this.plugin.getService().getContextCalculator().getUser(contexts), permission);
	}
	
	@Override
	public boolean isTransient() {
		return false;
	}
	
	public void clearCache() {
		for (EUserSubject subject : this.identifierSubjects.values()) {
			subject.clearCache();
		}
	}
}
