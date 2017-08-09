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
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.service.context.Context;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class ESubject implements Subject {
	protected final EverPermissions plugin;
	
	private final ESubjectCollection<ESubject> collection;
	
	protected final String identifier;

	public ESubject(final EverPermissions plugin, final String identifier, final ESubjectCollection<ESubject> collection) {
		this.plugin = plugin;
		
		this.identifier = identifier;
		this.collection = collection;
	}

	public abstract void reload();
	public abstract CompletableFuture<Void> load();
	
	public abstract MemorySubjectData getSubjectData();
	public abstract MemorySubjectData getTransientSubjectData();
	
	/*
	 * Accesseurs
	 */
	
	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public SubjectCollection getContainingCollection() {
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
		if (value != null) {
			return Optional.of(value);
		}
		
		// TempoData : Groups
		Iterator<SubjectReference> subjects = this.getTransientSubjectData().getParents(contexts).iterator();
		Optional<String> optValue;
		while(subjects.hasNext()) {
			optValue = subjects.next().resolve().join().getOption(contexts, option);
			if (optValue.isPresent()) {
				return optValue;
			}
		}
		
		// SubjectData : Permissions
		value = this.getSubjectData().getOptions(contexts).get(option);
		if (value != null) {
			return Optional.of(value);
		}
		
		// SubjectData : Groups
		subjects = this.getSubjectData().getParents(contexts).iterator();
		while(subjects.hasNext()) {
			optValue = subjects.next().resolve().join().getOption(contexts, option);
			if (optValue.isPresent()) {
				return optValue;
			}
		}
		return Optional.empty();
	}
	
	/*
	 * Permissions
	 */
	
	@Override
    public Tristate getPermissionValue(final Set<Context> type_contexts, final String permission) {
    	// TempoData : Permissions
    	Tristate value = this.getTransientSubjectData().getNodeTree(type_contexts).get(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// TempoData : Groupes
    	Iterator<SubjectReference> subjects = this.getTransientSubjectData().getParents(type_contexts).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().resolve().join().getPermissionValue(type_contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
    	
    	// MemoryData : Permissions
		value = this.getSubjectData().getNodeTree(type_contexts).getTristate(permission);
		if (!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// MemoryData : Groupes
    	subjects = this.getSubjectData().getParents(type_contexts).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().resolve().join().getPermissionValue(type_contexts, permission);
    		if (!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
        return Tristate.UNDEFINED;
    }
	
	/*
	 * Groupes
	 */
	
	@Override
	public List<SubjectReference> getParents(final Set<Context> contexts) {
		Preconditions.checkNotNull(contexts, "contexts");
		List<SubjectReference> list = new ArrayList<SubjectReference>();
		list.addAll(this.getTransientSubjectData().getParents(contexts));
		list.addAll(this.getSubjectData().getParents(contexts));
		return list;
	}
	
	@Override
	public boolean isChildOf(final Set<Context> contexts, final SubjectReference parent) {
		Preconditions.checkNotNull(contexts, "contexts");
		Preconditions.checkNotNull(parent, "parent");
		
		int cpt = 0;
		boolean children = false;
		List<SubjectReference> parents = this.getParents(contexts);
		while(cpt < parents.size() && !children){
			children = parents.get(cpt).equals(parent) || parents.get(cpt).resolve().join().isChildOf(contexts, parent);
			cpt++;
		}
		return children;
	}
	
	/*
	 * Java
	 */
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
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
	
	@Override
	public SubjectReference asSubjectReference() {
		return new ESubjectReference(this.plugin.getService(), this.getContainingCollection().getIdentifier(), this.getIdentifier());
	}

	@Override
	public boolean isSubjectDataPersisted() {
		return true;
	}
}
