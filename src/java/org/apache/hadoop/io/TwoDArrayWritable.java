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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_comment
comment|/** A Writable for 2D arrays containing a matrix of instances of a class. */
end_comment

begin_class
DECL|class|TwoDArrayWritable
specifier|public
class|class
name|TwoDArrayWritable
implements|implements
name|Writable
block|{
DECL|field|valueClass
specifier|private
name|Class
name|valueClass
decl_stmt|;
DECL|field|values
specifier|private
name|Writable
index|[]
index|[]
name|values
decl_stmt|;
DECL|method|TwoDArrayWritable (Class valueClass)
specifier|public
name|TwoDArrayWritable
parameter_list|(
name|Class
name|valueClass
parameter_list|)
block|{
name|this
operator|.
name|valueClass
operator|=
name|valueClass
expr_stmt|;
block|}
DECL|method|TwoDArrayWritable (Class valueClass, Writable[][] values)
specifier|public
name|TwoDArrayWritable
parameter_list|(
name|Class
name|valueClass
parameter_list|,
name|Writable
index|[]
index|[]
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|valueClass
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
DECL|method|toArray ()
specifier|public
name|Object
name|toArray
parameter_list|()
block|{
name|int
name|dimensions
index|[]
init|=
block|{
name|values
operator|.
name|length
block|,
literal|0
block|}
decl_stmt|;
name|Object
name|result
init|=
name|Array
operator|.
name|newInstance
argument_list|(
name|valueClass
argument_list|,
name|dimensions
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|resultRow
init|=
name|Array
operator|.
name|newInstance
argument_list|(
name|valueClass
argument_list|,
name|values
index|[
name|i
index|]
operator|.
name|length
argument_list|)
decl_stmt|;
name|Array
operator|.
name|set
argument_list|(
name|result
argument_list|,
name|i
argument_list|,
name|resultRow
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Array
operator|.
name|set
argument_list|(
name|resultRow
argument_list|,
name|j
argument_list|,
name|values
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|set (Writable[][] values)
specifier|public
name|void
name|set
parameter_list|(
name|Writable
index|[]
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
DECL|method|get ()
specifier|public
name|Writable
index|[]
index|[]
name|get
parameter_list|()
block|{
return|return
name|values
return|;
block|}
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
comment|// construct matrix
name|values
operator|=
operator|new
name|Writable
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
index|[]
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
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
operator|new
name|Writable
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
block|}
comment|// construct values
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
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Writable
name|value
decl_stmt|;
comment|// construct value
try|try
block|{
name|value
operator|=
operator|(
name|Writable
operator|)
name|valueClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|value
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// read a value
name|values
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|value
expr_stmt|;
comment|// store it in values
block|}
block|}
block|}
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
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// write values
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
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|length
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
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
index|[
name|j
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
block|}
end_class

end_unit

