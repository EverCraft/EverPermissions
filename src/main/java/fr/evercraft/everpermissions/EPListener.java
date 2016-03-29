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
