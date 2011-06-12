begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.map
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|map
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
name|net
operator|.
name|URI
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
name|conf
operator|.
name|Configuration
operator|.
name|IntegerRanges
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
name|RawComparator
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
name|Counter
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
name|InputFormat
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
name|InputSplit
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
name|JobID
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
name|MapContext
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
name|OutputCommitter
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
name|OutputFormat
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
name|Partitioner
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
name|Reducer
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
name|TaskAttemptID
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
name|Credentials
import|;
end_import

begin_comment
comment|/**  * A {@link Mapper} which wraps a given one to allow custom   * {@link Mapper.Context} implementations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|WrappedMapper
specifier|public
class|class
name|WrappedMapper
parameter_list|<
name|KEYIN
parameter_list|,
name|VALUEIN
parameter_list|,
name|KEYOUT
parameter_list|,
name|VALUEOUT
parameter_list|>
extends|extends
name|Mapper
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
block|{
comment|/**    * Get a wrapped {@link Mapper.Context} for custom implementations.    * @param mapContext<code>MapContext</code> to be wrapped    * @return a wrapped<code>Mapper.Context</code> for custom implementations    */
specifier|public
name|Mapper
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
operator|.
name|Context
DECL|method|getMapContext (MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> mapContext)
name|getMapContext
parameter_list|(
name|MapContext
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|mapContext
parameter_list|)
block|{
return|return
operator|new
name|Context
argument_list|(
name|mapContext
argument_list|)
return|;
block|}
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Context
specifier|public
class|class
name|Context
extends|extends
name|Mapper
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
operator|.
name|Context
block|{
DECL|field|mapContext
specifier|protected
name|MapContext
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|mapContext
decl_stmt|;
DECL|method|Context (MapContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> mapContext)
specifier|public
name|Context
parameter_list|(
name|MapContext
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|mapContext
parameter_list|)
block|{
name|this
operator|.
name|mapContext
operator|=
name|mapContext
expr_stmt|;
block|}
comment|/**      * Get the input split for this map.      */
DECL|method|getInputSplit ()
specifier|public
name|InputSplit
name|getInputSplit
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getInputSplit
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentKey ()
specifier|public
name|KEYIN
name|getCurrentKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|mapContext
operator|.
name|getCurrentKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentValue ()
specifier|public
name|VALUEIN
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|mapContext
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextKeyValue ()
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|mapContext
operator|.
name|nextKeyValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCounter (Enum<?> counterName)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|counterName
parameter_list|)
block|{
return|return
name|mapContext
operator|.
name|getCounter
argument_list|(
name|counterName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCounter (String groupName, String counterName)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|String
name|groupName
parameter_list|,
name|String
name|counterName
parameter_list|)
block|{
return|return
name|mapContext
operator|.
name|getCounter
argument_list|(
name|groupName
argument_list|,
name|counterName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputCommitter ()
specifier|public
name|OutputCommitter
name|getOutputCommitter
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getOutputCommitter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|write (KEYOUT key, VALUEOUT value)
specifier|public
name|void
name|write
parameter_list|(
name|KEYOUT
name|key
parameter_list|,
name|VALUEOUT
name|value
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|mapContext
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStatus ()
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptID ()
specifier|public
name|TaskAttemptID
name|getTaskAttemptID
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getTaskAttemptID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setStatus (String msg)
specifier|public
name|void
name|setStatus
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|mapContext
operator|.
name|setStatus
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getArchiveClassPaths ()
specifier|public
name|Path
index|[]
name|getArchiveClassPaths
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getArchiveClassPaths
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getArchiveTimestamps ()
specifier|public
name|String
index|[]
name|getArchiveTimestamps
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getArchiveTimestamps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheArchives ()
specifier|public
name|URI
index|[]
name|getCacheArchives
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mapContext
operator|.
name|getCacheArchives
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCacheFiles ()
specifier|public
name|URI
index|[]
name|getCacheFiles
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mapContext
operator|.
name|getCacheArchives
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinerClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getCombinerClass
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
return|return
name|mapContext
operator|.
name|getCombinerClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getConfiguration
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFileClassPaths ()
specifier|public
name|Path
index|[]
name|getFileClassPaths
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getFileClassPaths
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFileTimestamps ()
specifier|public
name|String
index|[]
name|getFileTimestamps
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getFileTimestamps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getGroupingComparator ()
specifier|public
name|RawComparator
argument_list|<
name|?
argument_list|>
name|getGroupingComparator
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getGroupingComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getInputFormatClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|InputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getInputFormatClass
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
return|return
name|mapContext
operator|.
name|getInputFormatClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getJar ()
specifier|public
name|String
name|getJar
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getJar
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getJobID ()
specifier|public
name|JobID
name|getJobID
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getJobID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getJobName ()
specifier|public
name|String
name|getJobName
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getJobName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getJobSetupCleanupNeeded ()
specifier|public
name|boolean
name|getJobSetupCleanupNeeded
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getJobSetupCleanupNeeded
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskCleanupNeeded ()
specifier|public
name|boolean
name|getTaskCleanupNeeded
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getTaskCleanupNeeded
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalCacheArchives ()
specifier|public
name|Path
index|[]
name|getLocalCacheArchives
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mapContext
operator|.
name|getLocalCacheArchives
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalCacheFiles ()
specifier|public
name|Path
index|[]
name|getLocalCacheFiles
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mapContext
operator|.
name|getLocalCacheFiles
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMapOutputKeyClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getMapOutputKeyClass
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getMapOutputKeyClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMapOutputValueClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getMapOutputValueClass
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getMapOutputValueClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMapperClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Mapper
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getMapperClass
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
return|return
name|mapContext
operator|.
name|getMapperClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxMapAttempts ()
specifier|public
name|int
name|getMaxMapAttempts
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getMaxMapAttempts
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxReduceAttempts ()
specifier|public
name|int
name|getMaxReduceAttempts
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getMaxReduceAttempts
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumReduceTasks ()
specifier|public
name|int
name|getNumReduceTasks
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getNumReduceTasks
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputFormatClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getOutputFormatClass
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
return|return
name|mapContext
operator|.
name|getOutputFormatClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputKeyClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getOutputKeyClass
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getOutputKeyClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputValueClass ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getOutputValueClass
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getOutputValueClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPartitionerClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Partitioner
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getPartitionerClass
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
return|return
name|mapContext
operator|.
name|getPartitionerClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReducerClass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getReducerClass
parameter_list|()
throws|throws
name|ClassNotFoundException
block|{
return|return
name|mapContext
operator|.
name|getReducerClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSortComparator ()
specifier|public
name|RawComparator
argument_list|<
name|?
argument_list|>
name|getSortComparator
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getSortComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSymlink ()
specifier|public
name|boolean
name|getSymlink
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getSymlink
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mapContext
operator|.
name|getWorkingDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|progress ()
specifier|public
name|void
name|progress
parameter_list|()
block|{
name|mapContext
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProfileEnabled ()
specifier|public
name|boolean
name|getProfileEnabled
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getProfileEnabled
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProfileParams ()
specifier|public
name|String
name|getProfileParams
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getProfileParams
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProfileTaskRange (boolean isMap)
specifier|public
name|IntegerRanges
name|getProfileTaskRange
parameter_list|(
name|boolean
name|isMap
parameter_list|)
block|{
return|return
name|mapContext
operator|.
name|getProfileTaskRange
argument_list|(
name|isMap
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getUser
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCredentials ()
specifier|public
name|Credentials
name|getCredentials
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getCredentials
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|mapContext
operator|.
name|getProgress
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

