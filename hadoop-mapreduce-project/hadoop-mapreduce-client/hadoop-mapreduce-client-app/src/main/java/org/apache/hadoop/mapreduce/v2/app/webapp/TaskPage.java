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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|ContainerId
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
name|ConverterUtils
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

begin_class
DECL|class|TaskPage
specifier|public
class|class
name|TaskPage
extends|extends
name|AppView
block|{
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
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
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
literal|".progress"
argument_list|,
literal|"Progress"
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
literal|".node"
argument_list|,
literal|"node"
argument_list|)
operator|.
name|th
argument_list|(
literal|".tsh"
argument_list|,
literal|"Started"
argument_list|)
operator|.
name|th
argument_list|(
literal|".tsh"
argument_list|,
literal|"Finished"
argument_list|)
operator|.
name|th
argument_list|(
literal|".tsh"
argument_list|,
literal|"Elapsed"
argument_list|)
operator|.
name|th
argument_list|(
literal|".note"
argument_list|,
literal|"Note"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|ta
range|:
name|getTaskAttempts
argument_list|()
control|)
block|{
name|String
name|taid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|ta
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|progress
init|=
name|percent
argument_list|(
name|ta
operator|.
name|getProgress
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ta
operator|.
name|getAssignedContainerID
argument_list|()
decl_stmt|;
name|String
name|nodeHttpAddr
init|=
name|ta
operator|.
name|getNodeHttpAddress
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|ta
operator|.
name|getLaunchTime
argument_list|()
decl_stmt|;
name|long
name|finishTime
init|=
name|ta
operator|.
name|getFinishTime
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
name|Times
operator|.
name|elapsed
argument_list|(
name|startTime
argument_list|,
name|finishTime
argument_list|)
decl_stmt|;
name|TD
argument_list|<
name|TR
argument_list|<
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|nodeTd
init|=
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|".id"
argument_list|,
name|taid
argument_list|)
operator|.
name|td
argument_list|(
literal|".progress"
argument_list|,
name|progress
argument_list|)
operator|.
name|td
argument_list|(
literal|".state"
argument_list|,
name|ta
operator|.
name|getState
argument_list|()
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
name|nodeHttpAddr
argument_list|)
argument_list|,
name|nodeHttpAddr
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerId
operator|!=
literal|null
condition|)
block|{
name|String
name|containerIdStr
init|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|nodeTd
operator|.
name|_
argument_list|(
literal|" "
argument_list|)
operator|.
name|a
argument_list|(
literal|".logslink"
argument_list|,
name|url
argument_list|(
literal|"http://"
argument_list|,
name|nodeHttpAddr
argument_list|,
literal|"node"
argument_list|,
literal|"containerlogs"
argument_list|,
name|containerIdStr
argument_list|,
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|,
literal|"logs"
argument_list|)
expr_stmt|;
block|}
name|nodeTd
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
literal|".ts"
argument_list|,
name|Times
operator|.
name|format
argument_list|(
name|startTime
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
literal|".ts"
argument_list|,
name|Times
operator|.
name|format
argument_list|(
name|finishTime
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
literal|".dt"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|elapsed
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
literal|".note"
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|join
argument_list|(
name|ta
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|tbody
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
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
DECL|method|preHead (Page.HTML<_> html)
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
name|_
argument_list|>
name|html
parameter_list|)
block|{
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|ACCORDION
argument_list|,
literal|"nav"
argument_list|)
argument_list|,
literal|"{autoHeight:false, active:3}"
argument_list|)
expr_stmt|;
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
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"attempts"
argument_list|)
expr_stmt|;
block|}
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
DECL|method|attemptsTableInit ()
specifier|private
name|String
name|attemptsTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

