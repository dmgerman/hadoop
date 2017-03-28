begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|CompositeService
import|;
end_import

begin_comment
comment|/**  * A service to initialize a  * {@link org.apache.hadoop.hdfs.server.federation.store.driver.StateStoreDriver  * StateStoreDriver} and maintain the connection to the data store. There  * are multiple state store driver connections supported:  *<ul>  *<li>File {@link org.apache.hadoop.hdfs.server.federation.store.driver.impl.  * StateStoreFileImpl StateStoreFileImpl}  *<li>ZooKeeper {@link org.apache.hadoop.hdfs.server.federation.store.driver.  * impl.StateStoreZooKeeperImpl StateStoreZooKeeperImpl}  *</ul>  *<p>  * The service also supports the dynamic registration of data interfaces such as  * the following:  *<ul>  *<li>{@link MembershipStateStore}: state of the Namenodes in the  * federation.  *<li>{@link MountTableStore}: Mount table between to subclusters.  * See {@link org.apache.hadoop.fs.viewfs.ViewFs ViewFs}.  *<li>{@link RouterStateStore}: State of the routers in the federation.  *</ul>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|StateStoreService
specifier|public
class|class
name|StateStoreService
extends|extends
name|CompositeService
block|{
comment|/** Identifier for the service. */
DECL|field|identifier
specifier|private
name|String
name|identifier
decl_stmt|;
comment|// Stub class
DECL|method|StateStoreService (String name)
specifier|public
name|StateStoreService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fetch a unique identifier for this state store instance. Typically it is    * the address of the router.    *    * @return Unique identifier for this store.    */
DECL|method|getIdentifier ()
specifier|public
name|String
name|getIdentifier
parameter_list|()
block|{
return|return
name|this
operator|.
name|identifier
return|;
block|}
comment|/**    * Set a unique synchronization identifier for this store.    *    * @param id Unique identifier, typically the router's RPC address.    */
DECL|method|setIdentifier (String id)
specifier|public
name|void
name|setIdentifier
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|id
expr_stmt|;
block|}
block|}
end_class

end_unit

