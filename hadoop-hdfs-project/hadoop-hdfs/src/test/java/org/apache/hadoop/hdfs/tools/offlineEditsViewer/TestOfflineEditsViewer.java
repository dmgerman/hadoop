begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineEditsViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineEditsViewer
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|server
operator|.
name|namenode
operator|.
name|FSEditLogOpCodes
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
name|OfflineEditsViewerHelper
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
name|tools
operator|.
name|offlineEditsViewer
operator|.
name|OfflineEditsViewer
operator|.
name|Flags
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
DECL|class|TestOfflineEditsViewer
specifier|public
class|class
name|TestOfflineEditsViewer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestOfflineEditsViewer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|obsoleteOpCodes
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|FSEditLogOpCodes
argument_list|,
name|Boolean
argument_list|>
name|obsoleteOpCodes
init|=
operator|new
name|HashMap
argument_list|<
name|FSEditLogOpCodes
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|initializeObsoleteOpCodes
argument_list|()
expr_stmt|;
block|}
DECL|field|buildDir
specifier|private
specifier|static
name|String
name|buildDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
decl_stmt|;
DECL|field|cacheDir
specifier|private
specifier|static
name|String
name|cacheDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
literal|"build/test/cache"
argument_list|)
decl_stmt|;
comment|// to create edits and get edits filename
DECL|field|nnHelper
specifier|private
specifier|static
specifier|final
name|OfflineEditsViewerHelper
name|nnHelper
init|=
operator|new
name|OfflineEditsViewerHelper
argument_list|()
decl_stmt|;
comment|/**    * Initialize obsoleteOpCodes    *    * Reason for suppressing "deprecation" warnings:    *    * These are the opcodes that are not used anymore, some    * are marked deprecated, we need to include them here to make    * sure we exclude them when checking for completeness of testing,    * that's why the "deprecation" warnings are suppressed.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|initializeObsoleteOpCodes ()
specifier|private
specifier|static
name|void
name|initializeObsoleteOpCodes
parameter_list|()
block|{
name|obsoleteOpCodes
operator|.
name|put
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_DATANODE_ADD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|obsoleteOpCodes
operator|.
name|put
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_DATANODE_REMOVE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|obsoleteOpCodes
operator|.
name|put
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_SET_NS_QUOTA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|obsoleteOpCodes
operator|.
name|put
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_CLEAR_NS_QUOTA
argument_list|,
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
block|{
operator|new
name|File
argument_list|(
name|cacheDir
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test the OfflineEditsViewer    */
annotation|@
name|Test
DECL|method|testGenerated ()
specifier|public
name|void
name|testGenerated
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"START - testing with generated edits"
argument_list|)
expr_stmt|;
name|nnHelper
operator|.
name|startCluster
argument_list|(
name|buildDir
operator|+
literal|"/dfs/"
argument_list|)
expr_stmt|;
comment|// edits generated by nnHelper (MiniDFSCluster), should have all op codes
comment|// binary, XML, reparsed binary
name|String
name|edits
init|=
name|nnHelper
operator|.
name|generateEdits
argument_list|()
decl_stmt|;
name|String
name|editsParsedXml
init|=
name|cacheDir
operator|+
literal|"/editsParsed.xml"
decl_stmt|;
name|String
name|editsReparsed
init|=
name|cacheDir
operator|+
literal|"/editsReparsed"
decl_stmt|;
comment|// parse to XML then back to binary
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runOev
argument_list|(
name|edits
argument_list|,
name|editsParsedXml
argument_list|,
literal|"xml"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runOev
argument_list|(
name|editsParsedXml
argument_list|,
name|editsReparsed
argument_list|,
literal|"binary"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// judgment time
name|assertTrue
argument_list|(
literal|"Edits "
operator|+
name|edits
operator|+
literal|" should have all op codes"
argument_list|,
name|hasAllOpCodes
argument_list|(
name|edits
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Generated edits and reparsed (bin to XML to bin) should be same"
argument_list|,
name|filesEqualIgnoreTrailingZeros
argument_list|(
name|edits
argument_list|,
name|editsReparsed
argument_list|)
argument_list|)
expr_stmt|;
comment|// removes edits so do this at the end
name|nnHelper
operator|.
name|shutdownCluster
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"END"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecoveryMode ()
specifier|public
name|void
name|testRecoveryMode
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"START - testing with generated edits"
argument_list|)
expr_stmt|;
name|nnHelper
operator|.
name|startCluster
argument_list|(
name|buildDir
operator|+
literal|"/dfs/"
argument_list|)
expr_stmt|;
comment|// edits generated by nnHelper (MiniDFSCluster), should have all op codes
comment|// binary, XML, reparsed binary
name|String
name|edits
init|=
name|nnHelper
operator|.
name|generateEdits
argument_list|()
decl_stmt|;
comment|// Corrupt the file by truncating the end
name|FileChannel
name|editsFile
init|=
operator|new
name|FileOutputStream
argument_list|(
name|edits
argument_list|,
literal|true
argument_list|)
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|editsFile
operator|.
name|truncate
argument_list|(
name|editsFile
operator|.
name|size
argument_list|()
operator|-
literal|5
argument_list|)
expr_stmt|;
name|String
name|editsParsedXml
init|=
name|cacheDir
operator|+
literal|"/editsRecoveredParsed.xml"
decl_stmt|;
name|String
name|editsReparsed
init|=
name|cacheDir
operator|+
literal|"/editsRecoveredReparsed"
decl_stmt|;
name|String
name|editsParsedXml2
init|=
name|cacheDir
operator|+
literal|"/editsRecoveredParsed2.xml"
decl_stmt|;
comment|// Can't read the corrupted file without recovery mode
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runOev
argument_list|(
name|edits
argument_list|,
name|editsParsedXml
argument_list|,
literal|"xml"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// parse to XML then back to binary
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runOev
argument_list|(
name|edits
argument_list|,
name|editsParsedXml
argument_list|,
literal|"xml"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runOev
argument_list|(
name|editsParsedXml
argument_list|,
name|editsReparsed
argument_list|,
literal|"binary"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runOev
argument_list|(
name|editsReparsed
argument_list|,
name|editsParsedXml2
argument_list|,
literal|"xml"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// judgment time
name|assertTrue
argument_list|(
literal|"Test round trip"
argument_list|,
name|filesEqualIgnoreTrailingZeros
argument_list|(
name|editsParsedXml
argument_list|,
name|editsParsedXml2
argument_list|)
argument_list|)
expr_stmt|;
comment|// removes edits so do this at the end
name|nnHelper
operator|.
name|shutdownCluster
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"END"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStored ()
specifier|public
name|void
name|testStored
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"START - testing with stored reference edits"
argument_list|)
expr_stmt|;
comment|// reference edits stored with source code (see build.xml)
comment|// binary, XML, reparsed binary
name|String
name|editsStored
init|=
name|cacheDir
operator|+
literal|"/editsStored"
decl_stmt|;
name|String
name|editsStoredParsedXml
init|=
name|cacheDir
operator|+
literal|"/editsStoredParsed.xml"
decl_stmt|;
name|String
name|editsStoredReparsed
init|=
name|cacheDir
operator|+
literal|"/editsStoredReparsed"
decl_stmt|;
comment|// reference XML version of editsStored (see build.xml)
name|String
name|editsStoredXml
init|=
name|cacheDir
operator|+
literal|"/editsStored.xml"
decl_stmt|;
comment|// parse to XML then back to binary
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runOev
argument_list|(
name|editsStored
argument_list|,
name|editsStoredParsedXml
argument_list|,
literal|"xml"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runOev
argument_list|(
name|editsStoredParsedXml
argument_list|,
name|editsStoredReparsed
argument_list|,
literal|"binary"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// judgement time
name|assertTrue
argument_list|(
literal|"Edits "
operator|+
name|editsStored
operator|+
literal|" should have all op codes"
argument_list|,
name|hasAllOpCodes
argument_list|(
name|editsStored
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reference XML edits and parsed to XML should be same"
argument_list|,
name|filesEqual
argument_list|(
name|editsStoredXml
argument_list|,
name|editsStoredParsedXml
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Reference edits and reparsed (bin to XML to bin) should be same"
argument_list|,
name|filesEqualIgnoreTrailingZeros
argument_list|(
name|editsStored
argument_list|,
name|editsStoredReparsed
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"END"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run OfflineEditsViewer    *    * @param inFilename input edits filename    * @param outFilename oputput edits filename    */
DECL|method|runOev (String inFilename, String outFilename, String processor, boolean recovery)
specifier|private
name|int
name|runOev
parameter_list|(
name|String
name|inFilename
parameter_list|,
name|String
name|outFilename
parameter_list|,
name|String
name|processor
parameter_list|,
name|boolean
name|recovery
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running oev ["
operator|+
name|inFilename
operator|+
literal|"] ["
operator|+
name|outFilename
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|OfflineEditsViewer
name|oev
init|=
operator|new
name|OfflineEditsViewer
argument_list|()
decl_stmt|;
name|Flags
name|flags
init|=
operator|new
name|Flags
argument_list|()
decl_stmt|;
name|flags
operator|.
name|setPrintToScreen
argument_list|()
expr_stmt|;
if|if
condition|(
name|recovery
condition|)
block|{
name|flags
operator|.
name|setRecoveryMode
argument_list|()
expr_stmt|;
block|}
return|return
name|oev
operator|.
name|go
argument_list|(
name|inFilename
argument_list|,
name|outFilename
argument_list|,
name|processor
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Checks that the edits file has all opCodes    *    * @param filename edits file    * @return true is edits (filename) has all opCodes    */
DECL|method|hasAllOpCodes (String inFilename)
specifier|private
name|boolean
name|hasAllOpCodes
parameter_list|(
name|String
name|inFilename
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|outFilename
init|=
name|inFilename
operator|+
literal|".stats"
decl_stmt|;
name|FileOutputStream
name|fout
init|=
operator|new
name|FileOutputStream
argument_list|(
name|outFilename
argument_list|)
decl_stmt|;
name|StatisticsEditsVisitor
name|visitor
init|=
operator|new
name|StatisticsEditsVisitor
argument_list|(
name|fout
argument_list|)
decl_stmt|;
name|OfflineEditsViewer
name|oev
init|=
operator|new
name|OfflineEditsViewer
argument_list|()
decl_stmt|;
if|if
condition|(
name|oev
operator|.
name|go
argument_list|(
name|inFilename
argument_list|,
name|outFilename
argument_list|,
literal|"stats"
argument_list|,
operator|new
name|Flags
argument_list|()
argument_list|,
name|visitor
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Statistics for "
operator|+
name|inFilename
operator|+
literal|"\n"
operator|+
name|visitor
operator|.
name|getStatisticsString
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|hasAllOpCodes
init|=
literal|true
decl_stmt|;
for|for
control|(
name|FSEditLogOpCodes
name|opCode
range|:
name|FSEditLogOpCodes
operator|.
name|values
argument_list|()
control|)
block|{
comment|// don't need to test obsolete opCodes
if|if
condition|(
name|obsoleteOpCodes
operator|.
name|containsKey
argument_list|(
name|opCode
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|opCode
operator|==
name|FSEditLogOpCodes
operator|.
name|OP_INVALID
condition|)
continue|continue;
name|Long
name|count
init|=
name|visitor
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
name|opCode
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|count
operator|==
literal|null
operator|)
operator|||
operator|(
name|count
operator|==
literal|0
operator|)
condition|)
block|{
name|hasAllOpCodes
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Opcode "
operator|+
name|opCode
operator|+
literal|" not tested in "
operator|+
name|inFilename
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|hasAllOpCodes
return|;
block|}
comment|/**    * Compare two files, ignore trailing zeros at the end,    * for edits log the trailing zeros do not make any difference,    * throw exception is the files are not same    *    * @param filenameSmall first file to compare (doesn't have to be smaller)    * @param filenameLarge second file to compare (doesn't have to be larger)    */
DECL|method|filesEqualIgnoreTrailingZeros (String filenameSmall, String filenameLarge)
specifier|private
name|boolean
name|filesEqualIgnoreTrailingZeros
parameter_list|(
name|String
name|filenameSmall
parameter_list|,
name|String
name|filenameLarge
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|small
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|DFSTestUtil
operator|.
name|loadFile
argument_list|(
name|filenameSmall
argument_list|)
argument_list|)
decl_stmt|;
name|ByteBuffer
name|large
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|DFSTestUtil
operator|.
name|loadFile
argument_list|(
name|filenameLarge
argument_list|)
argument_list|)
decl_stmt|;
comment|// now correct if it's otherwise
if|if
condition|(
name|small
operator|.
name|capacity
argument_list|()
operator|>
name|large
operator|.
name|capacity
argument_list|()
condition|)
block|{
name|ByteBuffer
name|tmpByteBuffer
init|=
name|small
decl_stmt|;
name|small
operator|=
name|large
expr_stmt|;
name|large
operator|=
name|tmpByteBuffer
expr_stmt|;
name|String
name|tmpFilename
init|=
name|filenameSmall
decl_stmt|;
name|filenameSmall
operator|=
name|filenameLarge
expr_stmt|;
name|filenameLarge
operator|=
name|tmpFilename
expr_stmt|;
block|}
comment|// compare from 0 to capacity of small
comment|// the rest of the large should be all zeros
name|small
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|small
operator|.
name|limit
argument_list|(
name|small
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|large
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|large
operator|.
name|limit
argument_list|(
name|small
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// compares position to limit
if|if
condition|(
operator|!
name|small
operator|.
name|equals
argument_list|(
name|large
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// everything after limit should be 0xFF
name|int
name|i
init|=
name|large
operator|.
name|limit
argument_list|()
decl_stmt|;
name|large
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|large
operator|.
name|capacity
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|large
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|FSEditLogOpCodes
operator|.
name|OP_INVALID
operator|.
name|getOpCode
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Compare two files, throw exception is they are not same    *    * @param filename1 first file to compare    * @param filename2 second file to compare    */
DECL|method|filesEqual (String filename1, String filename2)
specifier|private
name|boolean
name|filesEqual
parameter_list|(
name|String
name|filename1
parameter_list|,
name|String
name|filename2
parameter_list|)
throws|throws
name|IOException
block|{
comment|// make file 1 the small one
name|ByteBuffer
name|bb1
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|DFSTestUtil
operator|.
name|loadFile
argument_list|(
name|filename1
argument_list|)
argument_list|)
decl_stmt|;
name|ByteBuffer
name|bb2
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|DFSTestUtil
operator|.
name|loadFile
argument_list|(
name|filename2
argument_list|)
argument_list|)
decl_stmt|;
comment|// compare from 0 to capacity
name|bb1
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bb1
operator|.
name|limit
argument_list|(
name|bb1
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|bb2
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bb2
operator|.
name|limit
argument_list|(
name|bb2
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|bb1
operator|.
name|equals
argument_list|(
name|bb2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

