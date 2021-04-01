package personalityEmpathy;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="users")
public class User
  implements Serializable
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
  public ArrayList<String> messages;
  
  public User()
  {
    this.gender = -1;
    this.age = -1;
    this.relationship_status = -1;
    this.interested_in = -1;
    this.mf_relationship = -1;
    this.mf_dating = -1;
    this.mf_random = -1;
    this.mf_friendship = -1;
    this.mf_whatever = -1;
    this.mf_networking = -1;
    this.network_size = -1;
    this.timezone = -1;
    this.n_like = -1;
    this.n_status = -1;
    this.n_event = -1;
    this.n_concentration = -1;
    this.n_group = -1;
    this.n_work = -1;
    this.n_education = -1;
    this.n_tag = -1;
    this.n_diads = -1;
    this.messages = new ArrayList();
  }
}
