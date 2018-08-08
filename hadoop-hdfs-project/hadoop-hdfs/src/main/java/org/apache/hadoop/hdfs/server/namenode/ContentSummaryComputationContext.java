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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockStoragePolicySuite
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|DataInputStream
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ContentSummaryComputationContext
specifier|public
class|class
name|ContentSummaryComputationContext
block|{
DECL|field|dir
specifier|private
name|FSDirectory
name|dir
init|=
literal|null
decl_stmt|;
DECL|field|fsn
specifier|private
name|FSNamesystem
name|fsn
init|=
literal|null
decl_stmt|;
DECL|field|bsps
specifier|private
name|BlockStoragePolicySuite
name|bsps
init|=
literal|null
decl_stmt|;
DECL|field|counts
specifier|private
name|ContentCounts
name|counts
init|=
literal|null
decl_stmt|;
DECL|field|snapshotCounts
specifier|private
name|ContentCounts
name|snapshotCounts
init|=
literal|null
decl_stmt|;
DECL|field|nextCountLimit
specifier|private
name|long
name|nextCountLimit
init|=
literal|0
decl_stmt|;
DECL|field|limitPerRun
specifier|private
name|long
name|limitPerRun
init|=
literal|0
decl_stmt|;
DECL|field|yieldCount
specifier|private
name|long
name|yieldCount
init|=
literal|0
decl_stmt|;
DECL|field|sleepMilliSec
specifier|private
name|long
name|sleepMilliSec
init|=
literal|0
decl_stmt|;
DECL|field|sleepNanoSec
specifier|private
name|int
name|sleepNanoSec
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATED
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATED
init|=
literal|"Replicated"
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ContentSummaryComputationContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pc
specifier|private
name|FSPermissionChecker
name|pc
decl_stmt|;
comment|/**    * Constructor    *    * @param dir The FSDirectory instance    * @param fsn The FSNamesystem instance    * @param limitPerRun allowed number of operations in one    *        locking period. 0 or a negative number means    *        no limit (i.e. no yielding)    */
DECL|method|ContentSummaryComputationContext (FSDirectory dir, FSNamesystem fsn, long limitPerRun, long sleepMicroSec)
specifier|public
name|ContentSummaryComputationContext
parameter_list|(
name|FSDirectory
name|dir
parameter_list|,
name|FSNamesystem
name|fsn
parameter_list|,
name|long
name|limitPerRun
parameter_list|,
name|long
name|sleepMicroSec
parameter_list|)
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|fsn
argument_list|,
name|limitPerRun
argument_list|,
name|sleepMicroSec
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ContentSummaryComputationContext (FSDirectory dir, FSNamesystem fsn, long limitPerRun, long sleepMicroSec, FSPermissionChecker pc)
specifier|public
name|ContentSummaryComputationContext
parameter_list|(
name|FSDirectory
name|dir
parameter_list|,
name|FSNamesystem
name|fsn
parameter_list|,
name|long
name|limitPerRun
parameter_list|,
name|long
name|sleepMicroSec
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|fsn
operator|=
name|fsn
expr_stmt|;
name|this
operator|.
name|limitPerRun
operator|=
name|limitPerRun
expr_stmt|;
name|this
operator|.
name|nextCountLimit
operator|=
name|limitPerRun
expr_stmt|;
name|this
operator|.
name|counts
operator|=
operator|new
name|ContentCounts
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|snapshotCounts
operator|=
operator|new
name|ContentCounts
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|sleepMilliSec
operator|=
name|sleepMicroSec
operator|/
literal|1000
expr_stmt|;
name|this
operator|.
name|sleepNanoSec
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|sleepMicroSec
operator|%
literal|1000
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|this
operator|.
name|pc
operator|=
name|pc
expr_stmt|;
block|}
comment|/** Constructor for blocking computation. */
DECL|method|ContentSummaryComputationContext (BlockStoragePolicySuite bsps)
specifier|public
name|ContentSummaryComputationContext
parameter_list|(
name|BlockStoragePolicySuite
name|bsps
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|this
operator|.
name|bsps
operator|=
name|bsps
expr_stmt|;
block|}
comment|/** Return current yield count */
DECL|method|getYieldCount ()
specifier|public
name|long
name|getYieldCount
parameter_list|()
block|{
return|return
name|yieldCount
return|;
block|}
comment|/**    * Relinquish locks held during computation for a short while    * and reacquire them. This will give other threads a chance    * to acquire the contended locks and run.    *    * @return true if locks were released and reacquired.    */
DECL|method|yield ()
specifier|public
name|boolean
name|yield
parameter_list|()
block|{
comment|// Are we set up to do this?
if|if
condition|(
name|limitPerRun
operator|<=
literal|0
operator|||
name|dir
operator|==
literal|null
operator|||
name|fsn
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Have we reached the limit?
name|long
name|currentCount
init|=
name|counts
operator|.
name|getFileCount
argument_list|()
operator|+
name|counts
operator|.
name|getSymlinkCount
argument_list|()
operator|+
name|counts
operator|.
name|getDirectoryCount
argument_list|()
operator|+
name|counts
operator|.
name|getSnapshotableDirectoryCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentCount
operator|<=
name|nextCountLimit
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Update the next limit
name|nextCountLimit
operator|=
name|currentCount
operator|+
name|limitPerRun
expr_stmt|;
name|boolean
name|hadDirReadLock
init|=
name|dir
operator|.
name|hasReadLock
argument_list|()
decl_stmt|;
name|boolean
name|hadDirWriteLock
init|=
name|dir
operator|.
name|hasWriteLock
argument_list|()
decl_stmt|;
name|boolean
name|hadFsnReadLock
init|=
name|fsn
operator|.
name|hasReadLock
argument_list|()
decl_stmt|;
name|boolean
name|hadFsnWriteLock
init|=
name|fsn
operator|.
name|hasWriteLock
argument_list|()
decl_stmt|;
comment|// sanity check.
if|if
condition|(
operator|!
name|hadDirReadLock
operator|||
operator|!
name|hadFsnReadLock
operator|||
name|hadDirWriteLock
operator|||
name|hadFsnWriteLock
operator|||
name|dir
operator|.
name|getReadHoldCount
argument_list|()
operator|!=
literal|1
operator|||
name|fsn
operator|.
name|getReadHoldCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
comment|// cannot relinquish
return|return
literal|false
return|;
block|}
comment|// unlock
name|dir
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|readUnlock
argument_list|(
literal|"contentSummary"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMilliSec
argument_list|,
name|sleepNanoSec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{     }
finally|finally
block|{
comment|// reacquire
name|fsn
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|dir
operator|.
name|readLock
argument_list|()
expr_stmt|;
block|}
name|yieldCount
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Get the content counts */
DECL|method|getCounts ()
specifier|public
name|ContentCounts
name|getCounts
parameter_list|()
block|{
return|return
name|counts
return|;
block|}
DECL|method|getSnapshotCounts ()
specifier|public
name|ContentCounts
name|getSnapshotCounts
parameter_list|()
block|{
return|return
name|snapshotCounts
return|;
block|}
DECL|method|getBlockStoragePolicySuite ()
specifier|public
name|BlockStoragePolicySuite
name|getBlockStoragePolicySuite
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|(
name|bsps
operator|!=
literal|null
operator|||
name|fsn
operator|!=
literal|null
operator|)
argument_list|,
literal|"BlockStoragePolicySuite must be either initialized or available via"
operator|+
literal|" FSNameSystem"
argument_list|)
expr_stmt|;
return|return
operator|(
name|bsps
operator|!=
literal|null
operator|)
condition|?
name|bsps
else|:
name|fsn
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getStoragePolicySuite
argument_list|()
return|;
block|}
comment|/** Get the erasure coding policy. */
DECL|method|getErasureCodingPolicyName (INode inode)
specifier|public
name|String
name|getErasureCodingPolicyName
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
if|if
condition|(
name|inode
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|INodeFile
name|iNodeFile
init|=
name|inode
operator|.
name|asFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|iNodeFile
operator|.
name|isStriped
argument_list|()
condition|)
block|{
name|byte
name|ecPolicyId
init|=
name|iNodeFile
operator|.
name|getErasureCodingPolicyID
argument_list|()
decl_stmt|;
return|return
name|fsn
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getByID
argument_list|(
name|ecPolicyId
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|REPLICATED
return|;
block|}
block|}
if|if
condition|(
name|inode
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
return|return
literal|""
return|;
block|}
try|try
block|{
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
name|bins
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
name|din
init|=
operator|new
name|DataInputStream
argument_list|(
name|bins
argument_list|)
decl_stmt|;
name|String
name|ecPolicyName
init|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|din
argument_list|)
decl_stmt|;
return|return
name|dir
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getErasureCodingPolicyManager
argument_list|()
operator|.
name|getEnabledPolicyByName
argument_list|(
name|ecPolicyName
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|inode
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|getErasureCodingPolicyName
argument_list|(
name|inode
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Encountered error getting ec policy for "
operator|+
name|inode
operator|.
name|getFullPathName
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
return|return
literal|""
return|;
block|}
DECL|method|checkPermission (INodeDirectory inode, int snapshotId, FsAction access)
name|void
name|checkPermission
parameter_list|(
name|INodeDirectory
name|inode
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|FsAction
name|access
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|dir
operator|!=
literal|null
operator|&&
name|dir
operator|.
name|isPermissionEnabled
argument_list|()
operator|&&
name|pc
operator|!=
literal|null
operator|&&
operator|!
name|pc
operator|.
name|isSuperUser
argument_list|()
condition|)
block|{
name|pc
operator|.
name|checkPermission
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|access
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

