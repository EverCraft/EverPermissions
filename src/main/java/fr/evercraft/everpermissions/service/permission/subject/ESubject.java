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
package fr.evercraft.everpermissions.service.permission.subject;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.context.Context;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class ESubject implements Subject {
	protected final EverPermissions plugin;
	
    private final ESubjectCollection collection;
    
    protected final String identifier;

    public ESubject(final EverPermissions plugin, final String identifier, final ESubjectCollection collection) {
    	this.plugin = plugin;
    	
        this.identifier = identifier;
        this.collection = collection;
    }
    
    /*
     * Accesseurs
     */
    
    @Override
    public String getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public ESubjectCollection getContainingCollection() {
        return this.collection;
    }
    
    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.empty();
    }
    
    @Override
	public Set<Context> getActiveContexts() {
        return new HashSet<Context>();
	}
    
    /*
     * Options
     */
    
    @Override
    public Optional<String> getOption(final Set<Context> contexts, final String option) {
		// TempoData : Permissions
		String value = this.getTransientSubjectData().getOptions(contexts).get(option);
		if(value != null) {
			return Optional.of(value);
		}
    	
		// TempoData : Groups
    	Iterator<Subject> subjects = this.getTransientSubjectData().getParents(contexts).iterator();
    	Optional<String> optValue;
    	while(subjects.hasNext()) {
    		optValue = ((ESubject) subjects.next()).getOption(contexts, option);
    		if(optValue.isPresent()) {
    			return optValue;
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.getSubjectData().getOptions(contexts).get(option);
    	if(value != null) {
			return Optional.of(value);
		}
    	
    	// SubjectData : Groups
    	subjects = this.getSubjectData().getParents(contexts).iterator();
    	while(subjects.hasNext()) {
    		optValue = ((ESubject) subjects.next()).getOption(contexts, option);
    		if(optValue.isPresent()) {
    			return optValue;
    		}
    	}
        return Optional.empty();
    }
    
    /*
     * Groupes
     */
    
    @Override
    public List<Subject> getParents(final Set<Context> contexts) {
    	Preconditions.checkNotNull(contexts, "contexts");
    	List<Subject> list = new ArrayList<Subject>();
    	list.addAll(this.getTransientSubjectData().getParents(contexts));
    	list.addAll(this.getSubjectData().getParents(contexts));
        return list;
    }
    
    @Override
    public boolean isChildOf(final Set<Context> contexts, final Subject parent) {
        Preconditions.checkNotNull(contexts, "contexts");
        Preconditions.checkNotNull(parent, "parent");
        
        int cpt = 0;
        boolean children = false;
        List<Subject> parents = getParents(contexts);
        while(cpt < parents.size() && !children){
        	children = parents.get(cpt).equals(parent) || parents.get(cpt).isChildOf(contexts, parent);
        	cpt++;
        }
        return children;
    }
    
    /*
     * Java
     */
    
    @Override
    public boolean equals(final Object other) {
    	if(this == other) {
    		return true;
    	}
        if (other == null || !(other instanceof ESubject)) {
            return false;
        }
        return this.getIdentifier().equals(((ESubject) other).getIdentifier());
    }
    
	@Override
	public String toString() {
		return "ESubject [identifier=" + identifier + ", data=" + this.getSubjectData() + ", transientData=" + this.getTransientSubjectData() + "]";
	}
	
	/*
     * Jamais utilisé
     */
	
    @Override
    public boolean hasPermission(final Set<Context> contexts, final String permission) {
        return getPermissionValue(contexts, permission).asBoolean();
    }
}
