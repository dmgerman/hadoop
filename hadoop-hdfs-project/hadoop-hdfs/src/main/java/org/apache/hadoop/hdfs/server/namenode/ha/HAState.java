begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|ha
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
name|ha
operator|.
name|ServiceFailedException
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
operator|.
name|OperationCategory
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|UnsupportedActionException
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
name|ipc
operator|.
name|StandbyException
import|;
end_import

begin_comment
comment|/**  * Namenode base state to implement state machine pattern.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HAState
specifier|abstract
specifier|public
class|class
name|HAState
block|{
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
comment|/**    * Constructor    * @param name Name of the state.    */
DECL|method|HAState (String name)
specifier|public
name|HAState
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * Internal method to transition the state of a given namenode to a new state.    * @param nn Namenode    * @param s new state    * @throws ServiceFailedException on failure to transition to new state.    */
DECL|method|setStateInternal (final HAContext context, final HAState s)
specifier|protected
specifier|final
name|void
name|setStateInternal
parameter_list|(
specifier|final
name|HAContext
name|context
parameter_list|,
specifier|final
name|HAState
name|s
parameter_list|)
throws|throws
name|ServiceFailedException
block|{
name|exitState
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|setState
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|s
operator|.
name|enterState
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * Method to be overridden by subclasses to perform steps necessary for    * entering a state.    * @param context HA context    * @throws ServiceFailedException on failure to enter the state.    */
DECL|method|enterState (final HAContext context)
specifier|public
specifier|abstract
name|void
name|enterState
parameter_list|(
specifier|final
name|HAContext
name|context
parameter_list|)
throws|throws
name|ServiceFailedException
function_decl|;
comment|/**    * Method to be overridden by subclasses to perform steps necessary for    * exiting a state.    * @param context HA context    * @throws ServiceFailedException on failure to enter the state.    */
DECL|method|exitState (final HAContext context)
specifier|public
specifier|abstract
name|void
name|exitState
parameter_list|(
specifier|final
name|HAContext
name|context
parameter_list|)
throws|throws
name|ServiceFailedException
function_decl|;
comment|/**    * Move from the existing state to a new state    * @param context HA context    * @param s new state    * @throws ServiceFailedException on failure to transition to new state.    */
DECL|method|setState (HAContext context, HAState s)
specifier|public
name|void
name|setState
parameter_list|(
name|HAContext
name|context
parameter_list|,
name|HAState
name|s
parameter_list|)
throws|throws
name|ServiceFailedException
block|{
if|if
condition|(
name|this
operator|==
name|s
condition|)
block|{
comment|// Aleady in the new state
return|return;
block|}
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Transtion from state "
operator|+
name|this
operator|+
literal|" to "
operator|+
name|s
operator|+
literal|" is not allowed."
argument_list|)
throw|;
block|}
comment|/**    * Check if an operation is supported in a given state.    * @param context HA context    * @param op Type of the operation.    * @throws UnsupportedActionException if a given type of operation is not    *           supported in this state.    */
DECL|method|checkOperation (final HAContext context, final OperationCategory op)
specifier|public
specifier|abstract
name|void
name|checkOperation
parameter_list|(
specifier|final
name|HAContext
name|context
parameter_list|,
specifier|final
name|OperationCategory
name|op
parameter_list|)
throws|throws
name|StandbyException
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

