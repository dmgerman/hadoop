begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|File
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServlet
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
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|Before
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
name|http
operator|.
name|HttpServer2
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
DECL|class|TestJobEndNotifier
specifier|public
class|class
name|TestJobEndNotifier
block|{
DECL|field|server
name|HttpServer2
name|server
decl_stmt|;
DECL|field|baseUrl
name|URL
name|baseUrl
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|JobEndServlet
specifier|public
specifier|static
class|class
name|JobEndServlet
extends|extends
name|HttpServlet
block|{
DECL|field|calledTimes
specifier|public
specifier|static
specifier|volatile
name|int
name|calledTimes
init|=
literal|0
decl_stmt|;
DECL|field|requestUri
specifier|public
specifier|static
name|URI
name|requestUri
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|InputStreamReader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|calledTimes
operator|++
expr_stmt|;
try|try
block|{
name|requestUri
operator|=
operator|new
name|URI
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|,
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{       }
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Servlet that delays requests for a long time
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|DelayServlet
specifier|public
specifier|static
class|class
name|DelayServlet
extends|extends
name|HttpServlet
block|{
DECL|field|calledTimes
specifier|public
specifier|static
specifier|volatile
name|int
name|calledTimes
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|boolean
name|timedOut
init|=
literal|false
decl_stmt|;
name|calledTimes
operator|++
expr_stmt|;
try|try
block|{
comment|// Sleep for a long time
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|timedOut
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"DelayServlet should be interrupted"
argument_list|,
name|timedOut
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Servlet that fails all requests into it
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|FailServlet
specifier|public
specifier|static
class|class
name|FailServlet
extends|extends
name|HttpServlet
block|{
DECL|field|calledTimes
specifier|public
specifier|static
specifier|volatile
name|int
name|calledTimes
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|calledTimes
operator|++
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"I am failing!"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"build.webapps"
argument_list|,
literal|"build/webapps"
argument_list|)
operator|+
literal|"/test"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"http://localhost:0"
argument_list|)
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"delay"
argument_list|,
literal|"/delay"
argument_list|,
name|DelayServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"jobend"
argument_list|,
literal|"/jobend"
argument_list|,
name|JobEndServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"fail"
argument_list|,
literal|"/fail"
argument_list|,
name|FailServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|port
init|=
name|server
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|baseUrl
operator|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|JobEndServlet
operator|.
name|calledTimes
operator|=
literal|0
expr_stmt|;
name|JobEndServlet
operator|.
name|requestUri
operator|=
literal|null
expr_stmt|;
name|DelayServlet
operator|.
name|calledTimes
operator|=
literal|0
expr_stmt|;
name|FailServlet
operator|.
name|calledTimes
operator|=
literal|0
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
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Basic validation for localRunnerNotification.    */
annotation|@
name|Test
DECL|method|testLocalJobRunnerUriSubstitution ()
specifier|public
name|void
name|testLocalJobRunnerUriSubstitution
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|JobStatus
name|jobStatus
init|=
name|createTestJobStatus
argument_list|(
literal|"job_20130313155005308_0001"
argument_list|,
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
decl_stmt|;
name|JobConf
name|jobConf
init|=
name|createTestJobConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|0
argument_list|,
name|baseUrl
operator|+
literal|"jobend?jobid=$jobId&status=$jobStatus"
argument_list|)
decl_stmt|;
name|JobEndNotifier
operator|.
name|localRunnerNotification
argument_list|(
name|jobConf
argument_list|,
name|jobStatus
argument_list|)
expr_stmt|;
comment|// No need to wait for the notification to go thru since calls are
comment|// synchronous
comment|// Validate params
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|JobEndServlet
operator|.
name|calledTimes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"jobid=job_20130313155005308_0001&status=SUCCEEDED"
argument_list|,
name|JobEndServlet
operator|.
name|requestUri
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate job.end.retry.attempts for the localJobRunner.    */
annotation|@
name|Test
DECL|method|testLocalJobRunnerRetryCount ()
specifier|public
name|void
name|testLocalJobRunnerRetryCount
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|int
name|retryAttempts
init|=
literal|3
decl_stmt|;
name|JobStatus
name|jobStatus
init|=
name|createTestJobStatus
argument_list|(
literal|"job_20130313155005308_0001"
argument_list|,
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
decl_stmt|;
name|JobConf
name|jobConf
init|=
name|createTestJobConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|retryAttempts
argument_list|,
name|baseUrl
operator|+
literal|"fail"
argument_list|)
decl_stmt|;
name|JobEndNotifier
operator|.
name|localRunnerNotification
argument_list|(
name|jobConf
argument_list|,
name|jobStatus
argument_list|)
expr_stmt|;
comment|// Validate params
name|assertEquals
argument_list|(
name|retryAttempts
operator|+
literal|1
argument_list|,
name|FailServlet
operator|.
name|calledTimes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate that the notification times out after reaching    * mapreduce.job.end-notification.timeout.    */
annotation|@
name|Test
DECL|method|testNotificationTimeout ()
specifier|public
name|void
name|testNotificationTimeout
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Reduce the timeout to 1 second
name|conf
operator|.
name|setInt
argument_list|(
literal|"mapreduce.job.end-notification.timeout"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|JobStatus
name|jobStatus
init|=
name|createTestJobStatus
argument_list|(
literal|"job_20130313155005308_0001"
argument_list|,
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
decl_stmt|;
name|JobConf
name|jobConf
init|=
name|createTestJobConf
argument_list|(
name|conf
argument_list|,
literal|0
argument_list|,
name|baseUrl
operator|+
literal|"delay"
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|JobEndNotifier
operator|.
name|localRunnerNotification
argument_list|(
name|jobConf
argument_list|,
name|jobStatus
argument_list|)
expr_stmt|;
name|long
name|elapsedTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
decl_stmt|;
comment|// Validate params
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DelayServlet
operator|.
name|calledTimes
argument_list|)
expr_stmt|;
comment|// Make sure we timed out with time slightly above 1 second
comment|// (default timeout is in terms of minutes, so we'll catch the problem)
name|assertTrue
argument_list|(
name|elapsedTime
operator|<
literal|2000
argument_list|)
expr_stmt|;
block|}
DECL|method|createTestJobStatus (String jobId, int state)
specifier|private
specifier|static
name|JobStatus
name|createTestJobStatus
parameter_list|(
name|String
name|jobId
parameter_list|,
name|int
name|state
parameter_list|)
block|{
return|return
operator|new
name|JobStatus
argument_list|(
name|JobID
operator|.
name|forName
argument_list|(
name|jobId
argument_list|)
argument_list|,
literal|0.5f
argument_list|,
literal|0.0f
argument_list|,
name|state
argument_list|,
literal|"root"
argument_list|,
literal|"TestJobEndNotifier"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|createTestJobConf ( Configuration conf, int retryAttempts, String notificationUri)
specifier|private
specifier|static
name|JobConf
name|createTestJobConf
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|retryAttempts
parameter_list|,
name|String
name|notificationUri
parameter_list|)
block|{
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|jobConf
operator|.
name|setInt
argument_list|(
literal|"job.end.retry.attempts"
argument_list|,
name|retryAttempts
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
literal|"job.end.retry.interval"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setJobEndNotificationURI
argument_list|(
name|notificationUri
argument_list|)
expr_stmt|;
return|return
name|jobConf
return|;
block|}
block|}
end_class

end_unit

