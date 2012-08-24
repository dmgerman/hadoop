begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|client
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
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
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
name|URI
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
name|hdfs
operator|.
name|qjournal
operator|.
name|client
operator|.
name|AsyncLogger
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
name|qjournal
operator|.
name|client
operator|.
name|QuorumException
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
name|qjournal
operator|.
name|client
operator|.
name|QuorumJournalManager
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|GetJournalStateResponseProto
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|NewEpochResponseProto
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
name|EditLogOutputStream
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
name|NamespaceInfo
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
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Stubber
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Futures
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|SettableFuture
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
name|qjournal
operator|.
name|QJMTestUtil
operator|.
name|writeOp
import|;
end_import

begin_comment
comment|/**  * True unit tests for QuorumJournalManager  */
end_comment

begin_class
DECL|class|TestQuorumJournalManagerUnit
specifier|public
class|class
name|TestQuorumJournalManagerUnit
block|{
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|QuorumJournalManager
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
DECL|field|FAKE_NSINFO
specifier|private
specifier|static
specifier|final
name|NamespaceInfo
name|FAKE_NSINFO
init|=
operator|new
name|NamespaceInfo
argument_list|(
literal|12345
argument_list|,
literal|"mycluster"
argument_list|,
literal|"my-bp"
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|spyLoggers
specifier|private
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|spyLoggers
decl_stmt|;
DECL|field|qjm
specifier|private
name|QuorumJournalManager
name|qjm
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|spyLoggers
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|mockLogger
argument_list|()
argument_list|,
name|mockLogger
argument_list|()
argument_list|,
name|mockLogger
argument_list|()
argument_list|)
expr_stmt|;
name|qjm
operator|=
operator|new
name|QuorumJournalManager
argument_list|(
name|conf
argument_list|,
operator|new
name|URI
argument_list|(
literal|"qjournal://host/jid"
argument_list|)
argument_list|,
name|FAKE_NSINFO
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|createLoggers
parameter_list|(
name|AsyncLogger
operator|.
name|Factory
name|factory
parameter_list|)
block|{
return|return
name|spyLoggers
return|;
block|}
block|}
expr_stmt|;
for|for
control|(
name|AsyncLogger
name|logger
range|:
name|spyLoggers
control|)
block|{
name|futureReturns
argument_list|(
name|GetJournalStateResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setLastPromisedEpoch
argument_list|(
literal|0
argument_list|)
operator|.
name|setHttpPort
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|logger
argument_list|)
operator|.
name|getJournalState
argument_list|()
expr_stmt|;
name|futureReturns
argument_list|(
name|NewEpochResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|logger
argument_list|)
operator|.
name|newEpoch
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|logger
argument_list|)
operator|.
name|format
argument_list|(
name|Mockito
operator|.
expr|<
name|NamespaceInfo
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|qjm
operator|.
name|recoverUnfinalizedSegments
argument_list|()
expr_stmt|;
block|}
DECL|method|mockLogger ()
specifier|private
name|AsyncLogger
name|mockLogger
parameter_list|()
block|{
return|return
name|Mockito
operator|.
name|mock
argument_list|(
name|AsyncLogger
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|futureReturns (V value)
specifier|static
parameter_list|<
name|V
parameter_list|>
name|Stubber
name|futureReturns
parameter_list|(
name|V
name|value
parameter_list|)
block|{
name|ListenableFuture
argument_list|<
name|V
argument_list|>
name|ret
init|=
name|Futures
operator|.
name|immediateFuture
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|Mockito
operator|.
name|doReturn
argument_list|(
name|ret
argument_list|)
return|;
block|}
DECL|method|futureThrows (Throwable t)
specifier|static
name|Stubber
name|futureThrows
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|ListenableFuture
argument_list|<
name|?
argument_list|>
name|ret
init|=
name|Futures
operator|.
name|immediateFailedFuture
argument_list|(
name|t
argument_list|)
decl_stmt|;
return|return
name|Mockito
operator|.
name|doReturn
argument_list|(
name|ret
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testAllLoggersStartOk ()
specifier|public
name|void
name|testAllLoggersStartOk
parameter_list|()
throws|throws
name|Exception
block|{
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|qjm
operator|.
name|startLogSegment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQuorumOfLoggersStartOk ()
specifier|public
name|void
name|testQuorumOfLoggersStartOk
parameter_list|()
throws|throws
name|Exception
block|{
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureThrows
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"logger failed"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|qjm
operator|.
name|startLogSegment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQuorumOfLoggersFail ()
specifier|public
name|void
name|testQuorumOfLoggersFail
parameter_list|()
throws|throws
name|Exception
block|{
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureThrows
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"logger failed"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureThrows
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"logger failed"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|qjm
operator|.
name|startLogSegment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw when quorum failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QuorumException
name|qe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"logger failed"
argument_list|,
name|qe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteEdits ()
specifier|public
name|void
name|testWriteEdits
parameter_list|()
throws|throws
name|Exception
block|{
name|EditLogOutputStream
name|stm
init|=
name|createLogSegment
argument_list|()
decl_stmt|;
name|writeOp
argument_list|(
name|stm
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writeOp
argument_list|(
name|stm
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|stm
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
name|writeOp
argument_list|(
name|stm
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// The flush should log txn 1-2
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|2
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|2
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|2
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|stm
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Another flush should now log txn #3
name|stm
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|3L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|3L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|3L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|stm
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteEditsOneSlow ()
specifier|public
name|void
name|testWriteEditsOneSlow
parameter_list|()
throws|throws
name|Exception
block|{
name|EditLogOutputStream
name|stm
init|=
name|createLogSegment
argument_list|()
decl_stmt|;
name|writeOp
argument_list|(
name|stm
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|stm
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
comment|// Make the first two logs respond immediately
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
comment|// And the third log not respond
name|SettableFuture
argument_list|<
name|Void
argument_list|>
name|slowLog
init|=
name|SettableFuture
operator|.
expr|<
name|Void
operator|>
name|create
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|slowLog
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|sendEdits
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|byte
index|[]
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|stm
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|createLogSegment ()
specifier|private
name|EditLogOutputStream
name|createLogSegment
parameter_list|()
throws|throws
name|IOException
block|{
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|futureReturns
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spyLoggers
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|startLogSegment
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|EditLogOutputStream
name|stm
init|=
name|qjm
operator|.
name|startLogSegment
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|stm
return|;
block|}
block|}
end_class

end_unit

