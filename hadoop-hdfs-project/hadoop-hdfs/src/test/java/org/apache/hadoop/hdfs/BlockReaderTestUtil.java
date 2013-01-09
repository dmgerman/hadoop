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
name|DataOutputStream
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
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
name|net
operator|.
name|TcpPeerServer
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
name|net
operator|.
name|NetUtils
import|;
end_import

begin_comment
comment|/**  * A helper class to setup the cluster, and get to BlockReader and DataNode for a block.  */
end_comment

begin_class
DECL|class|BlockReaderTestUtil
specifier|public
class|class
name|BlockReaderTestUtil
block|{
DECL|field|conf
specifier|private
name|HdfsConfiguration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
comment|/**    * Setup the cluster    */
DECL|method|BlockReaderTestUtil (int replicationFactor)
specifier|public
name|BlockReaderTestUtil
parameter_list|(
name|int
name|replicationFactor
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
name|replicationFactor
argument_list|,
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockReaderTestUtil (int replicationFactor, HdfsConfiguration config)
specifier|public
name|BlockReaderTestUtil
parameter_list|(
name|int
name|replicationFactor
parameter_list|,
name|HdfsConfiguration
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
name|replicationFactor
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
name|format
argument_list|(
literal|true
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
block|}
comment|/**    * Shutdown cluster    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
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
DECL|method|getCluster ()
specifier|public
name|MiniDFSCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
DECL|method|getConf ()
specifier|public
name|HdfsConfiguration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Create a file of the given size filled with random data.    * @return  File data.    */
DECL|method|writeFile (Path filepath, int sizeKB)
specifier|public
name|byte
index|[]
name|writeFile
parameter_list|(
name|Path
name|filepath
parameter_list|,
name|int
name|sizeKB
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Write a file with the specified amount of data
name|DataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|filepath
argument_list|)
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
name|sizeKB
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**    * Get the list of Blocks for a file.    */
DECL|method|getFileBlocks (Path filepath, int sizeKB)
specifier|public
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|getFileBlocks
parameter_list|(
name|Path
name|filepath
parameter_list|,
name|int
name|sizeKB
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Return the blocks we just wrote
name|DFSClient
name|dfsclient
init|=
name|getDFSClient
argument_list|()
decl_stmt|;
return|return
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|filepath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|sizeKB
operator|*
literal|1024
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
return|;
block|}
comment|/**    * Get the DFSClient.    */
DECL|method|getDFSClient ()
specifier|public
name|DFSClient
name|getDFSClient
parameter_list|()
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|nnAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|DFSClient
argument_list|(
name|nnAddr
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Exercise the BlockReader and read length bytes.    *    * It does not verify the bytes read.    */
DECL|method|readAndCheckEOS (BlockReader reader, int length, boolean expectEof)
specifier|public
name|void
name|readAndCheckEOS
parameter_list|(
name|BlockReader
name|reader
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|expectEof
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|nRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|nRead
operator|<
name|length
condition|)
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"So far read "
operator|+
name|nRead
operator|+
literal|" - going to read more."
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|>
literal|0
argument_list|)
expr_stmt|;
name|nRead
operator|+=
name|n
expr_stmt|;
block|}
if|if
condition|(
name|expectEof
condition|)
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Done reading, expect EOF for next read."
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get a BlockReader for the given block.    */
DECL|method|getBlockReader (LocatedBlock testBlock, int offset, int lenToRead)
specifier|public
name|BlockReader
name|getBlockReader
parameter_list|(
name|LocatedBlock
name|testBlock
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|lenToRead
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|targetAddr
init|=
literal|null
decl_stmt|;
name|Socket
name|sock
init|=
literal|null
decl_stmt|;
name|ExtendedBlock
name|block
init|=
name|testBlock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|DatanodeInfo
index|[]
name|nodes
init|=
name|testBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|targetAddr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|nodes
index|[
literal|0
index|]
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
name|sock
operator|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|createSocket
argument_list|()
expr_stmt|;
name|sock
operator|.
name|connect
argument_list|(
name|targetAddr
argument_list|,
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|sock
operator|.
name|setSoTimeout
argument_list|(
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
return|return
name|BlockReaderFactory
operator|.
name|newBlockReader
argument_list|(
name|conf
argument_list|,
name|targetAddr
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|block
argument_list|,
name|testBlock
operator|.
name|getBlockToken
argument_list|()
argument_list|,
name|offset
argument_list|,
name|lenToRead
argument_list|,
literal|true
argument_list|,
literal|"BlockReaderTestUtil"
argument_list|,
name|TcpPeerServer
operator|.
name|peerFromSocket
argument_list|(
name|sock
argument_list|)
argument_list|,
name|nodes
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Get a DataNode that serves our testBlock.    */
DECL|method|getDataNode (LocatedBlock testBlock)
specifier|public
name|DataNode
name|getDataNode
parameter_list|(
name|LocatedBlock
name|testBlock
parameter_list|)
block|{
name|DatanodeInfo
index|[]
name|nodes
init|=
name|testBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|int
name|ipcport
init|=
name|nodes
index|[
literal|0
index|]
operator|.
name|getIpcPort
argument_list|()
decl_stmt|;
return|return
name|cluster
operator|.
name|getDataNode
argument_list|(
name|ipcport
argument_list|)
return|;
block|}
block|}
end_class

end_unit

