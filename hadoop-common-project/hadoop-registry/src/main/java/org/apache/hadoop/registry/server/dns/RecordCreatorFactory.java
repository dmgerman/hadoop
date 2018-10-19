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
name|xbill
operator|.
name|DNS
operator|.
name|AAAARecord
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
name|ARecord
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
name|CNAMERecord
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
name|DClass
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
name|PTRRecord
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
name|Record
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
name|SRVRecord
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
name|TXTRecord
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|Type
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A factory for creating DNS records.  */
end_comment

begin_class
DECL|class|RecordCreatorFactory
specifier|public
specifier|final
class|class
name|RecordCreatorFactory
block|{
DECL|field|ttl
specifier|private
specifier|static
name|long
name|ttl
decl_stmt|;
comment|/**    * Private constructor.    */
DECL|method|RecordCreatorFactory ()
specifier|private
name|RecordCreatorFactory
parameter_list|()
block|{   }
comment|/**    * Returns the DNS record creator for the provided type.    *    * @param type the DNS record type.    * @return the record creator.    */
DECL|method|getRecordCreator (int type)
specifier|static
name|RecordCreator
name|getRecordCreator
parameter_list|(
name|int
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|A
case|:
return|return
operator|new
name|ARecordCreator
argument_list|()
return|;
case|case
name|CNAME
case|:
return|return
operator|new
name|CNAMERecordCreator
argument_list|()
return|;
case|case
name|TXT
case|:
return|return
operator|new
name|TXTRecordCreator
argument_list|()
return|;
case|case
name|AAAA
case|:
return|return
operator|new
name|AAAARecordCreator
argument_list|()
return|;
case|case
name|PTR
case|:
return|return
operator|new
name|PTRRecordCreator
argument_list|()
return|;
case|case
name|SRV
case|:
return|return
operator|new
name|SRVRecordCreator
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No type "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/**    * Set the TTL value for the records created by the factory.    *    * @param ttl the ttl value, in seconds.    */
DECL|method|setTtl (long ttl)
specifier|public
specifier|static
name|void
name|setTtl
parameter_list|(
name|long
name|ttl
parameter_list|)
block|{
name|RecordCreatorFactory
operator|.
name|ttl
operator|=
name|ttl
expr_stmt|;
block|}
comment|/**    * A DNS Record creator.    *    * @param<R> the record type    * @param<T> the record's target type    */
DECL|interface|RecordCreator
specifier|public
interface|interface
name|RecordCreator
parameter_list|<
name|R
extends|extends
name|Record
parameter_list|,
name|T
parameter_list|>
block|{
DECL|method|create (Name name, T target)
name|R
name|create
parameter_list|(
name|Name
name|name
parameter_list|,
name|T
name|target
parameter_list|)
function_decl|;
block|}
comment|/**    * An A Record creator.    */
DECL|class|ARecordCreator
specifier|static
class|class
name|ARecordCreator
implements|implements
name|RecordCreator
argument_list|<
name|ARecord
argument_list|,
name|InetAddress
argument_list|>
block|{
comment|/**      * Creates an A record creator.      */
DECL|method|ARecordCreator ()
specifier|public
name|ARecordCreator
parameter_list|()
block|{     }
comment|/**      * Creates a DNS A record.      *      * @param name   the record name.      * @param target the record target/value.      * @return an A record.      */
DECL|method|create (Name name, InetAddress target)
annotation|@
name|Override
specifier|public
name|ARecord
name|create
parameter_list|(
name|Name
name|name
parameter_list|,
name|InetAddress
name|target
parameter_list|)
block|{
return|return
operator|new
name|ARecord
argument_list|(
name|name
argument_list|,
name|DClass
operator|.
name|IN
argument_list|,
name|ttl
argument_list|,
name|target
argument_list|)
return|;
block|}
block|}
comment|/**    * An AAAA Record creator.    */
DECL|class|AAAARecordCreator
specifier|static
class|class
name|AAAARecordCreator
implements|implements
name|RecordCreator
argument_list|<
name|AAAARecord
argument_list|,
name|InetAddress
argument_list|>
block|{
comment|/**      * Creates an AAAA record creator.      */
DECL|method|AAAARecordCreator ()
specifier|public
name|AAAARecordCreator
parameter_list|()
block|{     }
comment|/**      * Creates a DNS AAAA record.      *      * @param name   the record name.      * @param target the record target/value.      * @return an A record.      */
DECL|method|create (Name name, InetAddress target)
annotation|@
name|Override
specifier|public
name|AAAARecord
name|create
parameter_list|(
name|Name
name|name
parameter_list|,
name|InetAddress
name|target
parameter_list|)
block|{
return|return
operator|new
name|AAAARecord
argument_list|(
name|name
argument_list|,
name|DClass
operator|.
name|IN
argument_list|,
name|ttl
argument_list|,
name|target
argument_list|)
return|;
block|}
block|}
DECL|class|CNAMERecordCreator
specifier|static
class|class
name|CNAMERecordCreator
implements|implements
name|RecordCreator
argument_list|<
name|CNAMERecord
argument_list|,
name|Name
argument_list|>
block|{
comment|/**      * Creates a CNAME record creator.      */
DECL|method|CNAMERecordCreator ()
specifier|public
name|CNAMERecordCreator
parameter_list|()
block|{     }
comment|/**      * Creates a DNS CNAME record.      *      * @param name   the record name.      * @param target the record target/value.      * @return an A record.      */
DECL|method|create (Name name, Name target)
annotation|@
name|Override
specifier|public
name|CNAMERecord
name|create
parameter_list|(
name|Name
name|name
parameter_list|,
name|Name
name|target
parameter_list|)
block|{
return|return
operator|new
name|CNAMERecord
argument_list|(
name|name
argument_list|,
name|DClass
operator|.
name|IN
argument_list|,
name|ttl
argument_list|,
name|target
argument_list|)
return|;
block|}
block|}
comment|/**    * A TXT Record creator.    */
DECL|class|TXTRecordCreator
specifier|static
class|class
name|TXTRecordCreator
implements|implements
name|RecordCreator
argument_list|<
name|TXTRecord
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
block|{
comment|/**      * Creates a TXT record creator.      */
DECL|method|TXTRecordCreator ()
specifier|public
name|TXTRecordCreator
parameter_list|()
block|{     }
comment|/**      * Creates a DNS TXT record.      *      * @param name   the record name.      * @param target the record target/value.      * @return an A record.      */
DECL|method|create (Name name, List<String> target)
annotation|@
name|Override
specifier|public
name|TXTRecord
name|create
parameter_list|(
name|Name
name|name
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|target
parameter_list|)
block|{
return|return
operator|new
name|TXTRecord
argument_list|(
name|name
argument_list|,
name|DClass
operator|.
name|IN
argument_list|,
name|ttl
argument_list|,
name|target
argument_list|)
return|;
block|}
block|}
comment|/**    * A PTR Record creator.    */
DECL|class|PTRRecordCreator
specifier|static
class|class
name|PTRRecordCreator
implements|implements
name|RecordCreator
argument_list|<
name|PTRRecord
argument_list|,
name|Name
argument_list|>
block|{
comment|/**      * Creates a PTR record creator.      */
DECL|method|PTRRecordCreator ()
specifier|public
name|PTRRecordCreator
parameter_list|()
block|{     }
comment|/**      * Creates a DNS PTR record.      *      * @param name   the record name.      * @param target the record target/value.      * @return an A record.      */
DECL|method|create (Name name, Name target)
annotation|@
name|Override
specifier|public
name|PTRRecord
name|create
parameter_list|(
name|Name
name|name
parameter_list|,
name|Name
name|target
parameter_list|)
block|{
return|return
operator|new
name|PTRRecord
argument_list|(
name|name
argument_list|,
name|DClass
operator|.
name|IN
argument_list|,
name|ttl
argument_list|,
name|target
argument_list|)
return|;
block|}
block|}
comment|/**    * A SRV Record creator.    */
DECL|class|SRVRecordCreator
specifier|static
class|class
name|SRVRecordCreator
implements|implements
name|RecordCreator
argument_list|<
name|SRVRecord
argument_list|,
name|HostPortInfo
argument_list|>
block|{
comment|/**      * Creates a SRV record creator.      */
DECL|method|SRVRecordCreator ()
specifier|public
name|SRVRecordCreator
parameter_list|()
block|{     }
comment|/**      * Creates a DNS SRV record.      *      * @param name   the record name.      * @param target the record target/value.      * @return an A record.      */
DECL|method|create (Name name, HostPortInfo target)
annotation|@
name|Override
specifier|public
name|SRVRecord
name|create
parameter_list|(
name|Name
name|name
parameter_list|,
name|HostPortInfo
name|target
parameter_list|)
block|{
return|return
operator|new
name|SRVRecord
argument_list|(
name|name
argument_list|,
name|DClass
operator|.
name|IN
argument_list|,
name|ttl
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|target
operator|.
name|getPort
argument_list|()
argument_list|,
name|target
operator|.
name|getHost
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * An object for storing the host and port info used to generate SRV records.    */
DECL|class|HostPortInfo
specifier|public
specifier|static
class|class
name|HostPortInfo
block|{
DECL|field|host
specifier|private
name|Name
name|host
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
comment|/**      * Creates an object with a host and port pair.      *      * @param host the hostname/ip      * @param port the port value      */
DECL|method|HostPortInfo (Name host, int port)
specifier|public
name|HostPortInfo
parameter_list|(
name|Name
name|host
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|this
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the host name.      * @return the host name.      */
DECL|method|getHost ()
name|Name
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
comment|/**      * Set the host name.      * @param host the host name.      */
DECL|method|setHost (Name host)
name|void
name|setHost
parameter_list|(
name|Name
name|host
parameter_list|)
block|{
name|this
operator|.
name|host
operator|=
name|host
expr_stmt|;
block|}
comment|/**      * Get the port.      * @return the port.      */
DECL|method|getPort ()
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
comment|/**      * Set the port.      * @param port the port.      */
DECL|method|setPort (int port)
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

