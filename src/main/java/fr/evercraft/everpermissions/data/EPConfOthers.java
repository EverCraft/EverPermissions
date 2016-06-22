package fr.evercraft.everpermissions.data;

import java.util.HashMap;
import java.util.Map;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everpermissions.EverPermissions;

public class EPConfOthers extends EConfig {
	
	public EPConfOthers(final EverPermissions plugin) {
		super(plugin, "others", false);		
	}

	@Override
	public void loadDefault() {
		if(this.getNode().getValue() == null) {
			Map<String, Boolean> permissions = new HashMap<String, Boolean>();
			Map<String, String> options = new HashMap<String, String>();

			permissions.put("everapi", true);
			permissions.put("everchat", true);
			permissions.put("everessentials", true);
			permissions.put("evereconomy", true);
			permissions.put("evermails", true);
			permissions.put("everpermissions", true);
			permissions.put("minecraft", true);
			permissions.put("sponge", true);
			
			options.put("prefix", "&e");
			options.put("suffix", "");
			
			// Server
			addDefault("Server.permissions", permissions);
			addDefault("Server.options", options);
			
			// CommandBlock
			permissions.put("everessentials.kick", false);
			permissions.put("everessentials.stop", false);
			addDefault("CommandBlock.permissions", permissions);
			addDefault("CommandBlock.options", options);
		}
	}
}
