import static org.lwjgl.opengl.ARBBufferObject.GL_STATIC_DRAW_ARB;
import static org.lwjgl.opengl.ARBBufferObject.glBindBufferARB;
import static org.lwjgl.opengl.ARBBufferObject.glBufferDataARB;
import static org.lwjgl.opengl.ARBBufferObject.glGenBuffersARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;


public class StatusBar extends Model3D{
	int bars = 4;
	
	public StatusBar(){
	}
	
	
	  protected void startHUD() {
		    GL11.glMatrixMode(GL11.GL_PROJECTION);
		    GL11.glPushMatrix();
		    GL11.glLoadIdentity();
		    GL11.glOrtho(0, 1024, 0, 768, -1, 1);
		    //GL11.glTranslatef(x, y, z);
		    GL11.glMatrixMode(GL11.GL_MODELVIEW);
		    GL11.glPushMatrix();
		    GL11.glLoadIdentity();
		  }
		  
		  protected void endHUD() {
		    GL11.glMatrixMode(GL11.GL_PROJECTION);
		    GL11.glPopMatrix();
		    GL11.glMatrixMode(GL11.GL_MODELVIEW);
		    GL11.glPopMatrix();
		  }
	@Override
	  public void render3D()
	  {
	    // model view stack 
	   /* GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    
	    // save current matrix
	    GL11.glPushMatrix();

	    // TRANSLATE 
	    GL11.glTranslatef(m_nX, m_nY, m_nZ);

	    // ROTATE and SCALE
	    GL11.glTranslatef(0, 0, -3.5f);
	    if (m_rZ!=0)
	      GL11.glRotatef(m_rZ, 0, 0, 1);
	    if (m_rY!=0)
	      GL11.glRotatef(m_rY, 0, 1, 0);
	    if (m_rX!=0)
	      GL11.glRotatef(m_rX, 1, 0, 0);
	    if (m_sX!=1 || m_sY!=1 || m_sZ!=1)
	      GL11.glScalef(m_sX, m_sY, m_sZ);
	    GL11.glTranslatef(0, 0, 3.5f);    
	    */
		startHUD();
	    renderModel();
	    endHUD();
	    // discard current matrix
	  }
	void renderModel(){
		GL11.glBegin(GL11.GL_QUAD_STRIP); // draw triangels
		GL11.glColor3f(1, 0, 0);
		for (int i = 0; i < bars; i++){
			float dx = i * 25f;
			GL11.glVertex2f(25f+dx, 25);
			GL11.glVertex2f(50f+dx, 25f);
			GL11.glVertex2f(25f+dx, 50f);
			GL11.glVertex2f(50f+dx, 50f);
		}
	    GL11.glEnd();
	}
}
