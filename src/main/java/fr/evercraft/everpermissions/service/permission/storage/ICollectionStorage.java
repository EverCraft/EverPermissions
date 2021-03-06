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
package fr.evercraft.everpermissions.service.permission.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.services.permission.ESubject;
import fr.evercraft.everpermissions.service.permission.data.EPSubjectData;
import fr.evercraft.everpermissions.service.permission.subject.EPGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EPSubject;

public interface ICollectionStorage {
	
	public void reload();
	
	boolean register(String typeWorld);

	boolean unregister(String typeWorld);
    
    public boolean load(ESubject subject);
    
    public boolean load(Collection<ESubject> subjects);
    
    public boolean clear(EPSubjectData<?> subject);
    
    public boolean clear(EPSubjectData<?> subject, String typeWorld);
    
    public boolean setFriendlyIdentifier(EPSubject subject, @Nullable String name);
    
    public boolean setDefault(EPGroupSubject subject, String typeWorld, boolean value);
    
    /*
     * Permissions
     */

    public boolean setPermission(EPSubjectData<?> subject, String typeWorld, String  permission, Tristate value, boolean insert);

    public boolean clearPermissions(EPSubjectData<?> subject, String typeWorld);
    
    public boolean clearPermissions(EPSubjectData<?> subject);
    
    /*
     * Options
     */
    
    public boolean setOption(EPSubjectData<?> subject, String typeWorld, String type, String name, boolean insert);

    public boolean clearOptions(EPSubjectData<?> subject, String typeWorld);
    
    public boolean clearOptions(EPSubjectData<?> subject);

    /*
     * Groups
     */
    
    public boolean addParent(EPSubjectData<?> subject, String typeWorld, SubjectReference parent);
    
    public boolean setGroup(EPSubjectData<?> subject, String typeWorld, SubjectReference parent, boolean insert);

    public boolean removeParent(EPSubjectData<?> subject, String typeWorld, SubjectReference parent);

    public boolean clearParents(EPSubjectData<?> subject, String typeWorld);
    
    public boolean clearParents(EPSubjectData<?> subject);
    
    /*
     * 
     */

	public boolean hasSubject(String identifier);

	public Set<String> getAllIdentifiers();

	public Map<SubjectReference, Boolean> getAllWithPermission(String typeWorld, String permission);
}
