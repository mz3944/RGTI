public class ModelCharacterObj extends Model3D {

	private boolean isPlayer = false;
	private boolean isMoving = false;
	float movingAngle;
	float collisionDeltaTime = 0;

	public ModelCharacterObj(float x, float y, float z) {
		super(x, y, z);
	}

	public void setJaw(float yaw) {
		if (collisionDeltaTime == 0)
			this.yaw = yaw;
	}

	public boolean getIsPlayer() {
		return isPlayer;
	}

	public void setIsPlayer(boolean isPlayer) {
		this.isPlayer = isPlayer;
	}

	public void setIsMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public void updateWait(float dt) {
		collisionDeltaTime -= dt;
		if (collisionDeltaTime < 0)
			collisionDeltaTime = 0;
	}

	public void setMovingDirection(boolean[] movingDirection) {
		if (movingDirection[0] && movingDirection[1])
			movingAngle = 45;
		else if (movingDirection[1] && movingDirection[2])
			movingAngle = 135;
		else if (movingDirection[2] && movingDirection[3])
			movingAngle = -135;
		else if (movingDirection[3] && movingDirection[0])
			movingAngle = -45;
		else if (movingDirection[0])
			movingAngle = 0;
		else if (movingDirection[1])
			movingAngle = 90;
		else if (movingDirection[2])
			movingAngle = 180;
		else if (movingDirection[3])
			movingAngle = -90;
	}

	public boolean checkBounds(float x, float z, int distanceView) {
		boolean overBounds = false;
		if (m_nX > (x / 2 - distanceView) - 1) {
			m_nX = (x / 2 - distanceView) - 1;
			overBounds = true;
		}
		if (m_nX < -(x / 2 - distanceView)) {
			m_nX = -(x / 2 - distanceView);
			overBounds = true;
		}
		if (m_nZ > (z / 2 - distanceView) - 1) {
			m_nZ = (z / 2 - distanceView) - 1;
			overBounds = true;
		}
		if (m_nZ < -(z / 2 - distanceView)) {
			m_nZ = -(z / 2 - distanceView);
			overBounds = true;
		}

		return overBounds;
	}

	public void calcY(float[] p1, float[] p2, float[] p3, int MAP_X, int MAP_Z) {
		float det = (p2[2] - p3[2]) * (p1[0] - p3[0]) + (p3[0] - p2[0])
				* (p1[2] - p3[2]);

		float l1 = ((p2[2] - p3[2]) * (m_nX - p3[0]) + (p3[0] - p2[0])
				* (m_nZ - p3[2]))
				/ det;
		float l2 = ((p3[2] - p1[2]) * (m_nX - p3[0]) + (p1[0] - p3[0])
				* (m_nZ - p3[2]))
				/ det;
		float l3 = 1.0f - l1 - l2;

		m_nY = (l1 * p1[1] + l2 * p2[1] + l3 * p3[1]) + 0.3f;
	}

	public float getDistance(float x, float y) {
		float dx = m_nX - x;
		float dy = m_nZ - y;
		float d = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

		return d;
	}

	public boolean checkObjCollision(float x, float y, float yaw, float radius,
			boolean isPlayer) {
		if (!this.isPlayer && !isMoving)
			return false;

		float d = getDistance(x, y);
		if (d < radius) {
			if (!this.isPlayer && collisionDeltaTime == 0)
				collisionDeltaTime = 1.0f;
			float boundDist = radius - d;
			m_nX -= boundDist
					* (float) Math.sin(Math.toRadians(this.yaw + movingAngle));
			m_nZ -= boundDist
					* (float) Math.cos(Math.toRadians(this.yaw + movingAngle));

			if (!isPlayer && !this.isPlayer)
				yaw(Main.getRandomNumber(-180, 180));
			return true;
		}
		return false;
	}

	public void objOnObjPossition(float[] objposition, float objyaw,
			float dist2obj) {
		yaw = 180 - objyaw;
		m_nX += dist2obj * (float) Math.sin(Math.toRadians(yaw));
		m_nZ -= dist2obj * (float) Math.cos(Math.toRadians(yaw));
	}
}