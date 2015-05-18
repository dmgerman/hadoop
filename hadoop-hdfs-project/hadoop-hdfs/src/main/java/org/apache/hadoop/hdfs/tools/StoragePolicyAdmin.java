begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BlockStoragePolicy
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
name|HdfsConstants
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
name|HdfsFileStatus
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
comment|/**  * This class implements block storage policy operations.  */
end_comment

begin_class
DECL|class|StoragePolicyAdmin
specifier|public
class|class
name|StoragePolicyAdmin
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|method|main (String[] argsArray)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argsArray
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|StoragePolicyAdmin
name|admin
init|=
operator|new
name|StoragePolicyAdmin
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|admin
operator|.
name|run
argument_list|(
name|argsArray
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|StoragePolicyAdmin (Configuration conf)
specifier|public
name|StoragePolicyAdmin
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
literal|"storagepolicies"
argument_list|,
name|COMMANDS
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
literal|"storagepolicies"
argument_list|,
name|COMMANDS
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
comment|/** Command to list all the existing storage policies */
DECL|class|ListStoragePoliciesCommand
specifier|private
specifier|static
class|class
name|ListStoragePoliciesCommand
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
literal|"List all the existing block storage policies.\n"
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
name|BlockStoragePolicy
argument_list|>
name|policies
init|=
name|dfs
operator|.
name|getAllStoragePolicies
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Block Storage Policies:"
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockStoragePolicy
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
argument_list|)
expr_stmt|;
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
comment|/** Command to get the storage policy of a file/directory */
DECL|class|GetStoragePolicyCommand
specifier|private
specifier|static
class|class
name|GetStoragePolicyCommand
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
literal|"-getStoragePolicy"
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
literal|"The path of the file/directory for getting the storage policy"
argument_list|)
expr_stmt|;
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Get the storage policy of a file/directory.\n\n"
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
literal|"Please specify the path with -path.\nUsage:"
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
name|HdfsFileStatus
name|status
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
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
literal|"File/Directory does not exist: "
operator|+
name|path
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
name|byte
name|storagePolicyId
init|=
name|status
operator|.
name|getStoragePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|storagePolicyId
operator|==
name|HdfsConstants
operator|.
name|BLOCK_STORAGE_POLICY_ID_UNSPECIFIED
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The storage policy of "
operator|+
name|path
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|Collection
argument_list|<
name|BlockStoragePolicy
argument_list|>
name|policies
init|=
name|dfs
operator|.
name|getAllStoragePolicies
argument_list|()
decl_stmt|;
for|for
control|(
name|BlockStoragePolicy
name|p
range|:
name|policies
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getId
argument_list|()
operator|==
name|storagePolicyId
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The storage policy of "
operator|+
name|path
operator|+
literal|":\n"
operator|+
name|p
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Cannot identify the storage policy for "
operator|+
name|path
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
block|}
comment|/** Command to set the storage policy to a file/directory */
DECL|class|SetStoragePolicyCommand
specifier|private
specifier|static
class|class
name|SetStoragePolicyCommand
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
literal|"-setStoragePolicy"
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
literal|"The path of the file/directory to set storage"
operator|+
literal|" policy"
argument_list|)
expr_stmt|;
name|listing
operator|.
name|addRow
argument_list|(
literal|"<policy>"
argument_list|,
literal|"The name of the block storage policy"
argument_list|)
expr_stmt|;
return|return
name|getShortUsage
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Set the storage policy to a file/directory.\n\n"
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
literal|"Please specify the path for setting the storage "
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
name|policyName
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
name|policyName
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
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
name|policyName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Set storage policy "
operator|+
name|policyName
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
name|ListStoragePoliciesCommand
argument_list|()
block|,
operator|new
name|SetStoragePolicyCommand
argument_list|()
block|,
operator|new
name|GetStoragePolicyCommand
argument_list|()
block|}
decl_stmt|;
block|}
end_class

end_unit

