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
comment|/**  * This interface provides methods for the failover proxy to get IP addresses  * of the associated servers (NameNodes, RBF routers etc). Implementations will  * use their own service discovery mechanism, DNS, Zookeeper etc  */
end_comment

begin_interface
DECL|interface|DomainNameResolver
specifier|public
interface|interface
name|DomainNameResolver
block|{
comment|/**    * Takes one domain name and returns its IP addresses based on the actual    * service discovery methods.    *    * @param domainName    * @return all IP addresses    * @throws UnknownHostException    */
DECL|method|getAllByDomainName (String domainName)
name|InetAddress
index|[]
name|getAllByDomainName
parameter_list|(
name|String
name|domainName
parameter_list|)
throws|throws
name|UnknownHostException
function_decl|;
block|}
end_interface

end_unit

