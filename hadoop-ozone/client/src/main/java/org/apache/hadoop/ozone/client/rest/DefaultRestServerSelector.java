begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.rest
package|package
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
name|rest
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|ServiceInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Default selector randomly picks one of the REST Server from the list.  */
end_comment

begin_class
DECL|class|DefaultRestServerSelector
specifier|public
class|class
name|DefaultRestServerSelector
implements|implements
name|RestServerSelector
block|{
annotation|@
name|Override
DECL|method|getRestServer (List<ServiceInfo> restServices)
specifier|public
name|ServiceInfo
name|getRestServer
parameter_list|(
name|List
argument_list|<
name|ServiceInfo
argument_list|>
name|restServices
parameter_list|)
block|{
return|return
name|restServices
operator|.
name|get
argument_list|(
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restServices
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

