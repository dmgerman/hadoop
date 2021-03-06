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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * This mock resolver class returns the predefined resolving/reverse lookup  * results. By default it uses a default "test.foo.bar" domain with two  * IP addresses.  */
end_comment

begin_class
DECL|class|MockDomainNameResolver
specifier|public
class|class
name|MockDomainNameResolver
implements|implements
name|DomainNameResolver
block|{
DECL|field|DOMAIN
specifier|public
specifier|static
specifier|final
name|String
name|DOMAIN
init|=
literal|"test.foo.bar"
decl_stmt|;
comment|// This host will be used to mock non-resolvable host
DECL|field|UNKNOW_DOMAIN
specifier|public
specifier|static
specifier|final
name|String
name|UNKNOW_DOMAIN
init|=
literal|"unknown.foo.bar"
decl_stmt|;
DECL|field|BYTE_ADDR_1
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|BYTE_ADDR_1
init|=
operator|new
name|byte
index|[]
block|{
literal|10
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
DECL|field|BYTE_ADDR_2
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|BYTE_ADDR_2
init|=
operator|new
name|byte
index|[]
block|{
literal|10
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|}
decl_stmt|;
DECL|field|ADDR_1
specifier|public
specifier|static
specifier|final
name|String
name|ADDR_1
init|=
literal|"10.1.1.1"
decl_stmt|;
DECL|field|ADDR_2
specifier|public
specifier|static
specifier|final
name|String
name|ADDR_2
init|=
literal|"10.1.1.2"
decl_stmt|;
DECL|field|FQDN_1
specifier|public
specifier|static
specifier|final
name|String
name|FQDN_1
init|=
literal|"host01.com"
decl_stmt|;
DECL|field|FQDN_2
specifier|public
specifier|static
specifier|final
name|String
name|FQDN_2
init|=
literal|"host02.com"
decl_stmt|;
comment|/** Internal mapping of domain names and IP addresses. */
DECL|field|addrs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|InetAddress
index|[]
argument_list|>
name|addrs
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Internal mapping from IP addresses to fqdns. */
DECL|field|ptrMap
specifier|private
name|Map
argument_list|<
name|InetAddress
argument_list|,
name|String
argument_list|>
name|ptrMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MockDomainNameResolver ()
specifier|public
name|MockDomainNameResolver
parameter_list|()
block|{
try|try
block|{
name|InetAddress
name|nn1Address
init|=
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|BYTE_ADDR_1
argument_list|)
decl_stmt|;
name|InetAddress
name|nn2Address
init|=
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|BYTE_ADDR_2
argument_list|)
decl_stmt|;
name|addrs
operator|.
name|put
argument_list|(
name|DOMAIN
argument_list|,
operator|new
name|InetAddress
index|[]
block|{
name|nn1Address
block|,
name|nn2Address
block|}
argument_list|)
expr_stmt|;
name|ptrMap
operator|.
name|put
argument_list|(
name|nn1Address
argument_list|,
name|FQDN_1
argument_list|)
expr_stmt|;
name|ptrMap
operator|.
name|put
argument_list|(
name|nn2Address
argument_list|,
name|FQDN_2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAllByDomainName (String domainName)
specifier|public
name|InetAddress
index|[]
name|getAllByDomainName
parameter_list|(
name|String
name|domainName
parameter_list|)
throws|throws
name|UnknownHostException
block|{
if|if
condition|(
operator|!
name|addrs
operator|.
name|containsKey
argument_list|(
name|domainName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnknownHostException
argument_list|(
name|domainName
operator|+
literal|" is not resolvable"
argument_list|)
throw|;
block|}
return|return
name|addrs
operator|.
name|get
argument_list|(
name|domainName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHostnameByIP (InetAddress address)
specifier|public
name|String
name|getHostnameByIP
parameter_list|(
name|InetAddress
name|address
parameter_list|)
block|{
return|return
name|ptrMap
operator|.
name|containsKey
argument_list|(
name|address
argument_list|)
condition|?
name|ptrMap
operator|.
name|get
argument_list|(
name|address
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getAllResolvedHostnameByDomainName ( String domainName, boolean useFQDN)
specifier|public
name|String
index|[]
name|getAllResolvedHostnameByDomainName
parameter_list|(
name|String
name|domainName
parameter_list|,
name|boolean
name|useFQDN
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|InetAddress
index|[]
name|addresses
init|=
name|getAllByDomainName
argument_list|(
name|domainName
argument_list|)
decl_stmt|;
name|String
index|[]
name|hosts
init|=
operator|new
name|String
index|[
name|addresses
operator|.
name|length
index|]
decl_stmt|;
if|if
condition|(
name|useFQDN
condition|)
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
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hosts
index|[
name|i
index|]
operator|=
name|this
operator|.
name|ptrMap
operator|.
name|get
argument_list|(
name|addresses
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hosts
index|[
name|i
index|]
operator|=
name|addresses
index|[
name|i
index|]
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|hosts
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setAddressMap (Map<String, InetAddress[]> addresses)
specifier|public
name|void
name|setAddressMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|InetAddress
index|[]
argument_list|>
name|addresses
parameter_list|)
block|{
name|this
operator|.
name|addrs
operator|=
name|addresses
expr_stmt|;
block|}
block|}
end_class

end_unit

