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
//	private float[] position = null;
	// the rotation around the Y axis of the camera

	
	private boolean isPlayer = false;
	private boolean isMoving = false; //positionChanged
	float movingAngle;
	float health = 100;
	float wait = 0;
	
	//arrays for drawing character
	float[] vertices;
	float[] normals;
	float[] textures;
	float[] centerOffset;
	float collisionDeltaTime = 0; //for enemies only
	
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
		centerOffset = obj.centerOffset;
	    
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
		setPosition(x, y, z);
	}

	
	public void setJaw(float yaw) {
		if(wait == 0 && collisionDeltaTime == 0)
			this.yaw = yaw;
	}

	// increment the camera's current yaw rotation
	public void pitch(float amount) {
		// increment the pitch by the amount param
		super.pitch(amount);
		if(pitch > 45)
			pitch = 45;
		else if(pitch < -45)
			pitch = -45;
	}
	
	public boolean getIsPlayer() {
		return isPlayer;
	}
	public void setIsPlayer(boolean isPlayer) {
		this.isPlayer = isPlayer;
	}
	
	public void setIsMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}
	
	public boolean damage(int amount) {
		health -= 0.01*amount;
		if(health < 0) 
			health = 0;
		
		if(isPlayer) {
			if(health == 0) {
				System.out.println("You died!");
				return true;
			}
//			else
//				System.out.println("Health: " + health);
		}
		
		return false;
	}
	
	public void updateWait(float dt) {
		collisionDeltaTime -= dt;
		if(collisionDeltaTime < 0)
			collisionDeltaTime = 0;
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
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw + 180));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw + 180));
	}

	// strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw + 90));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw + 90));
	}

	// strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw - 90));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw - 90));
	}
	
	public void cameraFollowCharacter(){
		float camX, camY, camZ;
		float [] p = getPosition();
		camX = p[0];
		camY = p[1];
		camZ = p[2]+5;
		GL11.glTranslatef(-camX, -camY, -camZ);
	}
	
	public boolean checkBounds(float x, float z, int distanceView) {
		boolean overBounds = false;
		if(m_nX > (x/2 - distanceView) - 1) {
			m_nX = (x/2 - distanceView) - 1; 
			overBounds = true;
		}
		if(m_nX < -(x/2 - distanceView)) {
			m_nX = -(x/2 - distanceView);
			overBounds = true;
		}
		if(m_nZ > (z/2 - distanceView) - 1) {
			m_nZ = (z/2 - distanceView) - 1;
			overBounds = true;
		}
		if(m_nZ < -(z/2 - distanceView)) {
			m_nZ = -(z/2 - distanceView);
			overBounds = true;
		}
		
		return overBounds;
	}
	
	public void calcY(float[] p1, float[] p2, float[] p3, int MAP_X, int MAP_Z) {	
		float det = (p2[2] - p3[2]) * (p1[0] - p3[0]) + (p3[0] - p2[0]) * (p1[2] - p3[2]);
		
		float l1 = ((p2[2] - p3[2]) * (m_nX - p3[0]) + (p3[0] - p2[0]) * (m_nZ - p3[2])) / det;
		float l2 = ((p3[2] - p1[2]) * (m_nX - p3[0]) + (p1[0] - p3[0]) * (m_nZ - p3[2])) / det;
		float l3 = 1.0f - l1 - l2;
		
		m_nY = (l1 * p1[1] + l2 * p2[1] + l3 * p3[1]) + 0.3f;
	}
	
	public float getDistance(float x, float y) {
		float dx = m_nX-x;
		float dy = m_nZ-y;
		float d = (float)Math.sqrt(Math.pow(dx,2)+ Math.pow(dy,2));
		
		return d;
	}
	
	public boolean checkObjCollision(float x, float y, float yaw, float radius, boolean isPlayer) {
		if(!this.isPlayer && !isMoving)
			return false;

		float d = getDistance(x, y);
		if(d < radius) {
			if(!this.isPlayer && collisionDeltaTime == 0)
				collisionDeltaTime = 1.0f;
			float boundDist = radius-d;
			m_nX -= boundDist * (float) Math.sin(Math.toRadians(this.yaw+movingAngle));
			m_nZ -= boundDist * (float) Math.cos(Math.toRadians(this.yaw+movingAngle));

			if(!isPlayer && !this.isPlayer) 
				yaw(Refactored.getRandomNumber(-180,180));
			return true;
		}
		return false;
	}
	
	public boolean checkGraveCollision(float x, float z) {	System.out.println(x + " " + z + " " + m_nX + " " + m_nZ);
		float w = 3f;
		float d = 1f;
		float x1 = (x-w/2);
		float y1 = (z-d/2);
		float x2 = (x+w/2);
		float y2 = y1;
		float x3 = x2;
		float y3 = (z+d/2);
		float x4 = x1;
		float y4 = y3;
		
		if(m_nX >= x1 && m_nX <= x2 && m_nZ <= y3 && m_nZ >= y1) {System.out.println("SAD");
			float dist1 = getDistance(x1, y1);
			float dist2 = getDistance(x2, y2);
			float dist3 = getDistance(x3, y3);
			float dist4 = getDistance(x4, y4);
			
//			float distMin = Math.max(Math.max(Math.max(dist1, dist2), dist3), dist4);
			
			
			float min1 = Math.min(dist1, dist2);
			float min2 = Math.max(dist1, dist2);
			if(dist3 < min1)
				min1 = dist3;
			else if(dist3 < min2)
				min2 = dist3;
			if(dist4 < min1)
				min1 = dist4;
			else if(dist4 < min2)
				min2 = dist4;
			System.out.println(dist1 + " " + dist2 + " " + dist3 + " "+ dist4 + "    " + min1 +" " + min2);
			
//			if(distMin == dist1 || distMin == dist3)
//				m_nX = x1;
//			else
//				m_nX = x2;
//			if(distMin == dist1 || distMin == dist2)
//				m_nZ = y1;
//			else
//				m_nZ = y3;
			
			if((min1 == dist1 && min2 == dist2) || (min1 == dist2 && min2 == dist1)){
				m_nZ = y1;
				System.out.println("1");
			}
			else if((min1 == dist3 && min2 == dist4) || (min1 == dist4 && min2 == dist3)){
				m_nZ = y3;System.out.println("2");
				
			}
			else if((min1 == dist1 && min2 == dist3) || (min1 == dist3 && min2 == dist1)){
				m_nX = x1;System.out.println("3");
				
			}
			else{
				m_nX = x2;
				System.out.println("4");
			}
			
			return true;
		}

		return false;
	}
	
	public void CamOnObjPossition(float[] objposition, float objyaw) {		
		yaw   = 180-objyaw;
		float dist2obj = 3f;
		m_nX = m_nX + dist2obj * (float) Math.sin(Math.toRadians(yaw));
		m_nZ = m_nZ - dist2obj * (float) Math.cos(Math.toRadians(yaw));
        
//		m_nY = -(m_nY + lookAbove);
	}
	
	boolean intersects(float cX, float cY, float cR, float rX, float rY, float rW, float rH)
	{
	    float circleDistanceX = Math.abs(cX - rX);
	    float circleDistanceY =Math. abs(cY - rY);

	    if (circleDistanceX > (rW/2 + cR)) { return false; }
	    if (circleDistanceY > (rH/2 + cR)) { return false; }

	    if (circleDistanceX <= (rW/2)) { return true; } 
	    if (circleDistanceY <= (rH/2)) { return true; }

	    float cornerDistance_sq = (float)(Math.pow((circleDistanceX - rW/2) ,2) +
	    		Math.pow((circleDistanceY - rH/2),2));

	    if(cornerDistance_sq <= Math.pow(cR,2)) {
			m_nX -= cornerDistance_sq * (float) Math.sin(Math.toRadians(this.yaw));
			m_nZ -= cornerDistance_sq * (float) Math.cos(Math.toRadians(this.yaw));
			return true;
	    }
	    return false;
	}
	
	// translates and rotate the matrix so that it looks through the camera
	// this dose basic what gluLookAt() does
	public void lookThrough() {
		// roatate the pitch around the X axis
		GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		// roatate the yaw around the Y axis
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		// translate to the position vector's location
	}

	@Override
	public void render3D() {
	//	float [] p = getPosition();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// save current matrix
		GL11.glPushMatrix();

		//NOTE!!!!!!  
		// TRANSLATE
		GL11.glTranslatef(m_nX, m_nY, m_nZ);
		
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