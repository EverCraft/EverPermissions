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
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.exception.message.EMessageException;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.Args;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.services.permission.EUserCollection;
import fr.evercraft.everpermissions.EPCommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPCollectionTypes extends ESubCommand<EverPermissions> {
	
	private final Args.Builder pattern;
	
	public EPCollectionTypes(final EverPermissions plugin, final EPCollection parent) {
        super(plugin, parent, "types");
        
        this.pattern = Args.builder()
        		.arg((source, args) -> EPCommand.getAllCollections(this.plugin.getService()));
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.COLLECTION_TYPES.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.COLLECTION_TYPES_DESCRIPTION.getText();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return this.pattern.suggest(this.plugin, source, args);
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_COLLECTION.getString() + ">")
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
		
		EUserCollection collection = EPCommand.getCollection(source, this.plugin.getService(), argsString.get(0));
		return this.command(source, collection);
	}
	
	private CompletableFuture<Boolean> command(final CommandSource player, final EUserCollection collection) throws EMessageException {
		TreeMap<String, String> types = new TreeMap<String, String>(collection.getTypeWorlds());
		
		List<Text> list = new ArrayList<Text>();
		types.forEach((world, type) -> {
			list.add(EPMessages.COLLECTION_TYPES_LINE.getFormat()
					.toText("{world}", EChat.getButtomCopy(world),
							"{type}", EChat.getButtomCopy(type)));
		});
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EPMessages.COLLECTION_TYPES_TITLE.getFormat()
					.toText("{collection}", collection.getIdentifier())
					.toBuilder()
					.onClick(TextActions.runCommand("/" + this.getName()))
					.build(), 
				list, player);
		return CompletableFuture.completedFuture(true);
	}
}
