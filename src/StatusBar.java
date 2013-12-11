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
	int bars = 100;
	
	public StatusBar(){
	}
	
	
	  protected void startHUD() {
		    GL11.glMatrixMode(GL11.GL_PROJECTION);
		    GL11.glPushMatrix();
		    GL11.glLoadIdentity();
		    GL11.glOrtho(0, 1024, 0, 768, -1, 1);
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
		startHUD();
	    renderModel();
	    endHUD();
	  }
	void renderModel(){
		GL11.glBegin(GL11.GL_QUAD_STRIP); // draw triangels
		GL11.glColor3f(1, 0, 0);
		for (int i = 0; i < bars; i++){
			float dx = i * 1f;
			GL11.glVertex2f(20f+dx, 20f);
			GL11.glVertex2f(60f+dx, 20f);
			GL11.glVertex2f(20f+dx, 60f);
			GL11.glVertex2f(60f+dx, 60f);
		}
	    GL11.glEnd();
	}
}
