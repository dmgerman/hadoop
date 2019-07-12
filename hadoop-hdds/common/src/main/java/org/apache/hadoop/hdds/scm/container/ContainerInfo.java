begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container
package|package
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
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|max
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonAutoDetect
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonIgnore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|PropertyAccessor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectWriter
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
name|java
operator|.
name|io
operator|.
name|Externalizable
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
name|ObjectInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
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
name|Comparator
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
name|builder
operator|.
name|EqualsBuilder
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
name|builder
operator|.
name|HashCodeBuilder
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationFactor
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
operator|.
name|ReplicationType
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
name|pipeline
operator|.
name|PipelineID
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

begin_comment
comment|/**  * Class wraps ozone container info.  */
end_comment

begin_class
DECL|class|ContainerInfo
specifier|public
class|class
name|ContainerInfo
implements|implements
name|Comparator
argument_list|<
name|ContainerInfo
argument_list|>
implements|,
name|Comparable
argument_list|<
name|ContainerInfo
argument_list|>
implements|,
name|Externalizable
block|{
DECL|field|WRITER
specifier|private
specifier|static
specifier|final
name|ObjectWriter
name|WRITER
decl_stmt|;
DECL|field|SERIALIZATION_ERROR_MSG
specifier|private
specifier|static
specifier|final
name|String
name|SERIALIZATION_ERROR_MSG
init|=
literal|"Java serialization not"
operator|+
literal|" supported. Use protobuf instead."
decl_stmt|;
static|static
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|setVisibility
argument_list|(
name|PropertyAccessor
operator|.
name|FIELD
argument_list|,
name|JsonAutoDetect
operator|.
name|Visibility
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|setVisibility
argument_list|(
name|PropertyAccessor
operator|.
name|GETTER
argument_list|,
name|JsonAutoDetect
operator|.
name|Visibility
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|WRITER
operator|=
name|mapper
operator|.
name|writer
argument_list|()
expr_stmt|;
block|}
DECL|field|state
specifier|private
name|HddsProtos
operator|.
name|LifeCycleState
name|state
decl_stmt|;
annotation|@
name|JsonIgnore
DECL|field|pipelineID
specifier|private
name|PipelineID
name|pipelineID
decl_stmt|;
DECL|field|replicationFactor
specifier|private
name|ReplicationFactor
name|replicationFactor
decl_stmt|;
DECL|field|replicationType
specifier|private
name|ReplicationType
name|replicationType
decl_stmt|;
DECL|field|usedBytes
specifier|private
name|long
name|usedBytes
decl_stmt|;
DECL|field|numberOfKeys
specifier|private
name|long
name|numberOfKeys
decl_stmt|;
DECL|field|lastUsed
specifier|private
name|long
name|lastUsed
decl_stmt|;
comment|// The wall-clock ms since the epoch at which the current state enters.
DECL|field|stateEnterTime
specifier|private
name|long
name|stateEnterTime
decl_stmt|;
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
DECL|field|containerID
specifier|private
name|long
name|containerID
decl_stmt|;
DECL|field|deleteTransactionId
specifier|private
name|long
name|deleteTransactionId
decl_stmt|;
comment|// The sequenceId of a close container cannot change, and all the
comment|// container replica should have the same sequenceId.
DECL|field|sequenceId
specifier|private
name|long
name|sequenceId
decl_stmt|;
comment|/**    * Allows you to maintain private data on ContainerInfo. This is not    * serialized via protobuf, just allows us to maintain some private data.    */
annotation|@
name|JsonIgnore
DECL|field|data
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"parameternumber"
argument_list|)
DECL|method|ContainerInfo ( long containerID, HddsProtos.LifeCycleState state, PipelineID pipelineID, long usedBytes, long numberOfKeys, long stateEnterTime, String owner, long deleteTransactionId, long sequenceId, ReplicationFactor replicationFactor, ReplicationType repType)
name|ContainerInfo
parameter_list|(
name|long
name|containerID
parameter_list|,
name|HddsProtos
operator|.
name|LifeCycleState
name|state
parameter_list|,
name|PipelineID
name|pipelineID
parameter_list|,
name|long
name|usedBytes
parameter_list|,
name|long
name|numberOfKeys
parameter_list|,
name|long
name|stateEnterTime
parameter_list|,
name|String
name|owner
parameter_list|,
name|long
name|deleteTransactionId
parameter_list|,
name|long
name|sequenceId
parameter_list|,
name|ReplicationFactor
name|replicationFactor
parameter_list|,
name|ReplicationType
name|repType
parameter_list|)
block|{
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
name|this
operator|.
name|pipelineID
operator|=
name|pipelineID
expr_stmt|;
name|this
operator|.
name|usedBytes
operator|=
name|usedBytes
expr_stmt|;
name|this
operator|.
name|numberOfKeys
operator|=
name|numberOfKeys
expr_stmt|;
name|this
operator|.
name|lastUsed
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|stateEnterTime
operator|=
name|stateEnterTime
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|deleteTransactionId
operator|=
name|deleteTransactionId
expr_stmt|;
name|this
operator|.
name|sequenceId
operator|=
name|sequenceId
expr_stmt|;
name|this
operator|.
name|replicationFactor
operator|=
name|replicationFactor
expr_stmt|;
name|this
operator|.
name|replicationType
operator|=
name|repType
expr_stmt|;
block|}
comment|/**    * Needed for serialization findbugs.    */
DECL|method|ContainerInfo ()
specifier|public
name|ContainerInfo
parameter_list|()
block|{   }
DECL|method|fromProtobuf (HddsProtos.ContainerInfoProto info)
specifier|public
specifier|static
name|ContainerInfo
name|fromProtobuf
parameter_list|(
name|HddsProtos
operator|.
name|ContainerInfoProto
name|info
parameter_list|)
block|{
name|ContainerInfo
operator|.
name|Builder
name|builder
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
decl_stmt|;
return|return
name|builder
operator|.
name|setPipelineID
argument_list|(
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|info
operator|.
name|getPipelineID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
name|info
operator|.
name|getUsedBytes
argument_list|()
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
name|info
operator|.
name|getNumberOfKeys
argument_list|()
argument_list|)
operator|.
name|setState
argument_list|(
name|info
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|setStateEnterTime
argument_list|(
name|info
operator|.
name|getStateEnterTime
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
name|info
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
name|info
operator|.
name|getDeleteTransactionId
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|info
operator|.
name|getReplicationFactor
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|info
operator|.
name|getReplicationType
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getContainerID ()
specifier|public
name|long
name|getContainerID
parameter_list|()
block|{
return|return
name|containerID
return|;
block|}
DECL|method|getState ()
specifier|public
name|HddsProtos
operator|.
name|LifeCycleState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setState (HddsProtos.LifeCycleState state)
specifier|public
name|void
name|setState
parameter_list|(
name|HddsProtos
operator|.
name|LifeCycleState
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
DECL|method|getStateEnterTime ()
specifier|public
name|long
name|getStateEnterTime
parameter_list|()
block|{
return|return
name|stateEnterTime
return|;
block|}
DECL|method|getReplicationFactor ()
specifier|public
name|ReplicationFactor
name|getReplicationFactor
parameter_list|()
block|{
return|return
name|replicationFactor
return|;
block|}
DECL|method|getPipelineID ()
specifier|public
name|PipelineID
name|getPipelineID
parameter_list|()
block|{
return|return
name|pipelineID
return|;
block|}
DECL|method|getUsedBytes ()
specifier|public
name|long
name|getUsedBytes
parameter_list|()
block|{
return|return
name|usedBytes
return|;
block|}
DECL|method|setUsedBytes (long value)
specifier|public
name|void
name|setUsedBytes
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|usedBytes
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getNumberOfKeys ()
specifier|public
name|long
name|getNumberOfKeys
parameter_list|()
block|{
return|return
name|numberOfKeys
return|;
block|}
DECL|method|setNumberOfKeys (long value)
specifier|public
name|void
name|setNumberOfKeys
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|numberOfKeys
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getDeleteTransactionId ()
specifier|public
name|long
name|getDeleteTransactionId
parameter_list|()
block|{
return|return
name|deleteTransactionId
return|;
block|}
DECL|method|getSequenceId ()
specifier|public
name|long
name|getSequenceId
parameter_list|()
block|{
return|return
name|sequenceId
return|;
block|}
DECL|method|updateDeleteTransactionId (long transactionId)
specifier|public
name|void
name|updateDeleteTransactionId
parameter_list|(
name|long
name|transactionId
parameter_list|)
block|{
name|deleteTransactionId
operator|=
name|max
argument_list|(
name|transactionId
argument_list|,
name|deleteTransactionId
argument_list|)
expr_stmt|;
block|}
DECL|method|updateSequenceId (long sequenceID)
specifier|public
name|void
name|updateSequenceId
parameter_list|(
name|long
name|sequenceID
parameter_list|)
block|{
assert|assert
operator|(
name|isOpen
argument_list|()
operator|||
name|state
operator|==
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|QUASI_CLOSED
operator|)
assert|;
name|sequenceId
operator|=
name|max
argument_list|(
name|sequenceID
argument_list|,
name|sequenceId
argument_list|)
expr_stmt|;
block|}
DECL|method|containerID ()
specifier|public
name|ContainerID
name|containerID
parameter_list|()
block|{
return|return
operator|new
name|ContainerID
argument_list|(
name|getContainerID
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Gets the last used time from SCM's perspective.    *    * @return time in milliseconds.    */
DECL|method|getLastUsed ()
specifier|public
name|long
name|getLastUsed
parameter_list|()
block|{
return|return
name|lastUsed
return|;
block|}
DECL|method|getReplicationType ()
specifier|public
name|ReplicationType
name|getReplicationType
parameter_list|()
block|{
return|return
name|replicationType
return|;
block|}
DECL|method|updateLastUsedTime ()
specifier|public
name|void
name|updateLastUsedTime
parameter_list|()
block|{
name|lastUsed
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
DECL|method|getProtobuf ()
specifier|public
name|HddsProtos
operator|.
name|ContainerInfoProto
name|getProtobuf
parameter_list|()
block|{
name|HddsProtos
operator|.
name|ContainerInfoProto
operator|.
name|Builder
name|builder
init|=
name|HddsProtos
operator|.
name|ContainerInfoProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerID
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|setContainerID
argument_list|(
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|setUsedBytes
argument_list|(
name|getUsedBytes
argument_list|()
argument_list|)
operator|.
name|setNumberOfKeys
argument_list|(
name|getNumberOfKeys
argument_list|()
argument_list|)
operator|.
name|setState
argument_list|(
name|getState
argument_list|()
argument_list|)
operator|.
name|setStateEnterTime
argument_list|(
name|getStateEnterTime
argument_list|()
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
name|getDeleteTransactionId
argument_list|()
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|getPipelineID
argument_list|()
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|getReplicationFactor
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|getReplicationType
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
name|getOwner
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
DECL|method|setOwner (String owner)
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
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
literal|"ContainerInfo{"
operator|+
literal|"id="
operator|+
name|containerID
operator|+
literal|", state="
operator|+
name|state
operator|+
literal|", pipelineID="
operator|+
name|pipelineID
operator|+
literal|", stateEnterTime="
operator|+
name|stateEnterTime
operator|+
literal|", owner="
operator|+
name|owner
operator|+
literal|'}'
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
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ContainerInfo
name|that
init|=
operator|(
name|ContainerInfo
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|getContainerID
argument_list|()
argument_list|,
name|that
operator|.
name|getContainerID
argument_list|()
argument_list|)
comment|// TODO : Fix this later. If we add these factors some tests fail.
comment|// So Commenting this to continue and will enforce this with
comment|// Changes in pipeline where we remove Container Name to
comment|// SCMContainerinfo from Pipeline.
comment|// .append(pipeline.getFactor(), that.pipeline.getFactor())
comment|// .append(pipeline.getType(), that.pipeline.getType())
operator|.
name|append
argument_list|(
name|owner
argument_list|,
name|that
operator|.
name|owner
argument_list|)
operator|.
name|isEquals
argument_list|()
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
operator|new
name|HashCodeBuilder
argument_list|(
literal|11
argument_list|,
literal|811
argument_list|)
operator|.
name|append
argument_list|(
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getOwner
argument_list|()
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
comment|/**    * Compares its two arguments for order.  Returns a negative integer, zero, or    * a positive integer as the first argument is less than, equal to, or greater    * than the second.<p>    *    * @param o1 the first object to be compared.    * @param o2 the second object to be compared.    * @return a negative integer, zero, or a positive integer as the first    * argument is less than, equal to, or greater than the second.    * @throws NullPointerException if an argument is null and this comparator    *                              does not permit null arguments    * @throws ClassCastException   if the arguments' types prevent them from    *                              being compared by this comparator.    */
annotation|@
name|Override
DECL|method|compare (ContainerInfo o1, ContainerInfo o2)
specifier|public
name|int
name|compare
parameter_list|(
name|ContainerInfo
name|o1
parameter_list|,
name|ContainerInfo
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getLastUsed
argument_list|()
argument_list|,
name|o2
operator|.
name|getLastUsed
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compares this object with the specified object for order.  Returns a    * negative integer, zero, or a positive integer as this object is less than,    * equal to, or greater than the specified object.    *    * @param o the object to be compared.    * @return a negative integer, zero, or a positive integer as this object is    * less than, equal to, or greater than the specified object.    * @throws NullPointerException if the specified object is null    * @throws ClassCastException   if the specified object's type prevents it    *                              from being compared to this object.    */
annotation|@
name|Override
DECL|method|compareTo (ContainerInfo o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ContainerInfo
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|compare
argument_list|(
name|this
argument_list|,
name|o
argument_list|)
return|;
block|}
comment|/**    * Returns a JSON string of this object.    *    * @return String - json string    * @throws IOException    */
DECL|method|toJsonString ()
specifier|public
name|String
name|toJsonString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|WRITER
operator|.
name|writeValueAsString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns private data that is set on this containerInfo.    *    * @return blob, the user can interpret it any way they like.    */
DECL|method|getData ()
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|data
operator|!=
literal|null
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|data
argument_list|,
name|this
operator|.
name|data
operator|.
name|length
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Set private data on ContainerInfo object.    *    * @param data -- private data.    */
DECL|method|setData (byte[] data)
specifier|public
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|data
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Throws IOException as default java serialization is not supported. Use    * serialization via protobuf instead.    *    * @param out the stream to write the object to    * @throws IOException Includes any I/O exceptions that may occur    * @serialData Overriding methods should use this tag to describe    * the data layout of this Externalizable object.    * List the sequence of element types and, if possible,    * relate the element to a public/protected field and/or    * method of this Externalizable class.    */
annotation|@
name|Override
DECL|method|writeExternal (ObjectOutput out)
specifier|public
name|void
name|writeExternal
parameter_list|(
name|ObjectOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|SERIALIZATION_ERROR_MSG
argument_list|)
throw|;
block|}
comment|/**    * Throws IOException as default java serialization is not supported. Use    * serialization via protobuf instead.    *    * @param in the stream to read data from in order to restore the object    * @throws IOException            if I/O errors occur    * @throws ClassNotFoundException If the class for an object being    *                                restored cannot be found.    */
annotation|@
name|Override
DECL|method|readExternal (ObjectInput in)
specifier|public
name|void
name|readExternal
parameter_list|(
name|ObjectInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|SERIALIZATION_ERROR_MSG
argument_list|)
throw|;
block|}
comment|/**    * Builder class for ContainerInfo.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|state
specifier|private
name|HddsProtos
operator|.
name|LifeCycleState
name|state
decl_stmt|;
DECL|field|used
specifier|private
name|long
name|used
decl_stmt|;
DECL|field|keys
specifier|private
name|long
name|keys
decl_stmt|;
DECL|field|stateEnterTime
specifier|private
name|long
name|stateEnterTime
decl_stmt|;
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
DECL|field|containerID
specifier|private
name|long
name|containerID
decl_stmt|;
DECL|field|deleteTransactionId
specifier|private
name|long
name|deleteTransactionId
decl_stmt|;
DECL|field|sequenceId
specifier|private
name|long
name|sequenceId
decl_stmt|;
DECL|field|pipelineID
specifier|private
name|PipelineID
name|pipelineID
decl_stmt|;
DECL|field|replicationFactor
specifier|private
name|ReplicationFactor
name|replicationFactor
decl_stmt|;
DECL|field|replicationType
specifier|private
name|ReplicationType
name|replicationType
decl_stmt|;
DECL|method|setReplicationType ( ReplicationType repType)
specifier|public
name|Builder
name|setReplicationType
parameter_list|(
name|ReplicationType
name|repType
parameter_list|)
block|{
name|this
operator|.
name|replicationType
operator|=
name|repType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPipelineID (PipelineID pipelineId)
specifier|public
name|Builder
name|setPipelineID
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|)
block|{
name|this
operator|.
name|pipelineID
operator|=
name|pipelineId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setReplicationFactor (ReplicationFactor repFactor)
specifier|public
name|Builder
name|setReplicationFactor
parameter_list|(
name|ReplicationFactor
name|repFactor
parameter_list|)
block|{
name|this
operator|.
name|replicationFactor
operator|=
name|repFactor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setContainerID (long id)
specifier|public
name|Builder
name|setContainerID
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|id
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerID
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setState (HddsProtos.LifeCycleState lifeCycleState)
specifier|public
name|Builder
name|setState
parameter_list|(
name|HddsProtos
operator|.
name|LifeCycleState
name|lifeCycleState
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|lifeCycleState
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setUsedBytes (long bytesUsed)
specifier|public
name|Builder
name|setUsedBytes
parameter_list|(
name|long
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|used
operator|=
name|bytesUsed
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNumberOfKeys (long keyCount)
specifier|public
name|Builder
name|setNumberOfKeys
parameter_list|(
name|long
name|keyCount
parameter_list|)
block|{
name|this
operator|.
name|keys
operator|=
name|keyCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStateEnterTime (long time)
specifier|public
name|Builder
name|setStateEnterTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|stateEnterTime
operator|=
name|time
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOwner (String containerOwner)
specifier|public
name|Builder
name|setOwner
parameter_list|(
name|String
name|containerOwner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|containerOwner
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDeleteTransactionId (long deleteTransactionID)
specifier|public
name|Builder
name|setDeleteTransactionId
parameter_list|(
name|long
name|deleteTransactionID
parameter_list|)
block|{
name|this
operator|.
name|deleteTransactionId
operator|=
name|deleteTransactionID
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSequenceId (long sequenceID)
specifier|public
name|Builder
name|setSequenceId
parameter_list|(
name|long
name|sequenceID
parameter_list|)
block|{
name|this
operator|.
name|sequenceId
operator|=
name|sequenceID
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|ContainerInfo
name|build
parameter_list|()
block|{
return|return
operator|new
name|ContainerInfo
argument_list|(
name|containerID
argument_list|,
name|state
argument_list|,
name|pipelineID
argument_list|,
name|used
argument_list|,
name|keys
argument_list|,
name|stateEnterTime
argument_list|,
name|owner
argument_list|,
name|deleteTransactionId
argument_list|,
name|sequenceId
argument_list|,
name|replicationFactor
argument_list|,
name|replicationType
argument_list|)
return|;
block|}
block|}
comment|/**    * Check if a container is in open state, this will check if the    * container is either open or closing state. Any containers in these states    * is managed as an open container by SCM.    */
DECL|method|isOpen ()
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|state
operator|==
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
operator|||
name|state
operator|==
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
return|;
block|}
comment|/**    * Check if a container is in Open state, but Close has not been initiated.    * @return true if Open, false otherwise.    */
DECL|method|isOpenNotClosing ()
specifier|public
name|boolean
name|isOpenNotClosing
parameter_list|()
block|{
return|return
name|state
operator|==
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
return|;
block|}
block|}
end_class

end_unit

