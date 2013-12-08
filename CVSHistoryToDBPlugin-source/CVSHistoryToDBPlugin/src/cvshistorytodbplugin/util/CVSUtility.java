package cvshistorytodbplugin.util;

import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileHistory;

public class CVSUtility {

	public static int compare(String version1, String version2){
		if(version1==null && version2==null){
			return 0;
		}
		if(version2==null){
			return 1;
		}
		if(version1==null){
			return -1;
		}

		String[] v1 = version1.split("\\."); 
		String[] v2 = version2.split("\\.");
		
		for(int i=0; i<v1.length && i<v2.length; i++){
			int result = new Integer(v1[i]).compareTo(new Integer(v2[i]));
			if(result!=0){
				return result;
			}
		}
		
		// If all bits are same, then check length of versions
		return new Integer(v1.length).compareTo(v2.length);
	}
	
	public static String getPreviousVersion(String version, CVSFileHistory fileHistory){
		if(version!=null){
			try{
				if(version.endsWith(".1")){		// 1.1.2.3.1		--> 1.1.2.3
					IFileRevision revision = getVersionFromPreviousBranch(version, fileHistory);
					if(revision!=null){
						return revision.getContentIdentifier();
					}
				}else{							// 1.1.2.3.4		--> 1.1.2.3.3
					String lastSegment = version.substring(version.lastIndexOf('.')+1);
					return version.substring(0,version.lastIndexOf('.'))+"."+(Integer.parseInt(lastSegment)-1);
				}
			}catch(Exception e){
				Logger.error("Error while determining previous version of : "+version);
			}
		}
		return null;
	}
	
	private static IFileRevision getVersionFromPreviousBranch(String version, CVSFileHistory fileHistory){
		if(version.indexOf(".")>0){
			version = version.substring(0,version.lastIndexOf('.'));
			IFileRevision revision = fileHistory.getFileRevision(version);
			if(revision!=null){
				return revision;	
			}
			return getVersionFromPreviousBranch(version, fileHistory);
		}
		return null;
	}
}
