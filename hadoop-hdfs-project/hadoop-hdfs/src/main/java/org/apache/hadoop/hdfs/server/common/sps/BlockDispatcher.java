begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common.sps
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
name|common
operator|.
name|sps
package|;
end_package

begin_import
import|import static
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
name|PBHelperClient
operator|.
name|vintPrefixed
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|BlockPinningException
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
name|DataTransferProtoUtil
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
name|IOStreamPair
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
name|datatransfer
operator|.
name|sasl
operator|.
name|DataEncryptionKeyFactory
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
name|sasl
operator|.
name|SaslDataTransferClient
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
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|Status
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
name|protocol
operator|.
name|BlockStorageMovementCommand
operator|.
name|BlockMovingInfo
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Dispatching block replica moves between datanodes to satisfy the storage  * policy.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockDispatcher
specifier|public
class|class
name|BlockDispatcher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlockDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|connectToDnViaHostname
specifier|private
specifier|final
name|boolean
name|connectToDnViaHostname
decl_stmt|;
DECL|field|socketTimeout
specifier|private
specifier|final
name|int
name|socketTimeout
decl_stmt|;
DECL|field|ioFileBufferSize
specifier|private
specifier|final
name|int
name|ioFileBufferSize
decl_stmt|;
comment|/**    * Construct block dispatcher details.    *    * @param sockTimeout    *          soTimeout    * @param ioFileBuffSize    *          file io buffer size    * @param connectToDatanodeViaHostname    *          true represents connect via hostname, false otw    */
DECL|method|BlockDispatcher (int sockTimeout, int ioFileBuffSize, boolean connectToDatanodeViaHostname)
specifier|public
name|BlockDispatcher
parameter_list|(
name|int
name|sockTimeout
parameter_list|,
name|int
name|ioFileBuffSize
parameter_list|,
name|boolean
name|connectToDatanodeViaHostname
parameter_list|)
block|{
name|this
operator|.
name|socketTimeout
operator|=
name|sockTimeout
expr_stmt|;
name|this
operator|.
name|ioFileBufferSize
operator|=
name|ioFileBuffSize
expr_stmt|;
name|this
operator|.
name|connectToDnViaHostname
operator|=
name|connectToDatanodeViaHostname
expr_stmt|;
block|}
comment|/**    * Moves the given block replica to the given target node and wait for the    * response.    *    * @param blkMovingInfo    *          block to storage info    * @param saslClient    *          SASL for DataTransferProtocol on behalf of a client    * @param eb    *          extended block info    * @param sock    *          target node's socket    * @param km    *          for creation of an encryption key    * @param accessToken    *          connection block access token    * @return status of the block movement    */
DECL|method|moveBlock (BlockMovingInfo blkMovingInfo, SaslDataTransferClient saslClient, ExtendedBlock eb, Socket sock, DataEncryptionKeyFactory km, Token<BlockTokenIdentifier> accessToken)
specifier|public
name|BlockMovementStatus
name|moveBlock
parameter_list|(
name|BlockMovingInfo
name|blkMovingInfo
parameter_list|,
name|SaslDataTransferClient
name|saslClient
parameter_list|,
name|ExtendedBlock
name|eb
parameter_list|,
name|Socket
name|sock
parameter_list|,
name|DataEncryptionKeyFactory
name|km
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|accessToken
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Start moving block:{} from src:{} to destin:{} to satisfy "
operator|+
literal|"storageType, sourceStoragetype:{} and destinStoragetype:{}"
argument_list|,
name|blkMovingInfo
operator|.
name|getBlock
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getSource
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getTarget
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getSourceStorageType
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getTargetStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|DataOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|NetUtils
operator|.
name|connect
argument_list|(
name|sock
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|blkMovingInfo
operator|.
name|getTarget
argument_list|()
operator|.
name|getXferAddr
argument_list|(
name|connectToDnViaHostname
argument_list|)
argument_list|)
argument_list|,
name|socketTimeout
argument_list|)
expr_stmt|;
comment|// Set read timeout so that it doesn't hang forever against
comment|// unresponsive nodes. Datanode normally sends IN_PROGRESS response
comment|// twice within the client read timeout period (every 30 seconds by
comment|// default). Here, we make it give up after "socketTimeout * 5" period
comment|// of no response.
name|sock
operator|.
name|setSoTimeout
argument_list|(
name|socketTimeout
operator|*
literal|5
argument_list|)
expr_stmt|;
name|sock
operator|.
name|setKeepAlive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OutputStream
name|unbufOut
init|=
name|sock
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|InputStream
name|unbufIn
init|=
name|sock
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to datanode {}"
argument_list|,
name|blkMovingInfo
operator|.
name|getTarget
argument_list|()
argument_list|)
expr_stmt|;
name|IOStreamPair
name|saslStreams
init|=
name|saslClient
operator|.
name|socketSend
argument_list|(
name|sock
argument_list|,
name|unbufOut
argument_list|,
name|unbufIn
argument_list|,
name|km
argument_list|,
name|accessToken
argument_list|,
name|blkMovingInfo
operator|.
name|getTarget
argument_list|()
argument_list|)
decl_stmt|;
name|unbufOut
operator|=
name|saslStreams
operator|.
name|out
expr_stmt|;
name|unbufIn
operator|=
name|saslStreams
operator|.
name|in
expr_stmt|;
name|out
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|unbufOut
argument_list|,
name|ioFileBufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|unbufIn
argument_list|,
name|ioFileBufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|sendRequest
argument_list|(
name|out
argument_list|,
name|eb
argument_list|,
name|accessToken
argument_list|,
name|blkMovingInfo
operator|.
name|getSource
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getTargetStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|receiveResponse
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully moved block:{} from src:{} to destin:{} for"
operator|+
literal|" satisfying storageType:{}"
argument_list|,
name|blkMovingInfo
operator|.
name|getBlock
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getSource
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getTarget
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getTargetStorageType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|BlockMovementStatus
operator|.
name|DN_BLK_STORAGE_MOVEMENT_SUCCESS
return|;
block|}
catch|catch
parameter_list|(
name|BlockPinningException
name|e
parameter_list|)
block|{
comment|// Pinned block won't be able to move to a different node. So, its not
comment|// required to do retries, just marked as SUCCESS.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Pinned block can't be moved, so skipping block:{}"
argument_list|,
name|blkMovingInfo
operator|.
name|getBlock
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|BlockMovementStatus
operator|.
name|DN_BLK_STORAGE_MOVEMENT_SUCCESS
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: handle failure retries
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to move block:{} from src:{} to destin:{} to satisfy "
operator|+
literal|"storageType:{}"
argument_list|,
name|blkMovingInfo
operator|.
name|getBlock
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getSource
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getTarget
argument_list|()
argument_list|,
name|blkMovingInfo
operator|.
name|getTargetStorageType
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|BlockMovementStatus
operator|.
name|DN_BLK_STORAGE_MOVEMENT_FAILURE
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
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
comment|/** Send a reportedBlock replace request to the output stream. */
DECL|method|sendRequest (DataOutputStream out, ExtendedBlock eb, Token<BlockTokenIdentifier> accessToken, DatanodeInfo source, StorageType targetStorageType)
specifier|private
specifier|static
name|void
name|sendRequest
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|ExtendedBlock
name|eb
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|accessToken
parameter_list|,
name|DatanodeInfo
name|source
parameter_list|,
name|StorageType
name|targetStorageType
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|Sender
argument_list|(
name|out
argument_list|)
operator|.
name|replaceBlock
argument_list|(
name|eb
argument_list|,
name|targetStorageType
argument_list|,
name|accessToken
argument_list|,
name|source
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|source
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Receive a reportedBlock copy response from the input stream. */
DECL|method|receiveResponse (DataInputStream in)
specifier|private
specifier|static
name|void
name|receiveResponse
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockOpResponseProto
name|response
init|=
name|BlockOpResponseProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|IN_PROGRESS
condition|)
block|{
comment|// read intermediate responses
name|response
operator|=
name|BlockOpResponseProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|logInfo
init|=
literal|"reportedBlock move is failed"
decl_stmt|;
name|DataTransferProtoUtil
operator|.
name|checkBlockOpStatus
argument_list|(
name|response
argument_list|,
name|logInfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

