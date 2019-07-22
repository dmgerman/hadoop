begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
package|;
end_package

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
name|Collections
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|HadoopIllegalArgumentException
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
name|permission
operator|.
name|AclEntry
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
name|permission
operator|.
name|AclEntryScope
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
name|permission
operator|.
name|AclEntryType
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
name|permission
operator|.
name|AclStatus
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
name|permission
operator|.
name|AclUtil
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
name|permission
operator|.
name|FsAction
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
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|ScopedAclEntries
import|;
end_import

begin_comment
comment|/**  * Acl related operations  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AclCommands
class|class
name|AclCommands
extends|extends
name|FsCommand
block|{
DECL|field|GET_FACL
specifier|private
specifier|static
name|String
name|GET_FACL
init|=
literal|"getfacl"
decl_stmt|;
DECL|field|SET_FACL
specifier|private
specifier|static
name|String
name|SET_FACL
init|=
literal|"setfacl"
decl_stmt|;
DECL|method|registerCommands (CommandFactory factory)
specifier|public
specifier|static
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|addClass
argument_list|(
name|GetfaclCommand
operator|.
name|class
argument_list|,
literal|"-"
operator|+
name|GET_FACL
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|SetfaclCommand
operator|.
name|class
argument_list|,
literal|"-"
operator|+
name|SET_FACL
argument_list|)
expr_stmt|;
block|}
comment|/**    * Implementing the '-getfacl' command for the the FsShell.    */
DECL|class|GetfaclCommand
specifier|public
specifier|static
class|class
name|GetfaclCommand
extends|extends
name|FsCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
name|GET_FACL
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
name|String
name|USAGE
init|=
literal|"[-R]<path>"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
name|String
name|DESCRIPTION
init|=
literal|"Displays the Access Control Lists"
operator|+
literal|" (ACLs) of files and directories. If a directory has a default ACL,"
operator|+
literal|" then getfacl also displays the default ACL.\n"
operator|+
literal|"  -R: List the ACLs of all files and directories recursively.\n"
operator|+
literal|"<path>: File or directory to list.\n"
decl_stmt|;
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"R"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setRecursive
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"R"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"<path> is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Too many arguments"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|println
argument_list|(
literal|"# file: "
operator|+
name|item
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"# owner: "
operator|+
name|item
operator|.
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"# group: "
operator|+
name|item
operator|.
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|FsPermission
name|perm
init|=
name|item
operator|.
name|stat
operator|.
name|getPermission
argument_list|()
decl_stmt|;
if|if
condition|(
name|perm
operator|.
name|getStickyBit
argument_list|()
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"# flags: --"
operator|+
operator|(
name|perm
operator|.
name|getOtherAction
argument_list|()
operator|.
name|implies
argument_list|(
name|FsAction
operator|.
name|EXECUTE
argument_list|)
condition|?
literal|"t"
else|:
literal|"T"
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AclStatus
name|aclStatus
decl_stmt|;
specifier|final
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|stat
operator|.
name|hasAcl
argument_list|()
condition|)
block|{
name|aclStatus
operator|=
name|item
operator|.
name|fs
operator|.
name|getAclStatus
argument_list|(
name|item
operator|.
name|path
argument_list|)
expr_stmt|;
name|entries
operator|=
name|aclStatus
operator|.
name|getEntries
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aclStatus
operator|=
literal|null
expr_stmt|;
name|entries
operator|=
name|Collections
operator|.
expr|<
name|AclEntry
operator|>
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|ScopedAclEntries
name|scopedEntries
init|=
operator|new
name|ScopedAclEntries
argument_list|(
name|AclUtil
operator|.
name|getAclFromPermAndEntries
argument_list|(
name|perm
argument_list|,
name|entries
argument_list|)
argument_list|)
decl_stmt|;
name|printAclEntriesForSingleScope
argument_list|(
name|aclStatus
argument_list|,
name|perm
argument_list|,
name|scopedEntries
operator|.
name|getAccessEntries
argument_list|()
argument_list|)
expr_stmt|;
name|printAclEntriesForSingleScope
argument_list|(
name|aclStatus
argument_list|,
name|perm
argument_list|,
name|scopedEntries
operator|.
name|getDefaultEntries
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
comment|/**      * Prints all the ACL entries in a single scope.      * @param aclStatus AclStatus for the path      * @param fsPerm FsPermission for the path      * @param entries List<AclEntry> containing ACL entries of file      */
DECL|method|printAclEntriesForSingleScope (AclStatus aclStatus, FsPermission fsPerm, List<AclEntry> entries)
specifier|private
name|void
name|printAclEntriesForSingleScope
parameter_list|(
name|AclStatus
name|aclStatus
parameter_list|,
name|FsPermission
name|fsPerm
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
parameter_list|)
block|{
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|AclUtil
operator|.
name|isMinimalAcl
argument_list|(
name|entries
argument_list|)
condition|)
block|{
for|for
control|(
name|AclEntry
name|entry
range|:
name|entries
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|entry
operator|.
name|toStringStable
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|AclEntry
name|entry
range|:
name|entries
control|)
block|{
name|printExtendedAclEntry
argument_list|(
name|aclStatus
argument_list|,
name|fsPerm
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Prints a single extended ACL entry.  If the mask restricts the      * permissions of the entry, then also prints the restricted version as the      * effective permissions.  The mask applies to all named entries and also      * the unnamed group entry.      * @param aclStatus AclStatus for the path      * @param fsPerm FsPermission for the path      * @param entry AclEntry extended ACL entry to print      */
DECL|method|printExtendedAclEntry (AclStatus aclStatus, FsPermission fsPerm, AclEntry entry)
specifier|private
name|void
name|printExtendedAclEntry
parameter_list|(
name|AclStatus
name|aclStatus
parameter_list|,
name|FsPermission
name|fsPerm
parameter_list|,
name|AclEntry
name|entry
parameter_list|)
block|{
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|||
name|entry
operator|.
name|getType
argument_list|()
operator|==
name|AclEntryType
operator|.
name|GROUP
condition|)
block|{
name|FsAction
name|entryPerm
init|=
name|entry
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|FsAction
name|effectivePerm
init|=
name|aclStatus
operator|.
name|getEffectivePermission
argument_list|(
name|entry
argument_list|,
name|fsPerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|entryPerm
operator|!=
name|effectivePerm
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s\t#effective:%s"
argument_list|,
name|entry
argument_list|,
name|effectivePerm
operator|.
name|SYMBOL
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
name|entry
operator|.
name|toStringStable
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
name|entry
operator|.
name|toStringStable
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Implementing the '-setfacl' command for the the FsShell.    */
DECL|class|SetfaclCommand
specifier|public
specifier|static
class|class
name|SetfaclCommand
extends|extends
name|FsCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
name|SET_FACL
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
name|String
name|USAGE
init|=
literal|"[-R] [{-b|-k} {-m|-x<acl_spec>}<path>]"
operator|+
literal|"|[--set<acl_spec><path>]"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
name|String
name|DESCRIPTION
init|=
literal|"Sets Access Control Lists (ACLs)"
operator|+
literal|" of files and directories.\n"
operator|+
literal|"Options:\n"
operator|+
literal|"  -b :Remove all but the base ACL entries. The entries for user,"
operator|+
literal|" group and others are retained for compatibility with permission "
operator|+
literal|"bits.\n"
operator|+
literal|"  -k :Remove the default ACL.\n"
operator|+
literal|"  -R :Apply operations to all files and directories recursively.\n"
operator|+
literal|"  -m :Modify ACL. New entries are added to the ACL, and existing"
operator|+
literal|" entries are retained.\n"
operator|+
literal|"  -x :Remove specified ACL entries. Other ACL entries are retained.\n"
operator|+
literal|"  --set :Fully replace the ACL, discarding all existing entries."
operator|+
literal|" The<acl_spec> must include entries for user, group, and others"
operator|+
literal|" for compatibility with permission bits. If the ACL spec contains"
operator|+
literal|" only access entries, then the existing default entries are retained"
operator|+
literal|". If the ACL spec contains only default entries, then the existing"
operator|+
literal|" access entries are retained. If the ACL spec contains both access"
operator|+
literal|" and default entries, then both are replaced.\n"
operator|+
literal|"<acl_spec>: Comma separated list of ACL entries.\n"
operator|+
literal|"<path>: File or directory to modify.\n"
decl_stmt|;
DECL|field|cf
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"b"
argument_list|,
literal|"k"
argument_list|,
literal|"R"
argument_list|,
literal|"m"
argument_list|,
literal|"x"
argument_list|,
literal|"-set"
argument_list|)
decl_stmt|;
DECL|field|aclEntries
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
init|=
literal|null
decl_stmt|;
DECL|field|accessAclEntries
name|List
argument_list|<
name|AclEntry
argument_list|>
name|accessAclEntries
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setRecursive
argument_list|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"R"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Mix of remove and modify acl flags are not allowed
name|boolean
name|bothRemoveOptions
init|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"b"
argument_list|)
operator|&&
name|cf
operator|.
name|getOpt
argument_list|(
literal|"k"
argument_list|)
decl_stmt|;
name|boolean
name|bothModifyOptions
init|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"m"
argument_list|)
operator|&&
name|cf
operator|.
name|getOpt
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|boolean
name|oneRemoveOption
init|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"b"
argument_list|)
operator|||
name|cf
operator|.
name|getOpt
argument_list|(
literal|"k"
argument_list|)
decl_stmt|;
name|boolean
name|oneModifyOption
init|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"m"
argument_list|)
operator|||
name|cf
operator|.
name|getOpt
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|boolean
name|setOption
init|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"-set"
argument_list|)
decl_stmt|;
name|boolean
name|hasExpectedOptions
init|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"b"
argument_list|)
operator|||
name|cf
operator|.
name|getOpt
argument_list|(
literal|"k"
argument_list|)
operator|||
name|cf
operator|.
name|getOpt
argument_list|(
literal|"m"
argument_list|)
operator|||
name|cf
operator|.
name|getOpt
argument_list|(
literal|"x"
argument_list|)
operator|||
name|cf
operator|.
name|getOpt
argument_list|(
literal|"-set"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|bothRemoveOptions
operator|||
name|bothModifyOptions
operator|)
operator|||
operator|(
name|oneRemoveOption
operator|&&
name|oneModifyOption
operator|)
operator|||
operator|(
name|setOption
operator|&&
operator|(
name|oneRemoveOption
operator|||
name|oneModifyOption
operator|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Specified flags contains both remove and modify flags"
argument_list|)
throw|;
block|}
comment|// Only -m, -x and --set expects<acl_spec>
if|if
condition|(
name|oneModifyOption
operator|||
name|setOption
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Missing arguments:<acl_spec><path>"
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Missing either<acl_spec> or<path>"
argument_list|)
throw|;
block|}
name|aclEntries
operator|=
name|AclEntry
operator|.
name|parseAclSpec
argument_list|(
name|args
operator|.
name|removeFirst
argument_list|()
argument_list|,
operator|!
name|cf
operator|.
name|getOpt
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|aclEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Missing<acl_spec> entry"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"<path> is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Too many arguments"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|hasExpectedOptions
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Expected one of -b, -k, -m, -x or --set options"
argument_list|)
throw|;
block|}
comment|// In recursive mode, save a separate list of just the access ACL entries.
comment|// Only directories may have a default ACL.  When a recursive operation
comment|// encounters a file under the specified path, it must pass only the
comment|// access ACL entries.
if|if
condition|(
name|isRecursive
argument_list|()
operator|&&
operator|(
name|oneModifyOption
operator|||
name|setOption
operator|)
condition|)
block|{
name|accessAclEntries
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
for|for
control|(
name|AclEntry
name|entry
range|:
name|aclEntries
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getScope
argument_list|()
operator|==
name|AclEntryScope
operator|.
name|ACCESS
condition|)
block|{
name|accessAclEntries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|processPath (PathData item)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"b"
argument_list|)
condition|)
block|{
name|item
operator|.
name|fs
operator|.
name|removeAcl
argument_list|(
name|item
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"k"
argument_list|)
condition|)
block|{
name|item
operator|.
name|fs
operator|.
name|removeDefaultAcl
argument_list|(
name|item
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"m"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
init|=
name|getAclEntries
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|item
operator|.
name|fs
operator|.
name|modifyAclEntries
argument_list|(
name|item
operator|.
name|path
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"x"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
init|=
name|getAclEntries
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|item
operator|.
name|fs
operator|.
name|removeAclEntries
argument_list|(
name|item
operator|.
name|path
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|cf
operator|.
name|getOpt
argument_list|(
literal|"-set"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
init|=
name|getAclEntries
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|item
operator|.
name|fs
operator|.
name|setAcl
argument_list|(
name|item
operator|.
name|path
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Returns the ACL entries to use in the API call for the given path.  For a      * recursive operation, returns all specified ACL entries if the item is a      * directory or just the access ACL entries if the item is a file.  For a      * non-recursive operation, returns all specified ACL entries.      *      * @param item PathData path to check      * @return List<AclEntry> ACL entries to use in the API call      */
DECL|method|getAclEntries (PathData item)
specifier|private
name|List
argument_list|<
name|AclEntry
argument_list|>
name|getAclEntries
parameter_list|(
name|PathData
name|item
parameter_list|)
block|{
if|if
condition|(
name|isRecursive
argument_list|()
condition|)
block|{
return|return
name|item
operator|.
name|stat
operator|.
name|isDirectory
argument_list|()
condition|?
name|aclEntries
else|:
name|accessAclEntries
return|;
block|}
else|else
block|{
return|return
name|aclEntries
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

