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
name|Map
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

begin_comment
comment|/**  * An optional interface to implement if custom device scheduling is needed.  * If this is not implemented, the device framework will do scheduling.  * */
end_comment

begin_interface
DECL|interface|DevicePluginScheduler
specifier|public
interface|interface
name|DevicePluginScheduler
block|{
comment|/**    * Called when allocating devices. The framework will do all device book    * keeping and fail recovery. So this hook could be stateless and only do    * scheduling based on available devices passed in. It could be    * invoked multiple times by the framework. The hint in environment variables    * passed in could be potentially used in making better scheduling decision.    * For instance, GPU scheduling might support different kind of policy. The    * container can set it through environment variables.    * @param availableDevices Devices allowed to be chosen from.    * @param count Number of device to be allocated.    * @param env Environment variables of the container.    * @return A set of {@link Device} allocated    * */
DECL|method|allocateDevices (Set<Device> availableDevices, int count, Map<String, String> env)
name|Set
argument_list|<
name|Device
argument_list|>
name|allocateDevices
parameter_list|(
name|Set
argument_list|<
name|Device
argument_list|>
name|availableDevices
parameter_list|,
name|int
name|count
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

