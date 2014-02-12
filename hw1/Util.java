import java.util.*;
public class Util{
	public static String[] normalizeText(String sent){
		String[] split=sent.split(" ");
		for(int i = 0; i < split.length; i++){
			split[i]=split[i].toLowerCase();
//			Stemmer s = new Stemmer();
//			for(int j=0; j<split[i].length(); j++)
//			{
//				s.add(split[i].charAt(j));
//				s.stem();
//				split[i]=s.toString();
//			}
		}
		return split;
	}
    public static double[] randomInitArray(int n){
        //Currently fixed for debug purpose
        double [] arr=new double[n];
        for(int i=0; i<n; i++){
            arr[i]=0.3;
        }
        return arr;
    }
    public static double[][] randomInitMatrix(int n1, int n2){
        //Currently fixed for debug purpose
        double [][] m=new double[n1][n2];
        for(int i = 0; i < n1; i++){
            for(int j = 0; j < n2; j++){
                m[n1][n2]=0.3;
            }
        }
        return m;
    }
    
    public static double computeDiagNorm(int i,int m,int l, double tension){
    	if(l==0||m==0)
    		return 1.0;
    	double Z=0.0;
    	for(int j = 1; j <= l; j++){
    		Z+=Math.exp(-1*Math.abs(j*1.0/l-(i+1)*1.0/m)*tension);
    	}
    	return Z;
    }

    public static HashSet<String> genENVocab(List<SentPair> text){
        HashSet<String> vocab=new HashSet<String>();
        for(SentPair pair:text){
            String[] enlist=pair.en; 
            for(String s:enlist){
                //Do something fancy here
                vocab.add(s);
            }
        }
        return vocab;
    }
    
}
