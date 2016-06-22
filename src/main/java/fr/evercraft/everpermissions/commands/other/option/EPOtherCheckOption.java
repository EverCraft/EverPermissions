package fr.evercraft.everpermissions.commands.other.option;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherCheckOption extends ECommand<EverPermissions> {
	
	public EPOtherCheckOption(final EverPermissions plugin) {
        super(plugin, "permochecko", "manocheckv");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.OTHER_CHECK_OPTION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.OTHER_CHECK_OPTION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permochecko <" + EAMessages.ARGS_SUBJECT.get() + "> "
									   + "<" + EAMessages.ARGS_OPTION.get() + ">")
					.onClick(TextActions.suggestCommand("/permochecko "))
					.color(TextColors.RED)
					.build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			for(Subject subject : this.plugin.getService().getSytemSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
			for(Subject subject : this.plugin.getService().getCommandBlockSubjects().getAllSubjects()) {
				suggests.add(subject.getIdentifier());
			}
		} else if(args.size() == 2) {
			suggests.add("prefix");
			suggests.add("suffix");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 2) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le subject existe
			if(optSubject.isPresent()){
				resultat = command(source, optSubject.get(), args.get(1));
			// Le subject est introuvable
			} else {
				source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_NOT_FOUND.get()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final EOtherSubject subject, final String type) {
		String name = subject.getSubjectData().getOptions(new HashSet<Context>()).get(type);
		// Il n'y a aucune valeur
		if(name != null) {
			staff.sendMessage(ETextBuilder.toBuilder(EPMessages.PREFIX.getText())
					.append(EPMessages.OTHER_CHECK_OPTION_DEFINED.get()
						.replaceAll("<subject>", subject.getIdentifier())
						.replaceAll("<option>", type))
					.replace("<value>", Text.builder(name)
						.color(EChat.getTextColor(EPMessages.OTHER_CHECK_OPTION_DEFINED_NAME_COLOR.get()))
						.build())
					.build());
			return true;
		// Il n'y a pas de valeur
		} else {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_CHECK_OPTION_UNDEFINED.get()
					.replaceAll("<subject>", subject.getIdentifier())
					.replaceAll("<option>", type)));
		}
		return false;
	}
}
