begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer
package|package
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
name|StripedBlockInfo
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
name|shortcircuit
operator|.
name|ShortCircuitShm
operator|.
name|SlotId
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
comment|/**  * Transfer data to/from datanode using a streaming protocol.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|DataTransferProtocol
specifier|public
interface|interface
name|DataTransferProtocol
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataTransferProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Version for data transfers between clients and datanodes    * This should change when serialization of DatanodeInfo, not just    * when protocol changes. It is not very obvious.    */
comment|/*    * Version 28:    *    Declare methods in DataTransferProtocol interface.    */
DECL|field|DATA_TRANSFER_VERSION
name|int
name|DATA_TRANSFER_VERSION
init|=
literal|28
decl_stmt|;
comment|/**    * Read a block.    *    * @param blk the block being read.    * @param blockToken security token for accessing the block.    * @param clientName client's name.    * @param blockOffset offset of the block.    * @param length maximum number of bytes for this read.    * @param sendChecksum if false, the DN should skip reading and sending    *        checksums    * @param cachingStrategy  The caching strategy to use.    */
DECL|method|readBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, final String clientName, final long blockOffset, final long length, final boolean sendChecksum, final CachingStrategy cachingStrategy)
name|void
name|readBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|long
name|blockOffset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|,
specifier|final
name|boolean
name|sendChecksum
parameter_list|,
specifier|final
name|CachingStrategy
name|cachingStrategy
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write a block to a datanode pipeline.    * The receiver datanode of this call is the next datanode in the pipeline.    * The other downstream datanodes are specified by the targets parameter.    * Note that the receiver {@link DatanodeInfo} is not required in the    * parameter list since the receiver datanode knows its info.  However, the    * {@link StorageType} for storing the replica in the receiver datanode is a    * parameter since the receiver datanode may support multiple storage types.    *    * @param blk the block being written.    * @param storageType for storing the replica in the receiver datanode.    * @param blockToken security token for accessing the block.    * @param clientName client's name.    * @param targets other downstream datanodes in the pipeline.    * @param targetStorageTypes target {@link StorageType}s corresponding    *                           to the target datanodes.    * @param source source datanode.    * @param stage pipeline stage.    * @param pipelineSize the size of the pipeline.    * @param minBytesRcvd minimum number of bytes received.    * @param maxBytesRcvd maximum number of bytes received.    * @param latestGenerationStamp the latest generation stamp of the block.    * @param requestedChecksum the requested checksum mechanism    * @param cachingStrategy the caching strategy    * @param allowLazyPersist hint to the DataNode that the block can be    *                         allocated on transient storage i.e. memory and    *                         written to disk lazily    * @param pinning whether to pin the block, so Balancer won't move it.    * @param targetPinnings whether to pin the block on target datanode    */
DECL|method|writeBlock (final ExtendedBlock blk, final StorageType storageType, final Token<BlockTokenIdentifier> blockToken, final String clientName, final DatanodeInfo[] targets, final StorageType[] targetStorageTypes, final DatanodeInfo source, final BlockConstructionStage stage, final int pipelineSize, final long minBytesRcvd, final long maxBytesRcvd, final long latestGenerationStamp, final DataChecksum requestedChecksum, final CachingStrategy cachingStrategy, final boolean allowLazyPersist, final boolean pinning, final boolean[] targetPinnings)
name|void
name|writeBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|StorageType
name|storageType
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|DatanodeInfo
index|[]
name|targets
parameter_list|,
specifier|final
name|StorageType
index|[]
name|targetStorageTypes
parameter_list|,
specifier|final
name|DatanodeInfo
name|source
parameter_list|,
specifier|final
name|BlockConstructionStage
name|stage
parameter_list|,
specifier|final
name|int
name|pipelineSize
parameter_list|,
specifier|final
name|long
name|minBytesRcvd
parameter_list|,
specifier|final
name|long
name|maxBytesRcvd
parameter_list|,
specifier|final
name|long
name|latestGenerationStamp
parameter_list|,
specifier|final
name|DataChecksum
name|requestedChecksum
parameter_list|,
specifier|final
name|CachingStrategy
name|cachingStrategy
parameter_list|,
specifier|final
name|boolean
name|allowLazyPersist
parameter_list|,
specifier|final
name|boolean
name|pinning
parameter_list|,
specifier|final
name|boolean
index|[]
name|targetPinnings
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Transfer a block to another datanode.    * The block stage must be    * either {@link BlockConstructionStage#TRANSFER_RBW}    * or {@link BlockConstructionStage#TRANSFER_FINALIZED}.    *    * @param blk the block being transferred.    * @param blockToken security token for accessing the block.    * @param clientName client's name.    * @param targets target datanodes.    */
DECL|method|transferBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, final String clientName, final DatanodeInfo[] targets, final StorageType[] targetStorageTypes)
name|void
name|transferBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|DatanodeInfo
index|[]
name|targets
parameter_list|,
specifier|final
name|StorageType
index|[]
name|targetStorageTypes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Request short circuit access file descriptors from a DataNode.    *    * @param blk             The block to get file descriptors for.    * @param blockToken      Security token for accessing the block.    * @param slotId          The shared memory slot id to use, or null    *                          to use no slot id.    * @param maxVersion      Maximum version of the block data the client    *                          can understand.    * @param supportsReceiptVerification  True if the client supports    *                          receipt verification.    */
DECL|method|requestShortCircuitFds (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, SlotId slotId, int maxVersion, boolean supportsReceiptVerification)
name|void
name|requestShortCircuitFds
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|SlotId
name|slotId
parameter_list|,
name|int
name|maxVersion
parameter_list|,
name|boolean
name|supportsReceiptVerification
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Release a pair of short-circuit FDs requested earlier.    *    * @param slotId          SlotID used by the earlier file descriptors.    */
DECL|method|releaseShortCircuitFds (final SlotId slotId)
name|void
name|releaseShortCircuitFds
parameter_list|(
specifier|final
name|SlotId
name|slotId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Request a short circuit shared memory area from a DataNode.    *    * @param clientName       The name of the client.    */
DECL|method|requestShortCircuitShm (String clientName)
name|void
name|requestShortCircuitShm
parameter_list|(
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Receive a block from a source datanode    * and then notifies the namenode    * to remove the copy from the original datanode.    * Note that the source datanode and the original datanode can be different.    * It is used for balancing purpose.    *    * @param blk the block being replaced.    * @param storageType the {@link StorageType} for storing the block.    * @param blockToken security token for accessing the block.    * @param delHint the hint for deleting the block in the original datanode.    * @param source the source datanode for receiving the block.    */
DECL|method|replaceBlock (final ExtendedBlock blk, final StorageType storageType, final Token<BlockTokenIdentifier> blockToken, final String delHint, final DatanodeInfo source)
name|void
name|replaceBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|StorageType
name|storageType
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|delHint
parameter_list|,
specifier|final
name|DatanodeInfo
name|source
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Copy a block.    * It is used for balancing purpose.    *    * @param blk the block being copied.    * @param blockToken security token for accessing the block.    */
DECL|method|copyBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken)
name|void
name|copyBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get block checksum (MD5 of CRC32).    *    * @param blk a block.    * @param blockToken security token for accessing the block.    * @throws IOException    */
DECL|method|blockChecksum (ExtendedBlock blk, Token<BlockTokenIdentifier> blockToken)
name|void
name|blockChecksum
parameter_list|(
name|ExtendedBlock
name|blk
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get striped block group checksum (MD5 of CRC32).    *    * @param stripedBlockInfo a striped block info.    * @param blockToken security token for accessing the block.    * @param requestedNumBytes requested number of bytes in the block group    *                          to compute the checksum.    * @throws IOException    */
DECL|method|blockGroupChecksum (StripedBlockInfo stripedBlockInfo, Token<BlockTokenIdentifier> blockToken, long requestedNumBytes)
name|void
name|blockGroupChecksum
parameter_list|(
name|StripedBlockInfo
name|stripedBlockInfo
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|long
name|requestedNumBytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

