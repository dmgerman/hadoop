begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
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
name|YarnWebParams
operator|.
name|APPLICATION_ID
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
name|_TH
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|LogAggregationStatus
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
name|conf
operator|.
name|YarnConfiguration
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|LogAggregationReport
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
name|server
operator|.
name|resourcemanager
operator|.
name|ResourceManager
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppImpl
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
name|Apps
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
DECL|class|RMAppLogAggregationStatusBlock
specifier|public
class|class
name|RMAppLogAggregationStatusBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RMAppLogAggregationStatusBlock
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rm
specifier|private
specifier|final
name|ResourceManager
name|rm
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Inject
DECL|method|RMAppLogAggregationStatusBlock (ViewContext ctx, ResourceManager rm, Configuration conf)
name|RMAppLogAggregationStatusBlock
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|ResourceManager
name|rm
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|rm
operator|=
name|rm
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
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
name|String
name|aid
init|=
name|$
argument_list|(
name|APPLICATION_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|aid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|puts
argument_list|(
literal|"Bad request: requires Application ID"
argument_list|)
expr_stmt|;
return|return;
block|}
name|ApplicationId
name|appId
decl_stmt|;
try|try
block|{
name|appId
operator|=
name|Apps
operator|.
name|toAppID
argument_list|(
name|aid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|puts
argument_list|(
literal|"Invalid Application ID: "
operator|+
name|aid
argument_list|)
expr_stmt|;
return|return;
block|}
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Application "
argument_list|,
name|aid
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add LogAggregationStatus description table
comment|// to explain the meaning of different LogAggregationStatus
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|div_description
init|=
name|html
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|table_description
init|=
name|div_description
operator|.
name|table
argument_list|(
literal|"#LogAggregationStatusDecription"
argument_list|)
decl_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Log Aggregation Status"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Description"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|LogAggregationStatus
operator|.
name|DISABLED
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
literal|"Log Aggregation is Disabled."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|LogAggregationStatus
operator|.
name|NOT_START
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
literal|"Log Aggregation does not Start."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|LogAggregationStatus
operator|.
name|RUNNING
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
literal|"Log Aggregation is Running."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|LogAggregationStatus
operator|.
name|RUNNING_WITH_FAILURE
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
literal|"Log Aggregation is Running, but has failures "
operator|+
literal|"in previous cycles"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|LogAggregationStatus
operator|.
name|SUCCEEDED
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
literal|"Log Aggregation is Succeeded. All of the logs have been "
operator|+
literal|"aggregated successfully."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|LogAggregationStatus
operator|.
name|FAILED
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
literal|"Log Aggregation is Failed. At least one of the logs "
operator|+
literal|"have not been aggregated."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|LogAggregationStatus
operator|.
name|TIME_OUT
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
literal|"The application is finished, but the log aggregation status is "
operator|+
literal|"not updated for a long time. Not sure whether the log aggregation "
operator|+
literal|"is finished or not."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|table_description
operator|.
name|__
argument_list|()
expr_stmt|;
name|div_description
operator|.
name|__
argument_list|()
expr_stmt|;
name|RMApp
name|rmApp
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
comment|// Application Log aggregation status Table
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|div
init|=
name|html
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
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
name|h3
argument_list|(
literal|"Log Aggregation: "
operator|+
operator|(
name|rmApp
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|rmApp
operator|.
name|getLogAggregationStatusForAppReport
argument_list|()
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|rmApp
operator|.
name|getLogAggregationStatusForAppReport
argument_list|()
operator|.
name|name
argument_list|()
operator|)
argument_list|)
operator|.
name|table
argument_list|(
literal|"#LogAggregationStatus"
argument_list|)
decl_stmt|;
name|int
name|maxLogAggregationDiagnosticsInMemory
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_MAX_LOG_AGGREGATION_DIAGNOSTICS_IN_MEMORY
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_MAX_LOG_AGGREGATION_DIAGNOSTICS_IN_MEMORY
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"NodeId"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Log Aggregation Status"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Last "
operator|+
name|maxLogAggregationDiagnosticsInMemory
operator|+
literal|" Diagnostic Messages"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Last "
operator|+
name|maxLogAggregationDiagnosticsInMemory
operator|+
literal|" Failure Messages"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|logAggregationReports
init|=
name|rmApp
operator|.
name|getLogAggregationReportsForApp
argument_list|()
decl_stmt|;
if|if
condition|(
name|logAggregationReports
operator|!=
literal|null
operator|&&
operator|!
name|logAggregationReports
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|report
range|:
name|logAggregationReports
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LogAggregationStatus
name|status
init|=
name|report
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
decl_stmt|;
name|String
name|message
init|=
name|report
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getDiagnosticMessage
argument_list|()
decl_stmt|;
name|String
name|failureMessage
init|=
name|report
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
operator|(
name|RMAppImpl
operator|)
name|rmApp
operator|)
operator|.
name|getLogAggregationFailureMessagesForNM
argument_list|(
name|report
operator|.
name|getKey
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
name|report
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|status
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|status
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|message
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|message
argument_list|)
operator|.
name|td
argument_list|(
name|failureMessage
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|failureMessage
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|table
operator|.
name|__
argument_list|()
expr_stmt|;
name|div
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

