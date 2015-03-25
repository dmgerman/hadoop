begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
operator|.
name|HttpServer2
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
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
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
name|http
operator|.
name|FilterContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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

begin_class
DECL|class|TestAuthenticationFilter
specifier|public
class|class
name|TestAuthenticationFilter
extends|extends
name|TestCase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testConfiguration ()
specifier|public
name|void
name|testConfiguration
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
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.http.authentication.foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HttpServer2
operator|.
name|BIND_ADDRESS
argument_list|,
literal|"barhost"
argument_list|)
expr_stmt|;
name|FilterContainer
name|container
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FilterContainer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocationOnMock
parameter_list|)
throws|throws
name|Throwable
block|{
name|Object
index|[]
name|args
init|=
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"authentication"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AuthenticationFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|args
index|[
literal|2
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"cookie.path"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"simple"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"36000"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"token.validity"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"cookie.domain"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"simple.anonymous.allowed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"HTTP/barhost@LOCALHOST"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"kerberos.principal"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
operator|+
literal|"/hadoop.keytab"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"kerberos.keytab"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|container
argument_list|)
operator|.
name|addFilter
argument_list|(
name|Mockito
operator|.
expr|<
name|String
operator|>
name|anyObject
argument_list|()
argument_list|,
name|Mockito
operator|.
expr|<
name|String
operator|>
name|anyObject
argument_list|()
argument_list|,
name|Mockito
operator|.
expr|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|>
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|AuthenticationFilterInitializer
argument_list|()
operator|.
name|initFilter
argument_list|(
name|container
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

