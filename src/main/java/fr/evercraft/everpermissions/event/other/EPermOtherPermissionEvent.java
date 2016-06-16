package fr.evercraft.everpermissions.event.other;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermOtherEvent;

public class EPermOtherPermissionEvent extends EPermOtherEvent implements PermOtherEvent.Permission {

    public EPermOtherPermissionEvent(final Subject subject, final Cause cause) {
    	super(subject, cause, Action.OTHER_PERMISSION_CHANGED);
    }
}
