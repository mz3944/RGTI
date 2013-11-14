import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point4f;
import javax.vecmath.Point4i;



public class ModelCharacterObj {
	List <Point4f> vertex = new LinkedList<Point4f>();
	List <Point4f> normals = new LinkedList<Point4f>();
	List <Point4i> lines = new LinkedList<Point4i>();
	public void getModelObj(){
		try{
			String filepath = "cube.obj";
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
					}
					else if(niz[0].equals("vn")){
						float x = Float.parseFloat(niz[i+1]);
						float y = Float.parseFloat(niz[i+2]);
						float z = Float.parseFloat(niz[i+3]);
						normals.add(new Point4f(x,y,z,1));
					}
					else if (niz[0].equals("f")){
						int a = Integer.parseInt(niz[i+1].substring(0,1));
						int b = Integer.parseInt(niz[i+2].substring(0,1));
						int c = Integer.parseInt(niz[i+3].substring(0,1));
						int d = Integer.parseInt(niz[i+4].substring(0,1));
						lines.add(new Point4i(a,b,c,d));
					}
				}
		
			}
		
		}
		catch(IOException e){
			System.out.println(e.getStackTrace());
		}
	}
}