begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
comment|/**  * This class mainly test the MockDomainNameResolver comes working as expected.  */
end_comment

begin_class
DECL|class|TestMockDomainNameResolver
specifier|public
class|class
name|TestMockDomainNameResolver
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Rule
DECL|field|exception
specifier|public
specifier|final
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
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
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_DOMAINNAME_RESOLVER_IMPL
argument_list|,
name|MockDomainNameResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMockDomainNameResolverCanBeCreated ()
specifier|public
name|void
name|testMockDomainNameResolverCanBeCreated
parameter_list|()
throws|throws
name|IOException
block|{
name|DomainNameResolver
name|resolver
init|=
name|DomainNameResolverFactory
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HADOOP_DOMAINNAME_RESOLVER_IMPL
argument_list|)
decl_stmt|;
name|InetAddress
index|[]
name|addrs
init|=
name|resolver
operator|.
name|getAllByDomainName
argument_list|(
name|MockDomainNameResolver
operator|.
name|DOMAIN
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|addrs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MockDomainNameResolver
operator|.
name|ADDR_1
argument_list|,
name|addrs
index|[
literal|0
index|]
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MockDomainNameResolver
operator|.
name|ADDR_2
argument_list|,
name|addrs
index|[
literal|1
index|]
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMockDomainNameResolverCanNotBeCreated ()
specifier|public
name|void
name|testMockDomainNameResolverCanNotBeCreated
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|DomainNameResolver
name|resolver
init|=
name|DomainNameResolverFactory
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HADOOP_DOMAINNAME_RESOLVER_IMPL
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|UnknownHostException
operator|.
name|class
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|getAllByDomainName
argument_list|(
name|MockDomainNameResolver
operator|.
name|UNKNOW_DOMAIN
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

