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
package fr.evercraft.everpermissions.service.permission.collection;

import fr.evercraft.everapi.event.PermOtherEvent.Action;
import fr.evercraft.everapi.util.Chronometer;
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
    	
		if (!this.subjects.containsKey(identifier)) {
			Chronometer chronometer = new Chronometer();
			
			EOtherSubject subject = new EOtherSubject(this.plugin, identifier, EOthersCollection.this);
			this.subjects.put(identifier, subject);
			
			this.plugin.getELogger().debug("Loading other '" + identifier + "' in " +  chronometer.getMilliseconds().toString() + " ms");
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
		for (EOtherSubject subject : this.subjects.values()) {
			subject.reload();
		}
	}
	
	/**
	 * Change le nom des commandblocks
	 * @param identifier Le nom du subject
	 * @return Le nom
	 */
	public String getIdentifier(String identifier) {
		if (identifier.equals("@")){
    		identifier = EPermissionService.IDENTIFIER_COMMAND_BLOCK;
    	}
		return identifier;
	}
}
