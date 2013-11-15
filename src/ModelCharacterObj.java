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
	List <Quad> quads = new LinkedList<Quad>();
	public void getModelObj(){
		try{
			String filepath = "lowpoly-male-obj.obj";
			FileInputStream fis = new FileInputStream(filepath);
			DataInputStream dis = new DataInputStream(fis);
			String s = "";
			while((s = dis.readLine()) != null){
				String [] niz = s.split(" ");
				for (int i = 0; i < niz.length; i++){
					if(niz[0].equals("v")){
						
						float x = Float.parseFloat(niz[i+1]);
						float y = Float.parseFloat(niz[i+2]);
						float z = Float.parseFloat(niz[i+3]);
						vertex.add(new Point4f(x,y,z,1));
						i += 3;
					}
					else if(niz[0].equals("vn")){
						float x = Float.parseFloat(niz[i+1]);
						float y = Float.parseFloat(niz[i+2]);
						float z = Float.parseFloat(niz[i+3]);
						normals.add(new Point4f(x,y,z,1));
						i += 3;
					}
					else if (niz[0].equals("vt")){
						float x = Float.parseFloat(niz[i+1]);
						float y = Float.parseFloat(niz[i+2]);
						float z = Float.parseFloat(niz[i+3]);
						textures.add(new Point4f(x,y,z,1));
						i += 3;
					}
					else if (niz[0].equals("f")){
						String [] numbers = niz[i+1].split("/");
						int va = Integer.parseInt(numbers[0]);
						int ta = Integer.parseInt(numbers[1]);
						int na = Integer.parseInt(numbers[2]);
						numbers = niz[i+2].split("/");
						int vb = Integer.parseInt(numbers[0]);
						int tb = Integer.parseInt(numbers[1]);
						int nb = Integer.parseInt(numbers[2]);
						numbers = niz[i+3].split("/");
						int vc = Integer.parseInt(numbers[0]);
						int tc = Integer.parseInt(numbers[1]);
						int nc = Integer.parseInt(numbers[2]);
						numbers = niz[i+4].split("/");
						int vd = Integer.parseInt(numbers[0]);
						int td = Integer.parseInt(numbers[1]);
						int nd = Integer.parseInt(numbers[2]);
						FaceVTN a = new FaceVTN(va,ta,na);
						FaceVTN b = new FaceVTN(vb,tb,nb);
						FaceVTN c = new FaceVTN(vc,tc,nc);
						FaceVTN d = new FaceVTN(vd,td,nd);
						quads.add(new Quad(a,b,c,d));
						i += 4;
					}
				}
		
			}
		
		}
		catch(IOException e){
			System.out.println(e.getStackTrace());
		}
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
		GL11.glBegin(GL11.GL_QUADS); // draw independent quads
		
		for (int i = 0; i < quads.size(); i++)
		{
			Quad quad = quads.get(i);
			GL11.glColor3f(1, 0, 0);
			
			GL11.glVertex3f(vertex.get(quad.a.ivertex).getX(), vertex.get(quad.a.ivertex).getY(), vertex.get(quad.a.ivertex).getZ());
			GL11.glVertex3f(vertex.get(quad.b.ivertex).getX(), vertex.get(quad.b.ivertex).getY(), vertex.get(quad.b.ivertex).getZ());
			GL11.glVertex3f(vertex.get(quad.c.ivertex).getX(), vertex.get(quad.c.ivertex).getY(), vertex.get(quad.c.ivertex).getZ());
			GL11.glVertex3f(vertex.get(quad.d.ivertex).getX(), vertex.get(quad.d.ivertex).getY(), vertex.get(quad.d.ivertex).getZ());
		}  
	    GL11.glEnd();
	}
}