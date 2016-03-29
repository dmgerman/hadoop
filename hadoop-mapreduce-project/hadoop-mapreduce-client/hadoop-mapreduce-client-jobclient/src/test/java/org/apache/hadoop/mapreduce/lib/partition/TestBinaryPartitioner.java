begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.partition
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
name|partition
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|BinaryComparable
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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestBinaryPartitioner
specifier|public
class|class
name|TestBinaryPartitioner
block|{
annotation|@
name|Test
DECL|method|testDefaultOffsets ()
specifier|public
name|void
name|testDefaultOffsets
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BinaryPartitioner
argument_list|<
name|?
argument_list|>
name|partitioner
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|BinaryPartitioner
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|BinaryComparable
name|key1
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
decl_stmt|;
name|BinaryComparable
name|key2
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
decl_stmt|;
name|int
name|partition1
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|partition2
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|partition1
argument_list|,
name|partition2
argument_list|)
expr_stmt|;
name|key1
operator|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
name|key2
operator|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|6
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
name|partition1
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|partition2
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|partition1
operator|!=
name|partition2
argument_list|)
expr_stmt|;
name|key1
operator|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
name|key2
operator|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|6
block|}
argument_list|)
expr_stmt|;
name|partition1
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|partition2
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|partition1
operator|!=
name|partition2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomOffsets ()
specifier|public
name|void
name|testCustomOffsets
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BinaryComparable
name|key1
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
decl_stmt|;
name|BinaryComparable
name|key2
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|6
block|,
literal|2
block|,
literal|3
block|,
literal|7
block|,
literal|8
block|}
argument_list|)
decl_stmt|;
name|BinaryPartitioner
operator|.
name|setOffsets
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
operator|-
literal|3
argument_list|)
expr_stmt|;
name|BinaryPartitioner
argument_list|<
name|?
argument_list|>
name|partitioner
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|BinaryPartitioner
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|int
name|partition1
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|partition2
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|partition1
argument_list|,
name|partition2
argument_list|)
expr_stmt|;
name|BinaryPartitioner
operator|.
name|setOffsets
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|partitioner
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|BinaryPartitioner
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|partition1
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|partition2
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|partition1
argument_list|,
name|partition2
argument_list|)
expr_stmt|;
name|BinaryPartitioner
operator|.
name|setOffsets
argument_list|(
name|conf
argument_list|,
operator|-
literal|4
argument_list|,
operator|-
literal|3
argument_list|)
expr_stmt|;
name|partitioner
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|BinaryPartitioner
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|partition1
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|partition2
operator|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|partition1
argument_list|,
name|partition2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLowerBound ()
specifier|public
name|void
name|testLowerBound
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BinaryPartitioner
operator|.
name|setLeftOffset
argument_list|(
name|conf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BinaryPartitioner
argument_list|<
name|?
argument_list|>
name|partitioner
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|BinaryPartitioner
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|BinaryComparable
name|key1
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
decl_stmt|;
name|BinaryComparable
name|key2
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|6
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
decl_stmt|;
name|int
name|partition1
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|partition2
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|partition1
operator|!=
name|partition2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpperBound ()
specifier|public
name|void
name|testUpperBound
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BinaryPartitioner
operator|.
name|setRightOffset
argument_list|(
name|conf
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|BinaryPartitioner
argument_list|<
name|?
argument_list|>
name|partitioner
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|BinaryPartitioner
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|BinaryComparable
name|key1
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|)
decl_stmt|;
name|BinaryComparable
name|key2
init|=
operator|new
name|BytesWritable
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|6
block|}
argument_list|)
decl_stmt|;
name|int
name|partition1
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|partition2
init|=
name|partitioner
operator|.
name|getPartition
argument_list|(
name|key2
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|partition1
operator|!=
name|partition2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

