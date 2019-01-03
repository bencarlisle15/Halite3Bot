
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
		for (Cluster c: clusters) {
			Log.log("Cluster: " + c.center);
		}
		HashMap<Ship, Cluster> jobs = new HashMap<Ship, Cluster>();

		game.ready("New New Bot");

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
						if (!cluster.hasEnoughWorker()) {
							cluster.addWorker(ship);
							jobs.put(ship, cluster);
							break;
						}
					}
				}
				if (jobs.get(ship) == null) {
					clusters = gameMap.updateClusters(me);
					for (Cluster cluster : clusters) {
						if (!cluster.hasEnoughWorker()) {
							cluster.addWorker(ship);
							jobs.put(ship, cluster);
							break;
						}
					}
					if (jobs.get(ship) == null) {
						continue;
					}
				}
				Position nextPosition;
				if (ship.halite > 900 || ship.isFull()) {
					nextPosition = gameMap.nearestDropPoint(me, ship.position);
				} else if (gameMap.at(ship.position).halite > GameMap.stopEating || gameMap.at(ship.position).halite * 0.1 > ship.halite || ship.halite - gameMap.at(ship.position).halite*0.1 <= 900 && ship.halite > 900) {
					nextPosition = ship.position;
				} else {
					nextPosition = jobs.get(ship).getNextPosition(ship, gameMap);
				}
				if (nextPosition == null) {
					jobs.get(ship).removeWorker(ship);
					jobs.remove(ship);
					nextPosition = gameMap.nearestDropPoint(me, ship.position);
					Direction nextDirection = gameMap.navigate(ship, nextPosition, nextPositions, me.ships.values(), me, game.players);
					Position adjacentPosition = gameMap.getPositionFromDirection(ship.position, nextDirection);
					nextPositions.put(ship, adjacentPosition);
					commandQueue.add(ship.move(nextDirection));
				} else if (ship.position.equals(jobs.get(ship).center) && jobs.get(ship).isDropPoint(ship) && (me.halite - gameMap.at(ship.position).halite) > 4000) {
					commandQueue.add(ship.makeDropoff());
					jobs.remove(ship);
				} else {
					Direction nextDirection = gameMap.navigate(ship, nextPosition, nextPositions, me.ships.values(), me, game.players);
					Position adjacentPosition = gameMap.getPositionFromDirection(ship.position, nextDirection);
					nextPositions.put(ship, adjacentPosition);
					commandQueue.add(ship.move(nextDirection));
				}
			}
			for (Command c : commandQueue) {
				Log.log(c.command);
			}
			if (game.turnNumber <= 200 && me.halite > 1000 && me.ships.size() < GameMap.numberOfClusters && !gameMap.at(me.shipyard).isOccupied()) {
//			if (me.ships.size() <= 1 && !gameMap.at(me.shipyard).isOccupied()) {
				commandQueue.add(me.shipyard.spawn());
			}

			game.endTurn(commandQueue);
		}
	}
}
