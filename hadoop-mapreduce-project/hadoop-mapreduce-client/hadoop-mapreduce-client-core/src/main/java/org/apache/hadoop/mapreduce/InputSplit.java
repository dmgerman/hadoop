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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|mapred
operator|.
name|SplitLocationInfo
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
name|mapreduce
operator|.
name|InputFormat
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
name|mapreduce
operator|.
name|Mapper
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
name|mapreduce
operator|.
name|RecordReader
import|;
end_import

begin_comment
comment|/**  *<code>InputSplit</code> represents the data to be processed by an   * individual {@link Mapper}.   *  *<p>Typically, it presents a byte-oriented view on the input and is the   * responsibility of {@link RecordReader} of the job to process this and present  * a record-oriented view.  *   * @see InputFormat  * @see RecordReader  */
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
DECL|class|InputSplit
specifier|public
specifier|abstract
class|class
name|InputSplit
block|{
comment|/**    * Get the size of the split, so that the input splits can be sorted by size.    * @return the number of bytes in the split    * @throws IOException    * @throws InterruptedException    */
DECL|method|getLength ()
specifier|public
specifier|abstract
name|long
name|getLength
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the list of nodes by name where the data for the split would be local.    * The locations do not need to be serialized.    *     * @return a new array of the node nodes.    * @throws IOException    * @throws InterruptedException    */
specifier|public
specifier|abstract
DECL|method|getLocations ()
name|String
index|[]
name|getLocations
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Gets info about which nodes the input split is stored on and how it is    * stored at each location.    *     * @return list of<code>SplitLocationInfo</code>s describing how the split    *    data is stored at each location. A null value indicates that all the    *    locations have the data stored on disk.    * @throws IOException    */
annotation|@
name|Evolving
DECL|method|getLocationInfo ()
specifier|public
name|SplitLocationInfo
index|[]
name|getLocationInfo
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

