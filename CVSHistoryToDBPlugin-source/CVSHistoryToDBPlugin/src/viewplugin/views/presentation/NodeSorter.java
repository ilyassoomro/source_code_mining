package viewplugin.views.presentation;

import org.eclipse.jface.viewers.ViewerSorter;

import viewplugin.views.model.ProjectFolder;
import viewplugin.views.model.TreeFile;
import viewplugin.views.model.TreeFolder;

public class NodeSorter extends ViewerSorter {

	public NodeSorter(){
		super();
	}
	
	public int category(Object obj) {
		if(obj!=null){
			if(obj instanceof ProjectFolder){
				return 0;
			}
			else if(obj instanceof TreeFolder){
				return 1;
			}
			else if(obj instanceof TreeFile){  
				return 2;
			}
			
		}
		return 3;
    }
}
