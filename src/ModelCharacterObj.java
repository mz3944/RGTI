import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point4f;
import javax.vecmath.Point4i;

import org.lwjgl.opengl.GL11;



public class ModelCharacterObj extends Model3D {
	
	// 3d vector to store the character's position in
	private float[] position = null;
	// the rotation around the Y axis of the camera
	private float yaw = 0.0f;
	// the rotation around the X axis of the camera
	private float pitch = 0.0f;
	
	//lists for drawing character
	List <Point4f> vertex = new LinkedList<Point4f>();
	List <Point4f> normals = new LinkedList<Point4f>();
	List <Point4f> textures = new LinkedList<Point4f>();
	List <Polygon> polygons = new LinkedList<Polygon>();
	
	
	void initializeModel(){
		ReadObj obj = new ReadObj();
		obj.getModelObj();
		vertex = obj.vertex;
		normals = obj.normals;
		textures = obj.textures;
		polygons = obj.polygons;
	}
	
	public ModelCharacterObj(float x, float y, float z){
		//initialize position of character
		position = new float[] {x, y, z};
	}
	
	
	public float [] getPosition(){
		return position;
	}
	// increment the camera's current yaw rotation
	public void yaw(float amount) {
		// increment the yaw by the amount param
		yaw += amount;
	}
	
	public float getJaw() {
		return yaw;
	}

	// increment the camera's current yaw rotation
	public void pitch(float amount) {
		// increment the pitch by the amount param
		pitch -= amount;
		if(pitch > 45)
			pitch = 45;
		else if(pitch < -45)
			pitch = -45;
	}
	
	public float getPitch(){
		return pitch;
	}

	// moves the camera forward relative to its current rotation (yaw)
	public void walkForward(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		position[0] += distance * (float) Math.sin(Math.toRadians(yaw));
		position[2] -= distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw - 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw - 90));
	}

	// strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw + 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw + 90));
	}
	
	// translates and rotate the matrix so that it looks through the camera
	// this dose basic what gluLookAt() does
	public void lookThrough() {
		// roatate the pitch around the X axis
		GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		// roatate the yaw around the Y axis
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		// translate to the position vector's location
		GL11.glTranslatef(position[0], position[1], position[2]);
	}
	
	public void cameraFollowCharacter(){
		float camX, camY, camZ;
		float [] p = getPosition();
		camX = p[0];
		camY = p[1];
		camZ = p[2]+5;
		GL11.glTranslatef(-camX, -camY, -camZ);
	}
	
	public void checkBounds(float x, float z, int distanceView) {
		distanceView++;
		if(position[0] > (x/2 - distanceView) - 1)
			position[0] = (x/2 - distanceView) - 1; 
		if(position[0] < -(x/2 - distanceView))
			position[0] = -(x/2 - distanceView);
		if(position[2] > (z/2 - distanceView) - 1)
			position[2] = (z/2 - distanceView) - 1;
		if(position[2] < -(z/2 - distanceView))
			position[2] = -(z/2 - distanceView);
	}
	
	public void calcY(float[] p1, float[] p2, float[] p3, int MAP_X, int MAP_Z) {	
		float det = (p2[2] - p3[2]) * (p1[0] - p3[0]) + (p3[0] - p2[0]) * (p1[2] - p3[2]);
		
		float l1 = ((p2[2] - p3[2]) * (-position[0] - p3[0]) + (p3[0] - p2[0]) * (-position[2] - p3[2])) / det;
		float l2 = ((p3[2] - p1[2]) * (-position[0] - p3[0]) + (p1[0] - p3[0]) * (-position[2] - p3[2])) / det;
		float l3 = 1.0f - l1 - l2;
		
		position[1] = -(l1 * p1[1] + l2 * p2[1] + l3 * p3[1]);
		position[1] -= 5.0f;
	}
	
	@Override
	public void render3D() {
		float [] p = getPosition();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// save current matrix
		GL11.glPushMatrix();

		// TRANSLATE
		GL11.glTranslatef(p[0], p[1], p[2]);
		
		// ROTATE and SCALE
		GL11.glTranslatef(-p[0],-p[1] ,-p[2]);
		if (m_rZ != 0)
			GL11.glRotatef(m_rZ, 0, 0, 1);
		if (m_rY != 0)
			GL11.glRotatef(m_rY, 0, 1, 0);
		if (m_rX != 0)
			GL11.glRotatef(m_rX, 1, 0, 0);
		if (m_sX != 1 || m_sY != 1 || m_sZ != 1)
			GL11.glScalef(m_sX, m_sY, m_sZ);
		GL11.glTranslatef(p[0], p[1], p[2]);
		renderModel();

		// discard current matrix
		GL11.glPopMatrix();
		
	}
	void renderModel(){
//		initializeModel();
		
		GL11.glBegin(GL11.GL_TRIANGLES); // draw triangels 
		for (int i = 0; i < polygons.size(); i++)
		{
			Polygon polygon = polygons.get(i);
			GL11.glTexCoord2f(textures.get(polygon.a.itexture).getX(), textures.get(polygon.a.itexture).getY());
			GL11.glNormal3f(normals.get(polygon.a.inormal).getX(), normals.get(polygon.a.inormal).getY(), normals.get(polygon.a.inormal).getZ());
			GL11.glVertex3f(vertex.get(polygon.a.ivertex).getX(), vertex.get(polygon.a.ivertex).getY(), vertex.get(polygon.a.ivertex).getZ());
			
			GL11.glTexCoord2f(textures.get(polygon.b.itexture).getX(), textures.get(polygon.b.itexture).getY());
			GL11.glNormal3f(normals.get(polygon.b.inormal).getX(), normals.get(polygon.b.inormal).getY(), normals.get(polygon.b.inormal).getZ());
			GL11.glVertex3f(vertex.get(polygon.b.ivertex).getX(), vertex.get(polygon.b.ivertex).getY(), vertex.get(polygon.b.ivertex).getZ());
			
			GL11.glTexCoord2f(textures.get(polygon.c.itexture).getX(), textures.get(polygon.c.itexture).getY());
			GL11.glNormal3f(normals.get(polygon.c.inormal).getX(), normals.get(polygon.c.inormal).getY(), normals.get(polygon.c.inormal).getZ());
			GL11.glVertex3f(vertex.get(polygon.c.ivertex).getX(), vertex.get(polygon.c.ivertex).getY(), vertex.get(polygon.c.ivertex).getZ());
			
		}  
	    GL11.glEnd();
	}
}