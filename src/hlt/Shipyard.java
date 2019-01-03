package hlt;

public class Shipyard extends Entity implements DropPoint {
	public Shipyard(final PlayerId owner, final Position position) {
		super(owner, EntityId.NONE, position);
	}

	public Command spawn() {
		return Command.spawnShip();
	}
}
