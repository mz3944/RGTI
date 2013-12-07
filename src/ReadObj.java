import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class ReadObj {
	float[] verticesRearranged;
	float[] normalsRearranged;
	float[] texturesRearranged;
	
	public void getModelObj(String objectName){
		try{
			List <Float> vertices = new LinkedList<Float>();
			List <Float> normals = new LinkedList<Float>();
			List <Float> textures = new LinkedList<Float>();
			
			List <Integer> vertexIndices = new LinkedList<Integer>();
			List <Integer> normalIndices = new LinkedList<Integer>();
			List <Integer> textureIndices = new LinkedList<Integer>();
			
			String filepath = objectName;
			FileInputStream fis = new FileInputStream(filepath);
			DataInputStream dis = new DataInputStream(fis);
			String s = "";
			float resize = 0.04f;
			while((s = dis.readLine()) != null){
				String [] niz = s.split(" ");
				for (int i = 0; i < niz.length; i++){
					if(niz[0].equals("v") || niz[0].equals("vn") || niz[0].equals("vt") || niz[0].equals("f")){
						if(niz[0].equals("v")){
							float x = Float.parseFloat(niz[i+1]) * resize;
							float y = Float.parseFloat(niz[i+2]) * resize;
							float z = Float.parseFloat(niz[i+3]) * resize;

							vertices.add(x);
							vertices.add(y);
							vertices.add(z);
							i += 3;
						}
						else if(niz[0].equals("vn")){
							float x = Float.parseFloat(niz[i+1]) * resize;
							float y = Float.parseFloat(niz[i+2]) * resize;
							float z = Float.parseFloat(niz[i+3]) * resize;

							normals.add(x);
							normals.add(y);
							normals.add(z);
							i += 3;
						}
						else if (niz[0].equals("vt")){
							float x = Float.parseFloat(niz[i+1]);
							float y = Float.parseFloat(niz[i+2]);

							textures.add(x);
							textures.add(y);	
							i += 2;
						}
						else if (niz[0].equals("f")){
							if (niz.length == 5) {
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
								
								vertexIndices.add(va);
								vertexIndices.add(vb);
								vertexIndices.add(vc);
								vertexIndices.add(vc);
								vertexIndices.add(vd);
								vertexIndices.add(va);
								textureIndices.add(ta);
								textureIndices.add(tb);
								textureIndices.add(tc);
								textureIndices.add(tc);
								textureIndices.add(td);
								textureIndices.add(ta);
								normalIndices.add(na);
								normalIndices.add(nb);
								normalIndices.add(nc);
								normalIndices.add(nc);
								normalIndices.add(nd);
								normalIndices.add(na);
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

								vertexIndices.add(va);
								vertexIndices.add(vb);
								vertexIndices.add(vc);
								textureIndices.add(ta);
								textureIndices.add(tb);
								textureIndices.add(tc);
								normalIndices.add(na);
								normalIndices.add(nb);
								normalIndices.add(nc);
								i += 3;
							}
						}
					}
					else{
						break;
					}
				}
			}
			dis.close();

			rearrange(vertices, normals, textures, vertexIndices, normalIndices, textureIndices);
		}
		catch(IOException e){
			System.out.println(e.getStackTrace());
		}
	}
	
	private void rearrange(List <Float> vertices, List <Float> normals, List <Float> textures, List <Integer> vertexIndices, List <Integer> normalIndices, List <Integer> textureIndices) {
		verticesRearranged = new float[vertexIndices.size()*3];System.out.println(verticesRearranged.length);
		normalsRearranged = new float[normalIndices.size()*3];System.out.println(vertexIndices.size());
		texturesRearranged = new float[textureIndices.size()*2];
		for(int i = 0; i < vertexIndices.size(); i++) {
			verticesRearranged[i*3] = vertices.get(vertexIndices.get(i)*3);
			verticesRearranged[i*3+1] = vertices.get(vertexIndices.get(i)*3+1);
			verticesRearranged[i*3+2] = vertices.get(vertexIndices.get(i)*3+2);
			
			normalsRearranged[i*3] = normals.get(normalIndices.get(i)*3);
			normalsRearranged[i*3+1] = normals.get(normalIndices.get(i)*3+1);
			normalsRearranged[i*3+2] = normals.get(normalIndices.get(i)*3+2);
			
			texturesRearranged[i*2] = textures.get(textureIndices.get(i)*2);
			texturesRearranged[i*2+1] = textures.get(textureIndices.get(i)*2+1);
		}
	}
}