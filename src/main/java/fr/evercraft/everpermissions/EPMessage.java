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
		USER_CLEAR_DESCRIPTION(						"Supprime un joueur"),
		USER_CLEAR_STAFF(							"&7Vous avez réinitialisé toutes les données de &6{player}&7."),
		USER_CLEAR_PLAYER(							"&7Vos données ont été réinitialisé."),
		USER_CLEAR_EQUALS(							"&7Vous avez réinitialisé toutes vos données."),
		USER_CLEAR_BROADCAST_PLAYER(				"&7Tous les données de &6{player} &7 ont été réinitialisé par &6{staff}&7."),
		USER_CLEAR_BROADCAST_EQUALS(				"&7Tous les données de &6{player} &7 ont été réinitialisé."),

		// User : Group
		USER_ADD_GROUP_DESCRIPTION(					"Défini le groupe d'un joueur",
													"Define the group of a player"),
		USER_ADD_GROUP_STAFF(						"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_ADD_GROUP_PLAYER(						"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_ADD_GROUP_EQUALS(						"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_ADD_GROUP_BROADCAST_PLAYER(			"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_ADD_GROUP_BROADCAST_EQUALS(			"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_ADD_GROUP_ERROR_STAFF(					"&cErreur : &6{player} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_ADD_GROUP_ERROR_EQUALS(				"&cErreur : Vous êtes déjà &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_DEL_GROUP_DESCRIPTION(					"Supprime le groupe d'un joueur",
													"Removes the group of a player"),
		USER_DEL_GROUP_STAFF(						"&6{player} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEL_GROUP_PLAYER(						"&7Vous n'êtes plus &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_DEL_GROUP_EQUALS(						"&7Vous n'êtes plus &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEL_GROUP_BROADCAST_PLAYER(			"&6{player} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_DEL_GROUP_BROADCAST_EQUALS(			"&6{player} &7n'est plus &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEL_GROUP_ERROR_STAFF(					"&cErreur : &6{player} &cn'a pas de groupe &cdans les mondes de type &6{type}&c."),
		USER_DEL_GROUP_ERROR_EQUALS(				"&cErreur : Vous n'avez pas de groupe dans les mondes de type &6{type}&c."),
		
		USER_ADD_SUBGROUP_DESCRIPTION(				"Ajoute un sous-groupe à un joueur",
													"Add a group to a player's subgroup list"),
		USER_ADD_SUBGROUP_STAFF(					"&6{player} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_ADD_SUBGROUP_PLAYER(					"&7Vous possédez désormais les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_ADD_SUBGROUP_EQUALS(					"&7Vous possédez désormais les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_ADD_SUBGROUP_BROADCAST_PLAYER(			"&6{player} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_ADD_SUBGROUP_BROADCAST_EQUALS(			"&6{player} &7possède désormais les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_ADD_SUBGROUP_ERROR_STAFF(				"&cErreur : &6{player} &cpossède déjà les droits &6{group} &cdans les mondes de type &6{type}&c."),
		USER_ADD_SUBGROUP_ERROR_EQUALS(				"&cErreur : Vous possédez déjà les droits &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_DEL_SUBGROUP_DESCRIPTION(				"Supprime un sous-groupe à un joueur",
													"Remove a group from a player's subgroup list"),
		USER_DEL_SUBGROUP_STAFF(					"&6{player} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEL_SUBGROUP_PLAYER(					"&7Vous ne possédez plus les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_DEL_SUBGROUP_EQUALS(					"&7Vous ne possédez plus les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEL_SUBGROUP_BROADCAST_PLAYER(			"&6{player} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7 à cause de &6{staff}&7."),
		USER_DEL_SUBGROUP_BROADCAST_EQUALS(			"&6{player} &7ne possède plus les droits &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEL_SUBGROUP_ERROR_STAFF(				"&cErreur : &6{player} &cne possède pas les droits &6{group} &cdans les mondes de type &6{type}&c."),
		USER_DEL_SUBGROUP_ERROR_EQUALS(				"&cErreur : Vous ne possédez pas les droits &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_PROMOTE_DESCRIPTION(					"Promouvoit un joueur",
													"Allows promoting a player up the inheritance tree."),
		USER_PROMOTE_STAFF(							"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_PROMOTE_PLAYER(						"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_PROMOTE_EQUALS(						"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_PROMOTE_BROADCAST_PLAYER(				"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_PROMOTE_BROADCAST_EQUALS(				"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_PROMOTE_ERROR_STAFF( 					"&cErreur : &6{player} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_PROMOTE_ERROR_EQUALS(					"&cErreur : Vous êtes déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_PROMOTE_ERROR_DEMOTE_STAFF(			"&cErreur : &6{player} &cpossède déjà un grade supérieur à &6{group} &cdans les mondes de type &6{type}&c."),
		USER_PROMOTE_ERROR_DEMOTE_EQUALS(			"&cErreur : Vous possédez déjà un grade supérieur à &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_DEMOTE_DESCRIPTION(					"Rétrograde un joueur",
													"Allows demoting a player down the inheritance tree."),
		USER_DEMOTE_STAFF(							"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEMOTE_PLAYER(							"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_DEMOTE_EQUALS(							"&7Vous êtes désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEMOTE_BROADCAST_PLAYER(				"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7."),
		USER_DEMOTE_BROADCAST_EQUALS(				"&6{player} &7est désormais &6{group} &7dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_DEMOTE_ERROR_STAFF(					"&cErreur : &6{player} &cest déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_DEMOTE_ERROR_EQUALS(					"&cErreur : Vous êtes déjà &6{group} &cdans les mondes de type &6{type}&c."),
		USER_DEMOTE_ERROR_PROMOTE_STAFF(			"&cErreur : &6{player} &cpossède déjà un grade inférieur à &6{group} &cdans les mondes de type &6{type}&c."),
		USER_DEMOTE_ERROR_PROMOTE_EQUALS(			"&cErreur : Vous possédez déjà un grade inférieur à &6{group} &cdans les mondes de type &6{type}&c."),
		
		USER_LIST_GROUP_DESCRIPTION(				"Affiche la liste des groupes d'un joueur",
													"Tell the group that this user belongs to"),
		USER_LIST_GROUP_TITLE(						"&aLes groupes de &6{player} &a: &6{type}"),
		USER_LIST_GROUP_GROUP(						"    &6&l➤  Groupe : &7{group}"),
		USER_LIST_GROUP_GROUP_EMPTY(				"    &6&l➤  Groupe : &7Aucun"),
		USER_LIST_GROUP_SUBGROUP(					"    &6&l➤  Les sous-groupes : "),
		USER_LIST_GROUP_SUBGROUP_LINE(				"        &7&l●  &7{group}"),
		USER_LIST_GROUP_SUBGROUP_EMPTY(				"    &6&l➤  Sous-groupes : &7Aucun"),
		USER_LIST_GROUP_TRANSIENT_GROUP(			"    &6&l➤  Groupe temporaire : &7{group}"),
		USER_LIST_GROUP_TRANSIENT_SUBGROUP(			"    &6&l➤  Les groupes temporaires : "),
		USER_LIST_GROUP_TRANSIENT_SUBGROUP_LINE(	"        &7&l●  &7{group}"),
		
		// User : Permission
		USER_ADD_PERMISSION_DESCRIPTION(			"Ajoute une permission à un joueur",
													"Add permission directly to the player"),
		USER_ADD_PERMISSION_TRUE_STAFF(				"&6{player} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_ADD_PERMISSION_TRUE_EQUALS(			"&7Vous possédez désormais la permission '&6{permission}&7' &7dans les mondes de type &6{type}&7."),
		USER_ADD_PERMISSION_TRUE_BROADCAST_PLAYER(	"&6{player} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_ADD_PERMISSION_TRUE_BROADCAST_EQUALS(	"&6{player} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_ADD_PERMISSION_TRUE_ERROR_STAFF(		"&cErreur : &6{player} &cpossède déjà la permissions '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_ADD_PERMISSION_TRUE_ERROR_EQUALS(		"&cErreur : Vous possédez déjà la permissions '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_ADD_PERMISSION_FALSE_STAFF(			"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_ADD_PERMISSION_FALSE_EQUALS(			"&7Vous ne possédez plus la permission '&6{permission}&7' &7dans les mondes de type &6{type}&7."),
		USER_ADD_PERMISSION_FALSE_BROADCAST_PLAYER(	"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_ADD_PERMISSION_FALSE_BROADCAST_EQUALS(	"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_ADD_PERMISSION_FALSE_ERROR_STAFF(		"&cErreur : &6{player} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_ADD_PERMISSION_FALSE_ERROR_EQUALS(		"&cErreur : Vous ne possédez pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		USER_DEL_PERMISSION_DESCRIPTION(			"Retire une permission à un joueur",
													"Removes permission directly from the player"),
		USER_DEL_PERMISSION_STAFF(					"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_DEL_PERMISSION_EQUALS(					"&7Vous ne possédez plus la permission '&6{permission}&7' &7dans les mondes de type &6{type}&7."),
		USER_DEL_PERMISSION_BROADCAST_PLAYER(		"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7 grâce à &6{staff}&7."),
		USER_DEL_PERMISSION_BROADCAST_EQUALS(		"&6{player} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_DEL_PERMISSION_ERROR_STAFF(			"&cErreur : &6{player} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		USER_DEL_PERMISSION_ERROR_EQUALS(			"&cErreur : Vous ne possédez pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		USER_CHECK_PERMISSION_DESCRIPTION(			"Vérifie si un joueur a une permission",
													"Verify if user has a permission"),
		USER_CHECK_PERMISSION_TRUE_EQUALS(			"&7Vous possédez la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_PERMISSION_TRUE_STAFF(			"&6{player} &7possède la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_PERMISSION_FALSE_EQUALS(			"&7Vous ne possédez pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_PERMISSION_FALSE_STAFF(			"&6{player} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_PERMISSION_UNDEFINED_EQUALS(		"&7Vous ne possédez pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_PERMISSION_UNDEFINED_STAFF(		"&6{player} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		
		USER_LIST_PERMISSION_DESCRIPTION(			"Affiche la liste des permissions d'un joueur",
													"List all permissions from a player"),
		USER_LIST_PERMISSION_TITLE(					"&aLes permissions de &6{player} &a: &6{type}"),
		USER_LIST_PERMISSION_PERMISSION(			"    &6&l➤  Permissions : "),
		USER_LIST_PERMISSION_PERMISSION_LINE_TRUE(	"        &7&l●  &7{permission} : True"),
		USER_LIST_PERMISSION_PERMISSION_LINE_FALSE(	"        &7&l●  &7{permission} : False"),
		USER_LIST_PERMISSION_PERMISSION_EMPTY(		"    &6&l➤ Permissions : &7Aucune"),
		USER_LIST_PERMISSION_TRANSIENT(				"    &6&l➤ Permissions temporaires : "),
		USER_LIST_PERMISSION_TRANSIENT_LINE_TRUE(	"        &7&l●  &7{permission} : True"),
		USER_LIST_PERMISSION_TRANSIENT_LINE_FALSE(	"        &7&l●  &7{permission} : False"),
		
		// User : Option
		USER_ADD_OPTION_DESCRIPTION(				"Ajoute ou remplace une option à un joueur",
													"Add, or replaces, a option to a user"),
		USER_ADD_OPTION_STAFF(						"&6{player} &7possède désormais l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		USER_ADD_OPTION_EQUALS(						"&7Vous possédez désormais l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		
		USER_DEL_OPTION_DESCRIPTION(				"Supprime une option à un joueur",
													"Remove a option from a user."),
		USER_DEL_OPTION_STAFF(						"&6{player} &7ne possède plus l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		USER_DEL_OPTION_EQUALS(						"&7Vous ne possédez plus l'option '&6{option}&7' &7dans les mondes de type &6{type}&7."),
		USER_DEL_OPTION_ERROR_STAFF(				"&cErreur : &6{player} &cne possède pas l'option '&6{option}&c' dans les mondes de type &6{type}&c."),
		USER_DEL_OPTION_ERROR_EQUALS(				"&cErreur : Vous ne possédez pas l'option '&6{option}&c' dans les mondes de type &6{type}&c."),
		
		USER_CHECK_OPTION_DESCRIPTION(				"Vérifie si un joueur a une option",
													"Verify a value of a option of user"),
		USER_CHECK_OPTION_DEFINED_STAFF(			"&6{player} &7possède l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_OPTION_DEFINED_EQUALS(			"&7Vous possédez l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_OPTION_UNDEFINED_EQUALS(			"&6{player} &7ne possède pas l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		USER_CHECK_OPTION_UNDEFINED_STAFF(			"&7Vous ne possédez pas l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		
		USER_LIST_OPTION_DESCRIPTION(				"Affiche la liste des options d'un joueur",
													"List variables a user has"),
		USER_LIST_OPTION_TITLE(						"&aLes options de &6{player} &a: &6{type}"),
		USER_LIST_OPTION_OPTION(					"    &6&l➤  Options : "),
		USER_LIST_OPTION_OPTION_LINE(				"        &7&l●  &7{option} : &7'{value}&7'"),
		USER_LIST_OPTION_OPTION_EMPTY(				"    &6&l➤ Options : &7Aucune"),
		USER_LIST_OPTION_TRANSIENT(					"    &6&l➤ Options temporaires : "),
		USER_LIST_OPTION_TRANSIENT_LINE(			"        &7&l●  &7{option} : &7'{value}&7'"),
		
		// Group : Group
		GROUP_ADD_GROUP_DESCRIPTION(				"Ajoute un groupe à un monde"),
		GROUP_ADD_GROUP_STAFF(						"&7Vous avez créé le groupe &6{group} &7dans les mondes de type &6{type}&7."),
		GROUP_ADD_GROUP_ERROR(						"&cIl existe déjà un groupe &6{group} &cdans les mondes de type &6{type}&c."),
		
		GROUP_DEL_GROUP_DESCRIPTION(				"Supprime un groupe d'un monde"),
		GROUP_DEL_GROUP_STAFF(						"&7Vous avez supprimé le groupe &6{group} &7dans les mondes de type &6{type}&7."),
		
		GROUP_DEFAULT_GROUP_DESCRIPTION(			"Définit un groupe par défaut"),
		GROUP_DEFAULT_GROUP_TRUE(					"&7Le groupe &6{group} &7est désormais le groupe par défaut &7dans les mondes de type &6{type}&7."),
		GROUP_DEFAULT_GROUP_FALSE(					"&7Le groupe &6{group} &7n'est plus le groupe par défaut &7dans les mondes de type &6{type}&7."),
		GROUP_DEFAULT_GROUP_ERROR_TRUE(				"&cIl existe déjà un groupe par défaut &cdans les mondes de type &6{type}&c."),
		GROUP_DEFAULT_GROUP_ERROR_FALSE(			"&cLe groupe &6{group} &cn'est pas le groupe défaut &cdans les mondes de type &6{type}&c."),
		GROUP_DEFAULT_GROUP_ERROR_EQUALS(			"&cLe groupe &6{group} &cest déjà la groupe par défaut &cdans les mondes de type &6{type}&c."),
		GROUP_DEFAULT_GROUP_ERROR_BOOLEAN(			"&cErreur : La valeur 'default' ne peut-être que &6&lTrue &cou &6&lFalse"),
		
		GROUP_LIST_GROUP_DESCRIPTION(				"Affiche la liste des groupes d'un monde"),
		GROUP_LIST_GROUP_TITLE(						"&aLes groupes de type &6{type}"),
		GROUP_LIST_GROUP_DEFAULT(					"    &6&l➤  Le groupe par défaut : &7{group}"),
		GROUP_LIST_GROUP_NAME(						"    &6&l➤  Les groupes : "),
		GROUP_LIST_GROUP_LINE(						"        &7&l●  &7{group}"),
		GROUP_LIST_GROUP_EMPTY(						"    &6&l➤  Groupes : &7Aucun"),
		
		// Group : Inheritance
		GROUP_ADD_INHERITANCE_DESCRIPTION(			"Ajoute une inhéritance à un groupe"),
		GROUP_ADD_INHERITANCE_STAFF(				"&7Vous avez ajouté l'inhéritance &6{inheritance} &7au groupe &6{group} &7dans les mondes de type &6{type}&7."),
		GROUP_ADD_INHERITANCE_ERROR_HAVE(			"&cLe groupe &6{group} &cpossède déjà l'inhéritance &6{inheritance} &cdans les mondes de type &6{type}&c."),
		GROUP_ADD_INHERITANCE_ERROR_EQUALS(			"&cL'inhéritance &6{inheritance} &cne peut pas être ajouté au groupe &6{group}&c."),
		
		GROUP_DEL_INHERITANCE_DESCRIPTION(			"Supprime une inhéritance d'un groupe"),
		GROUP_DEL_INHERITANCE_STAFF(				"&7Vous avez supprimé l'inhéritance &6{inheritance} &7au groupe &6{group} &7dans les mondes de type &6{type}&7."),
		GROUP_DEL_INHERITANCE_ERROR(				"&cLe groupe &6{group} &cne possède pas l'inhéritance &6{inheritance} &cdans les mondes de type &6{type}&c."),
		
		GROUP_LIST_INHERITANCE_DESCRIPTION(			"Affiche la liste des inhéritances d'un groupe"),
		GROUP_LIST_INHERITANCE_TITLE(				"&aLes inhéritances du groupe &6{group} &a: &6{type}"),
		GROUP_LIST_INHERITANCE_INHERITANCE(			"    &6&l➤  Les inhéritances : "),
		GROUP_LIST_INHERITANCE_INHERITANCE_LINE(	"        &7&l●  &7{inheritance}"),
		GROUP_LIST_INHERITANCE_INHERITANCE_EMPTY(	"    &6&l➤  Inhéritance : &7Aucune"),
		GROUP_LIST_INHERITANCE_TRANSIENT(			"    &6&l➤  Les inhéritances temporaires : "),
		GROUP_LIST_INHERITANCE_TRANSIENT_LINE(		"        &7&l●  &7{inheritance}"),
		
		// Group : Permission
		GROUP_ADD_PERMISSION_DESCRIPTION(			"Ajoute une permission à un groupe"),
		GROUP_ADD_PERMISSION_TRUE(					"&7Le groupe &6{group} &7possède désormais la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_ADD_PERMISSION_FALSE(					"&7Le groupe &6{group} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_ADD_PERMISSION_ERROR_TRUE(			"&7Le groupe &6{group} &7possède déjà la permission '&6{permission}&7' dans les mondes de type &6{type}&c."),
		GROUP_ADD_PERMISSION_ERROR_FALSE(			"&7Le groupe &6{group} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&c."),
		
		GROUP_DEL_PERMISSION_DESCRIPTION(			"Retire une permission à un groupe"),
		GROUP_DEL_PERMISSION_STAFF(					"&6{group} &7ne possède plus la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_DEL_PERMISSION_ERROR(					"&cErreur : &6{group} &cne possède pas la permission '&6{permission}&c' dans les mondes de type &6{type}&c."),
		
		GROUP_CHECK_PERMISSION_DESCRIPTION(			"Vérifie si un groupe a une permission"),
		GROUP_CHECK_PERMISSION_TRUE(				"&7Le groupe &6{group} &7possède la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_CHECK_PERMISSION_FALSE(				"&7Le groupe &6{group} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		GROUP_CHECK_PERMISSION_UNDEFINED(			"&7Le groupe &6{group} &7ne possède pas la permission '&6{permission}&7' dans les mondes de type &6{type}&7."),
		
		GROUP_LIST_PERMISSION_DESCRIPTION(			"Affiche la liste des permissions d'un groupe"),
		GROUP_LIST_PERMISSION_TITLE(				"&aLes permissions du groupe &6{group} &a: &6{type}"),
		GROUP_LIST_PERMISSION_PERMISSION(			"    &6&l➤  Permissions : "),
		GROUP_LIST_PERMISSION_PERMISSION_LINE_TRUE(	"        &7&l●  &7{permission} : True"),
		GROUP_LIST_PERMISSION_PERMISSION_LINE_FALSE("        &7&l●  &7{permission} : False"),
		GROUP_LIST_PERMISSION_PERMISSION_EMPTY(		"    &6&l➤ Permissions : &7Aucune"),
		GROUP_LIST_PERMISSION_TRANSIENT(			"    &6&l➤ Permissions temporaires : "),
		GROUP_LIST_PERMISSION_TRANSIENT_LINE_TRUE(	"        &7&l●  &7{permission} : True"),
		GROUP_LIST_PERMISSION_TRANSIENT_LINE_FALSE(	"        &7&l●  &7{permission} : False"),
		
		// Group : Option
		GROUP_ADD_OPTION_DESCRIPTION(				"Ajoute une option à un groupe"),
		GROUP_ADD_OPTION_STAFF(						"&7Le groupe &6{group} &7possède désormais l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
	
		GROUP_DEL_OPTION_DESCRIPTION(				"Supprime une option à un groupe"),
		GROUP_DEL_OPTION_STAFF(						"&7Le groupe &6{group} &7ne possède plus l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		GROUP_DEL_OPTION_ERROR(						"&cErreur : &cLe groupe &6{group} &cne possède pas l'option '&6{option}&c' dans les mondes de type &6{type}&c."),
		
		GROUP_CHECK_OPTION_DESCRIPTION(				"Vérifie si un groupe a une option"),
		GROUP_CHECK_OPTION_DEFINED(					"&7Le groupe &6{group} &7possède l'option '&6{option}&7' avec la valeur '&6{value}&7' dans les mondes de type &6{type}&7."),
		GROUP_CHECK_OPTION_UNDEFINED(				"&7Le groupe &6{group} &7ne possède pas l'option '&6{option}&7' dans les mondes de type &6{type}&7."),
		
		GROUP_LIST_OPTION_DESCRIPTION(				"Affiche la liste des options d'un groupe"),
		GROUP_LIST_OPTION_TITLE(					"&aLes options de &6{group} &a: &6{type}"),
		GROUP_LIST_OPTION_OPTION(					"    &6&l➤  Options : "),
		GROUP_LIST_OPTION_OPTION_LINE(				"        &7&l●  &7{option} : &7'{value}&7'"),
		GROUP_LIST_OPTION_OPTION_EMPTY(				"    &6&l➤ Options : &7Aucune"),
		GROUP_LIST_OPTION_TRANSIENT(				"    &6&l➤ Options temporaires : "),
		GROUP_LIST_OPTION_TRANSIENT_LINE(			"        &7&l●  &7{option} : &7'{value}&7'"),
		
		// Other
		OTHER_NOT_FOUND(							"&cErreur : Ce subject n'existe pas."),
		
		// Other : Permission
		OTHER_ADD_PERMISSION_DESCRIPTION(			"Ajoute une permission à un Subject"),
		OTHER_ADD_PERMISSION_TRUE(					"&6{subject} &7possède désormais la permission '&6{permission}&7'."),
		OTHER_ADD_PERMISSION_FALSE(					"&6{subject} &7ne possède plus la permission '&6{permission}&7'."),
		OTHER_ADD_PERMISSION_ERROR_TRUE(			"&cErreur : &6{subject} &cpossède déjà la permissions '&6{permission}&c'."),
		OTHER_ADD_PERMISSION_ERROR_FALSE(			"&cErreur : &6{subject} &cne possède pas la permission '&6{permission}&c'."),
		
		OTHER_DEL_PERMISSION_DESCRIPTION(			"Retire une permission à un Subject"),
		OTHER_DEL_PERMISSION_PLAYER(				"&6{subject} &7ne possède plus la permission '&6{permission}&7'."),
		OTHER_DEL_PERMISSION_ERROR(					"&cErreur : &6{subject} &cne possède pas la permission '&6{permission}&c'."),
		
		OTHER_CHECK_PERMISSION_DESCRIPTION(			"Vérifie si un Subject a une permission"),
		OTHER_CHECK_PERMISSION_TRUE(				"&6{subject} &7possède la permission '&6{permission}&7'."),
		OTHER_CHECK_PERMISSION_FALSE(				"&6{subject} &7ne possède pas la permission '&6{permission}&7'."),	
		
		OTHER_LIST_PERMISSION_DESCRIPTION(			"Affiche la liste des permissions d'un Subject"),
		OTHER_LIST_PERMISSION_TITLE(				"&aLes permissions de &6{subject}"),
		OTHER_LIST_PERMISSION_PERMISSION(			"    &6&l➤  Permissions : "),
		OTHER_LIST_PERMISSION_PERMISSION_LINE_TRUE(	"        &7&l●  &7{permission} : True"),
		OTHER_LIST_PERMISSION_PERMISSION_LINE_FALSE("        &7&l●  &7{permission} : False"),
		OTHER_LIST_PERMISSION_PERMISSION_EMPTY(		"    &6&l➤ Permissions : &7Aucune"),
		OTHER_LIST_PERMISSION_TRANSIENT(			"    &6&l➤ Permissions temporaires : "),
		OTHER_LIST_PERMISSION_TRANSIENT_LINE_TRUE(	"        &7&l●  &7{permission} : True"),
		OTHER_LIST_PERMISSION_TRANSIENT_LINE_FALSE(	"        &7&l●  &7{permission} : False"),
		
		// User : Option
		OTHER_ADD_OPTION_DESCRIPTION(				"Ajoute une option à un Subject"),
		OTHER_ADD_OPTION_PLAYER(					"&6{subject} &7possède désormais l'option '&6{option}&7' avec la valeur '&6{value}&7'."),
		OTHER_ADD_OPTION_NAME_COLOR(				"&6"),
		
		OTHER_DEL_OPTION_DESCRIPTION(				"Supprime une option à un Subject"),
		OTHER_DEL_OPTION_PLAYER(					"&6{subject} &7ne possède plus l'option '&6{option}&7'."),
		OTHER_DEL_OPTION_ERROR(						"&cErreur : &6{subject} &cne possède pas l'option '&6{option}&c'."),
		
		OTHER_CHECK_OPTION_DESCRIPTION(				"Vérifie si un Subject a une option"),
		OTHER_CHECK_OPTION_DEFINED(					"&6{subject} &7possède l'option '&6{option}&7' avec la valeur '&6{value}&7'."),
		OTHER_CHECK_OPTION_UNDEFINED(				"&6{subject} &7ne possède pas l'option '&6{option}&7'."),
		
		OTHER_LIST_OPTION_DESCRIPTION(				"Affiche la liste des options d'un Subject"),
		OTHER_LIST_OPTION_TITLE(					"&aLes options de &6{subject}"),
		OTHER_LIST_OPTION_OPTION(					"    &6&l➤  Options : "),
		OTHER_LIST_OPTION_OPTION_LINE(				"        &7&l●  &7{option} : &7'{value}&7'"),
		OTHER_LIST_OPTION_OPTION_EMPTY(				" 	 &6&l➤ Options : &7Aucune"),
		OTHER_LIST_OPTION_TRANSIENT(				"    &6&l➤ Options temporaires : "),
		OTHER_LIST_OPTION_TRANSIENT_LINE(			"        &7&l●  &7{option} : &7'{value}&7'"),
		
		PERMISSIONS_BROADCAST(""),
		PERMISSIONS_COMMANDS_EXECUTE(""),
		PERMISSIONS_COMMANDS_HELP(""),
		PERMISSIONS_COMMANDS_RELOAD(""),
		PERMISSIONS_COMMANDS_MIGRATE(""),
		PERMISSIONS_COMMANDS_USER_CLEAR(""),
		PERMISSIONS_COMMANDS_USER_GROUP_ADD(""),
		PERMISSIONS_COMMANDS_USER_GROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_ADDSUB(""),
		PERMISSIONS_COMMANDS_USER_GROUP_REMOVESUB(""),
		PERMISSIONS_COMMANDS_USER_GROUP_PROMOTE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_DEMOTE(""),
		PERMISSIONS_COMMANDS_USER_GROUP_LIST(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_ADD(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_REMOVE(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_CHECK(""),
		PERMISSIONS_COMMANDS_USER_PERMISSION_LIST(""),
		PERMISSIONS_COMMANDS_USER_OPTION_ADD(""),
		PERMISSIONS_COMMANDS_USER_OPTION_REMOVE(""),
		PERMISSIONS_COMMANDS_USER_OPTION_CHECK(""),
		PERMISSIONS_COMMANDS_USER_OPTION_LIST(""),
		PERMISSIONS_COMMANDS_GROUP_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_DEFAULT(""),
		PERMISSIONS_COMMANDS_GROUP_LIST(""),
		PERMISSIONS_COMMANDS_GROUP_INHERITANCE_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_INHERITANCE_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_INHERITANCE_LIST(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_CHECK(""),
		PERMISSIONS_COMMANDS_GROUP_PERMISSION_LIST(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_ADD(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_REMOVE(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_CHECK(""),
		PERMISSIONS_COMMANDS_GROUP_OPTION_LIST(""),
		PERMISSIONS_COMMANDS_OTHER_GROUP_ADD(""),
		PERMISSIONS_COMMANDS_OTHER_GROUP_REMOVE(""),
		PERMISSIONS_COMMANDS_OTHER_GROUP_ADDSUB(""),
		PERMISSIONS_COMMANDS_OTHER_GROUP_REMOVESUB(""),
		PERMISSIONS_COMMANDS_OTHER_GROUP_PROMOTE(""),
		PERMISSIONS_COMMANDS_OTHER_GROUP_DEMOTE(""),
		PERMISSIONS_COMMANDS_OTHER_GROUP_LIST(""),
		PERMISSIONS_COMMANDS_OTHER_PERMISSION_ADD(""),
		PERMISSIONS_COMMANDS_OTHER_PERMISSION_REMOVE(""),
		PERMISSIONS_COMMANDS_OTHER_PERMISSION_CHECK(""),
		PERMISSIONS_COMMANDS_OTHER_PERMISSION_LIST(""),
		PERMISSIONS_COMMANDS_OTHER_OPTION_ADD(""),
		PERMISSIONS_COMMANDS_OTHER_OPTION_REMOVE(""),
		PERMISSIONS_COMMANDS_OTHER_OPTION_CHECK(""),
		PERMISSIONS_COMMANDS_OTHER_OPTION_LIST("");
		
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
}
