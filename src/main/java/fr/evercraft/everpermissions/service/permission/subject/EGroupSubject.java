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
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.data.group.EGroupData;
import fr.evercraft.everpermissions.service.permission.data.group.ETransientGroupData;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class EGroupSubject extends ESubject {
	private final EGroupData data;
	private final ETransientGroupData transientData;
	
	private final CopyOnWriteArraySet<String> worlds;
	
    public EGroupSubject(final EverPermissions plugin, final String identifier, final ESubjectCollection collection) {
    	super(plugin, identifier, collection);
    	
    	this.data = new EGroupData(this.plugin, this);
        this.transientData = new ETransientGroupData(this.plugin, this);
        
        this.worlds = new CopyOnWriteArraySet<String>();
    }

    /*
     * Accesseurs
     */
    
    @Override
	public EGroupData getSubjectData() {
		return this.data;
	}

	@Override
	public MemorySubjectData getTransientSubjectData() {
		return this.transientData;
	}
	
	/*
     * Permissions
     */
	
	@Override
    public Tristate getPermissionValue(final Set<Context> type_contexts, final String permission) {
    	// TempoData : Permissions
    	Tristate value = this.getTransientSubjectData().getNodeTree(type_contexts).get(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// TempoData : Groupes
    	Iterator<Subject> subjects = this.getTransientSubjectData().getParents(type_contexts).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().getPermissionValue(type_contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
    	
    	// MemoryData : Permissions
		value = this.getSubjectData().getNodeTree(type_contexts).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// MemoryData : Groupes
    	subjects = this.getSubjectData().getParents(type_contexts).iterator();
    	while(subjects.hasNext()) {
    		Tristate tristate = subjects.next().getPermissionValue(type_contexts, permission);
    		if (!tristate.equals(Tristate.UNDEFINED)) {
    			return tristate;
    		}
    	}
        return Tristate.UNDEFINED;
    }
	
	/*
	 * World
	 */
	
	public Set<String> getWorlds() {
		return this.worlds;
	}
	
	public boolean hasWorld(final String type) {
		return this.worlds.contains(type);
	}
	
	public boolean registerWorld(final String type) {
		return this.worlds.add(type);
	}
	
	public void clear(final String type) {
		Set<Context> contexts = EContextCalculator.getContextWorld(type);
		
		this.getSubjectData().clearParentsExecute(contexts);
		this.getSubjectData().clearOptionsExecute(contexts);
		this.getSubjectData().clearPermissionsExecute(contexts);
		
		this.getTransientSubjectData().clearParents(contexts);
		this.getTransientSubjectData().clearOptions(contexts);
		this.getTransientSubjectData().clearPermissions(contexts);
		
		this.worlds.remove(type);
	}
}