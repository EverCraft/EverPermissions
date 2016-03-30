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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EUserSubject;

public class EPUserListGroup extends ECommand<EverPermissions> {
	
	public EPUserListGroup(final EverPermissions plugin) {
        super(plugin, "permulist", "permulistg", "manwhois");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("USER_LIST_GROUP"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("USER_LIST_GROUP_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permulist <" + this.plugin.getEverAPI().getMessages().getArg("player") + "> "
									 + "[" + this.plugin.getEverAPI().getMessages().getArg("world") + "]")
					.onClick(TextActions.suggestCommand("/permulist "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests = null;
		} else if(args.size() == 2) {
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if(args.size() == 1) {
			Optional<User> optUser = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if(optUser.isPresent()){
				// Si la source est un joueur
				if(source instanceof EPlayer) {
					resultat = command(source, optUser.get(), ((EPlayer) source).getWorld().getName());
				// La source n'est pas un joueur
				} else {
					resultat = command(source, optUser.get(), this.plugin.getGame().getServer().getDefaultWorldName());
				}
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
			}
		// On connait le monde
		} else if(args.size() == 2) {
			Optional<User> optPlayer = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if(optPlayer.isPresent()){
				resultat = command(source, optPlayer.get(), args.get(1));
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final User player, final String world_name) {
		Optional<String> type_user = this.plugin.getManagerData().getTypeUser(world_name);
		this.plugin.getLogger().warn("Debug : typeuser" + type_user.get());
		// Monde existant
		if(type_user.isPresent()) {
			Set<Context> contexts = EContextCalculator.getContextWorld(world_name);
			EUserSubject user = this.plugin.getService().getUserSubjects().get(player.getIdentifier());
			// Joueur existant
			if(user != null) {
				List<Text> list = new ArrayList<Text>();
				
				// Le groupe
				Optional<Subject> group = user.getSubjectData().getParent(contexts);
				if(group.isPresent()) {
					list.add(EChat.of(this.plugin.getMessages().getMessage("USER_LIST_GROUP_GROUP").replaceAll("<group>", group.get().getIdentifier())));
				} else {
					list.add(this.plugin.getMessages().getText("USER_LIST_GROUP_GROUP_EMPTY"));
				}
				
				// Les sous-groupes
				List<Subject> groups = user.getSubjectData().getSubParents(contexts);
				if(groups.isEmpty()) {
					list.add(this.plugin.getMessages().getText("USER_LIST_GROUP_SUBGROUP_EMPTY"));
				} else {
					list.add(this.plugin.getMessages().getText("USER_LIST_GROUP_SUBGROUP"));
					for(Subject subject : groups) {
						list.add(EChat.of(this.plugin.getMessages().getMessage("USER_LIST_GROUP_SUBGROUP_LINE").replaceAll("<group>", subject.getIdentifier())));
					}
				}
				
				// Les groupes temporaires
				groups = user.getTransientSubjectData().getParents(contexts);
				if(!groups.isEmpty()) {
					list.add(this.plugin.getMessages().getText("USER_LIST_GROUP_TRANSIENT"));
					for(Subject subject : groups) {
						list.add(EChat.of(this.plugin.getMessages().getMessage("USER_LIST_GROUP_TRANSIENT_LINE").replaceAll("<group>", subject.getIdentifier())));
					}
				}
				
				this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(
						this.plugin.getMessages().getMessage("USER_LIST_GROUP_TITLE")
						.replaceAll("<player>", player.getName())
						.replaceAll("<type>", type_user.get())), 
						list, staff);
				return true;
			// Le joueur n'existe pas dans le service de permissions
			} else {
				staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("COMMAND_ERROR")));
			}
		// Le monde est introuvable
		} else {
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("WORLD_NOT_FOUND")
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
