package hlt;

import java.util.ArrayList;

public class Cluster implements Comparable<Cluster> {
	public Position center;
	public Position dropPointPosition;
	public int distance;
	public Ship worker;
	public static CompareType compareType;
	public int totalHalite;
	public ArrayList<Position> positions;
	public int priority;
	
	public Cluster(Position center, Position dropPointPosition, GameMap gameMap) {
		this.center = center;
		this.dropPointPosition = dropPointPosition;
		worker = null;
		distance = gameMap.calculateDistance(dropPointPosition, center);
		totalHalite = getTotalHalite(gameMap);
		positions = getPositions(gameMap);
		priority = getPriority(gameMap);
	}
	
	private int getPriority(GameMap gameMap) {
		int normalizedDistance = (int) (1000 * (1 - distance * 1.0 / (gameMap.height + gameMap.width)));
		int normalizedTotal = (int) (totalHalite / 9.0);
		return normalizedDistance * 10 + 5*normalizedTotal; 
	}

	private int getTotalHalite(GameMap gameMap) {
		int total = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <=1; j++) {
				total += gameMap.at(gameMap.normalize(new Position(center.x - i, center.y - j))).halite;
			}
		}
		return total;
	}
	
	private ArrayList<Position> getPositions(GameMap gameMap) {
		ArrayList<Position> positions = new ArrayList<Position>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <=1; j++) {
				positions.add(gameMap.normalize(new Position(center.x - i, center.y - j)));
			}
		}
		return positions;
	}

	@Override
	public int compareTo(Cluster cluster) {
		switch (compareType) {
		case SIZE:
			return cluster.totalHalite - totalHalite;
		case DISTANCE:
			return distance - cluster.distance;
		case PRIORITY:
			return cluster.priority - priority;
		default:
			return 0;
		}
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
		if (gameMap.at(center).halite >= GameMap.stopEating) {
			return center;
		}
		for (int i = -1; i <= 1; i++) {
			position = gameMap.normalize(new Position(center.x - 1, center.y + i));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		for (int i = -1; i <= 1; i++) {
			position = gameMap.normalize(new Position(center.x + i, center.y + 1));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		for (int i = 1; i >= -1; i--) {
			position = gameMap.normalize(new Position(center.x + 1, center.y + i));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		for (int i = 1; i >= -1; i--) {
			position = gameMap.normalize(new Position(center.x + i, center.y - 1));
			if (gameMap.at(position).halite >= GameMap.stopEating) {
				return position;
			}
		}
		return null;
	}
}
