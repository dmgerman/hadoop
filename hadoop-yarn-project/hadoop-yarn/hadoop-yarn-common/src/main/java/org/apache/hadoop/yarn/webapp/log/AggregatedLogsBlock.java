begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.log
package|package
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
name|log
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
name|webapp
operator|.
name|YarnWebParams
operator|.
name|APP_OWNER
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
name|CONTAINER_ID
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
name|ENTITY_STRING
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
name|NM_NODENAME
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
name|logaggregation
operator|.
name|LogAggregationWebUtils
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
name|logaggregation
operator|.
name|filecontroller
operator|.
name|LogAggregationFileController
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
name|logaggregation
operator|.
name|filecontroller
operator|.
name|LogAggregationFileControllerFactory
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
name|util
operator|.
name|WebAppUtils
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
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|class|AggregatedLogsBlock
specifier|public
class|class
name|AggregatedLogsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|factory
specifier|private
specifier|final
name|LogAggregationFileControllerFactory
name|factory
decl_stmt|;
annotation|@
name|Inject
DECL|method|AggregatedLogsBlock (Configuration conf)
name|AggregatedLogsBlock
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
name|factory
operator|=
operator|new
name|LogAggregationFileControllerFactory
argument_list|(
name|conf
argument_list|)
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
name|ContainerId
name|containerId
init|=
name|LogAggregationWebUtils
operator|.
name|verifyAndGetContainerId
argument_list|(
name|html
argument_list|,
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|LogAggregationWebUtils
operator|.
name|verifyAndGetNodeId
argument_list|(
name|html
argument_list|,
name|$
argument_list|(
name|NM_NODENAME
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|appOwner
init|=
name|LogAggregationWebUtils
operator|.
name|verifyAndGetAppOwner
argument_list|(
name|html
argument_list|,
name|$
argument_list|(
name|APP_OWNER
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|isValid
init|=
literal|true
decl_stmt|;
try|try
block|{
name|LogAggregationWebUtils
operator|.
name|getLogStartIndex
argument_list|(
name|html
argument_list|,
name|$
argument_list|(
literal|"start"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ne
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Invalid log start value: "
operator|+
name|$
argument_list|(
literal|"start"
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|isValid
operator|=
literal|false
expr_stmt|;
block|}
try|try
block|{
name|LogAggregationWebUtils
operator|.
name|getLogEndIndex
argument_list|(
name|html
argument_list|,
name|$
argument_list|(
literal|"end"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ne
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Invalid log end value: "
operator|+
name|$
argument_list|(
literal|"end"
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|isValid
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|containerId
operator|==
literal|null
operator|||
name|nodeId
operator|==
literal|null
operator|||
name|appOwner
operator|==
literal|null
operator|||
name|appOwner
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|isValid
condition|)
block|{
return|return;
block|}
name|ApplicationId
name|applicationId
init|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|String
name|logEntity
init|=
name|$
argument_list|(
name|ENTITY_STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|logEntity
operator|==
literal|null
operator|||
name|logEntity
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logEntity
operator|=
name|containerId
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|String
name|nmApplicationLogUrl
init|=
name|getApplicationLogURL
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LOG_AGGREGATION_ENABLED
argument_list|)
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Aggregation is not enabled. Try the nodemanager at "
operator|+
name|nodeId
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
if|if
condition|(
name|nmApplicationLogUrl
operator|!=
literal|null
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Or see application log at "
operator|+
name|nmApplicationLogUrl
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
name|LogAggregationFileController
name|fileController
decl_stmt|;
try|try
block|{
name|fileController
operator|=
name|this
operator|.
name|factory
operator|.
name|getFileControllerForRead
argument_list|(
name|applicationId
argument_list|,
name|appOwner
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|fnf
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Logs not available for "
operator|+
name|logEntity
operator|+
literal|". Aggregation may not be complete, Check back later or "
operator|+
literal|"try to find the container logs in the local directory of "
operator|+
literal|"nodemanager "
operator|+
name|nodeId
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
if|if
condition|(
name|nmApplicationLogUrl
operator|!=
literal|null
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Or see application log at "
operator|+
name|nmApplicationLogUrl
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
name|fileController
operator|.
name|renderAggregatedLogsBlock
argument_list|(
name|html
argument_list|,
name|this
operator|.
name|context
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getApplicationLogURL (ApplicationId applicationId)
specifier|private
name|String
name|getApplicationLogURL
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|String
name|appId
init|=
name|applicationId
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|appId
operator|==
literal|null
operator|||
name|appId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|nodeId
init|=
name|$
argument_list|(
name|NM_NODENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeId
operator|==
literal|null
operator|||
name|nodeId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|scheme
init|=
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|this
operator|.
name|conf
argument_list|)
condition|?
literal|"https://"
else|:
literal|"http://"
decl_stmt|;
name|String
name|webAppURLWithoutScheme
init|=
name|WebAppUtils
operator|.
name|getNMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|webAppURLWithoutScheme
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
name|String
name|httpPort
init|=
name|webAppURLWithoutScheme
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
decl_stmt|;
name|nodeId
operator|=
name|NodeId
operator|.
name|fromString
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|httpPort
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|scheme
argument_list|)
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
operator|.
name|append
argument_list|(
literal|"/node/application/"
argument_list|)
operator|.
name|append
argument_list|(
name|appId
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

