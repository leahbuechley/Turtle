package Turtle;

import java.lang.Math.*;
import processing.core.PApplet;

//some hand-coded (& inefficient) linear algebra here. should be replaed with better versions.

public class tLine {
	public tPoint p0;
	public tPoint p1;
	public int state;
	public float theta;
	PApplet myParent;
	private int internalHeight;
	private int internalWidth;

	public tLine(tPoint p0Input, tPoint p1Input, float thetaInput, int stateInput, PApplet theParent) {
		if (!((stateInput == penStates.PENDOWN) | (stateInput == penStates.PENUP) | (stateInput == penStates.PENFAT)
				| (stateInput == penStates.FILLED))) {
			System.out.println("BAD PEN INPUT in line!!");
			return;
		}
		p0 = new tPoint();
		p0 = p0Input;
		p1 = new tPoint();
		p1 = p1Input;
		this.state = stateInput;
		this.theta = thetaInput;
		myParent = theParent;
		internalHeight = theParent.height;
		internalWidth = theParent.width;
	}

	tLine(PApplet theParent) {
		p0 = new tPoint();
		p1 = new tPoint();
		state = penStates.PENDOWN;
		theta = 0;
		myParent = theParent;
		internalHeight = theParent.height;
		internalWidth = theParent.width;
	}

	public void draw() {
		if (state == penStates.PENDOWN | state == penStates.PENFAT)
			myParent.line(this.p0.x, this.p0.y, this.p1.x, this.p1.y);
	}

	void drawTurtle() {
		float x1, y1, x2, y2, x3, y3;
		x1 = this.p0.x + (float) (Math.sin(Math.toRadians(this.theta - 90)) * 5);
		y1 = this.p0.y + (float) (Math.cos(Math.toRadians(this.theta - 90)) * 5);
		x2 = this.p0.x + (float) (Math.sin(Math.toRadians(this.theta)) * 10);
		y2 = this.p0.y + (float) (Math.cos(Math.toRadians(this.theta)) * 10);
		x3 = this.p0.x + (float) (Math.sin(Math.toRadians(this.theta + 90)) * 5);
		y3 = this.p0.y + (float) (Math.cos(Math.toRadians(this.theta + 90)) * 5);

		myParent.triangle(x1, (internalHeight - y1), x2, (internalHeight - y2), x3, (internalHeight - y3));
	}

	// returns the area of the triangle made
	// by p0, p1 and (input to method) p2
	// negative is p2 is to the right of line p0->p1
	float triangleArea(tPoint p2) {
		float area = (((this.p1.x - this.p0.x) * (p2.y - this.p0.y)) - ((p2.x - this.p0.x) * (this.p1.y - this.p0.y)));
		return area;
	}

	float magnitude() {
		return (float) Math.sqrt(
				(this.p1.x - this.p0.x) * (this.p1.x - this.p0.x) + (this.p1.y - this.p0.y) * (this.p1.y - this.p0.y));
	}

	float dotProduct(tLine l2) {
		return ((this.p1.x - this.p0.x) * (l2.p1.x - l2.p0.x)) + ((this.p1.y - this.p0.y) * (l2.p1.y - l2.p0.y));
	}

	boolean left(tPoint p2) {
		return ((this.triangleArea(p2)) >= 0);
	}

	boolean collinear(tPoint p2) {
		if (this.triangleArea(p2) == 0)
			return true;
		return false;
	}

	boolean between(tPoint p2) {
		// first, make sure point p2 is *on* current line
		if (!this.collinear(p2))
			return false;

		// check to see if line is vertical
		if (this.p0.x != this.p1.x) {
			// if not vertical, check to see if point overlaps in x
			return (((this.p0.x < p2.x) && (p2.x < this.p1.x)) | ((this.p0.x > p2.x) && (p2.x > this.p1.x)));
		} else {
			// if vertical, check to see if point overlaps in y
			return (((this.p0.y < p2.y) && (p2.y < this.p1.y)) | ((this.p0.y > p2.y) && (p2.y > this.p1.y)));
		}
	}

	boolean intersectsProper(tLine l1) {
		// penup lines don't count
		if (l1.state == penStates.PENUP | this.state == penStates.PENUP)
			return false;

		// collinear doesn't count
		if (this.collinear(l1.p0) | this.collinear(l1.p1) | l1.collinear(this.p0) | l1.collinear(this.p1))
			return false;

		if (this.left(l1.p0) | this.left(l1.p1)) {
			if (this.left(l1.p0) && this.left(l1.p1))
				return false;
			if (l1.left(this.p0) | l1.left(this.p1)) {
				if (l1.left(this.p0) && l1.left(this.p1))
					return false;
				else
					return true;
			}
		}
		return false;
	}

	boolean intersects(tLine l1) {
		if (this.intersectsProper(l1)) {
			return true;
		} else if (this.between(l1.p0) | this.between(l1.p1) | l1.between(this.p0) | l1.between(this.p1)) {
			return true;
		}
		return false;
	}

	// useful for debugging
	public void printLine() {
		System.out.print("[");
		this.p0.printPoint();
		System.out.print(", ");
		this.p1.printPoint();
		System.out.print(", theta: " + theta);
		System.out.println("]");
	}
}