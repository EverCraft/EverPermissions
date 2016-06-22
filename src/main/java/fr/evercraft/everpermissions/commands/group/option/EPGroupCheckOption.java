package fr.evercraft.everpermissions.commands.group.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPGroupCheckOption extends ECommand<EverPermissions> {
	
	public EPGroupCheckOption(final EverPermissions plugin) {
        super(plugin, "permgchecko", "mangcheckv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.GROUP_CHECK_OPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.GROUP_CHECK_OPTION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permgchecko <" + EAMessages.ARGS_GROUP.get() + "> "
									   + "<" + EAMessages.ARGS_OPTION.get() + "> "
									   + "[" + EAMessages.ARGS_WORLD.get() + "]")
					.onClick(TextActions.suggestCommand("/permgchecko "))
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
			suggests.add("prefix");
			suggests.add("suffix");
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
	
	private boolean command(final CommandSource player, final String group_name, final String option, final String world_name) {
		Optional<String> type_group = this.plugin.getManagerData().getTypeGroup(world_name);
		// Monde existant
		if(type_group.isPresent()) {
			EGroupSubject group = this.plugin.getService().getGroupSubjects().get(group_name);
			// Groupe existant
			if(group != null && group.hasWorld(type_group.get())) {
				Set<Context> contexts = EContextCalculator.getContextWorld(type_group.get());
				String name = group.getSubjectData().getOptions(contexts).get(option);
				// Si il y a une valeur
				if(name != null) {
					player.sendMessage(ETextBuilder.toBuilder(EPMessages.PREFIX.getText())
							.append(EPMessages.GROUP_CHECK_OPTION_DEFINED.get()
								.replaceAll("<group>", group.getIdentifier())
								.replaceAll("<option>", option)
								.replaceAll("<type>", type_group.get()))
							.replace("<value>", Text.builder(name)
								.color(EChat.getTextColor(EPMessages.GROUP_CHECK_OPTION_DEFINED_NAME_COLOR.get()))
								.build())
							.build());
					return true;
				// Il n'y a pas de valeur
				} else {
					player.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.GROUP_CHECK_OPTION_UNDEFINED.get()
							.replaceAll("<group>", group.getIdentifier())
							.replaceAll("<option>", option)
							.replaceAll("<type>", type_group.get())));
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
