package cvshistorytodbplugin.model;

public class CVSFileType {
	Integer fileTypeId;
	String extension;
	
	public Integer getFileTypeId() {
		return fileTypeId;
	}
	public void setFileTypeId(Integer fileTypeId) {
		this.fileTypeId = fileTypeId;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension==null?"":extension;
	}
	@Override
	public String toString() {
		return "CVSFileType [fileTypeId=" + fileTypeId + ", extension="
				+ extension + "]";
	}
}
