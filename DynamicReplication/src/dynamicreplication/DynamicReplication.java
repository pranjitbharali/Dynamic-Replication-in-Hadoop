
package dynamicreplication;

import java.io.IOException;
import java.util.ArrayList;

public class DynamicReplication {


    public static void main(String[] args) throws IOException {
        AuditLogParser.Parse();
        Popularity p = new Popularity(args[0],3600);
        ArrayList<Integer> arrl= p.Max_concurrent_access("/dummy/computer_networks.txt");
        System.out.println(arrl);
        double ans = Popularity.Calculate_popularity(arrl);
        System.out.println(ans);
    }
    
}
