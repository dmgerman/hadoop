begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.preprocessor
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
name|preprocessor
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
name|ApplicationSubmissionContext
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

begin_comment
comment|/**  * Pre process the ApplicationSubmissionContext with server side info.  */
end_comment

begin_class
DECL|class|SubmissionContextPreProcessor
specifier|public
class|class
name|SubmissionContextPreProcessor
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
name|SubmissionContextPreProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_COMMANDS
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_COMMANDS
init|=
literal|"*"
decl_stmt|;
DECL|field|INITIAL_DELAY
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_DELAY
init|=
literal|1000
decl_stmt|;
DECL|enum|ContextProp
enum|enum
name|ContextProp
block|{
comment|// Node label Expression
DECL|enumConstant|NL
name|NL
argument_list|(
operator|new
name|NodeLabelProcessor
argument_list|()
argument_list|)
block|,
comment|// Queue
DECL|enumConstant|Q
name|Q
argument_list|(
operator|new
name|QueueProcessor
argument_list|()
argument_list|)
block|,
comment|// Tag Add
DECL|enumConstant|TA
name|TA
argument_list|(
operator|new
name|TagAddProcessor
argument_list|()
argument_list|)
block|;
DECL|field|cp
specifier|private
name|ContextProcessor
name|cp
decl_stmt|;
DECL|method|ContextProp (ContextProcessor cp)
name|ContextProp
parameter_list|(
name|ContextProcessor
name|cp
parameter_list|)
block|{
name|this
operator|.
name|cp
operator|=
name|cp
expr_stmt|;
block|}
block|}
DECL|field|hostsFilePath
specifier|private
name|String
name|hostsFilePath
decl_stmt|;
DECL|field|lastModified
specifier|private
specifier|volatile
name|long
name|lastModified
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|hostCommands
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ContextProp
argument_list|,
name|String
argument_list|>
argument_list|>
name|hostCommands
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|executorService
specifier|private
name|ScheduledExecutorService
name|executorService
decl_stmt|;
DECL|method|start (Configuration conf)
specifier|public
name|void
name|start
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|hostsFilePath
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SUBMISSION_PREPROCESSOR_FILE_PATH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SUBMISSION_PREPROCESSOR_FILE_PATH
argument_list|)
expr_stmt|;
name|int
name|refreshPeriod
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SUBMISSION_PREPROCESSOR_REFRESH_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SUBMISSION_PREPROCESSOR_REFRESH_INTERVAL_MS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Submission Context Preprocessor enabled: file=[{}], "
operator|+
literal|"interval=[{}]"
argument_list|,
name|this
operator|.
name|hostsFilePath
argument_list|,
name|refreshPeriod
argument_list|)
expr_stmt|;
name|executorService
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
expr_stmt|;
name|Runnable
name|refreshConf
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while refreshing Submission PreProcessor file [{}]"
argument_list|,
name|hostsFilePath
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|refreshPeriod
operator|>
literal|0
condition|)
block|{
name|executorService
operator|.
name|scheduleAtFixedRate
argument_list|(
name|refreshConf
argument_list|,
name|INITIAL_DELAY
argument_list|,
name|refreshPeriod
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|executorService
operator|.
name|schedule
argument_list|(
name|refreshConf
argument_list|,
name|INITIAL_DELAY
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|executorService
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|preProcess (String host, ApplicationId applicationId, ApplicationSubmissionContext submissionContext)
specifier|public
name|void
name|preProcess
parameter_list|(
name|String
name|host
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|ApplicationSubmissionContext
name|submissionContext
parameter_list|)
block|{
name|Map
argument_list|<
name|ContextProp
argument_list|,
name|String
argument_list|>
name|cMap
init|=
name|hostCommands
operator|.
name|get
argument_list|(
name|host
argument_list|)
decl_stmt|;
comment|// Try regex match
if|if
condition|(
name|cMap
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ContextProp
argument_list|,
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|hostCommands
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|DEFAULT_COMMANDS
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|cMap
operator|=
name|hostCommands
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|exception
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid regex pattern: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Set to default value
if|if
condition|(
name|cMap
operator|==
literal|null
condition|)
block|{
name|cMap
operator|=
name|hostCommands
operator|.
name|get
argument_list|(
name|DEFAULT_COMMANDS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cMap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContextProp
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|cMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|cp
operator|.
name|process
argument_list|(
name|host
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|applicationId
argument_list|,
name|submissionContext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
literal|null
operator|==
name|hostsFilePath
operator|||
name|hostsFilePath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Host list file path [{}] is empty or does not exist !!"
argument_list|,
name|hostsFilePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|hostFile
init|=
operator|new
name|File
argument_list|(
name|hostsFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hostFile
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|hostFile
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Host list file [{}] does not exist or is not a file !!"
argument_list|,
name|hostFile
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hostFile
operator|.
name|lastModified
argument_list|()
operator|<=
name|lastModified
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Host list file [{}] has not been modified from last refresh"
argument_list|,
name|hostFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FileInputStream
name|fileInputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|hostFile
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ContextProp
argument_list|,
name|String
argument_list|>
argument_list|>
name|tempHostCommands
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fileInputStream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Lines should start with hostname and be followed with commands.
comment|// Delimiter is any contiguous sequence of space or tab character.
comment|// Commands are of the form:
comment|//<KEY>=<VALUE>
comment|//   where KEY can be 'NL', 'Q' or 'TA' (more can be added later)
comment|//   (TA stands for 'Tag Add')
comment|// Sample lines:
comment|// ...
comment|// host1  NL=foo   Q=b
comment|// host2   Q=c NL=bar
comment|// ...
name|String
index|[]
name|commands
init|=
name|line
operator|.
name|split
argument_list|(
literal|"[ \t\n\f\r]+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|commands
operator|!=
literal|null
operator|&&
name|commands
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|String
name|host
init|=
name|commands
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|host
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
comment|// All lines starting with # is a comment
continue|continue;
block|}
name|Map
argument_list|<
name|ContextProp
argument_list|,
name|String
argument_list|>
name|cMap
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|commands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|cSplit
init|=
name|commands
index|[
name|i
index|]
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|cSplit
operator|==
literal|null
operator|||
name|cSplit
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No commands found for line [{}]"
argument_list|,
name|commands
index|[
name|i
index|]
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|cMap
operator|==
literal|null
condition|)
block|{
name|cMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|cMap
operator|.
name|put
argument_list|(
name|ContextProp
operator|.
name|valueOf
argument_list|(
name|cSplit
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|cSplit
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cMap
operator|!=
literal|null
operator|&&
name|cMap
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|tempHostCommands
operator|.
name|put
argument_list|(
name|host
argument_list|,
name|cMap
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Following commands registered for host[{}] : {}"
argument_list|,
name|host
argument_list|,
name|cMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|lastModified
operator|=
name|hostFile
operator|.
name|lastModified
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// Do not commit the new map if we have an Exception..
name|tempHostCommands
operator|=
literal|null
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|tempHostCommands
operator|!=
literal|null
operator|&&
name|tempHostCommands
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|hostCommands
operator|=
name|tempHostCommands
expr_stmt|;
block|}
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|reader
argument_list|,
name|fileInputStream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

