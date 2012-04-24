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
        // TODO: fill in this function.
		Vector3 l = new Vector3();
		
		for (Light light : lights) {
			if (!isShadowed(scene, light, record)) {
				l.sub(light.position, record.location);
				l.normalize();
				toEye.normalize();
				
				double cosTheta1 = Math.max(0,record.normal.dot(l));
				double cosTheta2 = Math.sqrt(1-Math.pow(Math.sqrt((1-cosTheta1*cosTheta1))/refractiveIndex, 2));
				//System.out.println(cosTheta1+" "+cosTheta2);

				double Fp = (refractiveIndex*cosTheta1-cosTheta2)/(refractiveIndex*cosTheta1+cosTheta2);
				double Fs = (cosTheta1-refractiveIndex*cosTheta2)/(cosTheta1+refractiveIndex*cosTheta2);
				double R = 0.5 * (Fp*Fp + Fs*Fs);
				//System.out.println(R);
				
				substrate.shade(outColor, scene, lights, toEye, record, depth, contribution, internal);
				outColor.scale(1-R);
				
		        double d = record.normal.dot(toEye);
		    	
			}
		}
        // Implement Fresnel equation (Shirley 13.1)
        // compute reflected contribution (make a recursive call to shadeRay)
        // compute substrate contribution (call substrate.shade(...))
		
		
    }
}
