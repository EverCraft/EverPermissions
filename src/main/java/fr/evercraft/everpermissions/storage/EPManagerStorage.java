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
package fr.evercraft.everpermissions.storage;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.storage.EConfigCollectionStorage;
import fr.evercraft.everpermissions.service.permission.storage.ESqlCollectionStorage;
import fr.evercraft.everpermissions.service.permission.storage.ICollectionStorage;

public class EPManagerStorage {
	private final EverPermissions plugin;
	
	private final ConcurrentMap<String, ICollectionStorage> configs;
	private final ConcurrentMap<String, ConcurrentMap<String, String>> worlds;
	
	public EPManagerStorage(EverPermissions plugin) throws PluginDisableException {
		this.plugin = plugin;
		
		// Fichier de configs
		this.configs = new ConcurrentHashMap<String, ICollectionStorage>();
		
		// Le nom du monde : Le type
		this.worlds = new ConcurrentHashMap<String, ConcurrentMap<String, String>>();
	}
	
	public void reload() {
		for (ICollectionStorage storage : this.configs.values()) {
			storage.reload();
		}
	}
	
	/*
	 * Groups
	 */
	public ICollectionStorage register(final String collection, final String world) {		
		ICollectionStorage config = this.configs.get(collection);
		if (config == null) {
			if (this.plugin.getDataBases().isEnable()) {
				config = new ESqlCollectionStorage(this.plugin, collection);
			} else {
				config = new EConfigCollectionStorage(this.plugin, collection);
			}
			this.configs.put(collection, config);
		}
		
		String typeWorld = this.plugin.getConfigs().register(collection, world);
		ConcurrentMap<String, String> worlds = this.worlds.get(collection);
		if (worlds == null) {
			worlds = new ConcurrentHashMap<String, String>();
			this.worlds.put(collection, worlds);
		}
		worlds.put(world, typeWorld);
		return config;
	}
	
	public void unregister(final String collection, final String world) {
		ConcurrentMap<String, String> worlds = this.worlds.get(collection);
		if (worlds == null) return;
		worlds.remove(world);
		
		if (!worlds.isEmpty()) return;
		this.worlds.remove(collection);
		this.configs.remove(collection);
	}

	public ICollectionStorage get(final String collection) {
		return this.configs.get(collection);
	}
	
	public Optional<String> getTypeWorld(final String collection, final String world) {
		return Optional.ofNullable(this.worlds.get(collection).get(world));
	}
}
