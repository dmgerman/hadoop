begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|scm
operator|.
name|storage
operator|.
name|BlockInputStream
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
name|ozone
operator|.
name|client
operator|.
name|io
operator|.
name|KeyInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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

begin_comment
comment|/**  * This class tests KeyInputStream and KeyOutputStream.  */
end_comment

begin_class
DECL|class|TestChunkStreams
specifier|public
class|class
name|TestChunkStreams
block|{
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testReadGroupInputStream ()
specifier|public
name|void
name|testReadGroupInputStream
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|KeyInputStream
name|groupInputStream
init|=
operator|new
name|KeyInputStream
argument_list|()
init|)
block|{
name|String
name|dataString
init|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
name|dataString
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|int
name|tempOffset
init|=
name|offset
decl_stmt|;
name|BlockInputStream
name|in
init|=
operator|new
name|BlockInputStream
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
block|{
specifier|private
name|long
name|pos
init|=
literal|0
decl_stmt|;
specifier|private
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
argument_list|,
name|tempOffset
argument_list|,
literal|100
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|()
return|;
block|}
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
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|readLen
init|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|readLen
expr_stmt|;
return|return
name|readLen
return|;
block|}
block|}
decl_stmt|;
name|offset
operator|+=
literal|100
expr_stmt|;
name|groupInputStream
operator|.
name|addStream
argument_list|(
name|in
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|resBuf
init|=
operator|new
name|byte
index|[
literal|500
index|]
decl_stmt|;
name|int
name|len
init|=
name|groupInputStream
operator|.
name|read
argument_list|(
name|resBuf
argument_list|,
literal|0
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataString
argument_list|,
operator|new
name|String
argument_list|(
name|resBuf
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testErrorReadGroupInputStream ()
specifier|public
name|void
name|testErrorReadGroupInputStream
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|KeyInputStream
name|groupInputStream
init|=
operator|new
name|KeyInputStream
argument_list|()
init|)
block|{
name|String
name|dataString
init|=
name|RandomStringUtils
operator|.
name|randomAscii
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
name|dataString
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|int
name|tempOffset
init|=
name|offset
decl_stmt|;
name|BlockInputStream
name|in
init|=
operator|new
name|BlockInputStream
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
block|{
specifier|private
name|long
name|pos
init|=
literal|0
decl_stmt|;
specifier|private
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
argument_list|,
name|tempOffset
argument_list|,
literal|100
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|()
return|;
block|}
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
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|readLen
init|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|readLen
expr_stmt|;
return|return
name|readLen
return|;
block|}
block|}
decl_stmt|;
name|offset
operator|+=
literal|100
expr_stmt|;
name|groupInputStream
operator|.
name|addStream
argument_list|(
name|in
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|resBuf
init|=
operator|new
name|byte
index|[
literal|600
index|]
decl_stmt|;
comment|// read 300 bytes first
name|int
name|len
init|=
name|groupInputStream
operator|.
name|read
argument_list|(
name|resBuf
argument_list|,
literal|0
argument_list|,
literal|340
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|groupInputStream
operator|.
name|getCurrentStreamIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|groupInputStream
operator|.
name|getRemainingOfIndex
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|340
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|340
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
name|resBuf
argument_list|,
name|UTF_8
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|340
argument_list|)
argument_list|)
expr_stmt|;
comment|// read following 300 bytes, but only 200 left
name|len
operator|=
name|groupInputStream
operator|.
name|read
argument_list|(
name|resBuf
argument_list|,
literal|340
argument_list|,
literal|260
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|groupInputStream
operator|.
name|getCurrentStreamIndex
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|groupInputStream
operator|.
name|getRemainingOfIndex
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|160
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dataString
argument_list|,
operator|new
name|String
argument_list|(
name|resBuf
argument_list|,
name|UTF_8
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
comment|// further read should get EOF
name|len
operator|=
name|groupInputStream
operator|.
name|read
argument_list|(
name|resBuf
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// reached EOF, further read should get -1
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

