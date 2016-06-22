package fr.evercraft.everpermissions.service.permission.subject;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.Iterator;
import java.util.Set;

public class ETempateSubject extends ESubject {
	private final MemorySubjectData data;
	private final MemorySubjectData transientData;

    public ETempateSubject(final EverPermissions plugin, final String identifier, final ESubjectCollection collection) {
    	super(plugin, identifier, collection);
    	
    	this.data = new MemorySubjectData(this.plugin.getService());
        this.transientData = new MemorySubjectData(this.plugin.getService());
    }

    /*
     * Accesseurs
     */
    
    @Override
	public MemorySubjectData getSubjectData() {
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
    public Tristate getPermissionValue(final Set<Context> contexts, final String permission) {
    	// TempoData : Permissions
    	Tristate value = this.getTransientSubjectData().getNodeTree(contexts).get(permission);
		if(!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// TempoData : Groupes
    	Iterator<Subject> subjects = this.getTransientSubjectData().getParents(contexts).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().getPermissionValue(contexts, permission);
    		if(!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
    	
    	// MemoryData : Permissions
		value = this.getSubjectData().getNodeTree(contexts).get(permission);
		if(!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// MemoryData : Groupes
    	subjects = this.getSubjectData().getParents(contexts).iterator();
    	while(subjects.hasNext()) {
    		Tristate tristate = subjects.next().getPermissionValue(contexts, permission);
    		if(!tristate.equals(Tristate.UNDEFINED)) {
    			return tristate;
    		}
    	}
        return Tristate.UNDEFINED;
    }
}