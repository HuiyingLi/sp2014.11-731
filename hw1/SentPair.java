import java.util.*;
public class SentPair{
    public String[] foreign;
    public String[] en;
    public List<String[]> refs;//for multiple references=
    public SentPair(String[] f, String[] e){
        this.foreign=f;
        this.en=e;
    }
}
