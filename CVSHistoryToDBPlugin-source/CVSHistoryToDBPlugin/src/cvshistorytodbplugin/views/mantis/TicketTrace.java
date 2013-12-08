package cvshistorytodbplugin.views.mantis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cvshistorytodbplugin.util.Logger;


public class TicketTrace {
	
	String ticketNumber;
	HashSet<String> filesWithVersion;
	HashMap<String, ArrayList<String>> fileAndVersions;
	HashMap<String, String> fileAndHighestVersion;
	
	public TicketTrace(String ticketNumber){
		this.ticketNumber=ticketNumber;
	}
	
	
	public String getTicketNumber() {
		return ticketNumber;
	}
	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}
	
	
	public HashSet<String> getFilesWithVersion() {
		return filesWithVersion;
	}
	public void setFilesWithVersion(HashSet<String> filesWithVersion) {
		this.filesWithVersion = filesWithVersion;
	}
	public boolean addFileWithVersion(String fileWithVersion){
		if(this.filesWithVersion==null){
			this.filesWithVersion = new HashSet<String>();
		}
		return this.filesWithVersion.add(fileWithVersion);
	}

	
	public HashMap<String, ArrayList<String>> getFileAndVersions() {
		if(fileAndVersions==null){
			populateFilesAndVersions();
		}
		return fileAndVersions;
	}

	private void populateFilesAndVersions(){
		String version = "";
		String fileName = "";
		ArrayList<String> versions = null;
		
		fileAndVersions = new HashMap<String, ArrayList<String>>();
		if(filesWithVersion!=null && filesWithVersion.size()>0){
			for (String fileWithVersion : filesWithVersion) {
				if(fileWithVersion.indexOf("  ")>-1){
					version = fileWithVersion.split("  ")[0];
					fileName = fileWithVersion.split("  ")[1];
	
					if(fileAndVersions.containsKey(fileName)){//if there are existing versions
						fileAndVersions.get(fileName).add(version);
					}else{ //if this is the first version
						versions = new ArrayList<String>();
						versions.add(version);
						fileAndVersions.put(fileName,versions);
					}
				}
			}
		}
	}
	
	public HashMap<String, String> getFileAndHighestVersion() {
		if(fileAndHighestVersion==null){
			populateFilesWithHighestVersions();
		}
		return fileAndHighestVersion;
	}

	private void populateFilesWithHighestVersions(){
		HashMap<String, ArrayList<String>> filesAndVersions = getFileAndVersions(); 
		
		fileAndHighestVersion = new HashMap<String, String>();
		
		ArrayList<String> versions = null;
		String highestVersion = null;
		int versionNumber=0;
		int highestVersionNumber = 0;
		if(!filesAndVersions.isEmpty()){
			for (String fileName : filesAndVersions.keySet()) {
				versions = filesAndVersions.get(fileName);
				try{
					if(!versions.isEmpty()){
						highestVersion = versions.get(0);
						highestVersionNumber = Integer.parseInt(highestVersion.substring(highestVersion.lastIndexOf(".")+1,highestVersion.length()));
					}
					for (String version : versions) {
						versionNumber = Integer.parseInt((version.substring(version.lastIndexOf(".")+1,version.length())));
						if(versionNumber>highestVersionNumber){
							highestVersion = version;
						}
					}
				}catch(Exception e){Logger.error("Error while determining Highest Version: "+e.getMessage());}
				fileAndHighestVersion.put(fileName, highestVersion);
			}
		}
	}


	@Override
	public String toString() {
		return "TicketTrace [ \nticketNumber=" + ticketNumber
				+ "\n filesWithVersion=" + filesWithVersion + "\n]";
	}
	
	
}
