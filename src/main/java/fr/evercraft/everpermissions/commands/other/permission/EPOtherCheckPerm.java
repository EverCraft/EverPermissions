package fr.evercraft.everpermissions.commands.other.permission;

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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherCheckPerm extends ECommand<EverPermissions> {
	
	public EPOtherCheckPerm(final EverPermissions plugin) {
        super(plugin, "permocheckp", "manocheckp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.OTHER_CHECK_PERMISSION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.OTHER_CHECK_PERMISSION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permocheckp <" + EAMessages.ARGS_SUBJECT.get() + "> "
									   + "<" + EAMessages.ARGS_PERMISSION.get() + ">")
					.onClick(TextActions.suggestCommand("/permocheckp "))
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
			suggests.add("ever");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 2) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if(optSubject.isPresent()){
				resultat = command(source, optSubject.get(), args.get(1));
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_NOT_FOUND.get()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	private boolean command(final CommandSource staff, final Subject subject, final String permission) {
		// Permission : True
		if(subject.hasPermission(permission)) {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_CHECK_PERMISSION_TRUE.get()
					.replaceAll("<subject>", subject.getIdentifier())
					.replaceAll("<permission>", permission)));
		// Permission : False
		} else {
			staff.sendMessage(EChat.of(EPMessages.PREFIX.get() + EPMessages.OTHER_CHECK_PERMISSION_FALSE.get()
					.replaceAll("<subject>", subject.getIdentifier())
					.replaceAll("<permission>", permission)));
		}
		return false;
	}
}
