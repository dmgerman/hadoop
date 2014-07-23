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
name|IOException
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
name|Collections
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
comment|/**    * Read access to this unmodifiable list is not synchronized.    * This list is replaced on modification holding "this" lock.    */
DECL|field|volumes
specifier|volatile
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|volumes
init|=
literal|null
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
DECL|field|numFailedVolumes
specifier|private
specifier|volatile
name|int
name|numFailedVolumes
decl_stmt|;
DECL|method|FsVolumeList (List<FsVolumeImpl> volumes, int failedVols, VolumeChoosingPolicy<FsVolumeImpl> blockChooser)
name|FsVolumeList
parameter_list|(
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|volumes
parameter_list|,
name|int
name|failedVols
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
name|volumes
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|volumes
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockChooser
operator|=
name|blockChooser
expr_stmt|;
name|this
operator|.
name|numFailedVolumes
operator|=
name|failedVols
expr_stmt|;
block|}
DECL|method|numberOfFailedVolumes ()
name|int
name|numberOfFailedVolumes
parameter_list|()
block|{
return|return
name|numFailedVolumes
return|;
block|}
comment|/**     * Get next volume. Synchronized to ensure {@link #curVolume} is updated    * by a single thread and next volume is chosen with no concurrent    * update to {@link #volumes}.    * @param blockSize free space needed on the volume    * @param storageType the desired {@link StorageType}     * @return next volume to store the block in.    */
DECL|method|getNextVolume (StorageType storageType, long blockSize)
specifier|synchronized
name|FsVolumeImpl
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
argument_list|<
name|FsVolumeImpl
argument_list|>
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
name|blockChooser
operator|.
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
name|dfsUsed
operator|+=
name|v
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
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
name|capacity
operator|+=
name|v
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
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
name|remaining
operator|+=
name|vol
operator|.
name|getAvailable
argument_list|()
expr_stmt|;
block|}
return|return
name|remaining
return|;
block|}
DECL|method|initializeReplicaMaps (ReplicaMap globalReplicaMap)
name|void
name|initializeReplicaMaps
parameter_list|(
name|ReplicaMap
name|globalReplicaMap
parameter_list|)
throws|throws
name|IOException
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
name|getVolumeMap
argument_list|(
name|globalReplicaMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAllVolumesMap (final String bpid, final ReplicaMap volumeMap)
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
DECL|method|getVolumeMap (String bpid, FsVolumeImpl volume, ReplicaMap volumeMap)
name|void
name|getVolumeMap
parameter_list|(
name|String
name|bpid
parameter_list|,
name|FsVolumeImpl
name|volume
parameter_list|,
name|ReplicaMap
name|volumeMap
parameter_list|)
throws|throws
name|IOException
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
name|volume
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
name|volume
operator|.
name|getVolumeMap
argument_list|(
name|bpid
argument_list|,
name|volumeMap
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
literal|"Time to add replicas to map for block pool "
operator|+
name|bpid
operator|+
literal|" on volume "
operator|+
name|volume
operator|+
literal|": "
operator|+
name|timeTaken
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link FsVolumeImpl#checkDirs()} on each volume, removing any    * volumes from the active list that result in a DiskErrorException.    *     * This method is synchronized to allow only one instance of checkDirs()     * call    * @return list of all the removed volumes.    */
DECL|method|checkDirs ()
specifier|synchronized
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|checkDirs
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|FsVolumeImpl
argument_list|>
name|removedVols
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
operator|new
name|ArrayList
argument_list|<
name|FsVolumeImpl
argument_list|>
argument_list|(
name|volumes
argument_list|)
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
name|removedVols
operator|==
literal|null
condition|)
block|{
name|removedVols
operator|=
operator|new
name|ArrayList
argument_list|<
name|FsVolumeImpl
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|removedVols
operator|.
name|add
argument_list|(
name|fsv
argument_list|)
expr_stmt|;
name|fsv
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// Remove the volume
name|numFailedVolumes
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|removedVols
operator|!=
literal|null
operator|&&
name|removedVols
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Replace volume list
name|volumes
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|volumeList
argument_list|)
expr_stmt|;
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Completed checkDirs. Removed "
operator|+
name|removedVols
operator|.
name|size
argument_list|()
operator|+
literal|" volumes. Current volumes: "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|removedVols
return|;
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
DECL|method|removeBlockPool (String bpid)
name|void
name|removeBlockPool
parameter_list|(
name|String
name|bpid
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

