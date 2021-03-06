begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
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
name|util
operator|.
name|Arrays
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
name|HadoopIllegalArgumentException
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
name|*
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|TestDelegationToken
operator|.
name|TestDelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|TestDelegationToken
operator|.
name|TestDelegationTokenSecretManager
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
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
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

begin_comment
comment|/** Unit tests for Token */
end_comment

begin_class
DECL|class|TestToken
specifier|public
class|class
name|TestToken
block|{
DECL|method|isEqual (Object a, Object b)
specifier|static
name|boolean
name|isEqual
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|a
operator|==
literal|null
condition|?
name|b
operator|==
literal|null
else|:
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
block|}
DECL|method|checkEqual (Token<TokenIdentifier> a, Token<TokenIdentifier> b)
specifier|static
name|boolean
name|checkEqual
parameter_list|(
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
name|a
parameter_list|,
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
name|b
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|b
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getPassword
argument_list|()
argument_list|,
name|b
operator|.
name|getPassword
argument_list|()
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|a
operator|.
name|getKind
argument_list|()
argument_list|,
name|b
operator|.
name|getKind
argument_list|()
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|a
operator|.
name|getService
argument_list|()
argument_list|,
name|b
operator|.
name|getService
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Test token serialization    */
annotation|@
name|Test
DECL|method|testTokenSerialization ()
specifier|public
name|void
name|testTokenSerialization
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Get a token
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
name|sourceToken
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|sourceToken
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
literal|"service"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write it to an output buffer
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|sourceToken
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// Read the token back
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
name|destToken
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|destToken
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checkEqual
argument_list|(
name|sourceToken
argument_list|,
name|destToken
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkUrlSafe (String str)
specifier|private
specifier|static
name|void
name|checkUrlSafe
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|len
init|=
name|str
operator|.
name|length
argument_list|()
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
name|len
condition|;
operator|++
name|i
control|)
block|{
name|char
name|ch
init|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'-'
condition|)
continue|continue;
if|if
condition|(
name|ch
operator|==
literal|'_'
condition|)
continue|continue;
if|if
condition|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
condition|)
continue|continue;
if|if
condition|(
name|ch
operator|>=
literal|'A'
operator|&&
name|ch
operator|<=
literal|'Z'
condition|)
continue|continue;
if|if
condition|(
name|ch
operator|>=
literal|'a'
operator|&&
name|ch
operator|<=
literal|'z'
condition|)
continue|continue;
name|fail
argument_list|(
literal|"Encoded string "
operator|+
name|str
operator|+
literal|" has invalid character at position "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEncodeWritable ()
specifier|public
name|void
name|testEncodeWritable
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"a"
block|,
literal|"bb"
block|,
literal|"ccc"
block|,
literal|"dddd"
block|,
literal|"eeeee"
block|,
literal|"ffffff"
block|,
literal|"ggggggg"
block|,
literal|"hhhhhhhh"
block|,
literal|"iiiiiiiii"
block|,
literal|"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLM"
operator|+
literal|"NOPQRSTUVWXYZ01234567890!@#$%^&*()-=_+[]{}|;':,./<>?"
block|}
decl_stmt|;
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|orig
decl_stmt|;
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|copy
init|=
operator|new
name|Token
argument_list|<>
argument_list|()
decl_stmt|;
comment|// ensure that for each string the input and output values match
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
name|String
name|val
init|=
name|values
index|[
name|i
index|]
decl_stmt|;
name|Token
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Input = {}"
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|orig
operator|=
operator|new
name|Token
argument_list|<>
argument_list|(
name|val
operator|.
name|getBytes
argument_list|()
argument_list|,
name|val
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|val
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|encode
init|=
name|orig
operator|.
name|encodeToUrlString
argument_list|()
decl_stmt|;
name|copy
operator|.
name|decodeFromUrlString
argument_list|(
name|encode
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|orig
argument_list|,
name|copy
argument_list|)
expr_stmt|;
name|checkUrlSafe
argument_list|(
name|encode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Test decodeWritable() with null newValue string argument,    * should throw HadoopIllegalArgumentException.    */
annotation|@
name|Test
DECL|method|testDecodeWritableArgSanityCheck ()
specifier|public
name|void
name|testDecodeWritableArgSanityCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<>
argument_list|()
decl_stmt|;
name|intercept
argument_list|(
name|HadoopIllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|token
operator|.
name|decodeFromUrlString
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDecodeIdentifier ()
specifier|public
name|void
name|testDecodeIdentifier
parameter_list|()
throws|throws
name|IOException
block|{
name|TestDelegationTokenSecretManager
name|secretManager
init|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|secretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|TestDelegationTokenIdentifier
name|id
init|=
operator|new
name|TestDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
literal|"owner"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"renewer"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"realUser"
argument_list|)
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<>
argument_list|(
name|id
argument_list|,
name|secretManager
argument_list|)
decl_stmt|;
name|TokenIdentifier
name|idCopy
init|=
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|id
argument_list|,
name|idCopy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|idCopy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

