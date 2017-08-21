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

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;

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
		this.plugin.getService().getUserSubjects().loadSubject(event.getProfile().getUniqueId().toString());
    }
	
	/**
	 * Supprime le joueur de la liste
	 */
    @Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Disconnect event) {
        this.plugin.getService().getUserSubjects().suggestUnload(event.getTargetEntity().getIdentifier());
    }
	
    /**
     * Chargement du monde
     */
    @Listener
    public void onLoadWorldEvent(final LoadWorldEvent event) {
    	if (!event.isCancelled()) {
    		this.plugin.getELogger().debug("Load world : " + event.getTargetWorld().getName());
    		this.plugin.getService().registerWorldType(event.getTargetWorld().getName());
    	}
    }
}
