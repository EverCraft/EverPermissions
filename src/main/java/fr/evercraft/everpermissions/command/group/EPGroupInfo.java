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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupInfo extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	private final EPGroup parent;
	
	public EPGroupInfo(final EverPermissions plugin, final EPGroup parent) {
        super(plugin, parent, "info");
        
        this.parent = parent;
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllGroups(args.getWorld().getName()));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_INFO.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_INFO_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_GROUP.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) throws EMessageException {
		Args args = this.pattern.build(this.plugin, source, argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() != 1) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		return this.command(source, argsString.get(0), args.getWorld().getName());
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String groupName, final String worldName) {
		Optional<String> typeGroup = this.plugin.getService().getGroupSubjects().getTypeWorld(worldName);
		// Monde introuvable
		if (!typeGroup.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("{world}", worldName)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<EGroupSubject> group = this.plugin.getService().getGroupSubjects().get(groupName);
		// Groupe introuvable
		if (!group.isPresent() || !group.get().hasTypeWorld(typeGroup.get())) {
			EPMessages.GROUP_NOT_FOUND_WORLD.sender()
				.replace("{group}", group.get().getFriendlyIdentifier().orElse(groupName))
				.replace("{type}", typeGroup.get())
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		List<Text> list = new ArrayList<Text>();
		this.addIdentifier(list, group.get());
		this.addName(list, group.get(), worldName);
		this.addDefault(list, group.get(), worldName, typeGroup.get());
		this.addInheritances(list, group.get(), worldName, typeGroup.get());
		this.addPermissions(list, group.get(), worldName, typeGroup.get());
		this.addOptions(list, group.get(), worldName, typeGroup.get());
		this.addInheritancesTransient(list, group.get(), worldName, typeGroup.get());
		this.addPermissionsTransient(list, group.get(), worldName, typeGroup.get());
		this.addOptionsTransient(list, group.get(), worldName, typeGroup.get());
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.GROUP_INFO_TITLE.getFormat().toText(
					"{group}", group.get().getName(),
					"{type}", typeGroup.get())
				.toBuilder()
				.onClick(TextActions.runCommand("/" + this.getName() + " " + Args.MARKER_WORLD + " \"" + worldName  + "\" \"" + groupName + "\""))
				.build(), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
	
	private void addIdentifier(List<Text> list, EGroupSubject group) {
		list.add(EPMessages.GROUP_INFO_IDENTIFIER.getFormat()
				.toText("{identifier}", EChat.getButtomCopy(group.getIdentifier())));
	}
	
	private void addName(List<Text> list, EGroupSubject group, String worldName) {
		list.add(EPMessages.GROUP_INFO_NAME.getFormat()
				.toText("{name}", this.parent.getButtonRename(group.getName(), worldName)));
	}
	
	private void addDefault(List<Text> list, EGroupSubject group, String worldName, String typeGroup) {
		if (group.isDefault(typeGroup)) {
			list.add(EPMessages.GROUP_INFO_DEFAULT.getFormat()
					.toText("{value}", this.parent.getButtonDefault(group.getName(), EPMessages.GROUP_INFO_DEFAULT_TRUE.getText(), true, worldName)));
		} else {
			list.add(EPMessages.GROUP_INFO_DEFAULT.getFormat()
					.toText("{value}", this.parent.getButtonDefault(group.getName(), EPMessages.GROUP_INFO_DEFAULT_FALSE.getText(), false, worldName)));
		}
	}
	
	// La liste des inheritances
	private void addInheritances(List<Text> list, EGroupSubject group, String worldName, String typeGroup) {
		List<SubjectReference> groups = group.getSubjectData().getParents(typeGroup);
		if (groups.isEmpty()) {
			list.add(EPMessages.GROUP_INFO_INHERITANCE_EMPTY.getText());
		} else {
			list.add(EPMessages.GROUP_INFO_INHERITANCE.getText());
			for (SubjectReference inheritance : groups) {
				list.add(EPMessages.GROUP_INFO_INHERITANCE_LINE.getFormat()
						.toText("{inheritance}", this.parent.getButtonInfo(inheritance.resolve().join().getFriendlyIdentifier().orElse(inheritance.getSubjectIdentifier()), worldName)));
			}
		}
	}
	
	// La liste des inheritances temporaires
	private void addInheritancesTransient(List<Text> list, EGroupSubject group, String worldName, String typeGroup) {
		List<SubjectReference> groups = group.getTransientSubjectData().getParents(typeGroup);
		if (!groups.isEmpty()) {
			list.add(EPMessages.GROUP_INFO_INHERITANCE_TRANSIENT.getText());
			for (SubjectReference inheritance : groups) {
				list.add(EPMessages.GROUP_INFO_INHERITANCE_TRANSIENT_LINE.getFormat()
						.toText("{inheritance}", this.parent.getButtonInfo(inheritance.resolve().join().getFriendlyIdentifier().orElse(inheritance.getSubjectIdentifier()), worldName)));
			}
		}
	}
	
	// La liste des permissions
	private void addPermissions(List<Text> list, EGroupSubject group, String worldName, String typeGroup) {
		TreeMap<String, Boolean> permissions = new TreeMap<String, Boolean>(group.getSubjectData().getPermissions(typeGroup));
		if (permissions.isEmpty()) {
			list.add(EPMessages.GROUP_INFO_PERMISSION_EMPTY.getText());
		} else {
			list.add(EPMessages.GROUP_INFO_PERMISSION.getText());
			for (Entry<String, Boolean> permission : permissions.entrySet()) {
				if (permission.getValue()) {
					list.add(EPMessages.GROUP_INFO_PERMISSION_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(group.getName(), permission.getKey(), true, worldName),
									"{value}", this.parent.getButtonPermissionValue(group.getName(), permission.getKey(), EPMessages.GROUP_INFO_PERMISSION_TRUE.getText(), true, worldName)));
				} else {
					list.add(EPMessages.GROUP_INFO_PERMISSION_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(group.getName(), permission.getKey(), false, worldName),
									"{value}", this.parent.getButtonPermissionValue(group.getName(), permission.getKey(), EPMessages.GROUP_INFO_PERMISSION_FALSE.getText(), false, worldName)));
				}
			}
		}
	}
	
	// La liste des permissions temporaires
	private void addPermissionsTransient(List<Text> list, EGroupSubject group, String worldName, String typeGroup) {
		TreeMap<String, Boolean> permissions = new TreeMap<String, Boolean>(group.getTransientSubjectData().getPermissions(typeGroup));
		if (!permissions.isEmpty()) {
			list.add(EPMessages.GROUP_INFO_PERMISSION_TRANSIENT.getText());
			for (Entry<String, Boolean> permission : permissions.entrySet()) {
				if (permission.getValue()) {
					list.add(EPMessages.GROUP_INFO_PERMISSION_TRANSIENT_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(group.getName(), permission.getKey(), true, worldName),
									"{value}", this.parent.getButtonPermissionValue(group.getName(), permission.getKey(), EPMessages.GROUP_INFO_PERMISSION_TRANSIENT_TRUE.getText(), true, worldName)));
				} else {
					list.add(EPMessages.GROUP_INFO_PERMISSION_TRANSIENT_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(group.getName(), permission.getKey(), false, worldName),
									"{value}", this.parent.getButtonPermissionValue(group.getName(), permission.getKey(), EPMessages.GROUP_INFO_PERMISSION_TRANSIENT_FALSE.getText(), false, worldName)));
				}
			}
		}
	}
	
	// La liste des options
	private void addOptions(List<Text> list, EGroupSubject group, String worldName, String typeGroup) {
		TreeMap<String, String> options = new TreeMap<String, String>(group.getSubjectData().getOptions(typeGroup));
		if (options.isEmpty()) {
			list.add(EPMessages.GROUP_INFO_OPTION_EMPTY.getText());
		} else {
			list.add(EPMessages.GROUP_INFO_OPTION.getText());
			for (Entry<String, String> option : options.entrySet()) {
				list.add(EPMessages.GROUP_INFO_OPTION_LINE.getFormat().toText(
							"{option}", this.parent.getButtonOptionKey(group.getName(), option.getKey(), option.getValue(), worldName),
							"{value}", this.parent.getButtonOptionValue(group.getName(), option.getKey(), option.getValue(), worldName)));
			}
		}
	}
	
	// La liste des options temporaires
	private void addOptionsTransient(List<Text> list, EGroupSubject group, String worldName, String typeGroup) {
		TreeMap<String, String> options = new TreeMap<String, String>(group.getTransientSubjectData().getOptions(typeGroup));
		if (!options.isEmpty()) {
			list.add(EPMessages.GROUP_INFO_OPTION_TRANSIENT.getText());
			for (Entry<String, String> option : options.entrySet()) {
				list.add(EPMessages.GROUP_INFO_OPTION_TRANSIENT_LINE.getFormat().toText(
						"{option}", this.parent.getButtonOptionKey(group.getName(), option.getKey(), option.getValue(), worldName),
						"{value}", this.parent.getButtonOptionValue(group.getName(), option.getKey(), option.getValue(), worldName)));
			}
		}
	}
}
