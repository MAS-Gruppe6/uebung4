package uebung4;

import java.util.HashMap;
import java.util.Random;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Initiator implements Agent {

	private int id;

	private Client[] clients;
	private Messenger[] messengers;

	private HashMap<Messenger, Double> trustValues;

	ContinuousSpace<Object> space;
	Grid<Object> grid;

	private boolean isIdle;

	private int remainingDays;

	public Initiator(Client[] clients, Messenger[] messengers, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.id = -1;
		this.clients = clients;
		this.messengers = messengers;
		this.grid = grid;
		this.space = space;

		this.trustValues = new HashMap<>();
		trustValues.put(messengers[0], 0.89);
		trustValues.put(messengers[1], 0.74);
		trustValues.put(messengers[2], 0.94);
		trustValues.put(messengers[3], 0.71);

		isIdle = false;
		Parameters params = RunEnvironment.getInstance().getParameters();
		remainingDays = params.getInteger("numOfDays");
	}

	public Messenger getMessengerFromId(int id) {
		for (Messenger messenger : messengers) {
			if (id == messenger.getId()) {
				return messenger;
			}
		}
		return null;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		if (!isIdle) {
			HashMap<Client, Messenger> agentRelations = new HashMap<>();
			for (Client client : clients) {
				double bestDistance = 99999.0;
				Messenger bestAgent = null;
				for (Messenger messenger : messengers) {
					double dist = grid.getDistance(grid.getLocation(client), grid.getLocation(messenger));
					Parameters params = RunEnvironment.getInstance().getParameters();
					dist = dist * (1.0 - (trustValues.get(messenger) / params.getDouble("trustMod")));
					if (dist < bestDistance) {
						bestDistance = dist;
						bestAgent = messenger;
					}
				}
				agentRelations.put(client, bestAgent);
			}

			for (Client client : clients) {
				agentRelations.get(client).addTarget(client);
			}
			isIdle = true;
			for (Messenger messenger : messengers) {
				messenger.go(true);
			}
		} else {
			MessageCenter msgCenter = MessageCenter.singleton;

			FIPA_Message msg = msgCenter.getMessage(this.id);
			if (msg != null) {
				if (msg.getContent().equals("inform-done")) {
					Random rand = new Random();
					Messenger m = getMessengerFromId(msg.getSender());
					m.numOfDeliveries += 1;
					boolean success = rand.nextDouble() < trustValues.get(m);
					calcTrustValue(success, m);
				}
			}

			if (checkForNewDay() && !msgCenter.messagesAvailable(this.id)) {
				if (remainingDays-- > 0) {
					isIdle = false;
				}
			}
		}

	}

	public boolean checkForNewDay() {
		for (Messenger messenger : messengers) {
			if (messenger.canGo) {
				return false;
			}
		}
		return true;
	}

	public void calcTrustValue(boolean success, Messenger m) {
		int oldAllDelivieries = m.numOfDeliveries - 1;
		int successfullDeliveries = (int) (oldAllDelivieries * trustValues.get(m));
		if (success) {
			successfullDeliveries++;
		}

		double newTrustValue = (double) successfullDeliveries / (double) m.numOfDeliveries;
		trustValues.put(m, newTrustValue);

	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public int getStartX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStartY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NdPoint getStartingPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub

	}

}
