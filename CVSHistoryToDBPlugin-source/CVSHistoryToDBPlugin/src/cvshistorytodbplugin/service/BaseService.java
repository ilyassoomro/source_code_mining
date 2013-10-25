package cvshistorytodbplugin.service;

import org.hibernate.Session;

public class BaseService{

	
	public static <Type> Type getByPrimaryKey(Class<Type> clazz, Integer id, Session session){
		return (Type)session.get(clazz, id);
	}
}
