package personalityEmpathy;

import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.language_translator.v3.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v3.model.IdentifiedLanguage;
import com.ibm.watson.developer_cloud.language_translator.v3.model.IdentifiedLanguages;
import com.ibm.watson.developer_cloud.language_translator.v3.model.IdentifyOptions;
import com.ibm.watson.developer_cloud.language_translator.v3.model.IdentifyOptions.Builder;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.developer_cloud.language_translator.v3.model.Translation;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslationResult;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import weka.classifiers.functions.LibSVM;

@Path("UserService")
public class UserService
{
	@POST
	@Path("userPersonality")
	@Produces({"application/json"})
	@Consumes({"application/json"})
	public ResultsPersonality getPersonality(User u)
			throws Exception
	{
		LanguageTranslator languageTranslator = new LanguageTranslator("2018-03-05");
		languageTranslator.setUsernameAndPassword("bd887565-3a26-4b2e-9177-d013507d6abf", "bleF87DI65Ts");

		String msgConcat = Utils.concatenateMessages(u.messages);
		IdentifyOptions options = new IdentifyOptions.Builder().text(msgConcat).build();
		IdentifiedLanguages languages = (IdentifiedLanguages)languageTranslator.identify(options).execute();
		if (!((IdentifiedLanguage)languages.getLanguages().get(0)).getLanguage().equals("en")) {
			for (int i = 0; i < u.messages.size(); i++)
			{
				try {
					TranslateOptions translateOptions = new TranslateOptions.Builder().addText((String)u.messages.get(i)).modelId(((IdentifiedLanguage)languages.getLanguages().get(0)).getLanguage() + "-en").build();
					TranslationResult result = (TranslationResult)languageTranslator.translate(translateOptions).execute();
					u.messages.set(i, ((Translation)result.getTranslations().get(0)).getTranslationOutput());
					System.out.println(((Translation)result.getTranslations().get(0)).getTranslationOutput());
				}catch(Exception e) {
					//si lasciano i messaggi così come sono
				}
			}
		}

		UserPersonality up = Utils.convertUserToUserPersonality(u);

		float neu = EstimatePersonality.getNeu(Utils.getInstanceFromUserForPersonality(up, "neu"));
		float agr = EstimatePersonality.getAgr(Utils.getInstanceFromUserForPersonality(up, "agr"));
		float ext = EstimatePersonality.getExt(Utils.getInstanceFromUserForPersonality(up, "ext"));
		float ope = EstimatePersonality.getOpe(Utils.getInstanceFromUserForPersonality(up, "ope"));
		float con = EstimatePersonality.getCon(Utils.getInstanceFromUserForPersonality(up, "con"));

		ResultsPersonality res = new ResultsPersonality(neu, agr, ext, ope, con);
		return res;
	}

	@POST
	@Path("userEmpathy")
	@Produces({"application/json"})
	@Consumes({"application/json"})
	public ResultsEmpathy getEmpathy(User u)
			throws Exception
	{
		LanguageTranslator languageTranslator = new LanguageTranslator("2018-03-05");
		languageTranslator.setUsernameAndPassword("bd887565-3a26-4b2e-9177-d013507d6abf", "bleF87DI65Ts");

		String msgConcat = Utils.concatenateMessages(u.messages);
		IdentifyOptions options = new IdentifyOptions.Builder().text(msgConcat).build();

		IdentifiedLanguages languages = (IdentifiedLanguages)languageTranslator.identify(options).execute();
		if (!((IdentifiedLanguage)languages.getLanguages().get(0)).getLanguage().equals("en")) {
			for (int i = 0; i < u.messages.size(); i++)
			{
				try {
					TranslateOptions translateOptions = new TranslateOptions.Builder().addText((String)u.messages.get(i)).modelId(((IdentifiedLanguage)languages.getLanguages().get(0)).getLanguage() + "-en").build();
					TranslationResult result = (TranslationResult)languageTranslator.translate(translateOptions).execute();
					u.messages.set(i, ((Translation)result.getTranslations().get(0)).getTranslationOutput());
					System.out.println(((Translation)result.getTranslations().get(0)).getTranslationOutput());
				}catch(Exception e) {
					//lascia i messaggi come sono
				}
			}
		}
		UserPersonality up = Utils.convertUserToUserPersonality(u);

		float neu = EstimatePersonality.getNeu(Utils.getInstanceFromUserForPersonality(up, "neu"));
		float agr = EstimatePersonality.getAgr(Utils.getInstanceFromUserForPersonality(up, "agr"));
		float ext = EstimatePersonality.getExt(Utils.getInstanceFromUserForPersonality(up, "ext"));
		float ope = EstimatePersonality.getOpe(Utils.getInstanceFromUserForPersonality(up, "ope"));
		float con = EstimatePersonality.getCon(Utils.getInstanceFromUserForPersonality(up, "con"));
		UserEmpathy ue = Utils.convertUserToUserEmpathy(up, neu, agr, ext, ope, con);
		float empathy = EstimatePersonality.getEmpathy(Utils.getInstanceFromUserForEmpathy(ue));
		return new ResultsEmpathy(empathy);
	}

	@POST
	@Path("opinionMining")
	@Produces({"application/json"})
	@Consumes({"application/json"})
	public ResultsOpinionMining getOpinionMining(UserOpinionMining u)
			throws Exception
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/media/mynewdrive/SHARED/develop/apache-tomcat-8.0.28/Models/PersonalityEmpathy/LibSVM2.model"));
		LibSVM classifier = (LibSVM)ois.readObject();
		ResultsOpinionMining res = new ResultsOpinionMining();
		LanguageTranslator languageTranslator = new LanguageTranslator("2018-03-05");
		languageTranslator.setUsernameAndPassword("bd887565-3a26-4b2e-9177-d013507d6abf", "bleF87DI65Ts");
		for (int i = 0; i < u.messages.size(); i++)
		{
			IdentifyOptions options = new IdentifyOptions.Builder().text((String)u.messages.get(i)).build();
			IdentifiedLanguages languages = (IdentifiedLanguages)languageTranslator.identify(options).execute();
			if (!((IdentifiedLanguage)languages.getLanguages().get(0)).getLanguage().equals("en"))
			{
				try {
					TranslateOptions translateOptions = new TranslateOptions.Builder().addText((String)u.messages.get(i)).modelId(((IdentifiedLanguage)languages.getLanguages().get(0)).getLanguage() + "-en").build();
					TranslationResult result = (TranslationResult)languageTranslator.translate(translateOptions).execute();
					u.messages.set(i, ((Translation)result.getTranslations().get(0)).getTranslationOutput());
				}catch(Exception e) {
					//lascia il messaggio così
				}
			}
		}
		System.out.println("prova");
		for (int i = 0; i < u.messages.size(); i++)
		{
			String key = new String();
			String topic = EstimatePersonality.estimateTopic(Utils.getInstanceFromText((String)u.messages.get(i)), classifier);

			key = key + topic + "_";
			try
			{
				URL url = new URL("http://90.147.170.25:8080/emotion-labeling/rest/analyze/emotionalLabeling");
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				String input = "text=" + (String)u.messages.get(i);

				OutputStream os = conn.getOutputStream();
				os.write(input.getBytes());
				os.flush();

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				System.out.println("Output from Server .... \n");
				String output;
				while ((output = br.readLine()) != null)
				{
					key = key + output;
				}
				conn.disconnect();
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if (res.results.containsKey(key)) {
				res.results.put(key, new Integer(((Integer)res.results.get(key)).intValue() + 1));
			} else {
				res.results.put(key, new Integer(1));
			}
		}
		System.out.println(res);
		return res;
	}
}
