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
package fr.evercraft.everpermissions.command.user.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserListOption extends ECommand<EverPermissions> {
	
	public EPUserListOption(final EverPermissions plugin) {
        super(plugin, "permulisto", "manulistv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.USER_LIST_OPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.USER_LIST_OPTION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permulisto <" + EAMessages.ARGS_PLAYER.get() + "> "
									  + "[" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/permulisto "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests = null;
		} else if (args.size() == 2) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if (args.size() == 1) {
			Optional<User> optUser = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if (optUser.isPresent()){
				// Si la source est un joueur
				if (source instanceof EPlayer) {
					resultat = command(source, optUser.get(), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					resultat = command(source, optUser.get(), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
			}
		// On connait le monde
		} else if (args.size() == 2) {
			Optional<User> optPlayer = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if (optPlayer.isPresent()){
				resultat = command(source, optPlayer.get(), args.get(1));
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final User player, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		// Monde existant
		if (type_user.isPresent()) {
			Set<Context> contexts = EContextCalculator.getContextWorld(world_name);
			EUserSubject user = this.plugin.getService().getUserSubjects().get(player.getIdentifier());
			// Joueur existant
			if (user != null) {
				List<Text> list = new ArrayList<Text>();
				
				// La liste des options
				Map<String, String> options = user.getSubjectData().getOptions(contexts);
				if (options.isEmpty()) {
					list.add(EPMessages.USER_LIST_OPTION_OPTION_EMPTY.getText());
				} else {
					list.add(EPMessages.USER_LIST_OPTION_OPTION.getText());
					for (Entry<String, String> permission : options.entrySet()) {
						list.add(ETextBuilder.toBuilder(EPMessages.USER_LIST_OPTION_OPTION_LINE.get()
									.replaceAll("<option>", permission.getKey()))
								.replace("<value>", Text.builder(permission.getValue())
									.color(EChat.getTextColor(EPMessages.USER_LIST_OPTION_OPTION_NAME_COLOR.get()))
									.build())
								.build());
					}
				}
				
				// La liste des options temporaires
				options = user.getTransientSubjectData().getOptions(contexts);
				if (!options.isEmpty()) {
					list.add(EPMessages.USER_LIST_OPTION_TRANSIENT.getText());
					for (Entry<String, String> permission : options.entrySet()) {
						list.add(ETextBuilder.toBuilder(EPMessages.USER_LIST_OPTION_TRANSIENT_LINE.get()
									.replaceAll("<option>", permission.getKey()))
								.replace("<value>", Text.builder(permission.getValue())
									.color(EChat.getTextColor(EPMessages.USER_LIST_OPTION_TRANSIENT_NAME_COLOR.get()))
									.build())
								.build());
					}
				}
				
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(
						EPMessages.USER_LIST_OPTION_TITLE.get()
						.replaceAll("<player>", player.getName())
						.replaceAll("<type>", type_user.get())), 
						list, staff);
				return true;
			// Le joueur n'existe pas dans le service de permissions
			} else {
				staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
			}
		// Le monde est introuvable
		} else {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
