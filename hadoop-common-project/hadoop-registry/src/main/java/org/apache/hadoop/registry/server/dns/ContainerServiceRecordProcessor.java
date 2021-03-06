begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.server.dns
package|package
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
name|PathNotFoundException
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
name|client
operator|.
name|types
operator|.
name|ServiceRecord
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
name|client
operator|.
name|types
operator|.
name|yarn
operator|.
name|YarnRegistryAttributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|Name
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|TextParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|Type
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
name|List
import|;
end_import

begin_comment
comment|/**  * A processor for generating container DNS records from registry service  * records.  */
end_comment

begin_class
DECL|class|ContainerServiceRecordProcessor
specifier|public
class|class
name|ContainerServiceRecordProcessor
extends|extends
name|BaseServiceRecordProcessor
block|{
comment|/**    * Create a container service record processor.    * @param record the service record    * @param path the service record registry node path    * @param domain the DNS zone/domain name    * @param zoneSelector returns the zone associated with the provided name.    * @throws Exception if an issue is generated during instantiation.    */
DECL|method|ContainerServiceRecordProcessor ( ServiceRecord record, String path, String domain, ZoneSelector zoneSelector)
specifier|public
name|ContainerServiceRecordProcessor
parameter_list|(
name|ServiceRecord
name|record
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|domain
parameter_list|,
name|ZoneSelector
name|zoneSelector
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|record
argument_list|,
name|path
argument_list|,
name|domain
argument_list|,
name|zoneSelector
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the DNS record type to descriptor mapping based on the    * provided service record.    * @param serviceRecord  the registry service record.    * @throws Exception if an issue arises.    */
DECL|method|initTypeToInfoMapping (ServiceRecord serviceRecord)
annotation|@
name|Override
specifier|public
name|void
name|initTypeToInfoMapping
parameter_list|(
name|ServiceRecord
name|serviceRecord
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|serviceRecord
operator|.
name|get
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_IP
argument_list|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|type
range|:
name|getRecordTypes
argument_list|()
control|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Type
operator|.
name|A
case|:
name|createAInfo
argument_list|(
name|serviceRecord
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|AAAA
case|:
name|createAAAAInfo
argument_list|(
name|serviceRecord
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|PTR
case|:
name|createPTRInfo
argument_list|(
name|serviceRecord
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|TXT
case|:
name|createTXTInfo
argument_list|(
name|serviceRecord
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown type "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**    * Create a container TXT record descriptor.    * @param serviceRecord the service record.    * @throws Exception if the descriptor creation yields an issue.    */
DECL|method|createTXTInfo (ServiceRecord serviceRecord)
specifier|protected
name|void
name|createTXTInfo
parameter_list|(
name|ServiceRecord
name|serviceRecord
parameter_list|)
throws|throws
name|Exception
block|{
name|TXTContainerRecordDescriptor
name|txtInfo
init|=
operator|new
name|TXTContainerRecordDescriptor
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|serviceRecord
argument_list|)
decl_stmt|;
name|registerRecordDescriptor
argument_list|(
name|Type
operator|.
name|TXT
argument_list|,
name|txtInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a container PTR record descriptor.    * @param record the service record.    * @throws Exception if the descriptor creation yields an issue.    */
DECL|method|createPTRInfo (ServiceRecord record)
specifier|protected
name|void
name|createPTRInfo
parameter_list|(
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|PTRContainerRecordDescriptor
name|ptrInfo
init|=
operator|new
name|PTRContainerRecordDescriptor
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|record
argument_list|)
decl_stmt|;
name|registerRecordDescriptor
argument_list|(
name|Type
operator|.
name|PTR
argument_list|,
name|ptrInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a container AAAA (IPv6) record descriptor.    * @param record the service record    * @throws Exception if the descriptor creation yields an issue.    */
DECL|method|createAAAAInfo (ServiceRecord record)
specifier|protected
name|void
name|createAAAAInfo
parameter_list|(
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|AAAAContainerRecordDescriptor
name|recordInfo
init|=
operator|new
name|AAAAContainerRecordDescriptor
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|record
argument_list|)
decl_stmt|;
name|registerRecordDescriptor
argument_list|(
name|Type
operator|.
name|AAAA
argument_list|,
name|recordInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a container A (IPv4) record descriptor.    * @param record service record.    * @throws Exception if the descriptor creation yields an issue.    */
DECL|method|createAInfo (ServiceRecord record)
specifier|protected
name|void
name|createAInfo
parameter_list|(
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|AContainerRecordDescriptor
name|recordInfo
init|=
operator|new
name|AContainerRecordDescriptor
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|record
argument_list|)
decl_stmt|;
name|registerRecordDescriptor
argument_list|(
name|Type
operator|.
name|A
argument_list|,
name|recordInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the record types associated with a container service record.    * @return the record type array    */
DECL|method|getRecordTypes ()
annotation|@
name|Override
specifier|public
name|int
index|[]
name|getRecordTypes
parameter_list|()
block|{
return|return
operator|new
name|int
index|[]
block|{
name|Type
operator|.
name|A
block|,
name|Type
operator|.
name|AAAA
block|,
name|Type
operator|.
name|PTR
block|,
name|Type
operator|.
name|TXT
block|}
return|;
block|}
comment|/**    * A container TXT record descriptor.    */
DECL|class|TXTContainerRecordDescriptor
class|class
name|TXTContainerRecordDescriptor
extends|extends
name|ContainerRecordDescriptor
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
block|{
comment|/**      * Creates a container TXT record descriptor.      * @param path registry path for service record      * @param record service record      * @throws Exception      */
DECL|method|TXTContainerRecordDescriptor (String path, ServiceRecord record)
specifier|public
name|TXTContainerRecordDescriptor
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initializes the descriptor parameters.      * @param serviceRecord  the service record.      */
DECL|method|init (ServiceRecord serviceRecord)
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|ServiceRecord
name|serviceRecord
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|setNames
argument_list|(
operator|new
name|Name
index|[]
block|{
name|getContainerName
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TextParseException
name|e
parameter_list|)
block|{
comment|// log
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// log
block|}
name|List
argument_list|<
name|String
argument_list|>
name|txts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|txts
operator|.
name|add
argument_list|(
literal|"id="
operator|+
name|serviceRecord
operator|.
name|get
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_ID
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|setTarget
argument_list|(
name|txts
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A container PTR record descriptor.    */
DECL|class|PTRContainerRecordDescriptor
class|class
name|PTRContainerRecordDescriptor
extends|extends
name|ContainerRecordDescriptor
argument_list|<
name|Name
argument_list|>
block|{
comment|/**      * Creates a container PTR record descriptor.      * @param path registry path for service record      * @param record service record      * @throws Exception      */
DECL|method|PTRContainerRecordDescriptor (String path, ServiceRecord record)
specifier|public
name|PTRContainerRecordDescriptor
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initializes the descriptor parameters.      * @param serviceRecord  the service record.      */
DECL|method|init (ServiceRecord serviceRecord)
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|ServiceRecord
name|serviceRecord
parameter_list|)
block|{
name|String
name|host
init|=
name|serviceRecord
operator|.
name|get
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_HOSTNAME
argument_list|)
decl_stmt|;
name|String
name|ip
init|=
name|serviceRecord
operator|.
name|get
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_IP
argument_list|)
decl_stmt|;
name|Name
name|reverseLookupName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|host
operator|!=
literal|null
operator|&&
name|ip
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reverseLookupName
operator|=
name|reverseIP
argument_list|(
name|ip
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|//LOG
block|}
block|}
name|this
operator|.
name|setNames
argument_list|(
operator|new
name|Name
index|[]
block|{
name|reverseLookupName
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|setTarget
argument_list|(
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TextParseException
name|e
parameter_list|)
block|{
comment|//LOG
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|//LOG
block|}
block|}
block|}
comment|/**    * A container A record descriptor.    */
DECL|class|AContainerRecordDescriptor
class|class
name|AContainerRecordDescriptor
extends|extends
name|ContainerRecordDescriptor
argument_list|<
name|InetAddress
argument_list|>
block|{
comment|/**      * Creates a container A record descriptor.      * @param path registry path for service record      * @param record service record      * @throws Exception      */
DECL|method|AContainerRecordDescriptor (String path, ServiceRecord record)
specifier|public
name|AContainerRecordDescriptor
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initializes the descriptor parameters.      * @param serviceRecord  the service record.      */
DECL|method|init (ServiceRecord serviceRecord)
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|ServiceRecord
name|serviceRecord
parameter_list|)
block|{
name|String
name|ip
init|=
name|serviceRecord
operator|.
name|get
argument_list|(
name|YarnRegistryAttributes
operator|.
name|YARN_IP
argument_list|)
decl_stmt|;
if|if
condition|(
name|ip
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No IP specified"
argument_list|)
throw|;
block|}
try|try
block|{
name|this
operator|.
name|setTarget
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|ip
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNames
argument_list|(
operator|new
name|Name
index|[]
block|{
name|getContainerName
argument_list|()
block|,
name|getContainerIDName
argument_list|()
block|,
name|getComponentName
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * A container AAAA record descriptor.    */
DECL|class|AAAAContainerRecordDescriptor
class|class
name|AAAAContainerRecordDescriptor
extends|extends
name|AContainerRecordDescriptor
block|{
comment|/**      * Creates a container AAAA record descriptor.      * @param path registry path for service record      * @param record service record      * @throws Exception      */
DECL|method|AAAAContainerRecordDescriptor (String path, ServiceRecord record)
specifier|public
name|AAAAContainerRecordDescriptor
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initializes the descriptor parameters.      * @param serviceRecord  the service record.      */
DECL|method|init (ServiceRecord serviceRecord)
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|ServiceRecord
name|serviceRecord
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|serviceRecord
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|setTarget
argument_list|(
name|getIpv6Address
argument_list|(
name|getTarget
argument_list|()
argument_list|)
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
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

