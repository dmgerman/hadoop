begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation.filecontroller
package|package
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|FileWriter
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|FileSystem
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
name|AggregatedLogFormat
operator|.
name|LogKey
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
name|AggregatedLogFormat
operator|.
name|LogValue
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
name|ContainerLogMeta
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
name|ContainerLogsRequest
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
name|LogAggregationFileControllerContext
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
name|logaggregation
operator|.
name|filecontroller
operator|.
name|tfile
operator|.
name|LogAggregationTFileController
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
name|View
operator|.
name|ViewContext
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
operator|.
name|Block
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test LogAggregationFileControllerFactory.  *  */
end_comment

begin_class
DECL|class|TestLogAggregationFileControllerFactory
specifier|public
class|class
name|TestLogAggregationFileControllerFactory
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testLogAggregationFileControllerFactory ()
specifier|public
name|void
name|testLogAggregationFileControllerFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|appOwner
init|=
literal|"test"
decl_stmt|;
name|String
name|remoteLogRootDir
init|=
literal|"target/app-logs/"
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|remoteLogRootDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR_SUFFIX
argument_list|,
literal|"log"
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LogAggregationFileControllerFactory
name|factory
init|=
operator|new
name|LogAggregationFileControllerFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LinkedList
argument_list|<
name|LogAggregationFileController
argument_list|>
name|list
init|=
name|factory
operator|.
name|getConfiguredLogAggregationFileControllerList
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|getFirst
argument_list|()
operator|instanceof
name|LogAggregationTFileController
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getFileControllerForWrite
argument_list|()
operator|instanceof
name|LogAggregationTFileController
argument_list|)
expr_stmt|;
name|Path
name|logPath
init|=
name|list
operator|.
name|getFirst
argument_list|()
operator|.
name|getRemoteAppLogDir
argument_list|(
name|appId
argument_list|,
name|appOwner
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|logPath
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|logPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|logPath
argument_list|)
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
operator|new
name|File
argument_list|(
name|logPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|"testLog"
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getFileControllerForRead
argument_list|(
name|appId
argument_list|,
name|appOwner
argument_list|)
operator|instanceof
name|LogAggregationTFileController
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|logPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_FILE_FORMATS
argument_list|,
literal|"TestLogAggregationFileController"
argument_list|)
expr_stmt|;
comment|// Did not set class for TestLogAggregationFileController,
comment|// should get the exception.
try|try
block|{
name|factory
operator|=
operator|new
name|LogAggregationFileControllerFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// should get exception
block|}
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_FILE_FORMATS
argument_list|,
literal|"TestLogAggregationFileController,TFile"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"yarn.log-aggregation.file-controller.TestLogAggregationFileController"
operator|+
literal|".class"
argument_list|,
name|TestLogAggregationFileController
operator|.
name|class
argument_list|,
name|LogAggregationFileController
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.log-aggregation.TestLogAggregationFileController"
operator|+
literal|".remote-app-log-dir"
argument_list|,
name|remoteLogRootDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.log-aggregation.TestLogAggregationFileController"
operator|+
literal|".remote-app-log-dir-suffix"
argument_list|,
literal|"testLog"
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|LogAggregationFileControllerFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|list
operator|=
name|factory
operator|.
name|getConfiguredLogAggregationFileControllerList
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|getFirst
argument_list|()
operator|instanceof
name|TestLogAggregationFileController
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|getLast
argument_list|()
operator|instanceof
name|LogAggregationTFileController
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getFileControllerForWrite
argument_list|()
operator|instanceof
name|TestLogAggregationFileController
argument_list|)
expr_stmt|;
name|logPath
operator|=
name|list
operator|.
name|getFirst
argument_list|()
operator|.
name|getRemoteAppLogDir
argument_list|(
name|appId
argument_list|,
name|appOwner
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|logPath
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|logPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|logPath
argument_list|)
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
operator|new
name|File
argument_list|(
name|logPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|"testLog"
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getFileControllerForRead
argument_list|(
name|appId
argument_list|,
name|appOwner
argument_list|)
operator|instanceof
name|TestLogAggregationFileController
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fs
operator|.
name|delete
argument_list|(
name|logPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestLogAggregationFileController
specifier|private
specifier|static
class|class
name|TestLogAggregationFileController
extends|extends
name|LogAggregationFileController
block|{
annotation|@
name|Override
DECL|method|initInternal (Configuration conf)
specifier|public
name|void
name|initInternal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|remoteDirStr
init|=
name|String
operator|.
name|format
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_REMOTE_APP_LOG_DIR_FMT
argument_list|,
name|this
operator|.
name|fileControllerName
argument_list|)
decl_stmt|;
name|this
operator|.
name|remoteRootLogDir
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|remoteDirStr
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|suffix
init|=
name|String
operator|.
name|format
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_REMOTE_APP_LOG_DIR_SUFFIX_FMT
argument_list|,
name|this
operator|.
name|fileControllerName
argument_list|)
decl_stmt|;
name|this
operator|.
name|remoteRootLogDirSuffix
operator|=
name|conf
operator|.
name|get
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|closeWriter ()
specifier|public
name|void
name|closeWriter
parameter_list|()
block|{
comment|// Do Nothing
block|}
annotation|@
name|Override
DECL|method|write (LogKey logKey, LogValue logValue)
specifier|public
name|void
name|write
parameter_list|(
name|LogKey
name|logKey
parameter_list|,
name|LogValue
name|logValue
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do Nothing
block|}
annotation|@
name|Override
DECL|method|postWrite (LogAggregationFileControllerContext record)
specifier|public
name|void
name|postWrite
parameter_list|(
name|LogAggregationFileControllerContext
name|record
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do Nothing
block|}
annotation|@
name|Override
DECL|method|initializeWriter (LogAggregationFileControllerContext context)
specifier|public
name|void
name|initializeWriter
parameter_list|(
name|LogAggregationFileControllerContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do Nothing
block|}
annotation|@
name|Override
DECL|method|readAggregatedLogs (ContainerLogsRequest logRequest, OutputStream os)
specifier|public
name|boolean
name|readAggregatedLogs
parameter_list|(
name|ContainerLogsRequest
name|logRequest
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|readAggregatedLogsMeta ( ContainerLogsRequest logRequest)
specifier|public
name|List
argument_list|<
name|ContainerLogMeta
argument_list|>
name|readAggregatedLogsMeta
parameter_list|(
name|ContainerLogsRequest
name|logRequest
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|renderAggregatedLogsBlock (Block html, ViewContext context)
specifier|public
name|void
name|renderAggregatedLogsBlock
parameter_list|(
name|Block
name|html
parameter_list|,
name|ViewContext
name|context
parameter_list|)
block|{
comment|// DO NOTHING
block|}
annotation|@
name|Override
DECL|method|getApplicationOwner (Path aggregatedLogPath, ApplicationId appId)
specifier|public
name|String
name|getApplicationOwner
parameter_list|(
name|Path
name|aggregatedLogPath
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAcls ( Path aggregatedLogPath, ApplicationId appId)
specifier|public
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|getApplicationAcls
parameter_list|(
name|Path
name|aggregatedLogPath
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

