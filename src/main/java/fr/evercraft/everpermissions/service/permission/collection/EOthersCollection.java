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
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EOthersCollection extends ESubjectCollection<EOtherSubject> {
	
    public EOthersCollection(final EverPermissions plugin, final String identifier) {
    	super(plugin, identifier);
    }

	@Override
	public CompletableFuture<Boolean> hasSubject(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<String, Subject>> loadSubjects(Set<String> identifiers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Set<String>> getAllIdentifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(String permission) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts,
			String permission) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EOtherSubject add(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
