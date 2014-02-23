import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


public class AlCombiner {
	String fAl = "out.16";
	String fRevAl = "out.16.rev";
	List<int[][]> al=null;
	List<int[][]> revAl=null;
	public AlCombiner() {
		al = Reader.readAlign(fAl);
		revAl = Reader.readAlign(fRevAl);
	}

	public List<int[][]> intersection() {
		List<int[][]> intersect = new ArrayList<int[][]>();
//		List<int[]> takenAl = new ArrayList<int[]>();
//		List<int[]> takenReval=new ArrayList<int[]>();
//		List<HashSet<Integer>> filledAllE=new ArrayList<HashSet<Integer>>();
//		List<HashSet<Integer>> filledAllF=new ArrayList<HashSet<Integer>>();
		for (int k = 0; k < al.size(); k++) {
            if(k%500==0){
                System.err.print(".");
            }

			List<IndexPair> inter = new ArrayList<IndexPair>();
			HashSet<Integer> filledE=new HashSet<Integer>();
			HashSet<Integer> filledF=new HashSet<Integer>();
			int[] tkAl=new int[al.size()];
			int[] tkReval=new int [revAl.size()];
			int[][] a = al.get(k);
			int[][] r = revAl.get(k);
			for (int i = 0; i < a.length; i++) {
				for (int j = 0; j < r.length; j++) {
					if (a[i][0] == r[j][0] && a[i][1] == r[j][1]) {
						//inter.add(new IndexPair(i, j));
						inter.add(new IndexPair(a[i][0],a[i][1]));
						tkAl[i]=1;
						tkReval[j]=1;
					}
				}
			}
			//int[][] toadd = new int[inter.size()][2];
			List<int[]> toadd=new ArrayList<int[]>();
			
			for (int i = 0; i < inter.size(); i++) {
				int[] ta=new int[2];
				ta[0] = inter.get(i).e;
				ta[1] = inter.get(i).f;
				toadd.add(ta);
				filledE.add(inter.get(i).e);
				filledF.add(inter.get(i).f);
			}
			
			for(int i= 0; i < r.length; i++){
				if(tkReval[i]==0){
					if(!filledE.contains(r[i][0])&&!filledF.contains(r[i][1])){
						toadd.add(r[i]);
						tkReval[i]=1;
						filledE.add(r[i][0]);
						filledF.add(r[i][1]);
					}
				}
			}
			for(int i= 0; i < r.length; i++){
				if(tkReval[i]==0){
					if(!filledE.contains(r[i][0])&&!filledF.contains(r[i][1])){
						toadd.add(r[i]);
						tkReval[i]=1;
						filledE.add(r[i][0]);
						filledF.add(r[i][1]);
					}
				}
			}
			//forward alignment 
			for(int i = 0; i < a.length; i++){
				if(tkAl[i]==0){//not taken
					if(!filledE.contains(a[i][0])&&!filledF.contains(a[i][1])){//both e and f not in
						toadd.add(a[i]);
						tkAl[i]=1;
						filledE.add(a[i][0]);
						filledF.add(a[i][1]);
					}
				}
			}
		
			int [][]toaddArr=toadd.toArray(new int[toadd.size()][2]);
			intersect.add(toaddArr);
		}
		//add no-match pairs
		
		return intersect;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AlCombiner ab = new AlCombiner();
		List<int[][]> result=ab.intersection();
		System.err.println("Begin printing");
		for(int[][] al:result){
			for(int i=0; i<al.length; i++){
				System.out.print(al[i][0]+"-"+al[i][1]+" ");
			}
			System.out.println();
		}
		
	}

}
