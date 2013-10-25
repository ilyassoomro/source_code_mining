package cvshistorytodbplugin.model;

public class CVSAuthor {
	Integer authorId; 
	String name;
	public Integer getAuthorId() {
		return authorId;
	}
	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "CVSAuthor [authorId=" + authorId + ", name=" + name + "]";
	}      

}
