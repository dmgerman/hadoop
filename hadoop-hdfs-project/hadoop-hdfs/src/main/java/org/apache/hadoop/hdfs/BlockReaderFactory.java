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
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|protocol
operator|.
name|datatransfer
operator|.
name|Sender
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
name|DataTransferProtos
operator|.
name|BlockOpResponseProto
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
name|protocolPB
operator|.
name|PBHelper
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|InvalidBlockTokenException
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
name|common
operator|.
name|HdfsServerConstants
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
name|ipc
operator|.
name|RemoteException
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
name|unix
operator|.
name|DomainSocket
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
name|AccessControlException
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
name|SecretManager
operator|.
name|InvalidToken
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

begin_comment
comment|/**   * Utility class to create BlockReader implementations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockReaderFactory
specifier|public
class|class
name|BlockReaderFactory
block|{
comment|/**    * Create a new BlockReader specifically to satisfy a read.    * This method also sends the OP_READ_BLOCK request.    *     * @param conf the DFSClient configuration    * @param file  File location    * @param block  The block object    * @param blockToken  The block token for security    * @param startOffset  The read offset, relative to block head    * @param len  The number of bytes to read, or -1 to read as many as    *             possible.    * @param bufferSize  The IO buffer size (not the client buffer size)    *                    Ignored except on the legacy BlockReader.    * @param verifyChecksum  Whether to verify checksum    * @param clientName  Client name.  Used for log messages.    * @param peer  The peer    * @param datanodeID  The datanode that the Peer is connected to    * @param domainSocketFactory  The DomainSocketFactory to notify if the Peer    *                             is a DomainPeer which turns out to be faulty.    *                             If null, no factory will be notified in this    *                             case.    * @param allowShortCircuitLocalReads  True if short-circuit local reads    *                                     should be allowed.    * @return New BlockReader instance    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|newBlockReader ( Configuration conf, String file, ExtendedBlock block, Token<BlockTokenIdentifier> blockToken, long startOffset, long len, boolean verifyChecksum, String clientName, Peer peer, DatanodeID datanodeID, DomainSocketFactory domSockFactory, boolean allowShortCircuitLocalReads)
specifier|public
specifier|static
name|BlockReader
name|newBlockReader
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|file
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|long
name|startOffset
parameter_list|,
name|long
name|len
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|,
name|String
name|clientName
parameter_list|,
name|Peer
name|peer
parameter_list|,
name|DatanodeID
name|datanodeID
parameter_list|,
name|DomainSocketFactory
name|domSockFactory
parameter_list|,
name|boolean
name|allowShortCircuitLocalReads
parameter_list|)
throws|throws
name|IOException
block|{
name|peer
operator|.
name|setReadTimeout
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
argument_list|)
expr_stmt|;
name|peer
operator|.
name|setWriteTimeout
argument_list|(
name|HdfsServerConstants
operator|.
name|WRITE_TIMEOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|peer
operator|.
name|getDomainSocket
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|allowShortCircuitLocalReads
operator|&&
operator|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_USE_LEGACY_BLOCKREADERLOCAL
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_USE_LEGACY_BLOCKREADERLOCAL_DEFAULT
argument_list|)
operator|)
condition|)
block|{
comment|// If this is a domain socket, and short-circuit local reads are
comment|// enabled, try to set up a BlockReaderLocal.
name|BlockReader
name|reader
init|=
name|newShortCircuitBlockReader
argument_list|(
name|conf
argument_list|,
name|file
argument_list|,
name|block
argument_list|,
name|blockToken
argument_list|,
name|startOffset
argument_list|,
name|len
argument_list|,
name|peer
argument_list|,
name|datanodeID
argument_list|,
name|domSockFactory
argument_list|,
name|verifyChecksum
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
comment|// One we've constructed the short-circuit block reader, we don't
comment|// need the socket any more.  So let's return it to the cache.
name|PeerCache
name|peerCache
init|=
name|PeerCache
operator|.
name|getInstance
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_CACHE_CAPACITY_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_CACHE_CAPACITY_DEFAULT
argument_list|)
argument_list|,
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_CACHE_EXPIRY_MSEC_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_CACHE_EXPIRY_MSEC_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
name|peerCache
operator|.
name|put
argument_list|(
name|datanodeID
argument_list|,
name|peer
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
comment|// If this is a domain socket and we couldn't (or didn't want to) set
comment|// up a BlockReaderLocal, check that we are allowed to pass data traffic
comment|// over the socket before proceeding.
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_DOMAIN_SOCKET_DATA_TRAFFIC
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_DOMAIN_SOCKET_DATA_TRAFFIC_DEFAULT
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Because we can't do short-circuit access, "
operator|+
literal|"and data traffic over domain sockets is disabled, "
operator|+
literal|"we cannot use this socket to talk to "
operator|+
name|datanodeID
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_USE_LEGACY_BLOCKREADER
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_USE_LEGACY_BLOCKREADER_DEFAULT
argument_list|)
condition|)
block|{
return|return
name|RemoteBlockReader
operator|.
name|newBlockReader
argument_list|(
name|file
argument_list|,
name|block
argument_list|,
name|blockToken
argument_list|,
name|startOffset
argument_list|,
name|len
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
argument_list|,
name|verifyChecksum
argument_list|,
name|clientName
argument_list|,
name|peer
argument_list|,
name|datanodeID
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|RemoteBlockReader2
operator|.
name|newBlockReader
argument_list|(
name|file
argument_list|,
name|block
argument_list|,
name|blockToken
argument_list|,
name|startOffset
argument_list|,
name|len
argument_list|,
name|verifyChecksum
argument_list|,
name|clientName
argument_list|,
name|peer
argument_list|,
name|datanodeID
argument_list|)
return|;
block|}
block|}
comment|/**    * Create a new short-circuit BlockReader.    *     * Here, we ask the DataNode to pass us file descriptors over our    * DomainSocket.  If the DataNode declines to do so, we'll return null here;    * otherwise, we'll return the BlockReaderLocal.  If the DataNode declines,    * this function will inform the DomainSocketFactory that short-circuit local    * reads are disabled for this DataNode, so that we don't ask again.    *     * @param conf               the configuration.    * @param file               the file name. Used in log messages.    * @param block              The block object.    * @param blockToken         The block token for security.    * @param startOffset        The read offset, relative to block head.    * @param len                The number of bytes to read, or -1 to read     *                           as many as possible.    * @param peer               The peer to use.    * @param datanodeID         The datanode that the Peer is connected to.    * @param domSockFactory     The DomainSocketFactory to notify if the Peer    *                           is a DomainPeer which turns out to be faulty.    *                           If null, no factory will be notified in this    *                           case.    * @param verifyChecksum     True if we should verify the checksums.    *                           Note: even if this is true, when    *                           DFS_CLIENT_READ_CHECKSUM_SKIP_CHECKSUM_KEY is    *                           set, we will skip checksums.    *    * @return                   The BlockReaderLocal, or null if the    *                           DataNode declined to provide short-circuit    *                           access.    * @throws IOException       If there was a communication error.    */
DECL|method|newShortCircuitBlockReader ( Configuration conf, String file, ExtendedBlock block, Token<BlockTokenIdentifier> blockToken, long startOffset, long len, Peer peer, DatanodeID datanodeID, DomainSocketFactory domSockFactory, boolean verifyChecksum)
specifier|private
specifier|static
name|BlockReaderLocal
name|newShortCircuitBlockReader
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|file
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|long
name|startOffset
parameter_list|,
name|long
name|len
parameter_list|,
name|Peer
name|peer
parameter_list|,
name|DatanodeID
name|datanodeID
parameter_list|,
name|DomainSocketFactory
name|domSockFactory
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|peer
operator|.
name|getOutputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
operator|new
name|Sender
argument_list|(
name|out
argument_list|)
operator|.
name|requestShortCircuitFds
argument_list|(
name|block
argument_list|,
name|blockToken
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|peer
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|BlockOpResponseProto
name|resp
init|=
name|BlockOpResponseProto
operator|.
name|parseFrom
argument_list|(
name|PBHelper
operator|.
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|DomainSocket
name|sock
init|=
name|peer
operator|.
name|getDomainSocket
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|resp
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|SUCCESS
case|:
name|BlockReaderLocal
name|reader
init|=
literal|null
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|FileInputStream
name|fis
index|[]
init|=
operator|new
name|FileInputStream
index|[
literal|2
index|]
decl_stmt|;
name|sock
operator|.
name|recvFileInputStreams
argument_list|(
name|fis
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BlockReaderLocal
argument_list|(
name|conf
argument_list|,
name|file
argument_list|,
name|block
argument_list|,
name|startOffset
argument_list|,
name|len
argument_list|,
name|fis
index|[
literal|0
index|]
argument_list|,
name|fis
index|[
literal|1
index|]
argument_list|,
name|datanodeID
argument_list|,
name|verifyChecksum
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|DFSClient
operator|.
name|LOG
argument_list|,
name|fis
index|[
literal|0
index|]
argument_list|,
name|fis
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|reader
return|;
case|case
name|ERROR_UNSUPPORTED
case|:
if|if
condition|(
operator|!
name|resp
operator|.
name|hasShortCircuitAccessVersion
argument_list|()
condition|)
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"short-circuit read access is disabled for "
operator|+
literal|"DataNode "
operator|+
name|datanodeID
operator|+
literal|".  reason: "
operator|+
name|resp
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|domSockFactory
operator|.
name|disableShortCircuitForPath
argument_list|(
name|sock
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"short-circuit read access for the file "
operator|+
name|file
operator|+
literal|" is disabled for DataNode "
operator|+
name|datanodeID
operator|+
literal|".  reason: "
operator|+
name|resp
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
case|case
name|ERROR_ACCESS_TOKEN
case|:
name|String
name|msg
init|=
literal|"access control error while "
operator|+
literal|"attempting to set up short-circuit access to "
operator|+
name|file
operator|+
name|resp
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|DFSClient
operator|.
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidBlockTokenException
argument_list|(
name|msg
argument_list|)
throw|;
default|default:
name|DFSClient
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"error while attempting to set up short-circuit "
operator|+
literal|"access to "
operator|+
name|file
operator|+
literal|": "
operator|+
name|resp
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|domSockFactory
operator|.
name|disableShortCircuitForPath
argument_list|(
name|sock
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * File name to print when accessing a block directly (from servlets)    * @param s Address of the block location    * @param poolId Block pool ID of the block    * @param blockId Block ID of the block    * @return string that has a file name for debug purposes    */
DECL|method|getFileName (final InetSocketAddress s, final String poolId, final long blockId)
specifier|public
specifier|static
name|String
name|getFileName
parameter_list|(
specifier|final
name|InetSocketAddress
name|s
parameter_list|,
specifier|final
name|String
name|poolId
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|)
block|{
return|return
name|s
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|poolId
operator|+
literal|":"
operator|+
name|blockId
return|;
block|}
comment|/**    * Get {@link BlockReaderLocalLegacy} for short circuited local reads.    * This block reader implements the path-based style of local reads    * first introduced in HDFS-2246.    */
DECL|method|getLegacyBlockReaderLocal (Configuration conf, String src, ExtendedBlock blk, Token<BlockTokenIdentifier> accessToken, DatanodeInfo chosenNode, int socketTimeout, long offsetIntoBlock, boolean connectToDnViaHostname)
specifier|static
name|BlockReader
name|getLegacyBlockReaderLocal
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|src
parameter_list|,
name|ExtendedBlock
name|blk
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|accessToken
parameter_list|,
name|DatanodeInfo
name|chosenNode
parameter_list|,
name|int
name|socketTimeout
parameter_list|,
name|long
name|offsetIntoBlock
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|)
throws|throws
name|InvalidToken
throws|,
name|IOException
block|{
try|try
block|{
return|return
name|BlockReaderLocalLegacy
operator|.
name|newBlockReader
argument_list|(
name|conf
argument_list|,
name|src
argument_list|,
name|blk
argument_list|,
name|accessToken
argument_list|,
name|chosenNode
argument_list|,
name|socketTimeout
argument_list|,
name|offsetIntoBlock
argument_list|,
name|blk
operator|.
name|getNumBytes
argument_list|()
operator|-
name|offsetIntoBlock
argument_list|,
name|connectToDnViaHostname
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
throw|throw
name|re
operator|.
name|unwrapRemoteException
argument_list|(
name|InvalidToken
operator|.
name|class
argument_list|,
name|AccessControlException
operator|.
name|class
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

