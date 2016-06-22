package fr.evercraft.everpermissions.service.permission.collection;

import fr.evercraft.everapi.event.PermOtherEvent.Action;
import fr.evercraft.everapi.java.Chronometer;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.EPermissionService;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

import org.spongepowered.api.service.permission.Subject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EOthersCollection extends ESubjectCollection {
	private final ConcurrentMap<String, EOtherSubject> subjects;
	
    public EOthersCollection(final EverPermissions plugin, final String identifier) {
    	super(plugin, identifier);
    	this.subjects = new ConcurrentHashMap<String, EOtherSubject>();
    }
    
    @Override
    public EOtherSubject get(String identifier) {
    	identifier = getIdentifier(identifier);
    	
		if(!this.subjects.containsKey(identifier)) {
			Chronometer chronometer = new Chronometer();
			
			EOtherSubject subject = new EOtherSubject(this.plugin, identifier, EOthersCollection.this);
			this.subjects.put(identifier, subject);
			
			this.plugin.getLogger().debug("Loading other '" + identifier + "' in " +  chronometer.getMilliseconds().toString() + " ms");
			this.plugin.getManagerEvent().post(subject, Action.OTHER_ADDED);
			
			return subject;
    	}
    	return this.subjects.get(identifier);
    }
    
    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Iterable<Subject> getAllSubjects() {
        return (Iterable) this.subjects.values();
    }
    
    @Override
    public boolean hasRegistered(String identifier) {
    	return this.subjects.containsKey(getIdentifier(identifier));
    }
	
	@Override
	public void reload() {
		for(EOtherSubject subject : this.subjects.values()) {
			subject.reload();
		}
	}
	
	/**
	 * Change le nom des commandblocks
	 * @param identifier Le nom du subject
	 * @return Le nom
	 */
	public String getIdentifier(String identifier) {
		if(identifier.equals("@")){
    		identifier = EPermissionService.IDENTIFIER_COMMAND_BLOCK;
    	}
		return identifier;
	}
}
