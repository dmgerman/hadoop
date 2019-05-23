begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
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
name|ozone
operator|.
name|recon
operator|.
name|ReconServerConfigKeys
operator|.
name|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY
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
name|ozone
operator|.
name|recon
operator|.
name|ReconServerConfigKeys
operator|.
name|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY_DEFAULT
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
name|ozone
operator|.
name|recon
operator|.
name|ReconServerConfigKeys
operator|.
name|RECON_OM_SNAPSHOT_TASK_INTERVAL
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
name|ozone
operator|.
name|recon
operator|.
name|ReconServerConfigKeys
operator|.
name|RECON_OM_SNAPSHOT_TASK_INTERVAL_DEFAULT
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|hdds
operator|.
name|cli
operator|.
name|GenericCli
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|recon
operator|.
name|spi
operator|.
name|ContainerDBServiceProvider
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
name|ozone
operator|.
name|recon
operator|.
name|spi
operator|.
name|OzoneManagerServiceProvider
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
name|ozone
operator|.
name|recon
operator|.
name|tasks
operator|.
name|ContainerKeyMapperTask
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Guice
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_comment
comment|/**  * Recon server main class that stops and starts recon services.  */
end_comment

begin_class
DECL|class|ReconServer
specifier|public
class|class
name|ReconServer
extends|extends
name|GenericCli
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
name|ReconServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|ScheduledExecutorService
name|scheduler
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
annotation|@
name|Inject
DECL|field|httpServer
specifier|private
name|ReconHttpServer
name|httpServer
decl_stmt|;
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
operator|new
name|ReconServer
argument_list|()
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneConfiguration
name|ozoneConfiguration
init|=
name|createOzoneConfiguration
argument_list|()
decl_stmt|;
name|OzoneConfigurationProvider
operator|.
name|setConfiguration
argument_list|(
name|ozoneConfiguration
argument_list|)
expr_stmt|;
name|injector
operator|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|ReconControllerModule
argument_list|()
argument_list|,
operator|new
name|ReconRestServletModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|rest
argument_list|(
literal|"/api/*"
argument_list|)
operator|.
name|packages
argument_list|(
literal|"org.apache.hadoop.ozone.recon.api"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|//Pass on injector to listener that does the Guice - Jersey HK2 bridging.
name|ReconGuiceServletContextListener
operator|.
name|setInjector
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|httpServer
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|ReconHttpServer
operator|.
name|class
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting Recon server"
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|scheduleReconTasks
argument_list|()
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error during stop Recon server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**    * Schedule the tasks that is required by Recon to keep its metadata up to    * date.    */
DECL|method|scheduleReconTasks ()
specifier|private
name|void
name|scheduleReconTasks
parameter_list|()
block|{
name|OzoneConfiguration
name|configuration
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|OzoneConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerDBServiceProvider
name|containerDBServiceProvider
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|ContainerDBServiceProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|OzoneManagerServiceProvider
name|ozoneManagerServiceProvider
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|OzoneManagerServiceProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|long
name|initialDelay
init|=
name|configuration
operator|.
name|getTimeDuration
argument_list|(
name|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY
argument_list|,
name|RECON_OM_SNAPSHOT_TASK_INITIAL_DELAY_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|interval
init|=
name|configuration
operator|.
name|getTimeDuration
argument_list|(
name|RECON_OM_SNAPSHOT_TASK_INTERVAL
argument_list|,
name|RECON_OM_SNAPSHOT_TASK_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|scheduleWithFixedDelay
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|ozoneManagerServiceProvider
operator|.
name|updateReconOmDBWithNewSnapshot
argument_list|()
expr_stmt|;
comment|// Schedule the task to read OM DB and write the reverse mapping to
comment|// Recon container DB.
name|ContainerKeyMapperTask
name|containerKeyMapperTask
init|=
operator|new
name|ContainerKeyMapperTask
argument_list|(
name|containerDBServiceProvider
argument_list|,
name|ozoneManagerServiceProvider
operator|.
name|getOMMetadataManagerInstance
argument_list|()
argument_list|)
decl_stmt|;
name|containerKeyMapperTask
operator|.
name|reprocess
argument_list|(
name|ozoneManagerServiceProvider
operator|.
name|getOMMetadataManagerInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to get OM "
operator|+
literal|"Snapshot"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|initialDelay
argument_list|,
name|interval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping Recon server"
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

