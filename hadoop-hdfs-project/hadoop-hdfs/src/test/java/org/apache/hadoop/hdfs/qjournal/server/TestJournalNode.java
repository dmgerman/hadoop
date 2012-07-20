begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.server
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
name|server
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
name|assertArrayEquals
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
name|assertFalse
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
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|URL
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
name|DFSTestUtil
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
name|HdfsConstants
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
name|QJMTestUtil
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|PrepareRecoveryResponseProto
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
name|server
operator|.
name|Journal
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
name|server
operator|.
name|JournalNode
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|primitives
operator|.
name|Bytes
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
name|primitives
operator|.
name|Ints
import|;
end_import

begin_class
DECL|class|TestJournalNode
specifier|public
class|class
name|TestJournalNode
block|{
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
argument_list|,
literal|0
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
DECL|field|jn
specifier|private
name|JournalNode
name|jn
decl_stmt|;
DECL|field|journal
specifier|private
name|Journal
name|journal
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
DECL|field|ch
specifier|private
name|IPCLoggerChannel
name|ch
decl_stmt|;
static|static
block|{
comment|// Avoid an error when we double-initialize JvmMetrics
name|DefaultMetricsSystem
operator|.
name|setMiniClusterMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_RPC_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|jn
operator|=
operator|new
name|JournalNode
argument_list|()
expr_stmt|;
name|jn
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|jn
operator|.
name|start
argument_list|()
expr_stmt|;
name|journal
operator|=
name|jn
operator|.
name|getOrCreateJournal
argument_list|(
name|JID
argument_list|)
expr_stmt|;
name|journal
operator|.
name|format
argument_list|(
name|FAKE_NSINFO
argument_list|)
expr_stmt|;
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
name|jn
operator|.
name|getBoundIpcAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|jn
operator|.
name|stop
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJournal ()
specifier|public
name|void
name|testJournal
parameter_list|()
throws|throws
name|Exception
block|{
name|IPCLoggerChannel
name|ch
init|=
operator|new
name|IPCLoggerChannel
argument_list|(
name|conf
argument_list|,
name|FAKE_NSINFO
argument_list|,
name|JID
argument_list|,
name|jn
operator|.
name|getBoundIpcAddress
argument_list|()
argument_list|)
decl_stmt|;
name|ch
operator|.
name|newEpoch
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ch
operator|.
name|startLogSegment
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|"hello"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReturnsSegmentInfoAtEpochTransition ()
specifier|public
name|void
name|testReturnsSegmentInfoAtEpochTransition
parameter_list|()
throws|throws
name|Exception
block|{
name|ch
operator|.
name|newEpoch
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ch
operator|.
name|startLogSegment
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Switch to a new epoch without closing earlier segment
name|NewEpochResponseProto
name|response
init|=
name|ch
operator|.
name|newEpoch
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getLastSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
name|ch
operator|.
name|finalizeLogSegment
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Switch to a new epoch after just closing the earlier segment.
name|response
operator|=
name|ch
operator|.
name|newEpoch
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getLastSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Start a segment but don't write anything, check newEpoch segment info
name|ch
operator|.
name|startLogSegment
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|response
operator|=
name|ch
operator|.
name|newEpoch
argument_list|(
literal|4
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|response
operator|.
name|getLastSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHttpServer ()
specifier|public
name|void
name|testHttpServer
parameter_list|()
throws|throws
name|Exception
block|{
name|InetSocketAddress
name|addr
init|=
name|jn
operator|.
name|getBoundHttpAddress
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|urlRoot
init|=
literal|"http://localhost:"
operator|+
name|addr
operator|.
name|getPort
argument_list|()
decl_stmt|;
comment|// Check default servlets.
name|String
name|pageContents
init|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
operator|new
name|URL
argument_list|(
name|urlRoot
operator|+
literal|"/jmx"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Bad contents: "
operator|+
name|pageContents
argument_list|,
name|pageContents
operator|.
name|contains
argument_list|(
literal|"Hadoop:service=JournalNode,name=JvmMetrics"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check JSP page.
name|pageContents
operator|=
name|DFSTestUtil
operator|.
name|urlGet
argument_list|(
operator|new
name|URL
argument_list|(
name|urlRoot
operator|+
literal|"/journalstatus.jsp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pageContents
operator|.
name|contains
argument_list|(
literal|"JournalNode"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create some edits on server side
name|byte
index|[]
name|EDITS_DATA
init|=
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|IPCLoggerChannel
name|ch
init|=
operator|new
name|IPCLoggerChannel
argument_list|(
name|conf
argument_list|,
name|FAKE_NSINFO
argument_list|,
name|JID
argument_list|,
name|jn
operator|.
name|getBoundIpcAddress
argument_list|()
argument_list|)
decl_stmt|;
name|ch
operator|.
name|newEpoch
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ch
operator|.
name|startLogSegment
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
name|EDITS_DATA
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|finalizeLogSegment
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Attempt to retrieve via HTTP, ensure we get the data back
comment|// including the header we expected
name|byte
index|[]
name|retrievedViaHttp
init|=
name|DFSTestUtil
operator|.
name|urlGetBytes
argument_list|(
operator|new
name|URL
argument_list|(
name|urlRoot
operator|+
literal|"/getJournal?segmentTxId=1&jid="
operator|+
name|JID
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|expected
init|=
name|Bytes
operator|.
name|concat
argument_list|(
name|Ints
operator|.
name|toByteArray
argument_list|(
name|HdfsConstants
operator|.
name|LAYOUT_VERSION
argument_list|)
argument_list|,
name|EDITS_DATA
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|retrievedViaHttp
argument_list|)
expr_stmt|;
comment|// Attempt to fetch a non-existent file, check that we get an
comment|// error status code
name|URL
name|badUrl
init|=
operator|new
name|URL
argument_list|(
name|urlRoot
operator|+
literal|"/getJournal?segmentTxId=12345&jid="
operator|+
name|JID
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|badUrl
operator|.
name|openConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|404
argument_list|,
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that the JournalNode performs correctly as a Paxos    *<em>Acceptor</em> process.    */
annotation|@
name|Test
DECL|method|testAcceptRecoveryBehavior ()
specifier|public
name|void
name|testAcceptRecoveryBehavior
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We need to run newEpoch() first, or else we have no way to distinguish
comment|// different proposals for the same decision.
try|try
block|{
name|ch
operator|.
name|prepareRecovery
argument_list|(
literal|1L
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw IllegalState when trying to run paxos without an epoch"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ise
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"bad epoch"
argument_list|,
name|ise
argument_list|)
expr_stmt|;
block|}
name|ch
operator|.
name|newEpoch
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// prepare() with no previously accepted value and no logs present
name|PrepareRecoveryResponseProto
name|prep
init|=
name|ch
operator|.
name|prepareRecovery
argument_list|(
literal|1L
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Prep: "
operator|+
name|prep
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prep
operator|.
name|hasAcceptedInEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prep
operator|.
name|hasSegmentState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make a log segment, and prepare again -- this time should see the
comment|// segment existing.
name|ch
operator|.
name|startLogSegment
argument_list|(
literal|1L
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ch
operator|.
name|sendEdits
argument_list|(
literal|1L
argument_list|,
literal|1
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|prep
operator|=
name|ch
operator|.
name|prepareRecovery
argument_list|(
literal|1L
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Prep: "
operator|+
name|prep
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prep
operator|.
name|hasAcceptedInEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prep
operator|.
name|hasSegmentState
argument_list|()
argument_list|)
expr_stmt|;
comment|// accept() should save the accepted value in persistent storage
comment|// TODO: should be able to accept without a URL here
name|ch
operator|.
name|acceptRecovery
argument_list|(
name|prep
operator|.
name|getSegmentState
argument_list|()
argument_list|,
operator|new
name|URL
argument_list|(
literal|"file:///dev/null"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// So another prepare() call from a new epoch would return this value
name|ch
operator|.
name|newEpoch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|ch
operator|.
name|setEpoch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|prep
operator|=
name|ch
operator|.
name|prepareRecovery
argument_list|(
literal|1L
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|prep
operator|.
name|getAcceptedInEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|prep
operator|.
name|getSegmentState
argument_list|()
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
comment|// A prepare() or accept() call from an earlier epoch should now be rejected
name|ch
operator|.
name|setEpoch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|ch
operator|.
name|prepareRecovery
argument_list|(
literal|1L
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"prepare from earlier epoch not rejected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"epoch 1 is less than the last promised epoch 2"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|ch
operator|.
name|acceptRecovery
argument_list|(
name|prep
operator|.
name|getSegmentState
argument_list|()
argument_list|,
operator|new
name|URL
argument_list|(
literal|"file:///dev/null"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"accept from earlier epoch not rejected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"epoch 1 is less than the last promised epoch 2"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO:
comment|// - add test that checks formatting behavior
comment|// - add test that checks rejects newEpoch if nsinfo doesn't match
block|}
end_class

end_unit

