begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|io
operator|.
name|OutputBuffer
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
name|SequenceFile
operator|.
name|Sorter
operator|.
name|RawKeyValueIterator
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
name|util
operator|.
name|Progressable
import|;
end_import

begin_comment
comment|/** This class provides a generic sort interface that should be implemented  * by specific sort algorithms. The use case is the following:  * A user class writes key/value records to a buffer, and finally wants to  * sort the buffer. This interface defines methods by which the user class  * can update the interface implementation with the offsets of the records  * and the lengths of the keys/values. The user class gives a reference to  * the buffer when the latter wishes to sort the records written to the buffer  * so far. Typically, the user class decides the point at which sort should  * happen based on the memory consumed so far by the buffer and the data  * structures maintained by an implementation of this interface. That is why  * a method is provided to get the memory consumed so far by the datastructures  * in the interface implementation.    */
end_comment

begin_interface
DECL|interface|BufferSorter
interface|interface
name|BufferSorter
extends|extends
name|JobConfigurable
block|{
comment|/** Pass the Progressable object so that sort can call progress while it is sorting    * @param reporter the Progressable object reference    */
DECL|method|setProgressable (Progressable reporter)
specifier|public
name|void
name|setProgressable
parameter_list|(
name|Progressable
name|reporter
parameter_list|)
function_decl|;
comment|/** When a key/value is added at a particular offset in the key/value buffer,     * this method is invoked by the user class so that the impl of this sort     * interface can update its datastructures.     * @param recordOffset the offset of the key in the buffer    * @param keyLength the length of the key    * @param valLength the length of the val in the buffer    */
DECL|method|addKeyValue (int recordoffset, int keyLength, int valLength)
specifier|public
name|void
name|addKeyValue
parameter_list|(
name|int
name|recordoffset
parameter_list|,
name|int
name|keyLength
parameter_list|,
name|int
name|valLength
parameter_list|)
function_decl|;
comment|/** The user class invokes this method to set the buffer that the specific     * sort algorithm should "indirectly" sort (generally, sort algorithm impl     * should access this buffer via comparators and sort offset-indices to the    * buffer).    * @param buffer the map output buffer    */
DECL|method|setInputBuffer (OutputBuffer buffer)
specifier|public
name|void
name|setInputBuffer
parameter_list|(
name|OutputBuffer
name|buffer
parameter_list|)
function_decl|;
comment|/** The framework invokes this method to get the memory consumed so far    * by an implementation of this interface.    * @return memoryUsed in bytes     */
DECL|method|getMemoryUtilized ()
specifier|public
name|long
name|getMemoryUtilized
parameter_list|()
function_decl|;
comment|/** Framework decides when to actually sort    */
DECL|method|sort ()
specifier|public
name|RawKeyValueIterator
name|sort
parameter_list|()
function_decl|;
comment|/** Framework invokes this to signal the sorter to cleanup    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

