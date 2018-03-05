begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
package|;
end_package

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
name|exceptions
operator|.
name|InvalidAllocationTagException
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
name|Set
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
comment|/**  * Class to describe all supported forms of namespaces for an allocation tag.  */
end_comment

begin_enum
DECL|enum|AllocationTagNamespaceType
specifier|public
enum|enum
name|AllocationTagNamespaceType
block|{
DECL|enumConstant|SELF
name|SELF
argument_list|(
literal|"self"
argument_list|)
block|,
DECL|enumConstant|NOT_SELF
name|NOT_SELF
argument_list|(
literal|"not-self"
argument_list|)
block|,
DECL|enumConstant|APP_ID
name|APP_ID
argument_list|(
literal|"app-id"
argument_list|)
block|,
DECL|enumConstant|APP_LABEL
name|APP_LABEL
argument_list|(
literal|"app-label"
argument_list|)
block|,
DECL|enumConstant|ALL
name|ALL
argument_list|(
literal|"all"
argument_list|)
block|;
DECL|field|typeKeyword
specifier|private
name|String
name|typeKeyword
decl_stmt|;
DECL|method|AllocationTagNamespaceType (String keyword)
name|AllocationTagNamespaceType
parameter_list|(
name|String
name|keyword
parameter_list|)
block|{
name|this
operator|.
name|typeKeyword
operator|=
name|keyword
expr_stmt|;
block|}
DECL|method|getTypeKeyword ()
specifier|public
name|String
name|getTypeKeyword
parameter_list|()
block|{
return|return
name|this
operator|.
name|typeKeyword
return|;
block|}
comment|/**    * Parses the namespace type from a given string.    * @param prefix namespace prefix.    * @return namespace type.    * @throws InvalidAllocationTagException    */
DECL|method|fromString (String prefix)
specifier|public
specifier|static
name|AllocationTagNamespaceType
name|fromString
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|InvalidAllocationTagException
block|{
for|for
control|(
name|AllocationTagNamespaceType
name|type
range|:
name|AllocationTagNamespaceType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|getTypeKeyword
argument_list|()
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|AllocationTagNamespaceType
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|AllocationTagNamespaceType
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|InvalidAllocationTagException
argument_list|(
literal|"Invalid namespace prefix: "
operator|+
name|prefix
operator|+
literal|", valid values are: "
operator|+
name|String
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|values
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getTypeKeyword
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

