begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|enterprise
operator|.
name|context
operator|.
name|RequestScoped
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
import|;
end_import

begin_comment
comment|/**  * Request specific identifiers.  */
end_comment

begin_class
annotation|@
name|RequestScoped
DECL|class|RequestIdentifier
specifier|public
class|class
name|RequestIdentifier
block|{
DECL|field|requestId
specifier|private
specifier|final
name|String
name|requestId
decl_stmt|;
DECL|field|amzId
specifier|private
specifier|final
name|String
name|amzId
decl_stmt|;
DECL|method|RequestIdentifier ()
specifier|public
name|RequestIdentifier
parameter_list|()
block|{
name|amzId
operator|=
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|8
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|requestId
operator|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
expr_stmt|;
block|}
DECL|method|getRequestId ()
specifier|public
name|String
name|getRequestId
parameter_list|()
block|{
return|return
name|requestId
return|;
block|}
DECL|method|getAmzId ()
specifier|public
name|String
name|getAmzId
parameter_list|()
block|{
return|return
name|amzId
return|;
block|}
block|}
end_class

end_unit

