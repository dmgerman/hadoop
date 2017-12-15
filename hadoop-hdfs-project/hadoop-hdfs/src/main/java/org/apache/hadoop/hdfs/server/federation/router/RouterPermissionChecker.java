begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MountTable
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
name|FSPermissionChecker
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * Class that helps in checking permissions in Router-based federation.  */
end_comment

begin_class
DECL|class|RouterPermissionChecker
specifier|public
class|class
name|RouterPermissionChecker
extends|extends
name|FSPermissionChecker
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RouterPermissionChecker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Mount table default permission. */
DECL|field|MOUNT_TABLE_PERMISSION_DEFAULT
specifier|public
specifier|static
specifier|final
name|short
name|MOUNT_TABLE_PERMISSION_DEFAULT
init|=
literal|00755
decl_stmt|;
DECL|method|RouterPermissionChecker (String routerOwner, String supergroup, UserGroupInformation callerUgi)
specifier|public
name|RouterPermissionChecker
parameter_list|(
name|String
name|routerOwner
parameter_list|,
name|String
name|supergroup
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
block|{
name|super
argument_list|(
name|routerOwner
argument_list|,
name|supergroup
argument_list|,
name|callerUgi
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Whether a mount table entry can be accessed by the current context.    *    * @param mountTable    *          MountTable being accessed    * @param access    *          type of action being performed on the cache pool    * @throws AccessControlException    *           if mount table cannot be accessed    */
DECL|method|checkPermission (MountTable mountTable, FsAction access)
specifier|public
name|void
name|checkPermission
parameter_list|(
name|MountTable
name|mountTable
parameter_list|,
name|FsAction
name|access
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|isSuperUser
argument_list|()
condition|)
block|{
return|return;
block|}
name|FsPermission
name|mode
init|=
name|mountTable
operator|.
name|getMode
argument_list|()
decl_stmt|;
if|if
condition|(
name|getUser
argument_list|()
operator|.
name|equals
argument_list|(
name|mountTable
operator|.
name|getOwnerName
argument_list|()
argument_list|)
operator|&&
name|mode
operator|.
name|getUserAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|isMemberOfGroup
argument_list|(
name|mountTable
operator|.
name|getGroupName
argument_list|()
argument_list|)
operator|&&
name|mode
operator|.
name|getGroupAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|getUser
argument_list|()
operator|.
name|equals
argument_list|(
name|mountTable
operator|.
name|getOwnerName
argument_list|()
argument_list|)
operator|&&
operator|!
name|isMemberOfGroup
argument_list|(
name|mountTable
operator|.
name|getGroupName
argument_list|()
argument_list|)
operator|&&
name|mode
operator|.
name|getOtherAction
argument_list|()
operator|.
name|implies
argument_list|(
name|access
argument_list|)
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Permission denied while accessing mount table "
operator|+
name|mountTable
operator|.
name|getSourcePath
argument_list|()
operator|+
literal|": user "
operator|+
name|getUser
argument_list|()
operator|+
literal|" does not have "
operator|+
name|access
operator|.
name|toString
argument_list|()
operator|+
literal|" permissions."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

