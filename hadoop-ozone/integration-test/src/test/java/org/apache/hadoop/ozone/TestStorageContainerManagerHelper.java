begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
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
name|collect
operator|.
name|Sets
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
name|primitives
operator|.
name|Longs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
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
name|DFSUtil
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
name|ObjectStoreHandler
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
name|DatanodeDetails
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
name|keyvalue
operator|.
name|helpers
operator|.
name|KeyUtils
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
name|keyvalue
operator|.
name|KeyValueContainerData
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
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyArgs
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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
name|web
operator|.
name|handlers
operator|.
name|BucketArgs
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
name|web
operator|.
name|handlers
operator|.
name|KeyArgs
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
name|web
operator|.
name|handlers
operator|.
name|UserArgs
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
name|web
operator|.
name|handlers
operator|.
name|VolumeArgs
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
name|web
operator|.
name|interfaces
operator|.
name|StorageHandler
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
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|utils
operator|.
name|MetadataKeyFilters
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
name|utils
operator|.
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
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
name|utils
operator|.
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
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
name|utils
operator|.
name|MetadataStore
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  * A helper class used by {@link TestStorageContainerManager} to generate  * some keys and helps to verify containers and blocks locations.  */
end_comment

begin_class
DECL|class|TestStorageContainerManagerHelper
specifier|public
class|class
name|TestStorageContainerManagerHelper
block|{
DECL|field|cluster
specifier|private
specifier|final
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|storageHandler
specifier|private
specifier|final
name|StorageHandler
name|storageHandler
decl_stmt|;
DECL|method|TestStorageContainerManagerHelper (MiniOzoneCluster cluster, Configuration conf)
specifier|public
name|TestStorageContainerManagerHelper
parameter_list|(
name|MiniOzoneCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|storageHandler
operator|=
operator|new
name|ObjectStoreHandler
argument_list|(
name|conf
argument_list|)
operator|.
name|getStorageHandler
argument_list|()
expr_stmt|;
block|}
DECL|method|createKeys (int numOfKeys, int keySize)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|createKeys
parameter_list|(
name|int
name|numOfKeys
parameter_list|,
name|int
name|keySize
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|keyLocationMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|String
name|volume
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucket
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|UserArgs
name|userArgs
init|=
operator|new
name|UserArgs
argument_list|(
literal|null
argument_list|,
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|VolumeArgs
name|createVolumeArgs
init|=
operator|new
name|VolumeArgs
argument_list|(
name|volume
argument_list|,
name|userArgs
argument_list|)
decl_stmt|;
name|createVolumeArgs
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|createVolumeArgs
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createVolume
argument_list|(
name|createVolumeArgs
argument_list|)
expr_stmt|;
name|BucketArgs
name|bucketArgs
init|=
operator|new
name|BucketArgs
argument_list|(
name|bucket
argument_list|,
name|createVolumeArgs
argument_list|)
decl_stmt|;
name|bucketArgs
operator|.
name|setAddAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setRemoveAcls
argument_list|(
operator|new
name|LinkedList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|bucketArgs
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|storageHandler
operator|.
name|createBucket
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
comment|// Write 20 keys in bucket.
name|Set
argument_list|<
name|String
argument_list|>
name|keyNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|KeyArgs
name|keyArgs
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numOfKeys
condition|;
name|i
operator|++
control|)
block|{
name|String
name|keyName
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|5
argument_list|)
operator|+
name|i
decl_stmt|;
name|keyNames
operator|.
name|add
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
name|keyArgs
operator|=
operator|new
name|KeyArgs
argument_list|(
name|keyName
argument_list|,
name|bucketArgs
argument_list|)
expr_stmt|;
name|keyArgs
operator|.
name|setSize
argument_list|(
name|keySize
argument_list|)
expr_stmt|;
comment|// Just for testing list keys call, so no need to write real data.
name|OutputStream
name|stream
init|=
name|storageHandler
operator|.
name|newKeyWriter
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|key
range|:
name|keyNames
control|)
block|{
name|OmKeyArgs
name|arg
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volume
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucket
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|key
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmKeyInfo
name|location
init|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|lookupKey
argument_list|(
name|arg
argument_list|)
decl_stmt|;
name|keyLocationMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
return|return
name|keyLocationMap
return|;
block|}
DECL|method|getPendingDeletionBlocks (Long containerID)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPendingDeletionBlocks
parameter_list|(
name|Long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pendingDeletionBlocks
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|MetadataStore
name|meta
init|=
name|getContainerMetadata
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|KeyPrefixFilter
name|filter
init|=
operator|new
name|KeyPrefixFilter
argument_list|()
operator|.
name|addFilter
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|kvs
init|=
name|meta
operator|.
name|getRangeKVs
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|kvs
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
block|{
name|String
name|key
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|pendingDeletionBlocks
operator|.
name|add
argument_list|(
name|key
operator|.
name|replace
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|pendingDeletionBlocks
return|;
block|}
DECL|method|getAllBlocks (Set<Long> containerIDs)
specifier|public
name|List
argument_list|<
name|Long
argument_list|>
name|getAllBlocks
parameter_list|(
name|Set
argument_list|<
name|Long
argument_list|>
name|containerIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|allBlocks
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Long
name|containerID
range|:
name|containerIDs
control|)
block|{
name|allBlocks
operator|.
name|addAll
argument_list|(
name|getAllBlocks
argument_list|(
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|allBlocks
return|;
block|}
DECL|method|getAllBlocks (Long containeID)
specifier|public
name|List
argument_list|<
name|Long
argument_list|>
name|getAllBlocks
parameter_list|(
name|Long
name|containeID
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|allBlocks
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|MetadataStore
name|meta
init|=
name|getContainerMetadata
argument_list|(
name|containeID
argument_list|)
decl_stmt|;
name|MetadataKeyFilter
name|filter
init|=
parameter_list|(
name|preKey
parameter_list|,
name|currentKey
parameter_list|,
name|nextKey
parameter_list|)
lambda|->
operator|!
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|currentKey
argument_list|)
operator|.
name|startsWith
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|kvs
init|=
name|meta
operator|.
name|getRangeKVs
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|MetadataKeyFilters
operator|.
name|getNormalKeyFilter
argument_list|()
argument_list|)
decl_stmt|;
name|kvs
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
block|{
name|allBlocks
operator|.
name|add
argument_list|(
name|Longs
operator|.
name|fromByteArray
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|allBlocks
return|;
block|}
DECL|method|getContainerMetadata (Long containerID)
specifier|private
name|MetadataStore
name|getContainerMetadata
parameter_list|(
name|Long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getClientProtocolServer
argument_list|()
operator|.
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|DatanodeDetails
name|leadDN
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
decl_stmt|;
name|OzoneContainer
name|containerServer
init|=
name|getContainerServerByDatanodeUuid
argument_list|(
name|leadDN
operator|.
name|getUuidString
argument_list|()
argument_list|)
decl_stmt|;
name|KeyValueContainerData
name|containerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|containerServer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
return|return
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|getContainerServerByDatanodeUuid (String dnUUID)
specifier|private
name|OzoneContainer
name|getContainerServerByDatanodeUuid
parameter_list|(
name|String
name|dnUUID
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|HddsDatanodeService
name|dn
range|:
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
control|)
block|{
if|if
condition|(
name|dn
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuidString
argument_list|()
operator|.
name|equals
argument_list|(
name|dnUUID
argument_list|)
condition|)
block|{
return|return
name|dn
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
return|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to get the ozone container "
operator|+
literal|"for given datanode ID "
operator|+
name|dnUUID
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

