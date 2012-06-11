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
name|mapred
operator|.
name|TaskStatus
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
name|TaskID
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
name|util
operator|.
name|Progress
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
DECL|class|TestShuffleScheduler
specifier|public
class|class
name|TestShuffleScheduler
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Test
DECL|method|testTipFailed ()
specifier|public
name|void
name|testTipFailed
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|job
operator|.
name|setNumMapTasks
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|TaskStatus
name|status
init|=
operator|new
name|TaskStatus
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|getIsMap
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addFetchFailedMap
parameter_list|(
name|TaskAttemptID
name|mapTaskId
parameter_list|)
block|{       }
block|}
decl_stmt|;
name|Progress
name|progress
init|=
operator|new
name|Progress
argument_list|()
decl_stmt|;
name|ShuffleScheduler
name|scheduler
init|=
operator|new
name|ShuffleScheduler
argument_list|(
name|job
argument_list|,
name|status
argument_list|,
literal|null
argument_list|,
name|progress
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JobID
name|jobId
init|=
operator|new
name|JobID
argument_list|()
decl_stmt|;
name|TaskID
name|taskId1
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|tipFailed
argument_list|(
name|taskId1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Progress should be 0.5"
argument_list|,
literal|0.5f
argument_list|,
name|progress
operator|.
name|getProgress
argument_list|()
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|scheduler
operator|.
name|waitUntilDone
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|TaskID
name|taskId0
init|=
operator|new
name|TaskID
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|tipFailed
argument_list|(
name|taskId0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Progress should be 1.0"
argument_list|,
literal|1.0f
argument_list|,
name|progress
operator|.
name|getProgress
argument_list|()
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|waitUntilDone
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

