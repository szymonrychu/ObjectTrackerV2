package simple.as.fuck.objecttrackerv2.elements;

public class Pointer{
	public int id;
	public float x;
	public float y;
	public Pointer(int id){
		this.id = id;
	}
	public double getDistance(double x, double y){
		double X = x - this.x;
		double Y = y - this.y;
		return Math.sqrt(X*X+Y*Y);
	}
}