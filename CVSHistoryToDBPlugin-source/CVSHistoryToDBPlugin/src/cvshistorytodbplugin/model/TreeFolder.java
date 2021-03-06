package cvshistorytodbplugin.model;


import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import cvshistorytodbplugin.util.ImageUtility;
import cvshistorytodbplugin.util.Logger;



public class TreeFolder extends TreeNode implements Cloneable {
	Folder folder;
	public TreeFolder(Folder folder) {
		super(folder.getName());
		this.folder = folder;
	}
	
	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
	}
	
	public boolean hasChildren() {
		listMembers();
		return super.hasChildren();
	}
	
	private boolean membersListed = false;
	private void listMembers(){
		if(!membersListed){
			try{
				IResource[] resources = folder.members();
				for(IResource resource: resources){
					if(resource instanceof Folder){
						this.addChild(new TreeFolder((Folder)resource));
					}
					else if(resource instanceof File){
						this.addChild(new TreeFile((File)resource));
					}
				}
			}catch(Exception e){
				Logger.error(e, "Error while listing members of Folder: "+folder);
			}finally{
				membersListed = true;
			}
		}
	}
	
	public Integer getChildCount(){
		return getFileCount(folder);
	}
	
	public static int getFileCount(Folder folder){
		int count=0;
		if(folder!=null){
			IResource[] resources;
			try {
				resources = folder.members();
				for(IResource resource: resources){
					if(resource instanceof Folder){
						count+=getFileCount((Folder)resource);
					}
					else if(resource instanceof File){
						count++;
					}
				}
			} catch (CoreException e) {
				Logger.error(e, "Error while determining file count of folder");
			}
		}
		return count;
	}
}
