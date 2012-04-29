package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Matrix3;
import ray.math.Point3;
import ray.math.Vector3;
import ray.shader.Shader;

/**
 * @author ags, modified by DS 4/2012
 */
public class Triangle extends Surface {
    /** The derived normal vector of this triangle, if not loaded from file  */
    Vector3 norm;
        
    /** The mesh that contains this triangle  */
    Mesh owner;
        
    /** 3 indices to the vertices of this triangle. */
    int []index;
        
    public Triangle(Mesh owner, int index0, int index1, int index2, Shader material) {
        this.owner = owner;
        index = new int[3];
        index[0] = index0;
        index[1] = index1;
        index[2] = index2;
                
        if(!owner.existsNormals()) {
            Point3 v0 = owner.getVertex(index0);
            Point3 v1 = owner.getVertex(index1);
            Point3 v2 = owner.getVertex(index2);
                
            Vector3 e0 = new Vector3(), e1 = new Vector3();
            e0.sub(v1, v0);
            e1.sub(v2, v0);
            norm = new Vector3();
            norm.cross(e0, e1);
            norm.normalize();
        }
                                
        this.setShader(material);
    }

    /**
     * Tests this surface for intersection with ray. If an intersection is found
     * record is filled out with the information about the intersection and the
     * method returns true. It returns false otherwise and the information in
     * outRecord is not modified.
     *
     * @param outRecord the output IntersectionRecord
     * @param ray the ray to intersect
     * @return true if the surface intersects the ray
     */
    public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    	Vector3 a_minus_b = new Vector3();
    	a_minus_b.sub(owner.getVertex(index[0]), owner.getVertex(index[1]));
    	
    	Vector3 a_minus_c = new Vector3();
    	a_minus_c.sub(owner.getVertex(index[0]), owner.getVertex(index[2]));
    	
    	Vector3 a_minus_e = new Vector3();
    	a_minus_e.sub(owner.getVertex(index[0]), rayIn.origin);
    	
    	
    	Matrix3 A = new Matrix3();
    	A.setByColumn(a_minus_b, a_minus_c, rayIn.direction);
    	double detA = A.determinant();
    	
    	Matrix3 T = new Matrix3();
    	T.setByColumn(a_minus_b, a_minus_c, a_minus_e);
    	
    	double tmpT = T.determinant() / detA;

    	if ((tmpT < rayIn.start) || (tmpT > rayIn.end)) {
    		return false;
    	}
    	
    	Matrix3 C = new Matrix3();
    	C.setByColumn(a_minus_b, a_minus_e, rayIn.direction);
    	double gamma = C.determinant() / detA;
    	
    	if ((gamma < 0) || (gamma > 1)) {
    		return false;
    	}
    	
    	Matrix3 B = new Matrix3();
    	B.setByColumn(a_minus_e, a_minus_c, rayIn.direction);
    	double beta = B.determinant() / detA;
    	
    	if ((beta < 0) || (beta > 1 - gamma)) {
    		return false;
    	}

    	outRecord.t = rayIn.end = tmpT;
    	outRecord.surface = this;
		outRecord.location.add(rayIn.origin, Vector3.getScaledVector(rayIn.direction, outRecord.t));
		if (owner.existsNormals()) {
			norm = new Vector3();
			norm.scaleAdd(1-beta-gamma, owner.getNormal(index[0]));
			norm.scaleAdd(gamma, owner.getNormal(index[2]));
			norm.scaleAdd(beta, owner.getNormal(index[1]));;
			norm.normalize();
			outRecord.normal.set(norm);
		} else {
			outRecord.normal.set(norm);
		}
		
        return true;
    }

    public String toString() {
        return "Triangle ";
    }
}