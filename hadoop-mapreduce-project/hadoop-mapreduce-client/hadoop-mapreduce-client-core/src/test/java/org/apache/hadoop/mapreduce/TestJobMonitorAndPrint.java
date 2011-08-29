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
name|Matchers
operator|.
name|anyInt
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
name|doAnswer
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
name|spy
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
name|ByteArrayOutputStream
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
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|mapreduce
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|Layout
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
name|Logger
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
name|WriterAppender
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
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_comment
comment|/**  * Test to make sure that command line output for   * job monitoring is correct and prints 100% for map and reduce before   * successful completion.  */
end_comment

begin_class
DECL|class|TestJobMonitorAndPrint
specifier|public
class|class
name|TestJobMonitorAndPrint
extends|extends
name|TestCase
block|{
DECL|field|job
specifier|private
name|Job
name|job
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|clientProtocol
specifier|private
name|ClientProtocol
name|clientProtocol
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|clientProtocol
operator|=
name|mock
argument_list|(
name|ClientProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|Cluster
name|cluster
init|=
name|mock
argument_list|(
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|cluster
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|cluster
operator|.
name|getClient
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clientProtocol
argument_list|)
expr_stmt|;
name|JobStatus
name|jobStatus
init|=
operator|new
name|JobStatus
argument_list|(
operator|new
name|JobID
argument_list|(
literal|"job_000"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
name|State
operator|.
name|RUNNING
argument_list|,
name|JobPriority
operator|.
name|HIGH
argument_list|,
literal|"tmp-user"
argument_list|,
literal|"tmp-jobname"
argument_list|,
literal|"tmp-jobfile"
argument_list|,
literal|"tmp-url"
argument_list|)
decl_stmt|;
name|job
operator|=
operator|new
name|Job
argument_list|(
name|cluster
argument_list|,
name|jobStatus
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|job
operator|=
name|spy
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJobMonitorAndPrint ()
specifier|public
name|void
name|testJobMonitorAndPrint
parameter_list|()
throws|throws
name|Exception
block|{
name|JobStatus
name|jobStatus_1
init|=
operator|new
name|JobStatus
argument_list|(
operator|new
name|JobID
argument_list|(
literal|"job_000"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1f
argument_list|,
literal|0.1f
argument_list|,
literal|0.1f
argument_list|,
literal|0f
argument_list|,
name|State
operator|.
name|RUNNING
argument_list|,
name|JobPriority
operator|.
name|HIGH
argument_list|,
literal|"tmp-user"
argument_list|,
literal|"tmp-jobname"
argument_list|,
literal|"tmp-jobfile"
argument_list|,
literal|"tmp-url"
argument_list|)
decl_stmt|;
name|JobStatus
name|jobStatus_2
init|=
operator|new
name|JobStatus
argument_list|(
operator|new
name|JobID
argument_list|(
literal|"job_000"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|JobPriority
operator|.
name|HIGH
argument_list|,
literal|"tmp-user"
argument_list|,
literal|"tmp-jobname"
argument_list|,
literal|"tmp-jobfile"
argument_list|,
literal|"tmp-url"
argument_list|)
decl_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|TaskCompletionEvent
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TaskCompletionEvent
index|[]
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
operator|new
name|TaskCompletionEvent
index|[
literal|0
index|]
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|job
argument_list|)
operator|.
name|getTaskCompletionEvents
argument_list|(
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|clientProtocol
operator|.
name|getJobStatus
argument_list|(
name|any
argument_list|(
name|JobID
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jobStatus_1
argument_list|,
name|jobStatus_2
argument_list|)
expr_stmt|;
comment|// setup the logger to capture all logs
name|Layout
name|layout
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getAppender
argument_list|(
literal|"stdout"
argument_list|)
operator|.
name|getLayout
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|WriterAppender
name|appender
init|=
operator|new
name|WriterAppender
argument_list|(
name|layout
argument_list|,
name|os
argument_list|)
decl_stmt|;
name|appender
operator|.
name|setThreshold
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|Logger
name|qlogger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
name|qlogger
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|job
operator|.
name|monitorAndPrintJob
argument_list|()
expr_stmt|;
name|qlogger
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|LineNumberReader
name|r
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|os
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|boolean
name|foundHundred
init|=
literal|false
decl_stmt|;
name|boolean
name|foundComplete
init|=
literal|false
decl_stmt|;
name|String
name|match_1
init|=
literal|"map 100% reduce 100%"
decl_stmt|;
name|String
name|match_2
init|=
literal|"completed successfully"
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|foundHundred
operator|=
name|line
operator|.
name|contains
argument_list|(
name|match_1
argument_list|)
expr_stmt|;
if|if
condition|(
name|foundHundred
condition|)
break|break;
block|}
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|foundComplete
operator|=
name|line
operator|.
name|contains
argument_list|(
name|match_2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|foundHundred
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|foundComplete
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

