begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.extdataset
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
name|extdataset
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|*
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
name|FsDatasetSpi
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
name|datanode
operator|.
name|fsdataset
operator|.
name|ReplicaInputStreams
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
name|ReplicaOutputStreams
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
name|DataNodeMetricHelper
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
name|metrics2
operator|.
name|MetricsCollector
import|;
end_import

begin_class
DECL|class|ExternalDatasetImpl
specifier|public
class|class
name|ExternalDatasetImpl
implements|implements
name|FsDatasetSpi
argument_list|<
name|ExternalVolumeImpl
argument_list|>
block|{
DECL|field|storage
specifier|private
specifier|final
name|DatanodeStorage
name|storage
init|=
operator|new
name|DatanodeStorage
argument_list|(
name|DatanodeStorage
operator|.
name|generateUuid
argument_list|()
argument_list|,
name|DatanodeStorage
operator|.
name|State
operator|.
name|NORMAL
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getFsVolumeReferences ()
specifier|public
name|FsVolumeReferences
name|getFsVolumeReferences
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|addVolume (StorageLocation location, List<NamespaceInfo> nsInfos)
specifier|public
name|void
name|addVolume
parameter_list|(
name|StorageLocation
name|location
parameter_list|,
name|List
argument_list|<
name|NamespaceInfo
argument_list|>
name|nsInfos
parameter_list|)
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|removeVolumes (Set<File> volumes, boolean clearFailure)
specifier|public
name|void
name|removeVolumes
parameter_list|(
name|Set
argument_list|<
name|File
argument_list|>
name|volumes
parameter_list|,
name|boolean
name|clearFailure
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getStorage (String storageUuid)
specifier|public
name|DatanodeStorage
name|getStorage
parameter_list|(
name|String
name|storageUuid
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageReports (String bpid)
specifier|public
name|StorageReport
index|[]
name|getStorageReports
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|StorageReport
index|[]
name|result
init|=
operator|new
name|StorageReport
index|[
literal|1
index|]
decl_stmt|;
name|result
index|[
literal|0
index|]
operator|=
operator|new
name|StorageReport
argument_list|(
name|storage
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getVolume (ExtendedBlock b)
specifier|public
name|ExternalVolumeImpl
name|getVolume
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getFinalizedBlocks (String bpid)
specifier|public
name|List
argument_list|<
name|FinalizedReplica
argument_list|>
name|getFinalizedBlocks
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getFinalizedBlocksOnPersistentStorage (String bpid)
specifier|public
name|List
argument_list|<
name|FinalizedReplica
argument_list|>
name|getFinalizedBlocksOnPersistentStorage
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{   }
annotation|@
name|Override
DECL|method|getMetaDataInputStream (ExtendedBlock b)
specifier|public
name|LengthInputStream
name|getMetaDataInputStream
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LengthInputStream
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
operator|new
name|ExternalReplica
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
operator|new
name|Block
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
operator|new
name|ReplicaInputStreams
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createTemporary (StorageType t, ExtendedBlock b)
specifier|public
name|ReplicaHandler
name|createTemporary
parameter_list|(
name|StorageType
name|t
parameter_list|,
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ReplicaHandler
argument_list|(
operator|new
name|ExternalReplicaInPipeline
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createRbw (StorageType t, ExtendedBlock b, boolean tf)
specifier|public
name|ReplicaHandler
name|createRbw
parameter_list|(
name|StorageType
name|t
parameter_list|,
name|ExtendedBlock
name|b
parameter_list|,
name|boolean
name|tf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ReplicaHandler
argument_list|(
operator|new
name|ExternalReplicaInPipeline
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|recoverRbw (ExtendedBlock b, long newGS, long minBytesRcvd, long maxBytesRcvd)
specifier|public
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
block|{
return|return
operator|new
name|ReplicaHandler
argument_list|(
operator|new
name|ExternalReplicaInPipeline
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
operator|new
name|ExternalReplicaInPipeline
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|append (ExtendedBlock b, long newGS, long expectedBlockLen)
specifier|public
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
block|{
return|return
operator|new
name|ReplicaHandler
argument_list|(
operator|new
name|ExternalReplicaInPipeline
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|recoverAppend (ExtendedBlock b, long newGS, long expectedBlockLen)
specifier|public
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
block|{
return|return
operator|new
name|ReplicaHandler
argument_list|(
operator|new
name|ExternalReplicaInPipeline
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|recoverClose (ExtendedBlock b, long newGS, long expectedBlkLen)
specifier|public
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
name|expectedBlkLen
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{   }
annotation|@
name|Override
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
block|{   }
annotation|@
name|Override
DECL|method|getBlockReports (String bpid)
specifier|public
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
block|{
specifier|final
name|Map
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|DatanodeStorage
argument_list|,
name|BlockListAsLongs
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|storage
argument_list|,
name|BlockListAsLongs
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheReport (String bpid)
specifier|public
name|List
argument_list|<
name|Long
argument_list|>
name|getCacheReport
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|contains (ExtendedBlock block)
specifier|public
name|boolean
name|contains
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock (ExtendedBlock b, long minLength, ReplicaState state)
specifier|public
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
block|{    }
annotation|@
name|Override
DECL|method|isValidBlock (ExtendedBlock b)
specifier|public
name|boolean
name|isValidBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isValidRbw (ExtendedBlock b)
specifier|public
name|boolean
name|isValidRbw
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|invalidate (String bpid, Block[] invalidBlks)
specifier|public
name|void
name|invalidate
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
index|[]
name|invalidBlks
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|cache (String bpid, long[] blockIds)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|uncache (String bpid, long[] blockIds)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|isCached (String bpid, long blockId)
specifier|public
name|boolean
name|isCached
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|checkDataDir ()
specifier|public
name|Set
argument_list|<
name|File
argument_list|>
name|checkDataDir
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{   }
annotation|@
name|Override
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
block|{   }
annotation|@
name|Override
DECL|method|hasEnoughResource ()
specifier|public
name|boolean
name|hasEnoughResource
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getReplicaVisibleLength (ExtendedBlock block)
specifier|public
name|long
name|getReplicaVisibleLength
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|initReplicaRecovery (RecoveringBlock rBlock)
specifier|public
name|ReplicaRecoveryInfo
name|initReplicaRecovery
parameter_list|(
name|RecoveringBlock
name|rBlock
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ReplicaRecoveryInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|ReplicaState
operator|.
name|FINALIZED
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateReplicaUnderRecovery (ExtendedBlock oldBlock, long recoveryId, long newBlockId, long newLength)
specifier|public
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
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
block|{   }
annotation|@
name|Override
DECL|method|shutdownBlockPool (String bpid)
specifier|public
name|void
name|shutdownBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{   }
annotation|@
name|Override
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
block|{   }
annotation|@
name|Override
DECL|method|getBlockLocalPathInfo (ExtendedBlock b)
specifier|public
name|BlockLocalPathInfo
name|getBlockLocalPathInfo
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BlockLocalPathInfo
argument_list|(
literal|null
argument_list|,
literal|"file"
argument_list|,
literal|"metafile"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|enableTrash (String bpid)
specifier|public
name|void
name|enableTrash
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|clearTrash (String bpid)
specifier|public
name|void
name|clearTrash
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|trashEnabled (String bpid)
specifier|public
name|boolean
name|trashEnabled
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setRollingUpgradeMarker (String bpid)
specifier|public
name|void
name|setRollingUpgradeMarker
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|clearRollingUpgradeMarker (String bpid)
specifier|public
name|void
name|clearRollingUpgradeMarker
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|submitBackgroundSyncFileRangeRequest (ExtendedBlock block, FileDescriptor fd, long offset, long nbytes, int flags)
specifier|public
name|void
name|submitBackgroundSyncFileRangeRequest
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|FileDescriptor
name|fd
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|nbytes
parameter_list|,
name|int
name|flags
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|onCompleteLazyPersist (String bpId, long blockId, long creationTime, File[] savedFiles, ExternalVolumeImpl targetVolume)
specifier|public
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
name|ExternalVolumeImpl
name|targetVolume
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|onFailLazyPersist (String bpId, long blockId)
specifier|public
name|void
name|onFailLazyPersist
parameter_list|(
name|String
name|bpId
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|moveBlockAcrossStorage (ExtendedBlock block, StorageType targetStorageType)
specifier|public
name|ReplicaInfo
name|moveBlockAcrossStorage
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|StorageType
name|targetStorageType
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockPoolUsed (String bpid)
specifier|public
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
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getDfsUsed ()
specifier|public
name|long
name|getDfsUsed
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getRemaining ()
specifier|public
name|long
name|getRemaining
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageInfo ()
specifier|public
name|String
name|getStorageInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getNumFailedVolumes ()
specifier|public
name|int
name|getNumFailedVolumes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getFailedStorageLocations ()
specifier|public
name|String
index|[]
name|getFailedStorageLocations
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getLastVolumeFailureDate ()
specifier|public
name|long
name|getLastVolumeFailureDate
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getEstimatedCapacityLostTotal ()
specifier|public
name|long
name|getEstimatedCapacityLostTotal
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getVolumeFailureSummary ()
specifier|public
name|VolumeFailureSummary
name|getVolumeFailureSummary
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheUsed ()
specifier|public
name|long
name|getCacheUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheCapacity ()
specifier|public
name|long
name|getCacheCapacity
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumBlocksCached ()
specifier|public
name|long
name|getNumBlocksCached
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumBlocksFailedToCache ()
specifier|public
name|long
name|getNumBlocksFailedToCache
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumBlocksFailedToUncache ()
specifier|public
name|long
name|getNumBlocksFailedToUncache
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Get metrics from the metrics source    *    * @param collector to contain the resulting metrics snapshot    * @param all if true, return all metrics even if unchanged.    */
annotation|@
name|Override
DECL|method|getMetrics (MetricsCollector collector, boolean all)
specifier|public
name|void
name|getMetrics
parameter_list|(
name|MetricsCollector
name|collector
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
try|try
block|{
name|DataNodeMetricHelper
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
name|this
argument_list|,
literal|"ExternalDataset"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//ignore exceptions
block|}
block|}
annotation|@
name|Override
DECL|method|setPinning (ExtendedBlock block)
specifier|public
name|void
name|setPinning
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
DECL|method|getPinning (ExtendedBlock block)
specifier|public
name|boolean
name|getPinning
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isDeletingBlock (String bpid, long blockId)
specifier|public
name|boolean
name|isDeletingBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

