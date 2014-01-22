begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * HttpFSServer implementation of the FileSystemAccess FileSystem for SSL.  *<p/>  * This implementation allows a user to access HDFS over HTTPS via a  * HttpFSServer server.  */
end_comment

begin_class
DECL|class|HttpsFSFileSystem
specifier|public
class|class
name|HttpsFSFileSystem
extends|extends
name|HttpFSFileSystem
block|{
DECL|field|SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME
init|=
literal|"swebhdfs"
decl_stmt|;
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|SCHEME
return|;
block|}
block|}
end_class

end_unit

