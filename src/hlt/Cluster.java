package hlt;

public class Cluster implements Comparable<Cluster> {
	public Position center;
	public Position shipyardPosition;
	public int distance;
	public Ship worker;

	public Cluster(Position center, Position shipyardPosition, GameMap gameMap) {
		this.center = center;
		this.shipyardPosition = shipyardPosition;
		worker = null;
		this.distance = gameMap.calculateDistance(shipyardPosition, center);
	}

	public int getTotalHalite(GameMap gameMap) {
		int total = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <=1; j++) {
				total += gameMap.at(gameMap.normalize(new Position(center.x-i, center.y-j))).halite;
			}
		}
		return total;
	}

	@Override
	public int compareTo(Cluster cluster) {
		return distance - cluster.distance;
	}

	public int hashCode() {
		return center.hashCode();
	}
	
	public boolean hasWorker() {
		return worker != null;
	}

	public void setWorker(Ship ship) {
		worker = ship;
	}
	
	public Position getNextPosition(Ship ship, GameMap gameMap) {
		Position position;
		Log.log("Looking for next position");
		if (gameMap.at(center).halite >= GameMap.stopEating) {
			return center;
		}
		Log.log("Done with center");
		for (int i = -1; i <= 1; i++) {
			position = gameMap.normalize(new Position(center.x - 1, center.y + i));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		Log.log("Done with west edge");
		for (int i = -1; i <= 1; i++) {
			position = gameMap.normalize(new Position(center.x + i, center.y + 1));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		Log.log("Done with south edge");
		for (int i = 1; i >= -1; i--) {
			position = gameMap.normalize(new Position(center.x + 1, center.y + i));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		Log.log("Done with east edge");
		for (int i = 1; i >= -1; i--) {
			position = gameMap.normalize(new Position(center.x + i, center.y - 1));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		Log.log("Done with north edge");
		return null;
	}
}
