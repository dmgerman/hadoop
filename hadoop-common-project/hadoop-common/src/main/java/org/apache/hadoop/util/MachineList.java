begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|net
operator|.
name|util
operator|.
name|SubnetUtils
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|net
operator|.
name|InetAddresses
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Container class which holds a list of ip/host addresses and   * answers membership queries.  *  * Accepts list of ip addresses, ip addreses in CIDR format and/or   * host addresses.  */
end_comment

begin_class
DECL|class|MachineList
specifier|public
class|class
name|MachineList
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MachineList
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|WILDCARD_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|WILDCARD_VALUE
init|=
literal|"*"
decl_stmt|;
comment|/**    * InetAddressFactory is used to obtain InetAddress from host.    * This class makes it easy to simulate host to ip mappings during testing.    *    */
DECL|class|InetAddressFactory
specifier|public
specifier|static
class|class
name|InetAddressFactory
block|{
DECL|field|S_INSTANCE
specifier|static
specifier|final
name|InetAddressFactory
name|S_INSTANCE
init|=
operator|new
name|InetAddressFactory
argument_list|()
decl_stmt|;
DECL|method|getByName (String host)
specifier|public
name|InetAddress
name|getByName
parameter_list|(
name|String
name|host
parameter_list|)
throws|throws
name|UnknownHostException
block|{
return|return
name|InetAddress
operator|.
name|getByName
argument_list|(
name|host
argument_list|)
return|;
block|}
block|}
DECL|field|all
specifier|private
specifier|final
name|boolean
name|all
decl_stmt|;
DECL|field|ipAddresses
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ipAddresses
decl_stmt|;
DECL|field|cidrAddresses
specifier|private
specifier|final
name|List
argument_list|<
name|SubnetUtils
operator|.
name|SubnetInfo
argument_list|>
name|cidrAddresses
decl_stmt|;
DECL|field|hostNames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|hostNames
decl_stmt|;
DECL|field|addressFactory
specifier|private
specifier|final
name|InetAddressFactory
name|addressFactory
decl_stmt|;
comment|/**    *     * @param hostEntries comma separated ip/cidr/host addresses    */
DECL|method|MachineList (String hostEntries)
specifier|public
name|MachineList
parameter_list|(
name|String
name|hostEntries
parameter_list|)
block|{
name|this
argument_list|(
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|hostEntries
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * @param hostEntries collection of separated ip/cidr/host addresses    */
DECL|method|MachineList (Collection<String> hostEntries)
specifier|public
name|MachineList
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|hostEntries
parameter_list|)
block|{
name|this
argument_list|(
name|hostEntries
argument_list|,
name|InetAddressFactory
operator|.
name|S_INSTANCE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Accepts a collection of ip/cidr/host addresses    *     * @param hostEntries    * @param addressFactory addressFactory to convert host to InetAddress    */
DECL|method|MachineList (Collection<String> hostEntries, InetAddressFactory addressFactory)
specifier|public
name|MachineList
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|hostEntries
parameter_list|,
name|InetAddressFactory
name|addressFactory
parameter_list|)
block|{
name|this
operator|.
name|addressFactory
operator|=
name|addressFactory
expr_stmt|;
if|if
condition|(
name|hostEntries
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
name|hostEntries
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
operator|&&
operator|(
name|hostEntries
operator|.
name|contains
argument_list|(
name|WILDCARD_VALUE
argument_list|)
operator|)
condition|)
block|{
name|all
operator|=
literal|true
expr_stmt|;
name|ipAddresses
operator|=
literal|null
expr_stmt|;
name|hostNames
operator|=
literal|null
expr_stmt|;
name|cidrAddresses
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|all
operator|=
literal|false
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ips
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SubnetUtils
operator|.
name|SubnetInfo
argument_list|>
name|cidrs
init|=
operator|new
name|LinkedList
argument_list|<
name|SubnetUtils
operator|.
name|SubnetInfo
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|hosts
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|hostEntry
range|:
name|hostEntries
control|)
block|{
comment|//ip address range
if|if
condition|(
name|hostEntry
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
try|try
block|{
name|SubnetUtils
name|subnet
init|=
operator|new
name|SubnetUtils
argument_list|(
name|hostEntry
argument_list|)
decl_stmt|;
name|subnet
operator|.
name|setInclusiveHostCount
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cidrs
operator|.
name|add
argument_list|(
name|subnet
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid CIDR syntax : "
operator|+
name|hostEntry
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|InetAddresses
operator|.
name|isInetAddress
argument_list|(
name|hostEntry
argument_list|)
condition|)
block|{
comment|//ip address
name|ips
operator|.
name|add
argument_list|(
name|hostEntry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//hostname
name|hosts
operator|.
name|add
argument_list|(
name|hostEntry
argument_list|)
expr_stmt|;
block|}
block|}
name|ipAddresses
operator|=
operator|(
name|ips
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|ips
else|:
literal|null
expr_stmt|;
name|cidrAddresses
operator|=
operator|(
name|cidrs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|cidrs
else|:
literal|null
expr_stmt|;
name|hostNames
operator|=
operator|(
name|hosts
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|hosts
else|:
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|all
operator|=
literal|false
expr_stmt|;
name|ipAddresses
operator|=
literal|null
expr_stmt|;
name|hostNames
operator|=
literal|null
expr_stmt|;
name|cidrAddresses
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Accepts an ip address and return true if ipAddress is in the list    * @param ipAddress    * @return true if ipAddress is part of the list    */
DECL|method|includes (String ipAddress)
specifier|public
name|boolean
name|includes
parameter_list|(
name|String
name|ipAddress
parameter_list|)
block|{
if|if
condition|(
name|all
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|ipAddress
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ipAddress is null."
argument_list|)
throw|;
block|}
comment|//check in the set of ipAddresses
if|if
condition|(
operator|(
name|ipAddresses
operator|!=
literal|null
operator|)
operator|&&
name|ipAddresses
operator|.
name|contains
argument_list|(
name|ipAddress
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|//iterate through the ip ranges for inclusion
if|if
condition|(
name|cidrAddresses
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SubnetUtils
operator|.
name|SubnetInfo
name|cidrAddress
range|:
name|cidrAddresses
control|)
block|{
if|if
condition|(
name|cidrAddress
operator|.
name|isInRange
argument_list|(
name|ipAddress
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
comment|//check if the ipAddress matches one of hostnames
if|if
condition|(
name|hostNames
operator|!=
literal|null
condition|)
block|{
comment|//convert given ipAddress to hostname and look for a match
name|InetAddress
name|hostAddr
decl_stmt|;
try|try
block|{
name|hostAddr
operator|=
name|addressFactory
operator|.
name|getByName
argument_list|(
name|ipAddress
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|hostAddr
operator|!=
literal|null
operator|)
operator|&&
name|hostNames
operator|.
name|contains
argument_list|(
name|hostAddr
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|//ignore the exception and proceed to resolve the list of hosts
block|}
comment|//loop through host addresses and convert them to ip and look for a match
for|for
control|(
name|String
name|host
range|:
name|hostNames
control|)
block|{
try|try
block|{
name|hostAddr
operator|=
name|addressFactory
operator|.
name|getByName
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
continue|continue;
block|}
if|if
condition|(
name|hostAddr
operator|.
name|getHostAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|ipAddress
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * returns the contents of the MachineList as a Collection<String>    * This can be used for testing     * @return contents of the MachineList    */
annotation|@
name|VisibleForTesting
DECL|method|getCollection ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getCollection
parameter_list|()
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|all
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|ipAddresses
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|ipAddresses
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hostNames
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|hostNames
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cidrAddresses
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SubnetUtils
operator|.
name|SubnetInfo
name|cidrAddress
range|:
name|cidrAddresses
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|cidrAddress
operator|.
name|getCidrSignature
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

