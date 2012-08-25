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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
comment|/**  * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
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
DECL|class|BinaryRecordInput
specifier|public
class|class
name|BinaryRecordInput
implements|implements
name|RecordInput
block|{
DECL|field|in
specifier|private
name|DataInput
name|in
decl_stmt|;
DECL|class|BinaryIndex
specifier|static
specifier|private
class|class
name|BinaryIndex
implements|implements
name|Index
block|{
DECL|field|nelems
specifier|private
name|int
name|nelems
decl_stmt|;
DECL|method|BinaryIndex (int nelems)
specifier|private
name|BinaryIndex
parameter_list|(
name|int
name|nelems
parameter_list|)
block|{
name|this
operator|.
name|nelems
operator|=
name|nelems
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|done ()
specifier|public
name|boolean
name|done
parameter_list|()
block|{
return|return
operator|(
name|nelems
operator|<=
literal|0
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|incr ()
specifier|public
name|void
name|incr
parameter_list|()
block|{
name|nelems
operator|--
expr_stmt|;
block|}
block|}
DECL|method|BinaryRecordInput ()
specifier|private
name|BinaryRecordInput
parameter_list|()
block|{}
DECL|method|setDataInput (DataInput inp)
specifier|private
name|void
name|setDataInput
parameter_list|(
name|DataInput
name|inp
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|inp
expr_stmt|;
block|}
DECL|field|bIn
specifier|private
specifier|static
name|ThreadLocal
name|bIn
init|=
operator|new
name|ThreadLocal
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|Object
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|BinaryRecordInput
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Get a thread-local record input for the supplied DataInput.    * @param inp data input stream    * @return binary record input corresponding to the supplied DataInput.    */
DECL|method|get (DataInput inp)
specifier|public
specifier|static
name|BinaryRecordInput
name|get
parameter_list|(
name|DataInput
name|inp
parameter_list|)
block|{
name|BinaryRecordInput
name|bin
init|=
operator|(
name|BinaryRecordInput
operator|)
name|bIn
operator|.
name|get
argument_list|()
decl_stmt|;
name|bin
operator|.
name|setDataInput
argument_list|(
name|inp
argument_list|)
expr_stmt|;
return|return
name|bin
return|;
block|}
comment|/** Creates a new instance of BinaryRecordInput */
DECL|method|BinaryRecordInput (InputStream strm)
specifier|public
name|BinaryRecordInput
parameter_list|(
name|InputStream
name|strm
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|strm
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new instance of BinaryRecordInput */
DECL|method|BinaryRecordInput (DataInput din)
specifier|public
name|BinaryRecordInput
parameter_list|(
name|DataInput
name|din
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|din
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte (final String tag)
specifier|public
name|byte
name|readByte
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readBool (final String tag)
specifier|public
name|boolean
name|readBool
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readBoolean
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readInt (final String tag)
specifier|public
name|int
name|readInt
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Utils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readLong (final String tag)
specifier|public
name|long
name|readLong
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Utils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFloat (final String tag)
specifier|public
name|float
name|readFloat
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readFloat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readDouble (final String tag)
specifier|public
name|double
name|readDouble
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readDouble
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readString (final String tag)
specifier|public
name|String
name|readString
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Utils
operator|.
name|fromBinaryString
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readBuffer (final String tag)
specifier|public
name|Buffer
name|readBuffer
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|Utils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|barr
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|barr
argument_list|)
expr_stmt|;
return|return
operator|new
name|Buffer
argument_list|(
name|barr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|startRecord (final String tag)
specifier|public
name|void
name|startRecord
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|endRecord (final String tag)
specifier|public
name|void
name|endRecord
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|startVector (final String tag)
specifier|public
name|Index
name|startVector
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BinaryIndex
argument_list|(
name|readInt
argument_list|(
name|tag
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|endVector (final String tag)
specifier|public
name|void
name|endVector
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|startMap (final String tag)
specifier|public
name|Index
name|startMap
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BinaryIndex
argument_list|(
name|readInt
argument_list|(
name|tag
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|endMap (final String tag)
specifier|public
name|void
name|endMap
parameter_list|(
specifier|final
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no-op
block|}
block|}
end_class

end_unit

