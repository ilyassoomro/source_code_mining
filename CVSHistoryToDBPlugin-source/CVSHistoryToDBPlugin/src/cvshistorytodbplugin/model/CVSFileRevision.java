package cvshistorytodbplugin.model;

import java.util.Date;
import java.util.Set;

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
	
	@Override
	public String toString() {
		return "CVSFileRevision [fileVersionId=" + fileVersionId + ", cvsFile="
				+ cvsFile + ", revision=" + revision + ", author=" + author
				+ ", committedDate=" + committedDate + ", comments=" + comments
				+ ", delta=" + delta + ", fileRevisionTags=" + fileRevisionTags
				+ "]";
	}
}
