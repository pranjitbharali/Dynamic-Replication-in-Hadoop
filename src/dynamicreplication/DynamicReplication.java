
package dynamicreplication;

import java.io.IOException;
import java.util.*;

public class DynamicReplication {


    public static void main(String[] args) {
        AuditLogParser.Parse();
        Popularity p = new Popularity("output.log",84600);
        HashMap result_hashmap= p.Max_concurrent_access();
        System.out.println(result_hashmap);
        Iterator iter = (Iterator) result_hashmap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String filepath = (String) entry.getKey();
            ArrayList<Integer> result = (ArrayList<Integer>) entry.getValue();
            double ans = Popularity.Calculate_popularity(result);
            System.out.println("polularity of "+filepath+" is\t"+ans);
        }
      //  double ans = Popularity.Calculate_popularity(result);
      //  System.out.println(ans);
    }
    
}
