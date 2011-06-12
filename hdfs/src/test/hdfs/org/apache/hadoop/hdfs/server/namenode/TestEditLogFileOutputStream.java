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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|DU
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestEditLogFileOutputStream
specifier|public
class|class
name|TestEditLogFileOutputStream
block|{
annotation|@
name|Test
DECL|method|testPreallocation ()
specifier|public
name|void
name|testPreallocation
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|File
name|editLog
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getEditLog
argument_list|()
operator|.
name|getFsEditName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Edit log should only be 4 bytes long"
argument_list|,
literal|4
argument_list|,
name|editLog
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Edit log disk space used should be one block"
argument_list|,
literal|4096
argument_list|,
operator|new
name|DU
argument_list|(
name|editLog
argument_list|,
name|conf
argument_list|)
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
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
literal|777
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Edit log should be 1MB + 4 bytes long"
argument_list|,
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|+
literal|4
argument_list|,
name|editLog
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// 256 blocks for the 1MB of preallocation space, 1 block for the original
comment|// 4 bytes
name|assertTrue
argument_list|(
literal|"Edit log disk space used should be at least 257 blocks"
argument_list|,
literal|257
operator|*
literal|4096
operator|<=
operator|new
name|DU
argument_list|(
name|editLog
argument_list|,
name|conf
argument_list|)
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

