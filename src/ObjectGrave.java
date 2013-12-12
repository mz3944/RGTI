
public class ObjectGrave extends Model3D {
	public ObjectGrave(float x, float y, float z, Terrain t) {
		float[][] triangle = t.getTrinagleLocation(x, z);
		float maxTemp = Math.max(triangle[0][1], triangle[1][1]);
		float maxY = Math.max(triangle[2][1], maxTemp);
		m_nX = x;
		m_nY = maxY + 0.3f;
		m_nZ = z;
	}
}
