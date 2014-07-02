package cvshistorytodbplugin.util;


import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cvshistorytodbplugin.model.ProjectFolder;


public class InputDialogUtility {

	
	public static String inputString(Shell shell, String title, String message, String initial, boolean acceptEmpty, String emptyValueMessage ){
		String inputStr = null; //"temp52_eve2";
		
		while(true){
			InputDialog input = new InputDialog(shell, title, message, initial, null);
			input.setBlockOnOpen(true);
			input.open();
			if(InputDialog.CANCEL == input.getReturnCode()){
				return null;
			}
			
			if(!acceptEmpty && Utility.isEmpty(input.getValue())){
				MessageDialog.openInformation(shell,"Error",
						emptyValueMessage);
				continue;
			}else{
				inputStr = input.getValue();
				break;
			}
		}
		return inputStr;
	}
	
	public static String getDBConfiguration(ProjectFolder projectFolder){
		return getDBConfiguration(projectFolder, false);
	}
	public static String getDBConfiguration(ProjectFolder projectFolder, boolean doNotPromptIfSet){
		String dbConfig = projectFolder.getDbConfig();
		
		if(Utility.isEmpty(dbConfig)){
			dbConfig = "jdbc:mysql://localhost:3309/dbname?user=root&password=test";
		}else if(doNotPromptIfSet){
			return dbConfig; 
		}
		final String projectName = projectFolder.getProject().getName();
		final String dbConfigFinal = dbConfig;
		final StringBuilder dbConfigBuilder = new StringBuilder();
		Display.getDefault().syncExec( new Runnable() {  
					public void run() {
						dbConfigBuilder.append(InputDialogUtility.inputString(null, "DB Configuration: "+projectName, "Database URL (i.e: jdbc:mysql://localhost:3309/dbname?user=root&password=test", dbConfigFinal, true, "Invalid database configuration"));
					} });
		
		if(!Utility.isEmpty(dbConfigBuilder.toString())){
			projectFolder.setDbConfig(dbConfigBuilder.toString());
		}
		return dbConfigBuilder.toString();
	}
}
