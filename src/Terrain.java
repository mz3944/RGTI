//import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;

public class Terrain extends Model3D {
	int MAP_X;
	int MAP_Z;
	float MAP_SCALE = 1.0f; // the scale of the terrain map
	float MAP_HEIGHT = 0.2f;

	// Terrain Data
	float[][][] terrain; // heightfield terrain data (0-255)

	float[][] facesNormals;
	float[][][] vertexNormals;

	int x0;
	int y0;
	int radius;
	float angleSize;
	float angle;

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
//		GL11.glTranslatef(m_nX, m_nY, m_nZ);

		renderModel();

		// discard current matrix
		GL11.glPopMatrix();
	}

	private void renderModel() {
		TreeSet<Point> drawablePixels = DrawCircle();

		int faceCnt = 0;
		int lastY = -1;
		int lastX = -1;
		Iterator iter = drawablePixels.iterator();
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		float a = 0.0f;

		while (iter.hasNext()) {
			Point u = (Point) iter.next();
			int z = u.y;

			if (lastY > -1 && lastY != z) {
				a = u.x % 2.0f;

				GL11.glVertex3f(terrain[z][lastX][0], terrain[z][lastX][1],
						terrain[z][lastX][2]);
				int thisX = u.x;
				GL11.glVertex3f(terrain[z][thisX][0], terrain[z][thisX][1],
						terrain[z][thisX][2]);
			}

			int x = u.x;
			GL11.glNormal3f(vertexNormals[z][x][0], vertexNormals[z][x][1],
					vertexNormals[z][x][2]);
			GL11.glTexCoord2f(a, 0.0f);
			GL11.glVertex3f(terrain[z][x][0], terrain[z][x][1],
					terrain[z][x][2]);
			faceCnt++;

			GL11.glNormal3f(vertexNormals[z + 1][x][0],
					vertexNormals[z + 1][x][1], vertexNormals[z + 1][x][2]);
			GL11.glTexCoord2f(a, 1.0f);
			GL11.glVertex3f(terrain[z + 1][x][0], terrain[z + 1][x][1],
					terrain[z + 1][x][2]);

			faceCnt++;
			a = (a + 1.0f) % 2.0f;
			lastY = u.y;
			lastX = u.x;

			iter.remove(); // avoids a ConcurrentModificationException
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
		String bmpPath = "terrain.bmp";
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
				terrain[z][x][1] = (float) imageData[z][x] * MAP_HEIGHT;
				terrain[z][x][2] = (float) z * MAP_SCALE;
			}
		}
	}

	// Box filtering convolution filter (filter size must be of type 2^n+1)
	protected void smoothTerrain(int filterSize) {
		for (int z = 0; z < MAP_Z; z++) {
			for (int x = 0; x < MAP_X; x++) {
				float sum = 0;
				int count = 0;
				for (int i = z - filterSize / 2; i <= z + filterSize / 2; i++) {
					if (i < 0 || i >= MAP_Z)
						continue;
					for (int j = x - filterSize / 2; j <= x + filterSize / 2; j++) {
						if (j < 0 || j >= MAP_X)
							continue;
						sum += terrain[i][j][1];
						count++;
					}
				}
				sum /= count;
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

	public float[][] getTrinagle(float x, float y) {
		int a = (int) (x / MAP_SCALE + MAP_X / 2 - 1);
		int b = a + 1;

		int c = (int) (y / MAP_SCALE + MAP_Z / 2 - 1);
		int d = c + 1;

		boolean upperLeftTri = (((x / MAP_SCALE + MAP_X / 2)) % MAP_SCALE)
				+ (((y / MAP_SCALE + MAP_Z / 2)) % MAP_SCALE) < MAP_SCALE;

		if (x >= (MAP_X / 2)) {
			a--;
			b--;
			upperLeftTri = false;
		}
		if (y >= (MAP_Z / 2)) {
			c--;
			d--;
			upperLeftTri = false;
		}

		float[] fir = terrain[c][b];
		float[] sec = terrain[d][a];
		float[] thi = terrain[(upperLeftTri ? c : d)][(upperLeftTri ? a : b)];

		return new float[][] { fir, sec, thi };
	}

	public float[][] getTrinagleLocation(float x, float y) {
		float[][] source = getTrinagle(x, y);
		float[][] triangle = new float[3][3]; // =
												// Arrays.copyOf(getTrinagle(x,y));

		for (int i = 0; i < source.length; i++)
			System.arraycopy(source[i], 0, triangle[i], 0, source[0].length);

		triangle[0][0] -= (MAP_X / 2 - 1);
		triangle[1][0] -= (MAP_X / 2 - 1);
		triangle[2][0] -= (MAP_X / 2 - 1);
		triangle[0][2] -= (MAP_Z / 2 - 1);
		triangle[1][2] -= (MAP_Z / 2 - 1);
		triangle[2][2] -= (MAP_Z / 2 - 1);

		return triangle;
	}

	public TreeSet<Point> DrawCircle() {
		Point a;
		TreeSet<Point> drawablePixels2 = new TreeSet<Point>();

		int x = radius, y = 0;
		int radiusError = 1 - x;

		float angleMin = angle - angleSize / 2;
		float angleMax = (angle + angleSize / 2) % 360;
		if (angleMin < 0)
			angleMin += 360;

		while (x >= y) {
			// octants
			int[][] index = { { (x + x0), (y + y0) }, { (y + x0), (x + y0) },
					{ (-x + x0), (y + y0) }, { (-y + x0), (x + y0) },
					{ (-x + x0), (-y + y0) }, { (-y + x0), (-x + y0) },
					{ (x + x0), (-y + y0) }, { (y + x0), (-x + y0) } };
			int[][] indexAngles = { { x, y }, { y, x }, { -x, y }, { -y, x },
					{ -x, -y }, { -y, -x }, { x, -y }, { y, -x } };

			for (int i = 0; i < 8; i++) {
				float g = (float) Math.toDegrees(Math.atan2(indexAngles[i][0],
						indexAngles[i][1]));

				if (g < 0)
					g += 360;
				if ((angleMax <= angleMin && (g >= angleMin || g <= angleMax))
						|| (angleMax > angleMin && g >= angleMin && g <= angleMax))
					drawablePixels2.add(new Point(index[i][0], index[i][1]));
			}

			y++;
			if (radiusError < 0)
				radiusError += 2 * y + 1;
			else {
				x--;
				radiusError += 2 * (y - x + 1);
			}
		}
		Iterator it = drawablePixels2.iterator();
		boolean found = false;
		int newX = 0;
		int newY = 0;
		while (it.hasNext() && !found) {
			Point u = (Point) it.next();
			newX = u.x;
			newY = u.y;

			int[][] directs = new int[][] { { newX, newY + 1 },
					{ newX + 1, newY }, { newX, newY - 1 }, { newX - 1, newY } };
			float g;
			for (int i = 0; i < 4; i++) {

				if ((directs[i][0] - x0) * (directs[i][0] - x0)
						+ (directs[i][1] - y0) * (directs[i][1] - y0) <= radius
						* radius) {
					g = (float) Math.toDegrees(Math.atan2(directs[i][0] - x0,
							directs[i][1] - y0));
					if (g < 0)
						g += 360;

					if ((angleMax <= angleMin && (g >= angleMin || g <= angleMax))
							|| (angleMax > angleMin && g >= angleMin && g <= angleMax)) {
						if (!drawablePixels2.contains(new Point(directs[i][0],
								directs[i][1]))) {
							drawablePixels2.add(new Point(directs[i][0],
									directs[i][1]));

							newX = directs[i][0];
							newY = directs[i][1];
							found = true;
							break;
						}
					}
				}
			}
		}
		if (found)
			drawablePixels2 = recurs(newX, newY, drawablePixels2, angleMin,
					angleMax);

		return drawablePixels2;
	}

	public void setVisibleArea(int x0, int y0, int radius, float angleSize,
			float angle, float backDist) {
		this.x0 = x0 + MAP_X / 2 - 1;
		this.y0 = y0 + MAP_Z / 2 - 1;
		this.radius = radius;
		this.angleSize = angleSize;

		this.angle = 180 - angle;

		this.angle %= 360;
		if (this.angle < 0)
			this.angle += 360;

		this.x0 += backDist * (float) Math.sin(Math.toRadians(angle));
		this.y0 -= backDist * (float) Math.cos(Math.toRadians(angle));
	}

	public HashSet<Point> getCircleSlice(HashSet<Point> drawablePixels) {

		return drawablePixels;
	}

	public TreeSet<Point> recurs(int x, int y, TreeSet<Point> drawablePixels,
			float angleMin, float angleMax) {
		float g;
		int[][] directs = new int[][] { { x, y + 1 }, { x + 1, y },
				{ x, y - 1 }, { x - 1, y } };

		for (int i = 0; i < 4; i++) {
			g = (float) Math.toDegrees(Math.atan2(directs[i][0] - x0,
					directs[i][1] - y0));

			if (g < 0)
				g += 360;
			if ((angleMax <= angleMin && (g >= angleMin || g <= angleMax))
					|| (angleMax > angleMin && g >= angleMin && g <= angleMax)) {
				if ((directs[i][0] - x0) * (directs[i][0] - x0)
						+ (directs[i][1] - y0) * (directs[i][1] - y0) <= radius
						* radius) {
					if (!drawablePixels.contains(new Point(directs[i][0],
							directs[i][1]))) {
						drawablePixels.add(new Point(directs[i][0],
								directs[i][1]));
						drawablePixels = recurs(directs[i][0], directs[i][1],
								drawablePixels, angleMin, angleMax);
					}
				}
			}
		}

		return drawablePixels;
	}
}

class Point implements Comparator<Point>, Comparable<Point> {
	public int x;
	public int y;

	Point() {
	}

	Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// Overriding the compareTo method
	public int compareTo(Point p) {
		int compare = Integer.compare(this.y, p.y);
		if (compare == 0)
			compare = Integer.compare(this.x, p.x);

		return compare;
	}

	// Overriding the compare method for sorting
	public int compare(Point p, Point p1) {
		return p.y - p1.y;
	}
}