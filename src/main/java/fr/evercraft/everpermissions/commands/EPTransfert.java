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
package fr.evercraft.everpermissions.commands;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.reflect.TypeToken;

import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.sponge.UtilsChat;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.data.EPConfUsers;
import fr.evercraft.everpermissions.data.EPManagerData;
import fr.evercraft.everpermissions.service.permission.subject.EGroupSubject;

public class EPTransfert extends ECommand<EverPermissions> {
	
	public EPTransfert(final EverPermissions plugin) {
        super(plugin, "permtransfer", "mantransfer");
    }

	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("TRANSFERT"));
	}

	public Text description(final CommandSource source) {
		return this.plugin.getMessages().getText("TRANSFERT_DESCRIPTION");
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
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests.add("sql");
			suggests.add("conf");
		}
		return suggests;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		if(args.size() == 1) {
			//Si la base de donnée est activé
			if(this.plugin.getManagerData().isSQL()) {
				// Transféré vers une base de donnée SQL
				if(args.get(0).equalsIgnoreCase("sql")) {
					source.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("TRANSFERT_SQL_CONFIRMATION"))
							.replace("<confirmation>", getButtonConfirmationSQL())
							.build());
				// Transféré vers un fichier de config
				} else if(args.get(0).equalsIgnoreCase("conf")) {
					source.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getText("PREFIX"))
							.append(this.plugin.getMessages().getMessage("TRANSFERT_CONF_CONFIRMATION"))
							.replace("<confirmation>", getButtonConfirmationConf())
							.build());
				// Erreur : sql ou conf
				} else {
					source.sendMessage(help(source));
				}
			// Error : SQL disable
			} else {
				source.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("TRANSFERT_DISABLE")));
			}
		} else if(args.size() == 2 && args.get(1).equalsIgnoreCase("confirmation")) {
			//Si la base de donnée est activé
			if(this.plugin.getManagerData().isSQL()) {
				// Transféré vers une base de donnée SQL
				if(args.get(0).equalsIgnoreCase("sql")) {
					this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> commandSQL(source)).submit(this.plugin);
					resultat = true;
				// Transféré vers un fichier de config
				} else if(args.get(0).equalsIgnoreCase("conf")) {
					this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> commandConf(source)).submit(this.plugin);
					resultat = true;
				// Erreur : sql ou conf
				} else {
					source.sendMessage(help(source));
				}
			// Error : SQL disable
			} else {
				source.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("TRANSFERT_DISABLE")));
			}
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}

	public Text getButtonConfirmationSQL(){
		return this.plugin.getMessages().getText("TRANSFERT_SQL_CONFIRMATION_VALID").toBuilder()
					.onHover(TextActions.showText(UtilsChat.of(this.plugin.getMessages().getMessage("TRANSFERT_SQL_CONFIRMATION_VALID_HOVER"))))
					.onClick(TextActions.runCommand("/permtransfer sql confirmation"))
					.build();
	}
	
	public Text getButtonConfirmationConf(){
		return this.plugin.getMessages().getText("TRANSFERT_CONF_CONFIRMATION_VALID").toBuilder()
					.onHover(TextActions.showText(UtilsChat.of(this.plugin.getMessages().getMessage("TRANSFERT_CONF_CONFIRMATION_VALID_HOVER"))))
					.onClick(TextActions.runCommand("/permtransfer conf confirmation"))
					.build();
	}
	
	/**
	 * Transféré les données des joueurs dans une base de donnée
	 * @param player Le joueur
	 * @return True si cela a correctement fonctionné
	 */
	private boolean commandSQL(final CommandSource player) {
		boolean resultat = false;
		
		Connection connection = null;
		PreparedStatement preparedPermissions = null;
		PreparedStatement preparedGroups = null;
		PreparedStatement preparedOptions = null;
		
		String queryPermissions = "INSERT INTO `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
								+ "VALUES (?, ?, ?, ?);";
		
		String queryGroups =  "INSERT INTO `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
							+ "VALUES (?, ?, ?, ?);";
		
		String queryOptions = "INSERT INTO `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
							+ "VALUES (?, ?, ?, ?);";
		
		try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();
    		
    		// Suppression des données qu'il y a actuellement dans base de données
    		if(this.plugin.getManagerData().getDataBases().clear(connection)) {
    			preparedPermissions = connection.prepareStatement(queryPermissions);
    			preparedGroups = connection.prepareStatement(queryGroups);
    			preparedOptions = connection.prepareStatement(queryOptions);
    			
    			File file = null;
    			ConfigurationNode config = null;
    			
    			// Pour tous les types de joueurs
    			for(String world : new HashSet<String>(this.plugin.getManagerData().getWorldUsers().values())) {
    				file = this.plugin.getPath().resolve(EPManagerData.MKDIR_USERS + "/" + world + ".conf").toFile();
    				// Si le fichier existe
    				if(file.exists()) {
    					try {
							config = HoconConfigurationLoader.builder().setFile(file).build().load();

							// Pour tous les joueurs
		    				for (Entry<Object, ? extends ConfigurationNode> conf : config.getChildrenMap().entrySet()) {
		    					if(conf.getKey() instanceof String) {
		    						String user = (String) conf.getKey();
				    	    			
			    	    			// Chargement des permissions
			    	    			for (Entry<Object, ? extends ConfigurationNode> permission : conf.getValue().getNode("permissions").getChildrenMap().entrySet()) {
			    		    			if(permission.getKey() instanceof String && permission.getValue().getValue() instanceof Boolean) {
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
			    		    			if(option.getKey() instanceof String && value != null) {
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
			    							EGroupSubject subject = this.plugin.getService().getGroupSubjects().get(subgroup);
			    							if(subject != null) {
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
			    	    			if(group != null) {
			    	    				EGroupSubject subject = this.plugin.getService().getGroupSubjects().get(group);
		    							if(subject != null) {
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
    						this.plugin.getLogger().warn("Error while loading the file '" + world + "': " + e.getMessage());
						}
    				}
    			}
    		}
    		resultat = true;
    	} catch (SQLException e) {
			this.plugin.getLogger().warn("Error during the transfer of the database : " + e.getMessage());
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
		
		if(resultat) {
			this.plugin.getLogger().info(this.plugin.getMessages().getMessage("TRANSFERT_SQL"));
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("TRANSFERT_SQL")));
		} else {
			player.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("TRANSFERT_ERROR")));
		}
		
		return resultat;
	}

	private void commandConf(final CommandSource source) {
		boolean resultat = false;
		
		Connection connection = null;
		PreparedStatement preparedPermissions = null;
		PreparedStatement preparedGroups = null;
		PreparedStatement preparedOptions = null;
		
		ResultSet list = null;
		EPConfUsers config = null;
		
		String queryPermissions = "SELECT *" 
								+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersPermissions() + "` "
								+ "WHERE `world` = ? ;";
		
		String queryGroups =  "SELECT *" 
							+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersGroups() + "` "
							+ "WHERE `world` = ? ;";
		
		String queryOptions = "SELECT *" 
							+ "FROM `" + this.plugin.getManagerData().getDataBases().getTableUsersOptions() + "` "
							+ "WHERE `world` = ? ;";
		
		try {
    		connection = this.plugin.getManagerData().getDataBases().getConnection();

			preparedPermissions = connection.prepareStatement(queryPermissions);
			preparedGroups = connection.prepareStatement(queryGroups);
			preparedOptions = connection.prepareStatement(queryOptions);
			
			File file = null;
			
			// Pour tous les types de joueurs
			for(String world : new HashSet<String>(this.plugin.getManagerData().getWorldUsers().values())) {
				// Supprime le fichier s'il existe on le supprime
				file = this.plugin.getPath().resolve(EPManagerData.MKDIR_USERS + "/" + world + ".conf").toFile();
				if(file.exists()) {
					file.delete();
				}
				
				// Création du nouveau fichier
				config = new EPConfUsers(this.plugin, EPManagerData.MKDIR_USERS + "/" + world);
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
					if(list.getBoolean("subgroup")) {
						EGroupSubject subject = this.plugin.getService().getGroupSubjects().get(list.getString("group"));
						if(subject != null) {
							List<String> subgroups;
							try {
								subgroups = config.getNode().getNode(list.getString("uuid")).getNode("subgroups").getList(TypeToken.of(String.class));
								subgroups.add(list.getString("group"));
								config.getNode().getNode(list.getString("uuid")).getNode("subgroups").setValue(subgroups);
							} catch (ObjectMappingException e) {}
						}
					// Si c'est un groupe
					} else {
						EGroupSubject subject = this.plugin.getService().getGroupSubjects().get(list.getString("group"));
						if(subject != null) {
							config.getNode().getNode(list.getString("uuid")).getNode("group").setValue(subject.getIdentifier());
						}
					}
				}
				config.save();
			}
    		resultat = true;
    	} catch (SQLException e) {
			this.plugin.getLogger().warn("Error during the transfer of the database : " + e.getMessage());
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
		
		if(resultat) {
			this.plugin.getLogger().info(this.plugin.getMessages().getMessage("TRANSFERT_CONF"));
			source.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("TRANSFERT_CONF")));
		} else {
			source.sendMessage(this.plugin.getMessages().getText("PREFIX").concat(this.plugin.getMessages().getText("TRANSFERT_ERROR")));
		}
	}
	
}
