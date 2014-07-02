package cvshistorytodbplugin.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cvshistorytodbplugin.model.db.DBAuthor;
import cvshistorytodbplugin.model.db.DBFile;
import cvshistorytodbplugin.model.db.DBFileType;
import cvshistorytodbplugin.model.db.DBProject;
import cvshistorytodbplugin.model.db.DBRepository;
import cvshistorytodbplugin.model.db.DBTag;
import cvshistorytodbplugin.model.db.Workspace;

public class HibernateCache {
	
	////////////////////////////////////// 1- Workspace ///////////////////////////////////////////////
	private static HashMap<Integer, Workspace> workspaceMap = new HashMap<Integer, Workspace>();
	public static Workspace getWorkspace(Integer id,   Session session){
		return (Workspace) session.get(Workspace.class, id);
	}
	public static Workspace getWorkspace(String name, String path,   Session session){
		Criteria criteria = session.createCriteria(Workspace.class).setCacheable(true);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("path", path));
		return (Workspace)criteria.uniqueResult();
	}

	////////////////////////////////////// 2- Repository ///////////////////////////////////////////////	
	private static HashMap<Integer, DBRepository> repositoryMap = new HashMap<Integer, DBRepository>();
	public static DBRepository getRepository(Integer id,   Session session){
		return (DBRepository) session.get(DBRepository.class, id);
	}
	public static DBRepository getRepository(String host, String path,   Session session){
		Criteria criteria = session.createCriteria(DBRepository.class).setCacheable(true);
		criteria.add(Restrictions.eq("host", host));
		criteria.add(Restrictions.eq("path", path));
		return (DBRepository)criteria.uniqueResult();
	}
	
	////////////////////////////////////// 3- CVSProject ///////////////////////////////////////////////	
	public static DBProject getProject(Integer id,   Session session){
		return (DBProject) session.get(DBProject.class, id);
	}
	public static DBProject getProject(String name, String path,   Session session){
		Criteria criteria = session.createCriteria(DBProject.class).setCacheable(true);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("path", path));
		return (DBProject)criteria.uniqueResult();
	}
	
	////////////////////////////////////// 4- CVSAuthor ///////////////////////////////////////////////	
	public static DBAuthor getAuthor(Integer id,  Session session){
		return (DBAuthor) session.get(DBAuthor.class, id);
	}
	public static DBAuthor getAuthor(String name, Session session){
		Criteria criteria = session.createCriteria(DBAuthor.class).setCacheable(true);
		criteria.add(Restrictions.eq("name", name));
		return (DBAuthor)criteria.uniqueResult();
	}
	
	////////////////////////////////////// 5- CVSFileType ///////////////////////////////////////////////	
	public static DBFileType getFileType(String extension, Session session){
		Criteria criteria = session.createCriteria(DBFileType.class).setCacheable(true);
		criteria.add(Restrictions.eq("extension", extension));
		return (DBFileType)criteria.uniqueResult();
	}
	
	//////////////////////////////////////6- CVSTag ///////////////////////////////////////////////	
	public static DBTag getTag(String tagName, Session session){
		Criteria criteria = session.createCriteria(DBTag.class).setCacheable(true);
		criteria.add(Restrictions.eq("tagName", tagName));
		return (DBTag)criteria.uniqueResult();
	}
	//////////////////////////////////////7- CVSFile ///////////////////////////////////////////////	
	public static DBFile getDBFile(String name, String path, Session session){
		Criteria criteria = session.createCriteria(DBFile.class).setCacheable(true);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("path", path));
		return (DBFile)criteria.uniqueResult();
	}
}
