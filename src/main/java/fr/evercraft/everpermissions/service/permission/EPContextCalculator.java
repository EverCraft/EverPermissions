/*
 * This file is part of EverPermissions.
 *
 * EverPermissions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverPermissions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverPermissions.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.everpermissions.service.permission;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.world.Locatable;

import com.google.common.collect.ImmutableSet;

import fr.evercraft.everapi.services.permission.ESubjectCollection;
import fr.evercraft.everpermissions.EPConfig;
import fr.evercraft.everpermissions.EverPermissions;

public class EPContextCalculator implements ContextCalculator<Subject> {
	
	public static final Set<Context> EMPTY = ImmutableSet.of();
	private final EverPermissions plugin;
	
    public EPContextCalculator(final EverPermissions plugin) {
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
    	if (context.getType().equals(Context.WORLD_KEY)) {
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
    public String getGroup(final Set<Context> contexts) {
    	return this.get(PermissionService.SUBJECTS_GROUP, contexts);
    }

    /**
     * Retourne le context selon le joueur
     * @param contexts Le context avec le monde
     * @return Le context avec le type du joueur
     */
    public String getUser(final Set<Context> contexts) {
		return this.get(PermissionService.SUBJECTS_USER, contexts);
    }
    
    public String get(final String identifierCollection, final Set<Context> contexts) {
    	Optional<ESubjectCollection<?>> collection = this.plugin.getService().get(identifierCollection);
    	if (!collection.isPresent()) return EPConfig.DEFAULT;
    	
    	return collection.get().getTypeWorld(EPContextCalculator.getWorld(contexts).orElse(""))
    		.orElseGet(() -> collection.get().getTypeWorld(EPContextCalculator.getWorld(SubjectData.GLOBAL_CONTEXT).orElse("")).orElse(EPConfig.DEFAULT));
    }
	 
    /**
     * Créer un context à partir d'un monde
     * @param world Le monde
     * @return Le context
     */
	public static Set<Context> of(final String world) {
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
			if (context.getType().equals(Context.WORLD_KEY)) {
				world = Optional.ofNullable(context.getName());
			}
		}
		return world;
    }
}
