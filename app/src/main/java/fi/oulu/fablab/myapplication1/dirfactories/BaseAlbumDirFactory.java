/* BaseAlbumDirFactory.java belongs to below Android example page
*  from Android Developer Platform.
*  Example page: https://developer.android.com/training/camera/photobasics.html
 */
package fi.oulu.fablab.myapplication1.dirfactories;

import java.io.File;

import android.os.Environment;

public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

	// Standard storage location for digital camera files
	private static final String CAMERA_DIR = "/dcim/";

	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File (
				Environment.getExternalStorageDirectory()
				+ CAMERA_DIR
				+ albumName
		);
	}
}
