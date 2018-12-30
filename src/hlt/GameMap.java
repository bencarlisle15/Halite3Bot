package hlt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GameMap {
	public final int width;
	public final int height;
	public final MapCell[][] cells;
	public final static int stopEating = 30;

	public GameMap(final int width, final int height) {
		this.width = width;
		this.height = height;

		cells = new MapCell[height][];
		for (int y = 0; y < height; ++y) {
			cells[y] = new MapCell[width];
		}
	}

	public MapCell at(final Position position) {
		final Position normalized = normalize(position);
		return cells[normalized.y][normalized.x];
	}

	public MapCell at(final Entity entity) {
		return at(entity.position);
	}

	public int calculateDistance(final Position source, final Position target) {
		final Position normalizedSource = normalize(source);
		final Position normalizedTarget = normalize(target);

		final int dx = Math.abs(normalizedSource.x - normalizedTarget.x);
		final int dy = Math.abs(normalizedSource.y - normalizedTarget.y);

		final int toroidal_dx = Math.min(dx, width - dx);
		final int toroidal_dy = Math.min(dy, height - dy);

		return toroidal_dx + toroidal_dy;
	}

	public Position normalize(final Position position) {
		final int x = ((position.x % width) + width) % width;
		final int y = ((position.y % height) + height) % height;
		return new Position(x, y);
	}

	public ArrayList<Direction> getUnsafeMoves(final Position source, final Position destination) {
		final ArrayList<Direction> possibleMoves = new ArrayList<>();

		final Position normalizedSource = normalize(source);
		final Position normalizedDestination = normalize(destination);

		final int dx = Math.abs(normalizedSource.x - normalizedDestination.x);
		final int dy = Math.abs(normalizedSource.y - normalizedDestination.y);
		final int wrapped_dx = width - dx;
		final int wrapped_dy = height - dy;

		if (normalizedSource.x < normalizedDestination.x) {
			possibleMoves.add(dx > wrapped_dx ? Direction.WEST : Direction.EAST);
		} else if (normalizedSource.x > normalizedDestination.x) {
			possibleMoves.add(dx < wrapped_dx ? Direction.WEST : Direction.EAST);
		}

		if (normalizedSource.y < normalizedDestination.y) {
			possibleMoves.add(dy > wrapped_dy ? Direction.NORTH : Direction.SOUTH);
		} else if (normalizedSource.y > normalizedDestination.y) {
			possibleMoves.add(dy < wrapped_dy ? Direction.NORTH : Direction.SOUTH);
		}

		return possibleMoves;
	}

	public Direction naiveNavigate(final Ship ship, final Position destination) {
		// getUnsafeMoves normalizes for us
		for (final Direction direction : getUnsafeMoves(ship.position, destination)) {
			final Position targetPos = ship.position.directionalOffset(direction);
			if (!at(targetPos).isOccupied()) {
				at(targetPos).markUnsafe(ship);
				return direction;
			}
		}

		return Direction.STILL;
	}
	public Direction navigate(final Ship ship, final Position destination, HashMap<Ship, Position> nextPositions, Collection<Ship> collection) {
		Direction direction;
		if (ship.position.x == destination.x) {
			if (isSouthOf(ship, destination)) {
				direction = Direction.NORTH;
			} else if (ship.position.y == destination.y) {
				direction = Direction.STILL;
			} else {
				direction = Direction.SOUTH;
			}
		} else if (ship.position.x > destination.x && ship.position.x - destination.x < (this.width + 1 + destination.x - ship.position.x) || ship.position.x < destination.x && destination.x - ship.position.x > (this.width + 1 + ship.position.x - destination.x)) {
			direction = Direction.WEST;
		} else {
			direction = Direction.EAST;
		}
		Position nextPosition = getPositionFromDirection(ship.position, direction);
		if (isFutureCrash(ship, nextPositions, nextPosition, collection)) {
			Log.log("ISCRASH");
			if (direction == Direction.STILL) {
				direction = findSafeDirection(ship, nextPositions, collection);
			} else if (direction == Direction.EAST || direction == Direction.WEST) {
				if (isSouthOf(ship, destination)) {
					direction = Direction.NORTH;
				} else {
					direction = Direction.SOUTH;
				}
				if (isFutureCrash(ship, nextPositions, getPositionFromDirection(ship.position, direction), collection)) {
					direction = findSafeDirection(ship, nextPositions, collection);
				}
			} else {
				if (!isFutureCrash(ship, nextPositions, getPositionFromDirection(ship.position, Direction.WEST), collection)) {
					direction = Direction.WEST;
				} else if (!isFutureCrash(ship, nextPositions, getPositionFromDirection(ship.position, Direction.EAST), collection)) {
					direction = Direction.EAST;
				} else {
					direction = findSafeDirection(ship, nextPositions, collection);
				}
			}
		}
		return direction;
	}
	
	private boolean isSouthOf(Ship ship, Position destination) {
		return ship.position.y > destination.y && ship.position.y - destination.y < (this.height + 1 + destination.y - ship.position.y) || ship.position.y < destination.y && destination.y - ship.position.y > (this.height + 1 + ship.position.y - destination.y);
	}
	
	private Direction findSafeDirection(Ship ship, HashMap<Ship, Position> nextPositions, Collection<Ship> allShips) {
		for (Direction direction : Direction.ALL_CARDINALS) {
			if (direction != Direction.STILL && !isFutureCrash(ship, nextPositions, getPositionFromDirection(ship.position, direction), allShips)) {
				return direction;
			}
		}
		return Direction.STILL;
	}
	
	private boolean isFutureCrash(Ship ship, HashMap<Ship, Position> nextPositions, Position nextPosition, Collection<Ship> allShips) {
		for (Ship s: allShips) {
			if (!ship.equals(s) && s.position.equals(nextPosition)) {
				return true;
			}
		}
		return nextPositions.values().contains(nextPosition);
	}

	void _update() {
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				cells[y][x].ship = null;
			}
		}

		final int updateCount = Input.readInput().getInt();

		for (int i = 0; i < updateCount; ++i) {
			final Input input = Input.readInput();
			final int x = input.getInt();
			final int y = input.getInt();

			cells[y][x].halite = input.getInt();
		}
	}

	static GameMap _generate() {
		final Input mapInput = Input.readInput();
		final int width = mapInput.getInt();
		final int height = mapInput.getInt();

		final GameMap map = new GameMap(width, height);

		for (int y = 0; y < height; ++y) {
			final Input rowInput = Input.readInput();

			for (int x = 0; x < width; ++x) {
				final int halite = rowInput.getInt();
				map.cells[y][x] = new MapCell(new Position(x, y), halite);
			}
		}

		return map;
	}

	public SortedArrayList<Cluster> updateClusters(Player me) {
		SortedArrayList<MapCell> cells = new SortedArrayList<MapCell>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells.insertSorted(at(new Position(i, j)));
			}
		}
		SortedArrayList<Cluster> clusters = new SortedArrayList<Cluster>();
		for (int i = 0; i < 10; i++) {
			Position position = cells.get(i).position;
			clusters.insertSorted(new Cluster(position, me.shipyard.position, this));
		}
		return clusters;
	}

	public Position getPositionFromDirection(Position position, Direction direction) {
		switch (direction) {
		case EAST:
			return this.normalize(new Position(position.x + 1, position.y));
		case NORTH:
			return this.normalize(new Position(position.x, position.y - 1));
		case SOUTH:
			return this.normalize(new Position(position.x, position.y + 1));
		case STILL:
			return this.normalize(position);
		case WEST:
			return this.normalize(new Position(position.x - 1, position.y));
		default:
			return this.normalize(position);

		}
	}
}
