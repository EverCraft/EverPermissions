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

import fr.evercraft.everpermissions.EPConfig;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EPTransientSubject;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EPTransientCollection extends EPSubjectCollection<EPTransientSubject> {

    public EPTransientCollection(final EverPermissions plugin, String collection) {
    	super(plugin, collection);
    }
    
	@Override
	protected EPTransientSubject add(String identifier) {
    	return new EPTransientSubject(this.plugin, identifier, this);
	}
	
	@Override
	public void suggestUnload(String identifier) {}
	
	@Override
	public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(Set<Context> contexts, String permission) {
		Preconditions.checkNotNull(contexts, "contexts");
		Preconditions.checkNotNull(permission, "permission");
		
		ImmutableMap.Builder<SubjectReference, Boolean> builder = ImmutableMap.builder();
		for (Subject subject : this.identifierSubjects.values()) {
			Tristate value = subject.getPermissionValue(contexts, permission);
			if (!value.equals(Tristate.UNDEFINED)) {
				builder.put(subject.asSubjectReference(), value.asBoolean());
			}
		}
		return CompletableFuture.completedFuture(builder.build());
	}
	
	@Override
	public boolean isTransient() {
		return true;
	}
	
	@Override
	public void reloadConfig() {
		// Stop
		this.worlds.clear();
		
		// Start
		for (World world : this.plugin.getGame().getServer().getWorlds()) {
			this.worlds.put(EPConfig.DEFAULT, world.getName());
		}
	}
	
	@Override
	public void registerWorld(final String world) {
		this.worlds.put(EPConfig.DEFAULT, world);
	}
}
