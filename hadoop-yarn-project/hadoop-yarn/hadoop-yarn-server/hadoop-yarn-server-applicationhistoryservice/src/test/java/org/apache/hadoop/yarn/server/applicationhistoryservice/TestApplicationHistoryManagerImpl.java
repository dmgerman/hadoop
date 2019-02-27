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
name|Map
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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|TestApplicationHistoryManagerImpl
specifier|public
class|class
name|TestApplicationHistoryManagerImpl
extends|extends
name|ApplicationHistoryStoreTestUtils
block|{
DECL|field|applicationHistoryManagerImpl
name|ApplicationHistoryManagerImpl
name|applicationHistoryManagerImpl
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_STORE
argument_list|,
name|MemoryApplicationHistoryStore
operator|.
name|class
argument_list|,
name|ApplicationHistoryStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|applicationHistoryManagerImpl
operator|=
operator|new
name|ApplicationHistoryManagerImpl
argument_list|()
expr_stmt|;
name|applicationHistoryManagerImpl
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|applicationHistoryManagerImpl
operator|.
name|start
argument_list|()
expr_stmt|;
name|store
operator|=
name|applicationHistoryManagerImpl
operator|.
name|getHistoryStore
argument_list|()
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
name|Exception
block|{
name|applicationHistoryManagerImpl
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testApplicationReport ()
specifier|public
name|void
name|testApplicationReport
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationId
name|appId
init|=
literal|null
decl_stmt|;
name|appId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writeApplicationStartData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|writeApplicationFinishData
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|writeApplicationAttemptStartData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|writeApplicationAttemptFinishData
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|ApplicationReport
name|appReport
init|=
name|applicationHistoryManagerImpl
operator|.
name|getApplication
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appReport
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appId
argument_list|,
name|appReport
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId
argument_list|,
name|appReport
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|,
name|appReport
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test type"
argument_list|,
name|appReport
operator|.
name|getApplicationType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test queue"
argument_list|,
name|appReport
operator|.
name|getQueue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testApplications ()
specifier|public
name|void
name|testApplications
parameter_list|()
throws|throws
name|IOException
block|{
name|ApplicationId
name|appId1
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId2
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId3
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|writeApplicationStartData
argument_list|(
name|appId1
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|writeApplicationFinishData
argument_list|(
name|appId1
argument_list|)
expr_stmt|;
name|writeApplicationStartData
argument_list|(
name|appId2
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|writeApplicationFinishData
argument_list|(
name|appId2
argument_list|)
expr_stmt|;
name|writeApplicationStartData
argument_list|(
name|appId3
argument_list|,
literal|4000
argument_list|)
expr_stmt|;
name|writeApplicationFinishData
argument_list|(
name|appId3
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationReport
argument_list|>
name|reports
init|=
name|applicationHistoryManagerImpl
operator|.
name|getApplications
argument_list|(
literal|2
argument_list|,
literal|2000L
argument_list|,
literal|5000L
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|reports
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reports
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|reports
operator|.
name|get
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|reports
operator|.
name|get
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|reports
operator|.
name|get
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

