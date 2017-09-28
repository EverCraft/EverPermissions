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
package fr.evercraft.everpermissions.service.permission.data;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.util.Tristate;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

public class EPNode extends HashMap<String, EPNode> {
	private static final long serialVersionUID = -3369286570178770580L;
	private static final Splitter NODE_SPLITTER = Splitter.on('.');
	
	private Tristate value;
	
	/*
	 * Constructor
	 */

    public EPNode() {
        super();
        this.value = Tristate.UNDEFINED;
    }
    
    private EPNode(final Tristate value) {
    	super();
        this.value = value;
    }
    
    private EPNode(final Map<String, EPNode> children, final Tristate value) {
    	super();
    	this.putAll(children);
        this.value = value;
    }
    
    /*
     * Accesseur
     */
    
    private void setTristate(final Tristate value) {
		this.value = value;
	}
    
    private Tristate getTristate() {
		return this.value;
	}
    
    /**
     * Clone
     * @return Nouveau node
     */
    public EPNode copy() {
    	return new EPNode(this, this.value);
    }

    /**
     * Retourne la valeur de la permission
     * @param permission La permission
     * @return La valeur de la permission
     */
    public Tristate getTristate(final String permission) {
        Iterable<String> parts = NODE_SPLITTER.split(permission.toLowerCase());
        EPNode currentNode = this;
        Tristate lastUndefinedVal = Tristate.UNDEFINED;
        for (String str : parts) {
            if (!currentNode.containsKey(str)) {
                break;
            }
            currentNode = currentNode.get(str);
            
            if (currentNode.getTristate() != Tristate.UNDEFINED) {
                lastUndefinedVal = currentNode.getTristate();
            }
        }
        return lastUndefinedVal;

    }

    /**
     * Retourne une Map avec les permissions et leur valeur
     * @return Permission : Valeur
     */
    public Map<String, Boolean> asMap() {
        ImmutableMap.Builder<String, Boolean> ret = ImmutableMap.builder();
        for (Map.Entry<String, EPNode> ent : this.entrySet()) {
            populateMap(ret, ent.getKey(), ent.getValue());
        }
        return ret.build();
    }

    /**
     * Fonction pour créer une Map
     * @param values La map
     * @param prefix Le prefix
     * @param currentNode Le node
     */
    private void populateMap(final ImmutableMap.Builder<String, Boolean> values, final String prefix, final EPNode currentNode) {
        if (currentNode.getTristate() != Tristate.UNDEFINED) {
            values.put(prefix, currentNode.value.asBoolean());
        }
        for (Map.Entry<String, EPNode> ent : currentNode.entrySet()) {
            populateMap(values, prefix + '.' + ent.getKey(), ent.getValue());
        }
    }
    
    /*
     * Fonctions
     */
    
    /**
     * Création d'un node à partir d'une map
     * @param values Permission : Valeur
     * @return Le node
     */
    public static EPNode of(final Map<String, Boolean> values) {
        return of(values, Tristate.UNDEFINED);
    }

    /**
     * Création d'un node à partir d'une map
     * @param values Permission : Valeur
     * @param defaultValue La valeur par défaut
     * @return Le node
     */
    public static EPNode of(final Map<String, Boolean> values, final Tristate defaultValue) {
    	EPNode newTree = new EPNode(defaultValue);
        for (Map.Entry<String, Boolean> value : values.entrySet()) {
            Iterable<String> parts = NODE_SPLITTER.split(value.getKey().toLowerCase());
            EPNode currentNode = newTree;
            for (String part : parts) {
                if (currentNode.containsKey(part)) {
                    currentNode = currentNode.get(part);
                } else {
                	EPNode newNode = new EPNode();
                    currentNode.put(part, newNode);
                    currentNode = newNode;
                }
            }
            currentNode.setTristate(Tristate.fromBoolean(value.getValue()));
        }
        return newTree;
    }
    
    /**
     * Modifie une permission
     * @param node Le node
     * @param value La valeur
     * @return Le nouveau node
     */
    public EPNode withValue(final String node, final Tristate value) {
        Iterable<String> parts = NODE_SPLITTER.split(node.toLowerCase());
        EPNode newRoot = this.copy();
        EPNode currentPtr = newRoot;

        for (String part : parts) {
        	EPNode oldChild = currentPtr.get(part);
        	EPNode newChild;
            if (oldChild == null) {
            	newChild = new EPNode();
            } else {
            	newChild = oldChild.copy();
            }
            currentPtr.put(part, newChild);
            currentPtr = newChild;
        }
        currentPtr.setTristate(value);
        return newRoot;
    }

    /**
     * Modifie des permissions
     * @param values Une map de permission
     * @return Le nouveau node
     */
    public EPNode withAll(final Map<String, Tristate> values) {
    	EPNode node = this;
        for (Map.Entry<String, Tristate> ent : values.entrySet()) {
        	node = node.withValue(ent.getKey(), ent.getValue());
        }
        return node;
    }
}
