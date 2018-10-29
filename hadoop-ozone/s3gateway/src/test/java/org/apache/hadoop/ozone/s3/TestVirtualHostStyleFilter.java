begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
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
name|fs
operator|.
name|InvalidRequestException
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
name|conf
operator|.
name|OzoneConfiguration
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
name|s3
operator|.
name|header
operator|.
name|AuthenticationHeaderParser
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|glassfish
operator|.
name|jersey
operator|.
name|internal
operator|.
name|PropertiesDelegate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|glassfish
operator|.
name|jersey
operator|.
name|server
operator|.
name|ContainerRequest
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
name|Before
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|SecurityContext
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * This class test virtual host style mapping conversion to path style.  */
end_comment

begin_class
DECL|class|TestVirtualHostStyleFilter
specifier|public
class|class
name|TestVirtualHostStyleFilter
block|{
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|s3HttpAddr
specifier|private
specifier|static
name|String
name|s3HttpAddr
decl_stmt|;
DECL|field|authenticationHeaderParser
specifier|private
name|AuthenticationHeaderParser
name|authenticationHeaderParser
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|s3HttpAddr
operator|=
literal|"localhost:9878"
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|S3GatewayConfigKeys
operator|.
name|OZONE_S3G_HTTP_ADDRESS_KEY
argument_list|,
name|s3HttpAddr
argument_list|)
expr_stmt|;
name|s3HttpAddr
operator|=
name|s3HttpAddr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s3HttpAddr
operator|.
name|lastIndexOf
argument_list|(
literal|":"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|S3GatewayConfigKeys
operator|.
name|OZONE_S3G_DOMAIN_NAME
argument_list|,
name|s3HttpAddr
argument_list|)
expr_stmt|;
name|authenticationHeaderParser
operator|=
operator|new
name|AuthenticationHeaderParser
argument_list|()
expr_stmt|;
name|authenticationHeaderParser
operator|.
name|setAuthHeader
argument_list|(
literal|"AWS ozone:scret"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create containerRequest object.    * @return ContainerRequest    * @throws Exception    */
DECL|method|createContainerRequest (String host, String path, String queryParams, boolean virtualHostStyle)
specifier|public
name|ContainerRequest
name|createContainerRequest
parameter_list|(
name|String
name|host
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|queryParams
parameter_list|,
name|boolean
name|virtualHostStyle
parameter_list|)
throws|throws
name|Exception
block|{
name|URI
name|baseUri
init|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
argument_list|)
decl_stmt|;
name|URI
name|virtualHostStyleUri
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
operator|&&
name|queryParams
operator|==
literal|null
condition|)
block|{
name|virtualHostStyleUri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|queryParams
operator|==
literal|null
condition|)
block|{
name|virtualHostStyleUri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|queryParams
operator|!=
literal|null
condition|)
block|{
name|virtualHostStyleUri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
name|path
operator|+
name|queryParams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|virtualHostStyleUri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
name|queryParams
argument_list|)
expr_stmt|;
block|}
name|URI
name|pathStyleUri
decl_stmt|;
if|if
condition|(
name|queryParams
operator|==
literal|null
condition|)
block|{
name|pathStyleUri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pathStyleUri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
name|path
operator|+
name|queryParams
argument_list|)
expr_stmt|;
block|}
name|String
name|httpMethod
init|=
literal|"DELETE"
decl_stmt|;
name|SecurityContext
name|securityContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|SecurityContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|PropertiesDelegate
name|propertiesDelegate
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|PropertiesDelegate
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerRequest
name|containerRequest
decl_stmt|;
if|if
condition|(
name|virtualHostStyle
condition|)
block|{
name|containerRequest
operator|=
operator|new
name|ContainerRequest
argument_list|(
name|baseUri
argument_list|,
name|virtualHostStyleUri
argument_list|,
name|httpMethod
argument_list|,
name|securityContext
argument_list|,
name|propertiesDelegate
argument_list|)
expr_stmt|;
name|containerRequest
operator|.
name|header
argument_list|(
name|HttpHeaders
operator|.
name|HOST
argument_list|,
name|host
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|containerRequest
operator|=
operator|new
name|ContainerRequest
argument_list|(
name|baseUri
argument_list|,
name|pathStyleUri
argument_list|,
name|httpMethod
argument_list|,
name|securityContext
argument_list|,
name|propertiesDelegate
argument_list|)
expr_stmt|;
name|containerRequest
operator|.
name|header
argument_list|(
name|HttpHeaders
operator|.
name|HOST
argument_list|,
name|host
argument_list|)
expr_stmt|;
block|}
return|return
name|containerRequest
return|;
block|}
annotation|@
name|Test
DECL|method|testVirtualHostStyle ()
specifier|public
name|void
name|testVirtualHostStyle
parameter_list|()
throws|throws
name|Exception
block|{
name|VirtualHostStyleFilter
name|virtualHostStyleFilter
init|=
operator|new
name|VirtualHostStyleFilter
argument_list|()
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|virtualHostStyleFilter
operator|.
name|setAuthenticationHeaderParser
argument_list|(
name|authenticationHeaderParser
argument_list|)
expr_stmt|;
name|ContainerRequest
name|containerRequest
init|=
name|createContainerRequest
argument_list|(
literal|"mybucket"
operator|+
literal|".localhost:9878"
argument_list|,
literal|"/myfile"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|filter
argument_list|(
name|containerRequest
argument_list|)
expr_stmt|;
name|URI
name|expected
init|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
literal|"/mybucket/myfile"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|containerRequest
operator|.
name|getRequestUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPathStyle ()
specifier|public
name|void
name|testPathStyle
parameter_list|()
throws|throws
name|Exception
block|{
name|VirtualHostStyleFilter
name|virtualHostStyleFilter
init|=
operator|new
name|VirtualHostStyleFilter
argument_list|()
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|virtualHostStyleFilter
operator|.
name|setAuthenticationHeaderParser
argument_list|(
name|authenticationHeaderParser
argument_list|)
expr_stmt|;
name|ContainerRequest
name|containerRequest
init|=
name|createContainerRequest
argument_list|(
name|s3HttpAddr
argument_list|,
literal|"/mybucket/myfile"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|filter
argument_list|(
name|containerRequest
argument_list|)
expr_stmt|;
name|URI
name|expected
init|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
literal|"/mybucket/myfile"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|containerRequest
operator|.
name|getRequestUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVirtualHostStyleWithCreateBucketRequest ()
specifier|public
name|void
name|testVirtualHostStyleWithCreateBucketRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|VirtualHostStyleFilter
name|virtualHostStyleFilter
init|=
operator|new
name|VirtualHostStyleFilter
argument_list|()
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|virtualHostStyleFilter
operator|.
name|setAuthenticationHeaderParser
argument_list|(
name|authenticationHeaderParser
argument_list|)
expr_stmt|;
name|ContainerRequest
name|containerRequest
init|=
name|createContainerRequest
argument_list|(
literal|"mybucket"
operator|+
literal|".localhost:9878"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|filter
argument_list|(
name|containerRequest
argument_list|)
expr_stmt|;
name|URI
name|expected
init|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
literal|"/mybucket"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|containerRequest
operator|.
name|getRequestUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVirtualHostStyleWithQueryParams ()
specifier|public
name|void
name|testVirtualHostStyleWithQueryParams
parameter_list|()
throws|throws
name|Exception
block|{
name|VirtualHostStyleFilter
name|virtualHostStyleFilter
init|=
operator|new
name|VirtualHostStyleFilter
argument_list|()
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|virtualHostStyleFilter
operator|.
name|setAuthenticationHeaderParser
argument_list|(
name|authenticationHeaderParser
argument_list|)
expr_stmt|;
name|ContainerRequest
name|containerRequest
init|=
name|createContainerRequest
argument_list|(
literal|"mybucket"
operator|+
literal|".localhost:9878"
argument_list|,
literal|null
argument_list|,
literal|"?prefix=bh"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|filter
argument_list|(
name|containerRequest
argument_list|)
expr_stmt|;
name|URI
name|expected
init|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
literal|"/mybucket?prefix=bh"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|containerRequest
operator|.
name|getRequestUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|containerRequest
operator|=
name|createContainerRequest
argument_list|(
literal|"mybucket"
operator|+
literal|".localhost:9878"
argument_list|,
literal|null
argument_list|,
literal|"?prefix=bh&type=dir"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|virtualHostStyleFilter
operator|.
name|filter
argument_list|(
name|containerRequest
argument_list|)
expr_stmt|;
name|expected
operator|=
operator|new
name|URI
argument_list|(
literal|"http://"
operator|+
name|s3HttpAddr
operator|+
literal|"/mybucket?prefix=bh&type=dir"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|containerRequest
operator|.
name|getRequestUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVirtualHostStyleWithNoMatchingDomain ()
specifier|public
name|void
name|testVirtualHostStyleWithNoMatchingDomain
parameter_list|()
throws|throws
name|Exception
block|{
name|VirtualHostStyleFilter
name|virtualHostStyleFilter
init|=
operator|new
name|VirtualHostStyleFilter
argument_list|()
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|virtualHostStyleFilter
operator|.
name|setAuthenticationHeaderParser
argument_list|(
name|authenticationHeaderParser
argument_list|)
expr_stmt|;
name|ContainerRequest
name|containerRequest
init|=
name|createContainerRequest
argument_list|(
literal|"mybucket"
operator|+
literal|".myhost:9999"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|virtualHostStyleFilter
operator|.
name|filter
argument_list|(
name|containerRequest
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testVirtualHostStyleWithNoMatchingDomain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidRequestException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"No matching domain"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testIncorrectVirtualHostStyle ()
specifier|public
name|void
name|testIncorrectVirtualHostStyle
parameter_list|()
throws|throws
name|Exception
block|{
name|VirtualHostStyleFilter
name|virtualHostStyleFilter
init|=
operator|new
name|VirtualHostStyleFilter
argument_list|()
decl_stmt|;
name|virtualHostStyleFilter
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|virtualHostStyleFilter
operator|.
name|setAuthenticationHeaderParser
argument_list|(
name|authenticationHeaderParser
argument_list|)
expr_stmt|;
name|ContainerRequest
name|containerRequest
init|=
name|createContainerRequest
argument_list|(
literal|"mybucket"
operator|+
literal|"localhost:9878"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|virtualHostStyleFilter
operator|.
name|filter
argument_list|(
name|containerRequest
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testIncorrectVirtualHostStyle failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidRequestException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"invalid format"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

