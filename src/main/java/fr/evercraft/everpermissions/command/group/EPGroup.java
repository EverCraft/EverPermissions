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
package fr.evercraft.everpermissions.command.group;

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

public class EPGroup extends EParentSubCommand<EverPermissions> {
	
	private final EPGroupInfo info;
	
	public EPGroup(final EverPermissions plugin, final EParentCommand<EverPermissions> command) {
		super(plugin, command, "group");
		
		this.info = new EPGroupInfo(this.plugin, this);
		new EPGroupCreate(this.plugin, this);
		new EPGroupRemove(this.plugin, this);
		new EPGroupDefault(this.plugin, this);
		new EPGroupList(this.plugin, this);
		new EPGroupRename(this.plugin, this);
		new EPGroupVerbose(this.plugin, this);
		
		new EPGroupPermission(this.plugin, this);
		new EPGroupOption(this.plugin, this);
		new EPGroupInheritance(this.plugin, this);
    }
	
	public EPGroupInfo getInfo() {
		return this.info;
	}
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_EXECUTE.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EPMessages.GROUP_DESCRIPTION.getText();
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return true;
	}
	
	public Text getButtonRename(final String groupName, final String worldName) {
		return EChat.of(groupName).toBuilder()
				.onClick(TextActions.suggestCommand("/" + this.getName() + " rename " + Args.MARKER_WORLD + " " + worldName  + " " + groupName))
				.onShiftClick(TextActions.insertText(groupName))
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
				.build();
	}
	
	public Text getButtonDefault(final String groupName, final Text valueText, final Boolean value, final String worldName) {
		return valueText.toBuilder()
				.onClick(TextActions.suggestCommand("/" + this.getName() + " default " + Args.MARKER_WORLD + " " + worldName  + " " + groupName + " " + value))
				.onShiftClick(TextActions.insertText(value.toString()))
				.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
				.build();
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
				.onClick(TextActions.runCommand("/" + this.getName() + " info " + Args.MARKER_WORLD + " \"" + worldName  + "\" \"" + name + "\""))
				.onShiftClick(TextActions.insertText(name))
				.onHover(TextActions.showText(EAMessages.MORE_INFORMATION.getText()))
				.build();
	}
}
