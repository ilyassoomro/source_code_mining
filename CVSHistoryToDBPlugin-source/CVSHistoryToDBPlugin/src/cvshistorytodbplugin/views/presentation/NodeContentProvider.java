package cvshistorytodbplugin.views.presentation;


import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

import cvshistorytodbplugin.model.ProjectFolder;
import cvshistorytodbplugin.model.TreeFolder;
import cvshistorytodbplugin.model.TreeNode;


@SuppressWarnings("rawtypes")
public class NodeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	private TreeNode invisibleRoot;
	private HashSet<ProjectFolder> projectFolders;
	private TreeViewer viewer;
	private IViewSite viewSite;
	
	
	public NodeContentProvider(IViewSite viewSite, TreeViewer viewer){
		this.viewSite = viewSite;
		this.viewer = viewer;
		projectFolders = new HashSet<ProjectFolder>();
		invisibleRoot = new TreeNode("");
		addProjects();
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		if (parent.equals(this.viewSite)) {
			if(invisibleRoot!=null){
				return getChildren(invisibleRoot);
			}
		}
		return getChildren(parent);
	}
	public Object getParent(Object child) {
		if (child instanceof TreeNode) {
			return ((TreeNode)child).getParent();
		}
		return null;
	}
	public Object [] getChildren(Object parent) {
		if (parent instanceof TreeNode) { 
			return ((TreeNode)parent).getChildren();
		}
		return new Object[0];
	}
	public boolean hasChildren(Object parent) {
		  
		if (parent instanceof TreeNode) 
			return ((TreeNode)parent).hasChildren();
		return false;
	}
	
	private void addProjects(){
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if(projects!=null && projects.length>0){
			for(IProject project:projects){
				ProjectFolder projFolder = new ProjectFolder(project);
				projectFolders.add(projFolder);
				invisibleRoot.addChild(projFolder);
			}
		}else{
			ProjectFolder projFolder = new ProjectFolder(null);
			projectFolders.add(projFolder);
			invisibleRoot.addChild(projFolder);
		}
	}
	
}
