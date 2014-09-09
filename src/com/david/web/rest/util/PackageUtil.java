/**
 * 
 */
package com.david.web.rest.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author david
 */
public class PackageUtil {
	
	private static final Logger logger = Logger.getLogger("PackageUtil");
	
	/**
	 * scan package
	 * @param packageName
	 * @return
	 */
	public static List<Class> scanPackage(String packageName){
		return scanPackage(PackageUtil.class.getClassLoader(), packageName);
	}
	/**
	 * scan package
	 * @param classLoader
	 * @param packageName
	 * @return classes
	 */
	public static List<Class> scanPackage(ClassLoader classLoader,String packageName){
		List<Class> classes = new ArrayList<Class>();
		try {
			Enumeration<URL> urls = classLoader.getResources(packageName.replaceAll("\\.", "/"));
			while(urls.hasMoreElements()){
				List<String> files = listFiles(new File(urls.nextElement().getFile()));
				for(String file : files){
					String className = String.format("%s.%s",packageName,file.substring(0, file.length()-6));
					//logger.info("scan class: "+className);
					try{
						classes.add(Class.forName(className));
					}catch(Exception ex){logger.severe(ex.getMessage());}
				}
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE,e.getMessage());
		}
		
		return classes;
	}
	
	/**
	 * 读取路径下的文件
	 * @param dir
	 * @return
	 */
	public static List<String> listFiles(File dir){
		final List<String> files = new ArrayList<String>();
		dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if(f.isDirectory()){
					files.addAll(listFiles(f));
				}else if(f.isFile() && f.getName().endsWith(".class")){
					files.add(f.getName());
				}
				return false;
			}
		});
		
		return files;
	}
}
