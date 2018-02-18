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
import fr.evercraft.everpermissions.service.permission.collection.EPSubjectCollection;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EPTransientSubject extends EPSubject {
	private final MemorySubjectData data;
	private final MemorySubjectData transientData;

    public EPTransientSubject(final EverPermissions plugin, final String identifier, final EPSubjectCollection<?> collection) {
    	super(plugin, identifier, collection);
    	
    	this.data = new MemorySubjectData(this);
        this.transientData = new MemorySubjectData(this);
    }
    
    @Override
	public void reload() {}

	/*
     * Permissions
     */
	
    @Override
    public Tristate getPermissionValue(final Set<Context> contexts, final String permission) {
    	// TempoData : Permissions
    	Tristate value = this.transientData.getNodeTree(contexts).get(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// TempoData : Groupes
    	for (SubjectReference subject : this.transientData.getParents(contexts)) {
    		value = subject.resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
    	
    	// MemoryData : Permissions
		value = this.data.getNodeTree(contexts).get(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// MemoryData : Groupes
    	for (SubjectReference subject : this.data.getParents(contexts)) {
    		value = subject.resolve().join().getPermissionValue(contexts, permission);
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
		// TempoData : Permissions
    	String value = this.transientData.getOptions(contexts).get(option);
		if (value != null) {
			this.plugin.getELogger().debug("TransientSubjectData 'Options' : (identifier='" + this.identifier + "';option='" + option + "';value='" + value + "')");
			return Optional.of(value);
		}
    	
		// TempoData : Groups
		for (SubjectReference subject : this.transientData.getParents(contexts)) {
    		value = subject.resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			this.plugin.getELogger().debug("TransientSubjectData 'Parents' : (identifier='" + this.identifier + "';option='" + option + "';value='" + value + "')");
    			return Optional.of(value);
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.data.getOptions(contexts).get(option);
		if (value != null) {
			this.plugin.getELogger().debug("SubjectData 'Options' : (identifier='" + this.identifier + "';option='" + option + "';value='" + value + "')");
			return Optional.of(value);
		}
    	
    	// SubjectData : SubGroup
		for (SubjectReference subject : this.transientData.getParents(contexts)) {
    		value = subject.resolve().join().getOption(contexts, option).orElse(null);
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
    	
    	List<SubjectReference> list = new ArrayList<SubjectReference>();
    	list.addAll(this.getSubjectData().getParents(contexts));
    	list.addAll(this.getTransientSubjectData().getParents(contexts));
        return list;
    }

	@Override
	public MemorySubjectData getSubjectData() {
		return this.data;
	}

	@Override
	public MemorySubjectData getTransientSubjectData() {
		return this.transientData;
	}
	
	@Override
	public boolean isSubjectDataPersisted() {
		return false;
	}

	@Override
	public List<SubjectReference> getParents(String typeGroup) {
		return Arrays.asList();
	}
}
