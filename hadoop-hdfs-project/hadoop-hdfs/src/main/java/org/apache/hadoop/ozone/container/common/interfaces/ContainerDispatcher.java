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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
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
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
comment|/**    * Dispatches commands to container layer.    * @param msg - Command Request    * @return Command Response    */
DECL|method|dispatch (ContainerCommandRequestProto msg)
name|ContainerCommandResponseProto
name|dispatch
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
function_decl|;
comment|/**    * Initialize the Dispatcher.    */
DECL|method|init ()
name|void
name|init
parameter_list|()
function_decl|;
comment|/**    * Shutdown Dispatcher services.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

