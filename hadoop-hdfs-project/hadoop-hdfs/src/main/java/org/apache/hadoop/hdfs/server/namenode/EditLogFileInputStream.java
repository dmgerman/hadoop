begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|classification
operator|.
name|InterfaceAudience
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
name|protocol
operator|.
name|LayoutFlags
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
name|LayoutVersion
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
name|HdfsServerConstants
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
name|HttpGetFailedException
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
name|web
operator|.
name|URLConnectionFactory
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Preconditions
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
name|Throwables
import|;
end_import

begin_comment
comment|/**  * An implementation of the abstract class {@link EditLogInputStream}, which  * reads edits from a file. That file may be either on the local disk or  * accessible via a URL.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|EditLogFileInputStream
specifier|public
class|class
name|EditLogFileInputStream
extends|extends
name|EditLogInputStream
block|{
DECL|field|log
specifier|private
specifier|final
name|LogSource
name|log
decl_stmt|;
DECL|field|firstTxId
specifier|private
specifier|final
name|long
name|firstTxId
decl_stmt|;
DECL|field|lastTxId
specifier|private
specifier|final
name|long
name|lastTxId
decl_stmt|;
DECL|field|isInProgress
specifier|private
specifier|final
name|boolean
name|isInProgress
decl_stmt|;
DECL|field|maxOpSize
specifier|private
name|int
name|maxOpSize
decl_stmt|;
DECL|enum|State
specifier|static
specifier|private
enum|enum
name|State
block|{
DECL|enumConstant|UNINIT
name|UNINIT
block|,
DECL|enumConstant|OPEN
name|OPEN
block|,
DECL|enumConstant|CLOSED
name|CLOSED
block|}
DECL|field|state
specifier|private
name|State
name|state
init|=
name|State
operator|.
name|UNINIT
decl_stmt|;
DECL|field|fStream
specifier|private
name|InputStream
name|fStream
init|=
literal|null
decl_stmt|;
DECL|field|logVersion
specifier|private
name|int
name|logVersion
init|=
literal|0
decl_stmt|;
DECL|field|reader
specifier|private
name|FSEditLogOp
operator|.
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|tracker
specifier|private
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
name|tracker
init|=
literal|null
decl_stmt|;
DECL|field|dataIn
specifier|private
name|DataInputStream
name|dataIn
init|=
literal|null
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EditLogInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Open an EditLogInputStream for the given file.    * The file is pretransactional, so has no txids    * @param name filename to open    * @throws LogHeaderCorruptException if the header is either missing or    *         appears to be corrupt/truncated    * @throws IOException if an actual IO error occurs while reading the    *         header    */
DECL|method|EditLogFileInputStream (File name)
name|EditLogFileInputStream
parameter_list|(
name|File
name|name
parameter_list|)
throws|throws
name|LogHeaderCorruptException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|name
argument_list|,
name|HdfsServerConstants
operator|.
name|INVALID_TXID
argument_list|,
name|HdfsServerConstants
operator|.
name|INVALID_TXID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Open an EditLogInputStream for the given file.    * @param name filename to open    * @param firstTxId first transaction found in file    * @param lastTxId last transaction id found in file    */
DECL|method|EditLogFileInputStream (File name, long firstTxId, long lastTxId, boolean isInProgress)
specifier|public
name|EditLogFileInputStream
parameter_list|(
name|File
name|name
parameter_list|,
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|,
name|boolean
name|isInProgress
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|FileLog
argument_list|(
name|name
argument_list|)
argument_list|,
name|firstTxId
argument_list|,
name|lastTxId
argument_list|,
name|isInProgress
argument_list|)
expr_stmt|;
block|}
comment|/**    * Open an EditLogInputStream for the given URL.    *    * @param connectionFactory    *          the URLConnectionFactory used to create the connection.    * @param url    *          the url hosting the log    * @param startTxId    *          the expected starting txid    * @param endTxId    *          the expected ending txid    * @param inProgress    *          whether the log is in-progress    * @return a stream from which edits may be read    */
DECL|method|fromUrl ( URLConnectionFactory connectionFactory, URL url, long startTxId, long endTxId, boolean inProgress)
specifier|public
specifier|static
name|EditLogInputStream
name|fromUrl
parameter_list|(
name|URLConnectionFactory
name|connectionFactory
parameter_list|,
name|URL
name|url
parameter_list|,
name|long
name|startTxId
parameter_list|,
name|long
name|endTxId
parameter_list|,
name|boolean
name|inProgress
parameter_list|)
block|{
return|return
operator|new
name|EditLogFileInputStream
argument_list|(
operator|new
name|URLLog
argument_list|(
name|connectionFactory
argument_list|,
name|url
argument_list|)
argument_list|,
name|startTxId
argument_list|,
name|endTxId
argument_list|,
name|inProgress
argument_list|)
return|;
block|}
comment|/**    * Create an EditLogInputStream from a {@link ByteString}, i.e. an in-memory    * collection of bytes.    *    * @param bytes The byte string to read from    * @param startTxId the expected starting transaction ID    * @param endTxId the expected ending transaction ID    * @param inProgress whether the log is in-progress    * @return An edit stream to read from    */
DECL|method|fromByteString (ByteString bytes, long startTxId, long endTxId, boolean inProgress)
specifier|public
specifier|static
name|EditLogInputStream
name|fromByteString
parameter_list|(
name|ByteString
name|bytes
parameter_list|,
name|long
name|startTxId
parameter_list|,
name|long
name|endTxId
parameter_list|,
name|boolean
name|inProgress
parameter_list|)
block|{
return|return
operator|new
name|EditLogFileInputStream
argument_list|(
operator|new
name|ByteStringLog
argument_list|(
name|bytes
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"ByteStringEditLog[%d, %d]"
argument_list|,
name|startTxId
argument_list|,
name|endTxId
argument_list|)
argument_list|)
argument_list|,
name|startTxId
argument_list|,
name|endTxId
argument_list|,
name|inProgress
argument_list|)
return|;
block|}
DECL|method|EditLogFileInputStream (LogSource log, long firstTxId, long lastTxId, boolean isInProgress)
specifier|private
name|EditLogFileInputStream
parameter_list|(
name|LogSource
name|log
parameter_list|,
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|,
name|boolean
name|isInProgress
parameter_list|)
block|{
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
name|this
operator|.
name|firstTxId
operator|=
name|firstTxId
expr_stmt|;
name|this
operator|.
name|lastTxId
operator|=
name|lastTxId
expr_stmt|;
name|this
operator|.
name|isInProgress
operator|=
name|isInProgress
expr_stmt|;
name|this
operator|.
name|maxOpSize
operator|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_OP_SIZE_DEFAULT
expr_stmt|;
block|}
DECL|method|init (boolean verifyLayoutVersion)
specifier|private
name|void
name|init
parameter_list|(
name|boolean
name|verifyLayoutVersion
parameter_list|)
throws|throws
name|LogHeaderCorruptException
throws|,
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|state
operator|==
name|State
operator|.
name|UNINIT
argument_list|)
expr_stmt|;
name|BufferedInputStream
name|bin
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fStream
operator|=
name|log
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|bin
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|fStream
argument_list|)
expr_stmt|;
name|tracker
operator|=
operator|new
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
argument_list|(
name|bin
argument_list|)
expr_stmt|;
name|dataIn
operator|=
operator|new
name|DataInputStream
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
try|try
block|{
name|logVersion
operator|=
name|readLogVersion
argument_list|(
name|dataIn
argument_list|,
name|verifyLayoutVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|eofe
parameter_list|)
block|{
throw|throw
operator|new
name|LogHeaderCorruptException
argument_list|(
literal|"No header found in log"
argument_list|)
throw|;
block|}
if|if
condition|(
name|logVersion
operator|==
operator|-
literal|1
condition|)
block|{
comment|// The edits in progress file is pre-allocated with 1MB of "-1" bytes
comment|// when it is created, then the header is written. If the header is
comment|// -1, it indicates the an exception occurred pre-allocating the file
comment|// and the header was never written. Therefore this is effectively a
comment|// corrupt and empty log.
throw|throw
operator|new
name|LogHeaderCorruptException
argument_list|(
literal|"No header present in log (value "
operator|+
literal|"is -1), probably due to disk space issues when it was created. "
operator|+
literal|"The log has no transactions and will be sidelined."
argument_list|)
throw|;
block|}
comment|// We assume future layout will also support ADD_LAYOUT_FLAGS
if|if
condition|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|LayoutVersion
operator|.
name|Feature
operator|.
name|ADD_LAYOUT_FLAGS
argument_list|,
name|logVersion
argument_list|)
operator|||
name|logVersion
operator|<
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
condition|)
block|{
try|try
block|{
name|LayoutFlags
operator|.
name|read
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|eofe
parameter_list|)
block|{
throw|throw
operator|new
name|LogHeaderCorruptException
argument_list|(
literal|"EOF while reading layout "
operator|+
literal|"flags from log"
argument_list|)
throw|;
block|}
block|}
name|reader
operator|=
name|FSEditLogOp
operator|.
name|Reader
operator|.
name|create
argument_list|(
name|dataIn
argument_list|,
name|tracker
argument_list|,
name|logVersion
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setMaxOpSize
argument_list|(
name|maxOpSize
argument_list|)
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|OPEN
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|dataIn
argument_list|,
name|tracker
argument_list|,
name|bin
argument_list|,
name|fStream
argument_list|)
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|CLOSED
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getFirstTxId ()
specifier|public
name|long
name|getFirstTxId
parameter_list|()
block|{
return|return
name|firstTxId
return|;
block|}
annotation|@
name|Override
DECL|method|getLastTxId ()
specifier|public
name|long
name|getLastTxId
parameter_list|()
block|{
return|return
name|lastTxId
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|log
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|nextOpImpl (boolean skipBrokenEdits)
specifier|private
name|FSEditLogOp
name|nextOpImpl
parameter_list|(
name|boolean
name|skipBrokenEdits
parameter_list|)
throws|throws
name|IOException
block|{
name|FSEditLogOp
name|op
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|UNINIT
case|:
try|try
block|{
name|init
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"caught exception initializing "
operator|+
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipBrokenEdits
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Throwables
operator|.
name|propagateIfPossible
argument_list|(
name|e
argument_list|,
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkState
argument_list|(
name|state
operator|!=
name|State
operator|.
name|UNINIT
argument_list|)
expr_stmt|;
return|return
name|nextOpImpl
argument_list|(
name|skipBrokenEdits
argument_list|)
return|;
case|case
name|OPEN
case|:
name|op
operator|=
name|reader
operator|.
name|readOp
argument_list|(
name|skipBrokenEdits
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|op
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|op
operator|.
name|hasTransactionId
argument_list|()
operator|)
condition|)
block|{
name|long
name|txId
init|=
name|op
operator|.
name|getTransactionId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|txId
operator|>=
name|lastTxId
operator|)
operator|&&
operator|(
name|lastTxId
operator|!=
name|HdfsServerConstants
operator|.
name|INVALID_TXID
operator|)
condition|)
block|{
comment|//
comment|// Sometimes, the NameNode crashes while it's writing to the
comment|// edit log.  In that case, you can end up with an unfinalized edit log
comment|// which has some garbage at the end.
comment|// JournalManager#recoverUnfinalizedSegments will finalize these
comment|// unfinished edit logs, giving them a defined final transaction
comment|// ID.  Then they will be renamed, so that any subsequent
comment|// readers will have this information.
comment|//
comment|// Since there may be garbage at the end of these "cleaned up"
comment|// logs, we want to be sure to skip it here if we've read everything
comment|// we were supposed to read out of the stream.
comment|// So we force an EOF on all subsequent reads.
comment|//
name|long
name|skipAmt
init|=
name|log
operator|.
name|length
argument_list|()
operator|-
name|tracker
operator|.
name|getPos
argument_list|()
decl_stmt|;
if|if
condition|(
name|skipAmt
operator|>
literal|0
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
literal|"skipping "
operator|+
name|skipAmt
operator|+
literal|" bytes at the end "
operator|+
literal|"of edit log  '"
operator|+
name|getName
argument_list|()
operator|+
literal|"': reached txid "
operator|+
name|txId
operator|+
literal|" out of "
operator|+
name|lastTxId
argument_list|)
expr_stmt|;
block|}
name|tracker
operator|.
name|clearLimit
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|tracker
argument_list|,
name|skipAmt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
break|break;
case|case
name|CLOSED
case|:
break|break;
comment|// return null
block|}
return|return
name|op
return|;
block|}
annotation|@
name|Override
DECL|method|scanNextOp ()
specifier|protected
name|long
name|scanNextOp
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|state
operator|==
name|State
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|FSEditLogOp
name|cachedNext
init|=
name|getCachedOp
argument_list|()
decl_stmt|;
return|return
name|cachedNext
operator|==
literal|null
condition|?
name|reader
operator|.
name|scanOp
argument_list|()
else|:
name|cachedNext
operator|.
name|txid
return|;
block|}
annotation|@
name|Override
DECL|method|nextOp ()
specifier|protected
name|FSEditLogOp
name|nextOp
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|nextOpImpl
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextValidOp ()
specifier|protected
name|FSEditLogOp
name|nextValidOp
parameter_list|()
block|{
try|try
block|{
return|return
name|nextOpImpl
argument_list|(
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"nextValidOp: got exception while reading "
operator|+
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getVersion (boolean verifyVersion)
specifier|public
name|int
name|getVersion
parameter_list|(
name|boolean
name|verifyVersion
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|==
name|State
operator|.
name|UNINIT
condition|)
block|{
name|init
argument_list|(
name|verifyVersion
argument_list|)
expr_stmt|;
block|}
return|return
name|logVersion
return|;
block|}
annotation|@
name|Override
DECL|method|getPosition ()
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
name|State
operator|.
name|OPEN
condition|)
block|{
return|return
name|tracker
operator|.
name|getPos
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|==
name|State
operator|.
name|OPEN
condition|)
block|{
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|state
operator|=
name|State
operator|.
name|CLOSED
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length ()
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
comment|// file size + size of both buffers
return|return
name|log
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isInProgress ()
specifier|public
name|boolean
name|isInProgress
parameter_list|()
block|{
return|return
name|isInProgress
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
comment|/**    * @param file          File being scanned and validated.    * @param maxTxIdToScan Maximum Tx ID to try to scan.    *                      The scan returns after reading this or a higher    *                      ID. The file portion beyond this ID is    *                      potentially being updated.    * @return Result of the validation    * @throws IOException    */
DECL|method|scanEditLog (File file, long maxTxIdToScan, boolean verifyVersion)
specifier|static
name|FSEditLogLoader
operator|.
name|EditLogValidation
name|scanEditLog
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|maxTxIdToScan
parameter_list|,
name|boolean
name|verifyVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|EditLogFileInputStream
name|in
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|EditLogFileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// read the header, initialize the inputstream, but do not check the
comment|// layoutversion
name|in
operator|.
name|getVersion
argument_list|(
name|verifyVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LogHeaderCorruptException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log file "
operator|+
name|file
operator|+
literal|" has no valid header"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
operator|new
name|FSEditLogLoader
operator|.
name|EditLogValidation
argument_list|(
literal|0
argument_list|,
name|HdfsServerConstants
operator|.
name|INVALID_TXID
argument_list|,
literal|true
argument_list|)
return|;
block|}
try|try
block|{
return|return
name|FSEditLogLoader
operator|.
name|scanEditLog
argument_list|(
name|in
argument_list|,
name|maxTxIdToScan
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read the header of fsedit log    * @param in fsedit stream    * @return the edit log version number    * @throws IOException if error occurs    */
annotation|@
name|VisibleForTesting
DECL|method|readLogVersion (DataInputStream in, boolean verifyLayoutVersion)
specifier|static
name|int
name|readLogVersion
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|boolean
name|verifyLayoutVersion
parameter_list|)
throws|throws
name|IOException
throws|,
name|LogHeaderCorruptException
block|{
name|int
name|logVersion
decl_stmt|;
try|try
block|{
name|logVersion
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|eofe
parameter_list|)
block|{
throw|throw
operator|new
name|LogHeaderCorruptException
argument_list|(
literal|"Reached EOF when reading log header"
argument_list|)
throw|;
block|}
if|if
condition|(
name|verifyLayoutVersion
operator|&&
operator|(
name|logVersion
argument_list|<
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|||
comment|// future version
name|logVersion
argument_list|>
name|Storage
operator|.
name|LAST_UPGRADABLE_LAYOUT_VERSION
operator|)
condition|)
block|{
comment|// unsupported
throw|throw
operator|new
name|LogHeaderCorruptException
argument_list|(
literal|"Unexpected version of the file system log file: "
operator|+
name|logVersion
operator|+
literal|". Current version = "
operator|+
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|+
literal|"."
argument_list|)
throw|;
block|}
return|return
name|logVersion
return|;
block|}
comment|/**    * Exception indicating that the header of an edits log file is    * corrupted. This can be because the header is not present,    * or because the header data is invalid (eg claims to be    * over a newer version than the running NameNode)    */
DECL|class|LogHeaderCorruptException
specifier|static
class|class
name|LogHeaderCorruptException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|LogHeaderCorruptException (String msg)
specifier|private
name|LogHeaderCorruptException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|LogSource
specifier|private
interface|interface
name|LogSource
block|{
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|length ()
specifier|public
name|long
name|length
parameter_list|()
function_decl|;
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
block|}
DECL|class|ByteStringLog
specifier|private
specifier|static
class|class
name|ByteStringLog
implements|implements
name|LogSource
block|{
DECL|field|bytes
specifier|private
specifier|final
name|ByteString
name|bytes
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|ByteStringLog (ByteString bytes, String name)
specifier|public
name|ByteStringLog
parameter_list|(
name|ByteString
name|bytes
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|newInput
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|length ()
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
DECL|class|FileLog
specifier|private
specifier|static
class|class
name|FileLog
implements|implements
name|LogSource
block|{
DECL|field|file
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
DECL|method|FileLog (File file)
specifier|public
name|FileLog
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Files
operator|.
name|newInputStream
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length ()
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|file
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
DECL|class|URLLog
specifier|private
specifier|static
class|class
name|URLLog
implements|implements
name|LogSource
block|{
DECL|field|url
specifier|private
specifier|final
name|URL
name|url
decl_stmt|;
DECL|field|advertisedSize
specifier|private
name|long
name|advertisedSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|CONTENT_LENGTH
specifier|private
specifier|final
specifier|static
name|String
name|CONTENT_LENGTH
init|=
literal|"Content-Length"
decl_stmt|;
DECL|field|connectionFactory
specifier|private
specifier|final
name|URLConnectionFactory
name|connectionFactory
decl_stmt|;
DECL|field|isSpnegoEnabled
specifier|private
specifier|final
name|boolean
name|isSpnegoEnabled
decl_stmt|;
DECL|method|URLLog (URLConnectionFactory connectionFactory, URL url)
specifier|public
name|URLLog
parameter_list|(
name|URLConnectionFactory
name|connectionFactory
parameter_list|,
name|URL
name|url
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
name|this
operator|.
name|isSpnegoEnabled
operator|=
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|SecurityUtil
operator|.
name|doAsCurrentUser
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|InputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpURLConnection
name|connection
decl_stmt|;
try|try
block|{
name|connection
operator|=
operator|(
name|HttpURLConnection
operator|)
name|connectionFactory
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|isSpnegoEnabled
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|connection
operator|.
name|getResponseCode
argument_list|()
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
throw|throw
operator|new
name|HttpGetFailedException
argument_list|(
literal|"Fetch of "
operator|+
name|url
operator|+
literal|" failed with status code "
operator|+
name|connection
operator|.
name|getResponseCode
argument_list|()
operator|+
literal|"\nResponse message:\n"
operator|+
name|connection
operator|.
name|getResponseMessage
argument_list|()
argument_list|,
name|connection
argument_list|)
throw|;
block|}
name|String
name|contentLength
init|=
name|connection
operator|.
name|getHeaderField
argument_list|(
name|CONTENT_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentLength
operator|!=
literal|null
condition|)
block|{
name|advertisedSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|contentLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|advertisedSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid "
operator|+
name|CONTENT_LENGTH
operator|+
literal|" header: "
operator|+
name|contentLength
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|CONTENT_LENGTH
operator|+
literal|" header is not provided "
operator|+
literal|"by the server when trying to fetch "
operator|+
name|url
argument_list|)
throw|;
block|}
return|return
name|connection
operator|.
name|getInputStream
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length ()
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|advertisedSize
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|url
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setMaxOpSize (int maxOpSize)
specifier|public
name|void
name|setMaxOpSize
parameter_list|(
name|int
name|maxOpSize
parameter_list|)
block|{
name|this
operator|.
name|maxOpSize
operator|=
name|maxOpSize
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|setMaxOpSize
argument_list|(
name|maxOpSize
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isLocalLog ()
specifier|public
name|boolean
name|isLocalLog
parameter_list|()
block|{
return|return
name|log
operator|instanceof
name|FileLog
return|;
block|}
block|}
end_class

end_unit

