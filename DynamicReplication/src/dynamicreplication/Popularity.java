
package dynamicreplication;

import java.io.BufferedReader;
import java.io.FileReader;
import static java.lang.Math.max;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Popularity {
    
    private final String logfile;
    private final int interval;
    
    Popularity(String s, int i){
        this.logfile = s;
        this.interval = i;
    }
    
    ArrayList<Integer> Max_concurrent_access(String filepath){
        ArrayList<Integer> result= new ArrayList();
        Date init = null;
        Date starttime, endtime;
        try {
            FileReader in = new FileReader(logfile);
            BufferedReader br = new BufferedReader(in);
            String input;
            int flag = 0;
            int time = 0;
            ArrayList<Pair<Date,String>> list= new ArrayList();
            while ((input = br.readLine()) != null) {
                String[] elements = input.split("\t");
                if(!elements[0].equals(filepath)) continue;
                starttime = String_to_date(elements[1]);
                endtime = String_to_date(elements[2]);
                if(flag==0){
                    init = starttime;
                    flag = 1;
                    time = 0;
                    list.clear();
                }
                while(starttime.compareTo(Add_time(init,interval))>0){
                    System.out.println(starttime+"   "+init+"  "+Add_time(init,interval)+" "+list.size());
                    result.add (Get_Max_Overlaps(list));
                    init = Add_time(init,interval);
                    time = time + 1;
                    list.clear();
                }
                list.add(new Pair(starttime,"start"));
                list.add(new Pair(endtime,"end"));
            }
            if(!list.isEmpty()){
                result.add(Get_Max_Overlaps(list));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    //    double p = Calculate_popularity(result);
        return result;
    }
   
    
    static Date String_to_date(String date_string){
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss"); 
        Date date = null;
        try {
            date = df.parse(date_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    static Date Add_time(Date date,int sec){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, sec);
        Date newdate = cal.getTime();
        return newdate;
    }
    
    static int Get_Max_Overlaps(ArrayList<Pair<Date,String>> list){
        int ans = 0;
        Collections.sort(list, new Comparator<Pair<Date,String>>() {
            @Override
            public int compare(Pair<Date,String> p1, Pair<Date,String> p2) {
                if (p1.getFirst() == null || p2.getFirst() == null)
                    return 0;
                return p1.getFirst().compareTo(p2.getFirst());
            }
        });
        int current  = 0;
        for(int i = 0; i<list.size(); i++){
            Date d = list.get(i).getFirst();
            String s = list.get(i).getSecond();
            if(s.equals("start")) current = current + 1;
            else if(s.equals("end")) current = current - 1;
            ans = max(ans,current);
        }
        return ans;
    }

    public static double Calculate_popularity(ArrayList<Integer> AL) {
        double pt,ct, alpha = 0.5;
        pt = ct = AL.get(0);
        for(int i=1;i<AL.size();i++){
            pt = alpha*ct + (1-alpha)*pt;
            ct = AL.get(i);
        }
        pt = alpha*ct + (1-alpha)*pt;
        return pt;
    }
    
}