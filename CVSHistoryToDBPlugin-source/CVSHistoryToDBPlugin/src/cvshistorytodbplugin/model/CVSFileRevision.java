package cvshistorytodbplugin.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ccvs.core.CVSMessages;
import org.eclipse.team.internal.ccvs.core.CVSStatus;
import org.eclipse.team.internal.ccvs.core.ICVSFolder;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteResource;
import org.eclipse.team.internal.ccvs.core.ICVSRepositoryLocation;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.Policy;
import org.eclipse.team.internal.ccvs.core.client.Command;
import org.eclipse.team.internal.ccvs.core.client.Command.LocalOption;
import org.eclipse.team.internal.ccvs.core.client.listeners.ICommandOutputListener;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileHistory;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;

import cvshistorytodbplugin.util.CVSUtility;
import cvshistorytodbplugin.util.Logger;

@SuppressWarnings("restriction")
public class CVSFileRevision {
	Integer fileVersionId; 
	CVSFile cvsFile;         
	String revision;        
	CVSAuthor author;       
	Date committedDate;  
	String comments;        
	String delta;           

	Set<CVSFileRevisionTag> fileRevisionTags;

	public Integer getFileVersionId() {
		return fileVersionId;
	}

	public void setFileVersionId(Integer fileVersionId) {
		this.fileVersionId = fileVersionId;
	}

	public CVSFile getCvsFile() {
		return cvsFile;
	}

	public void setCvsFile(CVSFile cvsFile) {
		this.cvsFile = cvsFile;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public CVSAuthor getAuthor() {
		return author;
	}

	public void setAuthor(CVSAuthor author) {
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

	public Set<CVSFileRevisionTag> getFileRevisionTags() {
		return fileRevisionTags;
	}

	public void setFileRevisionTags(Set<CVSFileRevisionTag> fileRevisionTags) {
		this.fileRevisionTags = fileRevisionTags;
	}
	
	public void setTags(Set<CVSTag> tags) {
		if(tags!=null){
			this.fileRevisionTags = new HashSet<CVSFileRevisionTag>();
			for(CVSTag tag: tags){
				CVSFileRevisionTag fileTag = new CVSFileRevisionTag();
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
		return "CVSFileRevision [fileVersionId=" + fileVersionId + ", cvsFile="
				+ (cvsFile!=null? cvsFile.getName():"") + ", revision=" + revision + ", author=" + (author!=null?author.getName():"")
				+ ", committedDate=" + committedDate + ", comments=" + comments
				+ ", delta=" + delta + ", fileRevisionTagsCount=" + (fileRevisionTags!=null? fileRevisionTags.size():"")
				+ "]";
	}
	
	/************************************ Static Methods *********************************/
	public static String getDeltaFromCVS(EclipseFile eclipseFile, CVSFileRevision revision,CVSFileHistory fileHistory, IProgressMonitor monitor){
		String version1 = revision.getRevision();
		String version2 = CVSUtility.getPreviousVersion(version1, fileHistory);
		if(version2!=null){
			org.eclipse.team.internal.ccvs.core.client.Session session = null;
			try {
				ICVSRemoteResource remoteFolder = CVSWorkspaceRoot.getRemoteResourceFor(eclipseFile.getParent());
				
				session = 
					new org.eclipse.team.internal.ccvs.core.client.Session(remoteFolder.getRepository(), eclipseFile.getParent(), false /* create backups */);
				
				session.open(Policy.subMonitorFor(monitor, 10), false /* read-only */);
			
				Logger.debug("\n\n\nCheck Difference between: "+version1 +"   and   "+version2+"\n");
				DiffOutputListener diffListener = new DiffOutputListener();
				IStatus status = Command.DIFF.execute(
					session,
					Command.NO_GLOBAL_OPTIONS,
					new LocalOption[] {
							 Command.DIFF.makeTagOption(new org.eclipse.team.internal.ccvs.core.CVSTag(version2, org.eclipse.team.internal.ccvs.core.CVSTag.VERSION)),
							 Command.DIFF.makeTagOption(new org.eclipse.team.internal.ccvs.core.CVSTag(version1, org.eclipse.team.internal.ccvs.core.CVSTag.VERSION))
					},
					new ICVSResource[] { eclipseFile },
					diffListener,
					Policy.subMonitorFor(monitor, 80));
					if(!diffListener.anyError){
						String delta = diffListener.buffer.toString();
						Logger.debug(delta);
						return delta;
					}
			}catch(Exception e){
				Logger.error(e,"Error while checking Diff: "+e.getMessage());
			}
			finally {
				try{
					session.close();
				}catch(Exception e){
					Logger.error(e,"Error while Closing Session: "+e.getMessage());
				}
				//monitor.done();
			}
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
