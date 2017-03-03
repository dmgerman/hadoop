begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|EnumSet
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
name|fs
operator|.
name|XAttr
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
name|XAttrSetFlag
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
name|hdfs
operator|.
name|XAttrHelper
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSDirectory
operator|.
name|DirOp
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
name|io
operator|.
name|WritableUtils
import|;
end_import

begin_import
import|import static
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|XATTR_ERASURECODING_POLICY
import|;
end_import

begin_comment
comment|/**  * Helper class to perform erasure coding related operations.  */
end_comment

begin_class
DECL|class|FSDirErasureCodingOp
specifier|final
class|class
name|FSDirErasureCodingOp
block|{
comment|/**    * Private constructor for preventing FSDirErasureCodingOp object    * creation. Static-only class.    */
DECL|method|FSDirErasureCodingOp ()
specifier|private
name|FSDirErasureCodingOp
parameter_list|()
block|{}
comment|/**    * Set an erasure coding policy on the given path.    *    * @param fsn The namespace    * @param srcArg The path of the target directory.    * @param ecPolicyName The erasure coding policy name to set on the target    *                    directory.    * @param logRetryCache whether to record RPC ids in editlog for retry    *          cache rebuilding    * @return {@link HdfsFileStatus}    * @throws IOException    * @throws HadoopIllegalArgumentException if the policy is not enabled    */
DECL|method|setErasureCodingPolicy (final FSNamesystem fsn, final String srcArg, final String ecPolicyName, final boolean logRetryCache)
specifier|static
name|HdfsFileStatus
name|setErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
specifier|final
name|String
name|ecPolicyName
parameter_list|,
specifier|final
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|String
name|src
init|=
name|srcArg
decl_stmt|;
name|FSPermissionChecker
name|pc
init|=
literal|null
decl_stmt|;
name|pc
operator|=
name|fsn
operator|.
name|getPermissionChecker
argument_list|()
expr_stmt|;
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
specifier|final
name|INodesInPath
name|iip
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getPolicyByName
argument_list|(
name|ecPolicyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecPolicy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Policy '"
operator|+
name|ecPolicyName
operator|+
literal|"' does not match any supported erasure coding "
operator|+
literal|"policies."
argument_list|)
throw|;
block|}
name|iip
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|DirOp
operator|.
name|WRITE_LINK
argument_list|)
expr_stmt|;
name|src
operator|=
name|iip
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|xAttrs
operator|=
name|setErasureCodingPolicyXAttr
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|,
name|ecPolicy
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetXAttrs
argument_list|(
name|src
argument_list|,
name|xAttrs
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|iip
argument_list|)
return|;
block|}
DECL|method|setErasureCodingPolicyXAttr (final FSNamesystem fsn, final INodesInPath srcIIP, ErasureCodingPolicy ecPolicy)
specifier|static
name|List
argument_list|<
name|XAttr
argument_list|>
name|setErasureCodingPolicyXAttr
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|INodesInPath
name|srcIIP
parameter_list|,
name|ErasureCodingPolicy
name|ecPolicy
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
assert|assert
name|fsd
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|srcIIP
argument_list|,
literal|"INodes cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ecPolicy
argument_list|,
literal|"EC policy cannot be null"
argument_list|)
expr_stmt|;
name|String
name|src
init|=
name|srcIIP
operator|.
name|getPath
argument_list|()
decl_stmt|;
specifier|final
name|INode
name|inode
init|=
name|srcIIP
operator|.
name|getLastINode
argument_list|()
decl_stmt|;
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Path not found: "
operator|+
name|srcIIP
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|inode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attempt to set an erasure coding policy "
operator|+
literal|"for a file "
operator|+
name|src
argument_list|)
throw|;
block|}
comment|// Check that the EC policy is one of the active policies.
name|boolean
name|validPolicy
init|=
literal|false
decl_stmt|;
name|ErasureCodingPolicy
index|[]
name|activePolicies
init|=
name|FSDirErasureCodingOp
operator|.
name|getErasureCodingPolicies
argument_list|(
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|activePolicy
range|:
name|activePolicies
control|)
block|{
if|if
condition|(
name|activePolicy
operator|.
name|equals
argument_list|(
name|ecPolicy
argument_list|)
condition|)
block|{
name|validPolicy
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|validPolicy
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ecPolicyNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ErasureCodingPolicy
name|activePolicy
range|:
name|activePolicies
control|)
block|{
name|ecPolicyNames
operator|.
name|add
argument_list|(
name|activePolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Policy [ "
operator|+
name|ecPolicy
operator|.
name|getName
argument_list|()
operator|+
literal|" ] does not match any of the "
operator|+
literal|"supported policies. Please select any one of "
operator|+
name|ecPolicyNames
argument_list|)
throw|;
block|}
specifier|final
name|XAttr
name|ecXAttr
decl_stmt|;
name|DataOutputStream
name|dOut
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|bOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|dOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|bOut
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|dOut
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ecXAttr
operator|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|XATTR_ERASURECODING_POLICY
argument_list|,
name|bOut
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|dOut
argument_list|)
expr_stmt|;
block|}
comment|// check whether the directory already has an erasure coding policy
comment|// directly on itself.
specifier|final
name|Boolean
name|hasEcXAttr
init|=
name|getErasureCodingPolicyXAttrForINode
argument_list|(
name|fsn
argument_list|,
name|inode
argument_list|)
operator|==
literal|null
condition|?
literal|false
else|:
literal|true
decl_stmt|;
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xattrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|xattrs
operator|.
name|add
argument_list|(
name|ecXAttr
argument_list|)
expr_stmt|;
specifier|final
name|EnumSet
argument_list|<
name|XAttrSetFlag
argument_list|>
name|flag
init|=
name|hasEcXAttr
condition|?
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|REPLACE
argument_list|)
else|:
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|FSDirXAttrOp
operator|.
name|unprotectedSetXAttrs
argument_list|(
name|fsd
argument_list|,
name|srcIIP
argument_list|,
name|xattrs
argument_list|,
name|flag
argument_list|)
expr_stmt|;
return|return
name|xattrs
return|;
block|}
comment|/**    * Unset erasure coding policy from the given directory.    *    * @param fsn The namespace    * @param srcArg The path of the target directory.    * @param logRetryCache whether to record RPC ids in editlog for retry    *          cache rebuilding    * @return {@link HdfsFileStatus}    * @throws IOException    */
DECL|method|unsetErasureCodingPolicy (final FSNamesystem fsn, final String srcArg, final boolean logRetryCache)
specifier|static
name|HdfsFileStatus
name|unsetErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
specifier|final
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|String
name|src
init|=
name|srcArg
decl_stmt|;
name|FSPermissionChecker
name|pc
init|=
name|fsn
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
specifier|final
name|INodesInPath
name|iip
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|iip
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|src
argument_list|,
name|DirOp
operator|.
name|WRITE_LINK
argument_list|)
expr_stmt|;
name|src
operator|=
name|iip
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|xAttrs
operator|=
name|removeErasureCodingPolicyXAttr
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|xAttrs
operator|!=
literal|null
condition|)
block|{
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logRemoveXAttrs
argument_list|(
name|src
argument_list|,
name|xAttrs
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|iip
argument_list|)
return|;
block|}
DECL|method|removeErasureCodingPolicyXAttr ( final FSNamesystem fsn, final INodesInPath srcIIP)
specifier|private
specifier|static
name|List
argument_list|<
name|XAttr
argument_list|>
name|removeErasureCodingPolicyXAttr
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|INodesInPath
name|srcIIP
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
assert|assert
name|fsd
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|srcIIP
argument_list|,
literal|"INodes cannot be null"
argument_list|)
expr_stmt|;
name|String
name|src
init|=
name|srcIIP
operator|.
name|getPath
argument_list|()
decl_stmt|;
specifier|final
name|INode
name|inode
init|=
name|srcIIP
operator|.
name|getLastINode
argument_list|()
decl_stmt|;
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Path not found: "
operator|+
name|srcIIP
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|inode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot unset an erasure coding policy "
operator|+
literal|"on a file "
operator|+
name|src
argument_list|)
throw|;
block|}
comment|// Check whether the directory has a specific erasure coding policy
comment|// directly on itself.
specifier|final
name|XAttr
name|ecXAttr
init|=
name|getErasureCodingPolicyXAttrForINode
argument_list|(
name|fsn
argument_list|,
name|inode
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecXAttr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xattrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|xattrs
operator|.
name|add
argument_list|(
name|ecXAttr
argument_list|)
expr_stmt|;
name|FSDirXAttrOp
operator|.
name|unprotectedRemoveXAttrs
argument_list|(
name|fsd
argument_list|,
name|srcIIP
operator|.
name|getPath
argument_list|()
argument_list|,
name|xattrs
argument_list|)
expr_stmt|;
return|return
name|xattrs
return|;
block|}
comment|/**    * Get the erasure coding policy information for specified path.    *    * @param fsn namespace    * @param src path    * @return {@link ErasureCodingPolicy}    * @throws IOException    * @throws FileNotFoundException if the path does not exist.    */
DECL|method|getErasureCodingPolicy (final FSNamesystem fsn, final String src)
specifier|static
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasReadLock
argument_list|()
assert|;
specifier|final
name|INodesInPath
name|iip
init|=
name|getINodesInPath
argument_list|(
name|fsn
argument_list|,
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|iip
operator|.
name|getLastINode
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Path not found: "
operator|+
name|iip
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|getErasureCodingPolicyForPath
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|)
return|;
block|}
comment|/**    * Check if the file or directory has an erasure coding policy.    *    * @param fsn namespace    * @param srcArg path    * @return Whether the file or directory has an erasure coding policy.    * @throws IOException    */
DECL|method|hasErasureCodingPolicy (final FSNamesystem fsn, final String srcArg)
specifier|static
name|boolean
name|hasErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasErasureCodingPolicy
argument_list|(
name|fsn
argument_list|,
name|getINodesInPath
argument_list|(
name|fsn
argument_list|,
name|srcArg
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Check if the file or directory has an erasure coding policy.    *    * @param fsn namespace    * @param iip inodes in the path containing the file    * @return Whether the file or directory has an erasure coding policy.    * @throws IOException    */
DECL|method|hasErasureCodingPolicy (final FSNamesystem fsn, final INodesInPath iip)
specifier|static
name|boolean
name|hasErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getErasureCodingPolicy
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Get the erasure coding policy.    *    * @param fsn namespace    * @param iip inodes in the path containing the file    * @return {@link ErasureCodingPolicy}    * @throws IOException    */
DECL|method|getErasureCodingPolicy (final FSNamesystem fsn, final INodesInPath iip)
specifier|static
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasReadLock
argument_list|()
assert|;
return|return
name|getErasureCodingPolicyForPath
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|)
return|;
block|}
comment|/**    * Get available erasure coding polices.    *    * @param fsn namespace    * @return {@link ErasureCodingPolicy} array    */
DECL|method|getErasureCodingPolicies (final FSNamesystem fsn)
specifier|static
name|ErasureCodingPolicy
index|[]
name|getErasureCodingPolicies
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasReadLock
argument_list|()
assert|;
return|return
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getPolicies
argument_list|()
return|;
block|}
DECL|method|getINodesInPath (final FSNamesystem fsn, final String srcArg)
specifier|private
specifier|static
name|INodesInPath
name|getINodesInPath
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
specifier|final
name|FSPermissionChecker
name|pc
init|=
name|fsn
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|srcArg
argument_list|,
name|DirOp
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsn
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
return|return
name|iip
return|;
block|}
DECL|method|getErasureCodingPolicyForPath (FSNamesystem fsn, INodesInPath iip)
specifier|private
specifier|static
name|ErasureCodingPolicy
name|getErasureCodingPolicyForPath
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|iip
argument_list|,
literal|"INodes cannot be null"
argument_list|)
expr_stmt|;
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|INode
argument_list|>
name|inodes
init|=
name|iip
operator|.
name|getReadOnlyINodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|inodes
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|INode
name|inode
init|=
name|inodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|inode
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|byte
name|id
init|=
name|inode
operator|.
name|asFile
argument_list|()
operator|.
name|getErasureCodingPolicyID
argument_list|()
decl_stmt|;
return|return
name|id
operator|<
literal|0
condition|?
literal|null
else|:
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getPolicyByID
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|// We don't allow setting EC policies on paths with a symlink. Thus
comment|// if a symlink is encountered, the dir shouldn't have EC policy.
comment|// TODO: properly support symlinks
if|if
condition|(
name|inode
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|XAttrFeature
name|xaf
init|=
name|inode
operator|.
name|getXAttrFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|xaf
operator|!=
literal|null
condition|)
block|{
name|XAttr
name|xattr
init|=
name|xaf
operator|.
name|getXAttr
argument_list|(
name|XATTR_ERASURECODING_POLICY
argument_list|)
decl_stmt|;
if|if
condition|(
name|xattr
operator|!=
literal|null
condition|)
block|{
name|ByteArrayInputStream
name|bIn
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|xattr
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|dIn
init|=
operator|new
name|DataInputStream
argument_list|(
name|bIn
argument_list|)
decl_stmt|;
name|String
name|ecPolicyName
init|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|dIn
argument_list|)
decl_stmt|;
return|return
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getPolicyByName
argument_list|(
name|ecPolicyName
argument_list|)
return|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getErasureCodingPolicyXAttrForINode ( FSNamesystem fsn, INode inode)
specifier|private
specifier|static
name|XAttr
name|getErasureCodingPolicyXAttrForINode
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INode
name|inode
parameter_list|)
throws|throws
name|IOException
block|{
comment|// INode can be null
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// We don't allow setting EC policies on paths with a symlink. Thus
comment|// if a symlink is encountered, the dir shouldn't have EC policy.
comment|// TODO: properly support symlinks
if|if
condition|(
name|inode
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|XAttrFeature
name|xaf
init|=
name|inode
operator|.
name|getXAttrFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|xaf
operator|!=
literal|null
condition|)
block|{
name|XAttr
name|xattr
init|=
name|xaf
operator|.
name|getXAttr
argument_list|(
name|XATTR_ERASURECODING_POLICY
argument_list|)
decl_stmt|;
if|if
condition|(
name|xattr
operator|!=
literal|null
condition|)
block|{
return|return
name|xattr
return|;
block|}
block|}
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

