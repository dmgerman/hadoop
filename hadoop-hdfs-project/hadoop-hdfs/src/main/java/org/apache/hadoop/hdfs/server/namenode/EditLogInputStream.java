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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_comment
comment|/**  * A generic abstract class to support reading edits log data from   * persistent storage.  *   * It should stream bytes from the storage exactly as they were written  * into the #{@link EditLogOutputStream}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|EditLogInputStream
specifier|public
specifier|abstract
class|class
name|EditLogInputStream
implements|implements
name|JournalStream
implements|,
name|Closeable
block|{
comment|/**     * @return the first transaction which will be found in this stream    */
DECL|method|getFirstTxId ()
specifier|public
specifier|abstract
name|long
name|getFirstTxId
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * @return the last transaction which will be found in this stream    */
DECL|method|getLastTxId ()
specifier|public
specifier|abstract
name|long
name|getLastTxId
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the stream.    * @throws IOException if an error occurred while closing    */
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Read an operation from the stream    * @return an operation from the stream or null if at end of stream    * @throws IOException if there is an error reading from the stream    */
DECL|method|readOp ()
specifier|public
specifier|abstract
name|FSEditLogOp
name|readOp
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Get the layout version of the data in the stream.    * @return the layout version of the ops in the stream.    * @throws IOException if there is an error reading the version    */
DECL|method|getVersion ()
specifier|public
specifier|abstract
name|int
name|getVersion
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the "position" of in the stream. This is useful for     * debugging and operational purposes.    *    * Different stream types can have a different meaning for     * what the position is. For file streams it means the byte offset    * from the start of the file.    *    * @return the position in the stream    */
DECL|method|getPosition ()
specifier|public
specifier|abstract
name|long
name|getPosition
parameter_list|()
function_decl|;
comment|/**    * Return the size of the current edits log.    */
DECL|method|length ()
specifier|public
specifier|abstract
name|long
name|length
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

