package cvshistorytodbplugin.model.db;

public class DBRepository {
	Integer repositoryId;  
	String host;          
	String port;           
	String path;
	String connectionType;
	public Integer getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(Integer repositoryId) {
		this.repositoryId = repositoryId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	@Override
	public String toString() {
		return "CVSRepository [repositoryId=" + repositoryId + ", host=" + host
				+ ", port=" + port + ", path=" + path + ", connectionType="
				+ connectionType + "]";
	}

}
