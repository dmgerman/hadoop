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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
DECL|class|BinaryRecordOutput
specifier|public
class|class
name|BinaryRecordOutput
implements|implements
name|RecordOutput
block|{
DECL|field|out
specifier|private
name|DataOutput
name|out
decl_stmt|;
DECL|method|BinaryRecordOutput ()
specifier|private
name|BinaryRecordOutput
parameter_list|()
block|{}
DECL|method|setDataOutput (DataOutput out)
specifier|private
name|void
name|setDataOutput
parameter_list|(
name|DataOutput
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
DECL|field|B_OUT
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|BinaryRecordOutput
argument_list|>
name|B_OUT
init|=
operator|new
name|ThreadLocal
argument_list|<
name|BinaryRecordOutput
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|BinaryRecordOutput
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|BinaryRecordOutput
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Get a thread-local record output for the supplied DataOutput.    * @param out data output stream    * @return binary record output corresponding to the supplied DataOutput.    */
DECL|method|get (DataOutput out)
specifier|public
specifier|static
name|BinaryRecordOutput
name|get
parameter_list|(
name|DataOutput
name|out
parameter_list|)
block|{
name|BinaryRecordOutput
name|bout
init|=
name|B_OUT
operator|.
name|get
argument_list|()
decl_stmt|;
name|bout
operator|.
name|setDataOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|bout
return|;
block|}
comment|/** Creates a new instance of BinaryRecordOutput */
DECL|method|BinaryRecordOutput (OutputStream out)
specifier|public
name|BinaryRecordOutput
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new instance of BinaryRecordOutput */
DECL|method|BinaryRecordOutput (DataOutput out)
specifier|public
name|BinaryRecordOutput
parameter_list|(
name|DataOutput
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte (byte b, String tag)
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBool (boolean b, String tag)
specifier|public
name|void
name|writeBool
parameter_list|(
name|boolean
name|b
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeInt (int i, String tag)
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|i
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Utils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeLong (long l, String tag)
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|l
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Utils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat (float f, String tag)
specifier|public
name|void
name|writeFloat
parameter_list|(
name|float
name|f
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeFloat
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDouble (double d, String tag)
specifier|public
name|void
name|writeDouble
parameter_list|(
name|double
name|d
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeString (String s, String tag)
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|Utils
operator|.
name|toBinaryString
argument_list|(
name|out
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBuffer (Buffer buf, String tag)
specifier|public
name|void
name|writeBuffer
parameter_list|(
name|Buffer
name|buf
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|barr
init|=
name|buf
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|buf
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|Utils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|barr
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startRecord (Record r, String tag)
specifier|public
name|void
name|startRecord
parameter_list|(
name|Record
name|r
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|endRecord (Record r, String tag)
specifier|public
name|void
name|endRecord
parameter_list|(
name|Record
name|r
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|startVector (ArrayList v, String tag)
specifier|public
name|void
name|startVector
parameter_list|(
name|ArrayList
name|v
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|v
operator|.
name|size
argument_list|()
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|endVector (ArrayList v, String tag)
specifier|public
name|void
name|endVector
parameter_list|(
name|ArrayList
name|v
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|startMap (TreeMap v, String tag)
specifier|public
name|void
name|startMap
parameter_list|(
name|TreeMap
name|v
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|v
operator|.
name|size
argument_list|()
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|endMap (TreeMap v, String tag)
specifier|public
name|void
name|endMap
parameter_list|(
name|TreeMap
name|v
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

