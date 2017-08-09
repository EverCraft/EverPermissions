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
package fr.evercraft.everpermissions.service.permission.data.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.services.worldguard.region.ProtectedRegion;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.EContextCalculator;
import fr.evercraft.everworldguard.protection.EProtectionService;
import fr.evercraft.everworldguard.protection.regions.EProtectedGlobalRegion;
import fr.evercraft.everworldguard.protection.regions.EProtectedRegion;

public class ESqlUserData implements IUserData {
	private final EverPermissions plugin;

    public ESqlUserData(final EverPermissions plugin) {
        this.plugin = plugin;
    }
    
    public CompletableFuture<Boolean> execute(final Function<Connection, Boolean> fun) {
    	return CompletableFuture.supplyAsync(() -> {
			Connection connection = null;
			try {
	    		return fun.apply(this.plugin.getManagerData().getDataBases().getConnection());
			} catch (ServerDisableException e) {
				e.execute();
			} finally {
				try {if (connection != null) connection.close();} catch (SQLException e) {}
		    }
			return false;
		}, this.plugin.getThreadAsync());
    }
    
    public CompletableFuture<Boolean> load(final EUserData subject) {
    	return CompletableFuture.supplyAsync(() -> {
			Connection connection = null;
			try {
				connection = this.plugin.getManagerData().getDataBases().getConnection();
				
				this.loadPermissions(connection, subject);
				this.loadOptions(connection, subject);
				this.loadGroups(connection, subject);
			} catch (ServerDisableException e) {
				e.execute();
			} catch (SQLException e) {
			} finally {
				try {if (connection != null) connection.close();} catch (SQLException e) {}
		    }
			return false;
		}, this.plugin.getThreadAsync());
    }
    
    public CompletableFuture<Boolean> load(final List<EUserData> subjects) {
    	return CompletableFuture.supplyAsync(() -> {
			Connection connection = null;
			try {
				connection = this.plugin.getManagerData().getDataBases().getConnection();
				
				for (EUserData subject : subjects) {
					this.loadPermissions(connection, subject);
					this.loadOptions(connection, subject);
					this.loadGroups(connection, subject);
				}
			} catch (ServerDisableException e) {
				e.execute();
			} catch (SQLException e) {
			} finally {
				try {if (connection != null) connection.close();} catch (SQLException e) {}
		    }
			return false;
		}, this.plugin.getThreadAsync());
    }
    
    public void loadPermissions(final Connection connection, final EUserData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
		String query = 	  "SELECT *" 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
						+ "WHERE `uuid` = ? ;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				subject.setPermissionExecute(list.getString("world"), list.getString("permission"), Tristate.fromBoolean(list.getBoolean("boolean")));
				this.plugin.getELogger().debug("Loading : ("
						+ "identifier='" + subject.getIdentifier() + "';"
						+ "permission='" + list.getString("permission") + "';"
						+ "value='" + list.getBoolean("boolean") + "';"
						+ "type='" + list.getString("world") + "')");
			}
		} catch (SQLException e) {
			this.plugin.getELogger().warn("Permissions error when loading : " + e.getMessage());
			throw e;
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}

	public void loadOptions(final Connection connection, final EUserData subject) throws SQLException {
		PreparedStatement preparedStatement = null;
		String query = 	  "SELECT *" 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
						+ "WHERE `uuid` = ? ;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				subject.setOptionExecute(list.getString("world"), list.getString("option"), list.getString("value"));
				this.plugin.getELogger().debug("Loading : ("
						+ "identifier=" + subject.getIdentifier() + ";"
						+ "option=" + list.getString("option") + ";"
						+ "value=" + list.getString("value") + ";"
						+ "type=" + list.getString("world") + ")");
			}
		} catch (SQLException e) {
			this.plugin.getELogger().warn("Options error when loading : " + e.getMessage());
			throw e;
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}

	public void loadGroups(final Connection connection, final EUserData subject) throws SQLException {
		PreparedStatement preparedStatement = null;
		String query = 	  "SELECT *" 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `uuid` = ? ;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			while(list.next()) {
				Subject group = this.plugin.getService().getGroupSubjects().loadSubject(list.getString("group")).join();
				if (group != null) {
					// Chargement des sous-groupes
					if (!list.getBoolean("subgroup") && subject.getParents(list.getString("world")).isEmpty()) {
						subject.addParentExecute(list.getString("world"), group.asSubjectReference());
						this.plugin.getELogger().debug("Loading : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "group=" + list.getString("group") + ";"
	    						+ "type=" + list.getString("world") + ")");
					// Chargement du groupe
					} else {
						subject.addSubParentExecute(list.getString("world"), group.asSubjectReference());
						this.plugin.getELogger().debug("Loading : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "subgroup=" + list.getString("group") + ";"
	    						+ "type=" + list.getString("world") + ")");
					}
				}
			}
		} catch (SQLException e) {
			this.plugin.getELogger().warn("Groups error when loading : " + e.getMessage());
			throw e;
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
    
    /*
     * Permissions
     */
	
	public CompletableFuture<Boolean> setPermission(final String subject, final String world, final String permission, Tristate value, final boolean insert) {
		this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> setPermissionAsync(subject, world, permission, value, insert))
			.name("Permission : setPermission").submit(this.plugin);
		return true;
	}
	
    public void setPermissionAsync(final String subject, final String world, final String permission, Tristate value, final boolean insert) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
    		// Supprime une permission
        	if (value.equals(Tristate.UNDEFINED)) {
        		String query = 	  "DELETE " 
			    				+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
			    				+ "WHERE `uuid` = ? AND `world` = ? AND `permission` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, subject);
				preparedStatement.setString(2, world);
				preparedStatement.setString(3, permission);
				
				this.plugin.getELogger().debug("Remove from database : (identifier='" + subject + "';permission='" + permission + "';type='" + world + "')");
			// Ajoute une permission
        	} else if (insert) {
        		String query = 	  "INSERT INTO `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
			    				+ "VALUES (?, ?, ?, ?);";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, subject);
				preparedStatement.setString(2, world);
				preparedStatement.setString(3, permission);
				preparedStatement.setBoolean(4, value.asBoolean());
				
				this.plugin.getELogger().debug("Adding to the database : (identifier='" + subject + "';permission='" + permission + "';value='" + value.asBoolean() + "';type='" + world + "')");
			// Mise à jour une permission
        	} else {
        		String query = 	  "UPDATE `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
        						+ "SET boolean = ? "
        						+ "WHERE `uuid` = ? AND `world` = ? AND `permission` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setBoolean(1, value.asBoolean());
				preparedStatement.setString(2, subject);
				preparedStatement.setString(3, world);
				preparedStatement.setString(4, permission);
				
				this.plugin.getELogger().debug("Updating the database : (identifier='" + subject + "';permission='" + permission + "';value='" + value.asBoolean() + "';type='" + world + "')");
        	}
        	preparedStatement.execute();
        } catch (SQLException e) {
        	this.plugin.getELogger().warn("Error during a change of permission : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean clearPermissions(final String subject, final String world) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearPermissionsAsync(subject, world))
			.name("Permission : clearPermissions").submit(this.plugin);
		return true;
    }

    public void clearPermissionsAsync(final String subject, final String world) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
						+ "WHERE `uuid` = ? AND `world` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
    		preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.execute();
			this.plugin.getELogger().debug("Removes the database permissions : (identifier='" + subject + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error permissions deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean clearPermissions(final String subject) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearPermissionsAsync(subject))
			.name("Permission : clearPermissionsAll").submit(this.plugin);
		return true;
    }
    
    public void clearPermissionsAsync(final String subject) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
						+ "WHERE `uuid` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
    		preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.execute();
			this.plugin.getELogger().debug("Removes the database permissions : (identifier='" + subject + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error permissions deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    /*
     * Options
     */
    
    public boolean setOption(final String subject, final String world, final String type, final String name, final boolean insert) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> setOptionAsync(subject, world, type, name, insert))
			.name("Permission : setOption").submit(this.plugin);
    	return true;
    }
    
    public void setOptionAsync(final String subject, final String world, final String option, final String value, final boolean insert) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
        	if (value == null) {
        		String query = 	  "DELETE " 
			    				+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
			    				+ "WHERE `uuid` = ? AND `world` = ? AND `option` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, subject);
				preparedStatement.setString(2, world);
				preparedStatement.setString(3, option);
				
				this.plugin.getELogger().debug("Remove from database : (identifier='" + subject + "';option='" + option + "';world='" + world + "')");
        	} else if (insert) {
        		String query = 	  "INSERT INTO `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
			    				+ "VALUES (?, ?, ?, ?);";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, subject);
				preparedStatement.setString(2, world);
				preparedStatement.setString(3, option);
				preparedStatement.setString(4, value);
				
				this.plugin.getELogger().debug("Adding to the database : (identifier='" + subject + "';option='" + option + "';value='" + value + "';world='" + world + "')");
        	} else {
        		String query = 	  "UPDATE `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
        						+ "SET `value` = ? "
        						+ "WHERE `uuid` = ? AND `world` = ? AND `option` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, value);
				preparedStatement.setString(2, subject);
				preparedStatement.setString(3, world);
				preparedStatement.setString(4, option);
				
				this.plugin.getELogger().debug("Updating the database : (identifier='" + subject + "';option='" + option + "';value='" + value + "';world='" + world + "')");
        	}
        	preparedStatement.execute();
        } catch (SQLException e) {
        	this.plugin.getELogger().warn("Error during a change of option : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean clearOptions(final String subject, final String world) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearOptionsAsync(subject, world))
			.name("Permission : clearOptions").submit(this.plugin);
		return true;
    }

    public void clearOptionsAsync(final String subject, final String world) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
						+ "WHERE `uuid` = ? AND `world` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database options : (identifier='" + subject + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error options deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }

    public boolean clearOptions(final String subject) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearOptionsAsync(subject))
			.name("Permission : clearOptionsAll").submit(this.plugin);
		return true;
    }
    
    public void clearOptionsAsync(final String subject) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
				+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
				+ "WHERE `uuid` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database options : (identifier='" + subject + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error options deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }

    /*
     * Groups
     */
    
    public boolean addParent(final String subject, final String world, final String parent) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> addParentAsync(subject, world, parent))
			.name("Permission : addParent").submit(this.plugin);
		return true;
    }
    
    public void addParentAsync(final String subject, final String world, final String parent) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "INSERT INTO `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "VALUES (?, ?, ?, ?);";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.setString(3, parent);
			preparedStatement.setBoolean(4, false);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Adding to the database : (identifier='" + subject + "';group='" + parent + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error during a change of group : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean removeParent(final String subject, final String world, final String parent) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> removeParentAsync(subject, world, parent))
			.name("Permission : removeParent").submit(this.plugin);
		return true;
    }

    public void removeParentAsync(final String subject, final String world, final String parent) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `uuid` = ? AND `world` = ? AND `group` = ? AND `subgroup` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.setString(3, parent);
			preparedStatement.setBoolean(4, false);
			preparedStatement.execute();
					
			this.plugin.getELogger().debug("Remove from database : (identifier='" + subject + "';group='" + parent + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error during a change of group : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean clearParents(final String subject, final String world) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearParentsAsync(subject, world))
			.name("Permission : clearParents").submit(this.plugin);
		return true;
    }
    
    public void clearParentsAsync(final String subject, final String world) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `uuid` = ? AND `world` = ? AND `subgroup` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.setBoolean(3, false);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database groups : (identifier='" + subject + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error groups deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean clearParents(final String subject) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearParentsAsync(subject))
			.name("Permission : clearParentsAll").submit(this.plugin);
    	return true;
    }

    public void clearParentsAsync(final String subject) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `uuid` = ? AND `subgroup` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setBoolean(2, false);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database groups : (identifier='" + subject + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error groups deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    /*
     * SubGroups
     */
    
    public boolean addSubParent(final String subject, final String world, final String parent) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> addSubParentAsync(subject, world, parent))
			.name("Permission : addSubParent").submit(this.plugin);
    	return true;
    }
    
    public void addSubParentAsync(final String subject, final String world, final String parent) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "INSERT INTO `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
    					+ "VALUES (?, ?, ?, ?);";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.setString(3, parent);
			preparedStatement.setBoolean(4, true);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Adding to the database : (identifier='" + subject + "';subgroup='" + parent + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error during a change of subgroup : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }

    public boolean removeSubParent(final String subject, final String world, final String parent) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> removeSubParentAsync(subject, world, parent))
			.name("Permission : removeSubParent").submit(this.plugin);
    	return true;
    }
    
    public void removeSubParentAsync(final String subject, final String world, final String parent) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `uuid` = ? AND `world` = ? AND `group` = ? AND `subgroup` = ? ;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.setString(3, parent);
			preparedStatement.setBoolean(4, true);
			preparedStatement.execute();			
			
			this.plugin.getELogger().debug("Remove from database : (identifier='" + subject + "';subgroup='" + parent + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error during a change of subgroup : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean clearSubParents(final String subject, final String world) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearSubParentsAsync(subject, world))
			.name("Permission : clearSubParents").submit(this.plugin);
    	return true;
    }
    
    public void clearSubParentsAsync(final String subject, final String world) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `uuid` = ? AND `world` = ? AND `subgroup` = ?;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, world);
			preparedStatement.setBoolean(3, true);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database subgroups : (identifier='" + subject + "';type='" + world + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error subgroups deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
    
    public boolean clearSubParents(final String subject) {
    	this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> clearSubParentsAsync(subject))
			.name("Permission : clearSubParentsAll").submit(this.plugin);
		return true;
    }

    public void clearSubParentsAsync(final String subject) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `uuid` = ? AND `subgroup` = ?;";
    	try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setBoolean(2, true);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database subgroups : (identifier='" + subject + "')");
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error subgroups deletions : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    }
}
