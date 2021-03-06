begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.typedbytes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|typedbytes
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
name|DataInput
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
name|*
import|;
end_import

begin_class
DECL|class|TestTypedBytesWritable
specifier|public
class|class
name|TestTypedBytesWritable
block|{
annotation|@
name|Test
DECL|method|testToString ()
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|TypedBytesWritable
name|tbw
init|=
operator|new
name|TypedBytesWritable
argument_list|()
decl_stmt|;
name|tbw
operator|.
name|setValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|tbw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tbw
operator|.
name|setValue
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12345"
argument_list|,
name|tbw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tbw
operator|.
name|setValue
argument_list|(
literal|123456789L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"123456789"
argument_list|,
name|tbw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tbw
operator|.
name|setValue
argument_list|(
operator|(
name|float
operator|)
literal|1.23
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.23"
argument_list|,
name|tbw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tbw
operator|.
name|setValue
argument_list|(
literal|1.23456789
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.23456789"
argument_list|,
name|tbw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tbw
operator|.
name|setValue
argument_list|(
literal|"random text"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"random text"
argument_list|,
name|tbw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIO ()
specifier|public
name|void
name|testIO
parameter_list|()
throws|throws
name|IOException
block|{
name|TypedBytesWritable
name|tbw
init|=
operator|new
name|TypedBytesWritable
argument_list|()
decl_stmt|;
name|tbw
operator|.
name|setValue
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutput
name|dout
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|tbw
operator|.
name|write
argument_list|(
name|dout
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|DataInput
name|din
init|=
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
name|TypedBytesWritable
name|readTbw
init|=
operator|new
name|TypedBytesWritable
argument_list|()
decl_stmt|;
name|readTbw
operator|.
name|readFields
argument_list|(
name|din
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tbw
argument_list|,
name|readTbw
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

