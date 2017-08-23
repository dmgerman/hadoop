begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client.params
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|client
operator|.
name|params
package|;
end_package

begin_import
import|import
name|com
operator|.
name|beust
operator|.
name|jcommander
operator|.
name|Parameter
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
name|service
operator|.
name|exceptions
operator|.
name|BadCommandArgumentsException
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

begin_class
DECL|class|ComponentArgsDelegate
specifier|public
class|class
name|ComponentArgsDelegate
extends|extends
name|AbstractArgsDelegate
block|{
comment|/**    * This is a listing of the roles to create    */
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
block|{
name|ARG_COMPONENT
block|,
name|ARG_COMPONENT_SHORT
block|}
argument_list|,
name|arity
operator|=
literal|2
argument_list|,
name|description
operator|=
literal|"--component<name><count> e.g. +1 incr by 1, -2 decr by 2, and 3 makes final count 3"
argument_list|,
name|splitter
operator|=
name|DontSplitArguments
operator|.
name|class
argument_list|)
DECL|field|componentTuples
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|componentTuples
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Get the role mapping (may be empty, but never null)    * @return role mapping    * @throws BadCommandArgumentsException parse problem    */
DECL|method|getComponentMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getComponentMap
parameter_list|()
throws|throws
name|BadCommandArgumentsException
block|{
return|return
name|convertTupleListToMap
argument_list|(
literal|"component"
argument_list|,
name|componentTuples
argument_list|)
return|;
block|}
DECL|method|getComponentTuples ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getComponentTuples
parameter_list|()
block|{
return|return
name|componentTuples
return|;
block|}
block|}
end_class

end_unit

