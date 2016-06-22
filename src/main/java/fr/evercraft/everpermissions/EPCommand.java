package fr.evercraft.everpermissions;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.command.ECommand;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everpermissions.EPMessage.EPMessages;

public class EPCommand extends ECommand<EverPermissions> {
	public EPCommand(final EverPermissions plugin) {
		super(plugin, "everpermissions", "permissions", "perms", "perm");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.EVERPERMISSIONS.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EPMessages.DESCRIPTION.getText();
	}
	
	@Override
	public Text help(final CommandSource source) {
		Text help;
		if(	source.hasPermission(EPPermissions.HELP.get()) ||
			source.hasPermission(EPPermissions.RELOAD.get())){
			Builder build = Text.builder("/everpermissions <");
			
			if(source.hasPermission(EPPermissions.HELP.get())){
				build = build.append(Text.builder("help").onClick(TextActions.suggestCommand("/everpermissions help")).build());
				if(source.hasPermission(EPPermissions.RELOAD.get())){
					build = build.append(Text.builder("|").build());
				}
			}
			
			if(source.hasPermission(EPPermissions.RELOAD.get())){
				build = build.append(Text.builder("reload").onClick(TextActions.suggestCommand("/everpermissions reload")).build());
			}
			
			build = build.append(Text.builder(">").build());
			help = build.color(TextColors.RED).build();
		} else {
			help = Text.builder("/everpermissions").onClick(TextActions.suggestCommand("/everpermissions"))
					.color(TextColors.RED).build();
		}
		return help;
	}
	
	@Override
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			if(source.hasPermission(EPPermissions.HELP.get())){
				suggests.add("help");
			}
			if(source.hasPermission(EPPermissions.RELOAD.get())){
				suggests.add("reload");
			}
		}
		return suggests;
	}
	
	@Override
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException, PluginDisableException {
		if(args.size() == 1){
			// Help
			if(args.get(0).equalsIgnoreCase("help")) {
				if(source.hasPermission(EPPermissions.HELP.get())) {					
					this.plugin.getEverAPI().getManagerService().getEPagination().helpCommand(this.plugin.getManagerCommands(), source, this.plugin);
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			// Reload
			} else if(args.get(0).equalsIgnoreCase("reload")) {
				if(source.hasPermission(EPPermissions.RELOAD.get())) {
					this.plugin.reload();
					source.sendMessage(EChat.of(EPMessages.PREFIX.get() + EAMessages.RELOAD_COMMAND.get()));
				} else {
					source.sendMessage(EAMessages.NO_PERMISSION.getText());
				}
			}
		} else {
			source.sendMessage(help(source));
		}
		return false;
	}
}
