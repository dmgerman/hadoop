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
name|List
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
comment|/** Name of the super user. */
DECL|field|superUser
specifier|private
specifier|final
name|String
name|superUser
decl_stmt|;
comment|/** Name of the super group. */
DECL|field|superGroup
specifier|private
specifier|final
name|String
name|superGroup
decl_stmt|;
DECL|method|RouterPermissionChecker (String user, String group, UserGroupInformation callerUgi)
specifier|public
name|RouterPermissionChecker
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|group
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
block|{
name|super
argument_list|(
name|user
argument_list|,
name|group
argument_list|,
name|callerUgi
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|superUser
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|superGroup
operator|=
name|group
expr_stmt|;
block|}
DECL|method|RouterPermissionChecker (String user, String group)
specifier|public
name|RouterPermissionChecker
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|user
argument_list|,
name|group
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|superUser
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|superGroup
operator|=
name|group
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
comment|/**    * Check the superuser privileges of the current RPC caller. This method is    * based on Datanode#checkSuperuserPrivilege().    * @throws AccessControlException If the user is not authorized.    */
annotation|@
name|Override
DECL|method|checkSuperuserPrivilege ()
specifier|public
name|void
name|checkSuperuserPrivilege
parameter_list|()
throws|throws
name|AccessControlException
block|{
comment|// Try to get the ugi in the RPC call.
name|UserGroupInformation
name|ugi
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ugi
operator|=
name|NameNode
operator|.
name|getRemoteUser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Ignore as we catch it afterwards
block|}
if|if
condition|(
name|ugi
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get the remote user name"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Cannot get the remote user name"
argument_list|)
throw|;
block|}
comment|// Is this by the Router user itself?
if|if
condition|(
name|ugi
operator|.
name|getUserName
argument_list|()
operator|.
name|equals
argument_list|(
name|superUser
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// Is the user a member of the super group?
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|ugi
operator|.
name|getGroupNames
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|groups
operator|.
name|contains
argument_list|(
name|superGroup
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// Not a superuser
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
operator|+
literal|" is not a super user"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

