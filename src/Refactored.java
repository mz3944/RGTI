import java.nio.IntBuffer;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Refactored extends BaseWindow {

	float posX = 0, posY = 0, posZ = 0, rotX = 0, rotY = 0, rotZ = 0,
			scale = 1;
	float camPosX = 0, camPosY = 0, camPosZ = -100.0f, camRotX = 90.0f,
			camRotY = 0, camRotZ = 0;
	float lean = 0.0f;
	boolean leanL = false, leanR = false;

	float dx = 0.0f;
	float dy = 0.0f;
	float dt = 0.0f; // length of frame
	float lastTime = 0.0f; // when the last frame was
	float time = 0.0f;

	float mouseSensitivity = 0.05f;
	float movementSpeed = 10.0f; // move 10 units per second

	FPCameraController camera;

	Terrain t;
	ModelCharacterObj MCO;
	IntBuffer m_Textures;

	int mouseX, mouseY, oldMouseX, oldMouseY;

	/**
	 * Initial setup of projection of the scene onto screen, lights etc.
	 */
	protected void setupView() {
		initializeModels();

		// enable depth buffer (off by default)
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// enable culling of back sides of polygons
		GL11.glEnable(GL11.GL_CULL_FACE);

		// mapping from normalized to window coordinates
		GL11.glViewport(0, 0, 1024, 768);

		// setup projection matrix stack
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45, 1024 / (float) 768, 1.0f, 130.0f);

		setCameraMatrix();

		// dodatki:

		// smooth shading - Gouraud
		GL11.glShadeModel(GL11.GL_SMOOTH);

		// lights
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);

		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, allocFloats(new float[] {
				0.2f, 0.2f, 0.2f, 0.0f }));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, allocFloats(new float[] {
				1.0f, 1.0f, 1.0f, 0.0f }));
		GL11.glLightf(GL11.GL_LIGHT0, GL11.GL_LINEAR_ATTENUATION, 7f);
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, allocFloats(new float[] {
				0f, 0f, 6.0f, 0f }));

		// fog
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFog(GL11.GL_FOG_COLOR, allocFloats(new float[] { 0.8f, 0.8f,
				0.8f, 0.0f }));
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
		GL11.glFogf(GL11.GL_FOG_DENSITY, 0.01f);

		// textures
		// enable 2D textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// select modulate to mix texture with color for shading; GL_REPLACE,
		// GL_MODULATE ...
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
				GL11.GL_MODULATE);

		// GL11.glEnable(GL11.GL_NORMALIZE);
	}

	protected void setCameraMatrix() {
		camera = new FPCameraController(0.0f, -10.0f, -100.0f);

		// hide the mouse
		Mouse.setGrabbed(true);

	}
	/**
	 * can be used for 3D model initialization
	 */
	protected void initializeModels() {
		t = new Terrain();
		MCO = new ModelCharacterObj();
		m_Textures = Texture.loadTextures2D(new String[] { "grass20_128.png" });
	}

	/**
	 * Resets the view of current frame
	 */
	protected void resetView() {
		// clear color and depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Renders current frame
	 */
	protected void renderFrame() {

		t.setPosition(-t.MAP_X / 2, 0, -t.MAP_Z / 2);
		t.setRotation(rotX, rotY, rotZ);
		t.setScaling(scale, scale, scale);

		MCO.setPosition(0,0,0);
		MCO.setRotation(rotX, rotY, rotZ);
		MCO.setScaling(scale, scale, scale);
		
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE,
				allocFloats(new float[] { 1.0f, 1.0f, 0.5f, 0.8f }));

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_Textures.get(0));
		MCO.render3D();
		t.render3D();

	}

	/**
	 * Processes Keyboard and Mouse input and spawns actions
	 */
	protected void processInput() {

		time = Sys.getTime();
		dt = (time - lastTime) / 1000.0f;
		lastTime = time;

		// distance in mouse movement from the last getDX() call.
		dx = Mouse.getDX();
		// distance in mouse movement from the last getDY() call.
		dy = Mouse.getDY();

		// controll camera yaw from x movement fromt the mouse
		camera.yaw(dx * mouseSensitivity);
		// controll camera pitch from y movement fromt the mouse
		camera.pitch(dy * mouseSensitivity);

		if (Keyboard.isKeyDown(Keyboard.KEY_W))// move forward
		{
			camera.walkForward(movementSpeed * dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S))// move backwards
		{
			camera.walkBackwards(movementSpeed * dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A))// strafe left
		{
			camera.strafeLeft(movementSpeed * dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D))// strafe right
		{
			camera.strafeRight(movementSpeed * dt);
		}

		// set the modelview matrix back to the identity
		GL11.glLoadIdentity();
		// look through the camera before you draw anything
		camera.lookThrough();

		super.processInput();
	}

	public static void main(String[] args) {
		(new Refactored()).execute();
	}
}
