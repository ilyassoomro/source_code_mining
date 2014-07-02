package cvshistorytodbplugin.views.actions;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class MantisViewKeyListener implements KeyListener{

	TreeViewer viewer;
	public MantisViewKeyListener(TreeViewer viewer){
		this.viewer = viewer;
	}
	
	boolean control=false;
	@Override
	public void keyPressed(KeyEvent key) {
		if (key.keyCode == SWT.CTRL) {
			control = true;
		}
		if (key.keyCode == ('c') && control) {
			
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		// TODO Auto-generated method stub
		control=false;
	}

}
