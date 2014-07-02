package cvshistorytodbplugin.views.actions;


import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.FileEditorInput;

import cvshistorytodbplugin.model.IRemoteVersionNode;
import cvshistorytodbplugin.model.TreeNode;
import cvshistorytodbplugin.util.ImageUtility;
import cvshistorytodbplugin.util.Logger;
import cvshistorytodbplugin.views.presentation.NodeContentProvider;


@SuppressWarnings("restriction")
public class FileOpenAction extends Action {
	TreeViewer viewer;

	Shell window;
	Button okButton;
	Button cancelButton;
	Text txtTicket;
	NodeContentProvider treeContentProvider;
	IViewSite viewSite;
	
	public FileOpenAction(TreeViewer viewer, NodeContentProvider treeContentProvider, IViewSite viewSite) {
		this.viewer = viewer;
		this.treeContentProvider = treeContentProvider;
		this.viewSite = viewSite;
		this.setText("Open");
		this.setToolTipText("Open File");
		this.setImageDescriptor(ImageUtility.FILE_IMAGE_DESCRIPTOR);
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(selection!=null && !selection.isEmpty()){
			Iterator<Object> it = selection.iterator();
			while(it.hasNext()){
				Object selectedObj = it.next();
				if(selectedObj instanceof TreeNode){
					try{
						FileOpenAction.open((TreeNode)selectedObj, viewSite);
					}catch(Exception e){
						MessageDialog.openInformation(viewer.getControl().getShell(),"Error",
								"Cannot open file due to following error:\n"+e.getMessage());
					}
				}
			}
		}
	}
	
	public static void open(final Object node, final IViewSite viewSite) throws Exception{ 
		
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(viewSite.getShell());
		progressMonitorDialog.run(false, true, new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException { 
				monitor.beginTask("Opening: ",1);
				try{
					
					monitor.worked(10);
				} catch (Exception e) {
					Logger.error(e, "Cannot Open file: "+node);
					MessageDialog.openInformation(viewSite.getShell(),"Error",
							"Cannot open file due to following error:\n"+e.getMessage());
				}
				finally{
					monitor.done();
				}
			}
		});
	}
	private static void openFile(IRemoteVersionNode versionNode, IProgressMonitor monitor) throws Exception{
		ICVSRemoteFile remoteFile = versionNode.getRemoteVersion();
		CVSUIPlugin.getPlugin().openEditor(remoteFile, monitor); 
	}
	 
	private static void openFile(EclipseFile eclipseFile, IProgressMonitor monitor)throws Exception{
		if(eclipseFile!=null){
			FileEditorInput editorInput = new FileEditorInput(eclipseFile.getIFile());
			IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(eclipseFile.getIFile().getName());
			if(desc==null){		// If no editor found, then open in text editor
				desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(".txt");
			}
			page.openEditor(editorInput, desc.getId());
		}else{
			throw new Exception("File not found:" +eclipseFile);
		}
	}
	
}
