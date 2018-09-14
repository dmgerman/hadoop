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
name|TASK_ID
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
name|AMParams
operator|.
name|TASK_TYPE
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
name|ACCORDION
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
name|DATATABLES
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
name|DATATABLES_ID
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
name|initID
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
name|postInitID
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
name|tableInit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|text
operator|.
name|StringEscapeUtils
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
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|app
operator|.
name|webapp
operator|.
name|App
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
name|MapTaskAttemptInfo
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
name|TaskAttemptInfo
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
name|MRWebAppUtil
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
name|hamlet2
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
name|hamlet2
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TBODY
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TFOOT
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|THEAD
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TR
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
name|hamlet2
operator|.
name|HamletSpec
operator|.
name|InputType
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/**  * A Page the shows the status of a given task  */
end_comment

begin_class
DECL|class|HsTaskPage
specifier|public
class|class
name|HsTaskPage
extends|extends
name|HsView
block|{
comment|/**    * A Block of HTML that will render a given task attempt.     */
DECL|class|AttemptsBlock
specifier|static
class|class
name|AttemptsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|app
specifier|final
name|App
name|app
decl_stmt|;
annotation|@
name|Inject
DECL|method|AttemptsBlock (App ctx)
name|AttemptsBlock
parameter_list|(
name|App
name|ctx
parameter_list|)
block|{
name|app
operator|=
name|ctx
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isValidRequest
argument_list|()
condition|)
block|{
name|html
operator|.
name|h2
argument_list|(
name|$
argument_list|(
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|TaskType
name|type
init|=
literal|null
decl_stmt|;
name|String
name|symbol
init|=
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|symbol
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|type
operator|=
name|MRApps
operator|.
name|taskType
argument_list|(
name|symbol
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|app
operator|.
name|getTask
argument_list|()
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|TR
argument_list|<
name|THEAD
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
name|headRow
init|=
name|html
operator|.
name|table
argument_list|(
literal|"#attempts"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
decl_stmt|;
name|headRow
operator|.
name|th
argument_list|(
literal|".id"
argument_list|,
literal|"Attempt"
argument_list|)
operator|.
name|th
argument_list|(
literal|".state"
argument_list|,
literal|"State"
argument_list|)
operator|.
name|th
argument_list|(
literal|".status"
argument_list|,
literal|"Status"
argument_list|)
operator|.
name|th
argument_list|(
literal|".node"
argument_list|,
literal|"Node"
argument_list|)
operator|.
name|th
argument_list|(
literal|".logs"
argument_list|,
literal|"Logs"
argument_list|)
operator|.
name|th
argument_list|(
literal|".tsh"
argument_list|,
literal|"Start Time"
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|headRow
operator|.
name|th
argument_list|(
literal|"Shuffle Finish Time"
argument_list|)
expr_stmt|;
name|headRow
operator|.
name|th
argument_list|(
literal|"Merge Finish Time"
argument_list|)
expr_stmt|;
block|}
name|headRow
operator|.
name|th
argument_list|(
literal|"Finish Time"
argument_list|)
expr_stmt|;
comment|//Attempt
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|headRow
operator|.
name|th
argument_list|(
literal|"Elapsed Time Shuffle"
argument_list|)
expr_stmt|;
comment|//Attempt
name|headRow
operator|.
name|th
argument_list|(
literal|"Elapsed Time Merge"
argument_list|)
expr_stmt|;
comment|//Attempt
name|headRow
operator|.
name|th
argument_list|(
literal|"Elapsed Time Reduce"
argument_list|)
expr_stmt|;
comment|//Attempt
block|}
name|headRow
operator|.
name|th
argument_list|(
literal|"Elapsed Time"
argument_list|)
operator|.
name|th
argument_list|(
literal|".note"
argument_list|,
literal|"Note"
argument_list|)
expr_stmt|;
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|headRow
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
comment|// Write all the data into a JavaScript array of arrays for JQuery
comment|// DataTables to display
name|StringBuilder
name|attemptsTableData
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"[\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|attempt
range|:
name|getTaskAttempts
argument_list|()
control|)
block|{
specifier|final
name|TaskAttemptInfo
name|ta
init|=
operator|new
name|MapTaskAttemptInfo
argument_list|(
name|attempt
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|taid
init|=
name|ta
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|nodeHttpAddr
init|=
name|ta
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|String
name|containerIdString
init|=
name|ta
operator|.
name|getAssignedContainerIdStr
argument_list|()
decl_stmt|;
name|String
name|nodeIdString
init|=
name|attempt
operator|.
name|getAssignedContainerMgrAddress
argument_list|()
decl_stmt|;
name|String
name|nodeRackName
init|=
name|ta
operator|.
name|getRack
argument_list|()
decl_stmt|;
name|long
name|attemptStartTime
init|=
name|ta
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|shuffleFinishTime
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|sortFinishTime
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|attemptFinishTime
init|=
name|ta
operator|.
name|getFinishTime
argument_list|()
decl_stmt|;
name|long
name|elapsedShuffleTime
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|elapsedSortTime
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|elapsedReduceTime
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|shuffleFinishTime
operator|=
name|attempt
operator|.
name|getShuffleFinishTime
argument_list|()
expr_stmt|;
name|sortFinishTime
operator|=
name|attempt
operator|.
name|getSortFinishTime
argument_list|()
expr_stmt|;
name|elapsedShuffleTime
operator|=
name|Times
operator|.
name|elapsed
argument_list|(
name|attemptStartTime
argument_list|,
name|shuffleFinishTime
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|elapsedSortTime
operator|=
name|Times
operator|.
name|elapsed
argument_list|(
name|shuffleFinishTime
argument_list|,
name|sortFinishTime
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|elapsedReduceTime
operator|=
name|Times
operator|.
name|elapsed
argument_list|(
name|sortFinishTime
argument_list|,
name|attemptFinishTime
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|long
name|attemptElapsed
init|=
name|Times
operator|.
name|elapsed
argument_list|(
name|attemptStartTime
argument_list|,
name|attemptFinishTime
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TaskId
name|taskId
init|=
name|attempt
operator|.
name|getID
argument_list|()
operator|.
name|getTaskId
argument_list|()
decl_stmt|;
name|attemptsTableData
operator|.
name|append
argument_list|(
literal|"[\""
argument_list|)
operator|.
name|append
argument_list|(
name|getAttemptId
argument_list|(
name|taskId
argument_list|,
name|ta
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|ta
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|ta
operator|.
name|getStatus
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
literal|"<a class='nodelink' href='"
operator|+
name|MRWebAppUtil
operator|.
name|getYARNWebappScheme
argument_list|()
operator|+
name|nodeHttpAddr
operator|+
literal|"'>"
argument_list|)
operator|.
name|append
argument_list|(
name|nodeRackName
operator|+
literal|"/"
operator|+
name|nodeHttpAddr
operator|+
literal|"</a>\",\""
argument_list|)
operator|.
name|append
argument_list|(
literal|"<a class='logslink' href='"
argument_list|)
operator|.
name|append
argument_list|(
name|url
argument_list|(
literal|"logs"
argument_list|,
name|nodeIdString
argument_list|,
name|containerIdString
argument_list|,
name|taid
argument_list|,
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'>logs</a>\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|attemptStartTime
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|attemptsTableData
operator|.
name|append
argument_list|(
name|shuffleFinishTime
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|sortFinishTime
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
expr_stmt|;
block|}
name|attemptsTableData
operator|.
name|append
argument_list|(
name|attemptFinishTime
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|attemptsTableData
operator|.
name|append
argument_list|(
name|elapsedShuffleTime
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|elapsedSortTime
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|elapsedReduceTime
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
expr_stmt|;
block|}
name|attemptsTableData
operator|.
name|append
argument_list|(
name|attemptElapsed
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|ta
operator|.
name|getNote
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"],\n"
argument_list|)
expr_stmt|;
block|}
comment|//Remove the last comma and close off the array of arrays
if|if
condition|(
name|attemptsTableData
operator|.
name|charAt
argument_list|(
name|attemptsTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
operator|==
literal|','
condition|)
block|{
name|attemptsTableData
operator|.
name|delete
argument_list|(
name|attemptsTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|attemptsTableData
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|attemptsTableData
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|html
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"var attemptsTableData="
operator|+
name|attemptsTableData
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|TR
argument_list|<
name|TFOOT
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
name|footRow
init|=
name|tbody
operator|.
name|__
argument_list|()
operator|.
name|tfoot
argument_list|()
operator|.
name|tr
argument_list|()
decl_stmt|;
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_name"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Attempt"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_state"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"State"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_status"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Status"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_node"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Node"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_node"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Logs"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_start_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Start Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"shuffle_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Shuffle Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"merge_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Merge Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_finish"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Finish Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"elapsed_shuffle_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Elapsed Shuffle Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"elapsed_merge_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Elapsed Merge Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"elapsed_reduce_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Elapsed Reduce Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|footRow
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"attempt_elapsed"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Elapsed Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"note"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Note"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
name|footRow
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
DECL|method|getAttemptId (TaskId taskId, TaskAttemptInfo ta)
specifier|protected
name|String
name|getAttemptId
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|TaskAttemptInfo
name|ta
parameter_list|)
block|{
return|return
name|ta
operator|.
name|getId
argument_list|()
return|;
block|}
comment|/**      * @return true if this is a valid request else false.      */
DECL|method|isValidRequest ()
specifier|protected
name|boolean
name|isValidRequest
parameter_list|()
block|{
return|return
name|app
operator|.
name|getTask
argument_list|()
operator|!=
literal|null
return|;
block|}
comment|/**      * @return all of the attempts to render.      */
DECL|method|getTaskAttempts ()
specifier|protected
name|Collection
argument_list|<
name|TaskAttempt
argument_list|>
name|getTaskAttempts
parameter_list|()
block|{
return|return
name|app
operator|.
name|getTask
argument_list|()
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
return|;
block|}
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.hs.webapp.HsView#preHead(org.apache.hadoop.yarn.webapp.hamlet.Hamlet.HTML)    */
DECL|method|preHead (Page.HTML<__> html)
annotation|@
name|Override
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|__
argument_list|>
name|html
parameter_list|)
block|{
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
comment|//override the nav config from commonPReHead
name|set
argument_list|(
name|initID
argument_list|(
name|ACCORDION
argument_list|,
literal|"nav"
argument_list|)
argument_list|,
literal|"{autoHeight:false, active:2}"
argument_list|)
expr_stmt|;
comment|//Set up the java script and CSS for the attempts table
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"attempts"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"attempts"
argument_list|)
argument_list|,
name|attemptsTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|postInitID
argument_list|(
name|DATATABLES
argument_list|,
literal|"attempts"
argument_list|)
argument_list|,
name|attemptsPostTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"attempts"
argument_list|)
expr_stmt|;
block|}
comment|/**    * The content of this page is the attempts block    * @return AttemptsBlock.class    */
DECL|method|content ()
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|content
parameter_list|()
block|{
return|return
name|AttemptsBlock
operator|.
name|class
return|;
block|}
comment|/**    * @return The end of the JS map that is the jquery datatable config for the    * attempts table.     */
DECL|method|attemptsTableInit ()
specifier|private
name|String
name|attemptsTableInit
parameter_list|()
block|{
name|TaskType
name|type
init|=
literal|null
decl_stmt|;
name|String
name|symbol
init|=
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|symbol
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|type
operator|=
name|MRApps
operator|.
name|taskType
argument_list|(
name|symbol
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TaskId
name|taskID
init|=
name|MRApps
operator|.
name|toTaskID
argument_list|(
name|$
argument_list|(
name|TASK_ID
argument_list|)
argument_list|)
decl_stmt|;
name|type
operator|=
name|taskID
operator|.
name|getTaskType
argument_list|()
expr_stmt|;
block|}
name|StringBuilder
name|b
init|=
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': attemptsTableData"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bDeferRender: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bProcessing: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n,aoColumnDefs:[\n"
argument_list|)
comment|//logs column should not filterable (it includes container ID which may pollute searches)
operator|.
name|append
argument_list|(
literal|"\n{'aTargets': [ 4 ]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'bSearchable': false }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'natural', 'aTargets': [ 0 ]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [ 5, 6"
argument_list|)
comment|//Column numbers are different for maps and reduces
operator|.
name|append
argument_list|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|?
literal|", 7, 8"
else|:
literal|""
argument_list|)
operator|.
name|append
argument_list|(
literal|" ], 'mRender': renderHadoopDate }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': ["
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|==
name|TaskType
operator|.
name|REDUCE
condition|?
literal|"9, 10, 11, 12"
else|:
literal|"7"
argument_list|)
operator|.
name|append
argument_list|(
literal|" ], 'mRender': renderHadoopElapsedTime }]"
argument_list|)
comment|// Sort by id upon page load
operator|.
name|append
argument_list|(
literal|"\n, aaSorting: [[0, 'asc']]"
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
decl_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|attemptsPostTableInit ()
specifier|private
name|String
name|attemptsPostTableInit
parameter_list|()
block|{
return|return
literal|"var asInitVals = new Array();\n"
operator|+
literal|"$('tfoot input').keyup( function () \n{"
operator|+
literal|"  attemptsDataTable.fnFilter( this.value, $('tfoot input').index(this) );\n"
operator|+
literal|"} );\n"
operator|+
literal|"$('tfoot input').each( function (i) {\n"
operator|+
literal|"  asInitVals[i] = this.value;\n"
operator|+
literal|"} );\n"
operator|+
literal|"$('tfoot input').focus( function () {\n"
operator|+
literal|"  if ( this.className == 'search_init' )\n"
operator|+
literal|"  {\n"
operator|+
literal|"    this.className = '';\n"
operator|+
literal|"    this.value = '';\n"
operator|+
literal|"  }\n"
operator|+
literal|"} );\n"
operator|+
literal|"$('tfoot input').blur( function (i) {\n"
operator|+
literal|"  if ( this.value == '' )\n"
operator|+
literal|"  {\n"
operator|+
literal|"    this.className = 'search_init';\n"
operator|+
literal|"    this.value = asInitVals[$('tfoot input').index(this)];\n"
operator|+
literal|"  }\n"
operator|+
literal|"} );\n"
return|;
block|}
block|}
end_class

end_unit

