import java.util.ArrayList;
import java.util.List;

public class AlCombiner {
	String fAl = "out.rev.1";
	String fRevAl = "out.1";
	List<int[][]> al=null;
	List<int[][]> revAl=null;
	public AlCombiner() {
		List<int[][]> al = Reader.readAlign(fAl);
		List<int[][]> revAl = Reader.readAlign(fRevAl);
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
				toadd[i][0] = inter.get(i).f;
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
		ab.intersection();
	}

}
