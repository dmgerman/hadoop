begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timeline
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
name|WritableComparator
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
name|yarn
operator|.
name|server
operator|.
name|timeline
operator|.
name|GenericObjectMapper
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TestGenericObjectMapper
specifier|public
class|class
name|TestGenericObjectMapper
block|{
annotation|@
name|Test
DECL|method|testEncoding ()
specifier|public
name|void
name|testEncoding
parameter_list|()
block|{
name|testEncoding
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|testEncoding
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|testEncoding
argument_list|(
literal|0l
argument_list|)
expr_stmt|;
name|testEncoding
argument_list|(
literal|128l
argument_list|)
expr_stmt|;
name|testEncoding
argument_list|(
literal|256l
argument_list|)
expr_stmt|;
name|testEncoding
argument_list|(
literal|512l
argument_list|)
expr_stmt|;
name|testEncoding
argument_list|(
operator|-
literal|256l
argument_list|)
expr_stmt|;
block|}
DECL|method|testEncoding (long l)
specifier|private
specifier|static
name|void
name|testEncoding
parameter_list|(
name|long
name|l
parameter_list|)
block|{
name|byte
index|[]
name|b
init|=
name|GenericObjectMapper
operator|.
name|writeReverseOrderedLong
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"error decoding"
argument_list|,
name|l
argument_list|,
name|GenericObjectMapper
operator|.
name|readReverseOrderedLong
argument_list|(
name|b
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
literal|5
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"error decoding at offset"
argument_list|,
name|l
argument_list|,
name|GenericObjectMapper
operator|.
name|readReverseOrderedLong
argument_list|(
name|buf
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|l
operator|>
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
name|byte
index|[]
name|a
init|=
name|GenericObjectMapper
operator|.
name|writeReverseOrderedLong
argument_list|(
name|l
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"error preserving ordering"
argument_list|,
literal|1
argument_list|,
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|<
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|byte
index|[]
name|c
init|=
name|GenericObjectMapper
operator|.
name|writeReverseOrderedLong
argument_list|(
name|l
operator|+
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"error preserving ordering"
argument_list|,
literal|1
argument_list|,
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|,
name|c
argument_list|,
literal|0
argument_list|,
name|c
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verify (Object o)
specifier|private
specifier|static
name|void
name|verify
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|o
argument_list|,
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|GenericObjectMapper
operator|.
name|write
argument_list|(
name|o
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValueTypes ()
specifier|public
name|void
name|testValueTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|verify
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|GenericObjectMapper
operator|.
name|write
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|GenericObjectMapper
operator|.
name|write
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1l
argument_list|)
expr_stmt|;
name|verify
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MIN_VALUE
operator|-
literal|1l
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|GenericObjectMapper
operator|.
name|write
argument_list|(
literal|42l
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|42
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|1.23
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"123"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"k1"
argument_list|,
literal|"v1"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"k2"
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

