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
package fr.evercraft.everpermissions.command.sub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPDescription extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPDescription(final EverPermissions plugin, final EPCommand parent) {
        super(plugin, parent, "description");
        
        this.pattern = Args.builder()
        		.arg((source, args) -> this.getAllPermissions());
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.DESCRIPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.COLLECTION_TYPES_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PERMISSION.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) throws EMessageException {
		Args args = this.pattern.build(this.plugin, source, argsList);
		List<String> argsString = args.getArgs();
		
		if (argsString.size() > 1) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		if (argsString.size() == 1) {
			return this.command(source, argsString.get(0));
		} else {
			return this.command(source);
		}
	}
	
	private CompletableFuture<Boolean> command(final CommandSource player, final String start) throws EMessageException {
		List<Text> list = new ArrayList<Text>();
		
		this.plugin.getService().getDescriptions().stream()
			.filter(description -> description.getId().startsWith(start))
			.sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
			.forEachOrdered(permission -> {
				Text description = permission.getDescription().orElse(Text.of());
				
				list.add(EPMessages.DESCRIPTION_LINE.getFormat()
						.toText("{permission}", this.getButtonDescription(permission.getId(), description),
								"{description}", description));
			});
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.DESCRIPTION_TITLE_SEARCH.getFormat()
					.toText("{permission}", start)
					.toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName() + " \"" + start + "\""))
					.build(), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
	
	private CompletableFuture<Boolean> command(final CommandSource player) throws EMessageException {
		List<Text> list = new ArrayList<Text>();
		
		this.plugin.getService().getDescriptions().stream()
			.sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
			.forEachOrdered(permission -> {
				Text description = permission.getDescription().orElse(Text.of());
				
				list.add(EPMessages.DESCRIPTION_LINE.getFormat()
						.toText("{permission}", this.getButtonDescription(permission.getId(), description),
								"{description}", description));
			});
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.DESCRIPTION_TITLE.getText()
					.toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName()))
					.build(), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
	
	public Text getButtonDescription(final String permission, final Text description) {
		return EPMessages.DESCRIPTION_PERMISSION.getFormat().toText(
					"{permission}", permission,
					"{description}", description).toBuilder()
				.onClick(TextActions.suggestCommand(permission))
				.onShiftClick(TextActions.insertText(permission))
				.onHover(TextActions.showText(EPMessages.DESCRIPTION_PERMISSION_HOVER.getFormat().toText(
						"{permission}", permission,
						"{description}", description)))
				.build();
	}
}
