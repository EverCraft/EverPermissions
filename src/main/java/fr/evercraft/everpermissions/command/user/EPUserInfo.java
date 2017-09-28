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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.java.UtilsCompletableFuture;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EPUserSubject;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPUserInfo extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	private final EPUser parent;
	
	public EPUserInfo(final EverPermissions plugin, final EPUser parent) {
        super(plugin, parent, "info");
        
        this.parent = parent;
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1)
        		.arg((source, args) -> this.getAllUsers(args.getArg(0).orElse("")));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_INFO.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_INFO_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]"
												 + " <" + EAMessages.ARGS_USER.getString() + ">")
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
		
		World world = args.getWorld();
		String typeUser = EPCommand.getTypeWorld(source, this.plugin.getService().getUserSubjects(), world.getName());
		
		return UtilsCompletableFuture.anyOf(args.getArg(0, Args.USERS).orElse(Arrays.asList()).stream()
			.map(user -> this.command(source, user, world.getName(), typeUser))
			.collect(Collectors.toList()));
	}
	
	private CompletableFuture<Boolean> command(final CommandSource staff, final EUser user, final String worldName, final String typeUser) {
		return this.plugin.getService().getUserSubjects().load(user.getIdentifier())
			.exceptionally(e -> null)
			.thenApply(subject -> {
				if (subject == null) {
					EAMessages.COMMAND_ERROR.sender()
						.prefix(EPMessages.PREFIX)
						.sendTo(staff);
					return false;
				}
				
				this.command(staff, user, subject, worldName, typeUser);
				return true;
			});
	}

	private void command(final CommandSource player, final EUser user, final EPUserSubject subject, final String worldName, final String typeUser) {
		List<Text> list = new ArrayList<Text>();
		this.addGroup(list, user, subject, worldName, typeUser);
		this.addSubGroups(list, user, subject, worldName, typeUser);
		this.addPermissions(list, user, subject, worldName, typeUser);
		this.addOptions(list, user, subject, worldName, typeUser);
		this.addGroupTransient(list, user, subject, worldName, typeUser);
		this.addSubGroupsTransient(list, user, subject, worldName, typeUser);
		this.addPermissionsTransient(list, user, subject, worldName, typeUser);
		this.addOptionsTransient(list, user, subject, worldName, typeUser);
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.USER_INFO_TITLE.getFormat().toText(
					"{player}", user.getName(),
					"{type}", typeUser)
				.toBuilder()
				.onClick(TextActions.runCommand("/" + this.getName() + " " + Args.MARKER_WORLD + " \"" + worldName  + "\" \"" + subject.getIdentifier() + "\""))
				.build(), 
				list, player);
	}
	
	// Le groupe
	public void addGroup(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		Optional<SubjectReference> group = subject.getSubjectData().getGroup(typeUser);
		if (!group.isPresent()) {
			list.add(EPMessages.USER_INFO_GROUP_EMPTY.getText());
		} else {
			list.add(EPMessages.USER_INFO_GROUP.getFormat()
					.toText("{group}", this.parent.getButtonInfo(group.get().resolve().join().getFriendlyIdentifier().orElse(group.get().getSubjectIdentifier()), worldName)));
		}
	}
	
	// La liste des sous-groupes
	public void addSubGroups(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		List<SubjectReference> groups = subject.getSubjectData().getSubGroup(typeUser);
		if (groups.isEmpty()) {
			list.add(EPMessages.USER_INFO_SUBGROUP_EMPTY.getText());
		} else {
			list.add(EPMessages.USER_INFO_SUBGROUP.getText());
			for (SubjectReference inheritance : groups) {
				list.add(EPMessages.USER_INFO_SUBGROUP_LINE.getFormat()
						.toText("{subgroup}", this.parent.getButtonInfo(inheritance.resolve().join().getFriendlyIdentifier().orElse(inheritance.getSubjectIdentifier()), worldName)));
			}
		}
	}
	
	// Le groupe
	public void addGroupTransient(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		Optional<SubjectReference> group = subject.getTransientSubjectData().getGroup(typeUser);
		if (group.isPresent()) {
			list.add(EPMessages.USER_INFO_GROUP_TRANSIENT.getFormat()
					.toText("{group}", this.parent.getButtonInfo(group.get().resolve().join().getFriendlyIdentifier().orElse(group.get().getSubjectIdentifier()), worldName)));
		}
	}
	
	// La liste des sous-groupes temporaires
	public void addSubGroupsTransient(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		List<SubjectReference> groups = subject.getTransientSubjectData().getSubGroup(typeUser);
		if (!groups.isEmpty()) {
			list.add(EPMessages.USER_INFO_SUBGROUP_TRANSIENT.getText());
			for (SubjectReference inheritance : groups) {
				list.add(EPMessages.USER_INFO_SUBGROUP_TRANSIENT_LINE.getFormat()
						.toText("{subgroup}", this.parent.getButtonInfo(inheritance.resolve().join().getFriendlyIdentifier().orElse(inheritance.getSubjectIdentifier()), worldName)));
			}
		}
	}
	
	// La liste des permissions
	public void addPermissions(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		TreeMap<String, Boolean> permissions = new TreeMap<String, Boolean>(subject.getSubjectData().getPermissions(typeUser));
		if (permissions.isEmpty()) {
			list.add(EPMessages.USER_INFO_PERMISSION_EMPTY.getText());
		} else {
			list.add(EPMessages.USER_INFO_PERMISSION.getText());
			for (Entry<String, Boolean> permission : permissions.entrySet()) {
				if (permission.getValue()) {
					list.add(EPMessages.USER_INFO_PERMISSION_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(user.getName(), permission.getKey(), true, worldName),
									"{value}", this.parent.getButtonPermissionValue(user.getName(), permission.getKey(), EPMessages.USER_INFO_PERMISSION_TRUE.getText(), true, worldName)));
				} else {
					list.add(EPMessages.USER_INFO_PERMISSION_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(user.getName(), permission.getKey(), false, worldName),
									"{value}", this.parent.getButtonPermissionValue(user.getName(), permission.getKey(), EPMessages.USER_INFO_PERMISSION_FALSE.getText(), false, worldName)));
				}
			}
		}
	}
	
	// La liste des permissions temporaires
	public void addPermissionsTransient(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		TreeMap<String, Boolean> permissions = new TreeMap<String, Boolean>(subject.getTransientSubjectData().getPermissions(typeUser));
		if (!permissions.isEmpty()) {
			list.add(EPMessages.USER_INFO_PERMISSION_TRANSIENT.getText());
			for (Entry<String, Boolean> permission : permissions.entrySet()) {
				if (permission.getValue()) {
					list.add(EPMessages.USER_INFO_PERMISSION_TRANSIENT_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(user.getName(), permission.getKey(), true, worldName),
									"{value}", this.parent.getButtonPermissionValue(user.getName(), permission.getKey(), EPMessages.USER_INFO_PERMISSION_TRANSIENT_TRUE.getText(), true, worldName)));
				} else {
					list.add(EPMessages.USER_INFO_PERMISSION_TRANSIENT_LINE.getFormat()
							.toText("{permission}", this.parent.getButtonPermissionKey(user.getName(), permission.getKey(), false, worldName),
									"{value}", this.parent.getButtonPermissionValue(user.getName(), permission.getKey(), EPMessages.USER_INFO_PERMISSION_TRANSIENT_FALSE.getText(), false, worldName)));
				}
			}
		}
	}
	
	// La liste des options
	public void addOptions(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		TreeMap<String, String> options = new TreeMap<String, String>(subject.getSubjectData().getOptions(typeUser));
		if (options.isEmpty()) {
			list.add(EPMessages.USER_INFO_OPTION_EMPTY.getText());
		} else {
			list.add(EPMessages.USER_INFO_OPTION.getText());
			for (Entry<String, String> option : options.entrySet()) {
				list.add(EPMessages.USER_INFO_OPTION_LINE.getFormat().toText(
							"{option}", this.parent.getButtonOptionKey(user.getName(), option.getKey(), option.getValue(), worldName),
							"{value}", this.parent.getButtonOptionValue(user.getName(), option.getKey(), option.getValue(), worldName)));
			}
		}
	}
	
	// La liste des options temporaires
	public void addOptionsTransient(List<Text> list, EUser user, EPUserSubject subject, String worldName, String typeUser) {
		TreeMap<String, String> options = new TreeMap<String, String>(subject.getTransientSubjectData().getOptions(typeUser));
		if (!options.isEmpty()) {
			list.add(EPMessages.USER_INFO_OPTION_TRANSIENT.getText());
			for (Entry<String, String> option : options.entrySet()) {
				list.add(EPMessages.USER_INFO_OPTION_TRANSIENT_LINE.getFormat().toText(
						"{option}", this.parent.getButtonOptionKey(user.getName(), option.getKey(), option.getValue(), worldName),
						"{value}", this.parent.getButtonOptionValue(user.getName(), option.getKey(), option.getValue(), worldName)));
			}
		}
	}
}
