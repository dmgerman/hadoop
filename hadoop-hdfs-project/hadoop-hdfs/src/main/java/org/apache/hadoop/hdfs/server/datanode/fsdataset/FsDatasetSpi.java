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
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

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
name|FileNotFoundException
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
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|util
operator|.
name|AutoCloseableLock
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|ReplicaState
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
name|ReplicaInPipeline
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
name|ReplicaHandler
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
name|ReplicaInfo
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
name|ReplicaNotFoundException
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
name|StorageLocation
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
name|UnexpectedReplicaStateException
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
name|fsdataset
operator|.
name|impl
operator|.
name|FsDatasetFactory
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
name|DatanodeStorage
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
name|NamespaceInfo
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|StorageReport
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
name|VolumeFailureSummary
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
name|FsDatasetFactory
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
comment|/**    * It behaviors as an unmodifiable list of FsVolume. Individual FsVolume can    * be obtained by using {@link #get(int)}.    *    * This also holds the reference counts for these volumes. It releases all the    * reference counts in {@link #close()}.    */
DECL|class|FsVolumeReferences
class|class
name|FsVolumeReferences
implements|implements
name|Iterable
argument_list|<
name|FsVolumeSpi
argument_list|>
implements|,
name|Closeable
block|{
DECL|field|references
specifier|private
specifier|final
name|List
argument_list|<
name|FsVolumeReference
argument_list|>
name|references
decl_stmt|;
DECL|method|FsVolumeReferences (List<S> curVolumes)
specifier|public
parameter_list|<
name|S
extends|extends
name|FsVolumeSpi
parameter_list|>
name|FsVolumeReferences
parameter_list|(
name|List
argument_list|<
name|S
argument_list|>
name|curVolumes
parameter_list|)
block|{
name|references
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|FsVolumeSpi
name|v
range|:
name|curVolumes
control|)
block|{
try|try
block|{
name|references
operator|.
name|add
argument_list|(
name|v
operator|.
name|obtainReference
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
comment|// This volume has been closed.
block|}
block|}
block|}
DECL|class|FsVolumeSpiIterator
specifier|private
specifier|static
class|class
name|FsVolumeSpiIterator
implements|implements
name|Iterator
argument_list|<
name|FsVolumeSpi
argument_list|>
block|{
DECL|field|references
specifier|private
specifier|final
name|List
argument_list|<
name|FsVolumeReference
argument_list|>
name|references
decl_stmt|;
DECL|field|idx
specifier|private
name|int
name|idx
init|=
literal|0
decl_stmt|;
DECL|method|FsVolumeSpiIterator (List<FsVolumeReference> refs)
name|FsVolumeSpiIterator
parameter_list|(
name|List
argument_list|<
name|FsVolumeReference
argument_list|>
name|refs
parameter_list|)
block|{
name|references
operator|=
name|refs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|idx
operator|<
name|references
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|FsVolumeSpi
name|next
parameter_list|()
block|{
name|int
name|refIdx
init|=
name|idx
operator|++
decl_stmt|;
return|return
name|references
operator|.
name|get
argument_list|(
name|refIdx
argument_list|)
operator|.
name|getVolume
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|FsVolumeSpi
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|FsVolumeSpiIterator
argument_list|(
name|references
argument_list|)
return|;
block|}
comment|/**      * Get the number of volumes.      */
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|references
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Get the volume for a given index.      */
DECL|method|get (int index)
specifier|public
name|FsVolumeSpi
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|references
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getVolume
argument_list|()
return|;
block|}
comment|/**      * Get the reference for a given index.      */
DECL|method|getReference (int index)
specifier|public
name|FsVolumeReference
name|getReference
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|references
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FsVolumeReference
name|ref
range|:
name|references
control|)
block|{
try|try
block|{
name|ref
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ioe
operator|=
name|e
expr_stmt|;
block|}
block|}
name|references
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
comment|/**    * Returns a list of FsVolumes that hold reference counts.    *    * The caller must release the reference of each volume by calling    * {@link FsVolumeReferences#close()}.    */
DECL|method|getFsVolumeReferences ()
name|FsVolumeReferences
name|getFsVolumeReferences
parameter_list|()
function_decl|;
comment|/**    * Add a new volume to the FsDataset.<p/>    *    * If the FSDataset supports block scanning, this function registers    * the new volume with the block scanner.    *    * @param location      The storage location for the new volume.    * @param nsInfos       Namespace information for the new volume.    */
DECL|method|addVolume ( final StorageLocation location, final List<NamespaceInfo> nsInfos)
name|void
name|addVolume
parameter_list|(
specifier|final
name|StorageLocation
name|location
parameter_list|,
specifier|final
name|List
argument_list|<
name|NamespaceInfo
argument_list|>
name|nsInfos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes a collection of volumes from FsDataset.    *    * If the FSDataset supports block scanning, this function removes    * the volumes from the block scanner.    *    * @param volumes  The paths of the volumes to be removed.    * @param clearFailure set true to clear the failure information about the    *                     volumes.    */
DECL|method|removeVolumes (Collection<StorageLocation> volumes, boolean clearFailure)
name|void
name|removeVolumes
parameter_list|(
name|Collection
argument_list|<
name|StorageLocation
argument_list|>
name|volumes
parameter_list|,
name|boolean
name|clearFailure
parameter_list|)
function_decl|;
comment|/** @return a storage with the given storage ID */
DECL|method|getStorage (final String storageUuid)
name|DatanodeStorage
name|getStorage
parameter_list|(
specifier|final
name|String
name|storageUuid
parameter_list|)
function_decl|;
comment|/** @return one or more storage reports for attached volumes. */
DECL|method|getStorageReports (String bpid)
name|StorageReport
index|[]
name|getStorageReports
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @return the volume that contains a replica of the block. */
DECL|method|getVolume (ExtendedBlock b)
name|V
name|getVolume
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
function_decl|;
comment|/** @return a volume information map (name => info). */
DECL|method|getVolumeInfoMap ()
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getVolumeInfoMap
parameter_list|()
function_decl|;
comment|/**    * Returns info about volume failures.    *    * @return info about volume failures, possibly null    */
DECL|method|getVolumeFailureSummary ()
name|VolumeFailureSummary
name|getVolumeFailureSummary
parameter_list|()
function_decl|;
comment|/**    * Gets a list of references to the finalized blocks for the given block pool.    *<p>    * Callers of this function should call    * {@link FsDatasetSpi#acquireDatasetLock} to avoid blocks' status being    * changed during list iteration.    *</p>    * @return a list of references to the finalized blocks for the given block    *         pool.    */
DECL|method|getFinalizedBlocks (String bpid)
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
name|getFinalizedBlocks
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * Check whether the in-memory block record matches the block on the disk,    * and, in case that they are not matched, update the record or mark it    * as corrupted.    */
DECL|method|checkAndUpdate (String bpid, long blockId, File diskFile, File diskMetaFile, FsVolumeSpi vol)
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
throws|throws
name|IOException
function_decl|;
comment|/**    * @param b - the block    * @return a stream if the meta-data of the block exists;    *         otherwise, return null.    * @throws IOException    */
DECL|method|getMetaDataInputStream (ExtendedBlock b )
name|LengthInputStream
name|getMetaDataInputStream
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the specified block's on-disk length (excluding metadata).    * @return   the specified block's on-disk length (excluding metadta)    * @throws IOException on error    */
DECL|method|getLength (ExtendedBlock b)
name|long
name|getLength
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get reference to the replica meta info in the replicasMap.     * To be called from methods that are synchronized on {@link FSDataset}    * @return replica from the replicas map    */
annotation|@
name|Deprecated
DECL|method|getReplica (String bpid, long blockId)
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
comment|/**    * Returns an input stream at specified offset of the specified block.    * @param b block    * @param seekOffset offset with in the block to seek to    * @return an input stream to read the contents of the specified block,    *  starting at the offset    * @throws IOException    */
DECL|method|getBlockInputStream (ExtendedBlock b, long seekOffset)
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
comment|/**    * Returns an input stream at specified offset of the specified block.    * The block is still in the tmp directory and is not finalized    * @return an input stream to read the contents of the specified block,    *  starting at the offset    * @throws IOException    */
DECL|method|getTmpInputStreams (ExtendedBlock b, long blkoff, long ckoff)
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
comment|/**    * Creates a temporary replica and returns the meta information of the replica    * .    *     * @param b block    * @return the meta info of the replica which is being written to    * @throws IOException if an error occurs    */
DECL|method|createTemporary (StorageType storageType, ExtendedBlock b)
name|ReplicaHandler
name|createTemporary
parameter_list|(
name|StorageType
name|storageType
parameter_list|,
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a RBW replica and returns the meta info of the replica    *     * @param b block    * @return the meta info of the replica which is being written to    * @throws IOException if an error occurs    */
DECL|method|createRbw (StorageType storageType, ExtendedBlock b, boolean allowLazyPersist)
name|ReplicaHandler
name|createRbw
parameter_list|(
name|StorageType
name|storageType
parameter_list|,
name|ExtendedBlock
name|b
parameter_list|,
name|boolean
name|allowLazyPersist
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Recovers a RBW replica and returns the meta info of the replica.    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param minBytesRcvd the minimum number of bytes that the replica could have    * @param maxBytesRcvd the maximum number of bytes that the replica could have    * @return the meta info of the replica which is being written to    * @throws IOException if an error occurs    */
DECL|method|recoverRbw (ExtendedBlock b, long newGS, long minBytesRcvd, long maxBytesRcvd)
name|ReplicaHandler
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
name|ReplicaInPipeline
name|convertTemporaryToRbw
parameter_list|(
name|ExtendedBlock
name|temporary
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Append to a finalized replica and returns the meta info of the replica.    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param expectedBlockLen the number of bytes the replica is expected to have    * @return the meata info of the replica which is being written to    * @throws IOException    */
DECL|method|append (ExtendedBlock b, long newGS, long expectedBlockLen)
name|ReplicaHandler
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
comment|/**    * Recover a failed append to a finalized replica and returns the meta    * info of the replica.    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param expectedBlockLen the number of bytes the replica is expected to have    * @return the meta info of the replica which is being written to    * @throws IOException    */
DECL|method|recoverAppend ( ExtendedBlock b, long newGS, long expectedBlockLen)
name|ReplicaHandler
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
comment|/**    * Recover a failed pipeline close.    * It bumps the replica's generation stamp and finalize it if RBW replica    *     * @param b block    * @param newGS the new generation stamp for the replica    * @param expectedBlockLen the number of bytes the replica is expected to have    * @return the storage uuid of the replica.    * @throws IOException    */
DECL|method|recoverClose (ExtendedBlock b, long newGS, long expectedBlockLen )
name|Replica
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
comment|/**    * Finalizes the block previously opened for writing using writeToBlock.    * The block size is what is in the parameter b and it must match the amount    *  of data written    * @throws IOException    * @throws ReplicaNotFoundException if the replica can not be found when the    * block is been finalized. For instance, the block resides on an HDFS volume    * that has been removed.    */
DECL|method|finalizeBlock (ExtendedBlock b)
name|void
name|finalizeBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Unfinalizes the block previously opened for writing using writeToBlock.    * The temporary file associated with this block is deleted.    * @throws IOException    */
DECL|method|unfinalizeBlock (ExtendedBlock b)
name|void
name|unfinalizeBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns one block report per volume.    * @param bpid Block Pool Id    * @return - a map of DatanodeStorage to block report for the volume.    */
DECL|method|getBlockReports (String bpid)
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
name|getBlockReports
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * Returns the cache report - the full list of cached block IDs of a    * block pool.    * @param   bpid Block Pool Id    * @return  the cache report - the full list of cached block IDs.    */
DECL|method|getCacheReport (String bpid)
name|List
argument_list|<
name|Long
argument_list|>
name|getCacheReport
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/** Does the dataset contain the block? */
DECL|method|contains (ExtendedBlock block)
name|boolean
name|contains
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
function_decl|;
comment|/**    * Check if a block is valid.    *    * @param b           The block to check.    * @param minLength   The minimum length that the block must have.  May be 0.    * @param state       If this is null, it is ignored.  If it is non-null, we    *                        will check that the replica has this state.    *    * @throws ReplicaNotFoundException          If the replica is not found    *    * @throws UnexpectedReplicaStateException   If the replica is not in the     *                                             expected state.    * @throws FileNotFoundException             If the block file is not found or there     *                                              was an error locating it.    * @throws EOFException                      If the replica length is too short.    *     * @throws IOException                       May be thrown from the methods called.     */
DECL|method|checkBlock (ExtendedBlock b, long minLength, ReplicaState state)
name|void
name|checkBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|minLength
parameter_list|,
name|ReplicaState
name|state
parameter_list|)
throws|throws
name|ReplicaNotFoundException
throws|,
name|UnexpectedReplicaStateException
throws|,
name|FileNotFoundException
throws|,
name|EOFException
throws|,
name|IOException
function_decl|;
comment|/**    * Is the block valid?    * @return - true if the specified block is valid    */
DECL|method|isValidBlock (ExtendedBlock b)
name|boolean
name|isValidBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
function_decl|;
comment|/**    * Is the block a valid RBW?    * @return - true if the specified block is a valid RBW    */
DECL|method|isValidRbw (ExtendedBlock b)
name|boolean
name|isValidRbw
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
function_decl|;
comment|/**    * Invalidates the specified blocks    * @param bpid Block pool Id    * @param invalidBlks - the blocks to be invalidated    * @throws IOException    */
DECL|method|invalidate (String bpid, Block invalidBlks[])
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
comment|/**    * Caches the specified blocks    * @param bpid Block pool id    * @param blockIds - block ids to cache    */
DECL|method|cache (String bpid, long[] blockIds)
name|void
name|cache
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
index|[]
name|blockIds
parameter_list|)
function_decl|;
comment|/**    * Uncaches the specified blocks    * @param bpid Block pool id    * @param blockIds - blocks ids to uncache    */
DECL|method|uncache (String bpid, long[] blockIds)
name|void
name|uncache
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
index|[]
name|blockIds
parameter_list|)
function_decl|;
comment|/**    * Determine if the specified block is cached.    * @param bpid Block pool id    * @param blockIds - block id    * @return true if the block is cached    */
DECL|method|isCached (String bpid, long blockId)
name|boolean
name|isCached
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
function_decl|;
comment|/**      * Check if all the data directories are healthy      * @return A set of unhealthy data directories.      */
DECL|method|checkDataDir ()
name|Set
argument_list|<
name|StorageLocation
argument_list|>
name|checkDataDir
parameter_list|()
function_decl|;
comment|/**    * Shutdown the FSDataset    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|/**    * Sets the file pointer of the checksum stream so that the last checksum    * will be overwritten    * @param b block    * @param outs The streams for the data file and checksum file    * @param checksumSize number of bytes each checksum has    * @throws IOException    */
DECL|method|adjustCrcChannelPosition (ExtendedBlock b, ReplicaOutputStreams outs, int checksumSize)
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
DECL|method|updateReplicaUnderRecovery (ExtendedBlock oldBlock, long recoveryId, long newBlockId, long newLength)
name|Replica
name|updateReplicaUnderRecovery
parameter_list|(
name|ExtendedBlock
name|oldBlock
parameter_list|,
name|long
name|recoveryId
parameter_list|,
name|long
name|newBlockId
parameter_list|,
name|long
name|newLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * add new block pool ID    * @param bpid Block pool Id    * @param conf Configuration    */
DECL|method|addBlockPool (String bpid, Configuration conf)
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
name|void
name|shutdownBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * Deletes the block pool directories. If force is false, directories are     * deleted only if no block files exist for the block pool. If force     * is true entire directory for the blockpool is deleted along with its    * contents.    * @param bpid BlockPool Id to be deleted.    * @param force If force is false, directories are deleted only if no    *        block files exist for the block pool, otherwise entire     *        directory for the blockpool is deleted along with its contents.    * @throws IOException    */
DECL|method|deleteBlockPool (String bpid, boolean force)
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
name|BlockLocalPathInfo
name|getBlockLocalPathInfo
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Enable 'trash' for the given dataset. When trash is enabled, files are    * moved to a separate trash directory instead of being deleted immediately.    * This can be useful for example during rolling upgrades.    */
DECL|method|enableTrash (String bpid)
name|void
name|enableTrash
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * Clear trash    */
DECL|method|clearTrash (String bpid)
name|void
name|clearTrash
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * @return true when trash is enabled    */
DECL|method|trashEnabled (String bpid)
name|boolean
name|trashEnabled
parameter_list|(
name|String
name|bpid
parameter_list|)
function_decl|;
comment|/**    * Create a marker file indicating that a rolling upgrade is in progress.    */
DECL|method|setRollingUpgradeMarker (String bpid)
name|void
name|setRollingUpgradeMarker
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete the rolling upgrade marker file if it exists.    * @param bpid    */
DECL|method|clearRollingUpgradeMarker (String bpid)
name|void
name|clearRollingUpgradeMarker
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * submit a sync_file_range request to AsyncDiskService.    */
DECL|method|submitBackgroundSyncFileRangeRequest (final ExtendedBlock block, final ReplicaOutputStreams outs, final long offset, final long nbytes, final int flags)
name|void
name|submitBackgroundSyncFileRangeRequest
parameter_list|(
specifier|final
name|ExtendedBlock
name|block
parameter_list|,
specifier|final
name|ReplicaOutputStreams
name|outs
parameter_list|,
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|long
name|nbytes
parameter_list|,
specifier|final
name|int
name|flags
parameter_list|)
function_decl|;
comment|/**    * Callback from RamDiskAsyncLazyPersistService upon async lazy persist task end    */
DECL|method|onCompleteLazyPersist (String bpId, long blockId, long creationTime, File[] savedFiles, V targetVolume)
name|void
name|onCompleteLazyPersist
parameter_list|(
name|String
name|bpId
parameter_list|,
name|long
name|blockId
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|File
index|[]
name|savedFiles
parameter_list|,
name|V
name|targetVolume
parameter_list|)
function_decl|;
comment|/**     * Callback from RamDiskAsyncLazyPersistService upon async lazy persist task fail     */
DECL|method|onFailLazyPersist (String bpId, long blockId)
name|void
name|onFailLazyPersist
parameter_list|(
name|String
name|bpId
parameter_list|,
name|long
name|blockId
parameter_list|)
function_decl|;
comment|/**      * Move block from one storage to another storage      */
DECL|method|moveBlockAcrossStorage (final ExtendedBlock block, StorageType targetStorageType)
name|ReplicaInfo
name|moveBlockAcrossStorage
parameter_list|(
specifier|final
name|ExtendedBlock
name|block
parameter_list|,
name|StorageType
name|targetStorageType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set a block to be pinned on this datanode so that it cannot be moved    * by Balancer/Mover.    *    * It is a no-op when dfs.datanode.block-pinning.enabled is set to false.    */
DECL|method|setPinning (ExtendedBlock block)
name|void
name|setPinning
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check whether the block was pinned    */
DECL|method|getPinning (ExtendedBlock block)
name|boolean
name|getPinning
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Confirm whether the block is deleting    */
DECL|method|isDeletingBlock (String bpid, long blockId)
name|boolean
name|isDeletingBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
function_decl|;
comment|/**    * Moves a given block from one volume to another volume. This is used by disk    * balancer.    *    * @param block       - ExtendedBlock    * @param destination - Destination volume    * @return Old replica info    */
DECL|method|moveBlockAcrossVolumes (final ExtendedBlock block, FsVolumeSpi destination)
name|ReplicaInfo
name|moveBlockAcrossVolumes
parameter_list|(
specifier|final
name|ExtendedBlock
name|block
parameter_list|,
name|FsVolumeSpi
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Acquire the lock of the data set.    */
DECL|method|acquireDatasetLock ()
name|AutoCloseableLock
name|acquireDatasetLock
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

