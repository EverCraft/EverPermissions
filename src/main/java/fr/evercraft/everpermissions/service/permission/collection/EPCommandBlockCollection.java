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
package fr.evercraft.everpermissions.service.permission.collection;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import fr.evercraft.everpermissions.EverPermissions;
import fr.evercraft.everpermissions.service.permission.subject.EPUserSubject;

public class EPCommandBlockCollection extends EPUserCollection {
	
	private EPUserSubject commandBlock;

	public EPCommandBlockCollection(final EverPermissions plugin, String collectionIdentifier) {
		super(plugin, collectionIdentifier);
	}
	
	@Override
	public boolean load() {
		this.commandBlock = super.load("@").join();
		return this.commandBlock != null;
	}
	
	@Override
	public Optional<EPUserSubject> get(String identifier) {
		return Optional.ofNullable(this.commandBlock);
	}
	
	@Override
	public CompletableFuture<EPUserSubject> load(String identifier) {
		return CompletableFuture.completedFuture(this.commandBlock);
	}
}
