import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point4f;


public class ReadObj {
	List <Point4f> vertex = new LinkedList<Point4f>();
	List <Point4f> normals = new LinkedList<Point4f>();
	List <Point4f> textures = new LinkedList<Point4f>();
	List <Polygon> polygons = new LinkedList<Polygon>();
	public void getModelObj(String objectName){
		try{
			String filepath = objectName;
			FileInputStream fis = new FileInputStream(filepath);
			DataInputStream dis = new DataInputStream(fis);
			String s = "";
			while((s = dis.readLine()) != null){
				String [] niz = s.split(" ");
				for (int i = 0; i < niz.length; i++){
					if(niz[0].equals("v") || niz[0].equals("vn") || niz[0].equals("vt") || niz[0].equals("f")){
						if(niz[0].equals("v")){
							float x = Float.parseFloat(niz[i+1]);
							float y = Float.parseFloat(niz[i+2]);
							float z = Float.parseFloat(niz[i+3]);
							vertex.add(new Point4f(x,y,z,1));
							i += 4;
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
							//float z = Float.parseFloat(niz[i+3]);
							textures.add(new Point4f(x,y,1,1));
							i += 3;
						}
						else if (niz[0].equals("f")){
							if (niz.length == 5){
								String [] numbers = niz[i+1].split("/");
								int va = Integer.parseInt(numbers[0])-1;
								int ta = Integer.parseInt(numbers[1])-1;
								int na = Integer.parseInt(numbers[2])-1;
								numbers = niz[i+2].split("/");
								int vb = Integer.parseInt(numbers[0])-1;
								int tb = Integer.parseInt(numbers[1])-1;
								int nb = Integer.parseInt(numbers[2])-1;
								numbers = niz[i+3].split("/");
								int vc = Integer.parseInt(numbers[0])-1;
								int tc = Integer.parseInt(numbers[1])-1;
								int nc = Integer.parseInt(numbers[2])-1;
								numbers = niz[i+4].split("/");
								int vd = Integer.parseInt(numbers[0])-1;
								int td = Integer.parseInt(numbers[1])-1;
								int nd = Integer.parseInt(numbers[2])-1;
								FaceVTN a = new FaceVTN(va,ta,na);
								FaceVTN b = new FaceVTN(vb,tb,nb);
								FaceVTN c = new FaceVTN(vc,tc,nc);
								FaceVTN d = new FaceVTN(vd,td,nd);
								polygons.add(new Polygon(a,b,c));
								polygons.add(new Polygon(a,c,d));
								i += 4;
							}
							else{
								String [] numbers = niz[i+1].split("/");
								int va = Integer.parseInt(numbers[0])-1;
								int ta = Integer.parseInt(numbers[1])-1;
								int na = Integer.parseInt(numbers[2])-1;
								numbers = niz[i+2].split("/");
								int vb = Integer.parseInt(numbers[0])-1;
								int tb = Integer.parseInt(numbers[1])-1;
								int nb = Integer.parseInt(numbers[2])-1;
								numbers = niz[i+3].split("/");
								int vc = Integer.parseInt(numbers[0])-1;
								int tc = Integer.parseInt(numbers[1])-1;
								int nc = Integer.parseInt(numbers[2])-1;
								FaceVTN a = new FaceVTN(va,ta,na);
								FaceVTN b = new FaceVTN(vb,tb,nb);
								FaceVTN c = new FaceVTN(vc,tc,nc);
								polygons.add(new Polygon(a,b,c));
								i += 3;
							}
						}
					}
					else{
						break;
					}
				}
			}
		}
		catch(IOException e){
			System.out.println(e.getStackTrace());
		}
	}
}
