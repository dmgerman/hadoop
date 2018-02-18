begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|lang3
operator|.
name|StringUtils
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
name|Context
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
name|fpga
operator|.
name|FpgaResourcePlugin
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
name|gpu
operator|.
name|GpuResourcePlugin
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Set
import|;
end_import

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
name|GPU_URI
import|;
end_import

begin_comment
comment|/**  * Manages {@link ResourcePlugin} configured on this NodeManager.  */
end_comment

begin_class
DECL|class|ResourcePluginManager
specifier|public
class|class
name|ResourcePluginManager
block|{
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
name|ResourcePluginManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SUPPORTED_RESOURCE_PLUGINS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|SUPPORTED_RESOURCE_PLUGINS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|GPU_URI
argument_list|,
name|FPGA_URI
argument_list|)
decl_stmt|;
DECL|field|configuredPlugins
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ResourcePlugin
argument_list|>
name|configuredPlugins
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|method|initialize (Context context)
specifier|public
specifier|synchronized
name|void
name|initialize
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|YarnException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|String
index|[]
name|plugins
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RESOURCE_PLUGINS
argument_list|)
decl_stmt|;
if|if
condition|(
name|plugins
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ResourcePlugin
argument_list|>
name|pluginMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Initialize each plugins
for|for
control|(
name|String
name|resourceName
range|:
name|plugins
control|)
block|{
name|resourceName
operator|=
name|resourceName
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|SUPPORTED_RESOURCE_PLUGINS
operator|.
name|contains
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Trying to initialize resource plugin with name="
operator|+
name|resourceName
operator|+
literal|", it is not supported, list of supported plugins:"
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|SUPPORTED_RESOURCE_PLUGINS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
name|pluginMap
operator|.
name|containsKey
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
comment|// Duplicated items, ignore ...
continue|continue;
block|}
name|ResourcePlugin
name|plugin
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|GPU_URI
argument_list|)
condition|)
block|{
name|plugin
operator|=
operator|new
name|GpuResourcePlugin
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|FPGA_URI
argument_list|)
condition|)
block|{
name|plugin
operator|=
operator|new
name|FpgaResourcePlugin
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|plugin
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"This shouldn't happen, plugin="
operator|+
name|resourceName
operator|+
literal|" should be loaded and initialized"
argument_list|)
throw|;
block|}
name|plugin
operator|.
name|initialize
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|pluginMap
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
block|}
name|configuredPlugins
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|pluginMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cleanup ()
specifier|public
specifier|synchronized
name|void
name|cleanup
parameter_list|()
throws|throws
name|YarnException
block|{
for|for
control|(
name|ResourcePlugin
name|plugin
range|:
name|configuredPlugins
operator|.
name|values
argument_list|()
control|)
block|{
name|plugin
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get resource name (such as gpu/fpga) to plugin references.    * @return read-only map of resource name to plugins.    */
DECL|method|getNameToPlugins ()
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|ResourcePlugin
argument_list|>
name|getNameToPlugins
parameter_list|()
block|{
return|return
name|configuredPlugins
return|;
block|}
block|}
end_class

end_unit

