begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.bkjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|bkjournal
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
name|assertTrue
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
name|fail
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
name|bookkeeper
operator|.
name|client
operator|.
name|BookKeeper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|bookkeeper
operator|.
name|client
operator|.
name|LedgerHandle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|bookkeeper
operator|.
name|conf
operator|.
name|ClientConfiguration
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooKeeper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
comment|/**  * Unit test for the bkjm's streams  */
end_comment

begin_class
DECL|class|TestBookKeeperEditLogStreams
specifier|public
class|class
name|TestBookKeeperEditLogStreams
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestBookKeeperEditLogStreams
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bkutil
specifier|private
specifier|static
name|BKJMUtil
name|bkutil
decl_stmt|;
DECL|field|numBookies
specifier|private
specifier|final
specifier|static
name|int
name|numBookies
init|=
literal|3
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupBookkeeper ()
specifier|public
specifier|static
name|void
name|setupBookkeeper
parameter_list|()
throws|throws
name|Exception
block|{
name|bkutil
operator|=
operator|new
name|BKJMUtil
argument_list|(
name|numBookies
argument_list|)
expr_stmt|;
name|bkutil
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownBookkeeper ()
specifier|public
specifier|static
name|void
name|teardownBookkeeper
parameter_list|()
throws|throws
name|Exception
block|{
name|bkutil
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that bkjm will refuse open a stream on an empty    * ledger.    */
annotation|@
name|Test
DECL|method|testEmptyInputStream ()
specifier|public
name|void
name|testEmptyInputStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ZooKeeper
name|zk
init|=
name|BKJMUtil
operator|.
name|connectZooKeeper
argument_list|()
decl_stmt|;
name|BookKeeper
name|bkc
init|=
operator|new
name|BookKeeper
argument_list|(
operator|new
name|ClientConfiguration
argument_list|()
argument_list|,
name|zk
argument_list|)
decl_stmt|;
try|try
block|{
name|LedgerHandle
name|lh
init|=
name|bkc
operator|.
name|createLedger
argument_list|(
name|BookKeeper
operator|.
name|DigestType
operator|.
name|CRC32
argument_list|,
literal|"foobar"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|lh
operator|.
name|close
argument_list|()
expr_stmt|;
name|EditLogLedgerMetadata
name|metadata
init|=
operator|new
name|EditLogLedgerMetadata
argument_list|(
literal|"/foobar"
argument_list|,
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
argument_list|,
name|lh
operator|.
name|getId
argument_list|()
argument_list|,
literal|0x1234
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|BookKeeperEditLogInputStream
argument_list|(
name|lh
argument_list|,
name|metadata
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't get this far, should have thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid first bk entry to read"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|metadata
operator|=
operator|new
name|EditLogLedgerMetadata
argument_list|(
literal|"/foobar"
argument_list|,
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
argument_list|,
name|lh
operator|.
name|getId
argument_list|()
argument_list|,
literal|0x1234
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|BookKeeperEditLogInputStream
argument_list|(
name|lh
argument_list|,
name|metadata
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't get this far, should have thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid first bk entry to read"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|bkc
operator|.
name|close
argument_list|()
expr_stmt|;
name|zk
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

