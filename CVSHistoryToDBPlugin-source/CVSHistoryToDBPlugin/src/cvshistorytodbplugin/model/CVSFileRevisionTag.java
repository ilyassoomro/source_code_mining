package cvshistorytodbplugin.model;

public class CVSFileRevisionTag {
	Integer fileRevisionTagId;
	CVSFileRevision fileRevision;    
	CVSTag tag;
	public Integer getFileRevisionTagId() {
		return fileRevisionTagId;
	}
	public void setFileRevisionTagId(Integer fileRevisionTagId) {
		this.fileRevisionTagId = fileRevisionTagId;
	}
	public CVSFileRevision getFileRevision() {
		return fileRevision;
	}
	public void setFileRevision(CVSFileRevision fileRevision) {
		this.fileRevision = fileRevision;
	}
	public CVSTag getTag() {
		return tag;
	}
	public void setTag(CVSTag tag) {
		this.tag = tag;
	}
	@Override
	public String toString() {
		return "CVSFileRevisionTag [fileRevisionTagId=" + fileRevisionTagId
				+ ", fileRevision=" + fileRevision + ", tag=" + tag + "]";
	}              

}
