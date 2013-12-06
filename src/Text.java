import org.lwjgl.opengl.GL11;


public class Text extends Model3D{
	  
	  BitmapText text;
	  String contentText = "";
	  int fontSize = 10;
	  float fw = 0.065f, dx = 0.010f, fh = 0.13f, ff = 0.65f;
	  int[] charPos = {0, 0};
	  int[] charCode = {0, 0};
	  int len = 0;
	  
	  public Text() {
	    super();
	    text = new BitmapText();
	  }
	  
	  public Text(String content, int size) {
	    super();
	    text = new BitmapText();
	    contentText = content;
	    len = contentText.length();
	    fontSize = size;
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
	    if (m_rZ!=0)
	      GL11.glRotatef(m_rZ, 0, 0, 1);
	    if (m_rY!=0)
	      GL11.glRotatef(m_rY, 0, 1, 0);
	    if (m_rX!=0)
	      GL11.glRotatef(m_rX, 1, 0, 0);
	    if (m_sX!=1 || m_sY!=1 || m_sZ!=1)
	      GL11.glScalef(m_sX, m_sY, m_sZ);
	    GL11.glTranslatef(0, 0, 0);    

	    renderModel();
	    
	    // discard current matrix
	    GL11.glPopMatrix();
	  }

	  private void renderModel()
	  {
		  GL11.glColor3f(1, 0, 0);
		  GL11.glBegin(GL11.GL_QUADS);
		    for (int i = 0; i < len; i++) {
		      charCode = text.getCode(contentText.charAt(i));
//		      GL11.glColor4f(1, 0.0f + (float)i/(float)len, 0, 1f);
		     // GL11.glTexCoord2f(dx+charCode[1]*fw, (charCode[0]+1)*fh);
		      GL11.glVertex3f(charPos[0], charPos[1], 1);
		     // GL11.glTexCoord2f(dx+(charCode[1]+1)*fw, (charCode[0]+1)*fh);  
		      GL11.glVertex3f(charPos[0]+ff*fontSize, charPos[1], 1);
		     // GL11.glTexCoord2f(dx+(charCode[1]+1)*fw, charCode[0]*fh);      
		      GL11.glVertex3f(charPos[0]+ff*fontSize, charPos[1]+fontSize, 1);
		     // GL11.glTexCoord2f(dx+charCode[1]*fw, charCode[0]*fh);          
		      GL11.glVertex3f(charPos[0], charPos[1]+fontSize, 1);
		      charPos[0]+=ff*fontSize;
		    }
		  GL11.glEnd();
	  }
}
