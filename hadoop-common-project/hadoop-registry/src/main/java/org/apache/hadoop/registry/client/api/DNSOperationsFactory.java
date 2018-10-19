begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|api
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|registry
operator|.
name|server
operator|.
name|dns
operator|.
name|RegistryDNS
import|;
end_import

begin_comment
comment|/**  * A factory for DNS operation service instances.  */
end_comment

begin_class
DECL|class|DNSOperationsFactory
specifier|public
specifier|final
class|class
name|DNSOperationsFactory
implements|implements
name|RegistryConstants
block|{
comment|/**    * DNS Implementation type.    */
DECL|enum|DNSImplementation
specifier|public
enum|enum
name|DNSImplementation
block|{
DECL|enumConstant|DNSJAVA
name|DNSJAVA
block|}
DECL|method|DNSOperationsFactory ()
specifier|private
name|DNSOperationsFactory
parameter_list|()
block|{   }
comment|/**    * Create and initialize a DNS operations instance.    *    * @param conf configuration    * @return a DNS operations instance    */
DECL|method|createInstance (Configuration conf)
specifier|public
specifier|static
name|DNSOperations
name|createInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|createInstance
argument_list|(
literal|"DNSOperations"
argument_list|,
name|DNSImplementation
operator|.
name|DNSJAVA
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Create and initialize a registry operations instance.    * Access rights will be determined from the configuration.    *    * @param name name of the instance    * @param impl the DNS implementation.    * @param conf configuration    * @return a registry operations instance    */
DECL|method|createInstance (String name, DNSImplementation impl, Configuration conf)
specifier|public
specifier|static
name|DNSOperations
name|createInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|DNSImplementation
name|impl
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|!=
literal|null
argument_list|,
literal|"Null configuration"
argument_list|)
expr_stmt|;
name|DNSOperations
name|operations
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|impl
condition|)
block|{
case|case
name|DNSJAVA
case|:
name|operations
operator|=
operator|new
name|RegistryDNS
argument_list|(
name|name
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s is not available"
argument_list|,
name|impl
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|//operations.init(conf);
return|return
name|operations
return|;
block|}
block|}
end_class

end_unit

