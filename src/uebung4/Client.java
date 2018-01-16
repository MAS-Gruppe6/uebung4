package uebung4;

import java.util.ArrayList;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Client implements Agent {

	public enum Status {
		IDLE, REQUESTING, WAITING
	}

	private int id;
	private NdPoint startingPoint;

	private ContinuousSpace<Object> space;

	private Grid<Object> grid;
	private Status status;

	private ArrayList<Agent> messengerProposals;
	private int refused;

	public Client(NdPoint start, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		startingPoint = start;
		id = -1;
		status = Status.IDLE;
		messengerProposals = new ArrayList<>();
		refused = 0;
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
