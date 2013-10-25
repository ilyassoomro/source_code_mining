package viewplugin.views.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;

import viewplugin.views.model.ProjectFolder;
import viewplugin.views.model.TreeNode;
import viewplugin.views.presentation.NodeContentProvider;

@SuppressWarnings({"rawtypes"})
public class ProjectOpenAction extends Action {
	TreeViewer viewer;

	Shell window;
	Button okButton;
	Button cancelButton;
	Text txtTicket;
	NodeContentProvider treeContentProvider;
	IViewSite viewSite;
	
	public ProjectOpenAction(TreeViewer viewer, NodeContentProvider treeContentProvider, IViewSite viewSite) {
		this.viewer = viewer;
		this.treeContentProvider = treeContentProvider;
		this.viewSite = viewSite;
		this.setText("Open Project");
		this.setToolTipText("Open Project");
	}
	
	public void run(){
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		Iterator iterator = selection.iterator();
		boolean doOpen = this.getText().equals("Open Project");
		boolean doClose = this.getText().equals("Close Project");
		while(iterator.hasNext()){
			TreeNode node = (TreeNode)iterator.next();
			if(node instanceof ProjectFolder){
				IProject p = ((ProjectFolder)node).getProject();
				if(p==null){
					continue;
				}
				try {
					if(doOpen && !p.isOpen()){
						p.open(new NullProgressMonitor());
					}
					else if(doClose && p.isOpen()){
						p.close(new NullProgressMonitor());
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		viewer.refresh();
	}
}
