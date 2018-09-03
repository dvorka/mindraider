/*
 ===========================================================================
   Copyright 2002-2018 Martin Dvorak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ===========================================================================
*/
package com.mindcognition.mindraider.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.mindcognition.mindraider.ui.swing.main.StatusBar;

public class Zipper {
	private static final Logger logger = Logger.getLogger(Zipper.class);

	/**
	 * Zip directory to archive.
	 * 
	 * @param zipFile
	 *            the zip file
	 * @param zippedDir
	 *            the zipped directory
	 * @param progress
	 *            the progress dialog JFrame
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             the generic I/O exception
	 */
	public static void zip(String zipFile, String zippedDir) throws FileNotFoundException,
			IOException {
		if (zipFile != null && zippedDir != null) {
			ZipOutputStream zipOutputStream = null;
			try {
				File file = new File(zippedDir);
				zipOutputStream = new ZipOutputStream(new FileOutputStream(
						zipFile));
				zipDirectory(zipOutputStream, zippedDir, file.getName());
			} catch (Exception e) {
				logger.error("Unable to zip directory!", e);
			} finally {
				if (zipOutputStream != null) {
					zipOutputStream.flush();
					zipOutputStream.close();
				}
			}
		}
	}
	
	/**
	 * Zip directory.
	 * 
	 * @param zipOutputStream
	 *            the zip output stream
	 * @param zippedDir
	 *            the zipped directory
	 * @param relativeDir
	 *            the relative directory
	 * @param progress
	 *            the progress dialog JFrame
	 */
	public static void zipDirectory(ZipOutputStream zipOutputStream,
			String zippedDir, String relativeDir) {
        StatusBar.setText(" Zipping: ",relativeDir,70);
		File zipDir = new File(zippedDir);
		// it is always directory
		String[] dirList = zipDir.list();

		if (dirList.length == 0) {
			// create also empty directories
			String entry = (relativeDir == null ? zippedDir : relativeDir
					+ File.separator);
			ZipEntry ze = new ZipEntry(entry);
			try {
				zipOutputStream.putNextEntry(ze);
			} catch (IOException e) {
				logger.debug("Unable to zip empty directory " + entry, e);
			}
		} else {
			for (int i = 0; i < dirList.length; i++) {
				File file = new File(zippedDir, dirList[i]);

				if (file.isDirectory()) {
					zipDirectory(zipOutputStream, file.getPath(),
							(relativeDir == null ? dirList[i] : relativeDir
									+ File.separator + dirList[i]));
				} else {
					FileInputStream fis = null;
					try {
						byte[] readBuffer = new byte[2048];
						int bytesReaded = 0;

						String fileToZip = file.getPath();
						String entry = (relativeDir == null ? dirList[i]
								: relativeDir + File.separator + dirList[i]);

				        StatusBar.setText(" Adding file: ",entry,70);

						fis = new FileInputStream(fileToZip);
						ZipEntry ze = new ZipEntry(entry);
						zipOutputStream.putNextEntry(ze);

						while ((bytesReaded = fis.read(readBuffer)) != -1) {
							zipOutputStream.write(readBuffer, 0, bytesReaded);
						}
					} catch (Exception e) {
						logger.debug("Unable to zip: " + e.getMessage(), e);
					} finally {
						if (fis != null) {
							try {
								fis.close();
							} catch (Exception ee) {
								logger.debug("Unable to close stream!", ee);
							}
						}
					}
				}
			}
		}
	}
}