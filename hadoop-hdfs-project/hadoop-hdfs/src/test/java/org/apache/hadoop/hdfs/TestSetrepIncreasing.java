begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
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
name|server
operator|.
name|datanode
operator|.
name|SimulatedFSDataset
import|;
end_import

begin_class
DECL|class|TestSetrepIncreasing
specifier|public
class|class
name|TestSetrepIncreasing
extends|extends
name|TestCase
block|{
DECL|method|setrep (int fromREP, int toREP, boolean simulatedStorage)
specifier|static
name|void
name|setrep
parameter_list|(
name|int
name|fromREP
parameter_list|,
name|int
name|toREP
parameter_list|,
name|boolean
name|simulatedStorage
parameter_list|)
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
if|if
condition|(
name|simulatedStorage
condition|)
block|{
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
literal|""
operator|+
name|fromREP
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
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
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not a HDFS: "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|instanceof
name|DistributedFileSystem
argument_list|)
expr_stmt|;
try|try
block|{
name|Path
name|root
init|=
name|TestDFSShell
operator|.
name|mkdir
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/test/setrep"
operator|+
name|fromREP
operator|+
literal|"-"
operator|+
name|toREP
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|f
init|=
name|TestDFSShell
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Verify setrep for changing replication
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-setrep"
block|,
literal|"-w"
block|,
literal|""
operator|+
name|toREP
block|,
literal|""
operator|+
name|f
block|}
decl_stmt|;
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|()
decl_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|shell
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"-setrep "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|//get fs again since the old one may be closed
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|FileStatus
name|file
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|long
name|len
init|=
name|file
operator|.
name|getLen
argument_list|()
decl_stmt|;
for|for
control|(
name|BlockLocation
name|locations
range|:
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|file
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|locations
operator|.
name|getHosts
argument_list|()
operator|.
name|length
operator|==
name|toREP
argument_list|)
expr_stmt|;
block|}
name|TestDFSShell
operator|.
name|show
argument_list|(
literal|"done setrep waiting: "
operator|+
name|root
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSetrepIncreasing ()
specifier|public
name|void
name|testSetrepIncreasing
parameter_list|()
throws|throws
name|IOException
block|{
name|setrep
argument_list|(
literal|3
argument_list|,
literal|7
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetrepIncreasingSimulatedStorage ()
specifier|public
name|void
name|testSetrepIncreasingSimulatedStorage
parameter_list|()
throws|throws
name|IOException
block|{
name|setrep
argument_list|(
literal|3
argument_list|,
literal|7
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

