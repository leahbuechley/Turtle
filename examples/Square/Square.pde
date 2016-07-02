import Turtle.*;
Turtle t;

void setup() {
  size(500,500);
  background(255);
  stroke(0);
  t = new Turtle(this);
}

void draw () {
   //draw a square
   t.forward(100);
   t.right(90);
   t.forward(100);
   t.right(90);
   t.forward(100);
   t.right(90);
   t.forward(100);
   t.right(90);
}