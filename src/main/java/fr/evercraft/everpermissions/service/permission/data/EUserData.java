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

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubject;

public class EUserData extends ESubjectData {	
	protected final ConcurrentMap<String, SubjectReference> groups;
	
	public EUserData(final EverPermissions plugin, final ESubject subject, boolean transientData) {
		super(plugin, subject, transientData);
		
		this.groups = new ConcurrentHashMap<String, SubjectReference>();
	}

	@Override
	public CompletableFuture<Boolean> reload() {
		return super.reload().thenCompose(result -> {
			if (!result) return CompletableFuture.completedFuture(false);
			
			this.write_lock.lock();
			try {
				this.groups.clear();
			} finally {
				this.write_lock.unlock();
			}
			
			return this.load();
		});
	}
	
	public CompletableFuture<Boolean> load() {
		return CompletableFuture.supplyAsync(() -> {
			this.write_lock.lock();
			try {
				this.parents.clear();
				this.groups.clear();
				this.permissions.clear();
				this.options.clear();
				
				if (!this.getSubject().getContainingCollection().getStorage().load(this.subject)) return false;
				
				// Chargement des groupes par défault
				for (World world : this.plugin.getGame().getServer().getWorlds()) {
					if (!world.isLoaded()) continue;
					
					Optional<String> type_group = this.plugin.getService().getGroupSubjects().getTypeWorld(world.getName());
					Optional<String> type_user = this.plugin.getService().getUserSubjects().getTypeWorld(world.getName());
					if (!type_group.isPresent() || !type_user.isPresent()) continue;
					if (this.getGroup(type_user.get()).isPresent()) continue;
					
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
		}, this.plugin.getThreadAsync());
	}
	
	/*
	 * Permissions
	 */

	@Override
	public Map<String, Boolean> getPermissions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getPermissions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	@Override
	public CompletableFuture<Boolean> setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.setPermission(this.plugin.getService().getContextCalculator().getUser(contexts), permission, value);
	}
	
	@Override
	public CompletableFuture<Boolean> clearPermissions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearPermissions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}

	/*
	 * Groups
	 */
	
	@Override
	public Map<Set<Context>, List<SubjectReference>> getAllParents() {
		this.read_lock.lock();
		try {
			Map<String, List<SubjectReference>> ret = new HashMap<String, List<SubjectReference>>();
			for (Entry<String, SubjectReference> parent : this.groups.entrySet()) {
				List<SubjectReference> list = new ArrayList<SubjectReference>();
				list.add(parent.getValue());
				ret.put(parent.getKey(), list);
			}
			
			for (Entry<String, List<SubjectReference>> parents : this.parents.entrySet()) {
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
			ImmutableList.Builder<SubjectReference> ret = ImmutableList.builder();
			
			SubjectReference parent = this.groups.get(typeWorld);
			if (parent != null) {
				ret.add(parent);
			}
			
			List<SubjectReference> group = this.parents.get(typeWorld);
			if (group != null) {
				ret.addAll(group);
			}
			
			return ret.build();
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public Optional<SubjectReference> getGroup(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getGroup(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public Optional<SubjectReference> getGroup(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			return Optional.ofNullable(this.groups.get(typeWorld));
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public List<SubjectReference> getSubGroup(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getSubGroup(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public List<SubjectReference> getSubGroup(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		this.read_lock.lock();
		try {
			return this.parents.get(typeWorld);
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public CompletableFuture<Boolean> setGroup(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.setGroup(this.plugin.getService().getContextCalculator().getUser(contexts), parent);
	}
	
	public CompletableFuture<Boolean> setGroup(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		return CompletableFuture.supplyAsync(() -> {
			boolean insert = true;
			
			this.read_lock.lock();
			try {
				SubjectReference oldParents = this.groups.get(typeWorld);
				insert = oldParents == null;
				
				if (!insert && oldParents.equals(parent)) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().setGroup(this, typeWorld, parent, insert)) return false;
			
			this.setGroupExecute(typeWorld, parent);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void setGroupExecute(final String typeWorld, final SubjectReference parent) {
		this.write_lock.lock();
		try {
			this.groups.put(typeWorld, parent);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	/*
	 * SubGroups
	 */

	public CompletableFuture<Boolean> addParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.addParent(this.plugin.getService().getContextCalculator().getUser(contexts), parent);
	}
	
	public CompletableFuture<Boolean> removeParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.removeParent(this.plugin.getService().getContextCalculator().getUser(contexts), parent);
	}
	
	public CompletableFuture<Boolean> removeParent(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				SubjectReference group = this.groups.get(typeWorld);
				List<SubjectReference> parents = this.parents.get(typeWorld);
				if (!(group == null || !group.equals(parent))  && !(parents == null || !parents.contains(parent))) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().removeParent(this, typeWorld, parent)) return false;
			
			this.removeParentExecute(typeWorld, parent);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void removeParentExecute(final String typeWorld, final SubjectReference parent) {
		this.write_lock.lock();
		try {
			SubjectReference group = this.groups.get(typeWorld);
			if (group != null && group.equals(parent)) {
				this.groups.remove(typeWorld);
			}
			
			List<SubjectReference> parents = new ArrayList<SubjectReference>(this.parents.get(typeWorld));
			parents.remove(parent);
			this.parents.replace(typeWorld, ImmutableList.copyOf(parents));
		} finally {
			this.write_lock.unlock();
		}
	}
	
	public CompletableFuture<Boolean> clearParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearParents(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	public CompletableFuture<Boolean> clearParents(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (!this.parents.containsKey(typeWorld) && !this.groups.containsKey(typeWorld)) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().clearParents(this, typeWorld)) return false;
			
			this.clearParentsExecute(typeWorld);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void clearParentsExecute(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.groups.remove(typeWorld);
			this.parents.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	public CompletableFuture<Boolean> clearParents() {
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (this.parents.isEmpty() && this.groups.isEmpty()) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getSubject().getContainingCollection().getStorage().clearParents(this)) return false;
			
			this.clearParentsExecute();
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void clearParentsExecute() {
		this.write_lock.lock();
		try {
			this.groups.clear();
			this.parents.clear();
		} finally {
			this.write_lock.unlock();
		}
	}

	/*
	 * Options
	 */
	
	@Override
	public Map<String, String> getOptions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getOptions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
	
	
	
	@Override
	public CompletableFuture<Boolean> setOption(final Set<Context> contexts, final String key, final String value) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.setOption(this.plugin.getService().getContextCalculator().getUser(contexts), key, value);
	}
	
	@Override
	public CompletableFuture<Boolean> clearOptions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearOptions(this.plugin.getService().getContextCalculator().getUser(contexts));
	}
}
