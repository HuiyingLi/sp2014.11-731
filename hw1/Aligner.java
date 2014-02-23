import java.io.*;
import java.util.*;
public class Aligner{
    public List<SentPair> text=null;
    public int N=6; //By default
    public boolean useNull=false;
    public boolean diag=true;
    public double tension=15.5;
    public static boolean reverse=true;
    //translation table:
    //Hashtable<f_word,Hashtable<e_word, probability>>
    public Hashtable<String, Hashtable<String, Double>> TTable=new Hashtable<String, Hashtable<String, Double>>();
    public Hashtable<IndexPair, Hashtable<IndexPair, Double>> ATable=new Hashtable<IndexPair, Hashtable<IndexPair, Double>>();
    /**
     * @text: processed parallel corpus
     * @align: alignment
     * @ N: number of EM iteration
     */
    public Aligner(List<SentPair> text, int N){
        this.text=text;
        this.N=N;
    }
/**
    public void IBMM1(){
        int K=text.size();
        
        for(int n=0; n<N; n++){
        	System.out.println("Iteration "+(n+1));
        	//for each iteration
            //set counters to all zeros
        	Hashtable<String, Double> esum=new Hashtable<String, Double>();
            Double tInit=0.0001;

            for(int k = 0; k < K; k ++){
                //for each training pair
            	String[] F=text.get(k).foreign;
            	String[] E=text.get(k).en;
                int m=F.length;
                int l=E.length;
                double[] probs=new double[l+1];
                //iterate over f
                for(int j = 0; j < m; j++){
                	double sum=0;
                	double prob_al=1.0/(l+1);//model1, uniform
                	if(!TTable.containsKey(F[j])){
                		TTable.put(F[j], new Hashtable<String, Double>());
                	}
                	//iterate over e
                	for(int i = 1; i < l+1; i++){
                		Double p= TTable.get(F[j]).get(E[i-1]);//t(f|e)
                		if(p==null){
                			TTable.get(F[j]).put(E[i-1], tInit);
                			p=tInit;
                		}  		
                		probs[i]=p;
                		sum+=probs[i];//sum over e
                	}
                	//then increment
                	for(int i = 1; i < l+1; i++){
                		TTable.get(F[j]).put(E[i-1], TTable.get(F[j]).get(E[i-1])+probs[i]/sum);//c(e,f)+=delta
                		if(!esum.containsKey(E[i-1])){
                			esum.put(E[i-1], 0.0);
                		}
                		esum.put(E[i-1],esum.get(E[i-1])+probs[i]/sum);//c(e)+=delta
                	}
                }        
            }
            //After going thru all examples, normalize probability
            Enumeration<String> fe=TTable.keys();
            while(fe.hasMoreElements()){
            	String fword=fe.nextElement();
            	Hashtable<String, Double> w2d=TTable.get(fword);
            	Enumeration<String> ee=w2d.keys();
            	while(ee.hasMoreElements()){
            		String eword=ee.nextElement();
            		Double p=w2d.get(eword);
            		w2d.put(eword, p/esum.get(eword));
            	}
            }
        }       
    }
    **/
    public void IBMM1(){
        int K=text.size();
        
        for(int n=0; n<N; n++){
        	System.out.println("Iteration "+(n+1));
        	//for each iteration
            //set counters to all zeros
        	Hashtable<String, Double> esum=new Hashtable<String, Double>();
        	esum.put("NULL", 0.0);
            Double tInit=0.0001;

            for(int k = 0; k < K; k ++){
            	
                //for each training pair
            	String[] F=text.get(k).foreign;
            	String[] E=text.get(k).en;
                int m=F.length;
                int l=E.length;
                double[] probs=new double[l+1];
                //iterate over f
                for(int i = 0; i < m; i++){
                	double sum=0;
                	double prob_al=1.0/(l+1);//model1, uniform
                	if(!TTable.containsKey(F[i])){
                		TTable.put(F[i], new Hashtable<String, Double>());
                	}
                	//iterate over e
                	for(int j = 1; j < l+1; j++){
                		Double p= TTable.get(F[i]).get(E[j-1]);//t(f|e)
                		if(p==null){
                			TTable.get(F[i]).put(E[j-1], tInit);
                			p=tInit;
                		}  		
                		probs[j]=p;
                		sum+=probs[j];//sum over e
                	}
                	if(useNull){
                		Double p=TTable.get(F[i]).get("NULL");
                		if(p==null){
                			TTable.get(F[i]).put("NULL", tInit);
                			p=tInit;
                		}
                		probs[0]=p;
                		sum+=probs[0];
                	}
                	//then increment
                	for(int j = 1; j < l+1; j++){
                		TTable.get(F[i]).put(E[j-1], TTable.get(F[i]).get(E[j-1])+probs[j]/sum);//c(e,f)+=delta
                		if(!esum.containsKey(E[j-1])){
                			esum.put(E[j-1], 0.0);
                		}
                		esum.put(E[j-1],esum.get(E[j-1])+probs[j]/sum);//c(e)+=delta
                	}
                	if(useNull){
                		TTable.get(F[i]).put("NULL", TTable.get(F[i]).get("NULL")+probs[0]/sum);
                		esum.put("NULL", esum.get("NULL")+probs[0]/sum);
                	}
                }        
            }
            //After going thru all examples, normalize probability
            Enumeration<String> fe=TTable.keys();
            while(fe.hasMoreElements()){
            	String fword=fe.nextElement();
            	Hashtable<String, Double> w2d=TTable.get(fword);
            	Enumeration<String> ee=w2d.keys();
            	while(ee.hasMoreElements()){
            		String eword=ee.nextElement();
            		Double p=w2d.get(eword);
            		w2d.put(eword, p/esum.get(eword));
            	}
            }
        }       
    }
    public void IBMM1diag(){
    	int K=text.size();
        
        for(int n=0; n<N; n++){
        	System.out.println("Iteration "+(n+1));
        	//for each iteration
            //set counters to all zeros
        	Hashtable<String, Double> esum=new Hashtable<String, Double>();
        	esum.put("NULL", 0.0);
            Double tInit=0.1;
           
            for(int k = 0; k < K; k ++){
                //for each training pair
            	
            	String[] F=text.get(k).foreign;
            	String[] E=text.get(k).en;
                int m=F.length;
                int l=E.length;
                double[] probs=new double[l+1];
                //iterate over f
                for(int i = 0; i < m; i++){
                	double sum=0;
                	double prob_al=1.0;//model1, uniform
                	
                	if(!TTable.containsKey(F[i])){
                		TTable.put(F[i], new Hashtable<String, Double>());
                	}
                	double diagNorm=Util.computeDiagNorm(i,m,l,tension);
                	//iterate over e
                	for(int j = 1; j < l+1; j++){
                		Double p= TTable.get(F[i]).get(E[j-1]);//t(f|e)
                		if(p==null){
                			TTable.get(F[i]).put(E[j-1], tInit);
                			p=tInit;
                		}  		
                		
                		if(diag){
                    		prob_al=Math.exp(-1*Math.abs((i+1)*1.0/m-j*1.0/l)*tension)/diagNorm;
                    	}
                		probs[j]=p*prob_al;
                		sum+=probs[j];//sum over e
                	}
                	if(useNull){
                		Double p=TTable.get(F[i]).get("NULL");
                		if(p==null){
                			TTable.get(F[i]).put("NULL", tInit);
                			p=tInit;
                		}
                		probs[0]=p;
                		sum+=probs[0];
                	}
                	//then increment
                	for(int j = 1; j < l+1; j++){
                		TTable.get(F[i]).put(E[j-1], TTable.get(F[i]).get(E[j-1])+probs[j]/sum);//c(e,f)+=delta
                		if(!esum.containsKey(E[j-1])){
                			esum.put(E[j-1], 0.0);
                		}
                		esum.put(E[j-1],esum.get(E[j-1])+probs[j]/sum);//c(e)+=delta
                	}
                	if(useNull){
                		TTable.get(F[i]).put("NULL", TTable.get(F[i]).get("NULL")+probs[0]/sum);
                		esum.put("NULL", esum.get("NULL")+probs[0]/sum);
                	}
                }        
            }
            //After going thru all examples, normalize probability
            Enumeration<String> fe=TTable.keys();
            while(fe.hasMoreElements()){
            	String fword=fe.nextElement();
            	Hashtable<String, Double> w2d=TTable.get(fword);
            	Enumeration<String> ee=w2d.keys();
            	while(ee.hasMoreElements()){
            		String eword=ee.nextElement();
            		Double p=w2d.get(eword);
            		w2d.put(eword, p/esum.get(eword));
            	}
            }
        }       
    }
    public void IBMM2(){
        int K=text.size();
        
        for(int n=0; n<N; n++){
        	System.out.println("Iteration "+(n+1));
        	//for each iteration
            //set counters to all zeros
        	Hashtable<String, Double> tsum=new Hashtable<String, Double>();
        	Hashtable<IndexPair,Hashtable<Integer, Double>> asum=new Hashtable<IndexPair,Hashtable<Integer, Double>>();
            Double tInit=0.0001;
            Double aInit=0.0001;
            for(int k = 0; k < K; k ++){
                //for each training pair
            	String[] F=text.get(k).foreign;
            	String[] E=text.get(k).en;
                int m=F.length;
                int l=E.length;
                IndexPair lenPair=new IndexPair(l,m);
                if(!this.ATable.containsKey(lenPair)){
                	this.ATable.put(lenPair, new Hashtable<IndexPair, Double>());
                }
                double[] probs=new double[l+1];
                //iterate over f
                for(int i = 0; i < m; i++){
                	double sum=0;
                	double prob_al=1.0/(l+1);//model1, uniform
                	if(!TTable.containsKey(F[i])){
                		TTable.put(F[i], new Hashtable<String, Double>());
                	}
                	//iterate over e
                	for(int j = 1; j < l+1; j++){
                		/**Translation**/
                		Double tp= TTable.get(F[i]).get(E[j-1]);//t(f|e)
                		if(tp==null){
                			TTable.get(F[i]).put(E[j-1], tInit);
                			tp=tInit;
                		}  		
                		
                		/**Alignment**/
                		IndexPair indPair=new IndexPair(j,i);
                		Double ap=ATable.get(lenPair).get(indPair);
                		if(ap==null){
                			ATable.get(lenPair).put(indPair, aInit);
                			ap=aInit;
                		}
                		probs[j]=tp*ap;
                		sum+=probs[j];//sum over e
                	}
                	//then increment
                	for(int j = 1; j < l+1; j++){
                		TTable.get(F[i]).put(E[j-1], TTable.get(F[i]).get(E[j-1])+probs[j]/sum);//c(e,f)+=delta
                		if(!tsum.containsKey(E[j-1])){
                			tsum.put(E[j-1], 0.0);
                		}
                		tsum.put(E[j-1],tsum.get(E[j-1])+probs[j]/sum);//c(e)+=delta
                		IndexPair indPair=new IndexPair(j,i);           
                		ATable.get(lenPair).put(indPair, ATable.get(lenPair).get(indPair)+probs[j]/sum);//c(j|i,l,m)+=delta
                		if(!asum.containsKey(lenPair)){
                			asum.put(lenPair, new Hashtable<Integer, Double>());
                		}
                		if(!asum.get(lenPair).containsKey(i))
                			asum.get(lenPair).put(i, 0.0);
                		asum.get(lenPair).put(i, asum.get(lenPair).get(i)+probs[j]/sum);//c(i,l,m)+=delta
                	}
                }        
            }
            //After going thru all examples, normalize probability
            Enumeration<String> fe=TTable.keys();
            while(fe.hasMoreElements()){
            	String fword=fe.nextElement();
            	Hashtable<String, Double> w2d=TTable.get(fword);
            	Enumeration<String> ee=w2d.keys();
            	while(ee.hasMoreElements()){
            		String eword=ee.nextElement();
            		Double p=w2d.get(eword);
            		w2d.put(eword, p/tsum.get(eword));
            	}
            	TTable.put(fword, w2d);
            }
            Enumeration<IndexPair> lene=ATable.keys();
            while(lene.hasMoreElements()){
            	IndexPair lenp=lene.nextElement();
            	Hashtable<IndexPair, Double> p2d=ATable.get(lenp);
            	Enumeration<IndexPair> pose=p2d.keys();
            	while(pose.hasMoreElements()){
            		IndexPair posp=pose.nextElement();
            		Double p = p2d.get(posp);
            		p2d.put(posp, p/asum.get(lenp).get(posp.f));
            	}
            	ATable.put(lenp, p2d);
            }
        }       
    }
    public void alignIBM1(boolean reverse){
    	for(SentPair pair:this.text){
    		String[]E=pair.en;
    		String[]F=pair.foreign;
    		for(int i = 0; i < F.length; i++){
    			int maxIndex=-1;
    			Double maxProb=0.0;
    			for(int j = 0; j < E.length; j++){
    				double p=TTable.get(F[i]).get(E[j]);
    				if(p>maxProb){
    					maxProb=p;
    					maxIndex=j;
    				}
    			}
    			if(useNull){
    				if(TTable.get(F[i]).get("NULL")>maxProb){
    					maxProb=TTable.get(F[i]).get("NULL");
    					maxIndex=-1;
    				}
    			}
    			if(!reverse){
    				if(maxIndex>=0)
    					System.out.print(i+"-"+maxIndex+" ");
    			}
    			else{
    				if(maxIndex>=0)
    					System.out.print(maxIndex+"-"+i+" ");
    			}
    		}
    		System.out.print("\n");
    	}
    }
    public void alignIBM2(){
    	for(SentPair pair:this.text){
    		String[]E=pair.en;
    		String[]F=pair.foreign;
    		Hashtable<IndexPair, Double> lentable=ATable.get(new IndexPair(E.length, F.length));
    		for(int i = 0; i < F.length; i++){
    			int maxIndex=-1;
    			Double maxProb=-1.0;
    			for(int j = 0; j < E.length; j++){
    				double tp=TTable.get(F[i]).get(E[j]);
    				Double ap=null;
    				if(lentable!=null){
    					ap=lentable.get(new IndexPair(j+1,i));
    				}
    				if(ap==null){
    					ap=0.0;
    				}
    				
    				if(tp*ap>maxProb){
    					maxProb=tp*ap;
    					maxIndex=j;
    				}
    			}
    			System.out.print(i+"-"+maxIndex+" ");
    		}
    		System.out.print("\n");
    	}
    }
    public static void main(String[] args){
    	//boolean reverse=false;
//        List<SentPair> sentPairs=Reader.readParallelCorpus("data/dev-test-train.de-en");
//    	List<SentPair> sentPairs=Reader.readParallelCorpus("5000.txt");
    	List<SentPair> sentPairs=Reader.readParallelCorpus("data/dev-test-train.de-en", reverse);
//    	List<SentPair> sentPairs=Reader.readParallelCorpus("10000.txt", true);//reverse readings
        Aligner aligner=new Aligner (sentPairs, 5);
//        aligner.IBMM1();
        aligner.IBMM1diag();
        aligner.alignIBM1(reverse);
//        aligner.IBMM2();
//        aligner.alignIBM2();
        //List<int[][]> aligns=Reader.readAlign(args[1]);
        //assert senPairs.size()==aligns.size();
        
    }
}
