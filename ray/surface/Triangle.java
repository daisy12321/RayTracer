package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
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

        // TODO: fill in this function.

        // use strategy from book, pp. 78-79

        return false;
    }

    public String toString() {
        return "Triangle ";
    }
}
