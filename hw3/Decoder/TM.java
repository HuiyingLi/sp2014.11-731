import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;
public class TM {
	//public static int 
	Hashtable<String, ArrayList<Pair>> table=new Hashtable<String, ArrayList<Pair>>();
	public ArrayList<Pair> get(String phrase){
		ArrayList<Pair> result= this.table.get(phrase);
		return result;
	}
	public TM(String TMpath){
		//ashtable<String, Hashtable<String, Double>> hash= new Hashtable<String, Hashtable<String, Double>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(TMpath));
			String line=null;
			
			while((line=br.readLine())!=null){
				String[] spl=line.split(" \\|\\|\\| ");
				String f=spl[0];
				String e = spl[1];
				Double p = Double.parseDouble(spl[2]);
				if(!this.table.containsKey(f)){
					this.table.put(f, new ArrayList<Pair>());	
				}
				Pair pair = new Pair(e, p);
				//his.table.put(f,this.table.get(f).add(pair));
				this.table.get(f).add(pair);
			}
			
			//sorting top translation
			Enumeration<String> e = this.table.keys();
			while(e.hasMoreElements()){
				String f=e.nextElement();
				ArrayList<Pair> plist=this.table.get(f);
				Collections.sort(plist);
				this.table.put(f, plist);
			}
			System.err.println("Finish loading TM");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public TM(String TMpath, int Nbest){
            //ashtable<String, Hashtable<String, Double>> hash= new Hashtable<String, Hashtable<String, Double>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(TMpath));
            String line=null;
            
            while((line=br.readLine())!=null){
                String[] spl=line.split(" \\|\\|\\| ");
                String f=spl[0];
                String e = spl[1];
                Double p = Double.parseDouble(spl[2]);
                if(!this.table.containsKey(f)){
                    this.table.put(f, new ArrayList<Pair>());	
                }
                Pair pair = new Pair(e, p);
                //his.table.put(f,this.table.get(f).add(pair));
                this.table.get(f).add(pair);
            }
            
            //sorting top translation
            Enumeration<String> e = this.table.keys();
            while(e.hasMoreElements()){
                String f=e.nextElement();
                ArrayList<Pair> plist=this.table.get(f);
                Collections.sort(plist);
                if(plist.size()>Nbest){
                    ArrayList<Pair> nlist=new ArrayList<Pair>();
                    for(int i = 0; i < Nbest; i++){
                        nlist.add(plist.get(i));
                    }
                    this.table.put(f,nlist);
                }else{
                    this.table.put(f, plist);
                }
            }
            System.err.println("Finish loading TM");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
