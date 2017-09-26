begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.meta
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|meta
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
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
name|cblock
operator|.
name|protocol
operator|.
name|proto
operator|.
name|CBlockClientServerProtocolProtos
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|Arrays
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

begin_comment
comment|/**  * The internal representation maintained by CBlock server as the info for  * a volume. Contains the list of containers belonging to this volume.  *  * Many methods of this class is made such that the volume information (  * including container list) can be easily transformed into a Json string  * that can be stored/parsed from a persistent store for cblock server  * persistence.  *  * This class is still work-in-progress.  */
end_comment

begin_class
DECL|class|VolumeDescriptor
specifier|public
class|class
name|VolumeDescriptor
block|{
comment|// The main data structure is the container location map
comment|// other thing are mainly just information
comment|// since only one operation at a time is allowed, no
comment|// need to consider concurrency control here
comment|// key is container id
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
name|VolumeDescriptor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerMap
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|ContainerDescriptor
argument_list|>
name|containerMap
decl_stmt|;
DECL|field|userName
specifier|private
name|String
name|userName
decl_stmt|;
DECL|field|blockSize
specifier|private
name|int
name|blockSize
decl_stmt|;
DECL|field|volumeSize
specifier|private
name|long
name|volumeSize
decl_stmt|;
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
comment|// this is essentially the ordered keys of containerMap
comment|// which is kind of redundant information. But since we
comment|// are likely to access it frequently based on ordering.
comment|// keeping this copy to avoid having to sort the key every
comment|// time
DECL|field|containerIdOrdered
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|containerIdOrdered
decl_stmt|;
comment|/**    * This is not being called explicitly, but this is necessary as    * it will be called by the parse method implicitly when    * reconstructing the object from json string. The get*() methods    * and set*() methods are for the same purpose also.    */
DECL|method|VolumeDescriptor ()
specifier|public
name|VolumeDescriptor
parameter_list|()
block|{
name|containerMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|containerIdOrdered
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|VolumeDescriptor (String userName, String volumeName, long volumeSize, int blockSize)
specifier|public
name|VolumeDescriptor
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|long
name|volumeSize
parameter_list|,
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|containerMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|this
operator|.
name|volumeSize
operator|=
name|volumeSize
expr_stmt|;
name|this
operator|.
name|containerIdOrdered
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
DECL|method|setUserName (String userName)
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
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
DECL|method|setVolumeName (String volumeName)
specifier|public
name|void
name|setVolumeName
parameter_list|(
name|String
name|volumeName
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
block|}
DECL|method|getVolumeSize ()
specifier|public
name|long
name|getVolumeSize
parameter_list|()
block|{
return|return
name|volumeSize
return|;
block|}
DECL|method|setVolumeSize (long volumeSize)
specifier|public
name|void
name|setVolumeSize
parameter_list|(
name|long
name|volumeSize
parameter_list|)
block|{
name|this
operator|.
name|volumeSize
operator|=
name|volumeSize
expr_stmt|;
block|}
DECL|method|getBlockSize ()
specifier|public
name|int
name|getBlockSize
parameter_list|()
block|{
return|return
name|blockSize
return|;
block|}
DECL|method|setBlockSize (int blockSize)
specifier|public
name|void
name|setBlockSize
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
block|}
DECL|method|setContainerIDs (ArrayList<String> containerIDs)
specifier|public
name|void
name|setContainerIDs
parameter_list|(
name|ArrayList
argument_list|<
name|String
argument_list|>
name|containerIDs
parameter_list|)
block|{
name|containerIdOrdered
operator|.
name|addAll
argument_list|(
name|containerIDs
argument_list|)
expr_stmt|;
block|}
DECL|method|addContainer (ContainerDescriptor containerDescriptor)
specifier|public
name|void
name|addContainer
parameter_list|(
name|ContainerDescriptor
name|containerDescriptor
parameter_list|)
block|{
name|containerMap
operator|.
name|put
argument_list|(
name|containerDescriptor
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|containerDescriptor
argument_list|)
expr_stmt|;
block|}
DECL|method|getPipelines ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|()
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Pipeline
argument_list|>
name|pipelines
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ContainerDescriptor
argument_list|>
name|entry
range|:
name|containerMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|pipelines
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|pipelines
return|;
block|}
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|VolumeInfo
name|info
init|=
name|getInfo
argument_list|()
decl_stmt|;
return|return
name|info
operator|.
name|getUsage
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|getInfo ()
specifier|public
name|VolumeInfo
name|getInfo
parameter_list|()
block|{
comment|// TODO : need to actually go through all containers of this volume and
comment|// ask for their utilization.
name|long
name|utilization
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ContainerDescriptor
argument_list|>
name|entry
range|:
name|containerMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|utilization
operator|+=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getUtilization
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|VolumeInfo
argument_list|(
name|this
operator|.
name|userName
argument_list|,
name|this
operator|.
name|volumeName
argument_list|,
name|this
operator|.
name|volumeSize
argument_list|,
name|this
operator|.
name|blockSize
argument_list|,
name|utilization
operator|*
name|blockSize
argument_list|)
return|;
block|}
DECL|method|getContainerIDs ()
specifier|public
name|String
index|[]
name|getContainerIDs
parameter_list|()
block|{
comment|//ArrayList<Long> ids = new ArrayList(containerMap.keySet());
comment|//return ids.toArray(new Long[ids.size()]);
return|return
name|containerIdOrdered
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|containerIdOrdered
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|getContainerIDsList ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getContainerIDsList
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|containerIdOrdered
argument_list|)
return|;
block|}
DECL|method|getContainerPipelines ()
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getContainerPipelines
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Pipeline
argument_list|>
name|tmp
init|=
name|getPipelines
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelineList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|containerIDString
range|:
name|containerIdOrdered
control|)
block|{
name|pipelineList
operator|.
name|add
argument_list|(
name|tmp
operator|.
name|get
argument_list|(
name|containerIDString
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pipelineList
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
name|String
name|string
init|=
literal|""
decl_stmt|;
name|string
operator|+=
literal|"Username:"
operator|+
name|userName
operator|+
literal|"\n"
expr_stmt|;
name|string
operator|+=
literal|"VolumeName:"
operator|+
name|volumeName
operator|+
literal|"\n"
expr_stmt|;
name|string
operator|+=
literal|"VolumeSize:"
operator|+
name|volumeSize
operator|+
literal|"\n"
expr_stmt|;
name|string
operator|+=
literal|"blockSize:"
operator|+
name|blockSize
operator|+
literal|"\n"
expr_stmt|;
name|string
operator|+=
literal|"containerIds:"
operator|+
name|containerIdOrdered
operator|+
literal|"\n"
expr_stmt|;
name|string
operator|+=
literal|"containerIdsWithObject:"
operator|+
name|containerMap
operator|.
name|keySet
argument_list|()
expr_stmt|;
return|return
name|string
return|;
block|}
specifier|public
name|CBlockClientServerProtocolProtos
operator|.
name|MountVolumeResponseProto
DECL|method|toProtobuf ()
name|toProtobuf
parameter_list|()
block|{
name|CBlockClientServerProtocolProtos
operator|.
name|MountVolumeResponseProto
operator|.
name|Builder
name|volume
init|=
name|CBlockClientServerProtocolProtos
operator|.
name|MountVolumeResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|volume
operator|.
name|setIsValid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setVolumeSize
argument_list|(
name|volumeSize
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setBlockSize
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|containerIDString
range|:
name|containerIdOrdered
control|)
block|{
name|ContainerDescriptor
name|containerDescriptor
init|=
name|containerMap
operator|.
name|get
argument_list|(
name|containerIDString
argument_list|)
decl_stmt|;
name|volume
operator|.
name|addAllContainerIDs
argument_list|(
name|containerDescriptor
operator|.
name|toProtobuf
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|volume
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|fromProtobuf (byte[] data)
specifier|public
specifier|static
name|VolumeDescriptor
name|fromProtobuf
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|InvalidProtocolBufferException
block|{
name|CBlockClientServerProtocolProtos
operator|.
name|MountVolumeResponseProto
name|volume
init|=
name|CBlockClientServerProtocolProtos
operator|.
name|MountVolumeResponseProto
operator|.
name|parseFrom
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
name|volume
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|volumeName
init|=
name|volume
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|long
name|volumeSize
init|=
name|volume
operator|.
name|getVolumeSize
argument_list|()
decl_stmt|;
name|int
name|blockSize
init|=
name|volume
operator|.
name|getBlockSize
argument_list|()
decl_stmt|;
name|VolumeDescriptor
name|volumeDescriptor
init|=
operator|new
name|VolumeDescriptor
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|,
name|volumeSize
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CBlockClientServerProtocolProtos
operator|.
name|ContainerIDProto
argument_list|>
name|containers
init|=
name|volume
operator|.
name|getAllContainerIDsList
argument_list|()
decl_stmt|;
name|String
index|[]
name|containerOrdering
init|=
operator|new
name|String
index|[
name|containers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|CBlockClientServerProtocolProtos
operator|.
name|ContainerIDProto
name|containerProto
range|:
name|containers
control|)
block|{
name|ContainerDescriptor
name|containerDescriptor
init|=
operator|new
name|ContainerDescriptor
argument_list|(
name|containerProto
operator|.
name|getContainerID
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|containerProto
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerProto
operator|.
name|hasPipeline
argument_list|()
condition|)
block|{
name|containerDescriptor
operator|.
name|setPipeline
argument_list|(
name|Pipeline
operator|.
name|getFromProtoBuf
argument_list|(
name|containerProto
operator|.
name|getPipeline
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|volumeDescriptor
operator|.
name|addContainer
argument_list|(
name|containerDescriptor
argument_list|)
expr_stmt|;
name|containerOrdering
index|[
name|containerDescriptor
operator|.
name|getContainerIndex
argument_list|()
index|]
operator|=
name|containerDescriptor
operator|.
name|getContainerID
argument_list|()
expr_stmt|;
block|}
name|volumeDescriptor
operator|.
name|setContainerIDs
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|containerOrdering
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|volumeDescriptor
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|userName
operator|.
name|hashCode
argument_list|()
operator|*
literal|37
operator|+
name|volumeName
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|VolumeDescriptor
condition|)
block|{
name|VolumeDescriptor
name|other
init|=
operator|(
name|VolumeDescriptor
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|userName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|||
operator|!
name|volumeName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|||
name|volumeSize
operator|!=
name|other
operator|.
name|getVolumeSize
argument_list|()
operator|||
name|blockSize
operator|!=
name|other
operator|.
name|getBlockSize
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|containerIdOrdered
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|containerIdOrdered
operator|.
name|size
argument_list|()
operator|||
name|containerMap
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|containerMap
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|containerIdOrdered
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|containerIdOrdered
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|other
operator|.
name|containerIdOrdered
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|containerMap
operator|.
name|equals
argument_list|(
name|other
operator|.
name|containerMap
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

