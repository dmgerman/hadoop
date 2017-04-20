begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Used for injecting faults in DFSClient and DFSOutputStream tests.  * Calls into this are a no-op in production code.   */
end_comment

begin_class
annotation|@
name|VisibleForTesting
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DataNodeFaultInjector
specifier|public
class|class
name|DataNodeFaultInjector
block|{
DECL|field|instance
specifier|private
specifier|static
name|DataNodeFaultInjector
name|instance
init|=
operator|new
name|DataNodeFaultInjector
argument_list|()
decl_stmt|;
DECL|method|get ()
specifier|public
specifier|static
name|DataNodeFaultInjector
name|get
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
DECL|method|set (DataNodeFaultInjector injector)
specifier|public
specifier|static
name|void
name|set
parameter_list|(
name|DataNodeFaultInjector
name|injector
parameter_list|)
block|{
name|instance
operator|=
name|injector
expr_stmt|;
block|}
DECL|method|getHdfsBlocksMetadata ()
specifier|public
name|void
name|getHdfsBlocksMetadata
parameter_list|()
block|{}
DECL|method|writeBlockAfterFlush ()
specifier|public
name|void
name|writeBlockAfterFlush
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|sendShortCircuitShmResponse ()
specifier|public
name|void
name|sendShortCircuitShmResponse
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|dropHeartbeatPacket ()
specifier|public
name|boolean
name|dropHeartbeatPacket
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|stopSendingPacketDownstream (final String mirrAddr)
specifier|public
name|void
name|stopSendingPacketDownstream
parameter_list|(
specifier|final
name|String
name|mirrAddr
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * Used as a hook to intercept the latency of sending packet.    */
DECL|method|logDelaySendingPacketDownstream ( final String mirrAddr, final long delayMs)
specifier|public
name|void
name|logDelaySendingPacketDownstream
parameter_list|(
specifier|final
name|String
name|mirrAddr
parameter_list|,
specifier|final
name|long
name|delayMs
parameter_list|)
throws|throws
name|IOException
block|{   }
DECL|method|delaySendingAckToUpstream (final String upstreamAddr)
specifier|public
name|void
name|delaySendingAckToUpstream
parameter_list|(
specifier|final
name|String
name|upstreamAddr
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * Used as a hook to intercept the latency of sending ack.    */
DECL|method|logDelaySendingAckToUpstream ( final String upstreamAddr, final long delayMs)
specifier|public
name|void
name|logDelaySendingAckToUpstream
parameter_list|(
specifier|final
name|String
name|upstreamAddr
parameter_list|,
specifier|final
name|long
name|delayMs
parameter_list|)
throws|throws
name|IOException
block|{   }
DECL|method|noRegistration ()
specifier|public
name|void
name|noRegistration
parameter_list|()
throws|throws
name|IOException
block|{ }
DECL|method|failMirrorConnection ()
specifier|public
name|void
name|failMirrorConnection
parameter_list|()
throws|throws
name|IOException
block|{ }
DECL|method|failPipeline (ReplicaInPipeline replicaInfo, String mirrorAddr)
specifier|public
name|void
name|failPipeline
parameter_list|(
name|ReplicaInPipeline
name|replicaInfo
parameter_list|,
name|String
name|mirrorAddr
parameter_list|)
throws|throws
name|IOException
block|{ }
DECL|method|startOfferService ()
specifier|public
name|void
name|startOfferService
parameter_list|()
throws|throws
name|Exception
block|{}
DECL|method|endOfferService ()
specifier|public
name|void
name|endOfferService
parameter_list|()
throws|throws
name|Exception
block|{}
block|}
end_class

end_unit

