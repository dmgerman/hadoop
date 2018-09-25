begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.object
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
name|object
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|HeaderParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PUT
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PathParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|io
operator|.
name|InputStream
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationFactor
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationType
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
name|client
operator|.
name|OzoneBucket
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
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
name|io
operator|.
name|IOUtils
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
name|s3
operator|.
name|EndpointBase
import|;
end_import

begin_comment
comment|/**  * File upload.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/{volume}/{bucket}/{path:.+}"
argument_list|)
DECL|class|PutObject
specifier|public
class|class
name|PutObject
extends|extends
name|EndpointBase
block|{
annotation|@
name|PUT
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
DECL|method|put ( @athParamR) String volumeName, @PathParam(R) String bucketName, @PathParam(R) String keyPath, @HeaderParam(R) long length, InputStream body)
specifier|public
name|void
name|put
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"volume"
argument_list|)
name|String
name|volumeName
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"bucket"
argument_list|)
name|String
name|bucketName
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"path"
argument_list|)
name|String
name|keyPath
parameter_list|,
annotation|@
name|HeaderParam
argument_list|(
literal|"Content-Length"
argument_list|)
name|long
name|length
parameter_list|,
name|InputStream
name|body
parameter_list|)
throws|throws
name|IOException
block|{
name|OzoneBucket
name|bucket
init|=
name|getBucket
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|OzoneOutputStream
name|output
init|=
name|bucket
operator|.
name|createKey
argument_list|(
name|keyPath
argument_list|,
name|length
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|body
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

