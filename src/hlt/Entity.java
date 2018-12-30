package hlt;

public class Entity {
	public final PlayerId owner;
	public final EntityId id;
	public final Position position;

	public Entity(final PlayerId owner, final EntityId id, final Position position) {
		this.owner = owner;
		this.id = id;
		this.position = position;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		return hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		return id.id;
	}
}
