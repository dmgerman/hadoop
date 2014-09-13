begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.testutil
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|testutil
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|LongWritable
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
name|Text
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
name|nativetask
operator|.
name|util
operator|.
name|BytesUtil
import|;
end_import

begin_class
DECL|class|MockValueClass
specifier|public
class|class
name|MockValueClass
implements|implements
name|Writable
block|{
DECL|field|DEFAULT_ARRAY_LENGTH
specifier|private
specifier|final
specifier|static
name|int
name|DEFAULT_ARRAY_LENGTH
init|=
literal|16
decl_stmt|;
DECL|field|a
specifier|private
name|int
name|a
init|=
literal|0
decl_stmt|;
DECL|field|array
specifier|private
name|byte
index|[]
name|array
decl_stmt|;
DECL|field|longWritable
specifier|private
specifier|final
name|LongWritable
name|longWritable
decl_stmt|;
DECL|field|txt
specifier|private
specifier|final
name|Text
name|txt
decl_stmt|;
DECL|field|rand
specifier|private
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|MockValueClass ()
specifier|public
name|MockValueClass
parameter_list|()
block|{
name|a
operator|=
name|rand
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|array
operator|=
operator|new
name|byte
index|[
name|DEFAULT_ARRAY_LENGTH
index|]
expr_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|array
argument_list|)
expr_stmt|;
name|longWritable
operator|=
operator|new
name|LongWritable
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|txt
operator|=
operator|new
name|Text
argument_list|(
name|BytesUtil
operator|.
name|toStringBinary
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|MockValueClass (byte[] seed)
specifier|public
name|MockValueClass
parameter_list|(
name|byte
index|[]
name|seed
parameter_list|)
block|{
name|a
operator|=
name|seed
operator|.
name|length
expr_stmt|;
name|array
operator|=
operator|new
name|byte
index|[
name|seed
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|seed
argument_list|,
literal|0
argument_list|,
name|array
argument_list|,
literal|0
argument_list|,
name|seed
operator|.
name|length
argument_list|)
expr_stmt|;
name|longWritable
operator|=
operator|new
name|LongWritable
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|txt
operator|=
operator|new
name|Text
argument_list|(
name|BytesUtil
operator|.
name|toStringBinary
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|set (byte[] seed)
specifier|public
name|void
name|set
parameter_list|(
name|byte
index|[]
name|seed
parameter_list|)
block|{
name|a
operator|=
name|seed
operator|.
name|length
expr_stmt|;
name|array
operator|=
operator|new
name|byte
index|[
name|seed
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|seed
argument_list|,
literal|0
argument_list|,
name|array
argument_list|,
literal|0
argument_list|,
name|seed
operator|.
name|length
argument_list|)
expr_stmt|;
name|longWritable
operator|.
name|set
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|txt
operator|.
name|set
argument_list|(
name|BytesUtil
operator|.
name|toStringBinary
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|array
argument_list|)
expr_stmt|;
name|longWritable
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|txt
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|a
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
specifier|final
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|array
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|array
argument_list|)
expr_stmt|;
name|longWritable
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|txt
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

