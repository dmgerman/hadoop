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
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
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
name|ExtendedBlock
import|;
end_import

begin_comment
comment|/**  * This is an interface for the underlying volume.  */
end_comment

begin_interface
DECL|interface|FsVolumeSpi
specifier|public
interface|interface
name|FsVolumeSpi
block|{
comment|/**    * Obtain a reference object that had increased 1 reference count of the    * volume.    *    * It is caller's responsibility to close {@link FsVolumeReference} to decrease    * the reference count on the volume.    */
DECL|method|obtainReference ()
name|FsVolumeReference
name|obtainReference
parameter_list|()
throws|throws
name|ClosedChannelException
function_decl|;
comment|/** @return the StorageUuid of the volume */
DECL|method|getStorageID ()
specifier|public
name|String
name|getStorageID
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
comment|/** @return the available storage space in bytes. */
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** @return the base path to the volume */
DECL|method|getBasePath ()
specifier|public
name|String
name|getBasePath
parameter_list|()
function_decl|;
comment|/** @return the path to the volume */
DECL|method|getPath (String bpid)
specifier|public
name|String
name|getPath
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @return the directory for the finalized blocks in the block pool. */
DECL|method|getFinalizedDir (String bpid)
specifier|public
name|File
name|getFinalizedDir
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
function_decl|;
comment|/** Returns true if the volume is NOT backed by persistent storage. */
DECL|method|isTransientStorage ()
specifier|public
name|boolean
name|isTransientStorage
parameter_list|()
function_decl|;
comment|/**    * Reserve disk space for an RBW block so a writer does not run out of    * space before the block is full.    */
DECL|method|reserveSpaceForRbw (long bytesToReserve)
specifier|public
name|void
name|reserveSpaceForRbw
parameter_list|(
name|long
name|bytesToReserve
parameter_list|)
function_decl|;
comment|/**    * Release disk space previously reserved for RBW block.    */
DECL|method|releaseReservedSpace (long bytesToRelease)
specifier|public
name|void
name|releaseReservedSpace
parameter_list|(
name|long
name|bytesToRelease
parameter_list|)
function_decl|;
comment|/**    * BlockIterator will return ExtendedBlock entries from a block pool in    * this volume.  The entries will be returned in sorted order.<p/>    *    * BlockIterator objects themselves do not always have internal    * synchronization, so they can only safely be used by a single thread at a    * time.<p/>    *    * Closing the iterator does not save it.  You must call save to save it.    */
DECL|interface|BlockIterator
specifier|public
interface|interface
name|BlockIterator
extends|extends
name|Closeable
block|{
comment|/**      * Get the next block.<p/>      *      * Note that this block may be removed in between the time we list it,      * and the time the caller tries to use it, or it may represent a stale      * entry.  Callers should handle the case where the returned block no      * longer exists.      *      * @return               The next block, or null if there are no      *                         more blocks.  Null if there was an error      *                         determining the next block.      *      * @throws IOException   If there was an error getting the next block in      *                         this volume.  In this case, EOF will be set on      *                         the iterator.      */
DECL|method|nextBlock ()
specifier|public
name|ExtendedBlock
name|nextBlock
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns true if we got to the end of the block pool.      */
DECL|method|atEnd ()
specifier|public
name|boolean
name|atEnd
parameter_list|()
function_decl|;
comment|/**      * Repositions the iterator at the beginning of the block pool.      */
DECL|method|rewind ()
specifier|public
name|void
name|rewind
parameter_list|()
function_decl|;
comment|/**      * Save this block iterator to the underlying volume.      * Any existing saved block iterator with this name will be overwritten.      * maxStalenessMs will not be saved.      *      * @throws IOException   If there was an error when saving the block      *                         iterator.      */
DECL|method|save ()
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Set the maximum staleness of entries that we will return.<p/>      *      * A maximum staleness of 0 means we will never return stale entries; a      * larger value will allow us to reduce resource consumption in exchange      * for returning more potentially stale entries.  Even with staleness set      * to 0, consumers of this API must handle race conditions where block      * disappear before they can be processed.      */
DECL|method|setMaxStalenessMs (long maxStalenessMs)
specifier|public
name|void
name|setMaxStalenessMs
parameter_list|(
name|long
name|maxStalenessMs
parameter_list|)
function_decl|;
comment|/**      * Get the wall-clock time, measured in milliseconds since the Epoch,      * when this iterator was created.      */
DECL|method|getIterStartMs ()
specifier|public
name|long
name|getIterStartMs
parameter_list|()
function_decl|;
comment|/**      * Get the wall-clock time, measured in milliseconds since the Epoch,      * when this iterator was last saved.  Returns iterStartMs if the      * iterator was never saved.      */
DECL|method|getLastSavedMs ()
specifier|public
name|long
name|getLastSavedMs
parameter_list|()
function_decl|;
comment|/**      * Get the id of the block pool which this iterator traverses.      */
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
function_decl|;
block|}
comment|/**    * Create a new block iterator.  It will start at the beginning of the    * block set.    *    * @param bpid             The block pool id to iterate over.    * @param name             The name of the block iterator to create.    *    * @return                 The new block iterator.    */
DECL|method|newBlockIterator (String bpid, String name)
specifier|public
name|BlockIterator
name|newBlockIterator
parameter_list|(
name|String
name|bpid
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Load a saved block iterator.    *    * @param bpid             The block pool id to iterate over.    * @param name             The name of the block iterator to load.    *    * @return                 The saved block iterator.    * @throws IOException     If there was an IO error loading the saved    *                           block iterator.    */
DECL|method|loadBlockIterator (String bpid, String name)
specifier|public
name|BlockIterator
name|loadBlockIterator
parameter_list|(
name|String
name|bpid
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the FSDatasetSpi which this volume is a part of.    */
DECL|method|getDataset ()
specifier|public
name|FsDatasetSpi
name|getDataset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

