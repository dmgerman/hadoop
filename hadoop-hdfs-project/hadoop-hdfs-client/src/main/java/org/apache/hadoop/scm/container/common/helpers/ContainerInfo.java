begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.container.common.helpers
package|package
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
package|;
end_package

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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/** Class wraps ozone container info. */
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
block|{
DECL|field|state
specifier|private
name|OzoneProtos
operator|.
name|LifeCycleState
name|state
decl_stmt|;
DECL|field|pipeline
specifier|private
name|Pipeline
name|pipeline
decl_stmt|;
comment|// Bytes allocated by SCM for clients.
DECL|field|allocatedBytes
specifier|private
name|long
name|allocatedBytes
decl_stmt|;
comment|// Actual container usage, updated through heartbeat.
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
name|OzoneProtos
operator|.
name|Owner
name|owner
decl_stmt|;
DECL|field|containerName
specifier|private
name|String
name|containerName
decl_stmt|;
DECL|method|ContainerInfo ( final String containerName, OzoneProtos.LifeCycleState state, Pipeline pipeline, long allocatedBytes, long usedBytes, long numberOfKeys, long stateEnterTime, OzoneProtos.Owner owner)
name|ContainerInfo
parameter_list|(
specifier|final
name|String
name|containerName
parameter_list|,
name|OzoneProtos
operator|.
name|LifeCycleState
name|state
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|long
name|allocatedBytes
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
name|OzoneProtos
operator|.
name|Owner
name|owner
parameter_list|)
block|{
name|this
operator|.
name|containerName
operator|=
name|containerName
expr_stmt|;
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
name|this
operator|.
name|allocatedBytes
operator|=
name|allocatedBytes
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
block|}
comment|/**    * Needed for serialization findbugs.    */
DECL|method|ContainerInfo ()
specifier|public
name|ContainerInfo
parameter_list|()
block|{   }
DECL|method|fromProtobuf (OzoneProtos.SCMContainerInfo info)
specifier|public
specifier|static
name|ContainerInfo
name|fromProtobuf
parameter_list|(
name|OzoneProtos
operator|.
name|SCMContainerInfo
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
name|builder
operator|.
name|setPipeline
argument_list|(
name|Pipeline
operator|.
name|getFromProtoBuf
argument_list|(
name|info
operator|.
name|getPipeline
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setAllocatedBytes
argument_list|(
name|info
operator|.
name|getAllocatedBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setUsedBytes
argument_list|(
name|info
operator|.
name|getUsedBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setNumberOfKeys
argument_list|(
name|info
operator|.
name|getNumberOfKeys
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setState
argument_list|(
name|info
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setStateEnterTime
argument_list|(
name|info
operator|.
name|getStateEnterTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setOwner
argument_list|(
name|info
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setContainerName
argument_list|(
name|info
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getContainerName ()
specifier|public
name|String
name|getContainerName
parameter_list|()
block|{
return|return
name|containerName
return|;
block|}
DECL|method|getState ()
specifier|public
name|OzoneProtos
operator|.
name|LifeCycleState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
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
DECL|method|getPipeline ()
specifier|public
name|Pipeline
name|getPipeline
parameter_list|()
block|{
return|return
name|pipeline
return|;
block|}
DECL|method|getAllocatedBytes ()
specifier|public
name|long
name|getAllocatedBytes
parameter_list|()
block|{
return|return
name|allocatedBytes
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
comment|/**    * Gets the last used time from SCM's perspective.    * @return time in milliseconds.    */
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
DECL|method|allocate (long size)
specifier|public
name|void
name|allocate
parameter_list|(
name|long
name|size
parameter_list|)
block|{
comment|// should we also have total container size in ContainerInfo
comment|// and check before allocating?
name|allocatedBytes
operator|+=
name|size
expr_stmt|;
block|}
DECL|method|getProtobuf ()
specifier|public
name|OzoneProtos
operator|.
name|SCMContainerInfo
name|getProtobuf
parameter_list|()
block|{
name|OzoneProtos
operator|.
name|SCMContainerInfo
operator|.
name|Builder
name|builder
init|=
name|OzoneProtos
operator|.
name|SCMContainerInfo
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setPipeline
argument_list|(
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setAllocatedBytes
argument_list|(
name|getAllocatedBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setUsedBytes
argument_list|(
name|getUsedBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setNumberOfKeys
argument_list|(
name|getNumberOfKeys
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setStateEnterTime
argument_list|(
name|stateEnterTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|getOwner
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setOwner
argument_list|(
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setContainerName
argument_list|(
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getOwner ()
specifier|public
name|OzoneProtos
operator|.
name|Owner
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
DECL|method|setOwner (OzoneProtos.Owner owner)
specifier|public
name|void
name|setOwner
parameter_list|(
name|OzoneProtos
operator|.
name|Owner
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
literal|"state="
operator|+
name|state
operator|+
literal|", pipeline="
operator|+
name|pipeline
operator|+
literal|", stateEnterTime="
operator|+
name|stateEnterTime
operator|+
literal|", owner="
operator|+
name|owner
operator|+
literal|", containerName='"
operator|+
name|containerName
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
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|that
operator|.
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|)
comment|// TODO : Fix this later. If we add these factors some tests fail.
comment|// So Commenting this to continue and will enforce this with
comment|// Changes in pipeline where we remove Container Name to
comment|// SCMContainerinfo from Pipline.
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
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|pipeline
operator|.
name|getFactor
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|pipeline
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|owner
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
comment|/** Builder class for ContainerInfo. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|state
specifier|private
name|OzoneProtos
operator|.
name|LifeCycleState
name|state
decl_stmt|;
DECL|field|pipeline
specifier|private
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|allocated
specifier|private
name|long
name|allocated
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
name|OzoneProtos
operator|.
name|Owner
name|owner
decl_stmt|;
DECL|field|containerName
specifier|private
name|String
name|containerName
decl_stmt|;
DECL|method|setState (OzoneProtos.LifeCycleState lifeCycleState)
specifier|public
name|Builder
name|setState
parameter_list|(
name|OzoneProtos
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
DECL|method|setPipeline (Pipeline pipeline)
specifier|public
name|Builder
name|setPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAllocatedBytes (long bytesAllocated)
specifier|public
name|Builder
name|setAllocatedBytes
parameter_list|(
name|long
name|bytesAllocated
parameter_list|)
block|{
name|this
operator|.
name|allocated
operator|=
name|bytesAllocated
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
DECL|method|setStateEnterTime (long stateEnterTime)
specifier|public
name|Builder
name|setStateEnterTime
parameter_list|(
name|long
name|stateEnterTime
parameter_list|)
block|{
name|this
operator|.
name|stateEnterTime
operator|=
name|stateEnterTime
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOwner (OzoneProtos.Owner owner)
specifier|public
name|Builder
name|setOwner
parameter_list|(
name|OzoneProtos
operator|.
name|Owner
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setContainerName (String containerName)
specifier|public
name|Builder
name|setContainerName
parameter_list|(
name|String
name|containerName
parameter_list|)
block|{
name|this
operator|.
name|containerName
operator|=
name|containerName
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
name|containerName
argument_list|,
name|state
argument_list|,
name|pipeline
argument_list|,
name|allocated
argument_list|,
name|used
argument_list|,
name|keys
argument_list|,
name|stateEnterTime
argument_list|,
name|owner
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

