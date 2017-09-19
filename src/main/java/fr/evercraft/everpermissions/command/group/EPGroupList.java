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
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Locatable;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPGroupList extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPGroupList(final EverPermissions plugin, final EPGroup command) {
        super(plugin, command, "list");
        
        this.pattern = Args.builder()
        		.value(EPCommand.MARKER_WORLD, 
    					(source, args) -> this.plugin.getService().getGroupSubjects().getTypeWorlds(),
    					(source, args) -> args.getArgs().size() <= 1);
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_LIST.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_LIST_GROUP_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " [" + EPCommand.MARKER_WORLD + " " + EAMessages.ARGS_WORLD.getString() + "]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) {
		Args args = this.pattern.build(argsList);
		
		if (!args.getArgs().isEmpty()) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		Optional<String> world = args.getValue(EPCommand.MARKER_WORLD);
		if (world.isPresent()) {
			return this.command(source, world.get());
		} else {
			if (source instanceof Locatable) {
				return this.command(source, ((Locatable) source).getWorld().getName());
			} else {
				return this.command(source, this.plugin.getGame().getServer().getDefaultWorldName());
			}
		}
	}

	private CompletableFuture<Boolean> command(final CommandSource player, final String worldName) {
		Optional<String> typeGroup = this.plugin.getService().getGroupSubjects().getTypeWorld(worldName);
		// Monde introuvable
		if (!typeGroup.isPresent()) {
			EAMessages.WORLD_NOT_FOUND.sender()
				.prefix(EPMessages.PREFIX)
				.replace("{world}", worldName)
				.sendTo(player);
			return CompletableFuture.completedFuture(false);
		}
		
		List<Text> list = new ArrayList<Text>();
		Set<EGroupSubject> groups = this.plugin.getService().getGroupSubjects().getGroups(typeGroup.get());
		
		// Aucun groupe
		if (groups.isEmpty()) {
			list.add(EPMessages.GROUP_LIST_GROUP_EMPTY.getText());
		// Les groupes
		} else {
			// Le groupe par défaut
			this.plugin.getService().getGroupSubjects().getDefaultGroup(typeGroup.get()).ifPresent(subject -> {
				list.add(EPMessages.GROUP_LIST_GROUP_DEFAULT.getFormat()
						.toText("{group}", subject.getName())
						.toBuilder()
						.onClick(TextActions.suggestCommand("/" + this.getParentName() + " info \"" + subject.getName() + "\""))
						.onShiftClick(TextActions.insertText(subject.getName()))
						.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
						.build());
			});
			
			// La liste des groupes
			list.add(EPMessages.GROUP_LIST_GROUP_NAME.getText());
			
			TreeMap<String, Text> groupsText = new TreeMap<String, Text>();
			for (EGroupSubject group : groups) {
				groupsText.put(group.getName(), EPMessages.GROUP_LIST_GROUP_LINE.getFormat()
						.toText("{group}", group.getName())
						.toBuilder()
						.onClick(TextActions.suggestCommand("/" + this.getParentName() + " info \"" + group.getName() + "\""))
						.onShiftClick(TextActions.insertText(group.getName()))
						.onHover(TextActions.showText(EAMessages.HOVER_COPY.getText()))
						.build());
			}
			list.addAll(groupsText.values());
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.GROUP_LIST_GROUP_TITLE.getFormat()
					.toText("{type}", typeGroup.get())
					.toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName() + " " + EPCommand.MARKER_WORLD + " \"" + worldName + "\""))
					.build(), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
}
