begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
name|datanode
operator|.
name|fsdataset
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|StorageLocation
import|;
end_import

begin_comment
comment|/**  * Tracks information about failure of a data volume.  */
end_comment

begin_class
DECL|class|VolumeFailureInfo
specifier|final
class|class
name|VolumeFailureInfo
block|{
DECL|field|failedStorageLocation
specifier|private
specifier|final
name|StorageLocation
name|failedStorageLocation
decl_stmt|;
DECL|field|failureDate
specifier|private
specifier|final
name|long
name|failureDate
decl_stmt|;
DECL|field|estimatedCapacityLost
specifier|private
specifier|final
name|long
name|estimatedCapacityLost
decl_stmt|;
comment|/**    * Creates a new VolumeFailureInfo, when the capacity lost from this volume    * failure is unknown.  Typically, this means the volume failed immediately at    * startup, so there was never a chance to query its capacity.    *    * @param failedStorageLocation storage location that has failed    * @param failureDate date/time of failure in milliseconds since epoch    */
DECL|method|VolumeFailureInfo (StorageLocation failedStorageLocation, long failureDate)
specifier|public
name|VolumeFailureInfo
parameter_list|(
name|StorageLocation
name|failedStorageLocation
parameter_list|,
name|long
name|failureDate
parameter_list|)
block|{
name|this
argument_list|(
name|failedStorageLocation
argument_list|,
name|failureDate
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new VolumeFailureInfo.    *    * @param failedStorageLocation storage location that has failed    * @param failureDate date/time of failure in milliseconds since epoch    * @param estimatedCapacityLost estimate of capacity lost in bytes    */
DECL|method|VolumeFailureInfo (StorageLocation failedStorageLocation, long failureDate, long estimatedCapacityLost)
specifier|public
name|VolumeFailureInfo
parameter_list|(
name|StorageLocation
name|failedStorageLocation
parameter_list|,
name|long
name|failureDate
parameter_list|,
name|long
name|estimatedCapacityLost
parameter_list|)
block|{
name|this
operator|.
name|failedStorageLocation
operator|=
name|failedStorageLocation
expr_stmt|;
name|this
operator|.
name|failureDate
operator|=
name|failureDate
expr_stmt|;
name|this
operator|.
name|estimatedCapacityLost
operator|=
name|estimatedCapacityLost
expr_stmt|;
block|}
comment|/**    * Returns the storage location that has failed.    *    * @return storage location that has failed    */
DECL|method|getFailedStorageLocation ()
specifier|public
name|StorageLocation
name|getFailedStorageLocation
parameter_list|()
block|{
return|return
name|this
operator|.
name|failedStorageLocation
return|;
block|}
comment|/**    * Returns date/time of failure    *    * @return date/time of failure in milliseconds since epoch    */
DECL|method|getFailureDate ()
specifier|public
name|long
name|getFailureDate
parameter_list|()
block|{
return|return
name|this
operator|.
name|failureDate
return|;
block|}
comment|/**    * Returns estimate of capacity lost.  This is said to be an estimate, because    * in some cases it's impossible to know the capacity of the volume, such as if    * we never had a chance to query its capacity before the failure occurred.    *    * @return estimate of capacity lost in bytes    */
DECL|method|getEstimatedCapacityLost ()
specifier|public
name|long
name|getEstimatedCapacityLost
parameter_list|()
block|{
return|return
name|this
operator|.
name|estimatedCapacityLost
return|;
block|}
block|}
end_class

end_unit

