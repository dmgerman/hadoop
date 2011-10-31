begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|JobACL
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
name|TaskAttemptId
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
name|TaskAttemptState
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
name|TaskId
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
name|job
operator|.
name|TaskAttempt
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|BuilderUtils
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
name|Times
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
name|AMWebApp
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * Render a block of HTML for a give job.  */
end_comment

begin_class
DECL|class|HsJobBlock
specifier|public
class|class
name|HsJobBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|appContext
specifier|final
name|AppContext
name|appContext
decl_stmt|;
DECL|field|killedMapAttempts
name|int
name|killedMapAttempts
init|=
literal|0
decl_stmt|;
DECL|field|failedMapAttempts
name|int
name|failedMapAttempts
init|=
literal|0
decl_stmt|;
DECL|field|successfulMapAttempts
name|int
name|successfulMapAttempts
init|=
literal|0
decl_stmt|;
DECL|field|killedReduceAttempts
name|int
name|killedReduceAttempts
init|=
literal|0
decl_stmt|;
DECL|field|failedReduceAttempts
name|int
name|failedReduceAttempts
init|=
literal|0
decl_stmt|;
DECL|field|successfulReduceAttempts
name|int
name|successfulReduceAttempts
init|=
literal|0
decl_stmt|;
DECL|field|avgMapTime
name|long
name|avgMapTime
init|=
literal|0
decl_stmt|;
DECL|field|avgReduceTime
name|long
name|avgReduceTime
init|=
literal|0
decl_stmt|;
DECL|field|avgShuffleTime
name|long
name|avgShuffleTime
init|=
literal|0
decl_stmt|;
DECL|field|avgSortTime
name|long
name|avgSortTime
init|=
literal|0
decl_stmt|;
DECL|field|numMaps
name|int
name|numMaps
decl_stmt|;
DECL|field|numReduces
name|int
name|numReduces
decl_stmt|;
DECL|method|HsJobBlock (AppContext appctx)
annotation|@
name|Inject
name|HsJobBlock
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
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.HtmlBlock#render(org.apache.hadoop.yarn.webapp.view.HtmlBlock.Block)    */
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
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
name|job
operator|.
name|getJobACLs
argument_list|()
decl_stmt|;
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
name|JobReport
name|jobReport
init|=
name|job
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|int
name|mapTasks
init|=
name|job
operator|.
name|getTotalMaps
argument_list|()
decl_stmt|;
name|int
name|mapTasksComplete
init|=
name|job
operator|.
name|getCompletedMaps
argument_list|()
decl_stmt|;
name|int
name|reduceTasks
init|=
name|job
operator|.
name|getTotalReduces
argument_list|()
decl_stmt|;
name|int
name|reducesTasksComplete
init|=
name|job
operator|.
name|getCompletedReduces
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|jobReport
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|finishTime
init|=
name|jobReport
operator|.
name|getFinishTime
argument_list|()
decl_stmt|;
name|countTasksAndAttempts
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|ResponseInfo
name|infoBlock
init|=
name|info
argument_list|(
literal|"Job Overview"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Job Name:"
argument_list|,
name|job
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"User Name:"
argument_list|,
name|job
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"State:"
argument_list|,
name|job
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Uberized:"
argument_list|,
name|job
operator|.
name|isUber
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
name|startTime
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Finished:"
argument_list|,
operator|new
name|Date
argument_list|(
name|finishTime
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
name|Times
operator|.
name|elapsed
argument_list|(
name|startTime
argument_list|,
name|finishTime
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
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
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
init|=
name|job
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
if|if
condition|(
name|diagnostics
operator|!=
literal|null
operator|&&
operator|!
name|diagnostics
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|diag
range|:
name|diagnostics
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|diag
argument_list|)
expr_stmt|;
block|}
name|infoBlock
operator|.
name|_
argument_list|(
literal|"Diagnostics:"
argument_list|,
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numMaps
operator|>
literal|0
condition|)
block|{
name|infoBlock
operator|.
name|_
argument_list|(
literal|"Average Map Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|avgMapTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numReduces
operator|>
literal|0
condition|)
block|{
name|infoBlock
operator|.
name|_
argument_list|(
literal|"Average Reduce Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|avgReduceTime
argument_list|)
argument_list|)
expr_stmt|;
name|infoBlock
operator|.
name|_
argument_list|(
literal|"Average Shuffle Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|avgShuffleTime
argument_list|)
argument_list|)
expr_stmt|;
name|infoBlock
operator|.
name|_
argument_list|(
literal|"Average Merge Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|avgSortTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|entry
range|:
name|acls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|infoBlock
operator|.
name|_
argument_list|(
literal|"ACL "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getAclName
argument_list|()
operator|+
literal|":"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|String
name|nodeHttpAddress
init|=
name|amInfo
operator|.
name|getNodeManagerHost
argument_list|()
operator|+
literal|":"
operator|+
name|amInfo
operator|.
name|getNodeManagerHttpPort
argument_list|()
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
name|amInfo
operator|.
name|getNodeManagerHost
argument_list|()
argument_list|,
name|amInfo
operator|.
name|getNodeManagerPort
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
name|amInfo
operator|.
name|getAppAttemptId
argument_list|()
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
name|amInfo
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
literal|"http://"
argument_list|,
name|nodeHttpAddress
argument_list|)
argument_list|,
name|nodeHttpAddress
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
literal|"logs"
argument_list|,
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|,
name|amInfo
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|jid
argument_list|,
name|job
operator|.
name|getUserName
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
literal|"Total"
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
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|mapTasks
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|mapTasksComplete
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
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|reduceTasks
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|reducesTasksComplete
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
name|failedMapAttempts
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
name|killedMapAttempts
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
name|successfulMapAttempts
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
name|failedReduceAttempts
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
name|killedReduceAttempts
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
name|successfulReduceAttempts
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
comment|/**    * Go through a job and update the member variables with counts for    * information to output in the page.    * @param job the job to get counts for.    */
DECL|method|countTasksAndAttempts (Job job)
specifier|private
name|void
name|countTasksAndAttempts
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|numReduces
operator|=
literal|0
expr_stmt|;
name|numMaps
operator|=
literal|0
expr_stmt|;
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|()
decl_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|tasks
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Attempts counts
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|attempts
init|=
name|task
operator|.
name|getAttempts
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|attempt
range|:
name|attempts
operator|.
name|values
argument_list|()
control|)
block|{
name|int
name|successful
init|=
literal|0
decl_stmt|,
name|failed
init|=
literal|0
decl_stmt|,
name|killed
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|NEW
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
comment|//Do Nothing
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|RUNNING
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
comment|//Do Nothing
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|SUCCESSFUL
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
operator|++
name|successful
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|FAILED
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
operator|++
name|failed
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TaskAttemptStateUI
operator|.
name|KILLED
operator|.
name|correspondsTo
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
operator|++
name|killed
expr_stmt|;
block|}
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|successfulMapAttempts
operator|+=
name|successful
expr_stmt|;
name|failedMapAttempts
operator|+=
name|failed
expr_stmt|;
name|killedMapAttempts
operator|+=
name|killed
expr_stmt|;
if|if
condition|(
name|attempt
operator|.
name|getState
argument_list|()
operator|==
name|TaskAttemptState
operator|.
name|SUCCEEDED
condition|)
block|{
name|numMaps
operator|++
expr_stmt|;
name|avgMapTime
operator|+=
operator|(
name|attempt
operator|.
name|getFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getLaunchTime
argument_list|()
operator|)
expr_stmt|;
block|}
break|break;
case|case
name|REDUCE
case|:
name|successfulReduceAttempts
operator|+=
name|successful
expr_stmt|;
name|failedReduceAttempts
operator|+=
name|failed
expr_stmt|;
name|killedReduceAttempts
operator|+=
name|killed
expr_stmt|;
if|if
condition|(
name|attempt
operator|.
name|getState
argument_list|()
operator|==
name|TaskAttemptState
operator|.
name|SUCCEEDED
condition|)
block|{
name|numReduces
operator|++
expr_stmt|;
name|avgShuffleTime
operator|+=
operator|(
name|attempt
operator|.
name|getShuffleFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getLaunchTime
argument_list|()
operator|)
expr_stmt|;
name|avgSortTime
operator|+=
name|attempt
operator|.
name|getSortFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getLaunchTime
argument_list|()
expr_stmt|;
name|avgReduceTime
operator|+=
operator|(
name|attempt
operator|.
name|getFinishTime
argument_list|()
operator|-
name|attempt
operator|.
name|getShuffleFinishTime
argument_list|()
operator|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
if|if
condition|(
name|numMaps
operator|>
literal|0
condition|)
block|{
name|avgMapTime
operator|=
name|avgMapTime
operator|/
name|numMaps
expr_stmt|;
block|}
if|if
condition|(
name|numReduces
operator|>
literal|0
condition|)
block|{
name|avgReduceTime
operator|=
name|avgReduceTime
operator|/
name|numReduces
expr_stmt|;
name|avgShuffleTime
operator|=
name|avgShuffleTime
operator|/
name|numReduces
expr_stmt|;
name|avgSortTime
operator|=
name|avgSortTime
operator|/
name|numReduces
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

