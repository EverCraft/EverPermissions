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
package fr.evercraft.everpermissions.data;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.world.World;

import fr.evercraft.everapi.event.PermSystemEvent.Action;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.data.user.EConfUserData;
import fr.evercraft.everpermissions.service.permission.data.user.ESqlUserData;
import fr.evercraft.everpermissions.service.permission.data.user.IUserData;

public class EPManagerData {
	public final static String MKDIR_GROUPS = "groups";
	public final static String MKDIR_USERS = "users";
	
	private final EverPermissions plugin;
	
	private final ConcurrentMap<String, EPConfGroups> conf_groups;
	private final ConcurrentMap<String, EPConfUsers> conf_users;
	
	private final ConcurrentMap<String, String> world_groups;
	private final ConcurrentMap<String, String> world_users;
	
	private boolean sql;
	private final EPDataBases database;
	private IUserData user_data;
	
	private EPConfOthers conf_others;
	
	public EPManagerData(EverPermissions plugin) throws PluginDisableException {
		this.plugin = plugin;
		
		// Fichier de configs
		this.conf_groups = new ConcurrentHashMap<String, EPConfGroups>();
		this.conf_users = new ConcurrentHashMap<String, EPConfUsers>();
		
		// Le nom du monde : Le type
		this.world_groups = new ConcurrentHashMap<String, String>();
		this.world_users = new ConcurrentHashMap<String, String>();
		
		// Initialisation de la DB
		this.sql = false;
		this.database = new EPDataBases(this.plugin);
		
		// Configs Others
		this.conf_others = new EPConfOthers(this.plugin);
		
		start();
	}
	
	/**
	 * Activation : Choix du UserData
	 */
	public void start() {
		// DB : SQL
		if(this.database.isEnable()) {
			this.sql = true;
			this.user_data = new ESqlUserData(this.plugin);
		// DB : Config
		} else {
			this.user_data = new EConfUserData(this.plugin);
		}
	}
	
	/**
	 * @throws PluginDisableException 
	 */
	public void reload() throws PluginDisableException {
		// Désactivation des groupes et des joueurs
		for(World world : this.plugin.getGame().getServer().getWorlds()) {
	    	this.plugin.getService().getGroupSubjects().removeWorld(world.getName());
	    	this.removeUser(world.getName());
		}
		
		// Rechargement
		this.database.reload();
		this.conf_others.reload();
		start();
		
		// Réactivation des mondes
		for(World world : this.plugin.getGame().getServer().getWorlds()) {
			if(world.isLoaded()) {
	    		this.plugin.getService().getGroupSubjects().registerWorld(world.getName());
	    		this.registerUser(world.getName());
			}
		}
		
		// Event
		this.plugin.getManagerEvent().post(Action.RELOADED);
	}

	/*
	 * Accesseur
	 */
	
	public boolean isSQL() {
		return this.sql;
	}

	public IUserData getUserData() {
		return this.user_data;
	}
	

	public EPConfOthers getConfOther() {
		return this.conf_others;
	}
	
	public EPDataBases getDataBases() {
		return this.database;
	}

	/*
	 * Groups
	 */
	
	/**
	 * Ajoute un monde à la liste
	 * @param world Le monde
	 * @return Retourne le fichier de configuration si le type de groupe n'existait pas encore
	 */
	public Optional<EPConfGroups> registerGroup(final String world) {
		String group = this.plugin.getConfigs().getGroups(world);
		this.world_groups.putIfAbsent(world, group);
		// Si le type de groupe n'existait pas encore
		if(!this.conf_groups.containsKey(group)) {
			this.conf_groups.putIfAbsent(group, new EPConfGroups(this.plugin, MKDIR_GROUPS + "/" + group));
			return Optional.ofNullable(this.conf_groups.get(group));
		}
		return Optional.empty();
	}
	
	/**
	 * Supprime un monde de la liste
	 * @param world Le monde
	 * @return Retourne le fichier de configuration s'il y a plus de monde qui utilise ce type
	 */
	public Optional<EPConfGroups> removeGroup(final String world) {
		String group = this.world_groups.get(world);
		this.world_groups.remove(world);
		// S'il y a plus de monde qui utilise ce type 
		if(group != null && !this.world_groups.containsValue(group)) {
			return Optional.ofNullable(this.conf_groups.remove(group));
		}
		return Optional.empty();
	}
	
	/**
	 * Retourne le fichier de configuration d'un type de groupe
	 * @param type Le type de groupe
	 * @return Retourne le fichier de configuration
	 */
	public Optional<EPConfGroups> getConfGroup(final String type) {
		return Optional.ofNullable(this.conf_groups.get(type));
	}
	
	/**
	 * Retourne le type de groupe d'un monde
	 * @param world Le monde
	 * @return Le type de groupe
	 */
	public Optional<String> getTypeGroup(final String world) {
		return Optional.ofNullable(this.world_groups.get(world));
	}
	
	public ConcurrentMap<String, EPConfGroups> getConfGroups() {
		return this.conf_groups;
	}
	
	public ConcurrentMap<String, String> getTypeGroups() {
		return this.world_groups;
	}
	
	/*
	 * Users
	 */
	
	/**
	 * Ajoute un monde à la liste
	 * @param world Le monde
	 * @return Retourne le fichier de configuration si le type de joueur n'existait pas encore
	 */
	public Optional<EPConfUsers> registerUser(final String world) {
		String user = this.plugin.getConfigs().getUsers(world);
		this.world_users.putIfAbsent(world, user);
		if(!sql && !this.conf_users.containsKey(user)) {
			this.conf_users.putIfAbsent(user, new EPConfUsers(this.plugin, MKDIR_USERS + "/" + user));
			return Optional.ofNullable(this.conf_users.get(user));
		}
		return Optional.empty();
	}
	
	/**
	 * Supprime un monde de la liste
	 * @param world Le monde
	 * @return Retourne le fichier de configuration s'il y a plus de monde qui utilise ce type
	 */
	public Optional<EPConfUsers> removeUser(final String world) {
		String user = this.world_users.get(world);
		this.world_users.remove(world);
		if(user != null && !this.world_users.containsValue(user)) {
			return Optional.ofNullable(this.conf_users.remove(user));
		}
		return Optional.empty();
	}
	
	/**
	 * Retourne le fichier de configuration d'un type de joueur
	 * @param type Le type de joueur
	 * @return Retourne le fichier de configuration
	 */
	public Optional<EPConfUsers> getConfUser(final String type) {
		return Optional.ofNullable(this.conf_users.get(type));
	}
	
	/**
	 * Retourne le type de joueur d'un monde
	 * @param world Le monde
	 * @return Le type de joueur
	 */
	public Optional<String> getTypeUser(final String world) {
		return Optional.ofNullable(this.world_users.get(world));
	}
	
	public ConcurrentMap<String, EPConfUsers> getConfUsers() {
		return this.conf_users;
	}
	
	public ConcurrentMap<String, String> getWorldUsers() {
		return this.world_users;
	}
	
	/*
	 * Fonctions
	 */
	
	/**
	 * Sauvegarde un type de groupe
	 * @param type Le type de groupe
	 */
	public void saveGroup(final String type){
		final Optional<EPConfGroups> conf = this.getConfGroup(type);
		if(conf.isPresent()) {
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> {
				conf.get().save();
			}).name("Permissions : save group (type='" + type + "')").submit(this.plugin);
		}
	}
	
	/**
	 * Sauvegarde de tous les types de groupe
	 * @param type Le type de groupe
	 */
	public void saveGroups(){
		this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> {
			for(EPConfGroups conf : conf_groups.values()) {
				conf.save();
			}
		}).name("Permissions : save groups").submit(this.plugin);
	}
	
	/**
	 * Sauvegarde un type de joueur
	 * @param type Le type de joueur
	 */
	public void saveUser(final String type){
		final Optional<EPConfUsers> conf = this.getConfUser(type);
		if(conf.isPresent()) {
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> {
				conf.get().save();
			}).name("Permissions : save user (type='" + type + "')").submit(this.plugin);
		}
	}
	
	/**
	 * Sauvegarde de tous les types de joueur
	 * @param type Le type de joueur
	 */
	public void saveUsers(){
		this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> {
			for(EPConfGroups conf : conf_groups.values()) {
				conf.save();
			}
		}).name("Permissions : save users").submit(this.plugin);
	}
	
	/**
	 * Sauvegarde les 'others'
	 */
	public void saveOther(){
		this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> {
			conf_others.save();
		}).name("Permissions : save other").submit(this.plugin);
	}
}
