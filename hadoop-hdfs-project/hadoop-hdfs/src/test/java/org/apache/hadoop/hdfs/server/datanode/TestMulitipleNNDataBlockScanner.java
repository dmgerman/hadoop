begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
DECL|class|TestMulitipleNNDataBlockScanner
specifier|public
class|class
name|TestMulitipleNNDataBlockScanner
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestMulitipleNNDataBlockScanner
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|bpids
name|String
name|bpids
index|[]
init|=
operator|new
name|String
index|[
literal|3
index|]
decl_stmt|;
DECL|field|fs
name|FileSystem
name|fs
index|[]
init|=
operator|new
name|FileSystem
index|[
literal|3
index|]
decl_stmt|;
DECL|method|setUp (int port)
specifier|public
name|void
name|setUp
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
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
name|numNameNodes
argument_list|(
literal|3
argument_list|)
operator|.
name|nameNodePort
argument_list|(
name|port
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|waitActive
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|bpids
index|[
name|i
index|]
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|i
argument_list|)
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|fs
index|[
name|i
index|]
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// Create 2 files on each namenode with 10 blocks each
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
index|[
name|i
index|]
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file1"
argument_list|)
argument_list|,
literal|1000
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
index|[
name|i
index|]
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file2"
argument_list|)
argument_list|,
literal|1000
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDataBlockScanner ()
specifier|public
name|void
name|testDataBlockScanner
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|setUp
argument_list|(
literal|9923
argument_list|)
expr_stmt|;
try|try
block|{
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|long
name|blocksScanned
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|blocksScanned
operator|!=
literal|20
condition|)
block|{
name|blocksScanned
operator|=
name|dn
operator|.
name|blockScanner
operator|.
name|getBlocksScannedInLastRun
argument_list|(
name|bpids
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for all blocks to be scanned for bpid="
operator|+
name|bpids
index|[
name|i
index|]
operator|+
literal|"; Scanned so far="
operator|+
name|blocksScanned
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|dn
operator|.
name|blockScanner
operator|.
name|printBlockReport
argument_list|(
name|buffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Block Report\n"
operator|+
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockScannerAfterRefresh ()
specifier|public
name|void
name|testBlockScannerAfterRefresh
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|setUp
argument_list|(
literal|9933
argument_list|)
expr_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|namenodesBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|bpidToShutdown
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|2
argument_list|)
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|String
name|nsId
init|=
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|namenodesBuilder
operator|.
name|append
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|namenodesBuilder
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
name|namenodesBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|dn
operator|.
name|refreshNamenodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|dn
operator|.
name|blockScanner
operator|.
name|getBlocksScannedInLastRun
argument_list|(
name|bpidToShutdown
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// Expected
name|LOG
operator|.
name|info
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|namenodesBuilder
operator|.
name|append
argument_list|(
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
name|namenodesBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dn
operator|.
name|refreshNamenodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|long
name|blocksScanned
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|blocksScanned
operator|!=
literal|20
condition|)
block|{
name|blocksScanned
operator|=
name|dn
operator|.
name|blockScanner
operator|.
name|getBlocksScannedInLastRun
argument_list|(
name|bpids
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for all blocks to be scanned for bpid="
operator|+
name|bpids
index|[
name|i
index|]
operator|+
literal|"; Scanned so far="
operator|+
name|blocksScanned
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockScannerAfterRestart ()
specifier|public
name|void
name|testBlockScannerAfterRestart
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|setUp
argument_list|(
literal|9943
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|restartDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|long
name|blocksScanned
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|blocksScanned
operator|!=
literal|20
condition|)
block|{
if|if
condition|(
name|dn
operator|.
name|blockScanner
operator|!=
literal|null
condition|)
block|{
name|blocksScanned
operator|=
name|dn
operator|.
name|blockScanner
operator|.
name|getBlocksScannedInLastRun
argument_list|(
name|bpids
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for all blocks to be scanned for bpid="
operator|+
name|bpids
index|[
name|i
index|]
operator|+
literal|"; Scanned so far="
operator|+
name|blocksScanned
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

