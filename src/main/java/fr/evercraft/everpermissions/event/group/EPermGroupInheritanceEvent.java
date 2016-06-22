package fr.evercraft.everpermissions.event.group;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermGroupEvent;

public class EPermGroupInheritanceEvent extends EPermGroupEvent implements PermGroupEvent.Inheritance {

    public EPermGroupInheritanceEvent(final Subject subject, final Cause cause) {
    	super(subject, cause, Action.GROUP_INHERITANCE_CHANGED);
    }
}
