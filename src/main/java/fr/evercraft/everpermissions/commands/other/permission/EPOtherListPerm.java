package fr.evercraft.everpermissions.commands.other.permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EOtherSubject;

public class EPOtherListPerm extends ECommand<EverPermissions> {
	
	public EPOtherListPerm(final EverPermissions plugin) {
        super(plugin, "permolistp", "manolistp");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.OTHER_LIST_PERMISSION.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.OTHER_LIST_PERMISSION_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permolistp <" + EAMessages.ARGS_SUBJECT.get() + ">")
					.onClick(TextActions.suggestCommand("/permolistp "))
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
			suggests.addAll(this.plugin.getManagerData().getTypeGroups().keySet());
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		if(args.size() == 1) {
			Optional<EOtherSubject> optSubject = this.plugin.getService().getOtherSubject(args.get(0));
			// Le joueur existe
			if(optSubject.isPresent()){
				resultat = command(source, optSubject.get());
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
	
	private boolean command(final CommandSource staff, final Subject subject) {
		Set<Context> contexts = new HashSet<Context>();
		List<Text> list = new ArrayList<Text>();
		
		// La liste des permissions
		Map<String, Boolean> permissions = subject.getSubjectData().getPermissions(contexts);
		if(permissions.isEmpty()) {
			list.add(EPMessages.OTHER_LIST_PERMISSION_PERMISSION_EMPTY.getText());
		} else {
			list.add(EPMessages.OTHER_LIST_PERMISSION_PERMISSION.getText());
			for(Entry<String, Boolean> permission : permissions.entrySet()) {
				if(permission.getValue()) {
					list.add(EChat.of(EPMessages.OTHER_LIST_PERMISSION_PERMISSION_LINE_TRUE.get()
							.replaceAll("<permission>", permission.getKey())));
				} else {
					list.add(EChat.of(EPMessages.OTHER_LIST_PERMISSION_PERMISSION_LINE_FALSE.get()
							.replaceAll("<permission>", permission.getKey())));
				}
			}
		}
		
		// La liste des permissions temporaires
		permissions = subject.getTransientSubjectData().getPermissions(contexts);
		if(!permissions.isEmpty()) {
			list.add(EPMessages.OTHER_LIST_PERMISSION_TRANSIENT.getText());
			for(Entry<String, Boolean> permission : permissions.entrySet()) {
				if(permission.getValue()) {
					list.add(EChat.of(EPMessages.OTHER_LIST_PERMISSION_TRANSIENT_LINE_TRUE.get()
							.replaceAll("<permission>", permission.getKey())));
				} else {
					list.add(EChat.of(EPMessages.OTHER_LIST_PERMISSION_TRANSIENT_LINE_FALSE.get()
							.replaceAll("<permission>", permission.getKey())));
				}
			}
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(
				EPMessages.OTHER_LIST_PERMISSION_TITLE.get()
				.replaceAll("<subject>", subject.getIdentifier())), 
				list, staff);
		return true;
	}
}
