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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EDataBase;

public class EPDataBases extends EDataBase<EverPermissions> {
	private static final String TABLE_USERS_PROFILS = "users_profils";
	private static final String TABLE_USERS_PERMISSIONS = "users_perms";
	private static final String TABLE_USERS_GROUPS = "users_groups";
	private static final String TABLE_USERS_OPTIONS = "users_options";

	public EPDataBases(EverPermissions plugin) throws PluginDisableException {
		super(plugin);
	}

	public boolean init() throws ServerDisableException {
		String profils ="CREATE TABLE IF NOT EXISTS {table} (" +
							"`uuid` varchar(36) NOT NULL," +
							"`collection` varchar(36)," +
							"`name` varchar(36)," +
							"PRIMARY KEY (`uuid`, `collection`, `name`));";
		initTable(this.getTableUsersProfiles(), profils);
		
		String permissions ="CREATE TABLE IF NOT EXISTS {table} (" +
							"`uuid` varchar(36) NOT NULL," +
							"`collection` varchar(36)," +
							"`world` varchar(36)," +
							"`permission` varchar(100) NOT NULL," +
							"`boolean` BOOLEAN NOT NULL," +
							"PRIMARY KEY (`uuid`, `collection`, `world`, `permission`));";
		initTable(this.getTableUsersPermissions(), permissions);
		
		String groups =		"CREATE TABLE IF NOT EXISTS {table} (" +
							"`uuid` varchar(36) NOT NULL," +
							"`collection` varchar(36)," +
							"`world` varchar(36)," +
							"`group` varchar(36) NOT NULL," +
							"`priority` INT NOT NULL AUTO_INCREMENT," +
							"PRIMARY KEY (`uuid`, `collection`, `world`, `group`, `priority`));";
		initTable(this.getTableUsersGroups(), groups);
		
		String spawns = 	"CREATE TABLE IF NOT EXISTS {table} (" +
							"`uuid` varchar(36) NOT NULL," +
							"`collection` varchar(36)," +
							"`world` varchar(36) NOT NULL," +
							"`option` varchar(50) NOT NULL," +
							"`value` varchar(100) NOT NULL," +
							"PRIMARY KEY (`uuid`, `collection`, `world`, `option`));";
		initTable(this.getTableUsersOptions(), spawns);		
		return true;
	}
	
	public String getTableUsersProfiles() {
		return this.getPrefix() + TABLE_USERS_PROFILS;
	}

	public String getTableUsersPermissions() {
		return this.getPrefix() + TABLE_USERS_PERMISSIONS;
	}

	public String getTableUsersGroups() {
		return this.getPrefix() + TABLE_USERS_GROUPS;
	}

	public String getTableUsersOptions() {
		return this.getPrefix() + TABLE_USERS_OPTIONS;
	}
	
	/**
	 * Supprimé les données de la base de données
	 * @param connection La connection SQL
	 * @return Retourne True s'il n'y a pas eu d'erreur
	 */
	public boolean clear(final Connection connection) {
		boolean resultat = false;
		PreparedStatement preparedStatement = null;
		try {
			// Profils
    		preparedStatement = connection.prepareStatement("TRUNCATE  `" + this.getTableUsersProfiles() + "` ;");
			preparedStatement.execute();
			preparedStatement.close();
			
			// Permissions
    		preparedStatement = connection.prepareStatement("TRUNCATE  `" + this.getTableUsersPermissions() + "` ;");
			preparedStatement.execute();
			preparedStatement.close();
			
			// Groupes
			preparedStatement = connection.prepareStatement("TRUNCATE  `" + this.getTableUsersGroups() + "` ;");
			preparedStatement.execute();
			preparedStatement.close();
		    	
			// Options
			preparedStatement = connection.prepareStatement("TRUNCATE  `" + this.getTableUsersOptions() + "` ;");
			preparedStatement.execute();
			preparedStatement.close();
			
			resultat = true;
    	} catch (SQLException e) {
			this.plugin.getELogger().warn("Error while deleting the database : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
		return resultat;
	}
}
