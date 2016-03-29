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
package fr.evercraft.everpermissions.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.EPermissionDescription;
import fr.evercraft.everpermissions.service.permission.collection.EOthersCollection;
import fr.evercraft.everpermissions.service.permission.collection.EGroupCollection;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.collection.ETemplateCollection;
import fr.evercraft.everpermissions.service.permission.collection.EUserCollection;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.context.ContextCalculator;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class EPermissionService implements PermissionService {
	public static final String IDENTIFIER_COMMAND_BLOCK = "CommandBlock";
	
	private final EverPermissions plugin;

	private final MemorySubjectData defaults;

    private final EUserCollection userCollection;
    private final EGroupCollection groupCollection;
    private final EOthersCollection commandBlockCollection;
	private final EOthersCollection systemCollection;
    
    private final EContextCalculator contextCalculator;
    
    private final CopyOnWriteArraySet<ContextCalculator<Subject>> contextCalculators;
    private final ConcurrentMap<String, ESubjectCollection> subjectCollections; 
    private final ConcurrentMap<String, EPermissionDescription> descriptions;

    public EPermissionService(final EverPermissions plugin) throws PluginDisableException {
    	this.plugin = plugin;
    	
    	this.defaults = new MemorySubjectData(this);
    	this.descriptions = new ConcurrentHashMap<String, EPermissionDescription>();
    	
    	// Context
    	this.contextCalculators = new CopyOnWriteArraySet<ContextCalculator<Subject>>();
    	this.contextCalculator = new EContextCalculator(this.plugin);
    	this.contextCalculators.add(this.contextCalculator);
    	
    	// Collection
    	this.groupCollection = new EGroupCollection(this.plugin);
    	this.userCollection = new EUserCollection(this.plugin);
    	this.systemCollection = new EOthersCollection(this.plugin, PermissionService.SUBJECTS_SYSTEM);
    	this.commandBlockCollection = new EOthersCollection(this.plugin, PermissionService.SUBJECTS_COMMAND_BLOCK);
    	
    	this.subjectCollections = new ConcurrentHashMap<String, ESubjectCollection>();
    	this.subjectCollections.put(PermissionService.SUBJECTS_USER, this.userCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_GROUP, this.groupCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_SYSTEM, this.systemCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_COMMAND_BLOCK, this.commandBlockCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_ROLE_TEMPLATE, new ETemplateCollection(this.plugin));
    }
    
    /**
     * Rechargement de toutes les collections
     */
    public void reload() {    	
    	for(ESubjectCollection collection : this.subjectCollections.values()) {
    		collection.reload();
    	}
    }
    
    /*
     * Accesseur : Collection
     */
    
    @Override
    public EUserCollection getUserSubjects() {
    	return this.userCollection;
    }

    @Override
    public EGroupCollection getGroupSubjects() {
    	return this.groupCollection;
    }

    public EOthersCollection getCommandBlockSubjects() {
    	return this.commandBlockCollection;
    }
    
    public EOthersCollection getSytemSubjects() {
    	return this.systemCollection;
    }
    
    @Override
    public MemorySubjectData getDefaultData() {
        return this.defaults;
    }
    
    /**
     * Retourne un EOtherSubject (Server, CommandBlock)
     * @param identifier L'identifiant du EOtherSubject
     * @return Un EOtherSubject
     */
    public Optional<EOtherSubject> getOtherSubject(final String identifier) {
		if(this.plugin.getService().getSytemSubjects().hasRegistered(identifier)) {
			return Optional.ofNullable(this.plugin.getService().getSytemSubjects().get(identifier));
		} else if(this.plugin.getService().getCommandBlockSubjects().hasRegistered(identifier)) {
			return Optional.ofNullable(this.plugin.getService().getCommandBlockSubjects().get(identifier));
		}
		return Optional.empty();
	}
    
    @Override
    public ESubjectCollection getSubjects(final String identifier) {
    	Preconditions.checkNotNull(identifier, "identifier");
    	if (!this.subjectCollections.containsKey(identifier)) {
    		 this.subjectCollections.putIfAbsent(identifier, new EOthersCollection(this.plugin, identifier));
    	}
    	return this.subjectCollections.get(identifier);
    }

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public ConcurrentMap<String, SubjectCollection> getKnownSubjects() {
        return (ConcurrentMap) this.subjectCollections;
    }
	
	/*
	 * Context
	 */

    @Override
    public void registerContextCalculator(final ContextCalculator<Subject> calculator) {}
    
    public EContextCalculator getContextCalculator() {
		return this.contextCalculator;
	}

    /*
     * Description
     */
    
    @Override
    public Optional<PermissionDescription.Builder> newDescriptionBuilder(final Object instance) {
        Optional<PluginContainer> container = this.plugin.getGame().getPluginManager().fromInstance(instance);
        if (container.isPresent()) {
        	return Optional.of(new EPermissionDescription.Builder(this, container.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<PermissionDescription> getDescription(final String identifier) {
        return Optional.ofNullable(this.descriptions.get(identifier));
    }

    @Override
    public Collection<PermissionDescription> getDescriptions() {
        return ImmutableSet.<PermissionDescription>copyOf(this.descriptions.values());
    }

	public void registerDescription(final EPermissionDescription description) {
		this.descriptions.put(description.getId(), description);
	}
}
