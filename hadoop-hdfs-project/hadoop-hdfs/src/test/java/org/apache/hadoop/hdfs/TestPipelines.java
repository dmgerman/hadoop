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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|Replica
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

begin_class
DECL|class|TestPipelines
specifier|public
class|class
name|TestPipelines
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
name|TestPipelines
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REPL_FACTOR
specifier|private
specifier|static
specifier|final
name|short
name|REPL_FACTOR
init|=
literal|3
decl_stmt|;
DECL|field|RAND_LIMIT
specifier|private
specifier|static
specifier|final
name|int
name|RAND_LIMIT
init|=
literal|2000
decl_stmt|;
DECL|field|FILE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
literal|10000
decl_stmt|;
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
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|rand
specifier|static
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|RAND_LIMIT
argument_list|)
decl_stmt|;
static|static
block|{
name|initLoggers
argument_list|()
expr_stmt|;
name|setConfiguration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|startUpCluster ()
specifier|public
name|void
name|startUpCluster
parameter_list|()
throws|throws
name|IOException
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
name|REPL_FACTOR
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutDownCluster ()
specifier|public
name|void
name|shutDownCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdownDataNodes
argument_list|()
expr_stmt|;
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
comment|/**    * Creates and closes a file of certain length.    * Calls append to allow next write() operation to add to the end of it    * After write() invocation, calls hflush() to make sure that data sunk through    * the pipeline and check the state of the last block's replica.    * It supposes to be in RBW state    *    * @throws IOException in case of an error    */
annotation|@
name|Test
DECL|method|pipeline_01 ()
specifier|public
name|void
name|pipeline_01
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running "
operator|+
name|METHOD_NAME
argument_list|)
expr_stmt|;
block|}
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|FILE_SIZE
argument_list|,
name|REPL_FACTOR
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invoking append but doing nothing otherwise..."
argument_list|)
expr_stmt|;
block|}
name|FSDataOutputStream
name|ofs
init|=
name|fs
operator|.
name|append
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|ofs
operator|.
name|writeBytes
argument_list|(
literal|"Some more stuff to write"
argument_list|)
expr_stmt|;
operator|(
operator|(
name|DFSOutputStream
operator|)
name|ofs
operator|.
name|getWrappedStream
argument_list|()
operator|)
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|lb
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
name|FILE_SIZE
operator|-
literal|1
argument_list|,
name|FILE_SIZE
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|Replica
name|r
init|=
name|cluster
operator|.
name|getFsDatasetTestUtils
argument_list|(
name|dn
argument_list|)
operator|.
name|fetchReplica
argument_list|(
name|lb
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Replica on DN "
operator|+
name|dn
operator|+
literal|" shouldn't be null"
argument_list|,
name|r
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should be RBW replica on "
operator|+
name|dn
operator|+
literal|" after sequence of calls append()/write()/hflush()"
argument_list|,
name|HdfsServerConstants
operator|.
name|ReplicaState
operator|.
name|RBW
argument_list|,
name|r
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ofs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * These two test cases are already implemented by    *    * @link{TestReadWhileWriting}    */
DECL|method|pipeline_02_03 ()
specifier|public
name|void
name|pipeline_02_03
parameter_list|()
block|{   }
DECL|method|writeData (final FSDataOutputStream out, final int length)
specifier|static
name|byte
index|[]
name|writeData
parameter_list|(
specifier|final
name|FSDataOutputStream
name|out
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bytesToWrite
init|=
name|length
decl_stmt|;
name|byte
index|[]
name|ret
init|=
operator|new
name|byte
index|[
name|bytesToWrite
index|]
decl_stmt|;
name|byte
index|[]
name|toWrite
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|written
init|=
literal|0
decl_stmt|;
name|Random
name|rb
init|=
operator|new
name|Random
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|bytesToWrite
operator|>
literal|0
condition|)
block|{
name|rb
operator|.
name|nextBytes
argument_list|(
name|toWrite
argument_list|)
expr_stmt|;
name|int
name|bytesToWriteNext
init|=
operator|(
literal|1024
operator|<
name|bytesToWrite
operator|)
condition|?
literal|1024
else|:
name|bytesToWrite
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|toWrite
argument_list|,
literal|0
argument_list|,
name|bytesToWriteNext
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|toWrite
argument_list|,
literal|0
argument_list|,
name|ret
argument_list|,
operator|(
name|ret
operator|.
name|length
operator|-
name|bytesToWrite
operator|)
argument_list|,
name|bytesToWriteNext
argument_list|)
expr_stmt|;
name|written
operator|+=
name|bytesToWriteNext
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Written: "
operator|+
name|bytesToWriteNext
operator|+
literal|"; Total: "
operator|+
name|written
argument_list|)
expr_stmt|;
block|}
name|bytesToWrite
operator|-=
name|bytesToWriteNext
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|setConfiguration ()
specifier|private
specifier|static
name|void
name|setConfiguration
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|int
name|customPerChecksumSize
init|=
literal|700
decl_stmt|;
name|int
name|customBlockSize
init|=
name|customPerChecksumSize
operator|*
literal|3
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|customPerChecksumSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|customBlockSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
name|customBlockSize
operator|/
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|initLoggers ()
specifier|private
specifier|static
name|void
name|initLoggers
parameter_list|()
block|{
name|DFSTestUtil
operator|.
name|setNameNodeLogLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DataNode
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
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
name|ALL
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

