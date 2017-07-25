package arajmony.wiki.util;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

public class FileUtil {
	
	
	
	/**
	 * 
	 * @param resourceFileRelPath
	 * @return
	 */
	public static InputStream getResourceFileStream(String resourceFileRelPath){
		ClassLoader classLoader = (new FileUtil()).getClass().getClassLoader();
		return classLoader.getResourceAsStream(resourceFileRelPath);
	}
	
	
	/**
	 * 
	 * @param resourceFileRelPath
	 * @return
	 */
	public static File getResourceAsFile(String resourceFileRelPath){
		
		ClassLoader classLoader = (new FileUtil()).getClass().getClassLoader();
		File f=null;
		try {
			f = new File(classLoader.getResource(resourceFileRelPath).toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return f;
		
	}
	
	
	
}
