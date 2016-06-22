package fr.evercraft.everpermissions.service.permission.subject;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.collection.ESubjectCollection;
import fr.evercraft.everpermissions.service.permission.data.other.EOtherData;
import fr.evercraft.everpermissions.service.permission.data.other.ETransientOtherData;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class EOtherSubject extends ESubject {
	private final EOtherData data;
	private final ETransientOtherData transientData;
	
    public EOtherSubject(final EverPermissions plugin, final String identifier, final ESubjectCollection collection) {
    	super(plugin, identifier, collection);
    	
    	this.data = new EOtherData(this.plugin, this);
        this.transientData = new ETransientOtherData(this.plugin, this);
    }
    
    /*
     * Accesseurs
     */
    
    @Override
	public EOtherData getSubjectData() {
		return this.data;
	}

	@Override
	public MemorySubjectData getTransientSubjectData() {
		return this.transientData;
	}

    @Override
    public Optional<CommandSource> getCommandSource() {
    	if(this.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_SYSTEM)) {
    		if (this.getIdentifier().equals("Server")) {
                return Optional.of(this.plugin.getGame().getServer().getConsole());
            } else if (this.getIdentifier().equals("RCON")) {
                // TODO: Implement RCON API?
            }
    	} else if(this.getContainingCollection().getIdentifier().equals(PermissionService.SUBJECTS_COMMAND_BLOCK)) {
    		// TODO: Implement CommandBlock API?
    	}
        return Optional.empty();
    }
   
    @Override
    public Tristate getPermissionValue(final Set<Context> contexts, final String permission) {
    	// TempoData : Permissions
    	Tristate value = this.getTransientSubjectData().getNodeTree(contexts).get(permission);
		if(!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// TempoData : Groupes
    	Iterator<Subject> subjects = this.getTransientSubjectData().getParents(contexts).iterator();
    	while(subjects.hasNext()) {
    		value = subjects.next().getPermissionValue(contexts, permission);
    		if(!value.equals(Tristate.UNDEFINED)) {
    			return value;
    		}
    	}
    	
    	// MemoryData : Permissions
		value = this.getSubjectData().getNodeTree(contexts).getTristate(permission);
		if(!value.equals(Tristate.UNDEFINED)) {
			return value;
		}
    	
		// MemoryData : Groupes
    	subjects = this.getSubjectData().getParents(contexts).iterator();
    	while(subjects.hasNext()) {
    		Tristate tristate = subjects.next().getPermissionValue(contexts, permission);
    		if(!tristate.equals(Tristate.UNDEFINED)) {
    			return tristate;
    		}
    	}
        return Tristate.UNDEFINED;
    }
	
	public void reload() {
		this.data.reload();
    	
		this.transientData.clearPermissions();
		this.transientData.clearOptions();
		this.transientData.clearParents();
    }
}