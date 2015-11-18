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
name|assertFalse
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|MalformedInputException
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

begin_comment
comment|/** Unit tests for NonUTF8. */
end_comment

begin_class
DECL|class|TestTextNonUTF8
specifier|public
class|class
name|TestTextNonUTF8
block|{
annotation|@
name|Test
DECL|method|testNonUTF8 ()
specifier|public
name|void
name|testNonUTF8
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this is a non UTF8 byte array
name|byte
name|b
index|[]
init|=
block|{
operator|-
literal|0x01
block|,
operator|-
literal|0x01
block|,
operator|-
literal|0x01
block|,
operator|-
literal|0x01
block|,
operator|-
literal|0x01
block|,
operator|-
literal|0x01
block|,
operator|-
literal|0x01
block|}
decl_stmt|;
name|boolean
name|nonUTF8
init|=
literal|false
decl_stmt|;
name|Text
name|t
init|=
operator|new
name|Text
argument_list|(
name|b
argument_list|)
decl_stmt|;
try|try
block|{
name|Text
operator|.
name|validateUTF8
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedInputException
name|me
parameter_list|)
block|{
name|nonUTF8
operator|=
literal|false
expr_stmt|;
block|}
comment|// asserting that the byte array is non utf8
name|assertFalse
argument_list|(
name|nonUTF8
argument_list|)
expr_stmt|;
name|byte
name|ret
index|[]
init|=
name|t
operator|.
name|getBytes
argument_list|()
decl_stmt|;
comment|// asseting that the byte array are the same when the Text
comment|// object is created.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|b
argument_list|,
name|ret
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

