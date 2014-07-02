package cvshistorytodbplugin.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;

public class FileHistoryUtility {

	public static IFileHistory getFileHistory(IFile file){
		RepositoryProvider rep = RepositoryProvider.getProvider(file.getProject());
		return rep.getFileHistoryProvider().getFileHistoryFor(file, IFileHistoryProvider.NONE, new NullProgressMonitor());
	}
	
	public static IFileRevision[] getFileRevisions(IFile file){
		return getFileRevisions(file, getFileHistory(file));
	}
	public static IFileRevision[] getFileRevisions(IFile file, IFileHistory fileHistory){
		return fileHistory.getFileRevisions(); 
	}
	
	public static IFileRevision getFileRevision(IFile file, String version){
		RepositoryProvider rep = RepositoryProvider.getProvider(file.getProject());
		IFileHistory fileHist = rep.getFileHistoryProvider().getFileHistoryFor(file, IFileHistoryProvider.NONE, new NullProgressMonitor());
		return getFileRevision(version, fileHist);
	}
	
	public static IFileRevision getFileRevision(String version, IFileHistory fileHistory){
		return fileHistory.getFileRevision(version);
	}
	
	public static boolean isHead(IFile file, String version){
		if(version.equals("1.1")){			//CVS
			return true;
		}else{
			IFileRevision revCommit = getFileRevision(file, version);
			if(revCommit instanceof RevCommit){
				if(((RevCommit)revCommit).getParents()!=null && ((RevCommit)revCommit).getParents().length>0){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;
	}
}
