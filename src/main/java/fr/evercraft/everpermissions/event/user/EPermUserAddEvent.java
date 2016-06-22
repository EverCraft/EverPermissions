package fr.evercraft.everpermissions.event.user;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.plugin.EPlugin;

public class EPermUserAddEvent extends EPermUserEvent implements PermUserEvent.Add {

    public EPermUserAddEvent(final Subject subject, final Cause cause, final EPlugin plugin) {
        super(subject, cause, plugin, Action.USER_ADDED);
    }
}
