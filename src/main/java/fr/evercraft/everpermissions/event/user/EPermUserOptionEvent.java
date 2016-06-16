package fr.evercraft.everpermissions.event.user;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.plugin.EPlugin;

public class EPermUserOptionEvent extends EPermUserEvent implements PermUserEvent.Option {

    public EPermUserOptionEvent(final Subject subject, final Cause cause, final EPlugin plugin) {
        super(subject, cause, plugin, Action.USER_OPTION_CHANGED);
    }
}
