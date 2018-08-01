begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|helpers
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
name|base
operator|.
name|Preconditions
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|KeyInfo
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Args for key block. The block instance for the key requested in putKey.  * This is returned from OM to client, and client use class to talk to  * datanode. Also, this is the metadata written to om.db on server side.  */
end_comment

begin_class
DECL|class|OmKeyInfo
specifier|public
specifier|final
class|class
name|OmKeyInfo
block|{
DECL|field|volumeName
specifier|private
specifier|final
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
specifier|final
name|String
name|bucketName
decl_stmt|;
comment|// name of key client specified
DECL|field|keyName
specifier|private
name|String
name|keyName
decl_stmt|;
DECL|field|dataSize
specifier|private
name|long
name|dataSize
decl_stmt|;
DECL|field|keyLocationVersions
specifier|private
name|List
argument_list|<
name|OmKeyLocationInfoGroup
argument_list|>
name|keyLocationVersions
decl_stmt|;
DECL|field|creationTime
specifier|private
specifier|final
name|long
name|creationTime
decl_stmt|;
DECL|field|modificationTime
specifier|private
name|long
name|modificationTime
decl_stmt|;
DECL|field|type
specifier|private
name|HddsProtos
operator|.
name|ReplicationType
name|type
decl_stmt|;
DECL|field|factor
specifier|private
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|method|OmKeyInfo (String volumeName, String bucketName, String keyName, List<OmKeyLocationInfoGroup> versions, long dataSize, long creationTime, long modificationTime, HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor)
specifier|private
name|OmKeyInfo
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|List
argument_list|<
name|OmKeyLocationInfoGroup
argument_list|>
name|versions
parameter_list|,
name|long
name|dataSize
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
name|this
operator|.
name|dataSize
operator|=
name|dataSize
expr_stmt|;
comment|// it is important that the versions are ordered from old to new.
comment|// Do this sanity check when versions got loaded on creating OmKeyInfo.
comment|// TODO : this is not necessary, here only because versioning is still a
comment|// work in-progress, remove this following check when versioning is
comment|// complete and prove correctly functioning
name|long
name|currentVersion
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|OmKeyLocationInfoGroup
name|version
range|:
name|versions
control|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|currentVersion
operator|+
literal|1
operator|==
name|version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|currentVersion
operator|=
name|version
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|keyLocationVersions
operator|=
name|versions
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getVolumeName ()
specifier|public
name|String
name|getVolumeName
parameter_list|()
block|{
return|return
name|volumeName
return|;
block|}
DECL|method|getBucketName ()
specifier|public
name|String
name|getBucketName
parameter_list|()
block|{
return|return
name|bucketName
return|;
block|}
DECL|method|getType ()
specifier|public
name|HddsProtos
operator|.
name|ReplicationType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getFactor ()
specifier|public
name|HddsProtos
operator|.
name|ReplicationFactor
name|getFactor
parameter_list|()
block|{
return|return
name|factor
return|;
block|}
DECL|method|getKeyName ()
specifier|public
name|String
name|getKeyName
parameter_list|()
block|{
return|return
name|keyName
return|;
block|}
DECL|method|setKeyName (String keyName)
specifier|public
name|void
name|setKeyName
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
block|}
DECL|method|getDataSize ()
specifier|public
name|long
name|getDataSize
parameter_list|()
block|{
return|return
name|dataSize
return|;
block|}
DECL|method|setDataSize (long size)
specifier|public
name|void
name|setDataSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|dataSize
operator|=
name|size
expr_stmt|;
block|}
DECL|method|getLatestVersionLocations ()
specifier|public
specifier|synchronized
name|OmKeyLocationInfoGroup
name|getLatestVersionLocations
parameter_list|()
block|{
return|return
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|keyLocationVersions
operator|.
name|get
argument_list|(
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|getKeyLocationVersions ()
specifier|public
name|List
argument_list|<
name|OmKeyLocationInfoGroup
argument_list|>
name|getKeyLocationVersions
parameter_list|()
block|{
return|return
name|keyLocationVersions
return|;
block|}
DECL|method|updateModifcationTime ()
specifier|public
name|void
name|updateModifcationTime
parameter_list|()
block|{
name|this
operator|.
name|modificationTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
comment|/**    * updates the length of the each block in the list given.    * This will be called when the key is being committed to OzoneManager.    *    * @param locationInfoList list of locationInfo    * @throws IOException    */
DECL|method|updateLocationInfoList (List<OmKeyLocationInfo> locationInfoList)
specifier|public
name|void
name|updateLocationInfoList
parameter_list|(
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoList
parameter_list|)
block|{
name|OmKeyLocationInfoGroup
name|keyLocationInfoGroup
init|=
name|getLatestVersionLocations
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|currentList
init|=
name|keyLocationInfoGroup
operator|.
name|getLocationList
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|keyLocationInfoGroup
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|locationInfoList
operator|.
name|size
argument_list|()
operator|<=
name|currentList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|OmKeyLocationInfo
name|current
range|:
name|currentList
control|)
block|{
comment|// For Versioning, while committing the key for the newer version,
comment|// we just need to update the lengths for new blocks. Need to iterate over
comment|// and find the new blocks added in the latest version.
for|for
control|(
name|OmKeyLocationInfo
name|info
range|:
name|locationInfoList
control|)
block|{
if|if
condition|(
name|info
operator|.
name|getBlockID
argument_list|()
operator|.
name|equals
argument_list|(
name|current
operator|.
name|getBlockID
argument_list|()
argument_list|)
condition|)
block|{
name|current
operator|.
name|setLength
argument_list|(
name|info
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/**    * Append a set of blocks to the latest version. Note that these blocks are    * part of the latest version, not a new version.    *    * @param newLocationList the list of new blocks to be added.    * @throws IOException    */
DECL|method|appendNewBlocks ( List<OmKeyLocationInfo> newLocationList)
specifier|public
specifier|synchronized
name|void
name|appendNewBlocks
parameter_list|(
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|newLocationList
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Appending new block, but no version exist"
argument_list|)
throw|;
block|}
name|OmKeyLocationInfoGroup
name|currentLatestVersion
init|=
name|keyLocationVersions
operator|.
name|get
argument_list|(
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|currentLatestVersion
operator|.
name|appendNewBlocks
argument_list|(
name|newLocationList
argument_list|)
expr_stmt|;
name|setModificationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a new set of blocks. The new blocks will be added as appending a new    * version to the all version list.    *    * @param newLocationList the list of new blocks to be added.    * @throws IOException    */
DECL|method|addNewVersion ( List<OmKeyLocationInfo> newLocationList)
specifier|public
specifier|synchronized
name|long
name|addNewVersion
parameter_list|(
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|newLocationList
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|latestVersionNum
decl_stmt|;
if|if
condition|(
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// no version exist, these blocks are the very first version.
name|keyLocationVersions
operator|.
name|add
argument_list|(
operator|new
name|OmKeyLocationInfoGroup
argument_list|(
literal|0
argument_list|,
name|newLocationList
argument_list|)
argument_list|)
expr_stmt|;
name|latestVersionNum
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// it is important that the new version are always at the tail of the list
name|OmKeyLocationInfoGroup
name|currentLatestVersion
init|=
name|keyLocationVersions
operator|.
name|get
argument_list|(
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// the new version is created based on the current latest version
name|OmKeyLocationInfoGroup
name|newVersion
init|=
name|currentLatestVersion
operator|.
name|generateNextVersion
argument_list|(
name|newLocationList
argument_list|)
decl_stmt|;
name|keyLocationVersions
operator|.
name|add
argument_list|(
name|newVersion
argument_list|)
expr_stmt|;
name|latestVersionNum
operator|=
name|newVersion
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
name|setModificationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|latestVersionNum
return|;
block|}
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|modificationTime
return|;
block|}
DECL|method|setModificationTime (long modificationTime)
specifier|public
name|void
name|setModificationTime
parameter_list|(
name|long
name|modificationTime
parameter_list|)
block|{
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
block|}
comment|/**    * Builder of OmKeyInfo.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
DECL|field|keyName
specifier|private
name|String
name|keyName
decl_stmt|;
DECL|field|dataSize
specifier|private
name|long
name|dataSize
decl_stmt|;
DECL|field|omKeyLocationInfoGroups
specifier|private
name|List
argument_list|<
name|OmKeyLocationInfoGroup
argument_list|>
name|omKeyLocationInfoGroups
decl_stmt|;
DECL|field|creationTime
specifier|private
name|long
name|creationTime
decl_stmt|;
DECL|field|modificationTime
specifier|private
name|long
name|modificationTime
decl_stmt|;
DECL|field|type
specifier|private
name|HddsProtos
operator|.
name|ReplicationType
name|type
decl_stmt|;
DECL|field|factor
specifier|private
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|method|setVolumeName (String volume)
specifier|public
name|Builder
name|setVolumeName
parameter_list|(
name|String
name|volume
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volume
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBucketName (String bucket)
specifier|public
name|Builder
name|setBucketName
parameter_list|(
name|String
name|bucket
parameter_list|)
block|{
name|this
operator|.
name|bucketName
operator|=
name|bucket
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setKeyName (String key)
specifier|public
name|Builder
name|setKeyName
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|key
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOmKeyLocationInfos ( List<OmKeyLocationInfoGroup> omKeyLocationInfoList)
specifier|public
name|Builder
name|setOmKeyLocationInfos
parameter_list|(
name|List
argument_list|<
name|OmKeyLocationInfoGroup
argument_list|>
name|omKeyLocationInfoList
parameter_list|)
block|{
name|this
operator|.
name|omKeyLocationInfoGroups
operator|=
name|omKeyLocationInfoList
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDataSize (long size)
specifier|public
name|Builder
name|setDataSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|dataSize
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCreationTime (long crTime)
specifier|public
name|Builder
name|setCreationTime
parameter_list|(
name|long
name|crTime
parameter_list|)
block|{
name|this
operator|.
name|creationTime
operator|=
name|crTime
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setModificationTime (long mTime)
specifier|public
name|Builder
name|setModificationTime
parameter_list|(
name|long
name|mTime
parameter_list|)
block|{
name|this
operator|.
name|modificationTime
operator|=
name|mTime
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setReplicationFactor (HddsProtos.ReplicationFactor factor)
specifier|public
name|Builder
name|setReplicationFactor
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|)
block|{
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setReplicationType (HddsProtos.ReplicationType type)
specifier|public
name|Builder
name|setReplicationType
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|OmKeyInfo
name|build
parameter_list|()
block|{
return|return
operator|new
name|OmKeyInfo
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|omKeyLocationInfoGroups
argument_list|,
name|dataSize
argument_list|,
name|creationTime
argument_list|,
name|modificationTime
argument_list|,
name|type
argument_list|,
name|factor
argument_list|)
return|;
block|}
block|}
DECL|method|getProtobuf ()
specifier|public
name|KeyInfo
name|getProtobuf
parameter_list|()
block|{
name|long
name|latestVersion
init|=
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|keyLocationVersions
operator|.
name|get
argument_list|(
name|keyLocationVersions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getVersion
argument_list|()
decl_stmt|;
return|return
name|KeyInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setDataSize
argument_list|(
name|dataSize
argument_list|)
operator|.
name|setFactor
argument_list|(
name|factor
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|addAllKeyLocationList
argument_list|(
name|keyLocationVersions
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OmKeyLocationInfoGroup
operator|::
name|getProtobuf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setLatestVersion
argument_list|(
name|latestVersion
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
operator|.
name|setModificationTime
argument_list|(
name|modificationTime
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getFromProtobuf (KeyInfo keyInfo)
specifier|public
specifier|static
name|OmKeyInfo
name|getFromProtobuf
parameter_list|(
name|KeyInfo
name|keyInfo
parameter_list|)
block|{
return|return
operator|new
name|OmKeyInfo
argument_list|(
name|keyInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|keyInfo
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|keyInfo
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|keyInfo
operator|.
name|getKeyLocationListList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OmKeyLocationInfoGroup
operator|::
name|getFromProtobuf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|,
name|keyInfo
operator|.
name|getDataSize
argument_list|()
argument_list|,
name|keyInfo
operator|.
name|getCreationTime
argument_list|()
argument_list|,
name|keyInfo
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|keyInfo
operator|.
name|getType
argument_list|()
argument_list|,
name|keyInfo
operator|.
name|getFactor
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

