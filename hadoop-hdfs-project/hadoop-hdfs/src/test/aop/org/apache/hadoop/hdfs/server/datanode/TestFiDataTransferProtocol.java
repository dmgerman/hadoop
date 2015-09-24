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
name|DoosAction
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
name|OomAction
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
operator|.
name|Action
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
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
DECL|class|TestFiDataTransferProtocol
specifier|public
class|class
name|TestFiDataTransferProtocol
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
DECL|field|conf
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
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
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
block|}
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
name|CommonConfigurationKeys
operator|.
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
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|DataTransferProtocol
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|ALL
parameter_list|)
constructor_decl|;
block|}
comment|/**    * 1. create files with dfs    * 2. write 1 byte    * 3. close file    * 4. open the same file    * 5. read the 1 byte and compare results    */
DECL|method|write1byte (String methodName)
specifier|static
name|void
name|write1byte
parameter_list|(
name|String
name|methodName
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|1
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
name|out
operator|.
name|write
argument_list|(
literal|1
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
specifier|final
name|int
name|b
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|b
argument_list|)
expr_stmt|;
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
DECL|method|runSlowDatanodeTest (String methodName, SleepAction a )
specifier|private
specifier|static
name|void
name|runSlowDatanodeTest
parameter_list|(
name|String
name|methodName
parameter_list|,
name|SleepAction
name|a
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
name|write1byte
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
block|}
DECL|method|runReceiverOpWriteBlockTest (String methodName, int errorIndex, Action<DatanodeID, IOException> a)
specifier|private
specifier|static
name|void
name|runReceiverOpWriteBlockTest
parameter_list|(
name|String
name|methodName
parameter_list|,
name|int
name|errorIndex
parameter_list|,
name|Action
argument_list|<
name|DatanodeID
argument_list|,
name|IOException
argument_list|>
name|a
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
name|fiReceiverOpWriteBlock
operator|.
name|set
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiPipelineInitErrorNonAppend
operator|.
name|set
argument_list|(
operator|new
name|VerificationAction
argument_list|(
name|methodName
argument_list|,
name|errorIndex
argument_list|)
argument_list|)
expr_stmt|;
name|write1byte
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
DECL|method|runStatusReadTest (String methodName, int errorIndex, Action<DatanodeID, IOException> a)
specifier|private
specifier|static
name|void
name|runStatusReadTest
parameter_list|(
name|String
name|methodName
parameter_list|,
name|int
name|errorIndex
parameter_list|,
name|Action
argument_list|<
name|DatanodeID
argument_list|,
name|IOException
argument_list|>
name|a
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
name|fiStatusRead
operator|.
name|set
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|t
operator|.
name|fiPipelineInitErrorNonAppend
operator|.
name|set
argument_list|(
operator|new
name|VerificationAction
argument_list|(
name|methodName
argument_list|,
name|errorIndex
argument_list|)
argument_list|)
expr_stmt|;
name|write1byte
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
DECL|method|runCallWritePacketToDisk (String methodName, int errorIndex, Action<DatanodeID, IOException> a)
specifier|private
specifier|static
name|void
name|runCallWritePacketToDisk
parameter_list|(
name|String
name|methodName
parameter_list|,
name|int
name|errorIndex
parameter_list|,
name|Action
argument_list|<
name|DatanodeID
argument_list|,
name|IOException
argument_list|>
name|a
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
name|fiCallWritePacketToDisk
operator|.
name|set
argument_list|(
name|a
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
name|errorIndex
argument_list|)
argument_list|)
expr_stmt|;
name|write1byte
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
comment|/**    * Pipeline setup:    * DN0 never responses after received setup request from client.    * Client gets an IOException and determine DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_01 ()
specifier|public
name|void
name|pipeline_Fi_01
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
name|runReceiverOpWriteBlockTest
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup:    * DN1 never responses after received setup request from client.    * Client gets an IOException and determine DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_02 ()
specifier|public
name|void
name|pipeline_Fi_02
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
name|runReceiverOpWriteBlockTest
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup:    * DN2 never responses after received setup request from client.    * Client gets an IOException and determine DN2 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_03 ()
specifier|public
name|void
name|pipeline_Fi_03
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
name|runReceiverOpWriteBlockTest
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup, DN1 never responses after received setup ack from DN2.    * Client gets an IOException and determine DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_04 ()
specifier|public
name|void
name|pipeline_Fi_04
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
name|runStatusReadTest
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup, DN0 never responses after received setup ack from DN1.    * Client gets an IOException and determine DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_05 ()
specifier|public
name|void
name|pipeline_Fi_05
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
name|runStatusReadTest
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup with DN0 very slow but it won't lead to timeout.    * Client finishes setup successfully.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_06 ()
specifier|public
name|void
name|pipeline_Fi_06
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
name|runSlowDatanodeTest
argument_list|(
name|methodName
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
literal|3000
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup with DN1 very slow but it won't lead to timeout.    * Client finishes setup successfully.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_07 ()
specifier|public
name|void
name|pipeline_Fi_07
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
name|runSlowDatanodeTest
argument_list|(
name|methodName
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
literal|3000
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup with DN2 very slow but it won't lead to timeout.    * Client finishes setup successfully.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_08 ()
specifier|public
name|void
name|pipeline_Fi_08
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
name|runSlowDatanodeTest
argument_list|(
name|methodName
argument_list|,
operator|new
name|SleepAction
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
literal|3000
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup, DN0 throws an OutOfMemoryException right after it    * received a setup request from client.    * Client gets an IOException and determine DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_09 ()
specifier|public
name|void
name|pipeline_Fi_09
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
name|runReceiverOpWriteBlockTest
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
operator|new
name|OomAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup, DN1 throws an OutOfMemoryException right after it    * received a setup request from DN0.    * Client gets an IOException and determine DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_10 ()
specifier|public
name|void
name|pipeline_Fi_10
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
name|runReceiverOpWriteBlockTest
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
operator|new
name|OomAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup, DN2 throws an OutOfMemoryException right after it    * received a setup request from DN1.    * Client gets an IOException and determine DN2 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_11 ()
specifier|public
name|void
name|pipeline_Fi_11
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
name|runReceiverOpWriteBlockTest
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
operator|new
name|OomAction
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup, DN1 throws an OutOfMemoryException right after it    * received a setup ack from DN2.    * Client gets an IOException and determine DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_12 ()
specifier|public
name|void
name|pipeline_Fi_12
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
name|runStatusReadTest
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
operator|new
name|OomAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pipeline setup, DN0 throws an OutOfMemoryException right after it    * received a setup ack from DN1.    * Client gets an IOException and determine DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_13 ()
specifier|public
name|void
name|pipeline_Fi_13
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
name|runStatusReadTest
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
operator|new
name|OomAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Write a packet, DN0 throws a DiskOutOfSpaceError    * when it writes the data to disk.    * Client gets an IOException and determine DN0 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_14 ()
specifier|public
name|void
name|pipeline_Fi_14
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
name|runCallWritePacketToDisk
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|,
operator|new
name|DoosAction
argument_list|(
name|methodName
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Write a packet, DN1 throws a DiskOutOfSpaceError    * when it writes the data to disk.    * Client gets an IOException and determine DN1 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_15 ()
specifier|public
name|void
name|pipeline_Fi_15
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
name|runCallWritePacketToDisk
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|,
operator|new
name|DoosAction
argument_list|(
name|methodName
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Streaming: Write a packet, DN2 throws a DiskOutOfSpaceError    * when it writes the data to disk.    * Client gets an IOException and determine DN2 bad.    */
annotation|@
name|Test
DECL|method|pipeline_Fi_16 ()
specifier|public
name|void
name|pipeline_Fi_16
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
name|runCallWritePacketToDisk
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|,
operator|new
name|DoosAction
argument_list|(
name|methodName
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

