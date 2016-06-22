package fr.evercraft.everpermissions.event.user;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import fr.evercraft.everapi.event.PermUserEvent;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.server.player.EPlayer;

public class EPermUserEvent implements PermUserEvent {

    private final Subject subject;
    private final Cause cause;
    private final Action action;
    
    private Optional<EPlayer> player;

    public EPermUserEvent(final Subject subject, final Cause cause, final EPlugin plugin, final Action action) {
        this.subject = subject;
        this.action = action;
        this.cause = cause;
        
        if(this.subject.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_USER)) {
    		try {
    			this.player = plugin.getEServer().getEPlayer(UUID.fromString(this.subject.getIdentifier()));
    		} catch(IllegalArgumentException e) {
    			this.player = Optional.empty();
    		}
    	} else {
    		this.player = Optional.empty();
    	}
    }

    public Subject getSubject() {
        return this.subject;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public Optional<EPlayer> getPlayer() {
    	return this.player;
    }

	@Override
	public Cause getCause() {
		return this.cause;
	}
}
