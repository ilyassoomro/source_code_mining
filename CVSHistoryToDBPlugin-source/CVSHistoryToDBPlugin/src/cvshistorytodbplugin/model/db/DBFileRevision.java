package cvshistorytodbplugin.model.db;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.egit.ui.internal.history.FileDiff;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.internal.ccvs.core.CVSMessages;
import org.eclipse.team.internal.ccvs.core.CVSStatus;
import org.eclipse.team.internal.ccvs.core.ICVSFolder;
import org.eclipse.team.internal.ccvs.core.ICVSRepositoryLocation;
import org.eclipse.team.internal.ccvs.core.client.listeners.ICommandOutputListener;

import cvshistorytodbplugin.util.Logger;

@SuppressWarnings("restriction")
public class DBFileRevision {
	Integer fileVersionId; 
	DBFile dbFile;         
	String revision;        
	DBAuthor author;       
	Date committedDate;  
	String comments;        
	String delta;           

	Set<DBFileRevisionTag> fileRevisionTags;

	public Integer getFileVersionId() {
		return fileVersionId;
	}

	public void setFileVersionId(Integer fileVersionId) {
		this.fileVersionId = fileVersionId;
	}

	public DBFile getDbFile() {
		return dbFile;
	}

	public void setDbFile(DBFile cvsFile) {
		this.dbFile = cvsFile;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public DBAuthor getAuthor() {
		return author;
	}

	public void setAuthor(DBAuthor author) {
		this.author = author;
	}

	public Date getCommittedDate() {
		return committedDate;
	}

	public void setCommittedDate(Date committedDate) {
		this.committedDate = committedDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDelta() {
		return delta;
	}

	public void setDelta(String delta) {
		this.delta = delta;
	}

	public Set<DBFileRevisionTag> getFileRevisionTags() {
		return fileRevisionTags;
	}

	public void setFileRevisionTags(Set<DBFileRevisionTag> fileRevisionTags) {
		this.fileRevisionTags = fileRevisionTags;
	}
	
	public void setTags(Set<DBTag> tags) {
		if(tags!=null){
			this.fileRevisionTags = new HashSet<DBFileRevisionTag>();
			for(DBTag tag: tags){
				DBFileRevisionTag fileTag = new DBFileRevisionTag();
				fileTag.setTag(tag);
				fileTag.setFileRevision(this);
				this.fileRevisionTags.add(fileTag);
			}
		}else if(this.fileRevisionTags!=null){
			this.fileRevisionTags.clear();
		}
	}
	
	@Override
	public String toString() {
		return "DBFileRevision [fileVersionId=" + fileVersionId + ", DBFile="
				+ (dbFile!=null? dbFile.getName():"") + ", revision=" + revision + ", author=" + (author!=null?author.getName():"")
				+ ", committedDate=" + committedDate + ", comments=" + comments
				+ ", delta=" + delta + ", fileRevisionTagsCount=" + (fileRevisionTags!=null? fileRevisionTags.size():"")
				+ "]";
	}
	
	/************************************ Static Methods *********************************/
	public static String getDeltaFromHistory(File file, DBFileRevision revision,IFileHistory fileHistory, IProgressMonitor monitor){
		String version1 = revision.getRevision();
		try {
			
			RepositoryMapping mapping = RepositoryMapping.getMapping(file);
			Repository rep = mapping.getRepository();
			String gitPath = mapping.getRepoRelativePath(file);
			ObjectReader reader =  rep.newObjectReader();
			
			RevWalk rw = new RevWalk(rep);
			RevCommit revCommitAfter = rw.parseCommit(rep.resolve(version1));
			//RevCommit revCommitBefore = rw.parseCommit(revCommitAfter.getParents()[0].getId());
			
			Git git = new Git(rep);
			OutputStream out = new ByteArrayOutputStream();
			DiffCommand diffCommand = git.diff();
			diffCommand.setOutputStream(out);
			diffCommand.setPathFilter(PathFilter.create(gitPath));
			
			CanonicalTreeParser rev1 = new CanonicalTreeParser();
			rev1.reset(reader, rw.parseTree(rep.resolve(version1)));
			diffCommand.setNewTree(rev1);
			  
			if(revCommitAfter.getParents()!=null && revCommitAfter.getParents().length>0){
				CanonicalTreeParser rev2 = new CanonicalTreeParser();
				rev2.reset(reader,  rw.parseTree(revCommitAfter.getParents()[0].getId()));
				diffCommand.setOldTree(rev2);
			}else{
				diffCommand.setOldTree(new EmptyTreeIterator());
			}
			
			diffCommand.call();
			String delta = out.toString();
			
			return delta;
		}catch(Exception e){
			Logger.error(e,"Error while checking Diff: "+e.getMessage());
		}
		return null;
	}
	
	public static class DiffOutputListener implements ICommandOutputListener{

		StringBuffer buffer = new StringBuffer();
		boolean anyError = false;
		@Override
		public IStatus messageLine(String line,
				ICVSRepositoryLocation location, ICVSFolder commandRoot,
				IProgressMonitor monitor) {
			buffer.append(line+"\n");
			return new CVSStatus(IStatus.OK, CVSMessages.ok);
		}

		@Override
		public IStatus errorLine(String line, ICVSRepositoryLocation location,
				ICVSFolder commandRoot, IProgressMonitor monitor) {
			Logger.error(line);
			anyError = true;
			return new CVSStatus(IStatus.OK, CVSMessages.ok);
		}
	}
	/*************************************************************************************/
}
