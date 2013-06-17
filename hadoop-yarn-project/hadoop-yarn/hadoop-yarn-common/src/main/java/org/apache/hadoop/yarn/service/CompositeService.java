begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|Evolving
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

begin_comment
comment|/**  * Composition of services.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|CompositeService
specifier|public
class|class
name|CompositeService
extends|extends
name|AbstractService
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
name|CompositeService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Policy on shutdown: attempt to close everything (purest) or    * only try to close started services (which assumes    * that the service implementations may not handle the stop() operation    * except when started.    * Irrespective of this policy, if a child service fails during    * its init() or start() operations, it will have stop() called on it.    */
DECL|field|STOP_ONLY_STARTED_SERVICES
specifier|protected
specifier|static
specifier|final
name|boolean
name|STOP_ONLY_STARTED_SERVICES
init|=
literal|false
decl_stmt|;
DECL|field|serviceList
specifier|private
specifier|final
name|List
argument_list|<
name|Service
argument_list|>
name|serviceList
init|=
operator|new
name|ArrayList
argument_list|<
name|Service
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|CompositeService (String name)
specifier|public
name|CompositeService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get an unmodifiable list of services    * @return a list of child services at the time of invocation -    * added services will not be picked up.    */
DECL|method|getServices ()
specifier|public
name|List
argument_list|<
name|Service
argument_list|>
name|getServices
parameter_list|()
block|{
synchronized|synchronized
init|(
name|serviceList
init|)
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|serviceList
argument_list|)
return|;
block|}
block|}
DECL|method|addService (Service service)
specifier|protected
name|void
name|addService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding service "
operator|+
name|service
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|serviceList
init|)
block|{
name|serviceList
operator|.
name|add
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeService (Service service)
specifier|protected
specifier|synchronized
name|boolean
name|removeService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
synchronized|synchronized
init|(
name|serviceList
init|)
block|{
return|return
name|serviceList
operator|.
name|add
argument_list|(
name|service
argument_list|)
return|;
block|}
block|}
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Service
argument_list|>
name|services
init|=
name|getServices
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": initing services, size="
operator|+
name|services
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Service
name|service
range|:
name|services
control|)
block|{
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Service
argument_list|>
name|services
init|=
name|getServices
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": starting services, size="
operator|+
name|services
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Service
name|service
range|:
name|services
control|)
block|{
comment|// start the service. If this fails that service
comment|// will be stopped and an exception raised
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
comment|//stop all services that were started
name|int
name|numOfServicesToStop
init|=
name|serviceList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": stopping services, size="
operator|+
name|numOfServicesToStop
argument_list|)
expr_stmt|;
block|}
name|stop
argument_list|(
name|numOfServicesToStop
argument_list|,
name|STOP_ONLY_STARTED_SERVICES
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stop the services in reverse order    *    * @param numOfServicesStarted index from where the stop should work    * @param stopOnlyStartedServices flag to say "only start services that are    * started, not those that are NOTINITED or INITED.    * @throws RuntimeException the first exception raised during the    * stop process -<i>after all services are stopped</i>    */
DECL|method|stop (int numOfServicesStarted, boolean stopOnlyStartedServices)
specifier|private
specifier|synchronized
name|void
name|stop
parameter_list|(
name|int
name|numOfServicesStarted
parameter_list|,
name|boolean
name|stopOnlyStartedServices
parameter_list|)
block|{
comment|// stop in reverse order of start
name|Exception
name|firstException
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Service
argument_list|>
name|services
init|=
name|getServices
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numOfServicesStarted
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|Service
name|service
init|=
name|services
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stopping service #"
operator|+
name|i
operator|+
literal|": "
operator|+
name|service
argument_list|)
expr_stmt|;
block|}
name|STATE
name|state
init|=
name|service
operator|.
name|getServiceState
argument_list|()
decl_stmt|;
comment|//depending on the stop police
if|if
condition|(
name|state
operator|==
name|STATE
operator|.
name|STARTED
operator|||
operator|(
operator|!
name|stopOnlyStartedServices
operator|&&
name|state
operator|==
name|STATE
operator|.
name|INITED
operator|)
condition|)
block|{
name|Exception
name|ex
init|=
name|ServiceOperations
operator|.
name|stopQuietly
argument_list|(
name|LOG
argument_list|,
name|service
argument_list|)
decl_stmt|;
if|if
condition|(
name|ex
operator|!=
literal|null
operator|&&
name|firstException
operator|==
literal|null
condition|)
block|{
name|firstException
operator|=
name|ex
expr_stmt|;
block|}
block|}
block|}
comment|//after stopping all services, rethrow the first exception raised
if|if
condition|(
name|firstException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ServiceStateException
operator|.
name|convert
argument_list|(
name|firstException
argument_list|)
throw|;
block|}
block|}
comment|/**    * JVM Shutdown hook for CompositeService which will stop the give    * CompositeService gracefully in case of JVM shutdown.    */
DECL|class|CompositeServiceShutdownHook
specifier|public
specifier|static
class|class
name|CompositeServiceShutdownHook
implements|implements
name|Runnable
block|{
DECL|field|compositeService
specifier|private
name|CompositeService
name|compositeService
decl_stmt|;
DECL|method|CompositeServiceShutdownHook (CompositeService compositeService)
specifier|public
name|CompositeServiceShutdownHook
parameter_list|(
name|CompositeService
name|compositeService
parameter_list|)
block|{
name|this
operator|.
name|compositeService
operator|=
name|compositeService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ServiceOperations
operator|.
name|stopQuietly
argument_list|(
name|compositeService
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

