package cvshistorytodbplugin.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.internal.ccvs.core.CVSMessages;
import org.eclipse.team.internal.ccvs.core.CVSStatus;
import org.eclipse.team.internal.ccvs.core.ICVSFolder;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteResource;
import org.eclipse.team.internal.ccvs.core.ICVSRepositoryLocation;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.Policy;
import org.eclipse.team.internal.ccvs.core.client.Command;
import org.eclipse.team.internal.ccvs.core.client.Command.LocalOption;
import org.eclipse.team.internal.ccvs.core.client.listeners.ICommandOutputListener;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileHistory;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;

import cvshistorytodbplugin.model.db.DBFileRevision;
import cvshistorytodbplugin.util.CVSUtility;
import cvshistorytodbplugin.util.Logger;

public class FileRevisionDeltaProvider {

	public static String getDeltaFromRepository(File file, String revision,IFileHistory fileHistory, IProgressMonitor monitor){
		if(fileHistory instanceof CVSFileHistory){
			return getDeltaFromCVS(file, revision, fileHistory, monitor);
		}else{					// FIXME Check if git repository
			return getDeltaFromGit(file, revision, fileHistory, monitor);
		}
	}
	
	private static String getDeltaFromCVS(File file, String revision,IFileHistory fileHistory, IProgressMonitor monitor){
		String version1 = revision;
		String version2 = CVSUtility.getPreviousVersion(version1, fileHistory);
		if(version2!=null){			// FIXME It does not return delta of first version
			org.eclipse.team.internal.ccvs.core.client.Session session = null;
			try {
				EclipseFile eclipseFile = (EclipseFile)CVSWorkspaceRoot.getCVSFileFor(file);
				ICVSRemoteResource remoteFolder = CVSWorkspaceRoot.getRemoteResourceFor(eclipseFile.getParent());
				
				session = 
					new org.eclipse.team.internal.ccvs.core.client.Session(remoteFolder.getRepository(), eclipseFile.getParent(), false /* create backups */);
				
				session.open(new NullProgressMonitor(), false /* read-only */);
			
				Logger.debug("\n\n\nCheck Difference between: "+version1 +"   and   "+version2+"\n");
				DiffOutputListener diffListener = new DiffOutputListener();
				IStatus status = Command.DIFF.execute(
					session,
					Command.NO_GLOBAL_OPTIONS,
					new LocalOption[] {
							 Command.DIFF.makeTagOption(new org.eclipse.team.internal.ccvs.core.CVSTag(version2, org.eclipse.team.internal.ccvs.core.CVSTag.VERSION)),
							 Command.DIFF.makeTagOption(new org.eclipse.team.internal.ccvs.core.CVSTag(version1, org.eclipse.team.internal.ccvs.core.CVSTag.VERSION))
					},
					new ICVSResource[] { eclipseFile },
					diffListener,
					new NullProgressMonitor());
					if(!diffListener.anyError){
						String delta = diffListener.buffer.toString();
						Logger.debug(delta);
						return delta;
					}
			}catch(Exception e){
				Logger.error(e,"Error while checking Diff: "+e.getMessage());
			}
			finally {
				try{
					session.close();
				}catch(Exception e){
					Logger.error(e,"Error while Closing Session: "+e.getMessage());
				}
				//monitor.done();
			}
		}
		return null;
	}
	
	public static class DiffOutputListener implements ICommandOutputListener{

		StringBuffer buffer = new StringBuffer();
		boolean anyError = false;
		@Override
		public IStatus messageLine(String line,
				ICVSRepositoryLocation location, ICVSFolder commandRoot,
				IProgressMonitor monitor) {
			buffer.append(line+"\n");
			return new CVSStatus(IStatus.OK, CVSMessages.ok);
		}

		@Override
		public IStatus errorLine(String line, ICVSRepositoryLocation location,
				ICVSFolder commandRoot, IProgressMonitor monitor) {
			Logger.error(line);
			anyError = true;
			return new CVSStatus(IStatus.OK, CVSMessages.ok);
		}
	}
	
	private static String getDeltaFromGit(File file, String revision,IFileHistory fileHistory, IProgressMonitor monitor){
		
		try {
			
			RepositoryMapping mapping = RepositoryMapping.getMapping(file);
			Repository rep = mapping.getRepository();
			String gitPath = mapping.getRepoRelativePath(file);
			ObjectReader reader =  rep.newObjectReader();
			
			RevWalk rw = new RevWalk(rep);
			RevCommit revCommitAfter = rw.parseCommit(rep.resolve(revision));
			//RevCommit revCommitBefore = rw.parseCommit(revCommitAfter.getParents()[0].getId());
			
			Git git = new Git(rep);
			OutputStream out = new ByteArrayOutputStream();
			DiffCommand diffCommand = git.diff();
			diffCommand.setOutputStream(out);
			diffCommand.setPathFilter(PathFilter.create(gitPath));
			
			CanonicalTreeParser rev1 = new CanonicalTreeParser();
			rev1.reset(reader, rw.parseTree(rep.resolve(revision)));
			diffCommand.setNewTree(rev1);
			  
			if(revCommitAfter.getParents()!=null && revCommitAfter.getParents().length>0){
				CanonicalTreeParser rev2 = new CanonicalTreeParser();
				rev2.reset(reader,  rw.parseTree(revCommitAfter.getParents()[0].getId()));
				diffCommand.setOldTree(rev2);
			}else{
				diffCommand.setOldTree(new EmptyTreeIterator());
			}
			
			diffCommand.call();
			String delta = out.toString();
			
			return delta;
		}catch(Exception e){
			Logger.error(e,"Error while checking Diff: "+e.getMessage());
		}
		return null;
	}
}
