begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
operator|.
name|Private
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
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|util
operator|.
name|resource
operator|.
name|ResourceUtils
import|;
end_import

begin_comment
comment|/**  * Common string manipulation helpers  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|StringHelper
specifier|public
specifier|final
class|class
name|StringHelper
block|{
comment|// Common joiners to avoid per join creation of joiners
DECL|field|SSV_JOINER
specifier|public
specifier|static
specifier|final
name|Joiner
name|SSV_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
DECL|field|CSV_JOINER
specifier|public
specifier|static
specifier|final
name|Joiner
name|CSV_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
decl_stmt|;
DECL|field|JOINER
specifier|public
specifier|static
specifier|final
name|Joiner
name|JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|_JOINER
specifier|public
specifier|static
specifier|final
name|Joiner
name|_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|'_'
argument_list|)
decl_stmt|;
DECL|field|PATH_JOINER
specifier|public
specifier|static
specifier|final
name|Joiner
name|PATH_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
DECL|field|PATH_ARG_JOINER
specifier|public
specifier|static
specifier|final
name|Joiner
name|PATH_ARG_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|"/:"
argument_list|)
decl_stmt|;
DECL|field|DOT_JOINER
specifier|public
specifier|static
specifier|final
name|Joiner
name|DOT_JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
DECL|field|SSV_SPLITTER
specifier|public
specifier|static
specifier|final
name|Splitter
name|SSV_SPLITTER
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|omitEmptyStrings
argument_list|()
operator|.
name|trimResults
argument_list|()
decl_stmt|;
DECL|field|_SPLITTER
specifier|public
specifier|static
specifier|final
name|Splitter
name|_SPLITTER
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|'_'
argument_list|)
operator|.
name|trimResults
argument_list|()
decl_stmt|;
DECL|field|ABS_URL_RE
specifier|private
specifier|static
specifier|final
name|Pattern
name|ABS_URL_RE
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(?:\\w+:)?//"
argument_list|)
decl_stmt|;
comment|/**    * Join on space.    * @param args to join    * @return args joined by space    */
DECL|method|sjoin (Object... args)
specifier|public
specifier|static
name|String
name|sjoin
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|SSV_JOINER
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Join on comma.    * @param args to join    * @return args joined by comma    */
DECL|method|cjoin (Object... args)
specifier|public
specifier|static
name|String
name|cjoin
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|CSV_JOINER
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Join on dot    * @param args to join    * @return args joined by dot    */
DECL|method|djoin (Object... args)
specifier|public
specifier|static
name|String
name|djoin
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|DOT_JOINER
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Join on underscore    * @param args to join    * @return args joined underscore    */
DECL|method|_join (Object... args)
specifier|public
specifier|static
name|String
name|_join
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|_JOINER
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Join on slash    * @param args to join    * @return args joined with slash    */
DECL|method|pjoin (Object... args)
specifier|public
specifier|static
name|String
name|pjoin
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|PATH_JOINER
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Join on slash and colon (e.g., path args in routing spec)    * @param args to join    * @return args joined with /:    */
DECL|method|pajoin (Object... args)
specifier|public
specifier|static
name|String
name|pajoin
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|PATH_ARG_JOINER
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Join without separator    * @param args    * @return joined args with no separator    */
DECL|method|join (Object... args)
specifier|public
specifier|static
name|String
name|join
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|JOINER
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Join with a separator    * @param sep the separator    * @param args to join    * @return args joined with a separator    */
DECL|method|joins (String sep, Object...args)
specifier|public
specifier|static
name|String
name|joins
parameter_list|(
name|String
name|sep
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
name|sep
argument_list|)
operator|.
name|join
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Split on space and trim results.    * @param s the string to split    * @return an iterable of strings    */
DECL|method|split (CharSequence s)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|split
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
return|return
name|SSV_SPLITTER
operator|.
name|split
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**    * Split on _ and trim results    * @param s the string to split    * @return an iterable of strings    */
DECL|method|_split (CharSequence s)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|_split
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
return|return
name|_SPLITTER
operator|.
name|split
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**    * Check whether a url is absolute or note    * @param url to check    * @return true if url starts with scheme:// or //    */
DECL|method|isAbsUrl (CharSequence url)
specifier|public
specifier|static
name|boolean
name|isAbsUrl
parameter_list|(
name|CharSequence
name|url
parameter_list|)
block|{
return|return
name|ABS_URL_RE
operator|.
name|matcher
argument_list|(
name|url
argument_list|)
operator|.
name|find
argument_list|()
return|;
block|}
comment|/**    * Join url components    * @param pathPrefix for relative urls    * @param args url components to join    * @return an url string    */
DECL|method|ujoin (String pathPrefix, String... args)
specifier|public
specifier|static
name|String
name|ujoin
parameter_list|(
name|String
name|pathPrefix
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|args
control|)
block|{
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|part
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|||
name|isAbsUrl
argument_list|(
name|part
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|uappend
argument_list|(
name|sb
argument_list|,
name|pathPrefix
argument_list|)
expr_stmt|;
name|uappend
argument_list|(
name|sb
argument_list|,
name|part
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|uappend
argument_list|(
name|sb
argument_list|,
name|part
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|uappend (StringBuilder sb, String part)
specifier|private
specifier|static
name|void
name|uappend
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|String
name|part
parameter_list|)
block|{
if|if
condition|(
operator|(
name|sb
operator|.
name|length
argument_list|()
operator|<=
literal|0
operator|||
name|sb
operator|.
name|charAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'/'
operator|)
operator|&&
operator|!
name|part
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
DECL|method|getResourceSecondsString (Map<String, Long> targetMap)
specifier|public
specifier|static
name|String
name|getResourceSecondsString
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|targetMap
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|targetMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|//completed app report in the timeline server doesn't have usage report
name|Long
name|memorySeconds
init|=
literal|0L
decl_stmt|;
name|Long
name|vcoreSeconds
init|=
literal|0L
decl_stmt|;
if|if
condition|(
name|targetMap
operator|.
name|containsKey
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|memorySeconds
operator|=
name|targetMap
operator|.
name|get
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|targetMap
operator|.
name|containsKey
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|vcoreSeconds
operator|=
name|targetMap
operator|.
name|get
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|strings
operator|.
name|add
argument_list|(
name|memorySeconds
operator|+
literal|" MB-seconds"
argument_list|)
expr_stmt|;
name|strings
operator|.
name|add
argument_list|(
name|vcoreSeconds
operator|+
literal|" vcore-seconds"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|tmp
init|=
name|ResourceUtils
operator|.
name|getResourceTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetMap
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|targetMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
operator|!
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|units
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|tmp
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|units
operator|=
name|tmp
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getUnits
argument_list|()
expr_stmt|;
block|}
name|strings
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"-"
operator|+
name|units
operator|+
literal|"seconds"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|strings
argument_list|)
return|;
block|}
block|}
end_class

end_unit

