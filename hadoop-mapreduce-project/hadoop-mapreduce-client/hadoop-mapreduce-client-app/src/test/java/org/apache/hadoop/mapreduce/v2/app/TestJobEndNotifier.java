begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
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
name|app
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doNothing
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
name|Proxy
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
name|HttpServer
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
name|JobContext
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
name|MRJobConfig
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
name|JobReport
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
name|app
operator|.
name|client
operator|.
name|ClientService
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
name|JobStateInternal
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
name|event
operator|.
name|JobEvent
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
name|event
operator|.
name|JobEventType
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
name|impl
operator|.
name|JobImpl
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
name|rm
operator|.
name|ContainerAllocator
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
name|rm
operator|.
name|ContainerAllocatorEvent
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
name|rm
operator|.
name|RMCommunicator
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
name|rm
operator|.
name|RMHeartbeatHandler
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

begin_comment
comment|/**  * Tests job end notification  *  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|TestJobEndNotifier
specifier|public
class|class
name|TestJobEndNotifier
extends|extends
name|JobEndNotifier
block|{
comment|//Test maximum retries is capped by MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS
DECL|method|testNumRetries (Configuration conf)
specifier|private
name|void
name|testNumRetries
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_ATTEMPTS
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected numTries to be 0, but was "
operator|+
name|numTries
argument_list|,
name|numTries
operator|==
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected numTries to be 1, but was "
operator|+
name|numTries
argument_list|,
name|numTries
operator|==
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected numTries to be 11, but was "
operator|+
name|numTries
argument_list|,
name|numTries
operator|==
literal|11
argument_list|)
expr_stmt|;
comment|//11 because number of _retries_ is 10
block|}
comment|//Test maximum retry interval is capped by
comment|//MR_JOB_END_NOTIFICATION_MAX_RETRY_INTERVAL
DECL|method|testWaitInterval (Configuration conf)
specifier|private
name|void
name|testWaitInterval
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_RETRY_INTERVAL
argument_list|,
literal|"5000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_INTERVAL
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected waitInterval to be 1000, but was "
operator|+
name|waitInterval
argument_list|,
name|waitInterval
operator|==
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_INTERVAL
argument_list|,
literal|"10000"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected waitInterval to be 5000, but was "
operator|+
name|waitInterval
argument_list|,
name|waitInterval
operator|==
literal|5000
argument_list|)
expr_stmt|;
comment|//Test negative numbers are set to default
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_INTERVAL
argument_list|,
literal|"-10"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected waitInterval to be 5000, but was "
operator|+
name|waitInterval
argument_list|,
name|waitInterval
operator|==
literal|5000
argument_list|)
expr_stmt|;
block|}
DECL|method|testTimeout (Configuration conf)
specifier|private
name|void
name|testTimeout
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_TIMEOUT
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected timeout to be 1000, but was "
operator|+
name|timeout
argument_list|,
name|timeout
operator|==
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|testProxyConfiguration (Configuration conf)
specifier|private
name|void
name|testProxyConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_PROXY
argument_list|,
literal|"somehost"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Proxy shouldn't be set because port wasn't specified"
argument_list|,
name|proxyToUse
operator|.
name|type
argument_list|()
operator|==
name|Proxy
operator|.
name|Type
operator|.
name|DIRECT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_PROXY
argument_list|,
literal|"somehost:someport"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Proxy shouldn't be set because port wasn't numeric"
argument_list|,
name|proxyToUse
operator|.
name|type
argument_list|()
operator|==
name|Proxy
operator|.
name|Type
operator|.
name|DIRECT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_PROXY
argument_list|,
literal|"somehost:1000"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Proxy should have been set but wasn't "
argument_list|,
name|proxyToUse
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"HTTP @ somehost:1000"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_PROXY
argument_list|,
literal|"socks@somehost:1000"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Proxy should have been socks but wasn't "
argument_list|,
name|proxyToUse
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"SOCKS @ somehost:1000"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_PROXY
argument_list|,
literal|"SOCKS@somehost:1000"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Proxy should have been socks but wasn't "
argument_list|,
name|proxyToUse
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"SOCKS @ somehost:1000"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_PROXY
argument_list|,
literal|"sfafn@somehost:1000"
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Proxy should have been http but wasn't "
argument_list|,
name|proxyToUse
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"HTTP @ somehost:1000"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that setting parameters has the desired effect    */
annotation|@
name|Test
DECL|method|checkConfiguration ()
specifier|public
name|void
name|checkConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|testNumRetries
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testWaitInterval
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testTimeout
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testProxyConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|field|notificationCount
specifier|protected
name|int
name|notificationCount
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|notifyURLOnce ()
specifier|protected
name|boolean
name|notifyURLOnce
parameter_list|()
block|{
name|boolean
name|success
init|=
name|super
operator|.
name|notifyURLOnce
argument_list|()
decl_stmt|;
name|notificationCount
operator|++
expr_stmt|;
return|return
name|success
return|;
block|}
comment|//Check retries happen as intended
annotation|@
name|Test
DECL|method|testNotifyRetries ()
specifier|public
name|void
name|testNotifyRetries
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
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_URL
argument_list|,
literal|"http://nonexistent"
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
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|this
operator|.
name|notificationCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|notify
argument_list|(
name|jobReport
argument_list|)
expr_stmt|;
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Only 1 try was expected but was : "
operator|+
name|this
operator|.
name|notificationCount
argument_list|,
name|this
operator|.
name|notificationCount
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should have taken more than 5 seconds it took "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|,
name|endTime
operator|-
name|startTime
operator|>
literal|5000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_ATTEMPTS
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_INTERVAL
argument_list|,
literal|"3000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_RETRY_INTERVAL
argument_list|,
literal|"3000"
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|notificationCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|notify
argument_list|(
name|jobReport
argument_list|)
expr_stmt|;
name|endTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Only 3 retries were expected but was : "
operator|+
name|this
operator|.
name|notificationCount
argument_list|,
name|this
operator|.
name|notificationCount
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should have taken more than 9 seconds it took "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|,
name|endTime
operator|-
name|startTime
operator|>
literal|9000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotificationOnLastRetryNormalShutdown ()
specifier|public
name|void
name|testNotificationOnLastRetryNormalShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpServer
name|server
init|=
name|startHttpServer
argument_list|()
decl_stmt|;
comment|// Act like it is the second attempt. Default max attempts is 2
name|MRApp
name|app
init|=
name|spy
argument_list|(
operator|new
name|MRAppWithCustomContainerAllocator
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|app
argument_list|)
operator|.
name|sysexit
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|MR_JOB_END_NOTIFICATION_URL
argument_list|,
name|JobEndServlet
operator|.
name|baseUrl
operator|+
literal|"jobend?jobid=$jobId&status=$jobStatus"
argument_list|)
expr_stmt|;
name|JobImpl
name|job
init|=
operator|(
name|JobImpl
operator|)
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|app
operator|.
name|waitForInternalState
argument_list|(
name|job
argument_list|,
name|JobStateInternal
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
comment|// Unregistration succeeds: successfullyUnregistered is set
name|app
operator|.
name|shutDownJob
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|app
operator|.
name|isLastAMRetry
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|JobEndServlet
operator|.
name|calledTimes
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"jobid="
operator|+
name|job
operator|.
name|getID
argument_list|()
operator|+
literal|"&status=SUCCEEDED"
argument_list|,
name|JobEndServlet
operator|.
name|requestUri
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JobState
operator|.
name|SUCCEEDED
operator|.
name|toString
argument_list|()
argument_list|,
name|JobEndServlet
operator|.
name|foundJobState
argument_list|)
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAbsentNotificationOnNotLastRetryUnregistrationFailure ()
specifier|public
name|void
name|testAbsentNotificationOnNotLastRetryUnregistrationFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpServer
name|server
init|=
name|startHttpServer
argument_list|()
decl_stmt|;
name|MRApp
name|app
init|=
name|spy
argument_list|(
operator|new
name|MRAppWithCustomContainerAllocator
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|app
argument_list|)
operator|.
name|sysexit
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|MR_JOB_END_NOTIFICATION_URL
argument_list|,
name|JobEndServlet
operator|.
name|baseUrl
operator|+
literal|"jobend?jobid=$jobId&status=$jobStatus"
argument_list|)
expr_stmt|;
name|JobImpl
name|job
init|=
operator|(
name|JobImpl
operator|)
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobEvent
argument_list|(
name|app
operator|.
name|getJobId
argument_list|()
argument_list|,
name|JobEventType
operator|.
name|JOB_AM_REBOOT
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForInternalState
argument_list|(
name|job
argument_list|,
name|JobStateInternal
operator|.
name|REBOOT
argument_list|)
expr_stmt|;
comment|// Now shutdown.
comment|// Unregistration fails: isLastAMRetry is recalculated, this is not
name|app
operator|.
name|shutDownJob
argument_list|()
expr_stmt|;
comment|// Not the last AM attempt. So user should that the job is still running.
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|app
operator|.
name|isLastAMRetry
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|JobEndServlet
operator|.
name|calledTimes
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|JobEndServlet
operator|.
name|requestUri
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|JobEndServlet
operator|.
name|foundJobState
argument_list|)
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotificationOnLastRetryUnregistrationFailure ()
specifier|public
name|void
name|testNotificationOnLastRetryUnregistrationFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpServer
name|server
init|=
name|startHttpServer
argument_list|()
decl_stmt|;
name|MRApp
name|app
init|=
name|spy
argument_list|(
operator|new
name|MRAppWithCustomContainerAllocator
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|app
argument_list|)
operator|.
name|sysexit
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|MR_JOB_END_NOTIFICATION_URL
argument_list|,
name|JobEndServlet
operator|.
name|baseUrl
operator|+
literal|"jobend?jobid=$jobId&status=$jobStatus"
argument_list|)
expr_stmt|;
name|JobImpl
name|job
init|=
operator|(
name|JobImpl
operator|)
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobEvent
argument_list|(
name|app
operator|.
name|getJobId
argument_list|()
argument_list|,
name|JobEventType
operator|.
name|JOB_AM_REBOOT
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForInternalState
argument_list|(
name|job
argument_list|,
name|JobStateInternal
operator|.
name|REBOOT
argument_list|)
expr_stmt|;
comment|// Now shutdown. User should see FAILED state.
comment|// Unregistration fails: isLastAMRetry is recalculated, this is
name|app
operator|.
name|shutDownJob
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|app
operator|.
name|isLastAMRetry
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|JobEndServlet
operator|.
name|calledTimes
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"jobid="
operator|+
name|job
operator|.
name|getID
argument_list|()
operator|+
literal|"&status=FAILED"
argument_list|,
name|JobEndServlet
operator|.
name|requestUri
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JobState
operator|.
name|FAILED
operator|.
name|toString
argument_list|()
argument_list|,
name|JobEndServlet
operator|.
name|foundJobState
argument_list|)
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|startHttpServer ()
specifier|private
specifier|static
name|HttpServer
name|startHttpServer
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
name|HttpServer
name|server
init|=
operator|new
name|HttpServer
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setBindAddress
argument_list|(
literal|"0.0.0.0"
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|start
argument_list|()
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
name|JobEndServlet
operator|.
name|baseUrl
operator|=
literal|"http://localhost:"
operator|+
name|server
operator|.
name|getPort
argument_list|()
operator|+
literal|"/"
expr_stmt|;
name|JobEndServlet
operator|.
name|foundJobState
operator|=
literal|null
expr_stmt|;
return|return
name|server
return|;
block|}
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
DECL|field|baseUrl
specifier|public
specifier|static
name|String
name|baseUrl
decl_stmt|;
DECL|field|foundJobState
specifier|public
specifier|static
name|String
name|foundJobState
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response)
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
name|foundJobState
operator|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"status"
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
DECL|class|MRAppWithCustomContainerAllocator
specifier|private
class|class
name|MRAppWithCustomContainerAllocator
extends|extends
name|MRApp
block|{
DECL|field|crushUnregistration
specifier|private
name|boolean
name|crushUnregistration
decl_stmt|;
DECL|method|MRAppWithCustomContainerAllocator (int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart, int startCount, boolean crushUnregistration)
specifier|public
name|MRAppWithCustomContainerAllocator
parameter_list|(
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|boolean
name|autoComplete
parameter_list|,
name|String
name|testName
parameter_list|,
name|boolean
name|cleanOnStart
parameter_list|,
name|int
name|startCount
parameter_list|,
name|boolean
name|crushUnregistration
parameter_list|)
block|{
name|super
argument_list|(
name|maps
argument_list|,
name|reduces
argument_list|,
name|autoComplete
argument_list|,
name|testName
argument_list|,
name|cleanOnStart
argument_list|,
name|startCount
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|crushUnregistration
operator|=
name|crushUnregistration
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContainerAllocator ( ClientService clientService, AppContext context)
specifier|protected
name|ContainerAllocator
name|createContainerAllocator
parameter_list|(
name|ClientService
name|clientService
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
name|context
operator|=
name|spy
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
operator|new
name|CustomContainerAllocator
argument_list|(
name|this
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|class|CustomContainerAllocator
specifier|private
class|class
name|CustomContainerAllocator
extends|extends
name|RMCommunicator
implements|implements
name|ContainerAllocator
implements|,
name|RMHeartbeatHandler
block|{
DECL|field|app
specifier|private
name|MRAppWithCustomContainerAllocator
name|app
decl_stmt|;
DECL|field|allocator
specifier|private
name|MRAppContainerAllocator
name|allocator
init|=
operator|new
name|MRAppContainerAllocator
argument_list|()
decl_stmt|;
DECL|method|CustomContainerAllocator ( MRAppWithCustomContainerAllocator app, AppContext context)
specifier|public
name|CustomContainerAllocator
parameter_list|(
name|MRAppWithCustomContainerAllocator
name|app
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|app
operator|=
name|app
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{       }
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
block|{       }
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
block|{
name|unregister
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doUnregistration ()
specifier|protected
name|void
name|doUnregistration
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|crushUnregistration
condition|)
block|{
name|app
operator|.
name|successfullyUnregistered
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"test exception"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|handle (ContainerAllocatorEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerAllocatorEvent
name|event
parameter_list|)
block|{
name|allocator
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLastHeartbeatTime ()
specifier|public
name|long
name|getLastHeartbeatTime
parameter_list|()
block|{
return|return
name|allocator
operator|.
name|getLastHeartbeatTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|runOnNextHeartbeat (Runnable callback)
specifier|public
name|void
name|runOnNextHeartbeat
parameter_list|(
name|Runnable
name|callback
parameter_list|)
block|{
name|allocator
operator|.
name|runOnNextHeartbeat
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|heartbeat ()
specifier|protected
name|void
name|heartbeat
parameter_list|()
throws|throws
name|Exception
block|{       }
block|}
block|}
block|}
end_class

end_unit

