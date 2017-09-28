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
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.services.permission.EGroupSubject;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupList extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	private final EPGroup parent;
	
	public EPGroupList(final EverPermissions plugin, final EPGroup parent) {
        super(plugin, parent, "list");
        
        this.parent = parent;
        this.pattern = Args.builder()
        		.value(Args.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1);
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_LIST.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_LIST_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + Args.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) throws EMessageException {
		Args args = this.pattern.build(this.plugin, source, argsList);
		
		if (!args.getArgs().isEmpty()) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		return this.command(source, args.getWorld().getName());
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String worldName) throws EMessageException {
		String typeGroup = EPCommand.getTypeWorld(player, this.plugin.getService().getGroupSubjects(), worldName);
		Set<EGroupSubject> groups = this.plugin.getService().getGroupSubjects().getGroups(typeGroup);
		
		List<Text> list = new ArrayList<Text>();
		
		// Aucun groupe
		if (groups.isEmpty()) {
			list.add(EPMessages.GROUP_LIST_EMPTY.getText());
		// Les groupes
		} else {
			// Le groupe par défaut
			this.plugin.getService().getGroupSubjects().getDefaultGroup(typeGroup).ifPresent(subject -> {
				list.add(EPMessages.GROUP_LIST_DEFAULT.getFormat()
						.toText("{group}", this.parent.getButtonInfo(subject.getName(), worldName)));
			});
			
			// La liste des groupes
			list.add(EPMessages.GROUP_LIST_NAME.getText());
			
			TreeMap<String, Text> groupsText = new TreeMap<String, Text>();
			for (EGroupSubject group : groups) {
				groupsText.put(group.getName(), EPMessages.GROUP_LIST_LINE.getFormat()
						.toText("{group}", this.parent.getButtonInfo(group.getName(), worldName)));
			}
			list.addAll(groupsText.values());
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.GROUP_LIST_TITLE.getFormat()
					.toText("{type}", typeGroup)
					.toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName() + " " + Args.MARKER_WORLD + " \"" + worldName + "\""))
					.build(), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
}
