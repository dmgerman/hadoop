begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.cli
package|package
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
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|PrintWriter
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|List
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
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|MissingArgumentException
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
name|cli
operator|.
name|Option
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
name|cli
operator|.
name|Options
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
name|lang
operator|.
name|StringUtils
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
name|lang
operator|.
name|time
operator|.
name|DateFormatUtils
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
name|util
operator|.
name|ToolRunner
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
name|NodeId
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
name|NodeReport
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
name|NodeState
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
name|ConverterUtils
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|NodeCLI
specifier|public
class|class
name|NodeCLI
extends|extends
name|YarnCLI
block|{
DECL|field|NODES_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|NODES_PATTERN
init|=
literal|"%16s\t%15s\t%17s\t%28s"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|NODE_STATE_CMD
specifier|private
specifier|static
specifier|final
name|String
name|NODE_STATE_CMD
init|=
literal|"states"
decl_stmt|;
DECL|field|NODE_ALL
specifier|private
specifier|static
specifier|final
name|String
name|NODE_ALL
init|=
literal|"all"
decl_stmt|;
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
throws|throws
name|Exception
block|{
name|NodeCLI
name|cli
init|=
operator|new
name|NodeCLI
argument_list|()
decl_stmt|;
name|cli
operator|.
name|setSysOutPrintStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|cli
operator|.
name|setSysErrPrintStream
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|cli
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|cli
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
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
name|Exception
block|{
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|STATUS_CMD
argument_list|,
literal|true
argument_list|,
literal|"Prints the status report of the node."
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|LIST_CMD
argument_list|,
literal|false
argument_list|,
literal|"List all running nodes. "
operator|+
literal|"Supports optional use of -states to filter nodes "
operator|+
literal|"based on node state, all -all to list all nodes."
argument_list|)
expr_stmt|;
name|Option
name|nodeStateOpt
init|=
operator|new
name|Option
argument_list|(
name|NODE_STATE_CMD
argument_list|,
literal|true
argument_list|,
literal|"Works with -list to filter nodes based on input comma-separated list of node states."
argument_list|)
decl_stmt|;
name|nodeStateOpt
operator|.
name|setValueSeparator
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|nodeStateOpt
operator|.
name|setArgs
argument_list|(
name|Option
operator|.
name|UNLIMITED_VALUES
argument_list|)
expr_stmt|;
name|nodeStateOpt
operator|.
name|setArgName
argument_list|(
literal|"States"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|nodeStateOpt
argument_list|)
expr_stmt|;
name|Option
name|allOpt
init|=
operator|new
name|Option
argument_list|(
name|NODE_ALL
argument_list|,
literal|false
argument_list|,
literal|"Works with -list to list all nodes."
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|allOpt
argument_list|)
expr_stmt|;
name|opts
operator|.
name|getOption
argument_list|(
name|STATUS_CMD
argument_list|)
operator|.
name|setArgName
argument_list|(
literal|"NodeId"
argument_list|)
expr_stmt|;
name|int
name|exitCode
init|=
operator|-
literal|1
decl_stmt|;
name|CommandLine
name|cliParser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cliParser
operator|=
operator|new
name|GnuParser
argument_list|()
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MissingArgumentException
name|ex
parameter_list|)
block|{
name|sysout
operator|.
name|println
argument_list|(
literal|"Missing argument for options"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
return|return
name|exitCode
return|;
block|}
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
literal|"status"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
return|return
name|exitCode
return|;
block|}
name|printNodeStatus
argument_list|(
name|cliParser
operator|.
name|getOptionValue
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
literal|"list"
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|NodeState
argument_list|>
name|nodeStates
init|=
operator|new
name|HashSet
argument_list|<
name|NodeState
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
name|NODE_ALL
argument_list|)
condition|)
block|{
for|for
control|(
name|NodeState
name|state
range|:
name|NodeState
operator|.
name|values
argument_list|()
control|)
block|{
name|nodeStates
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|cliParser
operator|.
name|hasOption
argument_list|(
name|NODE_STATE_CMD
argument_list|)
condition|)
block|{
name|String
index|[]
name|types
init|=
name|cliParser
operator|.
name|getOptionValues
argument_list|(
name|NODE_STATE_CMD
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
if|if
condition|(
operator|!
name|type
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|nodeStates
operator|.
name|add
argument_list|(
name|NodeState
operator|.
name|valueOf
argument_list|(
name|type
operator|.
name|trim
argument_list|()
operator|.
name|toUpperCase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|nodeStates
operator|.
name|add
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
block|}
name|listClusterNodes
argument_list|(
name|nodeStates
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|syserr
operator|.
name|println
argument_list|(
literal|"Invalid Command Usage : "
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * It prints the usage of the command    *     * @param opts    */
DECL|method|printUsage (Options opts)
specifier|private
name|void
name|printUsage
parameter_list|(
name|Options
name|opts
parameter_list|)
block|{
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"node"
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
comment|/**    * Lists the nodes matching the given node states    *     * @param nodeStates    * @throws YarnException    * @throws IOException    */
DECL|method|listClusterNodes (Set<NodeState> nodeStates)
specifier|private
name|void
name|listClusterNodes
parameter_list|(
name|Set
argument_list|<
name|NodeState
argument_list|>
name|nodeStates
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|sysout
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodesReport
init|=
name|client
operator|.
name|getNodeReports
argument_list|(
name|nodeStates
operator|.
name|toArray
argument_list|(
operator|new
name|NodeState
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Total Nodes:"
operator|+
name|nodesReport
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|printf
argument_list|(
name|NODES_PATTERN
argument_list|,
literal|"Node-Id"
argument_list|,
literal|"Node-State"
argument_list|,
literal|"Node-Http-Address"
argument_list|,
literal|"Number-of-Running-Containers"
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeReport
name|nodeReport
range|:
name|nodesReport
control|)
block|{
name|writer
operator|.
name|printf
argument_list|(
name|NODES_PATTERN
argument_list|,
name|nodeReport
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nodeReport
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|nodeReport
operator|.
name|getHttpAddress
argument_list|()
argument_list|,
name|nodeReport
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Prints the node report for node id.    *     * @param nodeIdStr    * @throws YarnException    */
DECL|method|printNodeStatus (String nodeIdStr)
specifier|private
name|void
name|printNodeStatus
parameter_list|(
name|String
name|nodeIdStr
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|NodeId
name|nodeId
init|=
name|ConverterUtils
operator|.
name|toNodeId
argument_list|(
name|nodeIdStr
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodesReport
init|=
name|client
operator|.
name|getNodeReports
argument_list|()
decl_stmt|;
comment|// Use PrintWriter.println, which uses correct platform line ending.
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|nodeReportStr
init|=
operator|new
name|PrintWriter
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|NodeReport
name|nodeReport
init|=
literal|null
decl_stmt|;
for|for
control|(
name|NodeReport
name|report
range|:
name|nodesReport
control|)
block|{
if|if
condition|(
operator|!
name|report
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|nodeReport
operator|=
name|report
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
literal|"Node Report : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tNode-Id : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tRack : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tNode-State : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tNode-Http-Address : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getHttpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tLast-Health-Update : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|DateFormatUtils
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|nodeReport
operator|.
name|getLastHealthReportTime
argument_list|()
argument_list|)
argument_list|,
literal|"E dd/MMM/yy hh:mm:ss:SSzz"
argument_list|)
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tHealth-Report : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getHealthReport
argument_list|()
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tContainers : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tMemory-Used : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
operator|(
name|nodeReport
operator|.
name|getUsed
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|"0MB"
else|:
operator|(
name|nodeReport
operator|.
name|getUsed
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|+
literal|"MB"
operator|)
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tMemory-Capacity : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|+
literal|"MB"
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tCPU-Used : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
operator|(
name|nodeReport
operator|.
name|getUsed
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|"0 vcores"
else|:
operator|(
name|nodeReport
operator|.
name|getUsed
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|+
literal|" vcores"
operator|)
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tCPU-Capacity : "
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|nodeReport
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|+
literal|" vcores"
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"\tNode-Labels : "
argument_list|)
expr_stmt|;
comment|// Create a List for node labels since we need it get sorted
name|List
argument_list|<
name|String
argument_list|>
name|nodeLabelsList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|report
operator|.
name|getNodeLabels
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|nodeLabelsList
argument_list|)
expr_stmt|;
name|nodeReportStr
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
name|nodeLabelsList
operator|.
name|iterator
argument_list|()
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeReport
operator|==
literal|null
condition|)
block|{
name|nodeReportStr
operator|.
name|print
argument_list|(
literal|"Could not find the node report for node id : "
operator|+
name|nodeIdStr
argument_list|)
expr_stmt|;
block|}
name|nodeReportStr
operator|.
name|close
argument_list|()
expr_stmt|;
name|sysout
operator|.
name|println
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

