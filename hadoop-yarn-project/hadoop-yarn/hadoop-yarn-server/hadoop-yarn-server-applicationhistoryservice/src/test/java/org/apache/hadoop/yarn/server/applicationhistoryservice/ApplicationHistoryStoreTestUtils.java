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
name|api
operator|.
name|records
operator|.
name|ContainerState
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
name|NodeId
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
name|Priority
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
name|YarnApplicationAttemptState
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptFinishData
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
name|records
operator|.
name|ApplicationAttemptStartData
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
name|records
operator|.
name|ApplicationFinishData
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
name|records
operator|.
name|ApplicationStartData
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
name|records
operator|.
name|ContainerFinishData
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
name|records
operator|.
name|ContainerStartData
import|;
end_import

begin_class
DECL|class|ApplicationHistoryStoreTestUtils
specifier|public
class|class
name|ApplicationHistoryStoreTestUtils
block|{
DECL|field|store
specifier|protected
name|ApplicationHistoryStore
name|store
decl_stmt|;
DECL|method|writeApplicationStartData (ApplicationId appId)
specifier|protected
name|void
name|writeApplicationStartData
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|applicationStarted
argument_list|(
name|ApplicationStartData
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test type"
argument_list|,
literal|"test queue"
argument_list|,
literal|"test user"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeApplicationStartData (ApplicationId appId, long startTime)
specifier|protected
name|void
name|writeApplicationStartData
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|long
name|startTime
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|applicationStarted
argument_list|(
name|ApplicationStartData
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test type"
argument_list|,
literal|"test queue"
argument_list|,
literal|"test user"
argument_list|,
literal|0
argument_list|,
name|startTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeApplicationFinishData (ApplicationId appId)
specifier|protected
name|void
name|writeApplicationFinishData
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|applicationFinished
argument_list|(
name|ApplicationFinishData
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|0
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|FinalApplicationStatus
operator|.
name|UNDEFINED
argument_list|,
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeApplicationAttemptStartData ( ApplicationAttemptId appAttemptId)
specifier|protected
name|void
name|writeApplicationAttemptStartData
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|applicationAttemptStarted
argument_list|(
name|ApplicationAttemptStartData
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeApplicationAttemptFinishData ( ApplicationAttemptId appAttemptId)
specifier|protected
name|void
name|writeApplicationAttemptFinishData
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|applicationAttemptFinished
argument_list|(
name|ApplicationAttemptFinishData
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|,
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test tracking url"
argument_list|,
name|FinalApplicationStatus
operator|.
name|UNDEFINED
argument_list|,
name|YarnApplicationAttemptState
operator|.
name|FINISHED
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|writeContainerStartData (ContainerId containerId)
specifier|protected
name|void
name|writeContainerStartData
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|containerStarted
argument_list|(
name|ContainerStartData
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|containerId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeContainerFinishData (ContainerId containerId)
specifier|protected
name|void
name|writeContainerFinishData
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|containerFinished
argument_list|(
name|ContainerFinishData
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|,
literal|0
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

