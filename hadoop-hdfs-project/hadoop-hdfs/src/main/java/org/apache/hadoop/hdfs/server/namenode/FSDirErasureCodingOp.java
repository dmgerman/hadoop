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
name|commons
operator|.
name|lang3
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
name|FileStatus
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
name|ErasureCodingPolicyInfo
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|CodecRegistry
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
name|security
operator|.
name|AccessControlException
import|;
end_import

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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
comment|/**    * Check if the ecPolicyName is valid and enabled, return the corresponding    * EC policy if is, including the REPLICATION EC policy.    * @param fsn namespace    * @param ecPolicyName name of EC policy to be checked    * @return an erasure coding policy if ecPolicyName is valid and enabled    * @throws IOException    */
DECL|method|getErasureCodingPolicyByName ( final FSNamesystem fsn, final String ecPolicyName)
specifier|static
name|ErasureCodingPolicy
name|getErasureCodingPolicyByName
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|ecPolicyName
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
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getEnabledPolicyByName
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
specifier|final
name|String
name|sysPolicies
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getEnabledPolicies
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ErasureCodingPolicy
operator|::
name|getName
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|", "
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Policy '%s' does not match any "
operator|+
literal|"enabled erasure"
operator|+
literal|" coding policies: [%s]. An erasure coding policy can be"
operator|+
literal|" enabled by enableErasureCodingPolicy API."
argument_list|,
name|ecPolicyName
argument_list|,
name|sysPolicies
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
name|ecPolicy
return|;
block|}
comment|/**    * Set an erasure coding policy on the given path.    *    * @param fsn The namespace    * @param srcArg The path of the target directory.    * @param ecPolicyName The erasure coding policy name to set on the target    *                    directory.    * @param logRetryCache whether to record RPC ids in editlog for retry    *          cache rebuilding    * @return {@link FileStatus}    * @throws IOException    * @throws HadoopIllegalArgumentException if the policy is not enabled    * @throws AccessControlException if the user does not have write access    */
DECL|method|setErasureCodingPolicy (final FSNamesystem fsn, final String srcArg, final String ecPolicyName, final FSPermissionChecker pc, final boolean logRetryCache)
specifier|static
name|FileStatus
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
name|FSPermissionChecker
name|pc
parameter_list|,
specifier|final
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
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
name|getErasureCodingPolicyByName
argument_list|(
name|fsn
argument_list|,
name|ecPolicyName
argument_list|)
decl_stmt|;
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
comment|// Write access is required to set erasure coding policy
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|,
name|FsAction
operator|.
name|WRITE
argument_list|)
expr_stmt|;
block|}
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
specifier|private
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
comment|/**    * Unset erasure coding policy from the given directory.    *    * @param fsn The namespace    * @param srcArg The path of the target directory.    * @param logRetryCache whether to record RPC ids in editlog for retry    *          cache rebuilding    * @return {@link FileStatus}    * @throws IOException    * @throws AccessControlException if the user does not have write access    */
DECL|method|unsetErasureCodingPolicy (final FSNamesystem fsn, final String srcArg, final FSPermissionChecker pc, final boolean logRetryCache)
specifier|static
name|FileStatus
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
name|FSPermissionChecker
name|pc
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
comment|// Write access is required to unset erasure coding policy
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|,
name|FsAction
operator|.
name|WRITE
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Add an erasure coding policy.    *    * @param fsn namespace    * @param policy the new policy to be added into system    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding    * @throws IOException    */
DECL|method|addErasureCodingPolicy (final FSNamesystem fsn, ErasureCodingPolicy policy, final boolean logRetryCache)
specifier|static
name|ErasureCodingPolicy
name|addErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
name|ErasureCodingPolicy
name|policy
parameter_list|,
specifier|final
name|boolean
name|logRetryCache
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|ErasureCodingPolicy
name|retPolicy
init|=
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|addPolicy
argument_list|(
name|policy
argument_list|)
decl_stmt|;
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logAddErasureCodingPolicy
argument_list|(
name|policy
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
return|return
name|retPolicy
return|;
block|}
comment|/**    * Remove an erasure coding policy.    *    * @param fsn namespace    * @param ecPolicyName the name of the policy to be removed    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding    * @throws IOException    */
DECL|method|removeErasureCodingPolicy (final FSNamesystem fsn, String ecPolicyName, final boolean logRetryCache)
specifier|static
name|void
name|removeErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ecPolicyName
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|removePolicy
argument_list|(
name|ecPolicyName
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logRemoveErasureCodingPolicy
argument_list|(
name|ecPolicyName
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enable an erasure coding policy.    *    * @param fsn namespace    * @param ecPolicyName the name of the policy to be enabled    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding    * @throws IOException    */
DECL|method|enableErasureCodingPolicy (final FSNamesystem fsn, String ecPolicyName, final boolean logRetryCache)
specifier|static
name|boolean
name|enableErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ecPolicyName
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|enablePolicy
argument_list|(
name|ecPolicyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logEnableErasureCodingPolicy
argument_list|(
name|ecPolicyName
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
comment|/**    * Disable an erasure coding policy.    *    * @param fsn namespace    * @param ecPolicyName the name of the policy to be disabled    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding    * @throws IOException    */
DECL|method|disableErasureCodingPolicy (final FSNamesystem fsn, String ecPolicyName, final boolean logRetryCache)
specifier|static
name|boolean
name|disableErasureCodingPolicy
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ecPolicyName
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|disablePolicy
argument_list|(
name|ecPolicyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logDisableErasureCodingPolicy
argument_list|(
name|ecPolicyName
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
return|return
name|success
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
return|return
name|FSDirXAttrOp
operator|.
name|unprotectedRemoveXAttrs
argument_list|(
name|fsd
argument_list|,
name|srcIIP
argument_list|,
name|xattrs
argument_list|)
return|;
block|}
comment|/**    * Get the erasure coding policy information for specified path.    *    * @param fsn namespace    * @param src path    * @return {@link ErasureCodingPolicy}, or null if no policy has    * been set or the policy is REPLICATION    * @throws IOException    * @throws FileNotFoundException if the path does not exist.    * @throws AccessControlException if no read access    */
DECL|method|getErasureCodingPolicy (final FSNamesystem fsn, final String src, FSPermissionChecker pc)
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
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
block|{
assert|assert
name|fsn
operator|.
name|hasReadLock
argument_list|()
assert|;
if|if
condition|(
name|FSDirectory
operator|.
name|isExactReservedName
argument_list|(
name|src
argument_list|)
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
specifier|final
name|INodesInPath
name|iip
init|=
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
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
if|if
condition|(
name|iip
operator|.
name|isDotSnapshotDir
argument_list|()
condition|)
block|{
name|ecPolicy
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
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
name|src
argument_list|)
throw|;
block|}
else|else
block|{
name|ecPolicy
operator|=
name|getErasureCodingPolicyForPath
argument_list|(
name|fsd
argument_list|,
name|iip
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ecPolicy
operator|!=
literal|null
operator|&&
name|ecPolicy
operator|.
name|isReplicationPolicy
argument_list|()
condition|)
block|{
name|ecPolicy
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|ecPolicy
return|;
block|}
comment|/**    * Get the erasure coding policy information for specified path and policy    * name. If ec policy name is given, it will be parsed and the corresponding    * policy will be returned. Otherwise, get the policy from the parents of the    * iip.    *    * @param fsn namespace    * @param ecPolicyName the ec policy name    * @param iip inodes in the path containing the file    * @return {@link ErasureCodingPolicy}, or null if no policy is found    * @throws IOException    */
DECL|method|getErasureCodingPolicy (FSNamesystem fsn, String ecPolicyName, INodesInPath iip)
specifier|static
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|String
name|ecPolicyName
parameter_list|,
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|ecPolicyName
argument_list|)
condition|)
block|{
name|ecPolicy
operator|=
name|FSDirErasureCodingOp
operator|.
name|getErasureCodingPolicyByName
argument_list|(
name|fsn
argument_list|,
name|ecPolicyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ecPolicy
operator|=
name|FSDirErasureCodingOp
operator|.
name|unprotectedGetErasureCodingPolicy
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|)
expr_stmt|;
block|}
return|return
name|ecPolicy
return|;
block|}
comment|/**    * Get the erasure coding policy, including the REPLICATION policy. This does    * not do any permission checking.    *    * @param fsn namespace    * @param iip inodes in the path containing the file    * @return {@link ErasureCodingPolicy}    * @throws IOException    */
DECL|method|unprotectedGetErasureCodingPolicy ( final FSNamesystem fsn, final INodesInPath iip)
specifier|static
name|ErasureCodingPolicy
name|unprotectedGetErasureCodingPolicy
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
operator|.
name|getFSDirectory
argument_list|()
argument_list|,
name|iip
argument_list|)
return|;
block|}
comment|/**    * Get available erasure coding polices.    *    * @param fsn namespace    * @return {@link ErasureCodingPolicyInfo} array    */
DECL|method|getErasureCodingPolicies ( final FSNamesystem fsn)
specifier|static
name|ErasureCodingPolicyInfo
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
comment|/**    * Get available erasure coding codecs and coders.    *    * @param fsn namespace    * @return {@link java.util.HashMap} array    */
DECL|method|getErasureCodingCodecs (final FSNamesystem fsn)
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getErasureCodingCodecs
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
name|CodecRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|getCodec2CoderCompactMap
argument_list|()
return|;
block|}
comment|//return erasure coding policy for path, including REPLICATION policy
DECL|method|getErasureCodingPolicyForPath ( FSDirectory fsd, INodesInPath iip)
specifier|private
specifier|static
name|ErasureCodingPolicy
name|getErasureCodingPolicyForPath
parameter_list|(
name|FSDirectory
name|fsd
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
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
name|iip
operator|.
name|length
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
name|iip
operator|.
name|getINode
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
name|getByID
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
argument_list|(
name|iip
operator|.
name|getPathSnapshotId
argument_list|()
argument_list|)
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
name|getByName
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

