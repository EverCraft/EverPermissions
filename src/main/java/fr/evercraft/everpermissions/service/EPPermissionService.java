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
import fr.evercraft.everapi.services.permission.EPermissionService;
import fr.evercraft.everapi.services.permission.ESubjectCollection;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EPContextCalculator;
import fr.evercraft.everpermissions.service.permission.EPPermissionDescription;
import fr.evercraft.everpermissions.service.permission.collection.EPCommandBlockCollection;
import fr.evercraft.everpermissions.service.permission.collection.EPGroupCollection;
import fr.evercraft.everpermissions.service.permission.collection.EPSubjectCollection;
import fr.evercraft.everpermissions.service.permission.collection.EPTransientCollection;
import fr.evercraft.everpermissions.service.permission.collection.EPUserCollection;
import fr.evercraft.everpermissions.service.permission.subject.EPSubjectReference;
import fr.evercraft.everpermissions.service.permission.subject.EPUserSubject;

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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

public class EPPermissionService implements EPermissionService {
	
	private final EverPermissions plugin;

	private EPUserSubject defaultSubject;

	private final EPUserCollection users;
	private final EPGroupCollection groups;
	private final EPUserCollection commandBlocks;
	private final EPUserCollection systems;
	private final EPUserCollection defaults;
	
	private final EPContextCalculator context;
	
	private final ConcurrentMap<String, EPSubjectCollection<?>> collections; 
	private final ConcurrentMap<String, EPPermissionDescription> descriptions;

	public EPPermissionService(final EverPermissions plugin) throws PluginDisableException {
		this.plugin = plugin;
		
		// Context
		this.context = new EPContextCalculator(this.plugin);
		
		// Collection
		this.defaults = new EPUserCollection(this.plugin, PermissionService.SUBJECTS_DEFAULT);
		this.groups = new EPGroupCollection(this.plugin);
		this.users = new EPUserCollection(this.plugin, PermissionService.SUBJECTS_USER);
		this.systems = new EPUserCollection(this.plugin, PermissionService.SUBJECTS_SYSTEM);
		this.commandBlocks = new EPCommandBlockCollection(this.plugin, PermissionService.SUBJECTS_COMMAND_BLOCK);
		
		this.descriptions = new ConcurrentHashMap<String, EPPermissionDescription>();
		
		this.collections = new ConcurrentHashMap<String, EPSubjectCollection<?>>();
		this.collections.put(PermissionService.SUBJECTS_USER.toLowerCase(), this.users);
		this.collections.put(PermissionService.SUBJECTS_GROUP.toLowerCase(), this.groups);
		this.collections.put(PermissionService.SUBJECTS_SYSTEM.toLowerCase(), this.systems);
		this.collections.put(PermissionService.SUBJECTS_COMMAND_BLOCK.toLowerCase(), this.commandBlocks);
		this.collections.put(PermissionService.SUBJECTS_DEFAULT.toLowerCase(), this.defaults);
		this.collections.put(PermissionService.SUBJECTS_ROLE_TEMPLATE.toLowerCase(), new EPTransientCollection(this.plugin, PermissionService.SUBJECTS_ROLE_TEMPLATE));
	}
	
	public void load() {
		this.defaultSubject = this.defaults.load(SUBJECT_DEFAULT).join();
		
		for (EPSubjectCollection<?> collection : this.collections.values()) {
			collection.load();
		}
	}
	
	public void reload() {		
		for (EPSubjectCollection<?> collection : this.collections.values()) {
			collection.reload();
		}
	}
	
	/*
	 * Accesseur : Collection
	 */
	
	@Override
	public EPUserCollection getUserSubjects() {
		return this.users;
	}

	@Override
	public EPGroupCollection getGroupSubjects() {
		return this.groups;
	}

	public EPUserCollection getCommandBlockSubjects() {
		return this.commandBlocks;
	}
	
	public EPUserCollection getSytemSubjects() {
		return this.systems;
	}
	
	@Override
	public EPUserSubject getDefaults() {
		return this.defaultSubject;
	}
	
	@Override
	public CompletableFuture<SubjectCollection> loadCollection(String identifier) {
		EPSubjectCollection<?> collection = this.collections.get(identifier.toLowerCase());
		if (collection != null) return CompletableFuture.completedFuture(collection);
		
		final EPSubjectCollection<?> newCollection = new EPUserCollection(this.plugin, identifier);
		this.collections.put(identifier.toLowerCase(), newCollection);
		return CompletableFuture.supplyAsync(() -> {
				newCollection.load();
				return newCollection;
			}, this.plugin.getThreadAsync());
	}

	@Override
	public Optional<SubjectCollection> getCollection(String identifier) {
		return Optional.ofNullable(this.collections.get(identifier.toLowerCase()));
	}
	
	public Optional<ESubjectCollection> get(String identifier) {
		return Optional.ofNullable(this.collections.get(identifier.toLowerCase()));
	}

	@Override
	public CompletableFuture<Boolean> hasCollection(String identifier) {
		if (this.collections.containsKey(identifier.toLowerCase())) return CompletableFuture.completedFuture(true);
		
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
	
	public EPContextCalculator getContextCalculator() {
		return this.context;
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
		return new EPPermissionDescription.Builder(this, container.get());
	}

	@Override
	public Optional<PermissionDescription> getDescription(final String identifier) {
		return Optional.ofNullable(this.descriptions.get(identifier));
	}

	@Override
	public Collection<PermissionDescription> getDescriptions() {
		return ImmutableSet.<PermissionDescription>copyOf(this.descriptions.values());
	}

	public void registerDescription(final EPPermissionDescription description) {
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
		return value -> value.length() <= 36;
	}

	@Override
	public Map<String, SubjectCollection> getLoadedCollections() {
		return ImmutableMap.copyOf(this.collections);
	}

	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		return CompletableFuture.supplyAsync(() -> this.plugin.getConfigs().getCollections(), this.plugin.getThreadAsync());
	}

	@Override
	public SubjectReference newSubjectReference(String collectionIdentifier, String subjectIdentifier) {
		return new EPSubjectReference(this, collectionIdentifier, subjectIdentifier);
	}

	@Override
	public void registerWorldType(String nameWorld) {
		this.plugin.getConfigs().registerWorld(nameWorld);
		
		for (EPSubjectCollection<?> collection : this.collections.values()) {
			collection.registerWorld(nameWorld);
		}
	}

	@Override
	public void clearCache() {
		for (EPSubjectCollection<?> collection : this.collections.values()) {
			collection.clearCache();
		}
	}
}
