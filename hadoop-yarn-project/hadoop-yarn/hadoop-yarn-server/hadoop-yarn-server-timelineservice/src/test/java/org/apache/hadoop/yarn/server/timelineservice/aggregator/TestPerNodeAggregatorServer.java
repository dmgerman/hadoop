begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.aggregator
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
name|aggregator
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestPerNodeAggregatorServer
specifier|public
class|class
name|TestPerNodeAggregatorServer
block|{
DECL|field|appAttemptId
specifier|private
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|method|TestPerNodeAggregatorServer ()
specifier|public
name|TestPerNodeAggregatorServer
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
name|PerNodeAggregatorServer
name|aggregator
init|=
name|createAggregatorAndAddApplication
argument_list|()
decl_stmt|;
comment|// aggregator should have a single app
name|assertTrue
argument_list|(
name|aggregator
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|aggregator
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
name|PerNodeAggregatorServer
name|aggregator
init|=
name|createAggregator
argument_list|()
decl_stmt|;
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
name|aggregator
operator|.
name|initializeContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// aggregator should not have that app
name|assertFalse
argument_list|(
name|aggregator
operator|.
name|hasApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
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
name|PerNodeAggregatorServer
name|aggregator
init|=
name|createAggregatorAndAddApplication
argument_list|()
decl_stmt|;
comment|// aggregator should have a single app
name|String
name|appIdStr
init|=
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|aggregator
operator|.
name|hasApplication
argument_list|(
name|appIdStr
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
name|aggregator
operator|.
name|stopContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// aggregator should not have that app
name|assertFalse
argument_list|(
name|aggregator
operator|.
name|hasApplication
argument_list|(
name|appIdStr
argument_list|)
argument_list|)
expr_stmt|;
name|aggregator
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
name|PerNodeAggregatorServer
name|aggregator
init|=
name|createAggregatorAndAddApplication
argument_list|()
decl_stmt|;
comment|// aggregator should have a single app
name|String
name|appIdStr
init|=
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|aggregator
operator|.
name|hasApplication
argument_list|(
name|appIdStr
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
name|aggregator
operator|.
name|stopContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// aggregator should still have that app
name|assertTrue
argument_list|(
name|aggregator
operator|.
name|hasApplication
argument_list|(
name|appIdStr
argument_list|)
argument_list|)
expr_stmt|;
name|aggregator
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
name|PerNodeAggregatorServer
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
name|PerNodeAggregatorServer
operator|.
name|launchServer
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
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
finally|finally
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|createAggregatorAndAddApplication ()
specifier|private
name|PerNodeAggregatorServer
name|createAggregatorAndAddApplication
parameter_list|()
block|{
name|PerNodeAggregatorServer
name|aggregator
init|=
name|createAggregator
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
name|aggregator
operator|.
name|initializeContainer
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|aggregator
return|;
block|}
DECL|method|createAggregator ()
specifier|private
name|PerNodeAggregatorServer
name|createAggregator
parameter_list|()
block|{
name|AppLevelServiceManager
name|serviceManager
init|=
name|spy
argument_list|(
operator|new
name|AppLevelServiceManager
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
name|serviceManager
argument_list|)
operator|.
name|getConfig
argument_list|()
expr_stmt|;
name|PerNodeAggregatorServer
name|aggregator
init|=
name|spy
argument_list|(
operator|new
name|PerNodeAggregatorServer
argument_list|(
name|serviceManager
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|aggregator
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

