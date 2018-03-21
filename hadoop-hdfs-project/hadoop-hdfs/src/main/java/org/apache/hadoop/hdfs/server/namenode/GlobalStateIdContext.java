begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|classification
operator|.
name|InterfaceStability
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
name|AlignmentContext
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
name|protobuf
operator|.
name|RpcHeaderProtos
operator|.
name|RpcResponseHeaderProto
import|;
end_import

begin_comment
comment|/**  * This is the server side implementation responsible for passing  * state alignment info to clients.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|GlobalStateIdContext
class|class
name|GlobalStateIdContext
implements|implements
name|AlignmentContext
block|{
DECL|field|namesystem
specifier|private
specifier|final
name|FSNamesystem
name|namesystem
decl_stmt|;
comment|/**    * Server side constructor.    * @param namesystem server side state provider    */
DECL|method|GlobalStateIdContext (FSNamesystem namesystem)
name|GlobalStateIdContext
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|)
block|{
name|this
operator|.
name|namesystem
operator|=
name|namesystem
expr_stmt|;
block|}
comment|/**    * Server side implementation for providing state alignment info.    */
annotation|@
name|Override
DECL|method|updateResponseState (RpcResponseHeaderProto.Builder header)
specifier|public
name|void
name|updateResponseState
parameter_list|(
name|RpcResponseHeaderProto
operator|.
name|Builder
name|header
parameter_list|)
block|{
name|header
operator|.
name|setStateId
argument_list|(
name|namesystem
operator|.
name|getLastWrittenTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Server side implementation only provides state alignment info.    * It does not receive state alignment info therefore this does nothing.    */
annotation|@
name|Override
DECL|method|receiveResponseState (RpcResponseHeaderProto header)
specifier|public
name|void
name|receiveResponseState
parameter_list|(
name|RpcResponseHeaderProto
name|header
parameter_list|)
block|{
comment|// Do nothing.
block|}
block|}
end_class

end_unit

