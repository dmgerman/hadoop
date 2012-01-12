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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|server
operator|.
name|protocol
operator|.
name|RemoteEditLog
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
name|RemoteEditLogManifest
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
name|collect
operator|.
name|ImmutableListMultimap
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
name|Lists
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
name|Multimaps
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
name|Sets
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

begin_comment
comment|/**  * Manages a collection of Journals. None of the methods are synchronized, it is  * assumed that FSEditLog methods, that use this class, use proper  * synchronization.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JournalSet
specifier|public
class|class
name|JournalSet
implements|implements
name|JournalManager
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Container for a JournalManager paired with its currently    * active stream.    *     * If a Journal gets disabled due to an error writing to its    * stream, then the stream will be aborted and set to null.    */
DECL|class|JournalAndStream
specifier|static
class|class
name|JournalAndStream
implements|implements
name|CheckableNameNodeResource
block|{
DECL|field|journal
specifier|private
specifier|final
name|JournalManager
name|journal
decl_stmt|;
DECL|field|disabled
specifier|private
name|boolean
name|disabled
init|=
literal|false
decl_stmt|;
DECL|field|stream
specifier|private
name|EditLogOutputStream
name|stream
decl_stmt|;
DECL|field|required
specifier|private
name|boolean
name|required
init|=
literal|false
decl_stmt|;
DECL|method|JournalAndStream (JournalManager manager, boolean required)
specifier|public
name|JournalAndStream
parameter_list|(
name|JournalManager
name|manager
parameter_list|,
name|boolean
name|required
parameter_list|)
block|{
name|this
operator|.
name|journal
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
block|}
DECL|method|startLogSegment (long txId)
specifier|public
name|void
name|startLogSegment
parameter_list|(
name|long
name|txId
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|stream
operator|==
literal|null
argument_list|)
expr_stmt|;
name|disabled
operator|=
literal|false
expr_stmt|;
name|stream
operator|=
name|journal
operator|.
name|startLogSegment
argument_list|(
name|txId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Closes the stream, also sets it to null.      */
DECL|method|closeStream ()
specifier|public
name|void
name|closeStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
return|return;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Close the Journal and Stream      */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closeStream
argument_list|()
expr_stmt|;
name|journal
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Aborts the stream, also sets it to null.      */
DECL|method|abort ()
specifier|public
name|void
name|abort
parameter_list|()
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|stream
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to abort stream "
operator|+
name|stream
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|stream
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|isActive ()
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|stream
operator|!=
literal|null
return|;
block|}
comment|/**      * Should be used outside JournalSet only for testing.      */
DECL|method|getCurrentStream ()
name|EditLogOutputStream
name|getCurrentStream
parameter_list|()
block|{
return|return
name|stream
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
literal|"JournalAndStream(mgr="
operator|+
name|journal
operator|+
literal|", "
operator|+
literal|"stream="
operator|+
name|stream
operator|+
literal|")"
return|;
block|}
DECL|method|setCurrentStreamForTests (EditLogOutputStream stream)
name|void
name|setCurrentStreamForTests
parameter_list|(
name|EditLogOutputStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
DECL|method|getManager ()
name|JournalManager
name|getManager
parameter_list|()
block|{
return|return
name|journal
return|;
block|}
DECL|method|isDisabled ()
specifier|private
name|boolean
name|isDisabled
parameter_list|()
block|{
return|return
name|disabled
return|;
block|}
DECL|method|setDisabled (boolean disabled)
specifier|private
name|void
name|setDisabled
parameter_list|(
name|boolean
name|disabled
parameter_list|)
block|{
name|this
operator|.
name|disabled
operator|=
name|disabled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isResourceAvailable ()
specifier|public
name|boolean
name|isResourceAvailable
parameter_list|()
block|{
return|return
operator|!
name|isDisabled
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isRequired ()
specifier|public
name|boolean
name|isRequired
parameter_list|()
block|{
return|return
name|required
return|;
block|}
block|}
DECL|field|journals
specifier|private
name|List
argument_list|<
name|JournalAndStream
argument_list|>
name|journals
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|minimumRedundantJournals
specifier|final
name|int
name|minimumRedundantJournals
decl_stmt|;
DECL|method|JournalSet (int minimumRedundantResources)
name|JournalSet
parameter_list|(
name|int
name|minimumRedundantResources
parameter_list|)
block|{
name|this
operator|.
name|minimumRedundantJournals
operator|=
name|minimumRedundantResources
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startLogSegment (final long txId)
specifier|public
name|EditLogOutputStream
name|startLogSegment
parameter_list|(
specifier|final
name|long
name|txId
parameter_list|)
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
name|jas
operator|.
name|startLogSegment
argument_list|(
name|txId
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|"starting log segment "
operator|+
name|txId
argument_list|)
expr_stmt|;
return|return
operator|new
name|JournalSetOutputStream
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|finalizeLogSegment (final long firstTxId, final long lastTxId)
specifier|public
name|void
name|finalizeLogSegment
parameter_list|(
specifier|final
name|long
name|firstTxId
parameter_list|,
specifier|final
name|long
name|lastTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|jas
operator|.
name|closeStream
argument_list|()
expr_stmt|;
name|jas
operator|.
name|getManager
argument_list|()
operator|.
name|finalizeLogSegment
argument_list|(
name|firstTxId
argument_list|,
name|lastTxId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"finalize log segment "
operator|+
name|firstTxId
operator|+
literal|", "
operator|+
name|lastTxId
argument_list|)
expr_stmt|;
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
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
name|jas
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|"close journal"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Find the best editlog input stream to read from txid.    * If a journal throws an CorruptionException while reading from a txn id,    * it means that it has more transactions, but can't find any from fromTxId.     * If this is the case and no other journal has transactions, we should throw    * an exception as it means more transactions exist, we just can't load them.    *    * @param fromTxnId Transaction id to start from.    * @return A edit log input stream with tranactions fromTxId     *         or null if no more exist    */
annotation|@
name|Override
DECL|method|getInputStream (long fromTxnId, boolean inProgressOk)
specifier|public
name|EditLogInputStream
name|getInputStream
parameter_list|(
name|long
name|fromTxnId
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|)
throws|throws
name|IOException
block|{
name|JournalManager
name|bestjm
init|=
literal|null
decl_stmt|;
name|long
name|bestjmNumTxns
init|=
literal|0
decl_stmt|;
name|CorruptionException
name|corruption
init|=
literal|null
decl_stmt|;
for|for
control|(
name|JournalAndStream
name|jas
range|:
name|journals
control|)
block|{
if|if
condition|(
name|jas
operator|.
name|isDisabled
argument_list|()
condition|)
continue|continue;
name|JournalManager
name|candidate
init|=
name|jas
operator|.
name|getManager
argument_list|()
decl_stmt|;
name|long
name|candidateNumTxns
init|=
literal|0
decl_stmt|;
try|try
block|{
name|candidateNumTxns
operator|=
name|candidate
operator|.
name|getNumberOfTransactions
argument_list|(
name|fromTxnId
argument_list|,
name|inProgressOk
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CorruptionException
name|ce
parameter_list|)
block|{
name|corruption
operator|=
name|ce
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to read input streams from JournalManager "
operator|+
name|candidate
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
continue|continue;
comment|// error reading disk, just skip
block|}
if|if
condition|(
name|candidateNumTxns
operator|>
name|bestjmNumTxns
condition|)
block|{
name|bestjm
operator|=
name|candidate
expr_stmt|;
name|bestjmNumTxns
operator|=
name|candidateNumTxns
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bestjm
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|corruption
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No non-corrupt logs for txid "
operator|+
name|fromTxnId
argument_list|,
name|corruption
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|bestjm
operator|.
name|getInputStream
argument_list|(
name|fromTxnId
argument_list|,
name|inProgressOk
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfTransactions (long fromTxnId, boolean inProgressOk)
specifier|public
name|long
name|getNumberOfTransactions
parameter_list|(
name|long
name|fromTxnId
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|num
init|=
literal|0
decl_stmt|;
for|for
control|(
name|JournalAndStream
name|jas
range|:
name|journals
control|)
block|{
if|if
condition|(
name|jas
operator|.
name|isDisabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping jas "
operator|+
name|jas
operator|+
literal|" since it's disabled"
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|long
name|newNum
init|=
name|jas
operator|.
name|getManager
argument_list|()
operator|.
name|getNumberOfTransactions
argument_list|(
name|fromTxnId
argument_list|,
name|inProgressOk
argument_list|)
decl_stmt|;
if|if
condition|(
name|newNum
operator|>
name|num
condition|)
block|{
name|num
operator|=
name|newNum
expr_stmt|;
block|}
block|}
block|}
return|return
name|num
return|;
block|}
comment|/**    * Returns true if there are no journals, all redundant journals are disabled,    * or any required journals are disabled.    *     * @return True if there no journals, all redundant journals are disabled,    * or any required journals are disabled.    */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
operator|!
name|NameNodeResourcePolicy
operator|.
name|areResourcesAvailable
argument_list|(
name|journals
argument_list|,
name|minimumRedundantJournals
argument_list|)
return|;
block|}
comment|/**    * Called when some journals experience an error in some operation.    */
DECL|method|disableAndReportErrorOnJournals (List<JournalAndStream> badJournals)
specifier|private
name|void
name|disableAndReportErrorOnJournals
parameter_list|(
name|List
argument_list|<
name|JournalAndStream
argument_list|>
name|badJournals
parameter_list|)
block|{
if|if
condition|(
name|badJournals
operator|==
literal|null
operator|||
name|badJournals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
comment|// nothing to do
block|}
for|for
control|(
name|JournalAndStream
name|j
range|:
name|badJournals
control|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Disabling journal "
operator|+
name|j
argument_list|)
expr_stmt|;
name|j
operator|.
name|abort
argument_list|()
expr_stmt|;
name|j
operator|.
name|setDisabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Implementations of this interface encapsulate operations that can be    * iteratively applied on all the journals. For example see    * {@link JournalSet#mapJournalsAndReportErrors}.    */
DECL|interface|JournalClosure
specifier|private
interface|interface
name|JournalClosure
block|{
comment|/**      * The operation on JournalAndStream.      * @param jas Object on which operations are performed.      * @throws IOException      */
DECL|method|apply (JournalAndStream jas)
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Apply the given operation across all of the journal managers, disabling    * any for which the closure throws an IOException.    * @param closure {@link JournalClosure} object encapsulating the operation.    * @param status message used for logging errors (e.g. "opening journal")    * @throws IOException If the operation fails on all the journals.    */
DECL|method|mapJournalsAndReportErrors ( JournalClosure closure, String status)
specifier|private
name|void
name|mapJournalsAndReportErrors
parameter_list|(
name|JournalClosure
name|closure
parameter_list|,
name|String
name|status
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|JournalAndStream
argument_list|>
name|badJAS
init|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
decl_stmt|;
for|for
control|(
name|JournalAndStream
name|jas
range|:
name|journals
control|)
block|{
try|try
block|{
name|closure
operator|.
name|apply
argument_list|(
name|jas
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error: "
operator|+
name|status
operator|+
literal|" failed for (journal "
operator|+
name|jas
operator|+
literal|")"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|badJAS
operator|.
name|add
argument_list|(
name|jas
argument_list|)
expr_stmt|;
block|}
block|}
name|disableAndReportErrorOnJournals
argument_list|(
name|badJAS
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|NameNodeResourcePolicy
operator|.
name|areResourcesAvailable
argument_list|(
name|journals
argument_list|,
name|minimumRedundantJournals
argument_list|)
condition|)
block|{
name|String
name|message
init|=
name|status
operator|+
literal|" failed for too many journals"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Error: "
operator|+
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
comment|/**    * An implementation of EditLogOutputStream that applies a requested method on    * all the journals that are currently active.    */
DECL|class|JournalSetOutputStream
specifier|private
class|class
name|JournalSetOutputStream
extends|extends
name|EditLogOutputStream
block|{
DECL|method|JournalSetOutputStream ()
name|JournalSetOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (final FSEditLogOp op)
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|FSEditLogOp
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|write
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"write op"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeRaw (final byte[] data, final int offset, final int length)
specifier|public
name|void
name|writeRaw
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|writeRaw
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"write bytes"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create ()
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"create"
argument_list|)
expr_stmt|;
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
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
name|jas
operator|.
name|closeStream
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|"close"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort ()
specifier|public
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
name|jas
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|"abort"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setReadyToFlush ()
specifier|public
name|void
name|setReadyToFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"setReadyToFlush"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flushAndSync ()
specifier|protected
name|void
name|flushAndSync
parameter_list|()
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|flushAndSync
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"flushAndSync"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|"flush"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shouldForceSync ()
specifier|public
name|boolean
name|shouldForceSync
parameter_list|()
block|{
for|for
control|(
name|JournalAndStream
name|js
range|:
name|journals
control|)
block|{
if|if
condition|(
name|js
operator|.
name|isActive
argument_list|()
operator|&&
name|js
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|shouldForceSync
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getNumSync ()
specifier|protected
name|long
name|getNumSync
parameter_list|()
block|{
for|for
control|(
name|JournalAndStream
name|jas
range|:
name|journals
control|)
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
return|return
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|getNumSync
argument_list|()
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setOutputBufferCapacity (final int size)
specifier|public
name|void
name|setOutputBufferCapacity
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
try|try
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
name|jas
operator|.
name|getManager
argument_list|()
operator|.
name|setOutputBufferCapacity
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|"setOutputBufferCapacity"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in setting outputbuffer capacity"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAllJournalStreams ()
name|List
argument_list|<
name|JournalAndStream
argument_list|>
name|getAllJournalStreams
parameter_list|()
block|{
return|return
name|journals
return|;
block|}
DECL|method|getJournalManagers ()
name|List
argument_list|<
name|JournalManager
argument_list|>
name|getJournalManagers
parameter_list|()
block|{
name|List
argument_list|<
name|JournalManager
argument_list|>
name|jList
init|=
operator|new
name|ArrayList
argument_list|<
name|JournalManager
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|JournalAndStream
name|j
range|:
name|journals
control|)
block|{
name|jList
operator|.
name|add
argument_list|(
name|j
operator|.
name|getManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|jList
return|;
block|}
DECL|method|add (JournalManager j, boolean required)
name|void
name|add
parameter_list|(
name|JournalManager
name|j
parameter_list|,
name|boolean
name|required
parameter_list|)
block|{
name|JournalAndStream
name|jas
init|=
operator|new
name|JournalAndStream
argument_list|(
name|j
argument_list|,
name|required
argument_list|)
decl_stmt|;
name|journals
operator|.
name|add
argument_list|(
name|jas
argument_list|)
expr_stmt|;
block|}
DECL|method|remove (JournalManager j)
name|void
name|remove
parameter_list|(
name|JournalManager
name|j
parameter_list|)
block|{
name|JournalAndStream
name|jasToRemove
init|=
literal|null
decl_stmt|;
for|for
control|(
name|JournalAndStream
name|jas
range|:
name|journals
control|)
block|{
if|if
condition|(
name|jas
operator|.
name|getManager
argument_list|()
operator|.
name|equals
argument_list|(
name|j
argument_list|)
condition|)
block|{
name|jasToRemove
operator|=
name|jas
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|jasToRemove
operator|!=
literal|null
condition|)
block|{
name|jasToRemove
operator|.
name|abort
argument_list|()
expr_stmt|;
name|journals
operator|.
name|remove
argument_list|(
name|jasToRemove
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|purgeLogsOlderThan (final long minTxIdToKeep)
specifier|public
name|void
name|purgeLogsOlderThan
parameter_list|(
specifier|final
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
name|jas
operator|.
name|getManager
argument_list|()
operator|.
name|purgeLogsOlderThan
argument_list|(
name|minTxIdToKeep
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|"purgeLogsOlderThan "
operator|+
name|minTxIdToKeep
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recoverUnfinalizedSegments ()
specifier|public
name|void
name|recoverUnfinalizedSegments
parameter_list|()
throws|throws
name|IOException
block|{
name|mapJournalsAndReportErrors
argument_list|(
operator|new
name|JournalClosure
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|(
name|JournalAndStream
name|jas
parameter_list|)
throws|throws
name|IOException
block|{
name|jas
operator|.
name|getManager
argument_list|()
operator|.
name|recoverUnfinalizedSegments
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
literal|"recoverUnfinalizedSegments"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a manifest of what finalized edit logs are available. All available    * edit logs are returned starting from the transaction id passed.    *     * @param fromTxId Starting transaction id to read the logs.    * @return RemoteEditLogManifest object.    */
DECL|method|getEditLogManifest (long fromTxId)
specifier|public
specifier|synchronized
name|RemoteEditLogManifest
name|getEditLogManifest
parameter_list|(
name|long
name|fromTxId
parameter_list|)
block|{
comment|// Collect RemoteEditLogs available from each FileJournalManager
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|allLogs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|JournalAndStream
name|j
range|:
name|journals
control|)
block|{
if|if
condition|(
name|j
operator|.
name|getManager
argument_list|()
operator|instanceof
name|FileJournalManager
condition|)
block|{
name|FileJournalManager
name|fjm
init|=
operator|(
name|FileJournalManager
operator|)
name|j
operator|.
name|getManager
argument_list|()
decl_stmt|;
try|try
block|{
name|allLogs
operator|.
name|addAll
argument_list|(
name|fjm
operator|.
name|getRemoteEditLogs
argument_list|(
name|fromTxId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot list edit logs in "
operator|+
name|fjm
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Group logs by their starting txid
name|ImmutableListMultimap
argument_list|<
name|Long
argument_list|,
name|RemoteEditLog
argument_list|>
name|logsByStartTxId
init|=
name|Multimaps
operator|.
name|index
argument_list|(
name|allLogs
argument_list|,
name|RemoteEditLog
operator|.
name|GET_START_TXID
argument_list|)
decl_stmt|;
name|long
name|curStartTxId
init|=
name|fromTxId
decl_stmt|;
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|logs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|ImmutableList
argument_list|<
name|RemoteEditLog
argument_list|>
name|logGroup
init|=
name|logsByStartTxId
operator|.
name|get
argument_list|(
name|curStartTxId
argument_list|)
decl_stmt|;
if|if
condition|(
name|logGroup
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we have a gap in logs - for example because we recovered some old
comment|// storage directory with ancient logs. Clear out any logs we've
comment|// accumulated so far, and then skip to the next segment of logs
comment|// after the gap.
name|SortedSet
argument_list|<
name|Long
argument_list|>
name|startTxIds
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|(
name|logsByStartTxId
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|startTxIds
operator|=
name|startTxIds
operator|.
name|tailSet
argument_list|(
name|curStartTxId
argument_list|)
expr_stmt|;
if|if
condition|(
name|startTxIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
else|else
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
literal|"Found gap in logs at "
operator|+
name|curStartTxId
operator|+
literal|": "
operator|+
literal|"not returning previous logs in manifest."
argument_list|)
expr_stmt|;
block|}
name|logs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|curStartTxId
operator|=
name|startTxIds
operator|.
name|first
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
comment|// Find the one that extends the farthest forward
name|RemoteEditLog
name|bestLog
init|=
name|Collections
operator|.
name|max
argument_list|(
name|logGroup
argument_list|)
decl_stmt|;
name|logs
operator|.
name|add
argument_list|(
name|bestLog
argument_list|)
expr_stmt|;
comment|// And then start looking from after that point
name|curStartTxId
operator|=
name|bestLog
operator|.
name|getEndTxId
argument_list|()
operator|+
literal|1
expr_stmt|;
block|}
name|RemoteEditLogManifest
name|ret
init|=
operator|new
name|RemoteEditLogManifest
argument_list|(
name|logs
argument_list|)
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
literal|"Generated manifest for logs since "
operator|+
name|fromTxId
operator|+
literal|":"
operator|+
name|ret
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Add sync times to the buffer.    */
DECL|method|getSyncTimes ()
name|String
name|getSyncTimes
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|JournalAndStream
name|jas
range|:
name|journals
control|)
block|{
if|if
condition|(
name|jas
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|jas
operator|.
name|getCurrentStream
argument_list|()
operator|.
name|getTotalSyncTime
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

