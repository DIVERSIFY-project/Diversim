package diversim.util.config;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.nfunk.jep.JEP;


/**
 * This class is the container for the configuration data used in {@link Configuration}; see that
 * class for more information.
 */
@SuppressWarnings("rawtypes")
public class ConfigContainer implements Serializable {


private static final long serialVersionUID = 1L;


/**
 * The properties object that stores all configuration information.
 */
private Properties config;


/**
 * The maximum depth that can be reached when analyzing expressions. This value can be substituted
 * by setting the configuration parameter PAR_MAXDEPTH.
 */
private int maxdepth;


public ConfigContainer(Properties config) {
  this.config = config;
  maxdepth = 100;

  Map<String, String> map = new TreeMap<String, String>();
  Enumeration e = config.propertyNames();
  while (e.hasMoreElements()) {
    String name = (String)e.nextElement();
    String value = config.getProperty(name);
    map.put(name, value);
  }
//  Iterator i = map.keySet().iterator();
//  while (i.hasNext()) {
//    String name = (String)i.next();
//    System.err.println("INIT " + name + ("".equals(map.get(name)) ? "" : " = " + map.get(name)));
//  }

}


/**
 * @return true if and only if name is a specified (exisitng) property.
 */
public boolean contains(String name) {
  boolean ret = config.containsKey(name);
  return ret;
}


/**
 * Reads given property. If not found, or the value is empty string then throws a
 * {@link RuntimeException}. Empty string is not accepted as false due to the similar function of
 * {@link #contains} which returns true in that case. True is returned if the lowercase value of the
 * property is "true", otherwise false is returned.
 *
 * @param name
 *          Name of configuration property
 */
public boolean getBoolean(String name) {
  try {
    return getBool(name);
  }
  catch (RuntimeException e) {
    manageException(name, e);
    return false;
  }
}


/**
 * The actual methods that implements getBoolean.
 */
private boolean getBool(String name) {
  if (config.getProperty(name) == null) {
    throw new RuntimeException(name);
  }
  if (config.getProperty(name).matches("\\p{Blank}*")) {
    throw new RuntimeException(name + " -> Blank value is not accepted when parsing Boolean.");
  }
  boolean ret = (new Boolean(config.getProperty(name))).booleanValue();
  return ret;
}


/**
 * Reads given configuration property.
 *
 * @param name
 *          Name of configuration property
 */
public int getInt(String name) {
  try {
    Number ret = getVal(name, name, 0);
    return ret.intValue();
  }
  catch (RuntimeException e) {
    manageException(name, e);
    return Integer.MAX_VALUE;
  }
}


/**
 * Reads given configuration property.
 *
 * @param name
 *          Name of configuration property
 */
public long getLong(String name) {
  try {
    Number ret = getVal(name, name, 0);
    return ret.longValue();
  }
  catch (RuntimeException e) {
    manageException(name, e);
    return Long.MAX_VALUE;
  }
}


/**
 * Reads given configuration property.
 *
 * @param name
 *          Name of configuration property
 */
public double getDouble(String name) {
  try {
    Number ret = getVal(name, name, 0);
    return ret.doubleValue();
  }
  catch (RuntimeException e) {
    manageException(name, e);
    return Double.MAX_VALUE;
  }
}


/**
 * Read numeric property values, parsing expression if necessary.
 *
 * @param initial
 *          the property name that started this expression evaluation
 * @param property
 *          the current property name to be evaluated
 * @param depth
 *          the depth reached so far
 * @return the evaluation of the expression associated to property
 */
private Number getVal(String initial, String property, int depth) {
  if (depth > maxdepth) {
    throw new RuntimeException(initial
        + " -> Probable recursive definition - exceeded maximum depth " + maxdepth);
  }

  String s = config.getProperty(property);
  if (s == null) {
    throw new RuntimeException("WARNING : missing property '" + property + "' when evaluating '" + initial + "'.");
  }
  else if (s.equals("")) {
    throw new RuntimeException("WARNING : empty config value for property '" + initial + "'.");
  }

  JEP jep = new JEP();
  jep.setAllowUndeclared(true);

  jep.parseExpression(s);
  String[] symbols = getSymbols(jep);
  for (int i = 0; i < symbols.length; i++) {
    Object d = getVal(initial, symbols[i], depth + 1);
    jep.addVariable(symbols[i], d);
  }
  Object ret = jep.getValueAsObject();
  if (jep.hasError()) System.err.println(jep.getErrorInfo());
  return (Number)ret;
}


/**
 * Returns an array of string, containing the symbols contained in the expression parsed by the
 * specified JEP parser.
 *
 * @param jep
 *          the java expression parser containing the list of variables
 * @return an array of strings.
 */
private String[] getSymbols(JEP jep) {
  Hashtable h = jep.getSymbolTable();
  String[] ret = new String[h.size()];
  Enumeration e = h.keys();
  int i = 0;
  while (e.hasMoreElements()) {
    ret[i++] = (String)e.nextElement();
  }
  return ret;
}


/**
 * Reads given configuration property. If not found, throws a MissingParameterException. Removes
 * trailing whitespace characters.
 *
 * @param name
 *          Name of configuration property
 */
public String getString(String name) {
  try {
    return getStr(name);
  }
  catch (RuntimeException e) {
    manageException(name, e);
    return "";
  }
}


/**
 * The actual method implementing getString().
 */
private String getStr(String name) {
  String result = config.getProperty(name);
  if (result == null) {
    throw new RuntimeException("WARNING : property '" + name + "' is missing.");
  }

  return result.trim();
}


/**
 * Returns an array of names prefixed by the specified name.
 *
 * @param name
 *          the component type (ie, the prefix)
 * @return the full property names in lexicografic order.
 */
public String[] getSpecies(String name) {
  ArrayList<String> ll = new ArrayList<String>();
  final String pref = name + ".";

  Enumeration e = config.propertyNames();
  while (e.hasMoreElements()) {
    String key = (String)e.nextElement();
    if (key.startsWith(pref) && key.indexOf(".", pref.length()) < 0
        && key.matches(".*\\.[0-9]$")) ll.add(key);
  }
  String[] res = ll.toArray(new String[ll.size()]);
  Arrays.sort(res);
  return res;
}


private void manageException(String name, Exception e) {

  System.err.println("Config : " + e.getMessage());

}

}
