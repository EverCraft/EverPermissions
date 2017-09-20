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
package fr.evercraft.everpermissions;

import fr.evercraft.everapi.plugin.EnumPermission;
import fr.evercraft.everapi.plugin.file.EnumMessage;
import fr.evercraft.everpermissions.EPMessage.EPMessages;

public enum EPPermissions implements EnumPermission {	
	BROADCAST("broadcast", EPMessages.PERMISSIONS_BROADCAST),
	
	// Commands :
	EVERPERMISSIONS("commands.execute", EPMessages.PERMISSIONS_COMMANDS_EXECUTE),
	HELP("commands.help", EPMessages.PERMISSIONS_COMMANDS_HELP),
	RELOAD("commands.reload", EPMessages.PERMISSIONS_COMMANDS_RELOAD),
	MIGRATE("commands.migrate", EPMessages.PERMISSIONS_COMMANDS_MIGRATE),
	
	// Joueur :
	USER_CLEAR("commands.user.clear", EPMessages.PERMISSIONS_COMMANDS_USER_CLEAR),
	
	// Joueur : Groupes
	USER_ADD_GROUP("commands.user.group.add", EPMessages.PERMISSIONS_COMMANDS_USER_GROUP_ADD),
	USER_DEL_GROUP("commands.user.group.remove", EPMessages.PERMISSIONS_COMMANDS_USER_GROUP_REMOVE),
	
	USER_ADD_SUBGROUP("commands.user.group.addsub", EPMessages.PERMISSIONS_COMMANDS_USER_GROUP_ADDSUB),
	USER_DEL_SUBGROUP("commands.user.group.removesub", EPMessages.PERMISSIONS_COMMANDS_USER_GROUP_REMOVESUB),
	
	USER_PROMOTE_GROUP("commands.user.group.promote", EPMessages.PERMISSIONS_COMMANDS_USER_GROUP_PROMOTE),
	USER_DEMOTE_GROUP("commands.user.group.demote", EPMessages.PERMISSIONS_COMMANDS_USER_GROUP_DEMOTE),
	USER_LIST_GROUP("commands.user.group.list", EPMessages.PERMISSIONS_COMMANDS_USER_GROUP_LIST),
	
	// Joueur : Permissions
	USER_ADD_PERMISSION("commands.user.permission.add", EPMessages.PERMISSIONS_COMMANDS_USER_PERMISSION_ADD),
	USER_DEL_PERMISSION("commands.user.permission.remove", EPMessages.PERMISSIONS_COMMANDS_USER_PERMISSION_REMOVE),
	
	USER_CHECK_PERMISSION("commands.user.permission.check", EPMessages.PERMISSIONS_COMMANDS_USER_PERMISSION_CHECK),
	USER_LIST_PERMISSION("commands.user.permission.list", EPMessages.PERMISSIONS_COMMANDS_USER_PERMISSION_LIST),
	
	// Joueur : Options
	USER_ADD_OPTION("commands.user.option.add", EPMessages.PERMISSIONS_COMMANDS_USER_OPTION_ADD),
	USER_DEL_OPTION("commands.user.option.remove", EPMessages.PERMISSIONS_COMMANDS_USER_OPTION_REMOVE),
	
	USER_CHECK_OPTION("commands.user.option.check", EPMessages.PERMISSIONS_COMMANDS_USER_OPTION_CHECK),
	USER_LIST_OPTION("commands.user.option.list", EPMessages.PERMISSIONS_COMMANDS_USER_OPTION_LIST),
	
	// Groupe : Groupes
	GROUP_EXECUTE("commands.group.execute", EPMessages.PERMISSIONS_COMMANDS_GROUP_EXECUTE),
	GROUP_ADD("commands.group.add", EPMessages.PERMISSIONS_COMMANDS_GROUP_ADD),
	GROUP_REMOVE("commands.group.remove", EPMessages.PERMISSIONS_COMMANDS_GROUP_REMOVE),
	GROUP_DEFAULT("commands.group.default", EPMessages.PERMISSIONS_COMMANDS_GROUP_DEFAULT),
	GROUP_LIST("commands.group.list", EPMessages.PERMISSIONS_COMMANDS_GROUP_LIST),
	GROUP_RENAME("commands.group.rename", EPMessages.PERMISSIONS_COMMANDS_GROUP_RENAME),
	GROUP_INFO("commands.group.info", EPMessages.PERMISSIONS_COMMANDS_GROUP_INFO),
	GROUP_VERBOSE("commands.group.verbose", EPMessages.PERMISSIONS_COMMANDS_GROUP_VERBOSE),
	
	// Groupe : Inheritance
	GROUP_INHERITANCE_EXECUTE("commands.group.inheritance.execute", EPMessages.PERMISSIONS_COMMANDS_GROUP_INHERITANCE_EXECUTE),
	GROUP_INHERITANCE_ADD("commands.group.inheritance.add", EPMessages.PERMISSIONS_COMMANDS_GROUP_INHERITANCE_ADD),
	GROUP_INHERITANCE_REMOVE("commands.group.inheritance.remove", EPMessages.PERMISSIONS_COMMANDS_GROUP_INHERITANCE_REMOVE),
	GROUP_INHERITANCE_INFO("commands.group.inheritance.info", EPMessages.PERMISSIONS_COMMANDS_GROUP_INHERITANCE_INFO),
	
	// Groupe : Permissions
	GROUP_PERMISSION_EXECUTE("commands.group.permission.execute", EPMessages.PERMISSIONS_COMMANDS_GROUP_PERMISSION_ADD),
	GROUP_PERMISSION_ADD("commands.group.permission.add", EPMessages.PERMISSIONS_COMMANDS_GROUP_PERMISSION_ADD),
	GROUP_PERMISSION_REMOVE("commands.group.permission.remove", EPMessages.PERMISSIONS_COMMANDS_GROUP_PERMISSION_REMOVE),
	GROUP_PERMISSION_CHECK("commands.group.permission.check", EPMessages.PERMISSIONS_COMMANDS_GROUP_PERMISSION_CHECK),
	GROUP_PERMISSION_INFO("commands.group.permission.info", EPMessages.PERMISSIONS_COMMANDS_GROUP_PERMISSION_INFO),
	
	// Groupe : Options
	GROUP_OPTION_EXECUTE("commands.group.option.execute", EPMessages.PERMISSIONS_COMMANDS_GROUP_OPTION_EXECUTE),
	GROUP_OPTION_ADD("commands.group.option.add", EPMessages.PERMISSIONS_COMMANDS_GROUP_OPTION_ADD),
	GROUP_OPTION_REMOVE("commands.group.option.remove", EPMessages.PERMISSIONS_COMMANDS_GROUP_OPTION_REMOVE),
	GROUP_OPTION_CHECK("commands.group.option.check", EPMessages.PERMISSIONS_COMMANDS_GROUP_OPTION_CHECK),
	GROUP_OPTION_INFO("commands.group.option.info", EPMessages.PERMISSIONS_COMMANDS_GROUP_OPTION_INFO),
	
	// Other : Groupes
	OTHER_ADD_GROUP("commands.other.group.add", EPMessages.PERMISSIONS_COMMANDS_OTHER_GROUP_ADD),
	OTHER_DEL_GROUP("commands.other.group.remove", EPMessages.PERMISSIONS_COMMANDS_OTHER_GROUP_REMOVE),
	
	OTHER_ADD_SUBGROUP("commands.other.group.addsub", EPMessages.PERMISSIONS_COMMANDS_OTHER_GROUP_ADDSUB),
	OTHER_DEL_SUBGROUP("commands.other.group.removesub", EPMessages.PERMISSIONS_COMMANDS_OTHER_GROUP_REMOVESUB),
	
	OTHER_PROMOTE_GROUP("commands.other.group.promote", EPMessages.PERMISSIONS_COMMANDS_OTHER_GROUP_PROMOTE),
	OTHER_DEMOTE_GROUP("commands.other.group.demote", EPMessages.PERMISSIONS_COMMANDS_OTHER_GROUP_DEMOTE),
	OTHER_LIST_GROUP("commands.other.group.list", EPMessages.PERMISSIONS_COMMANDS_OTHER_GROUP_LIST),
	
	// Other : Permissions
	OTHER_ADD_PERMISSION("commands.other.permission.add", EPMessages.PERMISSIONS_COMMANDS_OTHER_PERMISSION_ADD),
	OTHER_DEL_PERMISSION("commands.other.permission.remove", EPMessages.PERMISSIONS_COMMANDS_OTHER_PERMISSION_REMOVE),
	
	OTHER_CHECK_PERMISSION("commands.other.permission.check", EPMessages.PERMISSIONS_COMMANDS_OTHER_PERMISSION_CHECK),
	OTHER_LIST_PERMISSION("commands.other.permission.list", EPMessages.PERMISSIONS_COMMANDS_OTHER_PERMISSION_LIST),
	
	// Other : Options
	OTHER_ADD_OPTION("commands.other.option.add", EPMessages.PERMISSIONS_COMMANDS_OTHER_OPTION_ADD),
	OTHER_DEL_OPTION("commands.other.option.remove", EPMessages.PERMISSIONS_COMMANDS_OTHER_OPTION_REMOVE),
	
	OTHER_CHECK_OPTION("commands.other.option.check", EPMessages.PERMISSIONS_COMMANDS_OTHER_OPTION_CHECK),
	OTHER_LIST_OPTION("commands.other.option.list", EPMessages.PERMISSIONS_COMMANDS_OTHER_OPTION_LIST);
	
	private static final String PREFIX = "everpermissions";
	
	private final String permission;
	private final EnumMessage message;
	private final boolean value;
    
    private EPPermissions(final String permission, final EnumMessage message) {
    	this(permission, message, false);
    }
    
    private EPPermissions(final String permission, final EnumMessage message, final boolean value) {   	    	
    	this.permission = PREFIX + "." + permission;
    	this.message = message;
    	this.value = value;
    }

    @Override
    public String get() {
    	return this.permission;
	}

	@Override
	public boolean getDefault() {
		return this.value;
	}

	@Override
	public EnumMessage getMessage() {
		return this.message;
	}
}
