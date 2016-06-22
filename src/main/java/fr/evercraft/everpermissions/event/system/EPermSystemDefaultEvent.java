package fr.evercraft.everpermissions.event.system;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.event.PermSystemEvent;

public class EPermSystemDefaultEvent extends EPermSystemEvent implements PermSystemEvent.Default {

    public EPermSystemDefaultEvent(final Cause cause) {   	
        super(cause, Action.DEFAULT_GROUP_CHANGED);
    }
}
