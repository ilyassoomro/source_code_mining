package cvshistorytodbplugin.util;

import java.io.IOException;
import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class Logger {

	private static int ERROR = 1;
	private static int DEBUG = 0;
	private static MessageConsole console;
	private static MessageConsoleStream stream;
	private static MessageConsoleStream errStream;
	
	private static void print(int type, Object... messages ){
		if(console==null){
			prepareConsole();
		}
		if(messages!=null){
			if(type == ERROR){
				for(Object msg:messages){
					errStream.println("Error: "+new Date()+" : "+msg);
				}
				//showConsole();
			}else if(type == DEBUG){
				for(Object msg:messages){
					stream.println("Debug: "+new Date()+" : "+msg);
				}	
			}
		}
	}
	
	public static void showConsole(){
		if(console==null){
			prepareConsole();
		}
		console.activate();
	}
	
	public static void debug(Object... messages){
		print(DEBUG,messages);
	}
	public static void error(Object... messages){
		print(ERROR,messages);
	}
	public static void error(Throwable error,boolean showConsole, Object... messages){
		error(error, messages);
		if(showConsole){
			showConsole();
		}
	}
	public static void error(Throwable error, Object... messages){
		print(ERROR,messages);
		printStackTrace(error);
	}
	private static void printStackTrace(Throwable error){
		if(error!=null && error.getStackTrace()!=null){
			try {
				errStream.write(error+"\n");
				for(StackTraceElement t: error.getStackTrace()){
					errStream.write("\t"+t+"\n");
				}
				if(error.getCause()!=null){
					errStream.write("Caused by : ");
					printStackTrace(error.getCause());
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void prepareConsole(){
		console = findConsole("History TO DB");
		stream = console.newMessageStream();
		errStream = console.newMessageStream();
		errStream.setColor(new org.eclipse.swt.graphics.Color(Display.getCurrent(), 255,0,0));
			
	}
	
	private static MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	   }
}
