begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
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
name|FSDataOutputStream
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
name|DFSTestUtil
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
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
name|DataNode
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|io
operator|.
name|IOUtils
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|PrintStream
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
operator|.
name|FsDatasetTestUtil
operator|.
name|*
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
name|assertEquals
import|;
end_import

begin_class
DECL|class|TestDebugAdmin
specifier|public
class|class
name|TestDebugAdmin
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|admin
specifier|private
name|DebugAdmin
name|admin
decl_stmt|;
DECL|field|datanode
specifier|private
name|DataNode
name|datanode
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
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
name|numDataNodes
argument_list|(
literal|1
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|admin
operator|=
operator|new
name|DebugAdmin
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|datanode
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|runCmd (String[] cmd)
specifier|private
name|String
name|runCmd
parameter_list|(
name|String
index|[]
name|cmd
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
specifier|final
name|PrintStream
name|oldErr
init|=
name|System
operator|.
name|err
decl_stmt|;
specifier|final
name|PrintStream
name|oldOut
init|=
name|System
operator|.
name|out
decl_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|int
name|ret
decl_stmt|;
try|try
block|{
name|ret
operator|=
name|admin
operator|.
name|run
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setErr
argument_list|(
name|oldErr
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|oldOut
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
return|return
literal|"ret: "
operator|+
name|ret
operator|+
literal|", "
operator|+
name|bytes
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testRecoverLease ()
specifier|public
name|void
name|testRecoverLease
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"ret: 1, You must supply a -path argument to recoverLease.\n"
argument_list|,
name|runCmd
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"recoverLease"
block|,
literal|"-retries"
block|,
literal|"1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ret: 0, recoverLease SUCCEEDED on /foo\n"
argument_list|,
name|runCmd
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"recoverLease"
block|,
literal|"-path"
block|,
literal|"/foo"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testVerifyBlockChecksumCommand ()
specifier|public
name|void
name|testVerifyBlockChecksumCommand
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/bar"
argument_list|)
argument_list|,
literal|1234
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xdeadbeef
argument_list|)
expr_stmt|;
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
init|=
name|datanode
operator|.
name|getFSDataset
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|blockFile
init|=
name|getBlockFile
argument_list|(
name|fsd
argument_list|,
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ret: 1, You must specify a meta file with -meta\n"
argument_list|,
name|runCmd
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"verify"
block|,
literal|"-block"
block|,
name|blockFile
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|metaFile
init|=
name|getMetaFile
argument_list|(
name|fsd
argument_list|,
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ret: 0, Checksum type: "
operator|+
literal|"DataChecksum(type=CRC32C, chunkSize=512)\n"
argument_list|,
name|runCmd
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"verify"
block|,
literal|"-meta"
block|,
name|metaFile
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ret: 0, Checksum type: "
operator|+
literal|"DataChecksum(type=CRC32C, chunkSize=512)\n"
operator|+
literal|"Checksum verification succeeded on block file "
operator|+
name|blockFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\n"
argument_list|,
name|runCmd
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"verify"
block|,
literal|"-meta"
block|,
name|metaFile
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-block"
block|,
name|blockFile
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

