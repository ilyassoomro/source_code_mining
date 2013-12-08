package cvshistorytodbplugin.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;
import org.eclipse.team.internal.ccvs.core.ICVSFile;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileHistory;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;
import org.hibernate.Session;

import cvshistorytodbplugin.model.CVSAuthor;
import cvshistorytodbplugin.model.CVSFile;
import cvshistorytodbplugin.model.CVSFileType;
import cvshistorytodbplugin.model.CVSProject;
import cvshistorytodbplugin.model.CVSTag;
import cvshistorytodbplugin.model.Workspace;

public class HistoryToDBService {

	public static Workspace getWorkspace(IWorkspace iworkspace, Session session){
		String name = iworkspace.getRoot().getLocation().lastSegment();
		String path = iworkspace.getRoot().getLocation().toString();
		
		Workspace workspace = LocalCache.getWorkspace(name, path);
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
	public static CVSProject getProject(IProject iproject, Session session){
		
		String name = iproject.getName();
		String path = iproject.getLocation().toString();
		
		CVSProject project = LocalCache.getProject(name, path);
		// Save project if not found in local cache
		if(project==null){
			project = new CVSProject();
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
	public static CVSFileType getFileType(String extension, Session session){
		CVSFileType fileType = LocalCache.getFileType(extension);
		// Save fileType if not found in local cache
		if(fileType==null){
			fileType = new CVSFileType();
			fileType.setExtension(extension);
			fileType = BaseService.save(fileType, session);
		}
		return fileType;
	}
	
	public static CVSAuthor getAuthor(IFileRevision revision, Session session){
		
		String name = revision.getAuthor();
		
		CVSAuthor author = LocalCache.getAuthor(name);
		// Save author if not found in local cache
		if(author==null){
			author = new CVSAuthor();
			author.setName(name);
			author = BaseService.save(author, session);
		}
		return author;
	}
	
	public static Set<CVSTag> getCVSTags(IFileRevision revision, Session session){
		
		ITag[] tags = revision.getTags();
		Set<CVSTag> cvsTags = new HashSet<CVSTag>();
		if(tags!=null && tags.length>0){
			for(ITag tag: tags){
				CVSTag cvsTag = LocalCache.getTag(tag.getName());
				// Save tag if not found in local cache
				if(cvsTag==null){
					cvsTag = new CVSTag();
					cvsTag.setTagName(tag.getName());
					//cvsTag.setBranchNumber(tag.getBranchNumber());		// FIXME: getBranchNumber has been removed in later versions
					//cvsTag.setIsBase(tag.isBaseTag());
					//cvsTag.setIsHead(tag.isHeadTag());
					cvsTag = BaseService.save(cvsTag, session);
				}
				cvsTags.add(cvsTag);
			}
		}
		return cvsTags;
	}
	
	public static CVSFile getCVSFile(EclipseFile cfile, Session session){
		
		IResource file = cfile.getIResource();
		CVSFile cvsFile = LocalCache.getCVSFile(file.getName(), file.getLocation().toString(), session);
		
		if(cvsFile==null){		// IF file not found in cache or database, then save it
			cvsFile = new CVSFile();
			cvsFile.setName(file.getName());
			cvsFile.setSize(cfile.getSize());
			//cvsFile.setCreatedBy(file.get)
			//cvsFile.setCreatedDate(new Date());
			//cvsFile.setModifiedBy(file.get)
			cvsFile.setModifiedDate(new Date(file.getLocalTimeStamp()));
			cvsFile.setFileType(getFileType(file.getFileExtension(), session));
			cvsFile.setPath(file.getLocation().toString());
			cvsFile.setProject(getProject(file.getProject(), session));
			
			cvsFile = BaseService.save(cvsFile, session);
		}
		return cvsFile;
	}
	
	public static cvshistorytodbplugin.model.CVSFileRevision saveCVSFileRevision(IFileRevision revision, EclipseFile cfile,CVSFileHistory fileHistory, IProgressMonitor monitor, Session session){
		
		LocalCache.loadCache(session);		// load cache if not already loaded
		cvshistorytodbplugin.model.CVSFileRevision cvsFileRevision = new cvshistorytodbplugin.model.CVSFileRevision();
		cvsFileRevision.setAuthor(getAuthor(revision, session));
		cvsFileRevision.setComments(revision.getComment());
		cvsFileRevision.setCommittedDate(new Date(revision.getTimestamp()));
		CVSFile cvsFile = getCVSFile(cfile, session);
		if(revision.getContentIdentifier().equals("1.1")){		// If 1st commit revision, then get committed date (for creation date of file)
			cvsFile.setCreatedDate(cvsFileRevision.getCommittedDate());
			cvsFile.setCreatedBy(cvsFileRevision.getAuthor().getAuthorId());		// file creator author id
			session.update(cvsFile);	
		}
		cvsFileRevision.setCvsFile(cvsFile);
		cvsFileRevision.setRevision(revision.getContentIdentifier());
		// TODO need to get delta of this revision
		//cvsFileRevision.setDelta(delta)
		cvsFileRevision.setDelta(cvshistorytodbplugin.model.CVSFileRevision.getDeltaFromCVS(cfile, cvsFileRevision, fileHistory, monitor));
		cvsFileRevision.setTags(getCVSTags(revision, session));
		
		cvsFileRevision = BaseService.save(cvsFileRevision, session);
		
		return cvsFileRevision;
	}
	
}
