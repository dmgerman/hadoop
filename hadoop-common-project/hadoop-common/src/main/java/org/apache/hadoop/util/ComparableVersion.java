begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Code source of this file:
end_comment

begin_comment
comment|//   http://grepcode.com/file/repo1.maven.org/maven2/
end_comment

begin_comment
comment|//     org.apache.maven/maven-artifact/3.1.1/
end_comment

begin_comment
comment|//       org/apache/maven/artifact/versioning/ComparableVersion.java/
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Modifications made on top of the source:
end_comment

begin_comment
comment|//   1. Changed
end_comment

begin_comment
comment|//        package org.apache.maven.artifact.versioning;
end_comment

begin_comment
comment|//      to
end_comment

begin_comment
comment|//        package org.apache.hadoop.util;
end_comment

begin_comment
comment|//   2. Removed author tags to clear hadoop author tag warning
end_comment

begin_comment
comment|//        author<a href="mailto:kenney@apache.org">Kenney Westerhof</a>
end_comment

begin_comment
comment|//        author<a href="mailto:hboutemy@apache.org">HervÃ© Boutemy</a>
end_comment

begin_comment
comment|//
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ListIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  * Generic implementation of version comparison.  *   *<p>Features:  *<ul>  *<li>mixing of '<code>-</code>' (dash) and '<code>.</code>' (dot) separators,</li>  *<li>transition between characters and digits also constitutes a separator:  *<code>1.0alpha1 =&gt; [1, 0, alpha, 1]</code></li>  *<li>unlimited number of version components,</li>  *<li>version components in the text can be digits or strings,</li>  *<li>strings are checked for well-known qualifiers and the qualifier ordering is used for version ordering.  *     Well-known qualifiers (case insensitive) are:<ul>  *<li><code>alpha</code> or<code>a</code></li>  *<li><code>beta</code> or<code>b</code></li>  *<li><code>milestone</code> or<code>m</code></li>  *<li><code>rc</code> or<code>cr</code></li>  *<li><code>snapshot</code></li>  *<li><code>(the empty string)</code> or<code>ga</code> or<code>final</code></li>  *<li><code>sp</code></li>  *</ul>  *     Unknown qualifiers are considered after known qualifiers, with lexical order (always case insensitive),  *</li>  *<li>a dash usually precedes a qualifier, and is always less important than something preceded with a dot.</li>  *</ul></p>  *  * @see<a href="https://cwiki.apache.org/confluence/display/MAVENOLD/Versioning">"Versioning" on Maven Wiki</a>  */
end_comment

begin_class
DECL|class|ComparableVersion
specifier|public
class|class
name|ComparableVersion
implements|implements
name|Comparable
argument_list|<
name|ComparableVersion
argument_list|>
block|{
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|field|canonical
specifier|private
name|String
name|canonical
decl_stmt|;
DECL|field|items
specifier|private
name|ListItem
name|items
decl_stmt|;
DECL|interface|Item
specifier|private
interface|interface
name|Item
block|{
DECL|field|INTEGER_ITEM
name|int
name|INTEGER_ITEM
init|=
literal|0
decl_stmt|;
DECL|field|STRING_ITEM
name|int
name|STRING_ITEM
init|=
literal|1
decl_stmt|;
DECL|field|LIST_ITEM
name|int
name|LIST_ITEM
init|=
literal|2
decl_stmt|;
DECL|method|compareTo ( Item item )
name|int
name|compareTo
parameter_list|(
name|Item
name|item
parameter_list|)
function_decl|;
DECL|method|getType ()
name|int
name|getType
parameter_list|()
function_decl|;
DECL|method|isNull ()
name|boolean
name|isNull
parameter_list|()
function_decl|;
block|}
comment|/**      * Represents a numeric item in the version item list.      */
DECL|class|IntegerItem
specifier|private
specifier|static
class|class
name|IntegerItem
implements|implements
name|Item
block|{
DECL|field|BIG_INTEGER_ZERO
specifier|private
specifier|static
specifier|final
name|BigInteger
name|BIG_INTEGER_ZERO
init|=
operator|new
name|BigInteger
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|BigInteger
name|value
decl_stmt|;
DECL|field|ZERO
specifier|public
specifier|static
specifier|final
name|IntegerItem
name|ZERO
init|=
operator|new
name|IntegerItem
argument_list|()
decl_stmt|;
DECL|method|IntegerItem ()
specifier|private
name|IntegerItem
parameter_list|()
block|{
name|this
operator|.
name|value
operator|=
name|BIG_INTEGER_ZERO
expr_stmt|;
block|}
DECL|method|IntegerItem ( String str )
specifier|public
name|IntegerItem
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
operator|new
name|BigInteger
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
DECL|method|getType ()
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|INTEGER_ITEM
return|;
block|}
DECL|method|isNull ()
specifier|public
name|boolean
name|isNull
parameter_list|()
block|{
return|return
name|BIG_INTEGER_ZERO
operator|.
name|equals
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|compareTo ( Item item )
specifier|public
name|int
name|compareTo
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
if|if
condition|(
name|item
operator|==
literal|null
condition|)
block|{
return|return
name|BIG_INTEGER_ZERO
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|?
literal|0
else|:
literal|1
return|;
comment|// 1.0 == 1, 1.1> 1
block|}
switch|switch
condition|(
name|item
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INTEGER_ITEM
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|IntegerItem
operator|)
name|item
operator|)
operator|.
name|value
argument_list|)
return|;
case|case
name|STRING_ITEM
case|:
return|return
literal|1
return|;
comment|// 1.1> 1-sp
case|case
name|LIST_ITEM
case|:
return|return
literal|1
return|;
comment|// 1.1> 1-1
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid item: "
operator|+
name|item
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Represents a string in the version item list, usually a qualifier.      */
DECL|class|StringItem
specifier|private
specifier|static
class|class
name|StringItem
implements|implements
name|Item
block|{
DECL|field|QUALIFIERS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|QUALIFIERS
init|=
block|{
literal|"alpha"
block|,
literal|"beta"
block|,
literal|"milestone"
block|,
literal|"rc"
block|,
literal|"snapshot"
block|,
literal|""
block|,
literal|"sp"
block|}
decl_stmt|;
DECL|field|_QUALIFIERS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|_QUALIFIERS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|QUALIFIERS
argument_list|)
decl_stmt|;
DECL|field|ALIASES
specifier|private
specifier|static
specifier|final
name|Properties
name|ALIASES
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|ALIASES
operator|.
name|put
argument_list|(
literal|"ga"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ALIASES
operator|.
name|put
argument_list|(
literal|"final"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ALIASES
operator|.
name|put
argument_list|(
literal|"cr"
argument_list|,
literal|"rc"
argument_list|)
expr_stmt|;
block|}
comment|/**          * A comparable value for the empty-string qualifier. This one is used to determine if a given qualifier makes          * the version older than one without a qualifier, or more recent.          */
DECL|field|RELEASE_VERSION_INDEX
specifier|private
specifier|static
specifier|final
name|String
name|RELEASE_VERSION_INDEX
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|_QUALIFIERS
operator|.
name|indexOf
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|method|StringItem ( String value, boolean followedByDigit )
specifier|public
name|StringItem
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|followedByDigit
parameter_list|)
block|{
if|if
condition|(
name|followedByDigit
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// a1 = alpha-1, b1 = beta-1, m1 = milestone-1
switch|switch
condition|(
name|value
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
condition|)
block|{
case|case
literal|'a'
case|:
name|value
operator|=
literal|"alpha"
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|value
operator|=
literal|"beta"
expr_stmt|;
break|break;
case|case
literal|'m'
case|:
name|value
operator|=
literal|"milestone"
expr_stmt|;
break|break;
block|}
block|}
name|this
operator|.
name|value
operator|=
name|ALIASES
operator|.
name|getProperty
argument_list|(
name|value
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getType ()
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|STRING_ITEM
return|;
block|}
DECL|method|isNull ()
specifier|public
name|boolean
name|isNull
parameter_list|()
block|{
return|return
operator|(
name|comparableQualifier
argument_list|(
name|value
argument_list|)
operator|.
name|compareTo
argument_list|(
name|RELEASE_VERSION_INDEX
argument_list|)
operator|==
literal|0
operator|)
return|;
block|}
comment|/**          * Returns a comparable value for a qualifier.          *          * This method takes into account the ordering of known qualifiers then unknown qualifiers with lexical ordering.          *          * just returning an Integer with the index here is faster, but requires a lot of if/then/else to check for -1          * or QUALIFIERS.size and then resort to lexical ordering. Most comparisons are decided by the first character,          * so this is still fast. If more characters are needed then it requires a lexical sort anyway.          *          * @param qualifier          * @return an equivalent value that can be used with lexical comparison          */
DECL|method|comparableQualifier ( String qualifier )
specifier|public
specifier|static
name|String
name|comparableQualifier
parameter_list|(
name|String
name|qualifier
parameter_list|)
block|{
name|int
name|i
init|=
name|_QUALIFIERS
operator|.
name|indexOf
argument_list|(
name|qualifier
argument_list|)
decl_stmt|;
return|return
name|i
operator|==
operator|-
literal|1
condition|?
operator|(
name|_QUALIFIERS
operator|.
name|size
argument_list|()
operator|+
literal|"-"
operator|+
name|qualifier
operator|)
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|compareTo ( Item item )
specifier|public
name|int
name|compareTo
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
if|if
condition|(
name|item
operator|==
literal|null
condition|)
block|{
comment|// 1-rc< 1, 1-ga> 1
return|return
name|comparableQualifier
argument_list|(
name|value
argument_list|)
operator|.
name|compareTo
argument_list|(
name|RELEASE_VERSION_INDEX
argument_list|)
return|;
block|}
switch|switch
condition|(
name|item
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INTEGER_ITEM
case|:
return|return
operator|-
literal|1
return|;
comment|// 1.any< 1.1 ?
case|case
name|STRING_ITEM
case|:
return|return
name|comparableQualifier
argument_list|(
name|value
argument_list|)
operator|.
name|compareTo
argument_list|(
name|comparableQualifier
argument_list|(
operator|(
operator|(
name|StringItem
operator|)
name|item
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
case|case
name|LIST_ITEM
case|:
return|return
operator|-
literal|1
return|;
comment|// 1.any< 1-1
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid item: "
operator|+
name|item
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
comment|/**      * Represents a version list item. This class is used both for the global item list and for sub-lists (which start      * with '-(number)' in the version specification).      */
DECL|class|ListItem
specifier|private
specifier|static
class|class
name|ListItem
extends|extends
name|ArrayList
argument_list|<
name|Item
argument_list|>
implements|implements
name|Item
block|{
DECL|method|getType ()
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|LIST_ITEM
return|;
block|}
DECL|method|isNull ()
specifier|public
name|boolean
name|isNull
parameter_list|()
block|{
return|return
operator|(
name|size
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
DECL|method|normalize ()
name|void
name|normalize
parameter_list|()
block|{
for|for
control|(
name|ListIterator
argument_list|<
name|Item
argument_list|>
name|iterator
init|=
name|listIterator
argument_list|(
name|size
argument_list|()
argument_list|)
init|;
name|iterator
operator|.
name|hasPrevious
argument_list|()
condition|;
control|)
block|{
name|Item
name|item
init|=
name|iterator
operator|.
name|previous
argument_list|()
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|isNull
argument_list|()
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// remove null trailing items: 0, "", empty list
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
DECL|method|compareTo ( Item item )
specifier|public
name|int
name|compareTo
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
if|if
condition|(
name|item
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
comment|// 1-0 = 1- (normalize) = 1
block|}
name|Item
name|first
init|=
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|first
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|)
return|;
block|}
switch|switch
condition|(
name|item
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INTEGER_ITEM
case|:
return|return
operator|-
literal|1
return|;
comment|// 1-1< 1.0.x
case|case
name|STRING_ITEM
case|:
return|return
literal|1
return|;
comment|// 1-1> 1-sp
case|case
name|LIST_ITEM
case|:
name|Iterator
argument_list|<
name|Item
argument_list|>
name|left
init|=
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Item
argument_list|>
name|right
init|=
operator|(
operator|(
name|ListItem
operator|)
name|item
operator|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|left
operator|.
name|hasNext
argument_list|()
operator|||
name|right
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Item
name|l
init|=
name|left
operator|.
name|hasNext
argument_list|()
condition|?
name|left
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|Item
name|r
init|=
name|right
operator|.
name|hasNext
argument_list|()
condition|?
name|right
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
comment|// if this is shorter, then invert the compare and mul with -1
name|int
name|result
init|=
name|l
operator|==
literal|null
condition|?
operator|-
literal|1
operator|*
name|r
operator|.
name|compareTo
argument_list|(
name|l
argument_list|)
else|:
name|l
operator|.
name|compareTo
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|0
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"invalid item: "
operator|+
name|item
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"("
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Item
argument_list|>
name|iter
init|=
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|method|ComparableVersion ( String version )
specifier|public
name|ComparableVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|parseVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
DECL|method|parseVersion ( String version )
specifier|public
specifier|final
name|void
name|parseVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|version
expr_stmt|;
name|items
operator|=
operator|new
name|ListItem
argument_list|()
expr_stmt|;
name|version
operator|=
name|version
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
name|ListItem
name|list
init|=
name|items
decl_stmt|;
name|Stack
argument_list|<
name|Item
argument_list|>
name|stack
init|=
operator|new
name|Stack
argument_list|<
name|Item
argument_list|>
argument_list|()
decl_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|boolean
name|isDigit
init|=
literal|false
decl_stmt|;
name|int
name|startIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|version
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|version
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'.'
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|startIndex
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|IntegerItem
operator|.
name|ZERO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|parseItem
argument_list|(
name|isDigit
argument_list|,
name|version
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startIndex
operator|=
name|i
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'-'
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|startIndex
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|IntegerItem
operator|.
name|ZERO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|parseItem
argument_list|(
name|isDigit
argument_list|,
name|version
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startIndex
operator|=
name|i
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|isDigit
condition|)
block|{
name|list
operator|.
name|normalize
argument_list|()
expr_stmt|;
comment|// 1.0-* = 1-*
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|<
name|version
operator|.
name|length
argument_list|()
operator|)
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|version
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
condition|)
block|{
comment|// new ListItem only if previous were digits and new char is a digit,
comment|// ie need to differentiate only 1.1 from 1-1
name|list
operator|.
name|add
argument_list|(
name|list
operator|=
operator|new
name|ListItem
argument_list|()
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|c
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isDigit
operator|&&
name|i
operator|>
name|startIndex
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|StringItem
argument_list|(
name|version
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|i
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|startIndex
operator|=
name|i
expr_stmt|;
block|}
name|isDigit
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|isDigit
operator|&&
name|i
operator|>
name|startIndex
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|parseItem
argument_list|(
literal|true
argument_list|,
name|version
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|startIndex
operator|=
name|i
expr_stmt|;
block|}
name|isDigit
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|version
operator|.
name|length
argument_list|()
operator|>
name|startIndex
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|parseItem
argument_list|(
name|isDigit
argument_list|,
name|version
operator|.
name|substring
argument_list|(
name|startIndex
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|list
operator|=
operator|(
name|ListItem
operator|)
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|list
operator|.
name|normalize
argument_list|()
expr_stmt|;
block|}
name|canonical
operator|=
name|items
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|parseItem ( boolean isDigit, String buf )
specifier|private
specifier|static
name|Item
name|parseItem
parameter_list|(
name|boolean
name|isDigit
parameter_list|,
name|String
name|buf
parameter_list|)
block|{
return|return
name|isDigit
condition|?
operator|new
name|IntegerItem
argument_list|(
name|buf
argument_list|)
else|:
operator|new
name|StringItem
argument_list|(
name|buf
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|compareTo ( ComparableVersion o )
specifier|public
name|int
name|compareTo
parameter_list|(
name|ComparableVersion
name|o
parameter_list|)
block|{
return|return
name|items
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|items
argument_list|)
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|equals ( Object o )
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|(
name|o
operator|instanceof
name|ComparableVersion
operator|)
operator|&&
name|canonical
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ComparableVersion
operator|)
name|o
operator|)
operator|.
name|canonical
argument_list|)
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|canonical
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

