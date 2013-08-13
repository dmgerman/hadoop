begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task.reduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|reduce
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|mapred
operator|.
name|IndexRecord
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|MapOutputFile
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
name|mapred
operator|.
name|Reporter
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
name|mapred
operator|.
name|SpillRecord
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

begin_comment
comment|/**  * LocalFetcher is used by LocalJobRunner to perform a local filesystem  * fetch.  */
end_comment

begin_class
DECL|class|LocalFetcher
class|class
name|LocalFetcher
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Fetcher
argument_list|<
name|K
argument_list|,
name|V
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
name|LocalFetcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOCALHOST
specifier|private
specifier|static
specifier|final
name|MapHost
name|LOCALHOST
init|=
operator|new
name|MapHost
argument_list|(
literal|"local"
argument_list|,
literal|"local"
argument_list|)
decl_stmt|;
DECL|field|job
specifier|private
name|JobConf
name|job
decl_stmt|;
DECL|field|localMapFiles
specifier|private
name|Map
argument_list|<
name|TaskAttemptID
argument_list|,
name|MapOutputFile
argument_list|>
name|localMapFiles
decl_stmt|;
DECL|method|LocalFetcher (JobConf job, TaskAttemptID reduceId, ShuffleSchedulerImpl<K, V> scheduler, MergeManager<K,V> merger, Reporter reporter, ShuffleClientMetrics metrics, ExceptionReporter exceptionReporter, SecretKey shuffleKey, Map<TaskAttemptID, MapOutputFile> localMapFiles)
specifier|public
name|LocalFetcher
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|TaskAttemptID
name|reduceId
parameter_list|,
name|ShuffleSchedulerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|scheduler
parameter_list|,
name|MergeManager
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|merger
parameter_list|,
name|Reporter
name|reporter
parameter_list|,
name|ShuffleClientMetrics
name|metrics
parameter_list|,
name|ExceptionReporter
name|exceptionReporter
parameter_list|,
name|SecretKey
name|shuffleKey
parameter_list|,
name|Map
argument_list|<
name|TaskAttemptID
argument_list|,
name|MapOutputFile
argument_list|>
name|localMapFiles
parameter_list|)
block|{
name|super
argument_list|(
name|job
argument_list|,
name|reduceId
argument_list|,
name|scheduler
argument_list|,
name|merger
argument_list|,
name|reporter
argument_list|,
name|metrics
argument_list|,
name|exceptionReporter
argument_list|,
name|shuffleKey
argument_list|)
expr_stmt|;
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
name|this
operator|.
name|localMapFiles
operator|=
name|localMapFiles
expr_stmt|;
name|setName
argument_list|(
literal|"localfetcher#"
operator|+
name|id
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// Create a worklist of task attempts to work over.
name|Set
argument_list|<
name|TaskAttemptID
argument_list|>
name|maps
init|=
operator|new
name|HashSet
argument_list|<
name|TaskAttemptID
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskAttemptID
name|map
range|:
name|localMapFiles
operator|.
name|keySet
argument_list|()
control|)
block|{
name|maps
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|maps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
comment|// If merge is on, block
name|merger
operator|.
name|waitForResource
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|threadBusy
argument_list|()
expr_stmt|;
comment|// Copy as much as is possible.
name|doCopy
argument_list|(
name|maps
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|threadFree
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{       }
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exceptionReporter
operator|.
name|reportException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The crux of the matter...    */
DECL|method|doCopy (Set<TaskAttemptID> maps)
specifier|private
name|void
name|doCopy
parameter_list|(
name|Set
argument_list|<
name|TaskAttemptID
argument_list|>
name|maps
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|TaskAttemptID
argument_list|>
name|iter
init|=
name|maps
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TaskAttemptID
name|map
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"LocalFetcher "
operator|+
name|id
operator|+
literal|" going to fetch: "
operator|+
name|map
argument_list|)
expr_stmt|;
if|if
condition|(
name|copyMapOutput
argument_list|(
name|map
argument_list|)
condition|)
block|{
comment|// Successful copy. Remove this from our worklist.
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// We got back a WAIT command; go back to the outer loop
comment|// and block for InMemoryMerge.
break|break;
block|}
block|}
block|}
comment|/**    * Retrieve the map output of a single map task    * and send it to the merger.    */
DECL|method|copyMapOutput (TaskAttemptID mapTaskId)
specifier|private
name|boolean
name|copyMapOutput
parameter_list|(
name|TaskAttemptID
name|mapTaskId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Figure out where the map task stored its output.
name|Path
name|mapOutputFileName
init|=
name|localMapFiles
operator|.
name|get
argument_list|(
name|mapTaskId
argument_list|)
operator|.
name|getOutputFile
argument_list|()
decl_stmt|;
name|Path
name|indexFileName
init|=
name|mapOutputFileName
operator|.
name|suffix
argument_list|(
literal|".index"
argument_list|)
decl_stmt|;
comment|// Read its index to determine the location of our split
comment|// and its size.
name|SpillRecord
name|sr
init|=
operator|new
name|SpillRecord
argument_list|(
name|indexFileName
argument_list|,
name|job
argument_list|)
decl_stmt|;
name|IndexRecord
name|ir
init|=
name|sr
operator|.
name|getIndex
argument_list|(
name|reduce
argument_list|)
decl_stmt|;
name|long
name|compressedLength
init|=
name|ir
operator|.
name|partLength
decl_stmt|;
name|long
name|decompressedLength
init|=
name|ir
operator|.
name|rawLength
decl_stmt|;
comment|// Get the location for the map output - either in-memory or on-disk
name|MapOutput
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|mapOutput
init|=
name|merger
operator|.
name|reserve
argument_list|(
name|mapTaskId
argument_list|,
name|decompressedLength
argument_list|,
name|id
argument_list|)
decl_stmt|;
comment|// Check if we can shuffle *now* ...
if|if
condition|(
name|mapOutput
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"fetcher#"
operator|+
name|id
operator|+
literal|" - MergeManager returned Status.WAIT ..."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Go!
name|LOG
operator|.
name|info
argument_list|(
literal|"localfetcher#"
operator|+
name|id
operator|+
literal|" about to shuffle output of map "
operator|+
name|mapOutput
operator|.
name|getMapId
argument_list|()
operator|+
literal|" decomp: "
operator|+
name|decompressedLength
operator|+
literal|" len: "
operator|+
name|compressedLength
operator|+
literal|" to "
operator|+
name|mapOutput
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
comment|// now read the file, seek to the appropriate section, and send it.
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|FSDataInputStream
name|inStream
init|=
name|localFs
operator|.
name|open
argument_list|(
name|mapOutputFileName
argument_list|)
decl_stmt|;
try|try
block|{
name|inStream
operator|.
name|seek
argument_list|(
name|ir
operator|.
name|startOffset
argument_list|)
expr_stmt|;
name|mapOutput
operator|.
name|shuffle
argument_list|(
name|LOCALHOST
argument_list|,
name|inStream
argument_list|,
name|compressedLength
argument_list|,
name|decompressedLength
argument_list|,
name|metrics
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|inStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IOException closing inputstream from map output: "
operator|+
name|ioe
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|scheduler
operator|.
name|copySucceeded
argument_list|(
name|mapTaskId
argument_list|,
name|LOCALHOST
argument_list|,
name|compressedLength
argument_list|,
literal|0
argument_list|,
name|mapOutput
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
comment|// successful fetch.
block|}
block|}
end_class

end_unit

