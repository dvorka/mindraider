/*
 ===========================================================================
   Copyright 2002-2010 Martin Dvorak

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.log4j.Logger;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class);

	public static String inputStreamToString (InputStream in) {
	    StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    try {
	        for (int n; (n = in.read(b)) != -1;) {
	            out.append(new String(b, 0, n));
	        }	        
	    } catch(Exception e) {
	        logger.error(e.getMessage(),e);
	    } finally {
	        if(in!=null) {
	            try {
	                in.close();
	            } catch(Exception e) {
	                logger.error(e.getMessage(),e);
	            }
	        }
	    }
	    return out.toString();
	}	

	 /**
	 * Renew files - delete old ones and create empty new one.
	 * 
	 * @return the new File
	 */
	public static File renewFile(String file) {
		File result = new File(file);
		if (result.exists()) {
			result.delete();
		}
		try {
			result.createNewFile();
		} catch (IOException e) {
			logger.error("renewFile() - Unable to create file: " + file, e); // {{debug}}
		}

		return result;
	}

	/**
	 * Create directory.
	 * 
	 * @param path
	 *            the directory path to create
	 */
	public static void createDirectory(String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * Copy a file.
	 * 
	 * @param fromFile
	 *            the source file
	 * @param toFile
	 *            the destination file
	 * @throws Exception
	 *             a generic exception
	 */
	public static void copyFile(String fromFile, String toFile)
			throws Exception {
		File from = new File(fromFile);
		File to = new File(toFile);
		to.createNewFile();
		FileInputStream fileInputStream = new FileInputStream(from);
		BufferedInputStream in = new BufferedInputStream(fileInputStream);

		FileOutputStream fileOutputStream = new FileOutputStream(to);
		BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);

		byte[] buffer = new byte[1024];
		int i;
		while (in.available() > 0) {
			i = in.read(buffer);
			out.write(buffer, 0, i);
		}
		out.flush();
		in.close();
		out.close();
	}

	/**
	 * Normalize path.
	 * 
	 * @param path
	 *            the path to normalize
	 * @return the normalized path
	 */
	public static String normalizePath(String path) {
		if (path != null && path.length() > 0) {
			if (System.getProperty("os.name").toLowerCase().startsWith(
					"windows")) {
				return path.replace('/', '\\');
			}
			return path.replace('\\', '/');
		}
		return path;
	}

	/**
	 * Normalizes a string to NCName.
	 * 
	 * @param name
	 *            the NCName
	 * @param quoteChar
	 *            the quote character
	 * @return the normalized string
	 */
	public static String toNcName(String name, char quoteChar) {
		StringBuffer result = new StringBuffer("");
		if (name != null && name.length() > 0) {
			if (!Character.isLetter(name.charAt(0)) || !(name.charAt(0) < 128)) {
				name = "Resource" + name;
			}
			// now replace all non letter/characters with _
			for (int i = 0; i < name.length(); i++) {
				// due to internationalization, only characters with code < 128
				// are accepted
				if (Character.isLetterOrDigit(name.charAt(i))
						&& name.charAt(i) < 128) {
					result.append(name.charAt(i));
				} else {
					result.append(quoteChar);
				}

			}
		}
		return result.toString();
	}

	/**
	 * Normalizes a string to NCName.
	 * 
	 * @param name
	 *            the name string
	 * @return the NCName converted string
	 */
	public static String toNcName(String name) {
		return toNcName(name, '_');
	}

	/**
	 * Get NCName localname from URI (hack).
	 * 
	 * @param uri
	 *            the uri
	 * @return the NCName converted string
	 */
	public static String getNcNameFromUri(String uri) {
		if (uri == null) {
			return null;
		}
		return uri.substring(uri.indexOf('#') + 1, uri.length());
	}

	/**
	 * Get hostname string.
	 * 
	 * @return the hostname
	 */
	public static String getHostname() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (Exception e) {
			logger.error("getHostname()", e);
			return "localhost";
		}
	}

	/**
	 * Quote name to make it NcName.
	 * 
	 * @param ncName
	 *            the NCName
	 * @return the quoted NCName
	 */
	public static String quoteNcName(String ncName) {
		String result = null;

		// TODO to be implemented
		// replace except bukva and start with a letter (if it is not there) -
		// at the end

		return result;
	}

	/**
	 * Process only files under given directory.
	 * 
	 * @param dir
	 *            the directory from which delete
	 * @throws Exception
	 *             a generic exception
	 */
	public static void deleteSubtree(File dir) throws Exception {
		String fromFile = dir.getPath();
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteSubtree(new File(dir, children[i]));
			}
			logger.debug("Dir: " + fromFile);
			if (dir.delete()) {
				logger.debug(" Deleted!");
			} else {
				logger.debug(" Unable to delete!");
			}
		} else {
			logger.debug("File: " + fromFile);
			if (dir.delete()) {
				logger.debug(" Deleted!");
			} else {
				logger.debug(" Unable to delete!");
			}
		}
	}
	
    public static String getCurrentDataTimeAsPrettyString() {
        Calendar cal = new GregorianCalendar();
        
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(
                "%1$4d-%2$d-%3$d-%4$d.%5$02d.%6$02d",
                cal.get(Calendar.YEAR),
                (cal.get(Calendar.MONTH)+1),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND)
                );
        return sb.toString();
    }

}
