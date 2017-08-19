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
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectReference;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EGroupCollection extends ESubjectCollection<EGroupSubject> {
	private final ConcurrentMap<String, EGroupSubject> defaults;

    public EGroupCollection(final EverPermissions plugin) {
    	super(plugin, PermissionService.SUBJECTS_GROUP);
    	
    	this.defaults = new ConcurrentHashMap<String, EGroupSubject>();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public CompletableFuture<Boolean> load() {
    	return CompletableFuture.supplyAsync(() -> {
			Set<String> identifiers = this.storage.getAllIdentifiers();

			for (String identifier : identifiers) {
				identifier = identifier.toLowerCase();
				
				EGroupSubject newSubject = this.add(identifier);
				this.identifierSubjects.put(newSubject.getIdentifier().toLowerCase(), newSubject);
				newSubject.getFriendlyIdentifier().ifPresent(name -> this.nameSubjects.put(name.toLowerCase(), newSubject));
			}
			
			return this.storage.load((Collection) this.identifierSubjects.values());
		}, this.plugin.getThreadAsync());
    }
    
    public void reload() {
    	this.defaults.clear();
    	this.identifierSubjects.clear();
    	this.nameSubjects.clear();
    	
    	this.reloadConfig();
    	
    	this.load().join();
    }
    
    @Override
	protected EGroupSubject add(String identifier) {
		return new EGroupSubject(this.plugin, identifier, this);
	}
    
    @Override
	public void suggestUnload(String identifier) {}
    
    @Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
		Preconditions.checkNotNull(contexts, "contexts");
		Preconditions.checkNotNull(permission, "permission");
		
		return this.getAllWithPermission(this.plugin.getService().getContextCalculator().getUser(contexts), permission);
	}
    
    /*
     * Groupe
     */
    
    /**
     * Ajouter un groupe à un type de groupe
     * @param identifier Le nom du groupe
     * @param type Le type de groupe
     * @return False si le type de groupe n'existe pas
     */
    public CompletableFuture<Boolean> register(final String identifier, final String worldType) {
    	// Création du groupe si il n'existe pas
    	EGroupSubject group =  this.identifierSubjects.get(identifier);
    	if (group != null) {
    		group.registerTypeWorld(worldType);
    		
    		return CompletableFuture.supplyAsync(() -> this.storage.load(group), this.plugin.getThreadAsync());
    		
    	} 
    	
    	EGroupSubject newGroup = new EGroupSubject(this.plugin, this.nextUUID().toString(), this);
    	newGroup.registerTypeWorld(worldType);
		this.identifierSubjects.put(identifier.toLowerCase(), newGroup);
		return CompletableFuture.supplyAsync(() -> this.storage.load(newGroup), this.plugin.getThreadAsync());
    }
	
	/**
	 * Retourne la liste des groupes d'un type de groupe
	 * @param type Le type de groupe
	 * @return La liste des groupes
	 */
	public Set<EGroupSubject> getGroups(final String typeWorld) {
		Set<EGroupSubject> groups = new HashSet<EGroupSubject>();
		for (EGroupSubject group : this.identifierSubjects.values()) {
			if (group.hasTypeWorld(typeWorld)) {
				groups.add(group);
			}
		}
		return groups;
	}
	
	/*
     * GroupDefault
     */
    
    public ConcurrentMap<String, EGroupSubject> getDefaultGroups() {
		return this.defaults;
	}

	public Optional<EGroupSubject> getDefaultGroup(final String worldType) {
		return Optional.ofNullable(this.defaults.get(worldType));
	}
	
	public boolean setDefaultExecute(final String worldType, final EGroupSubject group) {
		this.defaults.put(worldType, group);
		return true;
	}
	
	public boolean removeDefaultExecute(final String worldType) {
		this.defaults.remove(worldType);
		return true;
	}
	
	public UUID nextUUID() {
		UUID uuid = null;
		do {
			uuid = UUID.randomUUID();
		} while (this.identifierSubjects.containsKey(uuid.toString()));
		return uuid;
	}
}
