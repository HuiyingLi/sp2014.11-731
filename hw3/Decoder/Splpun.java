import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import java.io.InputStreamReader;



public class Splpun {
	public static boolean debug=true;
	public LM lm=new LM("data/lm");
	public TM tm=new TM("data/tm");
	public double tension=5;
	//public int hypoNbest=Integer.MAX_VALUE;
	public int hypoNbest=100;
	public double wordPenalty;
	public String logpath="log/stack.log";
	public boolean log=false;
	private ArrayList<Hashtable<String, Hypothesis>> createStackDS(int sentL){
		ArrayList<Hashtable<String, Hypothesis>> stacks= new ArrayList<Hashtable<String, Hypothesis>>();//The String key is the lm_state
		for(int i = 0; i < sentL+1; i++)//len(f)+1
			stacks.add(new Hashtable<String,Hypothesis>());
		return stacks;
	}
	
	public String decode(String[] src) throws IOException{
		BufferedWriter logwriter = new BufferedWriter(new FileWriter(logpath));
		//initial hypothesis
		Hypothesis initHypo= new Hypothesis(0.0, "<s>", null, null);
		ArrayList<Hashtable<String ,Hypothesis>> stacks=createStackDS(src.length);
		stacks.get(0).put("<s>",initHypo);
		
		for(int i = 0; i < stacks.size(); i++){//for each stack in the stack list
			Hashtable<String, Hypothesis> stack=stacks.get(i);
			//sort the hypothesis within the stack with hypothesis.logprob
			
			ArrayList<Hypothesis> allhypo=new ArrayList<Hypothesis>(stack.values());
			Collections.sort(allhypo);
			logwriter.write("++++++++++++++"+i+"\n\n");
			
			for(Hypothesis h:allhypo){
				logwriter.write(h.phrase+"\t"+h.lm_state+"\t"+h.logprob+"\n");
			}
			for(int m=0; m<this.hypoNbest && m < stack.size(); m++){//for h in heapq:nlargest (prune)
				Hypothesis hypo = allhypo.get(m);
				for(int k=0; k<src.length; k++)//for each starting word in src (f) language and not only the first i words
				{					
					
					for(int j=k+1; j<src.length; j++){//for j in xrange(i+1,len(f)+1)
					//for(int j=i+1; j<src.length+1; j++){
						String ngram="";
						ArrayList<Integer> coveredInd=new ArrayList<Integer>();
						for(int t=k; t<j; t++)
						//for(int t=i; t<j; t++)
						{
							//if not in covered.. if in, break.
							if(hypo.covered.contains(t))
								break;
							coveredInd.add(t);
							ngram+=src[t]+" ";
						}
						ngram=ngram.trim();//f[i:j]
						ArrayList<Pair> trans=this.tm.get(ngram);
						if(trans!=null){//if f[i:j] in tm
							for(Pair phrase:trans){//for phrase in tm[f[i:j]]
								double logProb=hypo.logprob+phrase.logprob;
								String lm_state=hypo.lm_state;
								String[] phrasespl= phrase.text.split(" ");
								for(String word:phrasespl){
									String result=this.lm.score(lm_state, word);
									String[] resspl=result.split("\t");
									logProb+=Double.parseDouble(resspl[1]);
									lm_state=resspl[0];
								}//end of this loop, we get full lm of this e-phrase. 
								//Can add WordPenalty here:logprob+=wordpenalty
								logProb-=Math.log(phrasespl.length);
								//logProb-=Math.log(phrasespl.length);
								Pair newp= new Pair(phrase.text, phrase.logprob);
								if(debug){
									newp.text=ngram+"->"+phrase.text;
								}
								Hypothesis newHypo=new Hypothesis(logProb, lm_state, hypo,newp);
								newHypo.covered.addAll(hypo.covered);//add covered indices from previous steps
								newHypo.covered.addAll(coveredInd);//add indices covered in this step
								int stackToPut=newHypo.covered.size();
								
								if(stackToPut==src.length){//logprob += lm.end(lm_state) if j == len(f) else 0.0
									newHypo.logprob+=lm.end(lm_state);
								}
								
								//position distortion
								newHypo.logprob-=Math.log(Math.pow(tension, Math.abs(k-i+1)>1?Math.abs(k-i+1):0));
								//newHypo.logprob-=Math.log(Math.pow(tension, Math.abs(k-stackToPut+1)));
								if(!stacks.get(stackToPut).containsKey(lm_state)||stacks.get(stackToPut).get(lm_state).logprob<logProb){//second case is recombination
									stacks.get(stackToPut).put(lm_state, newHypo);	
								}
							}
						}
						else{
							//add thing so that this is a negative max hypothesis?
						}
						
					}
				}
			}
		}/*End of building the entire stack*/
		//sort the last stack of stacks and find the best hypothesis
		Hashtable<String, Hypothesis> stack = stacks.get(stacks.size()-2);
		ArrayList<Hypothesis> allhypo=new ArrayList<Hypothesis>(stack.values());
		//Find max:
		Hypothesis winner=null;
		double maxprob=-Double.MAX_VALUE;
		for (Hypothesis h:allhypo)
			if(h.logprob>maxprob){
				maxprob=h.logprob;
				winner=h;
			}
		//Hypothesis winner =Collections.max(allhypo);
		return extractEnglishRecursive(winner);
	}
	
	public String extractEnglishRecursive(Hypothesis h){
		if(h.predecessor==null){
			return "";
		}
		else{
			String content=h.phrase.text;
			if(debug){
				if(content.contains("->"))
					content=content.split("->")[1];
			}
			return extractEnglishRecursive(h.predecessor)+" "+content; 
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Splpun decoder = new Splpun();
		try {
			BufferedReader br = new BufferedReader(new FileReader("data/input.puntspl"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			String line=null;
			String complete="";
			while((line=br.readLine())!=null){
				//line="sin embargo , no vemos que se adopten los impuestos al carbono .";
				line=line.trim();
				if(line.equals("--")){
					bw.write(complete.trim()+"\n");
					bw.flush();
					complete="";
				}
				else{
					String result = decoder.decode(line.trim().split(" "));
					complete+=" "+result.trim()+" "+line.trim().split(" ")[line.trim().split(" ").length-1];
				}
				//bw.write(result+line.substring(line.length()-2)+"\n");
				//bw.flush();
				
		
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
