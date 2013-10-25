package viewplugin.util;

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
}
