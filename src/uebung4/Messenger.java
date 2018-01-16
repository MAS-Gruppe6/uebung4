package uebung4;

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
	private Status status;
	private int numProposals;
	private int acceptedClient;

	public Messenger(NdPoint start, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		startingPoint = start;
		id = -1;
		acceptedClient = -1;
		status = Status.IDLE;
		numProposals = 0;
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

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		MessageCenter msgCenter = MessageCenter.singleton;

		FIPA_Message msg = msgCenter.getMessage(this.id);
		if (this.status == Status.PROPOSING) {
			if (msg != null && msg.getContent().equals("reject-proposal")) {
				if (--numProposals == 0) {
					this.status = Status.IDLE;
					return;
				}
			} else if (msg != null && msg.getContent().equals("accept-proposal")) {
				numProposals = 0;
				acceptedClient = msg.getSender();
				this.status = Status.WORKING;
				return;
			}
		}

		if (this.status == Status.WORKING) {
			if (msg != null && msg.getContent().equals("cfp")) {
				msgCenter.send(this.id, msg.getSender(), FIPA_Performative.INFORM, "refuse");
			}
			if (msg != null && msg.getContent().equals("accept-proposal")) {
				msgCenter.send(this.id, msg.getSender(), FIPA_Performative.INFORM, "failure");
			}
			work(msgCenter.getAgent(acceptedClient));
		}

		if (msg != null && msg.getContent().equals("cfp") && this.status != Status.WORKING) {
			msgCenter.send(this.id, msg.getSender(), FIPA_Performative.REQUEST, "propose");
			this.status = Status.PROPOSING;
			this.numProposals++;
		}

	}

	private void work(Agent client) {
		NdPoint myPoint = space.getLocation(this);
		NdPoint otherPoint = new NdPoint(client.getStartX(), client.getStartY());
		double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
		// Ein Feld pro Tick bewegen
		space.moveByVector(this, 1, angle, 0);
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());

		if (grid.getDistance(grid.getLocation(this), grid.getLocation(client)) <= 1) {
			this.status = Status.IDLE;
			acceptedClient = -1;
			MessageCenter.singleton.send(this.id, client.getId(), FIPA_Performative.INFORM, "inform-done");
		}
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
