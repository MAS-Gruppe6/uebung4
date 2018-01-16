package uebung4;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Messenger implements Agent {

	public enum Status {
		IDLE, PROPOSING, WORKING
	}

	private ContinuousSpace<Object> space;

	private Grid<Object> grid;

	private NdPoint startingPoint;
	private int id;

	private ArrayList<Client> targets;
	private Client currentTarget;

	public boolean canGo;

	public int numOfDeliveries;

	public Messenger(NdPoint start, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		startingPoint = start;
		id = -1;
		targets = new ArrayList<>();
		canGo = false;
		currentTarget = null;
		numOfDeliveries = 100;
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

	public void go(boolean val) {
		canGo = val;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		MessageCenter msgCenter = MessageCenter.singleton;

		if (canGo) {
			if (currentTarget == null) {
				if (!targets.isEmpty()) {
					currentTarget = targets.remove(0);
				}
			}
			work(currentTarget);
		}
	}

	private void work(Agent client) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint;
		if (client == null) {
			otherPoint = new NdPoint(this.getStartX(), this.getStartY());
		} else {
			otherPoint = new NdPoint(client.getStartX(), client.getStartY());
		}
		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		// Ein Feld pro Tick bewegen
		space.moveByVector(this, 1, angle, 0);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());

		if (space.getDistance(space.getLocation(this), otherPoint) <= 1) {
			if (client != null) {
				MessageCenter.singleton.send(this.id, MessageCenter.singleton.initiator.getId(),
						FIPA_Performative.INFORM, "inform-done");
				currentTarget = null;
			} else {
				canGo = false;
			}

		}
	}

	public void addTarget(Client t) {
		targets.add(t);
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
