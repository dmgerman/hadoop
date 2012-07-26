begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
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
name|lib
operator|.
name|lang
operator|.
name|XException
import|;
end_import

begin_comment
comment|/**  * Exception thrown by the {@link DelegationTokenManager} service implementation.  */
end_comment

begin_class
DECL|class|DelegationTokenManagerException
specifier|public
class|class
name|DelegationTokenManagerException
extends|extends
name|XException
block|{
DECL|enum|ERROR
specifier|public
enum|enum
name|ERROR
implements|implements
name|XException
operator|.
name|ERROR
block|{
DECL|enumConstant|DT01
name|DT01
argument_list|(
literal|"Could not verify delegation token, {0}"
argument_list|)
block|,
DECL|enumConstant|DT02
name|DT02
argument_list|(
literal|"Could not renew delegation token, {0}"
argument_list|)
block|,
DECL|enumConstant|DT03
name|DT03
argument_list|(
literal|"Could not cancel delegation token, {0}"
argument_list|)
block|,
DECL|enumConstant|DT04
name|DT04
argument_list|(
literal|"Could not create delegation token, {0}"
argument_list|)
block|;
DECL|field|template
specifier|private
name|String
name|template
decl_stmt|;
DECL|method|ERROR (String template)
name|ERROR
parameter_list|(
name|String
name|template
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTemplate ()
specifier|public
name|String
name|getTemplate
parameter_list|()
block|{
return|return
name|template
return|;
block|}
block|}
DECL|method|DelegationTokenManagerException (ERROR error, Object... params)
specifier|public
name|DelegationTokenManagerException
parameter_list|(
name|ERROR
name|error
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|error
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

