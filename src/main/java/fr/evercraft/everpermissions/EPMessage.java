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

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EPMessage extends EMessage {

	public EPMessage(final EverPermissions plugin) {
		super(plugin, EPMessages.values());
	}
	
	public enum EPMessages implements EnumMessage {
		// Plugin :
		PREFIX("plugin.prefix", 										"[&4Ever&6&lPermissions&f] "),
		DESCRIPTION("plugin.description", 								"Gestion des permissions", 
																		"Permissions management"),
		GROUP_NOT_FOUND("plugin.messages.groupNotFound", 				"&cErreur : Ce groupe n'existe pas.", 
																		"&cError: This group doesn't exist."),
		GROUP_NOT_FOUND_WORLD("plugin.messages.groupNotFoundWorld", 	"&cErreur : Il n'existe pas de groupe &6<group> &cdans les mondes de type &6<type>&c.",
																		"&cError : There is no group &6<group> &cin type worlds &6<type>&c."),
		ERROR_BOOLEAN("plugin.messages.errorBoolean", 					"&cErreur : Une permission ne peut-être que &6&lTrue &cou &6&lFalse",
																		"&cError : Permission may only &6&lTrue &cor &6&lFalse"),
		WORLD_EMPTY("plugin.messages.worldEmpty", 						"&cVous devez préciser le nom du monde.", 
																		"&cYou must specify the name of the world."),
		
		// Commands :		
		TRANSFERT_DESCRIPTION("commands.transfert.description",			"Transfère les données des joueurs",
																		"Transfers the data of the players"),
		TRANSFERT_SQL_CONFIRMATION("commands.transfert.sqlConfirmation", 			"&7Souhaitez-vous vraiment transférer les données des joueurs dans une base de données &6SQL&7 ? <confirmation>"),
		TRANSFERT_SQL_CONFIRMATION_VALID("commands.transfert.sqlConfirmationValid", 				"&2&nConfirmer"),
		TRANSFERT_SQL_CONFIRMATION_VALID_HOVER("commands.transfert.sqlConfirmationValidHover", 		"&cCliquez ici pour réaliser le transfert"),
		TRANSFERT_CONF_CONFIRMATION("commands.transfert.confConfirmation", 	"&7Souhaitez-vous vraiment transférer les données des joueurs dans des &6fichiers de configuration&7 ? <confirmation>"),
		TRANSFERT_CONF_CONFIRMATION_VALID("commands.transfert.confConfirmationValid", 				"&2&nConfirmer"),
		TRANSFERT_CONF_CONFIRMATION_VALID_HOVER("commands.transfert.confConfirmationValidHover", 	"&cCliquez ici pour réaliser le transfert"),
		TRANSFERT_SQL("commands.transfert.sql", 						"&7Les données des joueurs ont bien été transférées dans la base de données."),
		TRANSFERT_CONF("commands.transfert.conf", 						"&7Les données des joueurs ont bien été transférées dans les fichiers de configurations."),
		TRANSFERT_DISABLE("commands.transfert.disable", 				"&cErreur : Vous devez être connecté à une base de données pour faire le transfert des données."),
		TRANSFERT_ERROR("commands.transfert.error", 					"&cErreur : Pendant le transfert des données."),
		
		// Commands : Disable
		COMMAND_OP("commands.op", 								"&cVous possédez un plugin de permission donc être opérateur n'a aucune influence."),
		COMMAND_DEOP("commands.deop", 							"&cVous possédez un plugin de permission donc être opérateur n'a aucune influence."),
		
		// User :
		USER_CLEAR_DESCRIPTION("user.clear.description", 		"Supprime un joueur"),
		USER_CLEAR_STAFF("user.clear.staff", 					"&7Vous avez réinitialisé toutes les données de &6<player>&7."),
		USER_CLEAR_EQUALS("user.clear.equals", 					"&7Vous avez réinitialisé toutes vos données."),
		USER_CLEAR_BROADCAST_PLAYER("user.clear.broadcastPlayer", 	"&7Tous les données de &6<player> &7 ont été réinitialisé par &6<staff>&7."),
		USER_CLEAR_BROADCAST_EQUALS("user.clear.broadcastEquals", 	"&7Tous les données de &6<player> &7 ont été réinitialisé."),

		// User : Group
		USER_ADD_GROUP_DESCRIPTION("user.group.add.description", 			"Défini le groupe d'un joueur",
																			"Define the group of a player"),
		USER_ADD_GROUP_STAFF("user.group.add.staff", 						"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_GROUP_PLAYER("user.group.add.player", 						"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_GROUP_EQUALS("user.group.add.equals", 						"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_GROUP_BROADCAST_PLAYER("user.group.add.broadcastPlayer", 	"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_GROUP_BROADCAST_EQUALS("user.group.add.broadcastEquals", 	"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_GROUP_ERROR_STAFF("user.group.add.errorStaff", 			"&cErreur : &6<player> &cest déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_ADD_GROUP_ERROR_EQUALS("user.group.add.errorEquals", 			"&cErreur : Vous êtes déjà &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_DEL_GROUP_DESCRIPTION("user.group.del.description", 			"Supprime le groupe d'un joueur",
																			"Removes the group of a player"),
		USER_DEL_GROUP_STAFF("user.group.del.staff", 						"&6<player> &7n'est plus &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_GROUP_PLAYER("user.group.del.player", 						"&7Vous n'êtes plus &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_GROUP_EQUALS("user.group.del.equals", 						"&7Vous n'êtes plus &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_GROUP_BROADCAST_PLAYER("user.group.del.broadcastPlayer", 	"&6<player> &7n'est plus &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_GROUP_BROADCAST_EQUALS("user.group.del.broadcastEquals", 	"&6<player> &7n'est plus &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_GROUP_ERROR_STAFF("user.group.del.errorStaff", 			"&cErreur : &6<player> &cn'a pas de groupe &cdans les mondes de type &6<type>&c."),
		USER_DEL_GROUP_ERROR_EQUALS("user.group.del.errorEquals", 			"&cErreur : Vous n'avez pas de groupe dans les mondes de type &6<type>&c."),
		
		USER_ADD_SUBGROUP_DESCRIPTION("user.group.addsub.description", 		"Ajoute un sous-groupe à un joueur",
																			"Add a group to a player's subgroup list"),
		USER_ADD_SUBGROUP_STAFF("user.group.addsub.staff", 					"&6<player> &7possède désormais les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_SUBGROUP_PLAYER("user.group.addsub.player", 				"&7Vous possédez désormais les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_ADD_SUBGROUP_EQUALS("user.group.addsub.equals", 				"&7Vous possédez désormais les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_SUBGROUP_BROADCAST_PLAYER("user.group.addsub.broadcastPlayer", "&6<player> &7possède désormais les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_ADD_SUBGROUP_BROADCAST_EQUALS("user.group.addsub.broadcastEquals", "&6<player> &7possède désormais les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_ADD_SUBGROUP_ERROR_STAFF("user.group.addsub.errorStaff", 		"&cErreur : &6<player> &cpossède déjà les droits &6<group> &cdans les mondes de type &6<type>&c."),
		USER_ADD_SUBGROUP_ERROR_EQUALS("user.group.addsub.errorEquals", 	"&cErreur : Vous possédez déjà les droits &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_DEL_SUBGROUP_DESCRIPTION("user.group.delsub.description", 		"Supprime un sous-groupe à un joueur",
																			"Remove a group from a player's subgroup list"),
		USER_DEL_SUBGROUP_STAFF("user.group.delsub.staff", 					"&6<player> &7ne possède plus les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_SUBGROUP_PLAYER("user.group.delsub.player", 				"&7Vous ne possédez plus les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_SUBGROUP_EQUALS("user.group.delsub.equals", 				"&7Vous ne possédez plus les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_SUBGROUP_BROADCAST_PLAYER("user.group.delsub.broadcastPlayer", "&6<player> &7ne possède plus les droits &6<group> &7dans les mondes de type &6<type>&7 à cause de &6<staff>&7."),
		USER_DEL_SUBGROUP_BROADCAST_EQUALS("user.group.delsub.broadcastEquals", "&6<player> &7ne possède plus les droits &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEL_SUBGROUP_ERROR_STAFF("user.group.delsub.errorStaff", 		"&cErreur : &6<player> &cne possède pas les droits &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEL_SUBGROUP_ERROR_EQUALS("user.group.delsub.errorEquals", 	"&cErreur : Vous ne possédez pas les droits &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_PROMOTE_DESCRIPTION("user.group.promote.description", 						"Promouvoit un joueur",
																						"Allows promoting a player up the inheritance tree."),
		USER_PROMOTE_STAFF("user.group.promote.staff", 									"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_PROMOTE_PLAYER("user.group.promote.player", 								"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_PROMOTE_EQUALS("user.group.promote.equals", 								"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_PROMOTE_BROADCAST_PLAYER("user.group.promote.broadcastPlayer", 			"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_PROMOTE_BROADCAST_EQUALS("user.group.promote.broadcastEquals", 			"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_PROMOTE_ERROR_STAFF("user.group.promote.errorStaff",  						"&cErreur : &6<player> &cest déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_PROMOTE_ERROR_EQUALS("user.group.promote.errorEquals", 					"&cErreur : Vous êtes déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_PROMOTE_ERROR_DEMOTE_STAFF("user.group.promote.errorgroupDemoteStaff", 	"&cErreur : &6<player> &cpossède déjà un grade supérieur à &6<group> &cdans les mondes de type &6<type>&c."),
		USER_PROMOTE_ERROR_DEMOTE_EQUALS("user.group.promote.errorgroupDemoteEquals", 	"&cErreur : Vous possédez déjà un grade supérieur à &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_DEMOTE_DESCRIPTION("user.group.demote.description", 				"Rétrograde un joueur",
																				"Allows demoting a player down the inheritance tree."),
		USER_DEMOTE_STAFF("user.group.demote.staff", 							"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEMOTE_PLAYER("user.group.demote.player", 							"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_DEMOTE_EQUALS("user.group.demote.equals", 							"&7Vous êtes désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEMOTE_BROADCAST_PLAYER("user.group.demote.broadcastPlayer", 		"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7."),
		USER_DEMOTE_BROADCAST_EQUALS("user.group.demote.broadcastEquals", 		"&6<player> &7est désormais &6<group> &7dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_DEMOTE_ERROR_STAFF("user.group.demote.errorStaff", 				"&cErreur : &6<player> &cest déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEMOTE_ERROR_EQUALS("user.group.demote.errorEquals",				"&cErreur : Vous êtes déjà &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEMOTE_ERROR_PROMOTE_STAFF("user.group.demote.errorPromoteStaff", 	"&cErreur : &6<player> &cpossède déjà un grade inférieur à &6<group> &cdans les mondes de type &6<type>&c."),
		USER_DEMOTE_ERROR_PROMOTE_EQUALS("user.group.demote.errorPromoteEquals","&cErreur : Vous possédez déjà un grade inférieur à &6<group> &cdans les mondes de type &6<type>&c."),
		
		USER_LIST_GROUP_DESCRIPTION("user.group.list.description", 			"Affiche la liste des groupes d'un joueur",
																			"Tell the group that this user belongs to"),
		USER_LIST_GROUP_TITLE("user.group.list.title", 						"&aLes groupes de &6<player> &a: &6<type>"),
		USER_LIST_GROUP_GROUP("user.group.list.group", 						"    &6&l➤  Groupe : &7<group>"),
		USER_LIST_GROUP_GROUP_EMPTY("user.group.list.groupEmpty", 			"    &6&l➤  Groupe : &7Aucun"),
		USER_LIST_GROUP_SUBGROUP("user.group.list.subgroup", 				"    &6&l➤  Les sous-groupes : "),
		USER_LIST_GROUP_SUBGROUP_LINE("user.group.list.subgroupLine", 		"        &7&l●  &7<group>"),
		USER_LIST_GROUP_SUBGROUP_EMPTY("user.group.list.subgroupEmpty", 	"    &6&l➤  Sous-groupes : &7Aucun"),
		USER_LIST_GROUP_TRANSIENT("user.group.list.transient", 				"    &6&l➤  Les groupes temporaires : "),
		USER_LIST_GROUP_TRANSIENT_LINE("user.group.list.transientLine", 	"        &7&l●  &7<group>"),
		
		// User : Permission
		USER_ADD_PERMISSION_DESCRIPTION("user.permission.add.description", 				"Ajoute une permission à un joueur",
																						"Add permission directly to the player"),
		USER_ADD_PERMISSION_TRUE_STAFF("user.permission.add.true.staff", 				"&6<player> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_TRUE_EQUALS("user.permission.add.true.equals", 				"&7Vous possédez désormais la permission '&6<permission>&7' &7dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_TRUE_BROADCAST_PLAYER("user.permission.add.true.broadcastPlayer",	"&6<player> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_PERMISSION_TRUE_BROADCAST_EQUALS("user.permission.add.true.broadcastEquals", 	"&6<player> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_TRUE_ERROR_STAFF("user.permission.add.true.errorStaff", 	"&cErreur : &6<player> &cpossède déjà la permissions '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_ADD_PERMISSION_TRUE_ERROR_EQUALS("user.permission.add.true.errorEquals", 	"&cErreur : Vous possédez déjà la permissions '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_ADD_PERMISSION_FALSE_STAFF("user.permission.add.false.staff", 				"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_FALSE_EQUALS("user.permission.add.false.equals", 			"&7Vous ne possédez plus la permission '&6<permission>&7' &7dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_FALSE_BROADCAST_PLAYER("user.permission.add.false.broadcastPlayer",	"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_ADD_PERMISSION_FALSE_BROADCAST_EQUALS("user.permission.add.false.broadcastEquals", "&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_PERMISSION_FALSE_ERROR_STAFF("user.permission.add.false.errorStaff", 	"&cErreur : &6<player> &cne possède pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_ADD_PERMISSION_FALSE_ERROR_EQUALS("user.permission.add.false.errorEquals",	 "&cErreur : Vous ne possédez pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		
		USER_DEL_PERMISSION_DESCRIPTION("user.permission.del.description", 				"Retire une permission à un joueur",
																						"Removes permission directly from the player"),
		USER_DEL_PERMISSION_STAFF("user.permission.del.staff", 							"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_DEL_PERMISSION_EQUALS("user.permission.del.equals", 						"&7Vous ne possédez plus la permission '&6<permission>&7' &7dans les mondes de type &6<type>&7."),
		USER_DEL_PERMISSION_BROADCAST_PLAYER("user.permission.del.broadcastPlayer",		"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7 grâce à &6<staff>&7."),
		USER_DEL_PERMISSION_BROADCAST_EQUALS("user.permission.del.broadcastEquals", 	"&6<player> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_DEL_PERMISSION_ERROR_STAFF("user.permission.del.errorStaff", 				"&cErreur : &6<player> &cne possède pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		USER_DEL_PERMISSION_ERROR_EQUALS("user.permission.del.errorEquals", 			"&cErreur : Vous ne possédez pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		
		USER_CHECK_PERMISSION_DESCRIPTION("user.permission.check.description", 			"Vérifie si un joueur a une permission",
																						"Verify if user has a permission"),
		USER_CHECK_PERMISSION_TRUE_EQUALS("user.permission.check.true.equals", 			"&7Vous possédez la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_TRUE_STAFF("user.permission.check.true.staff", 			"&6<player> &7possède la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_FALSE_EQUALS("user.permission.check.false.equals", 		"&7Vous ne possédez pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_FALSE_STAFF("user.permission.check.false.staff", 			"&6<player> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_UNDEFINED_EQUALS("user.permission.check.undefined.equals","&7Vous ne possédez pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_PERMISSION_UNDEFINED_STAFF("user.permission.check.undefined.staff", 	"&6<player> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		
		USER_LIST_PERMISSION_DESCRIPTION("user.permission.list.description", 					"Affiche la liste des permissions d'un joueur",
																								"List all permissions from a player"),
		USER_LIST_PERMISSION_TITLE("user.permission.list.title", 								"&aLes permissions de &6<player> &a: &6<type>"),
		USER_LIST_PERMISSION_PERMISSION("user.permission.list.permission", 						"    &6&l➤  Permissions : "),
		USER_LIST_PERMISSION_PERMISSION_LINE_TRUE("user.permission.list.permissionLine.true", 	"        &7&l●  &7<permission> : True"),
		USER_LIST_PERMISSION_PERMISSION_LINE_FALSE("user.permission.list.permissionLine.false", "        &7&l●  &7<permission> : False"),
		USER_LIST_PERMISSION_PERMISSION_EMPTY("user.permission.list.permissionEmpty", 			"    &6&l➤ Permissions : &7Aucune"),
		USER_LIST_PERMISSION_TRANSIENT("user.permission.list.transient", 						"    &6&l➤ Permissions temporaires : "),
		USER_LIST_PERMISSION_TRANSIENT_LINE_TRUE("user.permission.list.transientLine.true", 	"        &7&l●  &7<permission> : True"),
		USER_LIST_PERMISSION_TRANSIENT_LINE_FALSE("user.permission.list.transientLine.false", 	"        &7&l●  &7<permission> : False"),
		
		// User : Option
		USER_ADD_OPTION_DESCRIPTION("user.option.add.description", 				"Ajoute ou remplace une option à un joueur",
																				"Add, or replaces, a option to a user"),
		USER_ADD_OPTION_STAFF("user.option.add.staff", 							"&6<player> &7possède désormais l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_OPTION_STAFF_NAME_COLOR("user.option.add.staffNameColor", 		"&6"),
		USER_ADD_OPTION_EQUALS("user.option.add.equals", 						"&7Vous possédez désormais l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		USER_ADD_OPTION_EQUALS_NAME_COLOR("user.option.add.equalsNameColor", 	"&6"),
		
		USER_DEL_OPTION_DESCRIPTION("user.option.del.description", 				"Supprime une option à un joueur",
																				"Remove a option from a user."),
		USER_DEL_OPTION_STAFF("user.option.del.staff", 							"&6<player> &7ne possède plus l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		USER_DEL_OPTION_EQUALS("user.option.del.equals", 						"&7Vous ne possédez plus l'option '&6<option>&7' &7dans les mondes de type &6<type>&7."),
		USER_DEL_OPTION_ERROR_STAFF("user.option.del.errorStaff", 				"&cErreur : &6<player> &cne possède pas l'option '&6<option>&c' dans les mondes de type &6<type>&c."),
		USER_DEL_OPTION_ERROR_EQUALS("user.option.del.errorEquals", 			"&cErreur : Vous ne possédez pas l'option '&6<option>&c' dans les mondes de type &6<type>&c."),
		
		USER_CHECK_OPTION_DESCRIPTION("user.option.check.description", 							"Vérifie si un joueur a une option",
																								"Verify a value of a option of user"),
		USER_CHECK_OPTION_DEFINED_STAFF("user.option.check.define.staff", 						"&6<player> &7possède l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_OPTION_DEFINED_STAFF_NAME_COLOR("user.option.check.define.staffNameColor", 	"&6"),
		USER_CHECK_OPTION_DEFINED_EQUALS("user.option.check.define.equals", 					"&7Vous possédez l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_OPTION_DEFINED_EQUALS_NAME_COLOR("user.option.check.define.equalsNameColor", "&6"),
		USER_CHECK_OPTION_UNDEFINED_EQUALS("user.option.check.undefined.staff", 				"&6<player> &7ne possède pas l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		USER_CHECK_OPTION_UNDEFINED_STAFF("user.option.check.undefined.equals", 				"&7Vous ne possédez pas l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		
		USER_LIST_OPTION_DESCRIPTION("user.option.list.description", 				"Affiche la liste des options d'un joueur",
																					"List variables a user has"),
		USER_LIST_OPTION_TITLE("user.option.list.title", 							"&aLes options de &6<player> &a: &6<type>"),
		USER_LIST_OPTION_OPTION("user.option.list.option", 							"    &6&l➤  Options : "),
		USER_LIST_OPTION_OPTION_LINE("user.option.list.optionLine", 				"        &7&l●  &7<option> : &7'<value>&7'"),
		USER_LIST_OPTION_OPTION_EMPTY("user.option.list.optionEmpty", 				"    &6&l➤ Options : &7Aucune"),
		USER_LIST_OPTION_OPTION_NAME_COLOR("user.option.list.optionNameColor", 		"&7"),
		USER_LIST_OPTION_TRANSIENT("user.option.list.transient", 					"    &6&l➤ Options temporaires : "),
		USER_LIST_OPTION_TRANSIENT_LINE("user.option.list.transientLine", 			"        &7&l●  &7<option> : &7'<value>&7'"),
		USER_LIST_OPTION_TRANSIENT_NAME_COLOR("user.option.list.transientNameColor","&7"),
		
		// Group : Group
		GROUP_ADD_GROUP_DESCRIPTION("group.group.add.description", 		"Ajoute un groupe à un monde"),
		GROUP_ADD_GROUP_STAFF("group.group.add.staff", 					"&7Vous avez créé le groupe &6<group> &7dans les mondes de type &6<type>&7."),
		GROUP_ADD_GROUP_ERROR("group.group.add.error", 					"&cIl existe déjà un groupe &6<group> &cdans les mondes de type &6<type>&c."),
		
		GROUP_DEL_GROUP_DESCRIPTION("group.group.del.description", 		"Supprime un groupe d'un monde"),
		GROUP_DEL_GROUP_STAFF("group.group.del.staff", 					"&7Vous avez supprimé le groupe &6<group> &7dans les mondes de type &6<type>&7."),
		
		GROUP_DEFAULT_GROUP_DESCRIPTION("group.group.default.description", 		"Définit un groupe par défaut"),
		GROUP_DEFAULT_GROUP_TRUE("group.group.default.true", 					"&7Le groupe &6<group> &7est désormais le groupe par défaut &7dans les mondes de type &6<type>&7."),
		GROUP_DEFAULT_GROUP_FALSE("group.group.default.false", 					"&7Le groupe &6<group> &7n'est plus le groupe par défaut &7dans les mondes de type &6<type>&7."),
		GROUP_DEFAULT_GROUP_ERROR_TRUE("group.group.default.errorTrue", 		"&cIl existe déjà un groupe par défaut &cdans les mondes de type &6<type>&c."),
		GROUP_DEFAULT_GROUP_ERROR_FALSE("group.group.default.errorFalse", 		"&cLe groupe &6<group> &cn'est pas le groupe défaut &cdans les mondes de type &6<type>&c."),
		GROUP_DEFAULT_GROUP_ERROR_EQUALS("group.group.default.errorEquals", 	"&cLe groupe &6<group> &cest déjà la groupe par défaut &cdans les mondes de type &6<type>&c."),
		GROUP_DEFAULT_GROUP_ERROR_BOOLEAN("group.group.default.errorBoolean", 	"&cErreur : La valeur 'default' ne peut-être que &6&lTrue &cou &6&lFalse"),
		
		GROUP_LIST_GROUP_DESCRIPTION("group.group.list.description",			"Affiche la liste des groupes d'un monde"),
		GROUP_LIST_GROUP_TITLE("group.group.list.title", 						"&aLes groupes de type &6<type>"),
		GROUP_LIST_GROUP_DEFAULT("group.group.list.default", 					"    &6&l➤  Le groupe par défaut : &7<group>"),
		GROUP_LIST_GROUP_NAME("group.group.list.name", 							"    &6&l➤  Les groupes : "),
		GROUP_LIST_GROUP_LINE("group.group.list.line", 							"        &7&l●  &7<group>"),
		GROUP_LIST_GROUP_EMPTY("group.group.list.empty", 						"    &6&l➤  Groupes : &7Aucun"),
		
		// Group : Inheritance
		GROUP_ADD_INHERITANCE_DESCRIPTION("group.add.inheritance.description", 	"Ajoute une inhéritance à un groupe"),
		GROUP_ADD_INHERITANCE_STAFF("group.add.inheritance.staff",				"&7Vous avez ajouté l'inhéritance &6<inheritance> &7au groupe &6<group> &7dans les mondes de type &6<type>&7."),
		GROUP_ADD_INHERITANCE_ERROR_HAVE("group.add.inheritance.errorHave", 	"&cLe groupe &6<group> &cpossède déjà l'inhéritance &6<inheritance> &cdans les mondes de type &6<type>&c."),
		GROUP_ADD_INHERITANCE_ERROR_EQUALS("group.add.inheritance.errorEquals", "&cL'inhéritance &6<inheritance> &cne peut pas être ajouté au groupe &6<group>&c."),
		
		GROUP_DEL_INHERITANCE_DESCRIPTION("group.del.inheritance.description", 	"Supprime une inhéritance d'un groupe"),
		GROUP_DEL_INHERITANCE_STAFF("group.del.inheritance.staff", 				"&7Vous avez supprimé l'inhéritance &6<inheritance> &7au groupe &6<group> &7dans les mondes de type &6<type>&7."),
		GROUP_DEL_INHERITANCE_ERROR("group.del.inheritance.errorHave", 			"&cLe groupe &6<group> &cne possède pas l'inhéritance &6<inheritance> &cdans les mondes de type &6<type>&c."),
		
		GROUP_LIST_INHERITANCE_DESCRIPTION("group.list.inheritance.description", 			"Affiche la liste des inhéritances d'un groupe"),
		GROUP_LIST_INHERITANCE_TITLE("group.list.inheritance.title", 						"&aLes inhéritances du groupe &6<group> &a: &6<type>"),
		GROUP_LIST_INHERITANCE_INHERITANCE("group.list.inheritance.inheritance", 			"    &6&l➤  Les inhéritances : "),
		GROUP_LIST_INHERITANCE_INHERITANCE_LINE("group.list.inheritance.inheritanceLine", 	"        &7&l●  &7<inheritance>"),
		GROUP_LIST_INHERITANCE_INHERITANCE_EMPTY("group.list.inheritance.inheritanceEmpty", "    &6&l➤  Inhéritance : &7Aucune"),
		GROUP_LIST_INHERITANCE_TRANSIENT("group.list.inheritance.transient", 				"    &6&l➤  Les inhéritances temporaires : "),
		GROUP_LIST_INHERITANCE_TRANSIENT_LINE("group.list.inheritance.transientLine", 		"        &7&l●  &7<inheritance>"),
		
		// Group : Permission
		GROUP_ADD_PERMISSION_DESCRIPTION("group.permission.add.description", 	"Ajoute une permission à un groupe"),
		GROUP_ADD_PERMISSION_TRUE("group.permission.add.true", 					"&7Le groupe &6<group> &7possède désormais la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_ADD_PERMISSION_FALSE("group.permission.add.false", 				"&7Le groupe &6<group> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_ADD_PERMISSION_ERROR_TRUE("group.permission.add.errorTrue", 		"&7Le groupe &6<group> &7possède déjà la permission '&6<permission>&7' dans les mondes de type &6<type>&c."),
		GROUP_ADD_PERMISSION_ERROR_FALSE("group.permission.add.errorFalse", 	"&7Le groupe &6<group> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&c."),
		
		GROUP_DEL_PERMISSION_DESCRIPTION("group.permission.del.description", 	"Retire une permission à un groupe"),
		GROUP_DEL_PERMISSION_STAFF("group.permission.del.staff", 				"&6<group> &7ne possède plus la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_DEL_PERMISSION_ERROR("group.permission.del.error", 				"&cErreur : &6<group> &cne possède pas la permission '&6<permission>&c' dans les mondes de type &6<type>&c."),
		
		GROUP_CHECK_PERMISSION_DESCRIPTION("group.permission.check.description","Vérifie si un groupe a une permission"),
		GROUP_CHECK_PERMISSION_TRUE("group.permission.check.true", 				"&7Le groupe &6<group> &7possède la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_CHECK_PERMISSION_FALSE("group.permission.check.false", 			"&7Le groupe &6<group> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		GROUP_CHECK_PERMISSION_UNDEFINED("group.permission.check.undefined", 	"&7Le groupe &6<group> &7ne possède pas la permission '&6<permission>&7' dans les mondes de type &6<type>&7."),
		
		GROUP_LIST_PERMISSION_DESCRIPTION("group.permission.list.description", 						"Affiche la liste des permissions d'un groupe"),
		GROUP_LIST_PERMISSION_TITLE("group.permission.list.title", 									"&aLes permissions du groupe &6<group> &a: &6<type>"),
		GROUP_LIST_PERMISSION_PERMISSION("group.permission.list.permission", 						"    &6&l➤  Permissions : "),
		GROUP_LIST_PERMISSION_PERMISSION_LINE_TRUE("group.permission.list.permissionLine.true",		"        &7&l●  &7<permission> : True"),
		GROUP_LIST_PERMISSION_PERMISSION_LINE_FALSE("group.permission.list.permissionLine.false",	"        &7&l●  &7<permission> : False"),
		GROUP_LIST_PERMISSION_PERMISSION_EMPTY("group.permission.list.permissionEmpty", 			"    &6&l➤ Permissions : &7Aucune"),
		GROUP_LIST_PERMISSION_TRANSIENT("group.permission.list.transient", 							"    &6&l➤ Permissions temporaires : "),
		GROUP_LIST_PERMISSION_TRANSIENT_LINE_TRUE("group.permission.list.transientLine.true", 		"        &7&l●  &7<permission> : True"),
		GROUP_LIST_PERMISSION_TRANSIENT_LINE_FALSE("group.permission.list.transientLine.false", 	"        &7&l●  &7<permission> : False"),
		
		// Group : Option
		GROUP_ADD_OPTION_DESCRIPTION("group.option.add.description", 			"Ajoute une option à un groupe"),
		GROUP_ADD_OPTION_STAFF("group.option.add.staff", 						"&7Le groupe &6<group> &7possède désormais l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		GROUP_ADD_OPTION_STAFF_NAME_COLOR("group.option.add.staffNameColor", 	"&6"),
	
		GROUP_DEL_OPTION_DESCRIPTION("group.option.del.description", 			"Supprime une option à un groupe"),
		GROUP_DEL_OPTION_STAFF("group.option.del.staff", 						"&7Le groupe &6<group> &7ne possède plus l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		GROUP_DEL_OPTION_ERROR("group.option.del.error", 						"&cErreur : &cLe groupe &6<group> &cne possède pas l'option '&6<option>&c' dans les mondes de type &6<type>&c."),
		
		GROUP_CHECK_OPTION_DESCRIPTION("group.option.check.description", 					"Vérifie si un groupe a une option"),
		GROUP_CHECK_OPTION_DEFINED("group.option.check.define", 						"&7Le groupe &6<group> &7possède l'option '&6<option>&7' avec la valeur '&6<value>&7' dans les mondes de type &6<type>&7."),
		GROUP_CHECK_OPTION_DEFINED_NAME_COLOR("group.option.check.defineNameColor", 	"&6"),
		GROUP_CHECK_OPTION_UNDEFINED("group.option.check.undefined", 						"&7Le groupe &6<group> &7ne possède pas l'option '&6<option>&7' dans les mondes de type &6<type>&7."),
		
		GROUP_LIST_OPTION_DESCRIPTION("group.option.list.description", 					"Affiche la liste des options d'un groupe"),
		GROUP_LIST_OPTION_TITLE("group.option.list.title", 								"&aLes options de &6<group> &a: &6<type>"),
		GROUP_LIST_OPTION_OPTION("group.option.list.option", 							"    &6&l➤  Options : "),
		GROUP_LIST_OPTION_OPTION_LINE("group.option.list.optionLine",					"        &7&l●  &7<option> : &7'<value>&7'"),
		GROUP_LIST_OPTION_OPTION_EMPTY("group.option.list.optionEmpty", 				"    &6&l➤ Options : &7Aucune"),
		GROUP_LIST_OPTION_OPTION_NAME_COLOR("group.option.list.optionNameColor", 		"&7"),
		GROUP_LIST_OPTION_TRANSIENT("group.option.list.transient", 						"    &6&l➤ Options temporaires : "),
		GROUP_LIST_OPTION_TRANSIENT_LINE("group.option.list.transientLine", 			"        &7&l●  &7<option> : &7'<value>&7'"),
		GROUP_LIST_OPTION_TRANSIENT_NAME_COLOR("group.option.list.transientNameColor", 	"&7"),
		
		// Other
		OTHER_NOT_FOUND("other.notFound", 												"&cErreur : Ce subject n'existe pas."),
		
		// Other : Permission
		OTHER_ADD_PERMISSION_DESCRIPTION("other.permission.add.description", 			"Ajoute une permission à un Subject"),
		OTHER_ADD_PERMISSION_TRUE("other.permission.add.true", 							"&6<subject> &7possède désormais la permission '&6<permission>&7'."),
		OTHER_ADD_PERMISSION_FALSE("other.permission.add.false", 						"&6<subject> &7ne possède plus la permission '&6<permission>&7'."),
		OTHER_ADD_PERMISSION_ERROR_TRUE("other.permission.add.errorTrue", 				"&cErreur : &6<subject> &cpossède déjà la permissions '&6<permission>&c'."),
		OTHER_ADD_PERMISSION_ERROR_FALSE("other.permission.add.errorFalse", 			"&cErreur : &6<subject> &cne possède pas la permission '&6<permission>&c'."),
		
		OTHER_DEL_PERMISSION_DESCRIPTION("other.permission.del.description", 			"Retire une permission à un Subject"),
		OTHER_DEL_PERMISSION_PLAYER("other.permission.del.player", 						"&6<subject> &7ne possède plus la permission '&6<permission>&7'."),
		OTHER_DEL_PERMISSION_ERROR("other.permission.del.error", 						"&cErreur : &6<subject> &cne possède pas la permission '&6<permission>&c'."),
		
		OTHER_CHECK_PERMISSION_DESCRIPTION("other.permission.check.description", 		"Vérifie si un Subject a une permission"),
		OTHER_CHECK_PERMISSION_TRUE("other.permission.check.true", 						"&6<subject> &7possède la permission '&6<permission>&7'."),
		OTHER_CHECK_PERMISSION_FALSE("other.permission.check.false", 					"&6<subject> &7ne possède pas la permission '&6<permission>&7'."),	
		
		OTHER_LIST_PERMISSION_DESCRIPTION("other.permission.list.description", 						"Affiche la liste des permissions d'un Subject"),
		OTHER_LIST_PERMISSION_TITLE("other.permission.list.title", 									"&aLes permissions de &6<subject>"),
		OTHER_LIST_PERMISSION_PERMISSION("other.permission.list.permission", 						"    &6&l➤  Permissions : "),
		OTHER_LIST_PERMISSION_PERMISSION_LINE_TRUE("other.permission.list.permissionLine.true", 	"        &7&l●  &7<permission> : True"),
		OTHER_LIST_PERMISSION_PERMISSION_LINE_FALSE("other.permission.list.permissionLine.false",	"        &7&l●  &7<permission> : False"),
		OTHER_LIST_PERMISSION_PERMISSION_EMPTY("other.permission.list.permissionEmpty", 			"    &6&l➤ Permissions : &7Aucune"),
		OTHER_LIST_PERMISSION_TRANSIENT("other.permission.list.transient", 							"    &6&l➤ Permissions temporaires : "),
		OTHER_LIST_PERMISSION_TRANSIENT_LINE_TRUE("other.permission.list.transientLine.true",		"        &7&l●  &7<permission> : True"),
		OTHER_LIST_PERMISSION_TRANSIENT_LINE_FALSE("other.permission.list.transientLine.false", 	"        &7&l●  &7<permission> : False"),
		
		// User : Option
		OTHER_ADD_OPTION_DESCRIPTION("other.option.add.description", 					"Ajoute une option à un Subject"),
		OTHER_ADD_OPTION_PLAYER("other.option.add.player", 								"&6<subject> &7possède désormais l'option '&6<option>&7' avec la valeur '&6<value>&7'."),
		OTHER_ADD_OPTION_NAME_COLOR("other.option.add.nameColor", 						"&6"),
		
		OTHER_DEL_OPTION_DESCRIPTION("other.option.del.description", 					"Supprime une option à un Subject"),
		OTHER_DEL_OPTION_PLAYER("other.option.del.player", 								"&6<subject> &7ne possède plus l'option '&6<option>&7'."),
		OTHER_DEL_OPTION_ERROR("other.option.del.error", 								"&cErreur : &6<subject> &cne possède pas l'option '&6<option>&c'."),
		
		OTHER_CHECK_OPTION_DESCRIPTION("other.option.check.description", 				"Vérifie si un Subject a une option"),
		OTHER_CHECK_OPTION_DEFINED("other.option.check.define", 						"&6<subject> &7possède l'option '&6<option>&7' avec la valeur '&6<value>&7'."),
		OTHER_CHECK_OPTION_DEFINED_NAME_COLOR("other.option.check.defineNameColor", 	"&7"),
		OTHER_CHECK_OPTION_UNDEFINED("other.option.check.undefined", 					"&6<subject> &7ne possède pas l'option '&6<option>&7'."),
		
		OTHER_LIST_OPTION_DESCRIPTION("other.option.list.description", 					"Affiche la liste des options d'un Subject"),
		OTHER_LIST_OPTION_TITLE("other.option.list.title", 								"&aLes options de &6<subject>"),
		OTHER_LIST_OPTION_OPTION("other.option.list.option", 							"    &6&l➤  Options : "),
		OTHER_LIST_OPTION_OPTION_LINE("other.option.list.optionLine", 					"        &7&l●  &7<option> : &7'<value>&7'"),
		OTHER_LIST_OPTION_OPTION_EMPTY("other.option.list.optionEmpty", 				"    &6&l➤ Options : &7Aucune"),
		OTHER_LIST_OPTION_OPTION_NAME_COLOR("other.option.list.optionNameColor", 		"&7"),
		OTHER_LIST_OPTION_TRANSIENT("other.option.list.transient", 						"    &6&l➤ Options temporaires : "),
		OTHER_LIST_OPTION_TRANSIENT_LINE("other.option.list.transientLine", 			"        &7&l●  &7<option> : &7'<value>&7'"),
		OTHER_LIST_OPTION_TRANSIENT_NAME_COLOR("other.option.list.transientNameColor", 	"&7");
		
		private final String path;
	    private final Object french;
	    private final Object english;
	    private Object message;
	    
	    private EPMessages(final String path, final Object french) {   	
	    	this(path, french, french);
	    }
	    
	    private EPMessages(final String path, final Object french, final Object english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = path;	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french;
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public Object getFrench() {
			return this.french;
		}

		public Object getEnglish() {
			return this.english;
		}
		
		public String get() {
			if(this.message instanceof String) {
				return (String) this.message;
			}
			return this.message.toString();
		}
			
		@SuppressWarnings("unchecked")
		public List<String> getList() {
			if(this.message instanceof List) {
				return (List<String>) this.message;
			}
			return Arrays.asList(this.message.toString());
		}
		
		public void set(Object message) {
			this.message = message;
		}

		public Text getText() {
			return EChat.of(this.get());
		}
		
		public TextColor getColor() {
			return EChat.getTextColor(this.get());
		}

		public boolean has() {
			return !this.get().isEmpty();
		}
	}
}
