package cvshistorytodbplugin.views.actions;

import net.sf.versiontree.views.VersionTreeView;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.core.client.Command;
import org.eclipse.team.internal.ccvs.ui.operations.UpdateOperation;
import org.eclipse.team.internal.ccvs.ui.subscriber.WorkspaceSynchronizeParticipant;
import org.eclipse.team.internal.ui.TeamUIPlugin;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.ISynchronizeParticipant;
import org.eclipse.team.ui.synchronize.ResourceScope;
import org.eclipse.team.ui.synchronize.SubscriberParticipant;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;

import cvshistorytodbplugin.model.IResourceEditionNode;
import cvshistorytodbplugin.util.TreeViewerUtility;


public class TeamActions extends MenuManager {

	Action showHistory;
	Action showInVersionTree;
	Action syncronize;
	Action update;
	Action commit;
	TreeViewer viewer;
	IViewSite viewSite;
	
	public TeamActions(String text, TreeViewer viewer, IViewSite viewSite){
		super(text);
		this.viewer = viewer; 
		this.viewSite = viewSite;
		
		showHistory = new AbstractAction(viewSite.getShell(), "Show History", new ShowHistoryAction());
		showInVersionTree = new AbstractAction(viewSite.getShell(), "Show Version Tree", new ShowVersionTreeAction());
		syncronize = new AbstractAction(viewSite.getShell(), "Syncronize", new SyncronizeAction());
		update = new AbstractAction(viewSite.getShell(), "Update", new UpdateAction());
		commit = new AbstractAction(viewSite.getShell(), "Commit", new CommitAction());
		
	}
	
	public void fillTeamActions(){
		boolean singleFileSelected = TreeViewerUtility.isSingleFileSelected(viewer);
		showHistory.setEnabled(singleFileSelected);
		showInVersionTree.setEnabled(singleFileSelected);
		
		boolean isAnySelected = TreeViewerUtility.isAnySelected(viewer);
		syncronize.setEnabled(isAnySelected);
		update.setEnabled(isAnySelected);
		commit.setEnabled(isAnySelected);
		
		this.add(showHistory);
		this.add(showInVersionTree);
		this.add(new Separator());
		this.add(syncronize);
		this.add(update);
		this.add(commit);
	}
	
	private class ShowHistoryAction implements IAction{
		public void run() throws Exception{
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			if(selection!=null && selection.size()==1){
				Object node = selection.iterator().next();
				if(node instanceof IResourceEditionNode){
					TeamUI.showHistoryFor(TeamUIPlugin.getActivePage(), ((IResourceEditionNode)node).getResourceEditionNode().getRemoteResource(), null);
				}
			}
		}
	}
	
	private class ShowVersionTreeAction implements IAction{
		public void run() throws Exception{
				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				if(selection!=null && selection.size()==1){
					Object node = selection.iterator().next();
					if(node instanceof IResourceEditionNode){
						VersionTreeView view =(VersionTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VersionTreeView.VIEW_ID);
						ICVSRemoteFile resource = (ICVSRemoteFile)((IResourceEditionNode)node).getResourceEditionNode().getRemoteResource();
						if(resource==null){
							throw new Exception ("Resource not Found: "+node);
						}
						view.showVersionTree(resource);
					}
				}
		}
	}
	
	private class SyncronizeAction implements IAction{
		public void run() throws Exception{
			IResource[] resources = null;//FileUtility.getAllSelectedIResources(viewer);
			// First check if there is an existing matching participant
			WorkspaceSynchronizeParticipant participant = (WorkspaceSynchronizeParticipant)SubscriberParticipant.getMatchingParticipant(WorkspaceSynchronizeParticipant.ID, resources);
			// If there isn't, create one and add to the manager
			if (participant == null) {
                participant = new WorkspaceSynchronizeParticipant(new ResourceScope(resources));
				TeamUI.getSynchronizeManager().addSynchronizeParticipants(new ISynchronizeParticipant[] {participant});
			}
			participant.refresh(resources, viewSite);
		}
	}
	
	private class UpdateAction implements IAction{
		public void run() throws Exception{
			IResource[] resources =null;//FileUtility.getAllSelectedIResources(viewer);
			final UpdateOperation op = new UpdateOperation(viewSite.getPart(), Utils.getResourceMappings(resources), Command.NO_LOCAL_OPTIONS, null /* no tag */);
			/*op.done(new JobChangeEvent(){
				public IStatus getResult(){
					MantisLogger.debug("Files Updated Successfully");
					for(TreeFile file:FileUtility.getAllSelectedFiles(viewer)){
						try {
							file.refreshNode();
						} catch (TeamException e) {
							MantisLogger.error(e, "Error refreshing node: "+file);
						}
					}
					viewer.refresh();
					return Status.OK_STATUS;
				}
			});*/
			op.run();
		}
	}
	
	private class CommitAction implements IAction{
		public void run() {
			
		}
	}
}
