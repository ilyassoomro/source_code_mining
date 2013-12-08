package cvshistorytodbplugin.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import cvshistorytodbplugin.model.CVSAuthor;
import cvshistorytodbplugin.model.CVSFile;
import cvshistorytodbplugin.model.CVSFileType;
import cvshistorytodbplugin.model.CVSProject;
import cvshistorytodbplugin.model.CVSRepository;
import cvshistorytodbplugin.model.CVSTag;
import cvshistorytodbplugin.model.Workspace;

public class LocalCache {
	
	public static void cache(Object object){
		if(object instanceof Workspace){
			cacheWorkspace((Workspace)object);
		}
		else if(object instanceof CVSRepository){
			cacheRepository((CVSRepository)object);
		}
		else if(object instanceof CVSProject){
			cacheProject((CVSProject)object);
		}
		else if(object instanceof CVSAuthor){
			cacheAuthor((CVSAuthor)object);
		}
		else if(object instanceof CVSTag){
			cacheTag((CVSTag)object);
		}else{
			//throw new RuntimeException("LocalCache of Object type: "+object.getClass()+" is not supported");
		}
	}
	
	private static boolean cachedLoaded = false;
	public static void loadCache(Session session){
		if(!cachedLoaded){
			cacheList(BaseService.listAll(Workspace.class, session));
			cacheList(BaseService.listAll(CVSRepository.class, session));
			cacheList(BaseService.listAll(CVSProject.class, session));
			cacheList(BaseService.listAll(CVSAuthor.class, session));
			cacheList(BaseService.listAll(CVSFileType.class, session));
			cacheList(BaseService.listAll(CVSTag.class, session));
			cachedLoaded = true;
		}
	}
	
	private static void cacheList(List list){
		if(list!=null){
			for(Object obj: list){
				cache(obj);
			}
		}
	}
	
	////////////////////////////////////// 1- Workspace ///////////////////////////////////////////////
	private static HashMap<Integer, Workspace> workspaceMap = new HashMap<Integer, Workspace>();
	public static Workspace getWorkspace(Integer id){
		return workspaceMap.get(id);
	}
	public static Workspace getWorkspace(String name, String path){
		for(Workspace workspace : workspaceMap.values()){
			if((name==null || workspace.getName().equals(name)) &&
					(path==null || workspace.getPath().equals(path))){
				return workspace;
			}
		}
		return null;
	}
	public static void cacheWorkspace(Workspace workspace){
		workspaceMap.put(workspace.getWorkspaceId(), workspace);
	}
	
	////////////////////////////////////// 2- Repository ///////////////////////////////////////////////	
	private static HashMap<Integer, CVSRepository> repositoryMap = new HashMap<Integer, CVSRepository>();
	public static CVSRepository getRepository(Integer id){
		return repositoryMap.get(id);
	}
	public static CVSRepository getRepository(String host, String path){
		for(CVSRepository rep : repositoryMap.values()){
			if((host==null || rep.getHost().equals(host)) &&
					(path==null || rep.getPath().equals(path))){
				return rep;
			}
		}
		return null;
	}
	public static void cacheRepository(CVSRepository rep){
		repositoryMap.put(rep.getRepositoryId(), rep);
	}
	
	////////////////////////////////////// 3- CVSProject ///////////////////////////////////////////////	
	private static HashMap<Integer, CVSProject> projectMap = new HashMap<Integer, CVSProject>();
	public static CVSProject getProject(Integer id){
		return projectMap.get(id);
	}
	public static CVSProject getProject(String name, String path){
		for(CVSProject project : projectMap.values()){
			if((name==null || project.getName().equals(name)) &&
					(path==null || project.getPath().equals(path))){
				return project;
			}
		}
		return null;
	}
	public static void cacheProject(CVSProject project){
		projectMap.put(project.getProjectId(), project);
	}
	
	////////////////////////////////////// 4- CVSAuthor ///////////////////////////////////////////////	
	private static HashMap<Integer, CVSAuthor> authorMap = new HashMap<Integer, CVSAuthor>();
	public static CVSProject getAuthor(Integer id){
		return projectMap.get(id);
	}
	public static CVSAuthor getAuthor(String name){
		for(CVSAuthor author : authorMap.values()){
			if(name!=null && author.getName().equals(name)){
				return author;
			}
		}
		return null;
	}
	public static  void cacheAuthor(CVSAuthor author){
		authorMap.put(author.getAuthorId(), author);
	}
	
	////////////////////////////////////// 5- CVSFileType ///////////////////////////////////////////////	
	private static HashMap<Integer, CVSFileType> fileTypeMap = new HashMap<Integer, CVSFileType>();
	public static CVSFileType getFileType(Integer id){
		return fileTypeMap.get(id);
	}
	public static CVSFileType getFileType(String extension){
		for(CVSFileType fileType : fileTypeMap.values()){
			if(extension!=null && fileType.getExtension().equals(extension)){
				return fileType;
			}
		}
		return null;
	}
	public static void cacheFileType(CVSFileType fileType){
		fileTypeMap.put(fileType.getFileTypeId(), fileType);
	}
	
	//////////////////////////////////////6- CVSTag ///////////////////////////////////////////////	
	private static HashMap<Integer, CVSTag> tagMap = new HashMap<Integer, CVSTag>();
	public static CVSTag getTag(Integer id){
		return tagMap.get(id);
	}
	public static CVSTag getTag(String tagName){
		for(CVSTag tag : tagMap.values()){
			if(tagName!=null && tag.getTagName().equals(tagName)){
				return tag;
			}
		}
		return null;
	}
	public static void cacheTag(CVSTag tag){
		tagMap.put(tag.getTagId(), tag);
	}
	
	//////////////////////////////////////7- CVSFile ///////////////////////////////////////////////	
	private static LinkedList<CVSFile> cvsFiles = new LinkedList<CVSFile>();
	
	public static CVSFile getCVSFile(String name, String path, Session session){
		for(CVSFile cvsFile : cvsFiles){
			if(cvsFile.getName().equals(name) && cvsFile.getPath().equals(path)){
				return cvsFile;
			}
		}
		Criteria criteria = session.createCriteria(CVSFile.class);
		criteria.add(Restrictions.eq("name", name));
		criteria.add(Restrictions.eq("path", path));
		return (CVSFile)criteria.uniqueResult();
	}
	public static void cacheCVSFile(CVSFile cvsFile){
		cvsFiles.add(cvsFile);
		if(cvsFiles.size()>19){
			clearCVSFileCache();
		}
	}	
	private static void clearCVSFileCache(){		// Clear old cvsFiles until 10 files remained in cache
		while(cvsFiles.size()>10){
			cvsFiles.remove(0);
		}
	}
}
