package cvshistorytodbplugin.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class ImageUtility {

	public static ImageDescriptor createImageFromFile(Class location, String fileName){
		return ImageDescriptor.createFromFile(location, "/icons/"+fileName);
	}
	
	public static String VERSION_TREE_IMAGE_NAME = "version_tree.gif";
	public static ImageDescriptor VERSION_TREE_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, VERSION_TREE_IMAGE_NAME);
	public static Image VERSION_TREE_IMAGE = VERSION_TREE_IMAGE_DESCRIPTOR.createImage();
	
	public static String INCOMING_UPDATE_IMAGE_NAME = "incoming_update.png";
	public static ImageDescriptor INCOMING_UPDATE_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, INCOMING_UPDATE_IMAGE_NAME);
	public static Image INCOMING_UPDATE_IMAGE = INCOMING_UPDATE_IMAGE_DESCRIPTOR.createImage();
	
	public static String INCOMING_NEW_FILE_IMAGE_NAME = "incoming_new_file.png";
	public static ImageDescriptor INCOMING_NEW_FILE_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, INCOMING_NEW_FILE_IMAGE_NAME);
	public static Image INCOMING_NEW_FILE_IMAGE = INCOMING_NEW_FILE_IMAGE_DESCRIPTOR.createImage();
	
	public static String REPLACE_IMAGE_NAME = "replace.png";
	public static ImageDescriptor REPLACE_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, REPLACE_IMAGE_NAME);
	public static Image REPLACE_IMAGE = REPLACE_IMAGE_DESCRIPTOR.createImage();
		
	public static String OUTGOING_IMAGE_NAME = "outgoing.png";
	public static ImageDescriptor OUTGOING_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, OUTGOING_IMAGE_NAME);
	public static Image OUTGOING_IMAGE = OUTGOING_IMAGE_DESCRIPTOR.createImage();
	
	public static String FILE_IMAGE_NAME = "file.png";
	public static ImageDescriptor FILE_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, FILE_IMAGE_NAME);
	public static Image FILE_IMAGE = FILE_IMAGE_DESCRIPTOR.createImage();
	
	public static String FILE_VERSION_IMAGE_NAME = "file_version.png";
	public static ImageDescriptor FILE_VERSION__IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, FILE_VERSION_IMAGE_NAME);
	public static Image FILE_VERSION_FILE_IMAGE = FILE_VERSION__IMAGE_DESCRIPTOR.createImage();
	
	public static String FOLDER_IMAGE_NAME = "folder.png";
	public static ImageDescriptor FOLDER_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, FILE_IMAGE_NAME);
	public static Image FOLDER_IMAGE = FOLDER_IMAGE_DESCRIPTOR.createImage();
	
	public static String PROJECT_IMAGE_NAME = "project.png";
	public static ImageDescriptor PROJECT_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, PROJECT_IMAGE_NAME);
	public static Image PROJECT_IMAGE = PROJECT_IMAGE_DESCRIPTOR.createImage();
	
	public static String PROJECT_CLOSED_IMAGE_NAME = "project_closed.gif";
	public static ImageDescriptor PROJECT_CLOSED_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, PROJECT_CLOSED_IMAGE_NAME);
	public static Image PROJECT_CLOSED_IMAGE = PROJECT_CLOSED_IMAGE_DESCRIPTOR.createImage();
	
	public static String MANTIS_IMAGE_NAME = "mantis.gif";
	public static ImageDescriptor MANTIS_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, MANTIS_IMAGE_NAME);
	public static Image MANTIS_IMAGE = MANTIS_IMAGE_DESCRIPTOR.createImage();
	
	public static String TICKET_VERSION_IMAGE_NAME = "ticket_version.gif";
	public static ImageDescriptor TICKET_VERSION_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, TICKET_VERSION_IMAGE_NAME);
	public static Image TICKET_VERSION_IMAGE = TICKET_VERSION_IMAGE_DESCRIPTOR.createImage();
	
	public static String LOCAL_VERSION_IMAGE_NAME = "local_version.gif";
	public static ImageDescriptor LOCAL_VERSION_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, LOCAL_VERSION_IMAGE_NAME);
	public static Image LOCAL_VERSION_IMAGE = LOCAL_VERSION_IMAGE_DESCRIPTOR.createImage();
	
	public static String DEPENDENCY_IMAGE_NAME = "dependency.gif";
	public static ImageDescriptor DEPENDENCY_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, DEPENDENCY_IMAGE_NAME);
	public static Image DEPENDENCY_IMAGE = DEPENDENCY_IMAGE_DESCRIPTOR.createImage();
	
	public static String DEPENDENCY_FILE_IMAGE_NAME = "dependent_file.png";
	public static ImageDescriptor DEPENDENCY_FILE_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, DEPENDENCY_FILE_IMAGE_NAME);
	public static Image DEPENDENCY_FILE_IMAGE = DEPENDENCY_FILE_IMAGE_DESCRIPTOR.createImage();
	
	public static String TAG_IMAGE_NAME = "tag.gif";
	public static ImageDescriptor TAG_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, TAG_IMAGE_NAME);
	public static Image TAG_IMAGE = TAG_IMAGE_DESCRIPTOR.createImage();
	
	public static String REFRESH_IMAGE_NAME = "refresh.gif";
	public static ImageDescriptor REFRESH_IMAGE_DESCRIPTOR = ImageUtility.createImageFromFile(ImageUtility.class, REFRESH_IMAGE_NAME);
	public static Image REFRESH_IMAGE = REFRESH_IMAGE_DESCRIPTOR.createImage();
	
	
	public static Image combineImage(Image img1, Image img2){
		
		//Image img = new Image(img1.getDevice(), data)
		return null;
	}
}
