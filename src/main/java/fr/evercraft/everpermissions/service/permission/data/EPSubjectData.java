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
package fr.evercraft.everpermissions.service.permission.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import fr.evercraft.everapi.event.ESpongeEventFactory;
import fr.evercraft.everapi.services.permission.ESubjectData;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EPContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EPSubject;

public abstract class EPSubjectData<T extends EPSubject> implements ESubjectData {
	
	protected final EverPermissions plugin;
	protected final T subject;
	protected final boolean transientData;
	
	protected final ConcurrentMap<String, Map<String, String>> options;
	protected final ConcurrentMap<String, EPNode> permissions;
	protected final ConcurrentMap<String, List<SubjectReference>> parents;
	
	// MultiThreading
	private final ReadWriteLock lock;
	protected final Lock write_lock;
	protected final Lock read_lock;

	public EPSubjectData(final EverPermissions plugin, final T subject, boolean transientData) {
		Preconditions.checkNotNull(plugin, "plugin");
		Preconditions.checkNotNull(subject, "subject");
		
		this.plugin = plugin;
		this.subject = subject;
		this.transientData = transientData;
		
		this.options = new ConcurrentHashMap<String, Map<String, String>>();
		this.permissions = new ConcurrentHashMap<String, EPNode>();
		this.parents = new ConcurrentHashMap<String, List<SubjectReference>>();
		
		// MultiThreading
		this.lock = new ReentrantReadWriteLock();
		this.write_lock = this.lock.writeLock();
		this.read_lock = this.lock.readLock();
	}
	
	@Override
	public String getIdentifier() {
		return this.subject.getIdentifier();
	}
	
	@Override
	public T getSubject() {
		return this.subject;
	}
	
	@Override
	public String getCollectionIdentifier() {
		return this.subject.getCollectionIdentifier();
	}
	
	@Override
	public boolean isTransient() {
		return this.transientData;
	}

	public boolean reload() {
		this.write_lock.lock();
		try {
			this.permissions.clear();
			this.options.clear();
			this.parents.clear();
			
			return true;
		} finally {
			this.write_lock.unlock();
		}
	}
	
	public void onUpdate() {
		this.subject.clearCache();
		ESpongeEventFactory.createSubjectDataUpdate(this, Cause.source(this.plugin).build());
	}
	
	/*
	 * Permissions
	 */
	
	public EPNode getNodeTree(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			EPNode perms = this.permissions.get(typeWorld);
			if (perms == null) return new EPNode();
			return perms;
		} finally {
			this.read_lock.unlock();
		}
	}

	@Override
	public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
		ImmutableMap.Builder<Set<Context>, Map<String, Boolean>> ret = ImmutableMap.builder();
		for (Map.Entry<String, EPNode> ent : this.permissions.entrySet()) {
			ret.put(EPContextCalculator.of(ent.getKey()), ent.getValue().asMap());
		}
		return ret.build();
	}
	
	@Override
	public Map<String, Boolean> getPermissions(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			EPNode perms = this.permissions.get(typeWorld);
			if (perms == null) return ImmutableMap.of();
			return perms.asMap();
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> setPermission(final String typeWorld, final String permission, final Tristate value) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(permission, "permission");
		Preconditions.checkNotNull(value, "value");
		
		return CompletableFuture.supplyAsync(() -> {
			Boolean oldValue = this.getNodeTree(typeWorld).asMap().get(permission);
			boolean insert = oldValue == null;
			
			if (insert && value.equals(Tristate.UNDEFINED)) return false;
			if (!insert && !value.equals(Tristate.UNDEFINED) && oldValue.booleanValue() == value.asBoolean()) return false;
			
			if (!this.getSubject().getContainingCollection().getStorage().setPermission(this, typeWorld, permission, value, insert)) return false;
			
			this.setPermissionExecute(typeWorld, permission, value);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}

	public void setPermissionExecute(final String typeWorld, final String permission, final Tristate value) {
		this.write_lock.lock();
		try {
			EPNode oldTree = this.permissions.get(typeWorld);
			if (oldTree == null) {
				if (value != Tristate.UNDEFINED) {
					this.permissions.putIfAbsent(typeWorld, EPNode.of(ImmutableMap.of(permission, value.asBoolean())));
				}
			} else if (value == Tristate.UNDEFINED) {
				if (oldTree.getTristate(permission) != Tristate.UNDEFINED) {
					this.permissions.put(typeWorld, oldTree.withValue(permission, value));
				}
			} else if (!oldTree.asMap().containsKey(permission) || oldTree.asMap().get(permission) != value.asBoolean()) {
				this.permissions.put(typeWorld, oldTree.withValue(permission, value));
			} 
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> clearPermissions(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (this.permissions.containsKey(typeWorld)) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().clearPermissions(this, typeWorld)) return false;
			
			this.clearPermissionsExecute(typeWorld);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void clearPermissionsExecute(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.permissions.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> clearPermissions() {
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (this.permissions.isEmpty()) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().clearPermissions(this)) return false;
			
			this.clearPermissionsExecute();
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}

	public void clearPermissionsExecute() {
		this.write_lock.lock();
		try {
			this.permissions.clear();
		} finally {
			this.write_lock.unlock();
		}
	}

	/*
	 * Groups
	 */
	
	@Override
	public Map<Set<Context>, List<SubjectReference>> getAllParents() {
		this.read_lock.lock();
		try {
			ImmutableMap.Builder<Set<Context>, List<SubjectReference>> builder = ImmutableMap.builder();
			for (Entry<String, List<SubjectReference>> groups : this.parents.entrySet()) {
				builder.put(EPContextCalculator.of(groups.getKey()), groups.getValue());
			}
			return builder.build();
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public List<SubjectReference> getParents(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			List<SubjectReference> group = this.parents.get(typeWorld);
			if (group == null) return ImmutableList.of();
			return group;
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> addParent(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				List<SubjectReference> parents = this.parents.get(typeWorld);
				if (parents != null && parents.contains(parent)) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().addParent(this, typeWorld, parent)) return false;
			
			this.addParentExecute(typeWorld, parent);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void addParentExecute(final String typeWorld, final SubjectReference parent) {
		this.write_lock.lock();
		try {
			List<SubjectReference> parents = this.parents.get(typeWorld);
			if (parents == null) {
				this.parents.put(typeWorld, ImmutableList.of(parent));
			} else if (!parents.contains(parent)) {
				this.parents.replace(typeWorld, ImmutableList.<SubjectReference>builder().addAll(parents).add(parent).build());
			}
		} finally {
			this.write_lock.unlock();
		}
	}

	/*
	 * Options
	 */
	
	@Override
	public Map<Set<Context>, Map<String, String>> getAllOptions() {
		 ImmutableMap.Builder<Set<Context>, Map<String, String>> builder = ImmutableMap.builder();
		 for (Entry<String, Map<String, String>> option : this.options.entrySet()) {
		 	builder.put(EPContextCalculator.of(option.getKey()), ImmutableMap.copyOf(option.getValue()));
		 }
		 return builder.build();
	}
	
	@Override
	public Map<String, String> getOptions(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			Map<String, String> ret = this.options.get(typeWorld);
			if (ret == null) return ImmutableMap.of();
			return ImmutableMap.copyOf(ret);
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> setOption(final String typeWorld, final String key, final String value) {
		return CompletableFuture.supplyAsync(() -> {
			String oldValue = this.getOptions(typeWorld).get(key);
			boolean insert = oldValue == null;
			
			if (insert && value == null) return false;
			if (!insert && value != null && oldValue.equals(value)) return false;
			
			if (!this.getSubject().getContainingCollection().getStorage().setOption(this, typeWorld, key, value, insert)) return false;
			
			this.setOptionExecute(typeWorld, key, value);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}

	public void setOptionExecute(final String typeWorld, final String key, final String value) {
		this.write_lock.lock();
		try {
			Map<String, String> options = this.options.get(typeWorld);
			if (options == null) {
				options = new HashMap<String, String>();
			}
			
			// Si on supprime l'option
			if (value == null) {
				options.remove(key);
				
				if (options.isEmpty()) {
					this.options.remove(typeWorld);
				}
			} else {
				options.put(key, value);
			}
			this.options.put(typeWorld, options);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> clearOptions(final String typeWorld) {
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (!this.options.containsKey(typeWorld)) return false;
				
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().clearOptions(this, typeWorld)) return false;
			
			this.clearOptionsExecute(typeWorld);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void clearOptionsExecute(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.options.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> clearOptions() {
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (this.options.isEmpty()) return false;
				
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().clearOptions(this)) return false;
			
			this.clearOptionsExecute();
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void clearOptionsExecute() {
		this.write_lock.lock();
		try {
			this.options.clear();
		} finally {
			this.write_lock.unlock();
		}
	}
}
