begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher.testservices
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
operator|.
name|testservices
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An extension of {@link LaunchableRunningService} which returns null from  * the {@link #bindArgs(Configuration, List)} method.  */
end_comment

begin_class
DECL|class|NullBindLaunchableService
specifier|public
class|class
name|NullBindLaunchableService
extends|extends
name|LaunchableRunningService
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.service.launcher.testservices.NullBindLaunchableService"
decl_stmt|;
DECL|method|NullBindLaunchableService ()
specifier|public
name|NullBindLaunchableService
parameter_list|()
block|{
name|this
argument_list|(
literal|"NullBindLaunchableService"
argument_list|)
expr_stmt|;
block|}
DECL|method|NullBindLaunchableService (String name)
specifier|public
name|NullBindLaunchableService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bindArgs (Configuration config, List<String> args)
specifier|public
name|Configuration
name|bindArgs
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

