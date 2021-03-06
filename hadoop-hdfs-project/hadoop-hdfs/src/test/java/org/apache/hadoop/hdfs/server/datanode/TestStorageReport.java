begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|StorageType
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
name|DistributedFileSystem
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|DatanodeRegistration
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
name|DatanodeStorage
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
name|SlowDiskReports
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
name|SlowPeerReports
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
name|StorageReport
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertNotSame
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
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyLong
import|;
end_import

begin_class
DECL|class|TestStorageReport
specifier|public
class|class
name|TestStorageReport
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
name|TestStorageReport
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
literal|1
decl_stmt|;
DECL|field|storageType
specifier|private
specifier|static
specifier|final
name|StorageType
name|storageType
init|=
name|StorageType
operator|.
name|SSD
decl_stmt|;
comment|// pick non-default.
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
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
DECL|field|bpid
specifier|static
name|String
name|bpid
decl_stmt|;
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
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
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
name|numDataNodes
argument_list|(
name|REPL_FACTOR
argument_list|)
operator|.
name|storageTypes
argument_list|(
operator|new
name|StorageType
index|[]
block|{
name|storageType
block|,
name|storageType
block|}
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
name|bpid
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
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
name|cluster
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
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
comment|/**    * Ensure that storage type and storage state are propagated    * in Storage Reports.    */
annotation|@
name|Test
DECL|method|testStorageReportHasStorageTypeAndState ()
specifier|public
name|void
name|testStorageReportHasStorageTypeAndState
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Make sure we are not testing with the default type, that would not
comment|// be a very good test.
name|assertNotSame
argument_list|(
name|storageType
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Insert a spy object for the NN RPC.
name|DatanodeProtocolClientSideTranslatorPB
name|nnSpy
init|=
name|InternalDataNodeTestUtils
operator|.
name|spyOnBposToNN
argument_list|(
name|dn
argument_list|,
name|nn
argument_list|)
decl_stmt|;
comment|// Trigger a heartbeat so there is an interaction with the spy
comment|// object.
name|DataNodeTestUtils
operator|.
name|triggerHeartbeat
argument_list|(
name|dn
argument_list|)
expr_stmt|;
comment|// Verify that the callback passed in the expected parameters.
name|ArgumentCaptor
argument_list|<
name|StorageReport
index|[]
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|nnSpy
argument_list|)
operator|.
name|sendHeartbeat
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyBoolean
argument_list|()
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|SlowPeerReports
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|SlowDiskReports
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|StorageReport
index|[]
name|reports
init|=
name|captor
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageReport
name|report
range|:
name|reports
control|)
block|{
name|assertThat
argument_list|(
name|report
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|storageType
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|report
operator|.
name|getStorage
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|,
name|is
argument_list|(
name|DatanodeStorage
operator|.
name|State
operator|.
name|NORMAL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

