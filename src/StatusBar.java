import org.lwjgl.opengl.GL11;


public class StatusBar extends Model3D{
	int bars = 5;
	
	public StatusBar(){
	}
	
	@Override
	  public void render3D()
	  {
	    // model view stack 
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    
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

	    renderModel();
	    
	    // discard current matrix
	    GL11.glPopMatrix();
	  }
	void renderModel(){
		
		GL11.glBegin(GL11.GL_QUAD_STRIP); // draw triangels
		GL11.glColor3f(1, 0, 0);
		for (int i = 0; i < bars; i++){
			float dx = i * 0.3f;
			GL11.glVertex2f(1f+dx, 1f);
			GL11.glVertex2f(2f+dx, 1f);
			GL11.glVertex2f(1f+dx, 2f);
			GL11.glVertex2f(2f+dx, 2f);
		}
	    GL11.glEnd();
	}
}