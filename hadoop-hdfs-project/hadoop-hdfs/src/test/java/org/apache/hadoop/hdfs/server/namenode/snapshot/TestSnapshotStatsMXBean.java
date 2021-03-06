begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
operator|.
name|snapshot
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|hdfs
operator|.
name|DistributedFileSystem
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
DECL|class|TestSnapshotStatsMXBean
specifier|public
class|class
name|TestSnapshotStatsMXBean
block|{
comment|/**    * Test getting SnapshotStatsMXBean information    */
annotation|@
name|Test
DECL|method|testSnapshotStatsMXBeanInfo ()
specifier|public
name|void
name|testSnapshotStatsMXBeanInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|String
name|pathName
init|=
literal|"/snapshot"
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|pathName
argument_list|)
decl_stmt|;
try|try
block|{
name|cluster
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
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|SnapshotManager
name|sm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSnapshotManager
argument_list|()
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|createSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=NameNode,name=SnapshotInfo"
argument_list|)
decl_stmt|;
name|CompositeData
index|[]
name|directories
init|=
operator|(
name|CompositeData
index|[]
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"SnapshottableDirectories"
argument_list|)
decl_stmt|;
name|int
name|numDirectories
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|directories
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sm
operator|.
name|getNumSnapshottableDirs
argument_list|()
argument_list|,
name|numDirectories
argument_list|)
expr_stmt|;
name|CompositeData
index|[]
name|snapshots
init|=
operator|(
name|CompositeData
index|[]
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"Snapshots"
argument_list|)
decl_stmt|;
name|int
name|numSnapshots
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|snapshots
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sm
operator|.
name|getNumSnapshots
argument_list|()
argument_list|,
name|numSnapshots
argument_list|)
expr_stmt|;
name|CompositeData
name|d
init|=
operator|(
name|CompositeData
operator|)
name|Array
operator|.
name|get
argument_list|(
name|directories
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|CompositeData
name|s
init|=
operator|(
name|CompositeData
operator|)
name|Array
operator|.
name|get
argument_list|(
name|snapshots
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|String
operator|)
name|d
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
operator|)
operator|.
name|contains
argument_list|(
name|pathName
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|String
operator|)
name|s
operator|.
name|get
argument_list|(
literal|"snapshotDirectory"
argument_list|)
operator|)
operator|.
name|contains
argument_list|(
name|pathName
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

