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

import java.util.ArrayList;
import java.util.Collections;
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

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;

public abstract class ESubjectData implements SubjectData {
	
	protected final EverPermissions plugin;
	protected final Subject subject;
	
	protected final ConcurrentMap<String, Map<String, String>> options;
	protected final ConcurrentMap<String, ENode> permissions;
	protected final ConcurrentMap<String, List<SubjectReference>> groups;
	
	// MultiThreading
	private final ReadWriteLock lock;
	protected final Lock write_lock;
	protected final Lock read_lock;

	public ESubjectData(final EverPermissions plugin, final Subject subject) {
		Preconditions.checkNotNull(plugin, "plugin");
		Preconditions.checkNotNull(subject, "subject");
		
		this.plugin = plugin;
		this.subject = subject;
		
		this.options = new ConcurrentHashMap<String, Map<String, String>>();
		this.permissions = new ConcurrentHashMap<String, ENode>();
		this.groups = new ConcurrentHashMap<String, List<SubjectReference>>();
		
		// MultiThreading
		this.lock = new ReentrantReadWriteLock();
		this.write_lock = this.lock.writeLock();
		this.read_lock = this.lock.readLock();
	}
	
	public String getIdentifier() {
		return this.subject.getIdentifier();
	}
	
	public abstract CompletableFuture<Boolean> load();

	public CompletableFuture<Boolean> reload() {
		this.write_lock.lock();
		try {
			this.permissions.clear();
			this.options.clear();
			this.groups.clear();
			
			return CompletableFuture.completedFuture(true);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	/*
	 * Permissions
	 */
	
	public abstract CompletableFuture<Boolean> setPermission(Set<Context> contexts, String permission, Tristate value);
	public abstract CompletableFuture<Boolean> clearPermissions(Set<Context> contexts);
	public abstract CompletableFuture<Boolean> clearPermissions();

	
	public ENode getNodeTree(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			ENode perms = this.permissions.get(typeWorld);
			if (perms == null) return new ENode();
			return perms;
		} finally {
			this.read_lock.unlock();
		}
	}

	@Override
	public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
		ImmutableMap.Builder<Set<Context>, Map<String, Boolean>> ret = ImmutableMap.builder();
		for (Map.Entry<String, ENode> ent : this.permissions.entrySet()) {
			ret.put(EContextCalculator.of(ent.getKey()), ent.getValue().asMap());
		}
		return ret.build();
	}
	
	@Override
	public Map<String, Boolean> getPermissions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		
		String world = EContextCalculator.getWorld(contexts).orElse("");
		ENode perms = this.permissions.get(world);
		if (perms == null) return ImmutableMap.of();
		return perms.asMap();
	}

	public void setPermissionExecute(final String typeWorld, final String permission, final Tristate value) {
		this.write_lock.lock();
		try {
			ENode oldTree = this.permissions.get(typeWorld);
			if (oldTree == null) {
				if (value != Tristate.UNDEFINED) {
					this.permissions.putIfAbsent(typeWorld, ENode.of(ImmutableMap.of(permission, value.asBoolean())));
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
	
	public void clearPermissionsExecute(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.permissions.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
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
	public abstract CompletableFuture<Boolean> removeParent(Set<Context> contexts, SubjectReference parent);
	public abstract CompletableFuture<Boolean> clearParents(Set<Context> contexts);
	public abstract CompletableFuture<Boolean> clearParents();
	
	public Map<Set<Context>, List<SubjectReference>> getAllParents() {
		ImmutableMap.Builder<Set<Context>, List<SubjectReference>> builder = ImmutableMap.builder();
		for (Entry<String, List<SubjectReference>> groups : this.groups.entrySet()) {
			builder.put(EContextCalculator.of(groups.getKey()), groups.getValue());
		}
		return builder.build();
	}

	public List<SubjectReference> getParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		
		String world = EContextCalculator.getWorld(contexts).orElse("");
		
		List<SubjectReference> ret = this.groups.get(world);
		if (ret == null) return Collections.emptyList();
		return ret;
	}
	
	public void addParentExecute(final String typeWorld, final SubjectReference parent) {
		List<SubjectReference> parents = this.groups.get(typeWorld);
		if (parents == null) {
			this.groups.put(typeWorld, ImmutableList.of(parent));
		} else if (!parents.contains(parent)) {
			this.groups.replace(typeWorld, ImmutableList.<SubjectReference>builder().addAll(parents).add(parent).build());
		}
	}
	
	public void removeParentExecute(final String typeWorld, final SubjectReference parent) {
		List<SubjectReference> parents = new ArrayList<SubjectReference>(this.groups.get(typeWorld));
		if (parents != null && parents.contains(parent)) {
			parents.remove(parent);
			this.groups.replace(typeWorld, ImmutableList.copyOf(parents));
		}
	}	
	
	public void clearParentsExecute(final String typeWorld) {
		this.groups.remove(typeWorld);
	}
	
	public void clearParentsExecute() {
		this.groups.clear();
	}

	/*
	 * Options
	 */
	
	public abstract CompletableFuture<Boolean> setOption(Set<Context> contexts, String type, String name);
	public abstract CompletableFuture<Boolean> clearOptions(Set<Context> contexts);
	public abstract CompletableFuture<Boolean> clearOptions();
	
	@Override
	public Map<Set<Context>, Map<String, String>> getAllOptions() {
		 ImmutableMap.Builder<Set<Context>, Map<String, String>> builder = ImmutableMap.builder();
		 for (Entry<String, Map<String, String>> option : this.options.entrySet()) {
		 	builder.put(EContextCalculator.of(option.getKey()), ImmutableMap.copyOf(option.getValue()));
		 }
		 return builder.build();
	}

	@Override
	public Map<String, String> getOptions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		
		return this.getOptions(EContextCalculator.getWorld(contexts).orElse(""));
	}
	
	public Map<String, String> getOptions(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		Map<String, String> ret = this.options.get(typeWorld);
		if (ret == null) return ImmutableMap.of();
		return ImmutableMap.copyOf(ret);
	}

	public void setOptionExecute(final String typeWorld, final String key, final String value) {
		this.write_lock.lock();
		try {
			Map<String, String> origMap = this.options.get(typeWorld);
			if (origMap == null) {						
				this.options.put(typeWorld, ImmutableMap.of(key.toLowerCase(), value));
			} else {
				// Si on supprime l'option
				Map<String, String> newMap = new HashMap<String, String>(origMap);
				if (value == null) {
					newMap.remove(key);
				} else {
					newMap.put(key, value);
				}
				this.options.put(typeWorld, ImmutableMap.copyOf(newMap));
			}
		} finally {
			this.write_lock.unlock();
		}
	}
	
	public void clearOptionsExecute(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.options.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
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
