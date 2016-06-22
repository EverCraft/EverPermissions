package fr.evercraft.everpermissions.commands.disable;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EverPermissions;

public class EPDeop extends ECommand<EverPermissions> {
	
	public EPDeop(final EverPermissions plugin) {
        super(plugin, "deop");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission("minecraft.command.deop");
	}

	public Text description(final CommandSource source) {
		return null;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/perms help").onClick(TextActions.suggestCommand("/perms help"))
					.color(TextColors.RED).build();
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		source.sendMessage(EPMessages.PREFIX.getText().concat(EPMessages.COMMAND_DEOP.getText()));
		return false;
	}
}
