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

import org.spongepowered.api.command.CommandSource;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EnumPermission;

public enum EPPermissions implements EnumPermission {	
	BROADCAST("broadcast"),
	
	// Commands :
	EVERPERMISSIONS("commands.execute"),
	HELP("commands.help"),
	RELOAD("commands.reload"),
	MIGRATE("commands.migrate"),
	
	// Joueur :
	USER_CLEAR("commands.user.clear"),
	
	// Joueur : Groupes
	USER_ADD_GROUP("commands.user.group.add"),
	USER_DEL_GROUP("commands.user.group.del"),
	
	USER_ADD_SUBGROUP("commands.user.group.addsub"),
	USER_DEL_SUBGROUP("commands.user.group.delsub"),
	
	USER_PROMOTE_GROUP("commands.user.group.promote"),
	USER_DEMOTE_GROUP("commands.user.group.demote"),
	USER_LIST_GROUP("commands.user.group.list"),
	
	// Joueur : Permissions
	USER_ADD_PERMISSION("commands.user.permission.add"),
	USER_DEL_PERMISSION("commands.user.permission.del"),
	
	USER_CHECK_PERMISSION("commands.user.permission.check"),
	USER_LIST_PERMISSION("commands.user.permission.list"),
	
	// Joueur : Options
	USER_ADD_OPTION("commands.user.option.add"),
	USER_DEL_OPTION("commands.user.option.del"),
	
	USER_CHECK_OPTION("commands.user.option.check"),
	USER_LIST_OPTION("commands.user.option.list"),
	
	// Groupe : Groupes
	GROUP_ADD_GROUP("commands.group.group.add"),
	GROUP_DEL_GROUP("commands.group.group.del"),
	
	GROUP_DEFAULT_GROUP("commands.group.group.default"),
	GROUP_LIST_GROUP("commands.group.group.list"),
	
	// Groupe : Inheritance
	GROUP_ADD_INHERITANCE("commands.group.inheritance.add"),
	GROUP_DEL_INHERITANCE("commands.group.inheritance.del"),
	
	GROUP_LIST_INHERITANCE("commands.group.inheritance.list"),
	
	// Groupe : Permissions
	GROUP_ADD_PERMISSION("commands.group.permission.add"),
	GROUP_DEL_PERMISSION("commands.group.permission.del"),
	
	GROUP_CHECK_PERMISSION("commands.group.permission.check"),
	GROUP_LIST_PERMISSION("commands.group.permission.list"),
	
	// Groupe : Options
	GROUP_ADD_OPTION("commands.group.option.add"),
	GROUP_DEL_OPTION("group.option.del"),
	
	GROUP_CHECK_OPTION("group.option.check"),
	GROUP_LIST_OPTION("group.option.list"),
	
	// Other : Groupes
	OTHER_ADD_GROUP("commands.other.group.add"),
	OTHER_DEL_GROUP("commands.other.group.del"),
	
	OTHER_ADD_SUBGROUP("commands.other.group.addsub"),
	OTHER_DEL_SUBGROUP("commands.other.group.delsub"),
	
	OTHER_PROMOTE_GROUP("commands.other.group.promote"),
	OTHER_DEMOTE_GROUP("commands.other.group.demote"),
	OTHER_LIST_GROUP("commands.other.group.list"),
	
	// Other : Permissions
	OTHER_ADD_PERMISSION("commands.other.permission.add"),
	OTHER_DEL_PERMISSION("commands.other.permission.del"),
	
	OTHER_CHECK_PERMISSION("commands.other.permission.check"),
	OTHER_LIST_PERMISSION("commands.other.permission.list"),
	
	// Other : Options
	OTHER_ADD_OPTION("commands.other.option.add"),
	OTHER_DEL_OPTION("commands.other.option.del"),
	
	OTHER_CHECK_OPTION("commands.other.option.check"),
	OTHER_LIST_OPTION("commands.other.option.list");
	
	private final static String prefix = "everpermissions";
	
	private final String permission;
    
    private EPPermissions(final String permission) {   	
    	Preconditions.checkNotNull(permission, "La permission '" + this.name() + "' n'est pas définit");
    	
    	this.permission = permission;
    }

    public String get() {
		return EPPermissions.prefix + "." + this.permission;
	}
    
    public boolean has(CommandSource player) {
    	return player.hasPermission(this.get());
    }
}
