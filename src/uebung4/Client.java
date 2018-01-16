package uebung4;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

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

	@ScheduledMethod(start = 1, interval = 1)
	public void request() {
		MessageCenter msgCenter = MessageCenter.singleton;
		FIPA_Message msg = msgCenter.getMessage(this.id);
		switch (this.status) {
		case IDLE:
			msgCenter.requestPackage(this);
			status = Status.REQUESTING;
			messengerProposals.clear();
			refused = 0;
			break;
		case REQUESTING:
			if (msg != null && msg.getContent().equals("propose")) {
				messengerProposals.add(msgCenter.getAgent(msg.getSender()));
			}
			if (msg != null && msg.getContent().equals("refuse")) {
				refused++;
			}
			if (refused == msgCenter.numberOfMessengers) {
				this.status = Status.IDLE;
			} else if (messengerProposals.size() + refused == msgCenter.numberOfMessengers) {
				this.status = Status.WAITING;
				double bestDistance = 99999.0;
				Agent bestAgent = null;
				for (Agent agent : messengerProposals) {
					double dist = grid.getDistance(grid.getLocation(this), grid.getLocation(agent));
					if (dist < bestDistance) {
						bestDistance = dist;
						bestAgent = agent;
					}
				}

				msgCenter.send(this.id, bestAgent.getId(), FIPA_Performative.REQUEST, "accept-proposal");
				messengerProposals.remove(bestAgent);
				for (Agent agent : messengerProposals) {
					msgCenter.send(this.id, agent.getId(), FIPA_Performative.REQUEST, "reject-proposal");
				}
			}
			break;
		case WAITING:
			if (msg != null && msg.getContent().equals("failure")) {
				this.status = Status.IDLE;
			}
			if (msg != null && msg.getContent().equals("inform-done")) {
				Context<Object> context = ContextUtils.getContext(this);
				context.remove(this);

			}
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
