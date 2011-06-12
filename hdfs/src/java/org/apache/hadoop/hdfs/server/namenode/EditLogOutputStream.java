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
name|io
operator|.
name|OutputStream
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
name|server
operator|.
name|common
operator|.
name|Util
operator|.
name|now
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
name|Writable
import|;
end_import

begin_comment
comment|/**  * A generic abstract class to support journaling of edits logs into   * a persistent storage.  */
end_comment

begin_class
DECL|class|EditLogOutputStream
specifier|abstract
class|class
name|EditLogOutputStream
extends|extends
name|OutputStream
implements|implements
name|JournalStream
block|{
comment|// these are statistics counters
DECL|field|numSync
specifier|private
name|long
name|numSync
decl_stmt|;
comment|// number of sync(s) to disk
DECL|field|totalTimeSync
specifier|private
name|long
name|totalTimeSync
decl_stmt|;
comment|// total time to sync
DECL|method|EditLogOutputStream ()
name|EditLogOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|numSync
operator|=
name|totalTimeSync
operator|=
literal|0
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|write (int b)
specifier|abstract
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write edits log record into the stream.    * The record is represented by operation name and    * an array of Writable arguments.    *     * @param op operation    * @param writables array of Writable arguments    * @throws IOException    */
DECL|method|write (byte op, Writable ... writables)
specifier|abstract
name|void
name|write
parameter_list|(
name|byte
name|op
parameter_list|,
name|Writable
modifier|...
name|writables
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create and initialize underlying persistent edits log storage.    *     * @throws IOException    */
DECL|method|create ()
specifier|abstract
name|void
name|create
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** {@inheritDoc} */
DECL|method|close ()
specifier|abstract
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * All data that has been written to the stream so far will be flushed.    * New data can be still written to the stream while flushing is performed.    */
DECL|method|setReadyToFlush ()
specifier|abstract
name|void
name|setReadyToFlush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Flush and sync all data that is ready to be flush     * {@link #setReadyToFlush()} into underlying persistent store.    * @throws IOException    */
DECL|method|flushAndSync ()
specifier|abstract
specifier|protected
name|void
name|flushAndSync
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Flush data to persistent store.    * Collect sync metrics.    */
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|numSync
operator|++
expr_stmt|;
name|long
name|start
init|=
name|now
argument_list|()
decl_stmt|;
name|flushAndSync
argument_list|()
expr_stmt|;
name|long
name|end
init|=
name|now
argument_list|()
decl_stmt|;
name|totalTimeSync
operator|+=
operator|(
name|end
operator|-
name|start
operator|)
expr_stmt|;
block|}
comment|/**    * Return the size of the current edits log.    * Length is used to check when it is large enough to start a checkpoint.    */
DECL|method|length ()
specifier|abstract
name|long
name|length
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Implement the policy when to automatically sync the buffered edits log    * The buffered edits can be flushed when the buffer becomes full or    * a certain period of time is elapsed.    *     * @return true if the buffered data should be automatically synced to disk    */
DECL|method|shouldForceSync ()
specifier|public
name|boolean
name|shouldForceSync
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|isOperationSupported (byte op)
name|boolean
name|isOperationSupported
parameter_list|(
name|byte
name|op
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Return total time spent in {@link #flushAndSync()}    */
DECL|method|getTotalSyncTime ()
name|long
name|getTotalSyncTime
parameter_list|()
block|{
return|return
name|totalTimeSync
return|;
block|}
comment|/**    * Return number of calls to {@link #flushAndSync()}    */
DECL|method|getNumSync ()
name|long
name|getNumSync
parameter_list|()
block|{
return|return
name|numSync
return|;
block|}
annotation|@
name|Override
comment|// Object
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
block|}
end_class

end_unit

