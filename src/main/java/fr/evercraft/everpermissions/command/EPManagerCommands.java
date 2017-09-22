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
package fr.evercraft.everpermissions.command;

import java.util.TreeMap;

import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.command.disable.*;
import fr.evercraft.everpermissions.command.group.*;
import fr.evercraft.everpermissions.command.other.option.*;
import fr.evercraft.everpermissions.command.other.permission.*;
import fr.evercraft.everpermissions.command.sub.EPReload;
import fr.evercraft.everpermissions.command.user.EPUser;
import fr.evercraft.everpermissions.command.user.EPUserClear;
import fr.evercraft.everpermissions.command.user.EPUserInfo;
import fr.evercraft.everpermissions.command.user.group.*;
import fr.evercraft.everpermissions.command.user.option.*;
import fr.evercraft.everpermissions.command.user.permission.*;

public class EPManagerCommands extends TreeMap<String, ECommand<EverPermissions>>{
	
	private static final long serialVersionUID = 5945785433701848304L;

	private EverPermissions plugin;
	
	public EPManagerCommands(EverPermissions plugin){
		super();
		this.plugin = plugin;
		
		load();
	}
	
	public void load() {
		EPCommand manager_command = new EPCommand(this.plugin);
		manager_command.add(new EPReload(this.plugin, manager_command));
		
		// Group :
		EPGroup manager_group = new EPGroup(this.plugin, manager_command);
		manager_command.add(manager_group);
		
		manager_group.add(new EPGroupCreate(this.plugin, manager_group));
		manager_group.add(new EPGroupRemove(this.plugin, manager_group));
		manager_group.add(new EPGroupDefault(this.plugin, manager_group));
		manager_group.add(new EPGroupList(this.plugin, manager_group));
		manager_group.add(new EPGroupRename(this.plugin, manager_group));
		manager_group.add(new EPGroupInfo(this.plugin, manager_group));
		manager_group.add(new EPGroupVerbose(this.plugin, manager_group));
		
		EPGroupPermission manager_group_permission = new EPGroupPermission(this.plugin, manager_group);
		manager_group.add(manager_group_permission);
		
		manager_group_permission.add(new EPGroupPermissionAdd(this.plugin, manager_group_permission));
		manager_group_permission.add(new EPGroupPermissionRemove(this.plugin, manager_group_permission));
		manager_group_permission.add(new EPGroupPermissionCheck(this.plugin, manager_group_permission));
		
		EPGroupOption manager_group_option = new EPGroupOption(this.plugin, manager_group);
		manager_group.add(manager_group_option);
		
		manager_group_option.add(new EPGroupOptionAdd(this.plugin, manager_group_option));
		manager_group_option.add(new EPGroupOptionRemove(this.plugin, manager_group_option));
		manager_group_option.add(new EPGroupOptionCheck(this.plugin, manager_group_option));
		
		EPGroupInheritance manager_group_inheritance = new EPGroupInheritance(this.plugin, manager_group);
		manager_group.add(manager_group_inheritance);
		
		manager_group_inheritance.add(new EPGroupInheritanceAdd(this.plugin, manager_group_inheritance));
		manager_group_inheritance.add(new EPGroupInheritanceRemove(this.plugin, manager_group_inheritance));
		
		// User :
		EPUser manager_user = new EPUser(this.plugin, manager_command);
		manager_command.add(manager_user);
		
		manager_user.add(new EPUserInfo(this.plugin, manager_user));
		
		// Commands : Disable
		register(new EPDeop(this.plugin));
		register(new EPOp(this.plugin));
		
		// Commands :		
		register(new EPMigrate(this.plugin));
		
		// User : 
		register(new EPUserClear(this.plugin));
		
		// User : Group
		register(new EPUserAddGroup(this.plugin));
		register(new EPUserDelGroup(this.plugin));
		
		register(new EPUserAddSubGroup(this.plugin));
		register(new EPUserDelSubGroup(this.plugin));
		
		register(new EPUserPromoteGroup(this.plugin));
		register(new EPUserDemoteGroup(this.plugin));
		
		register(new EPUserListGroup(this.plugin));
		
		// User : Permission
		register(new EPUserAddPerm(this.plugin));
		register(new EPUserDelPerm(this.plugin));
		
		register(new EPUserCheckPerm(this.plugin));
		register(new EPUserListPerm(this.plugin));
		
		// User : Option
		register(new EPUserAddOption(this.plugin));
		register(new EPUserDelOption(this.plugin));
		
		register(new EPUserCheckOption(this.plugin));
		register(new EPUserListOption(this.plugin));
		
		// Other : Permission
		register(new EPOtherAddPerm(this.plugin));
		register(new EPOtherDelPerm(this.plugin));
		
		register(new EPOtherCheckPerm(this.plugin));
		register(new EPOtherListPerm(this.plugin));
		
		// Other : Option
		register(new EPOtherAddOption(this.plugin));
		register(new EPOtherDelOption(this.plugin));
		
		register(new EPOtherCheckOption(this.plugin));
		register(new EPOtherListOption(this.plugin));
		
		for (ECommand<EverPermissions> command : this.values()) {
			manager_command.add(command);
		}
	}

	private void register(ECommand<EverPermissions> command) {
		this.put(command.getName(), command);
	}
}
