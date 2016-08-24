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
package fr.evercraft.everpermissions.service.permission.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import fr.evercraft.everpermissions.EverPermissions;

public abstract class EOptionSubjectData implements SubjectData {
	protected final EverPermissions plugin;
	protected final Subject subject;
	
	protected final ConcurrentMap<Set<Context>, Map<String, String>> options = Maps.newConcurrentMap();
    protected final ConcurrentMap<Set<Context>, ENode> permissions = Maps.newConcurrentMap();
    protected final ConcurrentMap<Set<Context>, List<String>> groups = Maps.newConcurrentMap();

    public EOptionSubjectData(final EverPermissions plugin, final Subject subject) {
        checkNotNull(plugin, "plugin");
        checkNotNull(subject, "subject");
        
        this.plugin = plugin;
        this.subject = subject;
    }
    
    public String getIdentifier() {
		return this.subject.getIdentifier();
	}

	public void reload() {
		this.clearPermissionsExecute();
		this.clearOptionsExecute();
		this.clearParentsExecute();
    }
    
    /*
     * Permissions
     */
	
	public abstract boolean setPermission(Set<Context> contexts, String permission, Tristate value);
    public abstract boolean clearPermissions(Set<Context> contexts);
    public abstract boolean clearPermissions();

    public ENode getNodeTree(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
    	ENode perms = this.permissions.get(contexts);
    	if (perms != null) {
    		return perms;
    	}
    	return new ENode();
    }

    @Override
    public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
        ImmutableMap.Builder<Set<Context>, Map<String, Boolean>> ret = ImmutableMap.builder();
        for (Map.Entry<Set<Context>, ENode> ent : this.permissions.entrySet()) {
            ret.put(ent.getKey(), ent.getValue().asMap());
        }
        return ret.build();
    }
    
    @Override
    public Map<String, Boolean> getPermissions(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
    	ENode perms = this.permissions.get(contexts);
    	if (perms != null) {
    		return perms.asMap();
    	}
    	return Collections.emptyMap();
    }

    public boolean setPermissionExecute(Set<Context> contexts, final String permission, final Tristate value) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(permission, "permission");
    	checkNotNull(value, "value");

        boolean resultat = false;
        contexts = ImmutableSet.copyOf(contexts);
        ENode oldTree = this.permissions.get(contexts);
        
        // Aucune permissions 
        if (oldTree == null) {
        	// Si on ne supprime pas la permission
        	if (value != Tristate.UNDEFINED) {
        		this.permissions.putIfAbsent(contexts, ENode.of(ImmutableMap.of(permission, value.asBoolean())));
        		resultat = true;
        	}
        // Supprime une permission
        } else if (value == Tristate.UNDEFINED) {
        	// Si il a la permission
        	if (oldTree.getTristate(permission) != Tristate.UNDEFINED) {
        		this.permissions.replace(contexts, oldTree, oldTree.withValue(permission, value));
        		resultat = true;
        	}
        // Ajoute une permission
        } else {
        	// Si la permission est différente
        	if (!oldTree.asMap().containsKey(permission) || oldTree.asMap().get(permission) != value.asBoolean()) {
        		this.permissions.replace(contexts, oldTree, oldTree.withValue(permission, value));
        		resultat = true;
        	}
        } 
        return resultat;
    }
    
    public boolean clearPermissionsExecute(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
        return this.permissions.remove(contexts) != null;
    }

    public boolean clearPermissionsExecute() {
        boolean wasEmpty = this.permissions.isEmpty();
        this.permissions.clear();
        return !wasEmpty;
    }

    /*
     * Groups
     */
    
    public abstract boolean addParent(Set<Context> contexts, Subject parent);
    public abstract boolean removeParent(Set<Context> contexts, Subject parent);
    public abstract boolean clearParents(Set<Context> contexts);
    public abstract boolean clearParents();
    
    public Map<Set<Context>, List<Subject>> getAllParents() {
        ImmutableMap.Builder<Set<Context>, List<Subject>> ret = ImmutableMap.builder();
        for (Map.Entry<Set<Context>, List<String>> ent : this.groups.entrySet()) {
            ret.put(ent.getKey(), toSubjectList(ent.getValue()));
        }
        return ret.build();
    }

    public List<Subject> getParents(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
    	
        List<String> ret = this.groups.get(contexts);
        if (ret != null) {
        	return toSubjectList(ret);
        }
        return Collections.emptyList();
    }
    
    public boolean addParentExecute(Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
        contexts = ImmutableSet.copyOf(contexts);
        
        List<String> parents = this.groups.get(contexts);
        if (parents == null) {
        	this.groups.put(contexts, ImmutableList.<String>builder().add(parent.getIdentifier()).build());
        	return true;
        } else if (!parents.contains(parent.getIdentifier())) {
        	this.groups.replace(contexts, ImmutableList.<String>builder().addAll(parents).add(parent.getIdentifier()).build());
        	return true;
        }
        return false;
    }
    
    public boolean removeParentExecute(Set<Context> contexts, final Subject parent) {
    	checkNotNull(contexts, "contexts");
    	checkNotNull(parent, "parent");
    	
        contexts = ImmutableSet.copyOf(contexts);
        
        List<String> parents = new ArrayList<String>(this.groups.get(contexts));
        if (parents != null && parents.contains(parent.getIdentifier())) {
        	parents.remove(parent.getIdentifier());
        	this.groups.replace(contexts, ImmutableList.copyOf(parents));
        	return true;
        }
        return false;
    }    
    
    public boolean clearParentsExecute(final Set<Context> contexts) {
    	checkNotNull(contexts, "contexts");
        return this.groups.remove(contexts) != null;
    }
    
    public boolean clearParentsExecute() {
        boolean wasEmpty = this.groups.isEmpty();
        this.groups.clear();
        return !wasEmpty;
    }
    
    protected List<Subject> toSubjectList(final List<String> parents) {
        ImmutableList.Builder<Subject> ret = ImmutableList.builder();
        for (String parent : parents) {
            ret.add(this.plugin.getService().getGroupSubjects().get(parent));
        }
        return ret.build();
    }

    /*
     * Options
     */
    
    public abstract boolean setOption(Set<Context> contexts, String type, String name);
    public abstract boolean clearOptions(Set<Context> contexts);
    public abstract boolean clearOptions();
    
    @Override
    public Map<Set<Context>, Map<String, String>> getAllOptions() {
        return ImmutableMap.copyOf(this.options);
    }

    @Override
    public Map<String, String> getOptions(final Set<Context> contexts) {
    	Map<String, String> ret = this.options.get(contexts);
    	if (ret != null) {
    		return ImmutableMap.copyOf(ret);
    	}
        return ImmutableMap.of();
    }

    public boolean setOptionExecute(final Set<Context> contexts, final String key, final String value) {
        Map<String, String> origMap = this.options.get(contexts);

        boolean resultat = false;
        // Aucune option pour ce context
        if (origMap == null) {
        	// Si on ne supprime pas l'option
        	if (value != null) {
        		this.options.putIfAbsent(ImmutableSet.copyOf(contexts), ImmutableMap.of(key.toLowerCase(), value));
        		resultat = true;
        	}
        // Il y a des options
        } else {
        	// Si on supprime l'option
        	Map<String, String> newMap = new HashMap<String, String>();
        	if (value == null) {
        		newMap.putAll(origMap);
        		newMap.remove(key);
        	} else {
        		newMap.putAll(origMap);
        		newMap.put(key, value);
        	}
        	newMap = Collections.unmodifiableMap(newMap);
        	resultat = this.options.replace(contexts, origMap, newMap);
        }
        return resultat;
    }
    
    public boolean clearOptionsExecute(final Set<Context> contexts) {
        return this.options.remove(contexts) != null;
    }
    
    public boolean clearOptionsExecute() {
        this.options.clear();
        return true;
    }
}
