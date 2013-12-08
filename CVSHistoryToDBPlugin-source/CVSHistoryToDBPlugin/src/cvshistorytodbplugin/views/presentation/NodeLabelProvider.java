package cvshistorytodbplugin.views.presentation;


import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import cvshistorytodbplugin.model.ProjectFolder;
import cvshistorytodbplugin.model.TreeFile;
import cvshistorytodbplugin.model.TreeFolder;


public class NodeLabelProvider extends LabelProvider {
	public String getText(Object obj) {
		if(obj!=null){
			if(obj instanceof ProjectFolder){
				return ((ProjectFolder)obj).getName();
			}
			else if(obj instanceof TreeFile){  
				return ((TreeFile)obj).getName();
			}
			else if(obj instanceof TreeFolder){
				return ((TreeFolder)obj).getName();
			}
		}
		return obj.toString();
	}
	public Image getImage(Object obj) {
		if(obj!=null){
			if(obj instanceof ProjectFolder){
				return ((ProjectFolder)obj).getImage();
			}
			else if(obj instanceof TreeFile){
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
			}
			else if(obj instanceof TreeFolder){
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			}
		}
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
}
