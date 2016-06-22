package fr.evercraft.everpermissions.data;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.plugin.file.EConfig;

public class EPConfGroups extends EConfig {

	public EPConfGroups(final EPlugin plugin, final String name) {
		super(plugin, name, false);
	}

	@Override
	public void loadDefault() {
		if(this.getNode().getValue() == null) {
			Map<String, Boolean> permissions = new HashMap<String, Boolean>();
			Map<String, String> options = new HashMap<String, String>();
			
			// Default
			permissions.put("everchat.icon", true);
			
			permissions.put("evereconomy.help", true);
			permissions.put("evereconomy.pay", true);
			permissions.put("evereconomy.balance", true);
			permissions.put("evereconomy.balance.others", true);
			permissions.put("evereconomy.balancetop", true);
			
			permissions.put("everessentials.delhome", true);
			permissions.put("everessentials.sethome", true);
			permissions.put("everessentials.home", true);
			permissions.put("everessentials.motd", true);
			permissions.put("everessentials.rules", true);
			permissions.put("everessentials.suicide", true);
			permissions.put("everessentials.warp", true);
			permissions.put("everessentials.getpos", true);
			permissions.put("everessentials.help", true);
			permissions.put("everessentials.list", true);
			permissions.put("everessentials.ping", true);
			permissions.put("everessentials.rules", true);
			permissions.put("everessentials.spawn", true);
			permissions.put("everessentials.uuid", true);
			permissions.put("everessentials.repair", true);
			permissions.put("everessentials.repair.hand", true);
			permissions.put("everessentials.clearinventory", true);
			
			options.put("prefix", "&e");
			options.put("suffix", "");
			options.put("hover", "&7Balance : &a<BALANCE> <SYMBOL>[RT]&7Ping : &a<PING>&7 ms[RT]&7Connecté depuis : &a<LAST_DATE_PLAYED>");
			options.put("suggest", "/msg <NAME> ");
			
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
			
			permissions.put("everessentials.jump", true);
			permissions.put("everessentials.kick", true);
			permissions.put("everessentials.weather", true);
			permissions.put("everessentials.uuid.others", true);
			permissions.put("everessentials.color", true);
			permissions.put("everessentials.skull", true);
			permissions.put("everessentials.skull.others", true);
			permissions.put("everessentials.near.default", true);
			permissions.put("everessentials.me", true);
			permissions.put("everessentials.kick", true);
			permissions.put("everessentials.repair.hotbar", true);
			permissions.put("everessentials.lag", true);
			permissions.put("everessentials.ping.others", true);
			permissions.put("everessentials.info", true);
			permissions.put("everessentials.invsee", true);
			permissions.put("everessentials.sethome.multiple.paladin", true);
			permissions.put("everessentials.hat", true);
			permissions.put("everessentials.heal", true);
			permissions.put("everessentials.feed", true);
			permissions.put("everessentials.back", true);
			permissions.put("everessentials.broadcast", true);
			
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
			permissions.put("everessentials", true);
			permissions.put("evereconomy", true);
			permissions.put("evermails", true);
			permissions.put("everpermissions", true);
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
