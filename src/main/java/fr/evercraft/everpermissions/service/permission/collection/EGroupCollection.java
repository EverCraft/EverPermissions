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

import fr.evercraft.everapi.event.PermSystemEvent;
import fr.evercraft.everapi.event.PermGroupEvent.Action;
import fr.evercraft.everapi.util.Chronometer;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.storage.EPConfGroups;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import com.google.common.reflect.TypeToken;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EGroupCollection extends ESubjectCollection {
	private final ConcurrentMap<String, EGroupSubject> groups_default;
    private final ConcurrentMap<String, EGroupSubject> subject;

    public EGroupCollection(final EverPermissions plugin) {
    	super(plugin, PermissionService.SUBJECTS_GROUP);
    	this.groups_default = new ConcurrentHashMap<String, EGroupSubject>();
    	this.subject = new ConcurrentHashMap<String, EGroupSubject>();
    }
    
    @Override
    public EGroupSubject get(final String identifier) {
    	return this.subject.get(identifier.toLowerCase());
    }
    
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Iterable<Subject> getAllSubjects() {
        return (Iterable) this.subject.values();
    }
    
    @Override
    public boolean hasRegistered(final String identifier) {
    	return subject.containsKey(identifier.toLowerCase());
    }
    
    public void reload() {}
    
    /*
     * Groupe
     */
    
    /**
     * Ajouter un groupe à un type de groupe
     * @param identifier Le nom du groupe
     * @param type Le type de groupe
     * @return False si le type de groupe n'existe pas
     */
    public boolean register(final String identifier, final String type) {
		Optional<EPConfGroups> groups =  this.plugin.getManagerData().getConfGroup(type);
		if (groups.isPresent()) {
	    	groups.get().addDefault(identifier + ".default", false);
	    	groups.get().addDefault(identifier + ".inheritances", Arrays.asList());
	    	
	    	// Création du groupe si il n'existe pas
	    	EGroupSubject group =  this.get(identifier);
	    	if (group == null) {
	    		group = new EGroupSubject(this.plugin, identifier, this);
	    		this.subject.putIfAbsent(identifier.toLowerCase(), group);
	    	}
	    	group.registerWorld(type);
	    	
	    	this.plugin.getManagerData().saveGroup(type);
	    	this.plugin.getManagerEvent().post(group, Action.GROUP_ADDED);
	    	return true;
		}
    	return false;
    }
    
    /**
     * Supprime un groupe à un type de groupe
     * @param identifier Le nom du groupe
     * @param type Le type de groupe
     * @return False si le type de groupe n'existe pas
     */
    public boolean remove(final String identifier, final String type) {
    	Optional<EPConfGroups> groups =  this.plugin.getManagerData().getConfGroup(type);
    	if (groups.isPresent()) {
	    	groups.get().getNode().removeChild(identifier);
	    	EGroupSubject group = this.get(identifier);
	    	if (group != null) {
	    		group.clear(type);
	    		this.groups_default.remove(type, group);
	    		
	    		this.plugin.getManagerData().saveGroup(type);
	    		this.plugin.getManagerEvent().post(group, Action.GROUP_REMOVED);
	    		return true;
	    	}
    	}
    	return false;
    }
    
    /**
     * Ajoute un monde
     * @param world_name Le nom du monde
     */
	public void registerWorld(final String world_name) {
		Chronometer chronometer = new Chronometer();
		Optional<EPConfGroups> conf = this.plugin.getManagerData().registerGroup(world_name);
		// Si c'est un nouveau type de groupe
		if (conf.isPresent()) {
			Optional<String> type = this.plugin.getManagerData().getTypeGroup(world_name);
			if (type.isPresent()) {
				Set<Context> contexts = EContextCalculator.getContextWorld(type.get());
				// Chargement des permissions et des options
				for (Entry<Object, ? extends ConfigurationNode> group : conf.get().getNode().getChildrenMap().entrySet()) {
					if (group.getKey() instanceof String) {
						String group_name = (String) group.getKey();
						// Initialisation du groupe
			    		EGroupSubject subject;
			    		if (!this.subject.containsKey(group_name.toLowerCase())) {
			    			subject = new EGroupSubject(this.plugin, group_name, this);
			    			this.subject.put(group_name.toLowerCase(), subject);
			    		} else {
			    			subject = get(group_name);
			    		}
			    		subject.registerWorld(type.get());
			    		// Chargement des permissions
			    		for (Entry<Object, ? extends ConfigurationNode> permission : group.getValue().getNode("permissions").getChildrenMap().entrySet()) {
			    			if (group.getKey() instanceof String && permission.getValue().getValue() instanceof Boolean) {
			    				subject.getSubjectData().setPermissionExecute(contexts, (String) permission.getKey(), Tristate.fromBoolean(permission.getValue().getBoolean(false)));
			    			} else {
			    				this.plugin.getLogger().warn("Error : Loading group ("
			    						+ "type='" + type.get() + "';"
			    						+ "permission='" + permission.getValue().toString() + "')");
			    			}
			    		}
			    		// Chargement des options
			    		for (Entry<Object, ? extends ConfigurationNode> options : group.getValue().getNode("options").getChildrenMap().entrySet()) {
			    			if (group.getKey() instanceof String && options.getValue().getValue() instanceof String) {
			    				subject.getSubjectData().setOptionExecute(contexts, (String) options.getKey(), options.getValue().getString(""));
			    			} else {
			    				this.plugin.getLogger().warn("Error : Loading group ("
			    						+ "type='" + type.get() + "';"
			    						+ "option='" + options.getValue().toString() + "')");
			    			}
			    		}
					}
				}
				// Chargement des inheritances
				for (Entry<Object, ? extends ConfigurationNode> group : conf.get().getNode().getChildrenMap().entrySet()) {
					if (group.getKey() instanceof String) {
						String group_name = (String) group.getKey();
						EGroupSubject subject = get(group_name);
						try {
							for (String inheritance : group.getValue().getNode("inheritances").getList(TypeToken.of(String.class))) {
								EGroupSubject parent = get(inheritance);
								if (parent != null && !parent.equals(subject)) {
									subject.getSubjectData().addParentExecute(contexts, parent);
								} else {
									this.plugin.getLogger().warn("Error : Loading group ("
											+ "type='" + type.get() + "';"
											+ "group='" + subject.getIdentifier() + "';"
											+ "inheritance='" + inheritance +"')");
								}
							}
						} catch (ObjectMappingException e) {}
					}
				}
				// Event GROUP_ADDED
				// Ajout du groupe par défaut
				for (Entry<Object, ? extends ConfigurationNode> group : conf.get().getNode().getChildrenMap().entrySet()) {
					if (group.getKey() instanceof String) {
						String group_name = (String) group.getKey();
						EGroupSubject subject = get(group_name);
						if (subject != null) {
							this.plugin.getManagerEvent().post(subject, Action.GROUP_ADDED);
							// Si c'est un groupe par défaut
							if (group.getValue().getNode("default").getBoolean(false)) {
				    			this.groups_default.put(type.get(), subject);
				    			
				    			this.plugin.getManagerEvent().post(PermSystemEvent.Action.DEFAULT_GROUP_CHANGED);
				    			this.plugin.getLogger().debug("Group default : (world=" + type.get() + ";subject=" + subject.getIdentifier() + ")");
				    		}
						}
					}
				}
			}
		}
		this.plugin.getLogger().debug("Loading world '" + world_name + "' in " +  chronometer.getMilliseconds().toString() + " ms");
	}
	
	/**
	 * Supprime un monde
	 * @param world_name Le nom du monde
	 */
	public void removeWorld(final String world_name) {
    	Optional<String> world = this.plugin.getManagerData().getTypeGroup(world_name);
		if (world.isPresent()) {
			Optional<EPConfGroups> conf = this.plugin.getManagerData().removeGroup(world_name);
			if (conf.isPresent()) {
				for (Entry<Object, ? extends ConfigurationNode> group : conf.get().getNode().getChildrenMap().entrySet()) {
					if (group.getKey() instanceof String) {
						EGroupSubject subject = this.get((String) group.getKey());
						if (subject != null) {
							subject.clear(world.get());
							this.groups_default.remove(world.get(), subject);
							
							if (subject.getWorlds().isEmpty()) {
								this.subject.remove(subject);
							}

							this.plugin.getManagerEvent().post(subject, Action.GROUP_REMOVED);
							this.plugin.getLogger().debug("UnLoad world (Subject='" + subject.getIdentifier() + "';World='" + world.get() + "')");
						}
					}
				}
			}
		}
    }
	
	/**
	 * Retourne la liste des groupes d'un type de groupe
	 * @param type Le type de groupe
	 * @return La liste des groupes
	 */
	public Set<EGroupSubject> getGroups(final String type) {
		Set<EGroupSubject> groups = new HashSet<EGroupSubject>();
		for (EGroupSubject group : this.subject.values()) {
			if (group.hasWorld(type)) {
				groups.add(group);
			}
		}
		return groups;
	}
	
	/*
     * GroupDefault
     */
    
    /**
     * La liste des groupes par défaut selon les types de groupes
     * @return La liste des groupes par défaut
     */
    public ConcurrentMap<String, EGroupSubject> getDefaultGroups() {
		return groups_default;
	}

    /**
     * Le groupe par défaut d'un type de groupe
     * @param world Le monde
     * @return Le groupe par défaut
     */
	public Optional<EGroupSubject> getDefaultGroup(final String type) {
		return Optional.ofNullable(this.groups_default.get(type));
	}
	
	/**
	 * Ajoute un groupe par défaut et l'ajoute dans le fichier de config
	 * @param group Le groupe
	 * @param type Le type de groupe
	 * @return False si il n'y a pas déjà un groupe par défaut
	 */
	public boolean registerDefault(final EGroupSubject group, final String type) {
		// Il n'y a pas de groupe par défaut
		if (!this.groups_default.containsKey(type)) {
			Optional<EPConfGroups> groups =  this.plugin.getManagerData().getConfGroup(type);
			// Si le fichier de configuration existe
			if (groups.isPresent()) {
				groups.get().get(group.getIdentifier() + ".default").setValue(true);
				this.groups_default.putIfAbsent(type, group);
				
				this.plugin.getManagerData().saveGroup(type);
				this.plugin.getManagerEvent().post(PermSystemEvent.Action.DEFAULT_GROUP_CHANGED);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Supprime un groupe par défaut et le supprime du fichier de config
	 * @param group Le groupe
	 * @param type Le type de groupe
	 * @return False si il y a n'était pas celui par défault
	 */
	public boolean removeDefault(final EGroupSubject group, final String type) {
		// Il n'y a pas de groupe par défaut
		if (this.groups_default.containsKey(type) && this.groups_default.get(type).equals(group)) {
			Optional<EPConfGroups> groups =  this.plugin.getManagerData().getConfGroup(type);
			// Si le fichier de configuration existe
			if (groups.isPresent()) {
				groups.get().get(group.getIdentifier() + ".default").setValue(false);
				this.groups_default.remove(type, group);
				
				this.plugin.getManagerData().saveGroup(type);
				this.plugin.getManagerEvent().post(PermSystemEvent.Action.DEFAULT_GROUP_CHANGED);
				return true;
			}
		}
		return false;
	}
}
