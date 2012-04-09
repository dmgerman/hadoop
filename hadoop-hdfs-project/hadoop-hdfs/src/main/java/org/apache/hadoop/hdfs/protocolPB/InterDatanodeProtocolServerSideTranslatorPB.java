begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolPB
package|;
end_package

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
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|InterDatanodeProtocolProtos
operator|.
name|InitReplicaRecoveryRequestProto
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
name|protocol
operator|.
name|proto
operator|.
name|InterDatanodeProtocolProtos
operator|.
name|InitReplicaRecoveryResponseProto
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
name|protocol
operator|.
name|proto
operator|.
name|InterDatanodeProtocolProtos
operator|.
name|UpdateReplicaUnderRecoveryRequestProto
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
name|protocol
operator|.
name|proto
operator|.
name|InterDatanodeProtocolProtos
operator|.
name|UpdateReplicaUnderRecoveryResponseProto
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
name|protocol
operator|.
name|BlockRecoveryCommand
operator|.
name|RecoveringBlock
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
name|protocol
operator|.
name|InterDatanodeProtocol
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
name|protocol
operator|.
name|ReplicaRecoveryInfo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
import|;
end_import

begin_comment
comment|/**  * Implementation for protobuf service that forwards requests  * received on {@link InterDatanodeProtocolPB} to the  * {@link InterDatanodeProtocol} server implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|InterDatanodeProtocolServerSideTranslatorPB
specifier|public
class|class
name|InterDatanodeProtocolServerSideTranslatorPB
implements|implements
name|InterDatanodeProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|InterDatanodeProtocol
name|impl
decl_stmt|;
DECL|method|InterDatanodeProtocolServerSideTranslatorPB (InterDatanodeProtocol impl)
specifier|public
name|InterDatanodeProtocolServerSideTranslatorPB
parameter_list|(
name|InterDatanodeProtocol
name|impl
parameter_list|)
block|{
name|this
operator|.
name|impl
operator|=
name|impl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initReplicaRecovery ( RpcController unused, InitReplicaRecoveryRequestProto request)
specifier|public
name|InitReplicaRecoveryResponseProto
name|initReplicaRecovery
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|InitReplicaRecoveryRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RecoveringBlock
name|b
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getBlock
argument_list|()
argument_list|)
decl_stmt|;
name|ReplicaRecoveryInfo
name|r
decl_stmt|;
try|try
block|{
name|r
operator|=
name|impl
operator|.
name|initReplicaRecovery
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
return|return
name|InitReplicaRecoveryResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setReplicaFound
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|InitReplicaRecoveryResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setReplicaFound
argument_list|(
literal|true
argument_list|)
operator|.
name|setBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|r
argument_list|)
argument_list|)
operator|.
name|setState
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|r
operator|.
name|getOriginalReplicaState
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateReplicaUnderRecovery ( RpcController unused, UpdateReplicaUnderRecoveryRequestProto request)
specifier|public
name|UpdateReplicaUnderRecoveryResponseProto
name|updateReplicaUnderRecovery
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|UpdateReplicaUnderRecoveryRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
specifier|final
name|String
name|storageID
decl_stmt|;
try|try
block|{
name|storageID
operator|=
name|impl
operator|.
name|updateReplicaUnderRecovery
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|request
operator|.
name|getRecoveryId
argument_list|()
argument_list|,
name|request
operator|.
name|getNewLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|UpdateReplicaUnderRecoveryResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStorageID
argument_list|(
name|storageID
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

