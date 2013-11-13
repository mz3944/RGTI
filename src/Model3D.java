
public abstract class Model3D
{
  protected float m_nX, m_nY, m_nZ;
  protected float m_rX, m_rY, m_rZ;
  protected float m_sX=1, m_sY=1, m_sZ=1;
  
  public void setPosition(float p_X, float p_Y, float p_Z)
  {
    m_nX=p_X; m_nY=p_Y; m_nZ=p_Z;
  }
  public void setRotation(float p_X, float p_Y, float p_Z)
  {
    m_rX=p_X; m_rY=p_Y; m_rZ=p_Z;
  }
  public void setScaling(float p_X, float p_Y, float p_Z)
  {
    m_sX=p_X; m_sY=p_Y; m_sZ=p_Z;
  }
  
  public abstract void render3D();
  
  public float[] calcNormal(float[] v1, float[] v2, float[] v3) {
  	float[] vec1 = substract(v2, v1);
	float[] vec2 = substract(v3, v1);
	return new float[] {vec1[1] * vec2[2] - vec1[2] * vec2[1], vec1[2] * vec2[0] - vec1[0] * vec2[2],  vec1[0] * vec2[1] - vec1[1] * vec2[0]};
  }
  
  private float[] substract(float[] v1, float[] v2) {
    return new float[] {v1[0]-v2[0], v1[1]-v2[1], v1[2]-v2[2]};
  }
}