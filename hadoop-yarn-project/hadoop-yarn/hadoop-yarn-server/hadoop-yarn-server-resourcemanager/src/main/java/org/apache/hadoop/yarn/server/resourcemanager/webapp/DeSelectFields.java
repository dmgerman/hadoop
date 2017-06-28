begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
package|;
end_package

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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|BadRequestException
import|;
end_import

begin_comment
comment|/**  * DeSelectFields make the<code>/apps</code> api more flexible.  * It can be used to strip off more fields if there's such use case in future.  * You can simply extend it via two steps:  *<br> 1. add a<code>DeSelectType</code> enum with a string literals  *<br> 2. write your logical based on  * the return of method contains(DeSelectType)  */
end_comment

begin_class
DECL|class|DeSelectFields
specifier|public
class|class
name|DeSelectFields
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DeSelectFields
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|Set
argument_list|<
name|DeSelectType
argument_list|>
name|types
decl_stmt|;
DECL|method|DeSelectFields ()
specifier|public
name|DeSelectFields
parameter_list|()
block|{
name|this
operator|.
name|types
operator|=
operator|new
name|HashSet
argument_list|<
name|DeSelectType
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Initial DeSelectFields with unselected fields.    * @param unselectedFields a set of unselected field.    */
DECL|method|initFields (Set<String> unselectedFields)
specifier|public
name|void
name|initFields
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|unselectedFields
parameter_list|)
block|{
if|if
condition|(
name|unselectedFields
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|field
range|:
name|unselectedFields
control|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
index|[]
name|literalsArray
init|=
name|field
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|literals
range|:
name|literalsArray
control|)
block|{
if|if
condition|(
name|literals
operator|!=
literal|null
operator|&&
operator|!
name|literals
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|DeSelectType
name|type
init|=
name|DeSelectType
operator|.
name|obtainType
argument_list|(
name|literals
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid deSelects string "
operator|+
name|literals
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|DeSelectType
index|[]
name|typeArray
init|=
name|DeSelectType
operator|.
name|values
argument_list|()
decl_stmt|;
name|String
name|allSuppportLiterals
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|typeArray
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid deSelects string "
operator|+
name|literals
operator|.
name|trim
argument_list|()
operator|+
literal|" specified. It should be one of "
operator|+
name|allSuppportLiterals
argument_list|)
throw|;
block|}
else|else
block|{
name|this
operator|.
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**    * Determine the deselect type should be handled or not.    * @param type deselected type    * @return true if the deselect type should be handled    */
DECL|method|contains (DeSelectType type)
specifier|public
name|boolean
name|contains
parameter_list|(
name|DeSelectType
name|type
parameter_list|)
block|{
return|return
name|types
operator|.
name|contains
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**    * Deselect field type, can be boost in future.    */
DECL|enum|DeSelectType
specifier|public
enum|enum
name|DeSelectType
block|{
comment|/**      *<code>RESOURCE_REQUESTS</code> is the first      * supported type from YARN-6280.      */
DECL|enumConstant|RESOURCE_REQUESTS
name|RESOURCE_REQUESTS
argument_list|(
literal|"resourceRequests"
argument_list|)
block|;
DECL|field|literals
specifier|private
specifier|final
name|String
name|literals
decl_stmt|;
DECL|method|DeSelectType (String literals)
name|DeSelectType
parameter_list|(
name|String
name|literals
parameter_list|)
block|{
name|this
operator|.
name|literals
operator|=
name|literals
expr_stmt|;
block|}
comment|/**      * use literals as toString.      * @return the literals of this type.      */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|literals
return|;
block|}
comment|/**      * Obtain the<code>DeSelectType</code> by the literals given behind      *<code>deSelects</code> in URL.      *<br> e.g: deSelects="resourceRequests"      * @param literals e.g: resourceRequests      * @return<code>DeSelectType</code> e.g: DeSelectType.RESOURCE_REQUESTS      */
DECL|method|obtainType (String literals)
specifier|public
specifier|static
name|DeSelectType
name|obtainType
parameter_list|(
name|String
name|literals
parameter_list|)
block|{
for|for
control|(
name|DeSelectType
name|type
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|literals
operator|.
name|equalsIgnoreCase
argument_list|(
name|literals
argument_list|)
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

