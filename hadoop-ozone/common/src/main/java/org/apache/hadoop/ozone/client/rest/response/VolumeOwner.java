begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.rest.response
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
operator|.
name|rest
operator|.
name|response
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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonInclude
import|;
end_import

begin_comment
comment|/**  * Volume Owner represents the owner of a volume.  *  * This is a class instead of a string since we might need to extend this class  * to support other forms of authentication.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|VolumeOwner
specifier|public
class|class
name|VolumeOwner
block|{
annotation|@
name|JsonInclude
argument_list|(
name|JsonInclude
operator|.
name|Include
operator|.
name|NON_NULL
argument_list|)
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    * Constructor for VolumeOwner.    *    * @param name name of the User    */
DECL|method|VolumeOwner (String name)
specifier|public
name|VolumeOwner
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * Constructs Volume Owner.    */
DECL|method|VolumeOwner ()
specifier|public
name|VolumeOwner
parameter_list|()
block|{
name|name
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Returns the user name.    *    * @return Name    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

