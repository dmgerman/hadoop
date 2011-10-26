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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|java
operator|.
name|net
operator|.
name|URLConnection
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
name|Configurable
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
name|mortbay
operator|.
name|log
operator|.
name|Log
import|;
end_import

begin_comment
comment|/**  *<p>This class handles job end notification. Submitters of jobs can choose to  * be notified of the end of a job by supplying a URL to which a connection  * will be established.  *<ul><li> The URL connection is fire and forget by default.</li><li>  * User can specify number of retry attempts and a time interval at which to  * attempt retries</li><li>  * Cluster administrators can set final parameters to set maximum number of  * tries (0 would disable job end notification) and max time interval</li><li>  * The URL may contain sentinels which will be replaced by jobId and jobStatus   * (eg. SUCCEEDED/KILLED/FAILED)</li></ul>  *</p>  */
end_comment

begin_class
DECL|class|JobEndNotifier
specifier|public
class|class
name|JobEndNotifier
implements|implements
name|Configurable
block|{
DECL|field|JOB_ID
specifier|final
name|String
name|JOB_ID
init|=
literal|"$jobId"
decl_stmt|;
DECL|field|JOB_STATUS
specifier|final
name|String
name|JOB_STATUS
init|=
literal|"$jobStatus"
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|userUrl
specifier|protected
name|String
name|userUrl
decl_stmt|;
DECL|field|numTries
specifier|protected
name|int
name|numTries
decl_stmt|;
comment|//Number of tries to attempt notification
DECL|field|waitInterval
specifier|protected
name|int
name|waitInterval
decl_stmt|;
comment|//Time to wait between retrying notification
DECL|field|urlToNotify
specifier|protected
name|URL
name|urlToNotify
decl_stmt|;
comment|//URL to notify read from the config
comment|/**    * Parse the URL that needs to be notified of the end of the job, along    * with the number of retries in case of failure and the amount of time to    * wait between retries    * @param conf the configuration     */
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|numTries
operator|=
name|Math
operator|.
name|min
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_ATTEMPTS
argument_list|,
literal|0
argument_list|)
operator|+
literal|1
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|waitInterval
operator|=
name|Math
operator|.
name|min
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_RETRY_INTERVAL
argument_list|,
literal|5
argument_list|)
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_MAX_RETRY_INTERVAL
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|waitInterval
operator|=
operator|(
name|waitInterval
operator|<
literal|0
operator|)
condition|?
literal|5
else|:
name|waitInterval
expr_stmt|;
name|userUrl
operator|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MR_JOB_END_NOTIFICATION_URL
argument_list|)
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Notify the URL just once. Use best effort. Timeout hard coded to 5    * seconds.    */
DECL|method|notifyURLOnce ()
specifier|protected
name|boolean
name|notifyURLOnce
parameter_list|()
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Log
operator|.
name|info
argument_list|(
literal|"Job end notification trying "
operator|+
name|urlToNotify
argument_list|)
expr_stmt|;
name|URLConnection
name|conn
init|=
name|urlToNotify
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setConnectTimeout
argument_list|(
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setReadTimeout
argument_list|(
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setAllowUserInteraction
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|conn
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
name|Log
operator|.
name|info
argument_list|(
literal|"Job end notification to "
operator|+
name|urlToNotify
operator|+
literal|" succeeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Log
operator|.
name|warn
argument_list|(
literal|"Job end notification to "
operator|+
name|urlToNotify
operator|+
literal|" failed"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
comment|/**    * Notify a server of the completion of a submitted job. The server must have    * configured MRConfig.JOB_END_NOTIFICATION_URLS    * @param jobReport JobReport used to read JobId and JobStatus    * @throws InterruptedException    */
DECL|method|notify (JobReport jobReport)
specifier|public
name|void
name|notify
parameter_list|(
name|JobReport
name|jobReport
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// Do we need job-end notification?
if|if
condition|(
name|userUrl
operator|==
literal|null
condition|)
block|{
name|Log
operator|.
name|info
argument_list|(
literal|"Job end notification URL not set, skipping."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//Do string replacements for jobId and jobStatus
if|if
condition|(
name|userUrl
operator|.
name|contains
argument_list|(
name|JOB_ID
argument_list|)
condition|)
block|{
name|userUrl
operator|=
name|userUrl
operator|.
name|replace
argument_list|(
name|JOB_ID
argument_list|,
name|jobReport
operator|.
name|getJobId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|userUrl
operator|.
name|contains
argument_list|(
name|JOB_STATUS
argument_list|)
condition|)
block|{
name|userUrl
operator|=
name|userUrl
operator|.
name|replace
argument_list|(
name|JOB_STATUS
argument_list|,
name|jobReport
operator|.
name|getJobState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Create the URL, ensure sanity
try|try
block|{
name|urlToNotify
operator|=
operator|new
name|URL
argument_list|(
name|userUrl
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|mue
parameter_list|)
block|{
name|Log
operator|.
name|warn
argument_list|(
literal|"Job end notification couldn't parse "
operator|+
name|userUrl
argument_list|,
name|mue
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Send notification
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|numTries
operator|--
operator|>
literal|0
operator|&&
operator|!
name|success
condition|)
block|{
name|Log
operator|.
name|info
argument_list|(
literal|"Job end notification attempts left "
operator|+
name|numTries
argument_list|)
expr_stmt|;
name|success
operator|=
name|notifyURLOnce
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitInterval
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|Log
operator|.
name|warn
argument_list|(
literal|"Job end notification failed to notify : "
operator|+
name|urlToNotify
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Log
operator|.
name|info
argument_list|(
literal|"Job end notification succeeded for "
operator|+
name|jobReport
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

