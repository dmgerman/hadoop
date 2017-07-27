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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|C_TABLE
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
name|Counter
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
name|CounterGroup
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
name|TD
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

begin_class
DECL|class|CountersBlock
specifier|public
class|class
name|CountersBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|job
name|Job
name|job
decl_stmt|;
DECL|field|task
name|Task
name|task
decl_stmt|;
DECL|field|total
name|Counters
name|total
decl_stmt|;
DECL|field|map
name|Counters
name|map
decl_stmt|;
DECL|field|reduce
name|Counters
name|reduce
decl_stmt|;
DECL|method|CountersBlock (AppContext appCtx, ViewContext ctx)
annotation|@
name|Inject
name|CountersBlock
parameter_list|(
name|AppContext
name|appCtx
parameter_list|,
name|ViewContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|getCounters
argument_list|(
name|appCtx
argument_list|)
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
name|__
argument_list|(
literal|"Sorry, no counters for nonexistent"
argument_list|,
name|$
argument_list|(
name|JOB_ID
argument_list|,
literal|"job"
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|$
argument_list|(
name|TASK_ID
argument_list|)
operator|.
name|isEmpty
argument_list|()
operator|&&
name|task
operator|==
literal|null
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"Sorry, no counters for nonexistent"
argument_list|,
name|$
argument_list|(
name|TASK_ID
argument_list|,
literal|"task"
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|total
operator|==
literal|null
operator|||
name|total
operator|.
name|getGroupNames
argument_list|()
operator|==
literal|null
operator|||
name|total
operator|.
name|countCounters
argument_list|()
operator|==
literal|0
condition|)
block|{
name|String
name|type
init|=
name|$
argument_list|(
name|TASK_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|type
operator|=
name|$
argument_list|(
name|JOB_ID
argument_list|,
literal|"the job"
argument_list|)
expr_stmt|;
block|}
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"Sorry it looks like "
argument_list|,
name|type
argument_list|,
literal|" has no counters."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
name|String
name|urlBase
decl_stmt|;
name|String
name|urlId
decl_stmt|;
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
name|urlBase
operator|=
literal|"singletaskcounter"
expr_stmt|;
name|urlId
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|urlBase
operator|=
literal|"singlejobcounter"
expr_stmt|;
name|urlId
operator|=
name|MRApps
operator|.
name|toString
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|numGroups
init|=
literal|0
decl_stmt|;
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
name|tbody
init|=
name|html
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
operator|.
name|table
argument_list|(
literal|"#counters"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|".group.ui-state-default"
argument_list|,
literal|"Counter Group"
argument_list|)
operator|.
name|th
argument_list|(
literal|".ui-state-default"
argument_list|,
literal|"Counters"
argument_list|)
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
for|for
control|(
name|CounterGroup
name|g
range|:
name|total
control|)
block|{
name|CounterGroup
name|mg
init|=
name|map
operator|==
literal|null
condition|?
literal|null
else|:
name|map
operator|.
name|getGroup
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|CounterGroup
name|rg
init|=
name|reduce
operator|==
literal|null
condition|?
literal|null
else|:
name|reduce
operator|.
name|getGroup
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
operator|++
name|numGroups
expr_stmt|;
comment|// This is mostly for demonstration :) Typically we'd introduced
comment|// a CounterGroup block to reduce the verbosity. OTOH, this
comment|// serves as an indicator of where we're in the tag hierarchy.
name|TR
argument_list|<
name|THEAD
argument_list|<
name|TABLE
argument_list|<
name|TD
argument_list|<
name|TR
argument_list|<
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|groupHeadRow
init|=
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|$title
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|$class
argument_list|(
literal|"ui-state-default"
argument_list|)
operator|.
name|__
argument_list|(
name|fixGroupDisplayName
argument_list|(
name|g
operator|.
name|getDisplayName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|$class
argument_list|(
name|C_TABLE
argument_list|)
operator|.
name|table
argument_list|(
literal|".dt-counters"
argument_list|)
operator|.
name|$id
argument_list|(
name|job
operator|.
name|getID
argument_list|()
operator|+
literal|"."
operator|+
name|g
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|".name"
argument_list|,
literal|"Name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|groupHeadRow
operator|.
name|th
argument_list|(
literal|"Map"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Reduce"
argument_list|)
expr_stmt|;
block|}
comment|// Ditto
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|TD
argument_list|<
name|TR
argument_list|<
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|group
init|=
name|groupHeadRow
operator|.
name|th
argument_list|(
name|map
operator|==
literal|null
condition|?
literal|"Value"
else|:
literal|"Total"
argument_list|)
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
for|for
control|(
name|Counter
name|counter
range|:
name|g
control|)
block|{
comment|// Ditto
name|TR
argument_list|<
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|TD
argument_list|<
name|TR
argument_list|<
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|groupRow
init|=
name|group
operator|.
name|tr
argument_list|()
decl_stmt|;
if|if
condition|(
name|task
operator|==
literal|null
operator|&&
name|mg
operator|==
literal|null
operator|&&
name|rg
operator|==
literal|null
condition|)
block|{
name|groupRow
operator|.
name|td
argument_list|()
operator|.
name|$title
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
name|counter
operator|.
name|getDisplayName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|groupRow
operator|.
name|td
argument_list|()
operator|.
name|$title
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|a
argument_list|(
name|url
argument_list|(
name|urlBase
argument_list|,
name|urlId
argument_list|,
name|g
operator|.
name|getName
argument_list|()
argument_list|,
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|counter
operator|.
name|getDisplayName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|Counter
name|mc
init|=
name|mg
operator|==
literal|null
condition|?
literal|null
else|:
name|mg
operator|.
name|findCounter
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Counter
name|rc
init|=
name|rg
operator|==
literal|null
condition|?
literal|null
else|:
name|rg
operator|.
name|findCounter
argument_list|(
name|counter
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|groupRow
operator|.
name|td
argument_list|(
name|mc
operator|==
literal|null
condition|?
literal|"0"
else|:
name|String
operator|.
name|format
argument_list|(
literal|"%,d"
argument_list|,
name|mc
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|rc
operator|==
literal|null
condition|?
literal|"0"
else|:
name|String
operator|.
name|format
argument_list|(
literal|"%,d"
argument_list|,
name|rc
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|groupRow
operator|.
name|td
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%,d"
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|group
operator|.
name|__
argument_list|()
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
name|tbody
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
DECL|method|getCounters (AppContext ctx)
specifier|private
name|void
name|getCounters
parameter_list|(
name|AppContext
name|ctx
parameter_list|)
block|{
name|JobId
name|jobID
init|=
literal|null
decl_stmt|;
name|TaskId
name|taskID
init|=
literal|null
decl_stmt|;
name|String
name|tid
init|=
name|$
argument_list|(
name|TASK_ID
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|taskID
operator|=
name|MRApps
operator|.
name|toTaskID
argument_list|(
name|tid
argument_list|)
expr_stmt|;
name|jobID
operator|=
name|taskID
operator|.
name|getJobId
argument_list|()
expr_stmt|;
block|}
else|else
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
operator|!=
literal|null
operator|&&
operator|!
name|jid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|jobID
operator|=
name|MRApps
operator|.
name|toJobID
argument_list|(
name|jid
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|jobID
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|job
operator|=
name|ctx
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|taskID
operator|!=
literal|null
condition|)
block|{
name|task
operator|=
name|job
operator|.
name|getTask
argument_list|(
name|taskID
argument_list|)
expr_stmt|;
if|if
condition|(
name|task
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|total
operator|=
name|task
operator|.
name|getCounters
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// Get all types of counters
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
name|total
operator|=
name|job
operator|.
name|getAllCounters
argument_list|()
expr_stmt|;
name|boolean
name|needTotalCounters
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|total
operator|==
literal|null
condition|)
block|{
name|total
operator|=
operator|new
name|Counters
argument_list|()
expr_stmt|;
name|needTotalCounters
operator|=
literal|true
expr_stmt|;
block|}
name|map
operator|=
operator|new
name|Counters
argument_list|()
expr_stmt|;
name|reduce
operator|=
operator|new
name|Counters
argument_list|()
expr_stmt|;
for|for
control|(
name|Task
name|t
range|:
name|tasks
operator|.
name|values
argument_list|()
control|)
block|{
name|Counters
name|counters
init|=
name|t
operator|.
name|getCounters
argument_list|()
decl_stmt|;
if|if
condition|(
name|counters
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
switch|switch
condition|(
name|t
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|map
operator|.
name|incrAllCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reduce
operator|.
name|incrAllCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|needTotalCounters
condition|)
block|{
name|total
operator|.
name|incrAllCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|fixGroupDisplayName (CharSequence name)
specifier|private
name|String
name|fixGroupDisplayName
parameter_list|(
name|CharSequence
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|".\u200B"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$"
argument_list|,
literal|"\u200B$"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

