package ray.surface;

import java.util.Arrays;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;
public class Cone extends Surface {
    
    /** The center of the the truncated cone. */
    protected final Point3 center = new Point3();
    public void setCenter(Point3 center) { this.center.set(center); }

    /** The z-location of the tip of the cone. */
    protected double tipz = 0.0;
    public void setTipz(double tipz) { this.tipz = tipz; }
        
    /** The radius of the cone in the plane z = center.z. */
    protected double radius = 1.0;
    public void setRadius(double radius) { this.radius = radius; } 

    /** The height of the cone.
     * Truncation of the cone occurs at center.z-height/2 and center.z+height/2
     */
    protected double height = 1.0;
    public void setHeight(double height) { this.height = height; }

    public Cone() { }

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
        
        boolean doSide = true;
        boolean doCaps = true;
                
        boolean inters = false;
        double tw = Double.POSITIVE_INFINITY; // t value for intersection with cone wall
        double te = Double.POSITIVE_INFINITY; // t value for intersection with end caps
        Point3 q = new Point3(); // intersection point

        // compute ray-cone intersection
                
        // first, compute intersection with cone wall for ray p + t*d
        Vector3 ec = new Vector3();
        ec.sub(rayIn.origin, center); // e-c
        Vector3 d = new Vector3(rayIn.direction);
                
        double H = tipz - center.z;
        double R = radius;
                
        // common subexpressions:
        double s = R*R/(H*H); // scale factor for z dimension
        double pzh = ec.z - H; // offset tip from origin e
                
        // the parameters of the quadratic equation A*t^2 + B*t + C = 0:
        double A = d.x*d.x + d.y*d.y - s * d.z*d.z;
        double B = 2 * (ec.x*d.x + ec.y*d.y - s * pzh*d.z);
        double C = ec.x*ec.x + ec.y*ec.y - s * pzh*pzh;

        double discr = B*B - 4*A*C;
        if (discr >= 0.0) {
            double sd = Math.sqrt(discr);
            double t1 = (-B + sd) / (2 * A);
            if (t1 < rayIn.start ||  t1 > rayIn.end)
                t1 = Double.POSITIVE_INFINITY;
            rayIn.evaluate(q, t1); // intersection point
            if (Math.abs(q.z - center.z) > height/2)   // outside truncated range
                t1 = Double.POSITIVE_INFINITY;
            double t2 = (-B - sd) / (2 * A);
            if (t2 < rayIn.start ||  t2 > rayIn.end)
                t2 = Double.POSITIVE_INFINITY;
            rayIn.evaluate(q, t2); // intersection point
            if (Math.abs(q.z - center.z) > height/2)   // outside truncated range
                t2 = Double.POSITIVE_INFINITY;
            // phew - now t1, t2 are either valid or infinity
            double t = Math.min(t1,  t2);  // intersection with cone wall
            if (t >= rayIn.start &&  t <= rayIn.end) {
                if (doSide) {
                    inters = true;
                    tw = t;
                }
            }                   
        }

        // next, check intersection of ray with endcaps of cone

        // e.z + t * d.z == center.z +/- height/2;
        // t = (center.z - e.z +/- height/2) / d.z;
        double te1 = (-ec.z - height/2) / d.z;
        if (te1 < rayIn.start ||  te1 > rayIn.end)
            te1 = Double.POSITIVE_INFINITY;
        double te2 = (-ec.z + height/2) / d.z;
        if (te2 < rayIn.start ||  te2 > rayIn.end)
            te2 = Double.POSITIVE_INFINITY;
        double tem = Math.min(te1, te2);
        double dir = (te1 < te2) ? -1.0 : 1.0; // whether top or bottom end
        rayIn.evaluate(q, tem);  // intersection location with endcap
        Vector3 rad = new Vector3();
        rad.sub(center, q); // compute radius vector
        rad.z = 0;          // in x-y plane
        double r = (q.z - tipz) * R/H; // radius at z value q.z
        if (rad.dot(rad) <= r*r) { // intersection with end cap
            if (tem >= rayIn.start &&  tem <= rayIn.end) {
                if (doCaps) {
                    inters = true;
                    te = tem;
                }
            }
        }

        // fill in outRecord and compute normals
        if (inters) {
            double t = Math.min(tw, te);
            outRecord.t = t;
            rayIn.evaluate(q, t);
            outRecord.location.set(q);
            outRecord.surface = this;
            if (tw < te) { // intersection with wall
                outRecord.normal.sub(q, center); // compute normal
                outRecord.normal.z = 0;
                outRecord.normal.normalize(); // radial unit vector in z=0 plane
                outRecord.normal.z = R/H; // set z component according to slope of cone
                outRecord.normal.normalize();
            } else { // intersection with endcap
                outRecord.normal.set(0, 0, dir); // normal points in z direction
            }
            return true;
        }
        
        return false;
    }
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "Cone " + center + " "+ radius + " "+ height + " "+ tipz + " "+ shader + " end";
    }
}