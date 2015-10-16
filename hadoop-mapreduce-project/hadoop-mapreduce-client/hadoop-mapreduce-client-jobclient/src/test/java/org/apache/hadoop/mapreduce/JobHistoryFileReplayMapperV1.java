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
name|Set
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
name|io
operator|.
name|IntWritable
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
name|Writable
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TimelineServicePerformance
operator|.
name|PerfCounters
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
name|JobHistoryFileReplayHelper
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
name|JobHistoryFileReplayHelper
operator|.
name|JobFiles
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
name|MRJobConfig
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
name|TypeConverter
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|JobInfo
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|timeline
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
name|timeline
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
name|client
operator|.
name|api
operator|.
name|TimelineClient
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
name|TimelineClientImpl
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

begin_comment
comment|/**  * Mapper for TimelineServicePerformanceV1 that replays job history files to the  * timeline service.  *  */
end_comment

begin_class
DECL|class|JobHistoryFileReplayMapperV1
class|class
name|JobHistoryFileReplayMapperV1
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Mapper
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|Writable
argument_list|,
name|Writable
argument_list|>
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
name|JobHistoryFileReplayMapperV1
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|map (IntWritable key, IntWritable val, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|IntWritable
name|val
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// collect the apps it needs to process
name|TimelineClient
name|tlc
init|=
operator|new
name|TimelineClientImpl
argument_list|()
decl_stmt|;
name|TimelineEntityConverterV1
name|converter
init|=
operator|new
name|TimelineEntityConverterV1
argument_list|()
decl_stmt|;
name|JobHistoryFileReplayHelper
name|helper
init|=
operator|new
name|JobHistoryFileReplayHelper
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|int
name|replayMode
init|=
name|helper
operator|.
name|getReplayMode
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|JobFiles
argument_list|>
name|jobs
init|=
name|helper
operator|.
name|getJobFiles
argument_list|()
decl_stmt|;
name|JobHistoryFileParser
name|parser
init|=
name|helper
operator|.
name|getParser
argument_list|()
decl_stmt|;
if|if
condition|(
name|jobs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
operator|+
literal|" will process no jobs"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
operator|+
literal|" will process "
operator|+
name|jobs
operator|.
name|size
argument_list|()
operator|+
literal|" jobs"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|JobFiles
name|job
range|:
name|jobs
control|)
block|{
comment|// process each job
name|String
name|jobIdStr
init|=
name|job
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"processing "
operator|+
name|jobIdStr
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|JobID
operator|.
name|forName
argument_list|(
name|jobIdStr
argument_list|)
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|jobId
operator|.
name|getAppId
argument_list|()
decl_stmt|;
try|try
block|{
comment|// parse the job info and configuration
name|Path
name|historyFilePath
init|=
name|job
operator|.
name|getJobHistoryFilePath
argument_list|()
decl_stmt|;
name|Path
name|confFilePath
init|=
name|job
operator|.
name|getJobConfFilePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|historyFilePath
operator|==
literal|null
operator|)
operator|||
operator|(
name|confFilePath
operator|==
literal|null
operator|)
condition|)
block|{
continue|continue;
block|}
name|JobInfo
name|jobInfo
init|=
name|parser
operator|.
name|parseHistoryFile
argument_list|(
name|historyFilePath
argument_list|)
decl_stmt|;
name|Configuration
name|jobConf
init|=
name|parser
operator|.
name|parseConfiguration
argument_list|(
name|confFilePath
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"parsed the job history file and the configuration file for job "
operator|+
name|jobIdStr
argument_list|)
expr_stmt|;
comment|// create entities from job history and write them
name|long
name|totalTime
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|entitySet
init|=
name|converter
operator|.
name|createTimelineEntities
argument_list|(
name|jobInfo
argument_list|,
name|jobConf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"converted them into timeline entities for job "
operator|+
name|jobIdStr
argument_list|)
expr_stmt|;
comment|// use the current user for this purpose
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|long
name|startWrite
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|replayMode
condition|)
block|{
case|case
name|JobHistoryFileReplayHelper
operator|.
name|WRITE_ALL_AT_ONCE
case|:
name|writeAllEntities
argument_list|(
name|tlc
argument_list|,
name|entitySet
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
break|break;
case|case
name|JobHistoryFileReplayHelper
operator|.
name|WRITE_PER_ENTITY
case|:
name|writePerEntity
argument_list|(
name|tlc
argument_list|,
name|entitySet
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|context
operator|.
name|getCounter
argument_list|(
name|PerfCounters
operator|.
name|TIMELINE_SERVICE_WRITE_FAILURES
argument_list|)
operator|.
name|increment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"writing to the timeline service failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|long
name|endWrite
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|totalTime
operator|+=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|endWrite
operator|-
name|startWrite
argument_list|)
expr_stmt|;
name|int
name|numEntities
init|=
name|entitySet
operator|.
name|size
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"wrote "
operator|+
name|numEntities
operator|+
literal|" entities in "
operator|+
name|totalTime
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|context
operator|.
name|getCounter
argument_list|(
name|PerfCounters
operator|.
name|TIMELINE_SERVICE_WRITE_TIME
argument_list|)
operator|.
name|increment
argument_list|(
name|totalTime
argument_list|)
expr_stmt|;
name|context
operator|.
name|getCounter
argument_list|(
name|PerfCounters
operator|.
name|TIMELINE_SERVICE_WRITE_COUNTER
argument_list|)
operator|.
name|increment
argument_list|(
name|numEntities
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|progress
argument_list|()
expr_stmt|;
comment|// move it along
block|}
block|}
block|}
DECL|method|writeAllEntities (TimelineClient tlc, Set<TimelineEntity> entitySet, UserGroupInformation ugi)
specifier|private
name|void
name|writeAllEntities
parameter_list|(
name|TimelineClient
name|tlc
parameter_list|,
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|entitySet
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|tlc
operator|.
name|putEntities
argument_list|(
operator|(
name|TimelineEntity
index|[]
operator|)
name|entitySet
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writePerEntity (TimelineClient tlc, Set<TimelineEntity> entitySet, UserGroupInformation ugi)
specifier|private
name|void
name|writePerEntity
parameter_list|(
name|TimelineClient
name|tlc
parameter_list|,
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|entitySet
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entitySet
control|)
block|{
name|tlc
operator|.
name|putEntities
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"wrote entity "
operator|+
name|entity
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

