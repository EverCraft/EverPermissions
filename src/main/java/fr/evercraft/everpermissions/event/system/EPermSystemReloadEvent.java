package fr.evercraft.everpermissions.event.system;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.event.PermSystemEvent;

public class EPermSystemReloadEvent extends EPermSystemEvent implements PermSystemEvent.Reload {

    public EPermSystemReloadEvent(final Cause cause) {   	
        super(cause, Action.RELOADED);
    }
}
