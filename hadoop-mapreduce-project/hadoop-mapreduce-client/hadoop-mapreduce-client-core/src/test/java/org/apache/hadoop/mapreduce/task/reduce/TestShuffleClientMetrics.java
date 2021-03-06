begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task.reduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|reduce
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|JobID
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
name|TaskAttemptID
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
name|metrics2
operator|.
name|MetricsTag
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

begin_comment
comment|/**  * Unit test for {@link TestShuffleClientMetrics}.  */
end_comment

begin_class
DECL|class|TestShuffleClientMetrics
specifier|public
class|class
name|TestShuffleClientMetrics
block|{
DECL|field|TEST_JOB_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_JOB_NAME
init|=
literal|"Test job name"
decl_stmt|;
DECL|field|TEST_JOB_ID
specifier|private
specifier|static
specifier|final
name|String
name|TEST_JOB_ID
init|=
literal|"Test job id"
decl_stmt|;
DECL|field|TEST_TASK_ID
specifier|private
specifier|static
specifier|final
name|String
name|TEST_TASK_ID
init|=
literal|"Test task id"
decl_stmt|;
DECL|field|TEST_USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_USER_NAME
init|=
literal|"Test user name"
decl_stmt|;
annotation|@
name|Test
DECL|method|testShuffleMetricsTags ()
specifier|public
name|void
name|testShuffleMetricsTags
parameter_list|()
block|{
comment|// Set up
name|JobID
name|jobID
init|=
name|mock
argument_list|(
name|JobID
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|jobID
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TEST_JOB_ID
argument_list|)
expr_stmt|;
name|TaskAttemptID
name|reduceId
init|=
name|mock
argument_list|(
name|TaskAttemptID
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|reduceId
operator|.
name|getJobID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|reduceId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TEST_TASK_ID
argument_list|)
expr_stmt|;
name|JobConf
name|jobConf
init|=
name|mock
argument_list|(
name|JobConf
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|jobConf
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TEST_USER_NAME
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|jobConf
operator|.
name|getJobName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TEST_JOB_NAME
argument_list|)
expr_stmt|;
comment|// Act
name|ShuffleClientMetrics
name|shuffleClientMetrics
init|=
name|ShuffleClientMetrics
operator|.
name|create
argument_list|(
name|reduceId
argument_list|,
name|jobConf
argument_list|)
decl_stmt|;
comment|// Assert
name|MetricsTag
name|userMetrics
init|=
name|shuffleClientMetrics
operator|.
name|getMetricsRegistry
argument_list|()
operator|.
name|getTag
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_USER_NAME
argument_list|,
name|userMetrics
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|MetricsTag
name|jobNameMetrics
init|=
name|shuffleClientMetrics
operator|.
name|getMetricsRegistry
argument_list|()
operator|.
name|getTag
argument_list|(
literal|"jobName"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_JOB_NAME
argument_list|,
name|jobNameMetrics
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|MetricsTag
name|jobIdMetrics
init|=
name|shuffleClientMetrics
operator|.
name|getMetricsRegistry
argument_list|()
operator|.
name|getTag
argument_list|(
literal|"jobId"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_JOB_ID
argument_list|,
name|jobIdMetrics
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|MetricsTag
name|taskIdMetrics
init|=
name|shuffleClientMetrics
operator|.
name|getMetricsRegistry
argument_list|()
operator|.
name|getTag
argument_list|(
literal|"taskId"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_TASK_ID
argument_list|,
name|taskIdMetrics
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

