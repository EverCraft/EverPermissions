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
package fr.evercraft.everpermissions;

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.plugin.file.EMessage;

public class EPConfig extends EConfig<EverPermissions> {
	
	private static final String DEFAULT = "default";
	
	public EPConfig(final EverPermissions plugin) {
		super(plugin);
	}
	
	public void reload() {
		super.reload();
		this.plugin.getLogger().setDebug(this.isDebug());
	}
	
	@Override
	public void loadDefault() {
		addDefault("DEBUG", false, "Displays plugin performance in the logs");
		addDefault("LANGUAGE", EMessage.FRENCH, "Select language messages", "Examples : ", "  French : FR_fr", "  English : EN_en");
		
		addComment("SQL", 	"Save the user in a database : ",
							" H2 : \"jdbc:h2:" + this.plugin.getPath().toAbsolutePath() + "/permissions\"",
							" SQL : \"jdbc:mysql://[login[:password]@]<host>:<port>/<database>\"",
							"By default users are saving in the 'users/'");
		
		addDefault("SQL.enable", false);
		addDefault("SQL.url", "jdbc:mysql://root:password@localhost:3306/minecraft");
		addDefault("SQL.prefix", "everpermissions_");
		
		addComment("groups", 	"Configure the groups for each world",
								"Example : ",
								"	world=default",
								"	DIM1=default",
								"	DIM-1=nether",
								"The worlds 'world' and 'DIM1' will have the same groups but not 'DIM-1'");
		
		addComment("users", 	"Configure the users for each world",
								"Example : ",
								"	world=default",
								"	DIM1=default",
								"	DIM-1=nether",
								"The worlds 'world' and 'DIM1' will have the same users but not 'DIM-1'");
	}
	
	/**
	 * Donne le fichier de configuration des groupes du monde
	 * @param world Le monde
	 * @return Le nom du fichier de configuration
	 */
	public String getGroups(final String world) {
		String name = this.get("groups." + world).getString(null);
		if (name == null) {
			name = EPConfig.DEFAULT;
			this.get("groups." + world).setValue(name);
			this.save(true);
		}		
		return name;
	}
	
	/**
	 * Donne le fichier de configuration des joueurs du monde
	 * @param world Le monde
	 * @return Le nom du fichier de configuration
	 */
	public String getUsers(final String world) {
		String name = this.get("users." + world).getString(null);
		if (name == null) {
			name = EPConfig.DEFAULT;
			this.get("users." + world).setValue(name);
			this.save(true);
		}		
		return name;
	}
}
