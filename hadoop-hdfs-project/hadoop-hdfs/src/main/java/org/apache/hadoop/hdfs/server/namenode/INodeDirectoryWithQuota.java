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
name|PermissionStatus
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
name|DSQuotaExceededException
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
name|NSQuotaExceededException
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
name|QuotaExceededException
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * Directory INode class that has a quota restriction  */
end_comment

begin_class
DECL|class|INodeDirectoryWithQuota
specifier|public
class|class
name|INodeDirectoryWithQuota
extends|extends
name|INodeDirectory
block|{
comment|/** Name space quota */
DECL|field|nsQuota
specifier|private
name|long
name|nsQuota
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** Name space count */
DECL|field|namespace
specifier|private
name|long
name|namespace
init|=
literal|1L
decl_stmt|;
comment|/** Disk space quota */
DECL|field|dsQuota
specifier|private
name|long
name|dsQuota
init|=
name|HdfsConstants
operator|.
name|QUOTA_RESET
decl_stmt|;
comment|/** Disk space count */
DECL|field|diskspace
specifier|private
name|long
name|diskspace
init|=
literal|0L
decl_stmt|;
comment|/** Convert an existing directory inode to one with the given quota    *     * @param nsQuota Namespace quota to be assigned to this inode    * @param dsQuota Diskspace quota to be assigned to this indoe    * @param other The other inode from which all other properties are copied    */
DECL|method|INodeDirectoryWithQuota (INodeDirectory other, boolean adopt, long nsQuota, long dsQuota)
specifier|public
name|INodeDirectoryWithQuota
parameter_list|(
name|INodeDirectory
name|other
parameter_list|,
name|boolean
name|adopt
parameter_list|,
name|long
name|nsQuota
parameter_list|,
name|long
name|dsQuota
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|,
name|adopt
argument_list|)
expr_stmt|;
specifier|final
name|Quota
operator|.
name|Counts
name|counts
init|=
name|other
operator|.
name|computeQuotaUsage
argument_list|()
decl_stmt|;
name|this
operator|.
name|namespace
operator|=
name|counts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|)
expr_stmt|;
name|this
operator|.
name|diskspace
operator|=
name|counts
operator|.
name|get
argument_list|(
name|Quota
operator|.
name|DISKSPACE
argument_list|)
expr_stmt|;
name|this
operator|.
name|nsQuota
operator|=
name|nsQuota
expr_stmt|;
name|this
operator|.
name|dsQuota
operator|=
name|dsQuota
expr_stmt|;
block|}
comment|/** constructor with no quota verification */
DECL|method|INodeDirectoryWithQuota (long id, byte[] name, PermissionStatus permissions, long modificationTime, long nsQuota, long dsQuota)
name|INodeDirectoryWithQuota
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|nsQuota
parameter_list|,
name|long
name|dsQuota
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|permissions
argument_list|,
name|modificationTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|nsQuota
operator|=
name|nsQuota
expr_stmt|;
name|this
operator|.
name|dsQuota
operator|=
name|dsQuota
expr_stmt|;
block|}
comment|/** constructor with no quota verification */
DECL|method|INodeDirectoryWithQuota (long id, byte[] name, PermissionStatus permissions)
name|INodeDirectoryWithQuota
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|permissions
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/** Get this directory's namespace quota    * @return this directory's namespace quota    */
annotation|@
name|Override
DECL|method|getNsQuota ()
specifier|public
name|long
name|getNsQuota
parameter_list|()
block|{
return|return
name|nsQuota
return|;
block|}
comment|/** Get this directory's diskspace quota    * @return this directory's diskspace quota    */
annotation|@
name|Override
DECL|method|getDsQuota ()
specifier|public
name|long
name|getDsQuota
parameter_list|()
block|{
return|return
name|dsQuota
return|;
block|}
comment|/** Set this directory's quota    *     * @param nsQuota Namespace quota to be set    * @param dsQuota diskspace quota to be set    */
DECL|method|setQuota (long nsQuota, long dsQuota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|long
name|nsQuota
parameter_list|,
name|long
name|dsQuota
parameter_list|)
block|{
name|this
operator|.
name|nsQuota
operator|=
name|nsQuota
expr_stmt|;
name|this
operator|.
name|dsQuota
operator|=
name|dsQuota
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeQuotaUsage (Quota.Counts counts, boolean useCache, int lastSnapshotId)
specifier|public
name|Quota
operator|.
name|Counts
name|computeQuotaUsage
parameter_list|(
name|Quota
operator|.
name|Counts
name|counts
parameter_list|,
name|boolean
name|useCache
parameter_list|,
name|int
name|lastSnapshotId
parameter_list|)
block|{
if|if
condition|(
name|useCache
operator|&&
name|isQuotaSet
argument_list|()
condition|)
block|{
comment|// use cache value
name|counts
operator|.
name|add
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
name|counts
operator|.
name|add
argument_list|(
name|Quota
operator|.
name|DISKSPACE
argument_list|,
name|diskspace
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|computeQuotaUsage
argument_list|(
name|counts
argument_list|,
literal|false
argument_list|,
name|lastSnapshotId
argument_list|)
expr_stmt|;
block|}
return|return
name|counts
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary ( final ContentSummaryComputationContext summary)
specifier|public
name|ContentSummaryComputationContext
name|computeContentSummary
parameter_list|(
specifier|final
name|ContentSummaryComputationContext
name|summary
parameter_list|)
block|{
specifier|final
name|long
name|original
init|=
name|summary
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
name|Content
operator|.
name|DISKSPACE
argument_list|)
decl_stmt|;
name|long
name|oldYieldCount
init|=
name|summary
operator|.
name|getYieldCount
argument_list|()
decl_stmt|;
name|super
operator|.
name|computeContentSummary
argument_list|(
name|summary
argument_list|)
expr_stmt|;
comment|// Check only when the content has not changed in the middle.
if|if
condition|(
name|oldYieldCount
operator|==
name|summary
operator|.
name|getYieldCount
argument_list|()
condition|)
block|{
name|checkDiskspace
argument_list|(
name|summary
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
name|Content
operator|.
name|DISKSPACE
argument_list|)
operator|-
name|original
argument_list|)
expr_stmt|;
block|}
return|return
name|summary
return|;
block|}
DECL|method|checkDiskspace (final long computed)
specifier|private
name|void
name|checkDiskspace
parameter_list|(
specifier|final
name|long
name|computed
parameter_list|)
block|{
if|if
condition|(
operator|-
literal|1
operator|!=
name|getDsQuota
argument_list|()
operator|&&
name|diskspace
operator|!=
name|computed
condition|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"BUG: Inconsistent diskspace for directory "
operator|+
name|getFullPathName
argument_list|()
operator|+
literal|". Cached = "
operator|+
name|diskspace
operator|+
literal|" != Computed = "
operator|+
name|computed
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Get the number of names in the subtree rooted at this directory    * @return the size of the subtree rooted at this directory    */
DECL|method|numItemsInTree ()
name|long
name|numItemsInTree
parameter_list|()
block|{
return|return
name|namespace
return|;
block|}
annotation|@
name|Override
DECL|method|addSpaceConsumed (final long nsDelta, final long dsDelta, boolean verify)
specifier|public
specifier|final
name|void
name|addSpaceConsumed
parameter_list|(
specifier|final
name|long
name|nsDelta
parameter_list|,
specifier|final
name|long
name|dsDelta
parameter_list|,
name|boolean
name|verify
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
if|if
condition|(
name|isQuotaSet
argument_list|()
condition|)
block|{
comment|// The following steps are important:
comment|// check quotas in this inode and all ancestors before changing counts
comment|// so that no change is made if there is any quota violation.
comment|// (1) verify quota in this inode
if|if
condition|(
name|verify
condition|)
block|{
name|verifyQuota
argument_list|(
name|nsDelta
argument_list|,
name|dsDelta
argument_list|)
expr_stmt|;
block|}
comment|// (2) verify quota and then add count in ancestors
name|super
operator|.
name|addSpaceConsumed
argument_list|(
name|nsDelta
argument_list|,
name|dsDelta
argument_list|,
name|verify
argument_list|)
expr_stmt|;
comment|// (3) add count in this inode
name|addSpaceConsumed2Cache
argument_list|(
name|nsDelta
argument_list|,
name|dsDelta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|addSpaceConsumed
argument_list|(
name|nsDelta
argument_list|,
name|dsDelta
argument_list|,
name|verify
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Update the size of the tree    *     * @param nsDelta the change of the tree size    * @param dsDelta change to disk space occupied    */
DECL|method|addSpaceConsumed2Cache (long nsDelta, long dsDelta)
specifier|protected
name|void
name|addSpaceConsumed2Cache
parameter_list|(
name|long
name|nsDelta
parameter_list|,
name|long
name|dsDelta
parameter_list|)
block|{
name|namespace
operator|+=
name|nsDelta
expr_stmt|;
name|diskspace
operator|+=
name|dsDelta
expr_stmt|;
block|}
comment|/**     * Sets namespace and diskspace take by the directory rooted     * at this INode. This should be used carefully. It does not check     * for quota violations.    *     * @param namespace size of the directory to be set    * @param diskspace disk space take by all the nodes under this directory    */
DECL|method|setSpaceConsumed (long namespace, long diskspace)
name|void
name|setSpaceConsumed
parameter_list|(
name|long
name|namespace
parameter_list|,
name|long
name|diskspace
parameter_list|)
block|{
name|this
operator|.
name|namespace
operator|=
name|namespace
expr_stmt|;
name|this
operator|.
name|diskspace
operator|=
name|diskspace
expr_stmt|;
block|}
comment|/** Verify if the namespace quota is violated after applying delta. */
DECL|method|verifyNamespaceQuota (long delta)
name|void
name|verifyNamespaceQuota
parameter_list|(
name|long
name|delta
parameter_list|)
throws|throws
name|NSQuotaExceededException
block|{
if|if
condition|(
name|Quota
operator|.
name|isViolated
argument_list|(
name|nsQuota
argument_list|,
name|namespace
argument_list|,
name|delta
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NSQuotaExceededException
argument_list|(
name|nsQuota
argument_list|,
name|namespace
operator|+
name|delta
argument_list|)
throw|;
block|}
block|}
comment|/** Verify if the namespace count disk space satisfies the quota restriction     * @throws QuotaExceededException if the given quota is less than the count    */
DECL|method|verifyQuota (long nsDelta, long dsDelta)
name|void
name|verifyQuota
parameter_list|(
name|long
name|nsDelta
parameter_list|,
name|long
name|dsDelta
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
name|verifyNamespaceQuota
argument_list|(
name|nsDelta
argument_list|)
expr_stmt|;
if|if
condition|(
name|Quota
operator|.
name|isViolated
argument_list|(
name|dsQuota
argument_list|,
name|diskspace
argument_list|,
name|dsDelta
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DSQuotaExceededException
argument_list|(
name|dsQuota
argument_list|,
name|diskspace
operator|+
name|dsDelta
argument_list|)
throw|;
block|}
block|}
DECL|method|namespaceString ()
name|String
name|namespaceString
parameter_list|()
block|{
return|return
literal|"namespace: "
operator|+
operator|(
name|nsQuota
operator|<
literal|0
condition|?
literal|"-"
else|:
name|namespace
operator|+
literal|"/"
operator|+
name|nsQuota
operator|)
return|;
block|}
DECL|method|diskspaceString ()
name|String
name|diskspaceString
parameter_list|()
block|{
return|return
literal|"diskspace: "
operator|+
operator|(
name|dsQuota
operator|<
literal|0
condition|?
literal|"-"
else|:
name|diskspace
operator|+
literal|"/"
operator|+
name|dsQuota
operator|)
return|;
block|}
DECL|method|quotaString ()
name|String
name|quotaString
parameter_list|()
block|{
return|return
literal|", Quota["
operator|+
name|namespaceString
argument_list|()
operator|+
literal|", "
operator|+
name|diskspaceString
argument_list|()
operator|+
literal|"]"
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNamespace ()
specifier|public
name|long
name|getNamespace
parameter_list|()
block|{
return|return
name|this
operator|.
name|namespace
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDiskspace ()
specifier|public
name|long
name|getDiskspace
parameter_list|()
block|{
return|return
name|this
operator|.
name|diskspace
return|;
block|}
block|}
end_class

end_unit

