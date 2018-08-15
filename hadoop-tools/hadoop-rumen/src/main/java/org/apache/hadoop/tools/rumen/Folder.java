begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|util
operator|.
name|Comparator
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
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|Configured
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
name|util
operator|.
name|Tool
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
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_class
DECL|class|Folder
specifier|public
class|class
name|Folder
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|outputDuration
specifier|private
name|long
name|outputDuration
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|inputCycle
specifier|private
name|long
name|inputCycle
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|concentration
specifier|private
name|double
name|concentration
init|=
literal|1.0
decl_stmt|;
DECL|field|randomSeed
specifier|private
name|long
name|randomSeed
init|=
literal|0
decl_stmt|;
comment|// irrelevant if seeded == false
DECL|field|seeded
specifier|private
name|boolean
name|seeded
init|=
literal|false
decl_stmt|;
DECL|field|debug
specifier|private
name|boolean
name|debug
init|=
literal|false
decl_stmt|;
DECL|field|allowMissorting
specifier|private
name|boolean
name|allowMissorting
init|=
literal|false
decl_stmt|;
DECL|field|skewBufferLength
specifier|private
name|int
name|skewBufferLength
init|=
literal|0
decl_stmt|;
DECL|field|startsAfter
specifier|private
name|long
name|startsAfter
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
specifier|private
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Folder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|reader
specifier|private
name|DeskewedJobTraceReader
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|outGen
specifier|private
name|Outputter
argument_list|<
name|LoggedJob
argument_list|>
name|outGen
init|=
literal|null
decl_stmt|;
DECL|field|tempPaths
specifier|private
name|List
argument_list|<
name|Path
argument_list|>
name|tempPaths
init|=
operator|new
name|LinkedList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|tempDir
specifier|private
name|Path
name|tempDir
init|=
literal|null
decl_stmt|;
DECL|field|firstJobSubmitTime
specifier|private
name|long
name|firstJobSubmitTime
decl_stmt|;
DECL|field|timeDilation
specifier|private
name|double
name|timeDilation
decl_stmt|;
DECL|field|transcriptionRateFraction
specifier|private
name|double
name|transcriptionRateFraction
decl_stmt|;
DECL|field|transcriptionRateInteger
specifier|private
name|int
name|transcriptionRateInteger
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|field|TICKS_PER_SECOND
specifier|static
specifier|private
specifier|final
name|long
name|TICKS_PER_SECOND
init|=
literal|1000L
decl_stmt|;
comment|// error return codes
DECL|field|NON_EXISTENT_FILES
specifier|static
specifier|private
specifier|final
name|int
name|NON_EXISTENT_FILES
init|=
literal|1
decl_stmt|;
DECL|field|NO_INPUT_CYCLE_LENGTH
specifier|static
specifier|private
specifier|final
name|int
name|NO_INPUT_CYCLE_LENGTH
init|=
literal|2
decl_stmt|;
DECL|field|EMPTY_JOB_TRACE
specifier|static
specifier|private
specifier|final
name|int
name|EMPTY_JOB_TRACE
init|=
literal|3
decl_stmt|;
DECL|field|OUT_OF_ORDER_JOBS
specifier|static
specifier|private
specifier|final
name|int
name|OUT_OF_ORDER_JOBS
init|=
literal|4
decl_stmt|;
DECL|field|ALL_JOBS_SIMULTANEOUS
specifier|static
specifier|private
specifier|final
name|int
name|ALL_JOBS_SIMULTANEOUS
init|=
literal|5
decl_stmt|;
DECL|field|IO_ERROR
specifier|static
specifier|private
specifier|final
name|int
name|IO_ERROR
init|=
literal|6
decl_stmt|;
DECL|field|OTHER_ERROR
specifier|static
specifier|private
specifier|final
name|int
name|OTHER_ERROR
init|=
literal|7
decl_stmt|;
DECL|field|closees
specifier|private
name|Set
argument_list|<
name|Closeable
argument_list|>
name|closees
init|=
operator|new
name|HashSet
argument_list|<
name|Closeable
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|deletees
specifier|private
name|Set
argument_list|<
name|Path
argument_list|>
name|deletees
init|=
operator|new
name|HashSet
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|parseDuration (String durationString)
specifier|static
name|long
name|parseDuration
parameter_list|(
name|String
name|durationString
parameter_list|)
block|{
name|String
name|numeral
init|=
name|durationString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|durationString
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|char
name|durationCode
init|=
name|durationString
operator|.
name|charAt
argument_list|(
name|durationString
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|result
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numeral
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Negative durations are not allowed"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|durationCode
condition|)
block|{
case|case
literal|'D'
case|:
case|case
literal|'d'
case|:
return|return
literal|24L
operator|*
literal|60L
operator|*
literal|60L
operator|*
name|TICKS_PER_SECOND
operator|*
name|result
return|;
case|case
literal|'H'
case|:
case|case
literal|'h'
case|:
return|return
literal|60L
operator|*
literal|60L
operator|*
name|TICKS_PER_SECOND
operator|*
name|result
return|;
case|case
literal|'M'
case|:
case|case
literal|'m'
case|:
return|return
literal|60L
operator|*
name|TICKS_PER_SECOND
operator|*
name|result
return|;
case|case
literal|'S'
case|:
case|case
literal|'s'
case|:
return|return
name|TICKS_PER_SECOND
operator|*
name|result
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing or invalid duration code"
argument_list|)
throw|;
block|}
block|}
DECL|method|initialize (String[] args)
specifier|private
name|int
name|initialize
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|String
name|tempDirName
init|=
literal|null
decl_stmt|;
name|String
name|inputPathName
init|=
literal|null
decl_stmt|;
name|String
name|outputPathName
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|String
name|thisArg
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-starts-after"
argument_list|)
condition|)
block|{
name|startsAfter
operator|=
name|parseDuration
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-output-duration"
argument_list|)
condition|)
block|{
name|outputDuration
operator|=
name|parseDuration
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-input-cycle"
argument_list|)
condition|)
block|{
name|inputCycle
operator|=
name|parseDuration
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-concentration"
argument_list|)
condition|)
block|{
name|concentration
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-debug"
argument_list|)
condition|)
block|{
name|debug
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-allow-missorting"
argument_list|)
condition|)
block|{
name|allowMissorting
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-seed"
argument_list|)
condition|)
block|{
name|seeded
operator|=
literal|true
expr_stmt|;
name|randomSeed
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-skew-buffer-length"
argument_list|)
condition|)
block|{
name|skewBufferLength
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-temp-directory"
argument_list|)
condition|)
block|{
name|tempDirName
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|thisArg
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|thisArg
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal switch argument, "
operator|+
name|thisArg
operator|+
literal|" at position "
operator|+
name|i
argument_list|)
throw|;
block|}
else|else
block|{
name|inputPathName
operator|=
name|thisArg
expr_stmt|;
name|outputPathName
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|args
operator|.
name|length
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Too many non-switch arguments"
argument_list|)
throw|;
block|}
block|}
block|}
try|try
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|Path
name|inPath
init|=
operator|new
name|Path
argument_list|(
name|inputPathName
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|DeskewedJobTraceReader
argument_list|(
operator|new
name|JobTraceReader
argument_list|(
name|inPath
argument_list|,
name|conf
argument_list|)
argument_list|,
name|skewBufferLength
argument_list|,
operator|!
name|allowMissorting
argument_list|)
expr_stmt|;
name|Path
name|outPath
init|=
operator|new
name|Path
argument_list|(
name|outputPathName
argument_list|)
decl_stmt|;
name|outGen
operator|=
operator|new
name|DefaultOutputter
argument_list|<
name|LoggedJob
argument_list|>
argument_list|()
expr_stmt|;
name|outGen
operator|.
name|init
argument_list|(
name|outPath
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|tempDir
operator|=
name|tempDirName
operator|==
literal|null
condition|?
name|outPath
operator|.
name|getParent
argument_list|()
else|:
operator|new
name|Path
argument_list|(
name|tempDirName
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|tempDir
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|getFileStatus
argument_list|(
name|tempDir
argument_list|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Your temp directory is not a directory"
argument_list|)
throw|;
block|}
if|if
condition|(
name|inputCycle
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"You must have an input cycle length."
argument_list|)
expr_stmt|;
return|return
name|NO_INPUT_CYCLE_LENGTH
return|;
block|}
if|if
condition|(
name|outputDuration
operator|<=
literal|0
condition|)
block|{
name|outputDuration
operator|=
literal|60L
operator|*
literal|60L
operator|*
name|TICKS_PER_SECOND
expr_stmt|;
block|}
if|if
condition|(
name|inputCycle
operator|<=
literal|0
condition|)
block|{
name|inputCycle
operator|=
name|outputDuration
expr_stmt|;
block|}
name|timeDilation
operator|=
operator|(
name|double
operator|)
name|outputDuration
operator|/
operator|(
name|double
operator|)
name|inputCycle
expr_stmt|;
name|random
operator|=
name|seeded
condition|?
operator|new
name|Random
argument_list|(
name|randomSeed
argument_list|)
else|:
operator|new
name|Random
argument_list|()
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|randomSeed
operator|=
name|random
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"This run effectively has a -seed of "
operator|+
name|randomSeed
argument_list|)
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
name|randomSeed
argument_list|)
expr_stmt|;
name|seeded
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
name|NON_EXISTENT_FILES
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
name|initialize
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
return|return
name|run
argument_list|()
return|;
block|}
DECL|method|run ()
specifier|public
name|int
name|run
parameter_list|()
throws|throws
name|IOException
block|{
class|class
name|JobEntryComparator
implements|implements
name|Comparator
argument_list|<
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
argument_list|>
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
name|p1
parameter_list|,
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
name|p2
parameter_list|)
block|{
name|LoggedJob
name|j1
init|=
name|p1
operator|.
name|first
argument_list|()
decl_stmt|;
name|LoggedJob
name|j2
init|=
name|p2
operator|.
name|first
argument_list|()
decl_stmt|;
return|return
operator|(
name|j1
operator|.
name|getSubmitTime
argument_list|()
operator|<
name|j2
operator|.
name|getSubmitTime
argument_list|()
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
name|j1
operator|.
name|getSubmitTime
argument_list|()
operator|==
name|j2
operator|.
name|getSubmitTime
argument_list|()
operator|)
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
comment|// we initialize an empty heap so if we take an error before establishing
comment|// a real one the finally code goes through
name|Queue
argument_list|<
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
argument_list|>
name|heap
init|=
operator|new
name|PriorityQueue
argument_list|<
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|LoggedJob
name|job
init|=
name|reader
operator|.
name|nextJob
argument_list|()
decl_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The job trace is empty"
argument_list|)
expr_stmt|;
return|return
name|EMPTY_JOB_TRACE
return|;
block|}
comment|// If starts-after time is specified, skip the number of jobs till we reach
comment|// the starting time limit.
if|if
condition|(
name|startsAfter
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"starts-after time is specified. Initial job submit time : "
operator|+
name|job
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|approximateTime
init|=
name|job
operator|.
name|getSubmitTime
argument_list|()
operator|+
name|startsAfter
decl_stmt|;
name|job
operator|=
name|reader
operator|.
name|nextJob
argument_list|()
expr_stmt|;
name|long
name|skippedCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|job
operator|!=
literal|null
operator|&&
name|job
operator|.
name|getSubmitTime
argument_list|()
operator|<
name|approximateTime
condition|)
block|{
name|job
operator|=
name|reader
operator|.
name|nextJob
argument_list|()
expr_stmt|;
name|skippedCount
operator|++
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Considering jobs with submit time greater than "
operator|+
name|startsAfter
operator|+
literal|" ms. Skipped "
operator|+
name|skippedCount
operator|+
literal|" jobs."
argument_list|)
expr_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No more jobs to process in the trace with 'starts-after'"
operator|+
literal|" set to "
operator|+
name|startsAfter
operator|+
literal|"ms."
argument_list|)
expr_stmt|;
return|return
name|EMPTY_JOB_TRACE
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"The first job has a submit time of "
operator|+
name|job
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|firstJobSubmitTime
operator|=
name|job
operator|.
name|getSubmitTime
argument_list|()
expr_stmt|;
name|long
name|lastJobSubmitTime
init|=
name|firstJobSubmitTime
decl_stmt|;
name|int
name|numberJobs
init|=
literal|0
decl_stmt|;
name|long
name|currentIntervalEnd
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|Path
name|nextSegment
init|=
literal|null
decl_stmt|;
name|Outputter
argument_list|<
name|LoggedJob
argument_list|>
name|tempGen
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The first job has a submit time of "
operator|+
name|firstJobSubmitTime
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
try|try
block|{
comment|// At the top of this loop, skewBuffer has at most
comment|// skewBufferLength entries.
while|while
condition|(
name|job
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Random
name|tempNameGenerator
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|lastJobSubmitTime
operator|=
name|job
operator|.
name|getSubmitTime
argument_list|()
expr_stmt|;
operator|++
name|numberJobs
expr_stmt|;
if|if
condition|(
name|job
operator|.
name|getSubmitTime
argument_list|()
operator|>=
name|currentIntervalEnd
condition|)
block|{
if|if
condition|(
name|tempGen
operator|!=
literal|null
condition|)
block|{
name|tempGen
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|nextSegment
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
operator|&&
name|nextSegment
operator|==
literal|null
condition|;
operator|++
name|i
control|)
block|{
try|try
block|{
name|nextSegment
operator|=
operator|new
name|Path
argument_list|(
name|tempDir
argument_list|,
literal|"segment-"
operator|+
name|tempNameGenerator
operator|.
name|nextLong
argument_list|()
operator|+
literal|".json.gz"
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The next segment name is "
operator|+
name|nextSegment
argument_list|)
expr_stmt|;
block|}
name|FileSystem
name|fs
init|=
name|nextSegment
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|nextSegment
argument_list|)
condition|)
block|{
break|break;
block|}
continue|continue;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// no code -- file did not already exist
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// no code -- file exists now, or directory bad. We try three
comment|// times.
block|}
block|}
if|if
condition|(
name|nextSegment
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create a new file!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating "
operator|+
name|nextSegment
operator|+
literal|" for a job with a submit time of "
operator|+
name|job
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|deletees
operator|.
name|add
argument_list|(
name|nextSegment
argument_list|)
expr_stmt|;
name|tempPaths
operator|.
name|add
argument_list|(
name|nextSegment
argument_list|)
expr_stmt|;
name|tempGen
operator|=
operator|new
name|DefaultOutputter
argument_list|<
name|LoggedJob
argument_list|>
argument_list|()
expr_stmt|;
name|tempGen
operator|.
name|init
argument_list|(
name|nextSegment
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|long
name|currentIntervalNumber
init|=
operator|(
name|job
operator|.
name|getSubmitTime
argument_list|()
operator|-
name|firstJobSubmitTime
operator|)
operator|/
name|inputCycle
decl_stmt|;
name|currentIntervalEnd
operator|=
name|firstJobSubmitTime
operator|+
operator|(
operator|(
name|currentIntervalNumber
operator|+
literal|1
operator|)
operator|*
name|inputCycle
operator|)
expr_stmt|;
block|}
comment|// the temp files contain UDadjusted times, but each temp file's
comment|// content is in the same input cycle interval.
if|if
condition|(
name|tempGen
operator|!=
literal|null
condition|)
block|{
name|tempGen
operator|.
name|output
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
name|job
operator|=
name|reader
operator|.
name|nextJob
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|DeskewedJobTraceReader
operator|.
name|OutOfOrderException
name|e
parameter_list|)
block|{
return|return
name|OUT_OF_ORDER_JOBS
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|tempGen
operator|!=
literal|null
condition|)
block|{
name|tempGen
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastJobSubmitTime
operator|<=
name|firstJobSubmitTime
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"All of your job[s] have the same submit time."
operator|+
literal|"  Please just use your input file."
argument_list|)
expr_stmt|;
return|return
name|ALL_JOBS_SIMULTANEOUS
return|;
block|}
name|double
name|submitTimeSpan
init|=
name|lastJobSubmitTime
operator|-
name|firstJobSubmitTime
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Your input trace spans "
operator|+
operator|(
name|lastJobSubmitTime
operator|-
name|firstJobSubmitTime
operator|)
operator|+
literal|" ticks."
argument_list|)
expr_stmt|;
name|double
name|foldingRatio
init|=
name|submitTimeSpan
operator|*
operator|(
name|numberJobs
operator|+
literal|1
operator|)
operator|/
name|numberJobs
operator|/
name|inputCycle
decl_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"run: submitTimeSpan = "
operator|+
name|submitTimeSpan
operator|+
literal|", numberJobs = "
operator|+
name|numberJobs
operator|+
literal|", inputCycle = "
operator|+
name|inputCycle
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|.
name|neededSkewBufferSize
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"You needed a -skew-buffer-length of "
operator|+
name|reader
operator|.
name|neededSkewBufferSize
argument_list|()
operator|+
literal|" but no more, for this input."
argument_list|)
expr_stmt|;
block|}
name|double
name|tProbability
init|=
name|timeDilation
operator|*
name|concentration
operator|/
name|foldingRatio
decl_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"run: timeDilation = "
operator|+
name|timeDilation
operator|+
literal|", concentration = "
operator|+
name|concentration
operator|+
literal|", foldingRatio = "
operator|+
name|foldingRatio
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"The transcription probability is "
operator|+
name|tProbability
argument_list|)
expr_stmt|;
block|}
name|transcriptionRateInteger
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|tProbability
argument_list|)
expr_stmt|;
name|transcriptionRateFraction
operator|=
name|tProbability
operator|-
name|Math
operator|.
name|floor
argument_list|(
name|tProbability
argument_list|)
expr_stmt|;
comment|// Now read all the inputs in parallel
name|heap
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
argument_list|>
argument_list|(
name|tempPaths
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|JobEntryComparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|tempPath
range|:
name|tempPaths
control|)
block|{
name|JobTraceReader
name|thisReader
init|=
operator|new
name|JobTraceReader
argument_list|(
name|tempPath
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|closees
operator|.
name|add
argument_list|(
name|thisReader
argument_list|)
expr_stmt|;
name|LoggedJob
name|streamFirstJob
init|=
name|thisReader
operator|.
name|getNext
argument_list|()
decl_stmt|;
name|long
name|thisIndex
init|=
operator|(
name|streamFirstJob
operator|.
name|getSubmitTime
argument_list|()
operator|-
name|firstJobSubmitTime
operator|)
operator|/
name|inputCycle
decl_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"A job with submit time of "
operator|+
name|streamFirstJob
operator|.
name|getSubmitTime
argument_list|()
operator|+
literal|" is in interval # "
operator|+
name|thisIndex
argument_list|)
expr_stmt|;
block|}
name|adjustJobTimes
argument_list|(
name|streamFirstJob
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"That job's submit time is adjusted to "
operator|+
name|streamFirstJob
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|heap
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
argument_list|(
name|streamFirstJob
argument_list|,
name|thisReader
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
name|next
init|=
name|heap
operator|.
name|poll
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|maybeOutput
argument_list|(
name|next
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The most recent job has an adjusted submit time of "
operator|+
name|next
operator|.
name|first
argument_list|()
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|" Its replacement in the heap will come from input engine "
operator|+
name|next
operator|.
name|second
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LoggedJob
name|replacement
init|=
name|next
operator|.
name|second
argument_list|()
operator|.
name|getNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|replacement
operator|==
literal|null
condition|)
block|{
name|next
operator|.
name|second
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"That input engine is depleted."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|adjustJobTimes
argument_list|(
name|replacement
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The replacement has an adjusted submit time of "
operator|+
name|replacement
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|heap
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
argument_list|(
name|replacement
argument_list|,
name|next
operator|.
name|second
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|next
operator|=
name|heap
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|outGen
operator|!=
literal|null
condition|)
block|{
name|outGen
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Pair
argument_list|<
name|LoggedJob
argument_list|,
name|JobTraceReader
argument_list|>
name|heapEntry
range|:
name|heap
control|)
block|{
name|heapEntry
operator|.
name|second
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Closeable
name|closee
range|:
name|closees
control|)
block|{
name|closee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|debug
condition|)
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|deletee
range|:
name|deletees
control|)
block|{
name|FileSystem
name|fs
init|=
name|deletee
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
name|deletee
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// no code
block|}
block|}
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|maybeOutput (LoggedJob job)
specifier|private
name|void
name|maybeOutput
parameter_list|(
name|LoggedJob
name|job
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|transcriptionRateInteger
condition|;
operator|++
name|i
control|)
block|{
name|outGen
operator|.
name|output
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
name|transcriptionRateFraction
condition|)
block|{
name|outGen
operator|.
name|output
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|adjustJobTimes (LoggedJob adjustee)
specifier|private
name|void
name|adjustJobTimes
parameter_list|(
name|LoggedJob
name|adjustee
parameter_list|)
block|{
name|long
name|offsetInCycle
init|=
operator|(
name|adjustee
operator|.
name|getSubmitTime
argument_list|()
operator|-
name|firstJobSubmitTime
operator|)
operator|%
name|inputCycle
decl_stmt|;
name|long
name|outputOffset
init|=
call|(
name|long
call|)
argument_list|(
operator|(
name|double
operator|)
name|offsetInCycle
operator|*
name|timeDilation
argument_list|)
decl_stmt|;
name|long
name|adjustment
init|=
name|firstJobSubmitTime
operator|+
name|outputOffset
operator|-
name|adjustee
operator|.
name|getSubmitTime
argument_list|()
decl_stmt|;
name|adjustee
operator|.
name|adjustTimes
argument_list|(
name|adjustment
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param args    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Folder
name|instance
init|=
operator|new
name|Folder
argument_list|()
decl_stmt|;
name|int
name|result
init|=
literal|0
decl_stmt|;
try|try
block|{
name|result
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|instance
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|IO_ERROR
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|OTHER_ERROR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
end_class

end_unit

