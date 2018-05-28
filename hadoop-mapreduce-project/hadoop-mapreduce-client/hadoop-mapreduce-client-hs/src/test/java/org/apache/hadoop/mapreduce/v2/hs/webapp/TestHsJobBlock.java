begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
package|package
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
name|hs
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|Counters
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
name|*
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
name|impl
operator|.
name|pb
operator|.
name|JobIdPBImpl
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
name|app
operator|.
name|job
operator|.
name|Task
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
name|app
operator|.
name|webapp
operator|.
name|AMParams
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
name|hs
operator|.
name|CompletedJob
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
name|hs
operator|.
name|HistoryFileManager
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
name|hs
operator|.
name|HistoryFileManager
operator|.
name|HistoryFileInfo
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
name|hs
operator|.
name|JobHistory
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
name|hs
operator|.
name|UnparsedJob
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
name|jobhistory
operator|.
name|JHAdminConfig
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
name|util
operator|.
name|StringHelper
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
name|webapp
operator|.
name|Controller
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
name|webapp
operator|.
name|ResponseInfo
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
name|webapp
operator|.
name|SubView
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
name|webapp
operator|.
name|View
operator|.
name|ViewContext
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
name|webapp
operator|.
name|view
operator|.
name|BlockForTest
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
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
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
name|webapp
operator|.
name|view
operator|.
name|HtmlBlockForTest
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|HashMap
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|any
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
comment|/**  * Test the HsJobBlock generated for oversized jobs in JHS.  */
end_comment

begin_class
DECL|class|TestHsJobBlock
specifier|public
class|class
name|TestHsJobBlock
block|{
annotation|@
name|Test
DECL|method|testHsJobBlockForOversizeJobShouldDisplayWarningMessage ()
specifier|public
name|void
name|testHsJobBlockForOversizeJobShouldDisplayWarningMessage
parameter_list|()
block|{
name|int
name|maxAllowedTaskNum
init|=
literal|100
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setInt
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_LOADED_JOBS_TASKS_MAX
argument_list|,
name|maxAllowedTaskNum
argument_list|)
expr_stmt|;
name|JobHistory
name|jobHistory
init|=
operator|new
name|JobHistoryStubWithAllOversizeJobs
argument_list|(
name|maxAllowedTaskNum
argument_list|)
decl_stmt|;
name|jobHistory
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|Controller
operator|.
name|RequestContext
name|rc
init|=
name|mock
argument_list|(
name|Controller
operator|.
name|RequestContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ViewContext
name|view
init|=
name|mock
argument_list|(
name|ViewContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletRequest
name|req
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rc
operator|.
name|getRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|view
operator|.
name|requestContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rc
argument_list|)
expr_stmt|;
name|HsJobBlock
name|jobBlock
init|=
operator|new
name|HsJobBlock
argument_list|(
name|config
argument_list|,
name|jobHistory
argument_list|,
name|view
argument_list|)
block|{
comment|// override this so that job block can fetch a job id.
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moreParams
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|,
literal|"job_0000_0001"
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
block|}
decl_stmt|;
comment|// set up the test block to render HsJobBLock to
name|OutputStream
name|outputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
name|createBlockToCreateTo
argument_list|(
name|outputStream
argument_list|)
decl_stmt|;
name|jobBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|outputStream
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should display warning message for jobs that have too "
operator|+
literal|"many tasks"
argument_list|,
name|out
operator|.
name|contains
argument_list|(
literal|"Any job larger than "
operator|+
name|maxAllowedTaskNum
operator|+
literal|" will not be loaded"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHsJobBlockForNormalSizeJobShouldNotDisplayWarningMessage ()
specifier|public
name|void
name|testHsJobBlockForNormalSizeJobShouldNotDisplayWarningMessage
parameter_list|()
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
name|setInt
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_LOADED_JOBS_TASKS_MAX
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|JobHistory
name|jobHistory
init|=
operator|new
name|JobHitoryStubWithAllNormalSizeJobs
argument_list|()
decl_stmt|;
name|jobHistory
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|Controller
operator|.
name|RequestContext
name|rc
init|=
name|mock
argument_list|(
name|Controller
operator|.
name|RequestContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ViewContext
name|view
init|=
name|mock
argument_list|(
name|ViewContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletRequest
name|req
init|=
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rc
operator|.
name|getRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|view
operator|.
name|requestContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rc
argument_list|)
expr_stmt|;
name|HsJobBlock
name|jobBlock
init|=
operator|new
name|HsJobBlock
argument_list|(
name|config
argument_list|,
name|jobHistory
argument_list|,
name|view
argument_list|)
block|{
comment|// override this so that the job block can fetch a job id.
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moreParams
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|,
literal|"job_0000_0001"
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
comment|// override this to avoid view context lookup in render()
annotation|@
name|Override
specifier|public
name|ResponseInfo
name|info
parameter_list|(
name|String
name|about
parameter_list|)
block|{
return|return
operator|new
name|ResponseInfo
argument_list|()
operator|.
name|about
argument_list|(
name|about
argument_list|)
return|;
block|}
comment|// override this to avoid view context lookup in render()
annotation|@
name|Override
specifier|public
name|String
name|url
parameter_list|(
name|String
modifier|...
name|parts
parameter_list|)
block|{
return|return
name|StringHelper
operator|.
name|ujoin
argument_list|(
literal|""
argument_list|,
name|parts
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// set up the test block to render HsJobBLock to
name|OutputStream
name|outputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|HtmlBlock
operator|.
name|Block
name|block
init|=
name|createBlockToCreateTo
argument_list|(
name|outputStream
argument_list|)
decl_stmt|;
name|jobBlock
operator|.
name|render
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|block
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|out
init|=
name|outputStream
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should display job overview for the job."
argument_list|,
name|out
operator|.
name|contains
argument_list|(
literal|"ApplicationMaster"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createBlockToCreateTo ( OutputStream outputStream)
specifier|private
specifier|static
name|HtmlBlock
operator|.
name|Block
name|createBlockToCreateTo
parameter_list|(
name|OutputStream
name|outputStream
parameter_list|)
block|{
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|outputStream
argument_list|)
decl_stmt|;
name|HtmlBlock
name|html
init|=
operator|new
name|HtmlBlockForTest
argument_list|()
decl_stmt|;
return|return
operator|new
name|BlockForTest
argument_list|(
name|html
argument_list|,
name|printWriter
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|subView
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|cls
parameter_list|)
block|{       }
block|}
return|;
block|}
empty_stmt|;
comment|/**    * A JobHistory stub that treat all jobs as oversized and therefore will    * not parse their job history files but return a UnparseJob instance.    */
DECL|class|JobHistoryStubWithAllOversizeJobs
specifier|static
class|class
name|JobHistoryStubWithAllOversizeJobs
extends|extends
name|JobHistory
block|{
DECL|field|maxAllowedTaskNum
specifier|private
specifier|final
name|int
name|maxAllowedTaskNum
decl_stmt|;
DECL|method|JobHistoryStubWithAllOversizeJobs (int maxAllowedTaskNum)
specifier|public
name|JobHistoryStubWithAllOversizeJobs
parameter_list|(
name|int
name|maxAllowedTaskNum
parameter_list|)
block|{
name|this
operator|.
name|maxAllowedTaskNum
operator|=
name|maxAllowedTaskNum
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createHistoryFileManager ()
specifier|protected
name|HistoryFileManager
name|createHistoryFileManager
parameter_list|()
block|{
name|HistoryFileManager
name|historyFileManager
decl_stmt|;
try|try
block|{
name|HistoryFileInfo
name|historyFileInfo
init|=
name|createUnparsedJobHistoryFileInfo
argument_list|(
name|maxAllowedTaskNum
argument_list|)
decl_stmt|;
name|historyFileManager
operator|=
name|mock
argument_list|(
name|HistoryFileManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|historyFileManager
operator|.
name|getFileInfo
argument_list|(
name|any
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|historyFileInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// this should never happen
name|historyFileManager
operator|=
name|super
operator|.
name|createHistoryFileManager
argument_list|()
expr_stmt|;
block|}
return|return
name|historyFileManager
return|;
block|}
DECL|method|createUnparsedJobHistoryFileInfo ( int maxAllowedTaskNum)
specifier|private
specifier|static
name|HistoryFileInfo
name|createUnparsedJobHistoryFileInfo
parameter_list|(
name|int
name|maxAllowedTaskNum
parameter_list|)
throws|throws
name|IOException
block|{
name|HistoryFileInfo
name|fileInfo
init|=
name|mock
argument_list|(
name|HistoryFileInfo
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// create an instance of UnparsedJob for a large job
name|UnparsedJob
name|unparsedJob
init|=
name|mock
argument_list|(
name|UnparsedJob
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|unparsedJob
operator|.
name|getMaxTasksAllowed
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxAllowedTaskNum
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|unparsedJob
operator|.
name|getTotalMaps
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxAllowedTaskNum
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|unparsedJob
operator|.
name|getTotalReduces
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxAllowedTaskNum
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fileInfo
operator|.
name|loadJob
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|unparsedJob
argument_list|)
expr_stmt|;
return|return
name|fileInfo
return|;
block|}
block|}
comment|/**    * A JobHistory stub that treats all jobs as normal size and therefore will    * return a CompletedJob on HistoryFileInfo.loadJob().    */
DECL|class|JobHitoryStubWithAllNormalSizeJobs
specifier|static
class|class
name|JobHitoryStubWithAllNormalSizeJobs
extends|extends
name|JobHistory
block|{
annotation|@
name|Override
DECL|method|createHistoryFileManager ()
specifier|public
name|HistoryFileManager
name|createHistoryFileManager
parameter_list|()
block|{
name|HistoryFileManager
name|historyFileManager
decl_stmt|;
try|try
block|{
name|HistoryFileInfo
name|historyFileInfo
init|=
name|createParsedJobHistoryFileInfo
argument_list|()
decl_stmt|;
name|historyFileManager
operator|=
name|mock
argument_list|(
name|HistoryFileManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|historyFileManager
operator|.
name|getFileInfo
argument_list|(
name|any
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|historyFileInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// this should never happen
name|historyFileManager
operator|=
name|super
operator|.
name|createHistoryFileManager
argument_list|()
expr_stmt|;
block|}
return|return
name|historyFileManager
return|;
block|}
DECL|method|createParsedJobHistoryFileInfo ()
specifier|private
specifier|static
name|HistoryFileInfo
name|createParsedJobHistoryFileInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|HistoryFileInfo
name|fileInfo
init|=
name|mock
argument_list|(
name|HistoryFileInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|CompletedJob
name|job
init|=
name|createFakeCompletedJob
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|fileInfo
operator|.
name|loadJob
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|job
argument_list|)
expr_stmt|;
return|return
name|fileInfo
return|;
block|}
DECL|method|createFakeCompletedJob ()
specifier|private
specifier|static
name|CompletedJob
name|createFakeCompletedJob
parameter_list|()
block|{
name|CompletedJob
name|job
init|=
name|mock
argument_list|(
name|CompletedJob
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getTotalMaps
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getCompletedMaps
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getTotalReduces
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getCompletedReduces
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|JobId
name|jobId
init|=
name|createFakeJobId
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|JobReport
name|jobReport
init|=
name|mock
argument_list|(
name|JobReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|jobReport
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|jobReport
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|jobReport
operator|.
name|getFinishTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getReport
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jobReport
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getAMInfos
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|AMInfo
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"fake completed job"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getQueueName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"junit"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobState
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getAllCounters
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Counters
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getTasks
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
DECL|method|createFakeJobId ()
specifier|private
specifier|static
name|JobId
name|createFakeJobId
parameter_list|()
block|{
name|JobId
name|jobId
init|=
operator|new
name|JobIdPBImpl
argument_list|()
decl_stmt|;
name|jobId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
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
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appId
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|jobId
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
return|return
name|jobId
return|;
block|}
block|}
block|}
end_class

end_unit

