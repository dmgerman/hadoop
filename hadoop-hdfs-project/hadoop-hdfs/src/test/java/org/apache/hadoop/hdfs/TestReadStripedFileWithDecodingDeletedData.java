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
comment|/**  * Test online recovery with files with deleted blocks. This test is  * parameterized.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestReadStripedFileWithDecodingDeletedData
specifier|public
class|class
name|TestReadStripedFileWithDecodingDeletedData
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
name|TestReadStripedFileWithDecodingDeletedData
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
return|return
name|ReadStripedFileWithDecodingHelper
operator|.
name|getParameters
argument_list|()
return|;
block|}
DECL|field|fileLength
specifier|private
name|int
name|fileLength
decl_stmt|;
DECL|field|dataDelNum
specifier|private
name|int
name|dataDelNum
decl_stmt|;
DECL|field|parityDelNum
specifier|private
name|int
name|parityDelNum
decl_stmt|;
DECL|method|TestReadStripedFileWithDecodingDeletedData (int fileLength, int dataDelNum, int parityDelNum)
specifier|public
name|TestReadStripedFileWithDecodingDeletedData
parameter_list|(
name|int
name|fileLength
parameter_list|,
name|int
name|dataDelNum
parameter_list|,
name|int
name|parityDelNum
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
name|dataDelNum
operator|=
name|dataDelNum
expr_stmt|;
name|this
operator|.
name|parityDelNum
operator|=
name|parityDelNum
expr_stmt|;
block|}
comment|/**    * Delete tolerable number of block before reading.    * Verify the decoding works correctly.    */
annotation|@
name|Test
DECL|method|testReadCorruptedDataByDeleting ()
specifier|public
name|void
name|testReadCorruptedDataByDeleting
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|src
init|=
literal|"/deleted_"
operator|+
name|dataDelNum
operator|+
literal|"_"
operator|+
name|parityDelNum
decl_stmt|;
name|ReadStripedFileWithDecodingHelper
operator|.
name|testReadWithBlockCorrupted
argument_list|(
name|cluster
argument_list|,
name|dfs
argument_list|,
name|src
argument_list|,
name|fileLength
argument_list|,
name|dataDelNum
argument_list|,
name|parityDelNum
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

