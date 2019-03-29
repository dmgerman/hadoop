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
import|import static
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
name|records
operator|.
name|ResourceInformation
operator|.
name|FPGA_URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|records
operator|.
name|Resource
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
name|records
operator|.
name|ResourceInformation
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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnException
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
operator|.
name|FpgaDevice
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
name|resourceplugin
operator|.
name|NodeResourceUpdaterPlugin
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
name|util
operator|.
name|resource
operator|.
name|ResourceUtils
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

begin_class
DECL|class|FpgaNodeResourceUpdateHandler
specifier|public
class|class
name|FpgaNodeResourceUpdateHandler
extends|extends
name|NodeResourceUpdaterPlugin
block|{
DECL|field|fpgaDiscoverer
specifier|private
specifier|final
name|FpgaDiscoverer
name|fpgaDiscoverer
decl_stmt|;
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
name|FpgaNodeResourceUpdateHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FpgaNodeResourceUpdateHandler (FpgaDiscoverer fpgaDiscoverer)
specifier|public
name|FpgaNodeResourceUpdateHandler
parameter_list|(
name|FpgaDiscoverer
name|fpgaDiscoverer
parameter_list|)
block|{
name|this
operator|.
name|fpgaDiscoverer
operator|=
name|fpgaDiscoverer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateConfiguredResource (Resource res)
specifier|public
name|void
name|updateConfiguredResource
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|YarnException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing configured FPGA resources for the NodeManager."
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FpgaDevice
argument_list|>
name|list
init|=
name|fpgaDiscoverer
operator|.
name|getCurrentFpgaInfo
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|minors
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FpgaDevice
name|device
range|:
name|list
control|)
block|{
name|minors
operator|.
name|add
argument_list|(
name|device
operator|.
name|getMinor
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Didn't find any usable FPGAs on the NodeManager."
argument_list|)
expr_stmt|;
return|return;
block|}
name|long
name|count
init|=
name|minors
operator|.
name|size
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|configuredResourceTypes
init|=
name|ResourceUtils
operator|.
name|getResourceTypes
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|configuredResourceTypes
operator|.
name|containsKey
argument_list|(
name|FPGA_URI
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Wrong configurations, found "
operator|+
name|count
operator|+
literal|" usable FPGAs, however "
operator|+
name|FPGA_URI
operator|+
literal|" resource-type is not configured inside"
operator|+
literal|" resource-types.xml, please configure it to enable FPGA feature or"
operator|+
literal|" remove "
operator|+
name|FPGA_URI
operator|+
literal|" from "
operator|+
name|YarnConfiguration
operator|.
name|NM_RESOURCE_PLUGINS
argument_list|)
throw|;
block|}
name|res
operator|.
name|setResourceValue
argument_list|(
name|FPGA_URI
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

