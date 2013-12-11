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
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point4f;

import org.lwjgl.opengl.GL11;


public class ObjectGrave extends Model3D {
	//arrays for drawing character
	float[] vertices;
	float[] normals;
	float[] textures;
	
	  private static int BYTE_SIZE = 4;
	  private static int VERTEX_DIMENSION = 3;
	  private static int TEXTURE_DIMENSION = 2;
	
	  private int verticesAndNormalsAndTextureBufferID;
	  private FloatBuffer verticesAndNormalsAndTextureBuffer;
	
	public ObjectGrave(float x, float y, float z, Terrain t){
		float [][] triangle = t.getTrinagleLocation(x, z);
		float max12 = Math.max(triangle[0][1], triangle[1][1]);
		float maxY  = Math.max(triangle[2][1], max12);
	    m_nX = x;
	    m_nY = maxY + 0.3f;
		m_nZ = z;
	}
	void initializeModel(){
		ReadObj obj = new ReadObj();
		obj.getModelObj("Tombstone_RIP_obj.obj");
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
	}
	
//	public float getDistance(float x, float y) {
//		float dx = m_nX-x;
//		float dy = m_nZ-y;
//		float d = (float)Math.sqrt(Math.pow(dx,2)+ Math.pow(dy,2));
//		
//		return d;
//	}
//	
//	public boolean checkObjCollision(float x, float y, float yaw, boolean isPlayer) {
//		if(!this.isPlayer && !isMoving)
//			return false;
//
//		float d = getDistance(x, y);
//		if(d < 1.7f) {
//			if(!this.isPlayer && collisionDeltaTime == 0)
//				collisionDeltaTime = 1.0f;
//			float boundDist = 1.7f-d;
//			m_nX -= boundDist * (float) Math.sin(Math.toRadians(this.yaw+movingAngle));
//			m_nZ -= boundDist * (float) Math.cos(Math.toRadians(this.yaw+movingAngle));
//
//			if(!isPlayer && !this.isPlayer) 
//				yaw(Refactored.getRandomNumber(-180,180));
//			return true;
//		}
//		return false;
//	}
//	
//	boolean intersects(float cX, float cY, float cR, float rX, float rY, float rW, float rH)
//	{
//	    float circleDistanceX = Math.abs(cX - rX);
//	    float circleDistanceY =Math. abs(cY - rY);
//
//	    if (circleDistanceX > (rW/2 + cR)) { return false; }
//	    if (circleDistanceY > (rH/2 + cR)) { return false; }
//
//	    if (circleDistanceX <= (rW/2)) { return true; } 
//	    if (circleDistanceY <= (rH/2)) { return true; }
//
//	    float cornerDistance_sq = (float)(Math.pow((circleDistanceX - rW/2) ,2) +
//	    		Math.pow((circleDistanceY - rH/2),2));
//
//	    if(cornerDistance_sq <= Math.pow(cR,2)) {
//			m_nX -= cornerDistance_sq * (float) Math.sin(Math.toRadians(this.yaw));
//			m_nZ -= cornerDistance_sq * (float) Math.cos(Math.toRadians(this.yaw));
//			return true;
//	    }
//	    return false;
//	}
	
	public void render3D() {
		// model view stack
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// save current matrix
		GL11.glPushMatrix();

		// TRANSLATE
		
		// ROTATE and SCALE
		GL11.glTranslatef(m_nX, m_nY, m_nZ);
		if (m_rZ != 0)
			GL11.glRotatef(m_rZ, 0, 0, 1);
		if (m_rY != 0)
			GL11.glRotatef(m_rY, 0, 1, 0);
		if (m_rX != 0)
			GL11.glRotatef(m_rX, 1, 0, 0);
			GL11.glScalef(m_sX, m_sY, m_sZ);
		GL11.glTranslatef(0, 0, 0);

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
