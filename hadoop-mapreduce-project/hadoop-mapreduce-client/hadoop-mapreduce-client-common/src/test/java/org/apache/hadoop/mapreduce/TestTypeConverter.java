begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobState
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskState
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationIdPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationReportPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationResourceUsageReportPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|QueueInfoPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ResourcePBImpl
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
name|QueueState
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
name|when
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
name|Mockito
import|;
end_import

begin_class
DECL|class|TestTypeConverter
specifier|public
class|class
name|TestTypeConverter
block|{
annotation|@
name|Test
DECL|method|testEnums ()
specifier|public
name|void
name|testEnums
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|YarnApplicationState
name|applicationState
range|:
name|YarnApplicationState
operator|.
name|values
argument_list|()
control|)
block|{
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|applicationState
argument_list|,
name|FinalApplicationStatus
operator|.
name|FAILED
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TaskType
name|taskType
range|:
name|TaskType
operator|.
name|values
argument_list|()
control|)
block|{
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|taskType
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|JobState
name|jobState
range|:
name|JobState
operator|.
name|values
argument_list|()
control|)
block|{
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobState
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|QueueState
name|queueState
range|:
name|QueueState
operator|.
name|values
argument_list|()
control|)
block|{
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|queueState
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TaskState
name|taskState
range|:
name|TaskState
operator|.
name|values
argument_list|()
control|)
block|{
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|taskState
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFromYarn ()
specifier|public
name|void
name|testFromYarn
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|appStartTime
init|=
literal|612354
decl_stmt|;
name|YarnApplicationState
name|state
init|=
name|YarnApplicationState
operator|.
name|RUNNING
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
operator|new
name|ApplicationIdPBImpl
argument_list|()
decl_stmt|;
name|ApplicationReportPBImpl
name|applicationReport
init|=
operator|new
name|ApplicationReportPBImpl
argument_list|()
decl_stmt|;
name|applicationReport
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|applicationReport
operator|.
name|setYarnApplicationState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|applicationReport
operator|.
name|setStartTime
argument_list|(
name|appStartTime
argument_list|)
expr_stmt|;
name|applicationReport
operator|.
name|setUser
argument_list|(
literal|"TestTypeConverter-user"
argument_list|)
expr_stmt|;
name|ApplicationResourceUsageReportPBImpl
name|appUsageRpt
init|=
operator|new
name|ApplicationResourceUsageReportPBImpl
argument_list|()
decl_stmt|;
name|ResourcePBImpl
name|r
init|=
operator|new
name|ResourcePBImpl
argument_list|()
decl_stmt|;
name|r
operator|.
name|setMemory
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setNeededResources
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setNumReservedContainers
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setNumUsedContainers
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setReservedResources
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setUsedResources
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|applicationReport
operator|.
name|setApplicationResourceUsageReport
argument_list|(
name|appUsageRpt
argument_list|)
expr_stmt|;
name|JobStatus
name|jobStatus
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|applicationReport
argument_list|,
literal|"dummy-jobfile"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appStartTime
argument_list|,
name|jobStatus
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|state
operator|.
name|toString
argument_list|()
argument_list|,
name|jobStatus
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFromYarnApplicationReport ()
specifier|public
name|void
name|testFromYarnApplicationReport
parameter_list|()
block|{
name|ApplicationId
name|mockAppId
init|=
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockAppId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|12345L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockAppId
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|6789
argument_list|)
expr_stmt|;
name|ApplicationReport
name|mockReport
init|=
name|mock
argument_list|(
name|ApplicationReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockReport
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"dummy-tracking-url"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockReport
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockAppId
argument_list|)
expr_stmt|;
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
name|KILLED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockReport
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"dummy-user"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockReport
operator|.
name|getQueue
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"dummy-queue"
argument_list|)
expr_stmt|;
name|String
name|jobFile
init|=
literal|"dummy-path/job.xml"
decl_stmt|;
name|ApplicationResourceUsageReportPBImpl
name|appUsageRpt
init|=
operator|new
name|ApplicationResourceUsageReportPBImpl
argument_list|()
decl_stmt|;
name|ResourcePBImpl
name|r
init|=
operator|new
name|ResourcePBImpl
argument_list|()
decl_stmt|;
name|r
operator|.
name|setMemory
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setNeededResources
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setNumReservedContainers
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setNumUsedContainers
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setReservedResources
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|appUsageRpt
operator|.
name|setUsedResources
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appUsageRpt
argument_list|)
expr_stmt|;
name|JobStatus
name|status
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|mockReport
argument_list|,
name|jobFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"fromYarn returned null status"
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"jobFile set incorrectly"
argument_list|,
literal|"dummy-path/job.xml"
argument_list|,
name|status
operator|.
name|getJobFile
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"queue set incorrectly"
argument_list|,
literal|"dummy-queue"
argument_list|,
name|status
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"trackingUrl set incorrectly"
argument_list|,
literal|"dummy-tracking-url"
argument_list|,
name|status
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"user set incorrectly"
argument_list|,
literal|"dummy-user"
argument_list|,
name|status
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"schedulingInfo set incorrectly"
argument_list|,
literal|"dummy-tracking-url"
argument_list|,
name|status
operator|.
name|getSchedulingInfo
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"jobId set incorrectly"
argument_list|,
literal|6789
argument_list|,
name|status
operator|.
name|getJobID
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"state set incorrectly"
argument_list|,
name|JobStatus
operator|.
name|State
operator|.
name|KILLED
argument_list|,
name|status
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"needed mem info set incorrectly"
argument_list|,
literal|2048
argument_list|,
name|status
operator|.
name|getNeededMem
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"num rsvd slots info set incorrectly"
argument_list|,
literal|1
argument_list|,
name|status
operator|.
name|getNumReservedSlots
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"num used slots info set incorrectly"
argument_list|,
literal|3
argument_list|,
name|status
operator|.
name|getNumUsedSlots
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"rsvd mem info set incorrectly"
argument_list|,
literal|2048
argument_list|,
name|status
operator|.
name|getReservedMem
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"used mem info set incorrectly"
argument_list|,
literal|2048
argument_list|,
name|status
operator|.
name|getUsedMem
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFromYarnQueueInfo ()
specifier|public
name|void
name|testFromYarnQueueInfo
parameter_list|()
block|{
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
operator|new
name|QueueInfoPBImpl
argument_list|()
decl_stmt|;
name|queueInfo
operator|.
name|setQueueState
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
name|QueueState
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|QueueInfo
name|returned
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|queueInfo
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"queueInfo translation didn't work."
argument_list|,
name|returned
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|queueInfo
operator|.
name|getQueueState
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that child queues are converted too during conversion of the parent    * queue    */
annotation|@
name|Test
DECL|method|testFromYarnQueue ()
specifier|public
name|void
name|testFromYarnQueue
parameter_list|()
block|{
comment|//Define child queue
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
name|child
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
name|child
operator|.
name|getQueueState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|QueueState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//Define parent queue
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
name|List
argument_list|<
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
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
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
argument_list|>
argument_list|()
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
comment|//Add one child
name|Mockito
operator|.
name|when
argument_list|(
name|queueInfo
operator|.
name|getChildQueues
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|children
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|queueInfo
operator|.
name|getQueueState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|QueueState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//Call the function we're testing
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|QueueInfo
name|returned
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|queueInfo
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
comment|//Verify that the converted queue has the 1 child we had added
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"QueueInfo children weren't properly converted"
argument_list|,
name|returned
operator|.
name|getQueueChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

