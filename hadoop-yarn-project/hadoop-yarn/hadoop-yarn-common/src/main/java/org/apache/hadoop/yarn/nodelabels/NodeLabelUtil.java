begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
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
name|Strings
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
name|NodeAttribute
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
name|NodeAttributeKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Utility class for all NodeLabel and NodeAttribute operations.  */
end_comment

begin_class
DECL|class|NodeLabelUtil
specifier|public
specifier|final
class|class
name|NodeLabelUtil
block|{
DECL|method|NodeLabelUtil ()
specifier|private
name|NodeLabelUtil
parameter_list|()
block|{   }
DECL|field|MAX_LABEL_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LABEL_LENGTH
init|=
literal|255
decl_stmt|;
DECL|field|LABEL_OR_VALUE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|LABEL_OR_VALUE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[0-9a-zA-Z][0-9a-zA-Z-_]*"
argument_list|)
decl_stmt|;
DECL|field|PREFIX_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|PREFIX_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[0-9a-zA-Z][0-9a-zA-Z-_\\.]*"
argument_list|)
decl_stmt|;
DECL|field|ATTRIBUTE_VALUE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|ATTRIBUTE_VALUE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[0-9a-zA-Z][0-9a-zA-Z-_.]*"
argument_list|)
decl_stmt|;
DECL|field|ATTRIBUTE_NAME_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|ATTRIBUTE_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[0-9a-zA-Z][0-9a-zA-Z-_]*"
argument_list|)
decl_stmt|;
DECL|method|checkAndThrowLabelName (String label)
specifier|public
specifier|static
name|void
name|checkAndThrowLabelName
parameter_list|(
name|String
name|label
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|label
operator|==
literal|null
operator|||
name|label
operator|.
name|isEmpty
argument_list|()
operator|||
name|label
operator|.
name|length
argument_list|()
operator|>
name|MAX_LABEL_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"label added is empty or exceeds "
operator|+
name|MAX_LABEL_LENGTH
operator|+
literal|" character(s)"
argument_list|)
throw|;
block|}
name|label
operator|=
name|label
operator|.
name|trim
argument_list|()
expr_stmt|;
name|boolean
name|match
init|=
name|LABEL_OR_VALUE_PATTERN
operator|.
name|matcher
argument_list|(
name|label
argument_list|)
operator|.
name|matches
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|match
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"label name should only contains "
operator|+
literal|"{0-9, a-z, A-Z, -, _} and should not started with {-,_}"
operator|+
literal|", now it is= "
operator|+
name|label
argument_list|)
throw|;
block|}
block|}
DECL|method|checkAndThrowAttributeName (String attributeName)
specifier|public
specifier|static
name|void
name|checkAndThrowAttributeName
parameter_list|(
name|String
name|attributeName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|attributeName
operator|==
literal|null
operator|||
name|attributeName
operator|.
name|isEmpty
argument_list|()
operator|||
name|attributeName
operator|.
name|length
argument_list|()
operator|>
name|MAX_LABEL_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"attribute name added is empty or exceeds "
operator|+
name|MAX_LABEL_LENGTH
operator|+
literal|" character(s)"
argument_list|)
throw|;
block|}
name|attributeName
operator|=
name|attributeName
operator|.
name|trim
argument_list|()
expr_stmt|;
name|boolean
name|match
init|=
name|ATTRIBUTE_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|attributeName
argument_list|)
operator|.
name|matches
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|match
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"attribute name should only contains "
operator|+
literal|"{0-9, a-z, A-Z, -, _} and should not started with {-,_}"
operator|+
literal|", now it is= "
operator|+
name|attributeName
argument_list|)
throw|;
block|}
block|}
DECL|method|checkAndThrowAttributeValue (String value)
specifier|public
specifier|static
name|void
name|checkAndThrowAttributeValue
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
name|MAX_LABEL_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attribute value added exceeds "
operator|+
name|MAX_LABEL_LENGTH
operator|+
literal|" character(s)"
argument_list|)
throw|;
block|}
name|value
operator|=
name|value
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|boolean
name|match
init|=
name|ATTRIBUTE_VALUE_PATTERN
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
operator|.
name|matches
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|match
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"attribute value should only contains "
operator|+
literal|"{0-9, a-z, A-Z, -, _} and should not started with {-,_}"
operator|+
literal|", now it is= "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
DECL|method|checkAndThrowAttributePrefix (String prefix)
specifier|public
specifier|static
name|void
name|checkAndThrowAttributePrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attribute prefix cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|prefix
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
name|MAX_LABEL_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attribute value added exceeds "
operator|+
name|MAX_LABEL_LENGTH
operator|+
literal|" character(s)"
argument_list|)
throw|;
block|}
name|prefix
operator|=
name|prefix
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|boolean
name|match
init|=
name|PREFIX_PATTERN
operator|.
name|matcher
argument_list|(
name|prefix
argument_list|)
operator|.
name|matches
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|match
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"attribute value should only contains "
operator|+
literal|"{0-9, a-z, A-Z, -, _,.} and should not started with {-,_}"
operator|+
literal|", now it is= "
operator|+
name|prefix
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validate if a given set of attributes are valid. Attributes could be    * invalid if any of following conditions is met:    *    *<ul>    *<li>Missing prefix: the attribute doesn't have prefix defined</li>    *<li>Malformed attribute prefix: the prefix is not in valid format</li>    *</ul>    * @param attributeSet    * @throws IOException    */
DECL|method|validateNodeAttributes (Set<NodeAttribute> attributeSet)
specifier|public
specifier|static
name|void
name|validateNodeAttributes
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributeSet
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|attributeSet
operator|!=
literal|null
operator|&&
operator|!
name|attributeSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|NodeAttribute
name|nodeAttribute
range|:
name|attributeSet
control|)
block|{
name|NodeAttributeKey
name|attributeKey
init|=
name|nodeAttribute
operator|.
name|getAttributeKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|attributeKey
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"AttributeKey  must be set"
argument_list|)
throw|;
block|}
name|String
name|prefix
init|=
name|attributeKey
operator|.
name|getAttributePrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attribute prefix must be set"
argument_list|)
throw|;
block|}
comment|// Verify attribute prefix format.
name|checkAndThrowAttributePrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
comment|// Verify attribute name format.
name|checkAndThrowAttributeName
argument_list|(
name|attributeKey
operator|.
name|getAttributeName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify attribute value format.
name|checkAndThrowAttributeValue
argument_list|(
name|nodeAttribute
operator|.
name|getAttributeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Filter a set of node attributes by a given prefix. Returns a filtered    * set of node attributes whose prefix equals the given prefix.    * If the prefix is null or empty, then the original set is returned.    * @param attributeSet node attribute set    * @param prefix node attribute prefix    * @return a filtered set of node attributes    */
DECL|method|filterAttributesByPrefix ( Set<NodeAttribute> attributeSet, String prefix)
specifier|public
specifier|static
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|filterAttributesByPrefix
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributeSet
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
name|attributeSet
return|;
block|}
return|return
name|attributeSet
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|nodeAttribute
lambda|->
name|prefix
operator|.
name|equals
argument_list|(
name|nodeAttribute
operator|.
name|getAttributeKey
argument_list|()
operator|.
name|getAttributePrefix
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Are these two input node attributes the same.    * @return true if they are the same    */
DECL|method|isNodeAttributesEquals ( Set<NodeAttribute> leftNodeAttributes, Set<NodeAttribute> rightNodeAttributes)
specifier|public
specifier|static
name|boolean
name|isNodeAttributesEquals
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|leftNodeAttributes
parameter_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|rightNodeAttributes
parameter_list|)
block|{
if|if
condition|(
name|leftNodeAttributes
operator|==
literal|null
operator|&&
name|rightNodeAttributes
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|leftNodeAttributes
operator|==
literal|null
operator|||
name|rightNodeAttributes
operator|==
literal|null
operator|||
name|leftNodeAttributes
operator|.
name|size
argument_list|()
operator|!=
name|rightNodeAttributes
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|leftNodeAttributes
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|e
lambda|->
name|isNodeAttributeIncludes
argument_list|(
name|rightNodeAttributes
argument_list|,
name|e
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isNodeAttributeIncludes ( Set<NodeAttribute> nodeAttributes, NodeAttribute checkNodeAttribute)
specifier|private
specifier|static
name|boolean
name|isNodeAttributeIncludes
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|nodeAttributes
parameter_list|,
name|NodeAttribute
name|checkNodeAttribute
parameter_list|)
block|{
return|return
name|nodeAttributes
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|e
lambda|->
name|e
operator|.
name|equals
argument_list|(
name|checkNodeAttribute
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getAttributeValue
argument_list|()
argument_list|,
name|checkNodeAttribute
operator|.
name|getAttributeValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

