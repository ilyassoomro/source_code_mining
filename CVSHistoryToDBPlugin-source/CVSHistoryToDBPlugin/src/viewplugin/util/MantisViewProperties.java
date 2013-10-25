package viewplugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import viewplugin.util.Logger;

public class MantisViewProperties {       
    
    public static final String MANTIS_VIEW_FILE = "plugins/qatool.ini";
    private static Properties props = null;

    static {
    	reloadProperties();
    }
    
    public static Properties getProperties() {
        if(props == null) {
            props = new Properties(); 
            FileInputStream stream=null;
	        try {
	        	Logger.debug("+++++++ loading properties");
	        	
	        	stream = new FileInputStream(getMantisViewFile());
	        	props.load(stream);
	        	stream.close();
	        	Logger.debug("+++++++ properties loaded\n---------------\n");
	        	Logger.debug(props);
	        	Logger.debug("\n-------------------\n");
	        }
	        catch(Exception e){
	        	Logger.error("Error while reading properties: " + e);
	            e.printStackTrace();
	            props = null;
	            if(stream!=null){
	            	try {
						stream.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	            }
	        }
        }
        return props;
    }
    private static File getMantisViewFile(){
    	try {
	    	//URL resource = MantisViewProperties.class.getResource(MANTIS_VIEW_FILE); 
	    	//File file = new File(resource.getFile());
    		File file = new File(MANTIS_VIEW_FILE);
	    	if(!file.exists()){
	    		file.createNewFile();
	    	}
	    	return file;
    	}catch(Exception e){
        	Logger.error("Error while reading properties: " + e);
        }	
    	return null;
    }
    private static void saveProperties(){
    	if(props!=null){
    		try {
    			Logger.debug("Saving Application Properties...\n{\n"+props+"\n}\n");
    			FileOutputStream stream = new FileOutputStream(getMantisViewFile());
        		props.store(stream, "Saving Properties");
        		stream.close();
        		Logger.debug("Application Properties Saved successfully.");
        	}catch(Exception e){
            	Logger.error("Error while saving properties: " + e);
            }	
    	}
    }
    
    public static Properties reloadProperties() {
        props = null;
        return MantisViewProperties.getProperties();
    }
    /************************************************************************/
    
    public static String getWorkspace(){
    	return props!=null ? (String) props.get("workspace") : null;
    }
    public static void setWorkspace(String workspace){
    	if(props==null){
    		props = new Properties();
    	}
    	props.setProperty("workspace", workspace);
    	saveProperties();
    }
    
    /************************************************************************/
    public static Date getExpireDate() throws Exception{
    	try{
    		String[] date = decrypt((String)props.get("data1")).split("-");
    		return new Date(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])); 
	    }catch(Exception e){
    		throw new Exception(CORRUPTED_MESSAGE);
    	}
    }
    
    public static Boolean isExpired() throws Exception{
    	try{
    		String isExpired = decrypt((String)props.get("data2"));
    		if(isExpired.equals("true")){
    			return true;
    		}
    		else if(isExpired.equals("false")){			// If not expired, then check expire date
	    		Date today = new Date();
	    		Date expireDate = getExpireDate();
	    		if(today.after(expireDate)){
	    			setExpired(true);
	    			return true;
	    		}
    		}
    		else{
    			throw new Exception(CORRUPTED_MESSAGE);
    		}
    		return false;
	    }catch(Exception e){
    		throw new Exception(CORRUPTED_MESSAGE);
    	}
    }
    
    private static void setExpired(Boolean expired){
    	props.setProperty("data2", encrypt(expired ? "true" : "false"));
    	saveProperties();
    }
    
    /************************************************************************/
    private static char key = 0x0055;
    private static String encrypt(String str){
    	if(str!=null){
    		char[] data = str.toCharArray();
    		char[] encyData = new char[data.length];
    		for(int i=0; i<data.length;i++){
    			encyData[i] = (char)(data[i] ^ key);
    		} 
    		return new String(encyData);
    	}
    	return null;
    }
    private static String decrypt(String str){
    	return encrypt(str);
    }
    private static String CORRUPTED_MESSAGE = "NS QA Tool configuration is corrupted. \nPlease contact Mohammad Ilyas Soomro at: \nEmail: muhammadilyas_bsel@yahoo.com \nPhone: +923313551306"; 
    /************************************************************************/
    
}
