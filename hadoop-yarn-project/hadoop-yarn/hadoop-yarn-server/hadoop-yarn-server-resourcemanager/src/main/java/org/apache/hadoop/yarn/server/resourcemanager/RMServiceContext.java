begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ConfigurationProvider
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
name|Dispatcher
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
name|ahs
operator|.
name|RMApplicationHistoryWriter
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
name|metrics
operator|.
name|SystemMetricsPublisher
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
name|timelineservice
operator|.
name|RMTimelineCollectorManager
import|;
end_import

begin_comment
comment|/**  * RMServiceContext class maintains "Always On" services. Services that need to  * run always irrespective of the HA state of the RM. This is created during  * initialization of RMContextImpl.  *<p>  *<b>Note:</b> If any services to be added in this class, make sure service  * will be running always irrespective of the HA state of the RM  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RMServiceContext
specifier|public
class|class
name|RMServiceContext
block|{
DECL|field|rmDispatcher
specifier|private
name|Dispatcher
name|rmDispatcher
decl_stmt|;
DECL|field|isHAEnabled
specifier|private
name|boolean
name|isHAEnabled
decl_stmt|;
DECL|field|haServiceState
specifier|private
name|HAServiceState
name|haServiceState
init|=
name|HAServiceProtocol
operator|.
name|HAServiceState
operator|.
name|INITIALIZING
decl_stmt|;
DECL|field|adminService
specifier|private
name|AdminService
name|adminService
decl_stmt|;
DECL|field|configurationProvider
specifier|private
name|ConfigurationProvider
name|configurationProvider
decl_stmt|;
DECL|field|yarnConfiguration
specifier|private
name|Configuration
name|yarnConfiguration
decl_stmt|;
DECL|field|rmApplicationHistoryWriter
specifier|private
name|RMApplicationHistoryWriter
name|rmApplicationHistoryWriter
decl_stmt|;
DECL|field|systemMetricsPublisher
specifier|private
name|SystemMetricsPublisher
name|systemMetricsPublisher
decl_stmt|;
DECL|field|elector
specifier|private
name|EmbeddedElector
name|elector
decl_stmt|;
DECL|field|haServiceStateLock
specifier|private
specifier|final
name|Object
name|haServiceStateLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|resourceManager
specifier|private
name|ResourceManager
name|resourceManager
decl_stmt|;
DECL|field|timelineCollectorManager
specifier|private
name|RMTimelineCollectorManager
name|timelineCollectorManager
decl_stmt|;
DECL|method|getResourceManager ()
specifier|public
name|ResourceManager
name|getResourceManager
parameter_list|()
block|{
return|return
name|resourceManager
return|;
block|}
DECL|method|setResourceManager (ResourceManager rm)
specifier|public
name|void
name|setResourceManager
parameter_list|(
name|ResourceManager
name|rm
parameter_list|)
block|{
name|this
operator|.
name|resourceManager
operator|=
name|rm
expr_stmt|;
block|}
DECL|method|getConfigurationProvider ()
specifier|public
name|ConfigurationProvider
name|getConfigurationProvider
parameter_list|()
block|{
return|return
name|this
operator|.
name|configurationProvider
return|;
block|}
DECL|method|setConfigurationProvider ( ConfigurationProvider configurationProvider)
specifier|public
name|void
name|setConfigurationProvider
parameter_list|(
name|ConfigurationProvider
name|configurationProvider
parameter_list|)
block|{
name|this
operator|.
name|configurationProvider
operator|=
name|configurationProvider
expr_stmt|;
block|}
DECL|method|getDispatcher ()
specifier|public
name|Dispatcher
name|getDispatcher
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmDispatcher
return|;
block|}
DECL|method|setDispatcher (Dispatcher dispatcher)
name|void
name|setDispatcher
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|)
block|{
name|this
operator|.
name|rmDispatcher
operator|=
name|dispatcher
expr_stmt|;
block|}
DECL|method|getLeaderElectorService ()
specifier|public
name|EmbeddedElector
name|getLeaderElectorService
parameter_list|()
block|{
return|return
name|this
operator|.
name|elector
return|;
block|}
DECL|method|setLeaderElectorService (EmbeddedElector embeddedElector)
specifier|public
name|void
name|setLeaderElectorService
parameter_list|(
name|EmbeddedElector
name|embeddedElector
parameter_list|)
block|{
name|this
operator|.
name|elector
operator|=
name|embeddedElector
expr_stmt|;
block|}
DECL|method|getRMAdminService ()
specifier|public
name|AdminService
name|getRMAdminService
parameter_list|()
block|{
return|return
name|this
operator|.
name|adminService
return|;
block|}
DECL|method|setRMAdminService (AdminService service)
name|void
name|setRMAdminService
parameter_list|(
name|AdminService
name|service
parameter_list|)
block|{
name|this
operator|.
name|adminService
operator|=
name|service
expr_stmt|;
block|}
DECL|method|setHAEnabled (boolean rmHAEnabled)
name|void
name|setHAEnabled
parameter_list|(
name|boolean
name|rmHAEnabled
parameter_list|)
block|{
name|this
operator|.
name|isHAEnabled
operator|=
name|rmHAEnabled
expr_stmt|;
block|}
DECL|method|isHAEnabled ()
specifier|public
name|boolean
name|isHAEnabled
parameter_list|()
block|{
return|return
name|isHAEnabled
return|;
block|}
DECL|method|getHAServiceState ()
specifier|public
name|HAServiceState
name|getHAServiceState
parameter_list|()
block|{
synchronized|synchronized
init|(
name|haServiceStateLock
init|)
block|{
return|return
name|haServiceState
return|;
block|}
block|}
DECL|method|setHAServiceState (HAServiceState serviceState)
name|void
name|setHAServiceState
parameter_list|(
name|HAServiceState
name|serviceState
parameter_list|)
block|{
synchronized|synchronized
init|(
name|haServiceStateLock
init|)
block|{
name|this
operator|.
name|haServiceState
operator|=
name|serviceState
expr_stmt|;
block|}
block|}
DECL|method|getRMApplicationHistoryWriter ()
specifier|public
name|RMApplicationHistoryWriter
name|getRMApplicationHistoryWriter
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmApplicationHistoryWriter
return|;
block|}
DECL|method|setRMApplicationHistoryWriter ( RMApplicationHistoryWriter applicationHistoryWriter)
specifier|public
name|void
name|setRMApplicationHistoryWriter
parameter_list|(
name|RMApplicationHistoryWriter
name|applicationHistoryWriter
parameter_list|)
block|{
name|this
operator|.
name|rmApplicationHistoryWriter
operator|=
name|applicationHistoryWriter
expr_stmt|;
block|}
DECL|method|setSystemMetricsPublisher ( SystemMetricsPublisher metricsPublisher)
specifier|public
name|void
name|setSystemMetricsPublisher
parameter_list|(
name|SystemMetricsPublisher
name|metricsPublisher
parameter_list|)
block|{
name|this
operator|.
name|systemMetricsPublisher
operator|=
name|metricsPublisher
expr_stmt|;
block|}
DECL|method|getSystemMetricsPublisher ()
specifier|public
name|SystemMetricsPublisher
name|getSystemMetricsPublisher
parameter_list|()
block|{
return|return
name|this
operator|.
name|systemMetricsPublisher
return|;
block|}
DECL|method|getYarnConfiguration ()
specifier|public
name|Configuration
name|getYarnConfiguration
parameter_list|()
block|{
return|return
name|this
operator|.
name|yarnConfiguration
return|;
block|}
DECL|method|setYarnConfiguration (Configuration yarnConfiguration)
specifier|public
name|void
name|setYarnConfiguration
parameter_list|(
name|Configuration
name|yarnConfiguration
parameter_list|)
block|{
name|this
operator|.
name|yarnConfiguration
operator|=
name|yarnConfiguration
expr_stmt|;
block|}
DECL|method|getRMTimelineCollectorManager ()
specifier|public
name|RMTimelineCollectorManager
name|getRMTimelineCollectorManager
parameter_list|()
block|{
return|return
name|timelineCollectorManager
return|;
block|}
DECL|method|setRMTimelineCollectorManager ( RMTimelineCollectorManager collectorManager)
specifier|public
name|void
name|setRMTimelineCollectorManager
parameter_list|(
name|RMTimelineCollectorManager
name|collectorManager
parameter_list|)
block|{
name|this
operator|.
name|timelineCollectorManager
operator|=
name|collectorManager
expr_stmt|;
block|}
DECL|method|getHAZookeeperConnectionState ()
specifier|public
name|String
name|getHAZookeeperConnectionState
parameter_list|()
block|{
if|if
condition|(
name|elector
operator|==
literal|null
condition|)
block|{
return|return
literal|"Could not find leader elector. Verify both HA and automatic "
operator|+
literal|"failover are enabled."
return|;
block|}
else|else
block|{
return|return
name|elector
operator|.
name|getZookeeperConnectionState
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

