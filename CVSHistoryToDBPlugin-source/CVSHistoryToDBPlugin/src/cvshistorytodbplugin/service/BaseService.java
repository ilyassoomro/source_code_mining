package cvshistorytodbplugin.service;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import cvshistorytodbplugin.model.db.DBAuthor;
import cvshistorytodbplugin.model.db.DBFile;
import cvshistorytodbplugin.model.db.DBFileRevision;
import cvshistorytodbplugin.model.db.DBFileType;
import cvshistorytodbplugin.model.db.DBProject;
import cvshistorytodbplugin.model.db.DBRepository;
import cvshistorytodbplugin.model.db.DBTag;
import cvshistorytodbplugin.model.db.Workspace;
import cvshistorytodbplugin.util.Logger;

public class BaseService{

	
	public static <Type> Type getByPrimaryKey(Class<Type> clazz, Integer id, Session session){
		return (Type)session.get(clazz, id);
	}
	
	public static <Type> Type getObjectByCriteria(Class<Type> clazz, Criterion criteria, Session session){
		return getObjectByCriteria(clazz, new Criterion[]{criteria}, session);
	}
	public static <Type> Type getObjectByCriteria(Class<Type> clazz, Criterion[] criterias, Session session){
		Criteria criteria = session.createCriteria(clazz);
		if(criterias!=null){
			for(Criterion c: criterias){
				criteria.add(c);
			}
		}
		return (Type)criteria.uniqueResult();
	}
	
	public static <Type> List<Type> getListByCriteria(Class<Type> clazz, Criterion[] criterias, Session session){
		Criteria criteria = session.createCriteria(clazz);
		if(criterias!=null){
			for(Criterion c: criterias){
				criteria.add(c);
			}
		}
		return criteria.list();
	}
	
	public static <Type> Type save(Type object, Session session){
		try{
			//Type existingObject = getByUniqueKey(object, session);
			//if(existingObject==null){
				session.saveOrUpdate(object);
			//}else{
			//	return existingObject;
			//}
		}catch(ConstraintViolationException e){
			if(e.getCause()!=null && e.getCause().getMessage().toLowerCase().contains("duplicate entry")){
				Logger.error("Object already exist in database: "+object);
				//session.evict(object);
				//object = getByUniqueKey(object, session);
			}else{
				throw e;
			}
		}
		return object;
	}
	
	public static <Type> Type getByUniqueKey(Type object, Session session){
		if(object instanceof DBAuthor){
			return (Type)getObjectByCriteria(object.getClass(), Restrictions.eq("name", ((DBAuthor)object).getName()), session);
		}
		else if(object instanceof DBFile){
			return (Type)getObjectByCriteria(object.getClass(), new Criterion[]{Restrictions.eq("name", ((DBFile)object).getName()),
														Restrictions.eq("path", ((DBFile)object).getPath()),
														Restrictions.eq("project", ((DBFile)object).getProject())
																	}, session);
		}
		else if(object instanceof DBRepository){
			return (Type)getObjectByCriteria(object.getClass(), new Criterion[]{Restrictions.eq("host", ((DBRepository)object).getHost()),
														Restrictions.eq("path", ((DBRepository)object).getPath()),
														Restrictions.eq("port", ((DBRepository)object).getPort())
																	}, session);
		}
		else if(object instanceof DBTag){
			return (Type)getObjectByCriteria(object.getClass(), Restrictions.eq("tagName", ((DBTag)object).getTagName()), session);
		}
		else if(object instanceof DBFileRevision){
			return (Type)getObjectByCriteria(object.getClass(),new Criterion[]{Restrictions.eq("dbFile", ((DBFileRevision)object).getDbFile()),
														Restrictions.eq("revision", ((DBFileRevision)object).getRevision()),
														Restrictions.eq("committedDate", ((DBFileRevision)object).getCommittedDate())
																	}, session);
		}
		else if(object instanceof DBFileType){
			return (Type)getObjectByCriteria(object.getClass(), Restrictions.eq("extension", ((DBFileType)object).getExtension()), session);
		}
		else if(object instanceof DBProject){
			return (Type)getObjectByCriteria(object.getClass(), 
										new Criterion[]{Restrictions.eq("name", ((DBProject)object).getName()),
														Restrictions.eq("path", ((DBProject)object).getPath()),
														Restrictions.eq("workspace", ((DBProject)object).getWorkspace())
																	}, session);
		}
		else if(object instanceof Workspace){
			return (Type)getObjectByCriteria(object.getClass(), new Criterion[]{Restrictions.eq("name", ((Workspace)object).getName()),
														Restrictions.eq("path", ((Workspace)object).getPath()),
																	}, session);
		}
		else{
			return null;
		}
	}
	
	public static List listAll(Class clazz, Session session){
		return (List)session.createCriteria(clazz).list();
	}
}
