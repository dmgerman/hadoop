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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH
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
name|CommonConfigurationKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|protocol
operator|.
name|BlockListAsLongs
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|BPOfferService
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
name|impl
operator|.
name|FsDatasetImplTestUtils
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
name|protocol
operator|.
name|BlockReportContext
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
name|protocol
operator|.
name|DatanodeRegistration
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
name|protocol
operator|.
name|DatanodeStorage
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
name|protocol
operator|.
name|StorageBlockReport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|BeforeClass
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

begin_comment
comment|/**  * Tests that very large block reports can pass through the RPC server and  * deserialization layers successfully if configured.  */
end_comment

begin_class
DECL|class|TestLargeBlockReport
specifier|public
class|class
name|TestLargeBlockReport
block|{
DECL|field|conf
specifier|private
specifier|final
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dn
specifier|private
name|DataNode
name|dn
decl_stmt|;
DECL|field|bpos
specifier|private
name|BPOfferService
name|bpos
decl_stmt|;
DECL|field|nnProxy
specifier|private
name|DatanodeProtocolClientSideTranslatorPB
name|nnProxy
decl_stmt|;
DECL|field|bpRegistration
specifier|private
name|DatanodeRegistration
name|bpRegistration
decl_stmt|;
DECL|field|bpId
specifier|private
name|String
name|bpId
decl_stmt|;
DECL|field|dnStorage
specifier|private
name|DatanodeStorage
name|dnStorage
decl_stmt|;
DECL|field|reportId
specifier|private
specifier|final
name|long
name|reportId
init|=
literal|1
decl_stmt|;
DECL|field|fullBrLeaseId
specifier|private
specifier|final
name|long
name|fullBrLeaseId
init|=
literal|0
decl_stmt|;
DECL|field|sorted
specifier|private
specifier|final
name|boolean
name|sorted
init|=
literal|true
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
name|DFSTestUtil
operator|.
name|setNameNodeLogLevel
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
expr_stmt|;
name|FsDatasetImplTestUtils
operator|.
name|setFsDatasetImplLogLevel
argument_list|(
name|Level
operator|.
name|WARN
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
annotation|@
name|Test
DECL|method|testBlockReportExceedsLengthLimit ()
specifier|public
name|void
name|testBlockReportExceedsLengthLimit
parameter_list|()
throws|throws
name|Exception
block|{
comment|//protobuf's default limit increased to 2GB from protobuf 3.x onwards.
comment|//So there will not be any exception thrown from protobuf.
name|conf
operator|.
name|setInt
argument_list|(
name|IPC_MAXIMUM_DATA_LENGTH
argument_list|,
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
operator|/
literal|2
argument_list|)
expr_stmt|;
name|initCluster
argument_list|()
expr_stmt|;
comment|// Create a large enough report that we expect it will go beyond the RPC
comment|// server's length validation, and also protobuf length validation.
name|StorageBlockReport
index|[]
name|reports
init|=
name|createReports
argument_list|(
literal|6000000
argument_list|)
decl_stmt|;
try|try
block|{
name|nnProxy
operator|.
name|blockReport
argument_list|(
name|bpRegistration
argument_list|,
name|bpId
argument_list|,
name|reports
argument_list|,
operator|new
name|BlockReportContext
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|reportId
argument_list|,
name|fullBrLeaseId
argument_list|,
name|sorted
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed because of the too long RPC data length"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Expected.  We can't reliably assert anything about the exception type
comment|// or the message.  The NameNode just disconnects, and the details are
comment|// buried in the NameNode log.
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockReportSucceedsWithLargerLengthLimit ()
specifier|public
name|void
name|testBlockReportSucceedsWithLargerLengthLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|IPC_MAXIMUM_DATA_LENGTH
argument_list|,
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|initCluster
argument_list|()
expr_stmt|;
name|StorageBlockReport
index|[]
name|reports
init|=
name|createReports
argument_list|(
literal|6000000
argument_list|)
decl_stmt|;
name|nnProxy
operator|.
name|blockReport
argument_list|(
name|bpRegistration
argument_list|,
name|bpId
argument_list|,
name|reports
argument_list|,
operator|new
name|BlockReportContext
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|reportId
argument_list|,
name|fullBrLeaseId
argument_list|,
name|sorted
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates storage block reports, consisting of a single report with the    * requested number of blocks.  The block data is fake, because the tests just    * need to validate that the messages can pass correctly.  This intentionally    * uses the old-style decoding method as a helper.  The test needs to cover    * the new-style encoding technique.  Passing through that code path here    * would trigger an exception before the test is ready to deal with it.    *    * @param numBlocks requested number of blocks    * @return storage block reports    */
DECL|method|createReports (int numBlocks)
specifier|private
name|StorageBlockReport
index|[]
name|createReports
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
name|int
name|longsPerBlock
init|=
literal|3
decl_stmt|;
name|int
name|blockListSize
init|=
literal|2
operator|+
name|numBlocks
operator|*
name|longsPerBlock
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|longs
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|(
name|blockListSize
argument_list|)
decl_stmt|;
name|longs
operator|.
name|add
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|numBlocks
argument_list|)
argument_list|)
expr_stmt|;
name|longs
operator|.
name|add
argument_list|(
literal|0L
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
name|blockListSize
condition|;
operator|++
name|i
control|)
block|{
name|longs
operator|.
name|add
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BlockListAsLongs
name|blockList
init|=
name|BlockListAsLongs
operator|.
name|decodeLongs
argument_list|(
name|longs
argument_list|)
decl_stmt|;
name|StorageBlockReport
index|[]
name|reports
init|=
operator|new
name|StorageBlockReport
index|[]
block|{
operator|new
name|StorageBlockReport
argument_list|(
name|dnStorage
argument_list|,
name|blockList
argument_list|)
block|}
decl_stmt|;
return|return
name|reports
return|;
block|}
comment|/**    * Start a mini-cluster, and set up everything the tests need to use it.    *    * @throws Exception if initialization fails    */
DECL|method|initCluster ()
specifier|private
name|void
name|initCluster
parameter_list|()
throws|throws
name|Exception
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
name|dn
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
name|bpos
operator|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|nnProxy
operator|=
name|bpos
operator|.
name|getActiveNN
argument_list|()
expr_stmt|;
name|bpRegistration
operator|=
name|bpos
operator|.
name|bpRegistration
expr_stmt|;
name|bpId
operator|=
name|bpos
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
name|dnStorage
operator|=
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getBlockReports
argument_list|(
name|bpId
argument_list|)
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

