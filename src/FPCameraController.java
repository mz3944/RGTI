import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

//First Person Camera Controller
public class FPCameraController {
	// 3d vector to store the camera's position in
	private float[] position = null;
	// the rotation around the Y axis of the camera
	private float yaw = 0.0f;
	// the rotation around the X axis of the camera
	private float pitch = 0.0f;

	// Constructor that takes the starting x, y, z location of the camera
	public FPCameraController(float x, float y, float z) {
		// instantiate position Vector3f to the x y z params.
		position = new float[] {x, y, z};
	}

	// increment the camera's current yaw rotation
	public void yaw(float amount) {
		// increment the yaw by the amount param
		yaw += amount;
	}
	
	public float getJaw() {
		return yaw;
	}

	// increment the camera's current yaw rotation
	public void pitch(float amount) {
		// increment the pitch by the amount param
		pitch -= amount;System.out.println(pitch);
		if(pitch > 45)
			pitch = 45;
		else if(pitch < -45)
			pitch = -45;
	}

	// moves the camera forward relative to its current rotation (yaw)
	public void walkForward(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		position[0] += distance * (float) Math.sin(Math.toRadians(yaw));
		position[2] -= distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw - 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw - 90));
	}

	// strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance) {
		position[0] -= distance * (float) Math.sin(Math.toRadians(yaw + 90));
		position[2] += distance * (float) Math.cos(Math.toRadians(yaw + 90));
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