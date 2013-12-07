import java.nio.IntBuffer;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Refactored extends BaseWindow {

	float posX = 0, posY = 3, posZ = 0, rotX = 0, rotY = 0, rotZ = 0,
			scale = 1;
	float camPosX = 0.0f, camPosY = 7.0f, camPosZ = 10.0f, camRotX = 0.0f,
			camRotY = 0, camRotZ = 0;
	float lean = 0.0f;
	boolean leanL = false, leanR = false;
	int distanceView = 60;
	float angleView = 90;
	int backDistanceView = -4;

	float dx = 0.0f;
	float dy = 0.0f;
	float dt = 0.0f; // length of frame
	float lastTime = 0.0f; // when the last frame was
	float time = 0.0f;

	float mouseSensitivity = 0.05f;
	float movementSpeed = 8.0f; // move 10 units per second
	float rotateSpeed = 50.0f; // move mili degree per second

	
	float scaleChar = 1;//0.025f;
	float scaleTree = 1f;
	float scaleGrave = 0.3f;
	float scaleAxe = 0.02f;
	FPCameraController camera;

	Terrain t;
	ModelCharacterObj MCO;
	ModelCharacterObj MCO2;
	IntBuffer m_Textures;
	StatusBar SB;
	ObjectTree OT;
	ObjectGrave OG;
	ObjectGrave OG1;
	ObjectGrave OG2;
	WeaponAxeObj AO;
	Text text;

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
		GLU.gluPerspective(45, 1024 / (float) 768, 1.0f, 60.0f);

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
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2); // GL_LINEAR
		GL11.glFogf(GL11.GL_FOG_DENSITY, 0.04f); // 0.5f

//		GL11.glFogf(GL11.GL_FOG_START, 20.0f); // Fog Start Depth
//		GL11.glFogf(GL11.GL_FOG_END, 50.0f); // Fog End Depth

		// textures
		// enable 2D textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// select modulate to mix texture with color for shading; GL_REPLACE,
		// GL_MODULATE ...
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
				GL11.GL_MODULATE);

		// GL11.glEnable(GL11.GL_NORMALIZE);

		// background color
		GL11.glClearColor(0.8f, 0.8f, 0.8f, 0.0f);
	}

	protected void setCameraMatrix() {
		camera = new FPCameraController(camPosX, camPosY, camPosZ);

		// hide the mouse
		Mouse.setGrabbed(true);
	}
	/**
	 * can be used for 3D model initialization
	 */
	protected void initializeModels() {
		t = new Terrain();
		t.initialize();
		
		MCO = new ModelCharacterObj(posX,posY,posZ);
		MCO2 = new ModelCharacterObj(posX+10,posY,posZ-10);
		
		SB = new StatusBar();
		OT = new ObjectTree();
		OG = new ObjectGrave(3,posY,10,t);
		OG1 = new ObjectGrave(-3,posY,10,t);
		OG2 = new ObjectGrave(0,posY,20,t);
		AO = new WeaponAxeObj();
		text = new Text("Health",50);
		
		
		OT.initializeModel();
		OG.initializeModel();
		OG1.initializeModel();
		OG2.initializeModel();
		AO.initializeModel();
		MCO.initializeModel();
		MCO2.initializeModel();
		m_Textures = Texture.loadTextures2D(new String[] { "grass20_128.png", "grave.jpg", "font.png", "ColorMap_128.png" });
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
		t.setPosition(-(t.MAP_X / 2 - 1), 0, -(t.MAP_Z / 2 - 1));
		t.setRotation(rotX, rotY, rotZ);
		t.setScaling(scale, scale, scale);
		//float [] p = MCO.getPosition();
		//float [] cam = camera.getPosition();
		OT.setPosition(0, 5, 5);
		OT.setScaling(scaleTree, scaleTree, scaleTree);
		
		OG.setScaling(scaleGrave, scaleGrave, scaleGrave);
		OG1.setScaling(scaleGrave, scaleGrave, scaleGrave);
		OG2.setScaling(scaleGrave, scaleGrave, scaleGrave);
		
		AO.setPosition(0, 4, 1);
		AO.setScaling(scaleAxe, scaleAxe, scaleAxe);
		AO.setRotation(90, -90, 0);
		text.setPosition(0, 5, 1);
		text.setScaling(1f, 1f, 1f);
		text.render3D();
		//text.setScaling(5, 5, 5);
		SB.setPosition(2, 5, 4);
		//MCO.setPosition(-cam[0],-cam[1]-5,-cam[2]-5);
		//MCO.setRotation(rotX,MCO.getJaw(), rotZ);
		MCO.setScaling(scaleChar, scaleChar, scaleChar);
		MCO2.setScaling(scaleChar, scaleChar, scaleChar);
		//MCO.cameraFollowCharacter();
		SB.render3D();
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE,
				allocFloats(new float[] { 1.0f, 1.0f, 0.5f, 0.8f }));

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_Textures.get(0));
		t.render3D();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_Textures.get(3));
		MCO.render3D();
		MCO2.render3D();
		//OT.render3D();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_Textures.get(1));
//		OG.render3D();
//		OG1.render3D();
//		OG2.render3D();
//		AO.render3D();
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
		// object rotate
		if (Keyboard.isKeyDown(Keyboard.KEY_Q))// move forward
		{
			camera.yaw(movementSpeed * -dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E))// move backwards
		{
			camera.yaw(movementSpeed * dt);
		}
		//camera.yaw(dx * mouseSensitivity);
		//MCO.yaw(dx * mouseSensitivity);
		MCO.yaw(mouseSensitivity * -dx);
		
		
		// controll camera pitch from y movement fromt the mouse
		camera.pitch(dy * mouseSensitivity);
		//MCO.pitch(dy * mouseSensitivity);
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
		// object rotate
		if (Keyboard.isKeyDown(Keyboard.KEY_U))// move forward
		{
			//MCO.yaw(rotateSpeed * dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_O))// move backwards
		{
			//MCO.yaw(rotateSpeed * -dt);
		}
        // OBJECT move
		if (Keyboard.isKeyDown(Keyboard.KEY_K))// move forward
		{
			MCO.walkBackwards(movementSpeed * dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_I))// move backwards
		{
			MCO.walkForward(movementSpeed * dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_L))// strafe left
		{
			MCO.strafeRight(movementSpeed * dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_J))// strafe right
		{
			MCO.strafeLeft(movementSpeed * dt);
		}

		
		float[] objPos = MCO.getPosition();
		float[][] triangle = t
				.getTrinagleLocation(objPos[0], objPos[2]);
		MCO.calcY(triangle[0], triangle[1], triangle[2]);
        MCO.checkBounds(t.MAP_X * t.MAP_SCALE, t.MAP_Z * t.MAP_SCALE, distanceView);
    	camera.CamOnObjPossition(MCO.getPosition(), MCO.getJaw());
		//camera.checkBounds(t.MAP_X * t.MAP_SCALE, t.MAP_Z * t.MAP_SCALE, distanceView);
		/*float[] charPos = MCO.getPosition();
		float[][] triangle = t
				.getTrinagleLocation(-charPos[0], -charPos[2]);
		MCO.calcY(triangle[0], triangle[1], triangle[2], t.MAP_X, t.MAP_Z);*/
		
		
		/*t.setVisibleArea((int) -charPos[0], (int) -charPos[2],
				distanceView, angleView, MCO.getJaw(), backDistanceView);*/
    	
		float[] cameraPos = camera.getPosition();
		triangle = t.getTrinagleLocation(-cameraPos[0], -cameraPos[2]);
		camera.calcY(triangle[0], triangle[1], triangle[2], t.MAP_X, t.MAP_Z);
		
        t.setVisibleArea((int) -cameraPos[0], (int) -cameraPos[2],
				distanceView, angleView, camera.getJaw(), backDistanceView);
		
		// set the modelview matrix back to the identity
		GL11.glLoadIdentity();

		//MCO.lookThrough();
		// look through the camera before you draw anything
		//float[] objPos = MCO.getPosition();
			//GLU.gluLookAt(objPos[0], objPos[1], objPos[2], 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);		
		camera.lookThrough();
		super.processInput();
	}

	public static void main(String[] args) {
		(new Refactored()).execute();
	}
}
