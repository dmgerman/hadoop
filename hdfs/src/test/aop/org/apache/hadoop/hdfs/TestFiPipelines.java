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
name|server
operator|.
name|datanode
operator|.
name|BlockReceiverAspects
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
name|namenode
operator|.
name|FSNamesystem
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
DECL|class|TestFiPipelines
specifier|public
class|class
name|TestFiPipelines
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestFiPipelines
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REPL_FACTOR
specifier|private
specifier|static
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
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutDownCluster ()
specifier|synchronized
specifier|public
name|void
name|shutDownCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test initiates and sets actions created by injection framework. The actions    * work with both aspects of sending acknologment packets in a pipeline.    * Creates and closes a file of certain length< packet size.    * Injected actions will check if number of visible bytes at datanodes equals    * to number of acknoleged bytes    *    * @throws IOException in case of an error    */
annotation|@
name|Test
DECL|method|pipeline_04 ()
specifier|public
name|void
name|pipeline_04
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
specifier|final
name|PipelinesTestUtil
operator|.
name|PipelinesTest
name|pipst
init|=
operator|(
name|PipelinesTestUtil
operator|.
name|PipelinesTest
operator|)
name|PipelinesTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|pipst
operator|.
name|fiCallSetNumBytes
operator|.
name|set
argument_list|(
operator|new
name|PipelinesTestUtil
operator|.
name|ReceivedCheckAction
argument_list|(
name|METHOD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|pipst
operator|.
name|fiCallSetBytesAcked
operator|.
name|set
argument_list|(
operator|new
name|PipelinesTestUtil
operator|.
name|AckedCheckAction
argument_list|(
name|METHOD_NAME
argument_list|)
argument_list|)
expr_stmt|;
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
name|FSDataOutputStream
name|fsOut
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|TestPipelines
operator|.
name|writeData
argument_list|(
name|fsOut
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Similar to pipeline_04 but sends many packets into a pipeline     * @throws IOException in case of an error    */
annotation|@
name|Test
DECL|method|pipeline_05 ()
specifier|public
name|void
name|pipeline_05
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
specifier|final
name|PipelinesTestUtil
operator|.
name|PipelinesTest
name|pipst
init|=
operator|(
name|PipelinesTestUtil
operator|.
name|PipelinesTest
operator|)
name|PipelinesTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|pipst
operator|.
name|fiCallSetNumBytes
operator|.
name|set
argument_list|(
operator|new
name|PipelinesTestUtil
operator|.
name|ReceivedCheckAction
argument_list|(
name|METHOD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|pipst
operator|.
name|fiCallSetBytesAcked
operator|.
name|set
argument_list|(
operator|new
name|PipelinesTestUtil
operator|.
name|AckedCheckAction
argument_list|(
name|METHOD_NAME
argument_list|)
argument_list|)
expr_stmt|;
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
name|FSDataOutputStream
name|fsOut
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
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
literal|17
condition|;
name|i
operator|++
control|)
block|{
name|TestPipelines
operator|.
name|writeData
argument_list|(
name|fsOut
argument_list|,
literal|23
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * This quite tricky test prevents acknowledgement packets from a datanode    * This should block any write attempts after ackQueue is full.    * Test is blocking, so the MiniDFSCluster has to be killed harshly.    * @throws IOException in case of an error    */
annotation|@
name|Test
DECL|method|pipeline_06 ()
specifier|public
name|void
name|pipeline_06
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
specifier|final
name|int
name|MAX_PACKETS
init|=
literal|80
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
specifier|final
name|PipelinesTestUtil
operator|.
name|PipelinesTest
name|pipst
init|=
operator|(
name|PipelinesTestUtil
operator|.
name|PipelinesTest
operator|)
name|PipelinesTestUtil
operator|.
name|initTest
argument_list|()
decl_stmt|;
name|pipst
operator|.
name|setSuspend
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// This is ack. suspend test
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
name|FSDataOutputStream
name|fsOut
init|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
try|try
block|{
comment|// At this point let's start an external checker thread, which will
comment|// verify the test's results and shutdown the MiniDFSCluster for us,
comment|// because what it's gonna do has BLOCKING effect on datanodes
name|QueueChecker
name|cq
init|=
operator|new
name|QueueChecker
argument_list|(
name|pipst
argument_list|,
name|MAX_PACKETS
argument_list|)
decl_stmt|;
name|cq
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// The following value is explained by the fact that size of a packet isn't
comment|// necessary equals to the value of
comment|// DFSConfigKeys.DFS_CLIENT_WRITE_PACKET_SIZE_KEY
comment|// The actual logic is expressed in DFSClient#computePacketChunkSize
name|int
name|bytesToSend
init|=
literal|700
decl_stmt|;
while|while
condition|(
name|cnt
operator|<
literal|100
operator|&&
name|pipst
operator|.
name|getSuspend
argument_list|()
condition|)
block|{
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
literal|"_06(): "
operator|+
name|cnt
operator|++
operator|+
literal|" sending another "
operator|+
name|bytesToSend
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
block|}
name|TestPipelines
operator|.
name|writeData
argument_list|(
name|fsOut
argument_list|,
name|bytesToSend
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Getting unexpected exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
literal|"Last queued packet number "
operator|+
name|pipst
operator|.
name|getLastQueued
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Shouldn't be able to send more than 81 packet"
argument_list|,
name|pipst
operator|.
name|getLastQueued
argument_list|()
operator|<=
literal|81
argument_list|)
expr_stmt|;
block|}
DECL|class|QueueChecker
specifier|private
class|class
name|QueueChecker
extends|extends
name|Thread
block|{
DECL|field|test
name|PipelinesTestUtil
operator|.
name|PipelinesTest
name|test
decl_stmt|;
DECL|field|MAX
specifier|final
name|int
name|MAX
decl_stmt|;
DECL|field|done
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|method|QueueChecker (PipelinesTestUtil.PipelinesTest handle, int maxPackets)
specifier|public
name|QueueChecker
parameter_list|(
name|PipelinesTestUtil
operator|.
name|PipelinesTest
name|handle
parameter_list|,
name|int
name|maxPackets
parameter_list|)
block|{
name|test
operator|=
name|handle
expr_stmt|;
name|MAX
operator|=
name|maxPackets
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|done
condition|)
block|{
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
literal|"_06: checking for the limit "
operator|+
name|test
operator|.
name|getLastQueued
argument_list|()
operator|+
literal|" and "
operator|+
name|MAX
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|test
operator|.
name|getLastQueued
argument_list|()
operator|>=
name|MAX
condition|)
block|{
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
literal|"FI: Resume packets acking"
argument_list|)
expr_stmt|;
block|}
name|test
operator|.
name|setSuspend
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//Do not suspend ack sending any more
name|done
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|done
condition|)
try|try
block|{
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
literal|"_06: MAX isn't reached yet. Current="
operator|+
name|test
operator|.
name|getLastQueued
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{ }
block|}
name|assertTrue
argument_list|(
literal|"Shouldn't be able to send more than 81 packet"
argument_list|,
name|test
operator|.
name|getLastQueued
argument_list|()
operator|<=
literal|81
argument_list|)
expr_stmt|;
try|try
block|{
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
literal|"_06: shutting down the cluster"
argument_list|)
expr_stmt|;
block|}
comment|// It has to be done like that, because local version of shutDownCluster()
comment|// won't work, because it tries to close an instance of FileSystem too.
comment|// Which is where the waiting is happening.
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|shutDownCluster
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
literal|"End QueueChecker thread"
argument_list|)
expr_stmt|;
block|}
block|}
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
name|DFSConfigKeys
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
name|DFSConfigKeys
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
name|DFSConfigKeys
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
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SUPPORT_APPEND_KEY
argument_list|,
literal|true
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
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|stateChangeLog
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
name|FSNamesystem
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
name|DataNode
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
name|TestFiPipelines
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
name|FiTestUtil
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
name|BlockReceiverAspects
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
name|DFSClientAspects
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
block|}
end_class

end_unit

