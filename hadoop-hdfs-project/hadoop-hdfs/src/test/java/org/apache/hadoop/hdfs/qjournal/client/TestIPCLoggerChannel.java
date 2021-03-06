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
name|*
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
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|TimeUnit
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
name|qjournal
operator|.
name|client
operator|.
name|IPCLoggerChannel
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
name|LoggerTooFarBehindException
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
name|QJournalProtocol
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
name|RequestInfo
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
name|NameNodeLayoutVersion
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
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
operator|.
name|DelayAnswer
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
import|;
end_import

begin_class
DECL|class|TestIPCLoggerChannel
specifier|public
class|class
name|TestIPCLoggerChannel
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
name|TestIPCLoggerChannel
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
DECL|field|JID
specifier|private
specifier|static
specifier|final
name|String
name|JID
init|=
literal|"test-journalid"
decl_stmt|;
DECL|field|FAKE_ADDR
specifier|private
specifier|static
specifier|final
name|InetSocketAddress
name|FAKE_ADDR
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|FAKE_DATA
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|FAKE_DATA
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
DECL|field|mockProxy
specifier|private
specifier|final
name|QJournalProtocol
name|mockProxy
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|QJournalProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ch
specifier|private
name|IPCLoggerChannel
name|ch
decl_stmt|;
DECL|field|LIMIT_QUEUE_SIZE_MB
specifier|private
specifier|static
specifier|final
name|int
name|LIMIT_QUEUE_SIZE_MB
init|=
literal|1
decl_stmt|;
DECL|field|LIMIT_QUEUE_SIZE_BYTES
specifier|private
specifier|static
specifier|final
name|int
name|LIMIT_QUEUE_SIZE_BYTES
init|=
name|LIMIT_QUEUE_SIZE_MB
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
annotation|@
name|Before
DECL|method|setupMock ()
specifier|public
name|void
name|setupMock
parameter_list|()
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_QJOURNAL_QUEUE_SIZE_LIMIT_KEY
argument_list|,
name|LIMIT_QUEUE_SIZE_MB
argument_list|)
expr_stmt|;
comment|// Channel to the mock object instead of a real IPC proxy.
name|ch
operator|=
operator|new
name|IPCLoggerChannel
argument_list|(
name|conf
argument_list|,
name|FAKE_NSINFO
argument_list|,
name|JID
argument_list|,
name|FAKE_ADDR
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|QJournalProtocol
name|getProxy
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mockProxy
return|;
block|}
block|}
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleCall ()
specifier|public
name|void
name|testSimpleCall
parameter_list|()
throws|throws
name|Exception
block|{
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|FAKE_DATA
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProxy
argument_list|)
operator|.
name|journal
argument_list|(
name|Mockito
operator|.
expr|<
name|RequestInfo
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|3
argument_list|)
argument_list|,
name|Mockito
operator|.
name|same
argument_list|(
name|FAKE_DATA
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, once the queue eclipses the configure size limit,    * calls to journal more data are rejected.    */
annotation|@
name|Test
DECL|method|testQueueLimiting ()
specifier|public
name|void
name|testQueueLimiting
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Block the underlying fake proxy from actually completing any calls.
name|DelayAnswer
name|delayer
init|=
operator|new
name|DelayAnswer
argument_list|(
name|LOG
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
name|delayer
argument_list|)
operator|.
name|when
argument_list|(
name|mockProxy
argument_list|)
operator|.
name|journal
argument_list|(
name|Mockito
operator|.
expr|<
name|RequestInfo
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
name|same
argument_list|(
name|FAKE_DATA
argument_list|)
argument_list|)
expr_stmt|;
comment|// Queue up the maximum number of calls.
name|int
name|numToQueue
init|=
name|LIMIT_QUEUE_SIZE_BYTES
operator|/
name|FAKE_DATA
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numToQueue
condition|;
name|i
operator|++
control|)
block|{
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1L
argument_list|,
operator|(
name|long
operator|)
name|i
argument_list|,
literal|1
argument_list|,
name|FAKE_DATA
argument_list|)
expr_stmt|;
block|}
comment|// The accounting should show the correct total number queued.
name|assertEquals
argument_list|(
name|LIMIT_QUEUE_SIZE_BYTES
argument_list|,
name|ch
operator|.
name|getQueuedEditsSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// Trying to queue any more should fail.
try|try
block|{
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1L
argument_list|,
name|numToQueue
operator|+
literal|1
argument_list|,
literal|1
argument_list|,
name|FAKE_DATA
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail to queue more calls after queue was full"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|ee
operator|.
name|getCause
argument_list|()
operator|instanceof
name|LoggerTooFarBehindException
operator|)
condition|)
block|{
throw|throw
name|ee
throw|;
block|}
block|}
name|delayer
operator|.
name|proceed
argument_list|()
expr_stmt|;
comment|// After we allow it to proceeed, it should chug through the original queue
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|ch
operator|.
name|getQueuedEditsSize
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, if the remote node gets unsynchronized (eg some edits were    * missed or the node rebooted), the client stops sending edits until    * the next roll. Test for HDFS-3726.    */
annotation|@
name|Test
DECL|method|testStopSendingEditsWhenOutOfSync ()
specifier|public
name|void
name|testStopSendingEditsWhenOutOfSync
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"injected error"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockProxy
argument_list|)
operator|.
name|journal
argument_list|(
name|Mockito
operator|.
expr|<
name|RequestInfo
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
name|same
argument_list|(
name|FAKE_DATA
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1L
argument_list|,
literal|1L
argument_list|,
literal|1
argument_list|,
name|FAKE_DATA
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Injected JOOSE did not cause sendEdits() to throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"injected"
argument_list|,
name|ee
argument_list|)
expr_stmt|;
block|}
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProxy
argument_list|)
operator|.
name|journal
argument_list|(
name|Mockito
operator|.
expr|<
name|RequestInfo
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
name|same
argument_list|(
name|FAKE_DATA
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ch
operator|.
name|isOutOfSync
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1L
argument_list|,
literal|2L
argument_list|,
literal|1
argument_list|,
name|FAKE_DATA
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"sendEdits() should throw until next roll"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"disabled until next roll"
argument_list|,
name|ee
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// It should have failed without even sending the edits, since it was not sync.
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProxy
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|journal
argument_list|(
name|Mockito
operator|.
expr|<
name|RequestInfo
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|2L
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Mockito
operator|.
name|same
argument_list|(
name|FAKE_DATA
argument_list|)
argument_list|)
expr_stmt|;
comment|// It should have sent a heartbeat instead.
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProxy
argument_list|)
operator|.
name|heartbeat
argument_list|(
name|Mockito
operator|.
expr|<
name|RequestInfo
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
comment|// After a roll, sending new edits should not fail.
name|ch
operator|.
name|startLogSegment
argument_list|(
literal|3L
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|ch
operator|.
name|isOutOfSync
argument_list|()
argument_list|)
expr_stmt|;
name|ch
operator|.
name|sendEdits
argument_list|(
literal|3L
argument_list|,
literal|3L
argument_list|,
literal|1
argument_list|,
name|FAKE_DATA
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

