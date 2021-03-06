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
comment|/**  * A location in a remote namespace consisting of a nameservice ID and a HDFS  * path (destination). It also contains the federated location (source).  */
end_comment

begin_class
DECL|class|RemoteLocation
specifier|public
class|class
name|RemoteLocation
extends|extends
name|RemoteLocationContext
block|{
comment|/** Identifier of the remote namespace for this location. */
DECL|field|nameserviceId
specifier|private
specifier|final
name|String
name|nameserviceId
decl_stmt|;
comment|/** Identifier of the namenode in the namespace for this location. */
DECL|field|namenodeId
specifier|private
specifier|final
name|String
name|namenodeId
decl_stmt|;
comment|/** Path in the remote location. */
DECL|field|dstPath
specifier|private
specifier|final
name|String
name|dstPath
decl_stmt|;
comment|/** Original path in federation. */
DECL|field|srcPath
specifier|private
specifier|final
name|String
name|srcPath
decl_stmt|;
comment|/**    * Create a new remote location.    *    * @param nsId Destination namespace.    * @param dPath Path in the destination namespace.    * @param sPath Path in the federated level.    */
DECL|method|RemoteLocation (String nsId, String dPath, String sPath)
specifier|public
name|RemoteLocation
parameter_list|(
name|String
name|nsId
parameter_list|,
name|String
name|dPath
parameter_list|,
name|String
name|sPath
parameter_list|)
block|{
name|this
argument_list|(
name|nsId
argument_list|,
literal|null
argument_list|,
name|dPath
argument_list|,
name|sPath
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new remote location pointing to a particular namenode in the    * namespace.    *    * @param nsId Destination namespace.    * @param nnId Destination namenode.    * @param dPath Path in the destination namespace.    * @param sPath Path in the federated level    */
DECL|method|RemoteLocation (String nsId, String nnId, String dPath, String sPath)
specifier|public
name|RemoteLocation
parameter_list|(
name|String
name|nsId
parameter_list|,
name|String
name|nnId
parameter_list|,
name|String
name|dPath
parameter_list|,
name|String
name|sPath
parameter_list|)
block|{
name|this
operator|.
name|nameserviceId
operator|=
name|nsId
expr_stmt|;
name|this
operator|.
name|namenodeId
operator|=
name|nnId
expr_stmt|;
name|this
operator|.
name|dstPath
operator|=
name|dPath
expr_stmt|;
name|this
operator|.
name|srcPath
operator|=
name|sPath
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
name|String
name|ret
init|=
name|this
operator|.
name|nameserviceId
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|namenodeId
operator|!=
literal|null
condition|)
block|{
name|ret
operator|+=
literal|"-"
operator|+
name|this
operator|.
name|namenodeId
expr_stmt|;
block|}
return|return
name|ret
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
name|dstPath
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
name|this
operator|.
name|srcPath
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
name|getNameserviceId
argument_list|()
operator|+
literal|"->"
operator|+
name|this
operator|.
name|dstPath
return|;
block|}
block|}
end_class

end_unit

