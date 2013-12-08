package cvshistorytodbplugin.model;


import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import cvshistorytodbplugin.util.ImageUtility;



public class TreeFile extends TreeNode implements Cloneable {
	File file;
	public TreeFile(File file) {
		super(file.getName());
		this.file = file;
	}
	
	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}
	
	public File getFile(){
		return file;
	}
}
