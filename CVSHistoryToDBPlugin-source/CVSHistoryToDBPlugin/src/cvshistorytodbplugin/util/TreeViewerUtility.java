package cvshistorytodbplugin.util;


import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import cvshistorytodbplugin.model.IResourceEditionNode;


public class TreeViewerUtility {

	public static boolean isAnySelected(TreeViewer viewer){
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		return selection!=null && selection.size()>0;
	}
	
	public static boolean isAnyFileSelected(TreeViewer viewer){
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(selection!=null && selection.size()>0){
			Iterator it = selection.iterator();
			while(it.hasNext()){
				Object node = it.next();
				if(node instanceof IResourceEditionNode ){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean onlyFilesSelected(TreeViewer viewer){
		if(isAnySelected(viewer)){
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			Iterator it = selection.iterator();
			while(it.hasNext()){
				Object node = it.next();
				if(!(node instanceof IResourceEditionNode)){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean isSingleFileSelected(TreeViewer viewer){
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(selection!=null && selection.size()==1){
			Object node = selection.iterator().next();
			if(node instanceof IResourceEditionNode){
				return true;
			}
		}
		return false;
	}
	
	public static int getSelectionCount(TreeViewer viewer){
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		return selection!=null ? selection.size() : 0;
	}
	
	
}
