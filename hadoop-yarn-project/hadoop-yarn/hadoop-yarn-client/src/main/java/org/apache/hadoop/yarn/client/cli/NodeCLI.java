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
name|Date
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
literal|"%16s\t%10s\t%17s\t%18s"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
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
literal|"Lists all the nodes in the RUNNING state."
argument_list|)
expr_stmt|;
name|CommandLine
name|cliParser
init|=
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
decl_stmt|;
name|int
name|exitCode
init|=
operator|-
literal|1
decl_stmt|;
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
name|listClusterNodes
argument_list|()
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
comment|/**    * Lists all the nodes present in the cluster    *     * @throws YarnException    * @throws IOException    */
DECL|method|listClusterNodes ()
specifier|private
name|void
name|listClusterNodes
parameter_list|()
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
name|NodeState
operator|.
name|RUNNING
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
literal|"Running-Containers"
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
literal|"0M"
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
literal|"M"
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

