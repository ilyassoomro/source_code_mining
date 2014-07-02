package cvshistorytodbplugin.model.db;

import java.util.Date;

public class DBFile {
	
	Integer fileId;       
	String name;          
	String path;          
	DBFileType fileType;  
	Long size;          
	DBProject project;    
	Date createdDate ; 
	Integer createdBy;    
	Date modifiedDate; 
	Integer modifiedBy;
	
	public Integer getFileId() {
		return fileId;
	}
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public DBFileType getFileType() {
		return fileType;
	}
	public void setFileType(DBFileType fileType) {
		this.fileType = fileType;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public DBProject getProject() {
		return project;
	}
	public void setProject(DBProject project) {
		this.project = project;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public Integer getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	@Override
	public String toString() {
		return "CVSFile [fileId=" + fileId + ", name=" + name + ", path="
				+ path + ", fileType=" + (fileType!=null ? fileType.getExtension() :"") + ", size=" + size
				+ ", project=" + (project!=null? project.getName():"") + ", createdDate=" + createdDate
				+ ", createdBy=" + createdBy + ", modifiedDate=" + modifiedDate
				+ ", modifiedBy=" + modifiedBy + "]";
	}   

}
