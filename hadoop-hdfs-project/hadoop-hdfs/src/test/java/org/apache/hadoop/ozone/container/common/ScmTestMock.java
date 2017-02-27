begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common
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
name|protocol
operator|.
name|DatanodeID
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
name|StorageContainerDatanodeProtocol
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
name|VersionResponse
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
name|NullCommand
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|scm
operator|.
name|VersionInfo
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * SCM RPC mock class.  */
end_comment

begin_class
DECL|class|ScmTestMock
specifier|public
class|class
name|ScmTestMock
implements|implements
name|StorageContainerDatanodeProtocol
block|{
DECL|field|rpcResponseDelay
specifier|private
name|int
name|rpcResponseDelay
decl_stmt|;
DECL|field|heartbeatCount
specifier|private
name|AtomicInteger
name|heartbeatCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|rpcCount
specifier|private
name|AtomicInteger
name|rpcCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Returns the number of heartbeats made to this class.    *    * @return int    */
DECL|method|getHeartbeatCount ()
specifier|public
name|int
name|getHeartbeatCount
parameter_list|()
block|{
return|return
name|heartbeatCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns the number of RPC calls made to this mock class instance.    *    * @return - Number of RPC calls serviced by this class.    */
DECL|method|getRpcCount ()
specifier|public
name|int
name|getRpcCount
parameter_list|()
block|{
return|return
name|rpcCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Gets the RPC response delay.    *    * @return delay in milliseconds.    */
DECL|method|getRpcResponseDelay ()
specifier|public
name|int
name|getRpcResponseDelay
parameter_list|()
block|{
return|return
name|rpcResponseDelay
return|;
block|}
comment|/**    * Sets the RPC response delay.    *    * @param rpcResponseDelay - delay in milliseconds.    */
DECL|method|setRpcResponseDelay (int rpcResponseDelay)
specifier|public
name|void
name|setRpcResponseDelay
parameter_list|(
name|int
name|rpcResponseDelay
parameter_list|)
block|{
name|this
operator|.
name|rpcResponseDelay
operator|=
name|rpcResponseDelay
expr_stmt|;
block|}
comment|/**    * Returns SCM version.    *    * @return Version info.    */
annotation|@
name|Override
specifier|public
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionResponseProto
DECL|method|getVersion (StorageContainerDatanodeProtocolProtos .SCMVersionRequestProto unused)
name|getVersion
parameter_list|(
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionRequestProto
name|unused
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|sleepIfNeeded
argument_list|()
expr_stmt|;
name|VersionInfo
name|versionInfo
init|=
name|VersionInfo
operator|.
name|getLatestVersion
argument_list|()
decl_stmt|;
return|return
name|VersionResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVersion
argument_list|(
name|versionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|addValue
argument_list|(
name|VersionInfo
operator|.
name|DESCRIPTION_KEY
argument_list|,
name|versionInfo
operator|.
name|getDescription
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
return|;
block|}
DECL|method|sleepIfNeeded ()
specifier|private
name|void
name|sleepIfNeeded
parameter_list|()
block|{
if|if
condition|(
name|getRpcResponseDelay
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|getRpcResponseDelay
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// Just ignore this exception.
block|}
block|}
block|}
comment|/**    * Used by data node to send a Heartbeat.    *    * @param datanodeID - Datanode ID.    * @return - SCMHeartbeatResponseProto    * @throws IOException    */
annotation|@
name|Override
specifier|public
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatResponseProto
DECL|method|sendHeartbeat (DatanodeID datanodeID)
name|sendHeartbeat
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|heartbeatCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|sleepIfNeeded
argument_list|()
expr_stmt|;
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandResponseProto
name|cmdResponse
init|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|StorageContainerDatanodeProtocolProtos
operator|.
name|Type
operator|.
name|nullCmd
argument_list|)
operator|.
name|setNullCommand
argument_list|(
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NullCmdResponseProto
operator|.
name|parseFrom
argument_list|(
name|NullCommand
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addCommands
argument_list|(
name|cmdResponse
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Register Datanode.    *    * @param datanodeID - DatanodID.    * @param scmAddresses - List of SCMs this datanode is configured to    * communicate.    * @return SCM Command.    */
annotation|@
name|Override
specifier|public
name|StorageContainerDatanodeProtocolProtos
DECL|method|register (DatanodeID datanodeID, String[] scmAddresses)
operator|.
name|SCMRegisteredCmdResponseProto
name|register
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|,
name|String
index|[]
name|scmAddresses
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|sleepIfNeeded
argument_list|()
expr_stmt|;
return|return
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisteredCmdResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterID
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setDatanodeUUID
argument_list|(
name|datanodeID
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
operator|.
name|setErrorCode
argument_list|(
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisteredCmdResponseProto
operator|.
name|ErrorCode
operator|.
name|success
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

