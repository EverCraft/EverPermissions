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
package fr.evercraft.everpermissions.service.permission.data.user;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.event.PermUserEvent.Action;
import fr.evercraft.everpermissions.EverPermissions;

public class ETransientUserData extends MemorySubjectData {
	private final EverPermissions plugin;
	
	private final Subject subject;
	
	public ETransientUserData(final EverPermissions plugin, final Subject subject) {
		super(plugin.getService());
		
		this.plugin = plugin;
		this.subject = subject;
	}

    @Override
    public CompletableFuture<Boolean> setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
    	return super.setPermission(contexts, permission, value)
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }
    
    @Override
    public CompletableFuture<Boolean> clearPermissions(final Set<Context> context) {
    	return super.clearPermissions(context)
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }

    @Override
    public CompletableFuture<Boolean> clearPermissions() {
    	return super.clearPermissions()
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }

    @Override
    public CompletableFuture<Boolean> addParent(final Set<Context> contexts, final SubjectReference parent) {
    	return super.addParent(contexts, parent)
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }

    @Override
    public CompletableFuture<Boolean> removeParent(final Set<Context> contexts, final SubjectReference parent) {
    	return super.removeParent(contexts, parent)
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }
    
    @Override
    public CompletableFuture<Boolean> clearParents(final Set<Context> contexts) {
    	return super.clearParents(contexts)
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }

    @Override
    public CompletableFuture<Boolean> clearParents() {
    	return super.clearParents()
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }

    @Override
    public CompletableFuture<Boolean> setOption(final Set<Context> contexts, final String key, final @Nullable String value) {
    	return super.setOption(contexts, key, value)
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }

    @Override
    public CompletableFuture<Boolean> clearOptions(final Set<Context> contexts) {
    	return super.clearOptions(contexts)
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }

    @Override
    public CompletableFuture<Boolean> clearOptions() {
    	return super.clearOptions()
    		.thenApplyAsync(result -> {
    			if (!result) return false;
    			
    			this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
    			return true;
    		}, this.plugin.getThreadAsync());
    }
}
