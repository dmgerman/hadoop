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
name|FileInputStream
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|io
operator|.
name|IOUtils
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
name|webapp
operator|.
name|NotFoundException
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
name|PRE
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
DECL|field|nmContext
specifier|private
specifier|final
name|Context
name|nmContext
decl_stmt|;
annotation|@
name|Inject
DECL|method|ContainersLogsBlock (Context context)
specifier|public
name|ContainersLogsBlock
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|this
operator|.
name|nmContext
operator|=
name|context
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
name|ContainerId
operator|.
name|fromString
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
name|ex
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|(
literal|"Invalid container ID: "
operator|+
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
if|if
condition|(
name|$
argument_list|(
name|CONTAINER_LOG_TYPE
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|logFiles
init|=
name|ContainerLogsUtils
operator|.
name|getContainerLogDirs
argument_list|(
name|containerId
argument_list|,
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
argument_list|,
name|nmContext
argument_list|)
decl_stmt|;
name|printLogFileDirectory
argument_list|(
name|html
argument_list|,
name|logFiles
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|logFile
init|=
name|ContainerLogsUtils
operator|.
name|getContainerLogFile
argument_list|(
name|containerId
argument_list|,
name|$
argument_list|(
name|CONTAINER_LOG_TYPE
argument_list|)
argument_list|,
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
argument_list|,
name|nmContext
argument_list|)
decl_stmt|;
name|printLogFile
argument_list|(
name|html
argument_list|,
name|logFile
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|YarnException
name|ex
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|ex
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|printLogFile (Block html, File logFile)
specifier|private
name|void
name|printLogFile
parameter_list|(
name|Block
name|html
parameter_list|,
name|File
name|logFile
parameter_list|)
block|{
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
name|FileInputStream
name|logByteStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logByteStream
operator|=
name|ContainerLogsUtils
operator|.
name|openLogFileForRead
argument_list|(
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|,
name|logFile
argument_list|,
name|nmContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|__
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
name|__
argument_list|(
literal|" for full log"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|logByteStream
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|logByteStream
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
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
name|PRE
argument_list|<
name|Hamlet
argument_list|>
name|pre
init|=
name|html
operator|.
name|pre
argument_list|()
decl_stmt|;
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
name|pre
operator|.
name|__
argument_list|(
operator|new
name|String
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
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
name|pre
operator|.
name|__
argument_list|()
expr_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception reading log file "
operator|+
name|logFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|html
operator|.
name|h1
argument_list|(
literal|"Exception reading log file. It might be because log "
operator|+
literal|"file was aggregated : "
operator|+
name|logFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|logByteStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|logByteStream
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
DECL|method|printLogFileDirectory (Block html, List<File> containerLogsDirs)
specifier|private
name|void
name|printLogFileDirectory
parameter_list|(
name|Block
name|html
parameter_list|,
name|List
argument_list|<
name|File
argument_list|>
name|containerLogsDirs
parameter_list|)
block|{
comment|// Print out log types in lexical order
name|Collections
operator|.
name|sort
argument_list|(
name|containerLogsDirs
argument_list|)
expr_stmt|;
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
name|File
index|[]
name|logFiles
init|=
name|containerLogsDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|logFiles
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|logFiles
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|logFile
range|:
name|logFiles
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
name|__
argument_list|()
expr_stmt|;
block|}
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
name|$
argument_list|(
name|CONTAINER_ID
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
end_class

end_unit

