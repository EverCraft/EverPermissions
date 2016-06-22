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
package fr.evercraft.everpermissions.commands;

import java.util.TreeMap;

import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.commands.disable.EPDeop;
import fr.evercraft.everpermissions.commands.disable.EPOp;
import fr.evercraft.everpermissions.commands.group.group.*;
import fr.evercraft.everpermissions.commands.group.inheritance.*;
import fr.evercraft.everpermissions.commands.group.option.*;
import fr.evercraft.everpermissions.commands.group.permission.*;
import fr.evercraft.everpermissions.commands.other.option.*;
import fr.evercraft.everpermissions.commands.other.permission.*;
import fr.evercraft.everpermissions.commands.user.EPUserClear;
import fr.evercraft.everpermissions.commands.user.group.*;
import fr.evercraft.everpermissions.commands.user.option.*;
import fr.evercraft.everpermissions.commands.user.permission.*;

public class EPManagerCommands extends TreeMap<String, ECommand<EverPermissions>>{
	
	private static final long serialVersionUID = 5945785433701848304L;

	private EverPermissions plugin;
	
	public EPManagerCommands(EverPermissions plugin){
		super();
		this.plugin = plugin;
		
		load();
	}
	
	public void load() {
		new EPCommand(this.plugin);
		
		// Commands : Disable
		register(new EPDeop(this.plugin));
		register(new EPOp(this.plugin));
		
		// Commands :		
		register(new EPTransfert(this.plugin));
		
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
		
		// Group : Group
		register(new EPGroupAddGroup(this.plugin));
		register(new EPGroupDelGroup(this.plugin));
		
		register(new EPGroupDefaultGroup(this.plugin));
		register(new EPGroupListGroup(this.plugin));
		
		// Group : Inheritance
		register(new EPGroupAddInheritance(this.plugin));
		register(new EPGroupDelInheritance(this.plugin));
		
		register(new EPGroupListInheritance(this.plugin));
		
		// Group : Permission
		register(new EPGroupAddPerm(this.plugin));
		register(new EPGroupDelPerm(this.plugin));
		
		register(new EPGroupCheckPerm(this.plugin));
		register(new EPGroupListPerm(this.plugin));
		
		// Group : Option
		register(new EPGroupAddOption(this.plugin));
		register(new EPGroupDelOption(this.plugin));
		
		register(new EPGroupCheckOption(this.plugin));
		register(new EPGroupListOption(this.plugin));
		
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
	}

	private void register(ECommand<EverPermissions> command) {
		this.put(command.getName(), command);
	}
}
