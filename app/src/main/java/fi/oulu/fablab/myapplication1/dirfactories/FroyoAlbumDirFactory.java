/* FroyoAlbumDirFactory.java belongs to below Android example page
*  from Android Developer Platform.
*  Example page: https://developer.android.com/training/camera/photobasics.html
 */
package fi.oulu.fablab.myapplication1.dirfactories;

import java.io.File;

import android.os.Environment;

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

	@Override
	public File getAlbumStorageDir(String albumName) {
		// TODO Auto-generated method stub
		return new File(
		  Environment.getExternalStoragePublicDirectory(
		    Environment.DIRECTORY_PICTURES
		  ), 
		  albumName
		);
	}
}
