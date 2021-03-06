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
name|util
operator|.
name|Map
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
name|permission
operator|.
name|AclEntry
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
name|AclEntryScope
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
name|AclEntryType
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
name|AclStatus
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Validate resolver assigning all paths to a single owner/group.  */
end_comment

begin_class
DECL|class|TestSingleUGIResolver
specifier|public
class|class
name|TestSingleUGIResolver
block|{
DECL|field|name
annotation|@
name|Rule
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
DECL|field|TESTUID
specifier|private
specifier|static
specifier|final
name|int
name|TESTUID
init|=
literal|10101
decl_stmt|;
DECL|field|TESTGID
specifier|private
specifier|static
specifier|final
name|int
name|TESTGID
init|=
literal|10102
decl_stmt|;
DECL|field|TESTUSER
specifier|private
specifier|static
specifier|final
name|String
name|TESTUSER
init|=
literal|"tenaqvyybdhragqvatbf"
decl_stmt|;
DECL|field|TESTGROUP
specifier|private
specifier|static
specifier|final
name|String
name|TESTGROUP
init|=
literal|"tnyybcvatlnxf"
decl_stmt|;
DECL|field|ugi
specifier|private
name|SingleUGIResolver
name|ugi
init|=
operator|new
name|SingleUGIResolver
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|SingleUGIResolver
operator|.
name|UID
argument_list|,
name|TESTUID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|SingleUGIResolver
operator|.
name|GID
argument_list|,
name|TESTGID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SingleUGIResolver
operator|.
name|USER
argument_list|,
name|TESTUSER
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SingleUGIResolver
operator|.
name|GROUP
argument_list|,
name|TESTGROUP
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|name
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRewrite ()
specifier|public
name|void
name|testRewrite
parameter_list|()
block|{
name|FsPermission
name|p1
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
decl_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
literal|"dingo"
argument_list|,
literal|"dingo"
argument_list|,
name|p1
argument_list|)
argument_list|)
argument_list|,
name|p1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
name|TESTUSER
argument_list|,
literal|"dingo"
argument_list|,
name|p1
argument_list|)
argument_list|)
argument_list|,
name|p1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
literal|"dingo"
argument_list|,
name|TESTGROUP
argument_list|,
name|p1
argument_list|)
argument_list|)
argument_list|,
name|p1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
name|TESTUSER
argument_list|,
name|TESTGROUP
argument_list|,
name|p1
argument_list|)
argument_list|)
argument_list|,
name|p1
argument_list|)
expr_stmt|;
name|FsPermission
name|p2
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0x8000
argument_list|)
decl_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
literal|"dingo"
argument_list|,
literal|"dingo"
argument_list|,
name|p2
argument_list|)
argument_list|)
argument_list|,
name|p2
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
name|TESTUSER
argument_list|,
literal|"dingo"
argument_list|,
name|p2
argument_list|)
argument_list|)
argument_list|,
name|p2
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
literal|"dingo"
argument_list|,
name|TESTGROUP
argument_list|,
name|p2
argument_list|)
argument_list|)
argument_list|,
name|p2
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
name|TESTUSER
argument_list|,
name|TESTGROUP
argument_list|,
name|p2
argument_list|)
argument_list|)
argument_list|,
name|p2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|ids
init|=
name|ugi
operator|.
name|ugiMap
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TESTUSER
argument_list|,
name|ids
operator|.
name|get
argument_list|(
literal|10101
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TESTGROUP
argument_list|,
name|ids
operator|.
name|get
argument_list|(
literal|10102
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefault ()
specifier|public
name|void
name|testDefault
parameter_list|()
block|{
name|String
name|user
decl_stmt|;
try|try
block|{
name|user
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|user
operator|=
literal|"hadoop"
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|ids
init|=
name|ugi
operator|.
name|ugiMap
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|ids
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|ids
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAclResolution ()
specifier|public
name|void
name|testAclResolution
parameter_list|()
block|{
name|long
name|perm
decl_stmt|;
name|FsPermission
name|p1
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
name|file
argument_list|(
literal|"dingo"
argument_list|,
literal|"dingo"
argument_list|,
name|p1
argument_list|)
decl_stmt|;
name|perm
operator|=
name|ugi
operator|.
name|getPermissionsProto
argument_list|(
name|fileStatus
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|perm
argument_list|,
name|p1
argument_list|)
expr_stmt|;
name|AclEntry
name|aclEntry
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
name|USER
argument_list|)
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
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
literal|"dingo"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AclStatus
name|aclStatus
init|=
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
operator|.
name|owner
argument_list|(
literal|"dingo"
argument_list|)
operator|.
name|group
argument_list|(
operator|(
literal|"dingo"
operator|)
argument_list|)
operator|.
name|addEntry
argument_list|(
name|aclEntry
argument_list|)
operator|.
name|setPermission
argument_list|(
name|p1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|perm
operator|=
name|ugi
operator|.
name|getPermissionsProto
argument_list|(
literal|null
argument_list|,
name|aclStatus
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|perm
argument_list|,
name|p1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testInvalidUid ()
specifier|public
name|void
name|testInvalidUid
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|ugi
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|SingleUGIResolver
operator|.
name|UID
argument_list|,
operator|(
literal|1
operator|<<
literal|24
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
name|TESTUSER
argument_list|,
name|TESTGROUP
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testInvalidGid ()
specifier|public
name|void
name|testInvalidGid
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|ugi
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|SingleUGIResolver
operator|.
name|GID
argument_list|,
operator|(
literal|1
operator|<<
literal|24
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|resolve
argument_list|(
name|file
argument_list|(
name|TESTUSER
argument_list|,
name|TESTGROUP
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
DECL|method|testDuplicateIds ()
specifier|public
name|void
name|testDuplicateIds
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|SingleUGIResolver
operator|.
name|UID
argument_list|,
literal|4344
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|SingleUGIResolver
operator|.
name|GID
argument_list|,
literal|4344
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SingleUGIResolver
operator|.
name|USER
argument_list|,
name|TESTUSER
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SingleUGIResolver
operator|.
name|GROUP
argument_list|,
name|TESTGROUP
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|ugiMap
argument_list|()
expr_stmt|;
block|}
DECL|method|match (long encoded, FsPermission p)
specifier|static
name|void
name|match
parameter_list|(
name|long
name|encoded
parameter_list|,
name|FsPermission
name|p
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|p
argument_list|,
operator|new
name|FsPermission
argument_list|(
call|(
name|short
call|)
argument_list|(
name|encoded
operator|&
literal|0xFFFF
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|uid
init|=
operator|(
name|encoded
operator|>>>
name|UGIResolver
operator|.
name|USER_STRID_OFFSET
operator|)
decl_stmt|;
name|uid
operator|&=
name|UGIResolver
operator|.
name|USER_GROUP_STRID_MASK
expr_stmt|;
name|assertEquals
argument_list|(
name|TESTUID
argument_list|,
name|uid
argument_list|)
expr_stmt|;
name|long
name|gid
init|=
operator|(
name|encoded
operator|>>>
name|UGIResolver
operator|.
name|GROUP_STRID_OFFSET
operator|)
decl_stmt|;
name|gid
operator|&=
name|UGIResolver
operator|.
name|USER_GROUP_STRID_MASK
expr_stmt|;
name|assertEquals
argument_list|(
name|TESTGID
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
DECL|method|file (String user, String group, FsPermission perm)
specifier|static
name|FileStatus
name|file
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|group
parameter_list|,
name|FsPermission
name|perm
parameter_list|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"foo://bar:4344/baz/dingo"
argument_list|)
decl_stmt|;
return|return
operator|new
name|FileStatus
argument_list|(
literal|4344
operator|*
operator|(
literal|1
operator|<<
literal|20
operator|)
argument_list|,
comment|/* long length,             */
literal|false
argument_list|,
comment|/* boolean isdir,           */
literal|1
argument_list|,
comment|/* int block_replication,   */
literal|256
operator|*
operator|(
literal|1
operator|<<
literal|20
operator|)
argument_list|,
comment|/* long blocksize,          */
literal|0L
argument_list|,
comment|/* long modification_time,  */
literal|0L
argument_list|,
comment|/* long access_time,        */
name|perm
argument_list|,
comment|/* FsPermission permission, */
name|user
argument_list|,
comment|/* String owner,            */
name|group
argument_list|,
comment|/* String group,            */
name|p
argument_list|)
return|;
comment|/* Path path                */
block|}
block|}
end_class

end_unit

