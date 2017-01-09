begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
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
name|ipc
operator|.
name|RemoteException
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
name|protocolrecords
operator|.
name|GetNewApplicationRequest
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
name|SubmitApplicationRequest
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
name|ContainerLaunchContext
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
name|ACLsTestBase
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
name|RMAppState
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
name|utils
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
DECL|class|TestApplicationPriorityACLs
specifier|public
class|class
name|TestApplicationPriorityACLs
extends|extends
name|ACLsTestBase
block|{
DECL|field|defaultPriorityQueueA
specifier|private
specifier|final
name|int
name|defaultPriorityQueueA
init|=
literal|3
decl_stmt|;
DECL|field|defaultPriorityQueueB
specifier|private
specifier|final
name|int
name|defaultPriorityQueueB
init|=
literal|10
decl_stmt|;
DECL|field|maxPriorityQueueA
specifier|private
specifier|final
name|int
name|maxPriorityQueueA
init|=
literal|5
decl_stmt|;
DECL|field|maxPriorityQueueB
specifier|private
specifier|final
name|int
name|maxPriorityQueueB
init|=
literal|11
decl_stmt|;
DECL|field|clusterMaxPriority
specifier|private
specifier|final
name|int
name|clusterMaxPriority
init|=
literal|10
decl_stmt|;
annotation|@
name|Test
DECL|method|testApplicationACLs ()
specifier|public
name|void
name|testApplicationACLs
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*      * Cluster Max-priority is 10. User 'queueA_user' has permission to submit      * apps only at priority 5. Default priority for this user is 3.      */
comment|// Case 1: App will be submitted with priority 5.
name|verifyAppSubmitWithPrioritySuccess
argument_list|(
name|QUEUE_A_USER
argument_list|,
name|QUEUEA
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// Case 2: App will be rejected as submitted priority was 6.
name|verifyAppSubmitWithPriorityFailure
argument_list|(
name|QUEUE_A_USER
argument_list|,
name|QUEUEA
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|// Case 3: App will be submitted w/o priority, hence consider default 3.
name|verifyAppSubmitWithPrioritySuccess
argument_list|(
name|QUEUE_A_USER
argument_list|,
name|QUEUEA
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Case 4: App will be submitted with priority 11.
name|verifyAppSubmitWithPrioritySuccess
argument_list|(
name|QUEUE_B_USER
argument_list|,
name|QUEUEB
argument_list|,
literal|11
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyAppSubmitWithPrioritySuccess (String submitter, String queueName, int priority)
specifier|private
name|void
name|verifyAppSubmitWithPrioritySuccess
parameter_list|(
name|String
name|submitter
parameter_list|,
name|String
name|queueName
parameter_list|,
name|int
name|priority
parameter_list|)
throws|throws
name|Exception
block|{
name|Priority
name|appPriority
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|priority
operator|>
literal|0
condition|)
block|{
name|appPriority
operator|=
name|Priority
operator|.
name|newInstance
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// RM will consider default priority for the submitted user. So update
comment|// priority to the default value to compare.
name|priority
operator|=
name|defaultPriorityQueueA
expr_stmt|;
block|}
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|prepareForAppSubmission
argument_list|(
name|submitter
argument_list|,
name|queueName
argument_list|,
name|appPriority
argument_list|)
decl_stmt|;
name|submitAppToRMWithValidAcl
argument_list|(
name|submitter
argument_list|,
name|submissionContext
argument_list|)
expr_stmt|;
comment|// Ideally get app report here and check the priority.
name|verifyAppPriorityIsAccepted
argument_list|(
name|submitter
argument_list|,
name|submissionContext
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|priority
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyAppSubmitWithPriorityFailure (String submitter, String queueName, int priority)
specifier|private
name|void
name|verifyAppSubmitWithPriorityFailure
parameter_list|(
name|String
name|submitter
parameter_list|,
name|String
name|queueName
parameter_list|,
name|int
name|priority
parameter_list|)
throws|throws
name|Exception
block|{
name|Priority
name|appPriority
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
name|priority
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|prepareForAppSubmission
argument_list|(
name|submitter
argument_list|,
name|queueName
argument_list|,
name|appPriority
argument_list|)
decl_stmt|;
name|submitAppToRMWithInValidAcl
argument_list|(
name|submitter
argument_list|,
name|submissionContext
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareForAppSubmission (String submitter, String queueName, Priority priority)
specifier|private
name|ApplicationSubmissionContext
name|prepareForAppSubmission
parameter_list|(
name|String
name|submitter
parameter_list|,
name|String
name|queueName
parameter_list|,
name|Priority
name|priority
parameter_list|)
throws|throws
name|Exception
block|{
name|GetNewApplicationRequest
name|newAppRequest
init|=
name|GetNewApplicationRequest
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|ApplicationClientProtocol
name|submitterClient
init|=
name|getRMClientForUser
argument_list|(
name|submitter
argument_list|)
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|submitterClient
operator|.
name|getNewApplication
argument_list|(
name|newAppRequest
argument_list|)
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|Resource
name|resource
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerLaunchContext
name|amContainerSpec
init|=
name|ContainerLaunchContext
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|appSubmissionContext
init|=
name|ApplicationSubmissionContext
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
literal|"applicationName"
argument_list|,
name|queueName
argument_list|,
literal|null
argument_list|,
name|amContainerSpec
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
name|resource
argument_list|,
literal|"applicationType"
argument_list|)
decl_stmt|;
name|appSubmissionContext
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|appSubmissionContext
operator|.
name|setQueue
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|priority
condition|)
block|{
name|appSubmissionContext
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
return|return
name|appSubmissionContext
return|;
block|}
DECL|method|submitAppToRMWithValidAcl (String submitter, ApplicationSubmissionContext appSubmissionContext)
specifier|private
name|void
name|submitAppToRMWithValidAcl
parameter_list|(
name|String
name|submitter
parameter_list|,
name|ApplicationSubmissionContext
name|appSubmissionContext
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|ApplicationClientProtocol
name|submitterClient
init|=
name|getRMClientForUser
argument_list|(
name|submitter
argument_list|)
decl_stmt|;
name|SubmitApplicationRequest
name|submitRequest
init|=
name|SubmitApplicationRequest
operator|.
name|newInstance
argument_list|(
name|appSubmissionContext
argument_list|)
decl_stmt|;
name|submitterClient
operator|.
name|submitApplication
argument_list|(
name|submitRequest
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|waitForState
argument_list|(
name|appSubmissionContext
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
block|}
DECL|method|submitAppToRMWithInValidAcl (String submitter, ApplicationSubmissionContext appSubmissionContext)
specifier|private
name|void
name|submitAppToRMWithInValidAcl
parameter_list|(
name|String
name|submitter
parameter_list|,
name|ApplicationSubmissionContext
name|appSubmissionContext
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|ApplicationClientProtocol
name|submitterClient
init|=
name|getRMClientForUser
argument_list|(
name|submitter
argument_list|)
decl_stmt|;
name|SubmitApplicationRequest
name|submitRequest
init|=
name|SubmitApplicationRequest
operator|.
name|newInstance
argument_list|(
name|appSubmissionContext
argument_list|)
decl_stmt|;
try|try
block|{
name|submitterClient
operator|.
name|submitApplication
argument_list|(
name|submitRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|RemoteException
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyAppPriorityIsAccepted (String submitter, ApplicationId applicationId, int priority)
specifier|private
name|void
name|verifyAppPriorityIsAccepted
parameter_list|(
name|String
name|submitter
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|int
name|priority
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ApplicationClientProtocol
name|submitterClient
init|=
name|getRMClientForUser
argument_list|(
name|submitter
argument_list|)
decl_stmt|;
comment|/**      * If priority is greater than cluster max, RM will auto set to cluster max      * Consider this scenario as a special case.      */
if|if
condition|(
name|priority
operator|>
name|clusterMaxPriority
condition|)
block|{
name|priority
operator|=
name|clusterMaxPriority
expr_stmt|;
block|}
name|GetApplicationReportRequest
name|request
init|=
name|GetApplicationReportRequest
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
try|try
block|{
name|GetApplicationReportResponse
name|response
init|=
name|submitterClient
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|response
operator|.
name|getApplicationReport
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|priority
argument_list|)
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
literal|"Application submission should not fail."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csConf
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
name|QUEUEA
block|,
name|QUEUEB
block|,
name|QUEUEC
block|}
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
literal|50f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEB
argument_list|,
literal|25f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEC
argument_list|,
literal|25f
argument_list|)
expr_stmt|;
name|String
index|[]
name|aclsForA
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|aclsForA
index|[
literal|0
index|]
operator|=
name|QUEUE_A_USER
expr_stmt|;
name|aclsForA
index|[
literal|1
index|]
operator|=
name|QUEUE_A_GROUP
expr_stmt|;
name|csConf
operator|.
name|setPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|maxPriorityQueueA
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|defaultPriorityQueueA
argument_list|)
argument_list|,
name|aclsForA
argument_list|)
expr_stmt|;
name|String
index|[]
name|aclsForB
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|aclsForB
index|[
literal|0
index|]
operator|=
name|QUEUE_B_USER
expr_stmt|;
name|aclsForB
index|[
literal|1
index|]
operator|=
name|QUEUE_B_GROUP
expr_stmt|;
name|csConf
operator|.
name|setPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEB
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|maxPriorityQueueB
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|defaultPriorityQueueB
argument_list|)
argument_list|,
name|aclsForB
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|csConf
return|;
block|}
block|}
end_class

end_unit

