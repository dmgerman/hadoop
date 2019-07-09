begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|tasks
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
name|time
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
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
name|HashMap
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
name|Set
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
name|tuple
operator|.
name|ImmutablePair
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
name|tuple
operator|.
name|Pair
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
name|OMMetadataManager
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyLocationInfo
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
name|OmKeyLocationInfoGroup
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
name|recon
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerKeyPrefix
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
name|recon
operator|.
name|spi
operator|.
name|ContainerDBServiceProvider
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
name|db
operator|.
name|Table
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
name|db
operator|.
name|TableIterator
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

begin_comment
comment|/**  * Class to iterate over the OM DB and populate the Recon container DB with  * the container -> Key reverse mapping.  */
end_comment

begin_class
DECL|class|ContainerKeyMapperTask
specifier|public
class|class
name|ContainerKeyMapperTask
extends|extends
name|ReconDBUpdateTask
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
name|ContainerKeyMapperTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerDBServiceProvider
specifier|private
name|ContainerDBServiceProvider
name|containerDBServiceProvider
decl_stmt|;
DECL|field|tables
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|tables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ContainerKeyMapperTask (ContainerDBServiceProvider containerDBServiceProvider, OMMetadataManager omMetadataManager)
specifier|public
name|ContainerKeyMapperTask
parameter_list|(
name|ContainerDBServiceProvider
name|containerDBServiceProvider
parameter_list|,
name|OMMetadataManager
name|omMetadataManager
parameter_list|)
block|{
name|super
argument_list|(
literal|"ContainerKeyMapperTask"
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerDBServiceProvider
operator|=
name|containerDBServiceProvider
expr_stmt|;
try|try
block|{
name|tables
operator|.
name|add
argument_list|(
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioEx
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to listen on Key Table updates "
argument_list|,
name|ioEx
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read Key -> ContainerId data from OM snapshot DB and write reverse map    * (container, key) -> count to Recon Container DB.    */
annotation|@
name|Override
DECL|method|reprocess (OMMetadataManager omMetadataManager)
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|reprocess
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|)
block|{
name|long
name|omKeyCount
init|=
literal|0
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting a 'reprocess' run of ContainerKeyMapperTask."
argument_list|)
expr_stmt|;
name|Instant
name|start
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
comment|// initialize new container DB
name|containerDBServiceProvider
operator|.
name|initNewContainerDB
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|Table
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|omKeyInfoTable
init|=
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
decl_stmt|;
try|try
init|(
name|TableIterator
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|Table
operator|.
name|KeyValue
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
argument_list|>
name|keyIter
init|=
name|omKeyInfoTable
operator|.
name|iterator
argument_list|()
init|)
block|{
while|while
condition|(
name|keyIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Table
operator|.
name|KeyValue
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|kv
init|=
name|keyIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|OmKeyInfo
name|omKeyInfo
init|=
name|kv
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|writeOMKeyToContainerDB
argument_list|(
name|kv
operator|.
name|getKey
argument_list|()
argument_list|,
name|omKeyInfo
argument_list|)
expr_stmt|;
name|omKeyCount
operator|++
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Completed 'reprocess' of ContainerKeyMapperTask."
argument_list|)
expr_stmt|;
name|Instant
name|end
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|duration
init|=
name|Duration
operator|.
name|between
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"It took me "
operator|+
operator|(
name|double
operator|)
name|duration
operator|/
literal|1000.0
operator|+
literal|" seconds to "
operator|+
literal|"process "
operator|+
name|omKeyCount
operator|+
literal|" keys."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioEx
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to populate Container Key Prefix data in Recon DB. "
argument_list|,
name|ioEx
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskTables ()
specifier|protected
name|Collection
argument_list|<
name|String
argument_list|>
name|getTaskTables
parameter_list|()
block|{
return|return
name|tables
return|;
block|}
annotation|@
name|Override
DECL|method|process (OMUpdateEventBatch events)
name|Pair
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|process
parameter_list|(
name|OMUpdateEventBatch
name|events
parameter_list|)
block|{
name|Iterator
argument_list|<
name|OMDBUpdateEvent
argument_list|>
name|eventIterator
init|=
name|events
operator|.
name|getIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|eventIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|OMDBUpdateEvent
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|omdbUpdateEvent
init|=
name|eventIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|updatedKey
init|=
name|omdbUpdateEvent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|OmKeyInfo
name|updatedKeyValue
init|=
name|omdbUpdateEvent
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|omdbUpdateEvent
operator|.
name|getAction
argument_list|()
condition|)
block|{
case|case
name|PUT
case|:
name|writeOMKeyToContainerDB
argument_list|(
name|updatedKey
argument_list|,
name|updatedKeyValue
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|deleteOMKeyFromContainerDB
argument_list|(
name|updatedKey
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping DB update event : "
operator|+
name|omdbUpdateEvent
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception while updating key data : {} "
argument_list|,
name|updatedKey
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|ImmutablePair
argument_list|<>
argument_list|(
name|getTaskName
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Delete an OM Key from Container DB and update containerID -> no. of keys    * count.    *    * @param key key String.    * @throws IOException If Unable to write to container DB.    */
DECL|method|deleteOMKeyFromContainerDB (String key)
specifier|private
name|void
name|deleteOMKeyFromContainerDB
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|TableIterator
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|?
extends|extends
name|Table
operator|.
name|KeyValue
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|containerIterator
init|=
name|containerDBServiceProvider
operator|.
name|getContainerTableIterator
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ContainerKeyPrefix
argument_list|>
name|keysToBeDeleted
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|containerIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Table
operator|.
name|KeyValue
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|keyValue
init|=
name|containerIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|keyPrefix
init|=
name|keyValue
operator|.
name|getKey
argument_list|()
operator|.
name|getKeyPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyPrefix
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|keysToBeDeleted
operator|.
name|add
argument_list|(
name|keyValue
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|ContainerKeyPrefix
name|containerKeyPrefix
range|:
name|keysToBeDeleted
control|)
block|{
name|containerDBServiceProvider
operator|.
name|deleteContainerMapping
argument_list|(
name|containerKeyPrefix
argument_list|)
expr_stmt|;
comment|// decrement count and update containerKeyCount.
name|Long
name|containerID
init|=
name|containerKeyPrefix
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|long
name|keyCount
init|=
name|containerDBServiceProvider
operator|.
name|getKeyCountForContainer
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyCount
operator|>
literal|0
condition|)
block|{
name|containerDBServiceProvider
operator|.
name|storeContainerKeyCount
argument_list|(
name|containerID
argument_list|,
operator|--
name|keyCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Write an OM key to container DB and update containerID -> no. of keys    * count.    *    * @param key key String    * @param omKeyInfo omKeyInfo value    * @throws IOException if unable to write to recon DB.    */
DECL|method|writeOMKeyToContainerDB (String key, OmKeyInfo omKeyInfo)
specifier|private
name|void
name|writeOMKeyToContainerDB
parameter_list|(
name|String
name|key
parameter_list|,
name|OmKeyInfo
name|omKeyInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|containerCountToIncrement
init|=
literal|0
decl_stmt|;
for|for
control|(
name|OmKeyLocationInfoGroup
name|omKeyLocationInfoGroup
range|:
name|omKeyInfo
operator|.
name|getKeyLocationVersions
argument_list|()
control|)
block|{
name|long
name|keyVersion
init|=
name|omKeyLocationInfoGroup
operator|.
name|getVersion
argument_list|()
decl_stmt|;
for|for
control|(
name|OmKeyLocationInfo
name|omKeyLocationInfo
range|:
name|omKeyLocationInfoGroup
operator|.
name|getLocationList
argument_list|()
control|)
block|{
name|long
name|containerId
init|=
name|omKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|ContainerKeyPrefix
name|containerKeyPrefix
init|=
operator|new
name|ContainerKeyPrefix
argument_list|(
name|containerId
argument_list|,
name|key
argument_list|,
name|keyVersion
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerDBServiceProvider
operator|.
name|getCountForContainerKeyPrefix
argument_list|(
name|containerKeyPrefix
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// Save on writes. No need to save same container-key prefix
comment|// mapping again.
name|containerDBServiceProvider
operator|.
name|storeContainerKeyMapping
argument_list|(
name|containerKeyPrefix
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// check if container already exists and
comment|// increment the count of containers if it does not exist
if|if
condition|(
operator|!
name|containerDBServiceProvider
operator|.
name|doesContainerExists
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|containerCountToIncrement
operator|++
expr_stmt|;
block|}
comment|// update the count of keys for the given containerID
name|long
name|keyCount
init|=
name|containerDBServiceProvider
operator|.
name|getKeyCountForContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
comment|// increment the count and update containerKeyCount.
comment|// keyCount will be 0 if containerID is not found. So, there is no
comment|// need to initialize keyCount for the first time.
name|containerDBServiceProvider
operator|.
name|storeContainerKeyCount
argument_list|(
name|containerId
argument_list|,
operator|++
name|keyCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|containerCountToIncrement
operator|>
literal|0
condition|)
block|{
name|containerDBServiceProvider
operator|.
name|incrementContainerCountBy
argument_list|(
name|containerCountToIncrement
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

