begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
operator|.
name|impl
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|TreeMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeReference
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
name|VolumeChoosingPolicy
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
name|BlockScanner
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
name|Time
import|;
end_import

begin_class
DECL|class|FsVolumeList
class|class
name|FsVolumeList
block|{
DECL|field|volumes
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|FsVolumeImpl
argument_list|>
name|volumes
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Tracks volume failures, sorted by volume path.
DECL|field|volumeFailureInfos
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|VolumeFailureInfo
argument_list|>
name|volumeFailureInfos
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|VolumeFailureInfo
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|checkDirsMutex
specifier|private
name|Object
name|checkDirsMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|blockChooser
specifier|private
specifier|final
name|VolumeChoosingPolicy
argument_list|<
name|FsVolumeImpl
argument_list|>
name|blockChooser
decl_stmt|;
DECL|field|blockScanner
specifier|private
specifier|final
name|BlockScanner
name|blockScanner
decl_stmt|;
DECL|method|FsVolumeList (List<VolumeFailureInfo> initialVolumeFailureInfos, BlockScanner blockScanner, VolumeChoosingPolicy<FsVolumeImpl> blockChooser)
name|FsVolumeList
parameter_list|(
name|List
argument_list|<
name|VolumeFailureInfo
argument_list|>
name|initialVolumeFailureInfos
parameter_list|,
name|BlockScanner
name|blockScanner
parameter_list|,
name|VolumeChoosingPolicy
argument_list|<
name|FsVolumeImpl
argument_list|>
name|blockChooser
parameter_list|)
block|{
name|this
operator|.
name|blockChooser
operator|=
name|blockChooser
expr_stmt|;
name|this
operator|.
name|blockScanner
operator|=
name|blockScanner
expr_stmt|;
for|for
control|(
name|VolumeFailureInfo
name|volumeFailureInfo
range|:
name|initialVolumeFailureInfos
control|)
block|{
name|volumeFailureInfos
operator|.
name|put
argument_list|(
name|volumeFailureInfo
operator|.
name|getFailedStorageLocation
argument_list|()
argument_list|,
name|volumeFailureInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Return an immutable list view of all the volumes.    */
DECL|method|getVolumes ()
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|getVolumes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|volumes
argument_list|)
return|;
block|}
DECL|method|chooseVolume (List<FsVolumeImpl> list, long blockSize)
specifier|private
name|FsVolumeReference
name|chooseVolume
parameter_list|(
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|list
parameter_list|,
name|long
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|FsVolumeImpl
name|volume
init|=
name|blockChooser
operator|.
name|chooseVolume
argument_list|(
name|list
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|volume
operator|.
name|obtainReference
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Chosen a closed volume: "
operator|+
name|volume
argument_list|)
expr_stmt|;
comment|// blockChooser.chooseVolume returns DiskOutOfSpaceException when the list
comment|// is empty, indicating that all volumes are closed.
name|list
operator|.
name|remove
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**     * Get next volume.    *    * @param blockSize free space needed on the volume    * @param storageType the desired {@link StorageType}     * @return next volume to store the block in.    */
DECL|method|getNextVolume (StorageType storageType, long blockSize)
name|FsVolumeReference
name|getNextVolume
parameter_list|(
name|StorageType
name|storageType
parameter_list|,
name|long
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|FsVolumeImpl
name|v
range|:
name|volumes
control|)
block|{
if|if
condition|(
name|v
operator|.
name|getStorageType
argument_list|()
operator|==
name|storageType
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|chooseVolume
argument_list|(
name|list
argument_list|,
name|blockSize
argument_list|)
return|;
block|}
comment|/**    * Get next volume.    *    * @param blockSize free space needed on the volume    * @return next volume to store the block in.    */
DECL|method|getNextTransientVolume (long blockSize)
name|FsVolumeReference
name|getNextTransientVolume
parameter_list|(
name|long
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get a snapshot of currently available volumes.
specifier|final
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|curVolumes
init|=
name|getVolumes
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|curVolumes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|FsVolumeImpl
name|v
range|:
name|curVolumes
control|)
block|{
if|if
condition|(
name|v
operator|.
name|isTransientStorage
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|chooseVolume
argument_list|(
name|list
argument_list|,
name|blockSize
argument_list|)
return|;
block|}
DECL|method|getDfsUsed ()
name|long
name|getDfsUsed
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|dfsUsed
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|FsVolumeImpl
name|v
range|:
name|volumes
control|)
block|{
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|v
operator|.
name|obtainReference
argument_list|()
init|)
block|{
name|dfsUsed
operator|+=
name|v
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
comment|// ignore.
block|}
block|}
return|return
name|dfsUsed
return|;
block|}
DECL|method|getBlockPoolUsed (String bpid)
name|long
name|getBlockPoolUsed
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|dfsUsed
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|FsVolumeImpl
name|v
range|:
name|volumes
control|)
block|{
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|v
operator|.
name|obtainReference
argument_list|()
init|)
block|{
name|dfsUsed
operator|+=
name|v
operator|.
name|getBlockPoolUsed
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
comment|// ignore.
block|}
block|}
return|return
name|dfsUsed
return|;
block|}
DECL|method|getCapacity ()
name|long
name|getCapacity
parameter_list|()
block|{
name|long
name|capacity
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|FsVolumeImpl
name|v
range|:
name|volumes
control|)
block|{
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|v
operator|.
name|obtainReference
argument_list|()
init|)
block|{
name|capacity
operator|+=
name|v
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore.
block|}
block|}
return|return
name|capacity
return|;
block|}
DECL|method|getRemaining ()
name|long
name|getRemaining
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|remaining
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|FsVolumeSpi
name|vol
range|:
name|volumes
control|)
block|{
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|vol
operator|.
name|obtainReference
argument_list|()
init|)
block|{
name|remaining
operator|+=
name|vol
operator|.
name|getAvailable
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
name|remaining
return|;
block|}
DECL|method|getAllVolumesMap (final String bpid, final ReplicaMap volumeMap, final RamDiskReplicaTracker ramDiskReplicaMap)
name|void
name|getAllVolumesMap
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|ReplicaMap
name|volumeMap
parameter_list|,
specifier|final
name|RamDiskReplicaTracker
name|ramDiskReplicaMap
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|totalStartTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|IOException
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|replicaAddingThreads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|FsVolumeImpl
name|v
range|:
name|volumes
control|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|v
operator|.
name|obtainReference
argument_list|()
init|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding replicas to map for block pool "
operator|+
name|bpid
operator|+
literal|" on volume "
operator|+
name|v
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|v
operator|.
name|getVolumeMap
argument_list|(
name|bpid
argument_list|,
name|volumeMap
argument_list|,
name|ramDiskReplicaMap
argument_list|)
expr_stmt|;
name|long
name|timeTaken
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Time to add replicas to map for block pool"
operator|+
literal|" "
operator|+
name|bpid
operator|+
literal|" on volume "
operator|+
name|v
operator|+
literal|": "
operator|+
name|timeTaken
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"The volume "
operator|+
name|v
operator|+
literal|" is closed while "
operator|+
literal|"addng replicas, ignored."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught exception while adding replicas "
operator|+
literal|"from "
operator|+
name|v
operator|+
literal|". Will throw later."
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|replicaAddingThreads
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|replicaAddingThreads
control|)
block|{
try|try
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
block|}
name|long
name|totalTimeTaken
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|totalStartTime
decl_stmt|;
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Total time to add all replicas to map: "
operator|+
name|totalTimeTaken
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link FsVolumeImpl#checkDirs()} on each volume.    *     * Use checkDirsMutext to allow only one instance of checkDirs() call    *    * @return list of all the failed volumes.    */
DECL|method|checkDirs ()
name|Set
argument_list|<
name|File
argument_list|>
name|checkDirs
parameter_list|()
block|{
synchronized|synchronized
init|(
name|checkDirsMutex
init|)
block|{
name|Set
argument_list|<
name|File
argument_list|>
name|failedVols
init|=
literal|null
decl_stmt|;
comment|// Make a copy of volumes for performing modification
specifier|final
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|volumeList
init|=
name|getVolumes
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|FsVolumeImpl
argument_list|>
name|i
init|=
name|volumeList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|FsVolumeImpl
name|fsv
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|fsv
operator|.
name|obtainReference
argument_list|()
init|)
block|{
name|fsv
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskErrorException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Removing failed volume "
operator|+
name|fsv
operator|+
literal|": "
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|failedVols
operator|==
literal|null
condition|)
block|{
name|failedVols
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|failedVols
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|fsv
operator|.
name|getBasePath
argument_list|()
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
name|addVolumeFailureInfo
argument_list|(
name|fsv
argument_list|)
expr_stmt|;
name|removeVolume
argument_list|(
name|fsv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caught exception when obtaining "
operator|+
literal|"reference count on closed volume"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected IOException"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|failedVols
operator|!=
literal|null
operator|&&
name|failedVols
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Completed checkDirs. Found "
operator|+
name|failedVols
operator|.
name|size
argument_list|()
operator|+
literal|" failure volumes."
argument_list|)
expr_stmt|;
block|}
return|return
name|failedVols
return|;
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
name|volumes
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Dynamically add new volumes to the existing volumes that this DN manages.    *    * @param ref       a reference to the new FsVolumeImpl instance.    */
DECL|method|addVolume (FsVolumeReference ref)
name|void
name|addVolume
parameter_list|(
name|FsVolumeReference
name|ref
parameter_list|)
block|{
name|FsVolumeImpl
name|volume
init|=
operator|(
name|FsVolumeImpl
operator|)
name|ref
operator|.
name|getVolume
argument_list|()
decl_stmt|;
name|volumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockScanner
operator|!=
literal|null
condition|)
block|{
name|blockScanner
operator|.
name|addVolumeScanner
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the volume is not put into a volume scanner, it does not need to
comment|// hold the reference.
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|FsDatasetImpl
operator|.
name|LOG
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
comment|// If the volume is used to replace a failed volume, it needs to reset the
comment|// volume failure info for this volume.
name|removeVolumeFailureInfo
argument_list|(
operator|new
name|File
argument_list|(
name|volume
operator|.
name|getBasePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Added new volume: "
operator|+
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Dynamically remove a volume in the list.    * @param target the volume instance to be removed.    */
DECL|method|removeVolume (FsVolumeImpl target)
specifier|private
name|void
name|removeVolume
parameter_list|(
name|FsVolumeImpl
name|target
parameter_list|)
block|{
if|if
condition|(
name|volumes
operator|.
name|remove
argument_list|(
name|target
argument_list|)
condition|)
block|{
if|if
condition|(
name|blockScanner
operator|!=
literal|null
condition|)
block|{
name|blockScanner
operator|.
name|removeVolumeScanner
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|target
operator|.
name|closeAndWait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error occurs when waiting volume to close: "
operator|+
name|target
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|target
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed volume: "
operator|+
name|target
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Volume "
operator|+
name|target
operator|+
literal|" does not exist or is removed by others."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Dynamically remove volume in the list.    * @param volume the volume to be removed.    * @param clearFailure set true to remove failure info for this volume.    */
DECL|method|removeVolume (File volume, boolean clearFailure)
name|void
name|removeVolume
parameter_list|(
name|File
name|volume
parameter_list|,
name|boolean
name|clearFailure
parameter_list|)
block|{
for|for
control|(
name|FsVolumeImpl
name|fsVolume
range|:
name|volumes
control|)
block|{
name|String
name|basePath
init|=
operator|new
name|File
argument_list|(
name|fsVolume
operator|.
name|getBasePath
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|targetPath
init|=
name|volume
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
name|basePath
operator|.
name|equals
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
name|removeVolume
argument_list|(
name|fsVolume
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clearFailure
condition|)
block|{
name|removeVolumeFailureInfo
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getVolumeFailureInfos ()
name|VolumeFailureInfo
index|[]
name|getVolumeFailureInfos
parameter_list|()
block|{
name|Collection
argument_list|<
name|VolumeFailureInfo
argument_list|>
name|infos
init|=
name|volumeFailureInfos
operator|.
name|values
argument_list|()
decl_stmt|;
return|return
name|infos
operator|.
name|toArray
argument_list|(
operator|new
name|VolumeFailureInfo
index|[
name|infos
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|addVolumeFailureInfo (VolumeFailureInfo volumeFailureInfo)
name|void
name|addVolumeFailureInfo
parameter_list|(
name|VolumeFailureInfo
name|volumeFailureInfo
parameter_list|)
block|{
name|volumeFailureInfos
operator|.
name|put
argument_list|(
name|volumeFailureInfo
operator|.
name|getFailedStorageLocation
argument_list|()
argument_list|,
name|volumeFailureInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|addVolumeFailureInfo (FsVolumeImpl vol)
specifier|private
name|void
name|addVolumeFailureInfo
parameter_list|(
name|FsVolumeImpl
name|vol
parameter_list|)
block|{
name|addVolumeFailureInfo
argument_list|(
operator|new
name|VolumeFailureInfo
argument_list|(
operator|new
name|File
argument_list|(
name|vol
operator|.
name|getBasePath
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|Time
operator|.
name|now
argument_list|()
argument_list|,
name|vol
operator|.
name|getCapacity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|removeVolumeFailureInfo (File vol)
specifier|private
name|void
name|removeVolumeFailureInfo
parameter_list|(
name|File
name|vol
parameter_list|)
block|{
name|volumeFailureInfos
operator|.
name|remove
argument_list|(
name|vol
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addBlockPool (final String bpid, final Configuration conf)
name|void
name|addBlockPool
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|totalStartTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|IOException
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|blockPoolAddingThreads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|FsVolumeImpl
name|v
range|:
name|volumes
control|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|v
operator|.
name|obtainReference
argument_list|()
init|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Scanning block pool "
operator|+
name|bpid
operator|+
literal|" on volume "
operator|+
name|v
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|v
operator|.
name|addBlockPool
argument_list|(
name|bpid
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|long
name|timeTaken
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Time taken to scan block pool "
operator|+
name|bpid
operator|+
literal|" on "
operator|+
name|v
operator|+
literal|": "
operator|+
name|timeTaken
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
comment|// ignore.
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught exception while scanning "
operator|+
name|v
operator|+
literal|". Will throw later."
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|blockPoolAddingThreads
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|blockPoolAddingThreads
control|)
block|{
try|try
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
block|}
name|long
name|totalTimeTaken
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|totalStartTime
decl_stmt|;
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Total time to scan all replicas for block pool "
operator|+
name|bpid
operator|+
literal|": "
operator|+
name|totalTimeTaken
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
DECL|method|removeBlockPool (String bpid, Map<DatanodeStorage, BlockListAsLongs> blocksPerVolume)
name|void
name|removeBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
name|blocksPerVolume
parameter_list|)
block|{
for|for
control|(
name|FsVolumeImpl
name|v
range|:
name|volumes
control|)
block|{
name|v
operator|.
name|shutdownBlockPool
argument_list|(
name|bpid
argument_list|,
name|blocksPerVolume
operator|.
name|get
argument_list|(
name|v
operator|.
name|toDatanodeStorage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
block|{
for|for
control|(
name|FsVolumeImpl
name|volume
range|:
name|volumes
control|)
block|{
if|if
condition|(
name|volume
operator|!=
literal|null
condition|)
block|{
name|volume
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

