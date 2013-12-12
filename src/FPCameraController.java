import org.lwjgl.opengl.GL11;

// First/third Person Camera Controller
public class FPCameraController extends Model3D {
	private float dist2obj = 15.0f;
	private float lookAbove = 10.0f;

	// Constructor that takes the starting x, y, z location of the camera
	public FPCameraController(float x, float y, float z) {
		super(x, y, z);
	}

	public void CamOnObjPossition(float[] objposition, float objyaw) {
		yaw = 180 - objyaw;
		m_nX = -objposition[0] + dist2obj
				* (float) Math.sin(Math.toRadians(yaw));
		m_nZ = -objposition[2] - dist2obj
				* (float) Math.cos(Math.toRadians(yaw));
		m_nY = -(objposition[1] + lookAbove);
	}

	public void setDist2obj(int scr) {
		dist2obj -= (0.5f * scr);
		if (dist2obj < 2.5f)
			dist2obj = 2.5f;
		else if (dist2obj > 30.0f)
			dist2obj = 30.0f;
	}

	// moves the camera forward relative to its current rotation (yaw)
	public void walkForward(float distance) {
		m_nX -= distance * (float) Math.sin(Math.toRadians(yaw));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw));
		m_nZ -= distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		m_nX -= distance * (float) Math.sin(Math.toRadians(yaw - 90));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw - 90));
	}

	// strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance) {
		m_nX -= distance * (float) Math.sin(Math.toRadians(yaw + 90));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw + 90));
	}

	// translates and rotate the matrix so that it looks through the camera
	// this dose what gluLookAt() does
	public void lookThrough() {
		// roatate the pitch around the X axis
		GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		// roatate the yaw around the Y axis
		GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		// translate to the position vector's location
		GL11.glTranslatef(m_nX, m_nY, m_nZ);
	}
}