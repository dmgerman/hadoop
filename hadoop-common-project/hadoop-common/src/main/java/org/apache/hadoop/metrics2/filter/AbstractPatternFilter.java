begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.filter
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|filter
package|;
end_package

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
name|Matcher
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|configuration
operator|.
name|SubsetConfiguration
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
name|metrics2
operator|.
name|MetricsException
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
name|metrics2
operator|.
name|MetricsFilter
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
name|metrics2
operator|.
name|MetricsTag
import|;
end_import

begin_comment
comment|/**  * Base class for pattern based filters  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractPatternFilter
specifier|public
specifier|abstract
class|class
name|AbstractPatternFilter
extends|extends
name|MetricsFilter
block|{
DECL|field|INCLUDE_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|INCLUDE_KEY
init|=
literal|"include"
decl_stmt|;
DECL|field|EXCLUDE_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|EXCLUDE_KEY
init|=
literal|"exclude"
decl_stmt|;
DECL|field|INCLUDE_TAGS_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|INCLUDE_TAGS_KEY
init|=
literal|"include.tags"
decl_stmt|;
DECL|field|EXCLUDE_TAGS_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|EXCLUDE_TAGS_KEY
init|=
literal|"exclude.tags"
decl_stmt|;
DECL|field|includePattern
specifier|private
name|Pattern
name|includePattern
decl_stmt|;
DECL|field|excludePattern
specifier|private
name|Pattern
name|excludePattern
decl_stmt|;
DECL|field|includeTagPatterns
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|includeTagPatterns
decl_stmt|;
DECL|field|excludeTagPatterns
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Pattern
argument_list|>
name|excludeTagPatterns
decl_stmt|;
DECL|field|tagPattern
specifier|private
specifier|final
name|Pattern
name|tagPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(\\w+):(.*)"
argument_list|)
decl_stmt|;
DECL|method|AbstractPatternFilter ()
name|AbstractPatternFilter
parameter_list|()
block|{
name|includeTagPatterns
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|excludeTagPatterns
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (SubsetConfiguration conf)
specifier|public
name|void
name|init
parameter_list|(
name|SubsetConfiguration
name|conf
parameter_list|)
block|{
name|String
name|patternString
init|=
name|conf
operator|.
name|getString
argument_list|(
name|INCLUDE_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|patternString
operator|!=
literal|null
operator|&&
operator|!
name|patternString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setIncludePattern
argument_list|(
name|compile
argument_list|(
name|patternString
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|patternString
operator|=
name|conf
operator|.
name|getString
argument_list|(
name|EXCLUDE_KEY
argument_list|)
expr_stmt|;
if|if
condition|(
name|patternString
operator|!=
literal|null
operator|&&
operator|!
name|patternString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setExcludePattern
argument_list|(
name|compile
argument_list|(
name|patternString
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|patternStrings
init|=
name|conf
operator|.
name|getStringArray
argument_list|(
name|INCLUDE_TAGS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|patternStrings
operator|!=
literal|null
operator|&&
name|patternStrings
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|String
name|pstr
range|:
name|patternStrings
control|)
block|{
name|Matcher
name|matcher
init|=
name|tagPattern
operator|.
name|matcher
argument_list|(
name|pstr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Illegal tag pattern: "
operator|+
name|pstr
argument_list|)
throw|;
block|}
name|setIncludeTagPattern
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|compile
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|patternStrings
operator|=
name|conf
operator|.
name|getStringArray
argument_list|(
name|EXCLUDE_TAGS_KEY
argument_list|)
expr_stmt|;
if|if
condition|(
name|patternStrings
operator|!=
literal|null
operator|&&
name|patternStrings
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|String
name|pstr
range|:
name|patternStrings
control|)
block|{
name|Matcher
name|matcher
init|=
name|tagPattern
operator|.
name|matcher
argument_list|(
name|pstr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Illegal tag pattern: "
operator|+
name|pstr
argument_list|)
throw|;
block|}
name|setExcludeTagPattern
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|compile
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setIncludePattern (Pattern includePattern)
name|void
name|setIncludePattern
parameter_list|(
name|Pattern
name|includePattern
parameter_list|)
block|{
name|this
operator|.
name|includePattern
operator|=
name|includePattern
expr_stmt|;
block|}
DECL|method|setExcludePattern (Pattern excludePattern)
name|void
name|setExcludePattern
parameter_list|(
name|Pattern
name|excludePattern
parameter_list|)
block|{
name|this
operator|.
name|excludePattern
operator|=
name|excludePattern
expr_stmt|;
block|}
DECL|method|setIncludeTagPattern (String name, Pattern pattern)
name|void
name|setIncludeTagPattern
parameter_list|(
name|String
name|name
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
name|includeTagPatterns
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
DECL|method|setExcludeTagPattern (String name, Pattern pattern)
name|void
name|setExcludeTagPattern
parameter_list|(
name|String
name|name
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
name|excludeTagPatterns
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accepts (MetricsTag tag)
specifier|public
name|boolean
name|accepts
parameter_list|(
name|MetricsTag
name|tag
parameter_list|)
block|{
comment|// Accept if whitelisted
name|Pattern
name|ipat
init|=
name|includeTagPatterns
operator|.
name|get
argument_list|(
name|tag
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ipat
operator|!=
literal|null
operator|&&
name|ipat
operator|.
name|matcher
argument_list|(
name|tag
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Reject if blacklisted
name|Pattern
name|epat
init|=
name|excludeTagPatterns
operator|.
name|get
argument_list|(
name|tag
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|epat
operator|!=
literal|null
operator|&&
name|epat
operator|.
name|matcher
argument_list|(
name|tag
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Reject if no match in whitelist only mode
if|if
condition|(
operator|!
name|includeTagPatterns
operator|.
name|isEmpty
argument_list|()
operator|&&
name|excludeTagPatterns
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|accepts (Iterable<MetricsTag> tags)
specifier|public
name|boolean
name|accepts
parameter_list|(
name|Iterable
argument_list|<
name|MetricsTag
argument_list|>
name|tags
parameter_list|)
block|{
comment|// Accept if any include tag pattern matches
for|for
control|(
name|MetricsTag
name|t
range|:
name|tags
control|)
block|{
name|Pattern
name|pat
init|=
name|includeTagPatterns
operator|.
name|get
argument_list|(
name|t
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pat
operator|!=
literal|null
operator|&&
name|pat
operator|.
name|matcher
argument_list|(
name|t
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// Reject if any exclude tag pattern matches
for|for
control|(
name|MetricsTag
name|t
range|:
name|tags
control|)
block|{
name|Pattern
name|pat
init|=
name|excludeTagPatterns
operator|.
name|get
argument_list|(
name|t
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pat
operator|!=
literal|null
operator|&&
name|pat
operator|.
name|matcher
argument_list|(
name|t
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Reject if no match in whitelist only mode
if|if
condition|(
operator|!
name|includeTagPatterns
operator|.
name|isEmpty
argument_list|()
operator|&&
name|excludeTagPatterns
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|accepts (String name)
specifier|public
name|boolean
name|accepts
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// Accept if whitelisted
if|if
condition|(
name|includePattern
operator|!=
literal|null
operator|&&
name|includePattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Reject if blacklisted
if|if
condition|(
operator|(
name|excludePattern
operator|!=
literal|null
operator|&&
name|excludePattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Reject if no match in whitelist only mode
if|if
condition|(
name|includePattern
operator|!=
literal|null
operator|&&
name|excludePattern
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Compile a string pattern in to a pattern object    * @param s the string pattern to compile    * @return the compiled pattern object    */
DECL|method|compile (String s)
specifier|protected
specifier|abstract
name|Pattern
name|compile
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
block|}
end_class

end_unit

