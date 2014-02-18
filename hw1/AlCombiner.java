import java.util.ArrayList;
import java.util.List;

public class AlCombiner {
	String fAl = "test.out";
	String fRevAl = "test.rev";
	List<int[][]> al=null;
	List<int[][]> revAl=null;
	public AlCombiner() {
		al = Reader.readAlign(fAl);
		revAl = Reader.readAlign(fRevAl);
	}

	public List<int[][]> intersection() {
		List<int[][]> intersect = new ArrayList<int[][]>();
		for (int k = 0; k < al.size(); k++) {
			List<IndexPair> inter = new ArrayList<IndexPair>();
			int[][] a = al.get(k);
			int[][] r = revAl.get(k);
			for (int i = 0; i < a.length; i++) {
				for (int j = 0; j < r.length; j++) {
					if (a[i][0] == r[j][0] && a[i][1] == r[j][1]) {
						inter.add(new IndexPair(i, j));
					}
				}
			}
			int[][] toadd = new int[inter.size()][2];
			for (int i = 0; i < inter.size(); i++) {
				toadd[i][0] = inter.get(i).e;
				toadd[i][1] = inter.get(i).f;
			}
			intersect.add(toadd);
		}
		return intersect;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AlCombiner ab = new AlCombiner();
		List<int[][]> result=ab.intersection();
		System.out.println("Begin printing");
		for(int[][] al:result){
			for(int i=0; i<al.length; i++){
				System.out.print(al[i][0]+"-"+al[i][1]+" ");
			}
			System.out.println();
		}
		
	}

}
