package viewplugin.views.model;

import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;


/**
 * This interface is used to provide Resource Version Node for Compare Editor. <br/>
 * Any Resource which can be used for Left and Right side of compare editor, must implement this interface to provide left/right nodes.
 */
@SuppressWarnings("restriction")
public interface IRemoteVersionNode {
	/**
	 * Returns Resource Edition Node for either (left/right) side of Compare editor.
	 * @return
	 * @throws TeamException 
	 */
	ICVSRemoteFile getRemoteVersion() throws TeamException;
}
