begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.permission
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * AclUtil contains utility methods for manipulating ACLs.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|AclUtil
specifier|public
specifier|final
class|class
name|AclUtil
block|{
comment|/**    * Given permissions and extended ACL entries, returns the full logical ACL.    *    * @param perm FsPermission containing permissions    * @param entries List&lt;AclEntry&gt; containing extended ACL entries    * @return List&lt;AclEntry&gt; containing full logical ACL    */
DECL|method|getAclFromPermAndEntries (FsPermission perm, List<AclEntry> entries)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|getAclFromPermAndEntries
parameter_list|(
name|FsPermission
name|perm
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
parameter_list|)
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|acl
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|entries
operator|.
name|size
argument_list|()
operator|+
literal|3
argument_list|)
decl_stmt|;
comment|// Owner entry implied by owner permission bits.
name|acl
operator|.
name|add
argument_list|(
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|USER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getUserAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// All extended access ACL entries.
name|boolean
name|hasAccessAcl
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|AclEntry
argument_list|>
name|entryIter
init|=
name|entries
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|AclEntry
name|curEntry
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|entryIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|curEntry
operator|=
name|entryIter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|curEntry
operator|.
name|getScope
argument_list|()
operator|==
name|AclEntryScope
operator|.
name|DEFAULT
condition|)
block|{
break|break;
block|}
name|hasAccessAcl
operator|=
literal|true
expr_stmt|;
name|acl
operator|.
name|add
argument_list|(
name|curEntry
argument_list|)
expr_stmt|;
block|}
comment|// Mask entry implied by group permission bits, or group entry if there is
comment|// no access ACL (only default ACL).
name|acl
operator|.
name|add
argument_list|(
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|hasAccessAcl
condition|?
name|AclEntryType
operator|.
name|MASK
else|:
name|AclEntryType
operator|.
name|GROUP
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getGroupAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Other entry implied by other bits.
name|acl
operator|.
name|add
argument_list|(
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|OTHER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getOtherAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Default ACL entries.
if|if
condition|(
name|curEntry
operator|!=
literal|null
operator|&&
name|curEntry
operator|.
name|getScope
argument_list|()
operator|==
name|AclEntryScope
operator|.
name|DEFAULT
condition|)
block|{
name|acl
operator|.
name|add
argument_list|(
name|curEntry
argument_list|)
expr_stmt|;
while|while
condition|(
name|entryIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|acl
operator|.
name|add
argument_list|(
name|entryIter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|acl
return|;
block|}
comment|/**    * Translates the given permission bits to the equivalent minimal ACL.    *    * @param perm FsPermission to translate    * @return List&lt;AclEntry&gt; containing exactly 3 entries representing the    *         owner, group and other permissions    */
DECL|method|getMinimalAcl (FsPermission perm)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|getMinimalAcl
parameter_list|(
name|FsPermission
name|perm
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|USER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getUserAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|GROUP
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getGroupAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|)
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|OTHER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|perm
operator|.
name|getOtherAction
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Checks if the given entries represent a minimal ACL (contains exactly 3    * entries).    *    * @param entries List&lt;AclEntry&gt; entries to check    * @return boolean true if the entries represent a minimal ACL    */
DECL|method|isMinimalAcl (List<AclEntry> entries)
specifier|public
specifier|static
name|boolean
name|isMinimalAcl
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
parameter_list|)
block|{
return|return
name|entries
operator|.
name|size
argument_list|()
operator|==
literal|3
return|;
block|}
comment|/**    * There is no reason to instantiate this class.    */
DECL|method|AclUtil ()
specifier|private
name|AclUtil
parameter_list|()
block|{   }
block|}
end_class

end_unit

