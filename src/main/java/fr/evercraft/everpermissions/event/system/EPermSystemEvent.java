package fr.evercraft.everpermissions.event.system;

import org.spongepowered.api.event.cause.Cause;

import fr.evercraft.everapi.event.PermSystemEvent;

public class EPermSystemEvent implements PermSystemEvent {

    private final Action action;
    private final Cause cause;

    public EPermSystemEvent(final Cause cause, final Action action) {   	
        this.action = action;
        this.cause = cause;
    }
    
    @Override
    public Action getAction() {
        return this.action;
    }

	@Override
	public Cause getCause() {
		return this.cause;
	}
}
