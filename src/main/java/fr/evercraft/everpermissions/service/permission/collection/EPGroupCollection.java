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

import fr.evercraft.everapi.services.permission.EGroupCollection;
import fr.evercraft.everapi.services.permission.EGroupSubject;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EPGroupSubject;

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

public class EPGroupCollection extends EPSubjectCollection<EPGroupSubject> implements EGroupCollection {
	private final ConcurrentMap<String, EGroupSubject> defaults;

    public EPGroupCollection(final EverPermissions plugin) {
    	super(plugin, PermissionService.SUBJECTS_GROUP);
    	
    	this.defaults = new ConcurrentHashMap<String, EGroupSubject>();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean load() {
		Set<String> identifiers = this.storage.getAllIdentifiers();
	
		for (String identifier : identifiers) {
			identifier = identifier.toLowerCase();
			
			EPGroupSubject newSubject = this.add(identifier);
			this.identifierSubjects.put(identifier, newSubject);
		}
		
		if (!this.storage.load((Collection) this.identifierSubjects.values())) return false;
		
		for (EPGroupSubject subject : this.identifierSubjects.values()) {
			subject.getFriendlyIdentifier().ifPresent(name -> this.nameSubjects.put(name.toLowerCase(), subject));
		}
		return true;
    }
    
    public void reload() {
    	this.defaults.clear();
    	this.identifierSubjects.clear();
    	this.nameSubjects.clear();
    	
    	this.reloadConfig();
    	
    	this.load();
    }
    
    @Override
	protected EPGroupSubject add(String identifier) {
    	try {
    		return new EPGroupSubject(this.plugin, UUID.fromString(identifier).toString(), this);
    	} catch (IllegalArgumentException e) {
    		EPGroupSubject subject = new EPGroupSubject(this.plugin, this.nextUUID().toString(), this);
    		subject.setFriendlyIdentifierExecute(identifier);
    		return subject;
    	}
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
    public CompletableFuture<Boolean> register(final String name, final String worldType) {
    	// Création du groupe si il n'existe pas
    	EPGroupSubject group =  this.identifierSubjects.get(name);
    	if (group != null) {
    		group.registerTypeWorld(worldType);
    		
    		return CompletableFuture.supplyAsync(() -> this.storage.load(group), this.plugin.getThreadAsync());
    		
    	} 
    	
    	String identifier = this.nextUUID().toString();
    	
    	EPGroupSubject newGroup = new EPGroupSubject(this.plugin, identifier, this);
    	newGroup.setFriendlyIdentifierExecute(name);
    	newGroup.registerTypeWorld(worldType);
    	
		this.identifierSubjects.put(identifier, newGroup);
		this.nameSubjects.put(name.toLowerCase(), newGroup);
		return CompletableFuture.supplyAsync(() -> this.storage.load(newGroup), this.plugin.getThreadAsync());
    }
	
	/**
	 * Retourne la liste des groupes d'un type de groupe
	 * @param type Le type de groupe
	 * @return La liste des groupes
	 */
    @Override
	public Set<EGroupSubject> getGroups(final String typeWorld) {
		Set<EGroupSubject> groups = new HashSet<EGroupSubject>();
		for (EPGroupSubject group : this.identifierSubjects.values()) {
			if (group.hasTypeWorld(typeWorld)) {
				groups.add(group);
			}
		}
		return groups;
	}
	
	/*
     * GroupDefault
     */
    
	@Override
    public ConcurrentMap<String, EGroupSubject> getDefaultGroups() {
		return this.defaults;
	}

	@Override
	public Optional<EGroupSubject> getDefaultGroup(final String typeWorld) {
		return Optional.ofNullable(this.defaults.get(typeWorld));
	}
	
	public boolean setDefaultExecute(final String typeWorld, final EPGroupSubject group, boolean value) {
		if (value) {
			this.defaults.put(typeWorld, group);
		} else {
			this.defaults.remove(typeWorld);
		}
		return true;
	}
	
	public UUID nextUUID() {
		UUID uuid = null;
		do {
			uuid = UUID.randomUUID();
		} while (this.identifierSubjects.containsKey(uuid.toString()));
		return uuid;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	public Optional<String> getAll(String world) {
		// TODO Auto-generated method stub
		return null;
	}

}
