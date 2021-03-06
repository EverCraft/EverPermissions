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
package fr.evercraft.everpermissions.command.sub;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everpermissions.EPMessage.EPMessages;
import fr.evercraft.everpermissions.EPPermissions;
import fr.evercraft.everpermissions.EverPermissions;

public class EPMigrate extends ECommand<EverPermissions> {
	
	public EPMigrate(final EverPermissions plugin) {
        super(plugin, "permtransfer", "mantransfer");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EPPermissions.MIGRATE.get());
	}

	public Text description(final CommandSource source) {
		return EPMessages.TRANSFERT_DESCRIPTION.getText();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/permtransfer ").onClick(TextActions.suggestCommand("/permtransfer "))
				.append(Text.of("<"))
				.append(Text.builder("sql").onClick(TextActions.suggestCommand("/permtransfer sql")).build())
				.append(Text.of("|"))
				.append(Text.builder("conf").onClick(TextActions.suggestCommand("/permtransfer conf")).build())
				.append(Text.of("> [confirmation]"))
				.color(TextColors.RED).build();
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return Arrays.asList("sql", "conf");
		}
		return Arrays.asList();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			//Si la base de donnée est activé
			if (this.plugin.getDataBases().isEnable()) {
				// Transféré vers une base de donnée SQL
				if (args.get(0).equalsIgnoreCase("sql")) {
					EPMessages.TRANSFERT_SQL_CONFIRMATION.sender()
						.replace("{confirmation}", () -> this.getButtonConfirmationSQL())
						.sendTo(source);
				// Transféré vers un fichier de config
				} else if (args.get(0).equalsIgnoreCase("conf")) {
					EPMessages.TRANSFERT_CONF_CONFIRMATION.sender()
						.replace("{confirmation}", () -> this.getButtonConfirmationConf())
						.sendTo(source);
				// Erreur : sql ou conf
				} else {
					source.sendMessage(this.help(source));
				}
			// Error : SQL disable
			} else {
				EPMessages.TRANSFERT_DISABLE.sendTo(source);
			}
		} else if (args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			//Si la base de donnée est activé
			if (this.plugin.getDataBases().isEnable()) {
				// Transféré vers une base de donnée SQL
				if (args.get(0).equalsIgnoreCase("sql")) {
					this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> commandSQL(source)).submit(this.plugin);
					return CompletableFuture.completedFuture(true);
				// Transféré vers un fichier de config
				} else if (args.get(0).equalsIgnoreCase("conf")) {
					this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> commandConf(source)).submit(this.plugin);
					return CompletableFuture.completedFuture(true);
				// Erreur : sql ou conf
				} else {
					source.sendMessage(this.help(source));
				}
			// Error : SQL disable
			} else {
				EPMessages.TRANSFERT_DISABLE.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}

	public Text getButtonConfirmationSQL(){
		return EPMessages.TRANSFERT_SQL_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EPMessages.TRANSFERT_SQL_CONFIRMATION_VALID_HOVER.getText()))
					.onClick(TextActions.runCommand("/permtransfer sql confirmation"))
					.build();
	}
	
	public Text getButtonConfirmationConf(){
		return EPMessages.TRANSFERT_CONF_CONFIRMATION_VALID.getText().toBuilder()
					.onHover(TextActions.showText(EPMessages.TRANSFERT_CONF_CONFIRMATION_VALID_HOVER.getText()))
					.onClick(TextActions.runCommand("/permtransfer conf confirmation"))
					.build();
	}
	
	/**
	 * Transféré les données des joueurs dans une base de donnée
	 * @param player Le joueur
	 * @return True si cela a correctement fonctionné
	 */
	private CompletableFuture<Boolean> commandSQL(final CommandSource player) {
		boolean resultat = false;
		/*
		Connection connection = null;
		PreparedStatement preparedPermissions = null;
		PreparedStatement preparedGroups = null;
		PreparedStatement preparedOptions = null;
		
		String queryPermissions = "INSERT INTO `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
								+ "VALUES (?, ?, ?, ?);";
		
		String queryGroups =  "INSERT INTO `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
							+ "VALUES (?, ?, ?, ?);";
		
		String queryOptions = "INSERT INTO `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
							+ "VALUES (?, ?, ?, ?);";
		
		try {
    		connection = this.plugin.getDataBases().getConnection();
    		
    		// Suppression des données qu'il y a actuellement dans base de données
    		if (this.plugin.getDataBases().clear(connection)) {
    			preparedPermissions = connection.prepareStatement(queryPermissions);
    			preparedGroups = connection.prepareStatement(queryGroups);
    			preparedOptions = connection.prepareStatement(queryOptions);
    			
    			File file = null;
    			ConfigurationNode config = null;
    			
    			// Pour tous les types de joueurs
    			for (String world : new HashSet<String>(this.plugin.getManagerData().get(PermissionService.SUBJECTS_USER).values())) {
    				file = this.plugin.getPath().resolve(EPManagerStorage.MKDIR_USERS + "/" + world + ".conf").toFile();
    				// Si le fichier existe
    				if (file.exists()) {
    					try {
							config = HoconConfigurationLoader.builder().setFile(file).build().load();

							// Pour tous les joueurs
		    				for (Entry<Object, ? extends ConfigurationNode> conf : config.getChildrenMap().entrySet()) {
		    					if (conf.getKey() instanceof String) {
		    						String user = (String) conf.getKey();
				    	    			
			    	    			// Chargement des permissions
			    	    			for (Entry<Object, ? extends ConfigurationNode> permission : conf.getValue().getNode("permissions").getChildrenMap().entrySet()) {
			    		    			if (permission.getKey() instanceof String && permission.getValue().getValue() instanceof Boolean) {
			    		    				preparedPermissions.setString(1, user);
			    		    				preparedPermissions.setString(2, world);
			    		    				preparedPermissions.setString(3, (String) permission.getKey());
			    		    				preparedPermissions.setBoolean(4, permission.getValue().getBoolean(false));
			    		    				preparedPermissions.execute();
			    		    			}
			    		    		}
			    	    			
			    	    			// Chargement des options
			    	    			for (Entry<Object, ? extends ConfigurationNode> option : conf.getValue().getNode("options").getChildrenMap().entrySet()) {
			    	    				String value = option.getValue().getString(null);
			    		    			if (option.getKey() instanceof String && value != null) {
			    		    				preparedOptions.setString(1, user);
			    		    				preparedOptions.setString(2, world);
			    		    				preparedOptions.setString(3, (String) option.getKey());
			    		    				preparedOptions.setString(4, value);
			    		    				preparedOptions.execute();
			    		    			}
			    		    		}
			    	    			
			    	    			// Chargement les sous-groupes
			    	    			try {
			    						for (String subgroup : conf.getValue().getNode("subgroups").getList(TypeToken.of(String.class))) {
			    							Subject subject = this.plugin.getService().getGroupSubjects().loadSubject(subgroup).join();
			    							if (subject != null) {
			    								preparedGroups.setString(1, user);
			    								preparedGroups.setString(2, world);
			    								preparedGroups.setString(3, subject.getIdentifier());
			    								preparedGroups.setBoolean(4, true);
			    								preparedGroups.execute();
			    							}
			    						}
			    					} catch (ObjectMappingException e) {}
			    	    			
			    	    			// Chargement du groupe
			    	    			String group = conf.getValue().getNode("group").getString(null);
			    	    			if (group != null) {
			    	    				Subject subject = this.plugin.getService().getGroupSubjects().loadSubject(group).join();
		    							if (subject != null) {
		    								preparedGroups.setString(1, user);
		    								preparedGroups.setString(2, world);
		    								preparedGroups.setString(3, subject.getIdentifier());
		    								preparedGroups.setBoolean(4, false);
		    								preparedGroups.execute();
		    							}
			    	    			}
				    	    	}
		    				}
    					} catch (IOException e) {
    						this.plugin.getELogger().warn("Error while loading the file '" + world + "': " + e.getMessage());
						}
    				}
    			}
    		}
    		resultat = true;
    	} catch (SQLException e) {
			this.plugin.getELogger().warn("Error during the transfer of the database : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedPermissions != null) preparedPermissions.close();
				if (preparedGroups != null) preparedGroups.close();
				if (preparedOptions != null) preparedOptions.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
		
		if (resultat) {
			this.plugin.getELogger().info(EPMessages.TRANSFERT_SQL_LOG.getString());
			EPMessages.TRANSFERT_SQL.sendTo(player);
		} else {
			EPMessages.TRANSFERT_ERROR.sendTo(player);
		}
		*/
		return CompletableFuture.completedFuture(resultat);
	}

	private void commandConf(final CommandSource source) {
		/*boolean resultat = false;
		
		Connection connection = null;
		PreparedStatement preparedPermissions = null;
		PreparedStatement preparedGroups = null;
		PreparedStatement preparedOptions = null;
		
		ResultSet list = null;
		EPConfUsers config = null;
		
		String queryPermissions = "SELECT *" 
								+ "FROM `" + this.plugin.getDataBases().getTableUsersPermissions() + "` "
								+ "WHERE `world` = ? ;";
		
		String queryGroups =  "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableUsersGroups() + "` "
							+ "WHERE `world` = ? ;";
		
		String queryOptions = "SELECT *" 
							+ "FROM `" + this.plugin.getDataBases().getTableUsersOptions() + "` "
							+ "WHERE `world` = ? ;";
		
		try {
    		connection = this.plugin.getDataBases().getConnection();

			preparedPermissions = connection.prepareStatement(queryPermissions);
			preparedGroups = connection.prepareStatement(queryGroups);
			preparedOptions = connection.prepareStatement(queryOptions);
			
			File file = null;
			
			// Pour tous les types de joueurs
			for (String world : new HashSet<String>(this.plugin.getManagerData().getWorldUsers().values())) {
				// Supprime le fichier s'il existe on le supprime
				file = this.plugin.getPath().resolve(EPManagerStorage.MKDIR_USERS + "/" + world + ".conf").toFile();
				if (file.exists()) {
					file.delete();
				}
				
				// Création du nouveau fichier
				config = new EPConfUsers(this.plugin, EPManagerStorage.MKDIR_USERS + "/" + world);
				config.getNode().setValue(null);
				
				// Chargement des permissions
				preparedPermissions.setString(1, world);
				list = preparedPermissions.executeQuery();
				while (list.next()) {
					config.getNode().getNode(list.getString("uuid")).getNode("permissions").getNode(list.getString("permission")).setValue(list.getBoolean("boolean"));
				}
				
				// Chargement des options
				preparedOptions.setString(1, world);
				list = preparedOptions.executeQuery();
				while (list.next()) {
					config.getNode().getNode(list.getString("uuid")).getNode("options").getNode(list.getString("option")).setValue(list.getString("value"));
				}
				
				// Chargement des groupes
				preparedGroups.setString(1, world);
				list = preparedGroups.executeQuery();
				while (list.next()) {
					// Si c'est un sous-groupe
					if (list.getBoolean("subgroup")) {
						Subject subject = this.plugin.getService().getGroupSubjects().loadSubject(list.getString("group")).join();
						if (subject != null) {
							List<String> subgroups;
							try {
								subgroups = new ArrayList<String>(config.getNode().getNode(list.getString("uuid")).getNode("subgroups").getList(TypeToken.of(String.class)));
								subgroups.add(list.getString("group"));
								config.getNode().getNode(list.getString("uuid")).getNode("subgroups").setValue(subgroups);
							} catch (ObjectMappingException e) {}
						}
					// Si c'est un groupe
					} else {
						Subject subject = this.plugin.getService().getGroupSubjects().loadSubject(list.getString("group")).join();
						if (subject != null) {
							config.getNode().getNode(list.getString("uuid")).getNode("group").setValue(subject.getIdentifier());
						}
					}
				}
				config.save(true);
			}
    		resultat = true;
    	} catch (SQLException e) {
			this.plugin.getELogger().warn("Error during the transfer of the database : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedPermissions != null) preparedPermissions.close();
				if (preparedGroups != null) preparedGroups.close();
				if (preparedOptions != null) preparedOptions.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
		
		if (resultat) {
			this.plugin.getELogger().info(EPMessages.TRANSFERT_CONF_LOG.getString());
			EPMessages.TRANSFERT_CONF.sendTo(source);
		} else {
			EPMessages.TRANSFERT_ERROR.sendTo(source);
		}
	}*/
	}
}
