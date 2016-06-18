/**
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
package fr.evercraft.everpermissions.commands.group.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.java.UtilsBoolean;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupDefaultGroup extends ECommand<EverPermissions> {
	
	public EPGroupDefaultGroup(final EverPermissions plugin) {
        super(plugin, "permgdefault", "mangdefault");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_DEFAULT_GROUP.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_DEFAULT_GROUP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permgdefault <" + EAMessages.ARGS_GROUP.get() + "> <true|false> [" + EAMessages.ARGS_WORLD.get() + "]")
				.onClick(TextActions.suggestCommand("/permgdefault "))
				.color(TextColors.RED)
				.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(Subject subject : this.plugin.getService().getGroupSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
		} else if(args.size() == 2) {
			suggests.add("true");
			suggests.add("false");
		} else if(args.size() == 3) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if(args.size() == 2) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = command(source, args.get(0), args.get(1), ((EPlayer) source).getWorld().getName());
			// La source n'est pas un joueur
			} else {
				resultat = command(source, args.get(0), args.get(1), this.plugin.getGame().getServer().getDefaultWorldName());
			}
		// On connais le joueur
		} else if(args.size() == 3) {
			resultat = command(source, args.get(0), args.get(1), args.get(2));
		// Nombre d'argument monde
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource player, final String group_name, final String value_name, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if(type_group.isPresent()) {
			EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
			// Groupe existant
			if(group != null && group.hasWorld(type_group.get())) {
				Optional<Boolean> value = UtilsBoolean.parseBoolean(value_name);
				// La value est un boolean
				if(value.isPresent()) {
					if(value.get()) {
						// Le groupe a bien été mit par défaut
						if(this.plugin.getService().getGroupSubjects().registerDefault(group, type_group.get())) {
							player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_DEFAULT_GROUP_TRUE.get()
									.replaceAll("<group>", group_name)
									.replaceAll("<type>", type_group.get())));
							this.plugin.getService().getUserSubjects().reload();
							return true;
						// Le groupe n'a pas été mit par défaut
						} else {
							Optional<EGroupSubject> group_default = this.plugin.getService().getGroupSubjects().getDefaultGroup(type_group.get());
							// C'est déjà le groupe par défaut
							if(group_default.isPresent() && group_default.get().equals(group)) {
								player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_DEFAULT_GROUP_ERROR_EQUALS.get()
										.replaceAll("<group>", group_name)
										.replaceAll("<type>", type_group.get())));
							// Il y a déja un autre groupe par défaut
							} else {
								player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_DEFAULT_GROUP_ERROR_TRUE.get()
										.replaceAll("<group>", group_name)
										.replaceAll("<type>", type_group.get())));
							}
						}
					} else {
						// Le groupe n'est plus un groupe par défaut
						if(this.plugin.getService().getGroupSubjects().removeDefault(group, type_group.get())) {
							player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_DEFAULT_GROUP_FALSE.get()
									.replaceAll("<group>", group_name)
									.replaceAll("<type>", type_group.get())));
							this.plugin.getService().getUserSubjects().reload();
							return true;
						// Le groupe n'a pas un groupe par défaut
						} else {
							player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_DEFAULT_GROUP_ERROR_FALSE.get()
									.replaceAll("<group>", group_name)
									.replaceAll("<type>", type_group.get())));
						}
					}
				// La value n'est pas un boolean
				} else {
					player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_DEFAULT_GROUP_ERROR_BOOLEAN.get()));
				}
			// Le groupe est introuvable
			} else {
				player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_NOT_FOUND.get()
						.replaceAll("<group>", group_name)
						.replaceAll("<type>", type_group.get())));
			}
		// Le monde est introuvable
		} else {
			player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
