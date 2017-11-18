
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RunMultipleWordCount {
  
    
    public static void main(String[] args) throws InterruptedException{
        final String filename;
        final int total;
        try{
            filename = args[0];    
            total = Integer.parseInt(args[1]);
        }catch(Exception ex){
                System.out.println("Pass filepath and total no of threads as arguments");
                return;
        }
        System.out.println("running");
        System.out.println(executeCommand("hadoop fs -rm -R /wordcount"));
        Thread[] th = new Thread[total];
        for(int i=0;i<total;i++){
            final int r = i;
            th[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println(executeCommand("hadoop jar WordCountCustom.jar "+filename+" /wordcount/w"+r));
                    } catch (Exception ex) {
                    }
                }
            };
            th[i].start();
        }
        for(int i=0;i<total;i++)
            th[i].join();

        /*Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println(executeCommand("hadoop jar WordCountCustom.jar "+filename+" /wordcount/w1"));
                } catch (Exception ex) {
                }
            }
        };
        thread1.start();
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println(executeCommand("hadoop jar WordCountCustom.jar "+filename+" /wordcount/w2"));
                } catch (Exception ex) {
                }
            }
        };
        thread2.start();
        
        thread1.join();
        thread2.join();*/
    //  System.out.println(executeCommand("ping google.com"));
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
                System.exit(0);
		return output.toString();
	}
}
