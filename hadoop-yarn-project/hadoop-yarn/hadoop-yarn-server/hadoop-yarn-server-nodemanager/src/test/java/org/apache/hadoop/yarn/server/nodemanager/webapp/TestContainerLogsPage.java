begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|webapp
package|;
end_package

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
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|List
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|LocalDirsHandlerService
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
name|NodeHealthCheckerService
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
name|application
operator|.
name|Application
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
name|BuilderUtils
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
name|Test
import|;
end_import

begin_class
DECL|class|TestContainerLogsPage
specifier|public
class|class
name|TestContainerLogsPage
block|{
annotation|@
name|Test
DECL|method|testContainerLogDirs ()
specifier|public
name|void
name|testContainerLogDirs
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|logdirwithFile
init|=
literal|"file:///target/"
operator|+
name|TestNMWebServer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"LogDir"
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|logdirwithFile
argument_list|)
expr_stmt|;
name|NodeHealthCheckerService
name|healthChecker
init|=
operator|new
name|NodeHealthCheckerService
argument_list|()
decl_stmt|;
name|healthChecker
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
name|healthChecker
operator|.
name|getDiskHandler
argument_list|()
decl_stmt|;
comment|// Add an application and the corresponding containers
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|user
init|=
literal|"nobody"
decl_stmt|;
name|long
name|clusterTimeStamp
init|=
literal|1234
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
name|recordFactory
argument_list|,
name|clusterTimeStamp
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Application
name|app
init|=
name|mock
argument_list|(
name|Application
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|container1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|recordFactory
argument_list|,
name|appId
argument_list|,
name|appAttemptId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|files
init|=
literal|null
decl_stmt|;
name|files
operator|=
name|ContainerLogsPage
operator|.
name|ContainersLogsBlock
operator|.
name|getContainerLogDirs
argument_list|(
name|container1
argument_list|,
name|dirsHandler
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
operator|(
name|files
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"file:"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

