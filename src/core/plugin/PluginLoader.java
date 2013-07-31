package core.plugin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

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
	 * @param filename
	 *            the location of the plugin we want to load.
	 * @return's the object that we would like to work on so a instance of the
	 *           plugin.
	 * @throws Exception
	 *             as this is a complicated process it may throw any exception.
	 */
	public Object loadClassObj(File file, Class<?> classdef) throws Exception {
		// Get the full location of the file we are working on.
		String fileName = file.getAbsolutePath();

		// If the file doesn't have .class as a ending then we discard is
		// straight away.
		if (fileName.endsWith(".class")) {
			// Remove the extension from the file path.
			fileName = fileName.substring(0, fileName.indexOf('.'));

			// Get root path of the application, so we can get the classpath for
			// are plugin.
			String programRoot = PluginLoader.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();

			// Remove non essential part of the path.
			String className = fileName.replace(programRoot, "");

			// Replace the '/' left in the classname with have with '.' so that
			// java will load the class.
			className = className.replace('/', '.');

			// Check if the class has already been loaded if it has then we
			// don't need todo anything.
			Class<?> c = findLoadedClass(className);

			// So if it hasn't been loaded then we convert the file into bytes
			// and load it.
			if (c == null) {

				// Convert the class from bytes into a object

				int length = (int) file.length();
				byte[] classbytes = new byte[length];
				DataInputStream in = new DataInputStream(new FileInputStream(
						file));
				in.readFully(classbytes);
				in.close();

				// Define them bytes into something we can actually use.
				c = defineClass(className, classbytes, 0, length);
			}

			// Resolve the class, don't know why doesn't work without it
			resolveClass(c);
			// Check the class has loaded correctly and has the correct number
			// of methods.
			if (c.getInterfaces().length != 0 || c.getMethods().length != 0)
				try {
					return c.newInstance();
				} catch (final java.lang.InstantiationException e) {
					System.err.printf("Unable to load pluging in %s. (Exception message: %s)%n", c.getName(), e.getLocalizedMessage());
				}
		}
		// And if we can't load we return null so there is a status to say if it
		// hasn't loaded.
		return null;
	}
}
