begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.journalservice
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
name|journalservice
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|FileSystemTestHelper
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|FenceResponse
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
name|protocol
operator|.
name|FencedException
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
name|protocol
operator|.
name|JournalInfo
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Tests for {@link JournalService}  */
end_comment

begin_class
DECL|class|TestJournalService
specifier|public
class|class
name|TestJournalService
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|/**    * Test calls backs {@link JournalListener#rollLogs(JournalService, long)} and    * {@link JournalListener#journal(JournalService, long, int, byte[])} are    * called.    */
annotation|@
name|Test
DECL|method|testCallBacks ()
specifier|public
name|void
name|testCallBacks
parameter_list|()
throws|throws
name|Exception
block|{
name|JournalListener
name|listener
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|JournalListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|JournalService
name|service
init|=
literal|null
decl_stmt|;
try|try
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|service
operator|=
name|startJournalService
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|verifyRollLogsCallback
argument_list|(
name|service
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|verifyJournalCallback
argument_list|(
name|service
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|verifyFence
argument_list|(
name|service
argument_list|,
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|stop
argument_list|()
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
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|startJournalService (JournalListener listener)
specifier|private
name|JournalService
name|startJournalService
parameter_list|(
name|JournalListener
name|listener
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|nnAddr
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNameNodeAddress
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|serverAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|JournalService
name|service
init|=
operator|new
name|JournalService
argument_list|(
name|conf
argument_list|,
name|nnAddr
argument_list|,
name|serverAddr
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|service
return|;
block|}
comment|/**    * Starting {@link JournalService} should result in Namenode calling    * {@link JournalService#startLogSegment}, resulting in callback     * {@link JournalListener#rollLogs}    */
DECL|method|verifyRollLogsCallback (JournalService s, JournalListener l)
specifier|private
name|void
name|verifyRollLogsCallback
parameter_list|(
name|JournalService
name|s
parameter_list|,
name|JournalListener
name|l
parameter_list|)
throws|throws
name|IOException
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|l
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|rollLogs
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|s
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * File system write operations should result in JournalListener call    * backs.    */
DECL|method|verifyJournalCallback (JournalService s, JournalListener l)
specifier|private
name|void
name|verifyJournalCallback
parameter_list|(
name|JournalService
name|s
parameter_list|,
name|JournalListener
name|l
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/verifyJournalCallback"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileSystemTestHelper
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|fileName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|l
argument_list|,
name|Mockito
operator|.
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|journal
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|s
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
operator|(
name|byte
index|[]
operator|)
name|Mockito
operator|.
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyFence (JournalService s, NameNode nn)
specifier|public
name|void
name|verifyFence
parameter_list|(
name|JournalService
name|s
parameter_list|,
name|NameNode
name|nn
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|cid
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|int
name|nsId
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getNamespaceID
argument_list|()
decl_stmt|;
name|int
name|lv
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
comment|// Fence the journal service
name|JournalInfo
name|info
init|=
operator|new
name|JournalInfo
argument_list|(
name|lv
argument_list|,
name|cid
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
name|long
name|currentEpoch
init|=
name|s
operator|.
name|getEpoch
argument_list|()
decl_stmt|;
comment|// New epoch lower than the current epoch is rejected
try|try
block|{
name|s
operator|.
name|fence
argument_list|(
name|info
argument_list|,
operator|(
name|currentEpoch
operator|-
literal|1
operator|)
argument_list|,
literal|"fencer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FencedException
name|ignore
parameter_list|)
block|{
comment|/* Ignored */
block|}
comment|// New epoch equal to the current epoch is rejected
try|try
block|{
name|s
operator|.
name|fence
argument_list|(
name|info
argument_list|,
name|currentEpoch
argument_list|,
literal|"fencer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FencedException
name|ignore
parameter_list|)
block|{
comment|/* Ignored */
block|}
comment|// New epoch higher than the current epoch is successful
name|FenceResponse
name|resp
init|=
name|s
operator|.
name|fence
argument_list|(
name|info
argument_list|,
name|currentEpoch
operator|+
literal|1
argument_list|,
literal|"fencer"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

