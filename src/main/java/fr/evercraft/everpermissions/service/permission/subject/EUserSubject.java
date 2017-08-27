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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.data.EUserData;

public class EUserSubject extends ESubject {
	private final EUserData data;
	private final EUserData transientData;
	
    public EUserSubject(final EverPermissions plugin, final String identifier, final ESubjectCollection<?> collection) {
    	super(plugin, identifier, collection);

    	this.data = new EUserData(this.plugin, this, false);
        this.transientData = new EUserData(this.plugin, this, true);
    }
    
    public void reload() {
		this.data.reload();
		//this.transientData.reload();
    }
    
    /*
     * Accesseurs
     */
    
    public Optional<SubjectReference> getGroup() {
        return this.getGroup(this.getActiveContexts());
    }
    
    public Optional<SubjectReference> getGroup(final Set<Context> contexts) {
        return this.data.getGroup(contexts);
    }
    
    @Override
    public EUserData getSubjectData() {
        return this.data;
    }

    @Override
    public EUserData getTransientSubjectData() {
        return this.transientData;
    }
    
    @Override
	public boolean isSubjectDataPersisted() {
		return true;
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Optional<CommandSource> getCommandSource() {
        if (this.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_USER)) {
        	try {
        		return (Optional) this.plugin.getGame().getServer().getPlayer(UUID.fromString(this.getIdentifier()));
        	} catch (Exception e) {}
        } else if (this.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_SYSTEM)) {
    		if (this.getIdentifier().equals("Server")) {
                return Optional.of(this.plugin.getGame().getServer().getConsole());
            } else if (this.getIdentifier().equals("RCON")) {
                // TODO: Implement RCON API?
            }
    	} else if (this.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_COMMAND_BLOCK)) {
    		// TODO: Implement CommandBlock API?
    	}
        return Optional.empty();
    } 
    
    @Override
	public Set<Context> getActiveContexts() {
    	Set<Context> contexts = new HashSet<>();
    	this.plugin.getService().getContextCalculator().accumulateContexts(this, contexts);
        return Collections.unmodifiableSet(contexts);
	}
    
    /*
     * Permission
     */
	
    public Tristate getPermissionValue(final Set<Context> contexts, final String permission) {
    	// TODO Cache
    	
		String typeWorldUser = this.plugin.getService().getContextCalculator().getUser(contexts);
		// TempoData : Permissions
		Tristate value = this.transientData.getNodeTree(typeWorldUser).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			this.plugin.getELogger().debug("TransientSubjectData 'Permissions' : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='" + value.name() + "')");
			return value;
		}
		
    	// TempoData : SubGroup
    	for (SubjectReference subGroup : this.transientData.getSubGroup(typeWorldUser)) {
    		value = subGroup.resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getELogger().debug("SubjectData 'SubGroup' : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	
    	// TempoData : Groups
    	Optional<SubjectReference> subject = this.transientData.getGroup(typeWorldUser);
    	if(subject.isPresent()) {
    		value = subject.get().resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getELogger().debug("SubjectData 'Groups' : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.data.getNodeTree(typeWorldUser).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			this.plugin.getELogger().debug("SubjectData 'Permissions' : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='" + value.name() + "')");
			return value;
		}
    	
    	// SubjectData : SubGroup
		for (SubjectReference subGroup : this.data.getSubGroup(typeWorldUser)) {
    		value = subGroup.resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getELogger().debug("SubjectData 'SubGroup' : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	
    	// SubjectData : Groups
    	subject = this.data.getGroup(typeWorldUser);
    	if(subject.isPresent()) {
    		value = subject.get().resolve().join().getPermissionValue(contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getELogger().debug("SubjectData 'Groups' : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	// Subject Default
    	} else {
    		String typeWorldGroup = this.plugin.getService().getContextCalculator().getGroup(contexts);
    		Optional<EGroupSubject> defaultGroup = this.plugin.getService().getGroupSubjects().getDefaultGroup(typeWorldGroup);
    		if(defaultGroup.isPresent()) {
        		value = defaultGroup.get().getPermissionValue(contexts, permission);
        		if (!value.equals(Tristate.UNDEFINED)) {
        			this.plugin.getELogger().debug("SubjectData 'Default' : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='" + value.name() + "')");
        			return value;
        		}
    		}
    	}
    	
    	this.plugin.getELogger().debug("Undefined : (identifier='" + this.identifier + "';collection='" + this.collection + "';permission='" + permission + "';value='UNDEFINED')");
        return Tristate.UNDEFINED;
    }
    
    /*
     * Options
     */
    
    @Override
    public Optional<String> getOption(final Set<Context> contexts, final String option) {
    	String typeWorldUser = this.plugin.getService().getContextCalculator().getUser(contexts);
		// TempoData : Permissions
    	String value = this.transientData.getOptions(typeWorldUser).get(option);
		if (value != null) {
			this.plugin.getELogger().debug("TransientSubjectData 'Options' : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='" + value + "')");
			return Optional.of(value);
		}
    	
		// TempoData : SubGroup
    	for (SubjectReference subGroup : this.transientData.getSubGroup(typeWorldUser)) {
    		value = subGroup.resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			this.plugin.getELogger().debug("TransientSubjectData 'Permissions' : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='" + value + "')");
    			return Optional.of(value);
    		}
    	}
    	
    	// TempoData : Groups
    	Optional<SubjectReference> subject = this.transientData.getGroup(typeWorldUser);
    	if(subject.isPresent()) {
    		value = subject.get().resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			this.plugin.getELogger().debug("TransientSubjectData 'Parents' : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='" + value + "')");
    			return Optional.of(value);
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.data.getOptions(typeWorldUser).get(option);
		if (value != null) {
			this.plugin.getELogger().debug("SubjectData 'Options' : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='" + value + "')");
			return Optional.of(value);
		}
    	
    	// SubjectData : SubGroup
		for (SubjectReference subGroup : this.data.getSubGroup(typeWorldUser)) {
    		value = subGroup.resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			this.plugin.getELogger().debug("SubjectData 'SubGroup' : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='" + value + "')");
    			return Optional.of(value);
    		}
    	}
    	
    	// SubjectData : Groups
    	subject = this.data.getGroup(typeWorldUser);
    	if(subject.isPresent()) {
    		value = subject.get().resolve().join().getOption(contexts, option).orElse(null);
    		if (value != null) {
    			this.plugin.getELogger().debug("SubjectData 'Groups' : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='" + value + "')");
    			return Optional.of(value);
    		}
    	// Subject Default
    	} else {
    		String typeWorldGroup = this.plugin.getService().getContextCalculator().getGroup(contexts);
    		Optional<EGroupSubject> defaultGroup = this.plugin.getService().getGroupSubjects().getDefaultGroup(typeWorldGroup);
    		if(defaultGroup.isPresent()) {
        		value = defaultGroup.get().getOption(contexts, option).orElse(null);
        		if (value != null) {
        			this.plugin.getELogger().debug("SubjectData 'Default' : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='" + value + "')");
        			return Optional.of(value);
        		}
    		}
    	}
    	this.plugin.getELogger().debug("Undefined : (identifier='" + this.identifier + "';collection='" + this.collection + "';option='" + option + "';value='EMPTY')");
        return Optional.empty();
    }
	
	/*
     * Groupes
     */
    
    @Override
    public List<SubjectReference> getParents(final Set<Context> contexts) {
    	Preconditions.checkNotNull(contexts, "contexts");
        return this.getParents(this.plugin.getService().getContextCalculator().getUser(contexts));
    }
    
    public List<SubjectReference> getParents(final String typeWorldUser) {
    	List<SubjectReference> list = new ArrayList<SubjectReference>();
    	list.addAll(this.data.getParents(typeWorldUser));
    	list.addAll(this.transientData.getParents(typeWorldUser));
        return list;
    }
}
