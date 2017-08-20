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
package fr.evercraft.everpermissions.service.permission.subject;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;

import fr.evercraft.everpermissions.service.EPermissionService;

public class ESubjectReference implements SubjectReference {
	private final EPermissionService service;
	
	private final String collectionIdentifier;
	private final String subjectIdentifier;
	
	private long lastLookup = 0L;
	private WeakReference<Subject> cache = null;
	
	public ESubjectReference(EPermissionService service, String collectionIdentifier, String subjectIdentifier) {
		this.service = service;
		this.collectionIdentifier = collectionIdentifier; 
		this.subjectIdentifier = subjectIdentifier;
	}
	
	@Override
	public CompletableFuture<Subject> resolve() {
		long sinceLast = System.currentTimeMillis() - this.lastLookup;

		if (sinceLast >= TimeUnit.SECONDS.toMillis(15)) {
			if (this.cache != null) {
				Subject subject = this.cache.get();
				if (subject != null) {
					return CompletableFuture.completedFuture(subject);
				}
			}
		}
		
		return service.loadCollection(this.collectionIdentifier)
			.thenComposeAsync(collection -> collection.loadSubject(this.subjectIdentifier))
			.thenApply(subject -> {
				this.cache = new WeakReference<Subject>(subject);
				this.lastLookup = System.currentTimeMillis();
				return subject;
			});
	}

	@Override
	public String getCollectionIdentifier() {
		return this.collectionIdentifier;
	}

	@Override
	public String getSubjectIdentifier() {
		return this.subjectIdentifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SubjectReference)) return false;
		
		SubjectReference subject = (SubjectReference) obj;
		return this.collectionIdentifier.equalsIgnoreCase(subject.getCollectionIdentifier()) && 
				this.subjectIdentifier.equalsIgnoreCase(subject.getSubjectIdentifier());
	}

	@Override
	public String toString() {
		return "ESubjectReference [collectionIdentifier=" + this.collectionIdentifier + ", subjectIdentifier="
				+ this.subjectIdentifier + "]";
	}
}
