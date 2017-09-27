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
		PREFIX(										"[&4Ever&6&lPermissions&f] "),
		DESCRIPTION(								"Gestion des permissions", 
													"Permissions management"),
		GROUP_NOT_FOUND_WORLD(						"&cErreur : Il n'existe pas de groupe &6{group} &cdans les mondes de type &6{type}&c.",
													"&cError : There is no group &6{group} &cin type worlds &6{type}&c."),
		ERROR_BOOLEAN(								"&cErreur : Une permission ne peut-être que &6&lTrue &cou &6&lFalse",
													"&cError : Permission may only &6&lTrue &cor &6&lFalse"),
		WORLD_EMPTY(								"&cVous devez préciser le nom du monde.", 
													"&cYou must specify the name of the world."),
		
		// Commands :		
		TRANSFERT_DESCRIPTION(						"Transfère les données des joueurs",
													"Transfers the data of the players"),
		TRANSFERT_SQL_CONFIRMATION(					"&7Souhaitez-vous vraiment transférer les données des joueurs dans une base de données &6SQL&7 ? {confirmation}"),
		TRANSFERT_SQL_CONFIRMATION_VALID(			"&2&nConfirmer"),
		TRANSFERT_SQL_CONFIRMATION_VALID_HOVER(		"&cCliquez ici pour réaliser le transfert"),
		TRANSFERT_CONF_CONFIRMATION(				"&7Souhaitez-vous vraiment transférer les données des joueurs dans des &6fichiers de configuration&7 ? {confirmation}"),
		TRANSFERT_CONF_CONFIRMATION_VALID(			"&2&nConfirmer"),
		TRANSFERT_CONF_CONFIRMATION_VALID_HOVER(	"&cCliquez ici pour réaliser le transfert"),
		TRANSFERT_SQL(								"&7Les données des joueurs ont bien été transférées dans la base de données."),
		TRANSFERT_SQL_LOG(							"&7Les données des joueurs ont bien été transférées dans la base de données."),
		TRANSFERT_CONF(								"&7Les données des joueurs ont bien été transférées dans les fichiers de configurations."),
		TRANSFERT_CONF_LOG(							"&7Les données des joueurs ont bien été transférées dans les fichiers de configurations."),
		TRANSFERT_DISABLE(							"&cErreur : Vous devez être connecté à une base de données pour faire le transfert des données."),
		TRANSFERT_ERROR(							"&cErreur : Pendant le transfert des données."),
		
		// Commands : Disable
		COMMAND_OP(									"&cVous possédez un plugin de permission donc être opérateur n'a aucune influence."),
		COMMAND_DEOP(								"&cVous possédez un plugin de permission donc être opérateur n'a aucune influence."),
		
		// User :
		USER_DESCRIPTION(							"Gestion des joueurs"),
		
		USER_INFO_DESCRIPTION(						"Affiche les informations d'un groupe"),
		USER_INFO_TITLE(							"&aLe joueur &6{player} &adans les mondes de type &6{type}"),
		USER_INFO_GROUP(							"    &6&l➤ Groupe : &7{group}"),
		USER_INFO_GROUP_EMPTY(						"    &6&l➤ Groupe : &7Aucun"),
		USER_INFO_SUBGROUP(							"    &6&l➤ Sous-groupes : "),
		USER_INFO_SUBGROUP_LINE(					"        &7&l●  &7{subgroup}"),
		USER_INFO_SUBGROUP_EMPTY(					"    &6&l➤ Sous-goupes : &7Aucun"),
		USER_INFO_GROUP_TRANSIENT(					"    &6&l➤ Groupe temporaires : &7{group}"),
		USER_INFO_SUBGROUP_TRANSIENT(				"    &6&l➤ Sous-groupes temporaires : "),
		USER_INFO_SUBGROUP_TRANSIENT_LINE(			"        &7&l●  &7{subgroup}"),
		USER_INFO_PERMISSION(						"    &6&l➤ Permissions : "),
		USER_INFO_PERMISSION_LINE(					"        &7&l●  &7{permission} : {value}"),
		USER_INFO_PERMISSION_TRUE(					"True"),
		USER_INFO_PERMISSION_FALSE(					"False"),
		USER_INFO_PERMISSION_EMPTY(					"    &6&l➤ Permissions : &7Aucune"),
		USER_INFO_PERMISSION_TRANSIENT(				"    &6&l➤ Permissions temporaires : "),
		USER_INFO_PERMISSION_TRANSIENT_LINE(		"        &7&l●  &7{permission} : {value}"),
		USER_INFO_PERMISSION_TRANSIENT_TRUE(		"True"),
		USER_INFO_PERMISSION_TRANSIENT_FALSE(		"False"),
		USER_INFO_OPTION(							"    &6&l➤ Options : "),
		USER_INFO_OPTION_LINE(						"        &7&l●  &7{option} : &7'{value}&7'"),
		USER_INFO_OPTION_EMPTY(					"    &6&l➤ Options : &7Aucune"),
		USER_INFO_OPTION_TRANSIENT(				"    &6&l➤ Options temporaires : "),
		USER_INFO_OPTION_TRANSIENT_LINE(			"        &7&l●  &7{option} : &7'{value}&7'"),
		
		USER_CLEAR_DESCRIPTION(						"Supprime un joueur"),
		USER_CLEAR_CONFIRMATION(					"&7Souhaitez-vous vraiment réinitialiser toutes les données de &6{player} &7: {confirmation}"),
		USER_CLEAR_CONFIRMATION_BUTTON(				"&a[Confirmer]"),
		USER_CLEAR_CONFIRMATION_BUTTON_HOVER(		"&cCliquez ici pour réinitialisé toutes les données de &6{player}&c."),
		USER_CLEAR_STAFF(							"&7Vous avez réinitialisé toutes les données de &6{player}&7."),
		USER_CLEAR_PLAYER(							"&7Vos données ont été réinitialisé."),
		USER_CLEAR_EQUALS(							"&7Vous avez réinitialisé toutes vos données."),
		USER_CLEAR_BROADCAST_PLAYER(				"&7Tous les données de &6{player} &7 ont été réinitialisé par &6{staff}&7."),
		USER_CLEAR_BROADCAST_EQUALS(				"&7Tous les données de &6{player} &7 ont été réinitialisé."),

		// User : Group
		USER_GROUP_DESCRIPTION(						"Gestion du groupe d'un joueur"),
		
		USER_GROUP_SET_DESCRIPTION(					"Défini le groupe d'un joueur",
													"Define the group of a player"),
		USER_GROUP_SET_STAFF(						"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_SET_PLAYER(						"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_GROUP_SET_EQUALS(						"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_SET_BROADCAST_PLAYER(			"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_GROUP_SET_BROADCAST_EQUALS(			"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_SET_ERROR_STAFF(					"&cErreur : &6{player} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_GROUP_SET_ERROR_EQUALS(				"&cErreur : Vous êtes déjà &6{group} &cdans les mondes de type &6{type}&c."),
	
		USER_GROUP_REMOVE_DESCRIPTION(				"Supprime le groupe d'un joueur",
													"Removes the group of a player"),
		USER_GROUP_REMOVE_STAFF(					"&6{player} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_REMOVE_PLAYER(					"&7Vous n'êtes plus &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_GROUP_REMOVE_EQUALS(					"&7Vous n'êtes plus &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_REMOVE_BROADCAST_PLAYER(			"&6{player} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_GROUP_REMOVE_BROADCAST_EQUALS(			"&6{player} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_REMOVE_ERROR_STAFF(				"&cErreur : &6{player} &cn'a pas de groupe &cdans les mondes de type &6{type}&c."),
		USER_GROUP_REMOVE_ERROR_EQUALS(				"&cErreur : Vous n'avez pas de groupe dans les mondes de type &6{type}&c."),
		
		USER_GROUP_PROMOTE_DESCRIPTION(				"Promouvoit un joueur",
													"Allows promoting a player up the inheritance tree."),
		USER_GROUP_PROMOTE_STAFF(					"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_PROMOTE_PLAYER(					"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_GROUP_PROMOTE_EQUALS(					"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_PROMOTE_BROADCAST_PLAYER(		"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_GROUP_PROMOTE_BROADCAST_EQUALS(		"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_PROMOTE_ERROR_STAFF( 			"&cErreur : &6{player} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_GROUP_PROMOTE_ERROR_EQUALS(			"&cErreur : Vous êtes déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_GROUP_PROMOTE_ERROR_DEMOTE_STAFF(		"&cErreur : &6{player} &cpossède déjà un grade supérieur à &6{group} &cdans les mondes de type &6{type}&c."),
		USER_GROUP_PROMOTE_ERROR_DEMOTE_EQUALS(		"&cErreur : Vous possédez déjà un grade supérieur à &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_GROUP_DEMOTE_DESCRIPTION(				"Rétrograde un joueur",
													"Allows demoting a player down the inheritance tree."),
		USER_GROUP_DEMOTE_STAFF(					"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_DEMOTE_PLAYER(					"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_GROUP_DEMOTE_EQUALS(					"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_DEMOTE_BROADCAST_PLAYER(			"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_GROUP_DEMOTE_BROADCAST_EQUALS(			"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_GROUP_DEMOTE_ERROR_STAFF(				"&cErreur : &6{player} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_GROUP_DEMOTE_ERROR_EQUALS(				"&cErreur : Vous êtes déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_GROUP_DEMOTE_ERROR_PROMOTE_STAFF(		"&cErreur : &6{player} &cpossède déjà un grade inférieur à &6{group} &cdans les mondes de type &6{type}&c."),
		USER_GROUP_DEMOTE_ERROR_PROMOTE_EQUALS(		"&cErreur : Vous possédez déjà un grade inférieur à &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_GROUP_INFO_DESCRIPTION(				"Affiche le groupe du joueur"),
		USER_GROUP_INFO_TITLE(						"&aLe groupe de &6{player} &a: &6{type}"),
		
		// User : SubGroup
		USER_SUBGROUP_DESCRIPTION(					"Gestion des sous-groupes d'un joueur"),
		
		USER_SUBGROUP_ADD_DESCRIPTION(				"Ajoute un sous-groupe à un joueur",
													"Add a group to a player's subgroup list"),
		USER_SUBGROUP_ADD_STAFF(					"&6{player} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_SUBGROUP_ADD_PLAYER(					"&7Vous possédez désormais les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_SUBGROUP_ADD_EQUALS(					"&7Vous possédez désormais les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_SUBGROUP_ADD_BROADCAST_PLAYER(			"&6{player} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_SUBGROUP_ADD_BROADCAST_EQUALS(			"&6{player} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_SUBGROUP_ADD_ERROR_STAFF(				"&cErreur : &6{player} &cpossède déjà les droits &6{group} &cdans les mondes de type &6{type}&c."),
		USER_SUBGROUP_ADD_ERROR_EQUALS(				"&cErreur : Vous possédez déjà les droits &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_SUBGROUP_REMOVE_DESCRIPTION(			"Supprime un sous-groupe à un joueur",
													"Remove a group from a player's subgroup list"),
		USER_SUBGROUP_REMOVE_STAFF(					"&6{player} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_SUBGROUP_REMOVE_PLAYER(				"&7Vous ne possédez plus les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_SUBGROUP_REMOVE_EQUALS(				"&7Vous ne possédez plus les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_SUBGROUP_REMOVE_BROADCAST_PLAYER(		"&6{player} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_SUBGROUP_REMOVE_BROADCAST_EQUALS(		"&6{player} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_SUBGROUP_REMOVE_ERROR_STAFF(			"&cErreur : &6{player} &cne possède pas les droits &6{group} &cdans les mondes de type &6{type}&c."),
		USER_SUBGROUP_REMOVE_ERROR_EQUALS(			"&cErreur : Vous ne possédez pas les droits &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_SUBGROUP_INFO_DESCRIPTION(				"Affiche la liste des sous-groupes d'un joueur"),
		USER_SUBGROUP_INFO_TITLE(					"&aLes sous-groupes de &6{player} &a: &6{type}"),
		
		// User : Permission
		USER_PERMISSION_DESCRIPTION(				"Gestion des permissions d'un joueur"),
		
		USER_PERMISSION_ADD_DESCRIPTION(			"Ajoute une permission à un joueur",
													"Add permission directly to the player"),
		USER_PERMISSION_ADD_TRUE_STAFF(				"&6{player} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_ADD_TRUE_EQUALS(			"&7Vous possédez désormais la permission '&6{permission}&7' &7dans les mondes de type &6{type}&7."),
		USER_PERMISSION_ADD_TRUE_BROADCAST_PLAYER(	"&6{player} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_PERMISSION_ADD_TRUE_BROADCAST_EQUALS(	"&6{player} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_ADD_TRUE_ERROR_STAFF(		"&cErreur : &6{player} &cpossède déjà la permissions '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_PERMISSION_ADD_TRUE_ERROR_EQUALS(		"&cErreur : Vous possédez déjà la permissions '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_PERMISSION_ADD_FALSE_STAFF(			"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_ADD_FALSE_EQUALS(			"&7Vous ne possédez plus la permission '&6{permission}&7' &7dans les mondes de type &6{type}&7."),
		USER_PERMISSION_ADD_FALSE_BROADCAST_PLAYER(	"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_PERMISSION_ADD_FALSE_BROADCAST_EQUALS(	"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_ADD_FALSE_ERROR_STAFF(		"&cErreur : &6{player} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_PERMISSION_ADD_FALSE_ERROR_EQUALS(		"&cErreur : Vous ne possédez pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		USER_PERMISSION_REMOVE_DESCRIPTION(			"Retire une permission à un joueur",
													"Removes permission directly from the player"),
		USER_PERMISSION_REMOVE_STAFF(				"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_REMOVE_EQUALS(				"&7Vous ne possédez plus la permission '&6{permission}&7' &7dans les mondes de type &6{type}&7."),
		USER_PERMISSION_REMOVE_BROADCAST_PLAYER(	"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_PERMISSION_REMOVE_BROADCAST_EQUALS(	"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_REMOVE_ERROR_STAFF(			"&cErreur : &6{player} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_PERMISSION_REMOVE_ERROR_EQUALS(		"&cErreur : Vous ne possédez pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		USER_PERMISSION_CHECK_DESCRIPTION(			"Vérifie si un joueur a une permission",
													"Verify if user has a permission"),
		USER_PERMISSION_CHECK_TRUE_EQUALS(			"&7Vous possédez la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_CHECK_TRUE_STAFF(			"&6{player} &7possède la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_CHECK_FALSE_EQUALS(			"&7Vous ne possédez pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_CHECK_FALSE_STAFF(			"&6{player} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_CHECK_UNDEFINED_EQUALS(		"&7Vous ne possédez pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_PERMISSION_CHECK_UNDEFINED_STAFF(		"&6{player} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		
		USER_PERMISSION_INFO_DESCRIPTION(			"Affiche la liste des permissions d'un joueur",
													"List all permissions from a player"),
		USER_PERMISSION_INFO_TITLE(					"&aLes permissions de &6{player} &a: &6{type}"),
		
		// User : Option
		USER_OPTION_DESCRIPTION(					"Gestion des options d'un joueur"),
		
		USER_OPTION_ADD_DESCRIPTION(				"Ajoute ou remplace une option à un joueur",
													"Add, or replaces, a option to a user"),
		USER_OPTION_ADD_STAFF(						"&6{player} &7possède désormais l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		USER_OPTION_ADD_EQUALS(						"&7Vous possédez désormais l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		USER_OPTION_ADD_ERROR(						"&cErreur : Le groupe &6{group} &cpossède déjà l'option '&6{option}&c' avec la valeur '&6{value}&c' dans les mondes de type &6{type}&c."),
		
		USER_OPTION_REMOVE_DESCRIPTION(				"Supprime une option à un joueur",
													"Remove a option from a user."),
		USER_OPTION_REMOVE_STAFF(					"&6{player} &7ne possède plus l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		USER_OPTION_REMOVE_EQUALS(					"&7Vous ne possédez plus l'option '&6{option}&7' &7dans les mondes de type &6{type}&7."),
		USER_OPTION_REMOVE_ERROR_STAFF(				"&cErreur : &6{player} &cne possède pas l'option '&6{option}&c' dans les mondes de type &6{type}&c."),
		USER_OPTION_REMOVE_ERROR_EQUALS(			"&cErreur : Vous ne possédez pas l'option '&6{option}&c' dans les mondes de type &6{type}&c."),
		
		USER_OPTION_CHECK_DESCRIPTION(				"Vérifie si un joueur a une option",
													"Verify a value of a option of user"),
		USER_OPTION_CHECK_DEFINED_STAFF(			"&6{player} &7possède l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		USER_OPTION_CHECK_DEFINED_EQUALS(			"&7Vous possédez l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		USER_OPTION_CHECK_UNDEFINED_EQUALS(			"&6{player} &7ne possède pas l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		USER_OPTION_CHECK_UNDEFINED_STAFF(			"&7Vous ne possédez pas l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		
		USER_OPTION_INFO_DESCRIPTION(				"Affiche la liste des options d'un joueur",
													"List variables a user has"),
		USER_OPTION_INFO_TITLE(						"&aLes options de &6{player} &a: &6{type}"),
		
		// Group : Group
		GROUP_DESCRIPTION(							"Gestion des groupes"),
		
		GROUP_ADD_DESCRIPTION(						"Ajoute un groupe à un monde"),
		GROUP_ADD_STAFF(							"&7Vous avez créé le groupe &6{group} &7dans les mondes de type &6{type}&7."),
		GROUP_ADD_ERROR(							"&cIl existe déjà un groupe &6{group} &cdans les mondes de type &6{type}&c."),
		
		GROUP_REMOVE_DESCRIPTION(					"Supprime un groupe d'un monde"),
		GROUP_REMOVE_STAFF(							"&7Vous avez supprimé le groupe &6{group} &7dans les mondes de type &6{type}&7."),
		
		GROUP_VERBOSE_DESCRIPTION(					"Active/Désactive le mode verbose sur un groupe"),
		GROUP_VERBOSE_ENABLE(						"&7Vous avez &aactivé &7le mode verbose sur groupe &6{group}&7."),
		GROUP_VERBOSE_ENABLE_ARGS(					"&7Vous avez &aactivé &7le mode verbose sur groupe &6{group} &7avec les arguments &6{arguments}&7."),
		GROUP_VERBOSE_DISABLE(						"&7Vous avez &cdésactivé &7le mode verbose sur groupe &6{group}&7."),
		GROUP_VERBOSE_DISABLE_ARGS(					"&7Vous avez &cdésactivé &7le mode verbose sur groupe &6{group} &7avec les arguments &6{arguments}&7."),
		
		GROUP_RENAME_DESCRIPTION(					"Renomme un groupe"),
		GROUP_RENAME_STAFF(							"&7Vous avez renommé le groupe &6{oldName} &7en &6{newName}&7."),
		GROUP_RENAME_EQUALS(						"&cErreur : Le groupe &6{group} &cporte déjà ce nom."),
		GROUP_RENAME_ERROR(							"&cErreur : Il y a déjà un groupe avec le nom &6{group}&c."),
		
		GROUP_DEFAULT_DESCRIPTION(					"Définit un groupe par défaut"),
		GROUP_DEFAULT_TRUE(							"&7Le groupe &6{group} &7est désormais le groupe par défaut &7dans les mondes de type &6{type}&7."),
		GROUP_DEFAULT_FALSE(						"&7Le groupe &6{group} &7n'est plus le groupe par défaut &7dans les mondes de type &6{type}&7."),
		GROUP_DEFAULT_ERROR_TRUE(					"&cIl existe déjà un groupe par défaut &cdans les mondes de type &6{type}&c."),
		GROUP_DEFAULT_ERROR_FALSE(					"&cLe groupe &6{group} &cn'est pas le groupe défaut &cdans les mondes de type &6{type}&c."),
		GROUP_DEFAULT_ERROR_EQUALS(					"&cLe groupe &6{group} &cest déjà la groupe par défaut &cdans les mondes de type &6{type}&c."),
		
		GROUP_LIST_DESCRIPTION(						"Affiche la liste des groupes d'un monde"),
		GROUP_LIST_TITLE(							"&aLes groupes de type &6{type}"),
		GROUP_LIST_DEFAULT(							"    &6&l➤ Le groupe par défaut : &7{group}"),
		GROUP_LIST_NAME(							"    &6&l➤ Les groupes : "),
		GROUP_LIST_LINE(							"        &7&l●  &7{group}"),
		GROUP_LIST_EMPTY(							"    &6&l➤ Groupes : &7Aucun"),
		
		GROUP_INFO_DESCRIPTION(						"Affiche les informations d'un groupe"),
		GROUP_INFO_TITLE(							"&aLe groupe &6{group} &adans les mondes de type &6{type}"),
		GROUP_INFO_IDENTIFIER(						"    &6&l➤ UUID : &7{identifier}"),
		GROUP_INFO_NAME(							"    &6&l➤ Name : &7{name}"),
		GROUP_INFO_DEFAULT(							"    &6&l➤ Par défaut : &7{value}"),
		GROUP_INFO_DEFAULT_TRUE(					"True"),
		GROUP_INFO_DEFAULT_FALSE(					"False"),
		GROUP_INFO_INHERITANCE(						"    &6&l➤ Les inhéritances : "),
		GROUP_INFO_INHERITANCE_LINE(				"        &7&l●  &7{inheritance}"),
		GROUP_INFO_INHERITANCE_EMPTY(				"    &6&l➤ Inhéritance : &7Aucune"),
		GROUP_INFO_INHERITANCE_TRANSIENT(			"    &6&l➤ Les inhéritances temporaires : "),
		GROUP_INFO_INHERITANCE_TRANSIENT_LINE(		"        &7&l●  &7{inheritance}"),
		GROUP_INFO_PERMISSION(						"    &6&l➤ Permissions : "),
		GROUP_INFO_PERMISSION_LINE(					"        &7&l●  &7{permission} : {value}"),
		GROUP_INFO_PERMISSION_TRUE(					"True"),
		GROUP_INFO_PERMISSION_FALSE(				"False"),
		GROUP_INFO_PERMISSION_EMPTY(				"    &6&l➤ Permissions : &7Aucune"),
		GROUP_INFO_PERMISSION_TRANSIENT(			"    &6&l➤ Permissions temporaires : "),
		GROUP_INFO_PERMISSION_TRANSIENT_LINE(		"        &7&l●  &7{permission} : {value}"),
		GROUP_INFO_PERMISSION_TRANSIENT_TRUE(		"True"),
		GROUP_INFO_PERMISSION_TRANSIENT_FALSE(		"False"),
		GROUP_INFO_OPTION(							"    &6&l➤ Options : "),
		GROUP_INFO_OPTION_LINE(						"        &7&l●  &7{option} : &7'{value}&7'"),
		GROUP_INFO_OPTION_EMPTY(					"    &6&l➤ Options : &7Aucune"),
		GROUP_INFO_OPTION_TRANSIENT(				"    &6&l➤ Options temporaires : "),
		GROUP_INFO_OPTION_TRANSIENT_LINE(			"        &7&l●  &7{option} : &7'{value}&7'"),
		
		// Group : Inheritance
		GROUP_INHERITANCE_DESCRIPTION(				"Gestion des inhéritances d'un groupe"),
		
		GROUP_INHERITANCE_ADD_DESCRIPTION(			"Ajoute une inhéritance à un groupe"),
		GROUP_INHERITANCE_ADD_STAFF(				"&7Vous avez ajouté l'inhéritance &6{inheritance} &7au groupe &6{group} &7dans les mondes de type &6{type}&7."),
		GROUP_INHERITANCE_ADD_ERROR_HAVE(			"&cLe groupe &6{group} &cpossède déjà l'inhéritance &6{inheritance} &cdans les mondes de type &6{type}&c."),
		GROUP_INHERITANCE_ADD_ERROR_EQUALS(			"&cL'inhéritance &6{inheritance} &cne peut pas être ajouté au groupe &6{group}&c."),
		
		GROUP_INHERITANCE_REMOVE_DESCRIPTION(		"Supprime une inhéritance d'un groupe"),
		GROUP_INHERITANCE_REMOVE_STAFF(				"&7Vous avez supprimé l'inhéritance &6{inheritance} &7au groupe &6{group} &7dans les mondes de type &6{type}&7."),
		GROUP_INHERITANCE_REMOVE_ERROR(				"&cLe groupe &6{group} &cne possède pas l'inhéritance &6{inheritance} &cdans les mondes de type &6{type}&c."),
		
		GROUP_INHERITANCE_INFO_DESCRIPTION(			"Affiche la liste des inhéritances d'un groupe"),
		GROUP_INHERITANCE_INFO_TITLE(				"&aLes inhéritances du groupe &6{group} &a: &6{type}"),
		
		// Group : Permission
		GROUP_PERMISSION_DESCRIPTION(				"Gestion des permissions d'un groupe"),
		
		GROUP_PERMISSION_ADD_DESCRIPTION(			"Ajoute une permission à un groupe"),
		GROUP_PERMISSION_ADD_TRUE(					"&7Le groupe &6{group} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_PERMISSION_ADD_FALSE(					"&7Le groupe &6{group} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_PERMISSION_ADD_ERROR_TRUE(			"&cErreur : Le groupe &6{group} &cpossède déjà la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		GROUP_PERMISSION_ADD_ERROR_FALSE(			"&cErreur : Le groupe &6{group} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		GROUP_PERMISSION_REMOVE_DESCRIPTION(		"Retire une permission à un groupe"),
		GROUP_PERMISSION_REMOVE_STAFF(				"&6{group} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_PERMISSION_REMOVE_ERROR(				"&cErreur : &6{group} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		GROUP_PERMISSION_CHECK_DESCRIPTION(			"Vérifie si un groupe a une permission"),
		GROUP_PERMISSION_CHECK_TRUE(				"&7Le groupe &6{group} &7possède la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_PERMISSION_CHECK_FALSE(				"&7Le groupe &6{group} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_PERMISSION_CHECK_UNDEFINED(			"&7Le groupe &6{group} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		
		GROUP_PERMISSION_INFO_DESCRIPTION(			"Affiche la liste des permissions d'un groupe"),
		GROUP_PERMISSION_INFO_TITLE(				"&aLes permissions du groupe &6{group} &a: &6{type}"),
		
		// Group : Option
		GROUP_OPTION_DESCRIPTION(					"Gestion des options d'un groupe"),
		
		GROUP_OPTION_ADD_DESCRIPTION(				"Ajoute une option à un groupe"),
		GROUP_OPTION_ADD_STAFF(						"&7Le groupe &6{group} &7possède désormais l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		GROUP_OPTION_ADD_ERROR(						"&cErreur : Le groupe &6{group} &cpossède déjà l'option '&6{option}&c' avec la valeur '&6{value}&c' dans les mondes de type &6{type}&c."),
	
		GROUP_OPTION_REMOVE_DESCRIPTION(			"Supprime une option à un groupe"),
		GROUP_OPTION_REMOVE_STAFF(					"&7Le groupe &6{group} &7ne possède plus l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		GROUP_OPTION_REMOVE_ERROR(					"&cErreur : &cLe groupe &6{group} &cne possède pas l'option '&6{option}&c' dans les mondes de type &6{type}&c."),
		
		GROUP_OPTION_CHECK_DESCRIPTION(				"Vérifie si un groupe a une option"),
		GROUP_OPTION_CHECK_DEFINED(					"&7Le groupe &6{group} &7possède l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		GROUP_OPTION_CHECK_UNDEFINED(				"&7Le groupe &6{group} &7ne possède pas l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		
		GROUP_OPTION_INFO_DESCRIPTION(				"Affiche la liste des options d'un groupe"),
		GROUP_OPTION_INFO_TITLE(					"&aLes options de &6{group} &a: &6{type}"),
		
		// Collection :
		COLLECTION_DESCRIPTION(							"Gestion des joueurs"),
		
		COLLECTION_INFO_DESCRIPTION(					"Affiche les informations d'un groupe"),
		COLLECTION_INFO_TITLE(							"&aLe subject &6{subject} &adans les mondes de type &6{type}"),
		COLLECTION_INFO_COLLECTION(						"    &6&l➤ Collection : &7{collection}"),
		COLLECTION_INFO_GROUP(							"    &6&l➤ Groupe : &7{group}"),
		COLLECTION_INFO_GROUP_EMPTY(					"    &6&l➤ Groupe : &7Aucun"),
		COLLECTION_INFO_SUBGROUP(						"    &6&l➤ Sous-groupes : "),
		COLLECTION_INFO_SUBGROUP_LINE(					"        &7&l●  &7{subgroup}"),
		COLLECTION_INFO_SUBGROUP_EMPTY(					"    &6&l➤ Sous-goupes : &7Aucun"),
		COLLECTION_INFO_GROUP_TRANSIENT(				"    &6&l➤ Groupe temporaires : &7{group}"),
		COLLECTION_INFO_SUBGROUP_TRANSIENT(				"    &6&l➤ Sous-groupes temporaires : "),
		COLLECTION_INFO_SUBGROUP_TRANSIENT_LINE(		"        &7&l●  &7{subgroup}"),
		COLLECTION_INFO_PERMISSION(						"    &6&l➤ Permissions : "),
		COLLECTION_INFO_PERMISSION_LINE(				"        &7&l●  &7{permission} : {value}"),
		COLLECTION_INFO_PERMISSION_TRUE(				"True"),
		COLLECTION_INFO_PERMISSION_FALSE(				"False"),
		COLLECTION_INFO_PERMISSION_EMPTY(				"    &6&l➤ Permissions : &7Aucune"),
		COLLECTION_INFO_PERMISSION_TRANSIENT(			"    &6&l➤ Permissions temporaires : "),
		COLLECTION_INFO_PERMISSION_TRANSIENT_LINE(		"        &7&l●  &7{permission} : {value}"),
		COLLECTION_INFO_PERMISSION_TRANSIENT_TRUE(		"True"),
		COLLECTION_INFO_PERMISSION_TRANSIENT_FALSE(		"False"),
		COLLECTION_INFO_OPTION(							"    &6&l➤ Options : "),
		COLLECTION_INFO_OPTION_LINE(					"        &7&l●  &7{option} : &7'{value}&7'"),
		COLLECTION_INFO_OPTION_EMPTY(					"    &6&l➤ Options : &7Aucune"),
		COLLECTION_INFO_OPTION_TRANSIENT(				"    &6&l➤ Options temporaires : "),
		COLLECTION_INFO_OPTION_TRANSIENT_LINE(			"        &7&l●  &7{option} : &7'{value}&7'"),
		
		COLLECTION_CLEAR_DESCRIPTION(					"Supprime un joueur"),
		COLLECTION_CLEAR_CONFIRMATION(					"&7Souhaitez-vous vraiment réinitialiser toutes les données de &6{subject} &7: {confirmation}"),
		COLLECTION_CLEAR_CONFIRMATION_BUTTON(			"&a[Confirmer]"),
		COLLECTION_CLEAR_CONFIRMATION_BUTTON_HOVER(		"&cCliquez ici pour réinitialisé toutes les données de &6{subject}&c."),
		COLLECTION_CLEAR(								"&7Vous avez réinitialisé toutes les données de &6{subject}&7."),
		COLLECTION_CLEAR_BROADCAST(						"&7Tous les données de &6{subject} &7 ont été réinitialisé par &6{staff}&7."),

		// Collection : Group
		COLLECTION_GROUP_DESCRIPTION(					"Gestion du groupe d'un joueur"),
		
		COLLECTION_GROUP_SET_DESCRIPTION(				"Défini le groupe d'un joueur",
														"Define the group of a player"),
		COLLECTION_GROUP_SET(							"&6{subject} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		COLLECTION_GROUP_SET_BROADCAST(					"&6{subject} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		COLLECTION_GROUP_SET_ERROR(						"&cErreur : &6{subject} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
	
		COLLECTION_GROUP_REMOVE_DESCRIPTION(			"Supprime le groupe d'un joueur",
														"Removes the group of a player"),
		COLLECTION_GROUP_REMOVE(						"&6{subject} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7."),
		COLLECTION_GROUP_REMOVE_BROADCAST(				"&6{subject} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		COLLECTION_GROUP_REMOVE_ERROR(					"&cErreur : &6{subject} &cn'a pas de groupe &cdans les mondes de type &6{type}&c."),
		
		COLLECTION_GROUP_PROMOTE_DESCRIPTION(			"Promouvoit un joueur",
														"Allows promoting a player up the inheritance tree."),
		COLLECTION_GROUP_PROMOTE(						"&6{subject} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		COLLECTION_GROUP_PROMOTE_BROADCAST(				"&6{subject} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		COLLECTION_GROUP_PROMOTE_ERROR( 				"&cErreur : &6{subject} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		COLLECTION_GROUP_PROMOTE_ERROR_DEMOTE(			"&cErreur : &6{subject} &cpossède déjà un grade supérieur à &6{group} &cdans les mondes de type &6{type}&c."),
		
		COLLECTION_GROUP_DEMOTE_DESCRIPTION(			"Rétrograde un joueur",
														"Allows demoting a player down the inheritance tree."),
		COLLECTION_GROUP_DEMOTE(						"&6{subject} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		COLLECTION_GROUP_DEMOTE_BROADCAST(				"&6{subject} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		COLLECTION_GROUP_DEMOTE_ERROR(					"&cErreur : &6{subject} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		COLLECTION_GROUP_DEMOTE_ERROR_PROMOTE(			"&cErreur : &6{subject} &cpossède déjà un grade inférieur à &6{group} &cdans les mondes de type &6{type}&c."),
		
		COLLECTION_GROUP_INFO_DESCRIPTION(				"Affiche le groupe du joueur"),
		COLLECTION_GROUP_INFO_TITLE(					"&aLe groupe de &6{subject} &a: &6{type}"),
		
		// Collection : SubGroup
		COLLECTION_SUBGROUP_DESCRIPTION(				"Gestion des sous-groupes d'un joueur"),
		
		COLLECTION_SUBGROUP_ADD_DESCRIPTION(			"Ajoute un sous-groupe à un joueur",
														"Add a group to a player's subgroup list"),
		COLLECTION_SUBGROUP_ADD(						"&6{subject} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7."),
		COLLECTION_SUBGROUP_ADD_BROADCAST(				"&6{subject} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		COLLECTION_SUBGROUP_ADD_ERROR(					"&cErreur : &6{subject} &cpossède déjà les droits &6{group} &cdans les mondes de type &6{type}&c."),
		
		COLLECTION_SUBGROUP_REMOVE_DESCRIPTION(			"Supprime un sous-groupe à un joueur",
														"Remove a group from a player's subgroup list"),
		COLLECTION_SUBGROUP_REMOVE(						"&6{subject} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7."),
		COLLECTION_SUBGROUP_REMOVE_BROADCAST(			"&6{subject} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		COLLECTION_SUBGROUP_REMOVE_ERROR(				"&cErreur : &6{subject} &cne possède pas les droits &6{group} &cdans les mondes de type &6{type}&c."),
		
		COLLECTION_SUBGROUP_INFO_DESCRIPTION(			"Affiche la liste des sous-groupes d'un joueur"),
		COLLECTION_SUBGROUP_INFO_TITLE(					"&aLes sous-groupes de &6{subject} &a: &6{type}"),
		
		// Collection : Permission
		COLLECTION_PERMISSION_DESCRIPTION(				"Gestion des permissions d'un joueur"),
		
		COLLECTION_PERMISSION_ADD_DESCRIPTION(			"Ajoute une permission à un joueur",
														"Add permission directly to the player"),
		COLLECTION_PERMISSION_ADD_TRUE(					"&6{subject} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_PERMISSION_ADD_TRUE_BROADCAST(		"&6{subject} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		COLLECTION_PERMISSION_ADD_TRUE_ERROR(			"&cErreur : &6{subject} &cpossède déjà la permissions '&6{permission}&c' dans les mondes de type &6{type}&c."),
		COLLECTION_PERMISSION_ADD_FALSE(				"&6{subject} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_PERMISSION_ADD_FALSE_BROADCAST(		"&6{subject} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		COLLECTION_PERMISSION_ADD_FALSE_ERROR(			"&cErreur : &6{subject} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		COLLECTION_PERMISSION_REMOVE_DESCRIPTION(		"Retire une permission à un joueur",
														"Removes permission directly from the player"),
		COLLECTION_PERMISSION_REMOVE(					"&6{subject} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_PERMISSION_REMOVE_BROADCAST(			"&6{subject} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		COLLECTION_PERMISSION_REMOVE_ERROR(				"&cErreur : &6{subject} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		COLLECTION_PERMISSION_CHECK_DESCRIPTION(		"Vérifie si un joueur a une permission",
														"Verify if user has a permission"),
		COLLECTION_PERMISSION_CHECK_TRUE(				"&6{subject} &7possède la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_PERMISSION_CHECK_FALSE(				"&6{subject} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_PERMISSION_CHECK_UNDEFINED(			"&6{subject} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		
		COLLECTION_PERMISSION_INFO_DESCRIPTION(			"Affiche la liste des permissions d'un joueur",
														"List all permissions from a player"),
		COLLECTION_PERMISSION_INFO_TITLE(				"&aLes permissions de &6{subject} &a: &6{type}"),
		
		// Collection : Option
		COLLECTION_OPTION_DESCRIPTION(					"Gestion des options d'un joueur"),
		
		COLLECTION_OPTION_ADD_DESCRIPTION(				"Ajoute ou remplace une option à un joueur",
														"Add, or replaces, a option to a user"),
		COLLECTION_OPTION_ADD(							"&6{subject} &7possède désormais l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_OPTION_ADD_ERROR(					"&cErreur : Le groupe &6{group} &cpossède déjà l'option '&6{option}&c' avec la valeur '&6{value}&c' dans les mondes de type &6{type}&c."),
		
		COLLECTION_OPTION_REMOVE_DESCRIPTION(			"Supprime une option à un joueur",
														"Remove a option from a user."),
		COLLECTION_OPTION_REMOVE(						"&6{subject} &7ne possède plus l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_OPTION_REMOVE_ERROR(					"&cErreur : &6{subject} &cne possède pas l'option '&6{option}&c' dans les mondes de type &6{type}&c."),
		
		COLLECTION_OPTION_CHECK_DESCRIPTION(			"Vérifie si un joueur a une option",
														"Verify a value of a option of user"),
		COLLECTION_OPTION_CHECK_DEFINED(				"&6{subject} &7possède l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		COLLECTION_OPTION_CHECK_UNDEFINED(				"&7Vous ne possédez pas l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		
		COLLECTION_OPTION_INFO_DESCRIPTION(				"Affiche la liste des options d'un joueur",
														"List variables a user has"),
		COLLECTION_OPTION_INFO_TITLE(					"&aLes options de &6{subject} &a: &6{type}"),
		
		PERMISSIONS_BROADCAST(""),
		PERMISSIONS_COMMANDS_EXECUTE(""),
		PERMISSIONS_COMMANDS_HELP(""),
		PERMISSIONS_COMMANDS_RELOAD(""),
		PERMISSIONS_COMMANDS_MIGRATE(""),
		PERMISSIONS_COMMANDS_USER_EXECUTE(""),
		PERMISSIONS_COMMANDS_USER_CLEAR(""),
		PERMISSIONS_COMMANDS_USER_INFO(""),
		PERMISSIONS_COMMANDS_USER_VERBOSE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_EXECUTE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_ADD(""),
		PERMISSIONS_COMMANDS_USER_GROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_PROMOTE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_DEMOTE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_INFO(""),
		PERMISSIONS_COMMANDS_USER_SUBGROUP_EXECUTE(""),
		PERMISSIONS_COMMANDS_USER_SUBGROUP_ADD(""),
		PERMISSIONS_COMMANDS_USER_SUBGROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_USER_SUBGROUP_INFO(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_EXECUTE(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_ADD(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_REMOVE(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_CHECK(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_INFO(""),
		PERMISSIONS_COMMANDS_USER_OPTION_EXECUTE(""),
		PERMISSIONS_COMMANDS_USER_OPTION_ADD(""),
		PERMISSIONS_COMMANDS_USER_OPTION_REMOVE(""),
		PERMISSIONS_COMMANDS_USER_OPTION_CHECK(""),
		PERMISSIONS_COMMANDS_USER_OPTION_INFO(""),
		PERMISSIONS_COMMANDS_GROUP_EXECUTE(""),
		PERMISSIONS_COMMANDS_GROUP_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_DEFAULT(""),
		PERMISSIONS_COMMANDS_GROUP_LIST(""),
		PERMISSIONS_COMMANDS_GROUP_RENAME(""),
		PERMISSIONS_COMMANDS_GROUP_INFO(""),
		PERMISSIONS_COMMANDS_GROUP_VERBOSE(""),
		PERMISSIONS_COMMANDS_GROUP_INHERITANCE_EXECUTE(""),
		PERMISSIONS_COMMANDS_GROUP_INHERITANCE_INFO(""),
		PERMISSIONS_COMMANDS_GROUP_INHERITANCE_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_INHERITANCE_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_EXECUTE(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_CHECK(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_INFO(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_EXECUTE(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_CHECK(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_INFO(""),
		PERMISSIONS_COMMANDS_COLLECTION_EXECUTE(""),
		PERMISSIONS_COMMANDS_COLLECTION_CLEAR(""),
		PERMISSIONS_COMMANDS_COLLECTION_INFO(""),
		PERMISSIONS_COMMANDS_COLLECTION_VERBOSE(""),
		PERMISSIONS_COMMANDS_COLLECTION_GROUP_EXECUTE(""),
		PERMISSIONS_COMMANDS_COLLECTION_GROUP_ADD(""),
		PERMISSIONS_COMMANDS_COLLECTION_GROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_COLLECTION_GROUP_PROMOTE(""),
		PERMISSIONS_COMMANDS_COLLECTION_GROUP_DEMOTE(""),
		PERMISSIONS_COMMANDS_COLLECTION_GROUP_INFO(""),
		PERMISSIONS_COMMANDS_COLLECTION_SUBGROUP_EXECUTE(""),
		PERMISSIONS_COMMANDS_COLLECTION_SUBGROUP_ADD(""),
		PERMISSIONS_COMMANDS_COLLECTION_SUBGROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_COLLECTION_SUBGROUP_INFO(""),
		PERMISSIONS_COMMANDS_COLLECTION_PERMISSION_EXECUTE(""),
		PERMISSIONS_COMMANDS_COLLECTION_PERMISSION_ADD(""),
		PERMISSIONS_COMMANDS_COLLECTION_PERMISSION_REMOVE(""),
		PERMISSIONS_COMMANDS_COLLECTION_PERMISSION_CHECK(""),
		PERMISSIONS_COMMANDS_COLLECTION_PERMISSION_INFO(""),
		PERMISSIONS_COMMANDS_COLLECTION_OPTION_EXECUTE(""),
		PERMISSIONS_COMMANDS_COLLECTION_OPTION_ADD(""),
		PERMISSIONS_COMMANDS_COLLECTION_OPTION_REMOVE(""),
		PERMISSIONS_COMMANDS_COLLECTION_OPTION_CHECK(""),
		PERMISSIONS_COMMANDS_COLLECTION_OPTION_INFO("");
		
		private final String path;
	    private final EMessageBuilder french;
	    private final EMessageBuilder english;
	    private EMessageFormat message;
	    private EMessageBuilder builder;
	    
	    private EPMessages(final String french) {   	
	    	this(EMessageFormat.builder().chat(new EFormatString(french), true));
	    }
	    
	    private EPMessages(final String french, final String english) {   	
	    	this(EMessageFormat.builder().chat(new EFormatString(french), true), 
	    		EMessageFormat.builder().chat(new EFormatString(english), true));
	    }
	    
	    private EPMessages(final EMessageBuilder french) {   	
	    	this(french, french);
	    }
	    
	    private EPMessages(final EMessageBuilder french, final EMessageBuilder english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = this.resolvePath();	    	
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
	
	@Override
	public EnumMessage getPrefix() {
		return EPMessages.PREFIX;
	}
}
