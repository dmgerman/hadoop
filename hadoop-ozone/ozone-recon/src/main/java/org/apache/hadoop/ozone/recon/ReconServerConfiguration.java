begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
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

begin_comment
comment|/**  * This class contains constants for Recon configuration keys.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ReconServerConfiguration
specifier|public
specifier|final
class|class
name|ReconServerConfiguration
block|{
DECL|field|OZONE_RECON_HTTP_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_ENABLED_KEY
init|=
literal|"ozone.recon.http.enabled"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_BIND_HOST_KEY
init|=
literal|"ozone.recon.http-bind-host"
decl_stmt|;
DECL|field|OZONE_RECON_HTTPS_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTPS_BIND_HOST_KEY
init|=
literal|"ozone.recon.https-bind-host"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_ADDRESS_KEY
init|=
literal|"ozone.recon.http-address"
decl_stmt|;
DECL|field|OZONE_RECON_HTTPS_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTPS_ADDRESS_KEY
init|=
literal|"ozone.recon.https-address"
decl_stmt|;
DECL|field|OZONE_RECON_KEYTAB_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_KEYTAB_FILE
init|=
literal|"ozone.recon.keytab.file"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_HTTP_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_RECON_HTTP_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_RECON_HTTP_BIND_PORT_DEFAULT
init|=
literal|9888
decl_stmt|;
DECL|field|OZONE_RECON_HTTPS_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_RECON_HTTPS_BIND_PORT_DEFAULT
init|=
literal|9889
decl_stmt|;
DECL|field|OZONE_RECON_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL
init|=
literal|"ozone.recon.authentication.kerberos.principal"
decl_stmt|;
DECL|field|OZONE_RECON_DOMAIN_NAME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RECON_DOMAIN_NAME
init|=
literal|"ozone.recon.domain.name"
decl_stmt|;
comment|/**    * Private constructor for utility class.    */
DECL|method|ReconServerConfiguration ()
specifier|private
name|ReconServerConfiguration
parameter_list|()
block|{   }
block|}
end_class

end_unit

