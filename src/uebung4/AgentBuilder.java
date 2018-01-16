package uebung4;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;

public class AgentBuilder implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("Uebung4");

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.StrictBorders(), 50, 50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);

		// ebenfalls StrictBorder
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(), new SimpleGridAdder<Object>(), true, 50, 50));

		Messenger[] messengers = new Messenger[] { new Messenger(new NdPoint(5, 5), space, grid),
				new Messenger(new NdPoint(45, 5), space, grid), new Messenger(new NdPoint(5, 25), space, grid),
				new Messenger(new NdPoint(45, 45), space, grid) };

		Client[] clients = new Client[] { new Client(new NdPoint(15, 10), space, grid),
				new Client(new NdPoint(5, 35), space, grid), new Client(new NdPoint(40, 10), space, grid),
				new Client(new NdPoint(35, 45), space, grid), new Client(new NdPoint(40, 35), space, grid),
				new Client(new NdPoint(10, 35), space, grid) };

		MessageCenter msgCenter = MessageCenter.singleton;

		for (Messenger messenger : messengers) {
			context.add(messenger);
			space.moveTo(messenger, messenger.getStartX(), messenger.getStartY());
			NdPoint pt = space.getLocation(messenger);
			grid.moveTo(messenger, (int) pt.getX(), (int) pt.getY());
			msgCenter.addAgent(messenger);
		}

		for (Client client : clients) {
			context.add(client);
			space.moveTo(client, client.getStartX(), client.getStartY());
			NdPoint pt = space.getLocation(client);
			grid.moveTo(client, (int) pt.getX(), (int) pt.getY());
			msgCenter.addAgent(client);
		}

		return context;
	}

}
