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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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

begin_comment
comment|/**  * An implementation of a protocol for accessing filesystems over HTTPS. The  * following implementation provides a limited, read-only interface to a  * filesystem over HTTPS.  *  * @see org.apache.hadoop.hdfs.server.namenode.ListPathsServlet  * @see org.apache.hadoop.hdfs.server.namenode.FileDataServlet  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HsftpFileSystem
specifier|public
class|class
name|HsftpFileSystem
extends|extends
name|HftpFileSystem
block|{
DECL|field|TOKEN_KIND
specifier|public
specifier|static
specifier|final
name|Text
name|TOKEN_KIND
init|=
operator|new
name|Text
argument_list|(
literal|"HSFTP delegation"
argument_list|)
decl_stmt|;
DECL|field|SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME
init|=
literal|"hsftp"
decl_stmt|;
comment|/**    * Return the protocol scheme for the FileSystem.    *<p/>    *    * @return<code>hsftp</code>    */
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
comment|/**    * Return the underlying protocol that is used to talk to the namenode.    */
annotation|@
name|Override
DECL|method|getUnderlyingProtocol ()
specifier|protected
name|String
name|getUnderlyingProtocol
parameter_list|()
block|{
return|return
literal|"https"
return|;
block|}
annotation|@
name|Override
DECL|method|initTokenAspect ()
specifier|protected
name|void
name|initTokenAspect
parameter_list|()
block|{
name|tokenAspect
operator|=
operator|new
name|TokenAspect
argument_list|<
name|HsftpFileSystem
argument_list|>
argument_list|(
name|this
argument_list|,
name|tokenServiceName
argument_list|,
name|TOKEN_KIND
argument_list|)
expr_stmt|;
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

