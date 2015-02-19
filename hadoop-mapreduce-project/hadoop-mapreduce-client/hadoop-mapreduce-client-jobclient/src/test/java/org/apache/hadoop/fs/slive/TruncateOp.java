begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Random
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
name|slive
operator|.
name|OperationOutput
operator|.
name|OutputType
import|;
end_import

begin_comment
comment|/**  * Operation which selects a random file and truncates a random amount of bytes  * (selected from the configuration for truncate size) from that file,  * if it exists.  *   * This operation will capture statistics on success for bytes written, time  * taken (milliseconds), and success count and on failure it will capture the  * number of failures and the time taken (milliseconds) to fail.  */
end_comment

begin_class
DECL|class|TruncateOp
class|class
name|TruncateOp
extends|extends
name|Operation
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
name|TruncateOp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TruncateOp (ConfigExtractor cfg, Random rnd)
name|TruncateOp
parameter_list|(
name|ConfigExtractor
name|cfg
parameter_list|,
name|Random
name|rnd
parameter_list|)
block|{
name|super
argument_list|(
name|TruncateOp
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|cfg
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the file to truncate from    *     * @return Path    */
DECL|method|getTruncateFile ()
specifier|protected
name|Path
name|getTruncateFile
parameter_list|()
block|{
name|Path
name|fn
init|=
name|getFinder
argument_list|()
operator|.
name|getFile
argument_list|()
decl_stmt|;
return|return
name|fn
return|;
block|}
annotation|@
name|Override
comment|// Operation
DECL|method|run (FileSystem fs)
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|run
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|out
init|=
name|super
operator|.
name|run
argument_list|(
name|fs
argument_list|)
decl_stmt|;
try|try
block|{
name|Path
name|fn
init|=
name|getTruncateFile
argument_list|()
decl_stmt|;
name|boolean
name|waitOnTruncate
init|=
name|getConfig
argument_list|()
operator|.
name|shouldWaitOnTruncate
argument_list|()
decl_stmt|;
name|long
name|currentSize
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|fn
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
comment|// determine file status for file length requirement
comment|// to know if should fill in partial bytes
name|Range
argument_list|<
name|Long
argument_list|>
name|truncateSizeRange
init|=
name|getConfig
argument_list|()
operator|.
name|getTruncateSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|getConfig
argument_list|()
operator|.
name|shouldTruncateUseBlockSize
argument_list|()
condition|)
block|{
name|truncateSizeRange
operator|=
name|getConfig
argument_list|()
operator|.
name|getBlockSize
argument_list|()
expr_stmt|;
block|}
name|long
name|truncateSize
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|currentSize
operator|-
name|Range
operator|.
name|betweenPositive
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|truncateSizeRange
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|timeTaken
init|=
literal|0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to truncate file at "
operator|+
name|fn
operator|+
literal|" to size "
operator|+
name|Helper
operator|.
name|toByteInfo
argument_list|(
name|truncateSize
argument_list|)
argument_list|)
expr_stmt|;
block|{
comment|// truncate
name|long
name|startTime
init|=
name|Timer
operator|.
name|now
argument_list|()
decl_stmt|;
name|boolean
name|completed
init|=
name|fs
operator|.
name|truncate
argument_list|(
name|fn
argument_list|,
name|truncateSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|completed
operator|&&
name|waitOnTruncate
condition|)
name|waitForRecovery
argument_list|(
name|fs
argument_list|,
name|fn
argument_list|,
name|truncateSize
argument_list|)
expr_stmt|;
name|timeTaken
operator|+=
name|Timer
operator|.
name|elapsed
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|BYTES_WRITTEN
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|OK_TIME_TAKEN
argument_list|,
name|timeTaken
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|SUCCESSES
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Truncate file "
operator|+
name|fn
operator|+
literal|" to "
operator|+
name|Helper
operator|.
name|toByteInfo
argument_list|(
name|truncateSize
argument_list|)
operator|+
literal|" in "
operator|+
name|timeTaken
operator|+
literal|" milliseconds"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|NOT_FOUND
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error with truncating"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|OperationOutput
argument_list|(
name|OutputType
operator|.
name|LONG
argument_list|,
name|getType
argument_list|()
argument_list|,
name|ReportWriter
operator|.
name|FAILURES
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error with truncating"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
DECL|method|waitForRecovery (FileSystem fs, Path fn, long newLength)
specifier|private
name|void
name|waitForRecovery
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|fn
parameter_list|,
name|long
name|newLength
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting on truncate file recovery for "
operator|+
name|fn
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|FileStatus
name|stat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|fn
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|.
name|getLen
argument_list|()
operator|==
name|newLength
condition|)
break|break;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{}
block|}
block|}
block|}
end_class

end_unit

