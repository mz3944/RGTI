import org.lwjgl.opengl.GL11;

public class StatusBar extends Model3D {
	public int bars = 100;

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

	public void render3D() {
		startHUD();
		renderModel();
		endHUD();
	}

	void renderModel() {
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		GL11.glColor3f(1, 0, 0);
		GL11.glVertex2f(20f, 60f);
		GL11.glVertex2f(20f, 20f);
		GL11.glVertex2f(20f + bars*2, 60f);
		GL11.glVertex2f(20f + bars*2, 20f);
		GL11.glEnd();
	}
}