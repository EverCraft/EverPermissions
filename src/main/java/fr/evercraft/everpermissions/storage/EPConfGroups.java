/*
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
package fr.evercraft.everpermissions.storage;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everpermissions.EverPermissions;

public class EPConfGroups extends EConfig<EverPermissions> {

	public EPConfGroups(final EverPermissions plugin, final String name) {
		super(plugin, name, false);
	}

	@Override
	public void loadDefault() {
		if (this.getNode().getValue() == null) {
			Map<String, Boolean> permissions = new HashMap<String, Boolean>();
			Map<String, String> options = new HashMap<String, String>();
			
			// Default
			permissions.put("everchat.icon", true);
			
			permissions.put("evereconomy.help", true);
			permissions.put("evereconomy.pay", true);
			permissions.put("evereconomy.balance", true);
			permissions.put("evereconomy.balance.others", true);
			permissions.put("evereconomy.balancetop", true);
			
			permissions.put("everessentials.plugin.command", true);
			permissions.put("everessentials.plugin.help", true);
			permissions.put("everessentials.afk.command", true);
			permissions.put("everessentials.bed.command", true);
			permissions.put("everessentials.getpos.command", true);
			permissions.put("everessentials.hat.command", true);
			permissions.put("everessentials.home.command", true);
			permissions.put("everessentials.delhome.command", true);
			permissions.put("everessentials.sethome.command", true);
			permissions.put("everessentials.info.command", true);
			permissions.put("everessentials.lag.command", true);
			permissions.put("everessentials.list.command", true);
			permissions.put("everessentials.mail.command", true);
			permissions.put("everessentials.mail.send", true);
			permissions.put("everessentials.me.command", true);
			permissions.put("everessentials.motd.command", true);
			permissions.put("everessentials.ping.command", true);
			permissions.put("everessentials.ping.others", true);
			permissions.put("everessentials.rules.command", true);
			permissions.put("everessentials.spawn.command", true);
			permissions.put("everessentials.suicide.command", true);
			permissions.put("everessentials.tpaccept.command", true);
			permissions.put("everessentials.tpadeny.command", true);
			permissions.put("everessentials.tpa.command", true);
			permissions.put("everessentials.tpahere.command", true);
			permissions.put("everessentials.toggle.command", true);
			permissions.put("everessentials.warp.command", true);
			permissions.put("everessentials.whois.command", true);
			
			permissions.put("minecraft.command.help", true);

			options.put("prefix", "&e");
			options.put("suffix", "");
			options.put("hover", "&7Balance : &a{BALANCE} {SYMBOL}[RT]&7Ping : &a{PING}&7 ms[RT]&7Connecté depuis : &a{LAST_DATE_PLAYED}");
			options.put("suggest", "/msg {NAME} ");
			
			addDefault("Default.default", true);
			addDefault("Default.permissions", permissions);
			addDefault("Default.options", options);
			addDefault("Default.inheritances", Arrays.asList());
			
			// Moderator
			permissions.clear();
			options.clear();
			
			permissions.put("everchat.chat", true);
			permissions.put("everchat.format", true);
			permissions.put("everchat.magic", true);
			
			permissions.put("everessentials.afk", true);			
			permissions.put("everessentials.jump", true);
			permissions.put("everessentials.kick", true);
			permissions.put("everessentials.weather", true);
			permissions.put("everessentials.uuid", true);
			permissions.put("everessentials.color", true);
			permissions.put("everessentials.skull", true);
			permissions.put("everessentials.near.default", true);
			permissions.put("everessentials.me", true);
			permissions.put("everessentials.kick", true);
			permissions.put("everessentials.repair.hotbar", true);
			permissions.put("everessentials.lag", true);
			permissions.put("everessentials.ping.others", true);
			permissions.put("everessentials.info", true);
			permissions.put("everessentials.invsee", true);
			permissions.put("everessentials.sethome.multiple.command", true);
			permissions.put("everessentials.sethome.multiples.moderator", true);
			permissions.put("everessentials.hat", true);
			permissions.put("everessentials.heal", true);
			permissions.put("everessentials.feed", true);
			permissions.put("everessentials.back", true);
			permissions.put("everessentials.broadcast", true);
			permissions.put("everessentials.names", true);
			permissions.put("everessentials.mojang", true);
			
			permissions.put("evermails.use", true);
			
			options.put("prefix", "&2");
			options.put("suffix", "");
			
			addDefault("Moderator.default", false);
			addDefault("Moderator.permissions", permissions);
			addDefault("Moderator.options", options);
			addDefault("Moderator.inheritances", Arrays.asList("Default"));
			
			// Admin
			permissions.clear();
			options.clear();
			
			permissions.put("everapi", true);
			permissions.put("everchat", true);
			permissions.put("evercooldowns", true);
			permissions.put("evereconomy", true);
			permissions.put("everessentials", true);
			permissions.put("everinformations", true);
			permissions.put("evermails", true);
			permissions.put("everpermissions", true);
			permissions.put("everpvp", true);
			permissions.put("eversanctions", true);
			permissions.put("evermultiworlds", true);
			permissions.put("everworldguard", true);
			permissions.put("minecraft", true);
			permissions.put("sponge", true);
			
			options.put("prefix", "&c");
			options.put("suffix", "");
			
			addDefault("Admin.default", false);
			addDefault("Admin.permissions", permissions);
			addDefault("Admin.options", options);
			addDefault("Admin.inheritances", Arrays.asList("Moderator"));
		}
	}
	
}
