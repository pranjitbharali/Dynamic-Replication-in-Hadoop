
package dynamicreplication;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Replication {
    
    HashMap HM;

    Replication(HashMap popularity_hashmap) {
        this.HM = popularity_hashmap;
    }
    
    public void Replicate() throws InterruptedException{
        String filepath = "/dummy/Main.java";
        int rf = 3;
        String cmd = "hadoop fs -setrep "+rf+" "+filepath;
     //   String cmd = "hdfs dfs -stat %r "+filepath;
        String output = executeCommand(cmd);
        System.out.println(output);
    }
    
    private static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        
                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
                        String err = "";
                        while ((err = stdError.readLine()) != null) {
                            System.out.println(err);
                        }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
    
}
