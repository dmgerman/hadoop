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
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ImmutableList
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
name|conf
operator|.
name|Configuration
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
name|FileStatus
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
name|FileSystem
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
name|Path
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
name|fs
operator|.
name|permission
operator|.
name|*
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
name|DFSConfigKeys
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|INodeAttributeProvider
operator|.
name|AccessControlEnforcer
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
DECL|class|TestINodeAttributeProvider
specifier|public
class|class
name|TestINodeAttributeProvider
block|{
DECL|field|miniDFS
specifier|private
name|MiniDFSCluster
name|miniDFS
decl_stmt|;
DECL|field|CALLED
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|CALLED
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|MyAuthorizationProvider
specifier|public
specifier|static
class|class
name|MyAuthorizationProvider
extends|extends
name|INodeAttributeProvider
block|{
DECL|class|MyAccessControlEnforcer
specifier|public
specifier|static
class|class
name|MyAccessControlEnforcer
implements|implements
name|AccessControlEnforcer
block|{
annotation|@
name|Override
DECL|method|checkPermission (String fsOwner, String supergroup, UserGroupInformation ugi, INodeAttributes[] inodeAttrs, INode[] inodes, byte[][] pathByNameArr, int snapshotId, String path, int ancestorIndex, boolean doCheckOwner, FsAction ancestorAccess, FsAction parentAccess, FsAction access, FsAction subAccess, boolean ignoreEmptyDir)
specifier|public
name|void
name|checkPermission
parameter_list|(
name|String
name|fsOwner
parameter_list|,
name|String
name|supergroup
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|INodeAttributes
index|[]
name|inodeAttrs
parameter_list|,
name|INode
index|[]
name|inodes
parameter_list|,
name|byte
index|[]
index|[]
name|pathByNameArr
parameter_list|,
name|int
name|snapshotId
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|ancestorIndex
parameter_list|,
name|boolean
name|doCheckOwner
parameter_list|,
name|FsAction
name|ancestorAccess
parameter_list|,
name|FsAction
name|parentAccess
parameter_list|,
name|FsAction
name|access
parameter_list|,
name|FsAction
name|subAccess
parameter_list|,
name|boolean
name|ignoreEmptyDir
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|CALLED
operator|.
name|add
argument_list|(
literal|"checkPermission|"
operator|+
name|ancestorAccess
operator|+
literal|"|"
operator|+
name|parentAccess
operator|+
literal|"|"
operator|+
name|access
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|CALLED
operator|.
name|add
argument_list|(
literal|"start"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|CALLED
operator|.
name|add
argument_list|(
literal|"stop"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAttributes (String[] pathElements, final INodeAttributes inode)
specifier|public
name|INodeAttributes
name|getAttributes
parameter_list|(
name|String
index|[]
name|pathElements
parameter_list|,
specifier|final
name|INodeAttributes
name|inode
parameter_list|)
block|{
name|CALLED
operator|.
name|add
argument_list|(
literal|"getAttributes"
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|useDefault
init|=
name|useDefault
argument_list|(
name|pathElements
argument_list|)
decl_stmt|;
return|return
operator|new
name|INodeAttributes
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
name|inode
operator|.
name|isDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getLocalNameBytes
parameter_list|()
block|{
return|return
name|inode
operator|.
name|getLocalNameBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
operator|(
name|useDefault
operator|)
condition|?
name|inode
operator|.
name|getUserName
argument_list|()
else|:
literal|"foo"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
return|return
operator|(
name|useDefault
operator|)
condition|?
name|inode
operator|.
name|getGroupName
argument_list|()
else|:
literal|"bar"
return|;
block|}
annotation|@
name|Override
specifier|public
name|FsPermission
name|getFsPermission
parameter_list|()
block|{
return|return
operator|(
name|useDefault
operator|)
condition|?
name|inode
operator|.
name|getFsPermission
argument_list|()
else|:
operator|new
name|FsPermission
argument_list|(
name|getFsPermissionShort
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|getFsPermissionShort
parameter_list|()
block|{
return|return
operator|(
name|useDefault
operator|)
condition|?
name|inode
operator|.
name|getFsPermissionShort
argument_list|()
else|:
operator|(
name|short
operator|)
name|getPermissionLong
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPermissionLong
parameter_list|()
block|{
return|return
operator|(
name|useDefault
operator|)
condition|?
name|inode
operator|.
name|getPermissionLong
argument_list|()
else|:
literal|0770
return|;
block|}
annotation|@
name|Override
specifier|public
name|AclFeature
name|getAclFeature
parameter_list|()
block|{
name|AclFeature
name|f
decl_stmt|;
if|if
condition|(
name|useDefault
condition|)
block|{
name|f
operator|=
name|inode
operator|.
name|getAclFeature
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|AclEntry
name|acl
init|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
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
name|FsAction
operator|.
name|ALL
argument_list|)
operator|.
name|setName
argument_list|(
literal|"xxx"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|f
operator|=
operator|new
name|AclFeature
argument_list|(
name|AclEntryStatusFormat
operator|.
name|toInt
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|acl
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
annotation|@
name|Override
specifier|public
name|XAttrFeature
name|getXAttrFeature
parameter_list|()
block|{
name|XAttrFeature
name|x
decl_stmt|;
if|if
condition|(
name|useDefault
condition|)
block|{
name|x
operator|=
name|inode
operator|.
name|getXAttrFeature
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|x
operator|=
operator|new
name|XAttrFeature
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setValue
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
operator|(
name|useDefault
operator|)
condition|?
name|inode
operator|.
name|getModificationTime
argument_list|()
else|:
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getAccessTime
parameter_list|()
block|{
return|return
operator|(
name|useDefault
operator|)
condition|?
name|inode
operator|.
name|getAccessTime
argument_list|()
else|:
literal|0
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getExternalAccessControlEnforcer ( AccessControlEnforcer deafultEnforcer)
specifier|public
name|AccessControlEnforcer
name|getExternalAccessControlEnforcer
parameter_list|(
name|AccessControlEnforcer
name|deafultEnforcer
parameter_list|)
block|{
return|return
operator|new
name|MyAccessControlEnforcer
argument_list|()
return|;
block|}
DECL|method|useDefault (String[] pathElements)
specifier|private
name|boolean
name|useDefault
parameter_list|(
name|String
index|[]
name|pathElements
parameter_list|)
block|{
return|return
operator|(
name|pathElements
operator|.
name|length
operator|<
literal|2
operator|)
operator|||
operator|!
operator|(
name|pathElements
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"user"
argument_list|)
operator|&&
name|pathElements
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"authz"
argument_list|)
operator|)
return|;
block|}
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|CALLED
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_INODE_ATTRIBUTES_PROVIDER_KEY
argument_list|,
name|MyAuthorizationProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|EditLogFileOutputStream
operator|.
name|setShouldSkipFsyncForTesting
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|miniDFS
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|IOException
block|{
name|CALLED
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|miniDFS
operator|!=
literal|null
condition|)
block|{
name|miniDFS
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|miniDFS
operator|=
literal|null
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"stop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationToProvider ()
specifier|public
name|void
name|testDelegationToProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"start"
argument_list|)
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|miniDFS
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"u1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"g1"
block|}
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|miniDFS
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|CALLED
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"getAttributes"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"checkPermission|null|null|null"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"checkPermission|WRITE|null|null"
argument_list|)
argument_list|)
expr_stmt|;
name|CALLED
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"getAttributes"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"checkPermission|null|null|READ_EXECUTE"
argument_list|)
argument_list|)
expr_stmt|;
name|CALLED
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fs
operator|.
name|getAclStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"getAttributes"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|CALLED
operator|.
name|contains
argument_list|(
literal|"checkPermission|null|null|null"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomProvider ()
specifier|public
name|void
name|testCustomProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|miniDFS
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/xxx"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"supergroup"
argument_list|,
name|status
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/authz"
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/user/authz"
argument_list|)
decl_stmt|;
name|status
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|status
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0770
argument_list|)
argument_list|,
name|status
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|AclStatus
name|aclStatus
init|=
name|fs
operator|.
name|getAclStatus
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aclStatus
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|AclEntryType
operator|.
name|GROUP
argument_list|,
name|aclStatus
operator|.
name|getEntries
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"xxx"
argument_list|,
name|aclStatus
operator|.
name|getEntries
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|aclStatus
operator|.
name|getEntries
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xAttrs
init|=
name|fs
operator|.
name|getXAttrs
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|xAttrs
operator|.
name|containsKey
argument_list|(
literal|"user.test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|xAttrs
operator|.
name|get
argument_list|(
literal|"user.test"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

