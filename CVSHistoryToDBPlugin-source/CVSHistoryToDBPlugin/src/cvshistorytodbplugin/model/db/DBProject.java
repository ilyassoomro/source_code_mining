package cvshistorytodbplugin.model.db;


public class DBProject {
	Integer projectId;   
	String name;         
	String path;         
	Workspace workspace; 
	DBRepository repository;
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
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
	public Workspace getWorkspace() {
		return workspace;
	}
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}
	public DBRepository getRepository() {
		return repository;
	}
	public void setRepository(DBRepository repository) {
		this.repository = repository;
	}
	@Override
	public String toString() {
		return "CVSProject [projectId=" + projectId + ", name=" + name
				+ ", path=" + path + ", workspace=" + (workspace!=null? workspace.getName():"")
				+ ", repository=" + (repository!=null? repository.getHost()+"/"+repository.getPath():"") + "]";
	}

}
