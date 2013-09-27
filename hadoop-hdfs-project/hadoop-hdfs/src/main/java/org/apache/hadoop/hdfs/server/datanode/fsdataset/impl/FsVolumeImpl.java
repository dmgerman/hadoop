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
name|util
operator|.
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|DF
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
name|FileUtil
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
name|util
operator|.
name|DiskChecker
operator|.
name|DiskErrorException
import|;
end_import

begin_comment
comment|/**  * The underlying volume used to store replica.  *   * It uses the {@link FsDatasetImpl} object for synchronization.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FsVolumeImpl
class|class
name|FsVolumeImpl
implements|implements
name|FsVolumeSpi
block|{
DECL|field|dataset
specifier|private
specifier|final
name|FsDatasetImpl
name|dataset
decl_stmt|;
DECL|field|storageID
specifier|private
specifier|final
name|String
name|storageID
decl_stmt|;
DECL|field|storageType
specifier|private
specifier|final
name|StorageType
name|storageType
decl_stmt|;
DECL|field|bpSlices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
name|bpSlices
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|currentDir
specifier|private
specifier|final
name|File
name|currentDir
decl_stmt|;
comment|//<StorageDirectory>/current
DECL|field|usage
specifier|private
specifier|final
name|DF
name|usage
decl_stmt|;
DECL|field|reserved
specifier|private
specifier|final
name|long
name|reserved
decl_stmt|;
DECL|method|FsVolumeImpl (FsDatasetImpl dataset, String storageID, File currentDir, Configuration conf, StorageType storageType)
name|FsVolumeImpl
parameter_list|(
name|FsDatasetImpl
name|dataset
parameter_list|,
name|String
name|storageID
parameter_list|,
name|File
name|currentDir
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dataset
operator|=
name|dataset
expr_stmt|;
name|this
operator|.
name|storageID
operator|=
name|storageID
expr_stmt|;
name|this
operator|.
name|reserved
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DU_RESERVED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DU_RESERVED_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentDir
operator|=
name|currentDir
expr_stmt|;
name|File
name|parent
init|=
name|currentDir
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|this
operator|.
name|usage
operator|=
operator|new
name|DF
argument_list|(
name|parent
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
block|}
DECL|method|getCurrentDir ()
name|File
name|getCurrentDir
parameter_list|()
block|{
return|return
name|currentDir
return|;
block|}
DECL|method|getRbwDir (String bpid)
name|File
name|getRbwDir
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getRbwDir
argument_list|()
return|;
block|}
DECL|method|decDfsUsed (String bpid, long value)
name|void
name|decDfsUsed
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|value
parameter_list|)
block|{
synchronized|synchronized
init|(
name|dataset
init|)
block|{
name|BlockPoolSlice
name|bp
init|=
name|bpSlices
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bp
operator|!=
literal|null
condition|)
block|{
name|bp
operator|.
name|decDfsUsed
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|dataset
init|)
block|{
for|for
control|(
name|BlockPoolSlice
name|s
range|:
name|bpSlices
operator|.
name|values
argument_list|()
control|)
block|{
name|dfsUsed
operator|+=
name|s
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
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
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getDfsUsed
argument_list|()
return|;
block|}
comment|/**    * Calculate the capacity of the filesystem, after removing any    * reserved capacity.    * @return the unreserved number of bytes left in this filesystem. May be zero.    */
DECL|method|getCapacity ()
name|long
name|getCapacity
parameter_list|()
block|{
name|long
name|remaining
init|=
name|usage
operator|.
name|getCapacity
argument_list|()
operator|-
name|reserved
decl_stmt|;
return|return
name|remaining
operator|>
literal|0
condition|?
name|remaining
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|remaining
init|=
name|getCapacity
argument_list|()
operator|-
name|getDfsUsed
argument_list|()
decl_stmt|;
name|long
name|available
init|=
name|usage
operator|.
name|getAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
name|available
condition|)
block|{
name|remaining
operator|=
name|available
expr_stmt|;
block|}
return|return
operator|(
name|remaining
operator|>
literal|0
operator|)
condition|?
name|remaining
else|:
literal|0
return|;
block|}
DECL|method|getReserved ()
name|long
name|getReserved
parameter_list|()
block|{
return|return
name|reserved
return|;
block|}
DECL|method|getBlockPoolSlice (String bpid)
name|BlockPoolSlice
name|getBlockPoolSlice
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockPoolSlice
name|bp
init|=
name|bpSlices
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bp
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"block pool "
operator|+
name|bpid
operator|+
literal|" is not found"
argument_list|)
throw|;
block|}
return|return
name|bp
return|;
block|}
annotation|@
name|Override
DECL|method|getBasePath ()
specifier|public
name|String
name|getBasePath
parameter_list|()
block|{
return|return
name|currentDir
operator|.
name|getParent
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getDirectory
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getFinalizedDir
argument_list|()
return|;
block|}
comment|/**    * Make a deep copy of the list of currently active BPIDs    */
annotation|@
name|Override
DECL|method|getBlockPoolList ()
specifier|public
name|String
index|[]
name|getBlockPoolList
parameter_list|()
block|{
return|return
name|bpSlices
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|bpSlices
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * Temporary files. They get moved to the finalized block directory when    * the block is finalized.    */
DECL|method|createTmpFile (String bpid, Block b)
name|File
name|createTmpFile
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|createTmpFile
argument_list|(
name|b
argument_list|)
return|;
block|}
comment|/**    * RBW files. They get moved to the finalized block directory when    * the block is finalized.    */
DECL|method|createRbwFile (String bpid, Block b)
name|File
name|createRbwFile
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|createRbwFile
argument_list|(
name|b
argument_list|)
return|;
block|}
DECL|method|addBlock (String bpid, Block b, File f)
name|File
name|addBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|,
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|addBlock
argument_list|(
name|b
argument_list|,
name|f
argument_list|)
return|;
block|}
DECL|method|checkDirs ()
name|void
name|checkDirs
parameter_list|()
throws|throws
name|DiskErrorException
block|{
comment|// TODO:FEDERATION valid synchronization
for|for
control|(
name|BlockPoolSlice
name|s
range|:
name|bpSlices
operator|.
name|values
argument_list|()
control|)
block|{
name|s
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getVolumeMap (ReplicaMap volumeMap)
name|void
name|getVolumeMap
parameter_list|(
name|ReplicaMap
name|volumeMap
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BlockPoolSlice
name|s
range|:
name|bpSlices
operator|.
name|values
argument_list|()
control|)
block|{
name|s
operator|.
name|getVolumeMap
argument_list|(
name|volumeMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getVolumeMap (String bpid, ReplicaMap volumeMap)
name|void
name|getVolumeMap
parameter_list|(
name|String
name|bpid
parameter_list|,
name|ReplicaMap
name|volumeMap
parameter_list|)
throws|throws
name|IOException
block|{
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getVolumeMap
argument_list|(
name|volumeMap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add replicas under the given directory to the volume map    * @param volumeMap the replicas map    * @param dir an input directory    * @param isFinalized true if the directory has finalized replicas;    *                    false if the directory has rbw replicas    * @throws IOException     */
DECL|method|addToReplicasMap (String bpid, ReplicaMap volumeMap, File dir, boolean isFinalized)
name|void
name|addToReplicasMap
parameter_list|(
name|String
name|bpid
parameter_list|,
name|ReplicaMap
name|volumeMap
parameter_list|,
name|File
name|dir
parameter_list|,
name|boolean
name|isFinalized
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockPoolSlice
name|bp
init|=
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
comment|// TODO move this up
comment|// dfsUsage.incDfsUsed(b.getNumBytes()+metaFile.length());
name|bp
operator|.
name|addToReplicasMap
argument_list|(
name|volumeMap
argument_list|,
name|dir
argument_list|,
name|isFinalized
argument_list|)
expr_stmt|;
block|}
DECL|method|clearPath (String bpid, File f)
name|void
name|clearPath
parameter_list|(
name|String
name|bpid
parameter_list|,
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|clearPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
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
name|currentDir
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
block|{
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
argument_list|>
name|set
init|=
name|bpSlices
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
name|entry
range|:
name|set
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
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
block|{
name|File
name|bpdir
init|=
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|BlockPoolSlice
name|bp
init|=
operator|new
name|BlockPoolSlice
argument_list|(
name|bpid
argument_list|,
name|this
argument_list|,
name|bpdir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|bpSlices
operator|.
name|put
argument_list|(
name|bpid
argument_list|,
name|bp
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdownBlockPool (String bpid)
name|void
name|shutdownBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|BlockPoolSlice
name|bp
init|=
name|bpSlices
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bp
operator|!=
literal|null
condition|)
block|{
name|bp
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|bpSlices
operator|.
name|remove
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
block|}
DECL|method|isBPDirEmpty (String bpid)
name|boolean
name|isBPDirEmpty
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|volumeCurrentDir
init|=
name|this
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|File
name|bpDir
init|=
operator|new
name|File
argument_list|(
name|volumeCurrentDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|File
name|bpCurrentDir
init|=
operator|new
name|File
argument_list|(
name|bpDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
decl_stmt|;
name|File
name|finalizedDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_FINALIZED
argument_list|)
decl_stmt|;
name|File
name|rbwDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_RBW
argument_list|)
decl_stmt|;
if|if
condition|(
name|finalizedDir
operator|.
name|exists
argument_list|()
operator|&&
name|FileUtil
operator|.
name|list
argument_list|(
name|finalizedDir
argument_list|)
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|rbwDir
operator|.
name|exists
argument_list|()
operator|&&
name|FileUtil
operator|.
name|list
argument_list|(
name|rbwDir
argument_list|)
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|deleteBPDirectories (String bpid, boolean force)
name|void
name|deleteBPDirectories
parameter_list|(
name|String
name|bpid
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|volumeCurrentDir
init|=
name|this
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|File
name|bpDir
init|=
operator|new
name|File
argument_list|(
name|volumeCurrentDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bpDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// nothing to be deleted
return|return;
block|}
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|bpDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_TMP
argument_list|)
decl_stmt|;
name|File
name|bpCurrentDir
init|=
operator|new
name|File
argument_list|(
name|bpDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
decl_stmt|;
name|File
name|finalizedDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_FINALIZED
argument_list|)
decl_stmt|;
name|File
name|rbwDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_RBW
argument_list|)
decl_stmt|;
if|if
condition|(
name|force
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|bpDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|rbwDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|rbwDir
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|finalizedDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|finalizedDir
argument_list|)
throw|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|bpCurrentDir
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|bpCurrentDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|bpCurrentDir
argument_list|)
throw|;
block|}
for|for
control|(
name|File
name|f
range|:
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|bpDir
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|bpDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|bpDir
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getStorageID ()
specifier|public
name|String
name|getStorageID
parameter_list|()
block|{
return|return
name|storageID
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|storageType
return|;
block|}
block|}
end_class

end_unit

