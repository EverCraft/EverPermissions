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
package fr.evercraft.everpermissions.service.permission.subject;

import fr.evercraft.everapi.services.permission.ESubject;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.collection.EPSubjectCollection;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.service.context.Context;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class EPSubject implements ESubject {
	protected final EverPermissions plugin;
	
	protected final EPSubjectCollection<?> collection;
	protected final String identifier;
	protected String name;
	
	protected final Map<String, Set<String>> verboses;
	
	// MultiThreading
	private final ReadWriteLock lock;
	protected final Lock write_lock;
	protected final Lock read_lock;

	public EPSubject(final EverPermissions plugin, final String identifier, final EPSubjectCollection<?> collection) {
		this.plugin = plugin;
		
		this.identifier = identifier;
		this.collection = collection;
		this.verboses = new HashMap<String, Set<String>>();
		
		// MultiThreading
		this.lock = new ReentrantReadWriteLock();
		this.write_lock = this.lock.writeLock();
		this.read_lock = this.lock.readLock();
		
		this.plugin.getELogger().debug("Creation du subject (subject='" + this.identifier + "';collection='" + this.collection + "')");
	}

	@Override
	public abstract void reload();
	
	/*
	 * Accesseurs
	 */
	
	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public EPSubjectCollection<?> getContainingCollection() {
		return this.collection;
	}
	
	@Override
	public String getCollectionIdentifier() {
		return this.collection.getIdentifier();
	}
	
	@Override
	public Optional<CommandSource> getCommandSource() {
		return Optional.empty();
	}
	
	@Override
	public Set<Context> getActiveContexts() {
		return ImmutableSet.of();
	}
	
	@Override
	public boolean isChildOf(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		Preconditions.checkNotNull(parent, "parent");
		
		int cpt = 0;
		boolean children = false;
		List<SubjectReference> parents = this.getParents(contexts);
		while(cpt < parents.size() && !children){
			children = parents.get(cpt).equals(parent) || parents.get(cpt).resolve().join().isChildOf(contexts, parent);
			cpt++;
		}
		return children;
	}
	
	/*
	 * Java
	 */
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Subject)) {
			return false;
		}
		return this.getIdentifier().equals(((Subject) other).getIdentifier());
	}
	
	@Override
	public String toString() {
		return "ESubject [identifier=" + identifier + ", data=" + this.getSubjectData() + ", transientData=" + this.getTransientSubjectData() + "]";
	}
	
	@Override
	public SubjectReference asSubjectReference() {
		return new EPSubjectReference(this.plugin.getService(), this.getContainingCollection().getIdentifier(), this.getIdentifier());
	}
	
	@Override
	public Optional<String> getFriendlyIdentifier() {
		this.read_lock.lock();
		try {
			return Optional.ofNullable(this.name);
		} finally {
			this.read_lock.unlock();
		}
	}

	public CompletableFuture<Boolean> setFriendlyIdentifier(String name) {
		return CompletableFuture.supplyAsync(() -> {
			if (this.name == null && name == null) return false;
			if (this.name != null && name != null && this.name.equals(name)) return false;
			
			if (!this.getContainingCollection().getStorage().setFriendlyIdentifier(this, name)) return false;
			
			this.setFriendlyIdentifierExecute(name);
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void setFriendlyIdentifierExecute(String name) {
		this.write_lock.lock();
		try {
			this.name = name;
		} finally {
			this.write_lock.unlock();
		}
	}

	@Override
	public void clearCache() {}
	
	/*
	 * Verbose
	 */
	
	@Override
	public void addVerbose(CommandSource source, Set<String> permission) {
		this.write_lock.lock();
		try {
			this.verboses.put(source.getIdentifier(), ImmutableSet.copyOf(permission));
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public Optional<Set<String>> getVerbose(CommandSource source) {
		this.read_lock.lock();
		try {
			return Optional.ofNullable(this.verboses.get(source.getIdentifier()));
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public void removeVerbose(CommandSource source) {
		this.write_lock.lock();
		try {
			this.verboses.remove(source.getIdentifier());
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public ImmutableMap<String, Set<String>> getVerboses() {
		this.read_lock.lock();
		try {
			return ImmutableMap.copyOf(this.verboses);
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public void verbose(Optional<String> world, String permission, Tristate value) {
		
	}
	
	public void verbose(Optional<String> world, String option, Optional<String> value) {
		// TODO Auto-generated method stub
		
	}
}
