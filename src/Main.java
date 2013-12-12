import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;


public class Main extends BaseWindow {

	float playerPosX = 0, playerPosY = 3, playerPosZ = 0;
	float camPosX = 0.0f, camPosY = 0.0f, camPosZ = 0.0f, camRotX = 0.0f,
			camRotY = 0, camRotZ = 0;
	float terrRotX = 0, terrRotY = 0, terrRotZ = 0, terrScale = 1;
	int distanceView = 60; // how far player can see
	float angleView = 90; // what angle can player see
	int backDistanceView = -5; 

	float dx = 0.0f;
	float dy = 0.0f;
	float dt = 0.0f; // length of frame
	float lastTime = 0.0f; // when the last frame was
	float time = 0.0f;

	float mouseSensitivity = 0.05f;
	float movementSpeed = 8.0f;

	float scaleChar = 1;
	float scaleGrave = 10f;
	float charRad = 1.7f;
	float graveRad = 1.5f;
	FPCameraController camera;
	int enemyNumber = 300;
	int scoreI = 0;
	float secCounter = 0;
	int dmgCounter = 0;

	Terrain t;
	ModelCharacterObj player;
	ModelCharacterObj[] enemies;
	ObjectGrave[] OG;
	ModelRender m;

	IntBuffer m_Textures;
	StatusBar SB;
	Text health;
	Text score;

	boolean[] movingDirection = new boolean[4]; // forward, right, backward, left
	boolean startedMoving = false;
	float followPlayerRange = 100.0f; // radius, in which enemies follow player

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
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
		GL11.glFogf(GL11.GL_FOG_DENSITY, 0.04f);

		// textures
		// enable 2D textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		// select modulate to mix texture with color for shading; GL_REPLACE,
		// GL_MODULATE ...
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
				GL11.GL_MODULATE);

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
		t.setPosition(-(t.MAP_X / 2 - 1), 0, -(t.MAP_Z / 2 - 1));
		t.setRotation(terrRotX, terrRotY, terrRotZ);
		t.setScaling(terrScale, terrScale, terrScale);

		player = new ModelCharacterObj(playerPosX, playerPosY, playerPosZ);
		player.setScaling(scaleChar, scaleChar, scaleChar);
		player.setIsPlayer(true);
		
		enemies = new ModelCharacterObj[enemyNumber];

		OG = new ObjectGrave[enemyNumber];

		float xMap = t.MAP_X * t.MAP_SCALE;
		float zMap = t.MAP_Z * t.MAP_SCALE;
		float xMin = -(xMap / 2 - distanceView);
		float xMax = (xMap / 2 - distanceView) - 1;
		float zMin = -(zMap / 2 - distanceView);
		float zMax = (zMap / 2 - distanceView) - 1;
		for (int i = 0; i < enemyNumber; i++) {
			float x = getRandomNumber(xMin, xMax);
			float z = getRandomNumber(zMin, zMax);
			OG[i] = new ObjectGrave(x, 0, z, t);
			OG[i].setScaling(scaleGrave, scaleGrave, scaleGrave);
			enemies[i] = new ModelCharacterObj(x, 0, z);
			enemies[i].setScaling(scaleChar, scaleChar, scaleChar);
			float jawRot = 0;
			OG[i].setJaw(jawRot);
			enemies[i].objOnObjPossition(new float[] { x, 0, z }, jawRot, 3.0f);
			float[] objPos = enemies[i].getPosition();
			float[][] triangle = t.getTrinagleLocation(objPos[0], objPos[2]);
			enemies[i].calcY(triangle[0], triangle[1], triangle[2], t.MAP_X,
					t.MAP_Z);
			enemies[i].m_nY -= 8f;
			enemies[i].setJaw(getRandomNumber(-180, 180));
		}

		SB = new StatusBar();
		health = new Text("Health", 40);
		score = new Text("score" + scoreI, 20);

		m = new ModelRender();
		m.addModel(player, "basemesh_fuse.obj", "ColorMap_128.png");
		m.addModel(enemies, "basemesh_fuse.obj", "ColorMap_128.png");
		m.addModel(OG, "Tombstone_RIP_obj.obj", "grave.jpg");
		m.initializeModels();

		m_Textures = Texture.loadTextures2D(new String[] { "grass20_128.png",
				"font.png" });
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
		SB.render3D();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_Textures.get(1));
		score.contentText = "score: " + scoreI;
		score.render3D();

		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE,
				allocFloats(new float[] { 1.0f, 1.0f, 0.5f, 0.8f }));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_Textures.get(0));
		t.render3D();

		m.render3D();
	}

	/**
	 * Processes Keyboard and Mouse input and spawns actions
	 */

	protected void processInput() {

		time = Sys.getTime();
		dt = (time - lastTime) / 1000.0f;
		lastTime = time;

		secCounter += dt;

		// distance in mouse movement from the last getDX() call
		dx = Mouse.getDX();
		// distance in mouse movement from the last getDY() call
		dy = Mouse.getDY();

		// sets view
		int dWheel = Mouse.getDWheel();
		int scr = 0;
		if (dWheel < 0)
			scr = -1;
		else if (dWheel > 0)
			scr = 1;

		camera.setDist2obj(scr);

		// control camera yaw from y movement fromt the mouse
		player.yaw(mouseSensitivity * -dx);

		// control camera pitch from y movement fromt the mouse
		camera.pitch(dy * mouseSensitivity);

		// player move
		Arrays.fill(movingDirection, false);
		if (Keyboard.isKeyDown(Keyboard.KEY_W))// move forward
		{
			player.walkForward(movementSpeed * dt);
			movingDirection[0] = true;
			startedMoving = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S))// move backwards
		{
			player.walkBackwards(movementSpeed * dt);
			movingDirection[2] = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D))// move left
		{
			player.strafeRight(movementSpeed * dt);
			movingDirection[3] = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A))// move right
		{
			player.strafeLeft(movementSpeed * dt);
			movingDirection[1] = true;
		}

		float[] objPos = player.getPosition();
		float[][] triangle = t.getTrinagleLocation(objPos[0], objPos[2]);
		player.calcY(triangle[0], triangle[1], triangle[2], t.MAP_X, t.MAP_Z);
		player.checkBounds(t.MAP_X * t.MAP_SCALE, t.MAP_Z * t.MAP_SCALE,
				distanceView);
		camera.CamOnObjPossition(player.getPosition(), player.getJaw());
		player.setMovingDirection(movingDirection);

		for (int i = 0; i < m.enemyNumberRender; i++) {
			float[] objPos3 = enemies[i].getPosition();
			triangle = t.getTrinagleLocation(objPos3[0], objPos3[2]);
			enemies[i].calcY(triangle[0], triangle[1], triangle[2], t.MAP_X,
					t.MAP_Z);
			boolean overBounds = enemies[i].checkBounds(t.MAP_X * t.MAP_SCALE,
					t.MAP_Z * t.MAP_SCALE, distanceView);
			if (overBounds)
				enemies[i].setJaw(getRandomNumber(-180, 180));
			enemies[i].setIsMoving(true);
			enemies[i].updateWait(dt);

			// collision between enemies
			for (int j = 0; j < enemyNumber; j++) {
				if (j != i) {
					float[] tempObjPos = enemies[j].getPosition();
					enemies[i].checkObjCollision(tempObjPos[0], tempObjPos[2],
							enemies[j].getJaw(), charRad, false);
				}
			}
			for (int j = 0; j < enemyNumber; j++)
				enemies[i].checkObjCollision(OG[j].m_nX, OG[j].m_nZ, 0,
						graveRad, false);

			// collision between enemies and player
			float playerDistance = enemies[i].getDistance(objPos[0], objPos[2]);
			boolean playerContact = false;
			if (playerDistance <= followPlayerRange) {
				float[] directionVec = new float[2];
				directionVec[0] = (objPos[0]) - (objPos3[0]);
				directionVec[1] = (objPos[2]) - (objPos3[2]);

				float length = (float) Math.sqrt(directionVec[0]
						* directionVec[0] + directionVec[1] * directionVec[1]);
				if (length != 0) {
					directionVec[0] = directionVec[0] / length;
					directionVec[1] = directionVec[1] / length;
				}

				float newAngle = (float) Math.atan2(directionVec[1],
						directionVec[0]);

				newAngle = (float) Math.toDegrees(45 - (newAngle - 45));
				enemies[i].setJaw(newAngle);

				playerContact = enemies[i].checkObjCollision(objPos[0],
						objPos[2], player.getJaw(), charRad, true);
			}
			
			if (!playerContact && startedMoving)
				enemies[i].walkForward(movementSpeed * dt);
			
			if (playerContact) {
				dmgCounter++;
				if (dmgCounter >= 50) {
					SB.bars -= 1;
					dmgCounter = 0;
				}
			}
		}
		for (int i = 0; i < enemyNumber; i++)
			player.checkObjCollision(OG[i].m_nX, OG[i].m_nZ, 0, graveRad, false);

		if (secCounter >= 1.0f && startedMoving) {
			m.addEnemyRender();
			scoreI += 1;
			secCounter = 0;
		}

		if (SB.bars <= 0)
			Main.isRunning = false;

		float[] cameraPos = camera.getPosition();
		t.setVisibleArea((int) -cameraPos[0], (int) -cameraPos[2],
				distanceView, angleView, camera.getJaw(), backDistanceView);

		// set the modelview matrix back to the identity
		GL11.glLoadIdentity();

		camera.lookThrough();
		super.processInput();
	}

	public static float getRandomNumber(float min, float max) {
		float enemyAngle = min + (float) (Math.random() * ((max - min) + 1));
		return enemyAngle;
	}

	public static void main(String[] args) {
		(new Main()).execute();
	}
}