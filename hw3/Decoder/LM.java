import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class LM {
	
	Hashtable<String, double[]> table=new Hashtable<String, double[]>();
	
	public LM(String LMpath){
		try {
			BufferedReader br = new BufferedReader(new FileReader(LMpath));
			String line=null;
			while((line=br.readLine())!=null){
				String [] entry=line.split("\t");
			
				if(entry.length>1 && !entry[0].equals("ngram")){
					double logprob=Double.parseDouble(entry[0]);
					String ngram=entry[1];
					double backoff=0.0;
					if(entry.length==3){
						backoff=Double.parseDouble(entry[2]);
					}
					double[] ngram_stat={logprob, backoff};
					this.table.put(ngram, ngram_stat);
				}
			}
			System.err.println("Finish loading language model.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LM(String LMpath, String filter){
		try {
			BufferedReader br = new BufferedReader(new FileReader(LMpath));
			String line=null;
			while((line=br.readLine())!=null){
				String [] entry=line.split("\t");
			
				if(entry.length>1 && !entry[0].equals("ngram")){
					double logprob=Double.parseDouble(entry[0]);
					
					String ngram=entry[1];
					double backoff=0.0;
					if(entry.length==3){
						backoff=Double.parseDouble(entry[2]);
					}
					double[] ngram_stat={logprob, backoff};
					if((ngram.split(" ").length==1 && logprob>-13)||(ngram.split(" ").length==2 &&logprob>-7.8 )||(ngram.split(" ").length==3 && logprob>-4)){
						this.table.put(ngram, ngram_stat);
					}
				}
			}
			System.err.println("Finish loading language model.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param state
	 * @param word
	 * @return the return value is a String separated by tab where the first part is the
	 * lm_state the second part is the logprob 
	 */
	public String score(String state, String word){
		String ngram=state+" "+word;
		double score=0.0;
		while(ngram.length()>0 &&ngram.split(" ").length>0){
			if (this.table.containsKey(ngram)){
				String lm_state=ngram;
				if(ngram.split(" ").length>2){
					lm_state=ngram.substring(ngram.indexOf(" ")+1);
				}
				score+=this.table.get(ngram)[0];
				return lm_state+"\t"+String.valueOf(score);
			}else{//backoff
				int l=ngram.split(" ").length;
				if(ngram.split(" ").length>1){
					String backgram="";
					for(int i=0; i<l-1; i++){
						backgram+=ngram.split(" ")[i]+" ";
					}
					backgram=backgram.trim();
					score+=this.table.get(backgram)[1];
					ngram=ngram.substring(ngram.indexOf(" ")+1);
				}
				else{
					ngram="";			
				}
			}
		}
		return "UNK\t"+this.table.get("<unk>")[0];
	}
	
	public Double end(String lm_state){
		//return Double.parseDouble(score(lm_state,"</s>").split("\t")[1]);
		return Double.parseDouble(score(lm_state,".").split("\t")[1]);
	}
	
	public static void main(String[] args){
		LM lm = new LM("data/lm");
		String s ="the army turkey";
		String lm_state="<s>";
		String[] phrasespl= s.split(" ");
		double logProb=0.0;
		for(String word:phrasespl){
			String result=lm.score(lm_state, word);
			String[] resspl=result.split("\t");
			logProb+=Double.parseDouble(resspl[1]);
			lm_state=resspl[0];
		}
//		String result = lm.score("iraq ;","</s>");
		System.out.println(logProb);
	}
}
