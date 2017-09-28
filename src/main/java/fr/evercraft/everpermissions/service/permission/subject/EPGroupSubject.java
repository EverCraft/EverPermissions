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

import fr.evercraft.everapi.services.permission.EGroupSubject;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EPContextCalculator;
import fr.evercraft.everpermissions.service.permission.collection.EPSubjectCollection;
import fr.evercraft.everpermissions.service.permission.data.EPGroupData;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EPGroupSubject extends EPSubject implements EGroupSubject {
	private final EPGroupData data;
	private final EPGroupData transientData;
	
	private final Set<String> typeWorlds;
	
    public EPGroupSubject(final EverPermissions plugin, final String identifier, final EPSubjectCollection<?> collection) {
    	super(plugin, identifier, collection);
    	
    	this.data = new EPGroupData(this.plugin, this, false);
        this.transientData = new EPGroupData(this.plugin, this, true);
        
        this.typeWorlds = new HashSet<String>();
    }
    
    public void reload() {
		this.data.reload();
		//this.transientData.reload();
		this.typeWorlds.clear();
    }

    /*
     * Accesseurs
     */
    
    @Override
	public String getName() {
		return this.getFriendlyIdentifier().orElse(this.getIdentifier());
	}
    
    @Override
	public EPGroupData getSubjectData() {
		return this.data;
	}

	@Override
	public EPGroupData getTransientSubjectData() {
		return this.transientData;
	}
	
	@Override
	public boolean isSubjectDataPersisted() {
		return true;
	}
	
	/*
     * Permissions
     */
	
	@Override
	public Tristate getPermissionValue(final Set<Context> contexts, final String permission) {  		
		Tristate value = this.getPermissionExecute(contexts, permission);
		this.verbose(EPContextCalculator.getWorld(contexts), permission, value);
		return value;
	}
	
	private Tristate getPermissionExecute(final Set<Context> contexts, final String permission) {  		
		String typeWorldGroup = this.plugin.getService().getContextCalculator().getGroup(contexts);
		// TempoData : Permissions
		Tristate value = this.transientData.getNodeTree(typeWorldGroup).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// TempoData : Parent
    	for (SubjectReference parent : this.transientData.getParents(typeWorldGroup)) {
    		value = parent.resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.data.getNodeTree(typeWorldGroup).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
    	// SubjectData : Parent
    	for (SubjectReference parent : this.data.getParents(typeWorldGroup)) {
    		value = parent.resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
        return Tristate.UNDEFINED;
    }
	
	/*
     * Options
     */
	@Override
    public Optional<String> getOption(final Set<Context> contexts, final String option) {
		Optional<String> value = this.getOptionExecute(contexts, option);
		this.verbose(EPContextCalculator.getWorld(contexts), option, value);
		return value;
	}

	private Optional<String> getOptionExecute(final Set<Context> contexts, final String option) {
		String typeWorldGroup = this.plugin.getService().getContextCalculator().getGroup(contexts);
		// TempoData : Permissions
    	String value = this.transientData.getOptions(typeWorldGroup).get(option);
		if (value != null) {
			return Optional.of(value);
		}
    	
		// TempoData : Parent
    	for (SubjectReference parent : this.transientData.getParents(typeWorldGroup)) {
    		value = parent.resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			return Optional.of(value);
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.data.getOptions(typeWorldGroup).get(option);
		if (value != null) {
			return Optional.of(value);
		}
    	
		// SubjectData : Parent
    	for (SubjectReference parent : this.data.getParents(typeWorldGroup)) {
    		value = parent.resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			return Optional.of(value);
    		}
    	}
        return Optional.empty();
    }

	/*
     * Groupes
     */
    
    @Override
    public List<SubjectReference> getParents(final Set<Context> contexts) {
    	Preconditions.checkNotNull(contexts, "contexts");
        return this.getParents(this.plugin.getService().getContextCalculator().getGroup(contexts));
    }
    
    
    public List<SubjectReference> getParents(final String typeWorldGroup) {
    	List<SubjectReference> list = new ArrayList<SubjectReference>();
    	list.addAll(this.data.getParents(typeWorldGroup));
    	list.addAll(this.transientData.getParents(typeWorldGroup));
        return list;
    }
	
	/*
	 * World
	 */
    
    @Override
    public CompletableFuture<Boolean> setDefault(final String typeWorld, boolean value) {
    	Preconditions.checkNotNull(typeWorld, "typeWorld");
    	
    	return CompletableFuture.supplyAsync(() -> {
			this.read_lock.lock();
			
			Optional<EGroupSubject> oldDefault = this.plugin.getService().getGroupSubjects().getDefaultGroup(typeWorld);
			try {
				if (value && oldDefault.isPresent()) return false;
				if (!value && !oldDefault.isPresent()) return false;
			} finally {
				this.read_lock.unlock();
			}
			
			if (!this.getContainingCollection().getStorage().setDefault(this, typeWorld, value)) return false;
			
			this.plugin.getService().getGroupSubjects().setDefaultExecute(typeWorld, this, value);
			this.data.onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
    
    @Override
    public boolean isDefault(final String typeWorld) {
    	Optional<EGroupSubject> oldDefault = this.plugin.getService().getGroupSubjects().getDefaultGroup(typeWorld);
    	return oldDefault.isPresent() && oldDefault.get().equals(this);
    }
	
    @Override
	public Set<String> getTypeWorlds() {
		this.read_lock.lock();
		try {
			return ImmutableSet.copyOf(this.typeWorlds);
		} finally {
			this.read_lock.unlock();
		}
	}
	
    @Override
	public boolean hasTypeWorld(final String typeWorld) {
		this.read_lock.lock();
		try {
			return this.typeWorlds.contains(typeWorld);
		} finally {
			this.read_lock.unlock();
		}
	}
	
	public boolean registerTypeWorld(final String typeWorld) {
		this.write_lock.lock();
		try {
			return this.typeWorlds.add(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}

	public CompletableFuture<Boolean> clear(String typeWorld) {
		return this.data.clear(typeWorld).thenCompose(result -> {
			if (!result) return CompletableFuture.completedFuture(false);
			return this.transientData.clear(typeWorld);
		}).thenApply(result -> {
			if (!result) return false;
			
			this.write_lock.lock();
			try {
				this.typeWorlds.remove(typeWorld);
				
				if (this.typeWorlds.isEmpty()) {
					this.collection.suggestUnload(this.identifier);
				}
			} finally {
				this.write_lock.unlock();
			}
			return true;
		});
	}
	
	public void clearCache() {
    	this.write_lock.lock();
		try {
	    	this.plugin.getService().clearCache();
		} finally {
			this.write_lock.unlock();
		}
    }
}
