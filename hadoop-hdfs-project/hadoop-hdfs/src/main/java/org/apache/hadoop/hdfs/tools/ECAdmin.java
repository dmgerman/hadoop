begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|DistributedFileSystem
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
name|ErasureCodingPolicy
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
name|tools
operator|.
name|TableListing
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * CLI for the erasure code encoding operations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ECAdmin
specifier|public
class|class
name|ECAdmin
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"ec"
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
specifier|final
name|ECAdmin
name|admin
init|=
operator|new
name|ECAdmin
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|admin
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|ECAdmin (Configuration conf)
specifier|public
name|ECAdmin
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
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|AdminHelper
operator|.
name|printUsage
argument_list|(
literal|false
argument_list|,
name|NAME
argument_list|,
name|COMMANDS
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|AdminHelper
operator|.
name|Command
name|command
init|=
name|AdminHelper
operator|.
name|determineCommand
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|COMMANDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand command '"
operator|+
name|args
index|[
literal|0
index|]
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Command names must start with dashes."
argument_list|)
expr_stmt|;
block|}
name|AdminHelper
operator|.
name|printUsage
argument_list|(
literal|false
argument_list|,
name|NAME
argument_list|,
name|COMMANDS
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|argsList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|argsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|args
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|command
operator|.
name|run
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|argsList
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|AdminHelper
operator|.
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/** Command to list the set of enabled erasure coding policies. */
DECL|class|ListECPoliciesCommand
specifier|private
specifier|static
class|class
name|ListECPoliciesCommand
implements|implements
name|AdminHelper
operator|.
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-listPolicies"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|getName
argument_list|()
operator|+
literal|"]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Get the list of enabled erasure coding policies.\n"
operator|+
literal|"Policies can be enabled on the NameNode via `"
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_KEY
operator|+
literal|"`.\n"
return|;
block|}
annotation|@
name|Override
DECL|method|run (Configuration conf, List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|getName
argument_list|()
operator|+
literal|": Too many arguments"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|AdminHelper
operator|.
name|getDFS
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|Collection
argument_list|<
name|ErasureCodingPolicy
argument_list|>
name|policies
init|=
name|dfs
operator|.
name|getAllErasureCodingPolicies
argument_list|()
decl_stmt|;
if|if
condition|(
name|policies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No erasure coding policies are enabled on the "
operator|+
literal|"cluster."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The set of enabled policies can be "
operator|+
literal|"configured at '"
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_KEY
operator|+
literal|"' on the "
operator|+
literal|"NameNode."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Erasure Coding Policies:"
argument_list|)
expr_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|policy
range|:
name|policies
control|)
block|{
if|if
condition|(
name|policy
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\t"
operator|+
name|policy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|AdminHelper
operator|.
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
comment|/** Command to get the erasure coding policy for a file or directory */
DECL|class|GetECPolicyCommand
specifier|private
specifier|static
class|class
name|GetECPolicyCommand
implements|implements
name|AdminHelper
operator|.
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-getPolicy"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|getName
argument_list|()
operator|+
literal|" -path<path>]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
specifier|final
name|TableListing
name|listing
init|=
name|AdminHelper
operator|.
name|getOptionDescriptionListing
argument_list|()
decl_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<path>"
argument_list|,
literal|"The path of the file/directory for getting the erasure coding "
operator|+
literal|"policy"
argument_list|)
expr_stmt|;
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Get the erasure coding policy of a file/directory.\n\n"
operator|+
name|listing
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run (Configuration conf, List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-path"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Please specify the path with -path.\nUsage: "
operator|+
name|getLongUsage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|getName
argument_list|()
operator|+
literal|": Too many arguments"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|AdminHelper
operator|.
name|getDFS
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|dfs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicy
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The erasure coding policy of "
operator|+
name|path
operator|+
literal|" is "
operator|+
literal|"unspecified"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|AdminHelper
operator|.
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
comment|/** Command to set the erasure coding policy to a file/directory */
DECL|class|SetECPolicyCommand
specifier|private
specifier|static
class|class
name|SetECPolicyCommand
implements|implements
name|AdminHelper
operator|.
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-setPolicy"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|getName
argument_list|()
operator|+
literal|" -path<path> -policy<policy>]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
name|TableListing
name|listing
init|=
name|AdminHelper
operator|.
name|getOptionDescriptionListing
argument_list|()
decl_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<path>"
argument_list|,
literal|"The path of the file/directory to set "
operator|+
literal|"the erasure coding policy"
argument_list|)
expr_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<policy>"
argument_list|,
literal|"The name of the erasure coding policy"
argument_list|)
expr_stmt|;
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Set the erasure coding policy for a file/directory.\n\n"
operator|+
name|listing
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run (Configuration conf, List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-path"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Please specify the path for setting the EC "
operator|+
literal|"policy.\nUsage: "
operator|+
name|getLongUsage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|String
name|ecPolicyName
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-policy"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicyName
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Please specify the policy name.\nUsage: "
operator|+
name|getLongUsage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|getName
argument_list|()
operator|+
literal|": Too many arguments"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|AdminHelper
operator|.
name|getDFS
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|dfs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|p
argument_list|,
name|ecPolicyName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Set erasure coding policy "
operator|+
name|ecPolicyName
operator|+
literal|" on "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|AdminHelper
operator|.
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
comment|/** Command to unset the erasure coding policy set for a file/directory */
DECL|class|UnsetECPolicyCommand
specifier|private
specifier|static
class|class
name|UnsetECPolicyCommand
implements|implements
name|AdminHelper
operator|.
name|Command
block|{
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"-unsetPolicy"
return|;
block|}
annotation|@
name|Override
DECL|method|getShortUsage ()
specifier|public
name|String
name|getShortUsage
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|getName
argument_list|()
operator|+
literal|" -path<path>]\n"
return|;
block|}
annotation|@
name|Override
DECL|method|getLongUsage ()
specifier|public
name|String
name|getLongUsage
parameter_list|()
block|{
name|TableListing
name|listing
init|=
name|AdminHelper
operator|.
name|getOptionDescriptionListing
argument_list|()
decl_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<path>"
argument_list|,
literal|"The path of the directory "
operator|+
literal|"from which the erasure coding policy will be unset."
argument_list|)
expr_stmt|;
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Unset the erasure coding policy for a directory.\n\n"
operator|+
name|listing
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run (Configuration conf, List<String> args)
specifier|public
name|int
name|run
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|path
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-path"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Please specify a path.\nUsage: "
operator|+
name|getLongUsage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|getName
argument_list|()
operator|+
literal|": Too many arguments"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|AdminHelper
operator|.
name|getDFS
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|dfs
operator|.
name|unsetErasureCodingPolicy
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unset erasure coding policy from "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|AdminHelper
operator|.
name|prettifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|field|COMMANDS
specifier|private
specifier|static
specifier|final
name|AdminHelper
operator|.
name|Command
index|[]
name|COMMANDS
init|=
block|{
operator|new
name|ListECPoliciesCommand
argument_list|()
block|,
operator|new
name|GetECPolicyCommand
argument_list|()
block|,
operator|new
name|SetECPolicyCommand
argument_list|()
block|,
operator|new
name|UnsetECPolicyCommand
argument_list|()
block|}
decl_stmt|;
block|}
end_class

end_unit

