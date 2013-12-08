package cvshistorytodbplugin.views.mantis;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import cvshistorytodbplugin.util.Utility;


public class TicketUtility {
	
	public static TicketTrace fetchTicketTraces(String ticketNumber) {
		TicketTrace ticketTrace = new TicketTrace(ticketNumber);
		try {
		    
			// Construct data
		    String data = URLEncoder.encode("key1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");
		    data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");

		    // Send data
		    URL url = new URL("http://sibisoft.com/mantis/mantisNotes.php?id="+ticketNumber);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String[] lines ;; 
		    while ((line = rd.readLine()) != null) {
		    	if(line!=null && !line.trim().isEmpty()){
		    		lines = line.split("</br>");
		    		for (int i = 0; i < lines.length; i++) {
		    			if(!lines[i].startsWith("delete") && lines[i].indexOf(".")>=0){
		    				ticketTrace.addFileWithVersion(lines[i].trim());
		    			}
		    		}
		    	}
		    }
		    wr.close();
		    rd.close();
		 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ticketTrace;
	}
	
	public static String getTicketNumber(String ticketNumber){
		if(ticketNumber!=null){
			return ticketNumber.trim().replaceFirst("^0+(?!$)", "");
		}
		return null;
	}
	
	public static ArrayList<String>  getValidTicketNumbers(String input){
		ArrayList<String> ticketNumbers = new ArrayList<String>();
		if(input!=null){
			if(input.indexOf(",")>=0){
				String[] in = input.split(",");
				for(String i : in){
					String ticket = getValidTicketNumber(i);
					if(!Utility.isEmpty(ticket)){
						ticketNumbers.add(ticket);
					}
				}
			}else{
				String ticket = getValidTicketNumber(input);
				if(!Utility.isEmpty(ticket)){
					ticketNumbers.add(ticket);
				}
			}
			
		}
		return ticketNumbers;
	}
	
	public static String getValidTicketNumber(String input){
		if(input!=null){
			input = input.trim();
			if((input.startsWith("MT#") || input.startsWith("#"))){
				if(input.indexOf(':', input.indexOf('#'))>0){
					input = getTicketNumber(input.substring(input.indexOf('#')+1, input.indexOf(':')));
				}else{
					input = getTicketNumber(input.substring(input.indexOf('#')+1));
				}
			}
		}
		return input;
	}
	
}
