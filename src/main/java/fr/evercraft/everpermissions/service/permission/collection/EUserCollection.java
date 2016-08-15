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

import fr.evercraft.everapi.event.PermUserEvent.Action;
import fr.evercraft.everapi.java.Chronometer;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EUserCollection extends ESubjectCollection {
	private final ConcurrentMap<String, EUserSubject> subjects;
	private final LoadingCache<String, EUserSubject> cache;

	public EUserCollection(final EverPermissions plugin) {
		super(plugin, PermissionService.SUBJECTS_USER);
		
		this.subjects = new ConcurrentHashMap<String, EUserSubject>();
		this.cache = CacheBuilder.newBuilder()
					    .maximumSize(100)
					    .expireAfterAccess(1, TimeUnit.DAYS)
					    .removalListener(new RemovalListener<String, EUserSubject>() {
					    	/**
					    	 * Supprime un joueur du cache
					    	 */
							@Override
							public void onRemoval(RemovalNotification<String, EUserSubject> notification) {
								EUserCollection.this.plugin.getLogger().debug("Unloading the player cache : " + notification.getValue().getIdentifier());
								EUserCollection.this.plugin.getManagerEvent().post(notification.getValue(), Action.USER_REMOVED);
							}
					    	
					    })
					    .build(new CacheLoader<String, EUserSubject>() {
					    	/**
					    	 * Ajoute un joueur au cache
					    	 */
					        @Override
					        public EUserSubject load(String identifier){
					        	Chronometer chronometer = new Chronometer();
					        	
					            EUserSubject subject = new EUserSubject(EUserCollection.this.plugin, identifier, EUserCollection.this);
					            EUserCollection.this.plugin.getLogger().debug("Loading user '" + identifier + "' in " +  chronometer.getMilliseconds().toString() + " ms");
					            
					            EUserCollection.this.plugin.getManagerEvent().post(subject, Action.USER_ADDED);
					            return subject;
					        }
					    });
	}

	@Override
	public EUserSubject get(String identifier) {
		try {
			if(!this.subjects.containsKey(identifier)) {
				return this.cache.get(identifier);
	    	}
	    	return this.subjects.get(identifier);
		} catch (ExecutionException e) {
			this.plugin.getLogger().warn("Error : Loading user (identifier='" + identifier + "';message='" + e.getMessage() + "')");
			return null;
		}
	}
	
	@Override
	public boolean hasRegistered(String identifier) {
		try {
			return this.plugin.getGame().getServer().getPlayer(UUID.fromString(identifier)).isPresent();
		} catch (IllegalArgumentException e) {}
		return false;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterable<Subject> getAllSubjects() {
		return (Iterable) this.plugin.getGame().getServer().getOnlinePlayers();
	}
	
	/**
	 * Rechargement : Vide le cache et recharge tous les joueurs
	 */
	public void reload() {
		this.cache.cleanUp();
		for(EUserSubject subject : this.subjects.values()) {
			subject.reload();
		}
	}
	
	/**
	 * Ajoute un joueur à la liste
	 * @param identifier L'UUID du joueur
	 */
	public void registerPlayer(String identifier) {
		EUserSubject player = this.cache.getIfPresent(identifier);
		// Si le joueur est dans le cache
		if(player != null) {
			this.subjects.putIfAbsent(identifier, player);
			this.plugin.getLogger().debug("Loading player cache : " + identifier);
		// Si le joueur n'est pas dans le cache
		} else {
			Chronometer chronometer = new Chronometer();
			player = new EUserSubject(this.plugin, identifier, this);
			this.subjects.putIfAbsent(identifier, player);
			this.plugin.getLogger().debug("Loading player '" + identifier + "' in " +  chronometer.getMilliseconds().toString() + " ms");
		}
		this.plugin.getManagerEvent().post(player, Action.USER_ADDED);
	}
	
	/**
	 * Supprime un joueur à la liste et l'ajoute au cache
	 * @param identifier L'UUID du joueur
	 */
	public void removePlayer(String identifier) {
		EUserSubject player = this.subjects.remove(identifier);
		// Si le joueur existe
		if(player != null) {
			this.cache.put(identifier, player);
			this.plugin.getManagerEvent().post(player, Action.USER_REMOVED);
			this.plugin.getLogger().debug("Unloading the player : " + identifier);
		}
	}
}
