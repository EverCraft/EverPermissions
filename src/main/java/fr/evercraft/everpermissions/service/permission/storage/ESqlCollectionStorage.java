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
package fr.evercraft.everpermissions.service.permission.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.data.ESubjectData;
import fr.evercraft.everpermissions.service.permission.data.EUserData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubject;
import fr.evercraft.everpermissions.service.permission.subject.ESubjectReference;

public class ESqlCollectionStorage implements ICollectionStorage {
	private final EverPermissions plugin;
	
	private final String collection;

    public ESqlCollectionStorage(final EverPermissions plugin, final String collection) {
        this.plugin = plugin;
        this.collection = collection;
    }
    
    @Override
	public void reload() {
	}    
    
    public boolean load(final ESubject subject) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			
			SubjectData data = subject.getSubjectData();
			if (data instanceof ESubjectData) {
				this.loadPermissions(connection, (ESubjectData) data);
				this.loadOptions(connection, (ESubjectData) data);
				this.loadGroups(connection, (ESubjectData) data);
			}
			
			this.plugin.getELogger().debug("Chargement du subject (subject='" + subject.getIdentifier() + "';collection='" + subject.getCollectionIdentifier() + "')");
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public boolean load(final Collection<ESubject> subjects) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			
			for (ESubject subject : subjects) {
				SubjectData data = subject.getSubjectData();
				if (data instanceof ESubjectData) {
					this.loadPermissions(connection, (ESubjectData) data);
					this.loadOptions(connection, (ESubjectData) data);
					this.loadGroups(connection, (ESubjectData) data);
				}
				
				this.plugin.getELogger().debug("Chargement du subject (subject='" + subject.getIdentifier() + "';collection='" + subject.getCollectionIdentifier() + "')");
			}
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public void insertProfils(final Connection connection, final ESubjectData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableUsersProfiles() + "` "
						+ "VALUES (?, ?, ?);";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject.getIdentifier());
			preparedStatement.setString(2, this.collection);
			Optional<String> name = subject.getSubject().getFriendlyIdentifier();
			if (name.isPresent()) {
				preparedStatement.setString(3, name.get());
			} else {
				preparedStatement.setNull(3, Types.VARCHAR);
			}
			preparedStatement.execute();
		} catch (SQLException e) {
			this.plugin.getELogger().warn(" : " + e.getMessage());
			throw e;
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
    
    public void loadProfils(final Connection connection, final ESubjectData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
		String query = 	  "SELECT `name` " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersProfiles() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			if (list.next()) {
				subject.getSubject().setFriendlyIdentifierExecute(list.getString("name"));
			} else {
				this.insertProfils(connection, subject);
				this.plugin.getELogger().debug("Insert : ("
						+ "identifier='" + subject.getIdentifier() + "';"
						+ "collection='" + this.collection + "';"
						+ "name='" + list.getString("name") + "')");
			}
		} catch (SQLException e) {
			this.plugin.getELogger().warn("Permissions error when loading : " + e.getMessage());
			throw e;
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
    
    public void loadPermissions(final Connection connection, final ESubjectData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
		String query = 	  "SELECT *" 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				subject.setPermissionExecute(list.getString("world"), list.getString("permission"), Tristate.fromBoolean(list.getBoolean("boolean")));
				this.plugin.getELogger().debug("Loading : ("
						+ "identifier='" + subject.getIdentifier() + "';"
						+ "collection='" + this.collection + "';"
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

	public void loadOptions(final Connection connection, final ESubjectData subject) throws SQLException {
		PreparedStatement preparedStatement = null;
		String query = 	  "SELECT *" 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				subject.setOptionExecute(list.getString("world"), list.getString("option"), list.getString("value"));
				this.plugin.getELogger().debug("Loading : ("
						+ "identifier=" + subject.getIdentifier() + ";"
						+ "collection='" + this.collection + "';"
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

	public void loadGroups(final Connection connection, final ESubjectData subject) throws SQLException {
		PreparedStatement preparedStatement = null;
		String query = 	  "SELECT *" 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? "
						+ "ORDER BY `priority` ASC;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			ResultSet list = preparedStatement.executeQuery();
			while(list.next()) {
				Subject group = this.plugin.getService().getGroupSubjects().loadSubject(list.getString("group")).join();
				if (group != null) {
					if (list.getInt("priority") == 0 && subject instanceof EUserData) {
						((EUserData) subject).setGroupExecute(list.getString("world"), group.asSubjectReference());
						this.plugin.getELogger().debug("Loading : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "collection='" + this.collection + "';"
	    						+ "group=" + list.getString("group") + ";"
	    						+ "type=" + list.getString("world") + ")");
					} else {
						subject.addParentExecute(list.getString("world"), group.asSubjectReference());
						this.plugin.getELogger().debug("Loading : ("
	    						+ "identifier=" + subject.getIdentifier() + ";"
	    						+ "collection='" + this.collection + "';"
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
	
	@Override
	public boolean clear(ESubjectData subject) {
		Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			
			connection.setAutoCommit(false);
			this.clearPermissions(connection, subject);
			this.clearOptions(connection, subject);
			this.clearParents(connection, subject);
			this.clearProfils(connection, subject);
			connection.commit();
			
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
			try { connection.rollback();} catch (SQLException e1) {}
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
	}

	@Override
	public boolean clear(ESubjectData subject, String typeWorld) {
		Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			
			connection.setAutoCommit(false);
			this.clearPermissions(connection, subject, typeWorld);
			this.clearOptions(connection, subject, typeWorld);
			this.clearParents(connection, subject, typeWorld);
			connection.commit();
			
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
			try { connection.rollback();} catch (SQLException e1) {}
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
	}
	
	public boolean setFriendlyIdentifier(final ESubject subject, final @Nullable String name) {
		Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "UPDATE `" + this.plugin.getDataBases().getTableUsersProfiles() + "` "
						+ "SET `name` = ? "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
		try {
			connection = this.plugin.getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			if (name == null) {
				preparedStatement.setNull(1, Types.VARCHAR);
			} else {
				preparedStatement.setString(1, name);
			}
			preparedStatement.setString(2, this.collection);
			preparedStatement.setString(3, subject.getIdentifier());
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
        	this.plugin.getELogger().warn(" : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
		return false;
	}

	public boolean clearProfils(final Connection connection, final ESubjectData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE "
    					+ "FROM `" + this.plugin.getDataBases().getTableUsersProfiles() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
        	this.plugin.getELogger().warn(" : " + e.getMessage());
        	throw e;
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
    /*
     * Permissions
     */
	
    public boolean setPermission(final ESubjectData subject, final String typeWorld, final String permission, Tristate value, final boolean insert) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		// Supprime une permission
        	if (value.equals(Tristate.UNDEFINED)) {
        		String query = 	  "DELETE " 
			    				+ "FROM `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
			    				+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? AND `permission` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, this.collection);
				preparedStatement.setString(2, subject.getIdentifier());
				preparedStatement.setString(3, typeWorld);
				preparedStatement.setString(4, permission);
				
				this.plugin.getELogger().debug("Remove from database : ("
						+ "identifier='" + subject + "';"
						+ "permission='" + permission + "';"
						+ "type='" + typeWorld + "')");
			// Ajoute une permission
        	} else if (insert) {
        		String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
			    				+ "VALUES (?, ?, ?, ?, ?);";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, subject.getIdentifier());
				preparedStatement.setString(2, this.collection);
				preparedStatement.setString(3, typeWorld);
				preparedStatement.setString(4, permission);
				preparedStatement.setBoolean(5, value.asBoolean());
				
				this.plugin.getELogger().debug("Adding to the database : (identifier='" + subject + "';permission='" + permission + "';value='" + value.asBoolean() + "';type='" + typeWorld + "')");
			// Mise à jour une permission
        	} else {
        		String query = 	  "UPDATE `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
        						+ "SET boolean = ? "
        						+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? AND `permission` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setBoolean(1, value.asBoolean());
				preparedStatement.setString(2, this.collection);
				preparedStatement.setString(3, subject.getIdentifier());
				preparedStatement.setString(4, typeWorld);
				preparedStatement.setString(5, permission);
				
				this.plugin.getELogger().debug("Updating the database : ("
						+ "identifier='" + subject + "';"
						+ "permission='" + permission + "';"
						+ "value='" + value.asBoolean() + "';"
						+ "type='" + typeWorld + "')");
        	}
        	preparedStatement.execute();
        	return true;
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
    	return false;
    }
    
    public boolean clearPermissions(final ESubjectData subject, final String typeWorld) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			this.clearPermissions(connection, subject, typeWorld);
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public boolean clearPermissions(final Connection connection, final ESubjectData subject, final String typeWorld) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? ;";
    	try {
    		preparedStatement = connection.prepareStatement(query);
    		preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.setString(3, typeWorld);
			preparedStatement.execute();
			this.plugin.getELogger().debug("Removes the database permissions : ("
					+ "identifier='" + subject + "';"
					+ "type='" + typeWorld + "')");
			return true;
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error permissions deletions : " + e.getMessage());
    		throw e;
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
    }
    
    public boolean clearPermissions(final ESubjectData subject) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			this.clearPermissions(connection, subject);
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public boolean clearPermissions(final Connection connection, final ESubjectData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
    	try {
    		preparedStatement = connection.prepareStatement(query);
    		preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.execute();
			this.plugin.getELogger().debug("Removes the database permissions : ("
					+ "identifier='" + subject + "')");
			return true;
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error permissions deletions : " + e.getMessage());
    		throw e;
    	} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
    }
    
    /*
     * Options
     */
    
    public boolean setOption(final ESubjectData subject, final String typeWorld, final String option, final String value, final boolean insert) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
        	if (value == null) {
        		String query = 	  "DELETE " 
			    				+ "FROM `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
			    				+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? AND `option` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, this.collection);
				preparedStatement.setString(2, subject.getIdentifier());
				preparedStatement.setString(3, typeWorld);
				preparedStatement.setString(4, option);
				
				this.plugin.getELogger().debug("Remove from database : ("
						+ "identifier='" + subject + "';"
						+ "option='" + option + "';"
						+ "world='" + typeWorld + "')");
        	} else if (insert) {
        		String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
			    				+ "VALUES (?, ?, ?, ?, ?);";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, subject.getIdentifier());
				preparedStatement.setString(2, this.collection);
				preparedStatement.setString(3, typeWorld);
				preparedStatement.setString(4, option);
				preparedStatement.setString(5, value);
				
				this.plugin.getELogger().debug("Adding to the database : ("
						+ "identifier='" + subject + "';"
						+ "option='" + option + "';"
						+ "value='" + value + "';"
						+ "world='" + typeWorld + "')");
        	} else {
        		String query = 	  "UPDATE `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
        						+ "SET `value` = ? "
        						+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? AND `option` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, value);
				preparedStatement.setString(2, this.collection);
				preparedStatement.setString(3, subject.getIdentifier());
				preparedStatement.setString(4, typeWorld);
				preparedStatement.setString(5, option);
				
				this.plugin.getELogger().debug("Updating the database : ("
						+ "identifier='" + subject + "';"
						+ "option='" + option + "';"
						+ "value='" + value + "';"
						+ "world='" + typeWorld + "')");
        	}
        	preparedStatement.execute();
        	return true;
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
    	return false;
    }

    public boolean clearOptions(final ESubjectData subject, final String typeWorld) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			this.clearOptions(connection, subject, typeWorld);
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public boolean clearOptions(final Connection connection, final ESubjectData subject, final String typeWorld) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? ;";
    	try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.setString(3, typeWorld);
			preparedStatement.execute();
			this.plugin.getELogger().debug("Removes the database options : ("
					+ "identifier='" + subject + "';"
					+ "type='" + typeWorld + "')");
			return true;
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error options deletions : " + e.getMessage());
    		throw e;
    	} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
    }
    
    public boolean clearOptions(final ESubjectData subject) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			this.clearOptions(connection, subject);
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public boolean clearOptions(final Connection connection, final ESubjectData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
    	try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.execute();
			this.plugin.getELogger().debug("Removes the database options : ("
					+ "identifier='" + subject + "')");
			return true;
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error options deletions : " + e.getMessage());
    		throw e;
    	} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
    }

    /*
     * Groups
     */
    
    public boolean addParent(final ESubjectData subject, final String typeWorld, final SubjectReference parent) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
						+ "VALUES (?, ?, ?, ?);";
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, subject.getIdentifier());
			preparedStatement.setString(2, this.collection);
			preparedStatement.setString(3, typeWorld);
			preparedStatement.setString(4, parent.getSubjectIdentifier());
			preparedStatement.execute();
			return true;
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
    	return false;
    }
    
    public boolean setGroup(final ESubjectData subject, final String typeWorld, final SubjectReference parent, boolean insert) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		if (insert) {
    			String query = 	  "INSERT INTO `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
    							+ "VALUES (?, ?, ?, ?, ?);";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, subject.getIdentifier());
				preparedStatement.setString(2, this.collection);
				preparedStatement.setString(3, typeWorld);
				preparedStatement.setString(4, parent.getSubjectIdentifier());
				preparedStatement.setInt(5, 0);
				preparedStatement.execute();
				this.plugin.getELogger().debug("Adding to the database : ("
						+ "identifier='" + subject + "';"
						+ "group='" + parent.getSubjectIdentifier() + "';"
						+ "type='" + typeWorld + "')");
    		} else {
    			String query = 	  "UPDATE `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
								+ "SET `group` = ? "
								+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? AND `priority` = ? ;";
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, parent.getSubjectIdentifier());
				preparedStatement.setString(2, this.collection);
				preparedStatement.setString(3, subject.getIdentifier());
				preparedStatement.setString(4, typeWorld);
				preparedStatement.setInt(5, 0);
				
				this.plugin.getELogger().debug("Updating the database : ("
						+ "identifier='" + subject + "';"
						+ "group='" + parent.getSubjectIdentifier() + "';"
						+ "type='" + typeWorld + "'");
    		}
			return true;
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
    	return false;
    }

    public boolean removeParent(final ESubjectData subject, final String typeWorld, final SubjectReference parent) {
    	Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? AND `group` = ? ;";
    	try {
    		connection = this.plugin.getDataBases().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.setString(3, typeWorld);
			preparedStatement.setString(4, parent.getSubjectIdentifier());
			preparedStatement.execute();
					
			this.plugin.getELogger().debug("Remove from database : ("
					+ "identifier='" + subject + "';"
					+ "group='" + parent.getSubjectIdentifier() + "';"
					+ "type='" + typeWorld + "')");
			return true;
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
    	return false;
    }
    
    public boolean clearParents(final ESubjectData subject, final String typeWorld) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			this.clearParents(connection, subject, typeWorld);
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public boolean clearParents(final Connection connection, final ESubjectData subject, final String typeWorld) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? AND `world` = ? ;";
    	try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.setString(3, typeWorld);
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database groups : ("
					+ "identifier='" + subject + "';"
					+ "type='" + typeWorld + "')");
			return true;
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error groups deletions : " + e.getMessage());
    		throw e;
    	} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
    }

    public boolean clearParents(final ESubjectData subject) {
    	Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			this.clearParents(connection, subject);
			return true;
		} catch (ServerDisableException e) {
			e.execute();
		} catch (SQLException e) {
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
		return false;
    }
    
    public boolean clearParents(final Connection connection, final ESubjectData subject) throws SQLException {
    	PreparedStatement preparedStatement = null;
    	String query = 	  "DELETE " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ?;";
    	try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, subject.getIdentifier());
			preparedStatement.execute();
			
			this.plugin.getELogger().debug("Removes the database groups : ("
					+ "identifier='" + subject + "')");
			return true;
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error groups deletions : " + e.getMessage());
    		throw e;
    	} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
    }

    /*
     * Autre
     */
    
	@Override
	public boolean hasSubject(String identifier) {
		Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "SELECT `name` " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersProfiles() + "` "
						+ "WHERE `collection` = ? AND `identifier` = ? ;";
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, identifier);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				return true;
			}
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    	return false;
	}

	@Override
	public Set<String> getAllIdentifiers() {
		ImmutableSet.Builder<String> identifiers = ImmutableSet.builder();
		
		Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "SELECT `identifier` " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersProfiles() + "` "
						+ "WHERE `collection` = ? ;";
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				identifiers.add(list.getString("uuid"));
			}
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    	return identifiers.build();
	}

	@Override
	public Map<SubjectReference, Boolean> getAllWithPermission(String typeWorld, String permission) {
		ImmutableMap.Builder<SubjectReference, Boolean> identifiers = ImmutableMap.builder();
		
		Connection connection = null;
    	PreparedStatement preparedStatement = null;
    	String query = 	  "SELECT `identifier`, `boolean` " 
						+ "FROM `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
						+ "WHERE `collection` = ? AND `world` = ? AND `permission` = ? ;";
    	try {
    		connection = this.plugin.getDataBases().getConnection();
    		preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.collection);
			preparedStatement.setString(2, typeWorld);
			preparedStatement.setString(3, permission);
			ResultSet list = preparedStatement.executeQuery();
			while (list.next()) {
				identifiers.put(new ESubjectReference(this.plugin.getService(), this.collection, list.getString("uuid")), list.getBoolean("boolean"));
			}
    	} catch (SQLException e) {
    		this.plugin.getELogger().warn("Error : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
    	return identifiers.build();
	}

	@Override
	public boolean setDefault(EGroupSubject subject, String typeWorld, boolean value) {
		// Les groupes sont uniquement sauvegardé en fichier
		return false;
	}

	@Override
	public boolean register(String typeWorld) {
		return false;
	}

	@Override
	public boolean unregister(String typeWorld) {
		return false;
	}
}
