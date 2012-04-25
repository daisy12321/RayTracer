package ray.shader;

import java.util.ArrayList;


import ray.IntersectionRecord;
import ray.Ray;
import ray.RayTracer;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

public class Glazed extends Shader {
        
    protected double refractiveIndex;
    public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }
        
    protected Shader substrate;
    public void setSubstrate(Shader substrate) { this.substrate = substrate; }
        
    public Glazed() { }

    /**
     * Calculate the BRDF value for this material at the intersection described in record.
     * Returns the BRDF color in outColor.
     * @param outColor Space for the output color
     * @param scene The scene
     * @param lights The lights
     * @param toEye Vector pointing towards the eye
     * @param record The intersection record, which hold the location, normal, etc.
     * @param depth The depth of recursive calls.
     * @param contribution The contribution of the current ray.
     * @param internal You can ignore this for glazed.
     */
	public void shade(Color outColor, Scene scene, ArrayList<Light> lights,
			Vector3 toEye, IntersectionRecord record, int depth,
			double contribution, boolean internal) {
        // Implement Fresnel equation (Shirley 13.1)
        // compute reflected contribution (make a recursive call to shadeRay)
        // compute substrate contribution (call substrate.shade(...))
		
		if (depth > MAXDEPTH || contribution < MINCONTRIBUTION){ 
			return;
		}
		toEye.normalize();
		// compute reflected ray
        double d = record.normal.dot(toEye);
    	Vector3 r = new Vector3(record.normal.x, record.normal.y, record.normal.z);
    	r.scale(2*d);
    	r.sub(toEye);
    	
    	Ray refRay = new Ray(record.location, r);
		
		Color outColor1 = new Color();
		Color outColor2 = new Color();
		
		// TODO: fix the recursive call

		double cosTheta1 = Math.max(0,record.normal.dot(toEye));
		double cosTheta2 = Math.sqrt(1-(1-cosTheta1*cosTheta1)/(refractiveIndex * refractiveIndex));

		double Fp = (refractiveIndex*cosTheta1-cosTheta2)/(refractiveIndex*cosTheta1+cosTheta2);
		double Fs = (cosTheta1-refractiveIndex*cosTheta2)/(cosTheta1+refractiveIndex*cosTheta2);
		double R = 0.5 * (Fp*Fp + Fs*Fs);
		
		RayTracer.shadeRay(outColor1, scene, refRay, lights, depth+1, R, false);
		
		substrate.shade(outColor2, scene, lights, toEye, record, 1, 1, false);
		
    	outColor1.scale(R);
		outColor2.scale(1-R);
		
		outColor.add(outColor1);
		outColor.add(outColor2);

		outColor.clamp(0, 1);
    }
}
