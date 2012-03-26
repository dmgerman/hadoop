begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset
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
name|fsdataset
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|DFSConfigKeys
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
name|Block
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
name|BlockListAsLongs
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
name|BlockLocalPathInfo
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
name|server
operator|.
name|datanode
operator|.
name|DataStorage
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
name|FSDataset
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
name|Replica
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
name|ReplicaInPipelineInterface
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
name|metrics
operator|.
name|FSDatasetMBean
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
name|ReplicaRecoveryInfo
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
name|DiskChecker
operator|.
name|DiskErrorException
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
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * This is a service provider interface for the underlying storage that  * stores replicas for a data node.  * The default implementation stores replicas on local drives.   */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|FsDatasetSpi
specifier|public
interface|interface
name|FsDatasetSpi
parameter_list|<
name|V
extends|extends
name|FsVolumeSpi
parameter_list|>
extends|extends
name|FSDatasetMBean
block|{
comment|/**    * A factory for creating {@link FsDatasetSpi} objects.    */
DECL|class|Factory
specifier|public
specifier|static
specifier|abstract
class|class
name|Factory
parameter_list|<
name|D
extends|extends
name|FsDatasetSpi
parameter_list|<
name|?
parameter_list|>
parameter_list|>
block|{
comment|/** @return the configured factory. */
DECL|method|getFactory (Configuration conf)
specifier|public
specifier|static
name|Factory
argument_list|<
name|?
argument_list|>
name|getFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Factory
argument_list|>
name|clazz
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FSDATASET_FACTORY_KEY
argument_list|,
name|FSDataset
operator|.
name|Factory
operator|.
name|class
argument_list|,
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Create a new object. */
DECL|method|newInstance (DataNode datanode, DataStorage storage, Configuration conf)
specifier|public
specifier|abstract
name|D
name|newInstance
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|DataStorage
name|storage
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Does the factory create simulated objects? */
DECL|method|isSimulated ()
specifier|public
name|boolean
name|isSimulated
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Create rolling logs.    *     * @param prefix the prefix of the log names.    * @return rolling logs    */
DECL|method|createRollingLogs (String bpid, String prefix )
specifier|public
name|RollingLogs
name|createRollingLogs
parameter_list|(
name|String
name|bpid
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @return a list of volumes. */
DECL|method|getVolumes ()
specifier|public
name|List
argument_list|<
name|V
argument_list|>
name|getVolumes
parameter_list|()
function_decl|;
comment|/** @return the volume that contains a replica of the block. */
DECL|method|getVolume (ExtendedBlock b)
specifier|public
name|V
name|getVolume
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
function_decl|;
comment|/** @return a volume information map (name => info). */
DECL|method|getVolumeInfoMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getVolumeInfoMap
parameter_list|()
function_decl|;
comment|/** @return a list of block pools. */
DECL|method|getBlockPoolList ()
specifier|public
name|String
index|[]
name|getBlockPoolList
parameter_list|()
function_decl|;
comment|/** @return a list of finalized blocks for the given block pool. */
DECL|method|getFinalizedBlocks (String bpid)
specifier|public
name|List
argument_list|<
name|Block
argument_list|>
name|getFinalizedBlocks
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * Check whether the in-memory block record matches the block on the disk,    * and, in case that they are not matched, update the record or mark it    * as corrupted.    */
DECL|method|checkAndUpdate (String bpid, long blockId, File diskFile, File diskMetaFile, FsVolumeSpi vol)
specifier|public
name|void
name|checkAndUpdate
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|,
name|File
name|diskFile
parameter_list|,
name|File
name|diskMetaFile
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|)
function_decl|;
comment|/**    * @param b - the block    * @return a stream if the meta-data of the block exists;    *         otherwise, return null.    * @throws IOException    */
DECL|method|getMetaDataInputStream (ExtendedBlock b )
specifier|public
name|LengthInputStream
name|getMetaDataInputStream
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the specified block's on-disk length (excluding metadata)    * @param b    * @return   the specified block's on-disk length (excluding metadta)    * @throws IOException    */
DECL|method|getLength (ExtendedBlock b)
specifier|public
name|long
name|getLength
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get reference to the replica meta info in the replicasMap.     * To be called from methods that are synchronized on {@link FSDataset}    * @param blockId    * @return replica from the replicas map    */
annotation|@
name|Deprecated
DECL|method|getReplica (String bpid, long blockId)
specifier|public
name|Replica
name|getReplica
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
function_decl|;
comment|/**    * @return replica meta information    */
DECL|method|getReplicaString (String bpid, long blockId)
specifier|public
name|String
name|getReplicaString
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
function_decl|;
comment|/**    * @return the generation stamp stored with the block.    */
DECL|method|getStoredBlock (String bpid, long blkid)
specifier|public
name|Block
name|getStoredBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blkid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an input stream at specified offset of the specified block    * @param b    * @param seekOffset    * @return an input stream to read the contents of the specified block,    *  starting at the offset    * @throws IOException    */
DECL|method|getBlockInputStream (ExtendedBlock b, long seekOffset)
specifier|public
name|InputStream
name|getBlockInputStream
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|seekOffset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an input stream at specified offset of the specified block    * The block is still in the tmp directory and is not finalized    * @param b    * @param blkoff    * @param ckoff    * @return an input stream to read the contents of the specified block,    *  starting at the offset    * @throws IOException    */
DECL|method|getTmpInputStreams (ExtendedBlock b, long blkoff, long ckoff)
specifier|public
name|ReplicaInputStreams
name|getTmpInputStreams
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|blkoff
parameter_list|,
name|long
name|ckoff
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a temporary replica and returns the meta information of the replica    *     * @param b block    * @return the meta info of the replica which is being written to    * @throws IOException if an error occurs    */
DECL|method|createTemporary (ExtendedBlock b )
specifier|public
name|ReplicaInPipelineInterface
name|createTemporary
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a RBW replica and returns the meta info of the replica    *     * @param b block    * @return the meta info of the replica which is being written to    * @throws IOException if an error occurs    */
DECL|method|createRbw (ExtendedBlock b )
specifier|public
name|ReplicaInPipelineInterface
name|createRbw
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Recovers a RBW replica and returns the meta info of the replica    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param minBytesRcvd the minimum number of bytes that the replica could have    * @param maxBytesRcvd the maximum number of bytes that the replica could have    * @return the meta info of the replica which is being written to    * @throws IOException if an error occurs    */
DECL|method|recoverRbw (ExtendedBlock b, long newGS, long minBytesRcvd, long maxBytesRcvd)
specifier|public
name|ReplicaInPipelineInterface
name|recoverRbw
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|newGS
parameter_list|,
name|long
name|minBytesRcvd
parameter_list|,
name|long
name|maxBytesRcvd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Covert a temporary replica to a RBW.    * @param temporary the temporary replica being converted    * @return the result RBW    */
DECL|method|convertTemporaryToRbw ( ExtendedBlock temporary)
specifier|public
name|ReplicaInPipelineInterface
name|convertTemporaryToRbw
parameter_list|(
name|ExtendedBlock
name|temporary
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Append to a finalized replica and returns the meta info of the replica    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param expectedBlockLen the number of bytes the replica is expected to have    * @return the meata info of the replica which is being written to    * @throws IOException    */
DECL|method|append (ExtendedBlock b, long newGS, long expectedBlockLen)
specifier|public
name|ReplicaInPipelineInterface
name|append
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|newGS
parameter_list|,
name|long
name|expectedBlockLen
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Recover a failed append to a finalized replica    * and returns the meta info of the replica    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param expectedBlockLen the number of bytes the replica is expected to have    * @return the meta info of the replica which is being written to    * @throws IOException    */
DECL|method|recoverAppend (ExtendedBlock b, long newGS, long expectedBlockLen)
specifier|public
name|ReplicaInPipelineInterface
name|recoverAppend
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|newGS
parameter_list|,
name|long
name|expectedBlockLen
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Recover a failed pipeline close    * It bumps the replica's generation stamp and finalize it if RBW replica    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param expectedBlockLen the number of bytes the replica is expected to have    * @throws IOException    */
DECL|method|recoverClose (ExtendedBlock b, long newGS, long expectedBlockLen )
specifier|public
name|void
name|recoverClose
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|newGS
parameter_list|,
name|long
name|expectedBlockLen
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Finalizes the block previously opened for writing using writeToBlock.    * The block size is what is in the parameter b and it must match the amount    *  of data written    * @param b    * @throws IOException    */
DECL|method|finalizeBlock (ExtendedBlock b)
specifier|public
name|void
name|finalizeBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Unfinalizes the block previously opened for writing using writeToBlock.    * The temporary file associated with this block is deleted.    * @param b    * @throws IOException    */
DECL|method|unfinalizeBlock (ExtendedBlock b)
specifier|public
name|void
name|unfinalizeBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the block report - the full list of blocks stored under a     * block pool    * @param bpid Block Pool Id    * @return - the block report - the full list of blocks stored    */
DECL|method|getBlockReport (String bpid)
specifier|public
name|BlockListAsLongs
name|getBlockReport
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/** Does the dataset contain the block? */
DECL|method|contains (ExtendedBlock block)
specifier|public
name|boolean
name|contains
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
function_decl|;
comment|/**    * Is the block valid?    * @param b    * @return - true if the specified block is valid    */
DECL|method|isValidBlock (ExtendedBlock b)
specifier|public
name|boolean
name|isValidBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
function_decl|;
comment|/**    * Is the block a valid RBW?    * @param b    * @return - true if the specified block is a valid RBW    */
DECL|method|isValidRbw (ExtendedBlock b)
specifier|public
name|boolean
name|isValidRbw
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
function_decl|;
comment|/**    * Invalidates the specified blocks    * @param bpid Block pool Id    * @param invalidBlks - the blocks to be invalidated    * @throws IOException    */
DECL|method|invalidate (String bpid, Block invalidBlks[])
specifier|public
name|void
name|invalidate
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|invalidBlks
index|[]
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Check if all the data directories are healthy      * @throws DiskErrorException      */
DECL|method|checkDataDir ()
specifier|public
name|void
name|checkDataDir
parameter_list|()
throws|throws
name|DiskErrorException
function_decl|;
comment|/**    * Shutdown the FSDataset    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|/**    * Sets the file pointer of the checksum stream so that the last checksum    * will be overwritten    * @param b block    * @param outs The streams for the data file and checksum file    * @param checksumSize number of bytes each checksum has    * @throws IOException    */
DECL|method|adjustCrcChannelPosition (ExtendedBlock b, ReplicaOutputStreams outs, int checksumSize)
specifier|public
name|void
name|adjustCrcChannelPosition
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|ReplicaOutputStreams
name|outs
parameter_list|,
name|int
name|checksumSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks how many valid storage volumes there are in the DataNode.    * @return true if more than the minimum number of valid volumes are left     * in the FSDataSet.    */
DECL|method|hasEnoughResource ()
specifier|public
name|boolean
name|hasEnoughResource
parameter_list|()
function_decl|;
comment|/**    * Get visible length of the specified replica.    */
DECL|method|getReplicaVisibleLength (final ExtendedBlock block)
name|long
name|getReplicaVisibleLength
parameter_list|(
specifier|final
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Initialize a replica recovery.    * @return actual state of the replica on this data-node or     * null if data-node does not have the replica.    */
DECL|method|initReplicaRecovery (RecoveringBlock rBlock )
specifier|public
name|ReplicaRecoveryInfo
name|initReplicaRecovery
parameter_list|(
name|RecoveringBlock
name|rBlock
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update replica's generation stamp and length and finalize it.    * @return the ID of storage that stores the block    */
DECL|method|updateReplicaUnderRecovery (ExtendedBlock oldBlock, long recoveryId, long newLength)
specifier|public
name|String
name|updateReplicaUnderRecovery
parameter_list|(
name|ExtendedBlock
name|oldBlock
parameter_list|,
name|long
name|recoveryId
parameter_list|,
name|long
name|newLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * add new block pool ID    * @param bpid Block pool Id    * @param conf Configuration    */
DECL|method|addBlockPool (String bpid, Configuration conf)
specifier|public
name|void
name|addBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Shutdown and remove the block pool from underlying storage.    * @param bpid Block pool Id to be removed    */
DECL|method|shutdownBlockPool (String bpid)
specifier|public
name|void
name|shutdownBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * Deletes the block pool directories. If force is false, directories are     * deleted only if no block files exist for the block pool. If force     * is true entire directory for the blockpool is deleted along with its    * contents.    * @param bpid BlockPool Id to be deleted.    * @param force If force is false, directories are deleted only if no    *        block files exist for the block pool, otherwise entire     *        directory for the blockpool is deleted along with its contents.    * @throws IOException    */
DECL|method|deleteBlockPool (String bpid, boolean force)
specifier|public
name|void
name|deleteBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get {@link BlockLocalPathInfo} for the given block.    */
DECL|method|getBlockLocalPathInfo (ExtendedBlock b )
specifier|public
name|BlockLocalPathInfo
name|getBlockLocalPathInfo
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

