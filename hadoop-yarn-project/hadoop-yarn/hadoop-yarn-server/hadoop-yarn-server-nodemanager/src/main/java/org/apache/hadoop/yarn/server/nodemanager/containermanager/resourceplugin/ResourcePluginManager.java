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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

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
name|util
operator|.
name|ReflectionUtils
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|api
operator|.
name|deviceplugin
operator|.
name|DevicePlugin
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
name|api
operator|.
name|deviceplugin
operator|.
name|DevicePluginScheduler
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
name|api
operator|.
name|deviceplugin
operator|.
name|DeviceRegisterRequest
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
name|deviceframework
operator|.
name|DeviceMappingManager
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
name|deviceframework
operator|.
name|DevicePluginAdapter
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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
DECL|field|deviceMappingManager
specifier|private
name|DeviceMappingManager
name|deviceMappingManager
init|=
literal|null
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
throws|,
name|ClassNotFoundException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConf
argument_list|()
decl_stmt|;
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
operator|==
literal|null
operator|||
name|plugins
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No Resource plugins found from configuration!"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Found Resource plugins from configuration: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|plugins
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|plugins
operator|!=
literal|null
condition|)
block|{
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring duplicate Resource plugin definition: "
operator|+
name|resourceName
argument_list|)
expr_stmt|;
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
elseif|else
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized plugin {}"
argument_list|,
name|plugin
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
block|}
comment|// Try to load pluggable device plugins
name|boolean
name|puggableDeviceFrameworkEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED
argument_list|)
decl_stmt|;
if|if
condition|(
name|puggableDeviceFrameworkEnabled
condition|)
block|{
name|initializePluggableDevicePlugins
argument_list|(
name|context
argument_list|,
name|conf
argument_list|,
name|pluginMap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The pluggable device framework is not enabled."
operator|+
literal|" If you want, please set true to {}"
argument_list|,
name|YarnConfiguration
operator|.
name|NM_PLUGGABLE_DEVICE_FRAMEWORK_ENABLED
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
DECL|method|initializePluggableDevicePlugins (Context context, Configuration configuration, Map<String, ResourcePlugin> pluginMap)
specifier|public
name|void
name|initializePluggableDevicePlugins
parameter_list|(
name|Context
name|context
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ResourcePlugin
argument_list|>
name|pluginMap
parameter_list|)
throws|throws
name|YarnRuntimeException
throws|,
name|ClassNotFoundException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The pluggable device framework enabled,"
operator|+
literal|"trying to load the vendor plugins"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|deviceMappingManager
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"DeviceMappingManager initialized."
argument_list|)
expr_stmt|;
name|deviceMappingManager
operator|=
operator|new
name|DeviceMappingManager
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|pluginClassNames
init|=
name|configuration
operator|.
name|getStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|pluginClassNames
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Null value found in configuration: "
operator|+
name|YarnConfiguration
operator|.
name|NM_PLUGGABLE_DEVICE_FRAMEWORK_DEVICE_CLASSES
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|pluginClassName
range|:
name|pluginClassNames
control|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|pluginClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|pluginClassName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|DevicePlugin
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|pluginClazz
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class: "
operator|+
name|pluginClassName
operator|+
literal|" not instance of "
operator|+
name|DevicePlugin
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
comment|// sanity-check before initialization
name|checkInterfaceCompatibility
argument_list|(
name|DevicePlugin
operator|.
name|class
argument_list|,
name|pluginClazz
argument_list|)
expr_stmt|;
name|DevicePlugin
name|dpInstance
init|=
operator|(
name|DevicePlugin
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|pluginClazz
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
comment|// Try to register plugin
comment|// TODO: handle the plugin method timeout issue
name|DeviceRegisterRequest
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|dpInstance
operator|.
name|getRegisterRequestInfo
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Exception thrown from plugin's"
operator|+
literal|" getRegisterRequestInfo:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|resourceName
init|=
name|request
operator|.
name|getResourceName
argument_list|()
decl_stmt|;
comment|// check if someone has already registered this resource type name
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
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|resourceName
operator|+
literal|" already registered! Please change resource type name"
operator|+
literal|" or configure correct resource type name"
operator|+
literal|" in resource-types.xml for "
operator|+
name|pluginClassName
argument_list|)
throw|;
block|}
comment|// check resource name is valid and configured in resource-types.xml
if|if
condition|(
operator|!
name|isConfiguredResourceName
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|resourceName
operator|+
literal|" is not configured inside "
operator|+
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES_CONFIGURATION_FILE
operator|+
literal|" , please configure it first"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"New resource type: {} registered successfully by {}"
argument_list|,
name|resourceName
argument_list|,
name|pluginClassName
argument_list|)
expr_stmt|;
name|DevicePluginAdapter
name|pluginAdapter
init|=
operator|new
name|DevicePluginAdapter
argument_list|(
name|resourceName
argument_list|,
name|dpInstance
argument_list|,
name|deviceMappingManager
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adapter of {} created. Initializing.."
argument_list|,
name|pluginClassName
argument_list|)
expr_stmt|;
try|try
block|{
name|pluginAdapter
operator|.
name|initialize
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Adapter of "
operator|+
name|pluginClassName
operator|+
literal|" init failed!"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Adapter of {} init success!"
argument_list|,
name|pluginClassName
argument_list|)
expr_stmt|;
comment|// Store plugin as adapter instance
name|pluginMap
operator|.
name|put
argument_list|(
name|request
operator|.
name|getResourceName
argument_list|()
argument_list|,
name|pluginAdapter
argument_list|)
expr_stmt|;
comment|// If the device plugin implements DevicePluginScheduler interface
if|if
condition|(
name|dpInstance
operator|instanceof
name|DevicePluginScheduler
condition|)
block|{
comment|// check DevicePluginScheduler interface compatibility
name|checkInterfaceCompatibility
argument_list|(
name|DevicePluginScheduler
operator|.
name|class
argument_list|,
name|pluginClazz
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{} can schedule {} devices."
operator|+
literal|"Added as preferred device plugin scheduler"
argument_list|,
name|pluginClassName
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
name|deviceMappingManager
operator|.
name|addDevicePluginScheduler
argument_list|(
name|resourceName
argument_list|,
operator|(
name|DevicePluginScheduler
operator|)
name|dpInstance
argument_list|)
expr_stmt|;
block|}
block|}
comment|// end for
block|}
annotation|@
name|VisibleForTesting
comment|// Check if the implemented interfaces' signature is compatible
DECL|method|checkInterfaceCompatibility (Class<?> expectedClass, Class<?> actualClass)
specifier|public
name|void
name|checkInterfaceCompatibility
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|expectedClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|actualClass
parameter_list|)
throws|throws
name|YarnRuntimeException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Checking implemented interface's compatibility: {}"
argument_list|,
name|expectedClass
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Method
index|[]
name|expectedDevicePluginMethods
init|=
name|expectedClass
operator|.
name|getMethods
argument_list|()
decl_stmt|;
comment|// Check method compatibility
name|boolean
name|found
decl_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|expectedDevicePluginMethods
control|)
block|{
name|found
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Try to find method: {}"
argument_list|,
name|method
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Method
name|m
range|:
name|actualClass
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Method {} found in class {}"
argument_list|,
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|actualClass
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Method {} is not found in plugin"
argument_list|,
name|method
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Method "
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|" is expected but not implemented in "
operator|+
name|actualClass
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// end for
name|LOG
operator|.
name|info
argument_list|(
literal|"{} compatibility is ok."
argument_list|,
name|expectedClass
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|isConfiguredResourceName (String resourceName)
specifier|public
name|boolean
name|isConfiguredResourceName
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
comment|// check configured
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
name|resourceName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setDeviceMappingManager ( DeviceMappingManager deviceMappingManager)
specifier|public
name|void
name|setDeviceMappingManager
parameter_list|(
name|DeviceMappingManager
name|deviceMappingManager
parameter_list|)
block|{
name|this
operator|.
name|deviceMappingManager
operator|=
name|deviceMappingManager
expr_stmt|;
block|}
DECL|method|getDeviceMappingManager ()
specifier|public
name|DeviceMappingManager
name|getDeviceMappingManager
parameter_list|()
block|{
return|return
name|deviceMappingManager
return|;
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

