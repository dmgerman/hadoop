begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.exception
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
name|exception
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_BAD_REQUEST
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_CONFLICT
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
import|;
end_import

begin_comment
comment|/**  * This class represents errors from Ozone S3 service.  * This class needs to be updated to add new errors when required.  */
end_comment

begin_class
DECL|class|S3ErrorTable
specifier|public
specifier|final
class|class
name|S3ErrorTable
block|{
DECL|method|S3ErrorTable ()
specifier|private
name|S3ErrorTable
parameter_list|()
block|{
comment|//No one should construct this object.
block|}
DECL|field|INVALID_URI
specifier|public
specifier|static
specifier|final
name|OS3Exception
name|INVALID_URI
init|=
operator|new
name|OS3Exception
argument_list|(
literal|"InvalidURI"
argument_list|,
literal|"Couldn't parse the specified URI."
argument_list|,
name|HTTP_BAD_REQUEST
argument_list|)
decl_stmt|;
DECL|field|NO_SUCH_BUCKET
specifier|public
specifier|static
specifier|final
name|OS3Exception
name|NO_SUCH_BUCKET
init|=
operator|new
name|OS3Exception
argument_list|(
literal|"NoSuchBucket"
argument_list|,
literal|"The specified bucket does not exist"
argument_list|,
name|HTTP_NOT_FOUND
argument_list|)
decl_stmt|;
DECL|field|BUCKET_NOT_EMPTY
specifier|public
specifier|static
specifier|final
name|OS3Exception
name|BUCKET_NOT_EMPTY
init|=
operator|new
name|OS3Exception
argument_list|(
literal|"BucketNotEmpty"
argument_list|,
literal|"The bucket you tried to delete is not empty."
argument_list|,
name|HTTP_CONFLICT
argument_list|)
decl_stmt|;
DECL|field|NO_SUCH_OBJECT
specifier|public
specifier|static
specifier|final
name|OS3Exception
name|NO_SUCH_OBJECT
init|=
operator|new
name|OS3Exception
argument_list|(
literal|"NoSuchObject"
argument_list|,
literal|"The specified object does not exist"
argument_list|,
name|HTTP_NOT_FOUND
argument_list|)
decl_stmt|;
comment|/**    * Create a new instance of Error.    * @param e Error Template    * @param resource Resource associated with this exception    * @return creates a new instance of error based on the template    */
DECL|method|newError (OS3Exception e, Resource resource)
specifier|public
specifier|static
name|OS3Exception
name|newError
parameter_list|(
name|OS3Exception
name|e
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|OS3Exception
name|err
init|=
operator|new
name|OS3Exception
argument_list|(
name|e
operator|.
name|getCode
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getHttpCode
argument_list|()
argument_list|)
decl_stmt|;
name|err
operator|.
name|setResource
argument_list|(
name|resource
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|err
return|;
block|}
comment|/**    * Resources, which can be defined in OS3Exception.    */
DECL|enum|Resource
specifier|public
enum|enum
name|Resource
block|{
DECL|enumConstant|BUCKET
name|BUCKET
argument_list|(
literal|"Bucket"
argument_list|)
block|,
DECL|enumConstant|OBJECT
name|OBJECT
argument_list|(
literal|"Object"
argument_list|)
block|;
DECL|field|resource
specifier|private
specifier|final
name|String
name|resource
decl_stmt|;
comment|/**      * Constructs resource.      * @param value      */
DECL|method|Resource (String value)
name|Resource
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Get resource.      * @return string      */
DECL|method|getResource ()
specifier|public
name|String
name|getResource
parameter_list|()
block|{
return|return
name|this
operator|.
name|resource
return|;
block|}
block|}
block|}
end_class

end_unit

