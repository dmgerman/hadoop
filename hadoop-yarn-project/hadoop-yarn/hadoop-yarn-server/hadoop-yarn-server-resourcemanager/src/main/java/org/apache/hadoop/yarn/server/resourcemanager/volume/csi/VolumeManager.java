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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|api
operator|.
name|CsiAdaptorProtocol
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
name|resourcemanager
operator|.
name|volume
operator|.
name|csi
operator|.
name|provisioner
operator|.
name|VolumeProvisioningResults
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
name|resourcemanager
operator|.
name|volume
operator|.
name|csi
operator|.
name|provisioner
operator|.
name|VolumeProvisioningTask
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
name|ScheduledFuture
import|;
end_import

begin_comment
comment|/**  * Main interface for volume manager that manages all volumes.  * Volume manager talks to a CSI controller plugin to handle the  * volume operations before it is available to be published on  * any node manager.  */
end_comment

begin_interface
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|VolumeManager
specifier|public
interface|interface
name|VolumeManager
block|{
comment|/**    * @return all known volumes and their states.    */
DECL|method|getVolumeStates ()
name|VolumeStates
name|getVolumeStates
parameter_list|()
function_decl|;
comment|/**    * Start to supervise on a volume.    * @param volume    * @return the volume being managed by the manager.    */
DECL|method|addOrGetVolume (Volume volume)
name|Volume
name|addOrGetVolume
parameter_list|(
name|Volume
name|volume
parameter_list|)
function_decl|;
comment|/**    * Execute volume provisioning tasks as backend threads.    * @param volumeProvisioningTask    * @param delaySecond    */
DECL|method|schedule ( VolumeProvisioningTask volumeProvisioningTask, int delaySecond)
name|ScheduledFuture
argument_list|<
name|VolumeProvisioningResults
argument_list|>
name|schedule
parameter_list|(
name|VolumeProvisioningTask
name|volumeProvisioningTask
parameter_list|,
name|int
name|delaySecond
parameter_list|)
function_decl|;
comment|/**    * Register a csi-driver-adaptor to the volume manager.    * @param driverName    * @param client    */
DECL|method|registerCsiDriverAdaptor (String driverName, CsiAdaptorProtocol client)
name|void
name|registerCsiDriverAdaptor
parameter_list|(
name|String
name|driverName
parameter_list|,
name|CsiAdaptorProtocol
name|client
parameter_list|)
function_decl|;
comment|/**    * Returns the csi-driver-adaptor client from cache by the given driver name.    * If the client is not found, null is returned.    * @param driverName    * @return a csi-driver-adaptor client working for given driver or null    * if the adaptor could not be found.    */
DECL|method|getAdaptorByDriverName (String driverName)
name|CsiAdaptorProtocol
name|getAdaptorByDriverName
parameter_list|(
name|String
name|driverName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

