begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
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
comment|/**  * A serializable object which implements a simple, efficient, serialization   * protocol, based on {@link DataInput} and {@link DataOutput}.  *  *<p>Any<code>key</code> or<code>value</code> type in the Hadoop Map-Reduce  * framework implements this interface.</p>  *   *<p>Implementations typically implement a static<code>read(DataInput)</code>  * method which constructs a new instance, calls {@link #readFields(DataInput)}   * and returns the instance.</p>  *   *<p>Example:</p>  *<blockquote><pre>  *     public class MyWritable implements Writable {  *       // Some data  *       private int counter;  *       private long timestamp;  *  *       // Default constructor to allow (de)serialization  *       MyWritable() { }  *  *       public void write(DataOutput out) throws IOException {  *         out.writeInt(counter);  *         out.writeLong(timestamp);  *       }  *  *       public void readFields(DataInput in) throws IOException {  *         counter = in.readInt();  *         timestamp = in.readLong();  *       }  *  *       public static MyWritable read(DataInput in) throws IOException {  *         MyWritable w = new MyWritable();  *         w.readFields(in);  *         return w;  *       }  *     }  *</pre></blockquote>  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|Writable
specifier|public
interface|interface
name|Writable
block|{
comment|/**     * Serialize the fields of this object to<code>out</code>.    *     * @param out<code>DataOuput</code> to serialize this object into.    * @throws IOException    */
DECL|method|write (DataOutput out)
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Deserialize the fields of this object from<code>in</code>.      *     *<p>For efficiency, implementations should attempt to re-use storage in the     * existing object where possible.</p>    *     * @param in<code>DataInput</code> to deseriablize this object from.    * @throws IOException    */
DECL|method|readFields (DataInput in)
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

