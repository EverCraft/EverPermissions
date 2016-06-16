package fr.evercraft.everpermissions.event.group;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermGroupEvent;

public class EPermGroupEvent implements PermGroupEvent {

    private final Subject subject;
    private final Cause cause;
    private final Action action;

    public EPermGroupEvent(final Subject subject, final Cause cause, final Action action) {    	
        this.subject = subject;
        this.cause = cause;
        this.action = action;
    }

    @Override
    public Subject getSubject() {
        return this.subject;
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
