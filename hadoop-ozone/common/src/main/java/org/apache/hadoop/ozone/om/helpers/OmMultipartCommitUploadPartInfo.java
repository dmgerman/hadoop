begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|helpers
package|;
end_package

begin_comment
comment|/**  * This class holds information about the response from commit multipart  * upload part request.  */
end_comment

begin_class
DECL|class|OmMultipartCommitUploadPartInfo
specifier|public
class|class
name|OmMultipartCommitUploadPartInfo
block|{
DECL|field|partName
specifier|private
specifier|final
name|String
name|partName
decl_stmt|;
DECL|method|OmMultipartCommitUploadPartInfo (String name)
specifier|public
name|OmMultipartCommitUploadPartInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|partName
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getPartName ()
specifier|public
name|String
name|getPartName
parameter_list|()
block|{
return|return
name|partName
return|;
block|}
block|}
end_class

end_unit

