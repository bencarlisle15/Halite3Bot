package hlt;

import java.util.ArrayList;

public class Cluster implements Comparable<Cluster> {
	public Position center;
	public int dropPointDistance;
	public Ship[] workers;
	public int totalHalite;
	public ArrayList<Position> positions;
	public int priority;
	public static final int RADIUS = 3;
	
	public Cluster(Position center, Position dropPointPosition, GameMap gameMap) {
		this.center = center;
		workers = new Ship[RADIUS + 1];
		totalHalite = getTotalHaliteAndAddPositions(gameMap);
		priority = getPriority(gameMap);
		dropPointDistance = gameMap.calculateDistance(dropPointPosition, center);
		if (dropPointPosition.equals(center)) {
			workers[0] = PlaceboShip.ship;
			priority = Integer.MAX_VALUE;
		}
	}
	
	private static final class PlaceboShip extends Ship {
		
		public static final Ship ship = new PlaceboShip();
		
		private PlaceboShip() {
			super(new PlayerId(-1), new EntityId(-1), new Position(-1, -1), -1);
		}
		
		public boolean equals(Object o) {
			return false;
		}
		
		public int hashCode() {
			return -1;
		}

	}
	
	//TODO
	private int getPriority(GameMap gameMap) {
		int normalizedDistance = (int) (1000 * (1 - dropPointDistance * 1.0 / (gameMap.height + gameMap.width)));
		int normalizedTotal = (int) (totalHalite / 9.0);
		return normalizedDistance * 10 + 4*normalizedTotal;
	}

	private int getTotalHaliteAndAddPositions(GameMap gameMap) {
		int total = 0;
		positions = new ArrayList<Position>();
		for (int i = -RADIUS; i <= RADIUS; i++) {
			for (int j = -RADIUS; j <= RADIUS; j++) {
				total += gameMap.at(gameMap.normalize(new Position(center.x - i, center.y - j))).halite;
				positions.add(gameMap.normalize(new Position(center.x - i, center.y - j)));
			}
		}
		return total;
	}

	@Override
	public int compareTo(Cluster cluster) {
		return cluster.priority - priority;
	}

	public int hashCode() {
		return center.hashCode();
	}

	private int getDistance(Ship ship) {
		for (int i = 0; i < workers.length; i++) {
			if (workers[i] != null && workers[i].equals(ship)) {
				return i;
			}
		}
		 return -1;
	}
	
	public void removeWorker(Ship ship) {
		for (int i = 0; i < workers.length; i++) {
			if (workers[i] != null && workers[i].equals(ship)) {
				workers[i] = PlaceboShip.ship;
			}
		}
	}
	
	public Position getNextPosition(Ship ship, GameMap gameMap) {
		Position position;
		int distance = getDistance(ship);
		Log.log("SHIP: " + ship + "; DISTANCE:" + distance + " ; CLUSTER: " + center);
		if (distance == -1) {
			return null;
		}
		for (int i = -distance; i <= distance; i++) {
			position = gameMap.normalize(new Position(center.x - distance, center.y + i));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		for (int i = -distance; i <= distance; i++) {
			position = gameMap.normalize(new Position(center.x + i, center.y + distance));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		for (int i = distance; i >= -distance; i--) {
			position = gameMap.normalize(new Position(center.x + distance, center.y + i));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		for (int i = distance; i >= -distance; i--) {
			position = gameMap.normalize(new Position(center.x + i, center.y - distance));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		return null;
	}

	public boolean hasEnoughWorker() {
		for (int i = 0; i < workers.length; i++) {
			Log.log("CLUSTER POSITION: " + center + " I " + i + " is " + workers[i]);
			if (workers[i] == null) {
				return false;
			}
		}
		return true;
	}

	public boolean hasDropPoint() {
		return workers[0] != null;
	}

	public void addWorker(Ship ship) {
		for (int i = 0; i < workers.length; i++) {
			if (workers[i] == null) {
				workers[i] = ship;
				Log.log("ADDING TO " + i + "CLUSTER " + center);
				return;
			}
		}
	}

	public boolean isDropPoint(Ship ship) {
		return workers[0] != null && workers[0].equals(ship);
	}
}
