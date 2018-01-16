package uebung4;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Client implements Agent {

	private int id;
	private NdPoint startingPoint;

	private ContinuousSpace<Object> space;

	private Grid<Object> grid;

	public Client(NdPoint start, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		startingPoint = start;
		id = -1;
	}

	@Override
	public int getStartX() {
		return (int) startingPoint.getX();
	}

	@Override
	public int getStartY() {
		return (int) startingPoint.getY();
	}

	@Override
	public NdPoint getStartingPoint() {
		return startingPoint;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
}
