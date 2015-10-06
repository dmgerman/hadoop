begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|fs
operator|.
name|StorageType
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
name|DFSStripedOutputStream
operator|.
name|Coordinator
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
name|DatanodeInfo
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
name|HdfsFileStatus
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
name|LocatedBlock
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
name|datanode
operator|.
name|CachingStrategy
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
name|util
operator|.
name|ByteArrayManager
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
name|util
operator|.
name|DataChecksum
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
name|util
operator|.
name|Progressable
import|;
end_import

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

begin_comment
comment|/**  * This class extends {@link DataStreamer} to support writing striped blocks  * to datanodes.  * A {@link DFSStripedOutputStream} has multiple {@link StripedDataStreamer}s.  * Whenever the streamers need to talk the namenode, only the fastest streamer  * sends an rpc call to the namenode and then populates the result for the  * other streamers.  */
end_comment

begin_class
DECL|class|StripedDataStreamer
specifier|public
class|class
name|StripedDataStreamer
extends|extends
name|DataStreamer
block|{
DECL|field|coordinator
specifier|private
specifier|final
name|Coordinator
name|coordinator
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
DECL|method|StripedDataStreamer (HdfsFileStatus stat, DFSClient dfsClient, String src, Progressable progress, DataChecksum checksum, AtomicReference<CachingStrategy> cachingStrategy, ByteArrayManager byteArrayManage, String[] favoredNodes, short index, Coordinator coordinator)
name|StripedDataStreamer
parameter_list|(
name|HdfsFileStatus
name|stat
parameter_list|,
name|DFSClient
name|dfsClient
parameter_list|,
name|String
name|src
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|DataChecksum
name|checksum
parameter_list|,
name|AtomicReference
argument_list|<
name|CachingStrategy
argument_list|>
name|cachingStrategy
parameter_list|,
name|ByteArrayManager
name|byteArrayManage
parameter_list|,
name|String
index|[]
name|favoredNodes
parameter_list|,
name|short
name|index
parameter_list|,
name|Coordinator
name|coordinator
parameter_list|)
block|{
name|super
argument_list|(
name|stat
argument_list|,
literal|null
argument_list|,
name|dfsClient
argument_list|,
name|src
argument_list|,
name|progress
argument_list|,
name|checksum
argument_list|,
name|cachingStrategy
argument_list|,
name|byteArrayManage
argument_list|,
name|favoredNodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|coordinator
operator|=
name|coordinator
expr_stmt|;
block|}
DECL|method|getIndex ()
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|isHealthy ()
name|boolean
name|isHealthy
parameter_list|()
block|{
return|return
operator|!
name|streamerClosed
argument_list|()
operator|&&
operator|!
name|getErrorState
argument_list|()
operator|.
name|hasInternalError
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endBlock ()
specifier|protected
name|void
name|endBlock
parameter_list|()
block|{
name|coordinator
operator|.
name|offerEndBlock
argument_list|(
name|index
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|super
operator|.
name|endBlock
argument_list|()
expr_stmt|;
block|}
comment|/**    * The upper level DFSStripedOutputStream will allocate the new block group.    * All the striped data streamer only needs to fetch from the queue, which    * should be already be ready.    */
DECL|method|getFollowingBlock ()
specifier|private
name|LocatedBlock
name|getFollowingBlock
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|isHealthy
argument_list|()
condition|)
block|{
comment|// No internal block for this streamer, maybe no enough healthy DN.
comment|// Throw the exception which has been set by the StripedOutputStream.
name|this
operator|.
name|getLastException
argument_list|()
operator|.
name|check
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|coordinator
operator|.
name|getFollowingBlocks
argument_list|()
operator|.
name|poll
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextBlockOutputStream ()
specifier|protected
name|LocatedBlock
name|nextBlockOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
decl_stmt|;
name|LocatedBlock
name|lb
init|=
name|getFollowingBlock
argument_list|()
decl_stmt|;
name|block
operator|=
name|lb
operator|.
name|getBlock
argument_list|()
expr_stmt|;
name|block
operator|.
name|setNumBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bytesSent
operator|=
literal|0
expr_stmt|;
name|accessToken
operator|=
name|lb
operator|.
name|getBlockToken
argument_list|()
expr_stmt|;
name|DatanodeInfo
index|[]
name|nodes
init|=
name|lb
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|StorageType
index|[]
name|storageTypes
init|=
name|lb
operator|.
name|getStorageTypes
argument_list|()
decl_stmt|;
comment|// Connect to the DataNode. If fail the internal error state will be set.
name|success
operator|=
name|createBlockOutputStream
argument_list|(
name|nodes
argument_list|,
name|storageTypes
argument_list|,
literal|0L
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|block
operator|=
literal|null
expr_stmt|;
specifier|final
name|DatanodeInfo
name|badNode
init|=
name|nodes
index|[
name|getErrorState
argument_list|()
operator|.
name|getBadNodeIndex
argument_list|()
index|]
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Excluding datanode "
operator|+
name|badNode
argument_list|)
expr_stmt|;
name|excludedNodes
operator|.
name|put
argument_list|(
name|badNode
argument_list|,
name|badNode
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create new block."
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
name|lb
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|peekFollowingBlock ()
name|LocatedBlock
name|peekFollowingBlock
parameter_list|()
block|{
return|return
name|coordinator
operator|.
name|getFollowingBlocks
argument_list|()
operator|.
name|peek
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setupPipelineInternal (DatanodeInfo[] nodes, StorageType[] nodeStorageTypes)
specifier|protected
name|void
name|setupPipelineInternal
parameter_list|(
name|DatanodeInfo
index|[]
name|nodes
parameter_list|,
name|StorageType
index|[]
name|nodeStorageTypes
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
operator|!
name|streamerClosed
argument_list|()
operator|&&
name|dfsClient
operator|.
name|clientRunning
condition|)
block|{
if|if
condition|(
operator|!
name|handleRestartingDatanode
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|handleBadDatanode
argument_list|()
condition|)
block|{
comment|// for striped streamer if it is datanode error then close the stream
comment|// and return. no need to replace datanode
return|return;
block|}
comment|// get a new generation stamp and an access token
specifier|final
name|LocatedBlock
name|lb
init|=
name|coordinator
operator|.
name|getNewBlocks
argument_list|()
operator|.
name|take
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|long
name|newGS
init|=
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|setAccessToken
argument_list|(
name|lb
operator|.
name|getBlockToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// set up the pipeline again with the remaining nodes. when a striped
comment|// data streamer comes here, it must be in external error state.
assert|assert
name|getErrorState
argument_list|()
operator|.
name|hasExternalError
argument_list|()
assert|;
name|success
operator|=
name|createBlockOutputStream
argument_list|(
name|nodes
argument_list|,
name|nodeStorageTypes
argument_list|,
name|newGS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|failPacket4Testing
argument_list|()
expr_stmt|;
name|getErrorState
argument_list|()
operator|.
name|checkRestartingNodeDeadline
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
comment|// notify coordinator the result of createBlockOutputStream
synchronized|synchronized
init|(
name|coordinator
init|)
block|{
if|if
condition|(
operator|!
name|streamerClosed
argument_list|()
condition|)
block|{
name|coordinator
operator|.
name|updateStreamer
argument_list|(
name|this
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|coordinator
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|success
condition|)
block|{
comment|// wait for results of other streamers
name|success
operator|=
name|coordinator
operator|.
name|takeStreamerUpdateResult
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
comment|// if all succeeded, update its block using the new GS
name|block
operator|=
name|newBlock
argument_list|(
name|block
argument_list|,
name|newGS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise close the block stream and restart the recovery process
name|closeStream
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// if fail, close the stream. The internal error state and last
comment|// exception have already been set in createBlockOutputStream
comment|// TODO: wait for restarting DataNodes during RollingUpgrade
name|closeStream
argument_list|()
expr_stmt|;
name|setStreamerAsClosed
argument_list|()
expr_stmt|;
block|}
block|}
comment|// while
block|}
DECL|method|setExternalError ()
name|void
name|setExternalError
parameter_list|()
block|{
name|getErrorState
argument_list|()
operator|.
name|setExternalError
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|dataQueue
init|)
block|{
name|dataQueue
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
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
literal|"#"
operator|+
name|index
operator|+
literal|": "
operator|+
operator|(
operator|!
name|isHealthy
argument_list|()
condition|?
literal|"failed, "
else|:
literal|""
operator|)
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

