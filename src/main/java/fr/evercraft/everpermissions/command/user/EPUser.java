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
package fr.evercraft.everpermissions.command.user;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.EParentCommand;
import fr.evercraft.everapi.plugin.command.EParentSubCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPUser extends EParentSubCommand<EverPermissions> {
	
	private final EPUserInfo info;
	
	public EPUser(final EverPermissions plugin, final EParentCommand<EverPermissions> command) {
		super(plugin, command, "user");
		
		this.info = new EPUserInfo(this.plugin, this);
		
		new EPUserGroup(this.plugin, this);
		new EPUserSubGroup(this.plugin, this);
		new EPUserPermission(this.plugin, this);
		new EPUserOption(this.plugin, this);
		new EPUserClear(this.plugin, this);
		new EPUserTypes(this.plugin, this);
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_EXECUTE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EPMessages.USER_DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return true;
	}
	
	public EPUserInfo getInfo() {
		return this.info;
	}
	
	public Text getButtonPermissionKey(final String groupName, final String permission, final Boolean value, final String worldName) {
		return EChat.of(permission).toBuilder()
				.onClick(TextActions.suggestCommand("/" + this.getName() + " permission add " + Args.MARKER_WORLD + " " + worldName  + " " + groupName + " " + permission + " " + value))
				.onShiftClick(TextActions.insertText(permission))
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
				.build();
	}
	
	public Text getButtonPermissionValue(final String groupName, final String permission, final Text valueText, final Boolean value, final String worldName) {
		return valueText.toBuilder()
				.onClick(TextActions.suggestCommand("/" + this.getName() + " permission add " + Args.MARKER_WORLD + " " + worldName  + " " + groupName + " " + permission + " " + value))
				.onShiftClick(TextActions.insertText(permission))
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
				.build();
	}
	
	public Text getButtonOptionKey(final String groupName, final String option, final String value, final String worldName) {
		return EChat.of(option).toBuilder()
				.onClick(TextActions.suggestCommand("/" + this.getName() + " option add " + Args.MARKER_WORLD + " " + worldName  + " " + groupName + " " + option + " \"" + value + "\""))
				.onShiftClick(TextActions.insertText(option))
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
				.build();
	}
	
	public Text getButtonOptionValue(final String groupName, final String option, final String value, final String worldName) {
		return Text.of(value).toBuilder()
				.onClick(TextActions.suggestCommand("/" + this.getName() + " option add " + Args.MARKER_WORLD + " " + worldName  + " " + groupName + " " + option + " \"" + value + "\""))
				.onShiftClick(TextActions.insertText(value))
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
				.build();
	}
	
	public Text getButtonInfo(final String name, final String worldName) {
		return EChat.of(name).toBuilder()
				.onClick(TextActions.runCommand("/" + this.getParentName() + " group info " + Args.MARKER_WORLD + " \"" + worldName  + "\" \"" + name + "\""))
				.onShiftClick(TextActions.insertText(name))
				.onHover(TextActions.showText(EAMessages.MORE_INFORMATION.getText()))
				.build();
	}
}
