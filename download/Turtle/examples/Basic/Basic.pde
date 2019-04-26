import Turtle.*;
Turtle t;

void setup() {
  size(500,500);
  background(255);
  stroke(0);
  t = new Turtle(this);
  noLoop();
}

void draw () {
  //move forward 100 steps
  t.forward(100);
}
