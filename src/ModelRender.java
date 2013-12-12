import static org.lwjgl.opengl.ARBBufferObject.GL_STATIC_DRAW_ARB;
import static org.lwjgl.opengl.ARBBufferObject.glBindBufferARB;
import static org.lwjgl.opengl.ARBBufferObject.glBufferDataARB;
import static org.lwjgl.opengl.ARBBufferObject.glGenBuffersARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

public class ModelRender {

	// arrays for drawing character
	float[][] vertices;
	float[][] normals;
	float[][] textures;
	ArrayList<Model3D> transformations;
	ArrayList<Integer> modelsPerObjs;
	ArrayList<String> objLoc;
	ArrayList<String> objTex;
	IntBuffer m_Textures;

	int enemyNumberRender = 0;

	int numOfPoints = 0;
	int bufferSize = 0;

	private static int BYTE_SIZE = 4;
	private static int VERTEX_DIMENSION = 3;
	private static int TEXTURE_DIMENSION = 2;

	private int verticesAndNormalsAndTextureBufferID;
	private FloatBuffer verticesAndNormalsAndTextureBuffer;

	public ModelRender() {
		transformations = new ArrayList<Model3D>();
		modelsPerObjs = new ArrayList<Integer>();
		objLoc = new ArrayList<String>();
		objTex = new ArrayList<String>();
	}

	void initializeModels() {
		int numOfObjs = modelsPerObjs.size();
		vertices = new float[numOfObjs][];
		normals = new float[numOfObjs][];
		textures = new float[numOfObjs][];
		m_Textures = Texture.loadTextures2D(Arrays.copyOf(objTex.toArray(),
				objTex.toArray().length, String[].class));

		ReadObj obj = new ReadObj();
		for (int i = 0; i < numOfObjs; i++) {
			obj.getModelObj(objLoc.get(i));
			vertices[i] = obj.verticesRearranged;
			normals[i] = obj.normalsRearranged;
			textures[i] = obj.texturesRearranged;
			bufferSize += vertices[i].length;
		}
		numOfPoints = bufferSize;
		bufferSize += bufferSize + (bufferSize * (float) 2 / 3);

		// Generating buffers
		verticesAndNormalsAndTextureBufferID = glGenBuffersARB();

		// Binding buffers
		glBindBufferARB(GL_ARRAY_BUFFER_ARB,
				verticesAndNormalsAndTextureBufferID);

		// Creating buffers for the actual data
		verticesAndNormalsAndTextureBuffer = ByteBuffer
				.allocateDirect(bufferSize * BYTE_SIZE)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		for (int i = 0; i < numOfObjs; i++)
			verticesAndNormalsAndTextureBuffer.put(vertices[i]);

		for (int i = 0; i < numOfObjs; i++)
			verticesAndNormalsAndTextureBuffer.put(normals[i]);

		for (int i = 0; i < numOfObjs; i++)
			verticesAndNormalsAndTextureBuffer.put(textures[i]);

		verticesAndNormalsAndTextureBuffer.rewind();

		glBufferDataARB(GL_ARRAY_BUFFER_ARB,
				verticesAndNormalsAndTextureBuffer, GL_STATIC_DRAW_ARB);
	}

	public void addModel(Model3D m, String objLoc, String objTex) {
		transformations.add(m);
		modelsPerObjs.add(1);
		this.objLoc.add(objLoc);
		this.objTex.add(objTex);
	}

	public void addModel(Model3D[] ms, String objLoc, String objTex) {
		for (Model3D m : ms)
			transformations.add(m);
		modelsPerObjs.add(ms.length);
		this.objLoc.add(objLoc);
		this.objTex.add(objTex);
	}

	public void addEnemyRender() {
		enemyNumberRender++;
	}

	public void render3D() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		int startOffset = 0;
		int trID = 0;
		for (int i = 0; i < modelsPerObjs.size(); i++) {
			for (int j = 0; j < modelsPerObjs.get(i); j++) {
				// save current matrix
				GL11.glPushMatrix();

				Model3D m = transformations.get(trID++);
				GL11.glTranslatef(m.m_nX, m.m_nY, m.m_nZ);
				GL11.glScalef(m.m_sX, m.m_sY, m.m_sZ);
				GL11.glRotatef(m.yaw, 0, 1, 0);

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_Textures.get(i));
				renderModel(startOffset, vertices[i].length / 3);

				// discard current matrix
				GL11.glPopMatrix();
				
				if (i == 0 && j >= enemyNumberRender)
					break;
			}
			if (i != 0)
				startOffset += vertices[i].length / 3;
		}
	}

	void renderModel(int startOffset, int length) {
		// Set the pointers to vertices, normals and textures
		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(VERTEX_DIMENSION, GL_FLOAT, 0, 0);
		glEnableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glNormalPointer(GL_FLOAT, 0, (numOfPoints * BYTE_SIZE));
		glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glTexCoordPointer(TEXTURE_DIMENSION, GL_FLOAT, 0,
				((numOfPoints * 2) * BYTE_SIZE));

		// drawing triangles
		GL11.glDrawArrays(GL_TRIANGLES, startOffset * 2, length); 

		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glDisableClientState(GL_VERTEX_ARRAY);
	}
}