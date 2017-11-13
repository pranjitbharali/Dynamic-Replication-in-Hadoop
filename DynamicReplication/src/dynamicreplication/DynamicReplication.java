
package dynamicreplication;

import java.util.ArrayList;


public class DynamicReplication {


    public static void main(String[] args) {
        Popularity p = new Popularity("log.txt",3600);
        ArrayList<Integer> arrl= p.Max_concurrent_access("/dummy/Main.java");
        System.out.println(arrl);
        double ans = Popularity.Calculate_popularity(arrl);
        System.out.println(ans);
    }
    
}
