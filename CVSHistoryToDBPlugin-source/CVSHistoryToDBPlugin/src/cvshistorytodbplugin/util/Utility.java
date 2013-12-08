package cvshistorytodbplugin.util;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Utility {

	public static <T> ArrayList<T> getArrayList(T[] objs){
		ArrayList<T> list = null;
		if(objs!=null){
			list = new ArrayList<T>();
			for(T obj: objs){
				if(obj!=null){
					if(obj instanceof String && ((String) obj).trim().isEmpty()){
						continue;
					}
					list.add(obj);
				}
			}
		}
		return list;
	}
	
	public static <T> String getCommaSeparatedString(ArrayList<T> values){
		return getDelimitedString(values.toArray(), ",", null);
	}
	public static <T> String getCommaSeparatedString(T[] values){
		return getDelimitedString(values, ",", null);
	}
	public static <T> String getDelimitedString(T[] values, String delimiter){
		return getDelimitedString(values, delimiter, null);
	}
	public static <T> String getDelimitedString(ArrayList<T> values, String delimiter, String quotes){
		return getDelimitedString(values.toArray(), delimiter, null);
	}
	public static <T> String getDelimitedString(T[] values, String delimiter, String quotes){
		StringBuilder str = new StringBuilder("");
		if(values!=null && values.length>0){
			for(Object value: values){
				if(value!=null){
					if(value instanceof String && ((String) value).trim().isEmpty()){
						continue;
					}
					if(str.length()>0){
						str.append(delimiter);
					}
					if(quotes!=null) str.append(quotes);
					str.append(value);
					if(quotes!=null) str.append(quotes);
				}
			}
		}
		return str.toString();
	}
	
	public static boolean isEmpty(Object o){
		if(o==null){
			return true;
		}
		if(o instanceof String){
			String str = ((String) o).trim();
			if(str.isEmpty() || str.equals("null")){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isPositiveNumber(Object o){
		if(!isEmpty(o)){
			if(o instanceof Integer){
				return ((Integer)o).intValue()>0;
			}
			else {
				try{
					String str = o.toString();
					return new BigDecimal(str).doubleValue()>0;
				}catch(Exception e){}
			}
		}
		return false;
	}
}
