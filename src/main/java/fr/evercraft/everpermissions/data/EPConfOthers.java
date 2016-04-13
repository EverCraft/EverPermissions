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
