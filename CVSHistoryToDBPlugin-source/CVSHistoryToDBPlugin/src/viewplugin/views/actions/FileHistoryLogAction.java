package viewplugin.views.actions;

import java.util.Iterator;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Synchronizer;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileHistory;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;
import org.eclipse.ui.IViewSite;

import viewplugin.util.Logger;
import viewplugin.views.model.TreeFile;
import viewplugin.views.presentation.NodeContentProvider;

@SuppressWarnings({"restriction", "rawtypes"})
public class FileHistoryLogAction extends Action {
	TreeViewer viewer;

	Shell window;
	Button okButton;
	Button cancelButton;
	Text txtTicket;
	NodeContentProvider treeContentProvider;
	IViewSite viewSite;
	
	public FileHistoryLogAction(TreeViewer viewer, NodeContentProvider treeContentProvider, IViewSite viewSite) {
		this.viewer = viewer;
		this.treeContentProvider = treeContentProvider;
		this.viewSite = viewSite;
		this.setText("Log History");
		this.setToolTipText("Log History");
		//this.setImageDescriptor(ImageUtility.FILE_IMAGE_DESCRIPTOR);
	}
	
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(selection!=null && !selection.isEmpty()){
			
			try {
				Iterator it = selection.iterator();
				Logger.debug("Receiving File History of ...");
				StringBuilder message = new StringBuilder("\n\n");
				while(it.hasNext()){
					Object node = it.next();
					if(node instanceof TreeFile){
						File file = ((TreeFile) node).getFile();
						EclipseFile eclipseFile = (EclipseFile)CVSWorkspaceRoot.getCVSFileFor(file);
						Logger.debug(eclipseFile);
						
						message.append("\n\nFile: "+eclipseFile+"{\n");
						CVSFileHistory fileHistory = new CVSFileHistory(eclipseFile); 
						fileHistory.refresh(CVSFileHistory.REFRESH_REMOTE, new NullProgressMonitor());
						IFileRevision[] revisions = fileHistory.getFileRevisions();
						for(IFileRevision revision: revisions){ 
							message.append("\tRevision : "+revision.getContentIdentifier()+"\n");
							message.append("\tAuthor : "+revision.getAuthor()+"\n");
							message.append("\tDate : "+revision.getTimestamp()+"\n");
							message.append("\tComments : "+revision.getComment()+"\n"); 
							message.append("\tTags : {\n"); 
							ITag[] tags = revision.getTags(); 
							for(ITag tag: tags){
								message.append("\t\t"+tag.getName()+"\n"); 
							}
							message.append("\t}\n");
						}
						
						message.append("}\n");
					}
				}
				Logger.debug(message);
				Logger.showConsole();
				
			} catch (Exception e) {
				Logger.error(e, "Error while comparing Files");
				MessageDialog.openInformation(viewer.getControl().getShell(),"Error", "Error while comparing Files.\n"+e.getMessage());
			}
		}
	}
	
}
