begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
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
name|timelineservice
operator|.
name|storage
package|;
end_package

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
name|IOException
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
name|classification
operator|.
name|InterfaceStability
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|service
operator|.
name|AbstractService
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
name|timelineservice
operator|.
name|TimelineDomain
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
name|timelineservice
operator|.
name|TimelineEntities
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
name|timelineservice
operator|.
name|TimelineEntity
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
name|timelineservice
operator|.
name|TimelineWriteResponse
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
name|timelineservice
operator|.
name|TimelineWriteResponse
operator|.
name|TimelineWriteError
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
name|client
operator|.
name|api
operator|.
name|impl
operator|.
name|FileSystemTimelineWriter
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
name|timelineservice
operator|.
name|collector
operator|.
name|TimelineCollectorContext
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
name|timeline
operator|.
name|TimelineUtils
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
name|annotations
operator|.
name|VisibleForTesting
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
comment|/**  * This implements a FileSystem based backend for storing application timeline  * information. This implementation may not provide a complete implementation of  * all the necessary features. This implementation is provided solely for basic  * testing purposes, and should not be used in a non-test situation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FileSystemTimelineWriterImpl
specifier|public
class|class
name|FileSystemTimelineWriterImpl
extends|extends
name|AbstractService
implements|implements
name|TimelineWriter
block|{
comment|/** Config param for timeline service storage tmp root for FILE YARN-3264. */
DECL|field|TIMELINE_SERVICE_STORAGE_DIR_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
init|=
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PREFIX
operator|+
literal|"fs-writer.root-dir"
decl_stmt|;
DECL|field|TIMELINE_FS_WRITER_NUM_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|TIMELINE_FS_WRITER_NUM_RETRIES
init|=
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PREFIX
operator|+
literal|"fs-writer.num-retries"
decl_stmt|;
DECL|field|DEFAULT_TIMELINE_FS_WRITER_NUM_RETRIES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_TIMELINE_FS_WRITER_NUM_RETRIES
init|=
literal|0
decl_stmt|;
DECL|field|TIMELINE_FS_WRITER_RETRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|TIMELINE_FS_WRITER_RETRY_INTERVAL_MS
init|=
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PREFIX
operator|+
literal|"fs-writer.retry-interval-ms"
decl_stmt|;
DECL|field|DEFAULT_TIMELINE_FS_WRITER_RETRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_TIMELINE_FS_WRITER_RETRY_INTERVAL_MS
init|=
literal|1000L
decl_stmt|;
DECL|field|ENTITIES_DIR
specifier|public
specifier|static
specifier|final
name|String
name|ENTITIES_DIR
init|=
literal|"entities"
decl_stmt|;
comment|/** Default extension for output files. */
DECL|field|TIMELINE_SERVICE_STORAGE_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TIMELINE_SERVICE_STORAGE_EXTENSION
init|=
literal|".thist"
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|rootPath
specifier|private
name|Path
name|rootPath
decl_stmt|;
DECL|field|fsNumRetries
specifier|private
name|int
name|fsNumRetries
decl_stmt|;
DECL|field|fsRetryInterval
specifier|private
name|long
name|fsRetryInterval
decl_stmt|;
DECL|field|entitiesPath
specifier|private
name|Path
name|entitiesPath
decl_stmt|;
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
comment|/** default value for storage location on local disk. */
DECL|field|STORAGE_DIR_ROOT
specifier|private
specifier|static
specifier|final
name|String
name|STORAGE_DIR_ROOT
init|=
literal|"timeline_service_data"
decl_stmt|;
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
name|FileSystemTimelineWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FileSystemTimelineWriterImpl ()
name|FileSystemTimelineWriterImpl
parameter_list|()
block|{
name|super
argument_list|(
operator|(
name|FileSystemTimelineWriterImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (TimelineCollectorContext context, TimelineEntities entities, UserGroupInformation callerUgi)
specifier|public
name|TimelineWriteResponse
name|write
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineEntities
name|entities
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
throws|throws
name|IOException
block|{
name|TimelineWriteResponse
name|response
init|=
operator|new
name|TimelineWriteResponse
argument_list|()
decl_stmt|;
name|String
name|clusterId
init|=
name|context
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|String
name|userId
init|=
name|context
operator|.
name|getUserId
argument_list|()
decl_stmt|;
name|String
name|flowName
init|=
name|context
operator|.
name|getFlowName
argument_list|()
decl_stmt|;
name|String
name|flowVersion
init|=
name|context
operator|.
name|getFlowVersion
argument_list|()
decl_stmt|;
name|long
name|flowRunId
init|=
name|context
operator|.
name|getFlowRunId
argument_list|()
decl_stmt|;
name|String
name|appId
init|=
name|context
operator|.
name|getAppId
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entities
operator|.
name|getEntities
argument_list|()
control|)
block|{
name|writeInternal
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowName
argument_list|,
name|flowVersion
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entity
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|write (TimelineCollectorContext context, TimelineDomain domain)
specifier|public
name|TimelineWriteResponse
name|write
parameter_list|(
name|TimelineCollectorContext
name|context
parameter_list|,
name|TimelineDomain
name|domain
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO implementation for storing domain into FileSystem
return|return
literal|null
return|;
block|}
DECL|method|writeInternal (String clusterId, String userId, String flowName, String flowVersion, long flowRun, String appId, TimelineEntity entity, TimelineWriteResponse response)
specifier|private
specifier|synchronized
name|void
name|writeInternal
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|String
name|flowVersion
parameter_list|,
name|long
name|flowRun
parameter_list|,
name|String
name|appId
parameter_list|,
name|TimelineEntity
name|entity
parameter_list|,
name|TimelineWriteResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|entityTypePathStr
init|=
name|clusterId
operator|+
name|File
operator|.
name|separator
operator|+
name|userId
operator|+
name|File
operator|.
name|separator
operator|+
name|escape
argument_list|(
name|flowName
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|escape
argument_list|(
name|flowVersion
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|flowRun
operator|+
name|File
operator|.
name|separator
operator|+
name|appId
operator|+
name|File
operator|.
name|separator
operator|+
name|entity
operator|.
name|getType
argument_list|()
decl_stmt|;
name|Path
name|entityTypePath
init|=
operator|new
name|Path
argument_list|(
name|entitiesPath
argument_list|,
name|entityTypePathStr
argument_list|)
decl_stmt|;
try|try
block|{
name|mkdirs
argument_list|(
name|entityTypePath
argument_list|)
expr_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|entityTypePath
argument_list|,
name|entity
operator|.
name|getId
argument_list|()
operator|+
name|TIMELINE_SERVICE_STORAGE_EXTENSION
argument_list|)
decl_stmt|;
name|createFileWithRetries
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|byte
index|[]
name|record
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|writeFileWithRetries
argument_list|(
name|filePath
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted operation:"
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineWriteError
name|error
init|=
name|createTimelineWriteError
argument_list|(
name|entity
argument_list|)
decl_stmt|;
comment|/*        * TODO: set an appropriate error code after PoC could possibly be:        * error.setErrorCode(TimelineWriteError.IO_EXCEPTION);        */
name|response
operator|.
name|addError
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createTimelineWriteError (TimelineEntity entity)
specifier|private
name|TimelineWriteError
name|createTimelineWriteError
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|)
block|{
name|TimelineWriteError
name|error
init|=
operator|new
name|TimelineWriteError
argument_list|()
decl_stmt|;
name|error
operator|.
name|setEntityId
argument_list|(
name|entity
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|error
operator|.
name|setEntityType
argument_list|(
name|entity
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|error
return|;
block|}
annotation|@
name|Override
DECL|method|aggregate (TimelineEntity data, TimelineAggregationTrack track)
specifier|public
name|TimelineWriteResponse
name|aggregate
parameter_list|(
name|TimelineEntity
name|data
parameter_list|,
name|TimelineAggregationTrack
name|track
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getOutputRoot ()
name|String
name|getOutputRoot
parameter_list|()
block|{
return|return
name|rootPath
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|outputRoot
init|=
name|conf
operator|.
name|get
argument_list|(
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|STORAGE_DIR_ROOT
argument_list|)
decl_stmt|;
name|rootPath
operator|=
operator|new
name|Path
argument_list|(
name|outputRoot
argument_list|)
expr_stmt|;
name|entitiesPath
operator|=
operator|new
name|Path
argument_list|(
name|rootPath
argument_list|,
name|ENTITIES_DIR
argument_list|)
expr_stmt|;
name|fsNumRetries
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|TIMELINE_FS_WRITER_NUM_RETRIES
argument_list|,
name|DEFAULT_TIMELINE_FS_WRITER_NUM_RETRIES
argument_list|)
expr_stmt|;
name|fsRetryInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|TIMELINE_FS_WRITER_RETRY_INTERVAL_MS
argument_list|,
name|DEFAULT_TIMELINE_FS_WRITER_RETRY_INTERVAL_MS
argument_list|)
expr_stmt|;
name|config
operator|=
name|conf
expr_stmt|;
name|fs
operator|=
name|rootPath
operator|.
name|getFileSystem
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|mkdirsWithRetries
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|mkdirsWithRetries
argument_list|(
name|entitiesPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
comment|// no op
block|}
DECL|method|mkdirs (Path... paths)
specifier|private
name|void
name|mkdirs
parameter_list|(
name|Path
modifier|...
name|paths
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
operator|!
name|existsWithRetries
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|mkdirsWithRetries
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Code from FSRMStateStore.
DECL|method|mkdirsWithRetries (final Path dirPath)
specifier|private
name|void
name|mkdirsWithRetries
parameter_list|(
specifier|final
name|Path
name|dirPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
operator|new
name|FSAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|runWithRetries
argument_list|()
expr_stmt|;
block|}
DECL|method|writeFileWithRetries (final Path outputPath, final byte[] data)
specifier|private
name|void
name|writeFileWithRetries
parameter_list|(
specifier|final
name|Path
name|outputPath
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|FSAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|writeFile
argument_list|(
name|outputPath
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|runWithRetries
argument_list|()
expr_stmt|;
block|}
DECL|method|createFileWithRetries (final Path newFile)
specifier|private
name|boolean
name|createFileWithRetries
parameter_list|(
specifier|final
name|Path
name|newFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|FSAction
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|createFile
argument_list|(
name|newFile
argument_list|)
return|;
block|}
block|}
operator|.
name|runWithRetries
argument_list|()
return|;
block|}
DECL|method|existsWithRetries (final Path path)
specifier|private
name|boolean
name|existsWithRetries
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|FSAction
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
operator|.
name|runWithRetries
argument_list|()
return|;
block|}
DECL|class|FSAction
specifier|private
specifier|abstract
class|class
name|FSAction
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|run ()
specifier|abstract
name|T
name|run
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|runWithRetries ()
name|T
name|runWithRetries
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|int
name|retry
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
return|return
name|run
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception while executing a FS operation."
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|retry
operator|>
name|fsNumRetries
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Maxed out FS retries. Giving up!"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Will retry operation on FS. Retry no. "
operator|+
name|retry
operator|+
literal|" after sleeping for "
operator|+
name|fsRetryInterval
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|fsRetryInterval
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|createFile (Path newFile)
specifier|private
name|boolean
name|createFile
parameter_list|(
name|Path
name|newFile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|createNewFile
argument_list|(
name|newFile
argument_list|)
return|;
block|}
comment|/**    * In order to make this writeInternal atomic as a part of writeInternal    * we will first writeInternal data to .tmp file and then rename it.    * Here we are assuming that rename is atomic for underlying file system.    */
DECL|method|writeFile (Path outputPath, byte[] data)
specifier|protected
name|void
name|writeFile
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|tempPath
init|=
operator|new
name|Path
argument_list|(
name|outputPath
operator|.
name|getParent
argument_list|()
argument_list|,
name|outputPath
operator|.
name|getName
argument_list|()
operator|+
literal|".tmp"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fsOut
init|=
literal|null
decl_stmt|;
comment|// This file will be overwritten when app/attempt finishes for saving the
comment|// final status.
try|try
block|{
name|fsOut
operator|=
name|fs
operator|.
name|create
argument_list|(
name|tempPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|fsIn
init|=
name|fs
operator|.
name|open
argument_list|(
name|outputPath
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|fsIn
argument_list|,
name|fsOut
argument_list|,
name|config
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fsIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|outputPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fsOut
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|fsOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|tempPath
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Got an exception while writing file"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
comment|// specifically escape the separator character
DECL|method|escape (String str)
specifier|private
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'_'
argument_list|)
return|;
block|}
block|}
end_class

end_unit

