begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|DiskBalancerException
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
name|tools
operator|.
name|DiskBalancerCLI
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

begin_comment
comment|/**  * executes a given plan.  */
end_comment

begin_class
DECL|class|ExecuteCommand
specifier|public
class|class
name|ExecuteCommand
extends|extends
name|Command
block|{
comment|/**    * Constructs ExecuteCommand.    *    * @param conf - Configuration.    */
DECL|method|ExecuteCommand (Configuration conf)
specifier|public
name|ExecuteCommand
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
name|addValidCommandParameters
argument_list|(
name|DiskBalancerCLI
operator|.
name|EXECUTE
argument_list|,
literal|"Executes a given plan."
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
name|DiskBalancerCLI
operator|.
name|SKIPDATECHECK
argument_list|,
literal|"skips the date check and force execute the plan"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Executes the Client Calls.    *    * @param cmd - CommandLine    */
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
name|info
argument_list|(
literal|"Executing \"execute plan\" command"
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
name|DiskBalancerCLI
operator|.
name|EXECUTE
argument_list|)
argument_list|)
expr_stmt|;
name|verifyCommandOptions
argument_list|(
name|DiskBalancerCLI
operator|.
name|EXECUTE
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|String
name|planFile
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancerCLI
operator|.
name|EXECUTE
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|planFile
operator|!=
literal|null
operator|&&
operator|!
name|planFile
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Invalid plan file specified."
argument_list|)
expr_stmt|;
name|String
name|planData
init|=
literal|null
decl_stmt|;
try|try
init|(
name|FSDataInputStream
name|plan
init|=
name|open
argument_list|(
name|planFile
argument_list|)
init|)
block|{
name|planData
operator|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
name|boolean
name|skipDateCheck
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|DiskBalancerCLI
operator|.
name|SKIPDATECHECK
argument_list|)
condition|)
block|{
name|skipDateCheck
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Skipping date check on this plan. This could mean we are "
operator|+
literal|"executing an old plan and may not be the right plan for this "
operator|+
literal|"data node."
argument_list|)
expr_stmt|;
block|}
name|submitPlan
argument_list|(
name|planFile
argument_list|,
name|planData
argument_list|,
name|skipDateCheck
argument_list|)
expr_stmt|;
block|}
comment|/**    * Submits plan to a given data node.    *    * @param planFile - Plan file name    * @param planData - Plan data in json format    * @param skipDateCheck - skips date check    * @throws IOException    */
DECL|method|submitPlan (final String planFile, final String planData, boolean skipDateCheck)
specifier|private
name|void
name|submitPlan
parameter_list|(
specifier|final
name|String
name|planFile
parameter_list|,
specifier|final
name|String
name|planData
parameter_list|,
name|boolean
name|skipDateCheck
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|planData
argument_list|)
expr_stmt|;
name|NodePlan
name|plan
init|=
name|NodePlan
operator|.
name|parseJson
argument_list|(
name|planData
argument_list|)
decl_stmt|;
name|String
name|dataNodeAddress
init|=
name|plan
operator|.
name|getNodeName
argument_list|()
operator|+
literal|":"
operator|+
name|plan
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dataNodeAddress
argument_list|)
expr_stmt|;
name|ClientDatanodeProtocol
name|dataNode
init|=
name|getDataNodeProxy
argument_list|(
name|dataNodeAddress
argument_list|)
decl_stmt|;
name|String
name|planHash
init|=
name|DigestUtils
operator|.
name|shaHex
argument_list|(
name|planData
argument_list|)
decl_stmt|;
try|try
block|{
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|DiskBalancerCLI
operator|.
name|PLAN_VERSION
argument_list|,
name|planFile
argument_list|,
name|planData
argument_list|,
name|skipDateCheck
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskBalancerException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Submitting plan on  {} failed. Result: {}, Message: {}"
argument_list|,
name|plan
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
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
literal|"Execute command runs a submits a plan for execution on "
operator|+
literal|"the given data node.\n\n"
decl_stmt|;
name|String
name|footer
init|=
literal|"\nExecute command submits the job to data node and "
operator|+
literal|"returns immediately. The state of job can be monitored via query "
operator|+
literal|"command. "
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
literal|"hdfs diskbalancer -execute<planfile>"
argument_list|,
name|header
argument_list|,
name|DiskBalancerCLI
operator|.
name|getExecuteOptions
argument_list|()
argument_list|,
name|footer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

