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
