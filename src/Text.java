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

	  private void renderModel()
	  {
		  GL11.glColor3f(1, 0, 0);
		  GL11.glBegin(GL11.GL_QUADS);
		    for (int i = 0; i < len; i++) {
		      charCode = text.getCode(contentText.charAt(i));
//		      GL11.glColor4f(1, 0.0f + (float)i/(float)len, 0, 1f);
		      GL11.glTexCoord2f(dx+charCode[1]*fw, (charCode[0]+1)*fh);
		      GL11.glVertex3f(charPos[0], charPos[1], 1);
		      GL11.glTexCoord2f(dx+(charCode[1]+1)*fw, (charCode[0]+1)*fh);  
		      GL11.glVertex3f(charPos[0]+ff*fontSize, charPos[1], 1);
		      GL11.glTexCoord2f(dx+(charCode[1]+1)*fw, charCode[0]*fh);      
		      GL11.glVertex3f(charPos[0]+ff*fontSize, charPos[1]+fontSize, 1);
		      GL11.glTexCoord2f(dx+charCode[1]*fw, charCode[0]*fh);          
		      GL11.glVertex3f(charPos[0], charPos[1]+fontSize, 1);
		      charPos[0]+=ff*fontSize;
		    }
		  GL11.glEnd();
	  }
}
