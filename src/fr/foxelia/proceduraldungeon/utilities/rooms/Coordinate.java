package fr.foxelia.proceduraldungeon.utilities.rooms;

public class Coordinate {
	
	private double x;
	private double y;
	private double z;
	
	public Coordinate(double x, double y, double z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
}
