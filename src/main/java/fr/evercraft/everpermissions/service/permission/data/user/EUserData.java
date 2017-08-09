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
package fr.evercraft.everpermissions.service.permission.data.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import fr.evercraft.everapi.event.PermUserEvent.Action;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.data.ENode;
import fr.evercraft.everpermissions.service.permission.data.ESubjectData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EUserData extends ESubjectData {	
	protected final ConcurrentMap<String, SubjectReference> parents;
	
	public EUserData(final EverPermissions plugin, final EUserSubject subject) {
		super(plugin, subject);
		
		this.parents = new ConcurrentHashMap<String, SubjectReference>();
	}

	@Override
	public CompletableFuture<Boolean> reload() {
		return super.reload().thenCompose(result -> {
			if (!result) return CompletableFuture.completedFuture(false);
			
			this.write_lock.lock();
			try {
				this.parents.clear();
			} finally {
				this.write_lock.unlock();
			}
			
			return this.load();
		});
	}
	
	public CompletableFuture<Boolean> load() {
		return this.plugin.getManagerData().getUserData().load(this)
			.thenApply(result -> {
				if (!result) return false;
				
				this.write_lock.lock();
				try {
					// Chargement des groupes par défault
					for (World world : this.plugin.getGame().getServer().getWorlds()) {
						if (!world.isLoaded()) continue;
						
						Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world.getName());
						Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world.getName());
						if (!type_group.isPresent() || !type_user.isPresent()) continue;
						if (this.getParent(type_user.get()).isPresent()) continue;
						
						Optional<EGroupSubject> subject = this.plugin.getService().getGroupSubjects().getDefaultGroup(type_group.get());
						if (subject.isPresent()) {
							this.addParentExecute(type_user.get(), subject.get().asSubjectReference());
							this.plugin.getELogger().debug("Loading : ("
									+ "identifier=" + this.getIdentifier() + ";"
									+ "default_group=" + subject.get().getIdentifier() + ";"
									+ "type=" + type_user.get() + ")");
						}
					}
					return true;
				} finally {
					this.write_lock.unlock();
				}
			});
	}
	
	/*
	 * Permissions
	 */

	@Override
	public Map<String, Boolean> getPermissions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getPermissions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public Map<String, Boolean> getPermissions(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			ENode perms = this.permissions.get(typeWorld);
			if (perms == null) return ImmutableMap.of();
			return perms.asMap();
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.setPermission(this.plugin.getService().getContextCalculator().getUser(contexts), permission, value);
	}
	
	public CompletableFuture<Boolean> setPermission(final String typeWorld, final String permission, final Tristate value) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(permission, "permission");
		Preconditions.checkNotNull(value, "value");
		
		Boolean oldValue = this.getNodeTree(typeWorld).asMap().get(permission);
		boolean insert = oldValue == null;
		
		if (insert && value.equals(Tristate.UNDEFINED)) return CompletableFuture.completedFuture(false);
		if (!insert && !value.equals(Tristate.UNDEFINED) && oldValue.booleanValue() == value.asBoolean()) return CompletableFuture.completedFuture(false);

		return this.plugin.getManagerData().getUserData().setPermission(this.getIdentifier(), typeWorld, permission, value, insert)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.setPermissionExecute(typeWorld, permission, value);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	@Override
	public CompletableFuture<Boolean> clearPermissions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearPermissions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
		
	public CompletableFuture<Boolean> clearPermissions(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			if (this.permissions.containsKey(typeWorld)) return CompletableFuture.completedFuture(false);
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().clearPermissions(this.getIdentifier(), typeWorld)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearPermissionsExecute(typeWorld);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	@Override
	public CompletableFuture<Boolean> clearPermissions() {
		this.read_lock.lock();
		try {
			if (this.permissions.isEmpty()) return CompletableFuture.completedFuture(false);
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().clearPermissions(this.getIdentifier())
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearPermissionsExecute();
				this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}

	/*
	 * Groups
	 */
	
	@Override
	public Map<Set<Context>, List<SubjectReference>> getAllParents() {
		this.read_lock.lock();
		try {
			Map<String, List<SubjectReference>> ret = new HashMap<String, List<SubjectReference>>();
			for (Entry<String, SubjectReference> parent : this.parents.entrySet()) {
				List<SubjectReference> list = new ArrayList<SubjectReference>();
				list.add(parent.getValue());
				ret.put(parent.getKey(), list);
			}
			
			for (Entry<String, List<SubjectReference>> parents : this.groups.entrySet()) {
				List<SubjectReference> list = ret.get(parents.getKey());
				if (list == null) {
					ret.put(parents.getKey(), parents.getValue());
				} else {
					list.addAll(parents.getValue());
				}
			}
			
			ImmutableMap.Builder<Set<Context>, List<SubjectReference>> builder = ImmutableMap.builder();
			for (Entry<String, List<SubjectReference>> parents : ret.entrySet()) {
				builder.put(EContextCalculator.of(parents.getKey()), ImmutableList.copyOf(parents.getValue()));
			}
			return builder.build();
		} finally {
			this.read_lock.unlock();
		}
	}

	@Override
	public List<SubjectReference> getParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getParents(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public List<SubjectReference> getParents(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			List<SubjectReference> ret = this.groups.get(typeWorld);
			if (ret == null) return ImmutableList.of();
			return ret;
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public Optional<SubjectReference> getParent(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getParent(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public Optional<SubjectReference> getParent(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			return Optional.ofNullable(this.parents.get(typeWorld));
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> addParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.addParent(this.plugin.getService().getContextCalculator().getUser(contexts), parent);
	}
	
	public CompletableFuture<Boolean> addParent(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		this.read_lock.lock();
		try {
			SubjectReference oldParents = this.parents.get(typeWorld);
			boolean insert = oldParents == null;
			
			if (!insert && oldParents.equals(parent)) return CompletableFuture.completedFuture(false);
			
			return this.plugin.getManagerData().getUserData().addParent(this.getIdentifier(), typeWorld, parent, insert)
				.thenApplyAsync(result -> {
					if (!result) return false;
					
					this.addParentExecute(typeWorld, oldParents);
					this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
					return true;
				}, this.plugin.getThreadSync());
		} finally {
			this.read_lock.unlock();
		}
	}
	
	@Override
	public void addParentExecute(final String typeWorld, final SubjectReference parent) {
		this.write_lock.lock();
		try {
			this.parents.put(typeWorld, parent);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> removeParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.removeParent(this.plugin.getService().getContextCalculator().getUser(contexts), parent);
	}
	
	public CompletableFuture<Boolean> removeParent(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		this.read_lock.lock();
		try {
			SubjectReference oldParents = this.parents.get(typeWorld);
			if (oldParents == null || !oldParents.equals(parent)) return CompletableFuture.completedFuture(false);
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().removeParent(this.getIdentifier(), typeWorld, parent)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.removeParentExecute(typeWorld, parent);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	@Override
	public void removeParentExecute(final String typeWorld, final SubjectReference parent) {
		this.write_lock.lock();
		try {
			this.parents.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	@Override
	public CompletableFuture<Boolean> clearParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearParents(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public CompletableFuture<Boolean> clearParents(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			if (!this.parents.containsKey(typeWorld)) return CompletableFuture.completedFuture(false);
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().clearParents(this.getIdentifier(), typeWorld)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearParentsExecute(typeWorld);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	@Override
	public void clearParentsExecute(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.parents.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}

	@Override
	public CompletableFuture<Boolean> clearParents() {
		this.read_lock.lock();
		try {
			if (this.parents.isEmpty()) return CompletableFuture.completedFuture(false);
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().clearParents(this.getIdentifier())
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearParentsExecute();
				this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	@Override
	public void clearParentsExecute() {
		this.write_lock.lock();
		try {
			this.parents.clear();
		} finally {
			this.write_lock.unlock();
		}
	}
	
	/*
	 * SubGroups
	 */
	
	public Map<Set<Context>, List<SubjectReference>> getAllSubParents() {
		this.read_lock.lock();
		try {
			ImmutableMap.Builder<Set<Context>, List<SubjectReference>> ret = ImmutableMap.builder();
			for (Entry<String, List<SubjectReference>> ent : this.groups.entrySet()) {
				ret.put(EContextCalculator.of(ent.getKey()), ent.getValue());
			}
			return ret.build();
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public List<SubjectReference> getSubParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getSubParents(this.plugin.getService().getContextCalculator().getUser(contexts));
	}

	public List<SubjectReference> getSubParents(final String typeWorld) {
		this.read_lock.lock();
		try {
			Preconditions.checkNotNull(typeWorld, "typeWorld");
			
			List<SubjectReference> ret = this.groups.get(typeWorld);
			if (ret == null) return ImmutableList.of();
			return ret;
		} finally {
			this.read_lock.unlock();
		}
	}

	public CompletableFuture<Boolean> addSubParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.addSubParent(this.plugin.getService().getContextCalculator().getUser(contexts), parent);
	}
	
	public CompletableFuture<Boolean> addSubParent(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		this.read_lock.lock();
		try {
			List<SubjectReference> parents = this.groups.get(typeWorld);
			if (parents != null && parents.contains(parent)) return CompletableFuture.completedFuture(false);
			
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().addSubParent(this.getIdentifier(), typeWorld, parent)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.addSubParentExecute(typeWorld, parent);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	public void addSubParentExecute(final String typeWorld, final SubjectReference parent) {
		super.addParentExecute(typeWorld, parent);
	}
	
	public CompletableFuture<Boolean> removeSubParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.removeSubParent(this.plugin.getService().getContextCalculator().getUser(contexts), parent);
	}

	public CompletableFuture<Boolean> removeSubParent(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		this.read_lock.lock();
		try {
			List<SubjectReference> parents = this.groups.get(typeWorld);
			if (parents == null || !parents.contains(parent)) return CompletableFuture.completedFuture(false);
			
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().removeSubParent(this.getIdentifier(), typeWorld, parent)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.removeSubParentExecute(typeWorld, parent);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	public void removeSubParentExecute(final String typeWorld, final SubjectReference parent) {
		super.removeParentExecute(typeWorld, parent);
	}
	
	public CompletableFuture<Boolean> clearSubParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearSubParents(this.plugin.getService().getContextCalculator().getUser(contexts));
	}

	public CompletableFuture<Boolean> clearSubParents(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			if (!this.groups.containsKey(typeWorld)) return CompletableFuture.completedFuture(false);
			
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().clearSubParents(this.getIdentifier(), typeWorld)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearSubParentsExecute(typeWorld);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	public void clearSubParentsExecute(final String typeWorld) {
		super.clearParentsExecute(typeWorld);
	}
	
	public CompletableFuture<Boolean> clearSubParents() {
		this.read_lock.lock();
		try {
			if (this.groups.isEmpty()) return CompletableFuture.completedFuture(false);
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().clearSubParents(this.getIdentifier())
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearSubParentsExecute();
				this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	public void clearSubParentsExecute() {
		super.clearParentsExecute();
	}

	/*
	 * Options
	 */
	
	@Override
	public Map<String, String> getOptions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getOptions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
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
	public CompletableFuture<Boolean> setOption(final Set<Context> contexts, final String key, final String value) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.setOption(this.plugin.getService().getContextCalculator().getUser(contexts), key, value);
	}
	
	public CompletableFuture<Boolean> setOption(final String typeWorld, final String key, final String value) {
		String oldValue = this.getOptions(typeWorld).get(key);
		boolean insert = oldValue == null;
		
		if (insert && value == null) return CompletableFuture.completedFuture(false);
		if (!insert && value != null && oldValue.equals(value)) return CompletableFuture.completedFuture(false);
		
		return this.plugin.getManagerData().getUserData().setOption(this.getIdentifier(), typeWorld, key, value, insert)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.setOptionExecute(typeWorld, key, oldValue);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
	
	@Override
	public CompletableFuture<Boolean> clearOptions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearOptions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public CompletableFuture<Boolean> clearOptions(final String typeWorld) {
		this.read_lock.lock();
		try {
			if (!this.options.containsKey(typeWorld)) return CompletableFuture.completedFuture(false);
			
		} finally {
			this.read_lock.unlock();
		}

		return this.plugin.getManagerData().getUserData().clearOptions(this.getIdentifier(), typeWorld)
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearOptionsExecute(typeWorld);
				this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}

	@Override
	public CompletableFuture<Boolean> clearOptions() {
		this.read_lock.lock();
		try {
			if (this.options.isEmpty()) return CompletableFuture.completedFuture(false);
			
		} finally {
			this.read_lock.unlock();
		}
		
		return this.plugin.getManagerData().getUserData().clearOptions(this.getIdentifier())
			.thenApplyAsync(result -> {
				if (!result) return false;
				
				this.clearOptionsExecute();
				this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
				return true;
			}, this.plugin.getThreadSync());
	}
}
