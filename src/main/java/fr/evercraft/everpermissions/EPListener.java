package fr.evercraft.everpermissions;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;

public class EPListener {
	private EverPermissions plugin;
	
	public EPListener(final EverPermissions plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Ajoute le joueur dans le cache
	 */
	@Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Auth event) {
		this.plugin.getService().getUserSubjects().get(event.getProfile().getUniqueId().toString());
    }
	
	/**
	 * Ajoute le joueur à la liste
	 */
	@Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Join event) {
		this.plugin.getService().getUserSubjects().registerPlayer(event.getTargetEntity().getIdentifier());
    }
    
	/**
	 * Supprime le joueur de la liste
	 */
    @Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Disconnect event) {
        this.plugin.getService().getUserSubjects().removePlayer(event.getTargetEntity().getIdentifier());
    }
	
    /**
     * Chargement du monde
     */
    @Listener
    public void onLoadWorldEvent(final LoadWorldEvent event) {
    	if(!event.isCancelled()) {
    		this.plugin.getLogger().debug("Load world : " + event.getTargetWorld().getName());
    		this.plugin.getService().getGroupSubjects().registerWorld(event.getTargetWorld().getName());
    		this.plugin.getManagerData().registerUser(event.getTargetWorld().getName());
    		
    		this.plugin.getService().getUserSubjects().reload();
    	}
    }
    
    /**
     * Déchargement du monde
     */
    @Listener
    public void onUnloadWorldEvent(final UnloadWorldEvent event) {
    	if(!event.isCancelled()) {
    		this.plugin.getLogger().debug("Unload world : " + event.getTargetWorld().getName());
    		this.plugin.getService().getGroupSubjects().removeWorld(event.getTargetWorld().getName());
    		this.plugin.getManagerData().removeUser(event.getTargetWorld().getName());
    		
    		this.plugin.getService().getUserSubjects().reload();
    	}
    }
}
