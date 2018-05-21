begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.helpers
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
name|helpers
package|;
end_package

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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerType
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
operator|.
name|ContainerLifeCycleState
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
name|OzoneConsts
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
name|Collections
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * This class maintains the information about a container in the ozone world.  *<p>  * A container is a name, along with metadata- which is a set of key value  * pair.  */
end_comment

begin_class
DECL|class|ContainerData
specifier|public
class|class
name|ContainerData
block|{
DECL|field|metadata
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
DECL|field|dbPath
specifier|private
name|String
name|dbPath
decl_stmt|;
comment|// Path to Level DB Store.
comment|// Path to Physical file system where container and checksum are stored.
DECL|field|containerFilePath
specifier|private
name|String
name|containerFilePath
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|AtomicLong
name|bytesUsed
decl_stmt|;
DECL|field|maxSize
specifier|private
name|long
name|maxSize
decl_stmt|;
DECL|field|containerID
specifier|private
name|long
name|containerID
decl_stmt|;
DECL|field|state
specifier|private
name|ContainerLifeCycleState
name|state
decl_stmt|;
DECL|field|containerType
specifier|private
name|ContainerType
name|containerType
decl_stmt|;
DECL|field|containerDBType
specifier|private
name|String
name|containerDBType
decl_stmt|;
comment|/**    * Constructs a  ContainerData Object.    *    * @param containerID - ID    * @param conf - Configuration    */
DECL|method|ContainerData (long containerID, Configuration conf)
specifier|public
name|ContainerData
parameter_list|(
name|long
name|containerID
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|SCM_CONTAINER_CLIENT_MAX_SIZE_DEFAULT
argument_list|)
operator|*
name|OzoneConsts
operator|.
name|GB
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|ContainerLifeCycleState
operator|.
name|OPEN
expr_stmt|;
block|}
comment|/**    * Constructs a ContainerData object from ProtoBuf classes.    *    * @param protoData - ProtoBuf Message    * @throws IOException    */
DECL|method|getFromProtBuf ( ContainerProtos.ContainerData protoData, Configuration conf)
specifier|public
specifier|static
name|ContainerData
name|getFromProtBuf
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerData
name|protoData
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerData
name|data
init|=
operator|new
name|ContainerData
argument_list|(
name|protoData
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|protoData
operator|.
name|getMetadataCount
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
name|data
operator|.
name|addMetadata
argument_list|(
name|protoData
operator|.
name|getMetadata
argument_list|(
name|x
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|,
name|protoData
operator|.
name|getMetadata
argument_list|(
name|x
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasContainerPath
argument_list|()
condition|)
block|{
name|data
operator|.
name|setContainerPath
argument_list|(
name|protoData
operator|.
name|getContainerPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasDbPath
argument_list|()
condition|)
block|{
name|data
operator|.
name|setDBPath
argument_list|(
name|protoData
operator|.
name|getDbPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasState
argument_list|()
condition|)
block|{
name|data
operator|.
name|setState
argument_list|(
name|protoData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasBytesUsed
argument_list|()
condition|)
block|{
name|data
operator|.
name|setBytesUsed
argument_list|(
name|protoData
operator|.
name|getBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasSize
argument_list|()
condition|)
block|{
name|data
operator|.
name|setMaxSize
argument_list|(
name|protoData
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasContainerType
argument_list|()
condition|)
block|{
name|data
operator|.
name|setContainerType
argument_list|(
name|protoData
operator|.
name|getContainerType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protoData
operator|.
name|hasContainerDBType
argument_list|()
condition|)
block|{
name|data
operator|.
name|setContainerDBType
argument_list|(
name|protoData
operator|.
name|getContainerDBType
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
DECL|method|getContainerDBType ()
specifier|public
name|String
name|getContainerDBType
parameter_list|()
block|{
return|return
name|containerDBType
return|;
block|}
DECL|method|setContainerDBType (String containerDBType)
specifier|public
name|void
name|setContainerDBType
parameter_list|(
name|String
name|containerDBType
parameter_list|)
block|{
name|this
operator|.
name|containerDBType
operator|=
name|containerDBType
expr_stmt|;
block|}
comment|/**    * Returns a ProtoBuf Message from ContainerData.    *    * @return Protocol Buffer Message    */
DECL|method|getProtoBufMessage ()
specifier|public
name|ContainerProtos
operator|.
name|ContainerData
name|getProtoBufMessage
parameter_list|()
block|{
name|ContainerProtos
operator|.
name|ContainerData
operator|.
name|Builder
name|builder
init|=
name|ContainerProtos
operator|.
name|ContainerData
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setContainerID
argument_list|(
name|this
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|getDBPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setDbPath
argument_list|(
name|this
operator|.
name|getDBPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getContainerPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerPath
argument_list|(
name|this
operator|.
name|getContainerPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setState
argument_list|(
name|this
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|metadata
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ContainerProtos
operator|.
name|KeyValue
operator|.
name|Builder
name|keyValBuilder
init|=
name|ContainerProtos
operator|.
name|KeyValue
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addMetadata
argument_list|(
name|keyValBuilder
operator|.
name|setKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getBytesUsed
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|setBytesUsed
argument_list|(
name|this
operator|.
name|getBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getKeyCount
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|setKeyCount
argument_list|(
name|this
operator|.
name|getKeyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getMaxSize
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|setSize
argument_list|(
name|this
operator|.
name|getMaxSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getContainerType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerType
argument_list|(
name|containerType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getContainerDBType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setContainerDBType
argument_list|(
name|containerDBType
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|setContainerType (ContainerType containerType)
specifier|public
name|void
name|setContainerType
parameter_list|(
name|ContainerType
name|containerType
parameter_list|)
block|{
name|this
operator|.
name|containerType
operator|=
name|containerType
expr_stmt|;
block|}
DECL|method|getContainerType ()
specifier|public
name|ContainerType
name|getContainerType
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerType
return|;
block|}
comment|/**    * Adds metadata.    */
DECL|method|addMetadata (String key, String value)
specifier|public
name|void
name|addMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|metadata
init|)
block|{
if|if
condition|(
name|this
operator|.
name|metadata
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This key already exists. Key "
operator|+
name|key
argument_list|)
throw|;
block|}
name|metadata
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns all metadata.    */
DECL|method|getAllMetadata ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllMetadata
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|metadata
init|)
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|this
operator|.
name|metadata
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns value of a key.    */
DECL|method|getValue (String key)
specifier|public
name|String
name|getValue
parameter_list|(
name|String
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|metadata
init|)
block|{
return|return
name|metadata
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * Deletes a metadata entry from the map.    *    * @param key - Key    */
DECL|method|deleteKey (String key)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|String
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|metadata
init|)
block|{
name|metadata
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns path.    *    * @return - path    */
DECL|method|getDBPath ()
specifier|public
name|String
name|getDBPath
parameter_list|()
block|{
return|return
name|dbPath
return|;
block|}
comment|/**    * Sets path.    *    * @param path - String.    */
DECL|method|setDBPath (String path)
specifier|public
name|void
name|setDBPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|dbPath
operator|=
name|path
expr_stmt|;
block|}
comment|/**    * This function serves as the generic key for ContainerCache class. Both    * ContainerData and ContainerKeyData overrides this function to appropriately    * return the right name that can  be used in ContainerCache.    *    * @return String Name.    */
comment|// TODO: check the ContainerCache class to see if we are using the ContainerID instead.
comment|/*    public String getName() {     return getContainerID();   }*/
comment|/**    * Get container file path.    * @return - Physical path where container file and checksum is stored.    */
DECL|method|getContainerPath ()
specifier|public
name|String
name|getContainerPath
parameter_list|()
block|{
return|return
name|containerFilePath
return|;
block|}
comment|/**    * Set container Path.    * @param containerPath - File path.    */
DECL|method|setContainerPath (String containerPath)
specifier|public
name|void
name|setContainerPath
parameter_list|(
name|String
name|containerPath
parameter_list|)
block|{
name|this
operator|.
name|containerFilePath
operator|=
name|containerPath
expr_stmt|;
block|}
comment|/**    * Get container ID.    * @return - container ID.    */
DECL|method|getContainerID ()
specifier|public
specifier|synchronized
name|long
name|getContainerID
parameter_list|()
block|{
return|return
name|containerID
return|;
block|}
DECL|method|setState (ContainerLifeCycleState state)
specifier|public
specifier|synchronized
name|void
name|setState
parameter_list|(
name|ContainerLifeCycleState
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
DECL|method|getState ()
specifier|public
specifier|synchronized
name|ContainerLifeCycleState
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
comment|/**    * checks if the container is open.    * @return - boolean    */
DECL|method|isOpen ()
specifier|public
specifier|synchronized
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|ContainerLifeCycleState
operator|.
name|OPEN
operator|==
name|state
return|;
block|}
comment|/**    * Marks this container as closed.    */
DECL|method|closeContainer ()
specifier|public
specifier|synchronized
name|void
name|closeContainer
parameter_list|()
block|{
comment|// TODO: closed or closing here
name|setState
argument_list|(
name|ContainerLifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
DECL|method|setMaxSize (long maxSize)
specifier|public
name|void
name|setMaxSize
parameter_list|(
name|long
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
DECL|method|getMaxSize ()
specifier|public
name|long
name|getMaxSize
parameter_list|()
block|{
return|return
name|maxSize
return|;
block|}
DECL|method|getKeyCount ()
specifier|public
name|long
name|getKeyCount
parameter_list|()
block|{
return|return
name|metadata
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|setBytesUsed (long used)
specifier|public
name|void
name|setBytesUsed
parameter_list|(
name|long
name|used
parameter_list|)
block|{
name|this
operator|.
name|bytesUsed
operator|.
name|set
argument_list|(
name|used
argument_list|)
expr_stmt|;
block|}
DECL|method|addBytesUsed (long delta)
specifier|public
name|long
name|addBytesUsed
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
return|return
name|this
operator|.
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
return|;
block|}
DECL|method|getBytesUsed ()
specifier|public
name|long
name|getBytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

