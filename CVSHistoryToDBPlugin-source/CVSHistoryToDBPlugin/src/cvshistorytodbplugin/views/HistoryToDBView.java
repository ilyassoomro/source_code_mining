package cvshistorytodbplugin.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import viewplugin.util.TreeViewerUtility;
import viewplugin.views.actions.DBSettingAction;
import viewplugin.views.actions.FileHistoryLogAction;
import viewplugin.views.actions.FileHistorySaveAction;
import viewplugin.views.actions.FileOpenAction;
import viewplugin.views.actions.MantisViewKeyListener;
import viewplugin.views.actions.ProjectOpenAction;
import viewplugin.views.presentation.NodeContentHandler;
import viewplugin.views.presentation.NodeContentProvider;
import viewplugin.views.presentation.NodeLabelProvider;
import viewplugin.views.presentation.NodeSorter;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class HistoryToDBView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "cvshistorytodbplugin.views.HistoryToDBView";

	private TreeViewer viewer;

	private NodeContentProvider treeContentProvider;
	private NodeContentHandler treeContentHanlder;
	private Action refreshAction;
	private Action fileOpenAction;
	private Action fileHistoryLogAction;
	private Action fileHistorySaveAction;
	private Action dbSettingAction;
	
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {  
		plugin = this;
		initTool(); 
				
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeContentProvider = new NodeContentProvider(getViewSite(), viewer);
		viewer.setContentProvider(treeContentProvider);
		viewer.setLabelProvider(new NodeLabelProvider());
		viewer.setSorter(new NodeSorter());
		viewer.setInput(getViewSite());
		
	
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "ViewPlugin.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				HistoryToDBView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		viewer.getControl().addKeyListener(new MantisViewKeyListener(viewer));
		
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(dbSettingAction );
	}

	private void fillContextMenu(IMenuManager manager) {
		
		boolean isAnySelected = TreeViewerUtility.isAnySelected(viewer);
		boolean onlyFilesSelected = TreeViewerUtility.onlyFilesSelected(viewer);
		boolean isAnyFileSelected = TreeViewerUtility.isAnyFileSelected(viewer);
		int selectionCount = TreeViewerUtility.getSelectionCount(viewer);
		
		fileOpenAction.setEnabled(isAnyFileSelected);
		
		manager.add(new Separator());
		
		manager.add(new Separator());
		
		manager.add(fileOpenAction);
		manager.add(fileHistoryLogAction);
		manager.add(fileHistorySaveAction);
		manager.add(new Separator());
		manager.add(dbSettingAction);
		dbSettingAction.setEnabled(selectionCount>0);
		
		manager.add(new Separator());
		
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		
		
	}
	
	// View Tool Bar (Beside Minimize and maximize icons)
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator());
	}

	private void makeActions() {
		fileOpenAction = new FileOpenAction(viewer, treeContentProvider,  getViewSite());
		new ProjectOpenAction(viewer, treeContentProvider, getViewSite());
		fileHistoryLogAction = new FileHistoryLogAction(viewer, treeContentProvider, getViewSite());
		fileHistorySaveAction = new FileHistorySaveAction(viewer, treeContentProvider, getViewSite());
		
		dbSettingAction = new DBSettingAction(viewer, treeContentProvider, getViewSite());
		
		treeContentHanlder = new NodeContentHandler(viewer,getViewSite());
		
		refreshAction = new Action("Refresh", CVSUIPlugin.getPlugin().getImageDescriptor(ICVSUIConstants.IMG_REFRESH)) {
			public void run(){
				viewer.refresh();
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				treeContentHanlder.handleDoubleClick(event);
			}
		});
	}
	public void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"NS QA Tool",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public static IWorkbenchPage getTargetPage(){
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return window.getActivePage();
	}
	
	private void initTool(){
		Shell shell = getViewSite().getShell();
	    Display display = shell.getDisplay();
	    shell.setSize(700, 600);
	    
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    shell.setLocation(x, y);
	    this.setPartName("History to Database");
	}
	
	private static HistoryToDBView plugin = null;
	public static HistoryToDBView getPlugin(){
		return plugin;
	}
}