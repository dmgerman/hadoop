begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|nodemanager
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
name|ACCORDION_ID
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
name|THEMESWITCHER_ID
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
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|fs
operator|.
name|Path
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
name|records
operator|.
name|ApplicationAccessType
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
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|LocalDirsHandlerService
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerState
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|launcher
operator|.
name|ContainerLaunch
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
name|security
operator|.
name|ApplicationACLsManager
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
name|webapp
operator|.
name|YarnWebParams
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
DECL|class|ContainerLogsPage
specifier|public
class|class
name|ContainerLogsPage
extends|extends
name|NMView
block|{
DECL|field|REDIRECT_URL
specifier|public
specifier|static
specifier|final
name|String
name|REDIRECT_URL
init|=
literal|"redirect.url"
decl_stmt|;
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
name|String
name|redirectUrl
init|=
name|$
argument_list|(
name|REDIRECT_URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|redirectUrl
operator|==
literal|null
operator|||
name|redirectUrl
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|set
argument_list|(
name|TITLE
argument_list|,
name|join
argument_list|(
literal|"Logs for "
argument_list|,
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|redirectUrl
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
name|set
argument_list|(
name|TITLE
argument_list|,
name|join
argument_list|(
literal|"Failed redirect for "
argument_list|,
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Error getting redirect url. Fall through.
block|}
else|else
block|{
name|set
argument_list|(
name|TITLE
argument_list|,
name|join
argument_list|(
literal|"Redirecting to log server for "
argument_list|,
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|html
operator|.
name|meta_http
argument_list|(
literal|"refresh"
argument_list|,
literal|"1; url="
operator|+
name|redirectUrl
argument_list|)
expr_stmt|;
block|}
block|}
name|set
argument_list|(
name|ACCORDION_ID
argument_list|,
literal|"nav"
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
literal|"{autoHeight:false, active:0}"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|THEMESWITCHER_ID
argument_list|,
literal|"themeswitcher"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|content ()
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
name|ContainersLogsBlock
operator|.
name|class
return|;
block|}
DECL|class|ContainersLogsBlock
specifier|public
specifier|static
class|class
name|ContainersLogsBlock
extends|extends
name|HtmlBlock
implements|implements
name|YarnWebParams
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|nmContext
specifier|private
specifier|final
name|Context
name|nmContext
decl_stmt|;
DECL|field|aclsManager
specifier|private
specifier|final
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|dirsHandler
specifier|private
specifier|final
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
annotation|@
name|Inject
DECL|method|ContainersLogsBlock (Configuration conf, Context context, ApplicationACLsManager aclsManager, LocalDirsHandlerService dirsHandler)
specifier|public
name|ContainersLogsBlock
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Context
name|context
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|nmContext
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|aclsManager
operator|=
name|aclsManager
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
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
name|redirectUrl
init|=
name|$
argument_list|(
name|REDIRECT_URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|redirectUrl
operator|!=
literal|null
operator|&&
name|redirectUrl
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Failed while trying to construct the redirect url to the log"
operator|+
literal|" server. Log Server url may not be configured"
argument_list|)
expr_stmt|;
comment|//Intentional fallthrough.
block|}
name|ContainerId
name|containerId
decl_stmt|;
try|try
block|{
name|containerId
operator|=
name|ConverterUtils
operator|.
name|toContainerId
argument_list|(
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Invalid containerId "
operator|+
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
expr_stmt|;
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
name|Application
name|application
init|=
name|this
operator|.
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
name|this
operator|.
name|nmContext
operator|.
name|getContainers
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|application
operator|==
literal|null
condition|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Unknown container. Container either has not started or "
operator|+
literal|"has already completed or "
operator|+
literal|"doesn't belong to this node at all."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
comment|// Container may have alerady completed, but logs not aggregated yet.
name|printLogs
argument_list|(
name|html
argument_list|,
name|containerId
argument_list|,
name|applicationId
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|EnumSet
operator|.
name|of
argument_list|(
name|ContainerState
operator|.
name|NEW
argument_list|,
name|ContainerState
operator|.
name|LOCALIZING
argument_list|,
name|ContainerState
operator|.
name|LOCALIZED
argument_list|)
operator|.
name|contains
argument_list|(
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
condition|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Container is not yet running. Current state is "
operator|+
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|container
operator|.
name|getContainerState
argument_list|()
operator|==
name|ContainerState
operator|.
name|LOCALIZATION_FAILED
condition|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Container wasn't started. Localization failed."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|EnumSet
operator|.
name|of
argument_list|(
name|ContainerState
operator|.
name|RUNNING
argument_list|,
name|ContainerState
operator|.
name|EXITED_WITH_FAILURE
argument_list|,
name|ContainerState
operator|.
name|EXITED_WITH_SUCCESS
argument_list|)
operator|.
name|contains
argument_list|(
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
condition|)
block|{
name|printLogs
argument_list|(
name|html
argument_list|,
name|containerId
argument_list|,
name|applicationId
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|EnumSet
operator|.
name|of
argument_list|(
name|ContainerState
operator|.
name|KILLING
argument_list|,
name|ContainerState
operator|.
name|CONTAINER_CLEANEDUP_AFTER_KILL
argument_list|,
name|ContainerState
operator|.
name|CONTAINER_RESOURCES_CLEANINGUP
argument_list|)
operator|.
name|contains
argument_list|(
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
condition|)
block|{
comment|//Container may have generated some logs before being killed.
name|printLogs
argument_list|(
name|html
argument_list|,
name|containerId
argument_list|,
name|applicationId
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|container
operator|.
name|getContainerState
argument_list|()
operator|.
name|equals
argument_list|(
name|ContainerState
operator|.
name|DONE
argument_list|)
condition|)
block|{
comment|// Prev state unknown. Logs may be available.
name|printLogs
argument_list|(
name|html
argument_list|,
name|containerId
argument_list|,
name|applicationId
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Container is no longer running..."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
DECL|method|printLogs (Block html, ContainerId containerId, ApplicationId applicationId, Application application)
specifier|private
name|void
name|printLogs
parameter_list|(
name|Block
name|html
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|Application
name|application
parameter_list|)
block|{
comment|// Check for the authorization.
name|String
name|remoteUser
init|=
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUGI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteUser
operator|!=
literal|null
condition|)
block|{
name|callerUGI
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|callerUGI
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|aclsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|application
operator|.
name|getUser
argument_list|()
argument_list|,
name|applicationId
argument_list|)
condition|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"User ["
operator|+
name|remoteUser
operator|+
literal|"] is not authorized to view the logs for application "
operator|+
name|applicationId
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|$
argument_list|(
name|CONTAINER_LOG_TYPE
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|File
name|logFile
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logFile
operator|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|dirsHandler
operator|.
name|getLogPathToRead
argument_list|(
name|ContainerLaunch
operator|.
name|getRelativeContainerLogDir
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|$
argument_list|(
name|CONTAINER_LOG_TYPE
argument_list|)
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Cannot find this log on the local disk."
argument_list|)
expr_stmt|;
return|return;
block|}
name|long
name|start
init|=
name|$
argument_list|(
literal|"start"
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|?
operator|-
literal|4
operator|*
literal|1024
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|$
argument_list|(
literal|"start"
argument_list|)
argument_list|)
decl_stmt|;
name|start
operator|=
name|start
operator|<
literal|0
condition|?
name|logFile
operator|.
name|length
argument_list|()
operator|+
name|start
else|:
name|start
expr_stmt|;
name|start
operator|=
name|start
operator|<
literal|0
condition|?
literal|0
else|:
name|start
expr_stmt|;
name|long
name|end
init|=
name|$
argument_list|(
literal|"end"
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|?
name|logFile
operator|.
name|length
argument_list|()
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|$
argument_list|(
literal|"end"
argument_list|)
argument_list|)
decl_stmt|;
name|end
operator|=
name|end
operator|<
literal|0
condition|?
name|logFile
operator|.
name|length
argument_list|()
operator|+
name|end
else|:
name|end
expr_stmt|;
name|end
operator|=
name|end
operator|<
literal|0
condition|?
name|logFile
operator|.
name|length
argument_list|()
else|:
name|end
expr_stmt|;
if|if
condition|(
name|start
operator|>
name|end
condition|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Invalid start and end values. Start: ["
operator|+
name|start
operator|+
literal|"]"
operator|+
literal|", end["
operator|+
name|end
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|InputStreamReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|toRead
init|=
name|end
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|toRead
operator|<
name|logFile
operator|.
name|length
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
literal|"Showing "
operator|+
name|toRead
operator|+
literal|" bytes. Click "
argument_list|)
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"containerlogs"
argument_list|,
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|,
name|$
argument_list|(
name|APP_OWNER
argument_list|)
argument_list|,
name|logFile
operator|.
name|getName
argument_list|()
argument_list|,
literal|"?start=0"
argument_list|)
argument_list|,
literal|"here"
argument_list|)
operator|.
name|_
argument_list|(
literal|" for full log"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
comment|// TODO: Use secure IO Utils to avoid symlink attacks.
comment|// TODO Fix findBugs close warning along with IOUtils change
name|reader
operator|=
operator|new
name|FileReader
argument_list|(
name|logFile
argument_list|)
expr_stmt|;
name|int
name|bufferSize
init|=
literal|65536
decl_stmt|;
name|char
index|[]
name|cbuf
init|=
operator|new
name|char
index|[
name|bufferSize
index|]
decl_stmt|;
name|long
name|skipped
init|=
literal|0
decl_stmt|;
name|long
name|totalSkipped
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|totalSkipped
operator|<
name|start
condition|)
block|{
name|skipped
operator|=
name|reader
operator|.
name|skip
argument_list|(
name|start
operator|-
name|totalSkipped
argument_list|)
expr_stmt|;
name|totalSkipped
operator|+=
name|skipped
expr_stmt|;
block|}
name|int
name|len
init|=
literal|0
decl_stmt|;
name|int
name|currentToRead
init|=
name|toRead
operator|>
name|bufferSize
condition|?
name|bufferSize
else|:
operator|(
name|int
operator|)
name|toRead
decl_stmt|;
name|writer
argument_list|()
operator|.
name|write
argument_list|(
literal|"<pre>"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|reader
operator|.
name|read
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|currentToRead
argument_list|)
operator|)
operator|>
literal|0
operator|&&
name|toRead
operator|>
literal|0
condition|)
block|{
name|writer
argument_list|()
operator|.
name|write
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// TODO: HTMl Quoting?
name|toRead
operator|=
name|toRead
operator|-
name|len
expr_stmt|;
name|currentToRead
operator|=
name|toRead
operator|>
name|bufferSize
condition|?
name|bufferSize
else|:
operator|(
name|int
operator|)
name|toRead
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
argument_list|()
operator|.
name|write
argument_list|(
literal|"</pre>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Exception reading log-file. Log file was likely aggregated. "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Ignore
block|}
block|}
block|}
block|}
block|}
else|else
block|{
comment|// Just print out the log-types
name|List
argument_list|<
name|File
argument_list|>
name|containerLogsDirs
init|=
name|getContainerLogDirs
argument_list|(
name|containerId
argument_list|,
name|dirsHandler
argument_list|)
decl_stmt|;
name|boolean
name|foundLogFile
init|=
literal|false
decl_stmt|;
for|for
control|(
name|File
name|containerLogsDir
range|:
name|containerLogsDirs
control|)
block|{
for|for
control|(
name|File
name|logFile
range|:
name|containerLogsDir
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|foundLogFile
operator|=
literal|true
expr_stmt|;
name|html
operator|.
name|p
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"containerlogs"
argument_list|,
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|,
name|$
argument_list|(
name|APP_OWNER
argument_list|)
argument_list|,
name|logFile
operator|.
name|getName
argument_list|()
argument_list|,
literal|"?start=-4096"
argument_list|)
argument_list|,
name|logFile
operator|.
name|getName
argument_list|()
operator|+
literal|" : Total file length is "
operator|+
name|logFile
operator|.
name|length
argument_list|()
operator|+
literal|" bytes."
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|foundLogFile
condition|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"No logs available for container "
operator|+
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
return|return;
block|}
DECL|method|getContainerLogDirs (ContainerId containerId, LocalDirsHandlerService dirsHandler)
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|getContainerLogDirs
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
init|=
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|containerLogDirs
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|(
name|logDirs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|logDir
range|:
name|logDirs
control|)
block|{
name|String
name|appIdStr
init|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|appLogDir
init|=
operator|new
name|File
argument_list|(
name|logDir
argument_list|,
name|appIdStr
argument_list|)
decl_stmt|;
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
name|containerLogDirs
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|appLogDir
argument_list|,
name|containerIdStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|containerLogDirs
return|;
block|}
block|}
block|}
end_class

end_unit

