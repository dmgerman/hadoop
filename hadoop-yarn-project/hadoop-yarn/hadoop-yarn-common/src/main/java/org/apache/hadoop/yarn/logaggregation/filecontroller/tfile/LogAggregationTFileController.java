begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation.filecontroller.tfile
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
operator|.
name|tfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|ArrayList
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|math3
operator|.
name|util
operator|.
name|Pair
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
operator|.
name|Private
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
name|InterfaceStability
operator|.
name|Unstable
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
name|FileStatus
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
name|HarFs
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
name|fs
operator|.
name|RemoteIterator
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
name|LogReader
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
name|AggregatedLogFormat
operator|.
name|LogWriter
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
name|ContainerLogAggregationType
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
name|LogAggregationUtils
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
name|LogToolUtils
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

begin_comment
comment|/**  * The TFile log aggregation file Controller implementation.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|LogAggregationTFileController
specifier|public
class|class
name|LogAggregationTFileController
extends|extends
name|LogAggregationFileController
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LogAggregationTFileController
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|writer
specifier|private
name|LogWriter
name|writer
decl_stmt|;
DECL|field|tfReader
specifier|private
name|TFileLogReader
name|tfReader
init|=
literal|null
decl_stmt|;
DECL|method|LogAggregationTFileController ()
specifier|public
name|LogAggregationTFileController
parameter_list|()
block|{}
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
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|remoteRootLogDirSuffix
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR_SUFFIX
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR_SUFFIX
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|writer
operator|=
operator|new
name|LogWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|initialize
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|context
operator|.
name|getRemoteNodeTmpLogFileForApp
argument_list|()
argument_list|,
name|context
operator|.
name|getUserUgi
argument_list|()
argument_list|)
expr_stmt|;
comment|// Write ACLs once when the writer is created.
name|writer
operator|.
name|writeApplicationACLs
argument_list|(
name|context
operator|.
name|getAppAcls
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeApplicationOwner
argument_list|(
name|context
operator|.
name|getUserUgi
argument_list|()
operator|.
name|getShortUserName
argument_list|()
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
name|this
operator|.
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|writer
operator|=
literal|null
expr_stmt|;
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
name|this
operator|.
name|writer
operator|.
name|append
argument_list|(
name|logKey
argument_list|,
name|logValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postWrite (final LogAggregationFileControllerContext record)
specifier|public
name|void
name|postWrite
parameter_list|(
specifier|final
name|LogAggregationFileControllerContext
name|record
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Before upload logs, make sure the number of existing logs
comment|// is smaller than the configured NM log aggregation retention size.
if|if
condition|(
name|record
operator|.
name|isUploadedLogsInThisCycle
argument_list|()
operator|&&
name|record
operator|.
name|isLogAggregationInRolling
argument_list|()
condition|)
block|{
name|cleanOldLogs
argument_list|(
name|record
operator|.
name|getRemoteNodeLogFileForApp
argument_list|()
argument_list|,
name|record
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|record
operator|.
name|getUserUgi
argument_list|()
argument_list|)
expr_stmt|;
name|record
operator|.
name|increcleanupOldLogTimes
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Path
name|renamedPath
init|=
name|record
operator|.
name|getRollingMonitorInterval
argument_list|()
operator|<=
literal|0
condition|?
name|record
operator|.
name|getRemoteNodeLogFileForApp
argument_list|()
else|:
operator|new
name|Path
argument_list|(
name|record
operator|.
name|getRemoteNodeLogFileForApp
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|,
name|record
operator|.
name|getRemoteNodeLogFileForApp
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"_"
operator|+
name|record
operator|.
name|getLogUploadTimeStamp
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|rename
init|=
name|record
operator|.
name|isUploadedLogsInThisCycle
argument_list|()
decl_stmt|;
try|try
block|{
name|record
operator|.
name|getUserUgi
argument_list|()
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|remoteFS
init|=
name|record
operator|.
name|getRemoteNodeLogFileForApp
argument_list|()
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|rename
condition|)
block|{
name|remoteFS
operator|.
name|rename
argument_list|(
name|record
operator|.
name|getRemoteNodeTmpLogFileForApp
argument_list|()
argument_list|,
name|renamedPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remoteFS
operator|.
name|delete
argument_list|(
name|record
operator|.
name|getRemoteNodeTmpLogFileForApp
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to move temporary log file to final location: ["
operator|+
name|record
operator|.
name|getRemoteNodeTmpLogFileForApp
argument_list|()
operator|+
literal|"] to ["
operator|+
name|renamedPath
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Log uploaded failed for Application: "
operator|+
name|record
operator|.
name|getAppId
argument_list|()
operator|+
literal|" in NodeManager: "
operator|+
name|LogAggregationUtils
operator|.
name|getNodeString
argument_list|(
name|record
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|+
literal|" at "
operator|+
name|Times
operator|.
name|format
argument_list|(
name|record
operator|.
name|getLogUploadTimeStamp
argument_list|()
argument_list|)
operator|+
literal|"\n"
argument_list|)
throw|;
block|}
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
name|boolean
name|findLogs
init|=
literal|false
decl_stmt|;
name|boolean
name|createPrintStream
init|=
operator|(
name|os
operator|==
literal|null
operator|)
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|logRequest
operator|.
name|getAppId
argument_list|()
decl_stmt|;
name|String
name|nodeId
init|=
name|logRequest
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|logTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|logRequest
operator|.
name|getLogTypes
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|logRequest
operator|.
name|getLogTypes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logTypes
operator|.
name|addAll
argument_list|(
name|logRequest
operator|.
name|getLogTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|containerIdStr
init|=
name|logRequest
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|boolean
name|getAllContainers
init|=
operator|(
name|containerIdStr
operator|==
literal|null
operator|||
name|containerIdStr
operator|.
name|isEmpty
argument_list|()
operator|)
decl_stmt|;
name|long
name|size
init|=
name|logRequest
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|nodeFiles
init|=
name|LogAggregationUtils
operator|.
name|getRemoteNodeFileDir
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|logRequest
operator|.
name|getAppOwner
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|65535
index|]
decl_stmt|;
while|while
condition|(
name|nodeFiles
operator|!=
literal|null
operator|&&
name|nodeFiles
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|FileStatus
name|thisNodeFile
init|=
name|nodeFiles
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|nodeName
init|=
name|thisNodeFile
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|equals
argument_list|(
name|appId
operator|+
literal|".har"
argument_list|)
condition|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"har:///"
operator|+
name|thisNodeFile
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getRawPath
argument_list|()
argument_list|)
decl_stmt|;
name|nodeFiles
operator|=
name|HarFs
operator|.
name|get
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
operator|.
name|listStatusIterator
argument_list|(
name|p
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|(
name|nodeId
operator|==
literal|null
operator|||
name|nodeName
operator|.
name|contains
argument_list|(
name|LogAggregationUtils
operator|.
name|getNodeString
argument_list|(
name|nodeId
argument_list|)
argument_list|)
operator|)
operator|&&
operator|!
name|nodeName
operator|.
name|endsWith
argument_list|(
name|LogAggregationUtils
operator|.
name|TMP_FILE_SUFFIX
argument_list|)
condition|)
block|{
name|AggregatedLogFormat
operator|.
name|LogReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|AggregatedLogFormat
operator|.
name|LogReader
argument_list|(
name|conf
argument_list|,
name|thisNodeFile
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|DataInputStream
name|valueStream
decl_stmt|;
name|LogKey
name|key
init|=
operator|new
name|LogKey
argument_list|()
decl_stmt|;
name|valueStream
operator|=
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
while|while
condition|(
name|valueStream
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|getAllContainers
operator|||
operator|(
name|key
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|containerIdStr
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
name|createPrintStream
condition|)
block|{
name|os
operator|=
name|LogToolUtils
operator|.
name|createPrintStream
argument_list|(
name|logRequest
operator|.
name|getOutputLocalDir
argument_list|()
argument_list|,
name|thisNodeFile
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|String
name|fileType
init|=
name|valueStream
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|String
name|fileLengthStr
init|=
name|valueStream
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|long
name|fileLength
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|fileLengthStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|logTypes
operator|==
literal|null
operator|||
name|logTypes
operator|.
name|isEmpty
argument_list|()
operator|||
name|logTypes
operator|.
name|contains
argument_list|(
name|fileType
argument_list|)
condition|)
block|{
name|LogToolUtils
operator|.
name|outputContainerLog
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|nodeName
argument_list|,
name|fileType
argument_list|,
name|fileLength
argument_list|,
name|size
argument_list|,
name|Times
operator|.
name|format
argument_list|(
name|thisNodeFile
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|,
name|valueStream
argument_list|,
name|os
argument_list|,
name|buf
argument_list|,
name|ContainerLogAggregationType
operator|.
name|AGGREGATED
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b
init|=
name|aggregatedLogSuffix
argument_list|(
name|fileType
argument_list|)
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|findLogs
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|long
name|totalSkipped
init|=
literal|0
decl_stmt|;
name|long
name|currSkipped
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|currSkipped
operator|!=
operator|-
literal|1
operator|&&
name|totalSkipped
operator|<
name|fileLength
condition|)
block|{
name|currSkipped
operator|=
name|valueStream
operator|.
name|skip
argument_list|(
name|fileLength
operator|-
name|totalSkipped
argument_list|)
expr_stmt|;
name|totalSkipped
operator|+=
name|currSkipped
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|eof
parameter_list|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|createPrintStream
condition|)
block|{
name|closePrintStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|getAllContainers
condition|)
block|{
break|break;
block|}
block|}
comment|// Next container
name|key
operator|=
operator|new
name|LogKey
argument_list|()
expr_stmt|;
name|valueStream
operator|=
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|findLogs
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
name|List
argument_list|<
name|ContainerLogMeta
argument_list|>
name|containersLogMeta
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|containerIdStr
init|=
name|logRequest
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|String
name|nodeId
init|=
name|logRequest
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|logRequest
operator|.
name|getAppId
argument_list|()
decl_stmt|;
name|String
name|appOwner
init|=
name|logRequest
operator|.
name|getAppOwner
argument_list|()
decl_stmt|;
name|boolean
name|getAllContainers
init|=
operator|(
name|containerIdStr
operator|==
literal|null
operator|)
decl_stmt|;
name|String
name|nodeIdStr
init|=
operator|(
name|nodeId
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|LogAggregationUtils
operator|.
name|getNodeString
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|nodeFiles
init|=
name|LogAggregationUtils
operator|.
name|getRemoteNodeFileDir
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|appOwner
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeFiles
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"There is no available log fils for "
operator|+
literal|"application:"
operator|+
name|appId
argument_list|)
throw|;
block|}
while|while
condition|(
name|nodeFiles
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FileStatus
name|thisNodeFile
init|=
name|nodeFiles
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeIdStr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|thisNodeFile
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
name|nodeIdStr
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
if|if
condition|(
operator|!
name|thisNodeFile
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|LogAggregationUtils
operator|.
name|TMP_FILE_SUFFIX
argument_list|)
condition|)
block|{
name|AggregatedLogFormat
operator|.
name|LogReader
name|reader
init|=
operator|new
name|AggregatedLogFormat
operator|.
name|LogReader
argument_list|(
name|conf
argument_list|,
name|thisNodeFile
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|DataInputStream
name|valueStream
decl_stmt|;
name|LogKey
name|key
init|=
operator|new
name|LogKey
argument_list|()
decl_stmt|;
name|valueStream
operator|=
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
while|while
condition|(
name|valueStream
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|getAllContainers
operator|||
operator|(
name|key
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|containerIdStr
argument_list|)
operator|)
condition|)
block|{
name|ContainerLogMeta
name|containerLogMeta
init|=
operator|new
name|ContainerLogMeta
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|thisNodeFile
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|logMeta
init|=
name|LogReader
operator|.
name|readContainerMetaDataAndSkipData
argument_list|(
name|valueStream
argument_list|)
decl_stmt|;
name|containerLogMeta
operator|.
name|addLogMeta
argument_list|(
name|logMeta
operator|.
name|getFirst
argument_list|()
argument_list|,
name|logMeta
operator|.
name|getSecond
argument_list|()
argument_list|,
name|Times
operator|.
name|format
argument_list|(
name|thisNodeFile
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|eof
parameter_list|)
block|{
break|break;
block|}
block|}
name|containersLogMeta
operator|.
name|add
argument_list|(
name|containerLogMeta
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getAllContainers
condition|)
block|{
break|break;
block|}
block|}
comment|// Next container
name|key
operator|=
operator|new
name|LogKey
argument_list|()
expr_stmt|;
name|valueStream
operator|=
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|containersLogMeta
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
name|TFileAggregatedLogsBlock
name|block
init|=
operator|new
name|TFileAggregatedLogsBlock
argument_list|(
name|context
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|block
operator|.
name|render
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationOwner (Path aggregatedLog)
specifier|public
name|String
name|getApplicationOwner
parameter_list|(
name|Path
name|aggregatedLog
parameter_list|)
throws|throws
name|IOException
block|{
name|createTFileLogReader
argument_list|(
name|aggregatedLog
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|tfReader
operator|.
name|getLogReader
argument_list|()
operator|.
name|getApplicationOwner
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAcls ( Path aggregatedLog)
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
name|aggregatedLog
parameter_list|)
throws|throws
name|IOException
block|{
name|createTFileLogReader
argument_list|(
name|aggregatedLog
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|tfReader
operator|.
name|getLogReader
argument_list|()
operator|.
name|getApplicationAcls
argument_list|()
return|;
block|}
DECL|method|createTFileLogReader (Path aggregatedLog)
specifier|private
name|void
name|createTFileLogReader
parameter_list|(
name|Path
name|aggregatedLog
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|tfReader
operator|==
literal|null
operator|||
operator|!
name|this
operator|.
name|tfReader
operator|.
name|getAggregatedLogPath
argument_list|()
operator|.
name|equals
argument_list|(
name|aggregatedLog
argument_list|)
condition|)
block|{
name|LogReader
name|logReader
init|=
operator|new
name|LogReader
argument_list|(
name|conf
argument_list|,
name|aggregatedLog
argument_list|)
decl_stmt|;
name|this
operator|.
name|tfReader
operator|=
operator|new
name|TFileLogReader
argument_list|(
name|logReader
argument_list|,
name|aggregatedLog
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TFileLogReader
specifier|private
specifier|static
class|class
name|TFileLogReader
block|{
DECL|field|logReader
specifier|private
name|LogReader
name|logReader
decl_stmt|;
DECL|field|aggregatedLogPath
specifier|private
name|Path
name|aggregatedLogPath
decl_stmt|;
DECL|method|TFileLogReader (LogReader logReader, Path aggregatedLogPath)
name|TFileLogReader
parameter_list|(
name|LogReader
name|logReader
parameter_list|,
name|Path
name|aggregatedLogPath
parameter_list|)
block|{
name|this
operator|.
name|setLogReader
argument_list|(
name|logReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAggregatedLogPath
argument_list|(
name|aggregatedLogPath
argument_list|)
expr_stmt|;
block|}
DECL|method|getLogReader ()
specifier|public
name|LogReader
name|getLogReader
parameter_list|()
block|{
return|return
name|logReader
return|;
block|}
DECL|method|setLogReader (LogReader logReader)
specifier|public
name|void
name|setLogReader
parameter_list|(
name|LogReader
name|logReader
parameter_list|)
block|{
name|this
operator|.
name|logReader
operator|=
name|logReader
expr_stmt|;
block|}
DECL|method|getAggregatedLogPath ()
specifier|public
name|Path
name|getAggregatedLogPath
parameter_list|()
block|{
return|return
name|aggregatedLogPath
return|;
block|}
DECL|method|setAggregatedLogPath (Path aggregatedLogPath)
specifier|public
name|void
name|setAggregatedLogPath
parameter_list|(
name|Path
name|aggregatedLogPath
parameter_list|)
block|{
name|this
operator|.
name|aggregatedLogPath
operator|=
name|aggregatedLogPath
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

