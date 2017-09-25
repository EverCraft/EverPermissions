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

import java.util.Optional;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.command.group.EPGroup;
import fr.evercraft.everpermissions.command.sub.EPReload;
import fr.evercraft.everpermissions.command.user.EPUser;
import fr.evercraft.everpermissions.exception.GroupNotFoundWorldException;
import fr.evercraft.everpermissions.exception.GroupTypeWorldNotFoundException;
import fr.evercraft.everpermissions.service.EPermissionService;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPCommand extends EParentCommand<EverPermissions> {
	
	public EPCommand(final EverPermissions plugin) {
		super(plugin, "perms", "everpermissions", "permissions", "perm");
		
		new EPReload(this.plugin, this);
		new EPGroup(this.plugin, this);
		new EPUser(this.plugin, this);
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.EVERPERMISSIONS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EPMessages.DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EPPermissions.HELP.get());
	}
	
	public static String getTypeWorld(CommandSource source, ESubjectCollection<?> collection, String world) throws EMessageException {
		Optional<String> type = collection.getTypeWorld(world);
		if (!type.isPresent()) {
			throw new GroupNotFoundWorldException(source, world);
		} else {
			return type.get();
		}
	}
	
	public static EGroupSubject getGroup(CommandSource source, EPermissionService service, String groupName, String typeWorld) throws EMessageException {
		Optional<EGroupSubject> group = service.getGroupSubjects().get(groupName);
		if (!group.isPresent() || !group.get().hasTypeWorld(typeWorld)) {
			throw new GroupTypeWorldNotFoundException(source, groupName, typeWorld);
		} else {
			return group.get();
		}
	}
}
