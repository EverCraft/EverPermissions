package fr.evercraft.everpermissions;

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.PermissionService;

import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everpermissions.commands.EPManagerCommands;
import fr.evercraft.everpermissions.data.EPManagerData;
import fr.evercraft.everpermissions.event.EPManagerEvent;
import fr.evercraft.everpermissions.service.EPermissionService;

@Plugin(id = "fr.evercraft.everpermissions", 
		name = "EverPermissions", 
		version = "1.2", 
		description = "Permissions management",
		url = "http://evercraft.fr/",
		authors = {"rexbut"},
		dependencies = {
		    @Dependency(id = "fr.evercraft.everapi", version = "1.2"),
		    @Dependency(id = "fr.evercraft.everchat", optional = true)
		})
public class EverPermissions extends EPlugin {
	private EPConfig config;
	private EPMessage messages;
	
	private EPManagerEvent managerEvent;
	private EPManagerData managerData;
	private EPManagerCommands managerCommands;
	
	private EPermissionService service;

	@Override
	protected void onPreEnable() throws PluginDisableException, ServerDisableException {
		this.config = new EPConfig(this);
		this.messages = new EPMessage(this);
		
		this.managerData = new EPManagerData(this);		
		this.managerEvent = new EPManagerEvent(this);
		
		this.service = new EPermissionService(this);
		this.getGame().getServiceManager().setProvider(this, PermissionService.class, this.service);
	}
	
	@Override
	public void onCompleteEnable() {
		this.managerCommands = new EPManagerCommands(this);
		
		this.getGame().getEventManager().registerListeners(this, new EPListener(this));
	}
	
	@Override
	protected void onReload() throws PluginDisableException {
		this.reloadConfigurations();
		
		this.managerData.reload();
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

	public EPermissionService getService() {
		return this.service;
	}
	
	public EPManagerData getManagerData() {
		return this.managerData;
	}
	
	public EPManagerEvent getManagerEvent() {
		return this.managerEvent;
	}

	public EPManagerCommands getManagerCommands() {
		return this.managerCommands;
	}
}
