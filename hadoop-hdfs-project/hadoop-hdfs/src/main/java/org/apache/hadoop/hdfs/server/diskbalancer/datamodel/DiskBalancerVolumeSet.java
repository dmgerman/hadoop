begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.datamodel
package|package
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
name|diskbalancer
operator|.
name|datamodel
package|;
end_package

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
name|JsonIgnoreProperties
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
name|JsonProperty
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
name|Serializable
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
name|Comparator
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
name|TreeSet
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
comment|/**  * DiskBalancerVolumeSet is a collection of storage devices on the  * data node which are of similar StorageType.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
block|{
literal|"sortedQueue"
block|,
literal|"volumeCount"
block|,
literal|"idealUsed"
block|}
argument_list|)
DECL|class|DiskBalancerVolumeSet
specifier|public
class|class
name|DiskBalancerVolumeSet
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
name|DiskBalancerVolumeSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxDisks
specifier|private
specifier|final
name|int
name|maxDisks
init|=
literal|256
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"transient"
argument_list|)
DECL|field|isTransient
specifier|private
name|boolean
name|isTransient
decl_stmt|;
DECL|field|volumes
specifier|private
name|Set
argument_list|<
name|DiskBalancerVolume
argument_list|>
name|volumes
decl_stmt|;
annotation|@
name|JsonIgnore
DECL|field|sortedQueue
specifier|private
name|TreeSet
argument_list|<
name|DiskBalancerVolume
argument_list|>
name|sortedQueue
decl_stmt|;
DECL|field|storageType
specifier|private
name|String
name|storageType
decl_stmt|;
DECL|field|setID
specifier|private
name|String
name|setID
decl_stmt|;
DECL|field|idealUsed
specifier|private
name|double
name|idealUsed
decl_stmt|;
comment|/**    * Constructs Empty DiskNBalanceVolumeSet.    * This is needed by jackson    */
DECL|method|DiskBalancerVolumeSet ()
specifier|public
name|DiskBalancerVolumeSet
parameter_list|()
block|{
name|setID
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs a DiskBalancerVolumeSet.    *    * @param isTransient - boolean    */
DECL|method|DiskBalancerVolumeSet (boolean isTransient)
specifier|public
name|DiskBalancerVolumeSet
parameter_list|(
name|boolean
name|isTransient
parameter_list|)
block|{
name|this
operator|.
name|isTransient
operator|=
name|isTransient
expr_stmt|;
name|volumes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|maxDisks
argument_list|)
expr_stmt|;
name|sortedQueue
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
operator|new
name|MinHeap
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
literal|null
expr_stmt|;
name|setID
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs a new DiskBalancerVolumeSet.    */
DECL|method|DiskBalancerVolumeSet (DiskBalancerVolumeSet volumeSet)
specifier|public
name|DiskBalancerVolumeSet
parameter_list|(
name|DiskBalancerVolumeSet
name|volumeSet
parameter_list|)
block|{
name|this
operator|.
name|isTransient
operator|=
name|volumeSet
operator|.
name|isTransient
argument_list|()
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|volumeSet
operator|.
name|storageType
expr_stmt|;
name|this
operator|.
name|volumes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|volumeSet
operator|.
name|volumes
argument_list|)
expr_stmt|;
name|sortedQueue
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
operator|new
name|MinHeap
argument_list|()
argument_list|)
expr_stmt|;
name|setID
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tells us if this volumeSet is transient.    *    * @return - true or false    */
annotation|@
name|JsonProperty
argument_list|(
literal|"transient"
argument_list|)
DECL|method|isTransient ()
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
name|isTransient
return|;
block|}
comment|/**    * Set the transient properties for this volumeSet.    *    * @param transientValue - Boolean    */
annotation|@
name|JsonProperty
argument_list|(
literal|"transient"
argument_list|)
DECL|method|setTransient (boolean transientValue)
specifier|public
name|void
name|setTransient
parameter_list|(
name|boolean
name|transientValue
parameter_list|)
block|{
name|this
operator|.
name|isTransient
operator|=
name|transientValue
expr_stmt|;
block|}
comment|/**    * Computes Volume Data Density. Adding a new volume changes    * the volumeDataDensity for all volumes. So we throw away    * our priority queue and recompute everything.    *    * we discard failed volumes from this computation.    *    * totalCapacity = totalCapacity of this volumeSet    * totalUsed = totalDfsUsed for this volumeSet    * idealUsed = totalUsed / totalCapacity    * dfsUsedRatio = dfsUsedOnAVolume / Capacity On that Volume    * volumeDataDensity = idealUsed - dfsUsedRatio    */
DECL|method|computeVolumeDataDensity ()
specifier|public
name|void
name|computeVolumeDataDensity
parameter_list|()
block|{
name|long
name|totalCapacity
init|=
literal|0
decl_stmt|;
name|long
name|totalUsed
init|=
literal|0
decl_stmt|;
name|sortedQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// when we plan to re-distribute data we need to make
comment|// sure that we skip failed volumes.
for|for
control|(
name|DiskBalancerVolume
name|volume
range|:
name|volumes
control|)
block|{
if|if
condition|(
operator|!
name|volume
operator|.
name|isFailed
argument_list|()
operator|&&
operator|!
name|volume
operator|.
name|isSkip
argument_list|()
condition|)
block|{
if|if
condition|(
name|volume
operator|.
name|computeEffectiveCapacity
argument_list|()
operator|<
literal|0
condition|)
block|{
name|skipMisConfiguredVolume
argument_list|(
name|volume
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|totalCapacity
operator|+=
name|volume
operator|.
name|computeEffectiveCapacity
argument_list|()
expr_stmt|;
name|totalUsed
operator|+=
name|volume
operator|.
name|getUsed
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|totalCapacity
operator|!=
literal|0
condition|)
block|{
name|this
operator|.
name|idealUsed
operator|=
name|truncateDecimals
argument_list|(
name|totalUsed
operator|/
operator|(
name|double
operator|)
name|totalCapacity
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DiskBalancerVolume
name|volume
range|:
name|volumes
control|)
block|{
if|if
condition|(
operator|!
name|volume
operator|.
name|isFailed
argument_list|()
operator|&&
operator|!
name|volume
operator|.
name|isSkip
argument_list|()
condition|)
block|{
name|double
name|dfsUsedRatio
init|=
name|truncateDecimals
argument_list|(
name|volume
operator|.
name|getUsed
argument_list|()
operator|/
operator|(
name|double
operator|)
name|volume
operator|.
name|computeEffectiveCapacity
argument_list|()
argument_list|)
decl_stmt|;
name|volume
operator|.
name|setVolumeDataDensity
argument_list|(
name|this
operator|.
name|idealUsed
operator|-
name|dfsUsedRatio
argument_list|)
expr_stmt|;
name|sortedQueue
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Truncate to 4 digits since uncontrolled precision is some times    * counter intitive to what users expect.    * @param value - double.    * @return double.    */
DECL|method|truncateDecimals (double value)
specifier|private
name|double
name|truncateDecimals
parameter_list|(
name|double
name|value
parameter_list|)
block|{
specifier|final
name|int
name|multiplier
init|=
literal|10000
decl_stmt|;
return|return
call|(
name|double
call|)
argument_list|(
call|(
name|long
call|)
argument_list|(
name|value
operator|*
name|multiplier
argument_list|)
argument_list|)
operator|/
name|multiplier
return|;
block|}
DECL|method|skipMisConfiguredVolume (DiskBalancerVolume volume)
specifier|private
name|void
name|skipMisConfiguredVolume
parameter_list|(
name|DiskBalancerVolume
name|volume
parameter_list|)
block|{
comment|//probably points to some sort of mis-configuration. Log this and skip
comment|// processing this volume.
name|String
name|errMessage
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Real capacity is negative."
operator|+
literal|"This usually points to some "
operator|+
literal|"kind of mis-configuration.%n"
operator|+
literal|"Capacity : %d Reserved : %d "
operator|+
literal|"realCap = capacity - "
operator|+
literal|"reserved = %d.%n"
operator|+
literal|"Skipping this volume from "
operator|+
literal|"all processing. type : %s id"
operator|+
literal|" :%s"
argument_list|,
name|volume
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|volume
operator|.
name|getReserved
argument_list|()
argument_list|,
name|volume
operator|.
name|computeEffectiveCapacity
argument_list|()
argument_list|,
name|volume
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|volume
operator|.
name|getUuid
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errMessage
argument_list|)
expr_stmt|;
name|volume
operator|.
name|setSkip
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the number of volumes in the Volume Set.    *    * @return int    */
annotation|@
name|JsonIgnore
DECL|method|getVolumeCount ()
specifier|public
name|int
name|getVolumeCount
parameter_list|()
block|{
return|return
name|volumes
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Get Storage Type.    *    * @return String    */
DECL|method|getStorageType ()
specifier|public
name|String
name|getStorageType
parameter_list|()
block|{
return|return
name|storageType
return|;
block|}
comment|/**    * Set Storage Type.    * @param typeOfStorage -- StorageType    */
DECL|method|setStorageType (String typeOfStorage)
specifier|public
name|void
name|setStorageType
parameter_list|(
name|String
name|typeOfStorage
parameter_list|)
block|{
name|this
operator|.
name|storageType
operator|=
name|typeOfStorage
expr_stmt|;
block|}
comment|/**    * adds a given volume into this volume set.    *    * @param volume - volume to add.    *    * @throws Exception    */
DECL|method|addVolume (DiskBalancerVolume volume)
specifier|public
name|void
name|addVolume
parameter_list|(
name|DiskBalancerVolume
name|volume
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|,
literal|"volume cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isTransient
argument_list|()
operator|==
name|volume
operator|.
name|isTransient
argument_list|()
argument_list|,
literal|"Mismatch in volumeSet and volume's transient "
operator|+
literal|"properties."
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|storageType
operator|==
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|volumes
operator|.
name|size
argument_list|()
operator|==
literal|0L
argument_list|,
literal|"Storage Type is Null but"
operator|+
literal|" volume size is "
operator|+
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|volume
operator|.
name|getStorageType
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|storageType
operator|.
name|equals
argument_list|(
name|volume
operator|.
name|getStorageType
argument_list|()
argument_list|)
argument_list|,
literal|"Adding wrong type of disk to this volume set"
argument_list|)
expr_stmt|;
block|}
name|volumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|computeVolumeDataDensity
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a list diskVolumes that are part of this volume set.    *    * @return List    */
DECL|method|getVolumes ()
specifier|public
name|List
argument_list|<
name|DiskBalancerVolume
argument_list|>
name|getVolumes
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|volumes
argument_list|)
return|;
block|}
annotation|@
name|JsonIgnore
DECL|method|getSortedQueue ()
specifier|public
name|TreeSet
argument_list|<
name|DiskBalancerVolume
argument_list|>
name|getSortedQueue
parameter_list|()
block|{
return|return
name|sortedQueue
return|;
block|}
comment|/**    * Computes whether we need to do any balancing on this volume Set at all.    * It checks if any disks are out of threshold value    *    * @param thresholdPercentage - threshold - in percentage    *    * @return true if balancing is needed false otherwise.    */
DECL|method|isBalancingNeeded (double thresholdPercentage)
specifier|public
name|boolean
name|isBalancingNeeded
parameter_list|(
name|double
name|thresholdPercentage
parameter_list|)
block|{
name|double
name|threshold
init|=
name|thresholdPercentage
operator|/
literal|100.0d
decl_stmt|;
if|if
condition|(
name|volumes
operator|==
literal|null
operator|||
name|volumes
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
comment|// there is nothing we can do with a single volume.
comment|// so no planning needed.
return|return
literal|false
return|;
block|}
for|for
control|(
name|DiskBalancerVolume
name|vol
range|:
name|volumes
control|)
block|{
name|boolean
name|notSkip
init|=
operator|!
name|vol
operator|.
name|isFailed
argument_list|()
operator|&&
operator|!
name|vol
operator|.
name|isTransient
argument_list|()
operator|&&
operator|!
name|vol
operator|.
name|isSkip
argument_list|()
decl_stmt|;
name|Double
name|absDensity
init|=
name|truncateDecimals
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|vol
operator|.
name|getVolumeDataDensity
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|absDensity
operator|>
name|threshold
operator|)
operator|&&
name|notSkip
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Remove a volume from the current set.    *    * This call does not recompute the volumeDataDensity. It has to be    * done manually after this call.    *    * @param volume - Volume to remove    */
DECL|method|removeVolume (DiskBalancerVolume volume)
specifier|public
name|void
name|removeVolume
parameter_list|(
name|DiskBalancerVolume
name|volume
parameter_list|)
block|{
name|volumes
operator|.
name|remove
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|sortedQueue
operator|.
name|remove
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get Volume Set ID.    * @return String    */
DECL|method|getSetID ()
specifier|public
name|String
name|getSetID
parameter_list|()
block|{
return|return
name|setID
return|;
block|}
comment|/**    * Set VolumeSet ID.    * @param volID String    */
DECL|method|setSetID (String volID)
specifier|public
name|void
name|setSetID
parameter_list|(
name|String
name|volID
parameter_list|)
block|{
name|this
operator|.
name|setID
operator|=
name|volID
expr_stmt|;
block|}
comment|/**    * Gets the idealUsed for this volume set.    */
annotation|@
name|JsonIgnore
DECL|method|getIdealUsed ()
specifier|public
name|double
name|getIdealUsed
parameter_list|()
block|{
return|return
name|this
operator|.
name|idealUsed
return|;
block|}
DECL|class|MinHeap
specifier|static
class|class
name|MinHeap
implements|implements
name|Comparator
argument_list|<
name|DiskBalancerVolume
argument_list|>
implements|,
name|Serializable
block|{
comment|/**      * Compares its two arguments for order.  Returns a negative integer,      * zero, or a positive integer as the first argument is less than, equal      * to, or greater than the second.      */
annotation|@
name|Override
DECL|method|compare (DiskBalancerVolume first, DiskBalancerVolume second)
specifier|public
name|int
name|compare
parameter_list|(
name|DiskBalancerVolume
name|first
parameter_list|,
name|DiskBalancerVolume
name|second
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|second
operator|.
name|getVolumeDataDensity
argument_list|()
argument_list|,
name|first
operator|.
name|getVolumeDataDensity
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

