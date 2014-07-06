begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderDelegationTokenExtension
operator|.
name|DelegationTokenExtension
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
name|Text
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
name|Credentials
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
name|Token
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

begin_class
DECL|class|TestKeyProviderDelegationTokenExtension
specifier|public
class|class
name|TestKeyProviderDelegationTokenExtension
block|{
DECL|class|MockKeyProvider
specifier|public
specifier|static
specifier|abstract
class|class
name|MockKeyProvider
extends|extends
name|KeyProvider
implements|implements
name|DelegationTokenExtension
block|{   }
annotation|@
name|Test
DECL|method|testCreateExtension ()
specifier|public
name|void
name|testCreateExtension
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|KeyProvider
name|kp
init|=
operator|new
name|UserProvider
operator|.
name|Factory
argument_list|()
operator|.
name|createProvider
argument_list|(
operator|new
name|URI
argument_list|(
literal|"user:///"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|KeyProviderDelegationTokenExtension
name|kpDTE1
init|=
name|KeyProviderDelegationTokenExtension
operator|.
name|createKeyProviderDelegationTokenExtension
argument_list|(
name|kp
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|kpDTE1
argument_list|)
expr_stmt|;
comment|// Default implementation should be a no-op and return null
name|Assert
operator|.
name|assertNull
argument_list|(
name|kpDTE1
operator|.
name|addDelegationTokens
argument_list|(
literal|"user"
argument_list|,
name|credentials
argument_list|)
argument_list|)
expr_stmt|;
name|MockKeyProvider
name|mock
init|=
name|mock
argument_list|(
name|MockKeyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mock
operator|.
name|addDelegationTokens
argument_list|(
literal|"renewer"
argument_list|,
name|credentials
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Token
argument_list|<
name|?
argument_list|>
index|[]
block|{
operator|new
name|Token
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|Text
argument_list|(
literal|"kind"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"service"
argument_list|)
argument_list|)
block|}
block|)
function|;
name|KeyProviderDelegationTokenExtension
name|kpDTE2
init|=
name|KeyProviderDelegationTokenExtension
operator|.
name|createKeyProviderDelegationTokenExtension
argument_list|(
name|mock
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|tokens
init|=
name|kpDTE2
operator|.
name|addDelegationTokens
argument_list|(
literal|"renewer"
argument_list|,
name|credentials
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
parameter_list|(
name|tokens
parameter_list|)
constructor_decl|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"kind"
argument_list|,
name|tokens
index|[
literal|0
index|]
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_class

unit|}
end_unit

