begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|FSDataOutputStream
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
name|DFSConfigKeys
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
name|protocol
operator|.
name|ClientDatanodeProtocol
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
name|DiskBalancerConstants
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
name|server
operator|.
name|diskbalancer
operator|.
name|planner
operator|.
name|NodePlan
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
name|planner
operator|.
name|Step
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
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|List
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

begin_comment
comment|/**  * Class that implements Plan Command.  *<p>  * Plan command reads the Cluster Info and creates a plan for specified data  * node or a set of Data nodes.  *<p>  * It writes the output to a default location unless changed by the user.  */
end_comment

begin_class
DECL|class|PlanCommand
specifier|public
class|class
name|PlanCommand
extends|extends
name|Command
block|{
DECL|field|thresholdPercentage
specifier|private
name|double
name|thresholdPercentage
decl_stmt|;
DECL|field|bandwidth
specifier|private
name|int
name|bandwidth
decl_stmt|;
DECL|field|maxError
specifier|private
name|int
name|maxError
decl_stmt|;
comment|/**    * Constructs a plan command.    */
DECL|method|PlanCommand (Configuration conf)
specifier|public
name|PlanCommand
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|thresholdPercentage
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|bandwidth
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|maxError
operator|=
literal|0
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|NAMENODEURI
argument_list|,
literal|"Name Node URI or "
operator|+
literal|"file URI for cluster"
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|OUTFILE
argument_list|,
literal|"Output file"
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|BANDWIDTH
argument_list|,
literal|"Maximum Bandwidth to "
operator|+
literal|"be used while copying."
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|THRESHOLD
argument_list|,
literal|"Percentage skew that "
operator|+
literal|"we tolerate before diskbalancer starts working."
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|MAXERROR
argument_list|,
literal|"Max errors to tolerate "
operator|+
literal|"between 2 disks"
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|VERBOSE
argument_list|,
literal|"Run plan command in "
operator|+
literal|"verbose mode."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs the plan command. This command can be run with various options like    *<p>    * -plan -node IP -plan -node hostName -plan -node DatanodeUUID    *    * @param cmd - CommandLine    */
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing Plan Command."
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
name|PLAN
argument_list|)
argument_list|)
expr_stmt|;
name|verifyCommandOptions
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A node name is required to create a"
operator|+
literal|" plan."
argument_list|)
throw|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|BANDWIDTH
argument_list|)
condition|)
block|{
name|this
operator|.
name|bandwidth
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|BANDWIDTH
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|MAXERROR
argument_list|)
condition|)
block|{
name|this
operator|.
name|maxError
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|MAXERROR
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readClusterInfo
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|String
name|output
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|OUTFILE
argument_list|)
condition|)
block|{
name|output
operator|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|OUTFILE
argument_list|)
expr_stmt|;
block|}
name|setOutputPath
argument_list|(
name|output
argument_list|)
expr_stmt|;
comment|// -plan nodename is the command line argument.
name|DiskBalancerDataNode
name|node
init|=
name|getNode
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to find the specified node. "
operator|+
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|thresholdPercentage
operator|=
name|getThresholdPercentage
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"threshold Percentage is {}"
argument_list|,
name|this
operator|.
name|thresholdPercentage
argument_list|)
expr_stmt|;
name|setNodesToProcess
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|populatePathNames
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|NodePlan
name|plan
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|NodePlan
argument_list|>
name|plans
init|=
name|getCluster
argument_list|()
operator|.
name|computePlan
argument_list|(
name|this
operator|.
name|thresholdPercentage
argument_list|)
decl_stmt|;
name|setPlanParams
argument_list|(
name|plans
argument_list|)
expr_stmt|;
if|if
condition|(
name|plans
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|plan
operator|=
name|plans
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|FSDataOutputStream
name|beforeStream
init|=
name|create
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|DiskBalancer
operator|.
name|BEFORE_TEMPLATE
argument_list|,
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|beforeStream
operator|.
name|write
argument_list|(
name|getCluster
argument_list|()
operator|.
name|toJson
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|plan
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Writing plan to : {}"
argument_list|,
name|getOutputPath
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataOutputStream
name|planStream
init|=
name|create
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|DiskBalancer
operator|.
name|PLAN_TEMPLATE
argument_list|,
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|planStream
operator|.
name|write
argument_list|(
name|plan
operator|.
name|toJson
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No plan generated. DiskBalancing not needed for node: {} "
operator|+
literal|"threshold used: {}"
argument_list|,
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|)
argument_list|,
name|this
operator|.
name|thresholdPercentage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|VERBOSE
argument_list|)
operator|&&
name|plans
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|printToScreen
argument_list|(
name|plans
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Reads the Physical path of the disks we are balancing. This is needed to    * make the disk balancer human friendly and not used in balancing.    *    * @param node - Disk Balancer Node.    */
DECL|method|populatePathNames (DiskBalancerDataNode node)
specifier|private
name|void
name|populatePathNames
parameter_list|(
name|DiskBalancerDataNode
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dnAddress
init|=
name|node
operator|.
name|getDataNodeIP
argument_list|()
operator|+
literal|":"
operator|+
name|node
operator|.
name|getDataNodePort
argument_list|()
decl_stmt|;
name|ClientDatanodeProtocol
name|dnClient
init|=
name|getDataNodeProxy
argument_list|(
name|dnAddress
argument_list|)
decl_stmt|;
name|String
name|volumeNameJson
init|=
name|dnClient
operator|.
name|getDiskBalancerSetting
argument_list|(
name|DiskBalancerConstants
operator|.
name|DISKBALANCER_VOLUME_NAME
argument_list|)
decl_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|volumeMap
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|volumeNameJson
argument_list|,
name|HashMap
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|DiskBalancerVolumeSet
name|set
range|:
name|node
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
name|set
operator|.
name|getVolumes
argument_list|()
control|)
block|{
if|if
condition|(
name|volumeMap
operator|.
name|containsKey
argument_list|(
name|vol
operator|.
name|getUuid
argument_list|()
argument_list|)
condition|)
block|{
name|vol
operator|.
name|setPath
argument_list|(
name|volumeMap
operator|.
name|get
argument_list|(
name|vol
operator|.
name|getUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Gets extended help for this command.    */
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
literal|"creates a plan that describes how much data should be "
operator|+
literal|"moved between disks.\n\n"
decl_stmt|;
name|String
name|footer
init|=
literal|"\nPlan command creates a set of steps that represent a "
operator|+
literal|"planned data move. A plan file can be executed on a data node, which"
operator|+
literal|" will balance the data."
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
literal|"hdfs diskbalancer -uri<namenode> -plan "
operator|+
literal|"<hostname> [options]"
argument_list|,
name|header
argument_list|,
name|DiskBalancer
operator|.
name|getPlanOptions
argument_list|()
argument_list|,
name|footer
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get Threshold for planning purpose.    *    * @param cmd - Command Line Argument.    * @return double    */
DECL|method|getThresholdPercentage (CommandLine cmd)
specifier|private
name|double
name|getThresholdPercentage
parameter_list|(
name|CommandLine
name|cmd
parameter_list|)
block|{
name|Double
name|value
init|=
literal|0.0
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|THRESHOLD
argument_list|)
condition|)
block|{
name|value
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|THRESHOLD
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|value
operator|<=
literal|0.0
operator|)
operator|||
operator|(
name|value
operator|>
literal|100.0
operator|)
condition|)
block|{
name|value
operator|=
name|getConf
argument_list|()
operator|.
name|getDouble
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_MAX_DISK_THRUPUT
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_MAX_DISK_THRUPUT_DEFAULT
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|/**    * Prints a quick summary of the plan to screen.    *    * @param plans - List of NodePlans.    */
DECL|method|printToScreen (List<NodePlan> plans)
specifier|static
specifier|private
name|void
name|printToScreen
parameter_list|(
name|List
argument_list|<
name|NodePlan
argument_list|>
name|plans
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nPlan :\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"="
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|center
argument_list|(
literal|"Source Disk"
argument_list|,
literal|30
argument_list|)
operator|+
name|StringUtils
operator|.
name|center
argument_list|(
literal|"Dest.Disk"
argument_list|,
literal|30
argument_list|)
operator|+
name|StringUtils
operator|.
name|center
argument_list|(
literal|"Size"
argument_list|,
literal|10
argument_list|)
operator|+
name|StringUtils
operator|.
name|center
argument_list|(
literal|"Type"
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|NodePlan
name|plan
range|:
name|plans
control|)
block|{
for|for
control|(
name|Step
name|step
range|:
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s %s %s %s"
argument_list|,
name|StringUtils
operator|.
name|center
argument_list|(
name|step
operator|.
name|getSourceVolume
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|30
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|center
argument_list|(
name|step
operator|.
name|getDestinationVolume
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|30
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|center
argument_list|(
name|step
operator|.
name|getSizeString
argument_list|(
name|step
operator|.
name|getBytesToMove
argument_list|()
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|center
argument_list|(
name|step
operator|.
name|getDestinationVolume
argument_list|()
operator|.
name|getStorageType
argument_list|()
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"="
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets user specified plan parameters.    *    * @param plans - list of plans.    */
DECL|method|setPlanParams (List<NodePlan> plans)
specifier|private
name|void
name|setPlanParams
parameter_list|(
name|List
argument_list|<
name|NodePlan
argument_list|>
name|plans
parameter_list|)
block|{
for|for
control|(
name|NodePlan
name|plan
range|:
name|plans
control|)
block|{
for|for
control|(
name|Step
name|step
range|:
name|plan
operator|.
name|getVolumeSetPlans
argument_list|()
control|)
block|{
if|if
condition|(
name|this
operator|.
name|bandwidth
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting bandwidth to {}"
argument_list|,
name|this
operator|.
name|bandwidth
argument_list|)
expr_stmt|;
name|step
operator|.
name|setBandwidth
argument_list|(
name|this
operator|.
name|bandwidth
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|maxError
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting max error to {}"
argument_list|,
name|this
operator|.
name|maxError
argument_list|)
expr_stmt|;
name|step
operator|.
name|setMaxDiskErrors
argument_list|(
name|this
operator|.
name|maxError
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

