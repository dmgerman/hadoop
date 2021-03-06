begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|security
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
name|io
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * Constants for AM Secret Keys.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|AMSecretKeys
specifier|public
specifier|final
class|class
name|AMSecretKeys
block|{
DECL|field|YARN_APPLICATION_AM_KEYSTORE
specifier|public
specifier|final
specifier|static
name|Text
name|YARN_APPLICATION_AM_KEYSTORE
init|=
operator|new
name|Text
argument_list|(
literal|"yarn.application.am.keystore"
argument_list|)
decl_stmt|;
DECL|field|YARN_APPLICATION_AM_KEYSTORE_PASSWORD
specifier|public
specifier|final
specifier|static
name|Text
name|YARN_APPLICATION_AM_KEYSTORE_PASSWORD
init|=
operator|new
name|Text
argument_list|(
literal|"yarn.application.am.keystore.password"
argument_list|)
decl_stmt|;
DECL|field|YARN_APPLICATION_AM_TRUSTSTORE
specifier|public
specifier|final
specifier|static
name|Text
name|YARN_APPLICATION_AM_TRUSTSTORE
init|=
operator|new
name|Text
argument_list|(
literal|"yarn.application.am.truststore"
argument_list|)
decl_stmt|;
DECL|field|YARN_APPLICATION_AM_TRUSTSTORE_PASSWORD
specifier|public
specifier|final
specifier|static
name|Text
name|YARN_APPLICATION_AM_TRUSTSTORE_PASSWORD
init|=
operator|new
name|Text
argument_list|(
literal|"yarn.application.am.truststore.password"
argument_list|)
decl_stmt|;
DECL|method|AMSecretKeys ()
specifier|private
name|AMSecretKeys
parameter_list|()
block|{
comment|// not used
block|}
block|}
end_class

end_unit

