begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|service
operator|.
name|AbstractService
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
name|service
operator|.
name|Service
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
name|service
operator|.
name|ServiceStateChangeListener
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
name|event
operator|.
name|EventHandler
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
name|api
operator|.
name|ApplicationInitializationContext
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
name|api
operator|.
name|ApplicationTerminationContext
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
name|api
operator|.
name|AuxiliaryService
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
name|api
operator|.
name|ContainerInitializationContext
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
name|api
operator|.
name|ContainerTerminationContext
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
DECL|class|AuxServices
specifier|public
class|class
name|AuxServices
extends|extends
name|AbstractService
implements|implements
name|ServiceStateChangeListener
implements|,
name|EventHandler
argument_list|<
name|AuxServicesEvent
argument_list|>
block|{
DECL|field|STATE_STORE_ROOT_NAME
specifier|static
specifier|final
name|String
name|STATE_STORE_ROOT_NAME
init|=
literal|"nm-aux-services"
decl_stmt|;
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
name|AuxServices
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serviceMap
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AuxiliaryService
argument_list|>
name|serviceMap
decl_stmt|;
DECL|field|serviceMetaData
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceMetaData
decl_stmt|;
DECL|field|p
specifier|private
specifier|final
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[A-Za-z_]+[A-Za-z0-9_]*$"
argument_list|)
decl_stmt|;
DECL|method|AuxServices ()
specifier|public
name|AuxServices
parameter_list|()
block|{
name|super
argument_list|(
name|AuxServices
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|serviceMap
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AuxiliaryService
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|serviceMetaData
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// Obtain services from configuration in init()
block|}
DECL|method|addService (String name, AuxiliaryService service)
specifier|protected
specifier|final
specifier|synchronized
name|void
name|addService
parameter_list|(
name|String
name|name
parameter_list|,
name|AuxiliaryService
name|service
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding auxiliary service "
operator|+
name|service
operator|.
name|getName
argument_list|()
operator|+
literal|", \""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|serviceMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
DECL|method|getServices ()
name|Collection
argument_list|<
name|AuxiliaryService
argument_list|>
name|getServices
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|serviceMap
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return the meta data for all registered services, that have been started.    * If a service has not been started no metadata will be available. The key    * is the name of the service as defined in the configuration.    */
DECL|method|getMetaData ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|getMetaData
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|metaClone
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|(
name|serviceMetaData
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|serviceMetaData
init|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|entry
range|:
name|serviceMetaData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|metaClone
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|duplicate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|metaClone
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|FsPermission
name|storeDirPerms
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
decl_stmt|;
name|Path
name|stateStoreRoot
init|=
literal|null
decl_stmt|;
name|FileSystem
name|stateStoreFs
init|=
literal|null
decl_stmt|;
name|boolean
name|recoveryEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RECOVERY_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_RECOVERY_ENABLED
argument_list|)
decl_stmt|;
if|if
condition|(
name|recoveryEnabled
condition|)
block|{
name|stateStoreRoot
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RECOVERY_DIR
argument_list|)
argument_list|,
name|STATE_STORE_ROOT_NAME
argument_list|)
expr_stmt|;
name|stateStoreFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|auxNames
init|=
name|conf
operator|.
name|getStringCollection
argument_list|(
name|YarnConfiguration
operator|.
name|NM_AUX_SERVICES
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|sName
range|:
name|auxNames
control|)
block|{
try|try
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|validateAuxServiceName
argument_list|(
name|sName
argument_list|)
argument_list|,
literal|"The ServiceName: "
operator|+
name|sName
operator|+
literal|" set in "
operator|+
name|YarnConfiguration
operator|.
name|NM_AUX_SERVICES
operator|+
literal|" is invalid."
operator|+
literal|"The valid service name should only contain a-zA-Z0-9_ "
operator|+
literal|"and can not start with numbers"
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|AuxiliaryService
argument_list|>
name|sClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|YarnConfiguration
operator|.
name|NM_AUX_SERVICE_FMT
argument_list|,
name|sName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|AuxiliaryService
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|sClass
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No class defined for "
operator|+
name|sName
argument_list|)
throw|;
block|}
name|AuxiliaryService
name|s
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|sClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// TODO better use s.getName()?
if|if
condition|(
operator|!
name|sName
operator|.
name|equals
argument_list|(
name|s
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The Auxilurary Service named '"
operator|+
name|sName
operator|+
literal|"' in the "
operator|+
literal|"configuration is for "
operator|+
name|sClass
operator|+
literal|" which has "
operator|+
literal|"a name of '"
operator|+
name|s
operator|.
name|getName
argument_list|()
operator|+
literal|"'. Because these are "
operator|+
literal|"not the same tools trying to send ServiceData and read "
operator|+
literal|"Service Meta Data may have issues unless the refer to "
operator|+
literal|"the name in the config."
argument_list|)
expr_stmt|;
block|}
name|addService
argument_list|(
name|sName
argument_list|,
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|recoveryEnabled
condition|)
block|{
name|Path
name|storePath
init|=
operator|new
name|Path
argument_list|(
name|stateStoreRoot
argument_list|,
name|sName
argument_list|)
decl_stmt|;
name|stateStoreFs
operator|.
name|mkdirs
argument_list|(
name|storePath
argument_list|,
name|storeDirPerms
argument_list|)
expr_stmt|;
name|s
operator|.
name|setRecoveryPath
argument_list|(
name|storePath
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Failed to initialize "
operator|+
name|sName
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO fork(?) services running as configured user
comment|//      monitor for health, shutdown/restart(?) if any should die
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AuxiliaryService
argument_list|>
name|entry
range|:
name|serviceMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|AuxiliaryService
name|service
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|registerServiceListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ByteBuffer
name|meta
init|=
name|service
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|serviceMetaData
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
synchronized|synchronized
init|(
name|serviceMap
init|)
block|{
for|for
control|(
name|Service
name|service
range|:
name|serviceMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|service
operator|.
name|getServiceState
argument_list|()
operator|==
name|Service
operator|.
name|STATE
operator|.
name|STARTED
condition|)
block|{
name|service
operator|.
name|unregisterServiceListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|serviceMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|serviceMetaData
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|stateChanged (Service service)
specifier|public
name|void
name|stateChanged
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Service "
operator|+
name|service
operator|.
name|getName
argument_list|()
operator|+
literal|" changed state: "
operator|+
name|service
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (AuxServicesEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|AuxServicesEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got event "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" for appId "
operator|+
name|event
operator|.
name|getApplicationID
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|APPLICATION_INIT
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Got APPLICATION_INIT for service "
operator|+
name|event
operator|.
name|getServiceID
argument_list|()
argument_list|)
expr_stmt|;
name|AuxiliaryService
name|service
init|=
literal|null
decl_stmt|;
try|try
block|{
name|service
operator|=
name|serviceMap
operator|.
name|get
argument_list|(
name|event
operator|.
name|getServiceID
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|initializeApplication
argument_list|(
operator|new
name|ApplicationInitializationContext
argument_list|(
name|event
operator|.
name|getUser
argument_list|()
argument_list|,
name|event
operator|.
name|getApplicationID
argument_list|()
argument_list|,
name|event
operator|.
name|getServiceData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|logWarningWhenAuxServiceThrowExceptions
argument_list|(
name|service
argument_list|,
name|AuxServicesEventType
operator|.
name|APPLICATION_INIT
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|APPLICATION_STOP
case|:
for|for
control|(
name|AuxiliaryService
name|serv
range|:
name|serviceMap
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|serv
operator|.
name|stopApplication
argument_list|(
operator|new
name|ApplicationTerminationContext
argument_list|(
name|event
operator|.
name|getApplicationID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|logWarningWhenAuxServiceThrowExceptions
argument_list|(
name|serv
argument_list|,
name|AuxServicesEventType
operator|.
name|APPLICATION_STOP
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|CONTAINER_INIT
case|:
for|for
control|(
name|AuxiliaryService
name|serv
range|:
name|serviceMap
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|serv
operator|.
name|initializeContainer
argument_list|(
operator|new
name|ContainerInitializationContext
argument_list|(
name|event
operator|.
name|getUser
argument_list|()
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|logWarningWhenAuxServiceThrowExceptions
argument_list|(
name|serv
argument_list|,
name|AuxServicesEventType
operator|.
name|CONTAINER_INIT
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|CONTAINER_STOP
case|:
for|for
control|(
name|AuxiliaryService
name|serv
range|:
name|serviceMap
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|serv
operator|.
name|stopContainer
argument_list|(
operator|new
name|ContainerTerminationContext
argument_list|(
name|event
operator|.
name|getUser
argument_list|()
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|logWarningWhenAuxServiceThrowExceptions
argument_list|(
name|serv
argument_list|,
name|AuxServicesEventType
operator|.
name|CONTAINER_STOP
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown type: "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|validateAuxServiceName (String name)
specifier|private
name|boolean
name|validateAuxServiceName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|p
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
DECL|method|logWarningWhenAuxServiceThrowExceptions (AuxiliaryService service, AuxServicesEventType eventType, Throwable th)
specifier|private
name|void
name|logWarningWhenAuxServiceThrowExceptions
parameter_list|(
name|AuxiliaryService
name|service
parameter_list|,
name|AuxServicesEventType
name|eventType
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
operator|(
literal|null
operator|==
name|service
condition|?
literal|"The auxService is null"
else|:
literal|"The auxService name is "
operator|+
name|service
operator|.
name|getName
argument_list|()
operator|)
operator|+
literal|" and it got an error at event: "
operator|+
name|eventType
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

