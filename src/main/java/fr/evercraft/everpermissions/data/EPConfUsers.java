package fr.evercraft.everpermissions.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.plugin.file.EConfig;

public class EPConfUsers extends EConfig {

	public EPConfUsers(final EPlugin plugin, final String name) {
		super(plugin, name, false);
	}

	@Override
	public void loadDefault() {
		if(this.getNode().getValue() == null) {
			Map<String, Boolean> permissions = new HashMap<String, Boolean>();
			Map<String, String> options = new HashMap<String, String>();
			
			permissions.put("everapi.help", true);
			permissions.put("evereconomy.help", true);
			permissions.put("everessentials.help", false);
			permissions.put("everpermissions.help", true);
			
			options.put("prefix", "&c");
			options.put("suffix", "");
			options.put("hover", "&7Ping : &a<PING>&7 ms[RT]&7Connecté depuis : &a<LAST_DATE_PLAYED>");
			options.put("suggest", "/msg <NAME> ");
			
			// Rexbut
			addDefault("86f8f95b-e5e6-45c4-bf85-4d64dbd0903f.permissions", permissions);
			addDefault("86f8f95b-e5e6-45c4-bf85-4d64dbd0903f.options", options);
			addDefault("86f8f95b-e5e6-45c4-bf85-4d64dbd0903f.group", "Admin");
			addDefault("86f8f95b-e5e6-45c4-bf85-4d64dbd0903f.subgroup", Arrays.asList());
			
			// Lesbleu
			addDefault("f3345769-4c70-4a9f-9db9-bdb8f9e8a46c.permissions", permissions);
			addDefault("f3345769-4c70-4a9f-9db9-bdb8f9e8a46c.options", options);
			addDefault("f3345769-4c70-4a9f-9db9-bdb8f9e8a46c.group", "Moderator");
			addDefault("f3345769-4c70-4a9f-9db9-bdb8f9e8a46c.subgroup", Arrays.asList("Admin"));
		}
	}
}
