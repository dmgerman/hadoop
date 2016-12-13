begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|ObjectReader
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|JsonUtil
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

begin_comment
comment|/**  * DiskBalancerVolume represents a volume in the DataNode.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
DECL|class|DiskBalancerVolume
specifier|public
class|class
name|DiskBalancerVolume
block|{
DECL|field|READER
specifier|private
specifier|static
specifier|final
name|ObjectReader
name|READER
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|readerFor
argument_list|(
name|DiskBalancerVolume
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|capacity
specifier|private
name|long
name|capacity
decl_stmt|;
DECL|field|storageType
specifier|private
name|String
name|storageType
decl_stmt|;
DECL|field|used
specifier|private
name|long
name|used
decl_stmt|;
DECL|field|reserved
specifier|private
name|long
name|reserved
decl_stmt|;
DECL|field|uuid
specifier|private
name|String
name|uuid
decl_stmt|;
DECL|field|failed
specifier|private
name|boolean
name|failed
decl_stmt|;
DECL|field|isTransient
specifier|private
name|boolean
name|isTransient
decl_stmt|;
DECL|field|volumeDataDensity
specifier|private
name|double
name|volumeDataDensity
decl_stmt|;
DECL|field|skip
specifier|private
name|boolean
name|skip
init|=
literal|false
decl_stmt|;
DECL|field|isReadOnly
specifier|private
name|boolean
name|isReadOnly
decl_stmt|;
comment|/**    * Constructs DiskBalancerVolume.    */
DECL|method|DiskBalancerVolume ()
specifier|public
name|DiskBalancerVolume
parameter_list|()
block|{   }
comment|/**    * Parses a Json string and converts to DiskBalancerVolume.    *    * @param json - Json String    *    * @return DiskBalancerCluster    *    * @throws IOException    */
DECL|method|parseJson (String json)
specifier|public
specifier|static
name|DiskBalancerVolume
name|parseJson
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|READER
operator|.
name|readValue
argument_list|(
name|json
argument_list|)
return|;
block|}
comment|/**    * Get this volume Data Density    * Please see DiskBalancerVolumeSet#computeVolumeDataDensity to see how    * this is computed.    *    * @return float.    */
DECL|method|getVolumeDataDensity ()
specifier|public
name|double
name|getVolumeDataDensity
parameter_list|()
block|{
return|return
name|volumeDataDensity
return|;
block|}
comment|/**    * Sets this volume's data density.    *    * @param volDataDensity - density    */
DECL|method|setVolumeDataDensity (double volDataDensity)
specifier|public
name|void
name|setVolumeDataDensity
parameter_list|(
name|double
name|volDataDensity
parameter_list|)
block|{
name|this
operator|.
name|volumeDataDensity
operator|=
name|volDataDensity
expr_stmt|;
block|}
comment|/**    * Indicates if the volume is Transient in nature.    *    * @return true or false.    */
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
comment|/**    * Sets volumes transient nature.    *    * @param aTransient - bool    */
DECL|method|setTransient (boolean aTransient)
specifier|public
name|void
name|setTransient
parameter_list|(
name|boolean
name|aTransient
parameter_list|)
block|{
name|this
operator|.
name|isTransient
operator|=
name|aTransient
expr_stmt|;
block|}
comment|/**    * Compares two volumes and decides if it is the same volume.    *    * @param o Volume Object    *    * @return boolean    */
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
name|DiskBalancerVolume
name|that
init|=
operator|(
name|DiskBalancerVolume
operator|)
name|o
decl_stmt|;
return|return
name|uuid
operator|.
name|equals
argument_list|(
name|that
operator|.
name|uuid
argument_list|)
return|;
block|}
comment|/**    * Computes hash code for a diskBalancerVolume.    *    * @return int    */
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|uuid
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Capacity of this volume.    *    * @return long    */
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
comment|/**    * Get free space of the volume.    *    * @return long    */
annotation|@
name|JsonIgnore
DECL|method|getFreeSpace ()
specifier|public
name|long
name|getFreeSpace
parameter_list|()
block|{
return|return
name|getCapacity
argument_list|()
operator|-
name|getUsed
argument_list|()
return|;
block|}
comment|/**    * Get ratio between used space and capacity.    *    * @return double    */
annotation|@
name|JsonIgnore
DECL|method|getUsedRatio ()
specifier|public
name|double
name|getUsedRatio
parameter_list|()
block|{
return|return
operator|(
literal|1.0
operator|*
name|getUsed
argument_list|()
operator|)
operator|/
name|getCapacity
argument_list|()
return|;
block|}
comment|/**    * Get ratio between free space and capacity.    *    * @return double    */
annotation|@
name|JsonIgnore
DECL|method|getFreeRatio ()
specifier|public
name|double
name|getFreeRatio
parameter_list|()
block|{
return|return
operator|(
literal|1.0
operator|*
name|getFreeSpace
argument_list|()
operator|)
operator|/
name|getCapacity
argument_list|()
return|;
block|}
comment|/**    * Sets the capacity of this volume.    *    * @param totalCapacity long    */
DECL|method|setCapacity (long totalCapacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|long
name|totalCapacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|totalCapacity
expr_stmt|;
block|}
comment|/**    * Indicates if this is a failed volume.    *    * @return boolean    */
DECL|method|isFailed ()
specifier|public
name|boolean
name|isFailed
parameter_list|()
block|{
return|return
name|failed
return|;
block|}
comment|/**    * Sets the failed flag for this volume.    *    * @param fail boolean    */
DECL|method|setFailed (boolean fail)
specifier|public
name|void
name|setFailed
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
name|this
operator|.
name|failed
operator|=
name|fail
expr_stmt|;
block|}
comment|/**    * Returns the path for this volume.    *    * @return String    */
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**    * Sets the path for this volume.    *    * @param volPath Path    */
DECL|method|setPath (String volPath)
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|volPath
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|volPath
expr_stmt|;
block|}
comment|/**    * Gets the reserved size for this volume.    *    * @return Long - Reserved size.    */
DECL|method|getReserved ()
specifier|public
name|long
name|getReserved
parameter_list|()
block|{
return|return
name|reserved
return|;
block|}
comment|/**    * Sets the reserved size.    *    * @param reservedSize -- Sets the reserved.    */
DECL|method|setReserved (long reservedSize)
specifier|public
name|void
name|setReserved
parameter_list|(
name|long
name|reservedSize
parameter_list|)
block|{
name|this
operator|.
name|reserved
operator|=
name|reservedSize
expr_stmt|;
block|}
comment|/**    * Gets the StorageType.    *    * @return String StorageType.    */
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
comment|/**    * Sets the StorageType.    *    * @param typeOfStorage - Storage Type String.    */
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
comment|/**    * Gets the dfsUsed Size.    *    * @return - long - used space    */
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
block|{
return|return
name|used
return|;
block|}
comment|/**    * Sets the used Space for Long.    *    * @param dfsUsedSpace - dfsUsedSpace for this volume.    */
DECL|method|setUsed (long dfsUsedSpace)
specifier|public
name|void
name|setUsed
parameter_list|(
name|long
name|dfsUsedSpace
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|dfsUsedSpace
operator|<
name|this
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|used
operator|=
name|dfsUsedSpace
expr_stmt|;
block|}
comment|/**    * Gets the uuid for this volume.    *    * @return String - uuid of th volume    */
DECL|method|getUuid ()
specifier|public
name|String
name|getUuid
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
comment|/**    * Sets the uuid for this volume.    *    * @param id - String    */
DECL|method|setUuid (String id)
specifier|public
name|void
name|setUuid
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|uuid
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * Returns effective capacity of a volume.    *    * @return float - fraction that represents used capacity.    */
annotation|@
name|JsonIgnore
DECL|method|computeEffectiveCapacity ()
specifier|public
name|long
name|computeEffectiveCapacity
parameter_list|()
block|{
return|return
name|getCapacity
argument_list|()
operator|-
name|getReserved
argument_list|()
return|;
block|}
comment|/**    * returns a Json String.    *    * @return String    *    * @throws IOException    */
DECL|method|toJson ()
specifier|public
name|String
name|toJson
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * returns if we should skip this volume.    * @return true / false    */
DECL|method|isSkip ()
specifier|public
name|boolean
name|isSkip
parameter_list|()
block|{
return|return
name|skip
return|;
block|}
comment|/**    * Sets the Skip value for this volume.    * @param skipValue bool    */
DECL|method|setSkip (boolean skipValue)
specifier|public
name|void
name|setSkip
parameter_list|(
name|boolean
name|skipValue
parameter_list|)
block|{
name|this
operator|.
name|skip
operator|=
name|skipValue
expr_stmt|;
block|}
comment|/**    * Returns the usedPercentage of a disk.    * This is useful in debugging disk usage    * @return float    */
DECL|method|computeUsedPercentage ()
specifier|public
name|float
name|computeUsedPercentage
parameter_list|()
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|getUsed
argument_list|()
argument_list|)
operator|/
call|(
name|float
call|)
argument_list|(
name|getCapacity
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Tells us if a volume is transient.    * @param transientValue    */
DECL|method|setIsTransient (boolean transientValue)
specifier|public
name|void
name|setIsTransient
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
comment|/**    * Tells us if this volume is read-only.    * @return true / false    */
DECL|method|isReadOnly ()
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|isReadOnly
return|;
block|}
comment|/**    * Sets this volume as read only.    * @param readOnly - boolean    */
DECL|method|setReadOnly (boolean readOnly)
specifier|public
name|void
name|setReadOnly
parameter_list|(
name|boolean
name|readOnly
parameter_list|)
block|{
name|isReadOnly
operator|=
name|readOnly
expr_stmt|;
block|}
block|}
end_class

end_unit

