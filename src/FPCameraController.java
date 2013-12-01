import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

//First Person Camera Controller
public class FPCameraController {
	// 3d vector to store the camera's position in
	private float[] position = null;
	// look strait Y
	private float straitY = 5.0f; 
	// the rotation around the Y axis of the camera
	private float yaw = 180.0f;
	// the rotation around the X axis of the camera
	private float pitch = 0.0f;
	private float dist2obj = 10.0f;

	// Constructor that takes the starting x, y, z location of the camera
	public FPCameraController(float x, float y, float z) {
		// instantiate position Vector3f to the x y z params.
		position = new float[] {x, y, z};
		straitY = y;
	}

	public void CamOnObjPossition(float[] objposition, float objyaw) {
		// camera look to same yaw as object 		
		yaw   = 180 - objyaw;
	//	System.out.println("Folow cam yaw:" + Float.toString(yaw) + ", objyaw:" + Float.toString(objyaw));
		// move camera behind the object
//		position[1] = objposition[1];
		position[0] = -objposition[0] + dist2obj * (float) Math.sin(Math.toRadians(yaw));
		position[2] = -objposition[2] - dist2obj * (float) Math.cos(Math.toRadians(yaw));
	//	System.out.println("Folow cam X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
        float dy = -(-position[1] - objposition[1] - 3.0f);
        float atan = (float) Math.atan(dy/dist2obj);
        pitch = (float) -Math.toDegrees(atan); 
    	System.out.println("objY" + Float.toString(objposition[1]) + "camY" + Float.toString(position[1]) 
              //  + ", Folow cam pitch:" + Float.toString(ppitch) 
    			                  + ", pitch:" + Float.toString(pitch) 
    			                  + ", atan:" + Float.toString(atan) + ", dy=" + Float.toString(dy) );
	}

	// increment the camera's current yaw rotation
	public void yaw(float amount) {
		// increment the yaw by the amount param
		yaw += amount;
		//System.out.println("cam yaw:" + Float.toString(yaw));
	}
	
	public float getJaw() {
		return yaw;
	}

	// increment the camera's current yaw rotation
	public void pitch(float amount) {
		// increment the pitch by the amount param
		pitch -= amount;
		if(pitch > 45)
			pitch = 45;
		else if(pitch < -45)
			pitch = -45;
	}

	// moves the camera forward relative to its current rotation (yaw)
	public void walkForward(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw));
	//	System.out.println("Forw cam X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
	}

	// moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		position[0] += distance * (float) Math.sin(Math.toRadians(yaw));
		position[2] -= distance * (float) Math.cos(Math.toRadians(yaw));
		//System.out.println("Back cam X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
	}

	// strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw - 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw - 90));
	//	System.out.println("Left cam X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
	}

	// strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw + 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw + 90));
	//	System.out.println("Righ cam X:" + Float.toString(position[0]) + ", Z:" + Float.toString(position[2]));
	}

	// translates and rotate the matrix so that it looks through the camera
	// this dose basic what gluLookAt() does
	public void lookThrough() {
		// roatate the pitch around the X axis
		GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		// roatate the yaw around the Y axis
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		// translate to the position vector's location
		GL11.glTranslatef(position[0], position[1], position[2]);
	}
	
	public void calcY(float[] p1, float[] p2, float[] p3, int MAP_X, int MAP_Z) {	
		float det = (p2[2] - p3[2]) * (p1[0] - p3[0]) + (p3[0] - p2[0]) * (p1[2] - p3[2]);
		
		float l1 = ((p2[2] - p3[2]) * (-position[0] - p3[0]) + (p3[0] - p2[0]) * (-position[2] - p3[2])) / det;
		float l2 = ((p3[2] - p1[2]) * (-position[0] - p3[0]) + (p1[0] - p3[0]) * (-position[2] - p3[2])) / det;
		float l3 = 1.0f - l1 - l2;
		
		position[1] = -(l1 * p1[1] + l2 * p2[1] + l3 * p3[1]);
		position[1] -= 5.0f;
	}
	
	public float[] getPosition() {
		return position;
	}
	
	public void checkBounds(float x, float z, int distanceView) {
		distanceView++;
		if(position[0] > (x/2 - distanceView) - 1)
			position[0] = (x/2 - distanceView) - 1; 
		if(position[0] < -(x/2 - distanceView))
			position[0] = -(x/2 - distanceView);
		if(position[2] > (z/2 - distanceView) - 1)
			position[2] = (z/2 - distanceView) - 1;
		if(position[2] < -(z/2 - distanceView))
			position[2] = -(z/2 - distanceView);
	}
}