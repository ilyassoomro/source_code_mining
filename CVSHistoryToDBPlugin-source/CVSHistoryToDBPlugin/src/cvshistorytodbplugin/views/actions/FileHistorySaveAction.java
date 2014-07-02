package cvshistorytodbplugin.views.actions;


import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.ccvs.core.Policy;
import org.eclipse.ui.IViewSite;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cvshistorytodbplugin.model.ProjectFolder;
import cvshistorytodbplugin.model.TreeFile;
import cvshistorytodbplugin.model.TreeFolder;
import cvshistorytodbplugin.model.TreeNode;
import cvshistorytodbplugin.model.db.DBFileRevision;
import cvshistorytodbplugin.service.HistoryToDBService;
import cvshistorytodbplugin.util.FileHistoryUtility;
import cvshistorytodbplugin.util.HibernateSessionFactory;
import cvshistorytodbplugin.util.InputDialogUtility;
import cvshistorytodbplugin.util.Logger;
import cvshistorytodbplugin.views.presentation.NodeContentProvider;


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
		
		try {
			final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			new SaveHistoryJob(selection).schedule();
			
		} catch (Exception e) {
			Logger.error(e, "Error while saving File history");
			MessageDialog.openInformation(window,"Error", "Error while saving File history: "+e.getMessage());
		}
	}
	
	class SaveHistoryJob extends Job {
		IStructuredSelection selection;
		
		public SaveHistoryJob(IStructuredSelection selection) {
			super("Saving File History");
			this.selection =selection;
			resourceCount = getSelectedResourceCount(this.selection );
			currentResourceIndex = 1;
		}

		public IStatus run(IProgressMonitor monitor) {
			try {
				saveHistory(monitor);
				return Status.OK_STATUS;
			}catch (Exception e) {
				Logger.error(e, "Error while saving File history");
				throw new RuntimeException(e);
			}
		}

		private void saveHistory(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			
			try {
				if (resourceCount > 0) {
					monitor.beginTask("Saving File History: ", resourceCount);

					Iterator it = selection.iterator();
					Logger.debug("Saving history start");
					while (it.hasNext()) {
						Object node = it.next();
						if (node instanceof TreeNode) {
							ProjectFolder projectFolder = ((TreeNode) node).getFileProjectFolder();
							String dbConfig = InputDialogUtility.getDBConfiguration(projectFolder, true);

							if (node instanceof TreeFile) {
								saveFileHistory(((TreeFile) node),projectFolder, dbConfig, monitor);
							} else if (node instanceof TreeFolder || node instanceof ProjectFolder) {
								saveFolderHistory(((TreeNode) node), projectFolder, dbConfig, monitor);
							}
						}
					}
					Logger.debug("Saving history done");
				}
			} catch (OperationCanceledException c) {
				Logger.error("Save History Action Cancelled");
			} catch (Exception e) {
				Logger.error(e,"Error while saving File history: " + e.getMessage());
				throw new RuntimeException(e);
			} finally {
				monitor.done();
			}
		}
	}
	   
	private int getSelectedResourceCount(IStructuredSelection selection){
		int count=0;
		if(selection!=null && !selection.isEmpty()){
			Iterator it = selection.iterator();
			while(it.hasNext()){
				Object node = it.next();
				if(node instanceof TreeFile){
					count++;
				}
				else if(node instanceof TreeFolder){
					count+=((TreeFolder)node).getChildCount();
				}
				else if(node instanceof ProjectFolder){
					count+=((ProjectFolder)node).getChildCount();
				}
			}
		}
		return count;
	}
	
	private void saveFolderHistory(TreeNode treeFolder,ProjectFolder projectFolder,String dbConfig, IProgressMonitor monitor) throws Exception{
		if(treeFolder.hasChildren()){
			Object[] childs = treeFolder.getChildren();
			for(Object child: childs){
				if(child instanceof TreeFile){
					saveFileHistory((TreeFile)child, projectFolder, dbConfig, monitor);
				}
				else if(child instanceof TreeFolder){
					saveFolderHistory((TreeFolder)child, projectFolder, dbConfig, monitor);
				}
			}
		}
	}
	
	private int currentResourceIndex = 0;
	private int resourceCount = 0;
	
	private void saveFileHistory(TreeFile treeFile,ProjectFolder projectFolder,String dbConfig, IProgressMonitor monitor) throws Exception{
		
		File file = treeFile.getFile();
		String path = file.getParent().getFullPath().toString();
		if(path.length()>30){
			path = path.substring(0,29)+"..";
		}
		String message = "Saving History of File("+(currentResourceIndex)+" of "+resourceCount+"): "+(path+"/"+file.getName());
		//monitor  = Policy.subMonitorFor(monitor, 1);
		
		Transaction tx = null;
		try{
			Session session = HibernateSessionFactory.getSession(dbConfig, monitor);
			tx = session.beginTransaction();
			//monitor.worked(10);
			
			monitor.setTaskName(message);
			Logger.debug(message);
			
			Policy.checkCanceled(monitor);
			IFileHistory fileHistory = FileHistoryUtility.getFileHistory(file);
			if(fileHistory!=null){			// If file is not managed by repository
				IFileRevision[] revisions = FileHistoryUtility.getFileRevisions(file, fileHistory);
				if(revisions!=null){
					for(int i=0;i<revisions.length; i++){
						IFileRevision revision = revisions[i]; 
						
						Policy.checkCanceled(monitor);
						//HistoryToDBView.readAndUpdate();
						DBFileRevision dbFileRevision = HistoryToDBService.saveFileRevision(revision, file,fileHistory,monitor, session);
					}
				}
			}
			tx.commit();
			
		}catch(Exception e){
			if(tx!=null)
				tx.rollback();
			throw e;
		}
		finally{
			HibernateSessionFactory.closeSession();
		}
		currentResourceIndex++;
		monitor.worked(1);
		Policy.checkCanceled(monitor);
		//HistoryToDBView.readAndUpdate();
	}
}
