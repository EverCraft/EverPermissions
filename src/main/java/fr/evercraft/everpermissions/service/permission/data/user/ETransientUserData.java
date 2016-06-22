package fr.evercraft.everpermissions.service.permission.data.user;

import java.util.Set;

import javax.annotation.Nullable;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.Subject;
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
    public boolean setPermission(final Set<Context> contexts, final String permission, final Tristate value) {
        if(super.setPermission(contexts, permission, value)) {
        	this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
        	return true;
        }
        return false;
    }
    
    @Override
    public boolean clearPermissions(final Set<Context> context) {
    	 if(super.clearPermissions(context)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
         	return true;
         }
         return false;
    }

    @Override
    public boolean clearPermissions() {
    	 if(super.clearPermissions()) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_PERMISSION_CHANGED);
         	return true;
         }
         return false;
    }

    @Override
    public boolean addParent(final Set<Context> contexts, final Subject parent) {
    	if(super.addParent(contexts, parent)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean removeParent(final Set<Context> contexts, final Subject parent) {
    	if(super.addParent(contexts, parent)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
         	return true;
        }
    	return false;
    }
    
    @Override
    public boolean clearParents(final Set<Context> contexts) {
    	if(super.clearParents(contexts)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean clearParents() {
    	if(super.clearParents()) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_SUBGROUP_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean setOption(final Set<Context> contexts, final String key, final @Nullable String value) {
    	if(super.setOption(contexts, key, value)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean clearOptions(final Set<Context> contexts) {
    	if(super.clearOptions(contexts)) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
         	return true;
        }
    	return false;
    }

    @Override
    public boolean clearOptions() {
    	if(super.clearOptions()) {
         	this.plugin.getManagerEvent().post(this.subject, Action.USER_OPTION_CHANGED);
         	return true;
        }
    	return false;
    }
}
