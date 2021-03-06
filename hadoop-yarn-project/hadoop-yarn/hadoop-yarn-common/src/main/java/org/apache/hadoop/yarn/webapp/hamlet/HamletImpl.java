begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.hamlet
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
name|hamlet
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Splitter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
import|import static
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Iterator
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|text
operator|.
name|StringEscapeUtils
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
name|hamlet
operator|.
name|HamletImpl
operator|.
name|EOpt
operator|.
name|*
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
name|WebAppException
import|;
end_import

begin_comment
comment|/**  * A simple unbuffered generic hamlet implementation.  *  * Zero copy but allocation on every element, which could be  * optimized to use a thread-local element pool.  *  * Prints HTML as it builds. So the order is important.  * @deprecated Use org.apache.hadoop.yarn.webapp.hamlet2 package instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HamletImpl
specifier|public
class|class
name|HamletImpl
extends|extends
name|HamletSpec
block|{
DECL|field|INDENT_CHARS
specifier|private
specifier|static
specifier|final
name|String
name|INDENT_CHARS
init|=
literal|"  "
decl_stmt|;
DECL|field|SS
specifier|private
specifier|static
specifier|final
name|Splitter
name|SS
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|'.'
argument_list|)
operator|.
name|omitEmptyStrings
argument_list|()
operator|.
name|trimResults
argument_list|()
decl_stmt|;
DECL|field|SJ
specifier|private
specifier|static
specifier|final
name|Joiner
name|SJ
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
DECL|field|CJ
specifier|private
specifier|static
specifier|final
name|Joiner
name|CJ
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
decl_stmt|;
DECL|field|S_ID
specifier|static
specifier|final
name|int
name|S_ID
init|=
literal|0
decl_stmt|;
DECL|field|S_CLASS
specifier|static
specifier|final
name|int
name|S_CLASS
init|=
literal|1
decl_stmt|;
DECL|field|nestLevel
name|int
name|nestLevel
decl_stmt|;
DECL|field|indents
name|int
name|indents
decl_stmt|;
comment|// number of indent() called. mostly for testing.
DECL|field|out
specifier|private
specifier|final
name|PrintWriter
name|out
decl_stmt|;
DECL|field|sb
specifier|private
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// not shared
DECL|field|wasInline
specifier|private
name|boolean
name|wasInline
init|=
literal|false
decl_stmt|;
comment|/**    * Element options. (whether it needs end tag, is inline etc.)    */
DECL|enum|EOpt
specifier|public
enum|enum
name|EOpt
block|{
comment|/** needs end(close) tag */
DECL|enumConstant|ENDTAG
name|ENDTAG
block|,
comment|/** The content is inline */
DECL|enumConstant|INLINE
name|INLINE
block|,
comment|/** The content is preformatted */
DECL|enumConstant|PRE
name|PRE
block|}
empty_stmt|;
comment|/**    * The base class for elements    * @param<T> type of the parent (containing) element for the element    */
DECL|class|EImp
specifier|public
class|class
name|EImp
parameter_list|<
name|T
extends|extends
name|_
parameter_list|>
implements|implements
name|_Child
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|T
name|parent
decl_stmt|;
comment|// short cut for parent element
DECL|field|opts
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
decl_stmt|;
comment|// element options
DECL|field|started
specifier|private
name|boolean
name|started
init|=
literal|false
decl_stmt|;
DECL|field|attrsClosed
specifier|private
name|boolean
name|attrsClosed
init|=
literal|false
decl_stmt|;
DECL|method|EImp (String name, T parent, EnumSet<EOpt> opts)
name|EImp
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|parent
parameter_list|,
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|opts
operator|=
name|opts
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|_ ()
specifier|public
name|T
name|_
parameter_list|()
block|{
name|closeAttrs
argument_list|()
expr_stmt|;
operator|--
name|nestLevel
expr_stmt|;
name|printEndTag
argument_list|(
name|name
argument_list|,
name|opts
argument_list|)
expr_stmt|;
return|return
name|parent
return|;
block|}
DECL|method|_p (boolean quote, Object... args)
specifier|protected
name|void
name|_p
parameter_list|(
name|boolean
name|quote
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|closeAttrs
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|s
range|:
name|args
control|)
block|{
if|if
condition|(
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|PRE
argument_list|)
condition|)
block|{
name|indent
argument_list|(
name|opts
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
name|quote
condition|?
name|escapeHtml4
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|s
argument_list|)
argument_list|)
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|INLINE
argument_list|)
operator|&&
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|PRE
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|_v (Class<? extends SubView> cls)
specifier|protected
name|void
name|_v
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|cls
parameter_list|)
block|{
name|closeAttrs
argument_list|()
expr_stmt|;
name|subView
argument_list|(
name|cls
argument_list|)
expr_stmt|;
block|}
DECL|method|closeAttrs ()
specifier|protected
name|void
name|closeAttrs
parameter_list|()
block|{
if|if
condition|(
operator|!
name|attrsClosed
condition|)
block|{
name|startIfNeeded
argument_list|()
expr_stmt|;
operator|++
name|nestLevel
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|INLINE
argument_list|)
operator|&&
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|PRE
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|attrsClosed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|addAttr (String name, String value)
specifier|protected
name|void
name|addAttr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|attrsClosed
argument_list|,
literal|"attribute added after content"
argument_list|)
expr_stmt|;
name|startIfNeeded
argument_list|()
expr_stmt|;
name|printAttr
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addAttr (String name, Object value)
specifier|protected
name|void
name|addAttr
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|addAttr
argument_list|(
name|name
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addMediaAttr (String name, EnumSet<Media> media)
specifier|protected
name|void
name|addMediaAttr
parameter_list|(
name|String
name|name
parameter_list|,
name|EnumSet
argument_list|<
name|Media
argument_list|>
name|media
parameter_list|)
block|{
comment|// 6.13 comma-separated list
name|addAttr
argument_list|(
name|name
argument_list|,
name|CJ
operator|.
name|join
argument_list|(
name|media
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addRelAttr (String name, EnumSet<LinkType> types)
specifier|protected
name|void
name|addRelAttr
parameter_list|(
name|String
name|name
parameter_list|,
name|EnumSet
argument_list|<
name|LinkType
argument_list|>
name|types
parameter_list|)
block|{
comment|// 6.12 space-separated list
name|addAttr
argument_list|(
name|name
argument_list|,
name|SJ
operator|.
name|join
argument_list|(
name|types
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|startIfNeeded ()
specifier|private
name|void
name|startIfNeeded
parameter_list|()
block|{
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|printStartTag
argument_list|(
name|name
argument_list|,
name|opts
argument_list|)
expr_stmt|;
name|started
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|_inline (boolean choice)
specifier|protected
name|void
name|_inline
parameter_list|(
name|boolean
name|choice
parameter_list|)
block|{
if|if
condition|(
name|choice
condition|)
block|{
name|opts
operator|.
name|add
argument_list|(
name|INLINE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|opts
operator|.
name|remove
argument_list|(
name|INLINE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|_endTag (boolean choice)
specifier|protected
name|void
name|_endTag
parameter_list|(
name|boolean
name|choice
parameter_list|)
block|{
if|if
condition|(
name|choice
condition|)
block|{
name|opts
operator|.
name|add
argument_list|(
name|ENDTAG
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|opts
operator|.
name|remove
argument_list|(
name|ENDTAG
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|_pre (boolean choice)
specifier|protected
name|void
name|_pre
parameter_list|(
name|boolean
name|choice
parameter_list|)
block|{
if|if
condition|(
name|choice
condition|)
block|{
name|opts
operator|.
name|add
argument_list|(
name|PRE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|opts
operator|.
name|remove
argument_list|(
name|PRE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Generic
specifier|public
class|class
name|Generic
parameter_list|<
name|T
extends|extends
name|_
parameter_list|>
extends|extends
name|EImp
argument_list|<
name|T
argument_list|>
implements|implements
name|PCData
block|{
DECL|method|Generic (String name, T parent, EnumSet<EOpt> opts)
name|Generic
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|parent
parameter_list|,
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|parent
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
DECL|method|_inline ()
specifier|public
name|Generic
argument_list|<
name|T
argument_list|>
name|_inline
parameter_list|()
block|{
name|super
operator|.
name|_inline
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|_noEndTag ()
specifier|public
name|Generic
argument_list|<
name|T
argument_list|>
name|_noEndTag
parameter_list|()
block|{
name|super
operator|.
name|_endTag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|_pre ()
specifier|public
name|Generic
argument_list|<
name|T
argument_list|>
name|_pre
parameter_list|()
block|{
name|super
operator|.
name|_pre
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|_attr (String name, String value)
specifier|public
name|Generic
argument_list|<
name|T
argument_list|>
name|_attr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|addAttr
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|_elem (String name, EnumSet<EOpt> opts)
specifier|public
name|Generic
argument_list|<
name|Generic
argument_list|<
name|T
argument_list|>
argument_list|>
name|_elem
parameter_list|(
name|String
name|name
parameter_list|,
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
parameter_list|)
block|{
name|closeAttrs
argument_list|()
expr_stmt|;
return|return
operator|new
name|Generic
argument_list|<
name|Generic
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|(
name|name
argument_list|,
name|this
argument_list|,
name|opts
argument_list|)
return|;
block|}
DECL|method|elem (String name)
specifier|public
name|Generic
argument_list|<
name|Generic
argument_list|<
name|T
argument_list|>
argument_list|>
name|elem
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|_elem
argument_list|(
name|name
argument_list|,
name|of
argument_list|(
name|ENDTAG
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|_ (Object... lines)
specifier|public
name|Generic
argument_list|<
name|T
argument_list|>
name|_
parameter_list|(
name|Object
modifier|...
name|lines
parameter_list|)
block|{
name|_p
argument_list|(
literal|true
argument_list|,
name|lines
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|_r (Object... lines)
specifier|public
name|Generic
argument_list|<
name|T
argument_list|>
name|_r
parameter_list|(
name|Object
modifier|...
name|lines
parameter_list|)
block|{
name|_p
argument_list|(
literal|false
argument_list|,
name|lines
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|method|HamletImpl (PrintWriter out, int nestLevel, boolean wasInline)
specifier|public
name|HamletImpl
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|int
name|nestLevel
parameter_list|,
name|boolean
name|wasInline
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|nestLevel
operator|=
name|nestLevel
expr_stmt|;
name|this
operator|.
name|wasInline
operator|=
name|wasInline
expr_stmt|;
block|}
DECL|method|nestLevel ()
specifier|public
name|int
name|nestLevel
parameter_list|()
block|{
return|return
name|nestLevel
return|;
block|}
DECL|method|wasInline ()
specifier|public
name|boolean
name|wasInline
parameter_list|()
block|{
return|return
name|wasInline
return|;
block|}
DECL|method|setWasInline (boolean state)
specifier|public
name|void
name|setWasInline
parameter_list|(
name|boolean
name|state
parameter_list|)
block|{
name|wasInline
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getWriter ()
specifier|public
name|PrintWriter
name|getWriter
parameter_list|()
block|{
return|return
name|out
return|;
block|}
comment|/**    * Create a root-level generic element.    * Mostly for testing purpose.    * @param<T> type of the parent element    * @param name of the element    * @param opts {@link EOpt element options}    * @return the element    */
specifier|public
parameter_list|<
name|T
extends|extends
name|_
parameter_list|>
DECL|method|root (String name, EnumSet<EOpt> opts)
name|Generic
argument_list|<
name|T
argument_list|>
name|root
parameter_list|(
name|String
name|name
parameter_list|,
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
parameter_list|)
block|{
return|return
operator|new
name|Generic
argument_list|<
name|T
argument_list|>
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|opts
argument_list|)
return|;
block|}
DECL|method|root (String name)
specifier|public
parameter_list|<
name|T
extends|extends
name|_
parameter_list|>
name|Generic
argument_list|<
name|T
argument_list|>
name|root
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|root
argument_list|(
name|name
argument_list|,
name|of
argument_list|(
name|ENDTAG
argument_list|)
argument_list|)
return|;
block|}
DECL|method|printStartTag (String name, EnumSet<EOpt> opts)
specifier|protected
name|void
name|printStartTag
parameter_list|(
name|String
name|name
parameter_list|,
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
parameter_list|)
block|{
name|indent
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|sb
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// for easier mock test
block|}
DECL|method|indent (EnumSet<EOpt> opts)
specifier|protected
name|void
name|indent
parameter_list|(
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
parameter_list|)
block|{
if|if
condition|(
name|opts
operator|.
name|contains
argument_list|(
name|INLINE
argument_list|)
operator|&&
name|wasInline
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|wasInline
condition|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|wasInline
operator|=
name|opts
operator|.
name|contains
argument_list|(
name|INLINE
argument_list|)
operator|||
name|opts
operator|.
name|contains
argument_list|(
name|PRE
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nestLevel
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|INDENT_CHARS
argument_list|)
expr_stmt|;
block|}
operator|++
name|indents
expr_stmt|;
block|}
DECL|method|printEndTag (String name, EnumSet<EOpt> opts)
specifier|protected
name|void
name|printEndTag
parameter_list|(
name|String
name|name
parameter_list|,
name|EnumSet
argument_list|<
name|EOpt
argument_list|>
name|opts
parameter_list|)
block|{
if|if
condition|(
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|ENDTAG
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|PRE
argument_list|)
condition|)
block|{
name|indent
argument_list|(
name|opts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|wasInline
operator|=
name|opts
operator|.
name|contains
argument_list|(
name|INLINE
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|sb
operator|.
name|append
argument_list|(
literal|"</"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// ditto
if|if
condition|(
operator|!
name|opts
operator|.
name|contains
argument_list|(
name|INLINE
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|printAttr (String name, String value)
specifier|protected
name|void
name|printAttr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
operator|.
name|append
argument_list|(
name|escapeHtml4
argument_list|(
name|value
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sub-classes should override this to do something interesting.    * @param cls the sub-view class    */
DECL|method|subView (Class<? extends SubView> cls)
specifier|protected
name|void
name|subView
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|cls
parameter_list|)
block|{
name|indent
argument_list|(
name|of
argument_list|(
name|ENDTAG
argument_list|)
argument_list|)
expr_stmt|;
comment|// not an inline view
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|cls
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
comment|/**    * Parse selector into id and classes    * @param selector in the form of (#id)?(.class)*    * @return an two element array [id, "space-separated classes"].    *         Either element could be null.    * @throws WebAppException when both are null or syntax error.    */
DECL|method|parseSelector (String selector)
specifier|public
specifier|static
name|String
index|[]
name|parseSelector
parameter_list|(
name|String
name|selector
parameter_list|)
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|null
block|}
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|rs
init|=
name|SS
operator|.
name|split
argument_list|(
name|selector
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|rs
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|maybeId
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|maybeId
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'#'
condition|)
block|{
name|result
index|[
name|S_ID
index|]
operator|=
name|maybeId
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
index|[
name|S_CLASS
index|]
operator|=
name|SJ
operator|.
name|join
argument_list|(
name|Iterables
operator|.
name|skip
argument_list|(
name|rs
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
index|[
name|S_CLASS
index|]
operator|=
name|SJ
operator|.
name|join
argument_list|(
name|rs
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
throw|throw
operator|new
name|WebAppException
argument_list|(
literal|"Error parsing selector: "
operator|+
name|selector
argument_list|)
throw|;
block|}
comment|/**    * Set id and/or class attributes for an element.    * @param<E> type of the element    * @param e the element    * @param selector Haml form of "(#id)?(.class)*"    * @return the element    */
DECL|method|setSelector (E e, String selector)
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|CoreAttrs
parameter_list|>
name|E
name|setSelector
parameter_list|(
name|E
name|e
parameter_list|,
name|String
name|selector
parameter_list|)
block|{
name|String
index|[]
name|res
init|=
name|parseSelector
argument_list|(
name|selector
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
index|[
name|S_ID
index|]
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|$id
argument_list|(
name|res
index|[
name|S_ID
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|res
index|[
name|S_CLASS
index|]
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|$class
argument_list|(
name|res
index|[
name|S_CLASS
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
DECL|method|setLinkHref (E e, String href)
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|LINK
parameter_list|>
name|E
name|setLinkHref
parameter_list|(
name|E
name|e
parameter_list|,
name|String
name|href
parameter_list|)
block|{
if|if
condition|(
name|href
operator|.
name|endsWith
argument_list|(
literal|".css"
argument_list|)
condition|)
block|{
name|e
operator|.
name|$rel
argument_list|(
literal|"stylesheet"
argument_list|)
expr_stmt|;
comment|// required in html5
block|}
name|e
operator|.
name|$href
argument_list|(
name|href
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
DECL|method|setScriptSrc (E e, String src)
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|SCRIPT
parameter_list|>
name|E
name|setScriptSrc
parameter_list|(
name|E
name|e
parameter_list|,
name|String
name|src
parameter_list|)
block|{
if|if
condition|(
name|src
operator|.
name|endsWith
argument_list|(
literal|".js"
argument_list|)
condition|)
block|{
name|e
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
expr_stmt|;
comment|// required in html4
block|}
name|e
operator|.
name|$src
argument_list|(
name|src
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
end_class

end_unit

