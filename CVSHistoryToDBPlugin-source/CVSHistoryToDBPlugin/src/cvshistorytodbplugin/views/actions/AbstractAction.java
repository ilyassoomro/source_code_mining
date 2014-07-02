package cvshistorytodbplugin.views.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import cvshistorytodbplugin.util.Logger;


public class AbstractAction extends Action {
	IAction action;
	Shell shell;
	AbstractAction(Shell shell, String text, IAction action){
		this.setText(text);
		this.setToolTipText(text);
		this.action = action;
		this.shell = shell;
	}
	public void run() {
		try {
			action.run();
		} catch (Exception e) {
			Logger.error(e,"Error while "+getText());
			MessageDialog.openInformation(shell,"Error", "Error while "+getText()+".\n"+e.getMessage());
		}
	}
}
