package personalityEmpathy;

public class UserPersonality
{
  public int gender;
  public int age;
  public int relationship_status;
  public int interested_in;
  public int mf_relationship;
  public int mf_dating;
  public int mf_random;
  public int mf_friendship;
  public int mf_whatever;
  public int mf_networking;
  public int network_size;
  public int timezone;
  public int n_like;
  public int n_status;
  public int n_event;
  public int n_concentration;
  public int n_group;
  public int n_work;
  public int n_education;
  public int n_tag;
  public int n_diads;
  public double[] w2v;
  
  public UserPersonality()
  {
    this.w2v = new double[300];
  }
  
  public void setW2V(double[] f)
  {
    if (f.length == 300) {
      for (int i = 0; i < 300; i++) {
        this.w2v[i] = f[i];
      }
    }
  }
}
