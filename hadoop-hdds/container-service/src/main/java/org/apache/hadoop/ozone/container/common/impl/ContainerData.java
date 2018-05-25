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
comment|/**  * ContainerData is the in-memory representation of container metadata and is  * represented on disk by the .container file.  */
end_comment

begin_class
DECL|class|ContainerData
specifier|public
class|class
name|ContainerData
block|{
comment|//Type of the container.
comment|// For now, we support only KeyValueContainer.
DECL|field|containerType
specifier|private
specifier|final
name|ContainerType
name|containerType
decl_stmt|;
comment|// Unique identifier for the container
DECL|field|containerId
specifier|private
specifier|final
name|long
name|containerId
decl_stmt|;
comment|// Layout version of the container data
DECL|field|layOutVersion
specifier|private
specifier|final
name|ChunkLayOutVersion
name|layOutVersion
decl_stmt|;
comment|// Metadata of the container will be a key value pair.
comment|// This can hold information like volume name, owner etc.,
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
comment|// State of the Container
DECL|field|state
specifier|private
name|ContainerLifeCycleState
name|state
decl_stmt|;
comment|/** parameters for read/write statistics on the container. **/
DECL|field|readBytes
specifier|private
specifier|final
name|AtomicLong
name|readBytes
decl_stmt|;
DECL|field|writeBytes
specifier|private
specifier|final
name|AtomicLong
name|writeBytes
decl_stmt|;
DECL|field|readCount
specifier|private
specifier|final
name|AtomicLong
name|readCount
decl_stmt|;
DECL|field|writeCount
specifier|private
specifier|final
name|AtomicLong
name|writeCount
decl_stmt|;
comment|/**    * Creates a ContainerData Object, which holds metadata of the container.    * @param type - ContainerType    * @param containerId - ContainerId    */
DECL|method|ContainerData (ContainerType type, long containerId)
specifier|public
name|ContainerData
parameter_list|(
name|ContainerType
name|type
parameter_list|,
name|long
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerType
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|layOutVersion
operator|=
name|ChunkLayOutVersion
operator|.
name|getLatestVersion
argument_list|()
expr_stmt|;
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
name|state
operator|=
name|ContainerLifeCycleState
operator|.
name|OPEN
expr_stmt|;
name|this
operator|.
name|readCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|readBytes
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeBytes
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the containerId.    */
DECL|method|getContainerId ()
specifier|public
name|long
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
comment|/**    * Returns the type of the container.    * @return ContainerType    */
DECL|method|getContainerType ()
specifier|public
name|ContainerType
name|getContainerType
parameter_list|()
block|{
return|return
name|containerType
return|;
block|}
comment|/**    * Returns the state of the container.    * @return ContainerLifeCycleState    */
DECL|method|getState ()
specifier|public
specifier|synchronized
name|ContainerLifeCycleState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Set the state of the container.    * @param state    */
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
comment|/**    * Returns the layOutVersion of the actual container data format.    * @return layOutVersion    */
DECL|method|getLayOutVersion ()
specifier|public
name|ChunkLayOutVersion
name|getLayOutVersion
parameter_list|()
block|{
return|return
name|layOutVersion
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
comment|/**    * Retuns metadata of the container.    * @return metadata    */
DECL|method|getMetadata ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
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
comment|/**    * checks if the container is invalid.    * @return - boolean    */
DECL|method|isValid ()
specifier|public
specifier|synchronized
name|boolean
name|isValid
parameter_list|()
block|{
return|return
operator|!
operator|(
name|ContainerLifeCycleState
operator|.
name|INVALID
operator|==
name|state
operator|)
return|;
block|}
comment|/**    * checks if the container is closed.    * @return - boolean    */
DECL|method|isClosed ()
specifier|public
specifier|synchronized
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|ContainerLifeCycleState
operator|.
name|CLOSED
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
comment|/**    * Get the number of bytes read from the container.    * @return the number of bytes read from the container.    */
DECL|method|getReadBytes ()
specifier|public
name|long
name|getReadBytes
parameter_list|()
block|{
return|return
name|readBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of bytes read from the container.    * @param bytes number of bytes read.    */
DECL|method|incrReadBytes (long bytes)
specifier|public
name|void
name|incrReadBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|readBytes
operator|.
name|addAndGet
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the number of times the container is read.    * @return the number of times the container is read.    */
DECL|method|getReadCount ()
specifier|public
name|long
name|getReadCount
parameter_list|()
block|{
return|return
name|readCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of container read count by 1.    */
DECL|method|incrReadCount ()
specifier|public
name|void
name|incrReadCount
parameter_list|()
block|{
name|this
operator|.
name|readCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the number of bytes write into the container.    * @return the number of bytes write into the container.    */
DECL|method|getWriteBytes ()
specifier|public
name|long
name|getWriteBytes
parameter_list|()
block|{
return|return
name|writeBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of bytes write into the container.    * @param bytes the number of bytes write into the container.    */
DECL|method|incrWriteBytes (long bytes)
specifier|public
name|void
name|incrWriteBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|writeBytes
operator|.
name|addAndGet
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the number of writes into the container.    * @return the number of writes into the container.    */
DECL|method|getWriteCount ()
specifier|public
name|long
name|getWriteCount
parameter_list|()
block|{
return|return
name|writeCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of writes into the container by 1.    */
DECL|method|incrWriteCount ()
specifier|public
name|void
name|incrWriteCount
parameter_list|()
block|{
name|this
operator|.
name|writeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

