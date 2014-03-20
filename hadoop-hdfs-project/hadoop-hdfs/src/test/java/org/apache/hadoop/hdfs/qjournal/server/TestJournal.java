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
name|io
operator|.
name|File
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
name|FileUtil
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
name|protocol
operator|.
name|JournalOutOfSyncException
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
name|NewEpochResponseProtoOrBuilder
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
name|SegmentStateProto
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
name|common
operator|.
name|Storage
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
name|Storage
operator|.
name|StorageDirectory
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
name|StorageErrorReporter
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
name|io
operator|.
name|IOUtils
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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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

begin_class
DECL|class|TestJournal
specifier|public
class|class
name|TestJournal
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
argument_list|)
decl_stmt|;
DECL|field|FAKE_NSINFO_2
specifier|private
specifier|static
specifier|final
name|NamespaceInfo
name|FAKE_NSINFO_2
init|=
operator|new
name|NamespaceInfo
argument_list|(
literal|6789
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
literal|"test-journal"
decl_stmt|;
DECL|field|TEST_LOG_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_LOG_DIR
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
argument_list|,
literal|"TestJournal"
argument_list|)
decl_stmt|;
DECL|field|mockErrorReporter
specifier|private
name|StorageErrorReporter
name|mockErrorReporter
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageErrorReporter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|journal
specifier|private
name|Journal
name|journal
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
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_LOG_DIR
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|journal
operator|=
operator|new
name|Journal
argument_list|(
name|conf
argument_list|,
name|TEST_LOG_DIR
argument_list|,
name|JID
argument_list|,
name|mockErrorReporter
argument_list|)
expr_stmt|;
name|journal
operator|.
name|format
argument_list|(
name|FAKE_NSINFO
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|verifyNoStorageErrors ()
specifier|public
name|void
name|verifyNoStorageErrors
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|mockErrorReporter
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|reportErrorOnFile
argument_list|(
name|Mockito
operator|.
expr|<
name|File
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|journal
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test whether JNs can correctly handle editlog that cannot be decoded.    */
annotation|@
name|Test
DECL|method|testScanEditLog ()
specifier|public
name|void
name|testScanEditLog
parameter_list|()
throws|throws
name|Exception
block|{
comment|// use a future layout version
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// in the segment we write garbage editlog, which can be scanned but
comment|// cannot be decoded
specifier|final
name|int
name|numTxns
init|=
literal|5
decl_stmt|;
name|byte
index|[]
name|ops
init|=
name|QJMTestUtil
operator|.
name|createGabageTxns
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|numTxns
argument_list|,
name|ops
argument_list|)
expr_stmt|;
comment|// verify the in-progress editlog segment
name|SegmentStateProto
name|segmentState
init|=
name|journal
operator|.
name|getSegmentInfo
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|segmentState
operator|.
name|getIsInProgress
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numTxns
argument_list|,
name|segmentState
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|segmentState
operator|.
name|getStartTxId
argument_list|()
argument_list|)
expr_stmt|;
comment|// finalize the segment and verify it again
name|journal
operator|.
name|finalizeLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
name|numTxns
argument_list|)
expr_stmt|;
name|segmentState
operator|=
name|journal
operator|.
name|getSegmentInfo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|segmentState
operator|.
name|getIsInProgress
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numTxns
argument_list|,
name|segmentState
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|segmentState
operator|.
name|getStartTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testEpochHandling ()
specifier|public
name|void
name|testEpochHandling
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|journal
operator|.
name|getLastPromisedEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|NewEpochResponseProto
name|newEpoch
init|=
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|newEpoch
operator|.
name|hasLastSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|journal
operator|.
name|getLastPromisedEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newEpoch
operator|.
name|hasLastSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|journal
operator|.
name|getLastPromisedEpoch
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed to promise same epoch twice"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Proposed epoch 3<= last promise 3"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|12345L
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have rejected call from prior epoch"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"epoch 1 is less than the last promised epoch 3"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|12345L
argument_list|,
literal|100L
argument_list|,
literal|0
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have rejected call from prior epoch"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"epoch 1 is less than the last promised epoch 3"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMaintainCommittedTxId ()
specifier|public
name|void
name|testMaintainCommittedTxId
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
comment|// Send txids 1-3, with a request indicating only 0 committed
name|journal
operator|.
name|journal
argument_list|(
operator|new
name|RequestInfo
argument_list|(
name|JID
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|journal
operator|.
name|getCommittedTxnIdForTests
argument_list|()
argument_list|)
expr_stmt|;
comment|// Send 4-6, with request indicating that through 3 is committed.
name|journal
operator|.
name|journal
argument_list|(
operator|new
name|RequestInfo
argument_list|(
name|JID
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|4
argument_list|,
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|journal
operator|.
name|getCommittedTxnIdForTests
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testRestartJournal ()
specifier|public
name|void
name|testRestartJournal
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1
argument_list|,
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
expr_stmt|;
comment|// Don't finalize.
name|String
name|storageString
init|=
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|toColonSeparatedString
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"storage string: "
operator|+
name|storageString
argument_list|)
expr_stmt|;
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close to unlock the storage dir
comment|// Now re-instantiate, make sure history is still there
name|journal
operator|=
operator|new
name|Journal
argument_list|(
name|conf
argument_list|,
name|TEST_LOG_DIR
argument_list|,
name|JID
argument_list|,
name|mockErrorReporter
argument_list|)
expr_stmt|;
comment|// The storage info should be read, even if no writer has taken over.
name|assertEquals
argument_list|(
name|storageString
argument_list|,
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|toColonSeparatedString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|journal
operator|.
name|getLastPromisedEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|NewEpochResponseProtoOrBuilder
name|newEpoch
init|=
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|newEpoch
operator|.
name|getLastSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testFormatResetsCachedValues ()
specifier|public
name|void
name|testFormatResetsCachedValues
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|12345L
argument_list|)
expr_stmt|;
name|journal
operator|.
name|startLogSegment
argument_list|(
operator|new
name|RequestInfo
argument_list|(
name|JID
argument_list|,
literal|12345L
argument_list|,
literal|1L
argument_list|,
literal|0L
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345L
argument_list|,
name|journal
operator|.
name|getLastPromisedEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345L
argument_list|,
name|journal
operator|.
name|getLastWriterEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|isFormatted
argument_list|()
argument_list|)
expr_stmt|;
comment|// Close the journal in preparation for reformatting it.
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
name|journal
operator|.
name|format
argument_list|(
name|FAKE_NSINFO_2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|journal
operator|.
name|getLastPromisedEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|journal
operator|.
name|getLastWriterEpoch
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|isFormatted
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, if the writer crashes at the very beginning of a segment,    * before any transactions are written, that the next newEpoch() call    * returns the prior segment txid as its most recent segment.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testNewEpochAtBeginningOfSegment ()
specifier|public
name|void
name|testNewEpochAtBeginningOfSegment
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1
argument_list|,
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
expr_stmt|;
name|journal
operator|.
name|finalizeLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|3
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|NewEpochResponseProto
name|resp
init|=
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|resp
operator|.
name|getLastSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testJournalLocking ()
specifier|public
name|void
name|testJournalLocking
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
operator|.
name|isLockSupported
argument_list|()
argument_list|)
expr_stmt|;
name|StorageDirectory
name|sd
init|=
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageDir
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|,
name|Storage
operator|.
name|STORAGE_FILE_LOCK
argument_list|)
decl_stmt|;
comment|// Journal should be locked, since the format() call locks it.
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|lockFile
argument_list|)
expr_stmt|;
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|Journal
argument_list|(
name|conf
argument_list|,
name|TEST_LOG_DIR
argument_list|,
name|JID
argument_list|,
name|mockErrorReporter
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail to create another journal in same dir"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot lock storage"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Journal should no longer be locked after the close() call.
comment|// Hence, should be able to create a new Journal in the same dir.
name|Journal
name|journal2
init|=
operator|new
name|Journal
argument_list|(
name|conf
argument_list|,
name|TEST_LOG_DIR
argument_list|,
name|JID
argument_list|,
name|mockErrorReporter
argument_list|)
decl_stmt|;
name|journal2
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|journal2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test finalizing a segment after some batch of edits were missed.    * This should fail, since we validate the log before finalization.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testFinalizeWhenEditsAreMissed ()
specifier|public
name|void
name|testFinalizeWhenEditsAreMissed
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try to finalize up to txn 6, even though we only wrote up to txn 3.
try|try
block|{
name|journal
operator|.
name|finalizeLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not fail to finalize"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JournalOutOfSyncException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"but only written up to txid 3"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Check that, even if we re-construct the journal by scanning the
comment|// disk, we don't allow finalizing incorrectly.
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
name|journal
operator|=
operator|new
name|Journal
argument_list|(
name|conf
argument_list|,
name|TEST_LOG_DIR
argument_list|,
name|JID
argument_list|,
name|mockErrorReporter
argument_list|)
expr_stmt|;
try|try
block|{
name|journal
operator|.
name|finalizeLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not fail to finalize"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JournalOutOfSyncException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"disk only contains up to txid 3"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that finalizing a segment which doesn't exist throws the    * appropriate exception.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testFinalizeMissingSegment ()
specifier|public
name|void
name|testFinalizeMissingSegment
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|journal
operator|.
name|finalizeLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1000
argument_list|,
literal|1001
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not fail to finalize"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JournalOutOfSyncException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"No log file to finalize at transaction ID 1000"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assume that a client is writing to a journal, but loses its connection    * in the middle of a segment. Thus, any future journal() calls in that    * segment may fail, because some txns were missed while the connection was    * down.    *    * Eventually, the connection comes back, and the NN tries to start a new    * segment at a higher txid. This should abort the old one and succeed.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testAbortOldSegmentIfFinalizeIsMissed ()
specifier|public
name|void
name|testAbortOldSegmentIfFinalizeIsMissed
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Start a segment at txid 1, and write a batch of 3 txns.
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getInProgressEditLog
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try to start new segment at txid 6, this should abort old segment and
comment|// then succeed, allowing us to write txid 6-9.
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|6
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|6
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|6
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// The old segment should *not* be finalized.
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getInProgressEditLog
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|journal
operator|.
name|getStorage
argument_list|()
operator|.
name|getInProgressEditLog
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test behavior of startLogSegment() when a segment with the    * same transaction ID already exists.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testStartLogSegmentWhenAlreadyExists ()
specifier|public
name|void
name|testStartLogSegmentWhenAlreadyExists
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Start a segment at txid 1, and write just 1 transaction. This
comment|// would normally be the START_LOG_SEGMENT transaction.
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
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
expr_stmt|;
comment|// Try to start new segment at txid 1, this should succeed, because
comment|// we are allowed to re-start a segment if we only ever had the
comment|// START_LOG_SEGMENT transaction logged.
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
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
expr_stmt|;
comment|// This time through, write more transactions afterwards, simulating
comment|// real user transactions.
name|journal
operator|.
name|journal
argument_list|(
name|makeRI
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
name|QJMTestUtil
operator|.
name|createTxnData
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|6
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail to start log segment which would overwrite "
operator|+
literal|"an existing one"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"seems to contain valid transactions"
argument_list|,
name|ise
argument_list|)
expr_stmt|;
block|}
name|journal
operator|.
name|finalizeLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|7
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|// Ensure that we cannot overwrite a finalized segment
try|try
block|{
name|journal
operator|.
name|startLogSegment
argument_list|(
name|makeRI
argument_list|(
literal|8
argument_list|)
argument_list|,
literal|1
argument_list|,
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail to start log segment which would overwrite "
operator|+
literal|"an existing one"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"have a finalized segment"
argument_list|,
name|ise
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeRI (int serial)
specifier|private
specifier|static
name|RequestInfo
name|makeRI
parameter_list|(
name|int
name|serial
parameter_list|)
block|{
return|return
operator|new
name|RequestInfo
argument_list|(
name|JID
argument_list|,
literal|1
argument_list|,
name|serial
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testNamespaceVerification ()
specifier|public
name|void
name|testNamespaceVerification
parameter_list|()
throws|throws
name|Exception
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|journal
operator|.
name|newEpoch
argument_list|(
name|FAKE_NSINFO_2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail newEpoch() when namespaces mismatched"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Incompatible namespaceID"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

