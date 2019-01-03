package hlt;

public class ShipyardCluster extends Cluster {

	public ShipyardCluster(Position center, Position shipyardPosition, GameMap gameMap) {
		super(center, shipyardPosition, gameMap);
		
	}
	
	public Position getNextPosition(Ship ship, GameMap gameMap) {
		return center;
	}
	
	@Override
	public int compareTo(Cluster cluster) {
		return Integer.MIN_VALUE;
	}
}
