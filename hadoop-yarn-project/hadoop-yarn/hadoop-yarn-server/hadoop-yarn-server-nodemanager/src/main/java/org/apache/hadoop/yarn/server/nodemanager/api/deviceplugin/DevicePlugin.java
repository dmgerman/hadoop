begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.deviceplugin
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
name|nodemanager
operator|.
name|api
operator|.
name|deviceplugin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A must interface for vendor plugin to implement.  * */
end_comment

begin_interface
DECL|interface|DevicePlugin
specifier|public
interface|interface
name|DevicePlugin
block|{
comment|/**    * Called first when device plugin framework wants to register.    * @return DeviceRegisterRequest {@link DeviceRegisterRequest}    * @throws Exception    * */
DECL|method|getRegisterRequestInfo ()
name|DeviceRegisterRequest
name|getRegisterRequestInfo
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Called when update node resource.    * @return a set of {@link Device}, {@link java.util.TreeSet} recommended    * @throws Exception    * */
DECL|method|getDevices ()
name|Set
argument_list|<
name|Device
argument_list|>
name|getDevices
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Asking how these devices should be prepared/used    * before/when container launch. A plugin can do some tasks in its own or    * define it in DeviceRuntimeSpec to let the framework do it.    * For instance, define {@code VolumeSpec} to let the    * framework to create volume before running container.    *    * @param allocatedDevices A set of allocated {@link Device}.    * @param yarnRuntime Indicate which runtime YARN will use    *        Could be {@code RUNTIME_DEFAULT} or {@code RUNTIME_DOCKER}    *        in {@link DeviceRuntimeSpec} constants. The default means YARN's    *        non-docker container runtime is used. The docker means YARN's    *        docker container runtime is used.    * @return a {@link DeviceRuntimeSpec} description about environment,    * {@link         VolumeSpec}, {@link MountVolumeSpec}. etc    * @throws Exception    * */
DECL|method|onDevicesAllocated (Set<Device> allocatedDevices, YarnRuntimeType yarnRuntime)
name|DeviceRuntimeSpec
name|onDevicesAllocated
parameter_list|(
name|Set
argument_list|<
name|Device
argument_list|>
name|allocatedDevices
parameter_list|,
name|YarnRuntimeType
name|yarnRuntime
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Called after device released.    * @param releasedDevices A set of released devices    * @throws Exception    * */
DECL|method|onDevicesReleased (Set<Device> releasedDevices)
name|void
name|onDevicesReleased
parameter_list|(
name|Set
argument_list|<
name|Device
argument_list|>
name|releasedDevices
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

