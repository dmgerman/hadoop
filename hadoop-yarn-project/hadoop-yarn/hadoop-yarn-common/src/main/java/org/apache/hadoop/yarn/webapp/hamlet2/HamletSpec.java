begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.hamlet2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|hamlet2
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|SubView
import|;
end_import

begin_comment
comment|/**  * HTML5 compatible HTML4 builder interfaces.  *  *<p>Generated from HTML 4.01 strict DTD and HTML5 diffs.  *<br>cf. http://www.w3.org/TR/html4/  *<br>cf. http://www.w3.org/TR/html5-diff/  *<p> The omitted attributes and elements (from the 4.01 DTD)  * are for HTML5 compatibility.  *  *<p>Note, the common argument selector uses the same syntax as Haml/Sass:  *<pre>  selector ::= (#id)?(.class)*</pre>  * cf. http://haml-lang.com/  *  *<p>The naming convention used in this class is slightly different from  * normal classes. A CamelCase interface corresponds to an entity in the DTD.  * _CamelCase is for internal refactoring. An element builder interface is in  * UPPERCASE, corresponding to an element definition in the DTD. $lowercase is  * used as attribute builder methods to differentiate from element builder  * methods.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|class|HamletSpec
specifier|public
class|class
name|HamletSpec
block|{
comment|// The enum values are lowercase for better compression,
comment|// while avoiding runtime conversion.
comment|// cf. http://www.w3.org/Protocols/HTTP/Performance/Compression/HTMLCanon.html
comment|//     http://www.websiteoptimization.com/speed/tweak/lowercase/
comment|/** %Shape (case-insensitive) */
DECL|enum|Shape
specifier|public
enum|enum
name|Shape
block|{
comment|/**      * rectangle      */
DECL|enumConstant|rect
name|rect
block|,
comment|/**      * circle      */
DECL|enumConstant|circle
name|circle
block|,
comment|/**      * polygon      */
DECL|enumConstant|poly
name|poly
block|,
comment|/**      * default      */
DECL|enumConstant|Default
name|Default
block|}
empty_stmt|;
comment|/** Values for the %18n dir attribute (case-insensitive) */
DECL|enum|Dir
specifier|public
enum|enum
name|Dir
block|{
comment|/**      * left to right      */
DECL|enumConstant|ltr
name|ltr
block|,
comment|/**      * right to left      */
DECL|enumConstant|rtl
name|rtl
block|}
empty_stmt|;
comment|/** %MediaDesc (case-sensitive) */
DECL|enum|Media
specifier|public
enum|enum
name|Media
block|{
comment|/**      * computer screen      */
DECL|enumConstant|screen
name|screen
block|,
comment|/**      * teletype/terminal      */
DECL|enumConstant|tty
name|tty
block|,
comment|/**      * television      */
DECL|enumConstant|tv
name|tv
block|,
comment|/**      * projection      */
DECL|enumConstant|projection
name|projection
block|,
comment|/**      * mobile device      */
DECL|enumConstant|handheld
name|handheld
block|,
comment|/**      * print media      */
DECL|enumConstant|print
name|print
block|,
comment|/**      * braille      */
DECL|enumConstant|braille
name|braille
block|,
comment|/**      * aural      */
DECL|enumConstant|aural
name|aural
block|,
comment|/**      * suitable all media      */
DECL|enumConstant|all
name|all
block|}
empty_stmt|;
comment|/** %LinkTypes (case-insensitive) */
DECL|enum|LinkType
specifier|public
enum|enum
name|LinkType
block|{
comment|/**      *      */
DECL|enumConstant|alternate
name|alternate
block|,
comment|/**      *      */
DECL|enumConstant|stylesheet
name|stylesheet
block|,
comment|/**      *      */
DECL|enumConstant|start
name|start
block|,
comment|/**      *      */
DECL|enumConstant|next
name|next
block|,
comment|/**      *      */
DECL|enumConstant|prev
name|prev
block|,
comment|/**      *      */
DECL|enumConstant|contents
name|contents
block|,
comment|/**      *      */
DECL|enumConstant|index
name|index
block|,
comment|/**      *      */
DECL|enumConstant|glossary
name|glossary
block|,
comment|/**      *      */
DECL|enumConstant|copyright
name|copyright
block|,
comment|/**      *      */
DECL|enumConstant|chapter
name|chapter
block|,
comment|/**      *      */
DECL|enumConstant|section
name|section
block|,
comment|/**      *      */
DECL|enumConstant|subsection
name|subsection
block|,
comment|/**      *      */
DECL|enumConstant|appendix
name|appendix
block|,
comment|/**      *      */
DECL|enumConstant|help
name|help
block|,
comment|/**      *      */
DECL|enumConstant|bookmark
name|bookmark
block|}
empty_stmt|;
comment|/** Values for form methods (case-insensitive) */
DECL|enum|Method
specifier|public
enum|enum
name|Method
block|{
comment|/**      * HTTP GET      */
DECL|enumConstant|get
name|get
block|,
comment|/**      * HTTP POST      */
DECL|enumConstant|post
name|post
block|}
empty_stmt|;
comment|/** %InputType (case-insensitive) */
DECL|enum|InputType
specifier|public
enum|enum
name|InputType
block|{
comment|/**      *      */
DECL|enumConstant|text
name|text
block|,
comment|/**      *      */
DECL|enumConstant|password
name|password
block|,
comment|/**      *      */
DECL|enumConstant|checkbox
name|checkbox
block|,
comment|/**      *      */
DECL|enumConstant|radio
name|radio
block|,
comment|/**      *      */
DECL|enumConstant|submit
name|submit
block|,
comment|/**      *      */
DECL|enumConstant|reset
name|reset
block|,
comment|/**      *      */
DECL|enumConstant|file
name|file
block|,
comment|/**      *      */
DECL|enumConstant|hidden
name|hidden
block|,
comment|/**      *      */
DECL|enumConstant|image
name|image
block|,
comment|/**      *      */
DECL|enumConstant|button
name|button
block|}
empty_stmt|;
comment|/** Values for button types */
DECL|enum|ButtonType
specifier|public
enum|enum
name|ButtonType
block|{
comment|/**      *      */
DECL|enumConstant|button
name|button
block|,
comment|/**      *      */
DECL|enumConstant|submit
name|submit
block|,
comment|/**      *      */
DECL|enumConstant|reset
name|reset
block|}
empty_stmt|;
comment|/** %Scope (case-insensitive) */
DECL|enum|Scope
specifier|public
enum|enum
name|Scope
block|{
comment|/**      *      */
DECL|enumConstant|row
name|row
block|,
comment|/**      *      */
DECL|enumConstant|col
name|col
block|,
comment|/**      *      */
DECL|enumConstant|rowgroup
name|rowgroup
block|,
comment|/**      *      */
DECL|enumConstant|colgroup
name|colgroup
block|}
empty_stmt|;
comment|/**    * The element annotation for specifying element options other than    * attributes and allowed child elements    */
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|TYPE
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|Element
specifier|public
annotation_defn|@interface
name|Element
block|{
comment|/**      * Whether the start tag is required for the element.      * @return true if start tag is required      */
DECL|method|startTag ()
DECL|field|true
name|boolean
name|startTag
parameter_list|()
default|default
literal|true
function_decl|;
comment|/**      * Whether the end tag is required.      * @return true if end tag is required      */
DECL|method|endTag ()
DECL|field|true
name|boolean
name|endTag
parameter_list|()
default|default
literal|true
function_decl|;
block|}
comment|/**    *    */
DECL|interface|__
specifier|public
interface|interface
name|__
block|{}
comment|/**    *    */
DECL|interface|_Child
specifier|public
interface|interface
name|_Child
extends|extends
name|__
block|{
comment|/**      * Finish the current element.      * @return the parent element      */
DECL|method|__ ()
name|__
name|__
parameter_list|()
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Script
specifier|public
interface|interface
name|_Script
block|{
comment|/**      * Add a script element.      * @return a script element builder      */
DECL|method|script ()
name|SCRIPT
name|script
parameter_list|()
function_decl|;
comment|/**      * Add a script element      * @param src uri of the script      * @return the current element builder      */
DECL|method|script (String src)
name|_Script
name|script
parameter_list|(
name|String
name|src
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Object
specifier|public
interface|interface
name|_Object
block|{
comment|/**      * Add an object element.      * @return an object element builder      */
DECL|method|object ()
name|OBJECT
name|object
parameter_list|()
function_decl|;
comment|/**      * Add an object element.      * @param selector as #id.class etc.      * @return an object element builder      */
DECL|method|object (String selector)
name|OBJECT
name|object
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|/** %head.misc */
DECL|interface|HeadMisc
specifier|public
interface|interface
name|HeadMisc
extends|extends
name|_Script
extends|,
name|_Object
block|{
comment|/**      * Add a style element.      * @return a style element builder      */
DECL|method|style ()
name|STYLE
name|style
parameter_list|()
function_decl|;
comment|/**      * Add a css style element.      * @param lines content of the style sheet      * @return the current element builder      */
DECL|method|style (Object... lines)
name|HeadMisc
name|style
parameter_list|(
name|Object
modifier|...
name|lines
parameter_list|)
function_decl|;
comment|/**      * Add a meta element.      * @return a meta element builder      */
DECL|method|meta ()
name|META
name|meta
parameter_list|()
function_decl|;
comment|/**      * Add a meta element.      * Shortcut of<code>meta().$name(name).$content(content).__();</code>      * @param name of the meta element      * @param content of the meta element      * @return the current element builder      */
DECL|method|meta (String name, String content)
name|HeadMisc
name|meta
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|content
parameter_list|)
function_decl|;
comment|/**      * Add a meta element with http-equiv attribute.      * Shortcut of<br>      *<code>meta().$http_equiv(header).$content(content).__();</code>      * @param header for the http-equiv attribute      * @param content of the header      * @return the current element builder      */
DECL|method|meta_http (String header, String content)
name|HeadMisc
name|meta_http
parameter_list|(
name|String
name|header
parameter_list|,
name|String
name|content
parameter_list|)
function_decl|;
comment|/**      * Add a link element.      * @return a link element builder      */
DECL|method|link ()
name|LINK
name|link
parameter_list|()
function_decl|;
comment|/**      * Add a link element.      * Implementation should try to figure out type by the suffix of href.      * So<code>link("style.css");</code> is a shortcut of      *<code>link().$rel("stylesheet").$type("text/css").$href("style.css").__();      *</code>      * @param href of the link      * @return the current element builder      */
DECL|method|link (String href)
name|HeadMisc
name|link
parameter_list|(
name|String
name|href
parameter_list|)
function_decl|;
block|}
comment|/** %heading */
DECL|interface|Heading
specifier|public
interface|interface
name|Heading
block|{
comment|/**      * Add an H1 element.      * @return a new H1 element builder      */
DECL|method|h1 ()
name|H1
name|h1
parameter_list|()
function_decl|;
comment|/**      * Add a complete H1 element.      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h1 (String cdata)
name|Heading
name|h1
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete H1 element      * @param selector the css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h1 (String selector, String cdata)
name|Heading
name|h1
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add an H2 element.      * @return a new H2 element builder      */
DECL|method|h2 ()
name|H2
name|h2
parameter_list|()
function_decl|;
comment|/**      * Add a complete H2 element.      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h2 (String cdata)
name|Heading
name|h2
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete H1 element      * @param selector the css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h2 (String selector, String cdata)
name|Heading
name|h2
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add an H3 element.      * @return a new H3 element builder      */
DECL|method|h3 ()
name|H3
name|h3
parameter_list|()
function_decl|;
comment|/**      * Add a complete H3 element.      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h3 (String cdata)
name|Heading
name|h3
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete H1 element      * @param selector the css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h3 (String selector, String cdata)
name|Heading
name|h3
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add an H4 element.      * @return a new H4 element builder      */
DECL|method|h4 ()
name|H4
name|h4
parameter_list|()
function_decl|;
comment|/**      * Add a complete H4 element.      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h4 (String cdata)
name|Heading
name|h4
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete H4 element      * @param selector the css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h4 (String selector, String cdata)
name|Heading
name|h4
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add an H5 element.      * @return a new H5 element builder      */
DECL|method|h5 ()
name|H5
name|h5
parameter_list|()
function_decl|;
comment|/**      * Add a complete H5 element.      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h5 (String cdata)
name|Heading
name|h5
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete H5 element      * @param selector the css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h5 (String selector, String cdata)
name|Heading
name|h5
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add an H6 element.      * @return a new H6 element builder      */
DECL|method|h6 ()
name|H6
name|h6
parameter_list|()
function_decl|;
comment|/**      * Add a complete H6 element.      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h6 (String cdata)
name|Heading
name|h6
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete H6 element.      * @param selector the css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|h6 (String selector, String cdata)
name|Heading
name|h6
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/** %list */
DECL|interface|Listing
specifier|public
interface|interface
name|Listing
block|{
comment|/**      * Add a UL (unordered list) element.      * @return a new UL element builder      */
DECL|method|ul ()
name|UL
name|ul
parameter_list|()
function_decl|;
comment|/**      * Add a UL (unordered list) element.      * @param selector the css selector in the form of (#id)?(.class)*      * @return a new UL element builder      */
DECL|method|ul (String selector)
name|UL
name|ul
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a OL (ordered list) element.      * @return a new UL element builder      */
DECL|method|ol ()
name|OL
name|ol
parameter_list|()
function_decl|;
comment|/**      * Add a OL (ordered list) element.      * @param selector the css selector in the form of (#id)?(.class)*      * @return a new UL element builder      */
DECL|method|ol (String selector)
name|OL
name|ol
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|/** % preformatted */
DECL|interface|Preformatted
specifier|public
interface|interface
name|Preformatted
block|{
comment|/**      * Add a PRE (preformatted) element.      * @return a new PRE element builder      */
DECL|method|pre ()
name|PRE
name|pre
parameter_list|()
function_decl|;
comment|/**      * Add a PRE (preformatted) element.      * @param selector the css selector in the form of (#id)?(.class)*      * @return a new PRE element builder      */
DECL|method|pre (String selector)
name|PRE
name|pre
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|/** %coreattrs */
DECL|interface|CoreAttrs
specifier|public
interface|interface
name|CoreAttrs
block|{
comment|/** document-wide unique id      * @param id the id      * @return the current element builder      */
DECL|method|$id (String id)
name|CoreAttrs
name|$id
parameter_list|(
name|String
name|id
parameter_list|)
function_decl|;
comment|/** space-separated list of classes      * @param cls the classes      * @return the current element builder      */
DECL|method|$class (String cls)
name|CoreAttrs
name|$class
parameter_list|(
name|String
name|cls
parameter_list|)
function_decl|;
comment|/** associated style info      * @param style the style      * @return the current element builder      */
DECL|method|$style (String style)
name|CoreAttrs
name|$style
parameter_list|(
name|String
name|style
parameter_list|)
function_decl|;
comment|/** advisory title      * @param title the title      * @return the current element builder      */
DECL|method|$title (String title)
name|CoreAttrs
name|$title
parameter_list|(
name|String
name|title
parameter_list|)
function_decl|;
block|}
comment|/** %i18n */
DECL|interface|I18nAttrs
specifier|public
interface|interface
name|I18nAttrs
block|{
comment|/** language code      * @param lang the code      * @return the current element builder      */
DECL|method|$lang (String lang)
name|I18nAttrs
name|$lang
parameter_list|(
name|String
name|lang
parameter_list|)
function_decl|;
comment|/** direction for weak/neutral text      * @param dir the {@link Dir} value      * @return the current element builder      */
DECL|method|$dir (Dir dir)
name|I18nAttrs
name|$dir
parameter_list|(
name|Dir
name|dir
parameter_list|)
function_decl|;
block|}
comment|/** %events */
DECL|interface|EventsAttrs
specifier|public
interface|interface
name|EventsAttrs
block|{
comment|/** a pointer button was clicked      * @param onclick the script      * @return the current element builder      */
DECL|method|$onclick (String onclick)
name|EventsAttrs
name|$onclick
parameter_list|(
name|String
name|onclick
parameter_list|)
function_decl|;
comment|/** a pointer button was double clicked      * @param ondblclick the script      * @return the current element builder      */
DECL|method|$ondblclick (String ondblclick)
name|EventsAttrs
name|$ondblclick
parameter_list|(
name|String
name|ondblclick
parameter_list|)
function_decl|;
comment|/** a pointer button was pressed down      * @param onmousedown the script      * @return the current element builder      */
DECL|method|$onmousedown (String onmousedown)
name|EventsAttrs
name|$onmousedown
parameter_list|(
name|String
name|onmousedown
parameter_list|)
function_decl|;
comment|/** a pointer button was released      * @param onmouseup the script      * @return the current element builder      */
DECL|method|$onmouseup (String onmouseup)
name|EventsAttrs
name|$onmouseup
parameter_list|(
name|String
name|onmouseup
parameter_list|)
function_decl|;
comment|/** a pointer was moved onto      * @param onmouseover the script      * @return the current element builder      */
DECL|method|$onmouseover (String onmouseover)
name|EventsAttrs
name|$onmouseover
parameter_list|(
name|String
name|onmouseover
parameter_list|)
function_decl|;
comment|/** a pointer was moved within      * @param onmousemove the script      * @return the current element builder      */
DECL|method|$onmousemove (String onmousemove)
name|EventsAttrs
name|$onmousemove
parameter_list|(
name|String
name|onmousemove
parameter_list|)
function_decl|;
comment|/** a pointer was moved away      * @param onmouseout the script      * @return the current element builder      */
DECL|method|$onmouseout (String onmouseout)
name|EventsAttrs
name|$onmouseout
parameter_list|(
name|String
name|onmouseout
parameter_list|)
function_decl|;
comment|/** a key was pressed and released      * @param onkeypress the script      * @return the current element builder      */
DECL|method|$onkeypress (String onkeypress)
name|EventsAttrs
name|$onkeypress
parameter_list|(
name|String
name|onkeypress
parameter_list|)
function_decl|;
comment|/** a key was pressed down      * @param onkeydown the script      * @return the current element builder      */
DECL|method|$onkeydown (String onkeydown)
name|EventsAttrs
name|$onkeydown
parameter_list|(
name|String
name|onkeydown
parameter_list|)
function_decl|;
comment|/** a key was released      * @param onkeyup the script      * @return the current element builder      */
DECL|method|$onkeyup (String onkeyup)
name|EventsAttrs
name|$onkeyup
parameter_list|(
name|String
name|onkeyup
parameter_list|)
function_decl|;
block|}
comment|/** %attrs */
DECL|interface|Attrs
specifier|public
interface|interface
name|Attrs
extends|extends
name|CoreAttrs
extends|,
name|I18nAttrs
extends|,
name|EventsAttrs
block|{   }
comment|/** Part of %pre.exclusion */
DECL|interface|_FontSize
specifier|public
interface|interface
name|_FontSize
extends|extends
name|_Child
block|{
comment|// BIG omitted cf. http://www.w3.org/TR/html5-diff/
comment|/**      * Add a SMALL (small print) element      * @return a new SMALL element builder      */
DECL|method|small ()
name|SMALL
name|small
parameter_list|()
function_decl|;
comment|/**      * Add a complete small (small print) element.      * Shortcut of: small().__(cdata).__();      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|small (String cdata)
name|_FontSize
name|small
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete small (small print) element.      * Shortcut of: small().$id(id).$class(class).__(cdata).__();      * @param selector css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|small (String selector, String cdata)
name|_FontSize
name|small
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/** %fontstyle -(%pre.exclusion) */
DECL|interface|_FontStyle
specifier|public
interface|interface
name|_FontStyle
extends|extends
name|_Child
block|{
comment|// TT omitted
comment|/**      * Add an I (italic, alt voice/mood) element.      * @return the new I element builder      */
DECL|method|i ()
name|I
name|i
parameter_list|()
function_decl|;
comment|/**      * Add a complete I (italic, alt voice/mood) element.      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|i (String cdata)
name|_FontStyle
name|i
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete I (italic, alt voice/mood) element.      * @param selector the css selector in the form of (#id)?(.class)*      * @param cdata the content of the element      * @return the current element builder      */
DECL|method|i (String selector, String cdata)
name|_FontStyle
name|i
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a new B (bold/important) element.      * @return a new B element builder      */
DECL|method|b ()
name|B
name|b
parameter_list|()
function_decl|;
comment|/**      * Add a complete B (bold/important) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|b (String cdata)
name|_FontStyle
name|b
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete B (bold/important) element.      * @param selector the css select (#id)?(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|b (String selector, String cdata)
name|_FontStyle
name|b
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/** %fontstyle */
DECL|interface|FontStyle
specifier|public
interface|interface
name|FontStyle
extends|extends
name|_FontStyle
extends|,
name|_FontSize
block|{   }
comment|/** %phrase */
DECL|interface|Phrase
specifier|public
interface|interface
name|Phrase
extends|extends
name|_Child
block|{
comment|/**      * Add an EM (emphasized) element.      * @return a new EM element builder      */
DECL|method|em ()
name|EM
name|em
parameter_list|()
function_decl|;
comment|/**      * Add an EM (emphasized) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|em (String cdata)
name|Phrase
name|em
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add an EM (emphasized) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|em (String selector, String cdata)
name|Phrase
name|em
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a STRONG (important) element.      * @return a new STRONG element builder      */
DECL|method|strong ()
name|STRONG
name|strong
parameter_list|()
function_decl|;
comment|/**      * Add a complete STRONG (important) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|strong (String cdata)
name|Phrase
name|strong
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete STRONG (important) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|strong (String selector, String cdata)
name|Phrase
name|strong
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a DFN element.      * @return a new DFN element builder      */
DECL|method|dfn ()
name|DFN
name|dfn
parameter_list|()
function_decl|;
comment|/**      * Add a complete DFN element.      * @param cdata the content      * @return the current element builder      */
DECL|method|dfn (String cdata)
name|Phrase
name|dfn
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete DFN element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|dfn (String selector, String cdata)
name|Phrase
name|dfn
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a CODE (code fragment) element.      * @return a new CODE element builder      */
DECL|method|code ()
name|CODE
name|code
parameter_list|()
function_decl|;
comment|/**      * Add a complete CODE element.      * @param cdata the code      * @return the current element builder      */
DECL|method|code (String cdata)
name|Phrase
name|code
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete CODE element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the code      * @return the current element builder      */
DECL|method|code (String selector, String cdata)
name|Phrase
name|code
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a SAMP (sample) element.      * @return a new SAMP element builder      */
DECL|method|samp ()
name|SAMP
name|samp
parameter_list|()
function_decl|;
comment|/**      * Add a complete SAMP (sample) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|samp (String cdata)
name|Phrase
name|samp
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete SAMP (sample) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|samp (String selector, String cdata)
name|Phrase
name|samp
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a KBD (keyboard) element.      * @return a new KBD element builder      */
DECL|method|kbd ()
name|KBD
name|kbd
parameter_list|()
function_decl|;
comment|/**      * Add a KBD (keyboard) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|kbd (String cdata)
name|Phrase
name|kbd
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a KBD (keyboard) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|kbd (String selector, String cdata)
name|Phrase
name|kbd
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a VAR (variable) element.      * @return a new VAR element builder      */
DECL|method|var ()
name|VAR
name|var
parameter_list|()
function_decl|;
comment|/**      * Add a VAR (variable) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|var (String cdata)
name|Phrase
name|var
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a VAR (variable) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|var (String selector, String cdata)
name|Phrase
name|var
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a CITE element.      * @return a new CITE element builder      */
DECL|method|cite ()
name|CITE
name|cite
parameter_list|()
function_decl|;
comment|/**      * Add a CITE element.      * @param cdata the content      * @return the current element builder      */
DECL|method|cite (String cdata)
name|Phrase
name|cite
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a CITE element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|cite (String selector, String cdata)
name|Phrase
name|cite
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add an ABBR (abbreviation) element.      * @return a new ABBR element builder      */
DECL|method|abbr ()
name|ABBR
name|abbr
parameter_list|()
function_decl|;
comment|/**      * Add a ABBR (abbreviation) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|abbr (String cdata)
name|Phrase
name|abbr
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a ABBR (abbreviation) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|abbr (String selector, String cdata)
name|Phrase
name|abbr
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|// ACRONYM omitted, use ABBR
block|}
comment|/** Part of %pre.exclusion */
DECL|interface|_ImgObject
specifier|public
interface|interface
name|_ImgObject
extends|extends
name|_Object
extends|,
name|_Child
block|{
comment|/**      * Add a IMG (image) element.      * @return a new IMG element builder      */
DECL|method|img ()
name|IMG
name|img
parameter_list|()
function_decl|;
comment|/**      * Add a IMG (image) element.      * @param src the source URL of the image      * @return the current element builder      */
DECL|method|img (String src)
name|_ImgObject
name|img
parameter_list|(
name|String
name|src
parameter_list|)
function_decl|;
block|}
comment|/** Part of %pre.exclusion */
DECL|interface|_SubSup
specifier|public
interface|interface
name|_SubSup
extends|extends
name|_Child
block|{
comment|/**      * Add a SUB (subscript) element.      * @return a new SUB element builder      */
DECL|method|sub ()
name|SUB
name|sub
parameter_list|()
function_decl|;
comment|/**      * Add a complete SUB (subscript) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|sub (String cdata)
name|_SubSup
name|sub
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a complete SUB (subscript) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|sub (String selector, String cdata)
name|_SubSup
name|sub
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a SUP (superscript) element.      * @return a new SUP element builder      */
DECL|method|sup ()
name|SUP
name|sup
parameter_list|()
function_decl|;
comment|/**      * Add a SUP (superscript) element.      * @param cdata the content      * @return the current element builder      */
DECL|method|sup (String cdata)
name|_SubSup
name|sup
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a SUP (superscript) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|sup (String selector, String cdata)
name|_SubSup
name|sup
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Anchor
specifier|public
interface|interface
name|_Anchor
block|{
comment|/**      * Add a A (anchor) element.      * @return a new A element builder      */
DECL|method|a ()
name|A
name|a
parameter_list|()
function_decl|;
comment|/**      * Add a A (anchor) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new A element builder      */
DECL|method|a (String selector)
name|A
name|a
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/** Shortcut for<code>a().$href(href).__(anchorText).__();</code>      * @param href the URI      * @param anchorText for the URI      * @return the current element builder      */
DECL|method|a (String href, String anchorText)
name|_Anchor
name|a
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|anchorText
parameter_list|)
function_decl|;
comment|/** Shortcut for<code>a(selector).$href(href).__(anchorText).__();</code>      * @param selector in the form of (#id)?(.class)*      * @param href the URI      * @param anchorText for the URI      * @return the current element builder      */
DECL|method|a (String selector, String href, String anchorText)
name|_Anchor
name|a
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|href
parameter_list|,
name|String
name|anchorText
parameter_list|)
function_decl|;
block|}
comment|/**    * INS and DEL are unusual for HTML    * "in that they may serve as either block-level or inline elements    * (but not both)".    *<br>cf. http://www.w3.org/TR/html4/struct/text.html#h-9.4    *<br>cf. http://www.w3.org/TR/html5/edits.html#edits    */
DECL|interface|_InsDel
specifier|public
interface|interface
name|_InsDel
block|{
comment|/**      * Add an INS (insert) element.      * @return an INS element builder      */
DECL|method|ins ()
name|INS
name|ins
parameter_list|()
function_decl|;
comment|/**      * Add a complete INS element.      * @param cdata inserted data      * @return the current element builder      */
DECL|method|ins (String cdata)
name|_InsDel
name|ins
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a DEL (delete) element.      * @return a DEL element builder      */
DECL|method|del ()
name|DEL
name|del
parameter_list|()
function_decl|;
comment|/**      * Add a complete DEL element.      * @param cdata deleted data      * @return the current element builder      */
DECL|method|del (String cdata)
name|_InsDel
name|del
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/** %special -(A|%pre.exclusion) */
DECL|interface|_Special
specifier|public
interface|interface
name|_Special
extends|extends
name|_Script
extends|,
name|_InsDel
block|{
comment|/**      * Add a BR (line break) element.      * @return a new BR element builder      */
DECL|method|br ()
name|BR
name|br
parameter_list|()
function_decl|;
comment|/**      * Add a BR (line break) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return the current element builder      */
DECL|method|br (String selector)
name|_Special
name|br
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a MAP element.      * @return a new MAP element builder      */
DECL|method|map ()
name|MAP
name|map
parameter_list|()
function_decl|;
comment|/**      * Add a MAP element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new MAP element builder      */
DECL|method|map (String selector)
name|MAP
name|map
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a Q (inline quotation) element.      * @return a q (inline quotation) element builder      */
DECL|method|q ()
name|Q
name|q
parameter_list|()
function_decl|;
comment|/**      * Add a complete Q element.      * @param cdata the content      * @return the current element builder      */
DECL|method|q (String cdata)
name|_Special
name|q
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a Q element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|q (String selector, String cdata)
name|_Special
name|q
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a SPAN element.      * @return a new SPAN element builder      */
DECL|method|span ()
name|SPAN
name|span
parameter_list|()
function_decl|;
comment|/**      * Add a SPAN element.      * @param cdata the content      * @return the current element builder      */
DECL|method|span (String cdata)
name|_Special
name|span
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a SPAN element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|span (String selector, String cdata)
name|_Special
name|span
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a bdo (bidirectional override) element      * @return a bdo element builder      */
DECL|method|bdo ()
name|BDO
name|bdo
parameter_list|()
function_decl|;
comment|/**      * Add a bdo (bidirectional override) element      * @param dir the direction of the text      * @param cdata the text      * @return the current element builder      */
DECL|method|bdo (Dir dir, String cdata)
name|_Special
name|bdo
parameter_list|(
name|Dir
name|dir
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/** %special */
DECL|interface|Special
specifier|public
interface|interface
name|Special
extends|extends
name|_Anchor
extends|,
name|_ImgObject
extends|,
name|_SubSup
extends|,
name|_Special
block|{   }
comment|/**    *    */
DECL|interface|_Label
specifier|public
interface|interface
name|_Label
extends|extends
name|_Child
block|{
comment|/**      * Add a LABEL element.      * @return a new LABEL element builder      */
DECL|method|label ()
name|LABEL
name|label
parameter_list|()
function_decl|;
comment|/**      * Add a LABEL element.      * Shortcut of<code>label().$for(forId).__(cdata).__();</code>      * @param forId the for attribute      * @param cdata the content      * @return the current element builder      */
DECL|method|label (String forId, String cdata)
name|_Label
name|label
parameter_list|(
name|String
name|forId
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_FormCtrl
specifier|public
interface|interface
name|_FormCtrl
block|{
comment|/**      * Add a INPUT element.      * @return a new INPUT element builder      */
DECL|method|input ()
name|INPUT
name|input
parameter_list|()
function_decl|;
comment|/**      * Add a INPUT element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new INPUT element builder      */
DECL|method|input (String selector)
name|INPUT
name|input
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a SELECT element.      * @return a new SELECT element builder      */
DECL|method|select ()
name|SELECT
name|select
parameter_list|()
function_decl|;
comment|/**      * Add a SELECT element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new SELECT element builder      */
DECL|method|select (String selector)
name|SELECT
name|select
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a TEXTAREA element.      * @return a new TEXTAREA element builder      */
DECL|method|textarea ()
name|TEXTAREA
name|textarea
parameter_list|()
function_decl|;
comment|/**      * Add a TEXTAREA element.      * @param selector      * @return a new TEXTAREA element builder      */
DECL|method|textarea (String selector)
name|TEXTAREA
name|textarea
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a complete TEXTAREA element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|textarea (String selector, String cdata)
name|_FormCtrl
name|textarea
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a BUTTON element.      * @return a new BUTTON element builder      */
DECL|method|button ()
name|BUTTON
name|button
parameter_list|()
function_decl|;
comment|/**      * Add a BUTTON element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new BUTTON element builder      */
DECL|method|button (String selector)
name|BUTTON
name|button
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a complete BUTTON element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|button (String selector, String cdata)
name|_FormCtrl
name|button
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/** %formctrl */
DECL|interface|FormCtrl
specifier|public
interface|interface
name|FormCtrl
extends|extends
name|_Label
extends|,
name|_FormCtrl
block|{   }
comment|/**    *    */
DECL|interface|_Content
specifier|public
interface|interface
name|_Content
extends|extends
name|_Child
block|{
comment|/**      * Content of the element      * @param lines of content      * @return the current element builder      */
DECL|method|__ (Object... lines)
name|_Content
name|__
parameter_list|(
name|Object
modifier|...
name|lines
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_RawContent
specifier|public
interface|interface
name|_RawContent
extends|extends
name|_Child
block|{
comment|/**      * Raw (no need to be HTML escaped) content      * @param lines of content      * @return the current element builder      */
DECL|method|_r (Object... lines)
name|_RawContent
name|_r
parameter_list|(
name|Object
modifier|...
name|lines
parameter_list|)
function_decl|;
block|}
comment|/** #PCDATA */
DECL|interface|PCData
specifier|public
interface|interface
name|PCData
extends|extends
name|_Content
extends|,
name|_RawContent
block|{   }
comment|/** %inline */
DECL|interface|Inline
specifier|public
interface|interface
name|Inline
extends|extends
name|PCData
extends|,
name|FontStyle
extends|,
name|Phrase
extends|,
name|Special
extends|,
name|FormCtrl
block|{   }
comment|/**    *    */
DECL|interface|I
specifier|public
interface|interface
name|I
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|B
specifier|public
interface|interface
name|B
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|SMALL
specifier|public
interface|interface
name|SMALL
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|EM
specifier|public
interface|interface
name|EM
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|STRONG
specifier|public
interface|interface
name|STRONG
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|DFN
specifier|public
interface|interface
name|DFN
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|CODE
specifier|public
interface|interface
name|CODE
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|SAMP
specifier|public
interface|interface
name|SAMP
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|KBD
specifier|public
interface|interface
name|KBD
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|VAR
specifier|public
interface|interface
name|VAR
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|CITE
specifier|public
interface|interface
name|CITE
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|ABBR
specifier|public
interface|interface
name|ABBR
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|ACRONYM
specifier|public
interface|interface
name|ACRONYM
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|SUB
specifier|public
interface|interface
name|SUB
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|SUP
specifier|public
interface|interface
name|SUP
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|SPAN
specifier|public
interface|interface
name|SPAN
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/** The dir attribute is required for the BDO element */
DECL|interface|BDO
specifier|public
interface|interface
name|BDO
extends|extends
name|CoreAttrs
extends|,
name|I18nAttrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|BR
specifier|public
interface|interface
name|BR
extends|extends
name|CoreAttrs
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|_Form
specifier|public
interface|interface
name|_Form
block|{
comment|/**      * Add a FORM element.      * @return a new FORM element builder      */
DECL|method|form ()
name|FORM
name|form
parameter_list|()
function_decl|;
comment|/**      * Add a FORM element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new FORM element builder      */
DECL|method|form (String selector)
name|FORM
name|form
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_FieldSet
specifier|public
interface|interface
name|_FieldSet
block|{
comment|/**      * Add a FIELDSET element.      * @return a new FIELDSET element builder      */
DECL|method|fieldset ()
name|FIELDSET
name|fieldset
parameter_list|()
function_decl|;
comment|/**      * Add a FIELDSET element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new FIELDSET element builder      */
DECL|method|fieldset (String selector)
name|FIELDSET
name|fieldset
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|/** %block -(FORM|FIELDSET) */
DECL|interface|_Block
specifier|public
interface|interface
name|_Block
extends|extends
name|Heading
extends|,
name|Listing
extends|,
name|Preformatted
block|{
comment|/**      * Add a P (paragraph) element.      * @return a new P element builder      */
DECL|method|p ()
name|P
name|p
parameter_list|()
function_decl|;
comment|/**      * Add a P (paragraph) element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new P element builder      */
DECL|method|p (String selector)
name|P
name|p
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a DL (description list) element.      * @return a new DL element builder      */
DECL|method|dl ()
name|DL
name|dl
parameter_list|()
function_decl|;
comment|/**      * Add a DL element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new DL element builder      */
DECL|method|dl (String selector)
name|DL
name|dl
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a DIV element.      * @return a new DIV element builder      */
DECL|method|div ()
name|DIV
name|div
parameter_list|()
function_decl|;
comment|/**      * Add a DIV element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new DIV element builder      */
DECL|method|div (String selector)
name|DIV
name|div
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|// NOSCRIPT omitted
comment|// cf. http://www.w3.org/html/wg/tracker/issues/117
comment|/**      * Add a BLOCKQUOTE element.      * @return a new BLOCKQUOTE element builder      */
DECL|method|blockquote ()
name|BLOCKQUOTE
name|blockquote
parameter_list|()
function_decl|;
comment|/**      * Alias of blockquote      * @return a new BLOCKQUOTE element builder      */
DECL|method|bq ()
name|BLOCKQUOTE
name|bq
parameter_list|()
function_decl|;
comment|/**      * Add a HR (horizontal rule) element.      * @return a new HR element builder      */
DECL|method|hr ()
name|HR
name|hr
parameter_list|()
function_decl|;
comment|/**      * Add a HR element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new HR element builder      */
DECL|method|hr (String selector)
name|_Block
name|hr
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a TABLE element.      * @return a new TABLE element builder      */
DECL|method|table ()
name|TABLE
name|table
parameter_list|()
function_decl|;
comment|/**      * Add a TABLE element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new TABLE element builder      */
DECL|method|table (String selector)
name|TABLE
name|table
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a ADDRESS element.      * @return a new ADDRESS element builder      */
DECL|method|address ()
name|ADDRESS
name|address
parameter_list|()
function_decl|;
comment|/**      * Add a complete ADDRESS element.      * @param cdata the content      * @return the current element builder      */
DECL|method|address (String cdata)
name|_Block
name|address
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Embed a sub-view.      * @param cls the sub-view class      * @return the current element builder      */
DECL|method|__ (Class<? extends SubView> cls)
name|_Block
name|__
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|cls
parameter_list|)
function_decl|;
block|}
comment|/** %block */
DECL|interface|Block
specifier|public
interface|interface
name|Block
extends|extends
name|_Block
extends|,
name|_Form
extends|,
name|_FieldSet
block|{   }
comment|/** %flow */
DECL|interface|Flow
specifier|public
interface|interface
name|Flow
extends|extends
name|Block
extends|,
name|Inline
block|{   }
comment|/**    *    */
DECL|interface|_Body
specifier|public
interface|interface
name|_Body
extends|extends
name|Block
extends|,
name|_Script
extends|,
name|_InsDel
block|{   }
comment|/**    *    */
DECL|interface|BODY
specifier|public
interface|interface
name|BODY
extends|extends
name|Attrs
extends|,
name|_Body
extends|,
name|_Child
block|{
comment|/**      * The document has been loaded.      * @param script to invoke      * @return the current element builder      */
DECL|method|$onload (String script)
name|BODY
name|$onload
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/**      * The document has been removed      * @param script to invoke      * @return the current element builder      */
DECL|method|$onunload (String script)
name|BODY
name|$onunload
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|ADDRESS
specifier|public
interface|interface
name|ADDRESS
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|DIV
specifier|public
interface|interface
name|DIV
extends|extends
name|Attrs
extends|,
name|Flow
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|A
specifier|public
interface|interface
name|A
extends|extends
name|Attrs
extends|,
name|_Child
extends|,
comment|/* %inline -(A) */
name|PCData
extends|,
name|FontStyle
extends|,
name|Phrase
extends|,
name|_ImgObject
extends|,
name|_Special
extends|,
name|_SubSup
extends|,
name|FormCtrl
block|{
comment|// $charset omitted.
comment|/** advisory content type      * @param cdata the content-type      * @return the current element builder      */
DECL|method|$type (String cdata)
name|A
name|$type
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|// $name omitted. use id instead.
comment|/** URI for linked resource      * @param uri the URI      * @return the current element builder      */
DECL|method|$href (String uri)
name|A
name|$href
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** language code      * @param cdata the code      * @return the current element builder      */
DECL|method|$hreflang (String cdata)
name|A
name|$hreflang
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** forward link types      * @param linkTypes the types      * @return the current element builder      */
DECL|method|$rel (EnumSet<LinkType> linkTypes)
name|A
name|$rel
parameter_list|(
name|EnumSet
argument_list|<
name|LinkType
argument_list|>
name|linkTypes
parameter_list|)
function_decl|;
comment|/**      * forward link types      * @param linkTypes space-separated list of link types      * @return the current element builder.      */
DECL|method|$rel (String linkTypes)
name|A
name|$rel
parameter_list|(
name|String
name|linkTypes
parameter_list|)
function_decl|;
comment|// $rev omitted. Instead of rev="made", use rel="author"
comment|/** accessibility key character      * @param cdata the key      * @return the current element builder      */
DECL|method|$accesskey (String cdata)
name|A
name|$accesskey
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|// $shape and coords omitted. use area instead of a for image maps.
comment|/** position in tabbing order      * @param index the index      * @return the current element builder      */
DECL|method|$tabindex (int index)
name|A
name|$tabindex
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** the element got the focus      * @param script to invoke      * @return the current element builder      */
DECL|method|$onfocus (String script)
name|A
name|$onfocus
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element lost the focus      * @param script to invoke      * @return the current element builder      */
DECL|method|$onblur (String script)
name|A
name|$onblur
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|MAP
specifier|public
interface|interface
name|MAP
extends|extends
name|Attrs
extends|,
name|Block
extends|,
name|_Child
block|{
comment|/**      * Add a AREA element.      * @return a new AREA element builder      */
DECL|method|area ()
name|AREA
name|area
parameter_list|()
function_decl|;
comment|/**      * Add a AREA element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new AREA element builder      */
DECL|method|area (String selector)
name|AREA
name|area
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/** for reference by usemap      * @param name of the map      * @return the current element builder      */
DECL|method|$name (String name)
name|MAP
name|$name
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|AREA
specifier|public
interface|interface
name|AREA
extends|extends
name|Attrs
extends|,
name|_Child
block|{
comment|/** controls interpretation of coords      * @param shape of the area      * @return the current element builder      */
DECL|method|$shape (Shape shape)
name|AREA
name|$shape
parameter_list|(
name|Shape
name|shape
parameter_list|)
function_decl|;
comment|/** comma-separated list of lengths      * @param cdata coords of the area      * @return the current element builder      */
DECL|method|$coords (String cdata)
name|AREA
name|$coords
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** URI for linked resource      * @param uri the URI      * @return the current element builder      */
DECL|method|$href (String uri)
name|AREA
name|$href
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|// $nohref omitted./
comment|/** short description      * @param desc the description      * @return the current element builder      */
DECL|method|$alt (String desc)
name|AREA
name|$alt
parameter_list|(
name|String
name|desc
parameter_list|)
function_decl|;
comment|/** position in tabbing order      * @param index of the order      * @return the current element builder      */
DECL|method|$tabindex (int index)
name|AREA
name|$tabindex
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** accessibility key character      * @param cdata the key      * @return the current element builder      */
DECL|method|$accesskey (String cdata)
name|AREA
name|$accesskey
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** the element got the focus      * @param script to invoke      * @return the current element builder      */
DECL|method|$onfocus (String script)
name|AREA
name|$onfocus
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element lost the focus      * @param script to invoke      * @return the current element builder      */
DECL|method|$onblur (String script)
name|AREA
name|$onblur
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|LINK
specifier|public
interface|interface
name|LINK
extends|extends
name|Attrs
extends|,
name|_Child
block|{
comment|// $charset omitted
comment|/** URI for linked resource      * @param uri the URI      * @return the current element builder      */
DECL|method|$href (String uri)
name|LINK
name|$href
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** language code      * @param cdata the code      * @return the current element builder      */
DECL|method|$hreflang (String cdata)
name|LINK
name|$hreflang
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** advisory content type      * @param cdata the type      * @return the current element builder      */
DECL|method|$type (String cdata)
name|LINK
name|$type
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** forward link types      * @param linkTypes the types      * @return the current element builder      */
DECL|method|$rel (EnumSet<LinkType> linkTypes)
name|LINK
name|$rel
parameter_list|(
name|EnumSet
argument_list|<
name|LinkType
argument_list|>
name|linkTypes
parameter_list|)
function_decl|;
comment|/**      * forward link types.      * @param linkTypes space-separated link types      * @return the current element builder      */
DECL|method|$rel (String linkTypes)
name|LINK
name|$rel
parameter_list|(
name|String
name|linkTypes
parameter_list|)
function_decl|;
comment|// $rev omitted. Instead of rev="made", use rel="author"
comment|/** for rendering on these media      * @param mediaTypes the media types      * @return the current element builder      */
DECL|method|$media (EnumSet<Media> mediaTypes)
name|LINK
name|$media
parameter_list|(
name|EnumSet
argument_list|<
name|Media
argument_list|>
name|mediaTypes
parameter_list|)
function_decl|;
comment|/**      * for rendering on these media.      * @param mediaTypes comma-separated list of media      * @return the current element builder      */
DECL|method|$media (String mediaTypes)
name|LINK
name|$media
parameter_list|(
name|String
name|mediaTypes
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|IMG
specifier|public
interface|interface
name|IMG
extends|extends
name|Attrs
extends|,
name|_Child
block|{
comment|/** URI of image to embed      * @param uri the URI      * @return the current element builder      */
DECL|method|$src (String uri)
name|IMG
name|$src
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** short description      * @param desc the description      * @return the current element builder      */
DECL|method|$alt (String desc)
name|IMG
name|$alt
parameter_list|(
name|String
name|desc
parameter_list|)
function_decl|;
comment|// $longdesc omitted. use<a...><img..></a> instead
comment|// $name omitted. use id instead.
comment|/** override height      * @param pixels the height      * @return the current element builder      */
DECL|method|$height (int pixels)
name|IMG
name|$height
parameter_list|(
name|int
name|pixels
parameter_list|)
function_decl|;
comment|/**      * override height      * @param cdata the height (can use %, * etc.)      * @return the current element builder      */
DECL|method|$height (String cdata)
name|IMG
name|$height
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** override width      * @param pixels the width      * @return the current element builder      */
DECL|method|$width (int pixels)
name|IMG
name|$width
parameter_list|(
name|int
name|pixels
parameter_list|)
function_decl|;
comment|/**      * override width      * @param cdata the width (can use %, * etc.)      * @return the current element builder      */
DECL|method|$width (String cdata)
name|IMG
name|$width
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** use client-side image map      * @param uri the URI      * @return the current element builder      */
DECL|method|$usemap (String uri)
name|IMG
name|$usemap
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** use server-side image map      * @return the current element builder      */
DECL|method|$ismap ()
name|IMG
name|$ismap
parameter_list|()
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Param
specifier|public
interface|interface
name|_Param
extends|extends
name|_Child
block|{
comment|/**      * Add a PARAM (parameter) element.      * @return a new PARAM element builder      */
DECL|method|param ()
name|PARAM
name|param
parameter_list|()
function_decl|;
comment|/**      * Add a PARAM element.      * Shortcut of<code>param().$name(name).$value(value).__();</code>      * @param name of the value      * @param value the value      * @return the current element builder      */
DECL|method|param (String name, String value)
name|_Param
name|param
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|OBJECT
specifier|public
interface|interface
name|OBJECT
extends|extends
name|Attrs
extends|,
name|_Param
extends|,
name|Flow
extends|,
name|_Child
block|{
comment|// $declare omitted. repeat element completely
comment|// $archive, classid, codebase, codetype ommited. use data and type
comment|/** reference to object's data      * @param uri the URI      * @return the current element builder      */
DECL|method|$data (String uri)
name|OBJECT
name|$data
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** content type for data      * @param contentType the type      * @return the current element builder      */
DECL|method|$type (String contentType)
name|OBJECT
name|$type
parameter_list|(
name|String
name|contentType
parameter_list|)
function_decl|;
comment|// $standby omitted. fix the resource instead.
comment|/** override height      * @param pixels the height      * @return the current element builder      */
DECL|method|$height (int pixels)
name|OBJECT
name|$height
parameter_list|(
name|int
name|pixels
parameter_list|)
function_decl|;
comment|/**      * override height      * @param length the height (can use %, *)      * @return the current element builder      */
DECL|method|$height (String length)
name|OBJECT
name|$height
parameter_list|(
name|String
name|length
parameter_list|)
function_decl|;
comment|/** override width      * @param pixels the width      * @return the current element builder      */
DECL|method|$width (int pixels)
name|OBJECT
name|$width
parameter_list|(
name|int
name|pixels
parameter_list|)
function_decl|;
comment|/**      * override width      * @param length the height (can use %, *)      * @return the current element builder      */
DECL|method|$width (String length)
name|OBJECT
name|$width
parameter_list|(
name|String
name|length
parameter_list|)
function_decl|;
comment|/** use client-side image map      * @param uri the URI/name of the map      * @return the current element builder      */
DECL|method|$usemap (String uri)
name|OBJECT
name|$usemap
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** submit as part of form      * @param cdata the name of the object      * @return the current element builder      */
DECL|method|$name (String cdata)
name|OBJECT
name|$name
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** position in tabbing order      * @param index of the order      * @return the current element builder      */
DECL|method|$tabindex (int index)
name|OBJECT
name|$tabindex
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|PARAM
specifier|public
interface|interface
name|PARAM
block|{
comment|/** document-wide unique id      * @param cdata the id      * @return the current element builder      */
DECL|method|$id (String cdata)
name|PARAM
name|$id
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** property name. Required.      * @param cdata the name      * @return the current element builder      */
DECL|method|$name (String cdata)
name|PARAM
name|$name
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** property value      * @param cdata the value      * @return the current element builder      */
DECL|method|$value (String cdata)
name|PARAM
name|$value
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|// $type and valuetype omitted
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|HR
specifier|public
interface|interface
name|HR
extends|extends
name|Attrs
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|P
specifier|public
interface|interface
name|P
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|H1
specifier|public
interface|interface
name|H1
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|H2
specifier|public
interface|interface
name|H2
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|H3
specifier|public
interface|interface
name|H3
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|H4
specifier|public
interface|interface
name|H4
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|H5
specifier|public
interface|interface
name|H5
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|H6
specifier|public
interface|interface
name|H6
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|PRE
specifier|public
interface|interface
name|PRE
extends|extends
name|Attrs
extends|,
name|_Child
extends|,
comment|/* (%inline;)* -(%pre.exclusion) */
name|PCData
extends|,
name|_FontStyle
extends|,
name|Phrase
extends|,
name|_Anchor
extends|,
name|_Special
extends|,
name|FormCtrl
block|{   }
comment|/**    *    */
DECL|interface|Q
specifier|public
interface|interface
name|Q
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{
comment|/** URI for source document or msg      * @param uri the URI      * @return the current element builder      */
DECL|method|$cite (String uri)
name|Q
name|$cite
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|BLOCKQUOTE
specifier|public
interface|interface
name|BLOCKQUOTE
extends|extends
name|Attrs
extends|,
name|Block
extends|,
name|_Script
extends|,
name|_Child
block|{
comment|/** URI for source document or msg      * @param uri the URI      * @return the current element builder      */
DECL|method|$cite (String uri)
name|BLOCKQUOTE
name|$cite
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
block|}
comment|/**    * @see _InsDel INS/DEL quirks.    */
DECL|interface|INS
specifier|public
interface|interface
name|INS
extends|extends
name|Attrs
extends|,
name|Flow
extends|,
name|_Child
block|{
comment|/** info on reason for change      * @param uri      * @return the current element builder      */
DECL|method|$cite (String uri)
name|INS
name|$cite
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** date and time of change      * @param datetime      * @return the current element builder      */
DECL|method|$datetime (String datetime)
name|INS
name|$datetime
parameter_list|(
name|String
name|datetime
parameter_list|)
function_decl|;
block|}
comment|/**    * @see _InsDel INS/DEL quirks.    */
DECL|interface|DEL
specifier|public
interface|interface
name|DEL
extends|extends
name|Attrs
extends|,
name|Flow
extends|,
name|_Child
block|{
comment|/** info on reason for change      * @param uri the info URI      * @return the current element builder      */
DECL|method|$cite (String uri)
name|DEL
name|$cite
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** date and time of change      * @param datetime the time      * @return the current element builder      */
DECL|method|$datetime (String datetime)
name|DEL
name|$datetime
parameter_list|(
name|String
name|datetime
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Dl
specifier|public
interface|interface
name|_Dl
extends|extends
name|_Child
block|{
comment|/**      * Add a DT (term of the item) element.      * @return a new DT element builder      */
DECL|method|dt ()
name|DT
name|dt
parameter_list|()
function_decl|;
comment|/**      * Add a complete DT element.      * @param cdata the content      * @return the current element builder      */
DECL|method|dt (String cdata)
name|_Dl
name|dt
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a DD (definition/description) element.      * @return a new DD element builder      */
DECL|method|dd ()
name|DD
name|dd
parameter_list|()
function_decl|;
comment|/**      * Add a complete DD element.      * @param cdata the content      * @return the current element builder      */
DECL|method|dd (String cdata)
name|_Dl
name|dd
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|DL
specifier|public
interface|interface
name|DL
extends|extends
name|Attrs
extends|,
name|_Dl
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|DT
specifier|public
interface|interface
name|DT
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|DD
specifier|public
interface|interface
name|DD
extends|extends
name|Attrs
extends|,
name|Flow
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|_Li
specifier|public
interface|interface
name|_Li
extends|extends
name|_Child
block|{
comment|/**      * Add a LI (list item) element.      * @return a new LI element builder      */
DECL|method|li ()
name|LI
name|li
parameter_list|()
function_decl|;
comment|/**      * Add a LI element.      * @param cdata the content      * @return the current element builder      */
DECL|method|li (String cdata)
name|_Li
name|li
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|OL
specifier|public
interface|interface
name|OL
extends|extends
name|Attrs
extends|,
name|_Li
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|UL
specifier|public
interface|interface
name|UL
extends|extends
name|Attrs
extends|,
name|_Li
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|LI
specifier|public
interface|interface
name|LI
extends|extends
name|Attrs
extends|,
name|Flow
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|FORM
specifier|public
interface|interface
name|FORM
extends|extends
name|Attrs
extends|,
name|_Child
extends|,
comment|/* (%block;|SCRIPT)+ -(FORM) */
name|_Script
extends|,
name|_Block
extends|,
name|_FieldSet
block|{
comment|/** server-side form handler      * @param uri      * @return the current element builder      */
DECL|method|$action (String uri)
name|FORM
name|$action
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** HTTP method used to submit the form      * @param method      * @return the current element builder      */
DECL|method|$method (Method method)
name|FORM
name|$method
parameter_list|(
name|Method
name|method
parameter_list|)
function_decl|;
comment|/**      * contentype for "POST" method.      * The default is "application/x-www-form-urlencoded".      * Use "multipart/form-data" for input type=file      * @param enctype      * @return the current element builder      */
DECL|method|$enctype (String enctype)
name|FORM
name|$enctype
parameter_list|(
name|String
name|enctype
parameter_list|)
function_decl|;
comment|/** list of MIME types for file upload      * @param cdata      * @return the current element builder      */
DECL|method|$accept (String cdata)
name|FORM
name|$accept
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** name of form for scripting      * @param cdata      * @return the current element builder      */
DECL|method|$name (String cdata)
name|FORM
name|$name
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** the form was submitted      * @param script      * @return the current element builder      */
DECL|method|$onsubmit (String script)
name|FORM
name|$onsubmit
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the form was reset      * @param script      * @return the current element builder      */
DECL|method|$onreset (String script)
name|FORM
name|$onreset
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** (space and/or comma separated) list of supported charsets      * @param cdata      * @return the current element builder      */
DECL|method|$accept_charset (String cdata)
name|FORM
name|$accept_charset
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|LABEL
specifier|public
interface|interface
name|LABEL
extends|extends
name|Attrs
extends|,
name|_Child
extends|,
comment|/* (%inline;)* -(LABEL) */
name|PCData
extends|,
name|FontStyle
extends|,
name|Phrase
extends|,
name|Special
extends|,
name|_FormCtrl
block|{
comment|/** matches field ID value      * @param cdata      * @return the current element builder      */
DECL|method|$for (String cdata)
name|LABEL
name|$for
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** accessibility key character      * @param cdata      * @return the current element builder      */
DECL|method|$accesskey (String cdata)
name|LABEL
name|$accesskey
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** the element got the focus      * @param script      * @return the current element builder      */
DECL|method|$onfocus (String script)
name|LABEL
name|$onfocus
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element lost the focus      * @param script      * @return the current element builder      */
DECL|method|$onblur (String script)
name|LABEL
name|$onblur
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|INPUT
specifier|public
interface|interface
name|INPUT
extends|extends
name|Attrs
extends|,
name|_Child
block|{
comment|/** what kind of widget is needed. default is "text".      * @param inputType      * @return the current element builder      */
DECL|method|$type (InputType inputType)
name|INPUT
name|$type
parameter_list|(
name|InputType
name|inputType
parameter_list|)
function_decl|;
comment|/** submit as part of form      * @param cdata      * @return the current element builder      */
DECL|method|$name (String cdata)
name|INPUT
name|$name
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** Specify for radio buttons and checkboxes      * @param cdata      * @return the current element builder      */
DECL|method|$value (String cdata)
name|INPUT
name|$value
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** for radio buttons and check boxes      * @return the current element builder      */
DECL|method|$checked ()
name|INPUT
name|$checked
parameter_list|()
function_decl|;
comment|/** unavailable in this context      * @return the current element builder      */
DECL|method|$disabled ()
name|INPUT
name|$disabled
parameter_list|()
function_decl|;
comment|/** for text and passwd      * @return the current element builder      */
DECL|method|$readonly ()
name|INPUT
name|$readonly
parameter_list|()
function_decl|;
comment|/** specific to each type of field      * @param cdata      * @return the current element builder      */
DECL|method|$size (String cdata)
name|INPUT
name|$size
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** max chars for text fields      * @param length      * @return the current element builder      */
DECL|method|$maxlength (int length)
name|INPUT
name|$maxlength
parameter_list|(
name|int
name|length
parameter_list|)
function_decl|;
comment|/** for fields with images      * @param uri      * @return the current element builder      */
DECL|method|$src (String uri)
name|INPUT
name|$src
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** short description      * @param cdata      * @return the current element builder      */
DECL|method|$alt (String cdata)
name|INPUT
name|$alt
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|// $usemap omitted. use img instead of input for image maps.
comment|/** use server-side image map      * @return the current element builder      */
DECL|method|$ismap ()
name|INPUT
name|$ismap
parameter_list|()
function_decl|;
comment|/** position in tabbing order      * @param index      * @return the current element builder      */
DECL|method|$tabindex (int index)
name|INPUT
name|$tabindex
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** accessibility key character      * @param cdata      * @return the current element builder      */
DECL|method|$accesskey (String cdata)
name|INPUT
name|$accesskey
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** the element got the focus      * @param script      * @return the current element builder      */
DECL|method|$onfocus (String script)
name|INPUT
name|$onfocus
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element lost the focus      * @param script      * @return the current element builder      */
DECL|method|$onblur (String script)
name|INPUT
name|$onblur
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** some text was selected      * @param script      * @return the current element builder      */
DECL|method|$onselect (String script)
name|INPUT
name|$onselect
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element value was changed      * @param script      * @return the current element builder      */
DECL|method|$onchange (String script)
name|INPUT
name|$onchange
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** list of MIME types for file upload (csv)      * @param contentTypes      * @return the current element builder      */
DECL|method|$accept (String contentTypes)
name|INPUT
name|$accept
parameter_list|(
name|String
name|contentTypes
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Option
specifier|public
interface|interface
name|_Option
extends|extends
name|_Child
block|{
comment|/**      * Add a OPTION element.      * @return a new OPTION element builder      */
DECL|method|option ()
name|OPTION
name|option
parameter_list|()
function_decl|;
comment|/**      * Add a complete OPTION element.      * @param cdata the content      * @return the current element builder      */
DECL|method|option (String cdata)
name|_Option
name|option
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|SELECT
specifier|public
interface|interface
name|SELECT
extends|extends
name|Attrs
extends|,
name|_Option
extends|,
name|_Child
block|{
comment|/**      * Add a OPTGROUP element.      * @return a new OPTGROUP element builder      */
DECL|method|optgroup ()
name|OPTGROUP
name|optgroup
parameter_list|()
function_decl|;
comment|/** field name      * @param cdata      * @return the current element builder      */
DECL|method|$name (String cdata)
name|SELECT
name|$name
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** rows visible      * @param rows      * @return the current element builder      */
DECL|method|$size (int rows)
name|SELECT
name|$size
parameter_list|(
name|int
name|rows
parameter_list|)
function_decl|;
comment|/** default is single selection      * @return the current element builder      */
DECL|method|$multiple ()
name|SELECT
name|$multiple
parameter_list|()
function_decl|;
comment|/** unavailable in this context      * @return the current element builder      */
DECL|method|$disabled ()
name|SELECT
name|$disabled
parameter_list|()
function_decl|;
comment|/** position in tabbing order      * @param index      * @return the current element builder      */
DECL|method|$tabindex (int index)
name|SELECT
name|$tabindex
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** the element got the focus      * @param script      * @return the current element builder      */
DECL|method|$onfocus (String script)
name|SELECT
name|$onfocus
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element lost the focus      * @param script      * @return the current element builder      */
DECL|method|$onblur (String script)
name|SELECT
name|$onblur
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element value was changed      * @param script      * @return the current element builder      */
DECL|method|$onchange (String script)
name|SELECT
name|$onchange
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|OPTGROUP
specifier|public
interface|interface
name|OPTGROUP
extends|extends
name|Attrs
extends|,
name|_Option
extends|,
name|_Child
block|{
comment|/** unavailable in this context      * @return the current element builder      */
DECL|method|$disabled ()
name|OPTGROUP
name|$disabled
parameter_list|()
function_decl|;
comment|/** for use in hierarchical menus      * @param cdata      * @return the current element builder      */
DECL|method|$label (String cdata)
name|OPTGROUP
name|$label
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|OPTION
specifier|public
interface|interface
name|OPTION
extends|extends
name|Attrs
extends|,
name|PCData
extends|,
name|_Child
block|{
comment|/** currently selected option      * @return the current element builder      */
DECL|method|$selected ()
name|OPTION
name|$selected
parameter_list|()
function_decl|;
comment|/** unavailable in this context      * @return the current element builder      */
DECL|method|$disabled ()
name|OPTION
name|$disabled
parameter_list|()
function_decl|;
comment|/** for use in hierarchical menus      * @param cdata      * @return the current element builder      */
DECL|method|$label (String cdata)
name|OPTION
name|$label
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** defaults to element content      * @param cdata      * @return the current element builder      */
DECL|method|$value (String cdata)
name|OPTION
name|$value
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|TEXTAREA
specifier|public
interface|interface
name|TEXTAREA
extends|extends
name|Attrs
extends|,
name|PCData
extends|,
name|_Child
block|{
comment|/** variable name for the text      * @param cdata      * @return the current element builder      */
DECL|method|$name (String cdata)
name|TEXTAREA
name|$name
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** visible rows      * @param rows      * @return the current element builder      */
DECL|method|$rows (int rows)
name|TEXTAREA
name|$rows
parameter_list|(
name|int
name|rows
parameter_list|)
function_decl|;
comment|/** visible columns      * @param cols      * @return the current element builder      */
DECL|method|$cols (int cols)
name|TEXTAREA
name|$cols
parameter_list|(
name|int
name|cols
parameter_list|)
function_decl|;
comment|/** unavailable in this context      * @return the current element builder      */
DECL|method|$disabled ()
name|TEXTAREA
name|$disabled
parameter_list|()
function_decl|;
comment|/** text is readonly      * @return the current element builder      */
DECL|method|$readonly ()
name|TEXTAREA
name|$readonly
parameter_list|()
function_decl|;
comment|/** position in tabbing order      * @param index      * @return the current element builder      */
DECL|method|$tabindex (int index)
name|TEXTAREA
name|$tabindex
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** accessibility key character      * @param cdata      * @return the current element builder      */
DECL|method|$accesskey (String cdata)
name|TEXTAREA
name|$accesskey
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** the element got the focus      * @param script      * @return the current element builder      */
DECL|method|$onfocus (String script)
name|TEXTAREA
name|$onfocus
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element lost the focus      * @param script      * @return the current element builder      */
DECL|method|$onblur (String script)
name|TEXTAREA
name|$onblur
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** some text was selected      * @param script      * @return the current element builder      */
DECL|method|$onselect (String script)
name|TEXTAREA
name|$onselect
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element value was changed      * @param script      * @return the current element builder      */
DECL|method|$onchange (String script)
name|TEXTAREA
name|$onchange
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Legend
specifier|public
interface|interface
name|_Legend
extends|extends
name|_Child
block|{
comment|/**      * Add a LEGEND element.      * @return a new LEGEND element builder      */
DECL|method|legend ()
name|LEGEND
name|legend
parameter_list|()
function_decl|;
comment|/**      * Add a LEGEND element.      * @param cdata      * @return the current element builder      */
DECL|method|legend (String cdata)
name|_Legend
name|legend
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|FIELDSET
specifier|public
interface|interface
name|FIELDSET
extends|extends
name|Attrs
extends|,
name|_Legend
extends|,
name|PCData
extends|,
name|Flow
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|LEGEND
specifier|public
interface|interface
name|LEGEND
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{
comment|/** accessibility key character      * @param cdata      * @return the current element builder      */
DECL|method|$accesskey (String cdata)
name|LEGEND
name|$accesskey
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|BUTTON
specifier|public
interface|interface
name|BUTTON
extends|extends
comment|/* (%flow;)* -(A|%formctrl|FORM|FIELDSET) */
name|_Block
extends|,
name|PCData
extends|,
name|FontStyle
extends|,
name|Phrase
extends|,
name|_Special
extends|,
name|_ImgObject
extends|,
name|_SubSup
extends|,
name|Attrs
block|{
comment|/** name of the value      * @param cdata      * @return the current element builder      */
DECL|method|$name (String cdata)
name|BUTTON
name|$name
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** sent to server when submitted      * @param cdata      * @return the current element builder      */
DECL|method|$value (String cdata)
name|BUTTON
name|$value
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** for use as form button      * @param type      * @return the current element builder      */
DECL|method|$type (ButtonType type)
name|BUTTON
name|$type
parameter_list|(
name|ButtonType
name|type
parameter_list|)
function_decl|;
comment|/** unavailable in this context      * @return the current element builder      */
DECL|method|$disabled ()
name|BUTTON
name|$disabled
parameter_list|()
function_decl|;
comment|/** position in tabbing order      * @param index      * @return the current element builder      */
DECL|method|$tabindex (int index)
name|BUTTON
name|$tabindex
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** accessibility key character      * @param cdata      * @return the current element builder      */
DECL|method|$accesskey (String cdata)
name|BUTTON
name|$accesskey
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** the element got the focus      * @param script      * @return the current element builder      */
DECL|method|$onfocus (String script)
name|BUTTON
name|$onfocus
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
comment|/** the element lost the focus      * @param script      * @return the current element builder      */
DECL|method|$onblur (String script)
name|BUTTON
name|$onblur
parameter_list|(
name|String
name|script
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_TableRow
specifier|public
interface|interface
name|_TableRow
block|{
comment|/**      * Add a TR (table row) element.      * @return a new TR element builder      */
DECL|method|tr ()
name|TR
name|tr
parameter_list|()
function_decl|;
comment|/**      * Add a TR element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new TR element builder      */
DECL|method|tr (String selector)
name|TR
name|tr
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_TableCol
specifier|public
interface|interface
name|_TableCol
extends|extends
name|_Child
block|{
comment|/**      * Add a COL element.      * @return a new COL element builder      */
DECL|method|col ()
name|COL
name|col
parameter_list|()
function_decl|;
comment|/**      * Add a COL element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return the current element builder      */
DECL|method|col (String selector)
name|_TableCol
name|col
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Table
specifier|public
interface|interface
name|_Table
extends|extends
name|_TableRow
extends|,
name|_TableCol
block|{
comment|/**      * Add a CAPTION element.      * @return a new CAPTION element builder      */
DECL|method|caption ()
name|CAPTION
name|caption
parameter_list|()
function_decl|;
comment|/**      * Add a CAPTION element.      * @param cdata      * @return the current element builder      */
DECL|method|caption (String cdata)
name|_Table
name|caption
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a COLGROPU element.      * @return a new COLGROUP element builder      */
DECL|method|colgroup ()
name|COLGROUP
name|colgroup
parameter_list|()
function_decl|;
comment|/**      * Add a THEAD element.      * @return a new THEAD element builder      */
DECL|method|thead ()
name|THEAD
name|thead
parameter_list|()
function_decl|;
comment|/**      * Add a THEAD element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new THEAD element builder      */
DECL|method|thead (String selector)
name|THEAD
name|thead
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a TFOOT element.      * @return a new TFOOT element builder      */
DECL|method|tfoot ()
name|TFOOT
name|tfoot
parameter_list|()
function_decl|;
comment|/**      * Add a TFOOT element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new TFOOT element builder      */
DECL|method|tfoot (String selector)
name|TFOOT
name|tfoot
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|/**      * Add a tbody (table body) element.      * Must be after thead/tfoot and no tr at the same level.      * @return a new tbody element builder      */
DECL|method|tbody ()
name|TBODY
name|tbody
parameter_list|()
function_decl|;
comment|/**      * Add a TBODY element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new TBODY element builder      */
DECL|method|tbody (String selector)
name|TBODY
name|tbody
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
comment|// $summary, width, border, frame, rules, cellpadding, cellspacing omitted
comment|// use css instead
block|}
comment|/**    * TBODY should be used after THEAD/TFOOT, iff there're no TABLE.TR elements.    */
DECL|interface|TABLE
specifier|public
interface|interface
name|TABLE
extends|extends
name|Attrs
extends|,
name|_Table
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|CAPTION
specifier|public
interface|interface
name|CAPTION
extends|extends
name|Attrs
extends|,
name|Inline
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|THEAD
specifier|public
interface|interface
name|THEAD
extends|extends
name|Attrs
extends|,
name|_TableRow
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|TFOOT
specifier|public
interface|interface
name|TFOOT
extends|extends
name|Attrs
extends|,
name|_TableRow
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|TBODY
specifier|public
interface|interface
name|TBODY
extends|extends
name|Attrs
extends|,
name|_TableRow
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|COLGROUP
specifier|public
interface|interface
name|COLGROUP
extends|extends
name|Attrs
extends|,
name|_TableCol
extends|,
name|_Child
block|{
comment|/** default number of columns in group. default: 1      * @param cols      * @return the current element builder      */
DECL|method|$span (int cols)
name|COLGROUP
name|$span
parameter_list|(
name|int
name|cols
parameter_list|)
function_decl|;
comment|// $width omitted. use css instead.
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|COL
specifier|public
interface|interface
name|COL
extends|extends
name|Attrs
extends|,
name|_Child
block|{
comment|/** COL attributes affect N columns. default: 1      * @param cols      * @return the current element builder      */
DECL|method|$span (int cols)
name|COL
name|$span
parameter_list|(
name|int
name|cols
parameter_list|)
function_decl|;
comment|// $width omitted. use css instead.
block|}
comment|/**    *    */
DECL|interface|_Tr
specifier|public
interface|interface
name|_Tr
extends|extends
name|_Child
block|{
comment|/**      * Add a TH element.      * @return a new TH element builder      */
DECL|method|th ()
name|TH
name|th
parameter_list|()
function_decl|;
comment|/**      * Add a complete TH element.      * @param cdata the content      * @return the current element builder      */
DECL|method|th (String cdata)
name|_Tr
name|th
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a TH element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|th (String selector, String cdata)
name|_Tr
name|th
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a TD element.      * @return a new TD element builder      */
DECL|method|td ()
name|TD
name|td
parameter_list|()
function_decl|;
comment|/**      * Add a TD element.      * @param cdata the content      * @return the current element builder      */
DECL|method|td (String cdata)
name|_Tr
name|td
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a TD element.      * @param selector the css selector in the form of (#id)*(.class)*      * @param cdata the content      * @return the current element builder      */
DECL|method|td (String selector, String cdata)
name|_Tr
name|td
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|TR
specifier|public
interface|interface
name|TR
extends|extends
name|Attrs
extends|,
name|_Tr
extends|,
name|_Child
block|{   }
comment|/**    *    */
DECL|interface|_Cell
specifier|public
interface|interface
name|_Cell
extends|extends
name|Attrs
extends|,
name|Flow
extends|,
name|_Child
block|{
comment|// $abbr omited. begin cell text with terse text instead.
comment|// use $title for elaberation, when appropriate.
comment|// $axis omitted. use scope.
comment|/** space-separated list of id's for header cells      * @param cdata      * @return the current element builder      */
DECL|method|$headers (String cdata)
name|_Cell
name|$headers
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** scope covered by header cells      * @param scope      * @return the current element builder      */
DECL|method|$scope (Scope scope)
name|_Cell
name|$scope
parameter_list|(
name|Scope
name|scope
parameter_list|)
function_decl|;
comment|/** number of rows spanned by cell. default: 1      * @param rows      * @return the current element builder      */
DECL|method|$rowspan (int rows)
name|_Cell
name|$rowspan
parameter_list|(
name|int
name|rows
parameter_list|)
function_decl|;
comment|/** number of cols spanned by cell. default: 1      * @param cols      * @return the current element builder      */
DECL|method|$colspan (int cols)
name|_Cell
name|$colspan
parameter_list|(
name|int
name|cols
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|TH
specifier|public
interface|interface
name|TH
extends|extends
name|_Cell
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|TD
specifier|public
interface|interface
name|TD
extends|extends
name|_Cell
block|{   }
comment|/**    *    */
DECL|interface|_Head
specifier|public
interface|interface
name|_Head
extends|extends
name|HeadMisc
block|{
comment|/**      * Add a TITLE element.      * @return a new TITLE element builder      */
DECL|method|title ()
name|TITLE
name|title
parameter_list|()
function_decl|;
comment|/**      * Add a TITLE element.      * @param cdata the content      * @return the current element builder      */
DECL|method|title (String cdata)
name|_Head
name|title
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/**      * Add a BASE element.      * @return a new BASE element builder      */
DECL|method|base ()
name|BASE
name|base
parameter_list|()
function_decl|;
comment|/**      * Add a complete BASE element.      * @param uri      * @return the current element builder      */
DECL|method|base (String uri)
name|_Head
name|base
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|HEAD
specifier|public
interface|interface
name|HEAD
extends|extends
name|I18nAttrs
extends|,
name|_Head
extends|,
name|_Child
block|{
comment|// $profile omitted
block|}
comment|/**    *    */
DECL|interface|TITLE
specifier|public
interface|interface
name|TITLE
extends|extends
name|I18nAttrs
extends|,
name|PCData
extends|,
name|_Child
block|{   }
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|BASE
specifier|public
interface|interface
name|BASE
extends|extends
name|_Child
block|{
comment|/** URI that acts as base URI      * @param uri      * @return the current element builder      */
DECL|method|$href (String uri)
name|BASE
name|$href
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
annotation|@
name|Element
argument_list|(
name|endTag
operator|=
literal|false
argument_list|)
DECL|interface|META
specifier|public
interface|interface
name|META
extends|extends
name|I18nAttrs
extends|,
name|_Child
block|{
comment|/** HTTP response header name      * @param header      * @return the current element builder      */
DECL|method|$http_equiv (String header)
name|META
name|$http_equiv
parameter_list|(
name|String
name|header
parameter_list|)
function_decl|;
comment|/** metainformation name      * @param name      * @return the current element builder      */
DECL|method|$name (String name)
name|META
name|$name
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/** associated information      * @param cdata      * @return the current element builder      */
DECL|method|$content (String cdata)
name|META
name|$content
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|// $scheme omitted
block|}
comment|/**    *    */
DECL|interface|STYLE
specifier|public
interface|interface
name|STYLE
extends|extends
name|I18nAttrs
extends|,
name|_Content
extends|,
name|_Child
block|{
comment|/** content type of style language      * @param cdata      * @return the current element builder      */
DECL|method|$type (String cdata)
name|STYLE
name|$type
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** designed for use with these media      * @param media      * @return the current element builder      */
DECL|method|$media (EnumSet<Media> media)
name|STYLE
name|$media
parameter_list|(
name|EnumSet
argument_list|<
name|Media
argument_list|>
name|media
parameter_list|)
function_decl|;
comment|/** advisory title      * @param cdata      * @return the current element builder      */
DECL|method|$title (String cdata)
name|STYLE
name|$title
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|SCRIPT
specifier|public
interface|interface
name|SCRIPT
extends|extends
name|_Content
extends|,
name|_Child
block|{
comment|/** char encoding of linked resource      * @param cdata      * @return the current element builder      */
DECL|method|$charset (String cdata)
name|SCRIPT
name|$charset
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** content type of script language      * @param cdata      * @return the current element builder      */
DECL|method|$type (String cdata)
name|SCRIPT
name|$type
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** URI for an external script      * @param cdata      * @return the current element builder      */
DECL|method|$src (String cdata)
name|SCRIPT
name|$src
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
comment|/** UA may defer execution of script      * @param cdata      * @return the current element builder      */
DECL|method|$defer (String cdata)
name|SCRIPT
name|$defer
parameter_list|(
name|String
name|cdata
parameter_list|)
function_decl|;
block|}
comment|/**    *    */
DECL|interface|_Html
specifier|public
interface|interface
name|_Html
extends|extends
name|_Head
extends|,
name|_Body
extends|,
name|__
block|{
comment|/**      * Add a HEAD element.      * @return a new HEAD element builder      */
DECL|method|head ()
name|HEAD
name|head
parameter_list|()
function_decl|;
comment|/**      * Add a BODY element.      * @return a new BODY element builder      */
DECL|method|body ()
name|BODY
name|body
parameter_list|()
function_decl|;
comment|/**      * Add a BODY element.      * @param selector the css selector in the form of (#id)*(.class)*      * @return a new BODY element builder      */
DECL|method|body (String selector)
name|BODY
name|body
parameter_list|(
name|String
name|selector
parameter_list|)
function_decl|;
block|}
comment|// There is only one HEAD and BODY, in that order.
comment|/**    * The root element    */
DECL|interface|HTML
specifier|public
interface|interface
name|HTML
extends|extends
name|I18nAttrs
extends|,
name|_Html
block|{   }
block|}
end_class

end_unit

