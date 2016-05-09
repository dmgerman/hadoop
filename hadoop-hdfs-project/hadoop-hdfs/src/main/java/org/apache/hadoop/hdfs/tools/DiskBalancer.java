begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

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
name|BasicParser
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|command
operator|.
name|Command
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
name|command
operator|.
name|PlanCommand
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
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * DiskBalancer is a tool that can be used to ensure that data is spread evenly  * across volumes of same storage type.  *<p>  * For example, if you have 3 disks, with 100 GB , 600 GB and 200 GB on each  * disk, this tool will ensure that each disk will have 300 GB.  *<p>  * This tool can be run while data nodes are fully functional.  *<p>  * At very high level diskbalancer computes a set of moves that will make disk  * utilization equal and then those moves are executed by the datanode.  */
end_comment

begin_class
DECL|class|DiskBalancer
specifier|public
class|class
name|DiskBalancer
extends|extends
name|Configured
implements|implements
name|Tool
block|{
comment|/**    * Construct a DiskBalancer.    *    * @param conf    */
DECL|method|DiskBalancer (Configuration conf)
specifier|public
name|DiskBalancer
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
block|}
comment|/**    * NameNodeURI can point to either a real namenode, or a json file that    * contains the diskBalancer data in json form, that jsonNodeConnector knows    * how to deserialize.    *<p>    * Expected formats are :    *<p>    * hdfs://namenode.uri or file:///data/myCluster.json    */
DECL|field|NAMENODEURI
specifier|public
specifier|static
specifier|final
name|String
name|NAMENODEURI
init|=
literal|"uri"
decl_stmt|;
comment|/**    * Computes a plan for a given set of nodes.    */
DECL|field|PLAN
specifier|public
specifier|static
specifier|final
name|String
name|PLAN
init|=
literal|"plan"
decl_stmt|;
comment|/**    * Output file name, for commands like report, plan etc. This is an optional    * argument, by default diskbalancer will write all its output to    * /system/reports/diskbalancer of the current cluster it is operating    * against.    */
DECL|field|OUTFILE
specifier|public
specifier|static
specifier|final
name|String
name|OUTFILE
init|=
literal|"out"
decl_stmt|;
comment|/**    * Help for the program.    */
DECL|field|HELP
specifier|public
specifier|static
specifier|final
name|String
name|HELP
init|=
literal|"help"
decl_stmt|;
comment|/**    * Percentage of data unevenness that we are willing to live with. For example    * - a value like 10 indicates that we are okay with 10 % +/- from    * idealStorage Target.    */
DECL|field|THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|THRESHOLD
init|=
literal|"thresholdPercentage"
decl_stmt|;
comment|/**    * Specifies the maximum disk bandwidth to use per second.    */
DECL|field|BANDWIDTH
specifier|public
specifier|static
specifier|final
name|String
name|BANDWIDTH
init|=
literal|"bandwidth"
decl_stmt|;
comment|/**    * Specifies the maximum errors to tolerate.    */
DECL|field|MAXERROR
specifier|public
specifier|static
specifier|final
name|String
name|MAXERROR
init|=
literal|"maxerror"
decl_stmt|;
comment|/**    * Node name or IP against which Disk Balancer is being run.    */
DECL|field|NODE
specifier|public
specifier|static
specifier|final
name|String
name|NODE
init|=
literal|"node"
decl_stmt|;
comment|/**    * Runs the command in verbose mode.    */
DECL|field|VERBOSE
specifier|public
specifier|static
specifier|final
name|String
name|VERBOSE
init|=
literal|"v"
decl_stmt|;
comment|/**    * Template for the Before File. It is node.before.json.    */
DECL|field|BEFORE_TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|BEFORE_TEMPLATE
init|=
literal|"%s.before.json"
decl_stmt|;
comment|/**    * Template for the plan file. it is node.plan.json.    */
DECL|field|PLAN_TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|PLAN_TEMPLATE
init|=
literal|"%s.plan.json"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DiskBalancer
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Main for the  DiskBalancer Command handling.    *    * @param argv - System Args Strings[]    * @throws Exception    */
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|DiskBalancer
name|shell
init|=
operator|new
name|DiskBalancer
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|res
init|=
literal|0
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
comment|/**    * Execute the command with the given arguments.    *    * @param args command specific arguments.    * @return exit code.    * @throws Exception    */
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
name|getOpts
argument_list|()
decl_stmt|;
name|CommandLine
name|cmd
init|=
name|parseArgs
argument_list|(
name|args
argument_list|,
name|opts
argument_list|)
decl_stmt|;
return|return
name|dispatch
argument_list|(
name|cmd
argument_list|,
name|opts
argument_list|)
return|;
block|}
comment|/**    * returns the Command Line Options.    *    * @return Options    */
DECL|method|getOpts ()
specifier|private
name|Options
name|getOpts
parameter_list|()
block|{
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|addCommands
argument_list|(
name|opts
argument_list|)
expr_stmt|;
return|return
name|opts
return|;
block|}
comment|/**    * Adds commands that we handle to opts.    *    * @param opt - Optins    */
DECL|method|addCommands (Options opt)
specifier|private
name|void
name|addCommands
parameter_list|(
name|Options
name|opt
parameter_list|)
block|{
name|Option
name|nameNodeUri
init|=
operator|new
name|Option
argument_list|(
name|NAMENODEURI
argument_list|,
literal|true
argument_list|,
literal|"NameNode URI. e.g http://namenode"
operator|+
literal|".mycluster.com or file:///myCluster"
operator|+
literal|".json"
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|nameNodeUri
argument_list|)
expr_stmt|;
name|Option
name|outFile
init|=
operator|new
name|Option
argument_list|(
name|OUTFILE
argument_list|,
literal|true
argument_list|,
literal|"File to write output to, if not specified "
operator|+
literal|"defaults will be used."
operator|+
literal|"e.g -out outfile.txt"
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|outFile
argument_list|)
expr_stmt|;
name|Option
name|plan
init|=
operator|new
name|Option
argument_list|(
name|PLAN
argument_list|,
literal|false
argument_list|,
literal|"write plan to the default file"
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|Option
name|bandwidth
init|=
operator|new
name|Option
argument_list|(
name|BANDWIDTH
argument_list|,
literal|true
argument_list|,
literal|"Maximum disk bandwidth to"
operator|+
literal|" be consumed by diskBalancer. "
operator|+
literal|"Expressed as MBs per second."
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|bandwidth
argument_list|)
expr_stmt|;
name|Option
name|threshold
init|=
operator|new
name|Option
argument_list|(
name|THRESHOLD
argument_list|,
literal|true
argument_list|,
literal|"Percentage skew that we "
operator|+
literal|"tolerate before diskbalancer starts working or stops when reaching "
operator|+
literal|"that range."
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|threshold
argument_list|)
expr_stmt|;
name|Option
name|maxErrors
init|=
operator|new
name|Option
argument_list|(
name|MAXERROR
argument_list|,
literal|true
argument_list|,
literal|"Describes how many errors "
operator|+
literal|"can be tolerated while copying between a pair of disks."
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|maxErrors
argument_list|)
expr_stmt|;
name|Option
name|node
init|=
operator|new
name|Option
argument_list|(
name|NODE
argument_list|,
literal|true
argument_list|,
literal|"Node Name or IP"
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|Option
name|help
init|=
operator|new
name|Option
argument_list|(
name|HELP
argument_list|,
literal|true
argument_list|,
literal|"Help about a command or this message"
argument_list|)
decl_stmt|;
name|opt
operator|.
name|addOption
argument_list|(
name|help
argument_list|)
expr_stmt|;
block|}
comment|/**    * This function parses all command line arguments and returns the appropriate    * values.    *    * @param argv - Argv from main    * @return CommandLine    */
DECL|method|parseArgs (String[] argv, Options opts)
specifier|private
name|CommandLine
name|parseArgs
parameter_list|(
name|String
index|[]
name|argv
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
block|{
name|BasicParser
name|parser
init|=
operator|new
name|BasicParser
argument_list|()
decl_stmt|;
return|return
name|parser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|argv
argument_list|)
return|;
block|}
comment|/**    * Dispatches calls to the right command Handler classes.    *    * @param cmd - CommandLine    * @throws IOException    * @throws URISyntaxException    */
DECL|method|dispatch (CommandLine cmd, Options opts)
specifier|private
name|int
name|dispatch
parameter_list|(
name|CommandLine
name|cmd
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Command
name|currentCommand
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancer
operator|.
name|PLAN
argument_list|)
condition|)
block|{
name|currentCommand
operator|=
operator|new
name|PlanCommand
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
literal|80
argument_list|,
literal|"hdfs diskbalancer -uri [args]"
argument_list|,
literal|"disk balancer commands"
argument_list|,
name|opts
argument_list|,
literal|"Please correct your command and try again."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|currentCommand
operator|.
name|execute
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|printf
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

