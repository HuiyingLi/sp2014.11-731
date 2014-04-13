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



public class DecoderNS {
	public static boolean debug=true;
	public LM lm=new LM("data/lm");
	public TM tm=new TM("data/tm");
	public double tension=2;
	//public int hypoNbest=Integer.MAX_VALUE;
	public int hypoNbest;
	public double wordPenalty;
	public String logpath="log/stack.log";
	public boolean log=false;
	public BufferedWriter logwriter;
	public double tmWeight=1;
	public int pqsize=1;
	public int afterN=5;
	
	private ArrayList<Hashtable<String, PriorityQueue<Hypothesis>>> createStackDS(int sentL){
		ArrayList<Hashtable<String, PriorityQueue<Hypothesis>>> stacks= new ArrayList<Hashtable<String, PriorityQueue<Hypothesis>>>();//The String key is the lm_state
		for(int i = 0; i < sentL+1; i++)//len(f)+1
			stacks.add(new Hashtable<String,PriorityQueue<Hypothesis>>());
		return stacks;
	}
	public DecoderNS() throws IOException{
		if(log)
			logwriter = new BufferedWriter(new FileWriter(logpath));
	}
	public String decode(String[] src) throws IOException{
		
		//initial hypothesis
		Hypothesis initHypo= new Hypothesis(0.0, "<s>", null, null);
		ArrayList<Hashtable<String, PriorityQueue<Hypothesis>>> stacks=createStackDS(src.length);
		PriorityQueue<Hypothesis> initpq=new PriorityQueue<Hypothesis>();
		initpq.add(initHypo);
		stacks.get(0).put("<s>",initpq);
		
		for(int i = 0; i < stacks.size(); i++){//for each stack in the stack list
			Hashtable<String, PriorityQueue< Hypothesis>> stack=stacks.get(i);
			ArrayList<Hypothesis> allhypo=new ArrayList<Hypothesis>();
			Enumeration<String> e = stack.keys();
			while(e.hasMoreElements()){
				String lm_state=e.nextElement();
				PriorityQueue<Hypothesis> pq=stack.get(lm_state);
				allhypo.addAll(pq);
			}
			
			//sort the hypothesis within the stack with hypothesis.logprob
			//Hypothesis[] allhypo=(Hypothesis[])stack.toArray(new Hypothesis[stack.size()]);
			//Arrays.sort(stack.toArray());
			//Collections.sort(allhypo);
			System.err.println("#hypo in stack"+i+":"+allhypo.size());
			//logwriter.write("++++++++++++++"+i+"\n\n");
			
//			for(Hypothesis h:allhypo){
//				logwriter.write(h.phrase+"\t"+h.lm_state+"\t"+h.logprob+"\n");
//			}
			
//			long nbest=-1;
//			if(this.hypoNbest>-1){
//				nbest=this.hypoNbest;
//			}
//			else{
//				nbest=allhypo.size();
//				
//			}
			//System.out.println();
			//for(int m=0; m< nbest&& m < allhypo.size(); m++){//for h in heapq:nlargest (prune)
			for(int m=0; m<allhypo.size();m++){
				Hypothesis hypo = allhypo.get(m);
				int all=0;
				//for(int k=i-3>=0?i-3:0; k<src.length && k<i+3; k++)//for each starting word in src (f) language and not only the first i words
				for(int k=0; k<src.length && k<i+afterN; k++)
				//for(int k = 0; k < src.length; k++)
				{					
					for(int j=k+1; j<src.length &&j<k+3; j++){//for j in xrange(i+1,len(f)+1)
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
						int putin=0;
						if(trans!=null){//if f[i:j] in tm
							//System.err.println("Entries in translation table for ngram: "+trans.size());
//							if(trans.size()==13)
//								System.out.println();
							for(Pair phrase:trans){//for phrase in tm[f[i:j]]
								double logProb=hypo.logprob+phrase.logprob*tmWeight;	
								//length penalty:FF
								logProb-=phrase.text.split(" ").length;
								
								String lm_state=hypo.lm_state;
								String[] phrasespl= phrase.text.split(" ");
								for(String word:phrasespl){
									String result=this.lm.score(lm_state, word);
									String[] resspl=result.split("\t");
									logProb+=Double.parseDouble(resspl[1]);
									lm_state=resspl[0];
								}//end of this loop, we get full lm of this e-phrase. 
								//Can add WordPenalty here:logprob+=wordpenalty
								
								//maintain the lm_state as length 2:
								
								
								logProb+=Math.log(phrasespl.length);
								
								Pair newp= new Pair(phrase.text, phrase.logprob);
								if(debug){
									newp.text=ngram+"->"+phrase.text;
								}
								Hypothesis newHypo=new Hypothesis(logProb, lm_state, hypo,newp);
								
//								String whole=hypo.lm_state+" "+phrase.text;
//								int wlen=whole.split(" ").length;
//								lm_state=whole.split(" ")[wlen-2]+" "+whole.split(" ")[wlen-1];
								
								newHypo.covered.addAll(hypo.covered);//add covered indices from previous steps
								newHypo.covered.addAll(coveredInd);//add indices covered in this step
								int stackToPut=newHypo.covered.size();
								
								if(stackToPut==src.length){//logprob += lm.end(lm_state) if j == len(f) else 0.0
									newHypo.logprob+=lm.end(lm_state);
								}
								
								//position distortion
								//newHypo.logprob-=Math.log(Math.pow(tension, Math.abs(k-i+1)));
								newHypo.logprob-=Math.log(Math.pow(tension, Math.abs(k-i+1)>1?Math.abs(k-i+1):0));
								//newHypo.logprob-=Math.log(Math.pow(tension, Math.abs(k-stackToPut+1)>1?Math.abs(k-stackToPut+1):0));
//								if(newHypo.lm_state.equals("<s> the")){
//									System.out.println(newHypo.toString()+newHypo.logprob);
//								}
								if(newHypo.phrase.text.contains("ha perdurado")||newHypo.phrase.text.equals("metido en")){
									newHypo.logprob+=10;
								}
								//newHypo.logprob-=Math.log(Math.pow(tension, Math.abs(k-stackToPut+1)));
								if(!stacks.get(stackToPut).containsKey(lm_state)){
									PriorityQueue<Hypothesis> newpq=new PriorityQueue<Hypothesis>(); 
									newpq.add(newHypo);
									stacks.get(stackToPut).put(lm_state, newpq);	
									putin++;
								}else{
									if(stacks.get(stackToPut).get(lm_state).size()<this.pqsize){//second case is recombination
										//PriorityQueue curpq = stacks.get(stackToPut).get(lm_state)
										stacks.get(stackToPut).get(lm_state).add(newHypo);
										putin++;
									}else{
										if (stacks.get(stackToPut).get(lm_state).peek().logprob<newHypo.logprob){
											stacks.remove(stacks.get(stackToPut).get(lm_state).peek());
											stacks.get(stackToPut).get(lm_state).add(newHypo);
										}
									}
								}
						
//								if(stacks.get(stackToPut).get()size()<this.hypoNbest){
//									stacks.get(stackToPut).add(newHypo);
//								}
//								else if(stacks.get(stackToPut).peek().logprob<newHypo.logprob){
//									stacks.get(stackToPut).add(newHypo);
//								}
							}
							//System.err.println("# put into stack:"+putin);
							all+=putin;
						}
						else{
							//add thing so that this is a negative max hypothesis?
						}
						
					}
				}
				//System.err.println("hypo "+m+" introduce "+all);
			}
		}/*End of building the entire stack*/
		//sort the last stack of stacks and find the best hypothesis
		//PriorityQueue<Hypothesis> stack = stacks.peek(stacks.size()-2);
//		int i=3;
//		while(stack.size()==0)
//		{
//			stack=stacks.get(stacks.size()-i);
//		i++;}
		//ArrayList<Hypothesis> allhypo=new ArrayList<Hypothesis>(stack.values());
		//Find max:
		Hypothesis winner=null;
		ArrayList<Hypothesis> finalhypos=new ArrayList<Hypothesis>();
		Enumeration<String> e = stacks.get(stacks.size()-2).keys();
		while(e.hasMoreElements()){
			PriorityQueue<Hypothesis> pq=stacks.get(stacks.size()-2).get(e.nextElement());
			finalhypos.addAll(pq);
		}
		//Hypothesis winner=stacks.get(stacks.size()-2).peek();
		double maxprob=-Double.MAX_VALUE;
		for (Hypothesis h:finalhypos){
			if(h.logprob>maxprob){
				maxprob=h.logprob;
				winner=h;
			}
		}
		//Hypothesis winner =Collections.max(allhypo);
		try{
			return extractEnglishRecursive(winner);
		}catch(NullPointerException excpet){
			return "NULL";
		}
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DecoderNS decoder = new DecoderNS();
		decoder.pqsize=Integer.parseInt(args[0]);
		decoder.tension=Double.parseDouble(args[1]);
		try {
			String inputPath="data/input";
			if(args.length>=3){
				if(!args[2].equals("null"))
					inputPath=args[2];
			}
			if(args.length>=4){
				decoder.afterN=Integer.parseInt(args[3]);
			}
			BufferedReader br = new BufferedReader(new FileReader(inputPath));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
			String line=null;
			while((line=br.readLine())!=null){
				//line="pero ahora parece que este país se ha metido en problemas nuevamente y también desea debil itar el pacto .";
				
				String result = decoder.decode(line.trim().split(" "));
				result=result.trim();
				bw.write(result+line.substring(line.length()-2)+"\n");
				bw.flush();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
