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

begin_comment
comment|/**  * DNSDomainNameResolver wraps up the default DNS service for forward/reverse  * DNS lookup. It also provides a function to resolve a host name to all of  * fully qualified domain names belonging to the IPs from this host name  */
end_comment

begin_class
DECL|class|DNSDomainNameResolver
specifier|public
class|class
name|DNSDomainNameResolver
implements|implements
name|DomainNameResolver
block|{
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
return|return
name|InetAddress
operator|.
name|getAllByName
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
name|String
name|host
init|=
name|address
operator|.
name|getCanonicalHostName
argument_list|()
decl_stmt|;
if|if
condition|(
name|host
operator|!=
literal|null
operator|&&
name|host
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
name|host
operator|.
name|charAt
argument_list|(
name|host
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'.'
condition|)
block|{
name|host
operator|=
name|host
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|host
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|host
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
name|addresses
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
name|getHostnameByIP
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
name|addresses
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
block|}
end_class

end_unit

