begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
package|;
end_package

begin_comment
comment|/**  * ScmInfo wraps the result returned from SCM#getScmInfo which  * contains clusterId and the SCM Id.  */
end_comment

begin_class
DECL|class|ScmInfo
specifier|public
specifier|final
class|class
name|ScmInfo
block|{
DECL|field|clusterId
specifier|private
name|String
name|clusterId
decl_stmt|;
DECL|field|scmId
specifier|private
name|String
name|scmId
decl_stmt|;
comment|/**    * Builder for ScmInfo.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|clusterId
specifier|private
name|String
name|clusterId
decl_stmt|;
DECL|field|scmId
specifier|private
name|String
name|scmId
decl_stmt|;
comment|/**      * sets the cluster id.      * @param cid clusterId to be set      * @return Builder for ScmInfo      */
DECL|method|setClusterId (String cid)
specifier|public
name|Builder
name|setClusterId
parameter_list|(
name|String
name|cid
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|cid
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * sets the scmId.      * @param id scmId      * @return Builder for scmInfo      */
DECL|method|setScmId (String id)
specifier|public
name|Builder
name|setScmId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|scmId
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|ScmInfo
name|build
parameter_list|()
block|{
return|return
operator|new
name|ScmInfo
argument_list|(
name|clusterId
argument_list|,
name|scmId
argument_list|)
return|;
block|}
block|}
DECL|method|ScmInfo (String clusterId, String scmId)
specifier|private
name|ScmInfo
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|scmId
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|scmId
operator|=
name|scmId
expr_stmt|;
block|}
comment|/**    * Gets the clusterId from the Version file.    * @return ClusterId    */
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
comment|/**    * Gets the SCM Id from the Version file.    * @return SCM Id    */
DECL|method|getScmId ()
specifier|public
name|String
name|getScmId
parameter_list|()
block|{
return|return
name|scmId
return|;
block|}
block|}
end_class

end_unit

