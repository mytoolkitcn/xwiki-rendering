/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.rendering.internal.renderer.xhtml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.rendering.internal.renderer.xhtml.image.XHTMLImageRenderer;
import org.xwiki.rendering.internal.renderer.xhtml.link.XHTMLLinkRenderer;
import org.xwiki.rendering.listener.Format;
import org.xwiki.rendering.listener.HeaderLevel;
import org.xwiki.rendering.listener.ListType;
import org.xwiki.rendering.listener.MetaData;
import org.xwiki.rendering.listener.chaining.BlockStateChainingListener;
import org.xwiki.rendering.listener.chaining.BlockStateChainingListener.Event;
import org.xwiki.rendering.listener.chaining.EmptyBlockChainingListener;
import org.xwiki.rendering.listener.chaining.ListenerChain;
import org.xwiki.rendering.listener.chaining.MetaDataStateChainingListener;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.renderer.AbstractChainingPrintRenderer;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.renderer.printer.XHTMLWikiPrinter;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.syntax.SyntaxType;

/**
 * Convert listener events to XHTML.
 * 
 * @version $Id$
 * @since 1.8RC1
 */
public class XHTMLChainingRenderer extends AbstractChainingPrintRenderer
{
    private XHTMLLinkRenderer linkRenderer;

    private XHTMLImageRenderer imageRenderer;

    private XHTMLWikiPrinter xhtmlWikiPrinter;

    /**
     * @param linkRenderer the object to render link events into XHTML. This is done so that it's pluggable because link
     *            rendering depends on how the underlying system wants to handle it. For example for XWiki we check if
     *            the document exists, we get the document URL, etc.
     * @param imageRenderer the object to render image events into XHTML. This is done so that it's pluggable because
     *            image rendering depends on how the underlying system wants to handle it. For example for XWiki we
     *            check if the image exists as a document attachments, we get its URL, etc.
     * @param listenerChain the chain of listener filters used to compute various states
     */
    public XHTMLChainingRenderer(XHTMLLinkRenderer linkRenderer, XHTMLImageRenderer imageRenderer,
        ListenerChain listenerChain)
    {
        setListenerChain(listenerChain);

        this.linkRenderer = linkRenderer;
        this.imageRenderer = imageRenderer;
    }

    // State

    protected BlockStateChainingListener getBlockState()
    {
        return (BlockStateChainingListener) getListenerChain().getListener(BlockStateChainingListener.class);
    }

    protected EmptyBlockChainingListener getEmptyBlockState()
    {
        return (EmptyBlockChainingListener) getListenerChain().getListener(EmptyBlockChainingListener.class);
    }

    protected MetaDataStateChainingListener getMetaDataState()
    {
        return (MetaDataStateChainingListener) getListenerChain().getListener(MetaDataStateChainingListener.class);
    }

    // Printer

    @Override
    protected void pushPrinter(WikiPrinter wikiPrinter)
    {
        super.pushPrinter(wikiPrinter);
        getXHTMLWikiPrinter().setWikiPrinter(getPrinter());
    }

    @Override
    protected void popPrinter()
    {
        super.popPrinter();
        getXHTMLWikiPrinter().setWikiPrinter(getPrinter());
    }

    protected XHTMLWikiPrinter getXHTMLWikiPrinter()
    {
        if (this.xhtmlWikiPrinter == null) {
            this.xhtmlWikiPrinter = new XHTMLWikiPrinter(getPrinter());
        }
        return this.xhtmlWikiPrinter;
    }

    // Events

    @Override
    public void beginGroup(Map<String, String> parameters)
    {
        Map<String, String> clonedParameters = new LinkedHashMap<String, String>();
        clonedParameters.putAll(parameters);
        getXHTMLWikiPrinter().printXMLStartElement("div", clonedParameters);
    }

    @Override
    public void endGroup(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("div");
    }

    @Override
    public void beginFormat(Format format, Map<String, String> parameters)
    {
        switch (format) {
            case BOLD:
                getXHTMLWikiPrinter().printXMLStartElement("strong");
                break;
            case ITALIC:
                getXHTMLWikiPrinter().printXMLStartElement("em");
                break;
            case STRIKEDOUT:
                getXHTMLWikiPrinter().printXMLStartElement("del");
                break;
            case UNDERLINED:
                getXHTMLWikiPrinter().printXMLStartElement("ins");
                break;
            case SUPERSCRIPT:
                getXHTMLWikiPrinter().printXMLStartElement("sup");
                break;
            case SUBSCRIPT:
                getXHTMLWikiPrinter().printXMLStartElement("sub");
                break;
            case MONOSPACE:
                getXHTMLWikiPrinter().printXMLStartElement("tt");
                break;
            case NONE:
                break;
        }
        if (!parameters.isEmpty()) {
            getXHTMLWikiPrinter().printXMLStartElement("span", parameters);
        }
    }

    @Override
    public void endFormat(Format format, Map<String, String> parameters)
    {
        if (!parameters.isEmpty()) {
            getXHTMLWikiPrinter().printXMLEndElement("span");
        }
        switch (format) {
            case BOLD:
                getXHTMLWikiPrinter().printXMLEndElement("strong");
                break;
            case ITALIC:
                getXHTMLWikiPrinter().printXMLEndElement("em");
                break;
            case STRIKEDOUT:
                getXHTMLWikiPrinter().printXMLEndElement("del");
                break;
            case UNDERLINED:
                getXHTMLWikiPrinter().printXMLEndElement("ins");
                break;
            case SUPERSCRIPT:
                getXHTMLWikiPrinter().printXMLEndElement("sup");
                break;
            case SUBSCRIPT:
                getXHTMLWikiPrinter().printXMLEndElement("sub");
                break;
            case MONOSPACE:
                getXHTMLWikiPrinter().printXMLEndElement("tt");
                break;
            case NONE:
                break;
        }
    }

    @Override
    public void beginParagraph(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLStartElement("p", parameters);
    }

    @Override
    public void endParagraph(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("p");
    }

    @Override
    public void onNewLine()
    {
        getXHTMLWikiPrinter().printXMLElement("br");
    }

    @Override
    public void beginLink(ResourceReference reference, boolean isFreeStandingURI, Map<String, String> parameters)
    {
        // Ensure the link renderer is using the latest printer since the original printer used could have been
        // superseded by another one in the printer stack.
        this.linkRenderer.setXHTMLWikiPrinter(getXHTMLWikiPrinter());

        // If the ResourceReference doesn't have a base reference specified, then look for one in previously sent
        // events (it's sent in begin/endMetaData events).
        List<String> baseReferences = reference.getBaseReferences();
        if (baseReferences.isEmpty()) {
            reference.addBaseReferences(getMetaDataState().<String> getAllMetaData(MetaData.SOURCE));
        }

        this.linkRenderer.beginLink(reference, isFreeStandingURI, parameters);
    }

    @Override
    public void endLink(ResourceReference reference, boolean isFreeStandingURI, Map<String, String> parameters)
    {
        this.linkRenderer.setHasLabel(!getEmptyBlockState().isCurrentContainerBlockEmpty());
        this.linkRenderer.endLink(reference, isFreeStandingURI, parameters);
    }

    @Override
    public void beginHeader(HeaderLevel level, String id, Map<String, String> parameters)
    {
        Map<String, String> attributes = new LinkedHashMap<String, String>();

        attributes.put("id", id);
        attributes.putAll(parameters);

        // Section editing feature:
        // In order for the UI side to be able to add a section edit button we need to provide some information to it
        // and especially we need to tell it if the header was a header generated by a macro or not. The reason is
        // that macro-generated headers should not be editable by the user.
        // TODO: In the future it's possible that we'll want this kind of behavior implemented using a Transformation.
        // If we decide this then remove this code.
        if (getBlockState().isInMacro()) {
            String classAttributeName = "class";
            String classValue = attributes.get(classAttributeName);
            String newClassValue = "wikigeneratedheader";
            if (classValue == null) {
                classValue = newClassValue;
            } else {
                classValue = classValue.trim() + " " + newClassValue;
            }
            attributes.put(classAttributeName, classValue);
        }

        getXHTMLWikiPrinter().printXMLStartElement("h" + level.getAsInt(), attributes);
        // We generate a span so that CSS rules have a hook to perform some magic that wouldn't work on just a H
        // element. Like some IE6 magic and others.
        getXHTMLWikiPrinter().printXMLStartElement("span");
    }

    @Override
    public void endHeader(HeaderLevel level, String id, Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("span");
        getXHTMLWikiPrinter().printXMLEndElement("h" + level.getAsInt());
    }

    @Override
    public void onWord(String word)
    {
        getXHTMLWikiPrinter().printXML(word);
    }

    @Override
    public void onSpace()
    {
        // The XHTML printer will decide whether to print a normal space or a &nbsp;
        getXHTMLWikiPrinter().printSpace();
    }

    @Override
    public void onSpecialSymbol(char symbol)
    {
        getXHTMLWikiPrinter().printXML("" + symbol);
    }

    @Override
    public void beginList(ListType listType, Map<String, String> parameters)
    {
        if (listType == ListType.BULLETED) {
            getXHTMLWikiPrinter().printXMLStartElement("ul", parameters);
        } else {
            getXHTMLWikiPrinter().printXMLStartElement("ol", parameters);
        }
    }

    @Override
    public void beginListItem()
    {
        getXHTMLWikiPrinter().printXMLStartElement("li");
    }

    @Override
    public void endList(ListType listType, Map<String, String> parameters)
    {
        if (listType == ListType.BULLETED) {
            getXHTMLWikiPrinter().printXMLEndElement("ul");
        } else {
            getXHTMLWikiPrinter().printXMLEndElement("ol");
        }
    }

    @Override
    public void endListItem()
    {
        getXHTMLWikiPrinter().printXMLEndElement("li");
    }

    @Override
    public void onId(String name)
    {
        // Don't use the "name" attribute (see http://www.w3.org/TR/html4/struct/links.html#h-12.2.3).
        // If the id s in a paragraph use <span id="..."> and if in a standalone block then use
        // <div id="...">.
        if (getBlockState().isInLine()) {
            // Note: We're using <span><span/> and not <span/> since some browsers do not support the
            // <span/> syntax (FF3) when the content type is set to HTML instead of XHTML.
            getXHTMLWikiPrinter().printXMLStartElement("span", new String[][] {{"id", name}});
            getXHTMLWikiPrinter().printXMLEndElement("span");
        } else {
            getXHTMLWikiPrinter().printXMLStartElement("div", new String[][] {{"id", name}});
            getXHTMLWikiPrinter().printXMLEndElement("div");
        }
    }

    @Override
    public void onHorizontalLine(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLElement("hr", parameters);
    }

    @Override
    public void onVerbatim(String protectedString, boolean isInline, Map<String, String> parameters)
    {
        if (isInline) {
            // Note: We generate a tt element rather than a pre element since pre elements cannot be located inside
            // paragraphs for example. There also no tag in XHTML that has a semantic of preserving inline content so
            // tt is the closed to pre for inline.
            // The class is what is expected by wikimodel to understand the tt as meaning a verbatim and not a Monospace
            // element.
            getXHTMLWikiPrinter().printXMLStartElement("tt", new String[][] {{"class", "wikimodel-verbatim"}});
            getXHTMLWikiPrinter().printXML(protectedString);
            getXHTMLWikiPrinter().printXMLEndElement("tt");
        } else {
            getXHTMLWikiPrinter().printXMLStartElement("pre", parameters);
            getXHTMLWikiPrinter().printXML(protectedString);
            getXHTMLWikiPrinter().printXMLEndElement("pre");
        }
    }

    @Override
    public void onEmptyLines(int count)
    {
        // We need to use a special tag for empty lines since in XHTML the BR tag cannot be used outside of content
        // tags.
        // Note: We're using <div><div/> and not <div/> since some browsers do not support the <div/> syntax (FF3)
        // when the content type is set to HTML instead of XHTML.
        for (int i = 0; i < count; ++i) {
            getXHTMLWikiPrinter().printXMLStartElement("div", new String[][] {{"class", "wikimodel-emptyline"}});
            getXHTMLWikiPrinter().printXMLEndElement("div");
        }
    }

    /**
     * {@inheritDoc}
     * @since 2.0RC1
     */
    @Override
    public void beginDefinitionList(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLStartElement("dl", parameters);
    }

    /**
     * {@inheritDoc}
     * @since 2.0RC1
     */
    @Override
    public void endDefinitionList(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("dl");
    }

    @Override
    public void beginDefinitionTerm()
    {
        getXHTMLWikiPrinter().printXMLStartElement("dt");
    }

    @Override
    public void beginDefinitionDescription()
    {
        getXHTMLWikiPrinter().printXMLStartElement("dd");
    }

    @Override
    public void endDefinitionTerm()
    {
        getXHTMLWikiPrinter().printXMLEndElement("dt");
    }

    @Override
    public void endDefinitionDescription()
    {
        getXHTMLWikiPrinter().printXMLEndElement("dd");
    }

    @Override
    public void beginQuotation(Map<String, String> parameters)
    {
        if (getBlockState().isInQuotationLine()) {
            getXHTMLWikiPrinter().printXMLEndElement("p");
        }

        getXHTMLWikiPrinter().printXMLStartElement("blockquote", parameters);

        getXHTMLWikiPrinter().printXMLStartElement("p");
    }

    @Override
    public void endQuotation(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("p");

        getXHTMLWikiPrinter().printXMLEndElement("blockquote");

        if (getBlockState().isInQuotationLine()) {
            getXHTMLWikiPrinter().printXMLStartElement("p");
        }
    }

    @Override
    public void beginQuotationLine()
    {
        // Send a new line if the previous event was endQuotationLine since we need to separate each quotation line
        // or they'll printed next to each other and not on a new line each.
        if (getBlockState().isInQuotation() && getBlockState().getPreviousEvent() == Event.QUOTATION_LINE) {
            onNewLine();
        }
    }

    @Override
    public void beginTable(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLStartElement("table", parameters);
    }

    @Override
    public void beginTableRow(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLStartElement("tr", parameters);
    }

    @Override
    public void beginTableCell(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLStartElement("td", parameters);
    }

    @Override
    public void beginTableHeadCell(Map<String, String> parameters)
    {
        // Find proper scope attribute value
        Map<String, String> parametersWithScope;
        if (!parameters.containsKey("scope")) {
            parametersWithScope = new LinkedHashMap<String, String>(parameters);

            if (getBlockState().getCellRow() == 0 || getBlockState().getCellCol() > 0) {
                parametersWithScope.put("scope", "col");
            } else {
                parametersWithScope.put("scope", "row");
            }
        } else {
            parametersWithScope = parameters;
        }

        // Write th element
        getXHTMLWikiPrinter().printXMLStartElement("th", parametersWithScope);
    }

    @Override
    public void endTable(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("table");
    }

    @Override
    public void endTableRow(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("tr");
    }

    @Override
    public void endTableCell(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("td");
    }

    @Override
    public void endTableHeadCell(Map<String, String> parameters)
    {
        getXHTMLWikiPrinter().printXMLEndElement("th");
    }

    /**
     * {@inheritDoc}
     * @since 2.5RC1
     */
    @Override
    public void onImage(ResourceReference reference, boolean isFreeStandingURI, Map<String, String> parameters)
    {
        // Ensure the image renderer is using the latest printer since the original printer used could have been
        // superseded by another one in the printer stack.
        this.imageRenderer.setXHTMLWikiPrinter(getXHTMLWikiPrinter());

        // If the ResourceReference doesn't have a base reference specified, then look for one in previously sent
        // events (it's sent in begin/endMetaData events).
        List<String> baseReferences = reference.getBaseReferences();
        if (baseReferences.isEmpty()) {
            reference.addBaseReferences(getMetaDataState().<String> getAllMetaData(MetaData.SOURCE));
        }

        this.imageRenderer.onImage(reference, isFreeStandingURI, parameters);
    }

    @Override
    public void onRawText(String text, Syntax syntax)
    {
        // Directly inject the HTML content in the wiki printer (bypassing the XHTML printer)
        if ((syntax.getType() == SyntaxType.XHTML) || (syntax.getType() == SyntaxType.HTML)) {
            getXHTMLWikiPrinter().printRaw(text);
        }
    }
}