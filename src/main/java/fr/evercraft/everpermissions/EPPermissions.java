/**
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
	EVERPERMISSIONS("command"),
	
	HELP("help"),
	RELOAD("reload"),
	
	// Commands :
	TRANSFERT("transfert"),
	
	// Joueur :
	USER_CLEAR("user.clear"),
	
	// Joueur : Groupes
	USER_ADD_GROUP("user.group.add"),
	USER_DEL_GROUP("user.group.del"),
	
	USER_ADD_SUBGROUP("user.group.addsub"),
	USER_DEL_SUBGROUP("user.group.delsub"),
	
	USER_PROMOTE_GROUP("user.group.promote"),
	USER_DEMOTE_GROUP("user.group.demote"),
	USER_LIST_GROUP("user.group.list"),
	
	// Joueur : Permissions
	USER_ADD_PERMISSION("user.permission.add"),
	USER_DEL_PERMISSION("user.permission.del"),
	
	USER_CHECK_PERMISSION("user.permission.check"),
	USER_LIST_PERMISSION("user.permission.list"),
	
	// Joueur : Options
	USER_ADD_OPTION("user.option.add"),
	USER_DEL_OPTION("user.option.del"),
	
	USER_CHECK_OPTION("user.option.check"),
	USER_LIST_OPTION("user.option.list"),
	
	// Groupe : Groupes
	GROUP_ADD_GROUP("group.group.add"),
	GROUP_DEL_GROUP("group.group.del"),
	
	GROUP_DEFAULT_GROUP("group.group.default"),
	GROUP_LIST_GROUP("group.group.list"),
	
	// Groupe : Inheritance
	GROUP_ADD_INHERITANCE("group.inheritance.add"),
	GROUP_DEL_INHERITANCE("group.inheritance.del"),
	
	GROUP_LIST_INHERITANCE("group.inheritance.list"),
	
	// Groupe : Permissions
	GROUP_ADD_PERMISSION("group.permission.add"),
	GROUP_DEL_PERMISSION("group.permission.del"),
	
	GROUP_CHECK_PERMISSION("group.permission.check"),
	GROUP_LIST_PERMISSION("group.permission.list"),
	
	// Groupe : Options
	GROUP_ADD_OPTION("group.option.add"),
	GROUP_DEL_OPTION("group.option.del"),
	
	GROUP_CHECK_OPTION("group.option.check"),
	GROUP_LIST_OPTION("group.option.list"),
	
	// Other : Permissions
	OTHER_ADD_PERMISSION("other.permission.add"),
	OTHER_DEL_PERMISSION("other.permission.del"),
	
	OTHER_CHECK_PERMISSION("other.permission.check"),
	OTHER_LIST_PERMISSION("other.permission.list"),
	
	// Joueur : Options
	OTHER_ADD_OPTION("other.option.add"),
	OTHER_DEL_OPTION("other.option.del"),
	
	OTHER_CHECK_OPTION("other.option.check"),
	OTHER_LIST_OPTION("other.option.list");
	
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
