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
import fr.evercraft.everpermissions.service.permission.storage.EConfigCollectionStorage;
import fr.evercraft.everpermissions.service.permission.storage.ESqlCollectionStorage;
import fr.evercraft.everpermissions.service.permission.storage.ICollectionStorage;
import fr.evercraft.everpermissions.service.permission.subject.ESubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubjectReference;

import org.spongepowered.api.service.permission.PermissionService;
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
	
	protected final ConcurrentMap<String, T> identifierSubjects;
	protected final ConcurrentMap<String, T> nameSubjects;
	
	protected ICollectionStorage storage;
	protected final ConcurrentMap<String, String> worlds;

	public ESubjectCollection(final EverPermissions plugin, final String identifier) {
		this.plugin = plugin;
		this.identifier = identifier;
		
		this.identifierSubjects = new ConcurrentHashMap<String, T>();
		this.nameSubjects = new ConcurrentHashMap<String, T>();
		this.worlds = new ConcurrentHashMap<String, String>();
		
		this.reloadConfig();
	}
	
	public abstract boolean isTransient();
	
	/**
	 * Rechargement : Vide le cache et recharge tous les joueurs
	 */
	public void reload() {
		this.reloadConfig();
		
		for (T subject : this.identifierSubjects.values()) {
			subject.reload();
		}
	}
	
	public void reloadConfig() {
		// Stop
		this.worlds.clear();
		
		// Start
		this.plugin.getConfigs().registerCollection(this.identifier);
		this.worlds.putAll(this.plugin.getConfigs().getTypeWorld(this.identifier));
		
		if (!this.isTransient()) {
			if (this.plugin.getDataBases().isEnable() && (this.storage == null || !(this.storage instanceof ESqlCollectionStorage)) && this.identifier.equals(PermissionService.SUBJECTS_GROUP)) {
				if (this.identifier.equals(PermissionService.SUBJECTS_GROUP) ) {
					this.storage = new ESqlCollectionStorage(this.plugin, this.identifier);
				}
			} else if ((!this.plugin.getDataBases().isEnable() || this.identifier.equals(PermissionService.SUBJECTS_GROUP)) && (this.storage == null || !(this.storage instanceof EConfigCollectionStorage))) {
				this.storage = new EConfigCollectionStorage(this.plugin, this.identifier);
			} else {
				this.storage.reload();
			}
			
			for (String typeWorld : new HashSet<String>(this.worlds.values())) {
				this.storage.register(typeWorld);
			}
		}
	}
	
	public void registerWorld(final String world) {
		if (!this.worlds.containsKey(world)) {
			String typeWorld = plugin.getConfigs().getTypeWorld(this.identifier, world);
			
			this.worlds.put(world, this.plugin.getConfigs().getTypeWorld(this.identifier, typeWorld));
			
			if (!this.isTransient()) this.storage.register(typeWorld);
		}
	}
	
	public Optional<String> getTypeWorld(final String world) {
		return Optional.ofNullable(this.worlds.get(world));
	}
	
	public boolean load() {
		return true;
	}
	
	protected abstract T add(String identifier);
	
	public Optional<T> get(String identifier) {
		identifier = identifier.toLowerCase();
		
		if (identifier.length() == 36) {
			T subject = this.identifierSubjects.get(identifier);
			if (subject != null) return Optional.of(subject);
		}
		return Optional.ofNullable(this.nameSubjects.get(identifier));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Optional<Subject> getSubject(String identifier) {
		return (Optional) this.get(identifier);
	}
	
	public CompletableFuture<T> load(String identifier) {
		identifier = identifier.toLowerCase();
		
		Optional<T> cache = this.get(identifier);
		if (cache.isPresent()) return CompletableFuture.completedFuture(cache.get());
		
		final T subject = this.add(identifier);
		this.identifierSubjects.put(subject.getIdentifier().toLowerCase(), subject);
		subject.getFriendlyIdentifier().ifPresent(name -> this.nameSubjects.put(name.toLowerCase(), subject));
		this.storage.load(subject);
		return CompletableFuture.completedFuture(subject);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public CompletableFuture<Subject> loadSubject(String identifier) {
		return (CompletableFuture) this.load(identifier);
	}
	
	@Override
	public CompletableFuture<Map<String, Subject>> loadSubjects(Set<String> identifiers) {
		ImmutableMap.Builder<String, Subject> subjects = ImmutableMap.builder();
		Set<ESubject> newSubjects = new HashSet<ESubject>();
		for (String identifier : identifiers) {
			identifier = identifier.toLowerCase();
			
			T subject = this.get(identifier).orElse(null);
			if (subject == null) {
				T newSubject = this.add(identifier);
				newSubjects.add(newSubject);
				
				this.identifierSubjects.put(newSubject.getIdentifier().toLowerCase(), newSubject);
				newSubject.getFriendlyIdentifier().ifPresent(name -> this.nameSubjects.put(name.toLowerCase(), newSubject));
			} else {
				subjects.put(subject.getIdentifier().toLowerCase(), subject);
			}
		}
		
		if (newSubjects.isEmpty() || this.isTransient()) return CompletableFuture.completedFuture(subjects.build());
		
		return CompletableFuture.supplyAsync(() -> {
			this.storage.load(newSubjects);
			return subjects.build();
		}, this.plugin.getThreadAsync());
	}
	
	@Override
	public void suggestUnload(String identifier) {
		this.identifierSubjects.remove(identifier);
	}

	@Override
	public Collection<Subject> getLoadedSubjects() {
		return ImmutableSet.copyOf(this.identifierSubjects.values());
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
		
		return new ESubjectReference(this.plugin.getService(), this.identifier, subjectIdentifier);
	}
	
	@Override
	public Map<Subject, Boolean> getLoadedWithPermission(String permission) {
		Preconditions.checkNotNull(permission, "permission");
		
		ImmutableMap.Builder<Subject, Boolean> builder = ImmutableMap.builder();
		for (Subject subject : this.identifierSubjects.values()) {
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
		for (Subject subject : this.identifierSubjects.values()) {
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
		
		if (this.get(identifier).isPresent()) return CompletableFuture.completedFuture(true);
		if (this.isTransient()) return CompletableFuture.completedFuture(false);
		
		return CompletableFuture.supplyAsync(() -> {
			return this.storage.hasSubject(identifier);
		}, this.plugin.getThreadAsync());
	}
	
	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		if (this.isTransient()) return CompletableFuture.completedFuture(this.identifierSubjects.keySet());
		
		return CompletableFuture.supplyAsync(() -> {
			return this.storage.getAllIdentifiers();
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
			return this.storage.getAllWithPermission(typeWorld, permission.toLowerCase());
		}, this.plugin.getThreadAsync());
	}

	public ICollectionStorage getStorage() {
		return this.storage;
	}
}
