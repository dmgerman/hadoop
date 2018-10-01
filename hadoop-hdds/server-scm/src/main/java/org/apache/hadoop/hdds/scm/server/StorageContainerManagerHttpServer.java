begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
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
name|conf
operator|.
name|Configuration
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
name|scm
operator|.
name|ScmConfigKeys
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
name|server
operator|.
name|BaseHttpServer
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

begin_comment
comment|/**  * HttpServer2 wrapper for the Ozone Storage Container Manager.  */
end_comment

begin_class
DECL|class|StorageContainerManagerHttpServer
specifier|public
class|class
name|StorageContainerManagerHttpServer
extends|extends
name|BaseHttpServer
block|{
DECL|method|StorageContainerManagerHttpServer (Configuration conf)
specifier|public
name|StorageContainerManagerHttpServer
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
literal|"scm"
argument_list|)
expr_stmt|;
block|}
DECL|method|getHttpAddressKey ()
annotation|@
name|Override
specifier|protected
name|String
name|getHttpAddressKey
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTP_ADDRESS_KEY
return|;
block|}
DECL|method|getHttpBindHostKey ()
annotation|@
name|Override
specifier|protected
name|String
name|getHttpBindHostKey
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTP_BIND_HOST_KEY
return|;
block|}
DECL|method|getHttpsAddressKey ()
annotation|@
name|Override
specifier|protected
name|String
name|getHttpsAddressKey
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTPS_ADDRESS_KEY
return|;
block|}
DECL|method|getHttpsBindHostKey ()
annotation|@
name|Override
specifier|protected
name|String
name|getHttpsBindHostKey
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTPS_BIND_HOST_KEY
return|;
block|}
DECL|method|getBindHostDefault ()
annotation|@
name|Override
specifier|protected
name|String
name|getBindHostDefault
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTP_BIND_HOST_DEFAULT
return|;
block|}
DECL|method|getHttpBindPortDefault ()
annotation|@
name|Override
specifier|protected
name|int
name|getHttpBindPortDefault
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTP_BIND_PORT_DEFAULT
return|;
block|}
DECL|method|getHttpsBindPortDefault ()
annotation|@
name|Override
specifier|protected
name|int
name|getHttpsBindPortDefault
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTPS_BIND_PORT_DEFAULT
return|;
block|}
DECL|method|getKeytabFile ()
annotation|@
name|Override
specifier|protected
name|String
name|getKeytabFile
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|HDDS_SCM_HTTP_KERBEROS_KEYTAB_FILE_KEY
return|;
block|}
DECL|method|getSpnegoPrincipal ()
annotation|@
name|Override
specifier|protected
name|String
name|getSpnegoPrincipal
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|HDDS_SCM_HTTP_KERBEROS_PRINCIPAL_KEY
return|;
block|}
DECL|method|getEnabledKey ()
annotation|@
name|Override
specifier|protected
name|String
name|getEnabledKey
parameter_list|()
block|{
return|return
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HTTP_ENABLED_KEY
return|;
block|}
block|}
end_class

end_unit

