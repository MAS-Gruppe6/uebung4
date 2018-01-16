package uebung4;

import repast.simphony.space.continuous.NdPoint;

public interface Agent {

	public int getId();

	public int getStartX();

	public int getStartY();

	public NdPoint getStartingPoint();

	public void setId(int id);
}
