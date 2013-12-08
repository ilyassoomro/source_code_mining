package cvshistorytodbplugin.model;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class TreeNode implements IAdaptable, Cloneable, Comparable<TreeNode>, INodeImage {
	
	protected String name;
	protected TreeNode parent;
	protected ArrayList<TreeNode> children;
	
	public TreeNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public TreeNode getParent() {
		return parent;
	}
	
	public String toString() {
		return getName();
	}
	
	@SuppressWarnings("rawtypes") 
	public Object getAdapter(Class key) {
		return null;
	}

	public TreeNode clone(){
		TreeNode copy = new TreeNode(name);
		if(children!=null){
			for(TreeNode child: children){
				TreeNode copyChild =child.clone();
				copyChild.setParent(copy);
				copy.addChild(copyChild);
			}
		}
		return copy;
	}
	
	public int compareTo(TreeNode o){
		int result=1;
		if(o!=null){
			result = Integer.valueOf(this.getDepth()).compareTo(o.getDepth());
			if(result==0){
				result = name.compareTo(o.name);
			}
		}
		return result;
	}
	
	public void addChilds(ArrayList<? extends TreeNode> childs) {
		if(childs!=null){
			for(TreeNode child: childs){
				addChild(child);
			}
		}
	}
	public void addChild(TreeNode child) {
		if(child!=null){
			if(children==null){
				children = new ArrayList<TreeNode>();
			}
			children.add(child);
			child.setParent(this);
		}
	}
	public void removeChild(TreeNode child) {
		if(children!=null && child!=null){
			children.remove(child);
			child.setParent(null);
		}
	}
	public void removeAllChild() {
		if(children!=null){
			children.clear();
		}
	}
	
	public Object[] getChildren() {
		return children!=null ? children.toArray(new TreeNode[children.size()]) : null;
	}
	public boolean hasChildren() {
		return children!=null && children.size()>0;
	}
	
	public TreeNode getTop(){
		if(this.getParent()==null || this instanceof ProjectFolder){ // Most Top is project folder (or when no parent)
			return this;
		}else{
			return this.getParent().getTop();
		}
	}
	
	public ProjectFolder getFileProjectFolder(){
		TreeNode project = this.getTop();
		if(project instanceof ProjectFolder){
			return (ProjectFolder) project;
		}
		return null;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
	
	public int getDepth(){
		if(getParent()!=null){
			return getParent().getDepth() + 1;
		}
		return 0;
	}
}
