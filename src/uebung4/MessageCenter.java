package uebung4;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageCenter {

	ArrayList<FIPA_Message> messageList;
	HashMap<Integer, Agent> agents;

	public int numberOfAgents;
	public int numberOfMessengers;
	public int numberOfClients;

	public static final MessageCenter singleton = new MessageCenter();
	// Das MessageCenter dient als Plattform zum Nachrichtenaustausch.

	public MessageCenter() {
		this.messageList = new ArrayList<FIPA_Message>();
		this.agents = new HashMap<>();
		this.numberOfAgents = 0;
	}

	public void requestPackage(Client c) {
		for (int i = 0; i < agents.size(); i++) {
			Agent a = agents.get(i);
			if (a instanceof Messenger) {
				send(c.getId(), a.getId(), FIPA_Performative.REQUEST, "cfp");
			}
		}
	}

	public Agent getAgent(int id) {
		return agents.get(id);
	}

	public void addAgent(Agent a) {
		a.setId(numberOfAgents);
		this.agents.put(numberOfAgents++, a);
		if (a instanceof Messenger) {
			numberOfMessengers++;
		} else {
			numberOfClients++;
		}
	}

	public void addMessage(FIPA_Message message) {
		this.messageList.add(message);
	}

	// Pr�ft, ob noch eine Nachricht verf�gbar ist.
	public boolean messagesAvailable(int receiver) {
		for (FIPA_Message message : this.messageList) {
			if (message.getReceiver() == receiver) {
				return true;
			}
		}
		return false;
	}

	// Fragt genau eine Nachricht ab.
	public FIPA_Message getMessage(int receiver) {
		for (FIPA_Message message : this.messageList) {
			if (message.getReceiver() == receiver) {
				this.messageList.remove(message);
				return message;
			}
		}
		return null;
	}

	public void send(int sender, int receiver, FIPA_Performative performative, String content) {
		this.addMessage(new FIPA_Message(sender, receiver, performative, content));
	}
}