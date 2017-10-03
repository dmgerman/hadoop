begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_class
DECL|class|TestJobSummary
specifier|public
class|class
name|TestJobSummary
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestJobSummary
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|summary
specifier|private
name|JobSummary
name|summary
init|=
operator|new
name|JobSummary
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
block|{
name|JobId
name|mockJobId
init|=
name|mock
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJobId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testJobId"
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setJobId
argument_list|(
name|mockJobId
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setJobSubmitTime
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setJobLaunchTime
argument_list|(
literal|3L
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setFirstMapTaskLaunchTime
argument_list|(
literal|4L
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setFirstReduceTaskLaunchTime
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setJobFinishTime
argument_list|(
literal|6L
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setNumSucceededMaps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setNumFailedMaps
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setNumSucceededReduces
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setNumFailedReduces
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setNumKilledMaps
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setNumKilledReduces
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setUser
argument_list|(
literal|"testUser"
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setQueue
argument_list|(
literal|"testQueue"
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setJobStatus
argument_list|(
literal|"testJobStatus"
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setMapSlotSeconds
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setReduceSlotSeconds
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|summary
operator|.
name|setJobName
argument_list|(
literal|"testName"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEscapeJobSummary ()
specifier|public
name|void
name|testEscapeJobSummary
parameter_list|()
block|{
comment|// verify newlines are escaped
name|summary
operator|.
name|setJobName
argument_list|(
literal|"aa\rbb\ncc\r\ndd"
argument_list|)
expr_stmt|;
name|String
name|out
init|=
name|summary
operator|.
name|getJobSummaryString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"summary: "
operator|+
name|out
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"\r"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|out
operator|.
name|contains
argument_list|(
literal|"aa\\rbb\\ncc\\r\\ndd"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

