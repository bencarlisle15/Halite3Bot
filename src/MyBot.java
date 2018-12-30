
// This Java API uses camelCase instead of the snake_case as documented in the API docs.
//	 Otherwise the names of methods are consistent.

import hlt.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MyBot {
	public static void main(final String[] args) {
		final long rngSeed;
		if (args.length > 1) {
			rngSeed = Integer.parseInt(args[1]);
		} else {
			rngSeed = System.nanoTime();
		}
//		final Random rng = new Random(rngSeed);
		Game game = new Game();
		// At this point "game" variable is populated with initial map data.
		// This is a good place to do computationally expensive start-up pre-processing.
		// As soon as you call "ready" function below, the 2 second per turn timer will
		// start.

		// THIS IS FOR METHOD 2
		SortedArrayList<Cluster> clusters = game.gameMap.updateClusters(game.me);
		HashMap<Ship, Cluster> jobs = new HashMap<Ship, Cluster>();

		game.ready("MyJavaBot");

		Log.log("Successfully created bot! My Player ID is " + game.myId + ". Bot rng seed is " + rngSeed + ".");
		for (;;) {
			game.updateFrame();
			final Player me = game.me;
			final GameMap gameMap = game.gameMap;
			final ArrayList<Command> commandQueue = new ArrayList<Command>();
			final HashMap<Ship, Position> nextPositions = new HashMap<Ship, Position>();
			for (Ship ship : me.ships.values()) {
				if (!jobs.containsKey(ship)) {
					for (Cluster cluster : clusters) {
						if (!cluster.hasWorker()) {
							cluster.setWorker(ship);
							jobs.put(ship, cluster);
							break;
						}
					}
				}
				if (jobs.get(ship) == null) {
					clusters = gameMap.updateClusters(me);
					for (Cluster cluster : clusters) {
						if (!cluster.hasWorker()) {
							cluster.setWorker(ship);
							jobs.put(ship, cluster);
							break;
						}
					}
					if (jobs.get(ship) == null) {
						continue;
					}
				}
				Log.log("Cluster at : " + jobs.get(ship).center);
				Position nextPosition;
				if (ship.halite > 900 || ship.isFull()) {
					nextPosition = me.shipyard.position;
				} else if (gameMap.at(ship.position).halite > GameMap.stopEating || gameMap.at(ship.position).halite * 0.1 > ship.halite || ship.halite - gameMap.at(ship.position).halite*0.1 <= 900 && ship.halite > 900) {
					nextPosition = ship.position;
				} else {
					nextPosition = jobs.get(ship).getNextPosition(ship, gameMap);
				}
				Log.log("Going to " + nextPosition);
				
				if (nextPosition == null) {
					jobs.remove(ship);
					nextPosition = me.shipyard.position;
				}
				Direction nextDirection = gameMap.navigate(ship, nextPosition, nextPositions, me.ships.values());
				Position adjacentPosition = gameMap.getPositionFromDirection(ship.position, nextDirection);
				nextPositions.put(ship, adjacentPosition);
				Log.log("Size: " + nextPositions.size());
				commandQueue.add(ship.move(nextDirection));
			}
			String ans = "END: ";
			for (Ship s: nextPositions.keySet()) ans += s.id.id + " at " + s.position + " going to " + nextPositions.get(s) + ",";
			Log.log(ans);
			for (Command c : commandQueue) {
				Log.log(c.command);
			}
//			if (game.turnNumber <= 200 && me.halite / 4 >= Constants.SHIP_COST && !gameMap.at(me.shipyard).isOccupied() || me.ships.size() == 0) {
			if (me.ships.size() <= 1) {
				commandQueue.add(me.shipyard.spawn());
			}

			game.endTurn(commandQueue);
		}
	}
}
