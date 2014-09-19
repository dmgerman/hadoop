begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
package|;
end_package

begin_import
import|import static
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
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
name|AclTestHelpers
operator|.
name|*
import|;
end_import

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
name|HashMap
import|;
end_import

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
name|Map
operator|.
name|Entry
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
name|XAttrCodec
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
name|DFSUtil
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
name|XAttrHelper
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
name|DatanodeInfo
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
name|HdfsFileStatus
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
name|INodeId
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
name|Time
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
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
DECL|class|TestJsonUtil
specifier|public
class|class
name|TestJsonUtil
block|{
DECL|method|toFileStatus (HdfsFileStatus f, String parent)
specifier|static
name|FileStatus
name|toFileStatus
parameter_list|(
name|HdfsFileStatus
name|f
parameter_list|,
name|String
name|parent
parameter_list|)
block|{
return|return
operator|new
name|FileStatus
argument_list|(
name|f
operator|.
name|getLen
argument_list|()
argument_list|,
name|f
operator|.
name|isDir
argument_list|()
argument_list|,
name|f
operator|.
name|getReplication
argument_list|()
argument_list|,
name|f
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|f
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|f
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|f
operator|.
name|getPermission
argument_list|()
argument_list|,
name|f
operator|.
name|getOwner
argument_list|()
argument_list|,
name|f
operator|.
name|getGroup
argument_list|()
argument_list|,
name|f
operator|.
name|isSymlink
argument_list|()
condition|?
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getSymlink
argument_list|()
argument_list|)
else|:
literal|null
argument_list|,
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getFullName
argument_list|(
name|parent
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testHdfsFileStatus ()
specifier|public
name|void
name|testHdfsFileStatus
parameter_list|()
block|{
specifier|final
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
specifier|final
name|String
name|parent
init|=
literal|"/dir"
decl_stmt|;
specifier|final
name|HdfsFileStatus
name|status
init|=
operator|new
name|HdfsFileStatus
argument_list|(
literal|1001L
argument_list|,
literal|false
argument_list|,
literal|3
argument_list|,
literal|1L
operator|<<
literal|26
argument_list|,
literal|false
argument_list|,
name|now
argument_list|,
name|now
operator|+
literal|10
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0644
argument_list|)
argument_list|,
literal|"user"
argument_list|,
literal|"group"
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|FileStatus
name|fstatus
init|=
name|toFileStatus
argument_list|(
name|status
argument_list|,
name|parent
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"status  = "
operator|+
name|status
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fstatus = "
operator|+
name|fstatus
argument_list|)
expr_stmt|;
specifier|final
name|String
name|json
init|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|status
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"json    = "
operator|+
name|json
operator|.
name|replace
argument_list|(
literal|","
argument_list|,
literal|",\n  "
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|HdfsFileStatus
name|s2
init|=
name|JsonUtil
operator|.
name|toFileStatus
argument_list|(
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|json
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FileStatus
name|fs2
init|=
name|toFileStatus
argument_list|(
name|s2
argument_list|,
name|parent
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"s2      = "
operator|+
name|s2
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fs2     = "
operator|+
name|fs2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fstatus
argument_list|,
name|fs2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToDatanodeInfoWithoutSecurePort ()
specifier|public
name|void
name|testToDatanodeInfoWithoutSecurePort
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"ipAddr"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"hostName"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"storageID"
argument_list|,
literal|"fake-id"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"xferPort"
argument_list|,
literal|1337l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"infoPort"
argument_list|,
literal|1338l
argument_list|)
expr_stmt|;
comment|// deliberately don't include an entry for "infoSecurePort"
name|response
operator|.
name|put
argument_list|(
literal|"ipcPort"
argument_list|,
literal|1339l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"capacity"
argument_list|,
literal|1024l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"dfsUsed"
argument_list|,
literal|512l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"remaining"
argument_list|,
literal|512l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"blockPoolUsed"
argument_list|,
literal|512l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"lastUpdate"
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"xceiverCount"
argument_list|,
literal|4096l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"networkLocation"
argument_list|,
literal|"foo.bar.baz"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"adminState"
argument_list|,
literal|"NORMAL"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"cacheCapacity"
argument_list|,
literal|123l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"cacheUsed"
argument_list|,
literal|321l
argument_list|)
expr_stmt|;
name|JsonUtil
operator|.
name|toDatanodeInfo
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToDatanodeInfoWithName ()
specifier|public
name|void
name|testToDatanodeInfoWithName
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|// Older servers (1.x, 0.23, etc.) sends 'name' instead of ipAddr
comment|// and xferPort.
name|String
name|name
init|=
literal|"127.0.0.1:1004"
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"hostName"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"storageID"
argument_list|,
literal|"fake-id"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"infoPort"
argument_list|,
literal|1338l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"ipcPort"
argument_list|,
literal|1339l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"capacity"
argument_list|,
literal|1024l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"dfsUsed"
argument_list|,
literal|512l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"remaining"
argument_list|,
literal|512l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"blockPoolUsed"
argument_list|,
literal|512l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"lastUpdate"
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"xceiverCount"
argument_list|,
literal|4096l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"networkLocation"
argument_list|,
literal|"foo.bar.baz"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"adminState"
argument_list|,
literal|"NORMAL"
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"cacheCapacity"
argument_list|,
literal|123l
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"cacheUsed"
argument_list|,
literal|321l
argument_list|)
expr_stmt|;
name|DatanodeInfo
name|di
init|=
name|JsonUtil
operator|.
name|toDatanodeInfo
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|name
argument_list|,
name|di
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// The encoded result should contain name, ipAddr and xferPort.
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|JsonUtil
operator|.
name|toJsonMap
argument_list|(
name|di
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|name
argument_list|,
name|r
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|r
operator|.
name|get
argument_list|(
literal|"ipAddr"
argument_list|)
argument_list|)
expr_stmt|;
comment|// In this test, it is Integer instead of Long since json was not actually
comment|// involved in constructing the map.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1004
argument_list|,
call|(
name|int
call|)
argument_list|(
name|Integer
argument_list|)
name|r
operator|.
name|get
argument_list|(
literal|"xferPort"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Invalid names
name|String
index|[]
name|badNames
init|=
block|{
literal|"127.0.0.1"
block|,
literal|"127.0.0.1:"
block|,
literal|":"
block|,
literal|"127.0.0.1:sweet"
block|,
literal|":123"
block|}
decl_stmt|;
for|for
control|(
name|String
name|badName
range|:
name|badNames
control|)
block|{
name|response
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|badName
argument_list|)
expr_stmt|;
name|checkDecodeFailure
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|// Missing both name and ipAddr
name|response
operator|.
name|remove
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|checkDecodeFailure
argument_list|(
name|response
argument_list|)
expr_stmt|;
comment|// Only missing xferPort
name|response
operator|.
name|put
argument_list|(
literal|"ipAddr"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|checkDecodeFailure
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToAclStatus ()
specifier|public
name|void
name|testToAclStatus
parameter_list|()
block|{
name|String
name|jsonString
init|=
literal|"{\"AclStatus\":{\"entries\":[\"user::rwx\",\"user:user1:rw-\",\"group::rw-\",\"other::r-x\"],\"group\":\"supergroup\",\"owner\":\"testuser\",\"stickyBit\":false}}"
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|json
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"user1"
argument_list|,
name|READ_WRITE
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ_WRITE
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|OTHER
argument_list|,
name|READ_EXECUTE
argument_list|)
argument_list|)
decl_stmt|;
name|AclStatus
operator|.
name|Builder
name|aclStatusBuilder
init|=
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|aclStatusBuilder
operator|.
name|owner
argument_list|(
literal|"testuser"
argument_list|)
expr_stmt|;
name|aclStatusBuilder
operator|.
name|group
argument_list|(
literal|"supergroup"
argument_list|)
expr_stmt|;
name|aclStatusBuilder
operator|.
name|addEntries
argument_list|(
name|aclSpec
argument_list|)
expr_stmt|;
name|aclStatusBuilder
operator|.
name|stickyBit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Should be equal"
argument_list|,
name|aclStatusBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|JsonUtil
operator|.
name|toAclStatus
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToJsonFromAclStatus ()
specifier|public
name|void
name|testToJsonFromAclStatus
parameter_list|()
block|{
name|String
name|jsonString
init|=
literal|"{\"AclStatus\":{\"entries\":[\"user:user1:rwx\",\"group::rw-\"],\"group\":\"supergroup\",\"owner\":\"testuser\",\"stickyBit\":false}}"
decl_stmt|;
name|AclStatus
operator|.
name|Builder
name|aclStatusBuilder
init|=
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|aclStatusBuilder
operator|.
name|owner
argument_list|(
literal|"testuser"
argument_list|)
expr_stmt|;
name|aclStatusBuilder
operator|.
name|group
argument_list|(
literal|"supergroup"
argument_list|)
expr_stmt|;
name|aclStatusBuilder
operator|.
name|stickyBit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|USER
argument_list|,
literal|"user1"
argument_list|,
name|ALL
argument_list|)
argument_list|,
name|aclEntry
argument_list|(
name|ACCESS
argument_list|,
name|GROUP
argument_list|,
name|READ_WRITE
argument_list|)
argument_list|)
decl_stmt|;
name|aclStatusBuilder
operator|.
name|addEntries
argument_list|(
name|aclSpec
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|jsonString
argument_list|,
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|aclStatusBuilder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToJsonFromXAttrs ()
specifier|public
name|void
name|testToJsonFromXAttrs
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|jsonString
init|=
literal|"{\"XAttrs\":[{\"name\":\"user.a1\",\"value\":\"0x313233\"},"
operator|+
literal|"{\"name\":\"user.a2\",\"value\":\"0x313131\"}]}"
decl_stmt|;
name|XAttr
name|xAttr1
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a1"
argument_list|)
operator|.
name|setValue
argument_list|(
name|XAttrCodec
operator|.
name|decodeValue
argument_list|(
literal|"0x313233"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|XAttr
name|xAttr2
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a2"
argument_list|)
operator|.
name|setValue
argument_list|(
name|XAttrCodec
operator|.
name|decodeValue
argument_list|(
literal|"0x313131"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|xAttr1
argument_list|)
expr_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|xAttr2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|jsonString
argument_list|,
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|xAttrs
argument_list|,
name|XAttrCodec
operator|.
name|HEX
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToXAttrMap ()
specifier|public
name|void
name|testToXAttrMap
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|jsonString
init|=
literal|"{\"XAttrs\":[{\"name\":\"user.a1\",\"value\":\"0x313233\"},"
operator|+
literal|"{\"name\":\"user.a2\",\"value\":\"0x313131\"}]}"
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|json
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
name|XAttr
name|xAttr1
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a1"
argument_list|)
operator|.
name|setValue
argument_list|(
name|XAttrCodec
operator|.
name|decodeValue
argument_list|(
literal|"0x313233"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|XAttr
name|xAttr2
init|=
operator|(
operator|new
name|XAttr
operator|.
name|Builder
argument_list|()
operator|)
operator|.
name|setNameSpace
argument_list|(
name|XAttr
operator|.
name|NameSpace
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"a2"
argument_list|)
operator|.
name|setValue
argument_list|(
name|XAttrCodec
operator|.
name|decodeValue
argument_list|(
literal|"0x313131"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|xAttr1
argument_list|)
expr_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|xAttr2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|xAttrMap
init|=
name|XAttrHelper
operator|.
name|buildXAttrMap
argument_list|(
name|xAttrs
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|parsedXAttrMap
init|=
name|JsonUtil
operator|.
name|toXAttrs
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xAttrMap
operator|.
name|size
argument_list|()
argument_list|,
name|parsedXAttrMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|iter
init|=
name|xAttrMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|parsedXAttrMap
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetXAttrFromJson ()
specifier|public
name|void
name|testGetXAttrFromJson
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|jsonString
init|=
literal|"{\"XAttrs\":[{\"name\":\"user.a1\",\"value\":\"0x313233\"},"
operator|+
literal|"{\"name\":\"user.a2\",\"value\":\"0x313131\"}]}"
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|json
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
comment|// Get xattr: user.a2
name|byte
index|[]
name|value
init|=
name|JsonUtil
operator|.
name|getXAttr
argument_list|(
name|json
argument_list|,
literal|"user.a2"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|XAttrCodec
operator|.
name|decodeValue
argument_list|(
literal|"0x313131"
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|checkDecodeFailure (Map<String, Object> map)
specifier|private
name|void
name|checkDecodeFailure
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
try|try
block|{
name|JsonUtil
operator|.
name|toDatanodeInfo
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception not thrown against bad input."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

