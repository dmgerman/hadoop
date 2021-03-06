begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp
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
name|webapp
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|security
operator|.
name|UserGroupInformation
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
name|ApplicationBaseProtocol
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
name|protocolrecords
operator|.
name|GetContainerReportRequest
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
name|ContainerReport
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
name|ResourceInformation
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
name|exceptions
operator|.
name|YarnException
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
name|webapp
operator|.
name|dao
operator|.
name|ContainerInfo
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|CONTAINER_ID
import|;
end_import

begin_class
DECL|class|ContainerBlock
specifier|public
class|class
name|ContainerBlock
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
name|ContainerBlock
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|appBaseProt
specifier|protected
name|ApplicationBaseProtocol
name|appBaseProt
decl_stmt|;
annotation|@
name|Inject
DECL|method|ContainerBlock (ApplicationBaseProtocol appBaseProt, ViewContext ctx)
specifier|public
name|ContainerBlock
parameter_list|(
name|ApplicationBaseProtocol
name|appBaseProt
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
name|this
operator|.
name|appBaseProt
operator|=
name|appBaseProt
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
name|containerid
init|=
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|puts
argument_list|(
literal|"Bad request: requires container ID"
argument_list|)
expr_stmt|;
return|return;
block|}
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|containerId
operator|=
name|ContainerId
operator|.
name|fromString
argument_list|(
name|containerid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|puts
argument_list|(
literal|"Invalid container ID: "
operator|+
name|containerid
argument_list|)
expr_stmt|;
return|return;
block|}
name|UserGroupInformation
name|callerUGI
init|=
name|getCallerUGI
argument_list|()
decl_stmt|;
name|ContainerReport
name|containerReport
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|GetContainerReportRequest
name|request
init|=
name|GetContainerReportRequest
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|callerUGI
operator|==
literal|null
condition|)
block|{
name|containerReport
operator|=
name|getContainerReport
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|containerReport
operator|=
name|callerUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ContainerReport
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContainerReport
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getContainerReport
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Failed to read the container "
operator|+
name|containerid
operator|+
literal|"."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
name|message
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|containerReport
operator|==
literal|null
condition|)
block|{
name|puts
argument_list|(
literal|"Container not found: "
operator|+
name|containerid
argument_list|)
expr_stmt|;
return|return;
block|}
name|ContainerInfo
name|container
init|=
operator|new
name|ContainerInfo
argument_list|(
name|containerReport
argument_list|)
decl_stmt|;
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Container "
argument_list|,
name|containerid
argument_list|)
argument_list|)
expr_stmt|;
name|info
argument_list|(
literal|"Container Overview"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Container State:"
argument_list|,
name|container
operator|.
name|getContainerState
argument_list|()
operator|==
literal|null
condition|?
name|UNAVAILABLE
else|:
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Exit Status:"
argument_list|,
name|container
operator|.
name|getContainerExitStatus
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Node:"
argument_list|,
name|container
operator|.
name|getNodeHttpAddress
argument_list|()
operator|==
literal|null
condition|?
literal|"#"
else|:
name|container
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|,
name|container
operator|.
name|getNodeHttpAddress
argument_list|()
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|container
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Priority:"
argument_list|,
name|container
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Started:"
argument_list|,
name|Times
operator|.
name|format
argument_list|(
name|container
operator|.
name|getStartedTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
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
name|container
operator|.
name|getStartedTime
argument_list|()
argument_list|,
name|container
operator|.
name|getFinishedTime
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Resource:"
argument_list|,
name|getResources
argument_list|(
name|container
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Logs:"
argument_list|,
name|container
operator|.
name|getLogUrl
argument_list|()
operator|==
literal|null
condition|?
literal|"#"
else|:
name|container
operator|.
name|getLogUrl
argument_list|()
argument_list|,
name|container
operator|.
name|getLogUrl
argument_list|()
operator|==
literal|null
condition|?
literal|"N/A"
else|:
literal|"Logs"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Diagnostics:"
argument_list|,
name|container
operator|.
name|getDiagnosticsInfo
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|container
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|)
expr_stmt|;
name|html
operator|.
name|__
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a string representation of allocated resources to a container.    * Memory, followed with VCores are always the first two resources of    * the resulted string, followed with any custom resources, if any is present.    */
annotation|@
name|VisibleForTesting
DECL|method|getResources (ContainerInfo container)
name|String
name|getResources
parameter_list|(
name|ContainerInfo
name|container
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|allocatedResources
init|=
name|container
operator|.
name|getAllocatedResources
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getResourceAsString
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|,
name|allocatedResources
operator|.
name|get
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getResourceAsString
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|,
name|allocatedResources
operator|.
name|get
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|.
name|hasCustomResources
argument_list|()
condition|)
block|{
name|container
operator|.
name|getAllocatedResources
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
block|{
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
operator|&&
operator|!
name|key
operator|.
name|equals
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getResourceAsString
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getResourceAsString (String resourceName, long value)
specifier|private
name|String
name|getResourceAsString
parameter_list|(
name|String
name|resourceName
parameter_list|,
name|long
name|value
parameter_list|)
block|{
specifier|final
name|String
name|translatedResourceName
decl_stmt|;
switch|switch
condition|(
name|resourceName
condition|)
block|{
case|case
name|ResourceInformation
operator|.
name|MEMORY_URI
case|:
name|translatedResourceName
operator|=
literal|"Memory"
expr_stmt|;
break|break;
case|case
name|ResourceInformation
operator|.
name|VCORES_URI
case|:
name|translatedResourceName
operator|=
literal|"VCores"
expr_stmt|;
break|break;
default|default:
name|translatedResourceName
operator|=
name|resourceName
expr_stmt|;
break|break;
block|}
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
operator|+
literal|" "
operator|+
name|translatedResourceName
return|;
block|}
DECL|method|getContainerReport ( final GetContainerReportRequest request)
specifier|protected
name|ContainerReport
name|getContainerReport
parameter_list|(
specifier|final
name|GetContainerReportRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|appBaseProt
operator|.
name|getContainerReport
argument_list|(
name|request
argument_list|)
operator|.
name|getContainerReport
argument_list|()
return|;
block|}
block|}
end_class

end_unit

