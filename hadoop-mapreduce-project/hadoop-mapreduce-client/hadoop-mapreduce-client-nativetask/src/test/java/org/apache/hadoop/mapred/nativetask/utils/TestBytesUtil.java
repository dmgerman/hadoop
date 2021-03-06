begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.utils
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
name|utils
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
DECL|class|TestBytesUtil
specifier|public
class|class
name|TestBytesUtil
block|{
annotation|@
name|Test
DECL|method|testBytesIntConversion ()
specifier|public
name|void
name|testBytesIntConversion
parameter_list|()
block|{
specifier|final
name|int
name|a
init|=
literal|1000
decl_stmt|;
specifier|final
name|byte
index|[]
name|intBytes
init|=
name|Ints
operator|.
name|toByteArray
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|a
argument_list|,
name|BytesUtil
operator|.
name|toInt
argument_list|(
name|intBytes
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBytesLongConversion ()
specifier|public
name|void
name|testBytesLongConversion
parameter_list|()
block|{
specifier|final
name|long
name|l
init|=
literal|1000000L
decl_stmt|;
specifier|final
name|byte
index|[]
name|longBytes
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|l
argument_list|,
name|BytesUtil
operator|.
name|toLong
argument_list|(
name|longBytes
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBytesFloatConversion ()
specifier|public
name|void
name|testBytesFloatConversion
parameter_list|()
block|{
specifier|final
name|float
name|f
init|=
literal|3.14f
decl_stmt|;
specifier|final
name|byte
index|[]
name|floatBytes
init|=
name|BytesUtil
operator|.
name|toBytes
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|f
argument_list|,
name|BytesUtil
operator|.
name|toFloat
argument_list|(
name|floatBytes
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBytesDoubleConversion ()
specifier|public
name|void
name|testBytesDoubleConversion
parameter_list|()
block|{
specifier|final
name|double
name|d
init|=
literal|3.14
decl_stmt|;
specifier|final
name|byte
index|[]
name|doubleBytes
init|=
name|BytesUtil
operator|.
name|toBytes
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|d
argument_list|,
name|BytesUtil
operator|.
name|toDouble
argument_list|(
name|doubleBytes
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToStringBinary ()
specifier|public
name|void
name|testToStringBinary
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"\\x01\\x02ABC"
argument_list|,
name|BytesUtil
operator|.
name|toStringBinary
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|65
block|,
literal|66
block|,
literal|67
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"\\x10\\x11"
argument_list|,
name|BytesUtil
operator|.
name|toStringBinary
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|16
block|,
literal|17
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

