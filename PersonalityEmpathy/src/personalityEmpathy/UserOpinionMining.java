package personalityEmpathy;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="userOpinion")
public class UserOpinionMining
  implements Serializable
{
  public ArrayList<String> messages;
  
  public UserOpinionMining()
  {
    this.messages = new ArrayList();
  }
}
