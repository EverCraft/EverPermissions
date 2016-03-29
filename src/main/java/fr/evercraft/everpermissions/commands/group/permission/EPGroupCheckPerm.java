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
package fr.evercraft.everpermissions.commands.group.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsChat;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupCheckPerm extends ECommand<EverPermissions> {
	
	public EPGroupCheckPerm(final EverPermissions plugin) {
        super(plugin, "permgcheckp", "mangcheckp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("GROUP_CHECK_PERMISSION"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("GROUP_CHECK_PERMISSION_DESCRIPTION");
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permgcheckp <" + this.plugin.getEverAPI().getMessages().getArg("group") + "> "
									   + "<" + this.plugin.getEverAPI().getMessages().getArg("permission") + "> "
									   + "[" + this.plugin.getEverAPI().getMessages().getArg("world") + "]")
					.onClick(TextActions.suggestCommand("/permgcheckp "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(Subject subject : this.plugin.getService().getGroupSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
		} else if(args.size() == 2) {
			suggests.add("ever");
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
		// On connais le monde
		} else if(args.size() == 3) {
			resultat = command(source, args.get(0), args.get(1), args.get(2));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource player, final String group_name, final String permission, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if(type_group.isPresent()) {
			EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
			// Groupe existant
			if(group != null && group.hasWorld(type_group.get())) {
				Tristate value = group.getPermissionValue(EContextCalculator.getContextWorld(type_group.get()), permission);
				// Permission : True
				if(value.equals(Tristate.TRUE)) {
					player.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_CHECK_PERMISSION_TRUE")
							.replaceAll("<group>", group.getIdentifier())
							.replaceAll("<permission>", permission)
							.replaceAll("<type>", type_group.get())));
				// Permission : False
				} else if(value.equals(Tristate.FALSE)) {
					player.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_CHECK_PERMISSION_FALSE")
							.replaceAll("<group>", group.getIdentifier())
							.replaceAll("<permission>", permission)
							.replaceAll("<type>", type_group.get())));
				// Permission : Undefined
				} else {
					player.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_CHECK_PERMISSION_UNDEFINED")
							.replaceAll("<group>", group.getIdentifier())
							.replaceAll("<permission>", permission)
							.replaceAll("<type>", type_group.get())));
				}
			// Le groupe est introuvable
			} else {
				player.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getMessages().getMessage("GROUP_NOT_FOUND")
						.replaceAll("<group>", group_name)
						.replaceAll("<type>", type_group.get())));
			}
		// Le monde est introuvable
		} else {
			player.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("WORLD_NOT_FOUND")
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
