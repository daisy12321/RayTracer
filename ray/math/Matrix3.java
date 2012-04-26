package ray.math;
import ray.RayTracer;

public class Matrix3 {
	  public double[][] m = new double[3][3];

	  /**
	   * Default constructor.  
	   */
	  public Matrix3() {
	        setIdentity();
	  }

	  public void setByRow(Tuple3 newTuple1, Tuple3 newTuple2, Tuple3 newTuple3) {

		  	set(newTuple1.x, newTuple1.y, newTuple1.z,  
	    		newTuple2.x,newTuple2.y,newTuple2.z, 
	    		newTuple3.x,newTuple3.y,newTuple3.z);
	  }

	  public void setByColumn(Tuple3 newTuple1, Tuple3 newTuple2, Tuple3 newTuple3) {

		  	set(newTuple1.x, newTuple2.x, newTuple3.x,  
	    		newTuple1.y,newTuple2.y,newTuple3.y, 
	    		newTuple1.z,newTuple2.z,newTuple3.z);
	  }
	  
	  public void setIdentity() {
		    set(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d);
		  }
	  /**
	   * The explicit constructor.  This is the only constructor with any real
	   * code in it.  Values should be set here, and any variables that need to
	   * be calculated should be done here.
	   */
	  public void set(double newX0, double newY0, double newZ0, 
			  double newX1, double newY1, double newZ1, 
			  double newX2, double newY2, double newZ2) {
	    m[0][0] = newX0;
	    m[0][1] = newY0;
	    m[0][2] = newZ0;
	    m[1][0] = newX1;
	    m[1][1] = newY1;
	    m[1][2] = newZ1;
	    m[2][0] = newX2;
	    m[2][1] = newY2;
	    m[2][2] = newZ2;
	  }

	  public double determinant(){
		  return m[0][0]*m[1][1]*m[2][2] + m[0][1]*m[1][2]*m[2][0] + m[0][2]*m[1][0]*m[2][1] 
               - m[0][2]*m[1][1]*m[2][0] - m[0][1]*m[1][0]*m[2][2] - m[0][0]*m[1][2]*m[2][1] ;
	  }
}
