package hlt;

import java.util.ArrayList;

public class Position {
	public final int x;
	public final int y;

	public Position(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public ArrayList<Position> getSurroundingCardinals() {
		final ArrayList<Position> suroundingCardinals = new ArrayList<>();

		for (final Direction d : Direction.ALL_CARDINALS) {
			suroundingCardinals.add(directionalOffset(d));
		}

		return suroundingCardinals;
	}

	public Position directionalOffset(final Direction d) {
		final int dx;
		final int dy;

		switch (d) {
		case NORTH:
			dx = 0;
			dy = -1;
			break;
		case SOUTH:
			dx = 0;
			dy = 1;
			break;
		case EAST:
			dx = 1;
			dy = 0;
			break;
		case WEST:
			dx = -1;
			dy = 0;
			break;
		case STILL:
			dx = 0;
			dy = 0;
			break;
		default:
			throw new IllegalStateException("Unknown direction " + d);
		}

		return new Position(x + dx, y + dy);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Position position = (Position) o;
		return x == position.x && y == position.y;
	}

	@Override
	public int hashCode() {
		String yStr = (y % 10 == y ? "0" : "") + String.valueOf(y);
		return Integer.parseInt(String.valueOf(x) + yStr);
	}
	
	public String toString() {
		return String.valueOf(x) + ":" + String.valueOf(y);
	}
}
