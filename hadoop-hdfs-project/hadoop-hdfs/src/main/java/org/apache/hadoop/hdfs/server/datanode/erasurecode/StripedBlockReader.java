begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.erasurecode
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
operator|.
name|erasurecode
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
name|conf
operator|.
name|Configuration
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
name|ChecksumException
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
name|BlockReader
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
name|DFSUtilClient
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
name|DFSUtilClient
operator|.
name|CorruptedBlocks
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
name|client
operator|.
name|impl
operator|.
name|BlockReaderRemote
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
name|net
operator|.
name|Peer
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
name|ExtendedBlock
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|DataNode
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
name|StripedBlockUtil
operator|.
name|BlockReadStats
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
name|io
operator|.
name|IOUtils
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
name|net
operator|.
name|NetUtils
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
name|security
operator|.
name|token
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Callable
import|;
end_import

begin_comment
comment|/**  * StripedBlockReader is used to read block data from one source DN, it contains  * a block reader, read buffer and striped block index.  * Only allocate StripedBlockReader once for one source, and the StripedReader  * has the same array order with sources. Typically we only need to allocate  * minimum number (minRequiredSources) of StripedReader, and allocate  * new for new source DN if some existing DN invalid or slow.  * If some source DN is corrupt, set the corresponding blockReader to  * null and will never read from it again.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StripedBlockReader
class|class
name|StripedBlockReader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|stripedReader
specifier|private
name|StripedReader
name|stripedReader
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|short
name|index
decl_stmt|;
comment|// internal block index
DECL|field|block
specifier|private
specifier|final
name|ExtendedBlock
name|block
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|DatanodeInfo
name|source
decl_stmt|;
DECL|field|blockReader
specifier|private
name|BlockReader
name|blockReader
decl_stmt|;
DECL|field|buffer
specifier|private
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|isLocal
specifier|private
name|boolean
name|isLocal
decl_stmt|;
DECL|method|StripedBlockReader (StripedReader stripedReader, DataNode datanode, Configuration conf, short index, ExtendedBlock block, DatanodeInfo source, long offsetInBlock)
name|StripedBlockReader
parameter_list|(
name|StripedReader
name|stripedReader
parameter_list|,
name|DataNode
name|datanode
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|short
name|index
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|DatanodeInfo
name|source
parameter_list|,
name|long
name|offsetInBlock
parameter_list|)
block|{
name|this
operator|.
name|stripedReader
operator|=
name|stripedReader
expr_stmt|;
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|isLocal
operator|=
literal|false
expr_stmt|;
name|BlockReader
name|tmpBlockReader
init|=
name|createBlockReader
argument_list|(
name|offsetInBlock
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpBlockReader
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|blockReader
operator|=
name|tmpBlockReader
expr_stmt|;
block|}
block|}
DECL|method|getReadBuffer ()
name|ByteBuffer
name|getReadBuffer
parameter_list|()
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|buffer
operator|=
name|stripedReader
operator|.
name|allocateReadBuffer
argument_list|()
expr_stmt|;
block|}
return|return
name|buffer
return|;
block|}
DECL|method|freeReadBuffer ()
name|void
name|freeReadBuffer
parameter_list|()
block|{
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|resetBlockReader (long offsetInBlock)
name|void
name|resetBlockReader
parameter_list|(
name|long
name|offsetInBlock
parameter_list|)
block|{
name|this
operator|.
name|blockReader
operator|=
name|createBlockReader
argument_list|(
name|offsetInBlock
argument_list|)
expr_stmt|;
block|}
DECL|method|createBlockReader (long offsetInBlock)
specifier|private
name|BlockReader
name|createBlockReader
parameter_list|(
name|long
name|offsetInBlock
parameter_list|)
block|{
if|if
condition|(
name|offsetInBlock
operator|>=
name|block
operator|.
name|getNumBytes
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Peer
name|peer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|InetSocketAddress
name|dnAddr
init|=
name|stripedReader
operator|.
name|getSocketAddress4Transfer
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
init|=
name|datanode
operator|.
name|getBlockAccessToken
argument_list|(
name|block
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|BlockTokenIdentifier
operator|.
name|AccessMode
operator|.
name|READ
argument_list|)
argument_list|,
name|StorageType
operator|.
name|EMPTY_ARRAY
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|/*          * This can be further improved if the replica is local, then we can          * read directly from DN and need to check the replica is FINALIZED          * state, notice we should not use short-circuit local read which          * requires config for domain-socket in UNIX or legacy config in          * Windows. The network distance value isn't used for this scenario.          *          * TODO: add proper tracer          */
name|peer
operator|=
name|newConnectedPeer
argument_list|(
name|block
argument_list|,
name|dnAddr
argument_list|,
name|blockToken
argument_list|,
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|peer
operator|.
name|isLocal
argument_list|()
condition|)
block|{
name|this
operator|.
name|isLocal
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|BlockReaderRemote
operator|.
name|newBlockReader
argument_list|(
literal|"dummy"
argument_list|,
name|block
argument_list|,
name|blockToken
argument_list|,
name|offsetInBlock
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
operator|-
name|offsetInBlock
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|,
name|peer
argument_list|,
name|source
argument_list|,
literal|null
argument_list|,
name|stripedReader
operator|.
name|getCachingStrategy
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception while creating remote block reader, datanode {}"
argument_list|,
name|source
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|peer
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|newConnectedPeer (ExtendedBlock b, InetSocketAddress addr, Token<BlockTokenIdentifier> blockToken, DatanodeID datanodeId)
specifier|private
name|Peer
name|newConnectedPeer
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|DatanodeID
name|datanodeId
parameter_list|)
throws|throws
name|IOException
block|{
name|Peer
name|peer
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|Socket
name|sock
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|socketTimeout
init|=
name|datanode
operator|.
name|getDnConf
argument_list|()
operator|.
name|getSocketTimeout
argument_list|()
decl_stmt|;
try|try
block|{
name|sock
operator|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|createSocket
argument_list|()
expr_stmt|;
name|NetUtils
operator|.
name|connect
argument_list|(
name|sock
argument_list|,
name|addr
argument_list|,
name|socketTimeout
argument_list|)
expr_stmt|;
name|peer
operator|=
name|DFSUtilClient
operator|.
name|peerFromSocketAndKey
argument_list|(
name|datanode
operator|.
name|getSaslClient
argument_list|()
argument_list|,
name|sock
argument_list|,
name|datanode
operator|.
name|getDataEncryptionKeyFactoryForBlock
argument_list|(
name|b
argument_list|)
argument_list|,
name|blockToken
argument_list|,
name|datanodeId
argument_list|,
name|socketTimeout
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|peer
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|peer
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readFromBlock (final int length, final CorruptedBlocks corruptedBlocks)
name|Callable
argument_list|<
name|BlockReadStats
argument_list|>
name|readFromBlock
parameter_list|(
specifier|final
name|int
name|length
parameter_list|,
specifier|final
name|CorruptedBlocks
name|corruptedBlocks
parameter_list|)
block|{
return|return
operator|new
name|Callable
argument_list|<
name|BlockReadStats
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BlockReadStats
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|getReadBuffer
argument_list|()
operator|.
name|limit
argument_list|(
name|length
argument_list|)
expr_stmt|;
return|return
name|actualReadFromBlock
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Found Checksum error for {} from {} at {}"
argument_list|,
name|block
argument_list|,
name|source
argument_list|,
name|e
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
name|corruptedBlocks
operator|.
name|addCorruptedBlock
argument_list|(
name|block
argument_list|,
name|source
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Perform actual reading of bytes from block.    */
DECL|method|actualReadFromBlock ()
specifier|private
name|BlockReadStats
name|actualReadFromBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|buffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|n
operator|<
name|len
condition|)
block|{
name|int
name|nread
init|=
name|blockReader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|nread
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|n
operator|+=
name|nread
expr_stmt|;
name|stripedReader
operator|.
name|getReconstructor
argument_list|()
operator|.
name|incrBytesRead
argument_list|(
name|isLocal
argument_list|,
name|nread
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BlockReadStats
argument_list|(
name|n
argument_list|,
name|blockReader
operator|.
name|isShortCircuit
argument_list|()
argument_list|,
name|blockReader
operator|.
name|getNetworkDistance
argument_list|()
argument_list|)
return|;
block|}
comment|// close block reader
DECL|method|closeBlockReader ()
name|void
name|closeBlockReader
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|blockReader
argument_list|)
expr_stmt|;
name|blockReader
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getIndex ()
name|short
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|getBlockReader ()
name|BlockReader
name|getBlockReader
parameter_list|()
block|{
return|return
name|blockReader
return|;
block|}
block|}
end_class

end_unit

