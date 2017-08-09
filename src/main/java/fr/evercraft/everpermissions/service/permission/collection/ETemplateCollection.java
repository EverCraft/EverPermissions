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
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ETemplateCollection extends ESubjectCollection<EOtherSubject> {

    public ETemplateCollection(final EverPermissions plugin) {
    	super(plugin, PermissionService.SUBJECTS_ROLE_TEMPLATE);
    }
    
    @Override
	protected EOtherSubject add(String identifier) {
		return new EOtherSubject(this.plugin, identifier, this);
	}

	@Override
	public CompletableFuture<Boolean> hasSubject(String identifier) {
		return CompletableFuture.completedFuture(this.subjects.containsKey(identifier.toLowerCase()));
	}

	@Override
	public CompletableFuture<Map<String, Subject>> loadSubjects(Set<String> identifiers) {
		ImmutableMap.Builder<String, Subject> builder = ImmutableMap.builder();
		for (String identifier : identifiers) {
			EOtherSubject subject = this.subjects.get(identifier.toLowerCase());
			if (subject != null) {
				builder.put(subject.getIdentifier().toLowerCase(), subject);
			}
		}
		return CompletableFuture.completedFuture(builder.build());
	}

	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		return CompletableFuture.completedFuture(this.subjects.values().stream()
			 .map(subject -> subject.getIdentifier())
			 .collect(Collectors.toSet()));
	}

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(String permission) {
		ImmutableMap.Builder<SubjectReference, Boolean> builder = ImmutableMap.builder();
		for (Subject subject : this.subjects.values()) {
			Tristate value = subject.getPermissionValue(subject.getActiveContexts(), permission);
			if (!value.equals(Tristate.UNDEFINED)) {
				builder.put(subject.asSubjectReference(), value.asBoolean());
			}
		}
		return CompletableFuture.completedFuture(builder.build());
	}

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
		ImmutableMap.Builder<SubjectReference, Boolean> builder = ImmutableMap.builder();
		for (Subject subject : this.subjects.values()) {
			Tristate value = subject.getPermissionValue(contexts, permission);
			if (!value.equals(Tristate.UNDEFINED)) {
				builder.put(subject.asSubjectReference(), value.asBoolean());
			}
		}
		return CompletableFuture.completedFuture(builder.build());
	}
}
