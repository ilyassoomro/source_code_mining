package cvshistorytodbplugin.model;


import org.eclipse.swt.graphics.Image;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.ui.ResourceEditionNode;

import cvshistorytodbplugin.util.ImageUtility;


@SuppressWarnings("restriction")
public class DependencyVersionNode extends TreeNode implements Cloneable, IResourceEditionNode, IRemoteVersionNode {
	ICVSRemoteFile remoteFile;
	String version;
	String comments;
	Integer versionNo;
	
	public DependencyVersionNode(String version,Integer versionNo, String comments, ICVSRemoteFile remoteFile) {
		super(versionNo+": "+comments);
		this.remoteFile = remoteFile;
		this.versionNo = versionNo;
		this.version = version;
		this.comments = comments;
	}
	
	public Integer getVersionNo(){
		return versionNo;
	}
	public String getVersion() {
		return version;
	}

	public String getComments() {
		return comments;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public Image getImage() {
		return ImageUtility.DEPENDENCY_IMAGE;
	}

	private ResourceEditionNode resourceEditionNode;
	
	public ResourceEditionNode getResourceEditionNode() throws TeamException {
		if(resourceEditionNode==null){
			resourceEditionNode = new ResourceEditionNode(remoteFile);
		}
		return resourceEditionNode;
	}

	@Override
	public ICVSRemoteFile getRemoteVersion() throws TeamException {
		return remoteFile;
	}
}
