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
name|Collections
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
name|base
operator|.
name|Objects
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
comment|/**  * An AclStatus contains the ACL information of a specific file. AclStatus  * instances are immutable. Use a {@link Builder} to create a new instance.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AclStatus
specifier|public
class|class
name|AclStatus
block|{
DECL|field|owner
specifier|private
specifier|final
name|String
name|owner
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|String
name|group
decl_stmt|;
DECL|field|stickyBit
specifier|private
specifier|final
name|boolean
name|stickyBit
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|Iterable
argument_list|<
name|AclEntry
argument_list|>
name|entries
decl_stmt|;
comment|/**    * Returns the file owner.    *    * @return String file owner    */
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Returns the file group.    *    * @return String file group    */
DECL|method|getGroup ()
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
comment|/**    * Returns the sticky bit.    *     * @return boolean sticky bit    */
DECL|method|isStickyBit ()
specifier|public
name|boolean
name|isStickyBit
parameter_list|()
block|{
return|return
name|stickyBit
return|;
block|}
comment|/**    * Returns the list of all ACL entries, ordered by their natural ordering.    *    * @return Iterable<AclEntry> unmodifiable ordered list of all ACL entries    */
DECL|method|getEntries ()
specifier|public
name|Iterable
argument_list|<
name|AclEntry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|entries
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AclStatus
name|other
init|=
operator|(
name|AclStatus
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|owner
argument_list|,
name|other
operator|.
name|owner
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|group
argument_list|,
name|other
operator|.
name|group
argument_list|)
operator|&&
name|stickyBit
operator|==
name|other
operator|.
name|stickyBit
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|entries
argument_list|,
name|other
operator|.
name|entries
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|owner
argument_list|,
name|group
argument_list|,
name|stickyBit
argument_list|,
name|entries
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"owner: "
argument_list|)
operator|.
name|append
argument_list|(
name|owner
argument_list|)
operator|.
name|append
argument_list|(
literal|", group: "
argument_list|)
operator|.
name|append
argument_list|(
name|group
argument_list|)
operator|.
name|append
argument_list|(
literal|", acl: {"
argument_list|)
operator|.
name|append
argument_list|(
literal|"entries: "
argument_list|)
operator|.
name|append
argument_list|(
name|entries
argument_list|)
operator|.
name|append
argument_list|(
literal|", stickyBit: "
argument_list|)
operator|.
name|append
argument_list|(
name|stickyBit
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Builder for creating new Acl instances.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
DECL|field|group
specifier|private
name|String
name|group
decl_stmt|;
DECL|field|stickyBit
specifier|private
name|boolean
name|stickyBit
decl_stmt|;
DECL|field|entries
specifier|private
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|/**      * Sets the file owner.      *      * @param owner String file owner      * @return Builder this builder, for call chaining      */
DECL|method|owner (String owner)
specifier|public
name|Builder
name|owner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the file group.      *      * @param group String file group      * @return Builder this builder, for call chaining      */
DECL|method|group (String group)
specifier|public
name|Builder
name|group
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds an ACL entry.      *      * @param e AclEntry entry to add      * @return Builder this builder, for call chaining      */
DECL|method|addEntry (AclEntry e)
specifier|public
name|Builder
name|addEntry
parameter_list|(
name|AclEntry
name|e
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a list of ACL entries.      *      * @param entries AclEntry entries to add      * @return Builder this builder, for call chaining      */
DECL|method|addEntries (Iterable<AclEntry> entries)
specifier|public
name|Builder
name|addEntries
parameter_list|(
name|Iterable
argument_list|<
name|AclEntry
argument_list|>
name|entries
parameter_list|)
block|{
for|for
control|(
name|AclEntry
name|e
range|:
name|entries
control|)
name|this
operator|.
name|entries
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets sticky bit. If this method is not called, then the builder assumes      * false.      *      * @param stickyBit      *          boolean sticky bit      * @return Builder this builder, for call chaining      */
DECL|method|stickyBit (boolean stickyBit)
specifier|public
name|Builder
name|stickyBit
parameter_list|(
name|boolean
name|stickyBit
parameter_list|)
block|{
name|this
operator|.
name|stickyBit
operator|=
name|stickyBit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Builds a new AclStatus populated with the set properties.      *      * @return AclStatus new AclStatus      */
DECL|method|build ()
specifier|public
name|AclStatus
name|build
parameter_list|()
block|{
return|return
operator|new
name|AclStatus
argument_list|(
name|owner
argument_list|,
name|group
argument_list|,
name|stickyBit
argument_list|,
name|entries
argument_list|)
return|;
block|}
block|}
comment|/**    * Private constructor.    *    * @param file Path file associated to this ACL    * @param owner String file owner    * @param group String file group    * @param stickyBit the sticky bit    * @param entries the ACL entries    */
DECL|method|AclStatus (String owner, String group, boolean stickyBit, Iterable<AclEntry> entries)
specifier|private
name|AclStatus
parameter_list|(
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|boolean
name|stickyBit
parameter_list|,
name|Iterable
argument_list|<
name|AclEntry
argument_list|>
name|entries
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|stickyBit
operator|=
name|stickyBit
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|entriesCopy
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|entriesCopy
argument_list|)
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entriesCopy
expr_stmt|;
block|}
block|}
end_class

end_unit

