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
package fr.evercraft.everpermissions;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.sponge.UtilsChat;

public class EPCommand extends ECommand<EverPermissions> {
	public EPCommand(final EverPermissions plugin) {
		super(plugin, "everpermissions", "permissions", "perms", "perm");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getName());
	}

	@Override
	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("DESCRIPTION");
	}
	
	@Override
	public Text help(final CommandSource source) {
		Text help;
		if(	source.hasPermission(this.plugin.getPermissions().get("HELP")) ||
			source.hasPermission(this.plugin.getPermissions().get("RELOAD"))){
			Builder build = Text.builder("/everpermissions <");
			
			if(source.hasPermission(this.plugin.getPermissions().get("HELP"))){
				build = build.append(Text.builder("help").onClick(TextActions.suggestCommand("/everpermissions help")).build());
				if(source.hasPermission(this.plugin.getPermissions().get("RELOAD"))){
					build = build.append(Text.builder("|").build());
				}
			}
			
			if(source.hasPermission(this.plugin.getPermissions().get("RELOAD"))){
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
			if(source.hasPermission(this.plugin.getPermissions().get("HELP"))){
				suggests.add("help");
			}
			if(source.hasPermission(this.plugin.getPermissions().get("RELOAD"))){
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
				if(source.hasPermission(this.plugin.getPermissions().get("HELP"))) {					
					this.plugin.getEverAPI().getManagerService().getEPagination().helpCommand(this.plugin.getManagerCommands(), source, this.plugin);
				} else {
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			// Reload
			} else if(args.get(0).equalsIgnoreCase("reload")) {
				if(source.hasPermission(this.plugin.getPermissions().get("RELOAD"))) {
					this.plugin.reload();
					source.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("RELOAD_COMMAND")));
				} else {
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			}
		} else {
			source.sendMessage(help(source));
		}
		return false;
	}
}
