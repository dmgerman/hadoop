begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This package contains all the data types which can be saved to the registry  * and/or marshalled to and from JSON.  *<p>  * The core datatypes, {@link org.apache.hadoop.registry.client.types.ServiceRecord},  * and {@link org.apache.hadoop.registry.client.types.Endpoint} are  * what is used to describe services and their protocol endpoints in the registry.  *<p>  * Some adjacent interfaces exist to list attributes of the fields:  *<ul>  *<li>{@link org.apache.hadoop.registry.client.types.AddressTypes}</li>  *<li>{@link org.apache.hadoop.registry.client.types.yarn.PersistencePolicies}</li>  *<li>{@link org.apache.hadoop.registry.client.types.ProtocolTypes}</li>  *</ul>  *  * The {@link org.apache.hadoop.registry.client.types.RegistryPathStatus}  * class is not saved to the registry âit is the status of a registry  * entry that can be retrieved from the API call. It is still  * designed to be marshalled to and from JSON, as it can be served up  * from REST front ends to the registry.  *  */
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

end_unit

