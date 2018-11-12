begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.volume.csi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|volume
operator|.
name|csi
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|volume
operator|.
name|csi
operator|.
name|lifecycle
operator|.
name|Volume
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
name|yarn
operator|.
name|server
operator|.
name|volume
operator|.
name|csi
operator|.
name|VolumeId
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Volume manager states, including all managed volumes and their states.  */
end_comment

begin_class
DECL|class|VolumeStates
specifier|public
class|class
name|VolumeStates
block|{
DECL|field|volumeStates
specifier|private
specifier|final
name|Map
argument_list|<
name|VolumeId
argument_list|,
name|Volume
argument_list|>
name|volumeStates
decl_stmt|;
DECL|method|VolumeStates ()
specifier|public
name|VolumeStates
parameter_list|()
block|{
name|this
operator|.
name|volumeStates
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|getVolume (VolumeId volumeId)
specifier|public
name|Volume
name|getVolume
parameter_list|(
name|VolumeId
name|volumeId
parameter_list|)
block|{
return|return
name|volumeStates
operator|.
name|get
argument_list|(
name|volumeId
argument_list|)
return|;
block|}
comment|/**    * Add volume if it is not yet added.    * If a new volume is added with a same {@link VolumeId}    * with a existing volume, existing volume will be returned.    * @param volume volume to add    * @return volume added or existing volume    */
DECL|method|addVolumeIfAbsent (Volume volume)
specifier|public
name|Volume
name|addVolumeIfAbsent
parameter_list|(
name|Volume
name|volume
parameter_list|)
block|{
if|if
condition|(
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|volumeStates
operator|.
name|putIfAbsent
argument_list|(
name|volume
operator|.
name|getVolumeId
argument_list|()
argument_list|,
name|volume
argument_list|)
return|;
block|}
else|else
block|{
comment|// for dynamical provisioned volumes,
comment|// the volume ID might not be available at time being.
comment|// we can makeup one with the combination of driver+volumeName+timestamp
comment|// once the volume ID is generated, we should replace ID.
return|return
name|volume
return|;
block|}
block|}
block|}
end_class

end_unit

