package fr.evercraft.everpermissions.service.permission;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;

import fr.evercraft.everpermissions.EverPermissions;

public class EContextCalculator implements ContextCalculator<Subject> {
	private final EverPermissions plugin;
	
    public EContextCalculator(final EverPermissions plugin) {
        this.plugin = plugin;
    }

    /**
     * Ajoute le monde
     */
    @Override
    public void accumulateContexts(final Subject subject, final Set<Context> accumulator) {
    	Optional<CommandSource> subjSource = subject.getCommandSource();
        if (subjSource.isPresent()) {
            CommandSource source = subjSource.get();
            if (source instanceof Locatable) {
                accumulator.add(new Context(Context.WORLD_KEY, ((Locatable) source).getWorld().getName()));
            }
        }
    }

    /**
     * Vérifie le monde
     */
    @Override
    public boolean matches(final Context context, final Subject subject) {
    	if(context.getType().equals(Context.WORLD_KEY)) {
	    	Optional<CommandSource> subjSource = subject.getCommandSource();
	        if (subjSource.isPresent()) {
	            CommandSource source = subjSource.get();
	            if (source instanceof Locatable) {
	            	return context.getName().equals(((Locatable) source).getWorld().getName());
	            }
	        }
    	}
        return false;
    }
    
    /**
     * Retourne le context selon le groupe
     * @param contexts Le context avec le monde
     * @return Le context avec le type du groupe
     */
    public Set<Context> getContextGroup(final Set<Context> contexts) {
		Set<Context> accumulator = new HashSet<Context>();
		for(Context context : contexts) {
			if(context.getType().equals(Context.WORLD_KEY)) {
				Optional<String> world = this.plugin.getManagerData().getTypeGroup(context.getName());
				if(world.isPresent()) {
					accumulator.add(new Context(Context.WORLD_KEY, world.get()));
				}
			}
		}
		return accumulator;
    }

    /**
     * Retourne le context selon le joueur
     * @param contexts Le context avec le monde
     * @return Le context avec le type du joueur
     */
    public Set<Context> getContextUser(final Set<Context> contexts) {
		Set<Context> accumulator = new HashSet<Context>();
		for(Context context : contexts) {
			if(context.getType().equals(Context.WORLD_KEY)) {
				Optional<String> world = this.plugin.getManagerData().getTypeUser(context.getName());
				if(world.isPresent()) {
					accumulator.add(new Context(Context.WORLD_KEY, world.get()));
				}
			}
		}
		return accumulator;
    }
	 
    /**
     * Créer un context à partir d'un monde
     * @param world Le monde
     * @return Le context
     */
	public static Set<Context> getContextWorld(final String world) {
	    Set<Context> contexts = new HashSet<Context>();
		contexts.add(new Context(Context.WORLD_KEY, world));
		return Collections.unmodifiableSet(contexts);
    }
	
	/**
	 * Retourne le nom du monde selon un context
	 * @param contexts Le context
	 * @return Le nom du monde
	 */
	public static Optional<String> getWorld(final Set<Context> contexts) {
		Optional<String> world = Optional.empty();
		Iterator<Context> iterator = contexts.iterator();
		Context context;
		while(iterator.hasNext() && !world.isPresent()) {
			context = iterator.next();
			if(context.getType().equals(Context.WORLD_KEY)) {
				world = Optional.ofNullable(context.getName());
			}
		}
		return world;
    }
}
