
package dynamicreplication;

import java.util.*;

public class DynamicReplication {


    public static void main(String[] args) throws InterruptedException {
        AuditLogParser.Parse();
        Popularity p = new Popularity("output.log",86400);
        HashMap result_hashmap= p.Max_concurrent_access();
        System.out.println(result_hashmap);
        Iterator iter = (Iterator) result_hashmap.entrySet().iterator();
        HashMap popularity_hashmap = new HashMap();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String filepath = (String) entry.getKey();
            ArrayList<Integer> result = (ArrayList<Integer>) entry.getValue();
            double ans = Popularity.Calculate_popularity(result);
            popularity_hashmap.put(filepath, ans);
            System.out.println("polularity of "+filepath+" is\t"+ans);  
        }
        Replication rp = new Replication(popularity_hashmap);
        rp.Replicate();
    }
    
}
