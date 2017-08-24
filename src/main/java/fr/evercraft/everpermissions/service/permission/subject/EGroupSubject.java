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

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.data.EGroupData;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EGroupSubject extends ESubject {
	private final EGroupData data;
	private final EGroupData transientData;
	
	private final Set<String> typeWorlds;
	
    public EGroupSubject(final EverPermissions plugin, final String identifier, final ESubjectCollection<?> collection) {
    	super(plugin, identifier, collection);
    	
    	this.data = new EGroupData(this.plugin, this, false);
        this.transientData = new EGroupData(this.plugin, this, true);
        
        this.typeWorlds = new HashSet<String>();
    }
    
    public void reload() {
		this.data.reload();
		this.transientData.reload();
    }
    
    public CompletableFuture<Boolean> load() {
    	return this.data.load().thenCompose(result -> {
    		if (!result) return CompletableFuture.completedFuture(false);
    		return this.transientData.load();
    	});
	}

    /*
     * Accesseurs
     */
    
    @Override
	public EGroupData getSubjectData() {
		return this.data;
	}

	@Override
	public EGroupData getTransientSubjectData() {
		return this.transientData;
	}
	
	@Override
	public boolean isSubjectDataPersisted() {
		return true;
	}
	
	/*
     * Permissions
     */
	
	public Tristate getPermissionValue(final Set<Context> contexts, final String permission) {    	
		String typeWorldGroup = this.plugin.getService().getContextCalculator().getGroup(contexts);
		// TempoData : Permissions
		Tristate value = this.getTransientSubjectData().getNodeTree(typeWorldGroup).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			this.plugin.getELogger().debug("TransientSubjectData 'Permissions' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
			return value;
		}
    	
		// TempoData : Groups
    	Iterator<SubjectReference> subjects = this.getTransientSubjectData().getParents(typeWorldGroup).iterator();
    	while(subjects.hasNext()) {
    		value = ((ESubject)subjects.next().resolve().join()).getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getELogger().debug("TransientSubjectData 'Parents' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.getSubjectData().getNodeTree(typeWorldGroup).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			this.plugin.getELogger().debug("SubjectData 'Permissions' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
			return value;
		}
    	
    	// SubjectData : SubGroup
    	subjects = this.getSubjectData().getParents(typeWorldGroup).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getELogger().debug("SubjectData 'Parent' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	this.plugin.getELogger().debug("SubjectData '' : (identifier='" + this.identifier + "';permission='" + permission + "';value='UNDEFINED')");
        return Tristate.UNDEFINED;
    }
	
	/*
     * Options
     */
	
	@Override
    public Optional<String> getOption(final Set<Context> contexts, final String option) {
		String typeWorldGroup = this.plugin.getService().getContextCalculator().getGroup(contexts);
		// TempoData : Permissions
    	String value = this.getTransientSubjectData().getOptions(typeWorldGroup).get(option);
		if (value != null) {
			this.plugin.getELogger().debug("TransientSubjectData 'Options' : (identifier='" + this.identifier + "';option='" + option + "';value='" + value + "')");
			return Optional.of(value);
		}
    	
		// TempoData : Groups
    	Iterator<SubjectReference> subjects = this.getTransientSubjectData().getParents(typeWorldGroup).iterator();
    	while(subjects.hasNext()) {
    		value = ((ESubject)subjects.next().resolve().join()).getOption(contexts, option).orElse(null);
    		if (value != null) {
    			this.plugin.getELogger().debug("TransientSubjectData 'Parents' : (identifier='" + this.identifier + "';option='" + option + "';value='" + value + "')");
    			return Optional.of(value);
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.getSubjectData().getOptions(typeWorldGroup).get(option);
		if (value != null) {
			this.plugin.getELogger().debug("SubjectData 'Options' : (identifier='" + this.identifier + "';option='" + option + "';value='" + value + "')");
			return Optional.of(value);
		}
    	
    	// SubjectData : SubGroup
    	subjects = this.getSubjectData().getParents(typeWorldGroup).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			this.plugin.getELogger().debug("SubjectData 'Parent' : (identifier='" + this.identifier + "';option='" + option + "';value='" + value + "')");
    			return Optional.of(value);
    		}
    	}
    	this.plugin.getELogger().debug("SubjectData '' : (identifier='" + this.identifier + "';option='" + option + "';value='EMPTY')");
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
			this.getSubjectData().onUpdate();
			return true;
		}, this.plugin.getThreadAsync());
	}
    
    public boolean isDefault(final String typeWorld) {
    	Optional<EGroupSubject> oldDefault = this.plugin.getService().getGroupSubjects().getDefaultGroup(typeWorld);
    	return oldDefault.isPresent() && oldDefault.get().equals(this);
    }
	
	public Set<String> getTypeWorlds() {
		this.read_lock.lock();
		try {
			return ImmutableSet.copyOf(this.typeWorlds);
		} finally {
			this.read_lock.unlock();
		}
	}
	
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
	
	public void clear(final String typeWorld) {
		this.write_lock.lock();
		try {
			this.data.clearParentsExecute(typeWorld);
			this.data.clearOptionsExecute(typeWorld);
			this.data.clearPermissionsExecute(typeWorld);
			
			this.transientData.clearParents(typeWorld);
			this.transientData.clearOptions(typeWorld);
			this.transientData.clearPermissions(typeWorld);
		
			this.typeWorlds.remove(typeWorld);
		} finally {
			this.write_lock.unlock();
		}
	}

	public CompletableFuture<Boolean> remove(String typeWorld) {
		return null;
	}
}