begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.collector
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
name|timelineservice
operator|.
name|collector
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|ExitUtil
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
name|Shell
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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|ContainerId
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
name|api
operator|.
name|CollectorNodemanagerProtocol
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
name|ContainerType
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
name|protocolrecords
operator|.
name|GetTimelineCollectorContextRequest
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
name|protocolrecords
operator|.
name|GetTimelineCollectorContextResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_class
DECL|class|TestPerNodeTimelineCollectorsAuxService
specifier|public
class|class
name|TestPerNodeTimelineCollectorsAuxService
block|{
DECL|field|appAttemptId
specifier|private
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|auxService
specifier|private
name|PerNodeTimelineCollectorsAuxService
name|auxService
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|TestPerNodeTimelineCollectorsAuxService ()
specifier|public
name|TestPerNodeTimelineCollectorsAuxService
parameter_list|()
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|appAttemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
comment|// enable timeline service v.2
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_VERSION
argument_list|,
literal|2.0f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Shell
operator|.
name|ExitCodeException
block|{
if|if
condition|(
name|auxService
operator|!=
literal|null
condition|)
block|{
name|auxService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAddApplication ()
specifier|public
name|void
name|testAddApplication
parameter_list|()
throws|throws
name|Exception
block|{
name|auxService
operator|=
name|createCollectorAndAddApplication
argument_list|()
expr_stmt|;
comment|// auxService should have a single app
name|assertTrue
argument_list|(
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddApplicationNonAMContainer ()
specifier|public
name|void
name|testAddApplicationNonAMContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|auxService
operator|=
name|createCollector
argument_list|()
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|getContainerId
argument_list|(
literal|2L
argument_list|)
decl_stmt|;
comment|// not an AM
name|ContainerInitializationContext
name|context
init|=
name|mock
argument_list|(
name|ContainerInitializationContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|initializeContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// auxService should not have that app
name|assertFalse
argument_list|(
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveApplication ()
specifier|public
name|void
name|testRemoveApplication
parameter_list|()
throws|throws
name|Exception
block|{
name|auxService
operator|=
name|createCollectorAndAddApplication
argument_list|()
expr_stmt|;
comment|// auxService should have a single app
name|assertTrue
argument_list|(
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|getAMContainerId
argument_list|()
decl_stmt|;
name|ContainerTerminationContext
name|context
init|=
name|mock
argument_list|(
name|ContainerTerminationContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getContainerType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainerType
operator|.
name|APPLICATION_MASTER
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|stopContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// auxService should have the app's collector and need to remove only after
comment|// a configured period
name|assertTrue
argument_list|(
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500l
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
comment|// auxService should not have that app
name|assertFalse
argument_list|(
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveApplicationNonAMContainer ()
specifier|public
name|void
name|testRemoveApplicationNonAMContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|auxService
operator|=
name|createCollectorAndAddApplication
argument_list|()
expr_stmt|;
comment|// auxService should have a single app
name|assertTrue
argument_list|(
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|getContainerId
argument_list|(
literal|2L
argument_list|)
decl_stmt|;
comment|// not an AM
name|ContainerTerminationContext
name|context
init|=
name|mock
argument_list|(
name|ContainerTerminationContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|stopContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// auxService should still have that app
name|assertTrue
argument_list|(
name|auxService
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLaunch ()
specifier|public
name|void
name|testLaunch
parameter_list|()
throws|throws
name|Exception
block|{
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
try|try
block|{
name|auxService
operator|=
name|PerNodeTimelineCollectorsAuxService
operator|.
name|launchServer
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|createCollectorManager
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitUtil
operator|.
name|ExitException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|status
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|resetFirstExitException
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|PerNodeTimelineCollectorsAuxService
DECL|method|createCollectorAndAddApplication ()
name|createCollectorAndAddApplication
parameter_list|()
block|{
name|PerNodeTimelineCollectorsAuxService
name|auxService
init|=
name|createCollector
argument_list|()
decl_stmt|;
comment|// create an AM container
name|ContainerId
name|containerId
init|=
name|getAMContainerId
argument_list|()
decl_stmt|;
name|ContainerInitializationContext
name|context
init|=
name|mock
argument_list|(
name|ContainerInitializationContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getContainerType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ContainerType
operator|.
name|APPLICATION_MASTER
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|initializeContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|auxService
return|;
block|}
DECL|method|createCollector ()
specifier|private
name|PerNodeTimelineCollectorsAuxService
name|createCollector
parameter_list|()
block|{
name|NodeTimelineCollectorManager
name|collectorManager
init|=
name|createCollectorManager
argument_list|()
decl_stmt|;
name|PerNodeTimelineCollectorsAuxService
name|auxService
init|=
name|spy
argument_list|(
operator|new
name|PerNodeTimelineCollectorsAuxService
argument_list|(
name|collectorManager
argument_list|)
argument_list|)
decl_stmt|;
name|auxService
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|auxService
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|auxService
return|;
block|}
DECL|method|createCollectorManager ()
specifier|private
name|NodeTimelineCollectorManager
name|createCollectorManager
parameter_list|()
block|{
name|NodeTimelineCollectorManager
name|collectorManager
init|=
name|spy
argument_list|(
operator|new
name|NodeTimelineCollectorManager
argument_list|()
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|collectorManager
argument_list|)
operator|.
name|getConfig
argument_list|()
expr_stmt|;
name|CollectorNodemanagerProtocol
name|nmCollectorService
init|=
name|mock
argument_list|(
name|CollectorNodemanagerProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetTimelineCollectorContextResponse
name|response
init|=
name|GetTimelineCollectorContextResponse
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
try|try
block|{
name|when
argument_list|(
name|nmCollectorService
operator|.
name|getTimelineCollectorContext
argument_list|(
name|any
argument_list|(
name|GetTimelineCollectorContextRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
name|doReturn
argument_list|(
name|nmCollectorService
argument_list|)
operator|.
name|when
argument_list|(
name|collectorManager
argument_list|)
operator|.
name|getNMCollectorService
argument_list|()
expr_stmt|;
return|return
name|collectorManager
return|;
block|}
DECL|method|getAMContainerId ()
specifier|private
name|ContainerId
name|getAMContainerId
parameter_list|()
block|{
return|return
name|getContainerId
argument_list|(
literal|1L
argument_list|)
return|;
block|}
DECL|method|getContainerId (long id)
specifier|private
name|ContainerId
name|getContainerId
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
end_class

end_unit

