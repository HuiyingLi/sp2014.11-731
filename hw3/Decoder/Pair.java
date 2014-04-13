
public class Pair implements Comparable<Pair>{
	public Pair(String text, double prob){
		this.text=text;
		this.logprob=prob;
	}
	public String text;
	public double logprob;
	public String toString(){
		return this.text+":"+this.logprob;
	}
	@Override
	public int compareTo(Pair p0) {
		// TODO Auto-generated method stub
		if(p0.logprob>this.logprob)
			return 1;
		else if(p0.logprob<this.logprob)
			return -1;
		else
			return 0;
	}
	
}
