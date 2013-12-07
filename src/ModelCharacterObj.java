import static org.lwjgl.opengl.ARBBufferObject.GL_STATIC_DRAW_ARB;
import static org.lwjgl.opengl.ARBBufferObject.glBindBufferARB;
import static org.lwjgl.opengl.ARBBufferObject.glBufferDataARB;
import static org.lwjgl.opengl.ARBBufferObject.glGenBuffersARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;



public class ModelCharacterObj extends Model3D {
	
	// 3d vector to store the character's position in
	private float[] position = null;
	// the rotation around the Y axis of the camera
	private float yaw = 0.0f;
	// the rotation around the X axis of the camera
	private float pitch = 0.0f;
	
	private boolean isPlayer = false;
	private boolean isMoving = false; //positionChanged
	float movingAngle;
	
	//arrays for drawing character
	float[] vertices;
	float[] normals;
	float[] textures;
	
	  private static int BYTE_SIZE = 4;
	  private static int VERTEX_DIMENSION = 3;
	  private static int TEXTURE_DIMENSION = 2;
	
	  private int verticesAndNormalsAndTextureBufferID;
	  private FloatBuffer verticesAndNormalsAndTextureBuffer;
	
	
	void initializeModel(){	
		ReadObj obj = new ReadObj();
		obj.getModelObj("basemesh_fuse.obj");
		vertices = obj.verticesRearranged;
		normals = obj.normalsRearranged;
		textures = obj.texturesRearranged;
	    
	//  Generating buffers
	    verticesAndNormalsAndTextureBufferID = glGenBuffersARB();
	    
	//  Binding buffers
	    glBindBufferARB(GL_ARRAY_BUFFER_ARB, verticesAndNormalsAndTextureBufferID);
	    
	//  Creating buffers for the actual data													
	    verticesAndNormalsAndTextureBuffer = ByteBuffer.allocateDirect((vertices.length+normals.length+textures.length) * BYTE_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
	    verticesAndNormalsAndTextureBuffer.put(vertices);
	    verticesAndNormalsAndTextureBuffer.put(normals);
	    verticesAndNormalsAndTextureBuffer.put(textures);
	    verticesAndNormalsAndTextureBuffer.rewind();             

	//  Copying initialized buffers to memory (there is data in Buffers already)
	    glBufferDataARB(GL_ARRAY_BUFFER_ARB,verticesAndNormalsAndTextureBuffer,GL_STATIC_DRAW_ARB);
	    
	    //glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
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
	//	System.out.println("Obj yaw:" + Float.toString(yaw));
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
	
	public void setIsPlayer(boolean isPlayer) {
		this.isPlayer = isPlayer;
	}
	
	public void setMovingDirection(boolean[] movingDirection) {
		if(movingDirection[0] && movingDirection[1])
			movingAngle = 45;
		else if(movingDirection[1] && movingDirection[2])
			movingAngle = 135;
		else if(movingDirection[2] && movingDirection[3])
			movingAngle = -135;
		else if(movingDirection[3] && movingDirection[0])
			movingAngle = -45;
		else if(movingDirection[0])
			movingAngle = 0;
		else if(movingDirection[1])
			movingAngle = 90;
		else if(movingDirection[2])
			movingAngle = 180;
		else if(movingDirection[3])
			movingAngle = -90;
	}
	// moves the camera forward relative to its current rotation (yaw)
	public void walkForward(float distance) {
		position[0] += distance * (float) Math.sin(Math.toRadians(yaw));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw));
//		System.out.println("Forw obj X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
	}

	// moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		position[0] += distance * (float) Math.sin(Math.toRadians(yaw + 180));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw + 180));
	//	System.out.println("Back obj X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
	}

	// strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		position[0] += distance * (float) Math.sin(Math.toRadians(yaw + 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw + 90));
//		System.out.println("Left obj X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
	}

	// strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance) {
		position[0] += distance * (float) Math.sin(Math.toRadians(yaw - 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw - 90));
//		System.out.println("Righ obj X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
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
	
	public void calcY(float[] p1, float[] p2, float[] p3) {
		float max12 = Math.max(p1[1], p2[1]);
		float maxY  = Math.max(p3[1], max12);
	    position[1] = maxY + 0.3f;
	}
	
	public void checkObjCollision(float x, float y) {
		if(!isPlayer && !isMoving)
			return;
		
		float dx = position[0]-x;
		float dy = position[2]-y;
		float d = (float)Math.sqrt(Math.pow(dx,2)+ Math.pow(dy,2));
		System.out.println("asfsa  " + dx + " " + dy + " " + d);
		System.out.println(yaw);
		if(d < 1.7f) {
			float boundDist = 1.7f-d;
//			if(isPlayer) //to za use sicer, moram spremenit, torj dt ta if stauk stra, pa v refractor narest za usak object movingPosition!!!
//				yaw(movingAngle);
//			if(movingDirection[2])
//				boundDist = -boundDist; //poskrblenoce gremo u rikvrc(kaj pa u stran???) ---> mogu bi narest glede na kot !!!!!!!!
			position[0] -= boundDist * (float) Math.sin(Math.toRadians(yaw+movingAngle));
			position[2] -= boundDist * (float) Math.cos(Math.toRadians(yaw+movingAngle));
			if(!isPlayer)
				yaw(180);
		}
	}
	
	// translates and rotate the matrix so that it looks through the camera
	// this dose basic what gluLookAt() does
	public void lookThrough() {
		// roatate the pitch around the X axis
		GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		// roatate the yaw around the Y axis
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		// translate to the position vector's location
	//	GL11.glTranslatef(position[0], position[1], position[2]);
	}

	@Override
	public void render3D() {
	//	float [] p = getPosition();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// save current matrix
		GL11.glPushMatrix();

		//NOTE!!!!!!  
		// TRANSLATE
		GL11.glTranslatef(position[0], position[1], position[2]);
		
		// ROTATE and SCALE
		GL11.glScalef(m_sX, m_sY, m_sZ);
		GL11.glRotatef(yaw, 0, 1, 0);
//		GL11.glTranslatef(0.0f, 0.0f , 0.0f);
  	    renderModel();
		// discard current matrix
		GL11.glPopMatrix();
		
	}
	void renderModel(){
		//  Set the pointers to vertices, normals and textures
	    glEnableClientState(GL_VERTEX_ARRAY);
	    glVertexPointer(VERTEX_DIMENSION, GL_FLOAT, 0, 0);
	    glEnableClientState(GL11.GL_NORMAL_ARRAY);
	    GL11.glNormalPointer(GL_FLOAT, 0, (vertices.length * BYTE_SIZE)); 
	    glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);	
	    GL11.glTexCoordPointer(TEXTURE_DIMENSION, GL_FLOAT, 0, ((vertices.length+normals.length) * BYTE_SIZE));
	    
	    //  drawing triangles
	    GL11.glDrawArrays(GL_TRIANGLES, 0, vertices.length/3);
	    
	    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
	    GL11.glDisableClientState(GL_VERTEX_ARRAY);
	}
}