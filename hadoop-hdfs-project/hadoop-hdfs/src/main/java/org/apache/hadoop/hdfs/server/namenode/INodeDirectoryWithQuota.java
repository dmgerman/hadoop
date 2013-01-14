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
name|snapshot
operator|.
name|Snapshot
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
DECL|field|nsCount
specifier|private
name|long
name|nsCount
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
specifier|protected
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
name|INode
operator|.
name|DirCounts
name|counts
init|=
operator|new
name|INode
operator|.
name|DirCounts
argument_list|()
decl_stmt|;
name|other
operator|.
name|spaceConsumedInTree
argument_list|(
name|counts
argument_list|)
expr_stmt|;
name|this
operator|.
name|nsCount
operator|=
name|counts
operator|.
name|getNsCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|diskspace
operator|=
name|counts
operator|.
name|getDsCount
argument_list|()
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
DECL|method|INodeDirectoryWithQuota (long id, PermissionStatus permissions, long modificationTime, long nsQuota, long dsQuota)
name|INodeDirectoryWithQuota
parameter_list|(
name|long
name|id
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
DECL|method|INodeDirectoryWithQuota (long id, String name, PermissionStatus permissions)
name|INodeDirectoryWithQuota
parameter_list|(
name|long
name|id
parameter_list|,
name|String
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
DECL|method|setQuota (long nsQuota, long dsQuota, Snapshot latest)
specifier|public
name|void
name|setQuota
parameter_list|(
name|long
name|nsQuota
parameter_list|,
name|long
name|dsQuota
parameter_list|,
name|Snapshot
name|latest
parameter_list|)
block|{
name|recordModification
argument_list|(
name|latest
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
annotation|@
name|Override
DECL|method|spaceConsumedInTree (DirCounts counts)
name|DirCounts
name|spaceConsumedInTree
parameter_list|(
name|DirCounts
name|counts
parameter_list|)
block|{
name|counts
operator|.
name|nsCount
operator|+=
name|nsCount
expr_stmt|;
name|counts
operator|.
name|dsCount
operator|+=
name|diskspace
expr_stmt|;
return|return
name|counts
return|;
block|}
comment|/** Get the number of names in the subtree rooted at this directory    * @return the size of the subtree rooted at this directory    */
DECL|method|numItemsInTree ()
name|long
name|numItemsInTree
parameter_list|()
block|{
return|return
name|nsCount
return|;
block|}
DECL|method|diskspaceConsumed ()
name|long
name|diskspaceConsumed
parameter_list|()
block|{
return|return
name|diskspace
return|;
block|}
comment|/** Update the size of the tree    *     * @param nsDelta the change of the tree size    * @param dsDelta change to disk space occupied    */
DECL|method|addSpaceConsumed (long nsDelta, long dsDelta)
name|void
name|addSpaceConsumed
parameter_list|(
name|long
name|nsDelta
parameter_list|,
name|long
name|dsDelta
parameter_list|)
block|{
name|setSpaceConsumed
argument_list|(
name|nsCount
operator|+
name|nsDelta
argument_list|,
name|diskspace
operator|+
name|dsDelta
argument_list|)
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
name|nsCount
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
name|long
name|newCount
init|=
name|nsCount
operator|+
name|nsDelta
decl_stmt|;
name|long
name|newDiskspace
init|=
name|diskspace
operator|+
name|dsDelta
decl_stmt|;
if|if
condition|(
name|nsDelta
operator|>
literal|0
operator|||
name|dsDelta
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|nsQuota
operator|>=
literal|0
operator|&&
name|nsQuota
operator|<
name|newCount
condition|)
block|{
throw|throw
operator|new
name|NSQuotaExceededException
argument_list|(
name|nsQuota
argument_list|,
name|newCount
argument_list|)
throw|;
block|}
if|if
condition|(
name|dsQuota
operator|>=
literal|0
operator|&&
name|dsQuota
operator|<
name|newDiskspace
condition|)
block|{
throw|throw
operator|new
name|DSQuotaExceededException
argument_list|(
name|dsQuota
argument_list|,
name|newDiskspace
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

