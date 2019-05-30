begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.response
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmBucketInfo
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
name|util
operator|.
name|Time
import|;
end_import

begin_comment
comment|/**  * Helper class to test OMClientResponse classes.  */
end_comment

begin_class
DECL|class|TestOMResponseUtils
specifier|public
specifier|final
class|class
name|TestOMResponseUtils
block|{
comment|// No one can instantiate, this is just utility class with all static methods.
DECL|method|TestOMResponseUtils ()
specifier|private
name|TestOMResponseUtils
parameter_list|()
block|{   }
DECL|method|createBucket (String volume, String bucket)
specifier|public
specifier|static
name|OmBucketInfo
name|createBucket
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|)
block|{
return|return
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volume
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucket
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|true
argument_list|)
operator|.
name|addMetadata
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

