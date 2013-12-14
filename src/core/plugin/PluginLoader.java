package core.plugin;

import java.io.File;

/**
 * This is the method for loading each plugin into the program, it was based of
 * something i found on Google then heavily rewritten for the purpose we need it
 * for.
 * 
 * @author Tom Rosier(XeTK)
 */
public class PluginLoader extends ClassLoader {

	/**
	 * This converts a .class file to a loadable class and loads the object into
	 * the program for it to be used at will.
	 * 
	 * @param filename the location of the plugin we want to load.
	 * 
	 * @return's the object that we would like to work on so a instance of the plugin.
	 * @throws Exception as this is a complicated process it may throw any exception.
	 */
	public Object loadClassObj(File file) throws Exception {
		// Get the full location of the file we are working on.
		String fileName = file.getAbsolutePath();

		// If the file doesn't have .class as a ending then we discard is straight away.
		if (fileName.endsWith(".class")) {

			// Remove the extension from the file path.
			fileName = fileName.substring(0, fileName.indexOf('.'));

			// Get root path of the application, so we can get the classpath for are plugin.
			String programRoot = PluginLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();

			// Remove non essential part of the path.
			String className = fileName.replace(programRoot, "");

			// Replace the '/' left in the classname with have with '.' so that java will load the class.
			className = className.replace('/', '.');
			
			Class<?> klass = loadClass(className);
			
			if (klass.isAnnotationPresent(IsPlugin.class)) {
				IsPlugin annotation = klass.getAnnotation(IsPlugin.class);
				if(annotation.autoload()) {
					return klass.newInstance(); 
				}
			}
		}
		// And if we can't load we return null so there is a status to say if it hasn't loaded.
		return null;
	}
}
