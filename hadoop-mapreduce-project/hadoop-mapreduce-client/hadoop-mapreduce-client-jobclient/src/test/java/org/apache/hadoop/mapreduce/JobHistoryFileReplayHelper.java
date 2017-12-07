begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

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
name|util
operator|.
name|Collection
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
name|LocatedFileStatus
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
name|mapreduce
operator|.
name|Mapper
operator|.
name|Context
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

begin_class
DECL|class|JobHistoryFileReplayHelper
class|class
name|JobHistoryFileReplayHelper
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
name|JobHistoryFileReplayHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PROCESSING_PATH
specifier|static
specifier|final
name|String
name|PROCESSING_PATH
init|=
literal|"processing path"
decl_stmt|;
DECL|field|REPLAY_MODE
specifier|static
specifier|final
name|String
name|REPLAY_MODE
init|=
literal|"replay mode"
decl_stmt|;
DECL|field|WRITE_ALL_AT_ONCE
specifier|static
specifier|final
name|int
name|WRITE_ALL_AT_ONCE
init|=
literal|1
decl_stmt|;
DECL|field|WRITE_PER_ENTITY
specifier|static
specifier|final
name|int
name|WRITE_PER_ENTITY
init|=
literal|2
decl_stmt|;
DECL|field|REPLAY_MODE_DEFAULT
specifier|static
specifier|final
name|int
name|REPLAY_MODE_DEFAULT
init|=
name|WRITE_ALL_AT_ONCE
decl_stmt|;
DECL|field|JOB_ID_PARSER
specifier|private
specifier|static
name|Pattern
name|JOB_ID_PARSER
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(job_[0-9]+_([0-9]+)).*"
argument_list|)
decl_stmt|;
DECL|enum|FileType
DECL|enumConstant|JOB_HISTORY_FILE
DECL|enumConstant|JOB_CONF_FILE
DECL|enumConstant|UNKNOWN
specifier|private
enum|enum
name|FileType
block|{
name|JOB_HISTORY_FILE
block|,
name|JOB_CONF_FILE
block|,
name|UNKNOWN
block|}
empty_stmt|;
DECL|field|parser
name|JobHistoryFileParser
name|parser
decl_stmt|;
DECL|field|replayMode
name|int
name|replayMode
decl_stmt|;
DECL|field|jobFiles
name|Collection
argument_list|<
name|JobFiles
argument_list|>
name|jobFiles
decl_stmt|;
DECL|method|JobHistoryFileReplayHelper (Context context)
name|JobHistoryFileReplayHelper
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|int
name|taskId
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
name|TimelineServicePerformance
operator|.
name|NUM_MAPS_DEFAULT
argument_list|)
decl_stmt|;
name|replayMode
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|JobHistoryFileReplayHelper
operator|.
name|REPLAY_MODE
argument_list|,
name|JobHistoryFileReplayHelper
operator|.
name|REPLAY_MODE_DEFAULT
argument_list|)
expr_stmt|;
name|String
name|processingDir
init|=
name|conf
operator|.
name|get
argument_list|(
name|JobHistoryFileReplayHelper
operator|.
name|PROCESSING_PATH
argument_list|)
decl_stmt|;
name|Path
name|processingPath
init|=
operator|new
name|Path
argument_list|(
name|processingDir
argument_list|)
decl_stmt|;
name|FileSystem
name|processingFs
init|=
name|processingPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|parser
operator|=
operator|new
name|JobHistoryFileParser
argument_list|(
name|processingFs
argument_list|)
expr_stmt|;
name|jobFiles
operator|=
name|selectJobFiles
argument_list|(
name|processingFs
argument_list|,
name|processingPath
argument_list|,
name|taskId
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|getReplayMode ()
specifier|public
name|int
name|getReplayMode
parameter_list|()
block|{
return|return
name|replayMode
return|;
block|}
DECL|method|getJobFiles ()
specifier|public
name|Collection
argument_list|<
name|JobFiles
argument_list|>
name|getJobFiles
parameter_list|()
block|{
return|return
name|jobFiles
return|;
block|}
DECL|method|getParser ()
specifier|public
name|JobHistoryFileParser
name|getParser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
DECL|class|JobFiles
specifier|public
specifier|static
class|class
name|JobFiles
block|{
DECL|field|jobId
specifier|private
specifier|final
name|String
name|jobId
decl_stmt|;
DECL|field|jobHistoryFilePath
specifier|private
name|Path
name|jobHistoryFilePath
decl_stmt|;
DECL|field|jobConfFilePath
specifier|private
name|Path
name|jobConfFilePath
decl_stmt|;
DECL|method|JobFiles (String jobId)
specifier|public
name|JobFiles
parameter_list|(
name|String
name|jobId
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
block|}
DECL|method|getJobId ()
specifier|public
name|String
name|getJobId
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
DECL|method|getJobHistoryFilePath ()
specifier|public
name|Path
name|getJobHistoryFilePath
parameter_list|()
block|{
return|return
name|jobHistoryFilePath
return|;
block|}
DECL|method|setJobHistoryFilePath (Path jobHistoryFilePath)
specifier|public
name|void
name|setJobHistoryFilePath
parameter_list|(
name|Path
name|jobHistoryFilePath
parameter_list|)
block|{
name|this
operator|.
name|jobHistoryFilePath
operator|=
name|jobHistoryFilePath
expr_stmt|;
block|}
DECL|method|getJobConfFilePath ()
specifier|public
name|Path
name|getJobConfFilePath
parameter_list|()
block|{
return|return
name|jobConfFilePath
return|;
block|}
DECL|method|setJobConfFilePath (Path jobConfFilePath)
specifier|public
name|void
name|setJobConfFilePath
parameter_list|(
name|Path
name|jobConfFilePath
parameter_list|)
block|{
name|this
operator|.
name|jobConfFilePath
operator|=
name|jobConfFilePath
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|jobId
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|JobFiles
name|other
init|=
operator|(
name|JobFiles
operator|)
name|obj
decl_stmt|;
return|return
name|jobId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|jobId
argument_list|)
return|;
block|}
block|}
DECL|method|selectJobFiles (FileSystem fs, Path processingRoot, int i, int size)
specifier|private
name|Collection
argument_list|<
name|JobFiles
argument_list|>
name|selectJobFiles
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|processingRoot
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|JobFiles
argument_list|>
name|jobs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|it
init|=
name|fs
operator|.
name|listFiles
argument_list|(
name|processingRoot
argument_list|,
literal|true
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|LocatedFileStatus
name|status
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|status
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|path
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Matcher
name|m
init|=
name|JOB_ID_PARSER
operator|.
name|matcher
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|String
name|jobId
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|lastId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|mod
init|=
name|lastId
operator|%
name|size
decl_stmt|;
if|if
condition|(
name|mod
operator|!=
name|i
condition|)
block|{
continue|continue;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"this mapper will process file "
operator|+
name|fileName
argument_list|)
expr_stmt|;
comment|// it's mine
name|JobFiles
name|jobFiles
init|=
name|jobs
operator|.
name|get
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobFiles
operator|==
literal|null
condition|)
block|{
name|jobFiles
operator|=
operator|new
name|JobFiles
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|jobs
operator|.
name|put
argument_list|(
name|jobId
argument_list|,
name|jobFiles
argument_list|)
expr_stmt|;
block|}
name|setFilePath
argument_list|(
name|fileName
argument_list|,
name|path
argument_list|,
name|jobFiles
argument_list|)
expr_stmt|;
block|}
return|return
name|jobs
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|setFilePath (String fileName, Path path, JobFiles jobFiles)
specifier|private
name|void
name|setFilePath
parameter_list|(
name|String
name|fileName
parameter_list|,
name|Path
name|path
parameter_list|,
name|JobFiles
name|jobFiles
parameter_list|)
block|{
comment|// determine if we're dealing with a job history file or a job conf file
name|FileType
name|type
init|=
name|getFileType
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|JOB_HISTORY_FILE
case|:
if|if
condition|(
name|jobFiles
operator|.
name|getJobHistoryFilePath
argument_list|()
operator|==
literal|null
condition|)
block|{
name|jobFiles
operator|.
name|setJobHistoryFilePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"we already have the job history file "
operator|+
name|jobFiles
operator|.
name|getJobHistoryFilePath
argument_list|()
operator|+
literal|": skipping "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|JOB_CONF_FILE
case|:
if|if
condition|(
name|jobFiles
operator|.
name|getJobConfFilePath
argument_list|()
operator|==
literal|null
condition|)
block|{
name|jobFiles
operator|.
name|setJobConfFilePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"we already have the job conf file "
operator|+
name|jobFiles
operator|.
name|getJobConfFilePath
argument_list|()
operator|+
literal|": skipping "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|UNKNOWN
case|:
name|LOG
operator|.
name|warn
argument_list|(
literal|"unknown type: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFileType (String fileName)
specifier|private
name|FileType
name|getFileType
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
if|if
condition|(
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".jhist"
argument_list|)
condition|)
block|{
return|return
name|FileType
operator|.
name|JOB_HISTORY_FILE
return|;
block|}
if|if
condition|(
name|fileName
operator|.
name|endsWith
argument_list|(
literal|"_conf.xml"
argument_list|)
condition|)
block|{
return|return
name|FileType
operator|.
name|JOB_CONF_FILE
return|;
block|}
return|return
name|FileType
operator|.
name|UNKNOWN
return|;
block|}
block|}
end_class

end_unit

