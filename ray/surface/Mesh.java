package ray.surface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import ray.IntersectionRecord;
import ray.Ray;
import ray.RayTracer;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;


public class Mesh extends Surface{

    /** The number of vertices in the mesh * */
    protected int numVertices;

    /** The number of triangles in the mesh * */
    protected int numTriangles;

    protected Triangle [] triangles;
    protected float[] normals; // may be null
    protected float[] verts;
    protected float[] texcoords;
  
    /**
     * Default constructor creates an empty mesh
     */
    public Mesh() { }

    /**
     * Basic constructor. Sets mesh data array into the mesh structure. IMPORTANT:
     * The data array are not copies so changes to the input data array will
     * affect the mesh structure. The number of vertices and the number of
     * triangles are inferred from the lengths of the verts and tris array. If
     * either is not a multiple of three, an error is thrown.
     *
     * @param verts the vertex data
     * @param tris the triangle data
     * @param normals the normal data
     */
    public Mesh(float[] verts, int[] tris, float[] normals, float[] texcoords) {

        // check the inputs
        if (verts.length % 3 != 0)
            throw new Error("Vertex array for a triangle mesh is not a multiple of 3.");
        if (tris.length % 3 != 0)
            throw new Error("Triangle array for a triangle mesh is not a multiple of 3.");

        // Set data
        setMeshData(verts, tris, normals, texcoords);

    }
    public boolean existsNormals() {
        return normals!=null;
    }
    public boolean existsTexture() {
        return texcoords!=null;
    }
    public Point3 getVertex(int index) {
        return new Point3(verts[3*index],verts[3*index+1],verts[3*index+2]);
    }
    public Vector3 getNormal(int index) {
        return new Vector3(normals[3*index], normals[3*index+1],normals[3*index+2]);
    }
    public Point2 getTexcoords(int index) {
        return new Point2(texcoords[2*index], texcoords[2*index+1]);
    }
    
    /**
     * Sets the mesh data and builds the triangle array.
     * @param verts the vertices
     * @param tris the triangles
     * @param normals the normals
     */
    private void setMeshData(float[] verts, int[] tris, float[] normals, float[] textcoords) {

        this.numVertices = verts.length / 3;
        this.numTriangles = tris.length / 3;
        this.verts = verts;
        this.normals = normals;
        this.texcoords = textcoords;
        triangles = new Triangle[numTriangles];
        for(int i=0;i<numTriangles;i++) {
            triangles[i] = new Triangle(this, tris[i*3+0],tris[i*3+1],tris[i*3+2] ,shader);
        }
    
        // unused
        this.normals = normals;
    
    }

    public int getNumTriangles() {
        return numTriangles;
    }

    public int getNumVertices() {
        return numVertices;
    }

    /**
     * Set the data in this mesh to the data in fileName
     * @param fileName the name of a .msh file
     */
    public void setData(String fileName) {

        readMesh(this, RayTracer.getTestFolderPath() + fileName);

    }

  
    /**
     * Reads a .msh file into outputMesh.
     *
     * @param outputMesh the mesh to store the read data
     * @param fileName the name of the mesh file to read
     * @return the TriangleMesh from the file
     */
    public static final void readMesh(Mesh outputMesh, String fileName) {

        float[] vertices;
        int[] triangles;
        float[] normals;
        float[] texcoords;
        try {

            // Create a buffered reader for the mesh file
            BufferedReader fr = new BufferedReader(new FileReader(fileName));

            // Read the size of the file
            int nPoints = Integer.parseInt(fr.readLine());
            int nPolys = Integer.parseInt(fr.readLine());

            // Create arrays for mesh data
            vertices = new float[nPoints*3];
            triangles = new int[nPolys*3];
            normals = null;
            texcoords = null;
      
            boolean vertsRead = false;
            boolean trisRead = false;
      
            String line = fr.readLine();
            while(line != null) {
                if(line.equals("vertices")) {
                    for (int i = 0; i < vertices.length; ++i) {
                        vertices[i] = Float.parseFloat(fr.readLine());
                    }
                    vertsRead = true;
                }
                else if( line.equals("texcoords") ) {
                    texcoords = new float[nPoints * 2];
                    for (int i = 0; i < texcoords.length; ++i) {
                        texcoords[i] = Float.parseFloat(fr.readLine());
                    }
                }
                else if( line.equals("triangles")) {
                    for (int i = 0; i < triangles.length; ++i) {
                        triangles[i] = Integer.parseInt(fr.readLine());
                    }
                    trisRead = true;
                }
                else if( line.equals("normals")) {
                    normals = new float[nPoints*3];
                    for (int i = 0; i < normals.length; ++i) {
                        normals[i] = Float.parseFloat(fr.readLine());
                    }
                }
                line = fr.readLine();
            }
            if( !vertsRead )
                throw new Exception("Broken file - vertices expected");
                
            if( !trisRead )
                throw new Exception("Broken file - triangles expected.");

      
        }
        catch (Exception e) {
            throw new Error("Error reading mesh file: "+fileName);
        }

        //Set the data in the output Mesh
        outputMesh.setMeshData(vertices, triangles, normals, texcoords);

        //System.out.println("Read mesh of " + vertices.length + " verts");
    }

        
    public boolean intersect(IntersectionRecord outRecord, Ray rayIn) { return false; }
        
    // instead of adding the mesh, add the individual triangles to scene
    public void addTo(ArrayList<Surface> surfaces) {
        for (Triangle triangle : triangles)
            surfaces.add(triangle); 
    }
}
