begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
package|;
end_package

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
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|Schema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|generic
operator|.
name|GenericData
import|;
end_import

begin_class
DECL|class|AvroArrayUtils
specifier|public
class|class
name|AvroArrayUtils
block|{
DECL|field|ARRAY_INT
specifier|private
specifier|static
specifier|final
name|Schema
name|ARRAY_INT
init|=
name|Schema
operator|.
name|createArray
argument_list|(
name|Schema
operator|.
name|create
argument_list|(
name|Schema
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|NULL_PROGRESS_SPLITS_ARRAY
specifier|static
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|NULL_PROGRESS_SPLITS_ARRAY
init|=
operator|new
name|GenericData
operator|.
name|Array
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|0
argument_list|,
name|ARRAY_INT
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
DECL|method|toAvro (int values[])
name|toAvro
parameter_list|(
name|int
name|values
index|[]
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|values
operator|.
name|length
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
operator|++
name|i
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|fromAvro (List<Integer> avro)
specifier|public
specifier|static
name|int
index|[]
name|fromAvro
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|avro
parameter_list|)
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|avro
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|avro
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

