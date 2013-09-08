package diversim.util.config;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Class for handling configuration files. Extends the functionality of Properties by handling
 * files, system resources and command lines.
 */
public class ConfigProperties extends Properties {


private static final long serialVersionUID = 1L;


/**
 * Calls super constructor.
 */
public ConfigProperties() {
  super();
}


/**
 * Constructs a ConfigProperty object from a parameter list. The algorithm is as follows: first
 * <code>resource</code> is used to attempt loading default values from the give system resource.
 * Then all Strings in <code>pars</code> are processed in the order they appear in the array. For
 * <code>pars[i]</code>, first a property file with the name <code>pars[i]</code> is attempted to be
 * loaded. If the file does not exist or loading produces any other IOException,
 * <code>pars[i]</code> is interpreted as a property definition, and it is set.
 * <p>
 * A little inconvenience is that if pars[i] is supposed to be a command line argument, but it is a
 * valid filename at the same time by accident. The caller must take care of that.
 * <p>
 * No exceptions are thrown, instead error messages are written to the standard error. Users who
 * want a finer control should use the public methods of this class.
 *
 * @param pars
 *          The (probably command line) parameter list.
 * @param resource
 *          The name of the system resource that contains the defaults. null if there isn't any.
 */
public ConfigProperties(String[] pars, String resource) {

  try {
    if (resource != null) {
      loadSystemResource(resource);
      System.err.println("ConfigProperties: System resource " + resource + " loaded.");
    }
  }
  catch (Exception e) {
    System.err.println("ConfigProperties: " + e);
  }

  if (pars == null || pars.length == 0) return;

  for (int i = 0; i < pars.length; i++) {
    try {
      load(pars[i]);
      System.err.println("ConfigProperties: File " + pars[i] + " loaded.");
      pars[i] = "";
    }
    catch (IOException e) {
      try {
        loadPropertyString(pars[i]);
        System.err.println("ConfigProperties: Property '" + pars[i] + "' set.");
      }
      catch (Exception e2) {
        System.err.println("ConfigProperties: " + e2);
      }
    }
    catch (Exception e) {
      System.err.println("ConfigProperties: " + e);
    }

  }
}


/**
 * Constructs a ConfigProperty object by loading a file by calling {@link #load}.
 *
 * @param fileName
 *          The name of the configuration file.
 */
public ConfigProperties(String fileName) throws IOException {

  load(fileName);
}


/**
 * Calls super constructor.
 */
public ConfigProperties(Properties props) {

  super(props);
}


/**
 * Calls {@link #ConfigProperties(String[],String)} with resource set to null.
 */
public ConfigProperties(String[] pars) {

  this(pars, null);
}


/**
 * Loads given file. Calls <code>Properties.load</code> with a file input stream to the given file.
 */
public void load(String fileName) throws IOException {

  FileInputStream fis = new FileInputStream(fileName);
  load(fis);
  fis.close();
}


/**
 * Adds the properties from the given property file. Searches in the class path for the file with
 * the given name.
 */
public void loadSystemResource(String n) throws IOException {

  ClassLoader cl = getClass().getClassLoader();
  load(cl.getResourceAsStream(n));
}


/**
 * Appends a property defined in the given string. The string is considered as a property file line.
 * It is converted to a byte array according to the default character encoding and then loaded by
 * the <code>Properties.load</code> method. This means that the ISO-8859-1 (or compatible) encoding
 * is assumed.
 */
public void loadPropertyString(String prop) throws IOException {

  StringBuffer sb = new StringBuffer();
  sb.append(prop).append("\n");
  load(new ByteArrayInputStream(sb.toString().getBytes()));
}

}
