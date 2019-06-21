begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
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
name|impl
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
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|StorageContainerException
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
name|interfaces
operator|.
name|Container
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
name|interfaces
operator|.
name|ContainerDeletionChoosingPolicy
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
name|volume
operator|.
name|HddsVolume
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
name|IOException
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
name|Set
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
name|concurrent
operator|.
name|ConcurrentNavigableMap
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
name|ConcurrentSkipListMap
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
name|ConcurrentSkipListSet
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
comment|/**  * Class that manages Containers created on the datanode.  */
end_comment

begin_class
DECL|class|ContainerSet
specifier|public
class|class
name|ContainerSet
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
name|ContainerSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerMap
specifier|private
specifier|final
name|ConcurrentSkipListMap
argument_list|<
name|Long
argument_list|,
name|Container
argument_list|>
name|containerMap
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|missingContainerSet
specifier|private
specifier|final
name|ConcurrentSkipListSet
argument_list|<
name|Long
argument_list|>
name|missingContainerSet
init|=
operator|new
name|ConcurrentSkipListSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Add Container to container map.    * @param container    * @return If container is added to containerMap returns true, otherwise    * false    */
DECL|method|addContainer (Container container)
specifier|public
name|boolean
name|addContainer
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|,
literal|"container cannot be null"
argument_list|)
expr_stmt|;
name|long
name|containerId
init|=
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerMap
operator|.
name|putIfAbsent
argument_list|(
name|containerId
argument_list|,
name|container
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Container with container Id {} is added to containerMap"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
comment|// wish we could have done this from ContainerData.setState
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|commitSpace
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Container already exists with container Id {}"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Container already exists with "
operator|+
literal|"container Id "
operator|+
name|containerId
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|CONTAINER_EXISTS
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the Container with specified containerId.    * @param containerId    * @return Container    */
DECL|method|getContainer (long containerId)
specifier|public
name|Container
name|getContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerId
operator|>=
literal|0
argument_list|,
literal|"Container Id cannot be negative."
argument_list|)
expr_stmt|;
return|return
name|containerMap
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
return|;
block|}
comment|/**    * Removes the Container matching with specified containerId.    * @param containerId    * @return If container is removed from containerMap returns true, otherwise    * false    */
DECL|method|removeContainer (long containerId)
specifier|public
name|boolean
name|removeContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerId
operator|>=
literal|0
argument_list|,
literal|"Container Id cannot be negative."
argument_list|)
expr_stmt|;
name|Container
name|removed
init|=
name|containerMap
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Container with containerId {} is not present in "
operator|+
literal|"containerMap"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Container with containerId {} is removed from containerMap"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Return number of containers in container map.    * @return container count    */
annotation|@
name|VisibleForTesting
DECL|method|containerCount ()
specifier|public
name|int
name|containerCount
parameter_list|()
block|{
return|return
name|containerMap
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Return an container Iterator over {@link ContainerSet#containerMap}.    * @return {@literal Iterator<Container>}    */
DECL|method|getContainerIterator ()
specifier|public
name|Iterator
argument_list|<
name|Container
argument_list|>
name|getContainerIterator
parameter_list|()
block|{
return|return
name|containerMap
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Return an iterator of containers associated with the specified volume.    *    * @param  volume the HDDS volume which should be used to filter containers    * @return {@literal Iterator<Container>}    */
DECL|method|getContainerIterator (HddsVolume volume)
specifier|public
name|Iterator
argument_list|<
name|Container
argument_list|>
name|getContainerIterator
parameter_list|(
name|HddsVolume
name|volume
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|volumeUuid
init|=
name|volume
operator|.
name|getStorageID
argument_list|()
decl_stmt|;
return|return
name|containerMap
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|x
lambda|->
name|volumeUuid
operator|.
name|equals
argument_list|(
name|x
operator|.
name|getContainerData
argument_list|()
operator|.
name|getVolume
argument_list|()
operator|.
name|getStorageID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Return an containerMap iterator over {@link ContainerSet#containerMap}.    * @return containerMap Iterator    */
DECL|method|getContainerMapIterator ()
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Container
argument_list|>
argument_list|>
name|getContainerMapIterator
parameter_list|()
block|{
name|containerMap
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|containerMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Return a copy of the containerMap.    * @return containerMap    */
annotation|@
name|VisibleForTesting
DECL|method|getContainerMapCopy ()
specifier|public
name|Map
argument_list|<
name|Long
argument_list|,
name|Container
argument_list|>
name|getContainerMapCopy
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|containerMap
argument_list|)
return|;
block|}
comment|/**    * A simple interface for container Iterations.    *<p>    * This call make no guarantees about consistency of the data between    * different list calls. It just returns the best known data at that point of    * time. It is possible that using this iteration you can miss certain    * container from the listing.    *    * @param startContainerId - Return containers with Id&gt;= startContainerId.    * @param count - how many to return    * @param data - Actual containerData    * @throws StorageContainerException    */
DECL|method|listContainer (long startContainerId, long count, List<ContainerData> data)
specifier|public
name|void
name|listContainer
parameter_list|(
name|long
name|startContainerId
parameter_list|,
name|long
name|count
parameter_list|,
name|List
argument_list|<
name|ContainerData
argument_list|>
name|data
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|data
argument_list|,
literal|"Internal assertion: data cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|startContainerId
operator|>=
literal|0
argument_list|,
literal|"Start container Id cannot be negative"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|count
operator|>
literal|0
argument_list|,
literal|"max number of containers returned "
operator|+
literal|"must be positive"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"listContainer returns containerData starting from {} of count "
operator|+
literal|"{}"
argument_list|,
name|startContainerId
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|ConcurrentNavigableMap
argument_list|<
name|Long
argument_list|,
name|Container
argument_list|>
name|map
decl_stmt|;
if|if
condition|(
name|startContainerId
operator|==
literal|0
condition|)
block|{
name|map
operator|=
name|containerMap
operator|.
name|tailMap
argument_list|(
name|containerMap
operator|.
name|firstKey
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|=
name|containerMap
operator|.
name|tailMap
argument_list|(
name|startContainerId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|int
name|currentCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Container
name|entry
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|currentCount
operator|<
name|count
condition|)
block|{
name|data
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getContainerData
argument_list|()
argument_list|)
expr_stmt|;
name|currentCount
operator|++
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
block|}
comment|/**    * Get container report.    *    * @return The container report.    * @throws IOException    */
DECL|method|getContainerReport ()
specifier|public
name|ContainerReportsProto
name|getContainerReport
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting container report iteration."
argument_list|)
expr_stmt|;
comment|// No need for locking since containerMap is a ConcurrentSkipListMap
comment|// And we can never get the exact state since close might happen
comment|// after we iterate a point.
name|List
argument_list|<
name|Container
argument_list|>
name|containers
init|=
name|containerMap
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerReportsProto
operator|.
name|Builder
name|crBuilder
init|=
name|ContainerReportsProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Container
name|container
range|:
name|containers
control|)
block|{
name|crBuilder
operator|.
name|addReports
argument_list|(
name|container
operator|.
name|getContainerReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|crBuilder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|chooseContainerForBlockDeletion (int count, ContainerDeletionChoosingPolicy deletionPolicy)
specifier|public
name|List
argument_list|<
name|ContainerData
argument_list|>
name|chooseContainerForBlockDeletion
parameter_list|(
name|int
name|count
parameter_list|,
name|ContainerDeletionChoosingPolicy
name|deletionPolicy
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|ContainerData
argument_list|>
name|containerDataMap
init|=
name|containerMap
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|deletionPolicy
operator|.
name|isValidContainerType
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getContainerType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|,
name|e
lambda|->
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getContainerData
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|deletionPolicy
operator|.
name|chooseContainerForBlockDeletion
argument_list|(
name|count
argument_list|,
name|containerDataMap
argument_list|)
return|;
block|}
DECL|method|getMissingContainerSet ()
specifier|public
name|Set
argument_list|<
name|Long
argument_list|>
name|getMissingContainerSet
parameter_list|()
block|{
return|return
name|missingContainerSet
return|;
block|}
comment|/**    * Builds the missing container set by taking a diff total no containers    * actually found and number of containers which actually got created.    * This will only be called during the initialization of Datanode Service    * when  it still not a part of any write Pipeline.    * @param createdContainerSet ContainerId set persisted in the Ratis snapshot    */
DECL|method|buildMissingContainerSet (Set<Long> createdContainerSet)
specifier|public
name|void
name|buildMissingContainerSet
parameter_list|(
name|Set
argument_list|<
name|Long
argument_list|>
name|createdContainerSet
parameter_list|)
block|{
name|missingContainerSet
operator|.
name|addAll
argument_list|(
name|createdContainerSet
argument_list|)
expr_stmt|;
name|missingContainerSet
operator|.
name|removeAll
argument_list|(
name|containerMap
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

