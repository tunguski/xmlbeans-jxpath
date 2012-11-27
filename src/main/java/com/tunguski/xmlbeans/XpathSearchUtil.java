package com.tunguski.xmlbeans;

import static java.util.Arrays.*;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;


/**
 * Simple static util configuring xpath delegation.
 * @author Marek Romanowski
 * @since 25-11-2012
 */
public class XpathSearchUtil {


  /**
   * Select xpath with no custom options
   * @param xmlObject object from with search begins
   * @param xpath search path
   * @return list of {@link XmlObject}s found by search path
   */
  public static <E extends XmlObject> List<E> selectPath(
      XmlObject xmlObject, String xpath) {
    return selectPath(xmlObject, xpath, new XmlOptions());
  }


  /**
   * Select xpath with no custom options
   * @param xmlObject object from with search begins
   * @param xpath search path
   * @param options custom search options; some will be overriden to
   * force jxpath engine usage
   * @return list of {@link XmlObject}s found by search path
   */
  public static <E extends XmlObject> List<E> selectPath(
      XmlObject xmlObject, String xpath, XmlOptions options) {
    options.put(org.apache.xmlbeans.impl.store.Path._useDelegateForXpath, true);
    options.put(org.apache.xmlbeans.impl.store.Path.PATH_DELEGATE_INTERFACE,
        JXPath.class.getName());
    return (List<E>) asList(xmlObject.selectPath(xpath, options));
  }
}
