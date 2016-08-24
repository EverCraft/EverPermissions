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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import com.google.common.base.Preconditions;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.data.user.ETransientUserData;
import fr.evercraft.everpermissions.service.permission.data.user.EUserData;

public class EUserSubject extends ESubject {
	private final EUserData data;
	private final ETransientUserData transientData;
	
    public EUserSubject(final EverPermissions plugin, final String identifier, final ESubjectCollection collection) {
    	super(plugin, identifier, collection);

    	this.data = new EUserData(this.plugin, this);
        this.transientData = new ETransientUserData(this.plugin, this);
    }
    
    /*
     * Accesseurs
     */
    
    public Optional<Subject> getSubject() {
        return this.getSubject(this.getActiveContexts());
    }
    
    public Optional<Subject> getSubject(final Set<Context> contexts) {
        return this.data.getParent(contexts);
    }
    
    @Override
    public EUserData getSubjectData() {
        return this.data;
    }

    @Override
    public MemorySubjectData getTransientSubjectData() {
        return this.transientData;
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
    	Optional<Player> optPlayer = this.plugin.getGame().getServer().getPlayer(UUID.fromString(this.getIdentifier()));
        if (optPlayer.isPresent()) {
            return Optional.of(optPlayer.get());
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
		Set<Context> contexts_user = this.plugin.getService().getContextCalculator().getContextUser(contexts);
		// TempoData : Permissions
		Tristate value = this.getTransientSubjectData().getNodeTree(contexts_user).get(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			this.plugin.getLogger().debug("TransientSubjectData 'Permissions' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
			return value;
		}
    	
		// TempoData : Groups
    	Set<Context> contexts_group = this.plugin.getService().getContextCalculator().getContextGroup(contexts);
    	Iterator<Subject> subjects = this.getTransientSubjectData().getParents(contexts_user).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().getPermissionValue(contexts_group, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getLogger().debug("TransientSubjectData 'Parents' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.getSubjectData().getNodeTree(contexts_user).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			this.plugin.getLogger().debug("SubjectData 'Permissions' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
			return value;
		}
    	
    	// SubjectData : SubGroup
    	subjects = this.getSubjectData().getSubParentsContexts(contexts_user).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().getPermissionValue(contexts_group, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getLogger().debug("SubjectData 'SubGroup' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	
    	// SubjectData : Groups
    	subjects = this.getSubjectData().getParentsContexts(contexts_user).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().getPermissionValue(contexts_group, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			this.plugin.getLogger().debug("SubjectData 'Groups' : (identifier='" + this.identifier + "';permission='" + permission + "';value='" + value.name() + "')");
    			return value;
    		}
    	}
    	this.plugin.getLogger().debug("SubjectData '' : (identifier='" + this.identifier + "';permission='" + permission + "';value='UNDEFINED')");
        return Tristate.UNDEFINED;
    }
    
    /*
     * Options
     */
    
    @Override
    public Optional<String> getOption(final Set<Context> contexts, final String option) {
    	Set<Context> contexts_user = this.plugin.getService().getContextCalculator().getContextUser(contexts);
		// TempoData : Permissions
		String value = this.getTransientSubjectData().getOptions(contexts_user).get(option);
		if (value != null) {
			return Optional.of(value);
		}
    	
		// TempoData : Groups
		Set<Context> contexts_group = this.plugin.getService().getContextCalculator().getContextGroup(contexts);
    	Iterator<Subject> subjects = this.getTransientSubjectData().getParents(contexts_user).iterator();
    	Optional<String> optValue;
    	while(subjects.hasNext()) {
    		optValue = ((ESubject) subjects.next()).getOption(contexts_group, option);
    		if (optValue.isPresent()) {
    			return optValue;
    		}
    	}
    	
    	// SubjectData : Permissions
    	value = this.getSubjectData().getOptionsContexts(contexts_user).get(option);
    	if (value != null) {
			return Optional.of(value);
		}
    	
    	// SubjectData : Groups
    	subjects = this.getSubjectData().getParentsContexts(contexts_user).iterator();
    	while(subjects.hasNext()) {
    		optValue = ((ESubject) subjects.next()).getOption(contexts_group, option);
    		if (optValue.isPresent()) {
    			return optValue;
    		}
    	}
        return Optional.empty();
    }
	
	public void reload() {
		this.data.reload();
    	
		this.transientData.clearPermissions();
		this.transientData.clearOptions();
		this.transientData.clearParents();
    }
	
	 /*
     * Groupes
     */
    
    @Override
    public List<Subject> getParents(final Set<Context> contexts) {
    	Preconditions.checkNotNull(contexts, "contexts");
    	List<Subject> list = new ArrayList<Subject>();
    	list.addAll(this.getSubjectData().getParents(contexts));
    	list.addAll(this.getSubjectData().getSubParents(contexts));
    	list.addAll(this.getTransientSubjectData().getParents(contexts));
        return list;
    }
}
