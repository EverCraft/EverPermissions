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
package fr.evercraft.everpermissions.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.message.EMessageSender;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.EPermissionDescription;
import fr.evercraft.everpermissions.service.permission.collection.EGroupCollection;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.collection.ETemplateCollection;
import fr.evercraft.everpermissions.service.permission.collection.EUserCollection;
import fr.evercraft.everpermissions.service.permission.subject.ESubjectReference;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.context.ContextCalculator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

public class EPermissionService implements PermissionService {
	public static final String IDENTIFIER_COMMAND_BLOCK = "CommandBlock";
	public static final String SUBJECT_DEFAULT = "Default";
	
	private final EverPermissions plugin;

	private final Subject defaults;

    private final EUserCollection userCollection;
    private final EGroupCollection groupCollection;
    private final EUserCollection commandBlockCollection;
	private final EUserCollection systemCollection;
	private final EUserCollection defaultsCollection;
    
    private final EContextCalculator contextCalculator;
    
    private final CopyOnWriteArraySet<ContextCalculator<Subject>> contextCalculators;
    private final ConcurrentMap<String, ESubjectCollection<?>> subjectCollections; 
    private final ConcurrentMap<String, EPermissionDescription> descriptions;

    public EPermissionService(final EverPermissions plugin) throws PluginDisableException {
    	this.plugin = plugin;
    	
    	// Default
    	this.defaultsCollection = new EUserCollection(this.plugin, PermissionService.SUBJECTS_DEFAULT);
    	this.defaults = new EUserSubject(this.plugin, SUBJECT_DEFAULT, this.defaultsCollection);
    	
    	this.descriptions = new ConcurrentHashMap<String, EPermissionDescription>();
    	
    	// Context
    	this.contextCalculators = new CopyOnWriteArraySet<ContextCalculator<Subject>>();
    	this.contextCalculator = new EContextCalculator(this.plugin);
    	this.contextCalculators.add(this.contextCalculator);
    	
    	// Collection
    	this.groupCollection = new EGroupCollection(this.plugin);
    	this.userCollection = new EUserCollection(this.plugin, PermissionService.SUBJECTS_USER);
    	this.systemCollection = new EUserCollection(this.plugin, PermissionService.SUBJECTS_SYSTEM);
    	this.commandBlockCollection = new EUserCollection(this.plugin, PermissionService.SUBJECTS_COMMAND_BLOCK);
    	
    	this.subjectCollections = new ConcurrentHashMap<String, ESubjectCollection<?>>();
    	this.subjectCollections.put(PermissionService.SUBJECTS_USER.toLowerCase(), this.userCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_GROUP.toLowerCase(), this.groupCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_SYSTEM.toLowerCase(), this.systemCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_COMMAND_BLOCK.toLowerCase(), this.commandBlockCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_DEFAULT.toLowerCase(), this.defaultsCollection);
    	this.subjectCollections.put(PermissionService.SUBJECTS_ROLE_TEMPLATE.toLowerCase(), new ETemplateCollection(this.plugin));
    }
    
    /**
     * Rechargement de toutes les collections
     */
    public void reload() {    	
    	for (ESubjectCollection<?> collection : this.subjectCollections.values()) {
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

    public EUserCollection getCommandBlockSubjects() {
    	return this.commandBlockCollection;
    }
    
    public EUserCollection getSytemSubjects() {
    	return this.systemCollection;
    }
    
    public Set<String> getSuggestsOthers() {
    	TreeSet<String> suggests = new TreeSet<String>();
    	for (Subject subject : this.plugin.getService().getSytemSubjects().getLoadedSubjects()) {
			suggests.add(subject.getIdentifier());
		}
		for (Subject subject : this.plugin.getService().getCommandBlockSubjects().getLoadedSubjects()) {
			suggests.add(subject.getIdentifier());
		}
		return suggests;
    }
    
    @Override
    public Subject getDefaults() {
        return this.defaults;
    }
    
    /**
     * Retourne un EOtherSubject (Server, CommandBlock)
     * @param identifier L'identifiant du EOtherSubject
     * @return Un EOtherSubject
     */
    public Optional<Subject> getOtherSubject(final String identifier) {
    	Optional<Subject> subject = this.plugin.getService().getSytemSubjects().getSubject(identifier);
		if (subject.isPresent()) {
			return subject;
		}
		
		subject = this.plugin.getService().getCommandBlockSubjects().getSubject(identifier);
		if (subject.isPresent()) {
			return subject;
		}
		return Optional.empty();
	}
    
    @Override
	public CompletableFuture<SubjectCollection> loadCollection(String identifier) {
		ESubjectCollection<?> collection = this.subjectCollections.get(identifier.toLowerCase());
		if (collection != null) return CompletableFuture.completedFuture(collection);
		
		final ESubjectCollection<?> newCollection = new EUserCollection(this.plugin, identifier);
		this.subjectCollections.put(identifier.toLowerCase(), newCollection);
		return newCollection.load().thenApply(result -> newCollection);
	}

	@Override
	public Optional<SubjectCollection> getCollection(String identifier) {
		return Optional.ofNullable(this.subjectCollections.get(identifier.toLowerCase()));
	}

	@Override
	public CompletableFuture<Boolean> hasCollection(String identifier) {
		if (this.subjectCollections.containsKey(identifier.toLowerCase())) return CompletableFuture.completedFuture(true);
		
		return CompletableFuture.supplyAsync(() -> {
			File[] files = this.plugin.getPath().resolve("others").toFile().listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File directory, String fileName) {
			        return fileName.endsWith(".conf") && fileName.equalsIgnoreCase(identifier + ".conf");
				}
			});
		
			return files.length != 0;
		});
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
    public PermissionDescription.Builder newDescriptionBuilder(final Object instance) {
        Optional<PluginContainer> container = this.plugin.getGame().getPluginManager().fromInstance(instance);
        if (!container.isPresent()) {
        	throw new IllegalArgumentException("Couldn't find a plugin container for " + instance.getClass().getSimpleName());
        }
        return new EPermissionDescription.Builder(this, container.get());
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
	
	/*
	 * BroadCast
	 */

	public void broadcastMessage(CommandSource player, EMessageSender message) {
		message.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> 
			!other.equals(player) && other.hasPermission(EPPermissions.BROADCAST.get()));
	}
	
	public void broadcastMessage(CommandSource staff, UUID uuid, EMessageSender message) {
		message.sendAll(this.plugin.getEServer().getOnlineEPlayers(), other -> 
			!other.equals(staff) && !uuid.equals(other.getUniqueId()) &&  other.hasPermission(EPPermissions.BROADCAST.get()));
	}

	@Override
	public Predicate<String> getIdentifierValidityPredicate() {
		return value -> true;
	}

	@Override
	public Map<String, SubjectCollection> getLoadedCollections() {
		return ImmutableMap.copyOf(this.subjectCollections);
	}

	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		return CompletableFuture.supplyAsync(() -> {
			ImmutableSet.Builder<String> identifiers = ImmutableSet.builder();
			for (ESubjectCollection<?> collection : this.subjectCollections.values()) {
				identifiers.add(collection.getIdentifier());
			}

			File[] files = this.plugin.getPath().resolve("others").toFile().listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File directory, String fileName) {
			        return fileName.endsWith(".conf");
				}
			});
		
			for (File file : files) {
				identifiers.add(file.getName().replace("(.conf)$", ""));
			}
			
			return identifiers.build();
		});
	}

	@Override
	public SubjectReference newSubjectReference(String collectionIdentifier, String subjectIdentifier) {
		return new ESubjectReference(this, collectionIdentifier, subjectIdentifier);
	}
}
