package personalityEmpathy;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class Utils
{
	public static UserPersonality convertUserToUserPersonality(User u)
			throws ClassNotFoundException, SQLException
	{
		UserPersonality up = new UserPersonality();
		up.gender = u.gender;
		up.age = u.age;
		up.relationship_status = u.relationship_status;
		up.interested_in = u.interested_in;
		up.mf_relationship = u.mf_relationship;
		up.mf_dating = u.mf_dating;
		up.mf_random = u.mf_random;
		up.mf_friendship = u.mf_friendship;
		up.mf_whatever = u.mf_whatever;
		up.mf_networking = u.mf_networking;
		up.network_size = u.network_size;
		up.timezone = u.timezone;
		up.n_like = u.n_like;
		up.n_status = u.n_status;
		up.n_event = u.n_event;
		up.n_concentration = u.n_concentration;
		up.n_group = u.n_group;
		up.n_work = u.n_work;
		up.n_education = u.n_education;
		up.n_tag = u.n_tag;
		up.n_diads = u.n_diads;
		double[] w2v = calculateW2V(u.messages);
		up.setW2V(w2v);
		return up;
	}

	public static String concatenateMessages(ArrayList<String> msg)
	{
		String msgConcat = new String("");
		for (int i = 0; i < msg.size(); i++) {
			msgConcat = msgConcat + (String)msg.get(i) + ".";
		}
		return msgConcat;
	}

	public static UserEmpathy convertUserToUserEmpathy(UserPersonality u, float neu, float agr, float ext, float ope, float con)
			throws ClassNotFoundException, SQLException
	{
		UserEmpathy up = new UserEmpathy();
		up.gender = u.gender;
		up.age = u.age;
		up.relationship_status = u.relationship_status;
		up.interested_in = u.interested_in;
		up.mf_relationship = u.mf_relationship;
		up.mf_dating = u.mf_dating;
		up.mf_random = u.mf_random;
		up.mf_friendship = u.mf_friendship;
		up.mf_whatever = u.mf_whatever;
		up.mf_networking = u.mf_networking;
		up.network_size = u.network_size;
		up.timezone = u.timezone;
		up.n_like = u.n_like;
		up.n_status = u.n_status;
		up.n_event = u.n_event;
		up.n_concentration = u.n_concentration;
		up.n_group = u.n_group;
		up.n_work = u.n_work;
		up.n_education = u.n_education;
		up.n_tag = u.n_tag;
		up.n_diads = u.n_diads;
		up.neu = neu;
		up.agr = agr;
		up.ext = ext;
		up.ope = ope;
		up.con = con;
		for (int i = 0; i < 300; i++) {
			up.w2v[i] = u.w2v[i];
		}
		return up;
	}

	public static double[] calculateW2V(ArrayList<String> sArray)
			throws SQLException, ClassNotFoundException
	{
		double[] res = new double[300];

		Arrays.fill(res, 0.0D);

		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/user_profile", "pastorep", "umA2nWa2");

		String s = concatenateMessages(sArray);

		String[] msgSplit = s.split("[\\W]");

		int numWords = 0;
		if (s.length() != 0) {
			for (int i = 0; i < msgSplit.length; i++)
			{
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM wordtovec WHERE f0 ='" + msgSplit[i] + "'");
				while (rs.next())
				{
					for (int j = 0; j < 300; j++) {
						res[j] += rs.getFloat(2 + j);
					}
					numWords++;
				}
			}
		}
		if (numWords > 0) {
			for (int i = 0; i < 300; i++) {
				res[i] = (res[i])/numWords;
			}
		}
		else if(numWords == 0) {
			for(int i = 0; i < 300; i++)
				res[i] = -1;
		}
		return res;
	}

	public static Instances getInstanceFromText(String s)
			throws ClassNotFoundException, SQLException
	{
		ArrayList<String> fvClassVal = new ArrayList();
		fvClassVal.add("0");
		fvClassVal.add("1");
		fvClassVal.add("2");
		fvClassVal.add("3");
		fvClassVal.add("4");
		fvClassVal.add("5");
		fvClassVal.add("6");
		fvClassVal.add("7");
		Attribute topic = new Attribute("class", fvClassVal);
		ArrayList<Attribute> fvWekaAttributes = new ArrayList(151);
		fvWekaAttributes.add(topic);
		for (int i = 0; i < 150; i++)
		{
			Attribute w2v = new Attribute("f" + i);
			fvWekaAttributes.add(w2v);
		}
		Instances i = new Instances("prova", fvWekaAttributes, 0);
		i.setClassIndex(0);
		double[] instance = new double[151];
		float[] w2v = getW2VFromMessage(s);
		for (int j = 0; j < 150; j++) {
			if (w2v[j] != 0.0F) {
				instance[(1 + j)] = w2v[j];
			} else {
				instance[(1 + j)] = weka.core.Utils.missingValue();
			}
		}
		i.add(new DenseInstance(0.0D, instance));

		return i;
	}

	private static float[] getW2VFromMessage(String s)
			throws ClassNotFoundException, SQLException
	{
		float[] res = new float[300];
		Arrays.fill(res, 0.0F);

		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/user_profile", "pastorep", "umA2nWa2");

		String[] msgSplit = s.split("[\\W]");

		int numWords = 0;
		if (s.length() != 0) {
			for (int i = 0; i < msgSplit.length; i++)
			{
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM corpus_w2v WHERE word ='" + msgSplit[i] + "'");
				while (rs.next())
				{
					for (int j = 0; j < 100; j++) {
						res[j] += rs.getFloat(2 + j);
					}
					numWords++;
				}
				System.out.println(i);
			}
		}
		if (numWords > 0) {
			for (int i = 0; i < 100; i++) {
				res[i] = (res[i])/numWords;
			}
		}
		else if (numWords == 0) {
			for(int i = 0; i < 300; i++)
				res[i] = -1;
		}
		numWords = 0;
		if (s.length() != 0) {
			for (int i = 0; i < msgSplit.length; i++)
			{
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM tweets_w2v WHERE word ='" + msgSplit[i] + "'");
				while (rs.next())
				{
					for (int j = 0; j < 50; j++) {
						res[j] += rs.getFloat(2 + j);
					}
					numWords++;
				}
			}
		}
		if (numWords > 0) {
			for (int i = 0; i < 50; i++) {
				res[i] /= numWords;
			}
		}
		return res;
	}

	public static Instances getInstanceFromUserForPersonality(UserPersonality u, String tratto)
	{
		Attribute gender = new Attribute("gender");

		Attribute age = new Attribute("age");
		ArrayList<String> fvClassVal = new ArrayList();
		fvClassVal.add("1");
		fvClassVal.add("2");
		fvClassVal.add("3");
		fvClassVal.add("4");
		fvClassVal.add("5");
		fvClassVal.add("6");
		fvClassVal.add("7");
		fvClassVal.add("8");
		fvClassVal.add("9");
		fvClassVal.add("10");
		fvClassVal.add("11");
		fvClassVal.add("12");
		Attribute relationship_status = new Attribute("relationship_status", fvClassVal);
		fvClassVal = new ArrayList();
		fvClassVal.add("1");
		fvClassVal.add("2");
		fvClassVal.add("3");
		Attribute interested_in = new Attribute("interested_in", fvClassVal);
		Attribute mf_relationship = new Attribute("mf_relationship");
		Attribute mf_dating = new Attribute("mf_dating");
		Attribute mf_random = new Attribute("mf_random");
		Attribute mf_friendship = new Attribute("mf_friendship");
		Attribute mf_whatever = new Attribute("mf_whatever");
		Attribute mf_networking = new Attribute("mf_networking");
		Attribute network_size = new Attribute("network_size");
		Attribute timezone = new Attribute("timezone");
		Attribute n_like = new Attribute("n_like");
		Attribute n_status = new Attribute("n_status");
		Attribute n_event = new Attribute("n_event");
		Attribute n_concentration = new Attribute("n_concentration");
		Attribute n_group = new Attribute("n_group");
		Attribute n_work = new Attribute("n_work");
		Attribute n_education = new Attribute("n_education");
		Attribute n_tag = new Attribute("n_tag");
		Attribute n_diads = new Attribute("n_diads");
		ArrayList<Attribute> fvWekaAttributes = new ArrayList(322);
		fvWekaAttributes.add(gender);
		fvWekaAttributes.add(age);
		fvWekaAttributes.add(relationship_status);
		fvWekaAttributes.add(interested_in);
		fvWekaAttributes.add(mf_relationship);
		fvWekaAttributes.add(mf_dating);
		fvWekaAttributes.add(mf_random);
		fvWekaAttributes.add(mf_friendship);
		fvWekaAttributes.add(mf_whatever);
		fvWekaAttributes.add(mf_networking);
		fvWekaAttributes.add(network_size);
		fvWekaAttributes.add(timezone);
		fvWekaAttributes.add(n_like);
		fvWekaAttributes.add(n_status);
		fvWekaAttributes.add(n_event);
		fvWekaAttributes.add(n_concentration);
		fvWekaAttributes.add(n_group);
		fvWekaAttributes.add(n_work);
		fvWekaAttributes.add(n_education);
		fvWekaAttributes.add(n_tag);
		fvWekaAttributes.add(n_diads);
		for (int i = 0; i < 300; i++)
		{
			Attribute w2v = new Attribute("f" + i);
			fvWekaAttributes.add(w2v);
		}
		fvWekaAttributes.add(new Attribute(tratto));
		Instances i = new Instances("prova", fvWekaAttributes, 0);
		i.setClassIndex(321);

		double[] instance = new double[322];
		if (u.gender != -1) {
			instance[0] = u.gender;
		} else {
			instance[0] = weka.core.Utils.missingValue();
		}
		if (u.age != -1) {
			instance[1] = u.age;
		} else {
			instance[1] = weka.core.Utils.missingValue();
		}
		if (u.relationship_status != -1) {
			instance[2] = u.relationship_status;
		} else {
			instance[2] = weka.core.Utils.missingValue();
		}
		if (u.interested_in != -1) {
			instance[3] = u.interested_in;
		} else {
			instance[3] = weka.core.Utils.missingValue();
		}
		if (u.mf_relationship != -1) {
			instance[4] = u.mf_relationship;
		} else {
			instance[4] = weka.core.Utils.missingValue();
		}
		if (u.mf_dating != -1) {
			instance[5] = u.mf_dating;
		} else {
			instance[5] = weka.core.Utils.missingValue();
		}
		if (u.mf_random != -1) {
			instance[6] = u.mf_random;
		} else {
			instance[6] = weka.core.Utils.missingValue();
		}
		if (u.mf_friendship != -1) {
			instance[7] = u.mf_friendship;
		} else {
			instance[7] = weka.core.Utils.missingValue();
		}
		if (u.mf_whatever != -1) {
			instance[8] = u.mf_whatever;
		} else {
			instance[8] = weka.core.Utils.missingValue();
		}
		if (u.mf_networking != -1) {
			instance[9] = u.mf_networking;
		} else {
			instance[9] = weka.core.Utils.missingValue();
		}
		if (u.network_size != -1) {
			instance[10] = u.network_size;
		} else {
			instance[10] = weka.core.Utils.missingValue();
		}
		if (u.timezone != -1) {
			instance[11] = u.timezone;
		} else {
			instance[11] = weka.core.Utils.missingValue();
		}
		if (u.n_like != -1) {
			instance[12] = u.n_like;
		} else {
			instance[12] = weka.core.Utils.missingValue();
		}
		if (u.n_status != -1) {
			instance[13] = u.n_status;
		} else {
			instance[13] = weka.core.Utils.missingValue();
		}
		if (u.n_event != -1) {
			instance[14] = u.n_event;
		} else {
			instance[14] = weka.core.Utils.missingValue();
		}
		if (u.n_concentration != -1) {
			instance[15] = u.n_concentration;
		} else {
			instance[15] = weka.core.Utils.missingValue();
		}
		if (u.n_group != -1) {
			instance[16] = u.n_group;
		} else {
			instance[16] = weka.core.Utils.missingValue();
		}
		if (u.n_work != -1) {
			instance[17] = u.n_work;
		} else {
			instance[17] = weka.core.Utils.missingValue();
		}
		if (u.n_education != -1) {
			instance[18] = u.n_education;
		} else {
			instance[18] = weka.core.Utils.missingValue();
		}
		if (u.n_tag != -1) {
			instance[19] = u.n_tag;
		} else {
			instance[19] = weka.core.Utils.missingValue();
		}
		if (u.n_diads != -1) {
			instance[20] = u.n_diads;
		} else {
			instance[20] = weka.core.Utils.missingValue();
		}
		for (int j = 0; j < 300; j++) {
			if (u.w2v[j] != 0.0D) {
				instance[(21 + j)] = u.w2v[j];
			} else {
				instance[(21 + j)] = weka.core.Utils.missingValue();
			}
		}
		i.add(new DenseInstance(0.0D, instance));

		return i;
	}

	public static Instances getInstanceFromUserForEmpathy(UserEmpathy u)
	{
		Attribute gender = new Attribute("gender");

		Attribute age = new Attribute("age");
		ArrayList<String> fvClassVal = new ArrayList();
		fvClassVal.add("1");
		fvClassVal.add("2");
		fvClassVal.add("3");
		fvClassVal.add("4");
		fvClassVal.add("5");
		fvClassVal.add("6");
		fvClassVal.add("7");
		fvClassVal.add("8");
		fvClassVal.add("9");
		fvClassVal.add("10");
		fvClassVal.add("11");
		fvClassVal.add("12");
		Attribute relationship_status = new Attribute("relationship_status", fvClassVal);
		fvClassVal = new ArrayList();
		fvClassVal.add("1");
		fvClassVal.add("2");
		fvClassVal.add("3");
		Attribute interested_in = new Attribute("interested_in", fvClassVal);
		Attribute mf_relationship = new Attribute("mf_relationship");
		Attribute mf_dating = new Attribute("mf_dating");
		Attribute mf_random = new Attribute("mf_random");
		Attribute mf_friendship = new Attribute("mf_friendship");
		Attribute mf_whatever = new Attribute("mf_whatever");
		Attribute mf_networking = new Attribute("mf_networking");
		Attribute network_size = new Attribute("network_size");
		Attribute timezone = new Attribute("timezone");
		Attribute n_like = new Attribute("n_like");
		Attribute n_status = new Attribute("n_status");
		Attribute n_event = new Attribute("n_event");
		Attribute n_concentration = new Attribute("n_concentration");
		Attribute n_group = new Attribute("n_group");
		Attribute n_work = new Attribute("n_work");
		Attribute n_education = new Attribute("n_education");
		Attribute n_tag = new Attribute("n_tag");
		Attribute n_diads = new Attribute("n_diads");
		ArrayList<Attribute> fvWekaAttributes = new ArrayList(27);
		fvWekaAttributes.add(gender);
		fvWekaAttributes.add(age);
		fvWekaAttributes.add(relationship_status);
		fvWekaAttributes.add(interested_in);
		fvWekaAttributes.add(mf_relationship);
		fvWekaAttributes.add(mf_dating);
		fvWekaAttributes.add(mf_random);
		fvWekaAttributes.add(mf_friendship);
		fvWekaAttributes.add(mf_whatever);
		fvWekaAttributes.add(mf_networking);
		fvWekaAttributes.add(network_size);
		fvWekaAttributes.add(timezone);
		fvWekaAttributes.add(n_like);
		fvWekaAttributes.add(n_status);
		fvWekaAttributes.add(n_event);
		fvWekaAttributes.add(n_concentration);
		fvWekaAttributes.add(n_group);
		fvWekaAttributes.add(n_work);
		fvWekaAttributes.add(n_education);
		fvWekaAttributes.add(n_tag);
		fvWekaAttributes.add(n_diads);
		for (int i = 0; i < 300; i++)
		{
			Attribute w2v = new Attribute("f" + i);
			fvWekaAttributes.add(w2v);
		}
		fvWekaAttributes.add(new Attribute("neu"));
		fvWekaAttributes.add(new Attribute("agr"));
		fvWekaAttributes.add(new Attribute("ext"));
		fvWekaAttributes.add(new Attribute("ope"));
		fvWekaAttributes.add(new Attribute("con"));
		fvWekaAttributes.add(new Attribute("empathy"));
		Instances i = new Instances("prova", fvWekaAttributes, 0);
		i.setClassIndex(326);

		double[] instance = new double[327];
		if (u.gender != -1) {
			instance[0] = u.gender;
		} else {
			instance[0] = weka.core.Utils.missingValue();
		}
		if (u.age != -1) {
			instance[1] = u.age;
		} else {
			instance[1] = weka.core.Utils.missingValue();
		}
		if (u.relationship_status != -1) {
			instance[2] = u.relationship_status;
		} else {
			instance[2] = weka.core.Utils.missingValue();
		}
		if (u.interested_in != -1) {
			instance[3] = u.interested_in;
		} else {
			instance[3] = weka.core.Utils.missingValue();
		}
		if (u.mf_relationship != -1) {
			instance[4] = u.mf_relationship;
		} else {
			instance[4] = weka.core.Utils.missingValue();
		}
		if (u.mf_dating != -1) {
			instance[5] = u.mf_dating;
		} else {
			instance[5] = weka.core.Utils.missingValue();
		}
		if (u.mf_random != -1) {
			instance[6] = u.mf_random;
		} else {
			instance[6] = weka.core.Utils.missingValue();
		}
		if (u.mf_friendship != -1) {
			instance[7] = u.mf_friendship;
		} else {
			instance[7] = weka.core.Utils.missingValue();
		}
		if (u.mf_whatever != -1) {
			instance[8] = u.mf_whatever;
		} else {
			instance[8] = weka.core.Utils.missingValue();
		}
		if (u.mf_networking != -1) {
			instance[9] = u.mf_networking;
		} else {
			instance[9] = weka.core.Utils.missingValue();
		}
		if (u.network_size != -1) {
			instance[10] = u.network_size;
		} else {
			instance[10] = weka.core.Utils.missingValue();
		}
		if (u.timezone != -1) {
			instance[11] = u.timezone;
		} else {
			instance[11] = weka.core.Utils.missingValue();
		}
		if (u.n_like != -1) {
			instance[12] = u.n_like;
		} else {
			instance[12] = weka.core.Utils.missingValue();
		}
		if (u.n_status != -1) {
			instance[13] = u.n_status;
		} else {
			instance[13] = weka.core.Utils.missingValue();
		}
		if (u.n_event != -1) {
			instance[14] = u.n_event;
		} else {
			instance[14] = weka.core.Utils.missingValue();
		}
		if (u.n_concentration != -1) {
			instance[15] = u.n_concentration;
		} else {
			instance[15] = weka.core.Utils.missingValue();
		}
		if (u.n_group != -1) {
			instance[16] = u.n_group;
		} else {
			instance[16] = weka.core.Utils.missingValue();
		}
		if (u.n_work != -1) {
			instance[17] = u.n_work;
		} else {
			instance[17] = weka.core.Utils.missingValue();
		}
		if (u.n_education != -1) {
			instance[18] = u.n_education;
		} else {
			instance[18] = weka.core.Utils.missingValue();
		}
		if (u.n_tag != -1) {
			instance[19] = u.n_tag;
		} else {
			instance[19] = weka.core.Utils.missingValue();
		}
		if (u.n_diads != -1) {
			instance[20] = u.n_diads;
		} else {
			instance[20] = weka.core.Utils.missingValue();
		}
		for (int j = 0; j < 300; j++) {
			if (u.w2v[j] != 0.0D) {
				instance[(21 + j)] = u.w2v[j];
			} else {
				instance[(21 + j)] = weka.core.Utils.missingValue();
			}
		}
		instance[320] = u.neu;
		instance[321] = u.agr;
		instance[322] = u.ext;
		instance[324] = u.ope;
		instance[325] = u.con;
		i.add(new DenseInstance(0.0D, instance));

		return i;
	}

	public static void main(String[] args)
			throws ClassNotFoundException, SQLException
	{
		getInstanceFromText(new String("Apple is going to lounch the new Iphone! Wow"));
	}
}
