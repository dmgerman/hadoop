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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A class that encapsulates OzoneKeyLocation.  */
end_comment

begin_class
DECL|class|OzoneKeyDetails
specifier|public
class|class
name|OzoneKeyDetails
extends|extends
name|OzoneKey
block|{
comment|/**    * A list of block location information to specify replica locations.    */
DECL|field|ozoneKeyLocations
specifier|private
name|List
argument_list|<
name|OzoneKeyLocation
argument_list|>
name|ozoneKeyLocations
decl_stmt|;
comment|/**    * Constructs OzoneKeyDetails from OmKeyInfo.    */
DECL|method|OzoneKeyDetails (String volumeName, String bucketName, String keyName, long size, long creationTime, long modificationTime, List<OzoneKeyLocation> ozoneKeyLocations)
specifier|public
name|OzoneKeyDetails
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|long
name|size
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|List
argument_list|<
name|OzoneKeyLocation
argument_list|>
name|ozoneKeyLocations
parameter_list|)
block|{
name|super
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|size
argument_list|,
name|creationTime
argument_list|,
name|modificationTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|ozoneKeyLocations
operator|=
name|ozoneKeyLocations
expr_stmt|;
block|}
comment|/**    * Returns the location detail information of the specific Key.    */
DECL|method|getOzoneKeyLocations ()
specifier|public
name|List
argument_list|<
name|OzoneKeyLocation
argument_list|>
name|getOzoneKeyLocations
parameter_list|()
block|{
return|return
name|ozoneKeyLocations
return|;
block|}
comment|/**    * Set details of key location.    * @param ozoneKeyLocations - details of key location    */
DECL|method|setOzoneKeyLocations (List<OzoneKeyLocation> ozoneKeyLocations)
specifier|public
name|void
name|setOzoneKeyLocations
parameter_list|(
name|List
argument_list|<
name|OzoneKeyLocation
argument_list|>
name|ozoneKeyLocations
parameter_list|)
block|{
name|this
operator|.
name|ozoneKeyLocations
operator|=
name|ozoneKeyLocations
expr_stmt|;
block|}
block|}
end_class

end_unit

