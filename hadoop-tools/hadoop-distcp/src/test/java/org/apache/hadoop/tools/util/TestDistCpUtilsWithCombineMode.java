begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|util
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
name|test
operator|.
name|GenericTestUtils
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
name|Assert
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
name|TestName
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

begin_comment
comment|/**  * Test length and checksums comparison with checksum combine mode.  * When the combine mode is COMPOSITE_CRC, it should tolerate different file  * systems and different block sizes.  */
end_comment

begin_class
DECL|class|TestDistCpUtilsWithCombineMode
specifier|public
class|class
name|TestDistCpUtilsWithCombineMode
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDistCpUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|Rule
DECL|field|testName
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|create ()
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|config
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
if|if
condition|(
name|testName
operator|.
name|getMethodName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"WithCombineMode"
argument_list|)
condition|)
block|{
name|config
operator|.
name|set
argument_list|(
literal|"dfs.checksum.combine.mode"
argument_list|,
literal|"COMPOSITE_CRC"
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MIN_BLOCK_SIZE_KEY
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|destroy ()
specifier|public
name|void
name|destroy
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
DECL|method|testChecksumsComparisonWithCombineMode ()
specifier|public
name|void
name|testChecksumsComparisonWithCombineMode
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|compareSameContentButDiffBlockSizes
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception is found"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
DECL|method|testChecksumsComparisonWithoutCombineMode ()
specifier|public
name|void
name|testChecksumsComparisonWithoutCombineMode
parameter_list|()
block|{
try|try
block|{
name|compareSameContentButDiffBlockSizes
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected comparison to fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Checksum mismatch"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compareSameContentButDiffBlockSizes ()
specifier|private
name|void
name|compareSameContentButDiffBlockSizes
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|base
init|=
literal|"/tmp/verify-checksum-"
operator|+
name|testName
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"/"
decl_stmt|;
name|long
name|seed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|short
name|rf
init|=
literal|2
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|Path
name|basePath
init|=
operator|new
name|Path
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|basePath
argument_list|)
expr_stmt|;
comment|// create 2 files of same content but different block-sizes
name|Path
name|src
init|=
operator|new
name|Path
argument_list|(
name|base
operator|+
literal|"src"
argument_list|)
decl_stmt|;
name|Path
name|dst
init|=
operator|new
name|Path
argument_list|(
name|base
operator|+
literal|"dst"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|src
argument_list|,
literal|256
argument_list|,
literal|1024
argument_list|,
literal|512
argument_list|,
name|rf
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|dst
argument_list|,
literal|256
argument_list|,
literal|1024
argument_list|,
literal|1024
argument_list|,
name|rf
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// then compare
name|DistCpUtils
operator|.
name|compareFileLengthsAndChecksums
argument_list|(
literal|1024
argument_list|,
name|fs
argument_list|,
name|src
argument_list|,
literal|null
argument_list|,
name|fs
argument_list|,
name|dst
argument_list|,
literal|false
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

