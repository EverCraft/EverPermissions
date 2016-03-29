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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EDataBase;
import fr.evercraft.everpermissions.EverPermissions;

public class EPDataBases extends EDataBase<EverPermissions> {
	private String table_users_permissions;
	private String table_users_groups;
	private String table_users_options;

	public EPDataBases(EverPermissions plugin) {
		super(plugin);
	}

	public boolean init() throws ServerDisableException {
		this.table_users_permissions = "users_perms";
		String permissions ="CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`world` varchar(36)," +
							"`permission` varchar(50) NOT NULL," +
							"`boolean` BOOLEAN NOT NULL," +
							"PRIMARY KEY (`uuid`, `world`, `permission`));";
		initTable(this.getTableUsersPermissions(), permissions);
		
		this.table_users_groups = "users_groups";
		String groups =		"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`world` varchar(36)," +
							"`group` varchar(50) NOT NULL," +
							"`subgroup` BOOLEAN NOT NULL," +
							"PRIMARY KEY (`uuid`, `world`, `group`, `subgroup`));";
		initTable(this.getTableUsersGroups(), groups);
		
		this.table_users_options = "users_options";
		String spawns = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`uuid` varchar(36) NOT NULL," +
							"`world` varchar(36) NOT NULL," +
							"`option` varchar(50) NOT NULL," +
							"`value` varchar(50) NOT NULL," +
							"PRIMARY KEY (`uuid`, `world`, `option`));";
		initTable(this.getTableUsersOptions(), spawns);		
		return true;
	}

	public String getTableUsersPermissions() {
		return this.getPrefix() + this.table_users_permissions;
	}

	public String getTableUsersGroups() {
		return this.getPrefix() + this.table_users_groups;
	}

	public String getTableUsersOptions() {
		return this.getPrefix() + this.table_users_options;
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
			this.plugin.getLogger().warn("Error while deleting the database : " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
		return resultat;
	}
}
