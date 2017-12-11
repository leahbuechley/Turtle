package Turtle;

public class tPoint {
	public float x;
	public float y;

	public tPoint(float xInput, float yInput) {
		x = xInput;
		y = yInput;
	}

	public tPoint (double xInput, double yInput)
  {
    x = (float)xInput;
    y = (float)yInput;
  }

	public tPoint() {
		x = 0;
		y = 0;
	}

	// useful for debugging
	public void printPoint() {
		System.out.print("(");
		System.out.print((int) this.x);
		System.out.print(", ");
		System.out.print((int) this.y);
		System.out.print(") ");
	}
}