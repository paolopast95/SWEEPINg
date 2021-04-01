package personalityEmpathy;


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;

public class EstimatePersonality
{
	public static float getNeu(Instances inst)
			throws Exception
	{
		FileInputStream fis = new FileInputStream("/media/mynewdrive/SHARED/develop/apache-tomcat-8.0.28/Models/PersonalityEmpathy/conf8neu.model");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FilteredClassifier regressor = (FilteredClassifier)ois.readObject();
		return (float)(regressor.classifyInstance(inst.get(0)) / 5.0D);
	}

	public static float getAgr(Instances inst)
			throws Exception
	{
		FileInputStream fis = new FileInputStream("/media/mynewdrive/SHARED/develop/apache-tomcat-8.0.28/Models/PersonalityEmpathy/conf8agr.model");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FilteredClassifier regressor = (FilteredClassifier)ois.readObject();
		return (float)(regressor.classifyInstance(inst.get(0)) / 5.0D);
	}

	public static float getCon(Instances inst)
			throws Exception
	{
		FileInputStream fis = new FileInputStream("/media/mynewdrive/SHARED/develop/apache-tomcat-8.0.28/Models/PersonalityEmpathy/conf8con.model");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FilteredClassifier regressor = (FilteredClassifier)ois.readObject();
		return (float)(regressor.classifyInstance(inst.get(0)) / 5.0D);
	}

	public static float getOpe(Instances inst)
			throws Exception
	{
		FileInputStream fis = new FileInputStream("/media/mynewdrive/SHARED/develop/apache-tomcat-8.0.28/Models/PersonalityEmpathy/conf8ope.model");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FilteredClassifier regressor = (FilteredClassifier)ois.readObject();
		return (float)(regressor.classifyInstance(inst.get(0)) / 5.0D);
	}

	public static float getExt(Instances inst)
			throws Exception
	{
		FileInputStream fis = new FileInputStream("/media/mynewdrive/SHARED/develop/apache-tomcat-8.0.28/Models/PersonalityEmpathy/conf8ext.model");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FilteredClassifier regressor = (FilteredClassifier)ois.readObject();
		return (float)(regressor.classifyInstance(inst.get(0)) / 5.0D);
	}

	public static float getEmpathy(Instances inst)
			throws Exception
	{
		FileInputStream fis = new FileInputStream("/media/mynewdrive/SHARED/develop/apache-tomcat-8.0.28/Models/PersonalityEmpathy/conf11empathy.model");
		ObjectInputStream ois = new ObjectInputStream(fis);
		FilteredClassifier regressor = (FilteredClassifier)ois.readObject();
		return (float)regressor.classifyInstance(inst.get(0));
	}

	public static String estimateTopic(Instances data, LibSVM classifier)
			throws Exception
	{
		int value = (int)classifier.classifyInstance(data.get(0));
		switch (value)
		{
		case 0: 
			return "sport";
		case 1: 
			return "style";
		case 2: 
			return "travel";
		case 3: 
			return "politics";
		case 4: 
			return "movie";
		case 5: 
			return "tech";
		case 6: 
			return "art";
		case 7: 
			return "music";
		}
		return "";
	}

}
