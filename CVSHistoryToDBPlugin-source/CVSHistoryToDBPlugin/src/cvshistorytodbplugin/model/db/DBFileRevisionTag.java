package cvshistorytodbplugin.model.db;

public class DBFileRevisionTag {
	Integer fileRevisionTagId;
	DBFileRevision fileRevision;    
	DBTag tag;
	public Integer getFileRevisionTagId() {
		return fileRevisionTagId;
	}
	public void setFileRevisionTagId(Integer fileRevisionTagId) {
		this.fileRevisionTagId = fileRevisionTagId;
	}
	public DBFileRevision getFileRevision() {
		return fileRevision;
	}
	public void setFileRevision(DBFileRevision fileRevision) {
		this.fileRevision = fileRevision;
	}
	public DBTag getTag() {
		return tag;
	}
	public void setTag(DBTag tag) {
		this.tag = tag;
	}
	@Override
	public String toString() {
		return "CVSFileRevisionTag [fileRevisionTagId=" + fileRevisionTagId
				+ ", fileRevision=" + (fileRevision!=null?fileRevision.getRevision():"") + ", tag=" + (tag!=null? tag.getTagName():"") + "]";
	}              

}
