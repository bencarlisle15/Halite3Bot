package hlt;

public class PriorityMapCell implements Comparable<PriorityMapCell> {
	public int row;
	public int column;
	public int priority;
	public Ship nearestShip;

	public PriorityMapCell(int row, int column, int priority, Ship nearestShip) {
		this.row = row;
		this.column = column;
		this.priority = priority;
		this.nearestShip = nearestShip;
	}

	@Override
	public int compareTo(PriorityMapCell cell) {
		return cell.priority - priority;
	}

	public String toString() {
		return "Position: " + row + "," + column + " : " + priority + " : " + nearestShip.id;
	}
}
