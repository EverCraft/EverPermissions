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
package fr.evercraft.everpermissions.commands.user.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsChat;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserPromoteGroup extends ECommand<EverPermissions> {
	
	public EPUserPromoteGroup(final EverPermissions plugin) {
        super(plugin, "permupromote", "manpromote");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("USER_PROMOTE_GROUP"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("USER_PROMOTE_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permupromote <" + this.plugin.getEverAPI().getMessages().getArg("player") + "> "
										+ "<" + this.plugin.getEverAPI().getMessages().getArg("group") + "> "
										+ "[" + this.plugin.getEverAPI().getMessages().getArg("world") + "]")
					.onClick(TextActions.suggestCommand("/permupromote "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests = null;
		} else if(args.size() == 2) {
			for(Subject subject : this.plugin.getService().getGroupSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
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
			Optional<User> optUser = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if(optUser.isPresent()){
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = command(source, optUser.get(), args.get(1), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					resultat = command(source, optUser.get(), args.get(1), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				source.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
			}
		// On connait le monde
		} else if(args.size() == 3) {
			Optional<User> optPlayer = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = command(source, optPlayer.get(), args.get(1), args.get(2));
			// Le joueur est introuvable
			} else {
				source.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final User user, final String group_name, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if(type_user.isPresent() && type_group.isPresent()) {
			EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
			// Groupe existant
			if(group != null && group.hasWorld(type_group.get())) {
				EUserSubject subject = this.plugin.getService().getUserSubjects().get(user.getIdentifier());
				// Joueur existant
				if(subject != null) {
					Set<Context> contexts = EContextCalculator.getContextWorld(type_user.get());
					Optional<Subject> parent = subject.getSubjectData().getParent(contexts);
					// Le groupe du joueur est différent du nouveau groupe
					if(!(parent.isPresent() && parent.get().equals(group))) {
						// Le nouveau groupe n'est pas n'est pas un fils du groupe du joueur
						if(!parent.isPresent() || !parent.get().isChildOf(EContextCalculator.getContextWorld(type_group.get()), group)) {
							// Le groupe a bien changé
							if(user.getSubjectData().addParent(contexts, group)) {
								if(staff.equals(user)) {
									staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("USER_PROMOTE_EQUALS")
											.replaceAll("<player>", user.getName())
											.replaceAll("<group>", group.getIdentifier())
											.replaceAll("<type>", type_user.get())));
								} else {
									staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("USER_PROMOTE_STAFF")
											.replaceAll("<player>", user.getName())
											.replaceAll("<group>", group.getIdentifier())
											.replaceAll("<type>", type_user.get())));
									// Le joueur est connecté
									Optional<Player> player = user.getPlayer();
									if(player.isPresent()) {
										player.get().sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("USER_PROMOTE_PLAYER")
												.replaceAll("<staff>", staff.getName())
												.replaceAll("<group>", group.getIdentifier())
												.replaceAll("<type>", type_user.get())));
									}
								}
								return true;
							// Le groupe n'a pas été changé
							} else {
								staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("COMMAND_ERROR")));
							}
						// Le groupe est inférieur au groupe actuelle du joueur
						} else {
							if(staff.equals(user)) {
								staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("USER_PROMOTE_ERROR_DEMOTE_EQUALS")
										.replaceAll("<player>", user.getName())
										.replaceAll("<group>", group.getIdentifier())
										.replaceAll("<type>", type_user.get())));
							} else {
								staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("USER_PROMOTE_ERROR_DEMOTE_STAFF")
										.replaceAll("<player>", user.getName())
										.replaceAll("<group>", group.getIdentifier())
										.replaceAll("<type>", type_user.get())));
							}
						}
					// Le groupe du joueur est égale au nouveau groupe
					} else {
						if(staff.equals(user)) {
							staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("USER_PROMOTE_ERROR_EQUALS")
									.replaceAll("<player>", user.getName())
									.replaceAll("<group>", group.getIdentifier())
									.replaceAll("<type>", type_user.get())));
						} else {
							staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("USER_PROMOTE_ERROR_STAFF")
									.replaceAll("<player>", user.getName())
									.replaceAll("<group>", group.getIdentifier())
									.replaceAll("<type>", type_user.get())));
						}
					}
				// Le joueur n'existe pas dans le service de permissions
				} else {
					staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
				}
			// Le groupe est introuvable
			} else {
				staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_NOT_FOUND")
						.replaceAll("<group>", group_name)
						.replaceAll("<type>", type_user.get())));
			}
		// Le monde est introuvable
		} else {
			staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("WORLD_NOT_FOUND")
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
