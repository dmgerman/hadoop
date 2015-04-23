begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|linux
operator|.
name|resources
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
DECL|class|TestResourceHandlerModule
specifier|public
class|class
name|TestResourceHandlerModule
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestResourceHandlerModule
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|emptyConf
name|Configuration
name|emptyConf
decl_stmt|;
DECL|field|networkEnabledConf
name|Configuration
name|networkEnabledConf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|emptyConf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|networkEnabledConf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|networkEnabledConf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NETWORK_RESOURCE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//We need to bypass mtab parsing for figuring out cgroups mount locations
name|networkEnabledConf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_MOUNT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOutboundBandwidthHandler ()
specifier|public
name|void
name|testOutboundBandwidthHandler
parameter_list|()
block|{
try|try
block|{
comment|//This resourceHandler should be non-null only if network as a resource
comment|//is explicitly enabled
name|OutboundBandwidthResourceHandler
name|resourceHandler
init|=
name|ResourceHandlerModule
operator|.
name|getOutboundBandwidthResourceHandler
argument_list|(
name|emptyConf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|resourceHandler
argument_list|)
expr_stmt|;
comment|//When network as a resource is enabled this should be non-null
name|resourceHandler
operator|=
name|ResourceHandlerModule
operator|.
name|getOutboundBandwidthResourceHandler
argument_list|(
name|networkEnabledConf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|resourceHandler
argument_list|)
expr_stmt|;
comment|//Ensure that outbound bandwidth resource handler is present in the chain
name|ResourceHandlerChain
name|resourceHandlerChain
init|=
name|ResourceHandlerModule
operator|.
name|getConfiguredResourceHandlerChain
argument_list|(
name|networkEnabledConf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ResourceHandler
argument_list|>
name|resourceHandlers
init|=
name|resourceHandlerChain
operator|.
name|getResourceHandlerList
argument_list|()
decl_stmt|;
comment|//Exactly one resource handler in chain
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourceHandlers
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//Same instance is expected to be in the chain.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|resourceHandlers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|resourceHandler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Unexpected ResourceHandlerException: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

