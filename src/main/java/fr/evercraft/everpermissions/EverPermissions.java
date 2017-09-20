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

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.PermissionService;

import fr.evercraft.everapi.EverAPI;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everpermissions.command.EPManagerCommands;
import fr.evercraft.everpermissions.service.EPermissionService;

@Plugin(id = "everpermissions", 
		name = "EverPermissions", 
		version = EverAPI.VERSION, 
		description = "Permissions management",
		url = "http://evercraft.fr/",
		authors = {"rexbut"},
		dependencies = {
		    @Dependency(id = "everapi", version = EverAPI.VERSION),
		    @Dependency(id = "spongeapi", version = EverAPI.SPONGEAPI_VERSION)
		})
public class EverPermissions extends EPlugin<EverPermissions> {
	private EPConfig config;
	private EPMessage messages;
	
	private EPManagerCommands managerCommands;
	
	private EPermissionService service;
	private EPDataBases database;

	@Override
	protected void onPreEnable() throws PluginDisableException, ServerDisableException {
		this.config = new EPConfig(this);
		this.messages = new EPMessage(this);
		
		this.database = new EPDataBases(this);
		
		this.service = new EPermissionService(this);
		this.service.load();
		this.getGame().getServiceManager().setProvider(this, PermissionService.class, this.service);
	}
	
	@Override
	public void onCompleteEnable() {
		this.managerCommands = new EPManagerCommands(this);
		
		this.getGame().getEventManager().registerListeners(this, new EPListener(this));
	}
	
	@Override
	protected void onReload() throws PluginDisableException, ServerDisableException {
		super.onReload();
		
		this.service.reload();
	}
	
	@Override
	protected void onDisable() {
	}
	
	/*
	 * Accesseurs
	 */
	
	public EPConfig getConfigs() {
		return this.config;
	}
	
	public EPMessage getMessages() {
		return this.messages;
	}
	
	public EPPermissions[] getPermissions() {
		return EPPermissions.values();
	}
	
	public EPDataBases getDataBases() {
		return this.database;
	}

	public EPermissionService getService() {
		return this.service;
	}

	public EPManagerCommands getManagerCommands() {
		return this.managerCommands;
	}
}
