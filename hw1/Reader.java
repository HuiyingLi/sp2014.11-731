import java.io.*;
import java.util.*;
public class Reader{
    public static List<SentPair> readParallelCorpus(String fParaCorp){
        List<SentPair> text = new ArrayList<SentPair>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fParaCorp));
            String line=null;
            while((line=br.readLine())!=null){
                String[] spl=line.split(" \\|\\|\\| ") ;
                String[] f=Util.normalizeText(spl[0]);
                String[] e=Util.normalizeText(spl[1]);
                text.add(new SentPair(f, e));
            }
        }catch(IOException e){
        }
        return text;
    }
    public static List<SentPair> readParallelCorpus(String fParaCorp, boolean reverse){
        List<SentPair> text = new ArrayList<SentPair>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fParaCorp));
            String line=null;
            while((line=br.readLine())!=null){
                String[] spl=line.split(" \\|\\|\\| ") ;
                String[] e=null;
                String[] f=null;
                if(reverse){
                	f=Util.normalizeText(spl[1]);
                	e=Util.normalizeText(spl[0]);
                }
                else{
                	f=Util.normalizeText(spl[0]);
                    e=Util.normalizeText(spl[1]);
                }
                text.add(new SentPair(f, e));
            }
        }catch(IOException e){
        }
        return text;
    }
    public static List<int[][]>readAlign(String fAlign){
        List<int[][]> align=new ArrayList<int[][]>();
        try{
            BufferedReader br=new BufferedReader(new FileReader(fAlign));
            String line=null;
            while((line=br.readLine())!=null){
                String[] spl=line.split(" ");
                int[][] al=new int[spl.length][2];
                for(int i = 0; i < spl.length; i++){
                    String[] spl2=spl[i].split("-|\\?");
                    al[i][0]=Integer.parseInt(spl2[0]);
                    al[i][1]=Integer.parseInt(spl2[1]);
                }
                align.add(al);
            }
        }catch(IOException e){
        	e.printStackTrace();
        }
        return align;        
    }

    //test case
    public static void main(String args[]){

    }
}
