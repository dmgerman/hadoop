begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
package|;
end_package

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
name|times
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
name|verify
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|ApplicationClientProtocol
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
name|protocolrecords
operator|.
name|GetApplicationReportRequest
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
name|protocolrecords
operator|.
name|GetApplicationReportResponse
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
name|ApplicationAccessType
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
name|ApplicationReport
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
name|ApplicationSubmissionContext
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
name|YarnApplicationState
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
name|resourcemanager
operator|.
name|MockRM
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
name|ResourceManager
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
name|rmapp
operator|.
name|RMApp
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
DECL|class|TestYarnClient
specifier|public
class|class
name|TestYarnClient
block|{
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
block|{
comment|// More to come later.
block|}
annotation|@
name|Test
DECL|method|testClientStop ()
specifier|public
name|void
name|testClientStop
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ResourceManager
name|rm
init|=
operator|new
name|ResourceManager
argument_list|()
decl_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|YarnClient
name|client
init|=
operator|new
name|YarnClientImpl
argument_list|()
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testSubmitApplication ()
specifier|public
name|void
name|testSubmitApplication
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_CLIENT_APP_SUBMISSION_POLL_INTERVAL_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// speed up tests
specifier|final
name|YarnClient
name|client
init|=
operator|new
name|MockYarnClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|YarnApplicationState
index|[]
name|exitStates
init|=
operator|new
name|YarnApplicationState
index|[]
block|{
name|YarnApplicationState
operator|.
name|SUBMITTED
block|,
name|YarnApplicationState
operator|.
name|ACCEPTED
block|,
name|YarnApplicationState
operator|.
name|RUNNING
block|,
name|YarnApplicationState
operator|.
name|FINISHED
block|,
name|YarnApplicationState
operator|.
name|FAILED
block|,
name|YarnApplicationState
operator|.
name|KILLED
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exitStates
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|ApplicationSubmissionContext
name|context
init|=
name|mock
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationId
name|applicationId
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
name|i
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
operator|(
operator|(
name|MockYarnClient
operator|)
name|client
operator|)
operator|.
name|setYarnApplicationState
argument_list|(
name|exitStates
index|[
name|i
index|]
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|submitApplication
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
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected."
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
operator|(
operator|(
name|MockYarnClient
operator|)
name|client
operator|)
operator|.
name|mockReport
argument_list|,
name|times
argument_list|(
literal|4
operator|*
name|i
operator|+
literal|4
argument_list|)
argument_list|)
operator|.
name|getYarnApplicationState
argument_list|()
expr_stmt|;
block|}
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testApplicationType ()
specifier|public
name|void
name|testApplicationType
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
name|rootLogger
init|=
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|()
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"name"
argument_list|,
literal|"user"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"default"
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|"MAPREDUCE"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"YARN"
argument_list|,
name|app
operator|.
name|getApplicationType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"MAPREDUCE"
argument_list|,
name|app1
operator|.
name|getApplicationType
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testApplicationTypeLimit ()
specifier|public
name|void
name|testApplicationTypeLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
name|rootLogger
init|=
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|()
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"name"
argument_list|,
literal|"user"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"default"
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|"MAPREDUCE-LENGTH-IS-20"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"MAPREDUCE-LENGTH-IS-"
argument_list|,
name|app1
operator|.
name|getApplicationType
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|class|MockYarnClient
specifier|private
specifier|static
class|class
name|MockYarnClient
extends|extends
name|YarnClientImpl
block|{
DECL|field|mockReport
specifier|private
name|ApplicationReport
name|mockReport
decl_stmt|;
DECL|method|MockYarnClient ()
specifier|public
name|MockYarnClient
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|rmClient
operator|=
name|mock
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|GetApplicationReportResponse
name|mockResponse
init|=
name|mock
argument_list|(
name|GetApplicationReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|mockReport
operator|=
name|mock
argument_list|(
name|ApplicationReport
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|when
argument_list|(
name|rmClient
operator|.
name|getApplicationReport
argument_list|(
name|any
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected."
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|mockResponse
operator|.
name|getApplicationReport
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockReport
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{     }
DECL|method|setYarnApplicationState (YarnApplicationState state)
specifier|public
name|void
name|setYarnApplicationState
parameter_list|(
name|YarnApplicationState
name|state
parameter_list|)
block|{
name|when
argument_list|(
name|mockReport
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|YarnApplicationState
operator|.
name|NEW
argument_list|,
name|YarnApplicationState
operator|.
name|NEW_SAVING
argument_list|,
name|YarnApplicationState
operator|.
name|NEW_SAVING
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

