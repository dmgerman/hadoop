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
name|Timeout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|ReadStripedFileWithDecodingHelper
operator|.
name|BLOCK_SIZE
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
name|ReadStripedFileWithDecodingHelper
operator|.
name|FILE_LENGTHS
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
name|ReadStripedFileWithDecodingHelper
operator|.
name|NUM_DATA_UNITS
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
name|ReadStripedFileWithDecodingHelper
operator|.
name|NUM_PARITY_UNITS
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
name|ReadStripedFileWithDecodingHelper
operator|.
name|initializeCluster
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
name|ReadStripedFileWithDecodingHelper
operator|.
name|tearDownCluster
import|;
end_import

begin_comment
comment|/**  * Test online recovery with failed DNs. This test is parameterized.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestReadStripedFileWithDNFailure
specifier|public
class|class
name|TestReadStripedFileWithDNFailure
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestReadStripedFileWithDNFailure
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
specifier|static
name|DistributedFileSystem
name|dfs
decl_stmt|;
annotation|@
name|Rule
DECL|field|globalTimeout
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|=
name|initializeCluster
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|tearDownCluster
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|getParameters ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|getParameters
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|fileLength
range|:
name|FILE_LENGTHS
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_PARITY_UNITS
condition|;
name|i
operator|++
control|)
block|{
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|fileLength
block|,
name|i
operator|+
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|params
return|;
block|}
DECL|field|fileLength
specifier|private
name|int
name|fileLength
decl_stmt|;
DECL|field|dnFailureNum
specifier|private
name|int
name|dnFailureNum
decl_stmt|;
DECL|method|TestReadStripedFileWithDNFailure (int fileLength, int dnFailureNum)
specifier|public
name|TestReadStripedFileWithDNFailure
parameter_list|(
name|int
name|fileLength
parameter_list|,
name|int
name|dnFailureNum
parameter_list|)
block|{
name|this
operator|.
name|fileLength
operator|=
name|fileLength
expr_stmt|;
name|this
operator|.
name|dnFailureNum
operator|=
name|dnFailureNum
expr_stmt|;
block|}
comment|/**    * Shutdown tolerable number of Datanode before reading.    * Verify the decoding works correctly.    */
annotation|@
name|Test
DECL|method|testReadWithDNFailure ()
specifier|public
name|void
name|testReadWithDNFailure
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// setup a new cluster with no dead datanode
name|setup
argument_list|()
expr_stmt|;
name|ReadStripedFileWithDecodingHelper
operator|.
name|testReadWithDNFailure
argument_list|(
name|cluster
argument_list|,
name|dfs
argument_list|,
name|fileLength
argument_list|,
name|dnFailureNum
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|fileType
init|=
name|fileLength
operator|<
operator|(
name|BLOCK_SIZE
operator|*
name|NUM_DATA_UNITS
operator|)
condition|?
literal|"smallFile"
else|:
literal|"largeFile"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to read file with DN failure:"
operator|+
literal|" fileType = "
operator|+
name|fileType
operator|+
literal|", dnFailureNum = "
operator|+
name|dnFailureNum
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// tear down the cluster
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

