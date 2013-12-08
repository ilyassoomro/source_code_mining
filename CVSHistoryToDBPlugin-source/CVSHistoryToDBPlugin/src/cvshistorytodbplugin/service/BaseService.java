package cvshistorytodbplugin.service;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import cvshistorytodbplugin.model.CVSAuthor;
import cvshistorytodbplugin.model.CVSFile;
import cvshistorytodbplugin.model.CVSFileRevision;
import cvshistorytodbplugin.model.CVSFileType;
import cvshistorytodbplugin.model.CVSProject;
import cvshistorytodbplugin.model.CVSRepository;
import cvshistorytodbplugin.model.CVSTag;
import cvshistorytodbplugin.model.Workspace;
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
			Type existingObject = getByUniqueKey(object, session);
			if(existingObject==null){
				session.saveOrUpdate(object);
				LocalCache.cache(object);
			}else{
				return existingObject;
			}
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
		if(object instanceof CVSAuthor){
			return (Type)getObjectByCriteria(object.getClass(), Restrictions.eq("name", ((CVSAuthor)object).getName()), session);
		}
		else if(object instanceof CVSFile){
			return (Type)getObjectByCriteria(object.getClass(), new Criterion[]{Restrictions.eq("name", ((CVSFile)object).getName()),
														Restrictions.eq("path", ((CVSFile)object).getPath()),
														Restrictions.eq("project", ((CVSFile)object).getProject())
																	}, session);
		}
		else if(object instanceof CVSRepository){
			return (Type)getObjectByCriteria(object.getClass(), new Criterion[]{Restrictions.eq("host", ((CVSRepository)object).getHost()),
														Restrictions.eq("path", ((CVSRepository)object).getPath()),
														Restrictions.eq("port", ((CVSRepository)object).getPort())
																	}, session);
		}
		else if(object instanceof CVSTag){
			return (Type)getObjectByCriteria(object.getClass(), Restrictions.eq("tagName", ((CVSTag)object).getTagName()), session);
		}
		else if(object instanceof CVSFileRevision){
			return (Type)getObjectByCriteria(object.getClass(),new Criterion[]{Restrictions.eq("cvsFile", ((CVSFileRevision)object).getCvsFile()),
														Restrictions.eq("revision", ((CVSFileRevision)object).getRevision()),
														Restrictions.eq("committedDate", ((CVSFileRevision)object).getCommittedDate())
																	}, session);
		}
		else if(object instanceof CVSFileType){
			return (Type)getObjectByCriteria(object.getClass(), Restrictions.eq("extension", ((CVSFileType)object).getExtension()), session);
		}
		else if(object instanceof CVSProject){
			return (Type)getObjectByCriteria(object.getClass(), 
										new Criterion[]{Restrictions.eq("name", ((CVSProject)object).getName()),
														Restrictions.eq("path", ((CVSProject)object).getPath()),
														Restrictions.eq("workspace", ((CVSProject)object).getWorkspace())
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
