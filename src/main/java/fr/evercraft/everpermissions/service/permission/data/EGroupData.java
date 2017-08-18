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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EGroupData extends ESubjectData {

    public EGroupData(final EverPermissions plugin, final EGroupSubject subject, boolean transientData) {
        super(plugin, subject, transientData);
    }
    
    public CompletableFuture<Boolean> load() {
    	return CompletableFuture.completedFuture(true);
    }
    
    /*
     * Permissions
     */
    @Override
	public Map<String, Boolean> getPermissions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getPermissions(this.plugin.getService().getContextCalculator().getGroup(contexts));
	}
	
	@Override
	public CompletableFuture<Boolean> setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.setPermission(this.plugin.getService().getContextCalculator().getGroup(contexts), permission, value);
	}
	
	@Override
	public CompletableFuture<Boolean> clearPermissions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearPermissions(this.plugin.getService().getContextCalculator().getGroup(contexts));
	}
	
	/*
	 * Options
	 */
	
	@Override
	public Map<String, String> getOptions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getOptions(this.plugin.getService().getContextCalculator().getGroup(contexts));
	}
	
	
	
	@Override
	public CompletableFuture<Boolean> setOption(final Set<Context> contexts, final String key, final String value) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.setOption(this.plugin.getService().getContextCalculator().getGroup(contexts), key, value);
	}
	
	@Override
	public CompletableFuture<Boolean> clearOptions(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearOptions(this.plugin.getService().getContextCalculator().getGroup(contexts));
	}

    /*
     * Parent
     */
	
	public List<SubjectReference> getParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.getParents(this.plugin.getService().getContextCalculator().getGroup(contexts));
	}
	
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
	
	public CompletableFuture<Boolean> addParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.addParent(this.plugin.getService().getContextCalculator().getGroup(contexts), parent);
	}
	
	public CompletableFuture<Boolean> removeParent(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.removeParent(this.plugin.getService().getContextCalculator().getGroup(contexts), parent);
	}
    
	public CompletableFuture<Boolean> removeParent(final String typeWorld, final SubjectReference parent) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		Preconditions.checkNotNull(parent, "parent");
		
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				List<SubjectReference> parents = this.parents.get(typeWorld);
				if (parents == null || !parents.contains(parent)) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.plugin.getManagerData().get(this.getCollectionIdentifier()).removeParent(this, typeWorld, parent)) return false;
			
			this.removeParentExecute(typeWorld, parent);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void removeParentExecute(final String typeWorld, final SubjectReference parent) {
		this.write_lock.lock();
		try {
			List<SubjectReference> parents = new ArrayList<SubjectReference>(this.parents.get(typeWorld));
			parents.remove(parent);
			this.parents.replace(typeWorld, ImmutableList.copyOf(parents));
		} finally {
			this.write_lock.unlock();
		}
	}
	
	public CompletableFuture<Boolean> clearParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		return this.clearParents(this.plugin.getService().getContextCalculator().getGroup(contexts));
	}
	
	public CompletableFuture<Boolean> clearParents(final String typeWorld) {
		Preconditions.checkNotNull(typeWorld, "typeWorld");
		
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (!this.parents.containsKey(typeWorld)) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.plugin.getManagerData().get(this.getCollectionIdentifier()).clearParents(this, typeWorld)) return false;
			
			this.clearParentsExecute(typeWorld);
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void clearParentsExecute(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.parents.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}
	
	public CompletableFuture<Boolean> clearParents() {
		return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			try {
				if (this.parents.isEmpty()) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.plugin.getManagerData().get(this.getCollectionIdentifier()).clearParents(this)) return false;
			
			this.clearParentsExecute();
			this.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
	
	public void clearParentsExecute() {
		this.write_lock.lock();
		try {
			this.parents.clear();
		} finally {
			this.write_lock.unlock();
		}
	}
}
