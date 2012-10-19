begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp
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
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
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
operator|.
name|JOB_ID
import|;
end_import

begin_import
import|import static
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
operator|.
name|join
import|;
end_import

begin_import
import|import static
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
name|JQueryUI
operator|.
name|_EVEN
import|;
end_import

begin_import
import|import static
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
name|JQueryUI
operator|.
name|_INFO_WRAP
import|;
end_import

begin_import
import|import static
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
name|JQueryUI
operator|.
name|_ODD
import|;
end_import

begin_import
import|import static
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
name|JQueryUI
operator|.
name|_PROGRESSBAR
import|;
end_import

begin_import
import|import static
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
name|JQueryUI
operator|.
name|_PROGRESSBAR_VALUE
import|;
end_import

begin_import
import|import static
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
name|JQueryUI
operator|.
name|_TH
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|HttpConfig
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
name|AMInfo
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
name|JobId
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
name|AppContext
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
name|Job
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
name|dao
operator|.
name|AMAttemptInfo
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
name|dao
operator|.
name|JobInfo
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
name|util
operator|.
name|MRApps
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
name|util
operator|.
name|MRApps
operator|.
name|TaskAttemptStateUI
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
name|StringUtils
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
name|hamlet
operator|.
name|Hamlet
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|DIV
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|TABLE
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
name|InfoBlock
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|JobBlock
specifier|public
class|class
name|JobBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|appContext
specifier|final
name|AppContext
name|appContext
decl_stmt|;
DECL|method|JobBlock (AppContext appctx)
annotation|@
name|Inject
name|JobBlock
parameter_list|(
name|AppContext
name|appctx
parameter_list|)
block|{
name|appContext
operator|=
name|appctx
expr_stmt|;
block|}
DECL|method|render (Block html)
annotation|@
name|Override
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|String
name|jid
init|=
name|$
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|jid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|_
argument_list|(
literal|"Sorry, can't do anything without a JobID."
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
name|JobId
name|jobID
init|=
name|MRApps
operator|.
name|toJobID
argument_list|(
name|jid
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|appContext
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|_
argument_list|(
literal|"Sorry, "
argument_list|,
name|jid
argument_list|,
literal|" not found."
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
init|=
name|job
operator|.
name|getAMInfos
argument_list|()
decl_stmt|;
name|String
name|amString
init|=
name|amInfos
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
literal|"ApplicationMaster"
else|:
literal|"ApplicationMasters"
decl_stmt|;
name|JobInfo
name|jinfo
init|=
operator|new
name|JobInfo
argument_list|(
name|job
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|info
argument_list|(
literal|"Job Overview"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Job Name:"
argument_list|,
name|jinfo
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"State:"
argument_list|,
name|jinfo
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Uberized:"
argument_list|,
name|jinfo
operator|.
name|isUberized
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Started:"
argument_list|,
operator|new
name|Date
argument_list|(
name|jinfo
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Elapsed:"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|jinfo
operator|.
name|getElapsedTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|div
init|=
name|html
operator|.
name|_
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
comment|// MRAppMasters Table
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|table
init|=
name|div
operator|.
name|table
argument_list|(
literal|"#job"
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|amString
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Attempt Number"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Start Time"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Node"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Logs"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
for|for
control|(
name|AMInfo
name|amInfo
range|:
name|amInfos
control|)
block|{
name|AMAttemptInfo
name|attempt
init|=
operator|new
name|AMAttemptInfo
argument_list|(
name|amInfo
argument_list|,
name|jinfo
operator|.
name|getId
argument_list|()
argument_list|,
name|jinfo
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|attempt
operator|.
name|getAttemptId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
operator|new
name|Date
argument_list|(
name|attempt
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|".nodelink"
argument_list|,
name|url
argument_list|(
name|HttpConfig
operator|.
name|getSchemePrefix
argument_list|()
argument_list|,
name|attempt
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
argument_list|,
name|attempt
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|".logslink"
argument_list|,
name|url
argument_list|(
name|attempt
operator|.
name|getLogsLink
argument_list|()
argument_list|)
argument_list|,
literal|"logs"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|_
argument_list|()
expr_stmt|;
name|div
operator|.
name|_
argument_list|()
expr_stmt|;
name|html
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
operator|.
comment|// Tasks table
name|table
argument_list|(
literal|"#job"
argument_list|)
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Task Type"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Progress"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Total"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Pending"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Running"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Complete"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|(
name|_ODD
argument_list|)
operator|.
name|th
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"tasks"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|)
argument_list|,
literal|"Map"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|div
argument_list|(
name|_PROGRESSBAR
argument_list|)
operator|.
name|$title
argument_list|(
name|join
argument_list|(
name|jinfo
operator|.
name|getMapProgressPercent
argument_list|()
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
comment|// tooltip
name|div
argument_list|(
name|_PROGRESSBAR_VALUE
argument_list|)
operator|.
name|$style
argument_list|(
name|join
argument_list|(
literal|"width:"
argument_list|,
name|jinfo
operator|.
name|getMapProgressPercent
argument_list|()
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getMapsTotal
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getMapsPending
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getMapsRunning
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getMapsCompleted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|(
name|_EVEN
argument_list|)
operator|.
name|th
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"tasks"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|)
argument_list|,
literal|"Reduce"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|div
argument_list|(
name|_PROGRESSBAR
argument_list|)
operator|.
name|$title
argument_list|(
name|join
argument_list|(
name|jinfo
operator|.
name|getReduceProgressPercent
argument_list|()
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
comment|// tooltip
name|div
argument_list|(
name|_PROGRESSBAR_VALUE
argument_list|)
operator|.
name|$style
argument_list|(
name|join
argument_list|(
literal|"width:"
argument_list|,
name|jinfo
operator|.
name|getReduceProgressPercent
argument_list|()
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getReducesTotal
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getReducesPending
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getReducesRunning
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getReducesCompleted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
comment|// Attempts table
name|table
argument_list|(
literal|"#job"
argument_list|)
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Attempt Type"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"New"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Running"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Failed"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Killed"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Successful"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|(
name|_ODD
argument_list|)
operator|.
name|th
argument_list|(
literal|"Maps"
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|NEW
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getNewMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|RUNNING
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getRunningMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|FAILED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getFailedMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|KILLED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getKilledMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|SUCCESSFUL
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getSuccessfulMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|(
name|_EVEN
argument_list|)
operator|.
name|th
argument_list|(
literal|"Reduces"
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|NEW
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getNewReduceAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|RUNNING
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getRunningReduceAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|FAILED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getFailedReduceAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|KILLED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getKilledReduceAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|SUCCESSFUL
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|jinfo
operator|.
name|getSuccessfulReduceAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

