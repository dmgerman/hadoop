begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset
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
operator|.
name|fsdataset
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Rolling logs consist of a current log and a set of previous logs.  *  * The implementation should support a single appender and multiple readers.  */
end_comment

begin_interface
DECL|interface|RollingLogs
specifier|public
interface|interface
name|RollingLogs
block|{
comment|/**    * To iterate the lines of the logs.    */
DECL|interface|LineIterator
specifier|public
interface|interface
name|LineIterator
extends|extends
name|Iterator
argument_list|<
name|String
argument_list|>
extends|,
name|Closeable
block|{
comment|/** Is the iterator iterating the previous? */
DECL|method|isPrevious ()
specifier|public
name|boolean
name|isPrevious
parameter_list|()
function_decl|;
comment|/**      * Is the last read entry from previous? This should be called after      * reading.      */
DECL|method|isLastReadFromPrevious ()
specifier|public
name|boolean
name|isLastReadFromPrevious
parameter_list|()
function_decl|;
block|}
comment|/**    * To append text to the logs.    */
DECL|interface|Appender
specifier|public
interface|interface
name|Appender
extends|extends
name|Appendable
extends|,
name|Closeable
block|{   }
comment|/**    * Create an iterator to iterate the lines in the logs.    *     * @param skipPrevious Should it skip reading the previous log?     * @return a new iterator.    */
DECL|method|iterator (boolean skipPrevious)
specifier|public
name|LineIterator
name|iterator
parameter_list|(
name|boolean
name|skipPrevious
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the only appender to append text to the logs.    *   The same object is returned if it is invoked multiple times.    */
DECL|method|appender ()
specifier|public
name|Appender
name|appender
parameter_list|()
function_decl|;
comment|/**    * Roll current to previous.    *    * @return true if the rolling succeeded.    *   When it returns false, it is not equivalent to an error.     *   It means that the rolling cannot be performed at the moment,    *   e.g. the logs are being read.    */
DECL|method|roll ()
specifier|public
name|boolean
name|roll
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

