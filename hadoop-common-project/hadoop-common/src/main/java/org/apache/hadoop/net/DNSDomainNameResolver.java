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
comment|/**  * DNSDomainNameResolver takes one domain name and returns all of the IP  * addresses from the underlying DNS service.  */
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
block|}
end_class

end_unit

