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
import fr.evercraft.everpermissions.service.permission.subject.ESubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubjectReference;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

/**
 * Subject collection
 */
public abstract class ESubjectCollection<T extends ESubject> implements SubjectCollection {
	protected final EverPermissions plugin;
	
	private final String identifier;
	protected final ConcurrentMap<String, T> subjects;

	public ESubjectCollection(final EverPermissions plugin, final String identifier) {
		this.plugin = plugin;
		this.identifier = identifier;
		
		this.subjects = new ConcurrentHashMap<String, T>();
	}
	
	/**
	 * Rechargement : Vide le cache et recharge tous les joueurs
	 */
	public void reload() {
		for (T subject : this.subjects.values()) {
			subject.reload();
		}
	}
	
	public CompletableFuture<Boolean> load() {
		return CompletableFuture.completedFuture(true);
	}
	
	protected abstract T add(String identifier);
	
	@Override
	public CompletableFuture<Subject> loadSubject(String identifier) {
		T cache = this.subjects.get(identifier);
		if (cache != null) return CompletableFuture.completedFuture(cache);
		
		final T subject = this.add(identifier);
		this.subjects.put(subject.getIdentifier().toLowerCase(), subject);
		
		return CompletableFuture.supplyAsync(() -> {
			this.plugin.getManagerData().get(this.getIdentifier()).load(subject);
			return subject;
		}, this.plugin.getThreadAsync());
	}
	
	@Override
	public CompletableFuture<Map<String, Subject>> loadSubjects(Set<String> identifiers) {
		ImmutableMap.Builder<String, Subject> subjects = ImmutableMap.builder();
		Set<ESubject> newSubjects = new HashSet<ESubject>();
		for (String identifier : identifiers) {
			T subject = this.subjects.get(identifier);
			if (subject == null) {
				subject = this.add(identifier);
				newSubjects.add(subject);
			}
			this.subjects.put(subject.getIdentifier().toLowerCase(), subject);
			subjects.put(subject.getIdentifier().toLowerCase(), subject);
		}
		
		if (newSubjects.isEmpty()) return CompletableFuture.completedFuture(subjects.build());
		
		return CompletableFuture.supplyAsync(() -> {
			this.plugin.getManagerData().get(this.getIdentifier()).load(newSubjects);
			return subjects.build();
		}, this.plugin.getThreadAsync());
	}
	
	@Override
	public void suggestUnload(String identifier) {
		this.subjects.remove(identifier);
	}

	@Override
	public Optional<Subject> getSubject(String identifier) {
		return Optional.ofNullable(this.subjects.get(identifier));
	}
	
	@Override
	public Collection<Subject> getLoadedSubjects() {
		return ImmutableSet.copyOf(this.subjects.values());
	}
	
	public Predicate<String> getIdentifierValidityPredicate() {
		return subject -> true;
	}
	
	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public Subject getDefaults() {
		return this.plugin.getService().getDefaults();
	}
	
	@Override
	public SubjectReference newSubjectReference(String subjectIdentifier) {
		Preconditions.checkNotNull(subjectIdentifier, "subjectIdentifier");
		
		return new ESubjectReference(this.plugin.getService(), this.getIdentifier(), subjectIdentifier);
	}
	
	@Override
	public Map<Subject, Boolean> getLoadedWithPermission(String permission) {
		Preconditions.checkNotNull(permission, "permission");
		
		ImmutableMap.Builder<Subject, Boolean> builder = ImmutableMap.builder();
		for (Subject subject : this.subjects.values()) {
			Tristate value = subject.getPermissionValue(subject.getActiveContexts(), permission);
			if (!value.equals(Tristate.UNDEFINED)) {
				builder.put(subject, value.asBoolean());
			}
		}
		return builder.build();
	}

	@Override
	public Map<Subject, Boolean> getLoadedWithPermission(Set<Context> contexts, String permission) {
		Preconditions.checkNotNull(contexts, "contexts");
		Preconditions.checkNotNull(permission, "permission");
		
		ImmutableMap.Builder<Subject, Boolean> builder = ImmutableMap.builder();
		for (Subject subject : this.subjects.values()) {
			Tristate value = subject.getPermissionValue(contexts, permission);
			if (!value.equals(Tristate.UNDEFINED)) {
				builder.put(subject, value.asBoolean());
			}
		}
		return builder.build();
	}
	
	@Override
	public CompletableFuture<Boolean> hasSubject(String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		return CompletableFuture.supplyAsync(() -> {
			return this.plugin.getManagerData().get(this.getIdentifier()).hasSubject(identifier);
		}, this.plugin.getThreadAsync());
	}
	
	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		return CompletableFuture.supplyAsync(() -> {
			return this.plugin.getManagerData().get(this.getIdentifier()).getAllIdentifiers();
		}, this.plugin.getThreadAsync());
	}

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(String permission) {
		Preconditions.checkNotNull(permission, "permission");
		
		return this.getAllWithPermission(SubjectData.GLOBAL_CONTEXT, permission);
	}
	
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(String typeWorld, String permission) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(permission, "permission");
		
		return CompletableFuture.supplyAsync(() -> {
			return this.plugin.getManagerData().get(this.getIdentifier()).getAllWithPermission(typeWorld, permission);
		}, this.plugin.getThreadAsync());
	}
}
