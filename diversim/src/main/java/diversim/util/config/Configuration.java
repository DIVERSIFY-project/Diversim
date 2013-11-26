package diversim.util.config;


import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;


/**
 * Fully static class to store configuration information. It defines a method,
 * {@link #setConfig(Properties)}, to set configuration data.
 * All components can then access this configuration and utility methods to read property
 * values based on their names.
 * <p>
 * The design of this class also hides the actual implementation of the configuration which can be
 * Properties, XML, whatever. Currently only Properties is supported.
 * <p>
 * Apart from storing (name,value) pairs, this class also does some processing, and offers some
 * utility functions. This extended functionality consists of the following: reading values with
 * type checking, parsing expressions.
 * <p>
 * Note that the configuration is initialized using a Properties object. The class of this object
 * might implement some additional pre-processing on the file or provide an extended syntax for
 * defining property files. See {@link ParsedProperties} for more details.
 * <h3>Typed reading of values</h3> Properties can have arbitrary values of type String. This class
 * offers a set of read methods that perform the appropriate conversion of the string value to the
 * given type, eg long. They also allow for specifiying default values in case the given property is
 * not specified.
 *
 * <p>
 * Assuming the configuration is in Properties format (which is currently the only format available)
 * component types and names are defined as follows. Property names containing two non-empty words
 * separated by one dot (".") character are treated specially (the words contain word characters:
 * alphanumeric and underscore ("_")). The first word will be the type, and the second is the name
 * of a component. For example,
 *
 * <pre>
 *   control.conn ConnectivityObserver
 *   control.1 WireKOut
 *   control.2 PrintGraph
 * </pre>
 *
 * defines control components of names "conn","1" an "2" (arguments of the components not shown).
 * <p>
 * <h3>Expressions</h3> Numeric property values can be complex expressions, that are parsed using <a
 * href="http://www.singularsys.com/jep/">JEP</a>. For example,
 * 
 * <pre>
 *   MAG 2
 *   SIZE 2&circ;MAG
 * </pre>
 *
 * SIZE=4. You can also have complex expression trees like this:
 * 
 * <pre>
 *   A B+C
 *   B D+E
 *   C E+F
 *   D 1
 *   E F
 *   F 2
 * </pre>
 *
 * that results in A=7, B=3, C=4, D=1, E=2, F=2
 * <p>
 * Expressions like "sub-expression op sub-expression" are computed based on the type of the
 * sub-expressions. If both sub-expressions are integer, the computation is done using integer
 * arithmetics and the result is an integer. So, for example, 5/2 returns 2. If one of the
 * sub-expression is floating point, the computation is based on floating-point arithmetics (double
 * precision) and the result is a floating point value. So, for example, 5.0/2 returns 2.5.
 * <p>
 * Expressions are parsed recursively. Note that no optimization is done, so expression F is
 * evaluated three times here (due to the fact that appears twice in C and once in B). But since
 * properties are read just once at initialization, this is not a performance problem.
 * <p>
 * Finally, recursive definitions are not allowed (and without function definitions, they make no
 * sense). Since it is difficult to discover complex recursive chains, a simple trick is used: if
 * the depth of recursion is greater than a given threshold (currently 100)
 * an error message is printed. This avoids to fill the stack, that
 * results in an anonymous OutOfMemoryError.
 */
public class Configuration implements Serializable {


private static final long serialVersionUID = 1L;


/**
 * The properties object that stores all configuration information.
 */
private static ConfigContainer config = null;


/** to prevent construction */
private Configuration() {}


/**
 * Sets the system-wide configuration in Properties format. It can be called only once. After that
 * the configuration becomes unmodifiable (read only). If modification is attempted, a
 * RuntimeException is thrown and no change is made.
 *
 * @param p
 *          The Properties object containing configuration info
 * @throws IOException 
 */
public static void setConfig(String file) throws IOException {
  
//  if (config != null) {
//    throw new RuntimeException("Setting configuration was attempted twice.");
//  }
  //if (config == null)
    config = new ConfigContainer(new ParsedProperties(file));
}


/**
 * @return true if and only if name is a specified (exisitng) property.
 */
public static boolean contains(String name) {
  return config.contains(name);
}


/**
 * Reads given property. True is returned if the lowercase
 * value of the property is "true", otherwise false is returned.
 *
 * @param name
 *          Name of configuration property
 */
public static boolean getBoolean(String name) {
  return config.getBoolean(name);
}


/**
 * Reads given configuration property. If not found, returns the default value.
 *
 * @param name
 *          Name of configuration property
 * @param def
 *          default value
 */
public static int getInt(String name, int def) {
  int res = config.getInt(name);
  if (res == Integer.MAX_VALUE) return def;
  return res;
}


/**
 * Reads given configuration property.
 *
 * @param name
 *          Name of configuration property
 */
public static int getInt(String name) {
  int res = config.getInt(name);
  if (res == Integer.MAX_VALUE) {
    System.err.println("Config : ERROR : property '" + name + "' must be initialized.");
    System.exit(1);
  }
  return res;
}


/**
 * Reads given configuration property. If not found, returns the default value.
 *
 * @param name
 *          Name of configuration property
 * @param def
 *          default value
 */
public static long getLong(String name, long def) {
  long res = config.getLong(name);
  if (res == Long.MAX_VALUE) return def;
  return res;
}


/**
 * Reads given configuration property.
 *
 * @param name
 *          Name of configuration property
 */
public static long getLong(String name) {
  long res = config.getLong(name);
  if (res == Long.MAX_VALUE) {
    System.err.println("Config : ERROR : property '" + name + "' must be initialized.");
    System.exit(1);
  }
  return res;
}


/**
 * Reads given configuration property. If not found, returns the default value.
 *
 * @param name
 *          Name of configuration property
 * @param def
 *          default value
 */
public static double getDouble(String name, double def) {
  double res = config.getDouble(name);
  if (res == Double.MAX_VALUE) return def;
  return res;
}


/**
 * Reads given configuration property.
 *
 * @param name
 *          Name of configuration property
 */
public static double getDouble(String name) {
  double res = config.getDouble(name);
  if (res == Double.MAX_VALUE) {
    System.err.println("Config : ERROR : property '" + name + "' must be initialized.");
    System.exit(1);
  }
    return res;
}


/**
 * Reads given configuration property. If not found, returns the default value.
 *
 * @param name
 *          Name of configuration property
 * @param def
 *          default value
 */
public static String getString(String name, String def) {
  String res = config.getString(name);
  if (res.equals("")) return def;
  return res;
}


/**
 * Reads given configuration property. Removes
 * trailing whitespace characters.
 *
 * @param name
 *          Name of configuration property
 */
public static String getString(String name) {
  String res = config.getString(name);
  if (res.equals("")) {
    System.err.println("Config : ERROR : property '" + name + "' must be initialized.");
    throw new RuntimeException("not found");
    //System.exit(1);
  }
  return res;
}


/**
 * Returns an array of names prefixed by the specified name.
 *
 * @param name
 *          the component type (ie, the prefix)
 * @return the full property names in lexicografic order.
 */
public static String[] getSpecies(String name) {
  return config.getSpecies(name);
}

}
