package Turtle;

import processing.core.*;
import java.util.List;

/**
 * Turtle class, implements a LOGO Turtle for Processing
 * 
 * @author Leah Buechley
 */

/**
*
* @example Turtle 
*/

public class Turtle {
	public float currentX;
	public float currentY;
	public float currentTheta;
	public int currentPenState;
	private float savedX;
	private float savedY;
	private float savedTheta;
	private int savedState;
	private boolean pushFlag;
	private boolean wraparound;
	private tLine[] commandHistory;    //command history is an array of lines
	private Turtle[] pushHistory;  	//array to store pushed turtle states
	
	PApplet myParent;
	
	public final static String VERSION = "1.0.0";
	
	/**
	 * Basic constructor, creates a Turtle in the middle of the screen.
	 * 
	 * @param theParent
	 *            parent sketch in which Turtle is generated.
	 */
	 public Turtle(PApplet theParent)  
	  {
		myParent = theParent;
	    currentX = myParent.width/2;
	    currentY = myParent.height/2;
	    currentTheta = 0;
	    currentPenState = penStates.PENDOWN;
	    commandHistory = new tLine[0];
	    this.addHistoryLine();
	    pushFlag = false;
	    pushHistory = new Turtle[0];
	    wraparound = false;
	    theParent.registerMethod("draw", this);
	  }
	 
		/**
		 * Copy constructor, creates a copy of the input Turtle.
		 * 
		 * @param T
		 *            creates a new Turtle using parameters from the input Turtle, T.
		 */
	  public Turtle (Turtle T)
	  {
	    currentX = T.getX();
	    currentY = T.getY();
	    myParent = T.myParent;
	    currentTheta = T.getTheta();
	    currentPenState = T.getPenState();
	    commandHistory = new tLine[0];
	    this.addHistoryLine();
	    pushFlag = false;
	    pushHistory = T.pushHistory;
	    wraparound = T.wraparound;
	  }
	 
		/**
		 * Move Turtle forward.
		 * 
		 * @param distance
		 *            number of steps (maps to pixels) to move.
		 */
	 public void forward (float distance)
	  {
	    if (this.wraparound)
	      forwardWrapAround(distance);
	    else
	    {
	    currentX = currentX + (float)(Math.sin(Math.toRadians(currentTheta))*distance);
	    currentY = currentY + (float)(Math.cos(Math.toRadians(currentTheta))*distance);
	    this.addHistoryLine();
	    }
	  }
	 
		/**
		 * Move Turtle backward.
		 * 
		 * @param distance
		 *            number of steps (maps to pixels) to move.
		 */
	 public void back (float distance)
	  {
	    currentX = currentX - (float)(Math.sin(Math.toRadians(currentTheta))*distance);
	    currentY = currentY - (float)(Math.cos(Math.toRadians(currentTheta))*distance);
	    this.addHistoryLine();
	  }
		/**
		 * Turn Turtle to the right.
		 * 
		 * @param angle
		 *            degrees to turn.
		 */
	 public void right (float angle) 
	  {
	    currentTheta = currentTheta + angle;
	  }
	 
		/**
		 * Turn Turtle to the left.
		 * 
		 * @param angle
		 *            degrees to turn.
		 */
	 public void left (float angle) 
	  {
	    currentTheta = currentTheta - angle;
	  }
	 
		/**
		 * Set Turtle's pen state to PENUP.
		 * 
		 */
	 public void penUp ()
	  {
	    currentPenState = penStates.PENUP;
	  }

		/**
		 * Set Turtle's pen state to PENDOWN.
		 * 
		 */
	 public void penDown ()
	  {
	    currentPenState = penStates.PENDOWN;
	  }

		/**
		 * Save ("push") Turtle's current state to the stack.
		 * 
		 */
	 public void push()
	  {
	    Turtle [] tempTurtle= new Turtle[1];
	    tempTurtle[0]=new Turtle(this);
	    pushHistory = concat(pushHistory,tempTurtle);
	  }
	 
		/**
		 * Return Turtle to last saved state and remove ("pop") that state from the stack.
		 * 
		 */
	 public void pop()
	  {
	    int pushLength = pushHistory.length;
	    if (pushLength > 0)
	    {
	      this.currentPenState = penStates.PENUP; 
	      this.currentX =  this.pushHistory[pushLength-1].getX();
	      this.currentY =  this.pushHistory[pushLength-1].getY();
	      this.currentTheta = this.pushHistory[pushLength-1].getTheta();
	      this.addHistoryLine();
	      this.currentPenState = this.pushHistory[pushLength-1].getPenState();   
	      this.pushHistory = shorten(pushHistory);
	    }
	    else
	    {
	    	System.out.println("ERROR: tried to pop without a push");
	    }
	  }
		/**
		 * Calculate Turtle's distance from a point.
		 * 
		 * @param x
		 *            x coordinate of point.
		 *            
		 * @param y
		 *            y coordinate of point.
		 *            
		 */
	 public float distanceFromPoint(float x, float y)
	  {
	    float d = (float)Math.sqrt(((currentX-x)*(currentX-x))+((currentY-y)*(currentY-y)));
	    return d;
	  }
	 
		/**
		 * Answer question: if Turtle moves forward a distance will it cross its previous path.
		 * 
		 * @param distance
		 *            distance to move forward.
		 *            
		 */
	 public boolean closeToPath (float distance)
	  {
	    float nextX = this.currentX + (float)(Math.sin(Math.toRadians(this.currentTheta))*distance);
	    float nextY = this.currentY + (float)(Math.cos(Math.toRadians(this.currentTheta))*distance);
	    tPoint currentPoint = new tPoint(this.currentX, this.currentY);
	    tPoint nextPoint =  new tPoint(nextX, nextY);
	    tLine lineToCheck = new tLine(currentPoint, nextPoint, this.currentTheta, this.currentPenState, this.myParent);
	    int numLines = commandHistory.length;
	    for (int i=0;i<numLines;i++)
	    {
	      if (commandHistory[i].state!=penStates.PENUP)
	      {
	       if (commandHistory[i].intersects(lineToCheck))
	       {
	         //println(distanceFromPoint(commandHistory[i].p0.x, commandHistory[i].p0.y));
	         return true;
	       }
	      }
	    }
	    return false;
	  }
	  
		/**
		 * Move Turtle forward with wrap around. If Turtle "falls off" one edge of the screen, reappear on opposite edge. 
		 * 
		 * @param distance
		 *            number of steps (maps to pixels) to move.
		 */
	 public void forwardWrapAround (double distance)
	  {
	    float x, y;
	    int currentPenStateTemp = this.getPenState();
	    
	    x = currentX + (float)(Math.sin(Math.toRadians(currentTheta))*distance);
	    if ((myParent.width-x)<0 | (myParent.width-x)>myParent.width)
	    {
	      this.currentPenState=penStates.PENUP;
	      currentX = Math.abs(myParent.width-Math.abs(x));
	    }
	    else
	      currentX = x;
	      
	    y = currentY + (float)(Math.cos(Math.toRadians(currentTheta))*distance);
	    if ((myParent.height-y)<0 | (myParent.height-y)>myParent.height)
	    {
	      this.currentPenState=penStates.PENUP;
	      currentY = Math.abs(myParent.height-Math.abs(y));
	    }
	    else
	      currentY = y;
	    this.addHistoryLine();
	    this.currentPenState=currentPenStateTemp;
	  }
	 
		/**
		 * Get the X coordinate of Turtle's current position.
		 * 
		 * @return X coordiante
		 */
	 public float getX () 
	  {
	    return currentX;
	  }
		/**
		 * Get the Y coordinate of Turtle's current position.
		 * 
		 * @return Y coordiante
		 */
	 public float getY () 
	  {
	    return currentY;
	  }

		/**
		 * Get the Turtle's current heading (angle).
		 * 
		 * @return angle
		 */
	 public float getTheta()
	  {
	    return currentTheta;
	  }
	 
		/**
		 * Get the Turtle's current pen state.
		 * 
		 * @return pen state
		 */
	 public int getPenState()
	  {
	    return currentPenState;
	  }

	  int getLength()
	  {
	    return commandHistory.length;
	  }
	   
		/**
		 * Set the X coordinate of Turtle's current position.
		 * 
		 * @param x
		 * 			X coordinate
		 * 				
		 */
	  public void setX (float x) 
	  {
	    currentX = x;
	  }
	  
		/**
		 * Set the Y coordinate of Turtle's current position.
		 * 
		 * @param y
		 * 			Y coordinate
		 * 				
		 */
	  public void setY (float y) 
	  {
	    currentY = y;
	  }
	  
		/**
		 * Set the Turtle's current heading (angle).
		 * 
		 * @param theta
		 * 			angle input, in degrees
		 * 				
		 */
	  public void setTheta (float theta) 
	  {
	    currentTheta = theta;
	  }
	  
		/**
		 * Set the Turtle's current pen state. Use discouraged. Use penUp() and penDown() instead.
		 * 
		 * @param penStateInput
		 * 			PENUP=1, PENDOWN=0, PENFAT=3
		 * 				
		 */
	  public void setPenState (int penStateInput) 
	  {
	    currentPenState = penStateInput;
	  }
	  
		/**
		 * Jump Turtle to input point.
		 * 
		 * @param xInput
		 * 			X coordinate of point
		 * 
		 * @param yInput
		 * 			Y coordinate of point
		 * 				
		 */
	  public void goToPoint(float xInput, float yInput)
	  { 
	    currentX = xInput;
	    currentY = yInput;
	    this.addHistoryLine();
	  }

	  private void curveToPoint(float xInput, float yInput)
	  {     
	    float angle;
	    tPoint currentPoint; 
	    tPoint finalVectorPoint;
	    tLine finalVector;
	    float arcLength;
	    float angleStep;
	    float iterations = 100;
	    
	    currentPoint = new tPoint(this.currentX, this.currentY);
	    finalVectorPoint = new tPoint(xInput, yInput);
	    finalVector = new tLine(currentPoint, finalVectorPoint, this.currentTheta, this.currentPenState, this.myParent);
	    
	    angle = angleToPoint(xInput, yInput);
	    if (angle==0 | angle==180) 
	    {
	      arcLength = finalVector.magnitude();
	      angleStep = 0;
	      if (angle==180)  //weird special case, awkward hack
	        this.right(180);
	    }
	    else
	    {
	      arcLength = finalVector.magnitude()*(float)(Math.PI*(angle*2)/(360*Math.sin(Math.toRadians(angle))));    
	      angleStep = angle*2/iterations;
	    }
	    float stepSize = arcLength/iterations;
	    for (int i=0;i<iterations;i++)
	    {
	      this.forward(stepSize);
	      this.right(angleStep);
	    }
	  }

	  private float angleToPoint(float xInput, float yInput)
	  {
	    float angle;
	    tPoint currentPoint;
	    tPoint nextPoint;
	    tLine currentVector;
	    
	    tPoint finalVectorPoint;
	    tLine finalVector;
	    
	    currentPoint = new tPoint(this.currentX, this.currentY);
	    nextPoint = new tPoint(this.currentX+(Math.sin(Math.toRadians(currentTheta))*10), this.currentY+ (Math.cos(Math.toRadians(currentTheta))*10));
	    currentVector = new tLine(currentPoint, nextPoint, this.currentTheta, this.currentPenState, this.myParent);

	    finalVectorPoint = new tPoint(xInput, yInput);
	    finalVector = new tLine(currentPoint, finalVectorPoint, this.currentTheta, this.currentPenState, this.myParent);
	    
	    //calculate angle magnitude
	    angle = (float)Math.toDegrees(Math.acos(currentVector.dotProduct(finalVector)/(currentVector.magnitude()*finalVector.magnitude())));
	    if (Float.isNaN(angle))
	    {
	      //angle is eigher 180 or 0
	      angle = 0;
	    }
	    //calculate angle direction (is point to left or right of current heading)
	    if (currentVector.triangleArea(finalVectorPoint) > 0)
	    {
	      angle = -angle;
	    }
	    
	    return angle;
	  }

	 
	 private void addHistoryLine()
	  {
	    tLine[] tempLine = new tLine[1];
	    tPoint lastPoint = new tPoint();
	    tPoint nextPoint = new tPoint();
	    tLine currentLine = new tLine(this.myParent);
	    int historyLength = this.getLength();
	    nextPoint = new tPoint(this.currentX, this.currentY);
	    if (historyLength>0)
	    {  
	      lastPoint = commandHistory[historyLength-1].p1;
	      currentLine = new tLine(lastPoint, nextPoint, this.currentTheta, this.currentPenState, this.myParent);
	    }
	    else
	    {
	      currentLine = new tLine(nextPoint, nextPoint, this.currentTheta, this.currentPenState, this.myParent);
	    }
	    tempLine[0] = currentLine;
	    commandHistory = concat(commandHistory,tempLine);
	  }


	 private void deleteHistoryLine()
	  {
	    int historyLength = commandHistory.length;
	    tLine[] tempHistory= new tLine[0];

	    if (historyLength>0)
	    {
	      tempHistory = new tLine[historyLength-1];
	      for(int i=0;i<historyLength-1;i++)
	      {
	        tempHistory[i] = commandHistory[i];
	      }
	      currentX = tempHistory[historyLength-1-1].p1.x;
	      currentY = tempHistory[historyLength-1-1].p1.y;
	      currentTheta = tempHistory[historyLength-1-1].theta;
	      currentPenState = tempHistory[historyLength-1-1].state;
	    }
	    commandHistory = tempHistory;
	  }

		/**
		 * Draw method; draw Turtle's history to the screen. 
		 * You shouldn't call this directly unless you use
		 * {@link processing.core.PApplet#noLoop()}. Will be 
		 * called automatically by Processing. 
		 */
	  public void draw()
	  {
	    int numLines = this.commandHistory.length;
	    for (int i=0;i<numLines;i++)
	    {
	      if (this.commandHistory[i].state==penStates.PENDOWN)
	        this.commandHistory[i].draw();
	    }
	  }
	
	  
		private tLine[] concat(tLine[] array1, tLine[] array2) {
			int totalLength;
			int length1 = array1.length;
			totalLength = length1 + array2.length;
			
			tLine[] finalArray= new tLine[totalLength];
			for (int i=0;i<totalLength;i++)
			{
				if (i<length1)
					finalArray[i]=array1[i];
				else
					finalArray[i]=array2[i-length1];
			}
			return finalArray;
		}
		
		private Turtle[] concat(Turtle[] array1, Turtle[] array2) {
			int totalLength;
			int length1 = array1.length;
			totalLength = length1 + array2.length;
			
			Turtle[] finalArray= new Turtle[totalLength];
			for (int i=0;i<totalLength;i++)
			{
				if (i<length1)
					finalArray[i]=array1[i];
				else
					finalArray[i]=array2[i-length1];
			}
			return finalArray;
		}
		
		//remove last element from array
		private Turtle[] shorten(Turtle[] array1) {
			int newLength = array1.length-1;
			Turtle[] finalTurtle= new Turtle[newLength];
			for (int i=0;i<newLength;i++)
			{
				finalTurtle[i]=array1[i];
			}
			return finalTurtle;
			
		}
		
		/**
		 * Delete the Turtle's previous history and clear all previous drawing.
		 * 				
		 */
		public void clearTurtleHistory()
		  {
		    this.commandHistory = new tLine[0];
		    this.addHistoryLine();
		  }
		  
	
	
	private void welcome() {
		System.out.println("Turtle 1.0.0 by Leah Buechley http://leahbuechley.com");
	}
	
}

