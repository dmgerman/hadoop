begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_comment
comment|/**  * HDFS permission subclass used to indicate an ACL is present.  The ACL bit is  * not visible directly to users of {@link FsPermission} serialization.  This is  * done for backwards compatibility in case any existing clients assume the  * value of FsPermission is in a particular range.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FsAclPermission
specifier|public
class|class
name|FsAclPermission
extends|extends
name|FsPermission
block|{
DECL|field|ACL_BIT
specifier|private
specifier|final
specifier|static
name|short
name|ACL_BIT
init|=
literal|1
operator|<<
literal|12
decl_stmt|;
DECL|field|aclBit
specifier|private
specifier|final
name|boolean
name|aclBit
decl_stmt|;
comment|/**    * Constructs a new FsAclPermission based on the given FsPermission.    *    * @param perm FsPermission containing permission bits    */
DECL|method|FsAclPermission (FsPermission perm)
specifier|public
name|FsAclPermission
parameter_list|(
name|FsPermission
name|perm
parameter_list|)
block|{
name|super
argument_list|(
name|perm
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
name|aclBit
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Creates a new FsAclPermission by calling the base class constructor.    *    * @param perm short containing permission bits    */
DECL|method|FsAclPermission (short perm)
specifier|public
name|FsAclPermission
parameter_list|(
name|short
name|perm
parameter_list|)
block|{
name|super
argument_list|(
name|perm
argument_list|)
expr_stmt|;
name|aclBit
operator|=
operator|(
name|perm
operator|&
name|ACL_BIT
operator|)
operator|!=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toExtendedShort ()
specifier|public
name|short
name|toExtendedShort
parameter_list|()
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|toShort
argument_list|()
operator||
operator|(
name|aclBit
condition|?
name|ACL_BIT
else|:
literal|0
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAclBit ()
specifier|public
name|boolean
name|getAclBit
parameter_list|()
block|{
return|return
name|aclBit
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
comment|// This intentionally delegates to the base class.  This is only overridden
comment|// to suppress a FindBugs warning.
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
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
comment|// This intentionally delegates to the base class.  This is only overridden
comment|// to suppress a FindBugs warning.
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

