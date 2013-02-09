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
block|}
end_class

end_unit

