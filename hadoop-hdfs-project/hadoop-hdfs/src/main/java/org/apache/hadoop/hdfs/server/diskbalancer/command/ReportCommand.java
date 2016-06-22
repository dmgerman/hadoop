begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.command
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ListIterator
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
name|text
operator|.
name|StrBuilder
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
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
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
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerVolume
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
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerVolumeSet
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
name|hdfs
operator|.
name|tools
operator|.
name|DiskBalancer
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
name|base
operator|.
name|Preconditions
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * Executes the report command.  *  * This command will report volume information for a specific DataNode or top X  * DataNode(s) benefiting from running DiskBalancer.  *  * This is done by reading the cluster info, sorting the DiskbalancerNodes by  * their NodeDataDensity and printing out the info.  */
end_comment

begin_class
DECL|class|ReportCommand
specifier|public
class|class
name|ReportCommand
extends|extends
name|Command
block|{
DECL|field|out
specifier|private
name|PrintStream
name|out
decl_stmt|;
DECL|method|ReportCommand (Configuration conf, final PrintStream out)
specifier|public
name|ReportCommand
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|PrintStream
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|REPORT
argument_list|,
literal|"Report volume information of nodes."
argument_list|)
expr_stmt|;
name|String
name|desc
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Top number of nodes to be processed. Default: %d"
argument_list|,
name|getDefaultTop
argument_list|()
argument_list|)
decl_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|TOP
argument_list|,
name|desc
argument_list|)
expr_stmt|;
name|desc
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Print out volume information for a DataNode."
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|NODE
argument_list|,
name|desc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute (CommandLine cmd)
specifier|public
name|void
name|execute
parameter_list|(
name|CommandLine
name|cmd
parameter_list|)
throws|throws
name|Exception
block|{
name|StrBuilder
name|result
init|=
operator|new
name|StrBuilder
argument_list|()
decl_stmt|;
name|String
name|outputLine
init|=
literal|"Processing report command"
decl_stmt|;
name|recordOutput
argument_list|(
name|result
argument_list|,
name|outputLine
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|REPORT
argument_list|)
argument_list|)
expr_stmt|;
name|verifyCommandOptions
argument_list|(
name|DiskBalancer
operator|.
name|REPORT
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|readClusterInfo
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nodeFormat
init|=
literal|"%d/%d %s[%s:%d] -<%s>: %d volumes with node data density %.2f."
decl_stmt|;
specifier|final
name|String
name|nodeFormatWithoutSequence
init|=
literal|"%s[%s:%d] -<%s>: %d volumes with node data density %.2f."
decl_stmt|;
specifier|final
name|String
name|volumeFormat
init|=
literal|"[%s: volume-%s] - %.2f used: %d/%d, %.2f free: %d/%d, "
operator|+
literal|"isFailed: %s, isReadOnly: %s, isSkip: %s, isTransient: %s."
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|NODE
argument_list|)
condition|)
block|{
comment|/*        * Reporting volume information for a specific DataNode        */
name|handleNodeReport
argument_list|(
name|cmd
argument_list|,
name|result
argument_list|,
name|nodeFormatWithoutSequence
argument_list|,
name|volumeFormat
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// handle TOP
comment|/*        * Reporting volume information for top X DataNode(s)        */
name|handleTopReport
argument_list|(
name|cmd
argument_list|,
name|result
argument_list|,
name|nodeFormat
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|handleTopReport (final CommandLine cmd, final StrBuilder result, final String nodeFormat)
specifier|private
name|void
name|handleTopReport
parameter_list|(
specifier|final
name|CommandLine
name|cmd
parameter_list|,
specifier|final
name|StrBuilder
name|result
parameter_list|,
specifier|final
name|String
name|nodeFormat
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|getCluster
argument_list|()
operator|.
name|getNodes
argument_list|()
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
comment|/* extract value that identifies top X DataNode(s) */
name|setTopNodes
argument_list|(
name|parseTopNodes
argument_list|(
name|cmd
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * Reporting volume information of top X DataNode(s) in summary      */
specifier|final
name|String
name|outputLine
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Reporting top %d DataNode(s) benefiting from running DiskBalancer."
argument_list|,
name|getTopNodes
argument_list|()
argument_list|)
decl_stmt|;
name|recordOutput
argument_list|(
name|result
argument_list|,
name|outputLine
argument_list|)
expr_stmt|;
name|ListIterator
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|li
init|=
name|getCluster
argument_list|()
operator|.
name|getNodes
argument_list|()
operator|.
name|listIterator
argument_list|()
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
name|getTopNodes
argument_list|()
operator|&&
name|li
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DiskBalancerDataNode
name|dbdn
init|=
name|li
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|.
name|appendln
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|nodeFormat
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|getTopNodes
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getDataNodeName
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getDataNodeIP
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getDataNodePort
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getDataNodeUUID
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getVolumeCount
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getNodeDataDensity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleNodeReport (final CommandLine cmd, StrBuilder result, final String nodeFormat, final String volumeFormat)
specifier|private
name|void
name|handleNodeReport
parameter_list|(
specifier|final
name|CommandLine
name|cmd
parameter_list|,
name|StrBuilder
name|result
parameter_list|,
specifier|final
name|String
name|nodeFormat
parameter_list|,
specifier|final
name|String
name|volumeFormat
parameter_list|)
block|{
name|String
name|outputLine
init|=
literal|""
decl_stmt|;
comment|/*      * get value that identifies a DataNode from command line, it could be UUID,      * IP address or host name.      */
specifier|final
name|String
name|nodeVal
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|NODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|nodeVal
argument_list|)
condition|)
block|{
name|outputLine
operator|=
literal|"The value for '-node' is neither specified or empty."
expr_stmt|;
name|recordOutput
argument_list|(
name|result
argument_list|,
name|outputLine
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/*        * Reporting volume information for a specific DataNode        */
name|outputLine
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Reporting volume information for DataNode '%s'."
argument_list|,
name|nodeVal
argument_list|)
expr_stmt|;
name|recordOutput
argument_list|(
name|result
argument_list|,
name|outputLine
argument_list|)
expr_stmt|;
specifier|final
name|String
name|trueStr
init|=
literal|"True"
decl_stmt|;
specifier|final
name|String
name|falseStr
init|=
literal|"False"
decl_stmt|;
name|DiskBalancerDataNode
name|dbdn
init|=
name|getNode
argument_list|(
name|nodeVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbdn
operator|==
literal|null
condition|)
block|{
name|outputLine
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Can't find a DataNode that matches '%s'."
argument_list|,
name|nodeVal
argument_list|)
expr_stmt|;
name|recordOutput
argument_list|(
name|result
argument_list|,
name|outputLine
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|appendln
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|nodeFormat
argument_list|,
name|dbdn
operator|.
name|getDataNodeName
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getDataNodeIP
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getDataNodePort
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getDataNodeUUID
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getVolumeCount
argument_list|()
argument_list|,
name|dbdn
operator|.
name|getNodeDataDensity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|volumeList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|DiskBalancerVolumeSet
name|vset
range|:
name|dbdn
operator|.
name|getVolumeSets
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|DiskBalancerVolume
name|vol
range|:
name|vset
operator|.
name|getVolumes
argument_list|()
control|)
block|{
name|volumeList
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|volumeFormat
argument_list|,
name|vol
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|vol
operator|.
name|getPath
argument_list|()
argument_list|,
name|vol
operator|.
name|getUsedRatio
argument_list|()
argument_list|,
name|vol
operator|.
name|getUsed
argument_list|()
argument_list|,
name|vol
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|vol
operator|.
name|getFreeRatio
argument_list|()
argument_list|,
name|vol
operator|.
name|getFreeSpace
argument_list|()
argument_list|,
name|vol
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|vol
operator|.
name|isFailed
argument_list|()
condition|?
name|trueStr
else|:
name|falseStr
argument_list|,
name|vol
operator|.
name|isReadOnly
argument_list|()
condition|?
name|trueStr
else|:
name|falseStr
argument_list|,
name|vol
operator|.
name|isSkip
argument_list|()
condition|?
name|trueStr
else|:
name|falseStr
argument_list|,
name|vol
operator|.
name|isTransient
argument_list|()
condition|?
name|trueStr
else|:
name|falseStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|volumeList
argument_list|)
expr_stmt|;
name|result
operator|.
name|appendln
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
name|volumeList
operator|.
name|toArray
argument_list|()
argument_list|,
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Prints the help message.    */
annotation|@
name|Override
DECL|method|printHelp ()
specifier|public
name|void
name|printHelp
parameter_list|()
block|{
name|String
name|header
init|=
literal|"Report command reports the volume information of a given"
operator|+
literal|" datanode, or prints out the list of nodes that will benefit from "
operator|+
literal|"running disk balancer. Top defaults to "
operator|+
name|getDefaultTop
argument_list|()
decl_stmt|;
name|String
name|footer
init|=
literal|". E.g.:\n"
operator|+
literal|"hdfs diskbalancer -fs http://namenode.uri -report\n"
operator|+
literal|"hdfs diskbalancer -fs http://namenode.uri -report -top 5\n"
operator|+
literal|"hdfs diskbalancer -fs http://namenode.uri -report "
operator|+
literal|"-node {DataNodeID | IP | Hostname}"
decl_stmt|;
name|HelpFormatter
name|helpFormatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|helpFormatter
operator|.
name|printHelp
argument_list|(
literal|"hdfs diskbalancer -fs http://namenode.uri "
operator|+
literal|"-report [options]"
argument_list|,
name|header
argument_list|,
name|DiskBalancer
operator|.
name|getReportOptions
argument_list|()
argument_list|,
name|footer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

