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

import fr.evercraft.everapi.event.PermUserEvent.Action;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EUserCollection extends ESubjectCollection<EUserSubject> {

	public EUserCollection(final EverPermissions plugin) {
		super(plugin, PermissionService.SUBJECTS_USER);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected EUserSubject add(String identifier) {
		return new EUserSubject(this.plugin, identifier, (ESubjectCollection) this);
	}
	
	@Override
	public void suggestUnload(String identifier) {
		try {
			if (this.plugin.getGame().getServer().getPlayer(UUID.fromString(identifier)).isPresent()) return;
		} catch (Exception e) {}
		
		EUserSubject player = this.subjects.remove(identifier);
		if (player != null) {
			this.plugin.getManagerEvent().post(player, Action.USER_REMOVED);
			this.plugin.getELogger().debug("Unloading the player : " + identifier);
		}
	}
	
	@Override
	public CompletableFuture<Boolean> hasSubject(String identifier) {
		// TODO Requete DataBase
		return null;
	}
	
	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<String, Subject>> loadSubjects(Set<String> identifiers) {
		// TODO Requete DataBase
		return null;
	}

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(String permission) {
		// TODO Requete DataBase
		return null;
	}

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
		// TODO Requete DataBase
		return null;
	}
}
