begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authorize
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Permission
import|;
end_import

begin_comment
comment|/**  * An abstract definition of<em>service</em> as related to   * Service Level Authorization for Hadoop.  *   * Each service defines it's configuration key and also the necessary  * {@link Permission} required to access the service.  */
end_comment

begin_class
DECL|class|Service
specifier|public
class|class
name|Service
block|{
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|protocol
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
decl_stmt|;
DECL|method|Service (String key, Class<?> protocol)
specifier|public
name|Service
parameter_list|(
name|String
name|key
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|protocol
operator|=
name|protocol
expr_stmt|;
block|}
comment|/**    * Get the configuration key for the service.    * @return the configuration key for the service    */
DECL|method|getServiceKey ()
specifier|public
name|String
name|getServiceKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/**    * Get the protocol for the service    * @return the {@link Class} for the protocol    */
DECL|method|getProtocol ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getProtocol
parameter_list|()
block|{
return|return
name|protocol
return|;
block|}
block|}
end_class

end_unit

