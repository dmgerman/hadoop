begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.volume
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|volume
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|istack
operator|.
name|Nullable
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
name|GetSpaceUsed
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
name|checker
operator|.
name|Checkable
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
name|checker
operator|.
name|VolumeCheckResult
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
name|ozone
operator|.
name|common
operator|.
name|InconsistentStorageStateException
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|DataNodeLayoutVersion
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|DatanodeVersionFile
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ChunkLayOutVersion
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|utils
operator|.
name|HddsVolumeUtil
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|yetus
operator|.
name|audience
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
name|yetus
operator|.
name|audience
operator|.
name|InterfaceStability
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * HddsVolume represents volume in a datanode. {@link VolumeSet} maintains a  * list of HddsVolumes, one for each volume in the Datanode.  * {@link VolumeInfo} in encompassed by this class.  *<p>  * The disk layout per volume is as follows:  *<p>../hdds/VERSION  *<p>{@literal ../hdds/<<scmUuid>>/current/<<containerDir>>/<<containerID  *>>/metadata}  *<p>{@literal ../hdds/<<scmUuid>>/current/<<containerDir>>/<<containerID  *>>/<<dataDir>>}  *<p>  * Each hdds volume has its own VERSION file. The hdds volume will have one  * scmUuid directory for each SCM it is a part of (currently only one SCM is  * supported).  *  * During DN startup, if the VERSION file exists, we verify that the  * clusterID in the version file matches the clusterID from SCM.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|SuppressWarnings
argument_list|(
literal|"finalclass"
argument_list|)
DECL|class|HddsVolume
specifier|public
class|class
name|HddsVolume
implements|implements
name|Checkable
argument_list|<
name|Boolean
argument_list|,
name|VolumeCheckResult
argument_list|>
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
name|HddsVolume
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HDDS_VOLUME_DIR
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_VOLUME_DIR
init|=
literal|"hdds"
decl_stmt|;
DECL|field|hddsRootDir
specifier|private
specifier|final
name|File
name|hddsRootDir
decl_stmt|;
DECL|field|volumeInfo
specifier|private
specifier|final
name|VolumeInfo
name|volumeInfo
decl_stmt|;
DECL|field|state
specifier|private
name|VolumeState
name|state
decl_stmt|;
DECL|field|volumeIOStats
specifier|private
specifier|final
name|VolumeIOStats
name|volumeIOStats
decl_stmt|;
comment|// VERSION file properties
DECL|field|storageID
specifier|private
name|String
name|storageID
decl_stmt|;
comment|// id of the file system
DECL|field|clusterID
specifier|private
name|String
name|clusterID
decl_stmt|;
comment|// id of the cluster
DECL|field|datanodeUuid
specifier|private
name|String
name|datanodeUuid
decl_stmt|;
comment|// id of the DataNode
DECL|field|cTime
specifier|private
name|long
name|cTime
decl_stmt|;
comment|// creation time of the file system state
DECL|field|layoutVersion
specifier|private
name|int
name|layoutVersion
decl_stmt|;
comment|// layout version of the storage data
comment|/**    * Run a check on the current volume to determine if it is healthy.    * @param unused context for the check, ignored.    * @return result of checking the volume.    * @throws Exception if an exception was encountered while running    *            the volume check.    */
annotation|@
name|Override
DECL|method|check (@ullable Boolean unused)
specifier|public
name|VolumeCheckResult
name|check
parameter_list|(
annotation|@
name|Nullable
name|Boolean
name|unused
parameter_list|)
throws|throws
name|Exception
block|{
name|DiskChecker
operator|.
name|checkDir
argument_list|(
name|hddsRootDir
argument_list|)
expr_stmt|;
return|return
name|VolumeCheckResult
operator|.
name|HEALTHY
return|;
block|}
comment|/**    * Builder for HddsVolume.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|volumeRootStr
specifier|private
specifier|final
name|String
name|volumeRootStr
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|storageType
specifier|private
name|StorageType
name|storageType
decl_stmt|;
DECL|field|configuredCapacity
specifier|private
name|long
name|configuredCapacity
decl_stmt|;
DECL|field|datanodeUuid
specifier|private
name|String
name|datanodeUuid
decl_stmt|;
DECL|field|clusterID
specifier|private
name|String
name|clusterID
decl_stmt|;
DECL|field|failedVolume
specifier|private
name|boolean
name|failedVolume
init|=
literal|false
decl_stmt|;
DECL|method|Builder (String rootDirStr)
specifier|public
name|Builder
parameter_list|(
name|String
name|rootDirStr
parameter_list|)
block|{
name|this
operator|.
name|volumeRootStr
operator|=
name|rootDirStr
expr_stmt|;
block|}
DECL|method|conf (Configuration config)
specifier|public
name|Builder
name|conf
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|storageType (StorageType st)
specifier|public
name|Builder
name|storageType
parameter_list|(
name|StorageType
name|st
parameter_list|)
block|{
name|this
operator|.
name|storageType
operator|=
name|st
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|configuredCapacity (long capacity)
specifier|public
name|Builder
name|configuredCapacity
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|configuredCapacity
operator|=
name|capacity
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|datanodeUuid (String datanodeUUID)
specifier|public
name|Builder
name|datanodeUuid
parameter_list|(
name|String
name|datanodeUUID
parameter_list|)
block|{
name|this
operator|.
name|datanodeUuid
operator|=
name|datanodeUUID
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|clusterID (String cid)
specifier|public
name|Builder
name|clusterID
parameter_list|(
name|String
name|cid
parameter_list|)
block|{
name|this
operator|.
name|clusterID
operator|=
name|cid
expr_stmt|;
return|return
name|this
return|;
block|}
comment|// This is added just to create failed volume objects, which will be used
comment|// to create failed HddsVolume objects in the case of any exceptions caused
comment|// during creating HddsVolume object.
DECL|method|failedVolume (boolean failed)
specifier|public
name|Builder
name|failedVolume
parameter_list|(
name|boolean
name|failed
parameter_list|)
block|{
name|this
operator|.
name|failedVolume
operator|=
name|failed
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|HddsVolume
name|build
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|HddsVolume
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|HddsVolume (Builder b)
specifier|private
name|HddsVolume
parameter_list|(
name|Builder
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|b
operator|.
name|failedVolume
condition|)
block|{
name|StorageLocation
name|location
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|b
operator|.
name|volumeRootStr
argument_list|)
decl_stmt|;
name|hddsRootDir
operator|=
operator|new
name|File
argument_list|(
name|location
operator|.
name|getUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|HDDS_VOLUME_DIR
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|VolumeState
operator|.
name|NOT_INITIALIZED
expr_stmt|;
name|this
operator|.
name|clusterID
operator|=
name|b
operator|.
name|clusterID
expr_stmt|;
name|this
operator|.
name|datanodeUuid
operator|=
name|b
operator|.
name|datanodeUuid
expr_stmt|;
name|this
operator|.
name|volumeIOStats
operator|=
operator|new
name|VolumeIOStats
argument_list|()
expr_stmt|;
name|VolumeInfo
operator|.
name|Builder
name|volumeBuilder
init|=
operator|new
name|VolumeInfo
operator|.
name|Builder
argument_list|(
name|b
operator|.
name|volumeRootStr
argument_list|,
name|b
operator|.
name|conf
argument_list|)
operator|.
name|storageType
argument_list|(
name|b
operator|.
name|storageType
argument_list|)
operator|.
name|configuredCapacity
argument_list|(
name|b
operator|.
name|configuredCapacity
argument_list|)
decl_stmt|;
name|this
operator|.
name|volumeInfo
operator|=
name|volumeBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating Volume: "
operator|+
name|this
operator|.
name|hddsRootDir
operator|+
literal|" of  storage type : "
operator|+
name|b
operator|.
name|storageType
operator|+
literal|" and capacity : "
operator|+
name|volumeInfo
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Builder is called with failedVolume set, so create a failed volume
comment|// HddsVolumeObject.
name|hddsRootDir
operator|=
operator|new
name|File
argument_list|(
name|b
operator|.
name|volumeRootStr
argument_list|)
expr_stmt|;
name|volumeIOStats
operator|=
literal|null
expr_stmt|;
name|volumeInfo
operator|=
literal|null
expr_stmt|;
name|storageID
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|state
operator|=
name|VolumeState
operator|.
name|FAILED
expr_stmt|;
block|}
block|}
DECL|method|getVolumeInfo ()
specifier|public
name|VolumeInfo
name|getVolumeInfo
parameter_list|()
block|{
return|return
name|volumeInfo
return|;
block|}
comment|/**    * Initializes the volume.    * Creates the Version file if not present,    * otherwise returns with IOException.    * @throws IOException    */
DECL|method|initialize ()
specifier|private
name|void
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
name|VolumeState
name|intialVolumeState
init|=
name|analyzeVolumeState
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|intialVolumeState
condition|)
block|{
case|case
name|NON_EXISTENT
case|:
comment|// Root directory does not exist. Create it.
if|if
condition|(
operator|!
name|hddsRootDir
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory "
operator|+
name|hddsRootDir
argument_list|)
throw|;
block|}
name|setState
argument_list|(
name|VolumeState
operator|.
name|NOT_FORMATTED
argument_list|)
expr_stmt|;
name|createVersionFile
argument_list|()
expr_stmt|;
break|break;
case|case
name|NOT_FORMATTED
case|:
comment|// Version File does not exist. Create it.
name|createVersionFile
argument_list|()
expr_stmt|;
break|break;
case|case
name|NOT_INITIALIZED
case|:
comment|// Version File exists. Verify its correctness and update property fields.
name|readVersionFile
argument_list|()
expr_stmt|;
name|setState
argument_list|(
name|VolumeState
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
break|break;
case|case
name|INCONSISTENT
case|:
comment|// Volume Root is in an inconsistent state. Skip loading this volume.
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Volume is in an "
operator|+
name|VolumeState
operator|.
name|INCONSISTENT
operator|+
literal|" state. Skipped loading volume: "
operator|+
name|hddsRootDir
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unrecognized initial state : "
operator|+
name|intialVolumeState
operator|+
literal|"of volume : "
operator|+
name|hddsRootDir
argument_list|)
throw|;
block|}
block|}
DECL|method|analyzeVolumeState ()
specifier|private
name|VolumeState
name|analyzeVolumeState
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hddsRootDir
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// Volume Root does not exist.
return|return
name|VolumeState
operator|.
name|NON_EXISTENT
return|;
block|}
if|if
condition|(
operator|!
name|hddsRootDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// Volume Root exists but is not a directory.
return|return
name|VolumeState
operator|.
name|INCONSISTENT
return|;
block|}
name|File
index|[]
name|files
init|=
name|hddsRootDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
operator|||
name|files
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// Volume Root exists and is empty.
return|return
name|VolumeState
operator|.
name|NOT_FORMATTED
return|;
block|}
if|if
condition|(
operator|!
name|getVersionFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// Volume Root is non empty but VERSION file does not exist.
return|return
name|VolumeState
operator|.
name|INCONSISTENT
return|;
block|}
comment|// Volume Root and VERSION file exist.
return|return
name|VolumeState
operator|.
name|NOT_INITIALIZED
return|;
block|}
DECL|method|format (String cid)
specifier|public
name|void
name|format
parameter_list|(
name|String
name|cid
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cid
argument_list|,
literal|"clusterID cannot be null while "
operator|+
literal|"formatting Volume"
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterID
operator|=
name|cid
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create Version File and write property fields into it.    * @throws IOException    */
DECL|method|createVersionFile ()
specifier|private
name|void
name|createVersionFile
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|storageID
operator|=
name|HddsVolumeUtil
operator|.
name|generateUuid
argument_list|()
expr_stmt|;
name|this
operator|.
name|cTime
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
name|this
operator|.
name|layoutVersion
operator|=
name|ChunkLayOutVersion
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|clusterID
operator|==
literal|null
operator|||
name|datanodeUuid
operator|==
literal|null
condition|)
block|{
comment|// HddsDatanodeService does not have the cluster information yet. Wait
comment|// for registration with SCM.
name|LOG
operator|.
name|debug
argument_list|(
literal|"ClusterID not available. Cannot format the volume {}"
argument_list|,
name|this
operator|.
name|hddsRootDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|setState
argument_list|(
name|VolumeState
operator|.
name|NOT_FORMATTED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Write the version file to disk.
name|writeVersionFile
argument_list|()
expr_stmt|;
name|setState
argument_list|(
name|VolumeState
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeVersionFile ()
specifier|private
name|void
name|writeVersionFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|this
operator|.
name|storageID
argument_list|,
literal|"StorageID cannot be null in Version File"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|this
operator|.
name|clusterID
argument_list|,
literal|"ClusterID cannot be null in Version File"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|this
operator|.
name|datanodeUuid
argument_list|,
literal|"DatanodeUUID cannot be null in Version File"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|this
operator|.
name|cTime
operator|>
literal|0
argument_list|,
literal|"Creation Time should be positive"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|this
operator|.
name|layoutVersion
operator|==
name|DataNodeLayoutVersion
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|"Version File should have the latest LayOutVersion"
argument_list|)
expr_stmt|;
name|File
name|versionFile
init|=
name|getVersionFile
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Writing Version file to disk, {}"
argument_list|,
name|versionFile
argument_list|)
expr_stmt|;
name|DatanodeVersionFile
name|dnVersionFile
init|=
operator|new
name|DatanodeVersionFile
argument_list|(
name|this
operator|.
name|storageID
argument_list|,
name|this
operator|.
name|clusterID
argument_list|,
name|this
operator|.
name|datanodeUuid
argument_list|,
name|this
operator|.
name|cTime
argument_list|,
name|this
operator|.
name|layoutVersion
argument_list|)
decl_stmt|;
name|dnVersionFile
operator|.
name|createVersionFile
argument_list|(
name|versionFile
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read Version File and update property fields.    * Get common storage fields.    * Should be overloaded if additional fields need to be read.    *    * @throws IOException on error    */
DECL|method|readVersionFile ()
specifier|private
name|void
name|readVersionFile
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|versionFile
init|=
name|getVersionFile
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
name|DatanodeVersionFile
operator|.
name|readFrom
argument_list|(
name|versionFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|props
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InconsistentStorageStateException
argument_list|(
literal|"Version file "
operator|+
name|versionFile
operator|+
literal|" is missing"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading Version file from disk, {}"
argument_list|,
name|versionFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageID
operator|=
name|HddsVolumeUtil
operator|.
name|getStorageID
argument_list|(
name|props
argument_list|,
name|versionFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterID
operator|=
name|HddsVolumeUtil
operator|.
name|getClusterID
argument_list|(
name|props
argument_list|,
name|versionFile
argument_list|,
name|this
operator|.
name|clusterID
argument_list|)
expr_stmt|;
name|this
operator|.
name|datanodeUuid
operator|=
name|HddsVolumeUtil
operator|.
name|getDatanodeUUID
argument_list|(
name|props
argument_list|,
name|versionFile
argument_list|,
name|this
operator|.
name|datanodeUuid
argument_list|)
expr_stmt|;
name|this
operator|.
name|cTime
operator|=
name|HddsVolumeUtil
operator|.
name|getCreationTime
argument_list|(
name|props
argument_list|,
name|versionFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|layoutVersion
operator|=
name|HddsVolumeUtil
operator|.
name|getLayOutVersion
argument_list|(
name|props
argument_list|,
name|versionFile
argument_list|)
expr_stmt|;
block|}
DECL|method|getVersionFile ()
specifier|private
name|File
name|getVersionFile
parameter_list|()
block|{
return|return
name|HddsVolumeUtil
operator|.
name|getVersionFile
argument_list|(
name|hddsRootDir
argument_list|)
return|;
block|}
DECL|method|getHddsRootDir ()
specifier|public
name|File
name|getHddsRootDir
parameter_list|()
block|{
return|return
name|hddsRootDir
return|;
block|}
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
if|if
condition|(
name|volumeInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|volumeInfo
operator|.
name|getStorageType
argument_list|()
return|;
block|}
return|return
name|StorageType
operator|.
name|DEFAULT
return|;
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
DECL|method|getClusterID ()
specifier|public
name|String
name|getClusterID
parameter_list|()
block|{
return|return
name|clusterID
return|;
block|}
DECL|method|getDatanodeUuid ()
specifier|public
name|String
name|getDatanodeUuid
parameter_list|()
block|{
return|return
name|datanodeUuid
return|;
block|}
DECL|method|getCTime ()
specifier|public
name|long
name|getCTime
parameter_list|()
block|{
return|return
name|cTime
return|;
block|}
DECL|method|getLayoutVersion ()
specifier|public
name|int
name|getLayoutVersion
parameter_list|()
block|{
return|return
name|layoutVersion
return|;
block|}
DECL|method|getStorageState ()
specifier|public
name|VolumeState
name|getStorageState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|volumeInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|volumeInfo
operator|.
name|getCapacity
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|volumeInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|volumeInfo
operator|.
name|getAvailable
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|setState (VolumeState state)
specifier|public
name|void
name|setState
parameter_list|(
name|VolumeState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|isFailed ()
specifier|public
name|boolean
name|isFailed
parameter_list|()
block|{
return|return
operator|(
name|state
operator|==
name|VolumeState
operator|.
name|FAILED
operator|)
return|;
block|}
DECL|method|getVolumeIOStats ()
specifier|public
name|VolumeIOStats
name|getVolumeIOStats
parameter_list|()
block|{
return|return
name|volumeIOStats
return|;
block|}
DECL|method|failVolume ()
specifier|public
name|void
name|failVolume
parameter_list|()
block|{
name|setState
argument_list|(
name|VolumeState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
if|if
condition|(
name|volumeInfo
operator|!=
literal|null
condition|)
block|{
name|volumeInfo
operator|.
name|shutdownUsageThread
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|this
operator|.
name|state
operator|=
name|VolumeState
operator|.
name|NON_EXISTENT
expr_stmt|;
if|if
condition|(
name|volumeInfo
operator|!=
literal|null
condition|)
block|{
name|volumeInfo
operator|.
name|shutdownUsageThread
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * VolumeState represents the different states a HddsVolume can be in.    * NORMAL          =&gt; Volume can be used for storage    * FAILED          =&gt; Volume has failed due and can no longer be used for    *                    storing containers.    * NON_EXISTENT    =&gt; Volume Root dir does not exist    * INCONSISTENT    =&gt; Volume Root dir is not empty but VERSION file is    *                    missing or Volume Root dir is not a directory    * NOT_FORMATTED   =&gt; Volume Root exists but not formatted(no VERSION file)    * NOT_INITIALIZED =&gt; VERSION file exists but has not been verified for    *                    correctness.    */
DECL|enum|VolumeState
specifier|public
enum|enum
name|VolumeState
block|{
DECL|enumConstant|NORMAL
name|NORMAL
block|,
DECL|enumConstant|FAILED
name|FAILED
block|,
DECL|enumConstant|NON_EXISTENT
name|NON_EXISTENT
block|,
DECL|enumConstant|INCONSISTENT
name|INCONSISTENT
block|,
DECL|enumConstant|NOT_FORMATTED
name|NOT_FORMATTED
block|,
DECL|enumConstant|NOT_INITIALIZED
name|NOT_INITIALIZED
block|}
comment|/**    * Only for testing. Do not use otherwise.    */
annotation|@
name|VisibleForTesting
DECL|method|setScmUsageForTesting (GetSpaceUsed scmUsageForTest)
specifier|public
name|void
name|setScmUsageForTesting
parameter_list|(
name|GetSpaceUsed
name|scmUsageForTest
parameter_list|)
block|{
if|if
condition|(
name|volumeInfo
operator|!=
literal|null
condition|)
block|{
name|volumeInfo
operator|.
name|setScmUsageForTesting
argument_list|(
name|scmUsageForTest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

