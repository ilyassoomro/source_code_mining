package cvshistorytodbplugin.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;
import org.hibernate.Session;

import cvshistorytodbplugin.model.db.DBAuthor;
import cvshistorytodbplugin.model.db.DBFile;
import cvshistorytodbplugin.model.db.DBFileRevision;
import cvshistorytodbplugin.model.db.DBFileType;
import cvshistorytodbplugin.model.db.DBProject;
import cvshistorytodbplugin.model.db.DBTag;
import cvshistorytodbplugin.model.db.Workspace;
import cvshistorytodbplugin.util.FileHistoryUtility;

public class HistoryToDBService {

	public static Workspace getWorkspace(IWorkspace iworkspace, Session session){
		String name = iworkspace.getRoot().getLocation().lastSegment();
		String path = iworkspace.getRoot().getLocation().toString();
		
		Workspace workspace = HibernateCache.getWorkspace(name, path, session);
		// Save Workspace if not found in local cache
		if(workspace==null){
			workspace = new Workspace();
			workspace.setName(name);
			workspace.setPath(path);
			workspace = BaseService.save(workspace, session);
		}
		return workspace;
	}

	/*
	public static CVSRepository getRepository(CVSFileRevision revision, Session session){
		ICVSRepositoryLocation irepository = revision.getCVSRemoteFile().getRepository();
		return getRepository(irepository, session);
	}
	public static CVSRepository getRepository(ICVSRepositoryLocation irepository, Session session){
		String host = irepository.getHost();
		String path = irepository.getRootDirectory();
		
		CVSRepository repository = LocalCache.getRepository(host, path);
		// Save repository if not found in local cache
		if(repository==null){
			repository = new CVSRepository();
			repository.setHost(host);
			repository.setPath(path);
			repository.setPort(""+irepository.getPort());
			repository.setConnectionType(irepository.getMethod().getName());
			repository = BaseService.save(repository, session);
		}
		return repository;
	}
	 
	public static CVSProject getProject(CVSFileRevision revision, Session session){
		IProject iproject = revision.getCVSRemoteFile().getIResource().getProject();
		return getProject(iproject, session);
	}
	*/
	public static DBProject getProject(IProject iproject, Session session){
		
		String name = iproject.getName();
		String path = iproject.getLocation().toString();
		
		DBProject project = HibernateCache.getProject(name, path, session);
		// Save project if not found in local cache
		if(project==null){
			project = new DBProject();
			project.setName(name);
			project.setPath(path); 
			// TODO : need to find repository of project
			//project.setRepository(getRepository(revision, session));
			project.setWorkspace(getWorkspace(iproject.getWorkspace(), session));
			project = BaseService.save(project, session);
		}
		return project;
	}
	/*
	public static CVSFileType getFileType(CVSFileRevision revision, Session session){
		String extension = revision.getCVSRemoteFile().getIResource().getFileExtension();
		return getFileType(extension, session);
	}
	*/
	public static DBFileType getFileType(String extension, Session session){
		DBFileType fileType = HibernateCache.getFileType(extension, session);
		// Save fileType if not found in local cache
		if(fileType==null){
			fileType = new DBFileType();
			fileType.setExtension(extension);
			fileType = BaseService.save(fileType, session);
		}
		return fileType;
	}
	
	public static DBAuthor getAuthor(IFileRevision revision, Session session){
		
		String name = revision.getAuthor();
		
		DBAuthor author = HibernateCache.getAuthor(name, session);
		// Save author if not found in local cache
		if(author==null){
			author = new DBAuthor();
			author.setName(name);
			author = BaseService.save(author, session);
		}
		return author;
	}
	
	public static Set<DBTag> getDBTags(IFileRevision revision, Session session){
		
		ITag[] tags = revision.getTags();
		Set<DBTag> dbTags = new HashSet<DBTag>();
		if(tags!=null && tags.length>0){
			for(ITag tag: tags){
				DBTag dbTag = HibernateCache.getTag(tag.getName(), session);
				// Save tag if not found in local cache
				if(dbTag==null){
					dbTag = new DBTag();
					dbTag.setTagName(tag.getName());
					//dbTag.setBranchNumber(tag.getBranchNumber());		// FIXME: getBranchNumber has been removed in later versions
					//dbTag.setIsBase(tag.isBaseTag());
					//dbTag.setIsHead(tag.isHeadTag());
					dbTag = BaseService.save(dbTag, session);
				}
				dbTags.add(dbTag);
			}
		}
		return dbTags;
	}
	
	public static DBFile getDBFile(File file, Session session){
		
		//IResource file = cfile.getIResource();
		DBFile dbFile = HibernateCache.getDBFile(file.getName(), file.getLocation().toString(), session);
		
		if(dbFile==null){		// IF file not found in cache or database, then save it
			dbFile = new DBFile();
			dbFile.setName(file.getName());
			//dbFile.setSize(cfile.getSize());
			//dbFile.setCreatedBy(file.get)
			//dbFile.setCreatedDate(new Date());
			//dbFile.setModifiedBy(file.get)
			dbFile.setModifiedDate(new Date(file.getLocalTimeStamp()));
			dbFile.setFileType(getFileType(file.getFileExtension(), session));
			dbFile.setPath(file.getLocation().toString());
			dbFile.setProject(getProject(file.getProject(), session));
			
			dbFile = BaseService.save(dbFile, session);
		}
		return dbFile;
	}
	
	public static DBFileRevision saveFileRevision(IFileRevision revision,File file, IFileHistory fileHistory, IProgressMonitor monitor, Session session){
		
		DBFileRevision dbFileRevision = new DBFileRevision();
		dbFileRevision.setAuthor(getAuthor(revision, session));
		dbFileRevision.setComments(revision.getComment());
		dbFileRevision.setCommittedDate(new Date(revision.getTimestamp()));
		DBFile dbFile = getDBFile(file, session);
		if(FileHistoryUtility.isHead(file, revision.getContentIdentifier())){		// If 1st commit revision, then get committed date (for creation date of file)
			dbFile.setCreatedDate(dbFileRevision.getCommittedDate());
			dbFile.setCreatedBy(dbFileRevision.getAuthor().getAuthorId());		// file creator author id
			session.update(dbFile);	
		}
		dbFileRevision.setDbFile(dbFile);
		dbFileRevision.setRevision(revision.getContentIdentifier());
		
		dbFileRevision.setDelta(FileRevisionDeltaProvider.getDeltaFromRepository(file, dbFileRevision.getRevision(), fileHistory, monitor));
		dbFileRevision.setTags(getDBTags(revision, session));
		
		dbFileRevision = BaseService.save(dbFileRevision, session);
		
		return dbFileRevision;
	}
	
}
