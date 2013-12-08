package cvshistorytodbplugin.views.presentation;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;
import org.eclipse.ui.IViewSite;

import cvshistorytodbplugin.model.TreeFolder;
import cvshistorytodbplugin.views.actions.FileOpenAction;


public class NodeContentHandler {

	TreeViewer viewer;
	IViewSite viewSite;
	
	public NodeContentHandler(TreeViewer viewer, IViewSite viewSite){
		this.viewer = viewer;
		this.viewSite = viewSite;
	}
	
	public void handleDoubleClick(DoubleClickEvent event){
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		try{
			if(obj instanceof TreeFolder){
				if(viewer.getExpandedState(obj)){
					viewer.collapseToLevel(obj,1);
				}else{
					viewer.expandToLevel(obj, 1);
				}
			}
			else if(obj instanceof EclipseFile){
				FileOpenAction.open((EclipseFile)obj, viewSite);
			}
			
		}catch(Exception e){
			MessageDialog.openInformation(
					viewer.getControl().getShell(),
					"Error",
					"Cannot open file due to following error:\n"+e.getMessage());
		}
	}
}
