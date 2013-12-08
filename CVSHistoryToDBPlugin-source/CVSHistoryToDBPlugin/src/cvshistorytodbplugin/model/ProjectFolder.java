package cvshistorytodbplugin.model;


import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.graphics.Image;

import cvshistorytodbplugin.util.ImageUtility;
import cvshistorytodbplugin.util.Logger;


public class ProjectFolder extends TreeNode {

	public static final QualifiedName DB_CONFIG = new QualifiedName("DB_CONFIG", "DB_CONFIG");
	
	IProject project;
	String dbConfig;
	
	
	public ProjectFolder(IProject project) {
		super(project!=null ? project.getName() : "Unknwon Project");
		this.project = project;
	}
	
	@Override
	public int compareTo(TreeNode o) {
		if(o instanceof ProjectFolder){
			return this.project.getName().toLowerCase().compareTo(((ProjectFolder)o).project.getName().toLowerCase()); 
		}
		return super.compareTo(o);
	}

	public ProjectFolder clone(){
		ProjectFolder copy = new ProjectFolder(project);
		if(children!=null){
			for(TreeNode child: children){
				TreeNode copyChild =child.clone();
				copyChild.setParent(copy);
				copy.addChild(copyChild);
			}
		}
		return copy;
	}
	
	@Override
	public Image getImage() {
		if(project!=null && project.isOpen()){
			return ImageUtility.PROJECT_IMAGE;
		}else{
			return ImageUtility.PROJECT_CLOSED_IMAGE;
		}
	}
	
	public IProject getProject() {
		return project;
	}
	
	public boolean hasChildren() {
		listMembers();
		return super.hasChildren();
	}
	
	private boolean membersListed = false;
	private void listMembers(){
		if(project.isOpen()){
			if(!membersListed){
				try{
					IResource[] resources = project.members();
					for(IResource resource: resources){
						if(resource instanceof Folder){
							this.addChild(new TreeFolder((Folder)resource));
						}
						else if(resource instanceof File){
							this.addChild(new TreeFile((File)resource));
						}
					}
				}catch(Exception e){
					Logger.error(e, "Error while listing members of Project: "+project);
				}finally{
					membersListed = true;
				}
			}
		}
	}
	
	public String getDbConfig() {
		if(dbConfig==null){			// If db configuration is not yet loaded, then just load from project
			try {
				dbConfig = this.project.getPersistentProperty(DB_CONFIG);
			} catch (CoreException e) {
				Logger.error(e, "Error while determining DB Configuration of Project");
			}
		}
		return dbConfig!=null ? dbConfig : "";
	}
	
	public void setDbConfig(String dbConfig) {
		this.dbConfig = dbConfig;
		try {
			this.project.setPersistentProperty(DB_CONFIG, dbConfig);
		} catch (CoreException e) {
			Logger.error(e, "Error while setting DB Configuration of Project");
			Logger.showConsole();
		}

	}
}
