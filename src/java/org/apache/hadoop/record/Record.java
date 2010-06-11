begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
package|;
end_package

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
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|WritableComparable
import|;
end_import

begin_comment
comment|/**  * Abstract class that is extended by generated classes.  *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
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
DECL|class|Record
specifier|public
specifier|abstract
class|class
name|Record
implements|implements
name|WritableComparable
implements|,
name|Cloneable
block|{
comment|/**    * Serialize a record with tag (ususally field name)    * @param rout Record output destination    * @param tag record tag (Used only in tagged serialization e.g. XML)    */
DECL|method|serialize (RecordOutput rout, String tag)
specifier|public
specifier|abstract
name|void
name|serialize
parameter_list|(
name|RecordOutput
name|rout
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deserialize a record with a tag (usually field name)    * @param rin Record input source    * @param tag Record tag (Used only in tagged serialization e.g. XML)    */
DECL|method|deserialize (RecordInput rin, String tag)
specifier|public
specifier|abstract
name|void
name|deserialize
parameter_list|(
name|RecordInput
name|rin
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// inheric javadoc
DECL|method|compareTo (final Object peer)
specifier|public
specifier|abstract
name|int
name|compareTo
parameter_list|(
specifier|final
name|Object
name|peer
parameter_list|)
throws|throws
name|ClassCastException
function_decl|;
comment|/**    * Serialize a record without a tag    * @param rout Record output destination    */
DECL|method|serialize (RecordOutput rout)
specifier|public
name|void
name|serialize
parameter_list|(
name|RecordOutput
name|rout
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|serialize
argument_list|(
name|rout
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deserialize a record without a tag    * @param rin Record input source    */
DECL|method|deserialize (RecordInput rin)
specifier|public
name|void
name|deserialize
parameter_list|(
name|RecordInput
name|rin
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|deserialize
argument_list|(
name|rin
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// inherit javadoc
DECL|method|write (final DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|DataOutput
name|out
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|BinaryRecordOutput
name|bout
init|=
name|BinaryRecordOutput
operator|.
name|get
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|this
operator|.
name|serialize
argument_list|(
name|bout
argument_list|)
expr_stmt|;
block|}
comment|// inherit javadoc
DECL|method|readFields (final DataInput din)
specifier|public
name|void
name|readFields
parameter_list|(
specifier|final
name|DataInput
name|din
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|BinaryRecordInput
name|rin
init|=
name|BinaryRecordInput
operator|.
name|get
argument_list|(
name|din
argument_list|)
decl_stmt|;
name|this
operator|.
name|deserialize
argument_list|(
name|rin
argument_list|)
expr_stmt|;
block|}
comment|// inherit javadoc
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|ByteArrayOutputStream
name|s
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|CsvRecordOutput
name|a
init|=
operator|new
name|CsvRecordOutput
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|this
operator|.
name|serialize
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|s
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

