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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|GuiceServletContextListener
import|;
end_import

begin_comment
comment|/**  * Servlet Context Listener that provides the Guice injector.  */
end_comment

begin_class
DECL|class|ReconGuiceServletContextListener
specifier|public
class|class
name|ReconGuiceServletContextListener
extends|extends
name|GuiceServletContextListener
block|{
DECL|field|injector
specifier|private
specifier|static
name|Injector
name|injector
decl_stmt|;
annotation|@
name|Override
DECL|method|getInjector ()
specifier|public
name|Injector
name|getInjector
parameter_list|()
block|{
return|return
name|injector
return|;
block|}
DECL|method|setInjector (Injector inj)
specifier|static
name|void
name|setInjector
parameter_list|(
name|Injector
name|inj
parameter_list|)
block|{
name|injector
operator|=
name|inj
expr_stmt|;
block|}
block|}
end_class

end_unit

