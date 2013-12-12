public abstract class Model3D {
	
	protected float m_nX, m_nY, m_nZ;
	protected float m_rX, m_rY, m_rZ;
	protected float m_sX = 1, m_sY = 1, m_sZ = 1;

	protected float yaw = 0.0f;
	protected float pitch = 0.0f;

	public Model3D() {	
		
	}
	
	public Model3D(float x, float y, float z) {
		setPosition(x, y, z);
	}
	
	public void setPosition(float p_X, float p_Y, float p_Z) {
		m_nX = p_X;
		m_nY = p_Y;
		m_nZ = p_Z;
	}

	public float[] getPosition() {
		return new float[] { m_nX, m_nY, m_nZ };
	}

	public void setRotation(float p_X, float p_Y, float p_Z) {
		m_rX = p_X;
		m_rY = p_Y;
		m_rZ = p_Z;
	}

	public void setScaling(float p_X, float p_Y, float p_Z) {
		m_sX = p_X;
		m_sY = p_Y;
		m_sZ = p_Z;
	}

	// increment current yaw rotation
	public void yaw(float amount) {
		yaw += amount;
	}

	public float getJaw() {
		return yaw;
	}

	public void setJaw(float yaw) {
		this.yaw = yaw;
	}

	// decrement current pitch rotation
	public void pitch(float amount) {
		pitch -= amount;
		if (pitch > 45)
			pitch = 45;
		else if (pitch < -45)
			pitch = -45;
	}

	public float getPitch() {
		return pitch;
	}

	// moves the object forward relative to its current rotation (yaw)
	public void walkForward(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// moves the object backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw + 180));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw + 180));
	}

	// strafes the object left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw + 90));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw + 90));
	}

	// strafes the object right relitive to its current rotation (yaw)
	public void strafeRight(float distance) {
		m_nX += distance * (float) Math.sin(Math.toRadians(yaw - 90));
		m_nZ += distance * (float) Math.cos(Math.toRadians(yaw - 90));
	}

	public float[] calcNormal(float[] v1, float[] v2, float[] v3) {
		float[] vec1 = substract(v2, v1);
		float[] vec2 = substract(v3, v1);
		return new float[] { vec1[1] * vec2[2] - vec1[2] * vec2[1],
				vec1[2] * vec2[0] - vec1[0] * vec2[2],
				vec1[0] * vec2[1] - vec1[1] * vec2[0] };
	}

	private float[] substract(float[] v1, float[] v2) {
		return new float[] { v1[0] - v2[0], v1[1] - v2[1], v1[2] - v2[2] };
	}
}