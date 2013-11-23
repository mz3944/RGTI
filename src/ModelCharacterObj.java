import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point4f;
import javax.vecmath.Point4i;

import org.lwjgl.opengl.GL11;



public class ModelCharacterObj extends Model3D {
	List <Point4f> vertex = new LinkedList<Point4f>();
	List <Point4f> normals = new LinkedList<Point4f>();
	List <Point4f> textures = new LinkedList<Point4f>();
	List <Polygon> polygons = new LinkedList<Polygon>();
	void initializeModel(){
		ReadObj obj = new ReadObj();
		obj.getModelObj();
		vertex = obj.vertex;
		normals = obj.normals;
		textures = obj.textures;
		polygons = obj.polygons;
	}
	@Override
	public void render3D() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// save current matrix
		GL11.glPushMatrix();

		// TRANSLATE
		GL11.glTranslatef(m_nX, m_nY, m_nZ);

		// ROTATE and SCALE
		renderModel();

		// discard current matrix
		GL11.glPopMatrix();
		
	}
	void renderModel(){
//		initializeModel();
		
		GL11.glBegin(GL11.GL_TRIANGLES); // draw triangels 
		for (int i = 0; i < polygons.size(); i++)
		{
			Polygon polygon = polygons.get(i);
			GL11.glTexCoord2f(textures.get(polygon.a.itexture).getX(), textures.get(polygon.a.itexture).getY());
			GL11.glNormal3f(normals.get(polygon.a.inormal).getX(), normals.get(polygon.a.inormal).getY(), normals.get(polygon.a.inormal).getZ());
			GL11.glVertex3f(vertex.get(polygon.a.ivertex).getX(), vertex.get(polygon.a.ivertex).getY(), vertex.get(polygon.a.ivertex).getZ());
			
			GL11.glTexCoord2f(textures.get(polygon.b.itexture).getX(), textures.get(polygon.b.itexture).getY());
			GL11.glNormal3f(normals.get(polygon.b.inormal).getX(), normals.get(polygon.b.inormal).getY(), normals.get(polygon.b.inormal).getZ());
			GL11.glVertex3f(vertex.get(polygon.b.ivertex).getX(), vertex.get(polygon.b.ivertex).getY(), vertex.get(polygon.b.ivertex).getZ());
			
			GL11.glTexCoord2f(textures.get(polygon.c.itexture).getX(), textures.get(polygon.c.itexture).getY());
			GL11.glNormal3f(normals.get(polygon.c.inormal).getX(), normals.get(polygon.c.inormal).getY(), normals.get(polygon.c.inormal).getZ());
			GL11.glVertex3f(vertex.get(polygon.c.ivertex).getX(), vertex.get(polygon.c.ivertex).getY(), vertex.get(polygon.c.ivertex).getZ());
			
		}  
	    GL11.glEnd();
	}
}