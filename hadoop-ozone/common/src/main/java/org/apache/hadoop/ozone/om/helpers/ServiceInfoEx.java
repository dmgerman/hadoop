begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|helpers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Wrapper class for service discovery, design for broader usage such as  * security, etc.  */
end_comment

begin_class
DECL|class|ServiceInfoEx
specifier|public
class|class
name|ServiceInfoEx
block|{
DECL|field|infoList
specifier|private
name|List
argument_list|<
name|ServiceInfo
argument_list|>
name|infoList
decl_stmt|;
comment|// PEM encoded string of SCM CA certificate.
DECL|field|caCertificate
specifier|private
name|String
name|caCertificate
decl_stmt|;
DECL|method|ServiceInfoEx (List<ServiceInfo> infoList, String caCertificate)
specifier|public
name|ServiceInfoEx
parameter_list|(
name|List
argument_list|<
name|ServiceInfo
argument_list|>
name|infoList
parameter_list|,
name|String
name|caCertificate
parameter_list|)
block|{
name|this
operator|.
name|infoList
operator|=
name|infoList
expr_stmt|;
name|this
operator|.
name|caCertificate
operator|=
name|caCertificate
expr_stmt|;
block|}
DECL|method|getServiceInfoList ()
specifier|public
name|List
argument_list|<
name|ServiceInfo
argument_list|>
name|getServiceInfoList
parameter_list|()
block|{
return|return
name|infoList
return|;
block|}
DECL|method|getCaCertificate ()
specifier|public
name|String
name|getCaCertificate
parameter_list|()
block|{
return|return
name|caCertificate
return|;
block|}
block|}
end_class

end_unit

