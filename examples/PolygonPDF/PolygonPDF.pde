import processing.pdf.*;
import Turtle.*;
Turtle t;
String fileName;

void setup() {
  size(800,800);
  background(255);
  stroke(0);
  t = new Turtle(this);
  noLoop();
}

void draw () {
  background(255);
  //draw a polygon with 5 sides, each of length 100
  polygon(100,5);
}

//a polygon drawing procedure
void polygon(float sideLength, int numberOfSides)
{
  for (int i=0;i<numberOfSides;i++)
  {
    t.forward(sideLength);
    t.right(360/numberOfSides);
  }
}

void keyPressed() 
{
  //press the 's' key to save a pdf of your drawing
  if (key == 's') 
  {
    //name of file is "turtleDrawing" plus a unique(ish) number
    fileName= "turtleDrawing"+ second() +".pdf";
    beginRecord(PDF, fileName);
    draw();
    endRecord();
  }
} 
