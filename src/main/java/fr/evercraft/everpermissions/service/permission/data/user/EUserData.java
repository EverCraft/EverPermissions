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
package fr.evercraft.everpermissions.service.permission.data.user;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import fr.evercraft.everapi.services.permission.event.PermUserEvent.Action;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.data.ENode;
import fr.evercraft.everpermissions.service.permission.data.EOptionSubjectData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EUserData extends EOptionSubjectData {	
    protected final ConcurrentMap<Set<Context>, String> groups = Maps.newConcurrentMap();
    protected final ConcurrentMap<Set<Context>, List<String>> subgroups = Maps.newConcurrentMap();

    public EUserData(final EverPermissions plugin, final EUserSubject subject) {
        super(plugin, subject);
        load();
    }

    @Override
	public void reload() {
		super.reload();
		
		this.clearParentsExecute();
		this.clearSubParentsExecute();
		
		load();
    }
    
    public void load() {
    	this.plugin.getManagerData().getUserData().load(this);
    	
    	// Chargement des groupes par défault
    	for(World world : this.plugin.getGame().getServer().getWorlds()) {
    		if(world.isLoaded()) {
    			Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world.getName());
    			Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world.getName());
    			if(type_group.isPresent() && type_user.isPresent()) {
    				Set<Context> contexts = EContextCalculator.getContextWorld(type_user.get());
    				if(!this.getParentContexts(contexts).isPresent()) {
	    				Optional<EGroupSubject> subject = this.plugin.getService().getGroupSubjects().getDefaultGroup(type_group.get());
	    				if(subject.isPresent()) {
	    					this.addParentExecute(contexts, subject.get());
	    					this.plugin.getLogger().debug("Loading : ("
	        						+ "identifier=" + this.getIdentifier() + ";"
	        						+ "default_group=" + subject.get().getIdentifier() + ";"
	        						+ "type=" + type_user.get() + ")");
	    				}
    				}
    			}
    		}
    	}
    }
    /*
     * Permissions
     */

    @Override
    public Map<String, Boolean> getPermissions(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return getPermissionsContexts(contexts);
    }
    
    public Map<String, Boolean> getPermissionsContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
    	ENode perms = this.permissions.get(contexts);
    	if(perms != null) {
    		return perms.asMap();
    	}
    	return Collections.emptyMap();
    }
    
    @Override
    public boolean setPermission(Set<Context> contexts, final String permission, final Tristate value) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return setPermissionContexts(contexts, permission, value);
    }
    
    public boolean setPermissionContexts(final Set<Context> contexts, final String permission, final Tristate value) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(permission, "permission");
    	checkNotNull(value, "value");
    	
    	boolean insert = this.getNodeTree(contexts).asMap().get(permission) == null;
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.setPermissionExecute(contexts, permission, value)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().setPermission(this.getIdentifier(), world.get(), permission, value, insert)) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSIONS_CHANGED);
	    			return true;
	    		}
    		}
    	}
        return false;
    }
    
    @Override
    public boolean clearPermissions(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return clearPermissionsContexts(contexts);
    }
    	
    public boolean clearPermissionsContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearPermissionsExecute(contexts)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().clearPermissions(this.getIdentifier(), world.get())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSIONS_CHANGED);
	    			return true;
	    		}
    		}
    	}
        return false;
    }
    
    @Override
    public boolean clearPermissions() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearPermissionsExecute()) {
    		// Si la sauvegarde est réussie
    		if(this.plugin.getManagerData().getUserData().clearPermissions(this.getIdentifier())) {
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSIONS_CHANGED);
    			return true;
    		}
    	}
        return false;
    }

    /*
     * Groups
     */
    
    @Override
    public Map<Set<Context>, List<Subject>> getAllParents() {
        ImmutableMap.Builder<Set<Context>, List<Subject>> ret = ImmutableMap.builder();
        for (Map.Entry<Set<Context>, String> ent : this.groups.entrySet()) {
            ret.put(ent.getKey(), toSubjectList(ent.getValue()));
        }
        return ret.build();
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return getParentsContexts(contexts);
    }
    
    public List<Subject> getParentsContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
        String ret = this.groups.get(contexts);
        if(ret != null) {
        	return toSubjectList(ret);
        }
        return Collections.emptyList();
    }
    
    public Optional<Subject> getParent(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return getParentContexts(contexts);
    }
    
    public Optional<Subject> getParentContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
        String ret = this.groups.get(contexts);
        if(ret != null) {
        	return Optional.ofNullable(this.plugin.getService().getGroupSubjects().get(ret));
        }
        return Optional.empty();
    }
    
    public boolean removeParent(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return removeParentContexts(contexts);
    }
    
    public boolean removeParentContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
        return this.groups.remove(contexts) != null;
    }
    
    @Override
    public boolean addParent(Set<Context> contexts, final Subject parent) {
    	this.plugin.getLogger().warn("addParent");
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return addParentContexts(contexts, parent);
    }
    
    public boolean addParentContexts(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.addParentExecute(contexts, parent)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().addParent(this.getIdentifier(), world.get(), parent.getIdentifier())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
	    			return true;
	    		}
    		}
    	}
        return false;
    }

    public boolean addParentExecute(Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
        contexts = ImmutableSet.copyOf(contexts);
        String oldParents = this.groups.get(contexts);
        if(oldParents == null) {
        	this.groups.put(contexts, parent.getIdentifier());
        	return true;
        } else if(!oldParents.equals(parent.getIdentifier()) && this.removeParentContexts(contexts, this.plugin.getService().getGroupSubjects().get(oldParents))) {
        	this.groups.put(contexts, parent.getIdentifier());
        	return true;
        }
        return false;
    }
    
    @Override
    public boolean removeParent(Set<Context> contexts, final Subject parent) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return removeParentContexts(contexts, parent);
    }
    
    public boolean removeParentContexts(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.removeParentExecute(contexts, parent)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().removeParent(this.getIdentifier(), world.get(), parent.getIdentifier())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
	    			return true;
	    		}
    		}
    	}
        return false;
    }

    public boolean removeParentExecute(Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
        contexts = ImmutableSet.copyOf(contexts);
        if(this.groups.containsKey(contexts) && this.groups.get(contexts).equals(parent.getIdentifier())) {
        	this.groups.remove(contexts);
        	return true;
        }
        return false;
    }
    
    @Override
    public boolean clearParents(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return clearParentsContexts(contexts);
    }
    
    public boolean clearParentsContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearParentsExecute(contexts)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().clearParents(this.getIdentifier(), world.get())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
	    			return true;
	    		}
    		}
    	}
        return false;
    }
    
    public boolean clearParentsExecute(Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
        return this.groups.remove(contexts) != null;
    }

    @Override
    public boolean clearParents() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearParentsExecute()) {
    		if(this.plugin.getManagerData().getUserData().clearParents(this.getIdentifier())) {
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_GROUP_CHANGED);
    			return true;
    		}
    	}
        return false;
    }
    
    public boolean clearParentsExecute() {
        if(!this.groups.isEmpty()) {
        	this.groups.clear();
        	return true;
        }
        return false;
    }
    
    /*
     * SubGroups
     */
    
    public Map<Set<Context>, List<Subject>> getAllSubParents() {
        ImmutableMap.Builder<Set<Context>, List<Subject>> ret = ImmutableMap.builder();
        for (Map.Entry<Set<Context>, List<String>> ent : this.subgroups.entrySet()) {
            ret.put(ent.getKey(), toSubjectList(ent.getValue()));
        }
        return ret.build();
    }
    
    public List<Subject> getSubParents(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return getSubParentsContexts(contexts);
    }

    public List<Subject> getSubParentsContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
        List<String> ret = this.subgroups.get(contexts);
        if(ret != null) {
        	return toSubjectList(ret);
        }
        return Collections.emptyList();
    }

    public boolean addSubParent(Set<Context> contexts, final Subject parent) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return addSubParentContexts(contexts, parent);
    }
    
    public boolean addSubParentContexts(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.addSubParentExecute(contexts, parent)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().addSubParent(this.getIdentifier(), world.get(), parent.getIdentifier())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
	    			return true;
	    		}
    		}
        }
        return false;
    }
    
    public boolean addSubParentExecute(Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
        contexts = ImmutableSet.copyOf(contexts);
        List<String> parents = this.subgroups.get(contexts);
        if(parents == null) {
        	this.subgroups.put(contexts, ImmutableList.<String>builder().add(parent.getIdentifier()).build());
        	return true;
        } else if(!parents.contains(parent.getIdentifier())) {
        	this.subgroups.replace(contexts, ImmutableList.<String>builder().addAll(parents).add(parent.getIdentifier()).build());
        	return true;
        }
        return false;
    }
    
    public boolean removeSubParent(Set<Context> contexts, final Subject parent) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return removeSubParentContexts(contexts, parent);
    }

    public boolean removeSubParentContexts(final Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.removeSubParentExecute(contexts, parent)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().removeSubParent(this.getIdentifier(), world.get(), parent.getIdentifier())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
	    			return true;
	    		}
    		}
        }
        return false;
    }
    
    public boolean removeSubParentExecute(Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
        contexts = ImmutableSet.copyOf(contexts);
        List<String> parents = this.subgroups.get(contexts);
        if(parents != null) {
	        parents = new ArrayList<String>(parents);
	        if(parents != null && parents.contains(parent.getIdentifier())) {
	        	parents.remove(parent.getIdentifier());
	        	this.subgroups.replace(contexts, ImmutableList.copyOf(parents));
	        	return true;
	        }
        }
        return false;
    }
    
    public boolean clearSubParents(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return clearSubParentsContexts(contexts);
    }

    public boolean clearSubParentsContexts(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearSubParentsExecute(contexts)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().clearSubParents(this.getIdentifier(), world.get())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
	    			return true;
	    		}
    		}
        }
        return false;
    }
    
    public boolean clearSubParentsExecute(Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
        return this.subgroups.remove(contexts) != null;
    }
    
    public boolean clearSubParents() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearSubParentsExecute()) {
    		// Si la sauvegarde est réussie
    		if(this.plugin.getManagerData().getUserData().clearSubParents(this.getIdentifier())) {
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
    			return true;
    		}
        }
        return false;
    }
    
    public boolean clearSubParentsExecute() {
        boolean wasEmpty = this.subgroups.isEmpty();
        this.subgroups.clear();
        return !wasEmpty;
    }
    
    /*
     * Fonction groups
     */
    
    private List<Subject> toSubjectList(final String parent) {
        ImmutableList.Builder<Subject> ret = ImmutableList.builder();
        ret.add(this.plugin.getService().getGroupSubjects().get(parent));
        return ret.build();
    }

    /*
     * Options
     */
    
    @Override
    public Map<String, String> getOptions(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return getOptionsContexts(contexts);
    }
    
    public Map<String, String> getOptionsContexts(final Set<Context> contexts) {
    	Map<String, String> ret = this.options.get(contexts);
    	if(ret != null) {
    		return ImmutableMap.copyOf(ret);
    	}
        return ImmutableMap.of();
    }
    
    @Override
    public boolean setOption(Set<Context> contexts, final String type, final String name) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return setOptionContexts(contexts, type, name);
    }
    
    public boolean setOptionContexts(final Set<Context> contexts, final String type, final String name) {
    	boolean insert = (this.getOptionsContexts(contexts).get(type) == null);
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.setOptionExecute(contexts, type, name)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().setOption(this.getIdentifier(), world.get(), type, name, insert)) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
	    			return true;
	    		}
    		}
    	}
        return false;
    }
    
    @Override
    public boolean clearOptions(Set<Context> contexts) {
    	contexts = this.plugin.getService().getContextCalculator().getContextUser(contexts);
    	return clearOptionsContexts(contexts);
    }
    
    public boolean clearOptionsContexts(final Set<Context> contexts) {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearOptionsExecute(contexts)) {
    		Optional<String> world = EContextCalculator.getWorld(contexts);
    		// Si il y a un monde
    		if(world.isPresent()) {
    			// Si la sauvegarde est réussie
	    		if(this.plugin.getManagerData().getUserData().clearOptions(this.getIdentifier(), world.get())) {
	    			this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
	    			return true;
	    		}
    		}
    	}
        return false;
    }

    @Override
    public boolean clearOptions() {
    	// S'il n'y a pas d'erreur : on sauvegarde
    	if(this.clearOptionsExecute()) {
    		// Si la sauvegarde est réussie
    		if(this.plugin.getManagerData().getUserData().clearOptions(this.getIdentifier())) {
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
    			return true;
    		}
        }
        return false;
    }
}
