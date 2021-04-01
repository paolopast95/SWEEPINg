package personalityEmpathy;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResultsOpinionMining
{
  public HashMap<String, Integer> results;
  
  public ResultsOpinionMining()
  {
    this.results = new HashMap();
  }
}
