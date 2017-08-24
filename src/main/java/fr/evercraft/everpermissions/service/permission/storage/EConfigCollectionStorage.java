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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.data.ESubjectData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubject;

public class EConfigCollectionStorage implements ICollectionStorage {
	private final EverPermissions plugin;
	
	private final ConcurrentMap<String, EConfigSubjectStorage> storages;
	private final String collection;

    public EConfigCollectionStorage(final EverPermissions plugin, final String collection) {
        this.plugin = plugin;
        this.storages = new ConcurrentHashMap<String, EConfigSubjectStorage>();
        this.collection = collection;
    }
    
    @Override
    public boolean register(String typeWorld) {
    	if (this.storages.containsKey(typeWorld)) return false;
    	
    	this.storages.put(typeWorld, new EConfigSubjectStorage(this.plugin, this.collection, typeWorld));
    	return true;
    }
    
    @Override
    public boolean unregister(String typeWorld) {
    	return this.storages.remove(typeWorld) != null;
    }

	@Override
	public void reload() {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			storage.reload();
		}
	}

	@Override
	public boolean load(ESubject subject) {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			if (!storage.load(subject)) return false;
		}
		
		this.plugin.getELogger().debug("Chargement du subject (subject='" + subject.getIdentifier() + "';collection='" + subject.getCollectionIdentifier() + "')");
		return true;
	}
	
	@Override
	public boolean load(Collection<ESubject> subjects) {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			if (!storage.load(subjects)) {
				System.err.println("EConfigCollectionStorage erreur load : " + storage);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean setFriendlyIdentifier(ESubject subject, String name) {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			if (!storage.setFriendlyIdentifier(subject, name)) return false;
		}
		return true;
	}
	
	@Override
	public boolean setDefault(EGroupSubject subject, String typeWorld, boolean value) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.setDefault(subject, value);
	}

	@Override
	public boolean setPermission(ESubjectData subject, String typeWorld, String permission, Tristate value, boolean insert) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.setPermission(subject, permission, value, insert);
	}

	@Override
	public boolean clearPermissions(ESubjectData subject, String typeWorld) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.clearPermissions(subject);
	}

	@Override
	public boolean clearPermissions(ESubjectData subject) {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			if (!storage.clearPermissions(subject)) return false;
		}
		return true;
	}

	@Override
	public boolean setOption(ESubjectData subject, String typeWorld, String option, String value, boolean insert) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.setOption(subject, option, value, insert);
	}

	@Override
	public boolean clearOptions(ESubjectData subject, String typeWorld) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.clearOptions(subject);
	}

	@Override
	public boolean clearOptions(ESubjectData subject) {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			if (!storage.clearOptions(subject)) return false;
		}
		return true;
	}

	@Override
	public boolean addParent(ESubjectData subject, String typeWorld, SubjectReference parent) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.addParent(subject, parent);
	}

	@Override
	public boolean setGroup(ESubjectData subject, String typeWorld, SubjectReference parent, boolean insert) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.setGroup(subject, parent, insert);
	}

	@Override
	public boolean removeParent(ESubjectData subject, String typeWorld, SubjectReference parent) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.removeParent(subject, parent);
	}

	@Override
	public boolean clearParents(ESubjectData subject, String typeWorld) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return false;
		
		return storage.clearOptions(subject);
	}

	@Override
	public boolean clearParents(ESubjectData subject) {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			if (!storage.clearParents(subject)) return false;
		}
		return true;
	}

	@Override
	public boolean hasSubject(String identifier) {
		for (EConfigSubjectStorage storage : this.storages.values()) {
			if (storage.hasSubject(identifier)) return true;
		}
		return false;
	}

	@Override
	public Set<String> getAllIdentifiers() {
		ImmutableSet.Builder<String> identifiers = ImmutableSet.builder();
		for (EConfigSubjectStorage storage : this.storages.values()) {
			identifiers.addAll(storage.getAllIdentifiers());
		}
		return identifiers.build();
	}

	@Override
	public Map<SubjectReference, Boolean> getAllWithPermission(String typeWorld, String permission) {
		EConfigSubjectStorage storage = this.storages.get(typeWorld);
		if (storage == null) return ImmutableMap.of();
		
		return storage.getAllWithPermission(permission);
	}
}
