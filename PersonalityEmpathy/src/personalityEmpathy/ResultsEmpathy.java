package personalityEmpathy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResultsEmpathy
  implements Serializable
{
  public float empathy;
  
  public ResultsEmpathy() {}
  
  public ResultsEmpathy(float e)
  {
    this.empathy = e;
  }
}
