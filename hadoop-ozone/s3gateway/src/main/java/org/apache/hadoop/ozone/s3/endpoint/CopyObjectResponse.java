begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.endpoint
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
operator|.
name|endpoint
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
name|s3
operator|.
name|commontypes
operator|.
name|IsoDateAdapter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|adapters
operator|.
name|XmlJavaTypeAdapter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_comment
comment|/**  * Copy object Response.  */
end_comment

begin_class
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"ListAllMyBucketsResult"
argument_list|,
name|namespace
operator|=
literal|"http://s3.amazonaws.com/doc/2006-03-01/"
argument_list|)
DECL|class|CopyObjectResponse
specifier|public
class|class
name|CopyObjectResponse
block|{
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|IsoDateAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"LastModified"
argument_list|)
DECL|field|lastModified
specifier|private
name|Instant
name|lastModified
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"ETag"
argument_list|)
DECL|field|eTag
specifier|private
name|String
name|eTag
decl_stmt|;
DECL|method|getLastModified ()
specifier|public
name|Instant
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
return|;
block|}
DECL|method|setLastModified (Instant lastModified)
specifier|public
name|void
name|setLastModified
parameter_list|(
name|Instant
name|lastModified
parameter_list|)
block|{
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
block|}
DECL|method|getETag ()
specifier|public
name|String
name|getETag
parameter_list|()
block|{
return|return
name|eTag
return|;
block|}
DECL|method|setETag (String tag)
specifier|public
name|void
name|setETag
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
name|this
operator|.
name|eTag
operator|=
name|tag
expr_stmt|;
block|}
block|}
end_class

end_unit

