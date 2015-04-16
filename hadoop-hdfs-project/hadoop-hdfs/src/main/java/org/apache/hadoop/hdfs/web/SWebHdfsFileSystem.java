begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|io
operator|.
name|Text
import|;
end_import

begin_class
DECL|class|SWebHdfsFileSystem
specifier|public
class|class
name|SWebHdfsFileSystem
extends|extends
name|WebHdfsFileSystem
block|{
annotation|@
name|Override
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|WebHdfsConstants
operator|.
name|SWEBHDFS_SCHEME
return|;
block|}
annotation|@
name|Override
DECL|method|getTransportScheme ()
specifier|protected
name|String
name|getTransportScheme
parameter_list|()
block|{
return|return
literal|"https"
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenKind ()
specifier|protected
name|Text
name|getTokenKind
parameter_list|()
block|{
return|return
name|WebHdfsConstants
operator|.
name|SWEBHDFS_TOKEN_KIND
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultPort ()
specifier|protected
name|int
name|getDefaultPort
parameter_list|()
block|{
return|return
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_PORT_DEFAULT
return|;
block|}
block|}
end_class

end_unit

