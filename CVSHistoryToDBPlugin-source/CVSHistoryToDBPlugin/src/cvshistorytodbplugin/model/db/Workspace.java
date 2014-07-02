package cvshistorytodbplugin.model.db;

public class Workspace {
	Integer workspaceId;
	String name;        
	String path;
	
	public Integer getWorkspaceId() {
		return workspaceId;
	}
	
	public void setWorkspaceId(Integer workspaceId) {
		this.workspaceId = workspaceId;
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
	@Override
	public String toString() {
		return "Workspace [workspaceId=" + workspaceId + ", name=" + name
				+ ", path=" + path + "]";
	}        

}
