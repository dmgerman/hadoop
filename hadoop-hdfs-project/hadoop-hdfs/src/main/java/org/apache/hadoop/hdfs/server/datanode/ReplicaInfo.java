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
name|URI
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
name|fs
operator|.
name|LocalFileSystem
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|FsVolumeSpi
operator|.
name|ScanInfo
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
name|LengthInputStream
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
name|LightWeightResizableGSet
import|;
end_import

begin_comment
comment|/**  * This class is used by datanodes to maintain meta data of its replicas.  * It provides a general interface for meta information of a replica.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ReplicaInfo
specifier|abstract
specifier|public
class|class
name|ReplicaInfo
extends|extends
name|Block
implements|implements
name|Replica
implements|,
name|LightWeightResizableGSet
operator|.
name|LinkedElement
block|{
comment|/** For implementing {@link LightWeightResizableGSet.LinkedElement}. */
DECL|field|next
specifier|private
name|LightWeightResizableGSet
operator|.
name|LinkedElement
name|next
decl_stmt|;
comment|/** volume where the replica belongs. */
DECL|field|volume
specifier|private
name|FsVolumeSpi
name|volume
decl_stmt|;
comment|/** This is used by some tests and FsDatasetUtil#computeChecksum. */
DECL|field|DEFAULT_FILE_IO_PROVIDER
specifier|private
specifier|static
specifier|final
name|FileIoProvider
name|DEFAULT_FILE_IO_PROVIDER
init|=
operator|new
name|FileIoProvider
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**    * Constructor.    * @param block a block    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    */
DECL|method|ReplicaInfo (Block block, FsVolumeSpi vol)
name|ReplicaInfo
parameter_list|(
name|Block
name|block
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|)
block|{
name|this
argument_list|(
name|vol
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**   * Constructor   * @param vol volume where replica is located   * @param blockId block id   * @param len replica length   * @param genStamp replica generation stamp   */
DECL|method|ReplicaInfo (FsVolumeSpi vol, long blockId, long len, long genStamp)
name|ReplicaInfo
parameter_list|(
name|FsVolumeSpi
name|vol
parameter_list|,
name|long
name|blockId
parameter_list|,
name|long
name|len
parameter_list|,
name|long
name|genStamp
parameter_list|)
block|{
name|super
argument_list|(
name|blockId
argument_list|,
name|len
argument_list|,
name|genStamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|volume
operator|=
name|vol
expr_stmt|;
block|}
comment|/**    * Copy constructor.    * @param from where to copy from    */
DECL|method|ReplicaInfo (ReplicaInfo from)
name|ReplicaInfo
parameter_list|(
name|ReplicaInfo
name|from
parameter_list|)
block|{
name|this
argument_list|(
name|from
argument_list|,
name|from
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the volume where this replica is located on disk    */
DECL|method|getVolume ()
specifier|public
name|FsVolumeSpi
name|getVolume
parameter_list|()
block|{
return|return
name|volume
return|;
block|}
comment|/**    * Get the {@link FileIoProvider} for disk IO operations.    */
DECL|method|getFileIoProvider ()
specifier|public
name|FileIoProvider
name|getFileIoProvider
parameter_list|()
block|{
comment|// In tests and when invoked via FsDatasetUtil#computeChecksum, the
comment|// target volume for this replica may be unknown and hence null.
comment|// Use the DEFAULT_FILE_IO_PROVIDER with no-op hooks.
return|return
operator|(
name|volume
operator|!=
literal|null
operator|)
condition|?
name|volume
operator|.
name|getFileIoProvider
argument_list|()
else|:
name|DEFAULT_FILE_IO_PROVIDER
return|;
block|}
comment|/**    * Set the volume where this replica is located on disk.    */
DECL|method|setVolume (FsVolumeSpi vol)
name|void
name|setVolume
parameter_list|(
name|FsVolumeSpi
name|vol
parameter_list|)
block|{
name|this
operator|.
name|volume
operator|=
name|vol
expr_stmt|;
block|}
comment|/**    * Get the storageUuid of the volume that stores this replica.    */
annotation|@
name|Override
DECL|method|getStorageUuid ()
specifier|public
name|String
name|getStorageUuid
parameter_list|()
block|{
return|return
name|volume
operator|.
name|getStorageID
argument_list|()
return|;
block|}
comment|/**    * Number of bytes reserved for this replica on disk.    */
DECL|method|getBytesReserved ()
specifier|public
name|long
name|getBytesReserved
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Get the {@code URI} for where the data of this replica is stored.    * @return {@code URI} for the location of replica data.    */
DECL|method|getBlockURI ()
specifier|abstract
specifier|public
name|URI
name|getBlockURI
parameter_list|()
function_decl|;
comment|/**    * Returns an {@link InputStream} to the replica's data.    * @param seekOffset the offset at which the read is started from.    * @return the {@link InputStream} to read the replica data.    * @throws IOException if an error occurs in opening a stream to the data.    */
DECL|method|getDataInputStream (long seekOffset)
specifier|abstract
specifier|public
name|InputStream
name|getDataInputStream
parameter_list|(
name|long
name|seekOffset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an {@link OutputStream} to the replica's data.    * @param append indicates if the block should be opened for append.    * @return the {@link OutputStream} to write to the replica.    * @throws IOException if an error occurs in creating an {@link OutputStream}.    */
DECL|method|getDataOutputStream (boolean append)
specifier|abstract
specifier|public
name|OutputStream
name|getDataOutputStream
parameter_list|(
name|boolean
name|append
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return true if the replica's data exists.    */
DECL|method|blockDataExists ()
specifier|abstract
specifier|public
name|boolean
name|blockDataExists
parameter_list|()
function_decl|;
comment|/**    * Used to deletes the replica's block data.    *    * @return true if the replica's data is successfully deleted.    */
DECL|method|deleteBlockData ()
specifier|abstract
specifier|public
name|boolean
name|deleteBlockData
parameter_list|()
function_decl|;
comment|/**    * @return the length of the block on storage.    */
DECL|method|getBlockDataLength ()
specifier|abstract
specifier|public
name|long
name|getBlockDataLength
parameter_list|()
function_decl|;
comment|/**    * Get the {@code URI} for where the metadata of this replica is stored.    *    * @return {@code URI} for the location of replica metadata.    */
DECL|method|getMetadataURI ()
specifier|abstract
specifier|public
name|URI
name|getMetadataURI
parameter_list|()
function_decl|;
comment|/**    * Returns an {@link InputStream} to the replica's metadata.    * @param offset the offset at which the read is started from.    * @return the {@link LengthInputStream} to read the replica metadata.    * @throws IOException    */
DECL|method|getMetadataInputStream (long offset)
specifier|abstract
specifier|public
name|LengthInputStream
name|getMetadataInputStream
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an {@link OutputStream} to the replica's metadata.    * @param append indicates if the block metadata should be opened for append.    * @return the {@link OutputStream} to write to the replica's metadata.    * @throws IOException if an error occurs in creating an {@link OutputStream}.    */
DECL|method|getMetadataOutputStream (boolean append)
specifier|abstract
specifier|public
name|OutputStream
name|getMetadataOutputStream
parameter_list|(
name|boolean
name|append
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return true if the replica's metadata exists.    */
DECL|method|metadataExists ()
specifier|abstract
specifier|public
name|boolean
name|metadataExists
parameter_list|()
function_decl|;
comment|/**    * Used to deletes the replica's metadata.    *    * @return true if the replica's metadata is successfully deleted.    */
DECL|method|deleteMetadata ()
specifier|abstract
specifier|public
name|boolean
name|deleteMetadata
parameter_list|()
function_decl|;
comment|/**    * @return the length of the metadata on storage.    */
DECL|method|getMetadataLength ()
specifier|abstract
specifier|public
name|long
name|getMetadataLength
parameter_list|()
function_decl|;
comment|/**    * Rename the metadata {@link URI} to that referenced by {@code destURI}.    *    * @param destURI the target {@link URI}.    * @return true if the rename is successful.    * @throws IOException if an exception occurs in the rename.    */
DECL|method|renameMeta (URI destURI)
specifier|abstract
specifier|public
name|boolean
name|renameMeta
parameter_list|(
name|URI
name|destURI
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Rename the data {@link URI} to that referenced by {@code destURI}.    *    * @param destURI the target {@link URI}.    * @return true if the rename is successful.    * @throws IOException if an exception occurs in the rename.    */
DECL|method|renameData (URI destURI)
specifier|abstract
specifier|public
name|boolean
name|renameData
parameter_list|(
name|URI
name|destURI
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update this replica with the {@link StorageLocation} found.    * @param replicaLocation the {@link StorageLocation} found for this replica.    */
DECL|method|updateWithReplica (StorageLocation replicaLocation)
specifier|abstract
specifier|public
name|void
name|updateWithReplica
parameter_list|(
name|StorageLocation
name|replicaLocation
parameter_list|)
function_decl|;
comment|/**    * Check whether the block was pinned.    * @param localFS the local filesystem to use.    * @return true if the block is pinned.    * @throws IOException    */
DECL|method|getPinning (LocalFileSystem localFS)
specifier|abstract
specifier|public
name|boolean
name|getPinning
parameter_list|(
name|LocalFileSystem
name|localFS
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set a block to be pinned on this datanode so that it cannot be moved    * by Balancer/Mover.    *    * @param localFS the local filesystem to use.    * @throws IOException if there is an exception in the pinning.    */
DECL|method|setPinning (LocalFileSystem localFS)
specifier|abstract
specifier|public
name|void
name|setPinning
parameter_list|(
name|LocalFileSystem
name|localFS
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Bump a replica's generation stamp to a new one.    * Its on-disk meta file name is renamed to be the new one too.    *    * @param newGS new generation stamp    * @throws IOException if the change fails    */
DECL|method|bumpReplicaGS (long newGS)
specifier|abstract
specifier|public
name|void
name|bumpReplicaGS
parameter_list|(
name|long
name|newGS
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getOriginalReplica ()
specifier|abstract
specifier|public
name|ReplicaInfo
name|getOriginalReplica
parameter_list|()
function_decl|;
comment|/**    * Get the recovery id.    * @return the generation stamp that the replica will be bumped to    */
DECL|method|getRecoveryID ()
specifier|abstract
specifier|public
name|long
name|getRecoveryID
parameter_list|()
function_decl|;
comment|/**    * Set the recovery id.    * @param recoveryId the new recoveryId    */
DECL|method|setRecoveryID (long recoveryId)
specifier|abstract
specifier|public
name|void
name|setRecoveryID
parameter_list|(
name|long
name|recoveryId
parameter_list|)
function_decl|;
DECL|method|breakHardLinksIfNeeded ()
specifier|abstract
specifier|public
name|boolean
name|breakHardLinksIfNeeded
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|createInfo ()
specifier|abstract
specifier|public
name|ReplicaRecoveryInfo
name|createInfo
parameter_list|()
function_decl|;
DECL|method|compareWith (ScanInfo info)
specifier|abstract
specifier|public
name|int
name|compareWith
parameter_list|(
name|ScanInfo
name|info
parameter_list|)
function_decl|;
DECL|method|truncateBlock (long newLength)
specifier|abstract
specifier|public
name|void
name|truncateBlock
parameter_list|(
name|long
name|newLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|copyMetadata (URI destination)
specifier|abstract
specifier|public
name|void
name|copyMetadata
parameter_list|(
name|URI
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|copyBlockdata (URI destination)
specifier|abstract
specifier|public
name|void
name|copyBlockdata
parameter_list|(
name|URI
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Number of bytes originally reserved for this replica. The actual    * reservation is adjusted as data is written to disk.    *    * @return the number of bytes originally reserved for this replica.    */
DECL|method|getOriginalBytesReserved ()
specifier|public
name|long
name|getOriginalBytesReserved
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
comment|//Object
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", "
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|", "
operator|+
name|getState
argument_list|()
operator|+
literal|"\n  getNumBytes()     = "
operator|+
name|getNumBytes
argument_list|()
operator|+
literal|"\n  getBytesOnDisk()  = "
operator|+
name|getBytesOnDisk
argument_list|()
operator|+
literal|"\n  getVisibleLength()= "
operator|+
name|getVisibleLength
argument_list|()
operator|+
literal|"\n  getVolume()       = "
operator|+
name|getVolume
argument_list|()
operator|+
literal|"\n  getBlockURI()     = "
operator|+
name|getBlockURI
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isOnTransientStorage ()
specifier|public
name|boolean
name|isOnTransientStorage
parameter_list|()
block|{
return|return
name|volume
operator|.
name|isTransientStorage
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNext ()
specifier|public
name|LightWeightResizableGSet
operator|.
name|LinkedElement
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
annotation|@
name|Override
DECL|method|setNext (LightWeightResizableGSet.LinkedElement next)
specifier|public
name|void
name|setNext
parameter_list|(
name|LightWeightResizableGSet
operator|.
name|LinkedElement
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
block|}
end_class

end_unit

