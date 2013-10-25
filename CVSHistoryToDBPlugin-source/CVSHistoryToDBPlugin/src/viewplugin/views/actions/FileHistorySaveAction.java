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
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.ITag;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileHistory;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;
import org.eclipse.ui.IViewSite;
import org.hibernate.Session;

import viewplugin.util.HibernateSessionFactory;
import viewplugin.util.InputDialogUtility;
import viewplugin.util.Logger;
import viewplugin.views.model.ProjectFolder;
import viewplugin.views.model.TreeFile;
import viewplugin.views.presentation.NodeContentProvider;

@SuppressWarnings({"restriction", "rawtypes"})
public class FileHistorySaveAction extends Action {
	TreeViewer viewer;

	Shell window;
	Button okButton;
	Button cancelButton;
	Text txtTicket;
	NodeContentProvider treeContentProvider;
	IViewSite viewSite;
	
	public FileHistorySaveAction(TreeViewer viewer, NodeContentProvider treeContentProvider, IViewSite viewSite) {
		this.viewer = viewer;
		this.treeContentProvider = treeContentProvider;
		this.viewSite = viewSite;
		this.setText("Save History");
		this.setToolTipText("Save History");
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
						ProjectFolder projectFolder = ((TreeFile) node).getFileProjectFolder();
						String dbConfig = InputDialogUtility.getDBConfiguration(projectFolder, true);
						try{
							Session session = HibernateSessionFactory.getSession(dbConfig);
							
							EclipseFile eclipseFile = (EclipseFile)CVSWorkspaceRoot.getCVSFileFor(file);
							Logger.debug(eclipseFile);
							
							message.append("\n\nFile: "+eclipseFile+"{\n");
							CVSFileHistory fileHistory = new CVSFileHistory(eclipseFile); 
							fileHistory.refresh(CVSFileHistory.REFRESH_REMOTE, new NullProgressMonitor());
							IFileRevision[] revisions = fileHistory.getFileRevisions();
							for(IFileRevision revision: revisions){ 
								
								
								
								ITag[] tags = revision.getTags(); 
								for(ITag tag: tags){
									 
								}
								
							}
							
							message.append("}\n");
						}
						finally{
							HibernateSessionFactory.closeSession();
						}
					}
				}
				Logger.debug(message);
				Logger.showConsole();
				
			} catch (Throwable e) {
				Logger.error(e, "Error while saving File history");
				MessageDialog.openInformation(viewer.getControl().getShell(),"Error", "Error while saving File history.\n"+e.getMessage());
			}
		}
	}
	
}
