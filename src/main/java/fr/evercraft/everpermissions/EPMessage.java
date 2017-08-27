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

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.message.EMessageBuilder;
import fr.evercraft.everapi.message.EMessageFormat;
import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EPMessage extends EMessage<EverPermissions> {

	public EPMessage(final EverPermissions plugin) {
		super(plugin, EPMessages.values());
	}
	
	public enum EPMessages implements EnumMessage {
		// Plugin :
		PREFIX("PREFIX", 												"[&4Ever&6&lPermissions&f] "),
		DESCRIPTION("DESCRIPTION", 										"Gestion des permissions", 
																		"Permissions management"),
		GROUP_NOT_FOUND_WORLD("plugin.messagesGroupNotFoundWorld", 		"&cErreur : Il n'existe pas de groupe &6<group> &cdans les mondes de type &6<type>&c.",
																		"&cError : There is no group &6<group> &cin type worlds &6<type>&c."),
		ERROR_BOOLEAN("plugin.messagesErrorBoolean", 					"&cErreur : Une permission ne peut-être que &6&lTrue &cou &6&lFalse",
																		"&cError : Permission may only &6&lTrue &cor &6&lFalse"),
		WORLD_EMPTY("plugin.messages.worldEmpty", 						"&cVous devez préciser le nom du monde.", 
																		"&cYou must specify the name of the world."),
		
		// Commands :		
		TRANSFERT_DESCRIPTION("commandsTransfertDescription",								"Transfère les données des joueurs",
																							"Transfers the data of the players"),
		TRANSFERT_SQL_CONFIRMATION("commandsTransfertSqlConfirmation", 						"&7Souhaitez-vous vraiment transférer les données des joueurs dans une base de données &6SQL&7 ? <confirmation>"),
		TRANSFERT_SQL_CONFIRMATION_VALID("commandsTransfertSqlConfirmationValid", 			"&2&nConfirmer"),
		TRANSFERT_SQL_CONFIRMATION_VALID_HOVER("commandsTransfertSqlConfirmationValidHover","&cCliquez ici pour réaliser le transfert"),
		TRANSFERT_CONF_CONFIRMATION("commandsTransfertConfConfirmation", 					"&7Souhaitez-vous vraiment transférer les données des joueurs dans des &6fichiers de configuration&7 ? <confirmation>"),
		TRANSFERT_CONF_CONFIRMATION_VALID("commandsTransfertConfConfirmationValid", 		"&2&nConfirmer"),
		TRANSFERT_CONF_CONFIRMATION_VALID_HOVER("commandsTransfertConfConfirmationValidHover", 	"&cCliquez ici pour réaliser le transfert"),
		TRANSFERT_SQL("commandsTransfertSql", 												"&7Les données des joueurs ont bien été transférées dans la base de données."),
		TRANSFERT_SQL_LOG("commandsTransfertSqlLog", 										"&7Les données des joueurs ont bien été transférées dans la base de données."),
		TRANSFERT_CONF("commandsTransfertConf", 											"&7Les données des joueurs ont bien été transférées dans les fichiers de configurations."),
		TRANSFERT_CONF_LOG("commandsTransfertConfLog", 										"&7Les données des joueurs ont bien été transférées dans les fichiers de configurations."),
		TRANSFERT_DISABLE("commandsTransfertDisable", 										"&cErreur : Vous devez être connecté à une base de données pour faire le transfert des données."),
		TRANSFERT_ERROR("commandsTransfertError", 											"&cErreur : Pendant le transfert des données."),
		
		// Commands : Disable
		COMMAND_OP("commandsOp", 										"&cVous possédez un plugin de permission donc être opérateur n'a aucune influence."),
		COMMAND_DEOP("commandsDeop", 									"&cVous possédez un plugin de permission donc être opérateur n'a aucune influence."),
		
		// User :
		USER_CLEAR_DESCRIPTION("userClearDescription", 					"Supprime un joueur"),
		USER_CLEAR_STAFF("userClearStaff", 								"&7Vous avez réinitialisé toutes les données de &6<player>&7."),
		USER_CLEAR_PLAYER("userClearPlayer", 							"&7Vos données ont été réinitialisé."),
		USER_CLEAR_EQUALS("userClearEquals", 							"&7Vous avez réinitialisé toutes vos données."),
		USER_CLEAR_BROADCAST_PLAYER("userClearBroadcastPlayer", 		"&7Tous les données de &6<player> &7 ont été réinitialisé par &6<staff>&7."),
		USER_CLEAR_BROADCAST_EQUALS("userClearBroadcastEquals", 		"&7Tous les données de &6<player> &7 ont été réinitialisé."),

		// User : Group
		USER_ADD_GROUP_DESCRIPTION("userGroupAddDescription", 			"Défini le groupe d'un joueur",
																		"Define the group of a player"),
		USER_ADD_GROUP_STAFF("userGroupAddStaff", 						"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_GROUP_PLAYER("userGroupAddPlayer", 					"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_GROUP_EQUALS("userGroupAddEquals", 					"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_GROUP_BROADCAST_PLAYER("userGroupAddBroadcastPlayer", 	"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_GROUP_BROADCAST_EQUALS("userGroupAddBroadcastEquals", 	"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_GROUP_ERROR_STAFF("userGroupAddErrorStaff", 			"&cErreur : &6<player> &cest déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_ADD_GROUP_ERROR_EQUALS("userGroupAddErrorEquals", 			"&cErreur : Vous êtes déjà &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_DEL_GROUP_DESCRIPTION("userGroupDelDescription", 			"Supprime le groupe d'un joueur",
																		"Removes the group of a player"),
		USER_DEL_GROUP_STAFF("userGroupDelStaff", 						"&6<player> &7n'est plus &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_GROUP_PLAYER("userGroupDelPlayer", 					"&7Vous n'êtes plus &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_GROUP_EQUALS("userGroupDelEquals", 					"&7Vous n'êtes plus &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_GROUP_BROADCAST_PLAYER("userGroupDelBroadcastPlayer", 	"&6<player> &7n'est plus &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_GROUP_BROADCAST_EQUALS("userGroupDelBroadcastEquals", 	"&6<player> &7n'est plus &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_GROUP_ERROR_STAFF("userGroupDelErrorStaff", 			"&cErreur : &6<player> &cn'a pas de groupe &cdans les mondes de type &6<type>&c."),
		USER_DEL_GROUP_ERROR_EQUALS("userGroupDelErrorEquals", 			"&cErreur : Vous n'avez pas de groupe dans les mondes de type &6<type>&c."),
		
		USER_ADD_SUBGROUP_DESCRIPTION("userGroupAddsubDescription", 		"Ajoute un sous-groupe à un joueur",
																			"Add a group to a player's subgroup list"),
		USER_ADD_SUBGROUP_STAFF("userGroupAddsubStaff", 					"&6<player> &7possède désormais les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_SUBGROUP_PLAYER("userGroupAddsubPlayer", 					"&7Vous possédez désormais les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_ADD_SUBGROUP_EQUALS("userGroupAddsubEquals", 					"&7Vous possédez désormais les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_SUBGROUP_BROADCAST_PLAYER("userGroupAddsubBroadcastPlayer", "&6<player> &7possède désormais les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_ADD_SUBGROUP_BROADCAST_EQUALS("userGroupAddsubBroadcastEquals", "&6<player> &7possède désormais les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_SUBGROUP_ERROR_STAFF("userGroupAddsubErrorStaff", 			"&cErreur : &6<player> &cpossède déjà les droits &6<group> &cdans les mondes de type &6<type>&c."),
		USER_ADD_SUBGROUP_ERROR_EQUALS("userGroupAddsubErrorEquals", 		"&cErreur : Vous possédez déjà les droits &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_DEL_SUBGROUP_DESCRIPTION("userGroupDelsubDescription", 		"Supprime un sous-groupe à un joueur",
																			"Remove a group from a player's subgroup list"),
		USER_DEL_SUBGROUP_STAFF("userGroupDelsubStaff", 					"&6<player> &7ne possède plus les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_SUBGROUP_PLAYER("userGroupDelsubPlayer", 					"&7Vous ne possédez plus les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_SUBGROUP_EQUALS("userGroupDelsubEquals", 					"&7Vous ne possédez plus les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_SUBGROUP_BROADCAST_PLAYER("userGroupDelsubBroadcastPlayer", "&6<player> &7ne possède plus les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_SUBGROUP_BROADCAST_EQUALS("userGroupDelsubBroadcastEquals", "&6<player> &7ne possède plus les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_SUBGROUP_ERROR_STAFF("userGroupDelsubErrorStaff", 			"&cErreur : &6<player> &cne possède pas les droits &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEL_SUBGROUP_ERROR_EQUALS("userGroupDelsubErrorEquals", 		"&cErreur : Vous ne possédez pas les droits &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_PROMOTE_DESCRIPTION("userGroupPromoteDescription", 					"Promouvoit un joueur",
																					"Allows promoting a player up the inheritance tree."),
		USER_PROMOTE_STAFF("userGroupPromoteStaff", 								"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_PROMOTE_PLAYER("userGroupPromotePlayer", 								"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_PROMOTE_EQUALS("userGroupPromoteEquals", 								"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_PROMOTE_BROADCAST_PLAYER("userGroupPromoteBroadcastPlayer", 			"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_PROMOTE_BROADCAST_EQUALS("userGroupPromoteBroadcastEquals", 			"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_PROMOTE_ERROR_STAFF("userGroupPromoteErrorStaff",  					"&cErreur : &6<player> &cest déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_PROMOTE_ERROR_EQUALS("userGroupPromoteErrorEquals", 					"&cErreur : Vous êtes déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_PROMOTE_ERROR_DEMOTE_STAFF("userGroupPromoteErrorgroupDemoteStaff", 	"&cErreur : &6<player> &cpossède déjà un grade supérieur à &6<group> &cdans les mondes de type &6<type>&c."),
		USER_PROMOTE_ERROR_DEMOTE_EQUALS("userGroupPromoteErrorgroupDemoteEquals", 	"&cErreur : Vous possédez déjà un grade supérieur à &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_DEMOTE_DESCRIPTION("userGroupDemoteDescription", 					"Rétrograde un joueur",
																				"Allows demoting a player down the inheritance tree."),
		USER_DEMOTE_STAFF("userGroupDemoteStaff", 								"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEMOTE_PLAYER("userGroupDemotePlayer", 							"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_DEMOTE_EQUALS("userGroupDemoteEquals", 							"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEMOTE_BROADCAST_PLAYER("userGroupDemoteBroadcastPlayer", 			"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEMOTE_BROADCAST_EQUALS("userGroupDemoteBroadcastEquals", 			"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_DEMOTE_ERROR_STAFF("userGroupDemoteErrorStaff", 					"&cErreur : &6<player> &cest déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEMOTE_ERROR_EQUALS("userGroupDemoteErrorEquals",					"&cErreur : Vous êtes déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEMOTE_ERROR_PROMOTE_STAFF("userGroupDemoteErrorPromoteStaff", 	"&cErreur : &6<player> &cpossède déjà un grade inférieur à &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEMOTE_ERROR_PROMOTE_EQUALS("userGroupDemoteErrorPromoteEquals",	"&cErreur : Vous possédez déjà un grade inférieur à &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_LIST_GROUP_DESCRIPTION("userGroupListDescription", 				"Affiche la liste des groupes d'un joueur",
																				"Tell the group that this user belongs to"),
		USER_LIST_GROUP_TITLE("userGroupListTitle", 							"&aLes groupes de &6<player> &a: &6<type>"),
		USER_LIST_GROUP_GROUP("userGroupListGroup", 							"    &6&l➤  Groupe : &7<group>"),
		USER_LIST_GROUP_GROUP_EMPTY("userGroupListGroupEmpty", 					"    &6&l➤  Groupe : &7Aucun"),
		USER_LIST_GROUP_SUBGROUP("userGroupListSubgroup", 						"    &6&l➤  Les sous-groupes : "),
		USER_LIST_GROUP_SUBGROUP_LINE("userGroupListSubgroupLine", 				"        &7&l●  &7<group>"),
		USER_LIST_GROUP_SUBGROUP_EMPTY("userGroupListSubgroupEmpty", 			"    &6&l➤  Sous-groupes : &7Aucun"),
		USER_LIST_GROUP_TRANSIENT_GROUP("userGroupListTranstienGroup", 					"    &6&l➤  Groupe temporaire : &7<group>"),
		USER_LIST_GROUP_TRANSIENT_SUBGROUP("userGroupListTransientSubgroup", 			"    &6&l➤  Les groupes temporaires : "),
		USER_LIST_GROUP_TRANSIENT_SUBGROUP_LINE("userGroupListTransientSubgroupLine", 	"        &7&l●  &7<group>"),
		
		// User : Permission
		USER_ADD_PERMISSION_DESCRIPTION("userPermissionAddDescription", 					"Ajoute une permission à un joueur",
																							"Add permission directly to the player"),
		USER_ADD_PERMISSION_TRUE_STAFF("userPermissionAddTrueStaff", 						"&6<player> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_TRUE_EQUALS("userPermissionAddTrueEquals", 						"&7Vous possédez désormais la permission '&6<permission>&7' &7dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_TRUE_BROADCAST_PLAYER("userPermissionAddTrueBroadcastPlayer",	"&6<player> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_PERMISSION_TRUE_BROADCAST_EQUALS("userPermissionAddTrueBroadcastEquals", 	"&6<player> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_TRUE_ERROR_STAFF("userPermissionAddTrueErrorStaff", 			"&cErreur : &6<player> &cpossède déjà la permissions '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_ADD_PERMISSION_TRUE_ERROR_EQUALS("userPermissionAddTrueErrorEquals", 			"&cErreur : Vous possédez déjà la permissions '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_ADD_PERMISSION_FALSE_STAFF("userPermissionAddFalseStaff", 						"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_FALSE_EQUALS("userPermissionAddFalseEquals", 					"&7Vous ne possédez plus la permission '&6<permission>&7' &7dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_FALSE_BROADCAST_PLAYER("userPermissionAddFalseBroadcastPlayer",	"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_PERMISSION_FALSE_BROADCAST_EQUALS("userPermissionAddFalseBroadcastEquals", "&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_FALSE_ERROR_STAFF("userPermissionAddFalseErrorStaff", 			"&cErreur : &6<player> &cne possède pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_ADD_PERMISSION_FALSE_ERROR_EQUALS("userPermissionAddFalseErrorEquals",			"&cErreur : Vous ne possédez pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		
		USER_DEL_PERMISSION_DESCRIPTION("userPermissionDelDescription", 					"Retire une permission à un joueur",
																							"Removes permission directly from the player"),
		USER_DEL_PERMISSION_STAFF("userPermissionDelStaff", 								"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_DEL_PERMISSION_EQUALS("userPermissionDelEquals", 								"&7Vous ne possédez plus la permission '&6<permission>&7' &7dans les mondes de type &6<type>&7."),
		USER_DEL_PERMISSION_BROADCAST_PLAYER("userPermissionDelBroadcastPlayer",			"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_DEL_PERMISSION_BROADCAST_EQUALS("userPermissionDelBroadcastEquals", 			"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_DEL_PERMISSION_ERROR_STAFF("userPermissionDelErrorStaff", 						"&cErreur : &6<player> &cne possède pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_DEL_PERMISSION_ERROR_EQUALS("userPermissionDelErrorEquals", 					"&cErreur : Vous ne possédez pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		
		USER_CHECK_PERMISSION_DESCRIPTION("userPermissionCheckDescription", 				"Vérifie si un joueur a une permission",
																							"Verify if user has a permission"),
		USER_CHECK_PERMISSION_TRUE_EQUALS("userPermissionCheckTrueEquals", 					"&7Vous possédez la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_TRUE_STAFF("userPermissionCheckTrueStaff", 					"&6<player> &7possède la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_FALSE_EQUALS("userPermissionCheckFalseEquals", 				"&7Vous ne possédez pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_FALSE_STAFF("userPermissionCheckFalseStaff", 					"&6<player> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_UNDEFINED_EQUALS("userPermissionCheckUndefinedEquals",		"&7Vous ne possédez pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_UNDEFINED_STAFF("userPermissionCheckUndefinedStaff", 			"&6<player> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		
		USER_LIST_PERMISSION_DESCRIPTION("userPermissionListDescription", 					"Affiche la liste des permissions d'un joueur",
																							"List all permissions from a player"),
		USER_LIST_PERMISSION_TITLE("userPermissionListTitle", 								"&aLes permissions de &6<player> &a: &6<type>"),
		USER_LIST_PERMISSION_PERMISSION("userPermissionListPermission", 					"    &6&l➤  Permissions : "),
		USER_LIST_PERMISSION_PERMISSION_LINE_TRUE("userPermissionListPermissionLineTrue", 	"        &7&l●  &7<permission> : True"),
		USER_LIST_PERMISSION_PERMISSION_LINE_FALSE("userPermissionListPermissionLineFalse", "        &7&l●  &7<permission> : False"),
		USER_LIST_PERMISSION_PERMISSION_EMPTY("userPermissionListPermissionEmpty", 			"    &6&l➤ Permissions : &7Aucune"),
		USER_LIST_PERMISSION_TRANSIENT("userPermissionListTransient", 						"    &6&l➤ Permissions temporaires : "),
		USER_LIST_PERMISSION_TRANSIENT_LINE_TRUE("userPermissionListTransientLineTrue", 	"        &7&l●  &7<permission> : True"),
		USER_LIST_PERMISSION_TRANSIENT_LINE_FALSE("userPermissionListTransientLineFalse", 	"        &7&l●  &7<permission> : False"),
		
		// User : Option
		USER_ADD_OPTION_DESCRIPTION("userOptionAddDescription", 				"Ajoute ou remplace une option à un joueur",
																				"Add, or replaces, a option to a user"),
		USER_ADD_OPTION_STAFF("userOptionAddStaff", 							"&6<player> &7possède désormais l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_OPTION_EQUALS("userOptionAddEquals", 							"&7Vous possédez désormais l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		
		USER_DEL_OPTION_DESCRIPTION("userOptionDelDescription", 				"Supprime une option à un joueur",
																				"Remove a option from a user."),
		USER_DEL_OPTION_STAFF("userOptionDelStaff", 							"&6<player> &7ne possède plus l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		USER_DEL_OPTION_EQUALS("userOptionDelEquals", 							"&7Vous ne possédez plus l'option '&6<option>&7' &7dans les mondes de type &6<type>&7."),
		USER_DEL_OPTION_ERROR_STAFF("userOptionDelErrorStaff", 					"&cErreur : &6<player> &cne possède pas l'option '&6<option>&c' dans les mondes de type &6<type>&c."),
		USER_DEL_OPTION_ERROR_EQUALS("userOptionDelErrorEquals", 				"&cErreur : Vous ne possédez pas l'option '&6<option>&c' dans les mondes de type &6<type>&c."),
		
		USER_CHECK_OPTION_DESCRIPTION("userOptionCheckDescription", 			"Vérifie si un joueur a une option",
																				"Verify a value of a option of user"),
		USER_CHECK_OPTION_DEFINED_STAFF("userOptionCheckDefineStaff", 			"&6<player> &7possède l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_OPTION_DEFINED_EQUALS("userOptionCheckDefineEquals", 		"&7Vous possédez l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_OPTION_UNDEFINED_EQUALS("userOptionCheckUndefinedStaff", 	"&6<player> &7ne possède pas l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_OPTION_UNDEFINED_STAFF("userOptionCheckUndefinedEquals", 	"&7Vous ne possédez pas l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		
		USER_LIST_OPTION_DESCRIPTION("userOptionListDescription", 				"Affiche la liste des options d'un joueur",
																				"List variables a user has"),
		USER_LIST_OPTION_TITLE("userOptionListTitle", 							"&aLes options de &6<player> &a: &6<type>"),
		USER_LIST_OPTION_OPTION("userOptionListOption", 						"    &6&l➤  Options : "),
		USER_LIST_OPTION_OPTION_LINE("userOptionListOptionLine", 				"        &7&l●  &7<option> : &7'<value>&7'"),
		USER_LIST_OPTION_OPTION_EMPTY("userOptionListOptionEmpty", 				"    &6&l➤ Options : &7Aucune"),
		USER_LIST_OPTION_TRANSIENT("userOptionListTransient", 					"    &6&l➤ Options temporaires : "),
		USER_LIST_OPTION_TRANSIENT_LINE("userOptionListTransientLine", 			"        &7&l●  &7<option> : &7'<value>&7'"),
		
		// Group : Group
		GROUP_ADD_GROUP_DESCRIPTION("groupGroupAddDescription", 				"Ajoute un groupe à un monde"),
		GROUP_ADD_GROUP_STAFF("groupGroupAddStaff", 							"&7Vous avez créé le groupe &6<group> &7dans les mondes de type &6<type>&7."),
		GROUP_ADD_GROUP_ERROR("groupGroupAddError", 							"&cIl existe déjà un groupe &6<group> &cdans les mondes de type &6<type>&c."),
		
		GROUP_DEL_GROUP_DESCRIPTION("groupGroupDelDescription", 				"Supprime un groupe d'un monde"),
		GROUP_DEL_GROUP_STAFF("groupGroupDelStaff", 							"&7Vous avez supprimé le groupe &6<group> &7dans les mondes de type &6<type>&7."),
		
		GROUP_DEFAULT_GROUP_DESCRIPTION("groupGroupDefaultDescription", 		"Définit un groupe par défaut"),
		GROUP_DEFAULT_GROUP_TRUE("groupGroupDefaultTrue", 						"&7Le groupe &6<group> &7est désormais le groupe par défaut &7dans les mondes de type &6<type>&7."),
		GROUP_DEFAULT_GROUP_FALSE("groupGroupDefaultFalse", 					"&7Le groupe &6<group> &7n'est plus le groupe par défaut &7dans les mondes de type &6<type>&7."),
		GROUP_DEFAULT_GROUP_ERROR_TRUE("groupGroupDefaultErrorTrue", 			"&cIl existe déjà un groupe par défaut &cdans les mondes de type &6<type>&c."),
		GROUP_DEFAULT_GROUP_ERROR_FALSE("groupGroupDefaultErrorFalse", 			"&cLe groupe &6<group> &cn'est pas le groupe défaut &cdans les mondes de type &6<type>&c."),
		GROUP_DEFAULT_GROUP_ERROR_EQUALS("groupGroupDefaultErrorEquals", 		"&cLe groupe &6<group> &cest déjà la groupe par défaut &cdans les mondes de type &6<type>&c."),
		GROUP_DEFAULT_GROUP_ERROR_BOOLEAN("groupGroupDefaultErrorBoolean", 		"&cErreur : La valeur 'default' ne peut-être que &6&lTrue &cou &6&lFalse"),
		
		GROUP_LIST_GROUP_DESCRIPTION("groupGroupListDescription",				"Affiche la liste des groupes d'un monde"),
		GROUP_LIST_GROUP_TITLE("groupGroupListTitle", 							"&aLes groupes de type &6<type>"),
		GROUP_LIST_GROUP_DEFAULT("groupGroupListDefault", 						"    &6&l➤  Le groupe par défaut : &7<group>"),
		GROUP_LIST_GROUP_NAME("groupGroupListName", 							"    &6&l➤  Les groupes : "),
		GROUP_LIST_GROUP_LINE("groupGroupListLine", 							"        &7&l●  &7<group>"),
		GROUP_LIST_GROUP_EMPTY("groupGroupListEmpty", 							"    &6&l➤  Groupes : &7Aucun"),
		
		// Group : Inheritance
		GROUP_ADD_INHERITANCE_DESCRIPTION("groupAddInheritanceDescription", 	"Ajoute une inhéritance à un groupe"),
		GROUP_ADD_INHERITANCE_STAFF("groupAddInheritanceStaff",					"&7Vous avez ajouté l'inhéritance &6<inheritance> &7au groupe &6<group> &7dans les mondes de type &6<type>&7."),
		GROUP_ADD_INHERITANCE_ERROR_HAVE("groupAddInheritanceErrorHave", 		"&cLe groupe &6<group> &cpossède déjà l'inhéritance &6<inheritance> &cdans les mondes de type &6<type>&c."),
		GROUP_ADD_INHERITANCE_ERROR_EQUALS("groupAddInheritanceErrorEquals", 	"&cL'inhéritance &6<inheritance> &cne peut pas être ajouté au groupe &6<group>&c."),
		
		GROUP_DEL_INHERITANCE_DESCRIPTION("groupDelInheritanceDescription", 	"Supprime une inhéritance d'un groupe"),
		GROUP_DEL_INHERITANCE_STAFF("groupDelInheritanceStaff", 				"&7Vous avez supprimé l'inhéritance &6<inheritance> &7au groupe &6<group> &7dans les mondes de type &6<type>&7."),
		GROUP_DEL_INHERITANCE_ERROR("groupDelInheritanceErrorHave", 			"&cLe groupe &6<group> &cne possède pas l'inhéritance &6<inheritance> &cdans les mondes de type &6<type>&c."),
		
		GROUP_LIST_INHERITANCE_DESCRIPTION("groupListInheritanceDescription", 			"Affiche la liste des inhéritances d'un groupe"),
		GROUP_LIST_INHERITANCE_TITLE("groupListInheritanceTitle", 						"&aLes inhéritances du groupe &6<group> &a: &6<type>"),
		GROUP_LIST_INHERITANCE_INHERITANCE("groupListInheritanceInheritance", 			"    &6&l➤  Les inhéritances : "),
		GROUP_LIST_INHERITANCE_INHERITANCE_LINE("groupListInheritanceInheritanceLine", 	"        &7&l●  &7<inheritance>"),
		GROUP_LIST_INHERITANCE_INHERITANCE_EMPTY("groupListInheritanceInheritanceEmpty", "    &6&l➤  Inhéritance : &7Aucune"),
		GROUP_LIST_INHERITANCE_TRANSIENT("groupListInheritanceTransient", 				"    &6&l➤  Les inhéritances temporaires : "),
		GROUP_LIST_INHERITANCE_TRANSIENT_LINE("groupListInheritanceTransientLine", 		"        &7&l●  &7<inheritance>"),
		
		// Group : Permission
		GROUP_ADD_PERMISSION_DESCRIPTION("groupPermissionAddDescription", 	"Ajoute une permission à un groupe"),
		GROUP_ADD_PERMISSION_TRUE("groupPermissionAddTrue", 				"&7Le groupe &6<group> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_ADD_PERMISSION_FALSE("groupPermissionAddFalse", 				"&7Le groupe &6<group> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_ADD_PERMISSION_ERROR_TRUE("groupPermissionAddErrorTrue", 		"&7Le groupe &6<group> &7possède déjà la permission '&6<permission>&7' dans les mondes de type &6<type>&c."),
		GROUP_ADD_PERMISSION_ERROR_FALSE("groupPermissionAddErrorFalse", 	"&7Le groupe &6<group> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&c."),
		
		GROUP_DEL_PERMISSION_DESCRIPTION("groupPermissionDelDescription", 	"Retire une permission à un groupe"),
		GROUP_DEL_PERMISSION_STAFF("groupPermissionDelStaff", 				"&6<group> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_DEL_PERMISSION_ERROR("groupPermissionDelError", 				"&cErreur : &6<group> &cne possède pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		
		GROUP_CHECK_PERMISSION_DESCRIPTION("groupPermissionCheckDescription","Vérifie si un groupe a une permission"),
		GROUP_CHECK_PERMISSION_TRUE("groupPermissionCheckTrue", 			"&7Le groupe &6<group> &7possède la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_CHECK_PERMISSION_FALSE("groupPermissionCheckFalse", 			"&7Le groupe &6<group> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_CHECK_PERMISSION_UNDEFINED("groupPermissionCheckUndefined", 	"&7Le groupe &6<group> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		
		GROUP_LIST_PERMISSION_DESCRIPTION("groupPermissionListDescription", 					"Affiche la liste des permissions d'un groupe"),
		GROUP_LIST_PERMISSION_TITLE("groupPermissionListTitle", 								"&aLes permissions du groupe &6<group> &a: &6<type>"),
		GROUP_LIST_PERMISSION_PERMISSION("groupPermissionListPermission", 						"    &6&l➤  Permissions : "),
		GROUP_LIST_PERMISSION_PERMISSION_LINE_TRUE("groupPermissionListPermissionLineTrue",		"        &7&l●  &7<permission> : True"),
		GROUP_LIST_PERMISSION_PERMISSION_LINE_FALSE("groupPermissionListPermissionLineFalse",	"        &7&l●  &7<permission> : False"),
		GROUP_LIST_PERMISSION_PERMISSION_EMPTY("groupPermissionListPermissionEmpty", 			"    &6&l➤ Permissions : &7Aucune"),
		GROUP_LIST_PERMISSION_TRANSIENT("groupPermissionListTransient", 						"    &6&l➤ Permissions temporaires : "),
		GROUP_LIST_PERMISSION_TRANSIENT_LINE_TRUE("groupPermissionListTransientLineTrue", 		"        &7&l●  &7<permission> : True"),
		GROUP_LIST_PERMISSION_TRANSIENT_LINE_FALSE("groupPermissionListTransientLineFalse", 	"        &7&l●  &7<permission> : False"),
		
		// Group : Option
		GROUP_ADD_OPTION_DESCRIPTION("groupOptionAddDescription", 			"Ajoute une option à un groupe"),
		GROUP_ADD_OPTION_STAFF("groupOptionAddStaff", 						"&7Le groupe &6<group> &7possède désormais l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
	
		GROUP_DEL_OPTION_DESCRIPTION("groupOptionDelDescription", 			"Supprime une option à un groupe"),
		GROUP_DEL_OPTION_STAFF("groupOptionDelStaff", 						"&7Le groupe &6<group> &7ne possède plus l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		GROUP_DEL_OPTION_ERROR("groupOptionDelError", 						"&cErreur : &cLe groupe &6<group> &cne possède pas l'option '&6<option>&c' dans les mondes de type &6<type>&c."),
		
		GROUP_CHECK_OPTION_DESCRIPTION("groupOptionCheckDescription", 				"Vérifie si un groupe a une option"),
		GROUP_CHECK_OPTION_DEFINED("groupOptionCheckDefine", 						"&7Le groupe &6<group> &7possède l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		GROUP_CHECK_OPTION_UNDEFINED("groupOptionCheckUndefined", 					"&7Le groupe &6<group> &7ne possède pas l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		
		GROUP_LIST_OPTION_DESCRIPTION("groupOptionListDescription", 				"Affiche la liste des options d'un groupe"),
		GROUP_LIST_OPTION_TITLE("groupOptionListTitle", 							"&aLes options de &6<group> &a: &6<type>"),
		GROUP_LIST_OPTION_OPTION("groupOptionListOption", 							"    &6&l➤  Options : "),
		GROUP_LIST_OPTION_OPTION_LINE("groupOptionListOptionLine",					"        &7&l●  &7<option> : &7'<value>&7'"),
		GROUP_LIST_OPTION_OPTION_EMPTY("groupOptionListOptionEmpty", 				"    &6&l➤ Options : &7Aucune"),
		GROUP_LIST_OPTION_TRANSIENT("groupOptionListTransient", 					"    &6&l➤ Options temporaires : "),
		GROUP_LIST_OPTION_TRANSIENT_LINE("groupOptionListTransientLine", 			"        &7&l●  &7<option> : &7'<value>&7'"),
		
		// Other
		OTHER_NOT_FOUND("otherNotFound", 											"&cErreur : Ce subject n'existe pas."),
		
		// Other : Permission
		OTHER_ADD_PERMISSION_DESCRIPTION("otherPermissionAddDescription", 			"Ajoute une permission à un Subject"),
		OTHER_ADD_PERMISSION_TRUE("otherPermissionAddTrue", 						"&6<subject> &7possède désormais la permission '&6<permission>&7'."),
		OTHER_ADD_PERMISSION_FALSE("otherPermissionAddFalse", 						"&6<subject> &7ne possède plus la permission '&6<permission>&7'."),
		OTHER_ADD_PERMISSION_ERROR_TRUE("otherPermissionAddErrorTrue", 				"&cErreur : &6<subject> &cpossède déjà la permissions '&6<permission>&c'."),
		OTHER_ADD_PERMISSION_ERROR_FALSE("otherPermissionAddErrorFalse", 			"&cErreur : &6<subject> &cne possède pas la permission '&6<permission>&c'."),
		
		OTHER_DEL_PERMISSION_DESCRIPTION("otherPermissionDelDescription", 			"Retire une permission à un Subject"),
		OTHER_DEL_PERMISSION_PLAYER("otherPermissionDelPlayer", 					"&6<subject> &7ne possède plus la permission '&6<permission>&7'."),
		OTHER_DEL_PERMISSION_ERROR("otherPermissionDelError", 						"&cErreur : &6<subject> &cne possède pas la permission '&6<permission>&c'."),
		
		OTHER_CHECK_PERMISSION_DESCRIPTION("otherPermissionCheckDescription", 		"Vérifie si un Subject a une permission"),
		OTHER_CHECK_PERMISSION_TRUE("otherPermissionCheckTrue", 					"&6<subject> &7possède la permission '&6<permission>&7'."),
		OTHER_CHECK_PERMISSION_FALSE("otherPermissionCheckFalse", 					"&6<subject> &7ne possède pas la permission '&6<permission>&7'."),	
		
		OTHER_LIST_PERMISSION_DESCRIPTION("otherPermissionListDescription", 					"Affiche la liste des permissions d'un Subject"),
		OTHER_LIST_PERMISSION_TITLE("otherPermissionListTitle", 								"&aLes permissions de &6<subject>"),
		OTHER_LIST_PERMISSION_PERMISSION("otherPermissionListPermission", 						"    &6&l➤  Permissions : "),
		OTHER_LIST_PERMISSION_PERMISSION_LINE_TRUE("otherPermissionListPermissionLineTrue", 	"        &7&l●  &7<permission> : True"),
		OTHER_LIST_PERMISSION_PERMISSION_LINE_FALSE("otherPermissionListPermissionLineFalse",	"        &7&l●  &7<permission> : False"),
		OTHER_LIST_PERMISSION_PERMISSION_EMPTY("otherPermissionListPermissionEmpty", 			"    &6&l➤ Permissions : &7Aucune"),
		OTHER_LIST_PERMISSION_TRANSIENT("otherPermissionListTransient", 						"    &6&l➤ Permissions temporaires : "),
		OTHER_LIST_PERMISSION_TRANSIENT_LINE_TRUE("otherPermissionListTransientLineTrue",		"        &7&l●  &7<permission> : True"),
		OTHER_LIST_PERMISSION_TRANSIENT_LINE_FALSE("otherPermissionListTransientLineFalse", 	"        &7&l●  &7<permission> : False"),
		
		// User : Option
		OTHER_ADD_OPTION_DESCRIPTION("otherOptionAddDescription", 					"Ajoute une option à un Subject"),
		OTHER_ADD_OPTION_PLAYER("otherOptionAddPlayer", 							"&6<subject> &7possède désormais l'option '&6<option>&7' avec la valeur '&6<value>&7'."),
		OTHER_ADD_OPTION_NAME_COLOR("otherOptionAddNameColor", 						"&6"),
		
		OTHER_DEL_OPTION_DESCRIPTION("otherOptionDelDescription", 					"Supprime une option à un Subject"),
		OTHER_DEL_OPTION_PLAYER("otherOptionDelPlayer", 							"&6<subject> &7ne possède plus l'option '&6<option>&7'."),
		OTHER_DEL_OPTION_ERROR("otherOptionDelError", 								"&cErreur : &6<subject> &cne possède pas l'option '&6<option>&c'."),
		
		OTHER_CHECK_OPTION_DESCRIPTION("otherOptionCheckDescription", 				"Vérifie si un Subject a une option"),
		OTHER_CHECK_OPTION_DEFINED("otherOptionCheckDefine", 						"&6<subject> &7possède l'option '&6<option>&7' avec la valeur '&6<value>&7'."),
		OTHER_CHECK_OPTION_UNDEFINED("otherOptionCheckUndefined", 					"&6<subject> &7ne possède pas l'option '&6<option>&7'."),
		
		OTHER_LIST_OPTION_DESCRIPTION("otherOptionListDescription", 				"Affiche la liste des options d'un Subject"),
		OTHER_LIST_OPTION_TITLE("otherOptionListTitle", 							"&aLes options de &6<subject>"),
		OTHER_LIST_OPTION_OPTION("otherOptionListOption", 							"    &6&l➤  Options : "),
		OTHER_LIST_OPTION_OPTION_LINE("otherOptionListOptionLine", 					"        &7&l●  &7<option> : &7'<value>&7'"),
		OTHER_LIST_OPTION_OPTION_EMPTY("otherOptionListOptionEmpty", 				" 	 &6&l➤ Options : &7Aucune"),
		OTHER_LIST_OPTION_TRANSIENT("otherOptionListTransient", 					"    &6&l➤ Options temporaires : "),
		OTHER_LIST_OPTION_TRANSIENT_LINE("otherOptionListTransientLine", 			"        &7&l●  &7<option> : &7'<value>&7'");
		
		private final String path;
	    private final EMessageBuilder french;
	    private final EMessageBuilder english;
	    private EMessageFormat message;
	    private EMessageBuilder builder;
	    
	    private EPMessages(final String path, final String french) {   	
	    	this(path, EMessageFormat.builder().chat(new EFormatString(french), true));
	    }
	    
	    private EPMessages(final String path, final String french, final String english) {   	
	    	this(path, 
	    		EMessageFormat.builder().chat(new EFormatString(french), true), 
	    		EMessageFormat.builder().chat(new EFormatString(english), true));
	    }
	    
	    private EPMessages(final String path, final EMessageBuilder french) {   	
	    	this(path, french, french);
	    }
	    
	    private EPMessages(final String path, final EMessageBuilder french, final EMessageBuilder english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = path;	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french.build();
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public EMessageBuilder getFrench() {
			return this.french;
		}

		public EMessageBuilder getEnglish() {
			return this.english;
		}
		
		public EMessageFormat getMessage() {
			return this.message;
		}
		
		public EMessageBuilder getBuilder() {
			return this.builder;
		}
		
		public void set(EMessageBuilder message) {
			this.message = message.build();
			this.builder = message;
		}
	}
}
