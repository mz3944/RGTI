
public class Quad {
	FaceVTN a;
	FaceVTN b;
	FaceVTN c;
	FaceVTN d;
	
	public Quad(FaceVTN a, FaceVTN b, FaceVTN c, FaceVTN d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
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
	
	FaceVTN getVertexD(){
		return d;
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
	
	void setVertexD(FaceVTN x){
		d = x;
	}
}
