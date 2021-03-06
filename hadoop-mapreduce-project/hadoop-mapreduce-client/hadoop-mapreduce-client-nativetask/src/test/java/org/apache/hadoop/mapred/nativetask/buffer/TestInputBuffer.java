begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.buffer
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
name|buffer
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
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

begin_class
DECL|class|TestInputBuffer
specifier|public
class|class
name|TestInputBuffer
block|{
annotation|@
name|Test
DECL|method|testInputBuffer ()
specifier|public
name|void
name|testInputBuffer
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
literal|100
decl_stmt|;
specifier|final
name|InputBuffer
name|input1
init|=
operator|new
name|InputBuffer
argument_list|(
name|BufferType
operator|.
name|DIRECT_BUFFER
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|input1
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|BufferType
operator|.
name|DIRECT_BUFFER
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input1
operator|.
name|position
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input1
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input1
operator|.
name|remaining
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input1
operator|.
name|capacity
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|size
argument_list|)
expr_stmt|;
specifier|final
name|InputBuffer
name|input2
init|=
operator|new
name|InputBuffer
argument_list|(
name|BufferType
operator|.
name|HEAP_BUFFER
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|input2
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|BufferType
operator|.
name|HEAP_BUFFER
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input2
operator|.
name|position
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input2
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input2
operator|.
name|remaining
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input2
operator|.
name|capacity
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|size
argument_list|)
expr_stmt|;
specifier|final
name|InputBuffer
name|input3
init|=
operator|new
name|InputBuffer
argument_list|(
operator|new
name|byte
index|[
name|size
index|]
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|input3
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|BufferType
operator|.
name|HEAP_BUFFER
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input3
operator|.
name|position
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input3
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input3
operator|.
name|remaining
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|input3
operator|.
name|capacity
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

