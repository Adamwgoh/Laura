package imageanalysis;
/**
 * A general object class for easy extraction of a point. Similar to the class Point
 * @author Adam Goh
 *
 */
public class Coordinates {

	private int x;
	private int y;
	
	public Coordinates(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	/**
	 * produces a string listing the X value and Y Value
	 */
	@Override
	public String toString(){
		return "X is " + x + ", Y is " + y;
	}
	
	
	public int getDifference(){
		return Math.abs(x-y);
	}
}
