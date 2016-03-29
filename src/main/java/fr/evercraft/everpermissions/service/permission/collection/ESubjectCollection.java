/**
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

import fr.evercraft.everpermissions.EverPermissions;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Subject collection
 */
public abstract class ESubjectCollection implements SubjectCollection {
	protected final EverPermissions plugin;
    private final String identifier;

    public ESubjectCollection(final EverPermissions plugin, final String identifier) {
    	this.plugin = plugin;
        this.identifier = identifier;
    }
    
    @Override
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    public Map<Subject, Boolean> getAllWithPermission(final String permission) {
    	final Map<Subject, Boolean> map = new HashMap<Subject, Boolean>();
        for (Subject subject : getAllSubjects()) {
            Tristate value = subject.getPermissionValue(subject.getActiveContexts(), permission);
            if (value != Tristate.UNDEFINED) {
            	map.put(subject, value.asBoolean());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Map<Subject, Boolean> getAllWithPermission(final Set<Context> contexts, final String permission) {
    	final Map<Subject, Boolean> ret = new HashMap<Subject, Boolean>();
        for (Subject subject : getAllSubjects()) {
            Tristate value = subject.getPermissionValue(contexts, permission);
            if (value != Tristate.UNDEFINED) {
                ret.put(subject, value.asBoolean());
            }
        }
        return Collections.unmodifiableMap(ret);
    }
    
    public abstract void reload();
}
