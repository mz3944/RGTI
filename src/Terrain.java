import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Terrain extends Model3D {
	int MAP_X;
	int MAP_Z;
	float MAP_SCALE = 1.0f; // the scale of the terrain map
	// Terrain Data
	float[][][] terrain; // heightfield terrain data (0-255)

	float angle = 0.0f; // camera angle
	float radians = 0.0f; // camera angle in radians

	// Mouse/Camera Variables
	int mouseX, mouseY; // mouse coordinates
	float cameraX, cameraY, cameraZ; // camera coordinates
	float lookX, lookY, lookZ; // camera look-at coordinates

	float[][] facesNormals;
	float[][][] vertexNormals;

	public void render3D() {
		// model view stack
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// save current matrix
		GL11.glPushMatrix();

		// TRANSLATE
		GL11.glTranslatef(m_nX, m_nY, m_nZ);

		// ROTATE and SCALE
		GL11.glTranslatef(0, 0, -3.5f);
		if (m_rZ != 0)
			GL11.glRotatef(m_rZ, 0, 0, 1);
		if (m_rY != 0)
			GL11.glRotatef(m_rY, 0, 1, 0);
		if (m_rX != 0)
			GL11.glRotatef(m_rX, 1, 0, 0);
		if (m_sX != 1 || m_sY != 1 || m_sZ != 1)
			GL11.glScalef(m_sX, m_sY, m_sZ);
		GL11.glTranslatef(0, 0, 3.5f);

		renderModel();

		// discard current matrix
		GL11.glPopMatrix();
	}

	private void renderModel() {
		initialize();

		radians = (float) Math.PI * (angle - 90.0f) / 180.0f;

		// calculate the camera's position
		cameraX = lookX + (float) Math.sin(radians) * mouseY; // multiplying by
																// mouseY makes
																// the
		cameraZ = lookZ + (float) Math.cos(radians) * mouseY; // camera get
																// closer/farther
																// away with
																// mouseY
		cameraY = lookY + mouseY / 2.0f;

		// calculate the camera look-at coordinates as the center of the terrain
		// map
		lookX = (MAP_X * MAP_SCALE) / 2.0f;
		lookY = 150.0f;
		lookZ = -(MAP_Z * MAP_SCALE) / 2.0f;

		int faceCnt = 0;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		for (int z = 0; z < MAP_Z - 1; z++) {
			// dodamo blank triangles da lahko klicemo le enkratt triangle strip
			if (z != 0) {
				GL11.glVertex3f(terrain[z][MAP_X - 1][0],
						terrain[z][MAP_X - 1][1], terrain[z][MAP_X - 1][2]);
				GL11.glVertex3f(terrain[z][0][0], terrain[z][0][1],
						terrain[z][0][2]);
			}

			GL11.glNormal3f(vertexNormals[z][0][0], vertexNormals[z][0][1],
					vertexNormals[z][0][2]);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(terrain[z][0][0], terrain[z][0][1],
					terrain[z][0][2]);

			// draw vertex 1
			GL11.glNormal3f(vertexNormals[z + 1][0][0],
					vertexNormals[z + 1][0][1], vertexNormals[z + 1][0][2]);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(terrain[z + 1][0][0], terrain[z + 1][0][1],
					terrain[z + 1][0][2]);

			float a = 1.0f;
			for (int x = 0; x < MAP_X - 1; x++) {
				GL11.glNormal3f(vertexNormals[z][x + 1][0],
						vertexNormals[z][x + 1][1], vertexNormals[z][x + 1][2]);
				faceCnt++;
				GL11.glTexCoord2f(0.0f, a);
				GL11.glVertex3f(terrain[z][x + 1][0], terrain[z][x + 1][1],
						terrain[z][x + 1][2]);

				GL11.glNormal3f(vertexNormals[z + 1][x + 1][0],
						vertexNormals[z + 1][x + 1][1],
						vertexNormals[z + 1][x + 1][2]);
				faceCnt++;
				GL11.glTexCoord2f(1.0f, a);
				GL11.glVertex3f(terrain[z + 1][x + 1][0],
						terrain[z + 1][x + 1][1], terrain[z + 1][x + 1][2]);
				a = (a + 1.0f) % 2.0f;
			}

		}
		GL11.glEnd();
	}

	protected void initialize() {
		// initialize the terrain data
		initializeTerrain();
		smoothTerrain(3);
		vertexNormalization();
	}

	protected void initializeTerrain() {
		BitmapUtil bu = new BitmapUtil();
		String bmpPath = "heightmap.bmp";
		short[][] imageData = bu.loadBMP(bmpPath);

		MAP_X = bu.getBitmapWidth(); // size of map along x-axis
		MAP_Z = bu.getBitmapHeight(); // size of map along z-axis

		facesNormals = new float[(MAP_X - 1) * (MAP_Z - 1) * 2][3];
		vertexNormals = new float[MAP_Z][MAP_X][3];

		// loop through all of the heightfield points, calculating the
		// coordinates for each point
		terrain = new float[MAP_Z][MAP_X][3];
		for (int z = 0; z < MAP_Z; z++) {
			for (int x = 0; x < MAP_X; x++) {
				terrain[z][x][0] = (float) x * MAP_SCALE;
				terrain[z][x][1] = (float) imageData[z][x] / 20;
				terrain[z][x][2] = (float) z * MAP_SCALE;
			}
		}
	}

	// Box filtering convolution filter (filter size must be of type 2^n+1)
	protected void smoothTerrain(int filterSize) {
		for (int z = filterSize / 2; z < MAP_Z - filterSize / 2; z++) {
			for (int x = filterSize / 2; x < MAP_X - filterSize / 2; x++) {
				float sum = 0;
				for (int i = z - filterSize / 2; i <= z + filterSize / 2; i++) {
					for (int j = x - filterSize / 2; j <= x + filterSize / 2; j++) {
						sum += terrain[i][j][1];
					}
				}
				sum /= filterSize * filterSize;
				terrain[z][x][1] = sum;
			}
		}
	}

	protected void vertexNormalization() {
		int faceCnt = 0;
		for (int z = 0; z < MAP_Z - 1; z++) {
			for (int x = 0; x < MAP_X - 1; x++) {
				float[] n = calcNormal(terrain[z][x], terrain[z + 1][x],
						terrain[z][x + 1]);
				facesNormals[faceCnt++] = n;
				vertexNormals[z][x] = vecAdd(vertexNormals[z][x], n);
				vertexNormals[z + 1][x] = vecAdd(vertexNormals[z + 1][x], n);
				vertexNormals[z][x + 1] = vecAdd(vertexNormals[z][x + 1], n);

				n = calcNormal(terrain[z][x + 1], terrain[z + 1][x],
						terrain[z + 1][x + 1]);
				facesNormals[faceCnt++] = n;
				vertexNormals[z][x + 1] = vecAdd(vertexNormals[z][x + 1], n);
				vertexNormals[z + 1][x] = vecAdd(vertexNormals[z + 1][x], n);
				vertexNormals[z + 1][x + 1] = vecAdd(
						vertexNormals[z + 1][x + 1], n);
			}

		}

		for (int i = 0; i < MAP_Z; i++)
			for (int j = 0; j < MAP_X; j++)
				vertexNormals[i][j] = normalize(vertexNormals[i][j]);
	}

	protected float[] vecAdd(float[] a, float[] b) {
		return new float[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
	}

	protected float[] normalize(float[] a) {
		float length = (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2]
				* a[2]);
		if (length != 0) {
			a[0] = a[0] / length;
			a[1] = a[1] / length;
			a[2] = a[2] / length;
		}
		return a;
	}
}
