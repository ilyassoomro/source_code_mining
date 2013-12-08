package cvshistorytodbplugin.views.actions;


import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;

import cvshistorytodbplugin.model.ProjectFolder;
import cvshistorytodbplugin.model.TreeNode;
import cvshistorytodbplugin.util.InputDialogUtility;
import cvshistorytodbplugin.util.Logger;
import cvshistorytodbplugin.util.Utility;
import cvshistorytodbplugin.views.presentation.NodeContentProvider;


@SuppressWarnings({"rawtypes"})
public class DBSettingAction extends Action {
	TreeViewer viewer;

	Shell window;
	Button okButton;
	Button cancelButton;
	Text txtTicket;
	NodeContentProvider treeContentProvider;
	IViewSite viewSite;
	
	public DBSettingAction(TreeViewer viewer, NodeContentProvider treeContentProvider, IViewSite viewSite) {
		this.viewer = viewer;
		this.treeContentProvider = treeContentProvider;
		this.viewSite = viewSite;
		this.setText("Database Configuration");
		this.setToolTipText("Database Configuration");
	}
	
	public void run(){
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		boolean anyProjectSelected = false;
		if(selection!=null && !selection.isEmpty()){
			Iterator<Object> it = selection.iterator();
			while(it.hasNext()){
				Object selectedObj = it.next();
				if(selectedObj instanceof ProjectFolder){
					anyProjectSelected = true;
					try{
						ProjectFolder projectFolder = (ProjectFolder)selectedObj;
						String dbConfig = InputDialogUtility.getDBConfiguration(projectFolder);
						Logger.debug("DB confugration("+projectFolder+"): "+dbConfig);
						
					}catch(Exception e){
						MessageDialog.openInformation(viewer.getControl().getShell(),"Error",
								"Cannot open file due to following error:\n"+e.getMessage());
					}
				}
			}
		}
		
		if(!anyProjectSelected){
			MessageDialog.openInformation(viewer.getControl().getShell(),"Warning",
					"Select project to set db configuration.");
		}
	}
}
