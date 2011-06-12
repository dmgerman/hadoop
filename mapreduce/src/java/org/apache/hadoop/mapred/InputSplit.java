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
comment|/**  *<code>InputSplit</code> represents the data to be processed by an   * individual {@link Mapper}.   *  *<p>Typically, it presents a byte-oriented view on the input and is the   * responsibility of {@link RecordReader} of the job to process this and present  * a record-oriented view.  *   * @see InputFormat  * @see RecordReader  * @deprecated Use {@link org.apache.hadoop.mapreduce.InputSplit} instead.  */
end_comment

begin_interface
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|InputSplit
specifier|public
interface|interface
name|InputSplit
extends|extends
name|Writable
block|{
comment|/**    * Get the total number of bytes in the data of the<code>InputSplit</code>.    *     * @return the number of bytes in the input split.    * @throws IOException    */
DECL|method|getLength ()
name|long
name|getLength
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the list of hostnames where the input split is located.    *     * @return list of hostnames where data of the<code>InputSplit</code> is    *         located as an array of<code>String</code>s.    * @throws IOException    */
DECL|method|getLocations ()
name|String
index|[]
name|getLocations
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

