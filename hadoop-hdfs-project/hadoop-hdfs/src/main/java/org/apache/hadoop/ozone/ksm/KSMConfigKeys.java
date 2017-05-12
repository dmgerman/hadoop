begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
package|;
end_package

begin_comment
comment|/**  * KSM Constants.  */
end_comment

begin_class
DECL|class|KSMConfigKeys
specifier|public
specifier|final
class|class
name|KSMConfigKeys
block|{
comment|/**    * Never constructed.    */
DECL|method|KSMConfigKeys ()
specifier|private
name|KSMConfigKeys
parameter_list|()
block|{   }
DECL|field|OZONE_KSM_HANDLER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_HANDLER_COUNT_KEY
init|=
literal|"ozone.scm.handler.count.key"
decl_stmt|;
DECL|field|OZONE_KSM_HANDLER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_HANDLER_COUNT_DEFAULT
init|=
literal|200
decl_stmt|;
DECL|field|OZONE_KSM_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_ADDRESS_KEY
init|=
literal|"ozone.ksm.address"
decl_stmt|;
DECL|field|OZONE_KSM_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KSM_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_KSM_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KSM_PORT_DEFAULT
init|=
literal|9862
decl_stmt|;
block|}
end_class

end_unit

