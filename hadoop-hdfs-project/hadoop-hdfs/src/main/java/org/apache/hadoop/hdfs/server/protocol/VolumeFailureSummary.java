begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
package|;
end_package

begin_comment
comment|/**  * Summarizes information about data volume failures on a DataNode.  */
end_comment

begin_class
DECL|class|VolumeFailureSummary
specifier|public
class|class
name|VolumeFailureSummary
block|{
DECL|field|failedStorageLocations
specifier|private
specifier|final
name|String
index|[]
name|failedStorageLocations
decl_stmt|;
DECL|field|lastVolumeFailureDate
specifier|private
specifier|final
name|long
name|lastVolumeFailureDate
decl_stmt|;
DECL|field|estimatedCapacityLostTotal
specifier|private
specifier|final
name|long
name|estimatedCapacityLostTotal
decl_stmt|;
comment|/**    * Creates a new VolumeFailureSummary.    *    * @param failedStorageLocations storage locations that have failed    * @param lastVolumeFailureDate date/time of last volume failure in    *     milliseconds since epoch    * @param estimatedCapacityLostTotal estimate of capacity lost in bytes    */
DECL|method|VolumeFailureSummary (String[] failedStorageLocations, long lastVolumeFailureDate, long estimatedCapacityLostTotal)
specifier|public
name|VolumeFailureSummary
parameter_list|(
name|String
index|[]
name|failedStorageLocations
parameter_list|,
name|long
name|lastVolumeFailureDate
parameter_list|,
name|long
name|estimatedCapacityLostTotal
parameter_list|)
block|{
name|this
operator|.
name|failedStorageLocations
operator|=
name|failedStorageLocations
expr_stmt|;
name|this
operator|.
name|lastVolumeFailureDate
operator|=
name|lastVolumeFailureDate
expr_stmt|;
name|this
operator|.
name|estimatedCapacityLostTotal
operator|=
name|estimatedCapacityLostTotal
expr_stmt|;
block|}
comment|/**    * Returns each storage location that has failed, sorted.    *    * @return each storage location that has failed, sorted    */
DECL|method|getFailedStorageLocations ()
specifier|public
name|String
index|[]
name|getFailedStorageLocations
parameter_list|()
block|{
return|return
name|this
operator|.
name|failedStorageLocations
return|;
block|}
comment|/**    * Returns the date/time of the last volume failure in milliseconds since    * epoch.    *    * @return date/time of last volume failure in milliseconds since epoch    */
DECL|method|getLastVolumeFailureDate ()
specifier|public
name|long
name|getLastVolumeFailureDate
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastVolumeFailureDate
return|;
block|}
comment|/**    * Returns estimate of capacity lost.  This is said to be an estimate, because    * in some cases it's impossible to know the capacity of the volume, such as if    * we never had a chance to query its capacity before the failure occurred.    *    * @return estimate of capacity lost in bytes    */
DECL|method|getEstimatedCapacityLostTotal ()
specifier|public
name|long
name|getEstimatedCapacityLostTotal
parameter_list|()
block|{
return|return
name|this
operator|.
name|estimatedCapacityLostTotal
return|;
block|}
block|}
end_class

end_unit

