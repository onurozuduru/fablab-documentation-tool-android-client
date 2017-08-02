/* AlbumStorageDirFactory.java belongs to below Android example page
*  from Android Developer Platform.
*  Example page: https://developer.android.com/training/camera/photobasics.html
 */
package fi.oulu.fablab.myapplication1.dirfactories;

import java.io.File;

public abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}
