package fr.evercraft.everpermissions.service.permission.collection;

import fr.evercraft.everapi.java.Chronometer;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ETemplateCollection extends ESubjectCollection {
    private final Map<String, EOtherSubject> subjects;

    public ETemplateCollection(final EverPermissions plugin) {
    	super(plugin, PermissionService.SUBJECTS_ROLE_TEMPLATE);
    	
    	this.subjects = new ConcurrentHashMap<String, EOtherSubject>();
    }
    
    @Override
    public EOtherSubject get(final String identifier) {
		if(!this.subjects.containsKey(identifier)) {			
			Chronometer chronometer = new Chronometer();
			
			EOtherSubject subject = new EOtherSubject(this.plugin, identifier, ETemplateCollection.this);
			this.subjects.put(identifier, subject);
			
			this.plugin.getLogger().debug("Loading template '" + identifier + "' in " +  chronometer.getMilliseconds().toString() + " ms");
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
    	return subjects.containsKey(identifier);
    }
    
    @Override
	public void reload() {
	}
}
