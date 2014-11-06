begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.types
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
name|types
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * some common protocol types  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|ProtocolTypes
specifier|public
interface|interface
name|ProtocolTypes
block|{
comment|/**    * Addresses are URIs of Hadoop Filesystem paths: {@value}.    */
DECL|field|PROTOCOL_FILESYSTEM
name|String
name|PROTOCOL_FILESYSTEM
init|=
literal|"hadoop/filesystem"
decl_stmt|;
comment|/**    * Hadoop IPC,  "classic" or protobuf : {@value}.    */
DECL|field|PROTOCOL_HADOOP_IPC
name|String
name|PROTOCOL_HADOOP_IPC
init|=
literal|"hadoop/IPC"
decl_stmt|;
comment|/**    * Corba IIOP: {@value}.    */
DECL|field|PROTOCOL_IIOP
name|String
name|PROTOCOL_IIOP
init|=
literal|"IIOP"
decl_stmt|;
comment|/**    * REST: {@value}.    */
DECL|field|PROTOCOL_REST
name|String
name|PROTOCOL_REST
init|=
literal|"REST"
decl_stmt|;
comment|/**    * Java RMI: {@value}.    */
DECL|field|PROTOCOL_RMI
name|String
name|PROTOCOL_RMI
init|=
literal|"RMI"
decl_stmt|;
comment|/**    * SunOS RPC, as used by NFS and similar: {@value}.    */
DECL|field|PROTOCOL_SUN_RPC
name|String
name|PROTOCOL_SUN_RPC
init|=
literal|"sunrpc"
decl_stmt|;
comment|/**    * Thrift-based protocols: {@value}.    */
DECL|field|PROTOCOL_THRIFT
name|String
name|PROTOCOL_THRIFT
init|=
literal|"thrift"
decl_stmt|;
comment|/**    * Custom TCP protocol: {@value}.    */
DECL|field|PROTOCOL_TCP
name|String
name|PROTOCOL_TCP
init|=
literal|"tcp"
decl_stmt|;
comment|/**    * Custom UPC-based protocol : {@value}.    */
DECL|field|PROTOCOL_UDP
name|String
name|PROTOCOL_UDP
init|=
literal|"udp"
decl_stmt|;
comment|/**    * Default value âthe protocol is unknown : "{@value}"    */
DECL|field|PROTOCOL_UNKNOWN
name|String
name|PROTOCOL_UNKNOWN
init|=
literal|""
decl_stmt|;
comment|/**    * Web page: {@value}.    *    * This protocol implies that the URLs are designed for    * people to view via web browsers.    */
DECL|field|PROTOCOL_WEBUI
name|String
name|PROTOCOL_WEBUI
init|=
literal|"webui"
decl_stmt|;
comment|/**    * Web Services: {@value}.    */
DECL|field|PROTOCOL_WSAPI
name|String
name|PROTOCOL_WSAPI
init|=
literal|"WS-*"
decl_stmt|;
comment|/**    * A zookeeper binding: {@value}.    */
DECL|field|PROTOCOL_ZOOKEEPER_BINDING
name|String
name|PROTOCOL_ZOOKEEPER_BINDING
init|=
literal|"zookeeper"
decl_stmt|;
block|}
end_interface

end_unit

