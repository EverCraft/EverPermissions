package fr.evercraft.everpermissions.commands.group.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupListGroup extends ECommand<EverPermissions> {
	
	public EPGroupListGroup(final EverPermissions plugin) {
        super(plugin, "permglist", "permglistg", "manglist");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_LIST_GROUP.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_LIST_GROUP_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permglist [" + EAMessages.ARGS_WORLD.get() + "]").onClick(TextActions.suggestCommand("/permglist "))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		// Si on ne connait pas le monde
		if(args.size() == 0) {
			// Si la source est un joueur
			if(source instanceof EPlayer) {
				resultat = command(source, ((EPlayer) source).getWorld().getName());
			// La source n'est pas un joueur
			} else {
				resultat = command(source, this.plugin.getGame().getServer().getDefaultWorldName());
			}
		// On connait le monde
		} else if(args.size() == 1) {
			resultat = command(source, args.get(0));
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource player, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if(type_group.isPresent()) {
			List<Text> list = new ArrayList<Text>();
			Set<EGroupSubject> groups = this.plugin.getService().getGroupSubjects().getGroups(type_group.get());
			
			// Aucun groupe
			if(groups.isEmpty()) {
				list.add(EPMessages.GROUP_LIST_GROUP_EMPTY.getText());
			// Les groupes
			} else {
				// Le groupe par défaut
				Optional<EGroupSubject> subject = this.plugin.getService().getGroupSubjects().getDefaultGroup(type_group.get());
				if(subject.isPresent()) {
					list.add(EChat.of(EPMessages.GROUP_LIST_GROUP_DEFAULT.get()
							.replaceAll("<group>", subject.get().getIdentifier())));
				}
				
				// La liste des groupes
				list.add(EPMessages.GROUP_LIST_GROUP_NAME.getText());
				for (EGroupSubject group : groups) {
					list.add(EChat.of(EPMessages.GROUP_LIST_GROUP_LINE.get()
							.replaceAll("<group>", group.getIdentifier())));
				}
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(
					EPMessages.GROUP_LIST_GROUP_TITLE.get()
					.replaceAll("<type>", type_group.get())), 
					list, player);
			return true;
		// Le monde est introuvable
		} else {
			player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.WORLD_NOT_FOUND.get()
					.replaceAll("<world>", world_name)));
		}
		return false;
	}
}
