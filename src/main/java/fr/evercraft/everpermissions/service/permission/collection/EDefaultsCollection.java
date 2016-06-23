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

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.EPermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.util.Arrays;

public class EDefaultsCollection extends ESubjectCollection {
	
    public EDefaultsCollection(final EverPermissions plugin) {
    	super(plugin, EPermissionService.SUBJECTS_DEFAULTS);
    }
    
    @Override
    public Subject get(String identifier) {
    	return this.getDefaults();
    }
    
    @Override
    public Iterable<Subject> getAllSubjects() {
        return Arrays.asList(this.getDefaults());
    }
    
    @Override
    public boolean hasRegistered(String identifier) {
    	return identifier.equals(EPermissionService.SUBJECTS_DEFAULTS);
    }
	
	@Override
	public void reload() {
	}
	
}
