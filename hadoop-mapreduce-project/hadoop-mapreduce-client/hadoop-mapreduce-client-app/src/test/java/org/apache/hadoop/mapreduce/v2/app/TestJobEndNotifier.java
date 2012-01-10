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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Tests job end notification  *  */
end_comment

begin_class
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
literal|"5"
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
literal|"Expected waitInterval to be 1, but was "
operator|+
name|waitInterval
argument_list|,
name|waitInterval
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
name|MR_JOB_END_RETRY_INTERVAL
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
literal|"Expected waitInterval to be 5, but was "
operator|+
name|waitInterval
argument_list|,
name|waitInterval
operator|==
literal|5
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
literal|"Expected waitInterval to be 5, but was "
operator|+
name|waitInterval
argument_list|,
name|waitInterval
operator|==
literal|5
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
name|JobReport
name|jobReport
init|=
name|Mockito
operator|.
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
block|}
end_class

end_unit

