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

import fr.evercraft.everapi.plugin.EPermission;
import fr.evercraft.everapi.plugin.EPlugin;

public class EPPermission extends EPermission {

	public EPPermission(final EPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void load() {
		add("EVERPERMISSIONS", "command");
		add("HELP", "help");
		add("RELOAD", "reload");
		
		// Commands :
		add("TRANSFERT", "transfert");
		
		// Joueur :
		add("USER_CLEAR", "user.clear");
		
		// Joueur : Groupes
		add("USER_ADD_GROUP", "user.group.add");
		add("USER_DEL_GROUP", "user.group.del");
		
		add("USER_ADD_SUBGROUP", "user.group.addsub");
		add("USER_DEL_SUBGROUP", "user.group.delsub");
		
		add("USER_PROMOTE_GROUP", "user.group.promote");
		add("USER_DEMOTE_GROUP", "user.group.demote");
		add("USER_LIST_GROUP", "user.group.list");
		
		// Joueur : Permissions
		add("USER_ADD_PERMISSION", "user.permission.add");
		add("USER_DEL_PERMISSION", "user.permission.del");
		
		add("USER_CHECK_PERMISSION", "user.permission.check");
		add("USER_LIST_PERMISSION", "user.permission.list");
		
		// Joueur : Options
		add("USER_ADD_OPTION", "user.option.add");
		add("USER_DEL_OPTION", "user.option.del");
		
		add("USER_CHECK_OPTION", "user.option.check");
		add("USER_LIST_OPTION", "user.option.list");
		
		// Groupe : Groupes
		add("GROUP_ADD_GROUP", "group.group.add");
		add("GROUP_DEL_GROUP", "group.group.del");
		
		add("GROUP_DEFAULT_GROUP", "group.group.default");
		add("GROUP_LIST_GROUP", "group.group.list");
		
		// Groupe : Inheritance
		add("GROUP_ADD_INHERITANCE", "group.inheritance.add");
		add("GROUP_DEL_INHERITANCE", "group.inheritance.del");
		
		add("GROUP_LIST_INHERITANCE", "group.inheritance.list");
		
		// Groupe : Permissions
		add("GROUP_ADD_PERMISSION", "group.permission.add");
		add("GROUP_DEL_PERMISSION", "group.permission.del");
		
		add("GROUP_CHECK_PERMISSION", "group.permission.check");
		add("GROUP_LIST_PERMISSION", "group.permission.list");
		
		// Groupe : Options
		add("GROUP_ADD_OPTION", "group.option.add");
		add("GROUP_DEL_OPTION", "group.option.del");
		
		add("GROUP_CHECK_OPTION", "group.option.check");
		add("GROUP_LIST_OPTION", "group.option.list");
		
		// Other : Permissions
		add("OTHER_ADD_PERMISSION", "other.permission.add");
		add("OTHER_DEL_PERMISSION", "other.permission.del");
		
		add("OTHER_CHECK_PERMISSION", "other.permission.check");
		add("OTHER_LIST_PERMISSION", "other.permission.list");
		
		// Joueur : Options
		add("OTHER_ADD_OPTION", "other.option.add");
		add("OTHER_DEL_OPTION", "other.option.del");
		
		add("OTHER_CHECK_OPTION", "other.option.check");
		add("OTHER_LIST_OPTION", "other.option.list");
	}
}
