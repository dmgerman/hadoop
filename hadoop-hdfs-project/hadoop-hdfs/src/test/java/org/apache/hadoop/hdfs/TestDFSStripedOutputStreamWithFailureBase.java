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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|protocol
operator|.
name|AddErasureCodingPolicyResponse
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
name|DatanodeInfo
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
name|ErasureCodingPolicy
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
name|LocatedBlock
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenSecretManager
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|SecurityTestUtil
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
name|blockmanagement
operator|.
name|BlockManager
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
name|blockmanagement
operator|.
name|BlockPlacementPolicy
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
name|namenode
operator|.
name|NameNode
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
name|erasurecode
operator|.
name|CodecUtil
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
name|erasurecode
operator|.
name|ECSchema
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
name|erasurecode
operator|.
name|ErasureCodeNative
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|NativeRSRawErasureCoderFactory
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
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
name|org
operator|.
name|slf4j
operator|.
name|event
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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

begin_comment
comment|/**  * Base class for test striped file write operation.  */
end_comment

begin_class
DECL|class|TestDFSStripedOutputStreamWithFailureBase
specifier|public
class|class
name|TestDFSStripedOutputStreamWithFailureBase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDFSStripedOutputStreamWithFailureBase
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DFSOutputStream
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DataStreamer
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DFSClient
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlockPlacementPolicy
operator|.
name|class
argument_list|)
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
block|}
DECL|field|cellSize
specifier|protected
specifier|final
name|int
name|cellSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
comment|// 8k
DECL|field|stripesPerBlock
specifier|protected
specifier|final
name|int
name|stripesPerBlock
init|=
literal|4
decl_stmt|;
DECL|field|ecPolicy
specifier|protected
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
DECL|field|dataBlocks
specifier|protected
name|int
name|dataBlocks
decl_stmt|;
DECL|field|parityBlocks
specifier|protected
name|int
name|parityBlocks
decl_stmt|;
DECL|field|blockSize
specifier|protected
name|int
name|blockSize
decl_stmt|;
DECL|field|blockGroupSize
specifier|protected
name|int
name|blockGroupSize
decl_stmt|;
DECL|field|dnIndexSuite
specifier|private
name|int
index|[]
index|[]
name|dnIndexSuite
decl_stmt|;
DECL|field|lengths
specifier|protected
name|List
argument_list|<
name|Integer
argument_list|>
name|lengths
decl_stmt|;
DECL|field|RANDOM
specifier|protected
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|dir
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|TestDFSStripedOutputStreamWithFailureBase
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|FLUSH_POS
specifier|protected
specifier|static
specifier|final
name|int
name|FLUSH_POS
init|=
literal|9
operator|*
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_DEFAULT
operator|+
literal|1
decl_stmt|;
DECL|method|getEcSchema ()
specifier|public
name|ECSchema
name|getEcSchema
parameter_list|()
block|{
return|return
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getSchema
argument_list|()
return|;
block|}
comment|/*    * Initialize erasure coding policy.    */
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{
name|ecPolicy
operator|=
operator|new
name|ErasureCodingPolicy
argument_list|(
name|getEcSchema
argument_list|()
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
name|dataBlocks
operator|=
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
expr_stmt|;
name|parityBlocks
operator|=
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
expr_stmt|;
name|blockSize
operator|=
name|cellSize
operator|*
name|stripesPerBlock
expr_stmt|;
name|blockGroupSize
operator|=
name|blockSize
operator|*
name|dataBlocks
expr_stmt|;
name|dnIndexSuite
operator|=
name|getDnIndexSuite
argument_list|()
expr_stmt|;
name|lengths
operator|=
name|newLengths
argument_list|()
expr_stmt|;
block|}
DECL|method|newLengths ()
name|List
argument_list|<
name|Integer
argument_list|>
name|newLengths
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|lens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|lens
operator|.
name|add
argument_list|(
name|FLUSH_POS
operator|+
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|b
init|=
literal|0
init|;
name|b
operator|<=
literal|2
condition|;
name|b
operator|++
control|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|stripesPerBlock
operator|*
name|dataBlocks
condition|;
name|c
operator|++
control|)
block|{
for|for
control|(
name|int
name|delta
init|=
operator|-
literal|1
init|;
name|delta
operator|<=
literal|1
condition|;
name|delta
operator|++
control|)
block|{
specifier|final
name|int
name|length
init|=
name|b
operator|*
name|blockGroupSize
operator|+
name|c
operator|*
name|cellSize
operator|+
name|delta
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|lens
operator|.
name|size
argument_list|()
operator|+
literal|": length="
operator|+
name|length
operator|+
literal|", (b, c, d) = ("
operator|+
name|b
operator|+
literal|", "
operator|+
name|c
operator|+
literal|", "
operator|+
name|delta
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|lens
operator|.
name|add
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|lens
return|;
block|}
DECL|method|getDnIndexSuite ()
specifier|private
name|int
index|[]
index|[]
name|getDnIndexSuite
parameter_list|()
block|{
specifier|final
name|int
name|maxNumLevel
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|maxPerLevel
init|=
literal|5
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|allLists
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numIndex
init|=
name|parityBlocks
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
argument_list|<
name|maxNumLevel
operator|&&
name|numIndex
argument_list|>
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|lists
init|=
name|combinations
argument_list|(
name|dataBlocks
operator|+
name|parityBlocks
argument_list|,
name|numIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|lists
operator|.
name|size
argument_list|()
operator|>
name|maxPerLevel
condition|)
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|lists
argument_list|)
expr_stmt|;
name|lists
operator|=
name|lists
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|maxPerLevel
argument_list|)
expr_stmt|;
block|}
name|allLists
operator|.
name|addAll
argument_list|(
name|lists
argument_list|)
expr_stmt|;
name|numIndex
operator|--
expr_stmt|;
block|}
name|int
index|[]
index|[]
name|dnIndexArray
init|=
operator|new
name|int
index|[
name|allLists
operator|.
name|size
argument_list|()
index|]
index|[]
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
name|dnIndexArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
index|[]
name|list
init|=
operator|new
name|int
index|[
name|allLists
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|list
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|list
index|[
name|j
index|]
operator|=
name|allLists
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
name|dnIndexArray
index|[
name|i
index|]
operator|=
name|list
expr_stmt|;
block|}
return|return
name|dnIndexArray
return|;
block|}
comment|// get all combinations of k integers from {0,...,n-1}
DECL|method|combinations (int n, int k)
specifier|private
specifier|static
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|combinations
parameter_list|(
name|int
name|n
parameter_list|,
name|int
name|k
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|res
init|=
operator|new
name|LinkedList
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|k
operator|>=
literal|1
operator|&&
name|n
operator|>=
name|k
condition|)
block|{
name|getComb
argument_list|(
name|n
argument_list|,
name|k
argument_list|,
operator|new
name|Stack
argument_list|<
name|Integer
argument_list|>
argument_list|()
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|method|getComb (int n, int k, Stack<Integer> stack, List<List<Integer>> res)
specifier|private
specifier|static
name|void
name|getComb
parameter_list|(
name|int
name|n
parameter_list|,
name|int
name|k
parameter_list|,
name|Stack
argument_list|<
name|Integer
argument_list|>
name|stack
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|res
parameter_list|)
block|{
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
name|k
condition|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|stack
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|next
init|=
name|stack
operator|.
name|empty
argument_list|()
condition|?
literal|0
else|:
name|stack
operator|.
name|peek
argument_list|()
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|next
operator|<
name|n
condition|)
block|{
name|stack
operator|.
name|push
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|getComb
argument_list|(
name|n
argument_list|,
name|k
argument_list|,
name|stack
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|next
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|stack
operator|.
name|empty
argument_list|()
condition|)
block|{
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getKillPositions (int fileLen, int num)
name|int
index|[]
name|getKillPositions
parameter_list|(
name|int
name|fileLen
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|int
index|[]
name|positions
init|=
operator|new
name|int
index|[
name|num
index|]
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|positions
index|[
name|i
index|]
operator|=
name|fileLen
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
operator|/
operator|(
name|num
operator|+
literal|1
operator|)
expr_stmt|;
block|}
return|return
name|positions
return|;
block|}
DECL|method|getLength (int i)
name|Integer
name|getLength
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|i
operator|>=
literal|0
operator|&&
name|i
operator|<
name|lengths
operator|.
name|size
argument_list|()
condition|?
name|lengths
operator|.
name|get
argument_list|(
name|i
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|setup (Configuration conf)
name|void
name|setup
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NUM_DATA_BLOCKS  = "
operator|+
name|dataBlocks
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NUM_PARITY_BLOCKS= "
operator|+
name|parityBlocks
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CELL_SIZE        = "
operator|+
name|cellSize
operator|+
literal|" (="
operator|+
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|cellSize
argument_list|,
literal|"B"
argument_list|,
literal|2
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"BLOCK_SIZE       = "
operator|+
name|blockSize
operator|+
literal|" (="
operator|+
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|blockSize
argument_list|,
literal|"B"
argument_list|,
literal|2
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"BLOCK_GROUP_SIZE = "
operator|+
name|blockGroupSize
operator|+
literal|" (="
operator|+
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|blockGroupSize
argument_list|,
literal|"B"
argument_list|,
literal|2
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
decl_stmt|;
if|if
condition|(
name|ErasureCodeNative
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_RS_RAWCODERS_KEY
argument_list|,
name|NativeRSRawErasureCoderFactory
operator|.
name|CODER_NAME
argument_list|)
expr_stmt|;
block|}
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
name|numDNs
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
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|AddErasureCodingPolicyResponse
index|[]
name|res
init|=
name|dfs
operator|.
name|addErasureCodingPolicies
argument_list|(
operator|new
name|ErasureCodingPolicy
index|[]
block|{
name|ecPolicy
block|}
argument_list|)
decl_stmt|;
name|ecPolicy
operator|=
name|res
index|[
literal|0
index|]
operator|.
name|getPolicy
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|enableErasureCodingPolicy
argument_list|(
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|enableAllECPolicies
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|dir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|newHdfsConfiguration ()
name|HdfsConfiguration
name|newHdfsConfiguration
parameter_list|()
block|{
specifier|final
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|runTest (final int length)
name|void
name|runTest
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
block|{
specifier|final
name|HdfsConfiguration
name|conf
init|=
name|newHdfsConfiguration
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|dn
init|=
literal|0
init|;
name|dn
operator|<
name|dataBlocks
operator|+
name|parityBlocks
condition|;
name|dn
operator|++
control|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"runTest: dn="
operator|+
name|dn
operator|+
literal|", length="
operator|+
name|length
argument_list|)
expr_stmt|;
name|setup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|length
argument_list|,
operator|new
name|int
index|[]
block|{
name|length
operator|/
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|dn
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
specifier|final
name|String
name|err
init|=
literal|"failed, dn="
operator|+
name|dn
operator|+
literal|", length="
operator|+
name|length
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|runTestWithMultipleFailure (final int length)
name|void
name|runTestWithMultipleFailure
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|HdfsConfiguration
name|conf
init|=
name|newHdfsConfiguration
argument_list|()
decl_stmt|;
for|for
control|(
name|int
index|[]
name|dnIndex
range|:
name|dnIndexSuite
control|)
block|{
name|int
index|[]
name|killPos
init|=
name|getKillPositions
argument_list|(
name|length
argument_list|,
name|dnIndex
operator|.
name|length
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"runTestWithMultipleFailure: length=="
operator|+
name|length
operator|+
literal|", killPos="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|killPos
argument_list|)
operator|+
literal|", dnIndex="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|dnIndex
argument_list|)
argument_list|)
expr_stmt|;
name|setup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|length
argument_list|,
name|killPos
argument_list|,
name|dnIndex
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
specifier|final
name|String
name|err
init|=
literal|"failed, killPos="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|killPos
argument_list|)
operator|+
literal|", dnIndex="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|dnIndex
argument_list|)
operator|+
literal|", length="
operator|+
name|length
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|err
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * runTest implementation.    * @param length file length    * @param killPos killing positions in ascending order    * @param dnIndex DN index to kill when meets killing positions    * @param tokenExpire wait token to expire when kill a DN    * @throws Exception    */
DECL|method|runTest (final int length, final int[] killPos, final int[] dnIndex, final boolean tokenExpire)
name|void
name|runTest
parameter_list|(
specifier|final
name|int
name|length
parameter_list|,
specifier|final
name|int
index|[]
name|killPos
parameter_list|,
specifier|final
name|int
index|[]
name|dnIndex
parameter_list|,
specifier|final
name|boolean
name|tokenExpire
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|killPos
index|[
literal|0
index|]
operator|<=
name|FLUSH_POS
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"killPos="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|killPos
argument_list|)
operator|+
literal|"<= FLUSH_POS="
operator|+
name|FLUSH_POS
operator|+
literal|", length="
operator|+
name|length
operator|+
literal|", dnIndex="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|dnIndex
argument_list|)
argument_list|)
expr_stmt|;
return|return;
comment|//skip test
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|length
operator|>
name|killPos
index|[
literal|0
index|]
argument_list|,
literal|"length=%s<= killPos=%s"
argument_list|,
name|length
argument_list|,
name|killPos
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|killPos
operator|.
name|length
operator|==
name|dnIndex
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"dn"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|dnIndex
argument_list|)
operator|+
literal|"len"
operator|+
name|length
operator|+
literal|"kill"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|killPos
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fullPath
init|=
name|p
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"fullPath="
operator|+
name|fullPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenExpire
condition|)
block|{
specifier|final
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
specifier|final
name|BlockTokenSecretManager
name|sm
init|=
name|bm
operator|.
name|getBlockTokenSecretManager
argument_list|()
decl_stmt|;
comment|// set a short token lifetime (6 second)
name|SecurityTestUtil
operator|.
name|setBlockTokenLifetime
argument_list|(
name|sm
argument_list|,
literal|6000L
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicInteger
name|pos
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|dfs
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|DFSStripedOutputStream
name|stripedOut
init|=
operator|(
name|DFSStripedOutputStream
operator|)
name|out
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
comment|// first GS of this block group which never proceeds blockRecovery
name|long
name|firstGS
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|oldGS
init|=
operator|-
literal|1
decl_stmt|;
comment|// the old GS before bumping
name|List
argument_list|<
name|Long
argument_list|>
name|gsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeInfo
argument_list|>
name|killedDN
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numKilled
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|pos
operator|.
name|get
argument_list|()
operator|<
name|length
condition|;
control|)
block|{
specifier|final
name|int
name|i
init|=
name|pos
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|numKilled
operator|<
name|killPos
operator|.
name|length
operator|&&
name|i
operator|==
name|killPos
index|[
name|numKilled
index|]
condition|)
block|{
name|assertTrue
argument_list|(
name|firstGS
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|long
name|gs
init|=
name|getGenerationStamp
argument_list|(
name|stripedOut
argument_list|)
decl_stmt|;
if|if
condition|(
name|numKilled
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|firstGS
argument_list|,
name|gs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//TODO: implement hflush/hsync and verify gs strict greater than oldGS
name|assertTrue
argument_list|(
name|gs
operator|>=
name|oldGS
argument_list|)
expr_stmt|;
block|}
name|oldGS
operator|=
name|gs
expr_stmt|;
if|if
condition|(
name|tokenExpire
condition|)
block|{
name|DFSTestUtil
operator|.
name|flushInternal
argument_list|(
name|stripedOut
argument_list|)
expr_stmt|;
name|waitTokenExpires
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|killedDN
operator|.
name|add
argument_list|(
name|killDatanode
argument_list|(
name|cluster
argument_list|,
name|stripedOut
argument_list|,
name|dnIndex
index|[
name|numKilled
index|]
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|numKilled
operator|++
expr_stmt|;
block|}
name|write
argument_list|(
name|out
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
name|blockGroupSize
operator|==
name|FLUSH_POS
condition|)
block|{
name|firstGS
operator|=
name|getGenerationStamp
argument_list|(
name|stripedOut
argument_list|)
expr_stmt|;
name|oldGS
operator|=
name|firstGS
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
name|blockGroupSize
operator|==
literal|0
condition|)
block|{
name|gsList
operator|.
name|add
argument_list|(
name|oldGS
argument_list|)
expr_stmt|;
block|}
block|}
name|gsList
operator|.
name|add
argument_list|(
name|oldGS
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|dnIndex
operator|.
name|length
argument_list|,
name|numKilled
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|waitBlockGroupsReported
argument_list|(
name|dfs
argument_list|,
name|fullPath
argument_list|,
name|numKilled
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|checkData
argument_list|(
name|dfs
argument_list|,
name|p
argument_list|,
name|length
argument_list|,
name|killedDN
argument_list|,
name|gsList
argument_list|,
name|blockGroupSize
argument_list|)
expr_stmt|;
block|}
DECL|method|write (FSDataOutputStream out, int i)
specifier|static
name|void
name|write
parameter_list|(
name|FSDataOutputStream
name|out
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|StripedFileTestUtil
operator|.
name|getByte
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed at i="
operator|+
name|i
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|getGenerationStamp (DFSStripedOutputStream out)
specifier|static
name|long
name|getGenerationStamp
parameter_list|(
name|DFSStripedOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|gs
init|=
name|out
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"getGenerationStamp returns "
operator|+
name|gs
argument_list|)
expr_stmt|;
return|return
name|gs
return|;
block|}
DECL|method|getDatanodes (StripedDataStreamer streamer)
specifier|static
name|DatanodeInfo
name|getDatanodes
parameter_list|(
name|StripedDataStreamer
name|streamer
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|streamer
operator|.
name|getNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|datanodes
operator|==
literal|null
condition|)
block|{
comment|// try peeking following block.
specifier|final
name|LocatedBlock
name|lb
init|=
name|streamer
operator|.
name|peekFollowingBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|lb
operator|!=
literal|null
condition|)
block|{
name|datanodes
operator|=
name|lb
operator|.
name|getLocations
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|datanodes
operator|!=
literal|null
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|datanodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|datanodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
name|datanodes
index|[
literal|0
index|]
return|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ie
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
DECL|method|killDatanode (MiniDFSCluster cluster, DFSStripedOutputStream out, final int dnIndex, final AtomicInteger pos)
specifier|static
name|DatanodeInfo
name|killDatanode
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|DFSStripedOutputStream
name|out
parameter_list|,
specifier|final
name|int
name|dnIndex
parameter_list|,
specifier|final
name|AtomicInteger
name|pos
parameter_list|)
block|{
specifier|final
name|StripedDataStreamer
name|s
init|=
name|out
operator|.
name|getStripedDataStreamer
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeInfo
name|datanode
init|=
name|getDatanodes
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"killDatanode "
operator|+
name|dnIndex
operator|+
literal|": "
operator|+
name|datanode
operator|+
literal|", pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|datanode
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|datanode
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|datanode
return|;
block|}
DECL|method|waitTokenExpires (FSDataOutputStream out)
specifier|private
name|void
name|waitTokenExpires
parameter_list|(
name|FSDataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
init|=
name|DFSTestUtil
operator|.
name|getBlockToken
argument_list|(
name|out
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|SecurityTestUtil
operator|.
name|isBlockTokenExpired
argument_list|(
name|token
argument_list|)
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{       }
block|}
block|}
block|}
end_class

end_unit

