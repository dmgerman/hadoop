begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * The federation state store tracks persistent values that are shared between  * multiple routers.  *<p>  * Data is stored in data records that inherit from a common class. Data records  * are serialized when written to the data store using a modular serialization  * implementation. The default is profobuf serialization. Data is stored as rows  * of records of the same type with each data member in a record representing a  * column.  *<p>  * The state store uses a modular data storage  * {@link org.apache.hadoop.hdfs.server.federation.store.driver.StateStoreDriver  * StateStoreDriver} to handle querying, updating and deleting data records. The  * data storage driver is initialized and maintained by the {@link  * org.apache.hadoop.hdfs.server.federation.store.StateStoreService  * FederationStateStoreService}. The state store  * supports fetching all records of a type, filtering by column values or  * fetching a single record by its primary key.  *<p>  * The state store contains several API interfaces, one for each data records  * type.  *<ul>  *<li>FederationMembershipStateStore: state of all Namenodes in the federation.  * Uses the MembershipState record.  *<li>FederationMountTableStore: Mount table mapping paths in the global  * namespace to individual subcluster paths. Uses the MountTable record.  *<li>RouterStateStore: State of all routers in the federation. Uses the  * RouterState record.  *</ul>  * Each API is defined in a separate interface. The implementations of these  * interfaces are responsible for accessing the {@link  * org.apache.hadoop.hdfs.server.federation.store.driver.StateStoreDriver  * StateStoreDriver} to query, update and delete data records.  */
end_comment

begin_annotation
annotation|@
name|InterfaceAudience
operator|.
name|Private
end_annotation

begin_annotation
annotation|@
name|InterfaceStability
operator|.
name|Evolving
end_annotation

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
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

end_unit

