package cvshistorytodbplugin.model;

public class CVSTag {
	Integer tagId;       
	String tagName;     
	Boolean isBase;      
	Boolean isHead;      
	String branchNumber;
	public Integer getTagId() {
		return tagId;
	}
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public Boolean getIsBase() {
		return isBase;
	}
	public void setIsBase(Boolean isBase) {
		this.isBase = isBase;
	}
	public Boolean getIsHead() {
		return isHead;
	}
	public void setIsHead(Boolean isHead) {
		this.isHead = isHead;
	}
	public String getBranchNumber() {
		return branchNumber;
	}
	public void setBranchNumber(String branchNumber) {
		this.branchNumber = branchNumber;
	}
	@Override
	public String toString() {
		return "CVSTag [tagId=" + tagId + ", tagName=" + tagName + ", isBase="
				+ isBase + ", isHead=" + isHead + ", branchNumber="
				+ branchNumber + "]";
	}

}
