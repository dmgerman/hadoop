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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Arrays
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
name|Properties
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
name|mapreduce
operator|.
name|jobhistory
operator|.
name|HistoryEvent
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
name|hs
operator|.
name|JobHistory
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

begin_comment
comment|/**  * The main driver of the Rumen Parser.  */
end_comment

begin_class
DECL|class|TraceBuilder
specifier|public
class|class
name|TraceBuilder
extends|extends
name|Configured
implements|implements
name|Tool
block|{
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
name|TraceBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RUN_METHOD_FAILED_EXIT_CODE
specifier|static
specifier|final
name|int
name|RUN_METHOD_FAILED_EXIT_CODE
init|=
literal|3
decl_stmt|;
DECL|field|topologyBuilder
name|TopologyBuilder
name|topologyBuilder
init|=
operator|new
name|TopologyBuilder
argument_list|()
decl_stmt|;
DECL|field|traceWriter
name|Outputter
argument_list|<
name|LoggedJob
argument_list|>
name|traceWriter
decl_stmt|;
DECL|field|topologyWriter
name|Outputter
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|topologyWriter
decl_stmt|;
DECL|class|MyOptions
specifier|static
class|class
name|MyOptions
block|{
DECL|field|inputDemuxerClass
name|Class
argument_list|<
name|?
extends|extends
name|InputDemuxer
argument_list|>
name|inputDemuxerClass
init|=
name|DefaultInputDemuxer
operator|.
name|class
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|clazzTraceOutputter
name|Class
argument_list|<
name|?
extends|extends
name|Outputter
argument_list|>
name|clazzTraceOutputter
init|=
name|DefaultOutputter
operator|.
name|class
decl_stmt|;
DECL|field|traceOutput
name|Path
name|traceOutput
decl_stmt|;
DECL|field|topologyOutput
name|Path
name|topologyOutput
decl_stmt|;
DECL|field|inputs
name|List
argument_list|<
name|Path
argument_list|>
name|inputs
init|=
operator|new
name|LinkedList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MyOptions (String[] args, Configuration conf)
name|MyOptions
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|int
name|switchTop
init|=
literal|0
decl_stmt|;
comment|// to determine if the input paths should be recursively scanned or not
name|boolean
name|doRecursiveTraversal
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|args
index|[
name|switchTop
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
name|switchTop
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-demuxer"
argument_list|)
condition|)
block|{
name|inputDemuxerClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|args
index|[
operator|++
name|switchTop
index|]
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|InputDemuxer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|switchTop
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"-recursive"
argument_list|)
condition|)
block|{
name|doRecursiveTraversal
operator|=
literal|true
expr_stmt|;
block|}
operator|++
name|switchTop
expr_stmt|;
block|}
name|traceOutput
operator|=
operator|new
name|Path
argument_list|(
name|args
index|[
literal|0
operator|+
name|switchTop
index|]
argument_list|)
expr_stmt|;
name|topologyOutput
operator|=
operator|new
name|Path
argument_list|(
name|args
index|[
literal|1
operator|+
name|switchTop
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
operator|+
name|switchTop
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
name|inputs
operator|.
name|addAll
argument_list|(
name|processInputArgument
argument_list|(
name|args
index|[
name|i
index|]
argument_list|,
name|conf
argument_list|,
name|doRecursiveTraversal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Compare the history file names, not the full paths.      * Job history file name format is such that doing lexicographic sort on the      * history file names should result in the order of jobs' submission times.      */
DECL|class|HistoryLogsComparator
specifier|private
specifier|static
class|class
name|HistoryLogsComparator
implements|implements
name|Comparator
argument_list|<
name|FileStatus
argument_list|>
implements|,
name|Serializable
block|{
annotation|@
name|Override
DECL|method|compare (FileStatus file1, FileStatus file2)
specifier|public
name|int
name|compare
parameter_list|(
name|FileStatus
name|file1
parameter_list|,
name|FileStatus
name|file2
parameter_list|)
block|{
return|return
name|file1
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|file2
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Processes the input file/folder argument. If the input is a file,      * then it is directly considered for further processing by TraceBuilder.      * If the input is a folder, then all the history logs in the      * input folder are considered for further processing.      *      * If isRecursive is true, then the input path is recursively scanned      * for job history logs for further processing by TraceBuilder.      *      * NOTE: If the input represents a globbed path, then it is first flattened      *       and then the individual paths represented by the globbed input      *       path are considered for further processing.      *      * @param input        input path, possibly globbed      * @param conf         configuration      * @param isRecursive  whether to recursively traverse the input paths to      *                     find history logs      * @return the input history log files' paths      * @throws FileNotFoundException      * @throws IOException      */
DECL|method|processInputArgument (String input, Configuration conf, boolean isRecursive)
specifier|static
name|List
argument_list|<
name|Path
argument_list|>
name|processInputArgument
parameter_list|(
name|String
name|input
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|boolean
name|isRecursive
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|Path
name|inPath
init|=
operator|new
name|Path
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|inPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|inStatuses
init|=
name|fs
operator|.
name|globStatus
argument_list|(
name|inPath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|inputPaths
init|=
operator|new
name|LinkedList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|inStatuses
operator|==
literal|null
operator|||
name|inStatuses
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|inputPaths
return|;
block|}
for|for
control|(
name|FileStatus
name|inStatus
range|:
name|inStatuses
control|)
block|{
name|Path
name|thisPath
init|=
name|inStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|inStatus
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// Find list of files in this path(recursively if -recursive option
comment|// is specified).
name|List
argument_list|<
name|FileStatus
argument_list|>
name|historyLogs
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|iter
init|=
name|fs
operator|.
name|listFiles
argument_list|(
name|thisPath
argument_list|,
name|isRecursive
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|LocatedFileStatus
name|child
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|child
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fileName
operator|.
name|endsWith
argument_list|(
literal|".crc"
argument_list|)
operator|||
name|fileName
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
operator|)
condition|)
block|{
name|historyLogs
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|historyLogs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Add the sorted history log file names in this path to the
comment|// inputPaths list
name|FileStatus
index|[]
name|sortableNames
init|=
name|historyLogs
operator|.
name|toArray
argument_list|(
operator|new
name|FileStatus
index|[
name|historyLogs
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|sortableNames
argument_list|,
operator|new
name|HistoryLogsComparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FileStatus
name|historyLog
range|:
name|sortableNames
control|)
block|{
name|inputPaths
operator|.
name|add
argument_list|(
name|historyLog
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|inputPaths
operator|.
name|add
argument_list|(
name|thisPath
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|inputPaths
return|;
block|}
block|}
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
name|TraceBuilder
name|builder
init|=
operator|new
name|TraceBuilder
argument_list|()
decl_stmt|;
name|int
name|result
init|=
name|RUN_METHOD_FAILED_EXIT_CODE
decl_stmt|;
try|try
block|{
name|result
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|builder
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|System
operator|.
name|exit
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|Exception
block|{
name|MyOptions
name|options
init|=
operator|new
name|MyOptions
argument_list|(
name|args
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|traceWriter
operator|=
name|options
operator|.
name|clazzTraceOutputter
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|traceWriter
operator|.
name|init
argument_list|(
name|options
operator|.
name|traceOutput
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|topologyWriter
operator|=
operator|new
name|DefaultOutputter
argument_list|<
name|LoggedNetworkTopology
argument_list|>
argument_list|()
expr_stmt|;
name|topologyWriter
operator|.
name|init
argument_list|(
name|options
operator|.
name|topologyOutput
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|JobBuilder
name|jobBuilder
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|options
operator|.
name|inputs
control|)
block|{
name|InputDemuxer
name|inputDemuxer
init|=
name|options
operator|.
name|inputDemuxerClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|inputDemuxer
operator|.
name|bindTo
argument_list|(
name|p
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to bind Path "
operator|+
name|p
operator|+
literal|" .  Skipping..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Pair
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
name|filePair
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|filePair
operator|=
name|inputDemuxer
operator|.
name|getNext
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|RewindableInputStream
name|ris
init|=
operator|new
name|RewindableInputStream
argument_list|(
name|filePair
operator|.
name|second
argument_list|()
argument_list|)
decl_stmt|;
name|JobHistoryParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|jobID
init|=
name|JobHistoryUtils
operator|.
name|extractJobID
argument_list|(
name|filePair
operator|.
name|first
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobID
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"File skipped: Invalid file name: "
operator|+
name|filePair
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|(
name|jobBuilder
operator|==
literal|null
operator|)
operator|||
operator|(
operator|!
name|jobBuilder
operator|.
name|getJobID
argument_list|()
operator|.
name|equals
argument_list|(
name|jobID
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
name|jobBuilder
operator|!=
literal|null
condition|)
block|{
name|traceWriter
operator|.
name|output
argument_list|(
name|jobBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|jobBuilder
operator|=
operator|new
name|JobBuilder
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|JobHistoryUtils
operator|.
name|isJobConfXml
argument_list|(
name|filePair
operator|.
name|first
argument_list|()
argument_list|)
condition|)
block|{
name|processJobConf
argument_list|(
name|JobConfigurationParser
operator|.
name|parse
argument_list|(
name|ris
operator|.
name|rewind
argument_list|()
argument_list|)
argument_list|,
name|jobBuilder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
name|JobHistoryParserFactory
operator|.
name|getParser
argument_list|(
name|ris
argument_list|)
expr_stmt|;
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"File skipped: Cannot find suitable parser: "
operator|+
name|filePair
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|processJobHistory
argument_list|(
name|parser
argument_list|,
name|jobBuilder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
name|ris
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
name|parser
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|filePair
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"TraceBuilder got an error while processing the [possibly virtual] file "
operator|+
name|filePair
operator|.
name|first
argument_list|()
operator|+
literal|" within Path "
operator|+
name|p
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|inputDemuxer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|jobBuilder
operator|!=
literal|null
condition|)
block|{
name|traceWriter
operator|.
name|output
argument_list|(
name|jobBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|jobBuilder
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No job found in traces: "
argument_list|)
expr_stmt|;
block|}
name|topologyWriter
operator|.
name|output
argument_list|(
name|topologyBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|traceWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|topologyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|processJobConf (Properties properties, JobBuilder jobBuilder)
specifier|private
name|void
name|processJobConf
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|JobBuilder
name|jobBuilder
parameter_list|)
block|{
name|jobBuilder
operator|.
name|process
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|topologyBuilder
operator|.
name|process
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
DECL|method|processJobHistory (JobHistoryParser parser, JobBuilder jobBuilder)
name|void
name|processJobHistory
parameter_list|(
name|JobHistoryParser
name|parser
parameter_list|,
name|JobBuilder
name|jobBuilder
parameter_list|)
throws|throws
name|IOException
block|{
name|HistoryEvent
name|e
decl_stmt|;
while|while
condition|(
operator|(
name|e
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|jobBuilder
operator|.
name|process
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|topologyBuilder
operator|.
name|process
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|finish ()
name|void
name|finish
parameter_list|()
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|traceWriter
argument_list|,
name|topologyWriter
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

