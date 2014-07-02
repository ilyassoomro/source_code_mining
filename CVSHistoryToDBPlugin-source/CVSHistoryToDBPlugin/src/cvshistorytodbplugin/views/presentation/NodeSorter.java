package cvshistorytodbplugin.views.presentation;


import org.eclipse.jface.viewers.ViewerSorter;

import cvshistorytodbplugin.model.ProjectFolder;
import cvshistorytodbplugin.model.TreeFile;
import cvshistorytodbplugin.model.TreeFolder;


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
