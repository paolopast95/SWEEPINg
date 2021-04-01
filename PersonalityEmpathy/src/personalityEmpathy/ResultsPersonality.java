package personalityEmpathy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResultsPersonality
  implements Serializable
{
  public float neu;
  public float agr;
  public float ext;
  public float ope;
  public float con;
  
  public ResultsPersonality() {}
  
  public ResultsPersonality(float n, float a, float e, float o, float c)
  {
    this.neu = n;
    this.agr = a;
    this.ext = e;
    this.ope = o;
    this.con = c;
  }
}
