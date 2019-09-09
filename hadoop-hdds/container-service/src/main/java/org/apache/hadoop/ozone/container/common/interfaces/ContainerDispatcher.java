begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
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
name|interfaces
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|transport
operator|.
name|server
operator|.
name|ratis
operator|.
name|DispatcherContext
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

begin_comment
comment|/**  * Dispatcher acts as the bridge between the transport layer and  * the actual container layer. This layer is capable of transforming  * protobuf objects into corresponding class and issue the function call  * into the lower layers.  *  * The reply from the request is dispatched to the client.  */
end_comment

begin_interface
DECL|interface|ContainerDispatcher
specifier|public
interface|interface
name|ContainerDispatcher
block|{
comment|/**    * Dispatches commands to container layer.    * @param msg - Command Request    * @param context - Context info related to ContainerStateMachine    * @return Command Response    */
DECL|method|dispatch (ContainerCommandRequestProto msg, DispatcherContext context)
name|ContainerCommandResponseProto
name|dispatch
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|DispatcherContext
name|context
parameter_list|)
function_decl|;
comment|/**    * Validates whether the container command should be executed on the pipeline    * or not. Will be invoked by the leader node in the Ratis pipeline    * @param msg containerCommand    * @throws StorageContainerException    */
DECL|method|validateContainerCommand ( ContainerCommandRequestProto msg)
name|void
name|validateContainerCommand
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Initialize the Dispatcher.    */
DECL|method|init ()
name|void
name|init
parameter_list|()
function_decl|;
comment|/**    * finds and builds the missing containers in case of a lost disk etc    * in the ContainerSet. It also validates the BCSID of the containers found.    */
DECL|method|buildMissingContainerSetAndValidate (Map<Long, Long> container2BCSIDMap)
name|void
name|buildMissingContainerSetAndValidate
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|container2BCSIDMap
parameter_list|)
function_decl|;
comment|/**    * Shutdown Dispatcher services.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|/**    * Returns the handler for the specified containerType.    * @param containerType    * @return    */
DECL|method|getHandler (ContainerProtos.ContainerType containerType)
name|Handler
name|getHandler
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerType
name|containerType
parameter_list|)
function_decl|;
comment|/**    * If scmId is not set, this will set scmId, otherwise it is a no-op.    * @param scmId    */
DECL|method|setScmId (String scmId)
name|void
name|setScmId
parameter_list|(
name|String
name|scmId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

