
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
    
    HashMap Max_concurrent_access(){
        HashMap result_hashmap = new HashMap();
        HashMap list_hashmap = new HashMap();
        HashMap init_hashmap = new HashMap();
        Date START = null;
        Date starttime, endtime;
        String filepath;
        try {
            FileReader in = new FileReader(logfile);
            BufferedReader br = new BufferedReader(in);
            String input;
            int flag = 0;
            int time = 0;
            while ((input = br.readLine()) != null) {
                String[] elements = input.split("\t");
                filepath = elements[0];
                starttime = String_to_date(elements[1]);
                endtime = String_to_date(elements[2]);
                if(flag==0){
                    START = starttime;
                    flag = 1;
                    time = 0;
                }
                ArrayList<Integer> result = (ArrayList<Integer>) result_hashmap.get(filepath);
                if(result == null){
                    result = new ArrayList<>();
                    result_hashmap.put(filepath, result);
                }
                ArrayList<Pair<Date,String>> list = (ArrayList<Pair<Date,String>>) list_hashmap.get(filepath);
                if(list == null){
                    list = new ArrayList<>();
                    list_hashmap.put(filepath, list);
                }
                Date init = (Date) init_hashmap.get(filepath);
                if(init == null){
                    init = START;
                    init_hashmap.put(filepath, init);
                }
                while(starttime.compareTo(Add_time(init,interval))>0){
                  //  System.out.println(starttime+"   "+init+"  "+Add_time(init,interval)+" "+list.size());
                    result.add (Get_Max_Overlaps(list));
                    init = Add_time(init,interval);
                    time = time + 1;
                    list.clear();
                }
                list.add(new Pair(starttime,"start"));
                list.add(new Pair(endtime,"end"));
                list_hashmap.put(filepath, list);
                result_hashmap.put(filepath, result);
                init_hashmap.put(filepath, init);
            }
            Iterator iter = (Iterator) result_hashmap.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                filepath = (String) entry.getKey();
                ArrayList<Integer> result = (ArrayList<Integer>) entry.getValue();
                System.out.println("filepath is  " + filepath);
                ArrayList<Pair<Date,String>> list = (ArrayList<Pair<Date,String>>) list_hashmap.get(filepath);
                Date init = (Date) init_hashmap.get(filepath);
                Date now_time = new Date();
                while(now_time.compareTo(Add_time(init,interval))>0){
                    result.add (Get_Max_Overlaps(list));
                    init = Add_time(init,interval);
                    time = time + 1;
                    list.clear();
                }
            }
            /*if(!list.isEmpty()){
                result.add(Get_Max_Overlaps(list));
            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    //    double p = Calculate_popularity(result);
        return result_hashmap;
    }
   
    
    private static Date String_to_date(String date_string){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS"); 
        Date date = null;
        try {
            date = df.parse(date_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    private static Date Add_time(Date date,int sec){
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