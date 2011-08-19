begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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

begin_comment
comment|/**  * The record reader breaks the data into key/value pairs for input to the  * {@link Mapper}.  * @param<KEYIN>  * @param<VALUEIN>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|RecordReader
specifier|public
specifier|abstract
class|class
name|RecordReader
parameter_list|<
name|KEYIN
parameter_list|,
name|VALUEIN
parameter_list|>
implements|implements
name|Closeable
block|{
comment|/**    * Called once at initialization.    * @param split the split that defines the range of records to read    * @param context the information about the task    * @throws IOException    * @throws InterruptedException    */
DECL|method|initialize (InputSplit split, TaskAttemptContext context )
specifier|public
specifier|abstract
name|void
name|initialize
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Read the next key, value pair.    * @return true if a key/value pair was read    * @throws IOException    * @throws InterruptedException    */
specifier|public
specifier|abstract
DECL|method|nextKeyValue ()
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the current key    * @return the current key or null if there is no current key    * @throws IOException    * @throws InterruptedException    */
specifier|public
specifier|abstract
DECL|method|getCurrentKey ()
name|KEYIN
name|getCurrentKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the current value.    * @return the object that was read    * @throws IOException    * @throws InterruptedException    */
specifier|public
specifier|abstract
DECL|method|getCurrentValue ()
name|VALUEIN
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * The current progress of the record reader through its data.    * @return a number between 0.0 and 1.0 that is the fraction of the data read    * @throws IOException    * @throws InterruptedException    */
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Close the record reader.    */
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

