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
name|ArrayList
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
name|Objects
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Defines a single entry in an ACL.  An ACL entry has a type (user, group,  * mask, or other), an optional name (referring to a specific user or group), a  * set of permissions (any combination of read, write and execute), and a scope  * (access or default).  AclEntry instances are immutable.  Use a {@link Builder}  * to create a new instance.  */
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
DECL|class|AclEntry
specifier|public
class|class
name|AclEntry
block|{
DECL|field|type
specifier|private
specifier|final
name|AclEntryType
name|type
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|permission
specifier|private
specifier|final
name|FsAction
name|permission
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|AclEntryScope
name|scope
decl_stmt|;
comment|/**    * Returns the ACL entry type.    *    * @return AclEntryType ACL entry type    */
DECL|method|getType ()
specifier|public
name|AclEntryType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * Returns the optional ACL entry name.    *    * @return String ACL entry name, or null if undefined    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Returns the set of permissions in the ACL entry.    *    * @return FsAction set of permissions in the ACL entry    */
DECL|method|getPermission ()
specifier|public
name|FsAction
name|getPermission
parameter_list|()
block|{
return|return
name|permission
return|;
block|}
comment|/**    * Returns the scope of the ACL entry.    *    * @return AclEntryScope scope of the ACL entry    */
DECL|method|getScope ()
specifier|public
name|AclEntryScope
name|getScope
parameter_list|()
block|{
return|return
name|scope
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
name|AclEntry
name|other
init|=
operator|(
name|AclEntry
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|type
argument_list|,
name|other
operator|.
name|type
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|name
argument_list|,
name|other
operator|.
name|name
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|permission
argument_list|,
name|other
operator|.
name|permission
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|scope
argument_list|,
name|other
operator|.
name|scope
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
name|type
argument_list|,
name|name
argument_list|,
name|permission
argument_list|,
name|scope
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|scope
operator|==
name|AclEntryScope
operator|.
name|DEFAULT
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"default:"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|type
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|permission
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|permission
operator|.
name|SYMBOL
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Builder for creating new AclEntry instances.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|type
specifier|private
name|AclEntryType
name|type
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|permission
specifier|private
name|FsAction
name|permission
decl_stmt|;
DECL|field|scope
specifier|private
name|AclEntryScope
name|scope
init|=
name|AclEntryScope
operator|.
name|ACCESS
decl_stmt|;
comment|/**      * Sets the ACL entry type.      *      * @param type AclEntryType ACL entry type      * @return Builder this builder, for call chaining      */
DECL|method|setType (AclEntryType type)
specifier|public
name|Builder
name|setType
parameter_list|(
name|AclEntryType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the optional ACL entry name.      *      * @param name String optional ACL entry name      * @return Builder this builder, for call chaining      */
DECL|method|setName (String name)
specifier|public
name|Builder
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the set of permissions in the ACL entry.      *      * @param permission FsAction set of permissions in the ACL entry      * @return Builder this builder, for call chaining      */
DECL|method|setPermission (FsAction permission)
specifier|public
name|Builder
name|setPermission
parameter_list|(
name|FsAction
name|permission
parameter_list|)
block|{
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the scope of the ACL entry.  If this method is not called, then the      * builder assumes {@link AclEntryScope#ACCESS}.      *      * @param scope AclEntryScope scope of the ACL entry      * @return Builder this builder, for call chaining      */
DECL|method|setScope (AclEntryScope scope)
specifier|public
name|Builder
name|setScope
parameter_list|(
name|AclEntryScope
name|scope
parameter_list|)
block|{
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Builds a new AclEntry populated with the set properties.      *      * @return AclEntry new AclEntry      */
DECL|method|build ()
specifier|public
name|AclEntry
name|build
parameter_list|()
block|{
return|return
operator|new
name|AclEntry
argument_list|(
name|type
argument_list|,
name|name
argument_list|,
name|permission
argument_list|,
name|scope
argument_list|)
return|;
block|}
block|}
comment|/**    * Private constructor.    *    * @param type AclEntryType ACL entry type    * @param name String optional ACL entry name    * @param permission FsAction set of permissions in the ACL entry    * @param scope AclEntryScope scope of the ACL entry    */
DECL|method|AclEntry (AclEntryType type, String name, FsAction permission, AclEntryScope scope)
specifier|private
name|AclEntry
parameter_list|(
name|AclEntryType
name|type
parameter_list|,
name|String
name|name
parameter_list|,
name|FsAction
name|permission
parameter_list|,
name|AclEntryScope
name|scope
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
comment|/**    * Parses a string representation of an ACL spec into a list of AclEntry    * objects. Example: "user::rwx,user:foo:rw-,group::r--,other::---"    *     * @param aclSpec    *          String representation of an ACL spec.    * @param includePermission    *          for setAcl operations this will be true. i.e. AclSpec should    *          include permissions.<br>    *          But for removeAcl operation it will be false. i.e. AclSpec should    *          not contain permissions.<br>    *          Example: "user:foo,group:bar"    * @return Returns list of {@link AclEntry} parsed    */
DECL|method|parseAclSpec (String aclSpec, boolean includePermission)
specifier|public
specifier|static
name|List
argument_list|<
name|AclEntry
argument_list|>
name|parseAclSpec
parameter_list|(
name|String
name|aclSpec
parameter_list|,
name|boolean
name|includePermission
parameter_list|)
block|{
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|AclEntry
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|aclStrings
init|=
name|StringUtils
operator|.
name|getStringCollection
argument_list|(
name|aclSpec
argument_list|,
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|aclStr
range|:
name|aclStrings
control|)
block|{
name|AclEntry
name|aclEntry
init|=
name|parseAclEntry
argument_list|(
name|aclStr
argument_list|,
name|includePermission
argument_list|)
decl_stmt|;
name|aclEntries
operator|.
name|add
argument_list|(
name|aclEntry
argument_list|)
expr_stmt|;
block|}
return|return
name|aclEntries
return|;
block|}
comment|/**    * Parses a string representation of an ACL into a AclEntry object.<br>    *     * @param aclStr    *          String representation of an ACL.<br>    *          Example: "user:foo:rw-"    * @param includePermission    *          for setAcl operations this will be true. i.e. Acl should include    *          permissions.<br>    *          But for removeAcl operation it will be false. i.e. Acl should not    *          contain permissions.<br>    *          Example: "user:foo,group:bar,mask::"    * @return Returns an {@link AclEntry} object    */
DECL|method|parseAclEntry (String aclStr, boolean includePermission)
specifier|public
specifier|static
name|AclEntry
name|parseAclEntry
parameter_list|(
name|String
name|aclStr
parameter_list|,
name|boolean
name|includePermission
parameter_list|)
block|{
name|AclEntry
operator|.
name|Builder
name|builder
init|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// Here "::" represent one empty string.
comment|// StringUtils.getStringCollection() will ignore this.
name|String
index|[]
name|split
init|=
name|aclStr
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|split
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid<aclSpec> : "
operator|+
name|aclStr
argument_list|)
throw|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
if|if
condition|(
literal|"default"
operator|.
name|equals
argument_list|(
name|split
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
comment|// default entry
name|index
operator|++
expr_stmt|;
name|builder
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|split
operator|.
name|length
operator|<=
name|index
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid<aclSpec> : "
operator|+
name|aclStr
argument_list|)
throw|;
block|}
name|AclEntryType
name|aclType
init|=
literal|null
decl_stmt|;
try|try
block|{
name|aclType
operator|=
name|Enum
operator|.
name|valueOf
argument_list|(
name|AclEntryType
operator|.
name|class
argument_list|,
name|split
index|[
name|index
index|]
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setType
argument_list|(
name|aclType
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid type of acl in<aclSpec> :"
operator|+
name|aclStr
argument_list|)
throw|;
block|}
if|if
condition|(
name|split
operator|.
name|length
operator|>
name|index
condition|)
block|{
name|String
name|name
init|=
name|split
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|index
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|includePermission
condition|)
block|{
if|if
condition|(
name|split
operator|.
name|length
operator|<=
name|index
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid<aclSpec> : "
operator|+
name|aclStr
argument_list|)
throw|;
block|}
name|String
name|permission
init|=
name|split
index|[
name|index
index|]
decl_stmt|;
name|FsAction
name|fsAction
init|=
name|FsAction
operator|.
name|getFsAction
argument_list|(
name|permission
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fsAction
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid permission in<aclSpec> : "
operator|+
name|aclStr
argument_list|)
throw|;
block|}
name|builder
operator|.
name|setPermission
argument_list|(
name|fsAction
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|split
operator|.
name|length
operator|>
name|index
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid<aclSpec> : "
operator|+
name|aclStr
argument_list|)
throw|;
block|}
name|AclEntry
name|aclEntry
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|aclEntry
return|;
block|}
comment|/**    * Convert a List of AclEntries into a string - the reverse of parseAclSpec.    * @param aclSpec List of AclEntries to convert    * @return String representation of aclSpec    */
DECL|method|aclSpecToString (List<AclEntry> aclSpec)
specifier|public
specifier|static
name|String
name|aclSpecToString
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|AclEntry
name|e
range|:
name|aclSpec
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
comment|// remove last ,
block|}
block|}
end_class

end_unit

