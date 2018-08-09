begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * OzoneACL classes define bucket ACLs used in OZONE.  *  * ACLs in Ozone follow this pattern.  *<ul>  *<li>user:name:rw  *<li>group:name:rw  *<li>world::rw  *</ul>  */
end_comment

begin_class
DECL|class|OzoneAcl
specifier|public
class|class
name|OzoneAcl
block|{
DECL|field|type
specifier|private
name|OzoneACLType
name|type
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|rights
specifier|private
name|OzoneACLRights
name|rights
decl_stmt|;
comment|/**    * Constructor for OzoneAcl.    */
DECL|method|OzoneAcl ()
specifier|public
name|OzoneAcl
parameter_list|()
block|{   }
comment|/**    * Constructor for OzoneAcl.    *    * @param type - Type    * @param name - Name of user    * @param rights - Rights    */
DECL|method|OzoneAcl (OzoneACLType type, String name, OzoneACLRights rights)
specifier|public
name|OzoneAcl
parameter_list|(
name|OzoneACLType
name|type
parameter_list|,
name|String
name|name
parameter_list|,
name|OzoneACLRights
name|rights
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|rights
operator|=
name|rights
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|OzoneACLType
operator|.
name|WORLD
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected name part in world type"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
operator|(
name|type
operator|==
name|OzoneACLType
operator|.
name|USER
operator|)
operator|||
operator|(
name|type
operator|==
name|OzoneACLType
operator|.
name|GROUP
operator|)
operator|)
operator|&&
operator|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"User or group name is required"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Parses an ACL string and returns the ACL object.    *    * @param acl - Acl String , Ex. user:anu:rw    *    * @return - Ozone ACLs    */
DECL|method|parseAcl (String acl)
specifier|public
specifier|static
name|OzoneAcl
name|parseAcl
parameter_list|(
name|String
name|acl
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
operator|(
name|acl
operator|==
literal|null
operator|)
operator|||
name|acl
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ACLs cannot be null or empty"
argument_list|)
throw|;
block|}
name|String
index|[]
name|parts
init|=
name|acl
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|<
literal|3
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ACLs are not in expected format"
argument_list|)
throw|;
block|}
name|OzoneACLType
name|aclType
init|=
name|OzoneACLType
operator|.
name|valueOf
argument_list|(
name|parts
index|[
literal|0
index|]
operator|.
name|toUpperCase
argument_list|()
argument_list|)
decl_stmt|;
name|OzoneACLRights
name|rights
init|=
name|OzoneACLRights
operator|.
name|getACLRight
argument_list|(
name|parts
index|[
literal|2
index|]
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO : Support sanitation of these user names by calling into
comment|// userAuth Interface.
return|return
operator|new
name|OzoneAcl
argument_list|(
name|aclType
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|,
name|rights
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
name|type
operator|+
literal|":"
operator|+
name|name
operator|+
literal|":"
operator|+
name|OzoneACLRights
operator|.
name|getACLRightsString
argument_list|(
name|rights
argument_list|)
return|;
block|}
comment|/**    * Returns a hash code value for the object. This method is    * supported for the benefit of hash tables.    *    * @return a hash code value for this object.    *    * @see Object#equals(Object)    * @see System#identityHashCode    */
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
name|hash
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|,
name|this
operator|.
name|getRights
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|this
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns name.    *    * @return name    */
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
comment|/**    * Returns Rights.    *    * @return - Rights    */
DECL|method|getRights ()
specifier|public
name|OzoneACLRights
name|getRights
parameter_list|()
block|{
return|return
name|rights
return|;
block|}
comment|/**    * Returns Type.    *    * @return type    */
DECL|method|getType ()
specifier|public
name|OzoneACLType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * Indicates whether some other object is "equal to" this one.    *    * @param obj the reference object with which to compare.    *    * @return {@code true} if this object is the same as the obj    * argument; {@code false} otherwise.    */
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
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
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|OzoneAcl
name|otherAcl
init|=
operator|(
name|OzoneAcl
operator|)
name|obj
decl_stmt|;
return|return
name|otherAcl
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|otherAcl
operator|.
name|getRights
argument_list|()
operator|==
name|this
operator|.
name|getRights
argument_list|()
operator|&&
name|otherAcl
operator|.
name|getType
argument_list|()
operator|==
name|this
operator|.
name|getType
argument_list|()
return|;
block|}
comment|/**    * ACL types.    */
DECL|enum|OzoneACLType
specifier|public
enum|enum
name|OzoneACLType
block|{
DECL|enumConstant|USER
name|USER
parameter_list|(
name|OzoneConsts
operator|.
name|OZONE_ACL_USER_TYPE
parameter_list|)
operator|,
DECL|enumConstant|GROUP
constructor|GROUP(OzoneConsts.OZONE_ACL_GROUP_TYPE
block|)
enum|,
DECL|enumConstant|WORLD
name|WORLD
parameter_list|(
name|OzoneConsts
operator|.
name|OZONE_ACL_WORLD_TYPE
parameter_list|)
constructor_decl|;
comment|/**      * String value for this Enum.      */
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
comment|/**      * Init OzoneACLtypes enum.      *      * @param val String type for this enum.      */
DECL|method|OzoneACLType (String val)
name|OzoneACLType
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|value
operator|=
name|val
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**    * ACL rights.    */
end_comment

begin_enum
DECL|enum|OzoneACLRights
specifier|public
enum|enum
name|OzoneACLRights
block|{
DECL|enumConstant|READ
DECL|enumConstant|WRITE
DECL|enumConstant|READ_WRITE
name|READ
block|,
name|WRITE
block|,
name|READ_WRITE
block|;
comment|/**      * Returns the ACL rights based on passed in String.      *      * @param type ACL right string      *      * @return OzoneACLRights      */
DECL|method|getACLRight (String type)
specifier|public
specifier|static
name|OzoneACLRights
name|getACLRight
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ACL right cannot be empty"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|OzoneConsts
operator|.
name|OZONE_ACL_READ
case|:
return|return
name|OzoneACLRights
operator|.
name|READ
return|;
case|case
name|OzoneConsts
operator|.
name|OZONE_ACL_WRITE
case|:
return|return
name|OzoneACLRights
operator|.
name|WRITE
return|;
case|case
name|OzoneConsts
operator|.
name|OZONE_ACL_READ_WRITE
case|:
case|case
name|OzoneConsts
operator|.
name|OZONE_ACL_WRITE_READ
case|:
return|return
name|OzoneACLRights
operator|.
name|READ_WRITE
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ACL right is not recognized"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns String representation of ACL rights.      * @param acl OzoneACLRights      * @return String representation of acl      */
DECL|method|getACLRightsString (OzoneACLRights acl)
specifier|public
specifier|static
name|String
name|getACLRightsString
parameter_list|(
name|OzoneACLRights
name|acl
parameter_list|)
block|{
switch|switch
condition|(
name|acl
condition|)
block|{
case|case
name|READ
case|:
return|return
name|OzoneConsts
operator|.
name|OZONE_ACL_READ
return|;
case|case
name|WRITE
case|:
return|return
name|OzoneConsts
operator|.
name|OZONE_ACL_WRITE
return|;
case|case
name|READ_WRITE
case|:
return|return
name|OzoneConsts
operator|.
name|OZONE_ACL_READ_WRITE
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ACL right is not recognized"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

unit|}
end_unit

