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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Delayed
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|methods
operator|.
name|GetMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|JobEndNotifier
specifier|public
class|class
name|JobEndNotifier
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JobEndNotifier
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|createNotification (JobConf conf, JobStatus status)
specifier|private
specifier|static
name|JobEndStatusInfo
name|createNotification
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobStatus
name|status
parameter_list|)
block|{
name|JobEndStatusInfo
name|notification
init|=
literal|null
decl_stmt|;
name|String
name|uri
init|=
name|conf
operator|.
name|getJobEndNotificationURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
comment|// +1 to make logic for first notification identical to a retry
name|int
name|retryAttempts
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|JobContext
operator|.
name|MR_JOB_END_RETRY_ATTEMPTS
argument_list|,
literal|0
argument_list|)
operator|+
literal|1
decl_stmt|;
name|long
name|retryInterval
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|JobContext
operator|.
name|MR_JOB_END_RETRY_INTERVAL
argument_list|,
literal|30000
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|contains
argument_list|(
literal|"$jobId"
argument_list|)
condition|)
block|{
name|uri
operator|=
name|uri
operator|.
name|replace
argument_list|(
literal|"$jobId"
argument_list|,
name|status
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uri
operator|.
name|contains
argument_list|(
literal|"$jobStatus"
argument_list|)
condition|)
block|{
name|String
name|statusStr
init|=
operator|(
name|status
operator|.
name|getRunState
argument_list|()
operator|==
name|JobStatus
operator|.
name|SUCCEEDED
operator|)
condition|?
literal|"SUCCEEDED"
else|:
operator|(
name|status
operator|.
name|getRunState
argument_list|()
operator|==
name|JobStatus
operator|.
name|FAILED
operator|)
condition|?
literal|"FAILED"
else|:
literal|"KILLED"
decl_stmt|;
name|uri
operator|=
name|uri
operator|.
name|replace
argument_list|(
literal|"$jobStatus"
argument_list|,
name|statusStr
argument_list|)
expr_stmt|;
block|}
name|notification
operator|=
operator|new
name|JobEndStatusInfo
argument_list|(
name|uri
argument_list|,
name|retryAttempts
argument_list|,
name|retryInterval
argument_list|)
expr_stmt|;
block|}
return|return
name|notification
return|;
block|}
DECL|method|httpNotification (String uri)
specifier|private
specifier|static
name|int
name|httpNotification
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
name|url
init|=
operator|new
name|URI
argument_list|(
name|uri
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|HttpClient
name|m_client
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|HttpMethod
name|method
init|=
operator|new
name|GetMethod
argument_list|(
name|url
operator|.
name|getEscapedURI
argument_list|()
argument_list|)
decl_stmt|;
name|method
operator|.
name|setRequestHeader
argument_list|(
literal|"Accept"
argument_list|,
literal|"*/*"
argument_list|)
expr_stmt|;
return|return
name|m_client
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
return|;
block|}
comment|// for use by the LocalJobRunner, without using a thread&queue,
comment|// simple synchronous way
DECL|method|localRunnerNotification (JobConf conf, JobStatus status)
specifier|public
specifier|static
name|void
name|localRunnerNotification
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|JobStatus
name|status
parameter_list|)
block|{
name|JobEndStatusInfo
name|notification
init|=
name|createNotification
argument_list|(
name|conf
argument_list|,
name|status
argument_list|)
decl_stmt|;
if|if
condition|(
name|notification
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|notification
operator|.
name|configureForRetry
argument_list|()
condition|)
block|{
try|try
block|{
name|int
name|code
init|=
name|httpNotification
argument_list|(
name|notification
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|!=
literal|200
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid response status code: "
operator|+
name|code
argument_list|)
throw|;
block|}
else|else
block|{
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Notification error ["
operator|+
name|notification
operator|.
name|getUri
argument_list|()
operator|+
literal|"]"
argument_list|,
name|ioex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Notification error ["
operator|+
name|notification
operator|.
name|getUri
argument_list|()
operator|+
literal|"]"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|notification
operator|.
name|getRetryInterval
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|iex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Notification retry error ["
operator|+
name|notification
operator|+
literal|"]"
argument_list|,
name|iex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|JobEndStatusInfo
specifier|private
specifier|static
class|class
name|JobEndStatusInfo
implements|implements
name|Delayed
block|{
DECL|field|uri
specifier|private
name|String
name|uri
decl_stmt|;
DECL|field|retryAttempts
specifier|private
name|int
name|retryAttempts
decl_stmt|;
DECL|field|retryInterval
specifier|private
name|long
name|retryInterval
decl_stmt|;
DECL|field|delayTime
specifier|private
name|long
name|delayTime
decl_stmt|;
DECL|method|JobEndStatusInfo (String uri, int retryAttempts, long retryInterval)
name|JobEndStatusInfo
parameter_list|(
name|String
name|uri
parameter_list|,
name|int
name|retryAttempts
parameter_list|,
name|long
name|retryInterval
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|retryAttempts
operator|=
name|retryAttempts
expr_stmt|;
name|this
operator|.
name|retryInterval
operator|=
name|retryInterval
expr_stmt|;
name|this
operator|.
name|delayTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
DECL|method|getUri ()
specifier|public
name|String
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
DECL|method|getRetryAttempts ()
specifier|public
name|int
name|getRetryAttempts
parameter_list|()
block|{
return|return
name|retryAttempts
return|;
block|}
DECL|method|getRetryInterval ()
specifier|public
name|long
name|getRetryInterval
parameter_list|()
block|{
return|return
name|retryInterval
return|;
block|}
DECL|method|configureForRetry ()
specifier|public
name|boolean
name|configureForRetry
parameter_list|()
block|{
name|boolean
name|retry
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|getRetryAttempts
argument_list|()
operator|>
literal|0
condition|)
block|{
name|retry
operator|=
literal|true
expr_stmt|;
name|delayTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|retryInterval
expr_stmt|;
block|}
name|retryAttempts
operator|--
expr_stmt|;
return|return
name|retry
return|;
block|}
DECL|method|getDelay (TimeUnit unit)
specifier|public
name|long
name|getDelay
parameter_list|(
name|TimeUnit
name|unit
parameter_list|)
block|{
name|long
name|n
init|=
name|this
operator|.
name|delayTime
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
return|return
name|unit
operator|.
name|convert
argument_list|(
name|n
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
DECL|method|compareTo (Delayed d)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Delayed
name|d
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|delayTime
operator|-
operator|(
operator|(
name|JobEndStatusInfo
operator|)
name|d
operator|)
operator|.
name|delayTime
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|JobEndStatusInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|delayTime
operator|==
operator|(
operator|(
name|JobEndStatusInfo
operator|)
name|o
operator|)
operator|.
name|delayTime
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|37
operator|*
literal|17
operator|+
call|(
name|int
call|)
argument_list|(
name|delayTime
operator|^
operator|(
name|delayTime
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"URL: "
operator|+
name|uri
operator|+
literal|" remaining retries: "
operator|+
name|retryAttempts
operator|+
literal|" interval: "
operator|+
name|retryInterval
return|;
block|}
block|}
block|}
end_class

end_unit

