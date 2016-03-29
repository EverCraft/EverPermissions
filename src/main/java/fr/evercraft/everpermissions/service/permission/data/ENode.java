/**
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
import java.util.regex.Pattern;

import org.spongepowered.api.util.Tristate;

import com.google.common.collect.ImmutableMap;

public class ENode extends HashMap<String, ENode> {
	private static final long serialVersionUID = -3369286570178770580L;
	private static final Pattern SPLIT_REGEX = Pattern.compile("\\.");
	
	private Tristate value;
	
	/*
	 * Constructor
	 */

    public ENode() {
        super();
        this.value = Tristate.UNDEFINED;
    }
    
    private ENode(final Tristate value) {
    	super();
        this.value = value;
    }
    
    private ENode(final Map<String, ENode> children, final Tristate value) {
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
    public ENode copy() {
    	return new ENode(this, this.value);
    }

    /**
     * Retourne la valeur de la permission
     * @param permission La permission
     * @return La valeur de la permission
     */
    public Tristate getTristate(final String permission) {
        String[] parts = SPLIT_REGEX.split(permission.toLowerCase());
        ENode currentNode = this;
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
        for (Map.Entry<String, ENode> ent : this.entrySet()) {
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
    private void populateMap(final ImmutableMap.Builder<String, Boolean> values, final String prefix, final ENode currentNode) {
        if (currentNode.getTristate() != Tristate.UNDEFINED) {
            values.put(prefix, currentNode.value.asBoolean());
        }
        for (Map.Entry<String, ENode> ent : currentNode.entrySet()) {
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
    public static ENode of(final Map<String, Boolean> values) {
        return of(values, Tristate.UNDEFINED);
    }

    /**
     * Création d'un node à partir d'une map
     * @param values Permission : Valeur
     * @param defaultValue La valeur par défaut
     * @return Le node
     */
    public static ENode of(final Map<String, Boolean> values, final Tristate defaultValue) {
    	ENode newTree = new ENode(defaultValue);
        for (Map.Entry<String, Boolean> value : values.entrySet()) {
            String[] parts = SPLIT_REGEX.split(value.getKey().toLowerCase());
            ENode currentNode = newTree;
            for (String part : parts) {
                if (currentNode.containsKey(part)) {
                    currentNode = currentNode.get(part);
                } else {
                	ENode newNode = new ENode();
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
    public ENode withValue(final String node, final Tristate value) {
        String[] parts = SPLIT_REGEX.split(node.toLowerCase());
        ENode newRoot = this.copy();
        ENode currentPtr = newRoot;

        for (String part : parts) {
        	ENode oldChild = currentPtr.get(part);
        	ENode newChild;
            if(oldChild == null) {
            	newChild = new ENode();
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
    public ENode withAll(final Map<String, Tristate> values) {
    	ENode node = this;
        for (Map.Entry<String, Tristate> ent : values.entrySet()) {
        	node = node.withValue(ent.getKey(), ent.getValue());
        }
        return node;
    }
}
