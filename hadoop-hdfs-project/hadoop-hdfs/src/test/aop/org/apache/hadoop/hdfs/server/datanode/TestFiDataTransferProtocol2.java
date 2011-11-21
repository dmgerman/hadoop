begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|fi
operator|.
name|DataTransferTestUtil
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
name|fi
operator|.
name|DataTransferTestUtil
operator|.
name|CountdownDoosAction
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
name|fi
operator|.
name|DataTransferTestUtil
operator|.
name|CountdownOomAction
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
name|fi
operator|.
name|DataTransferTestUtil
operator|.
name|CountdownSleepAction
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
name|fi
operator|.
name|DataTransferTestUtil
operator|.
name|DataTransferTest
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
name|fi
operator|.
name|DataTransferTestUtil
operator|.
name|SleepAction
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
name|fi
operator|.
name|DataTransferTestUtil
operator|.
name|VerificationAction
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
name|fi
operator|.
name|FiTestUtil
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
name|IO_FILE_BUFFER_SIZE_KEY
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
name|FSDataInputStream
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
name|DFSClient
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
name|datatransfer
operator|.
name|DataTransferProtocol
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
name|Assert
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
comment|/** Test DataTransferProtocol with fault injection. */
end_comment

begin_class
DECL|class|TestFiDataTransferProtocol2
specifier|public
class|class
name|TestFiDataTransferProtocol2
block|{
DECL|field|REPLICATION
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|static
specifier|final
name|long
name|BLOCKSIZE
init|=
literal|1L
operator|*
operator|(
literal|1L
operator|<<
literal|20
operator|)
decl_stmt|;
DECL|field|PACKET_SIZE
specifier|static
specifier|final
name|int
name|PACKET_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|MIN_N_PACKET
specifier|static
specifier|final
name|int
name|MIN_N_PACKET
init|=
literal|3
decl_stmt|;
DECL|field|MAX_N_PACKET
specifier|static
specifier|final
name|int
name|MAX_N_PACKET
init|=
literal|10
decl_stmt|;
DECL|field|MAX_SLEEP
specifier|static
specifier|final
name|int
name|MAX_SLEEP
init|=
literal|1000
decl_stmt|;
DECL|field|conf
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
static|static
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HANDLER_COUNT_KEY
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
name|DFS_REPLICATION_KEY
argument_list|,
name|REPLICATION
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
name|PACKET_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
DECL|field|bytes
specifier|static
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|MAX_N_PACKET
operator|*
name|PACKET_SIZE
index|]
decl_stmt|;
DECL|field|toRead
specifier|static
specifier|final
name|byte
index|[]
name|toRead
init|=
operator|new
name|byte
index|[
name|MAX_N_PACKET
operator|*
name|PACKET_SIZE
index|]
decl_stmt|;
DECL|method|createFile (FileSystem fs, Path p )
specifier|static
specifier|private
name|FSDataOutputStream
name|createFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|true
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|REPLICATION
argument_list|,
name|BLOCKSIZE
argument_list|)
return|;
block|}
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|BlockReceiver
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|DFSClient
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|DataTransferProtocol
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
comment|/**    * 1. create files with dfs    * 2. write MIN_N_PACKET to MAX_N_PACKET packets    * 3. close file    * 4. open the same file    * 5. read the bytes and compare results    */
DECL|method|writeSeveralPackets (String methodName)
specifier|private
specifier|static
name|void
name|writeSeveralPackets
parameter_list|(
name|String
name|methodName
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Random
name|r
init|=
name|FiTestUtil
operator|.
name|RANDOM
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nPackets
init|=
name|FiTestUtil
operator|.
name|nextRandomInt
argument_list|(
name|MIN_N_PACKET
argument_list|,
name|MAX_N_PACKET
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|lastPacketSize
init|=
name|FiTestUtil
operator|.
name|nextRandomInt
argument_list|(
literal|1
argument_list|,
name|PACKET_SIZE
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|size
init|=
operator|(
name|nPackets
operator|-
literal|1
operator|)
operator|*
name|PACKET_SIZE
operator|+
name|lastPacketSize
decl_stmt|;
name|FiTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"size="
operator|+
name|size
operator|+
literal|", nPackets="
operator|+
name|nPackets
operator|+
literal|", lastPacketSize="
operator|+
name|lastPacketSize
argument_list|)
expr_stmt|;
specifier|final
name|MiniDFSCluster
name|cluster
init|=
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
name|REPLICATION
operator|+
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|methodName
operator|+
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|createFile
argument_list|(
name|dfs
argument_list|,
name|p
argument_list|)
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|ran
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|ran
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|FSDataInputStream
name|in
init|=
name|dfs
operator|.
name|open
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|int
name|totalRead
init|=
literal|0
decl_stmt|;
name|int
name|nRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|nRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|toRead
argument_list|,
name|totalRead
argument_list|,
name|size
operator|-
name|totalRead
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|totalRead
operator|+=
name|nRead
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Cannot read file."
argument_list|,
name|size
argument_list|,
name|totalRead
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"File content differ."
argument_list|,
name|bytes
index|[
name|i
index|]
operator|==
name|toRead
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initSlowDatanodeTest (DataTransferTest t, SleepAction a)
specifier|private
specifier|static
name|void
name|initSlowDatanodeTest
parameter_list|(
name|DataTransferTest
name|t
parameter_list|,
name|SleepAction
name|a
parameter_list|)
throws|throws
name|IOException
block|{
name|t
operator|.
name|fiCallReceivePacket
operator|.
name|set
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiReceiverOpWriteBlock
operator|.
name|set
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiStatusRead
operator|.
name|set
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
DECL|method|runTest17_19 (String methodName, int dnIndex)
specifier|private
name|void
name|runTest17_19
parameter_list|(
name|String
name|methodName
parameter_list|,
name|int
name|dnIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|FiTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|methodName
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
specifier|final
name|DataTransferTest
name|t
init|=
operator|(
name|DataTransferTest
operator|)
name|DataTransferTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiCallWritePacketToDisk
operator|.
name|set
argument_list|(
operator|new
name|CountdownDoosAction
argument_list|(
name|methodName
argument_list|,
name|dnIndex
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiPipelineErrorAfterInit
operator|.
name|set
argument_list|(
operator|new
name|VerificationAction
argument_list|(
name|methodName
argument_list|,
name|dnIndex
argument_list|)
argument_list|)
expr_stmt|;
name|writeSeveralPackets
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|t
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|runTest29_30 (String methodName, int dnIndex)
specifier|private
name|void
name|runTest29_30
parameter_list|(
name|String
name|methodName
parameter_list|,
name|int
name|dnIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|FiTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|methodName
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
specifier|final
name|DataTransferTest
name|t
init|=
operator|(
name|DataTransferTest
operator|)
name|DataTransferTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiAfterDownstreamStatusRead
operator|.
name|set
argument_list|(
operator|new
name|CountdownOomAction
argument_list|(
name|methodName
argument_list|,
name|dnIndex
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiPipelineErrorAfterInit
operator|.
name|set
argument_list|(
operator|new
name|VerificationAction
argument_list|(
name|methodName
argument_list|,
name|dnIndex
argument_list|)
argument_list|)
expr_stmt|;
name|writeSeveralPackets
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|t
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|runTest34_35 (String methodName, int dnIndex)
specifier|private
name|void
name|runTest34_35
parameter_list|(
name|String
name|methodName
parameter_list|,
name|int
name|dnIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|FiTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|methodName
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
specifier|final
name|DataTransferTest
name|t
init|=
operator|(
name|DataTransferTest
operator|)
name|DataTransferTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|t
operator|.
name|fiAfterDownstreamStatusRead
operator|.
name|set
argument_list|(
operator|new
name|CountdownSleepAction
argument_list|(
name|methodName
argument_list|,
name|dnIndex
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiPipelineErrorAfterInit
operator|.
name|set
argument_list|(
operator|new
name|VerificationAction
argument_list|(
name|methodName
argument_list|,
name|dnIndex
argument_list|)
argument_list|)
expr_stmt|;
name|writeSeveralPackets
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|t
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming:    * Randomize datanode speed, write several packets,    * DN0 throws a DiskOutOfSpaceError when it writes the third packet to disk.    * Client gets an IOException and determines DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_17 ()
specifier|public
name|void
name|pipeline_Fi_17
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|runTest17_19
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming:    * Randomize datanode speed, write several packets,    * DN1 throws a DiskOutOfSpaceError when it writes the third packet to disk.    * Client gets an IOException and determines DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_18 ()
specifier|public
name|void
name|pipeline_Fi_18
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|runTest17_19
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming:    * Randomize datanode speed, write several packets,    * DN2 throws a DiskOutOfSpaceError when it writes the third packet to disk.    * Client gets an IOException and determines DN2 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_19 ()
specifier|public
name|void
name|pipeline_Fi_19
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|runTest17_19
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Client writes several packets with DN0 very slow. Client    * finishes write successfully.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_20 ()
specifier|public
name|void
name|pipeline_Fi_20
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|FiTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|methodName
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
specifier|final
name|DataTransferTest
name|t
init|=
operator|(
name|DataTransferTest
operator|)
name|DataTransferTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|writeSeveralPackets
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Client writes several packets with DN1 very slow. Client    * finishes write successfully.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_21 ()
specifier|public
name|void
name|pipeline_Fi_21
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|FiTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|methodName
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
specifier|final
name|DataTransferTest
name|t
init|=
operator|(
name|DataTransferTest
operator|)
name|DataTransferTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|writeSeveralPackets
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Client writes several packets with DN2 very slow. Client    * finishes write successfully.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_22 ()
specifier|public
name|void
name|pipeline_Fi_22
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|FiTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|methodName
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
specifier|final
name|DataTransferTest
name|t
init|=
operator|(
name|DataTransferTest
operator|)
name|DataTransferTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|initSlowDatanodeTest
argument_list|(
name|t
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
name|MAX_SLEEP
argument_list|)
argument_list|)
expr_stmt|;
name|writeSeveralPackets
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Randomize datanode speed, write several packets, DN1 throws a    * OutOfMemoryException when it receives the ack of the third packet from DN2.    * Client gets an IOException and determines DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_29 ()
specifier|public
name|void
name|pipeline_Fi_29
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|runTest29_30
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Randomize datanode speed, write several packets, DN0 throws a    * OutOfMemoryException when it receives the ack of the third packet from DN1.    * Client gets an IOException and determines DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_30 ()
specifier|public
name|void
name|pipeline_Fi_30
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|runTest29_30
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Write several packets, DN1 never responses when it receives the    * ack of the third packet from DN2. Client gets an IOException and determines    * DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_34 ()
specifier|public
name|void
name|pipeline_Fi_34
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|runTest34_35
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Write several packets, DN0 never responses when it receives the    * ack of the third packet from DN1. Client gets an IOException and determines    * DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_35 ()
specifier|public
name|void
name|pipeline_Fi_35
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|methodName
init|=
name|FiTestUtil
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|runTest34_35
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

