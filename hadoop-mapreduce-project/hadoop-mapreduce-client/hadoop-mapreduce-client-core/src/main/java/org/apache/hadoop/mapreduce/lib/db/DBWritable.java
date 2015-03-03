begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
comment|/**  * Objects that are read from/written to a database should implement  *<code>DBWritable</code>. DBWritable, is similar to {@link Writable}   * except that the {@link #write(PreparedStatement)} method takes a   * {@link PreparedStatement}, and {@link #readFields(ResultSet)}   * takes a {@link ResultSet}.   *<p>  * Implementations are responsible for writing the fields of the object   * to PreparedStatement, and reading the fields of the object from the   * ResultSet.   *   *<p>Example:</p>  * If we have the following table in the database :  *<pre>  * CREATE TABLE MyTable (  *   counter        INTEGER NOT NULL,  *   timestamp      BIGINT  NOT NULL,  * );  *</pre>  * then we can read/write the tuples from/to the table with :  *<p><pre>  * public class MyWritable implements Writable, DBWritable {  *   // Some data       *   private int counter;  *   private long timestamp;  *         *   //Writable#write() implementation  *   public void write(DataOutput out) throws IOException {  *     out.writeInt(counter);  *     out.writeLong(timestamp);  *   }  *         *   //Writable#readFields() implementation  *   public void readFields(DataInput in) throws IOException {  *     counter = in.readInt();  *     timestamp = in.readLong();  *   }  *         *   public void write(PreparedStatement statement) throws SQLException {  *     statement.setInt(1, counter);  *     statement.setLong(2, timestamp);  *   }  *         *   public void readFields(ResultSet resultSet) throws SQLException {  *     counter = resultSet.getInt(1);  *     timestamp = resultSet.getLong(2);  *   }   * }  *</pre>  */
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
DECL|interface|DBWritable
specifier|public
interface|interface
name|DBWritable
block|{
comment|/**    * Sets the fields of the object in the {@link PreparedStatement}.    * @param statement the statement that the fields are put into.    * @throws SQLException    */
DECL|method|write (PreparedStatement statement)
specifier|public
name|void
name|write
parameter_list|(
name|PreparedStatement
name|statement
parameter_list|)
throws|throws
name|SQLException
function_decl|;
comment|/** 	 * Reads the fields of the object from the {@link ResultSet}.  	 * @param resultSet the {@link ResultSet} to get the fields from. 	 * @throws SQLException 	 */
DECL|method|readFields (ResultSet resultSet)
specifier|public
name|void
name|readFields
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
function_decl|;
block|}
end_interface

end_unit

