
public class IndexPair {
	public int e;
	public int f;
	public IndexPair(int i, int j){
		this.e=i;
		this.f=j;
	}
	public String toString(){
		return "("+Integer.toString(e)+" "+Integer.toString(f)+")";
	}
	public boolean equals(Object obj) 
	{
		IndexPair o=(IndexPair)obj;
		if(o.e==this.e && o.f==this.f)
			return true;
		else
			return false;
	}
	public int hashCode(){
		return this.toString().hashCode();
	}
}
