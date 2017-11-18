package dynamicreplication;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class AuditLogParser {
	private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}");
	private static final Pattern CMD_PATTERN = Pattern.compile("cmd=(\\w+)");
//	private static final Pattern SRC_PATTERN = Pattern.compile("src=((\"[\\w\\/-]+\\.java\")|([\\w\\/-]+))"); //tried, doesn't work for files																								// with spaces in file names
        private static final Pattern SRC_PATTERN = Pattern.compile("src=(/[A-Za-z0-9_/.]+)");
        
	private static final String OPEN_CMD = "open";
	
	private static final String HDFS_URI = "hdfs://localhost:54310";
        
        public static class Logger {
            public void log(String start_time, String end_time, String path) throws IOException { 
                PrintWriter out = new PrintWriter(new FileWriter("output.log", true), true);
                out.write(path+"\t"+start_time+"\t"+end_time+"\n" );
                out.close();
            }
        }
	
	public static void Parse() {
                Logger lg = new Logger();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("logs/hdfs-audit.log")));
		} catch (FileNotFoundException ex) {
			System.out.println("Cannot open input file:");
			ex.printStackTrace();
			return;
		}
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", HDFS_URI);
		FileSystem fs;
                try {
                    fs = FileSystem.get(conf);
                } catch (IOException ex) {
                    System.out.println("Cannot connect to HDFS");
                    ex.printStackTrace();
                    return;
                }
                String line;
                String file_path= "", start_time = "", end_time = "";
		try {
                    while((line = br.readLine()) != null) {
                            Matcher cmdMatcher = CMD_PATTERN.matcher(line);
                            if (cmdMatcher.find()) {
                                    if (!cmdMatcher.group(1).equals(OPEN_CMD)) {
                                            continue;
                                    }
                            } else {
                                    System.out.println("ERROR: cmd not found in log: "+line);
                                    return;
                            }
                            Matcher tsMatcher = TIMESTAMP_PATTERN.matcher(line);
                            if(tsMatcher.find()) {
                                    start_time = tsMatcher.group(0).trim();
                            } else {
                                    System.out.println("ERROR: Timestamp not found in log: " + line);
                                    return;
                            }
                            Matcher srcMatcher = SRC_PATTERN.matcher(line);
                            if(srcMatcher.find()) {
                                    file_path = srcMatcher.group(1).trim();
                                    System.out.println(file_path);
                            } else {
                                    System.out.println("ERROR: src not found in file open log: "+line);
                                    return;
                            }
                            try{
                                FileStatus status = fs.getFileStatus(new Path(file_path));
                                if (status.isFile()) {
                                    long len = status.getLen();
                                    System.out.println(len);
                                    Date start = String_to_date(start_time);
                                    Date end = Add_time(start, (int)(len/10));
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
                                    end_time = df.format(end);
                                }

                                lg.log(start_time, end_time, file_path);
                            }catch(IOException e){
                            }
                    }
                } catch (IOException ex) {
                    System.out.println("Cannot read log file");
                    ex.printStackTrace();
                    return;
                }
                try {
                    br.close();
                } catch (IOException ex){
                    ex.printStackTrace();
                    return;
                }
   	}
        
    private static Date String_to_date(String date_string){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS"); 
        Date date = null;
        try {
            date = df.parse(date_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    private static Date Add_time(Date date,int millisec){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MILLISECOND, millisec);
        Date newdate = cal.getTime();
        return newdate;
    }
}