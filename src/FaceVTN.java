
public class FaceVTN {
	int ivertex;
	int itexture;
	int inormal;
	
	public FaceVTN(int vertex, int texture, int normal){
		ivertex = vertex;
		itexture = texture;
		inormal = normal;
	}
	
	int getVertex(){
		return ivertex;
	}
	
	int getTexture(){
		return itexture;
	}
	
	int getNormal(){
		return inormal;
	}
	
	void setVertex(int v){
		ivertex = v;
	}
	
	void setTexture(int t){
		itexture = t;
	}
	
	void setNormal(int n){
		inormal = n;
	}
}
