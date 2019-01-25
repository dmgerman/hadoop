begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
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
name|ozone
operator|.
name|OzoneAcl
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * This class encapsulates the arguments that are  * required for creating a volume.  */
end_comment

begin_class
DECL|class|VolumeArgs
specifier|public
specifier|final
class|class
name|VolumeArgs
block|{
DECL|field|admin
specifier|private
specifier|final
name|String
name|admin
decl_stmt|;
DECL|field|owner
specifier|private
specifier|final
name|String
name|owner
decl_stmt|;
DECL|field|quota
specifier|private
specifier|final
name|String
name|quota
decl_stmt|;
DECL|field|acls
specifier|private
specifier|final
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
decl_stmt|;
DECL|field|metadata
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
comment|/**    * Private constructor, constructed via builder.    * @param admin Administrator's name.    * @param owner Volume owner's name    * @param quota Volume Quota.    * @param acls User to access rights map.    */
DECL|method|VolumeArgs (String admin, String owner, String quota, List<OzoneAcl> acls, Map<String, String> metadata)
specifier|private
name|VolumeArgs
parameter_list|(
name|String
name|admin
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|quota
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|admin
operator|=
name|admin
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|quota
operator|=
name|quota
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
comment|/**    * Returns the Admin Name.    * @return String.    */
DECL|method|getAdmin ()
specifier|public
name|String
name|getAdmin
parameter_list|()
block|{
return|return
name|admin
return|;
block|}
comment|/**    * Returns the owner Name.    * @return String    */
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Returns Volume Quota.    * @return Quota.    */
DECL|method|getQuota ()
specifier|public
name|String
name|getQuota
parameter_list|()
block|{
return|return
name|quota
return|;
block|}
comment|/**    * Return custom key value map.    *    * @return metadata    */
DECL|method|getMetadata ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
DECL|method|getAcls ()
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
comment|/**    * Returns new builder class that builds a OmVolumeArgs.    *    * @return Builder    */
DECL|method|newBuilder ()
specifier|public
specifier|static
name|VolumeArgs
operator|.
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|VolumeArgs
operator|.
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Builder for OmVolumeArgs.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|adminName
specifier|private
name|String
name|adminName
decl_stmt|;
DECL|field|ownerName
specifier|private
name|String
name|ownerName
decl_stmt|;
DECL|field|volumeQuota
specifier|private
name|String
name|volumeQuota
decl_stmt|;
DECL|field|listOfAcls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|listOfAcls
decl_stmt|;
DECL|field|metadata
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|setAdmin (String admin)
specifier|public
name|VolumeArgs
operator|.
name|Builder
name|setAdmin
parameter_list|(
name|String
name|admin
parameter_list|)
block|{
name|this
operator|.
name|adminName
operator|=
name|admin
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOwner (String owner)
specifier|public
name|VolumeArgs
operator|.
name|Builder
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
name|this
operator|.
name|ownerName
operator|=
name|owner
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setQuota (String quota)
specifier|public
name|VolumeArgs
operator|.
name|Builder
name|setQuota
parameter_list|(
name|String
name|quota
parameter_list|)
block|{
name|this
operator|.
name|volumeQuota
operator|=
name|quota
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addMetadata (String key, String value)
specifier|public
name|VolumeArgs
operator|.
name|Builder
name|addMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|metadata
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAcls (List<OzoneAcl> acls)
specifier|public
name|VolumeArgs
operator|.
name|Builder
name|setAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|listOfAcls
operator|=
name|acls
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a CreateVolumeArgument.      * @return CreateVolumeArgs.      */
DECL|method|build ()
specifier|public
name|VolumeArgs
name|build
parameter_list|()
block|{
return|return
operator|new
name|VolumeArgs
argument_list|(
name|adminName
argument_list|,
name|ownerName
argument_list|,
name|volumeQuota
argument_list|,
name|listOfAcls
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

