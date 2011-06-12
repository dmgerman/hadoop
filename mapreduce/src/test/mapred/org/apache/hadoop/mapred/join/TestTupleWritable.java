begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|join
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|DataInputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|BooleanWritable
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
name|BytesWritable
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
name|FloatWritable
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
name|IntWritable
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
name|io
operator|.
name|WritableUtils
import|;
end_import

begin_class
DECL|class|TestTupleWritable
specifier|public
class|class
name|TestTupleWritable
extends|extends
name|TestCase
block|{
DECL|method|makeTuple (Writable[] writs)
specifier|private
name|TupleWritable
name|makeTuple
parameter_list|(
name|Writable
index|[]
name|writs
parameter_list|)
block|{
name|Writable
index|[]
name|sub1
init|=
block|{
name|writs
index|[
literal|1
index|]
block|,
name|writs
index|[
literal|2
index|]
block|}
decl_stmt|;
name|Writable
index|[]
name|sub3
init|=
block|{
name|writs
index|[
literal|4
index|]
block|,
name|writs
index|[
literal|5
index|]
block|}
decl_stmt|;
name|Writable
index|[]
name|sub2
init|=
block|{
name|writs
index|[
literal|3
index|]
block|,
operator|new
name|TupleWritable
argument_list|(
name|sub3
argument_list|)
block|,
name|writs
index|[
literal|6
index|]
block|}
decl_stmt|;
name|Writable
index|[]
name|vals
init|=
block|{
name|writs
index|[
literal|0
index|]
block|,
operator|new
name|TupleWritable
argument_list|(
name|sub1
argument_list|)
block|,
operator|new
name|TupleWritable
argument_list|(
name|sub2
argument_list|)
block|,
name|writs
index|[
literal|7
index|]
block|,
name|writs
index|[
literal|8
index|]
block|,
name|writs
index|[
literal|9
index|]
block|}
decl_stmt|;
comment|// [v0, [v1, v2], [v3, [v4, v5], v6], v7, v8, v9]
name|TupleWritable
name|ret
init|=
operator|new
name|TupleWritable
argument_list|(
name|vals
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6
condition|;
operator|++
name|i
control|)
block|{
name|ret
operator|.
name|setWritten
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|TupleWritable
operator|)
name|sub2
index|[
literal|1
index|]
operator|)
operator|.
name|setWritten
argument_list|(
literal|0
argument_list|)
expr_stmt|;
operator|(
operator|(
name|TupleWritable
operator|)
name|sub2
index|[
literal|1
index|]
operator|)
operator|.
name|setWritten
argument_list|(
literal|1
argument_list|)
expr_stmt|;
operator|(
operator|(
name|TupleWritable
operator|)
name|vals
index|[
literal|1
index|]
operator|)
operator|.
name|setWritten
argument_list|(
literal|0
argument_list|)
expr_stmt|;
operator|(
operator|(
name|TupleWritable
operator|)
name|vals
index|[
literal|1
index|]
operator|)
operator|.
name|setWritten
argument_list|(
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
operator|++
name|i
control|)
block|{
operator|(
operator|(
name|TupleWritable
operator|)
name|vals
index|[
literal|2
index|]
operator|)
operator|.
name|setWritten
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|makeRandomWritables ()
specifier|private
name|Writable
index|[]
name|makeRandomWritables
parameter_list|()
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Writable
index|[]
name|writs
init|=
block|{
operator|new
name|BooleanWritable
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"dingo"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"yak"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|}
decl_stmt|;
return|return
name|writs
return|;
block|}
DECL|method|makeRandomWritables (int numWrits)
specifier|private
name|Writable
index|[]
name|makeRandomWritables
parameter_list|(
name|int
name|numWrits
parameter_list|)
block|{
name|Writable
index|[]
name|writs
init|=
name|makeRandomWritables
argument_list|()
decl_stmt|;
name|Writable
index|[]
name|manyWrits
init|=
operator|new
name|Writable
index|[
name|numWrits
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|manyWrits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|manyWrits
index|[
name|i
index|]
operator|=
name|writs
index|[
name|i
operator|%
name|writs
operator|.
name|length
index|]
expr_stmt|;
block|}
return|return
name|manyWrits
return|;
block|}
DECL|method|verifIter (Writable[] writs, TupleWritable t, int i)
specifier|private
name|int
name|verifIter
parameter_list|(
name|Writable
index|[]
name|writs
parameter_list|,
name|TupleWritable
name|t
parameter_list|,
name|int
name|i
parameter_list|)
block|{
for|for
control|(
name|Writable
name|w
range|:
name|t
control|)
block|{
if|if
condition|(
name|w
operator|instanceof
name|TupleWritable
condition|)
block|{
name|i
operator|=
name|verifIter
argument_list|(
name|writs
argument_list|,
operator|(
operator|(
name|TupleWritable
operator|)
name|w
operator|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|assertTrue
argument_list|(
literal|"Bad value"
argument_list|,
name|w
operator|.
name|equals
argument_list|(
name|writs
index|[
name|i
operator|++
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
DECL|method|testIterable ()
specifier|public
name|void
name|testIterable
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Writable
index|[]
name|writs
init|=
block|{
operator|new
name|BooleanWritable
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"dingo"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"yak"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|}
decl_stmt|;
name|TupleWritable
name|t
init|=
operator|new
name|TupleWritable
argument_list|(
name|writs
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6
condition|;
operator|++
name|i
control|)
block|{
name|t
operator|.
name|setWritten
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|verifIter
argument_list|(
name|writs
argument_list|,
name|t
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedIterable ()
specifier|public
name|void
name|testNestedIterable
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Writable
index|[]
name|writs
init|=
block|{
operator|new
name|BooleanWritable
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"dingo"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"yak"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|}
decl_stmt|;
name|TupleWritable
name|sTuple
init|=
name|makeTuple
argument_list|(
name|writs
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Bad count"
argument_list|,
name|writs
operator|.
name|length
operator|==
name|verifIter
argument_list|(
name|writs
argument_list|,
name|sTuple
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWritable ()
specifier|public
name|void
name|testWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Writable
index|[]
name|writs
init|=
block|{
operator|new
name|BooleanWritable
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|FloatWritable
argument_list|(
name|r
operator|.
name|nextFloat
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"dingo"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|LongWritable
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|,
operator|new
name|BytesWritable
argument_list|(
literal|"yak"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|,
operator|new
name|IntWritable
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
block|}
decl_stmt|;
name|TupleWritable
name|sTuple
init|=
name|makeTuple
argument_list|(
name|writs
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|sTuple
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|TupleWritable
name|dTuple
init|=
operator|new
name|TupleWritable
argument_list|()
decl_stmt|;
name|dTuple
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to write/read tuple"
argument_list|,
name|sTuple
operator|.
name|equals
argument_list|(
name|dTuple
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testWideWritable ()
specifier|public
name|void
name|testWideWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|Writable
index|[]
name|manyWrits
init|=
name|makeRandomWritables
argument_list|(
literal|131
argument_list|)
decl_stmt|;
name|TupleWritable
name|sTuple
init|=
operator|new
name|TupleWritable
argument_list|(
name|manyWrits
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|manyWrits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
name|sTuple
operator|.
name|setWritten
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|sTuple
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|TupleWritable
name|dTuple
init|=
operator|new
name|TupleWritable
argument_list|()
decl_stmt|;
name|dTuple
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to write/read tuple"
argument_list|,
name|sTuple
operator|.
name|equals
argument_list|(
name|dTuple
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All tuple data has not been read from the stream"
argument_list|,
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testWideWritable2 ()
specifier|public
name|void
name|testWideWritable2
parameter_list|()
throws|throws
name|Exception
block|{
name|Writable
index|[]
name|manyWrits
init|=
name|makeRandomWritables
argument_list|(
literal|71
argument_list|)
decl_stmt|;
name|TupleWritable
name|sTuple
init|=
operator|new
name|TupleWritable
argument_list|(
name|manyWrits
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|manyWrits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sTuple
operator|.
name|setWritten
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|sTuple
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|TupleWritable
name|dTuple
init|=
operator|new
name|TupleWritable
argument_list|()
decl_stmt|;
name|dTuple
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to write/read tuple"
argument_list|,
name|sTuple
operator|.
name|equals
argument_list|(
name|dTuple
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All tuple data has not been read from the stream"
argument_list|,
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests a tuple writable with more than 64 values and the values set written    * spread far apart.    */
DECL|method|testSparseWideWritable ()
specifier|public
name|void
name|testSparseWideWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|Writable
index|[]
name|manyWrits
init|=
name|makeRandomWritables
argument_list|(
literal|131
argument_list|)
decl_stmt|;
name|TupleWritable
name|sTuple
init|=
operator|new
name|TupleWritable
argument_list|(
name|manyWrits
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|manyWrits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|65
operator|==
literal|0
condition|)
block|{
name|sTuple
operator|.
name|setWritten
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|sTuple
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|TupleWritable
name|dTuple
init|=
operator|new
name|TupleWritable
argument_list|()
decl_stmt|;
name|dTuple
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to write/read tuple"
argument_list|,
name|sTuple
operator|.
name|equals
argument_list|(
name|dTuple
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All tuple data has not been read from the stream"
argument_list|,
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testWideTuple ()
specifier|public
name|void
name|testWideTuple
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|emptyText
init|=
operator|new
name|Text
argument_list|(
literal|"Should be empty"
argument_list|)
decl_stmt|;
name|Writable
index|[]
name|values
init|=
operator|new
name|Writable
index|[
literal|64
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
name|emptyText
argument_list|)
expr_stmt|;
name|values
index|[
literal|42
index|]
operator|=
operator|new
name|Text
argument_list|(
literal|"Number 42"
argument_list|)
expr_stmt|;
name|TupleWritable
name|tuple
init|=
operator|new
name|TupleWritable
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|tuple
operator|.
name|setWritten
argument_list|(
literal|42
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|tuple
operator|.
name|size
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
name|boolean
name|has
init|=
name|tuple
operator|.
name|has
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
literal|42
condition|)
block|{
name|assertTrue
argument_list|(
name|has
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"Tuple position is incorrectly labelled as set: "
operator|+
name|pos
argument_list|,
name|has
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testWideTuple2 ()
specifier|public
name|void
name|testWideTuple2
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|emptyText
init|=
operator|new
name|Text
argument_list|(
literal|"Should be empty"
argument_list|)
decl_stmt|;
name|Writable
index|[]
name|values
init|=
operator|new
name|Writable
index|[
literal|64
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
name|emptyText
argument_list|)
expr_stmt|;
name|values
index|[
literal|9
index|]
operator|=
operator|new
name|Text
argument_list|(
literal|"Number 9"
argument_list|)
expr_stmt|;
name|TupleWritable
name|tuple
init|=
operator|new
name|TupleWritable
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|tuple
operator|.
name|setWritten
argument_list|(
literal|9
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|tuple
operator|.
name|size
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
name|boolean
name|has
init|=
name|tuple
operator|.
name|has
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
literal|9
condition|)
block|{
name|assertTrue
argument_list|(
name|has
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"Tuple position is incorrectly labelled as set: "
operator|+
name|pos
argument_list|,
name|has
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Tests that we can write more than 64 values.    */
DECL|method|testWideTupleBoundary ()
specifier|public
name|void
name|testWideTupleBoundary
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|emptyText
init|=
operator|new
name|Text
argument_list|(
literal|"Should not be set written"
argument_list|)
decl_stmt|;
name|Writable
index|[]
name|values
init|=
operator|new
name|Writable
index|[
literal|65
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
name|emptyText
argument_list|)
expr_stmt|;
name|values
index|[
literal|64
index|]
operator|=
operator|new
name|Text
argument_list|(
literal|"Should be the only value set written"
argument_list|)
expr_stmt|;
name|TupleWritable
name|tuple
init|=
operator|new
name|TupleWritable
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|tuple
operator|.
name|setWritten
argument_list|(
literal|64
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|tuple
operator|.
name|size
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
name|boolean
name|has
init|=
name|tuple
operator|.
name|has
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
literal|64
condition|)
block|{
name|assertTrue
argument_list|(
name|has
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"Tuple position is incorrectly labelled as set: "
operator|+
name|pos
argument_list|,
name|has
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Tests compatibility with pre-0.21 versions of TupleWritable    */
DECL|method|testPreVersion21Compatibility ()
specifier|public
name|void
name|testPreVersion21Compatibility
parameter_list|()
throws|throws
name|Exception
block|{
name|Writable
index|[]
name|manyWrits
init|=
name|makeRandomWritables
argument_list|(
literal|64
argument_list|)
decl_stmt|;
name|PreVersion21TupleWritable
name|oldTuple
init|=
operator|new
name|PreVersion21TupleWritable
argument_list|(
name|manyWrits
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|manyWrits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
name|oldTuple
operator|.
name|setWritten
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|oldTuple
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|TupleWritable
name|dTuple
init|=
operator|new
name|TupleWritable
argument_list|()
decl_stmt|;
name|dTuple
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Tuple writable is unable to read pre-0.21 versions of TupleWritable"
argument_list|,
name|oldTuple
operator|.
name|isCompatible
argument_list|(
name|dTuple
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All tuple data has not been read from the stream"
argument_list|,
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPreVersion21CompatibilityEmptyTuple ()
specifier|public
name|void
name|testPreVersion21CompatibilityEmptyTuple
parameter_list|()
throws|throws
name|Exception
block|{
name|Writable
index|[]
name|manyWrits
init|=
operator|new
name|Writable
index|[
literal|0
index|]
decl_stmt|;
name|PreVersion21TupleWritable
name|oldTuple
init|=
operator|new
name|PreVersion21TupleWritable
argument_list|(
name|manyWrits
argument_list|)
decl_stmt|;
comment|// don't set any values written
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|oldTuple
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|TupleWritable
name|dTuple
init|=
operator|new
name|TupleWritable
argument_list|()
decl_stmt|;
name|dTuple
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Tuple writable is unable to read pre-0.21 versions of TupleWritable"
argument_list|,
name|oldTuple
operator|.
name|isCompatible
argument_list|(
name|dTuple
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"All tuple data has not been read from the stream"
argument_list|,
operator|-
literal|1
argument_list|,
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes to the DataOutput stream in the same way as pre-0.21 versions of    * {@link TupleWritable#write(DataOutput)}    */
DECL|class|PreVersion21TupleWritable
specifier|private
specifier|static
class|class
name|PreVersion21TupleWritable
block|{
DECL|field|values
specifier|private
name|Writable
index|[]
name|values
decl_stmt|;
DECL|field|written
specifier|private
name|long
name|written
init|=
literal|0L
decl_stmt|;
DECL|method|PreVersion21TupleWritable (Writable[] vals)
specifier|private
name|PreVersion21TupleWritable
parameter_list|(
name|Writable
index|[]
name|vals
parameter_list|)
block|{
name|written
operator|=
literal|0L
expr_stmt|;
name|values
operator|=
name|vals
expr_stmt|;
block|}
DECL|method|setWritten (int i)
specifier|private
name|void
name|setWritten
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|written
operator||=
literal|1L
operator|<<
name|i
expr_stmt|;
block|}
DECL|method|has (int i)
specifier|private
name|boolean
name|has
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|0
operator|!=
operator|(
operator|(
literal|1L
operator|<<
name|i
operator|)
operator|&
name|written
operator|)
return|;
block|}
DECL|method|write (DataOutput out)
specifier|private
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|written
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|values
index|[
name|i
index|]
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|has
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|values
index|[
name|i
index|]
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|values
operator|.
name|length
return|;
block|}
DECL|method|isCompatible (TupleWritable that)
specifier|public
name|boolean
name|isCompatible
parameter_list|(
name|TupleWritable
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|size
argument_list|()
operator|!=
name|that
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|has
argument_list|(
name|i
argument_list|)
operator|!=
name|that
operator|.
name|has
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|has
argument_list|(
name|i
argument_list|)
operator|&&
operator|!
name|values
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|that
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

