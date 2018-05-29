begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine.commandhandler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|commandhandler
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|SCMConnectionManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
import|;
end_import

begin_comment
comment|/**  * Generic interface for handlers.  */
end_comment

begin_interface
DECL|interface|CommandHandler
specifier|public
interface|interface
name|CommandHandler
block|{
comment|/**    * Handles a given SCM command.    * @param command - SCM Command    * @param container - Ozone Container.    * @param context - Current Context.    * @param connectionManager - The SCMs that we are talking to.    */
DECL|method|handle (SCMCommand command, OzoneContainer container, StateContext context, SCMConnectionManager connectionManager)
name|void
name|handle
parameter_list|(
name|SCMCommand
name|command
parameter_list|,
name|OzoneContainer
name|container
parameter_list|,
name|StateContext
name|context
parameter_list|,
name|SCMConnectionManager
name|connectionManager
parameter_list|)
function_decl|;
comment|/**    * Returns the command type that this command handler handles.    * @return Type    */
DECL|method|getCommandType ()
name|SCMCommandProto
operator|.
name|Type
name|getCommandType
parameter_list|()
function_decl|;
comment|/**    * Returns number of times this handler has been invoked.    * @return int    */
DECL|method|getInvocationCount ()
name|int
name|getInvocationCount
parameter_list|()
function_decl|;
comment|/**    * Returns the average time this function takes to run.    * @return  long    */
DECL|method|getAverageRunTime ()
name|long
name|getAverageRunTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

