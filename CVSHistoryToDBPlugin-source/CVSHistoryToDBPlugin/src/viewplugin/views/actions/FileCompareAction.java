package viewplugin.views.actions;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.internal.ccvs.ui.CVSCompareEditorInput;
import org.eclipse.team.internal.ui.synchronize.actions.OpenInCompareAction;
import org.eclipse.ui.IViewSite;

import viewplugin.util.Logger;
import viewplugin.views.model.IResourceEditionNode;
import viewplugin.views.presentation.NodeContentProvider;
import cvshistorytodbplugin.views.HistoryToDBView;

@SuppressWarnings({"restriction", "rawtypes"})
public class FileCompareAction extends Action {
	TreeViewer viewer;

	Shell window;
	Button okButton;
	Button cancelButton;
	Text txtTicket;
	NodeContentProvider treeContentProvider;
	IViewSite viewSite;
	
	public FileCompareAction(TreeViewer viewer, NodeContentProvider treeContentProvider, IViewSite viewSite) {
		this.viewer = viewer;
		this.treeContentProvider = treeContentProvider;
		this.viewSite = viewSite;
		this.setText("Compare");
		this.setToolTipText("Compare File");
		//this.setImageDescriptor(ImageUtility.FILE_IMAGE_DESCRIPTOR);
	}
	
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(selection!=null && !selection.isEmpty() && selection.size()==2){
			
			try {
				Iterator it = selection.iterator();
				Object leftObj = it.next();
				Object rightObj = it.next();
				
				if(leftObj instanceof IResourceEditionNode && rightObj instanceof IResourceEditionNode ){
					CVSCompareEditorInput editor = new CVSCompareEditorInput(((IResourceEditionNode)leftObj).getResourceEditionNode(), 
							((IResourceEditionNode)rightObj).getResourceEditionNode());
				
					OpenInCompareAction.openCompareEditor(editor, HistoryToDBView.getTargetPage());
				}
				
			} catch (Exception e) {
				Logger.error(e, "Error while comparing Files");
				MessageDialog.openInformation(viewer.getControl().getShell(),"Error", "Error while comparing Files.\n"+e.getMessage());
			}
		}
	}
	
}
