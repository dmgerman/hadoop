begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Enum representing queue state  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|QueueState
specifier|public
enum|enum
name|QueueState
block|{
DECL|enumConstant|STOPPED
DECL|enumConstant|RUNNING
DECL|enumConstant|UNDEFINED
name|STOPPED
argument_list|(
literal|"stopped"
argument_list|)
block|,
name|RUNNING
argument_list|(
literal|"running"
argument_list|)
block|,
name|UNDEFINED
argument_list|(
literal|"undefined"
argument_list|)
block|;
DECL|field|stateName
specifier|private
specifier|final
name|String
name|stateName
decl_stmt|;
DECL|field|enumMap
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|QueueState
argument_list|>
name|enumMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueueState
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|QueueState
name|state
range|:
name|QueueState
operator|.
name|values
argument_list|()
control|)
block|{
name|enumMap
operator|.
name|put
argument_list|(
name|state
operator|.
name|getStateName
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|QueueState (String stateName)
name|QueueState
parameter_list|(
name|String
name|stateName
parameter_list|)
block|{
name|this
operator|.
name|stateName
operator|=
name|stateName
expr_stmt|;
block|}
comment|/**    * @return the stateName    */
DECL|method|getStateName ()
specifier|public
name|String
name|getStateName
parameter_list|()
block|{
return|return
name|stateName
return|;
block|}
DECL|method|getState (String state)
specifier|public
specifier|static
name|QueueState
name|getState
parameter_list|(
name|String
name|state
parameter_list|)
block|{
name|QueueState
name|qState
init|=
name|enumMap
operator|.
name|get
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|qState
operator|==
literal|null
condition|)
block|{
return|return
name|UNDEFINED
return|;
block|}
return|return
name|qState
return|;
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
name|stateName
return|;
block|}
block|}
end_enum

end_unit

