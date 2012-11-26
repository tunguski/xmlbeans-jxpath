package com.tunguski.xmlbeans;

/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.apache.xmlbeans.impl.store.PathDelegate.SelectPathInterface;


/**
 * https://issues.apache.org/jira/secure/attachment/12378652/jxpath-support.patch
 * @author Robert Marcano
 */
public class JXPath
        implements SelectPathInterface {

    /**
     * Construct given an XPath expression string.
     * @param xpathExpr The XPath expression.
     * @param contextVar The name of the context variable
     * @param namespaceMap a map of prefix/uri bindings for NS support
     * @param defaultNS the uri for the default element NS, if any
     */
    public JXPath(String xpathExpr, String contextVar,
                       Map namespaceMap, String defaultNS)
    {
        _queryExpr = xpathExpr;
        // TODO JXPath does not supports setting the default namespave
        this.namespaceMap = namespaceMap.entrySet().toArray();
    }

    /**
     * Select all nodes that are selectable by this XPath
     * expression. If multiple nodes match, multiple nodes
     * will be returned.
     * <p/>
     * <p/>
     * <b>NOTE:</b> In most cases, nodes will be returned
     * in document-order, as defined by the XML Canonicalization
     * specification.  The exception occurs when using XPath
     * expressions involving the <code>union</code> operator
     * (denoted with the pipe '|' character).
     * </p>
     * <p/>
     * <p/>
     * <b>NOTE:</b> Param node must be a Dom node which will be used during the xpath
     * execution and iteration through the results. A call of node.dispose() must be done
     * after reading all results.
     * </p>
     *
     * @param node The node, nodeset or Context object for evaluation.
     * This value can be null.
     * @return The <code>a list</code> of all items selected
     *         by this XPath expression.
     */
    public List selectNodes(Object node)
    {
        JXPathContext context = JXPathContext.newContext(node);

        // registering namespace prefixes
        for (int i = 0; i < namespaceMap.length; i++)
        {
            Map.Entry entry = (Map.Entry) namespaceMap[i];
            context.registerNamespace((String) entry.getKey(),
                    (String) entry.getValue());
        }

        List searchResults = context.selectNodes(_queryExpr);
        Iterator searchResultsIter = searchResults.iterator();
        List resultsList = new ArrayList(searchResults.size());
        // id XPath function is returning internal pointers instead of nodes, so
        // we need to filter them
        while (searchResultsIter.hasNext())
        {
            Object value = searchResultsIter.next();
            if (value instanceof DOMNodePointer) {
              value = ((DOMNodePointer)value).getNode();
            } else if (value instanceof NullPointer) {
              value = null;
            }

            if (value != null) {
              resultsList.add(value);
            }
        }
        return resultsList;
    }

    @Override
    public List selectPath(Object node)
    {
        return selectNodes(node);
    }


    private final Object[] namespaceMap;
    private final String _queryExpr;
}