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
		    if (contentText != "Health")
		    	GL11.glTranslatef(870, 720, 0);
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

	  public void render3D()
	  {
		startHUD();
	    renderModel();
	    endHUD();
	  }

	  private void renderModel()
	  {
		  GL11.glColor3f(1, 0, 0);
		  text.renderString(contentText, fontSize);
	  }
}
