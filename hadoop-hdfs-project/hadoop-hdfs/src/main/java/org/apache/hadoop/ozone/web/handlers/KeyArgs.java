begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.handlers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|handlers
package|;
end_package

begin_comment
comment|/**  * Class that packages all key Arguments.  */
end_comment

begin_class
DECL|class|KeyArgs
specifier|public
class|class
name|KeyArgs
extends|extends
name|BucketArgs
block|{
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|delete
specifier|private
name|boolean
name|delete
decl_stmt|;
DECL|field|hash
specifier|private
name|String
name|hash
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
decl_stmt|;
comment|/**    * Constructor for Key Args.    *    * @param volumeName - Volume Name    * @param bucketName - Bucket Name    * @param objectName - Key    */
DECL|method|KeyArgs (String volumeName, String bucketName, String objectName, UserArgs args)
specifier|public
name|KeyArgs
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|objectName
parameter_list|,
name|UserArgs
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|objectName
expr_stmt|;
block|}
comment|/**    * Get Key Name.    *    * @return String    */
DECL|method|getKeyName ()
specifier|public
name|String
name|getKeyName
parameter_list|()
block|{
return|return
name|this
operator|.
name|key
return|;
block|}
comment|/**    * Checks if this request is for a Delete key.    *    * @return boolean    */
DECL|method|isDelete ()
specifier|public
name|boolean
name|isDelete
parameter_list|()
block|{
return|return
name|delete
return|;
block|}
comment|/**    * Sets the key request as a Delete Request.    *    * @param delete bool, indicating if this is a delete request    */
DECL|method|setDelete (boolean delete)
specifier|public
name|void
name|setDelete
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{
name|this
operator|.
name|delete
operator|=
name|delete
expr_stmt|;
block|}
comment|/**    * Computed File hash.    *    * @return String    */
DECL|method|getHash ()
specifier|public
name|String
name|getHash
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
comment|/**    * Sets the hash String.    *    * @param hash String    */
DECL|method|setHash (String hash)
specifier|public
name|void
name|setHash
parameter_list|(
name|String
name|hash
parameter_list|)
block|{
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
block|}
comment|/**    * Returns the file size.    *    * @return long - file size    */
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Set Size.    *    * @param size Size of the file    */
DECL|method|setSize (long size)
specifier|public
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**    * Returns the name of the resource.    *    * @return String    */
annotation|@
name|Override
DECL|method|getResourceName ()
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
return|return
name|super
operator|.
name|getResourceName
argument_list|()
operator|+
literal|"/"
operator|+
name|getKeyName
argument_list|()
return|;
block|}
comment|/**    * Parent name of this resource.    *    * @return String.    */
annotation|@
name|Override
DECL|method|getParentName ()
specifier|public
name|String
name|getParentName
parameter_list|()
block|{
return|return
name|super
operator|.
name|getResourceName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

