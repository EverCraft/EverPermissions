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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import fr.evercraft.everpermissions.service.EPermissionService;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EPermissionDescription implements PermissionDescription {
    private final EPermissionService service;
    private final String id;
    private final Text description;
    private final PluginContainer plugin;

    public EPermissionDescription(EPermissionService service, PluginContainer plugin, String id, Text description) {
    	this.service = checkNotNull(service, "service");
        this.id = checkNotNull(id, "id");
        this.description = checkNotNull(description, "description");
        this.plugin = checkNotNull(plugin, "owner");
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Text getDescription() {
        return this.description;
    }

    @Override
    public Map<Subject, Boolean> getAssignedSubjects(String type) {
        return this.service.getSubjects(type).getAllWithPermission(getId());
    }

    @Override
    public PluginContainer getOwner() {
        return this.plugin;
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EPermissionDescription other = (EPermissionDescription) obj;
        return this.id.equals(other.id) && this.plugin.equals(other.plugin) && this.description.equals(other.description);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("owner", this.plugin)
                .add("id", this.id)
                .toString();
    }

    public static class Builder implements PermissionDescription.Builder {
        private final PluginContainer plugin;
        private final EPermissionService service;
        
        private String id;
        private Text description;
        private final Map<String, Tristate> roleAssignments;

        public Builder(EPermissionService service, PluginContainer plugin) {
        	this.service = service;
            this.plugin = plugin;
            this.roleAssignments = new LinkedHashMap<>();
        }

        @Override
        public Builder id(String id) {
            this.id = checkNotNull(id, "id");
            return this;
        }

        @Override
        public Builder description(Text description) {
            this.description = checkNotNull(description, "description");
            return this;
        }

        @Override
        public Builder assign(String role, boolean value) {
            Preconditions.checkNotNull(role, "role");
            this.roleAssignments.put(role, Tristate.fromBoolean(value));
            return this;
        }

        @Override
        public EPermissionDescription register() throws IllegalStateException {
            checkState(this.id != null, "No id set");
            checkState(this.description != null, "No description set");
            EPermissionDescription description = new EPermissionDescription(this.service, this.plugin, this.id, this.description);
            this.service.registerDescription(description);

            // Set role-templates
            SubjectCollection subjects = this.service.getSubjects(PermissionService.SUBJECTS_ROLE_TEMPLATE);
            for (Entry<String, Tristate> assignment : this.roleAssignments.entrySet()) {
                Subject subject = subjects.get(assignment.getKey());
                subject.getTransientSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, this.id, assignment.getValue());
            }
            return description;
        }
    }
}
