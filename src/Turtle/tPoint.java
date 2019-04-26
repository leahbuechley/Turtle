package Turtle;

public class tPoint
{
  public float x;
  public float y;

  tPoint (float xInput, float yInput)
  {
    x = xInput;
    y = yInput;
  }
  
  tPoint (double xInput, double yInput)
  {
    x = (float)xInput;
    y = (float)yInput;
  }

  tPoint ()
  {
    x = 0;
    y = 0;
  }
}