begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|assertNotNull
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
name|service
operator|.
name|Service
operator|.
name|STATE
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|webapp
operator|.
name|AHSWebApp
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
DECL|class|TestApplicationHistoryServer
specifier|public
class|class
name|TestApplicationHistoryServer
block|{
DECL|field|historyServer
name|ApplicationHistoryServer
name|historyServer
init|=
literal|null
decl_stmt|;
comment|// simple test init/start/stop ApplicationHistoryServer. Status should change.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
DECL|method|testStartStopServer ()
specifier|public
name|void
name|testStartStopServer
parameter_list|()
throws|throws
name|Exception
block|{
name|historyServer
operator|=
operator|new
name|ApplicationHistoryServer
argument_list|()
expr_stmt|;
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|historyServer
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|,
name|historyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|historyServer
operator|.
name|getServices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationHistoryClientService
name|historyService
init|=
name|historyServer
operator|.
name|getClientService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|historyServer
operator|.
name|getClientService
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|,
name|historyService
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|historyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|historyService
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|,
name|historyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test launch method
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
name|historyServer
operator|=
name|ApplicationHistoryServer
operator|.
name|launchAppHistoryServer
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
block|}
annotation|@
name|After
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|historyServer
operator|!=
literal|null
condition|)
block|{
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|AHSWebApp
operator|.
name|resetInstance
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

