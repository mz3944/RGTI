
public class Polygon {
	FaceVTN a;
	FaceVTN b;
	FaceVTN c;
	
	public Polygon(FaceVTN a, FaceVTN b, FaceVTN c){
		this.a = a;
		this.b = b;
		this.c = c;
	} 
	
	FaceVTN getVertexA(){
		return a;
	}
	
	FaceVTN getVertexB(){
		return b;
	}
	
	FaceVTN getVertexC(){
		return c;
	}
	
	void setVertexA(FaceVTN x){
		a = x;
	}
	
	void setVertexB(FaceVTN x){
		b = x;
	}

	void setVertexC(FaceVTN x){
		c = x;
	}
}
