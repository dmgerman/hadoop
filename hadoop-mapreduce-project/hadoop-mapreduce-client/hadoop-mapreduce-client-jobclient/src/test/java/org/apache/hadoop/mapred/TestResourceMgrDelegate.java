begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|ArrayList
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
name|mapreduce
operator|.
name|JobStatus
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
name|mapreduce
operator|.
name|JobStatus
operator|.
name|State
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
name|GetApplicationsRequest
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
name|GetApplicationsResponse
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
name|GetQueueInfoRequest
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
name|GetQueueInfoResponse
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
name|ApplicationResourceUsageReport
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
name|FinalApplicationStatus
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
name|Resource
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
name|client
operator|.
name|api
operator|.
name|impl
operator|.
name|YarnClientImpl
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
name|util
operator|.
name|Records
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestResourceMgrDelegate
specifier|public
class|class
name|TestResourceMgrDelegate
block|{
comment|/**    * Tests that getRootQueues makes a request for the (recursive) child queues    * @throws IOException    */
annotation|@
name|Test
DECL|method|testGetRootQueues ()
specifier|public
name|void
name|testGetRootQueues
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|ApplicationClientProtocol
name|applicationsManager
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetQueueInfoResponse
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|GetQueueInfoResponse
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|QueueInfo
name|queueInfo
init|=
name|Mockito
operator|.
name|mock
argument_list|(
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
name|QueueInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|response
operator|.
name|getQueueInfo
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|queueInfo
argument_list|)
expr_stmt|;
try|try
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|applicationsManager
operator|.
name|getQueueInfo
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|GetQueueInfoRequest
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
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ResourceMgrDelegate
name|delegate
init|=
operator|new
name|ResourceMgrDelegate
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|this
operator|.
name|client
operator|instanceof
name|YarnClientImpl
argument_list|)
expr_stmt|;
operator|(
operator|(
name|YarnClientImpl
operator|)
name|this
operator|.
name|client
operator|)
operator|.
name|setRMClient
argument_list|(
name|applicationsManager
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|delegate
operator|.
name|getRootQueues
argument_list|()
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|GetQueueInfoRequest
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|GetQueueInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|applicationsManager
argument_list|)
operator|.
name|getQueueInfo
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Children of root queue not requested"
argument_list|,
name|argument
operator|.
name|getValue
argument_list|()
operator|.
name|getIncludeChildQueues
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Request wasn't to recurse through children"
argument_list|,
name|argument
operator|.
name|getValue
argument_list|()
operator|.
name|getRecursive
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|tesAllJobs ()
specifier|public
name|void
name|tesAllJobs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ApplicationClientProtocol
name|applicationsManager
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetApplicationsResponse
name|allApplicationsResponse
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|()
decl_stmt|;
name|applications
operator|.
name|add
argument_list|(
name|getApplicationReport
argument_list|(
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|,
name|FinalApplicationStatus
operator|.
name|FAILED
argument_list|)
argument_list|)
expr_stmt|;
name|applications
operator|.
name|add
argument_list|(
name|getApplicationReport
argument_list|(
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|,
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|)
argument_list|)
expr_stmt|;
name|applications
operator|.
name|add
argument_list|(
name|getApplicationReport
argument_list|(
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|,
name|FinalApplicationStatus
operator|.
name|KILLED
argument_list|)
argument_list|)
expr_stmt|;
name|applications
operator|.
name|add
argument_list|(
name|getApplicationReport
argument_list|(
name|YarnApplicationState
operator|.
name|FAILED
argument_list|,
name|FinalApplicationStatus
operator|.
name|FAILED
argument_list|)
argument_list|)
expr_stmt|;
name|allApplicationsResponse
operator|.
name|setApplicationList
argument_list|(
name|applications
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|applicationsManager
operator|.
name|getApplications
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|GetApplicationsRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|allApplicationsResponse
argument_list|)
expr_stmt|;
name|ResourceMgrDelegate
name|resourceMgrDelegate
init|=
operator|new
name|ResourceMgrDelegate
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|this
operator|.
name|client
operator|instanceof
name|YarnClientImpl
argument_list|)
expr_stmt|;
operator|(
operator|(
name|YarnClientImpl
operator|)
name|this
operator|.
name|client
operator|)
operator|.
name|setRMClient
argument_list|(
name|applicationsManager
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|JobStatus
index|[]
name|allJobs
init|=
name|resourceMgrDelegate
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|State
operator|.
name|FAILED
argument_list|,
name|allJobs
index|[
literal|0
index|]
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|allJobs
index|[
literal|1
index|]
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|State
operator|.
name|KILLED
argument_list|,
name|allJobs
index|[
literal|2
index|]
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|State
operator|.
name|FAILED
argument_list|,
name|allJobs
index|[
literal|3
index|]
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getApplicationReport ( YarnApplicationState yarnApplicationState, FinalApplicationStatus finalApplicationStatus)
specifier|private
name|ApplicationReport
name|getApplicationReport
parameter_list|(
name|YarnApplicationState
name|yarnApplicationState
parameter_list|,
name|FinalApplicationStatus
name|finalApplicationStatus
parameter_list|)
block|{
name|ApplicationReport
name|appReport
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ApplicationReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationResourceUsageReport
name|appResources
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ApplicationResourceUsageReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appReport
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appResources
operator|.
name|getNeededResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appResources
operator|.
name|getReservedResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appResources
operator|.
name|getUsedResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appResources
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|yarnApplicationState
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appReport
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|finalApplicationStatus
argument_list|)
expr_stmt|;
return|return
name|appReport
return|;
block|}
block|}
end_class

end_unit

