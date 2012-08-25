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
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|TestWritable
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
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

begin_comment
comment|/** Unit tests for MD5Hash. */
end_comment

begin_class
DECL|class|TestMD5Hash
specifier|public
class|class
name|TestMD5Hash
extends|extends
name|TestCase
block|{
DECL|method|TestMD5Hash (String name)
specifier|public
name|TestMD5Hash
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|getTestHash ()
specifier|public
specifier|static
name|MD5Hash
name|getTestHash
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageDigest
name|digest
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|RANDOM
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|digest
operator|.
name|update
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
operator|new
name|MD5Hash
argument_list|(
name|digest
operator|.
name|digest
argument_list|()
argument_list|)
return|;
block|}
DECL|field|D00
specifier|protected
specifier|static
name|byte
index|[]
name|D00
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
decl_stmt|;
DECL|field|DFF
specifier|protected
specifier|static
name|byte
index|[]
name|DFF
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|}
decl_stmt|;
DECL|method|testMD5Hash ()
specifier|public
name|void
name|testMD5Hash
parameter_list|()
throws|throws
name|Exception
block|{
name|MD5Hash
name|md5Hash
init|=
name|getTestHash
argument_list|()
decl_stmt|;
specifier|final
name|MD5Hash
name|md5Hash00
init|=
operator|new
name|MD5Hash
argument_list|(
name|D00
argument_list|)
decl_stmt|;
specifier|final
name|MD5Hash
name|md5HashFF
init|=
operator|new
name|MD5Hash
argument_list|(
name|DFF
argument_list|)
decl_stmt|;
name|MD5Hash
name|orderedHash
init|=
operator|new
name|MD5Hash
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
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|,
literal|14
block|,
literal|15
block|,
literal|16
block|}
argument_list|)
decl_stmt|;
name|MD5Hash
name|backwardHash
init|=
operator|new
name|MD5Hash
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|3
block|,
operator|-
literal|4
block|,
operator|-
literal|5
block|,
operator|-
literal|6
block|,
operator|-
literal|7
block|,
operator|-
literal|8
block|,
operator|-
literal|9
block|,
operator|-
literal|10
block|,
operator|-
literal|11
block|,
operator|-
literal|12
block|,
operator|-
literal|13
block|,
operator|-
literal|14
block|,
operator|-
literal|15
block|,
operator|-
literal|16
block|}
argument_list|)
decl_stmt|;
name|MD5Hash
name|closeHash1
init|=
operator|new
name|MD5Hash
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
decl_stmt|;
name|MD5Hash
name|closeHash2
init|=
operator|new
name|MD5Hash
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
decl_stmt|;
comment|// test i/o
name|TestWritable
operator|.
name|testWritable
argument_list|(
name|md5Hash
argument_list|)
expr_stmt|;
name|TestWritable
operator|.
name|testWritable
argument_list|(
name|md5Hash00
argument_list|)
expr_stmt|;
name|TestWritable
operator|.
name|testWritable
argument_list|(
name|md5HashFF
argument_list|)
expr_stmt|;
comment|// test equals()
name|assertEquals
argument_list|(
name|md5Hash
argument_list|,
name|md5Hash
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|md5Hash00
argument_list|,
name|md5Hash00
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|md5HashFF
argument_list|,
name|md5HashFF
argument_list|)
expr_stmt|;
comment|// test compareTo()
name|assertTrue
argument_list|(
name|md5Hash
operator|.
name|compareTo
argument_list|(
name|md5Hash
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|md5Hash00
operator|.
name|compareTo
argument_list|(
name|md5Hash
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|md5HashFF
operator|.
name|compareTo
argument_list|(
name|md5Hash
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// test toString and string ctor
name|assertEquals
argument_list|(
name|md5Hash
argument_list|,
operator|new
name|MD5Hash
argument_list|(
name|md5Hash
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|md5Hash00
argument_list|,
operator|new
name|MD5Hash
argument_list|(
name|md5Hash00
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|md5HashFF
argument_list|,
operator|new
name|MD5Hash
argument_list|(
name|md5HashFF
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x01020304
argument_list|,
name|orderedHash
operator|.
name|quarterDigest
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0xfffefdfc
argument_list|,
name|backwardHash
operator|.
name|quarterDigest
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x0102030405060708L
argument_list|,
name|orderedHash
operator|.
name|halfDigest
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0xfffefdfcfbfaf9f8L
argument_list|,
name|backwardHash
operator|.
name|halfDigest
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hash collision"
argument_list|,
name|closeHash1
operator|.
name|hashCode
argument_list|()
operator|!=
name|closeHash2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|MD5Hash
name|hash
init|=
operator|new
name|MD5Hash
argument_list|(
name|DFF
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hash
argument_list|,
name|md5HashFF
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|t2
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|MD5Hash
name|hash
init|=
operator|new
name|MD5Hash
argument_list|(
name|D00
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hash
argument_list|,
name|md5Hash00
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
name|t2
operator|.
name|start
argument_list|()
expr_stmt|;
name|t1
operator|.
name|join
argument_list|()
expr_stmt|;
name|t2
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|testFactoryReturnsClearedHashes ()
specifier|public
name|void
name|testFactoryReturnsClearedHashes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// A stream that will throw an IOE after reading some bytes
name|ByteArrayInputStream
name|failingStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"xxxx"
operator|.
name|getBytes
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Injected fault"
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|String
name|TEST_STRING
init|=
literal|"hello"
decl_stmt|;
comment|// Calculate the correct digest for the test string
name|MD5Hash
name|expectedHash
init|=
name|MD5Hash
operator|.
name|digest
argument_list|(
name|TEST_STRING
argument_list|)
decl_stmt|;
comment|// Hashing again should give the same result
name|assertEquals
argument_list|(
name|expectedHash
argument_list|,
name|MD5Hash
operator|.
name|digest
argument_list|(
name|TEST_STRING
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try to hash a stream which will fail halfway through
try|try
block|{
name|MD5Hash
operator|.
name|digest
argument_list|(
name|failingStream
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didnt throw!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// Make sure we get the same result
name|assertEquals
argument_list|(
name|expectedHash
argument_list|,
name|MD5Hash
operator|.
name|digest
argument_list|(
name|TEST_STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

