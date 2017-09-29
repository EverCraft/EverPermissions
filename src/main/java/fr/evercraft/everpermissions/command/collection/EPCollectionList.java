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
package fr.evercraft.everpermissions.command.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPCollectionList extends ESubCommand<EverPermissions> {
	
	public EPCollectionList(final EverPermissions plugin, final EPCollection parent) {
        super(plugin, parent, "list");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.COLLECTION_LIST.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.COLLECTION_LIST_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName() + " "))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> argsList) throws EMessageException {
		if (!argsList.isEmpty()) {
			source.sendMessage(this.help(source));
			return CompletableFuture.completedFuture(false);
		}
		
		return this.command(source);
	}

	private CompletableFuture<Boolean> command(final CommandSource player) throws EMessageException {
		List<String> collections = this.plugin.getService().getLoadedCollections().values().stream()
				.map(collection -> collection.getIdentifier())
				.sorted()
				.collect(Collectors.toList());
		
		List<Text> list = new ArrayList<Text>();
			
		for (String collection : collections) {
			list.add(EPMessages.COLLECTION_LIST_LINE.getFormat()
					.toText("{collection}", EChat.getButtomCopy(collection)));
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.COLLECTION_LIST_TITLE.getText()
					.toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName()))
					.build(), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
}
