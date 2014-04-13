import java.util.ArrayList;
import java.util.HashSet;
public class Hypothesis implements Comparable<Hypothesis>{
	public Hypothesis(double logprob, String lm_state, Hypothesis pred, Pair phrase){
		this.logprob=logprob;
		this.lm_state=lm_state;
		this.predecessor=pred;
		this.phrase=phrase;
		this.covered=new HashSet<Integer>();
	}
	public double logprob;
	public String lm_state;
	public Hypothesis predecessor;
	public HashSet<Integer> covered; //The indices of src (f) that has been translated.
	public Pair phrase;
	@Override
	public int compareTo(Hypothesis o0) {
		// TODO Auto-generated method stub
		if(o0.logprob>this.logprob)
			return 1;
		else if(o0.logprob<this.logprob)
			return -1;
		else
			return 0;
	}
	
	public String toString(){
		//return "["+String.valueOf(this.logprob)+"|"+lm_state+"|"+this.phrase.toString()+"]";
		if(this.phrase==null){
			return "null";
		}
		return this.phrase.text;
	}
}
