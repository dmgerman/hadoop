begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.fpga
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
name|containermanager
operator|.
name|resourceplugin
operator|.
name|fpga
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
name|conf
operator|.
name|Configurable
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|Path
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|resources
operator|.
name|fpga
operator|.
name|FpgaResourceAllocator
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
name|Map
import|;
end_import

begin_comment
comment|/**  * FPGA plugin interface for vendor to implement. Used by {@link FpgaDiscoverer} and  * {@link org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.fpga.FpgaResourceHandlerImpl}  * to discover devices/download IP/configure IP  * */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|AbstractFpgaVendorPlugin
specifier|public
interface|interface
name|AbstractFpgaVendorPlugin
extends|extends
name|Configurable
block|{
comment|/**    * Check vendor's toolchain and required environment    * */
DECL|method|initPlugin (Configuration conf)
name|boolean
name|initPlugin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Diagnose the devices using vendor toolchain but no need to parse device information    * */
DECL|method|diagnose (int timeout)
name|boolean
name|diagnose
parameter_list|(
name|int
name|timeout
parameter_list|)
function_decl|;
comment|/**    * Discover the vendor's FPGA devices with execution time constraint    * @param timeout The vendor plugin should return result during this time    * @return The result will be added to FPGAResourceAllocator for later scheduling    * */
DECL|method|discover (int timeout)
name|List
argument_list|<
name|FpgaResourceAllocator
operator|.
name|FpgaDevice
argument_list|>
name|discover
parameter_list|(
name|int
name|timeout
parameter_list|)
function_decl|;
comment|/**    * Since all vendor plugins share a {@link org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.fpga.FpgaResourceAllocator}    * which distinguish FPGA devices by type. Vendor plugin must report this.    * */
DECL|method|getFpgaType ()
name|String
name|getFpgaType
parameter_list|()
function_decl|;
comment|/**    * The vendor plugin download required IP files to a required directory.    * It should check if the IP file has already been downloaded.    * @param id The identifier for IP file. Comes from application, ie. matrix_multi_v1    * @param dstDir The plugin should download IP file to this directory    * @param localizedResources The container localized resource can be searched for IP file. Key is    * localized file path and value is soft link names    * @return The absolute path string of IP file    * */
DECL|method|downloadIP (String id, String dstDir, Map<Path, List<String>> localizedResources)
name|String
name|downloadIP
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|dstDir
parameter_list|,
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|localizedResources
parameter_list|)
function_decl|;
comment|/**    * The vendor plugin configure an IP file to a device    * @param ipPath The absolute path of the IP file    * @param majorMinorNumber The device in format&lt;major:minor&gt;    * @return configure device ok or not    * */
DECL|method|configureIP (String ipPath, String majorMinorNumber)
name|boolean
name|configureIP
parameter_list|(
name|String
name|ipPath
parameter_list|,
name|String
name|majorMinorNumber
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

