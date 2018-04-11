begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
name|resolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RemoteLocationContext
import|;
end_import

begin_comment
comment|/**  * Represents information about a single nameservice/namespace in a federated  * HDFS cluster.  */
end_comment

begin_class
DECL|class|FederationNamespaceInfo
specifier|public
class|class
name|FederationNamespaceInfo
extends|extends
name|RemoteLocationContext
block|{
comment|/** Block pool identifier. */
DECL|field|blockPoolId
specifier|private
specifier|final
name|String
name|blockPoolId
decl_stmt|;
comment|/** Cluster identifier. */
DECL|field|clusterId
specifier|private
specifier|final
name|String
name|clusterId
decl_stmt|;
comment|/** Nameservice identifier. */
DECL|field|nameserviceId
specifier|private
specifier|final
name|String
name|nameserviceId
decl_stmt|;
DECL|method|FederationNamespaceInfo (String bpId, String clId, String nsId)
specifier|public
name|FederationNamespaceInfo
parameter_list|(
name|String
name|bpId
parameter_list|,
name|String
name|clId
parameter_list|,
name|String
name|nsId
parameter_list|)
block|{
name|this
operator|.
name|blockPoolId
operator|=
name|bpId
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|clId
expr_stmt|;
name|this
operator|.
name|nameserviceId
operator|=
name|nsId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNameserviceId ()
specifier|public
name|String
name|getNameserviceId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nameserviceId
return|;
block|}
annotation|@
name|Override
DECL|method|getDest ()
specifier|public
name|String
name|getDest
parameter_list|()
block|{
return|return
name|this
operator|.
name|nameserviceId
return|;
block|}
annotation|@
name|Override
DECL|method|getSrc ()
specifier|public
name|String
name|getSrc
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * The HDFS cluster id for this namespace.    *    * @return Cluster identifier.    */
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterId
return|;
block|}
comment|/**    * The HDFS block pool id for this namespace.    *    * @return Block pool identifier.    */
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|this
operator|.
name|blockPoolId
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|nameserviceId
operator|+
literal|"->"
operator|+
name|this
operator|.
name|blockPoolId
operator|+
literal|":"
operator|+
name|this
operator|.
name|clusterId
return|;
block|}
block|}
end_class

end_unit

